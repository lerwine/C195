package scheduler;

import com.mysql.jdbc.Connection;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
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

/**
 * Manages SQL connection dependencies.
 * SQL connections are opened when needed, and then closes the connection after there have been no dependencies for 1 second.
 * @author Leonard T. Erwine
 */
public final class SqlConnectionDependency {
    private static final String DB_DRIVER = "com.mysql.jdbc.Driver";
    
    //<editor-fold defaultstate="collapsed" desc="Synchronization fields">
    
    // Synchronization object for changes that affect more than one SQL connection dependency.
    private static final Object ALL_DEPENDENCIES_LOCK = new Object();
    // Synchronization object for changes that affect only the current SQL connection dependency.
    private final Object currentDepencencyLock = new Object();
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Dependency chain fields">
    
    // The latest in a chain of SQL connection dependencies.
    private static Optional<SqlConnectionDependency> LATEST = Optional.empty();
    // The SQL connection dependency that precedes the current one.
    private Optional<SqlConnectionDependency> previous;
    // The SQL connection dependency that follows the current one.
    private Optional<SqlConnectionDependency> next;
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="connection property">
    
    // The currently opened SQL connection.
    private static Optional<Connection> CONNECTION = Optional.empty();
    
    public static final String PROP_CONNECTION = "connection property";
    
    /**
     * Gets the current {@link Connection}.
     * @return The current {@link Connection} or null if no SQL connection dependency is open.
     */
    public final Connection getconnection() {
        // Value of "open" field won't be changed during a lock on the current dependency.
        synchronized(currentDepencencyLock) {
            if (open)
                return CONNECTION.get();
        }
        return null;
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="open property">
    
    private boolean open;
    
    public static final String PROP_OPEN = "open";
    
    /**
     * Gets a value that indicates whether an SQL connection dependency is open.
     * @return {@code true} if an SQL connection dependency is open; otherwise, {@code false}.
     */
    public final boolean isOpen() { return open; }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Constructors">
    
    public SqlConnectionDependency(boolean open) throws SQLException {
        next = Optional.empty();
        if (open)
            open();
        else
            previous = Optional.empty();
    }
    
    public SqlConnectionDependency() {
        next = Optional.empty();
        previous = Optional.empty();
    }
    
    //</editor-fold>
    
    /**
     * Opens an SQL connection dependency.
     * If this is the first dependency, then a new SQL database {@link Connection} will be established.
     * @return An opened SQL database {@link Connection}.
     * @throws java.sql.SQLException
     */
    public final Connection open() throws SQLException {
        Connection result;
        // Value of "open" field won't be changed during a lock on the current dependency.
        synchronized(currentDepencencyLock) {
            if (open)
                throw new InternalException("SQL connection dependency is already open");
            // "next", "previous", "latest", "closeAt", "closeService" and "closeTask" properties should only be changed during a lock on all dependencies.
            synchronized(ALL_DEPENDENCIES_LOCK) {
                if ((previous = LATEST).isPresent())
                    LATEST = LATEST.get().next = Optional.of(this);
                else {
                    if (!CONNECTION.isPresent()) {
                        try { Class.forName(DB_DRIVER); }
                        catch (ClassNotFoundException ex) {
                            Logger.getLogger(SqlConnectionDependency.class.getName()).log(Level.SEVERE, null, ex);
                            throw new InternalException("Error getting driver class", ex);
                        }
                        String url = AppConfig.getConnectionUrl();
                        CONNECTION = Optional.of((Connection)DriverManager.getConnection(url, AppConfig.getDbLoginName(), AppConfig.getDbLoginPassword()));
                        Logger.getLogger(getClass().getName()).log(Level.INFO, String.format("Connected to %s", url));
                    }
                    LATEST = Optional.of(this);
                }
                open = true;
                // Store current connection in local variable since it will be referenced by property change support while not in a lock.
                result = CONNECTION.get();
            }
        }
        try { propertyChangeSupport.firePropertyChange(PROP_CONNECTION, null, result); }
        finally { propertyChangeSupport.firePropertyChange(PROP_OPEN, false, true); }
        return result;
    }
    
    /**
     * Closes the current SQL connection dependency.
     * @throws SQLException 
     */
    public final void close() throws SQLException {
        Connection oldConnection;
        // "open" field should only be changed during a lock on the current dependency.
        synchronized(currentDepencencyLock) {
            if (!open)
                throw new InternalException("SQL connection dependency is already closed");
            oldConnection = CONNECTION.get();
            // "next", "previous", "latest", "closeAt", "closeService" and "closeTask" properties should only be changed during a lock on all dependencies.
            synchronized(ALL_DEPENDENCIES_LOCK) {
                if (next.isPresent()) {
                    if ((next.get().previous = previous).isPresent()) {
                        previous.get().next = next;
                        previous = Optional.empty();
                    }
                    next = Optional.empty();
                } else {
                    if ((LATEST = previous).isPresent())
                        previous = previous.get().next = Optional.empty();
                    else {
                        previous = LATEST = Optional.empty();
                        // Close SQL connection in one second if it's not needed.
                        DelayedConnectionCloser.start();
                    }
                }
            }
            open = false;
        }
        try { propertyChangeSupport.firePropertyChange(PROP_CONNECTION, oldConnection, null); }
        finally { propertyChangeSupport.firePropertyChange(PROP_OPEN, true, false); }
    }
    
    /**
     * Forces SQL {@link Connection} to be closed immediately.
     * @throws SQLException 
     */
    public static void forceClose() throws SQLException {
        ScheduledExecutorService svc = null;
        try {
            // "next", "previous", "latest", "closeAt", "closeService" and "closeTask" properties should only be changed during a lock on all dependencies.
            synchronized(ALL_DEPENDENCIES_LOCK) {
                // We need to be sure that the "closeService" field is always set to null by invoking this method,
                // and we also need to shut it down.
                svc = DelayedConnectionCloser.closeService;
                DelayedConnectionCloser.closeService = null;
                // Remove each dependency, but don't call the "close" method.
                // Otherwise, when the thread which created them calls "close", it will throw an exception.
                while (LATEST.isPresent()) {
                    SqlConnectionDependency d = LATEST.get();
                    d.previous = (LATEST = d.previous).get().next = Optional.empty();
                }
                // Make sure another thread hasn't already closed the database connection.
                if (CONNECTION.isPresent()) {
                    // We need to set the "connection" field to empty before attempting to close it, so
                    // we don't end up in an endless close loop, should another thread try to open a dependency.
                    Connection c = CONNECTION.get();
                    String host = c.getHost();
                    CONNECTION = Optional.empty();
                    c.close();
                    Logger.getLogger(SqlConnectionDependency.class.getName()).log(Level.INFO, String.format("Disconnected from %s", host));
                }
            }
        } finally {
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
            synchronized(ALL_DEPENDENCIES_LOCK) {
                // If as SQL dependency was started, do not close the connection, and return false to indicate connection was not closed.
                if (LATEST.isPresent())
                    return false;
                // If the SQL connection was already closed, we can return true to indicate that the connection is closed.
                if (!CONNECTION.isPresent())
                    return true;
                // See if we reached the date and time when the connection is supposed to be closed.
                if (LocalDateTime.now().compareTo(closeAt) > 0) {
                    Connection c = CONNECTION.get();
                    String host = c.getHost();
                    CONNECTION = Optional.empty();
                    c.close();
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
        SqlConnectionDependency dep = new SqlConnectionDependency(true);
        T result;
        try { result = func.apply(dep.getconnection()); } finally { dep.close(); }
        return result;
    }
    
    /**
     * Executes a consumer, passing an open SQL connection.
     * @param consumer The callback to invoke.
     * @throws SQLException 
     */
    public static void apply(Consumer<Connection> consumer) throws SQLException {
        SqlConnectionDependency dep = new SqlConnectionDependency(true);
        try { consumer.accept(dep.getconnection()); } finally { dep.close(); }
    }
    
    private transient final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    /**
     * Adds a PropertyChangeListener.
     * @param listener The listener to add.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Removes a PropertyChangeListener.
     * @param listener The listener to remove.
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }
}
