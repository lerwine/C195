package util;

import com.mysql.jdbc.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import scheduler.AppConfig;
import scheduler.InternalException;

/**
 * Manages SQL connection dependencies.
 * SQL connections are opened when needed, and then closes the connection after there have been no dependencies for 1 second.
 * @author Leonard T. Erwine
 */
public final class SqlConnectionDependency implements AutoCloseable {
    private static final String DB_DRIVER = "com.mysql.jdbc.Driver";
    
    // Synchronization object for changes that affect more than one SQL connection dependency.
    private static final Object MONITOR = new Object();
    
    //<editor-fold defaultstate="collapsed" desc="Dependency chain fields">
    
    // The latest in a chain of SQL connection dependencies.
    private static SqlConnectionDependency LATEST = null;
    // The SQL connection dependency that precedes the current one.
    private SqlConnectionDependency previous;
    // The SQL connection dependency that follows the current one.
    private SqlConnectionDependency next = null;
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="connection property">
    
    // The currently opened SQL connection.
    private static Connection CONNECTION = null;
    
    private final ReadOnlyObjectWrapper<Connection> connection;

    public Connection getConnection() { return connection.get(); }

    public ReadOnlyObjectProperty<Connection> connectionProperty() {
        return connection.getReadOnlyProperty();
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Constructors">
    
    public SqlConnectionDependency() throws SQLException {
        // "next", "previous", "latest", "closeAt", "closeService" and "closeTask" properties should only be changed during a lock on all dependencies.
        synchronized(MONITOR) {
            if ((previous = LATEST) != null)
                previous.next = LATEST = this;
            else
                LATEST = this;
            if (CONNECTION == null) {
                try { Class.forName(DB_DRIVER); }
                catch (ClassNotFoundException ex) {
                    Logger.getLogger(SqlConnectionDependency.class.getName()).log(Level.SEVERE, null, ex);
                    throw new InternalException("Error getting driver class", ex);
                }
                String url = AppConfig.getConnectionUrl();
                CONNECTION = (Connection)DriverManager.getConnection(url, AppConfig.getDbLoginName(), AppConfig.getDbLoginPassword());
                Logger.getLogger(getClass().getName()).log(Level.INFO, String.format("Connected to %s", url));
            }
        }
        connection = new ReadOnlyObjectWrapper<>(CONNECTION);
    }
    
    //</editor-fold>
    
    /**
     * Closes the current SQL connection dependency.
     * @throws SQLException 
     */
    @Override
    public final void close() throws SQLException {
        // "next", "previous", "latest", "closeAt", "closeService" and "closeTask" properties should only be changed during a lock on all dependencies.
        synchronized(MONITOR) {
            if (connection.get() == null)
                throw new InternalException("SQL connection dependency is already closed");
            if (next != null) {
                if ((next.previous = previous) != null) {
                    previous.next = next;
                    previous = null;
                }
                next = null;
            } else if ((LATEST = previous) != null)
                previous = previous.next = null;
            else
                // Close SQL connection in one second if it's not needed.
                DelayedConnectionCloser.start();
        }
        connection.set(null);
    }
    
    /**
     * Forces SQL {@link Connection} to be closed immediately.
     * @throws SQLException 
     */
    public static void forceClose() throws SQLException {
        ScheduledExecutorService svc = null;
        ArrayList<SqlConnectionDependency> closed = new ArrayList<>();
        try {
            // "next", "previous", "latest", "closeAt", "closeService" and "closeTask" properties should only be changed during a lock on all dependencies.
            synchronized(MONITOR) {
                Connection c = CONNECTION;
                CONNECTION = null;
                // We need to be sure that the "closeService" field is always set to null by invoking this method,
                // and we also need to shut it down.
                svc = DelayedConnectionCloser.closeService;
                DelayedConnectionCloser.closeService = null;
                // Remove each dependency, but don't call the "close" method.
                // Otherwise, when the thread which created them calls "close", it will throw an exception.
                while (LATEST != null) {
                    SqlConnectionDependency d = LATEST;
                    closed.add(d);
                    d.previous = (LATEST = d.previous).next = null;
                }
                // Make sure another thread hasn't already closed the database connection.
                if (c != null) {
                    String host = c.getHost();
                    c.close();
                    Logger.getLogger(SqlConnectionDependency.class.getName()).log(Level.INFO, String.format("Disconnected from %s", host));
                }
            }
        } finally {
            closed.stream().forEach((SqlConnectionDependency d) -> d.connection.set(null));
            if (svc != null)
                svc.shutdownNow();
        }
    }
    
    /**
     * Class which is instantiated to close the SQL {@link Connection} after 1 second without any SQL connection dependencies.
     */
    static class DelayedConnectionCloser implements Callable<Boolean> {
        private static ScheduledExecutorService closeService;
        private static Optional<Future<?>> closeTask = Optional.empty();
        private static LocalDateTime closeAt;

        static void start() {
            closeAt = LocalDateTime.now().plusSeconds(1);
            if (closeTask.isPresent() && !closeTask.get().isDone())
                return;
            if (closeService == null)
                closeService = Executors.newSingleThreadScheduledExecutor();
            DelayedConnectionCloser closer = new DelayedConnectionCloser();
            closeTask = Optional.of(closeService.schedule(closer, 100, TimeUnit.MILLISECONDS));
        }
        
        @Override
        public Boolean call() throws Exception {
            synchronized(MONITOR) {
                // If as SQL dependency was started, do not close the connection, and return false to indicate connection was not closed.
                if (LATEST != null)
                    return false;
                // If the SQL connection was already closed, we can return true to indicate that the connection is closed.
                if (CONNECTION == null)
                    return true;
                // See if we reached the date and time when the connection is supposed to be closed.
                if (LocalDateTime.now().compareTo(closeAt) > 0) {
                    String host;
                    try (Connection c = CONNECTION) {
                        host = c.getMetaData().getURL();
                        CONNECTION = null;
                    }
                    Logger.getLogger(getClass().getName()).log(Level.INFO, String.format("Disconnected from %s", host));
                    return true;
                }
                // Start another delayed close task.
                closeTask = Optional.of(closeService.schedule(this, 100, TimeUnit.MILLISECONDS));
            }
            return false;
        }
    }
    
    /**
     * Gets a value with an open SQL connection.
     * @param <T> The type of return value.
     * @param func Callback which produces the return value.
     * @return The value produced by the specified function.
     * @throws SQLException 
     */
    public static final <T> T get(Function<Connection, T> func) throws SQLException {
        T result;
        try (SqlConnectionDependency dep = new SqlConnectionDependency()) {
            result = func.apply(dep.getConnection());
        }
        return result;
    }
    
    /**
     * Executes a consumer, passing an open SQL connection.
     * @param consumer The callback to invoke.
     * @throws SQLException 
     */
    public static void apply(Consumer<Connection> consumer) throws SQLException {
        try (SqlConnectionDependency dep = new SqlConnectionDependency()) {
            consumer.accept(dep.getConnection());
        }
    }
}
