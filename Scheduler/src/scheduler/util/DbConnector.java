package scheduler.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import scheduler.AppConfig;

/**
 * Manages SQL {@link java.sql.Connection} dependencies.
 * SQL connections are opened when needed, and then closes the connection after there have been no dependencies after a predetermined delay.
 * This allows nested method calls to use the same database connection without having to open several different connections.
 * @author Leonard T. Erwine
 */
public final class DbConnector implements AutoCloseable {
    //<editor-fold defaultstate="collapsed" desc="Fields and Properties">

    private static final Object SYNC_ROOT = new Object();
    
    /**
     * Name of database driver.
     */
    public static final String DB_DRIVER = "com.mysql.jdbc.Driver";
    
    /**
     * Number of seconds to delay closing DB connection after last DbConnector has closed.
     */
    public static final int CONNECTION_CLOSE_DELAY_SECONDS = 1;

    private static final Logger LOG = Logger.getLogger(DbConnector.class.getName());
    
    private static Future<?> closeTask = null;
    private static LocalDateTime closeAt;
    
    //<editor-fold defaultstate="collapsed" desc="SQL connection dependency tracking fields">
    
    // The latest in the chain of SQL connection dependencies or null if there are no SQL connection dependencies.
    private static DbConnector LATEST = null;
    
    // The currently opened SQL connection or null if no connection is open.
    private static Connection CONNECTION = null;
    
    // The SQL connection dependency that precedes the current one.
    private DbConnector previous;
    
    // The SQL connection dependency that follows the current one.
    private DbConnector next = null;
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="connection property">
    
    private Connection connection;
    
    /**
     * Gets the current SQL DB {@link java.sql.Connection}.
     * @return The current opened SQL DB {@link java.sql.Connection} or {@code null} if the current DbConnector is in a closed state.
     */
    public Connection getConnection() { return connection; }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="state property">
    
    public static final int STATE_NOT_CONNECTED = 0;
    
    public static final int STATE_CONNECTING = 1;
    
    public static final int STATE_CONNECTED = 2;
    
    public static final int STATE_CONNECTION_ERROR = -1;
    
    private static int CURRENT_STATE = STATE_NOT_CONNECTED;
    
    public int getState() { return (null == connection) ? STATE_NOT_CONNECTED : CURRENT_STATE; }
    //</editor-fold>
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Constructors">

    /**
     * Initializes a new DbConnector object with an opened {@link java.sql.Connection}.
     * 
     * @throws SQLException if a database access error occurs or a connection timeout threshold has been exceeded.
     * @throws ClassNotFoundException if the driver class was not found.
     */
    public DbConnector() throws SQLException, ClassNotFoundException {
        this(false);
    }
    
    /**
     * Initializes a new DbConnector object.
     * 
     * @param doNotOpen Do not automatically open a new {@link java.sql.Connection}.
     * @throws SQLException if a database access error occurs or a connection timeout threshold has been exceeded.
     * @throws ClassNotFoundException if the driver class was not found.
     */
    public DbConnector(boolean doNotOpen) throws SQLException, ClassNotFoundException {
        connection = null;
        if (!doNotOpen)
            open();
    }
    
    //</editor-fold>

    /**
     * Ensures that the current DbConnector is an an opened state, opening a new SQL DB {@link java.sql.Connection} if necessary.
     * @return an opened SQL DB {@link java.sql.Connection}.
     * @throws SQLException if a database access error occurs or a connection timeout threshold has been exceeded.
     * @throws ClassNotFoundException if the SQL database driver class was not found.
     */
    public final Connection open() throws SQLException, ClassNotFoundException {
        synchronized (SYNC_ROOT) {
            // connection property is null when the current DbConnector is in a closed state.
            if (null == connection) {
                if (CONNECTION != null && !CONNECTION.isClosed()) {
                    if ((previous = LATEST) != null)
                        previous.next = this;
                    LATEST = this;
                    connection = CONNECTION;
                    return CONNECTION;
                }
            } else if (!CONNECTION.isClosed())
                return CONNECTION;
        
            CURRENT_STATE = STATE_CONNECTING;
            try {
                // Initialize database driver class
                Class.forName(DB_DRIVER);

                // Make new connection.
                String url = AppConfig.getConnectionUrl();
                LOG.log(Level.INFO, String.format("Connecting to %s", url));
                CONNECTION = (Connection)DriverManager.getConnection(url, AppConfig.getDbLoginName(), AppConfig.getDbLoginPassword());
                for (DbConnector d = LATEST; d != null; d = d.previous)
                    d.connection = CONNECTION;
                LOG.log(Level.INFO, String.format("Connected to %s", url));
                CURRENT_STATE = STATE_CONNECTED;
            } finally {
                if (CURRENT_STATE == STATE_CONNECTING) {
                    CURRENT_STATE = STATE_CONNECTION_ERROR;
                    CONNECTION = null;
                }
                for (DbConnector d = LATEST; d != null; d = d.previous)
                    d.connection = CONNECTION;
                if (connection == null) {
                    if ((previous = LATEST) != null)
                        previous.next = this;
                    LATEST = this;
                    connection = CONNECTION;
                }
            }
        }
        return CONNECTION;
    }
    
    /**
     * Closes the current SQL connection dependency.
     * If this is the last DbConnector that currently requires an SQL connection, then the actual
     * SQL DB {@link java.sql.Connection} will be closed after the predetermined delay.
     */
    @Override
    public final void close() {
        synchronized (SYNC_ROOT) {
            if (connection == null)
                return;
            try {
                if (next == null) {
                    if ((LATEST = previous) != null)
                        previous = previous.next = null;
                    else {
                        closeAt = LocalDateTime.now().plusSeconds(CONNECTION_CLOSE_DELAY_SECONDS);
                        if (closeTask != null)
                            return;
                        // Close SQL connection in one second if it's not needed.
                        scheduleClose();
                    }
                } else {
                    if ((next.previous = previous) != null) {
                        previous.next = next;
                        previous = null;
                    }
                    next = null;
                }
            }
            finally { connection = null; }
        }
    }
    
    // Schedules a task to close the DB connection after a predetermined delay.
    private static void scheduleClose() {
        synchronized (SYNC_ROOT) {
            ScheduledExecutorService svc = Executors.newSingleThreadScheduledExecutor();
            try {
                closeTask = svc.schedule(() -> {
                    checkClose();
                }, CONNECTION_CLOSE_DELAY_SECONDS, TimeUnit.SECONDS);
            } finally { svc.shutdown(); }
        }
    }
    
    // Invoked from separate thread to close the DB connection after a predetermined delay.
    private static void checkClose() {
        synchronized (SYNC_ROOT) {
            closeTask = null;
            // If LATEST != null, then do not close. If CONNECTION is null, connection is already closed.
            if (null != LATEST || null == CONNECTION)
                return;
            // See if we reached the date and time when the connection is supposed to be closed.
            if (LocalDateTime.now().compareTo(closeAt) < 0)
                scheduleClose(); // Check back later to see if we need to close the connection.
            else {
                try { CONNECTION.close(); }
                catch (SQLException ex) {
                    LOG.log(Level.SEVERE, "Error closing database connection", ex);
                } finally {
                    CONNECTION = null;
                    CURRENT_STATE = STATE_NOT_CONNECTED;
                }
                LOG.log(Level.INFO, String.format("Disconnected from %s", AppConfig.getDbServerName()));
            }
        }
    }
    
    /**
     * Forces SQL {@link Connection} to be closed immediately.
     * @throws SQLException 
     */
    public static void forceClose() throws SQLException {
        synchronized (SYNC_ROOT) {
            ScheduledExecutorService svc = null;
            ArrayList<DbConnector> closed = new ArrayList<>();
            try {
                Connection c = CONNECTION;
                CONNECTION = null;
                CURRENT_STATE = STATE_NOT_CONNECTED;
                if (closeTask != null) {
                    closeTask.cancel(true);
                    closeTask = null;
                }
                // Remove each dependency, but don't call the "close" method.
                // Otherwise, when the thread which created them calls "close", it will throw an exception.
                DbConnector d = LATEST;
                LATEST = null;
                while (d != null) {
                    closed.add(d);
                    DbConnector p = d.previous;
                    d.previous = d.next = null;
                    d = p;
                }
                // Make sure another thread hasn't already closed the database connection.
                if (c != null) {
                    c.close();
                   LOG.log(Level.INFO, String.format("Disconnected from %s", AppConfig.getDbServerName()));
                }
            } finally {
                try {
                    closed.stream().forEach((DbConnector d) -> d.connection = null);
                }
                finally {
                    if (svc != null)
                        svc.shutdownNow();
                }
            }
        }
    }
    
    /**
     * Performs an operation using a {@link DbConnectionConsumer}, providing the opened SQL connection.
     * @param consumer The operation to perform.
     * @throws Exception if unable to open a connection or perform the operation.
     */
    public static void apply(DbConnectionConsumer consumer) throws Exception {
        Objects.requireNonNull(consumer);
        try (DbConnector dep = new DbConnector()) {
            consumer.accept(dep.getConnection());
        }
    }
    
    /**
     * Produces a result value using a {@link DbConnectedCallable}, providing the opened SQL connection.
     * @param <T> The return value type.
     * @param callable The task that produces the result value.
     * @return The value produced by the {@link DbConnectedCallable} delegate.
     * @throws Exception if unable to open a connection or perform the operation.
     */
    public static <T> T call(DbConnectedCallable<T> callable) throws Exception {
        Objects.requireNonNull(callable);
        try (DbConnector dep = new DbConnector()) {
            return callable.call(dep.getConnection());
        }
    }
}
