package scheduler.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import scheduler.AppResources;

/**
 * Manages SQL {@link java.sql.Connection} dependencies. SQL connections are opened when needed, and then closes the connection after there have been
 * no dependencies after a predetermined delay. This allows nested method calls to use the same database connection without having to pass the
 * connection instance.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public final class DbConnector implements AutoCloseable {

    public static final int STATE_NOT_CONNECTED = 0;

    public static final int STATE_CONNECTING = 1;

    public static final int STATE_CONNECTED = 2;

    public static final int STATE_CONNECTION_ERROR = -1;

    /**
     * Number of seconds to delay closing DB connection after last DbConnector has closed.
     */
    public static final int CONNECTION_CLOSE_DELAY_MILLISECONDS = 1000;

    /**
     * Name of database driver.
     */
    public static final String DB_DRIVER = "com.mysql.jdbc.Driver";

    private static final Object SYNC_ROOT = new Object();

    private static final Logger LOG = Logger.getLogger(DbConnector.class.getName());

    // The currently opened SQL connection or null if no connection is open.
    private static Connection CONNECTION = null;

    private static int CURRENT_STATE = STATE_NOT_CONNECTED;

    // The latest in the chain of SQL connection dependencies or null if there are no SQL connection dependencies.
    private static DbConnector LATEST = null;

    private static long CLOSE_AT;

    private static Future<Boolean> CLOSE_CONNECTION_TASK;

    private static void scheduleClose(int delayMilliseconds) {
        synchronized (SYNC_ROOT) {
            CLOSE_AT = System.currentTimeMillis() + (long) delayMilliseconds;
            if (CLOSE_CONNECTION_TASK != null) {
                return;
            }
            ScheduledExecutorService svc = Executors.newSingleThreadScheduledExecutor();
            try {
                CLOSE_CONNECTION_TASK = svc.schedule(() -> checkClose(), delayMilliseconds, TimeUnit.MILLISECONDS);
            } finally {
                svc.shutdown();
            }
        }
    }

    // Invoked from separate thread to close the DB connection after a predetermined delay.
    private static boolean checkClose() {
        synchronized (SYNC_ROOT) {
            CLOSE_CONNECTION_TASK = null;
            // If LATEST != null, then do not close. If CONNECTION is null, connection is already closed.
            if (null != LATEST || null == CONNECTION) {
                return false;
            }
            long closeIn = CLOSE_AT - System.currentTimeMillis();
            int delayMilliseconds = (closeIn < 1L) ? 0 : ((closeIn > (long) CONNECTION_CLOSE_DELAY_MILLISECONDS)
                    ? CONNECTION_CLOSE_DELAY_MILLISECONDS : (int) closeIn);
            // See if we reached the date and time when the connection is supposed to be closed.
            if (delayMilliseconds > 0) {
                LOG.fine(() -> String.format("Not ready to close. Checking again in %d milliseconds", delayMilliseconds));
                scheduleClose(delayMilliseconds); // Check back later to see if we need to close the connection.
                return false;
            }
            try {
                CONNECTION.close();
            } catch (SQLException ex) {
                LOG.log(Level.SEVERE, String.format("Error closing database connection to %s",
                        AppResources.getDbServerName()), ex);
            } finally {
                CONNECTION = null;
                CURRENT_STATE = STATE_NOT_CONNECTED;
            }
            LOG.fine(() -> String.format("Disconnected from %s", AppResources.getDbServerName()));
        }
        return true;
    }

    /**
     * Forces {@link #CONNECTION} and all open {@link DbConnector}s to be closed immediately.
     *
     * @throws SQLException
     */
    public static void forceCloseAll() throws SQLException {
        synchronized (SYNC_ROOT) {
            Connection c = CONNECTION;
            CONNECTION = null;
            if (CURRENT_STATE != STATE_CONNECTION_ERROR) {
                CURRENT_STATE = STATE_NOT_CONNECTED;
            }
            try {
                if (CLOSE_CONNECTION_TASK != null) {
                    CLOSE_CONNECTION_TASK.cancel(true);
                }
            } finally {
                CLOSE_CONNECTION_TASK = null;
                // Remove each dependency, but don't call the "close" method.
                // Otherwise, when the thread which created them calls "close", it will throw an exception.
                if (null != LATEST) {
                    DbConnector d = LATEST;
                    while (null != (LATEST = d.previous)) {
                        LATEST.connection = null;
                        d.previous = LATEST.next = null;
                    }
                    d.connection = null;
                    d.next = null;
                }
                // Make sure another thread hasn't already closed the database connection.
                if (c != null) {
                    try {
                        c.close();
                        LOG.fine(() -> String.format("Disconnected from %s", AppResources.getDbServerName()));
                    } catch (SQLException ex) {
                        LOG.log(Level.SEVERE, String.format("Error closing database connection to %s", AppResources.getDbServerName()), ex);
                    }
                }
            }
        }
    }

    /**
     * Performs an operation using a {@link ThrowableConsumer}, providing the opened SQL connection.
     *
     * @param consumer The operation to perform.
     * @throws java.sql.SQLException if unable to perform the database operation.
     * @throws java.lang.ClassNotFoundException if unable to load the database driver.
     */
    public static void accept(ThrowableConsumer<Connection, SQLException> consumer) throws SQLException, ClassNotFoundException {
        Objects.requireNonNull(consumer);
        try (DbConnector dep = new DbConnector()) {
            consumer.accept(dep.getConnection());
        }
    }

    /**
     * Produces a result value using a {@link ThrowableFunction}, providing the opened SQL connection.
     *
     * @param <T> The return value type.
     * @param callable The task that produces the result value.
     * @return The value produced by the {@link ThrowableFunction} delegate.
     * @throws java.sql.SQLException if unable to perform the database operation.
     * @throws java.lang.ClassNotFoundException if unable to load the database driver.
     */
    public static <T> T apply(ThrowableFunction<Connection, T, SQLException> callable) throws SQLException, ClassNotFoundException {
        Objects.requireNonNull(callable);
        try (DbConnector dep = new DbConnector()) {
            return callable.apply(dep.getConnection());
        }
    }

    // The SQL connection dependency that precedes the current one.
    private DbConnector previous = null;

    // The SQL connection dependency that follows the current one.
    private DbConnector next = null;

    private Connection connection = null;

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
        if (!doNotOpen) {
            open();
        }
    }

    /**
     * Gets the current SQL DB {@link java.sql.Connection}.
     *
     * @return The current opened SQL DB {@link java.sql.Connection} or {@code null} if the current DbConnector is in a closed state.
     */
    public Connection getConnection() {
        return connection;
    }

    public int getState() {
        return (null == connection) ? STATE_NOT_CONNECTED : CURRENT_STATE;
    }

    /**
     * Ensures that the current DbConnector is an an opened state, opening a new SQL DB {@link java.sql.Connection} if necessary.
     *
     * @return an opened SQL DB {@link java.sql.Connection}.
     * @throws SQLException if a database access error occurs or a connection timeout threshold has been exceeded.
     * @throws ClassNotFoundException if the SQL database driver class was not found.
     */
    public final Connection open() throws SQLException, ClassNotFoundException {
        synchronized (SYNC_ROOT) {
            // connection property is null when the current DbConnector is in a closed state.
            if (null != CONNECTION && !CONNECTION.isClosed()) {
                if (null == connection) {
                    if (null != LATEST) {
                        (previous = LATEST).next = this;
                    }
                    LATEST = this;
                }
                connection = CONNECTION;
                return connection;
            }

            CURRENT_STATE = STATE_CONNECTING;
            try {
                // Initialize database driver class
                Class.forName(DB_DRIVER);
                // Make new connection.
                String url = AppResources.getConnectionUrl();
                LOG.fine(() -> String.format("Connecting to %s", url));
                CONNECTION = Objects.requireNonNull(DriverManager.getConnection(url, AppResources.getDbLoginName(), AppResources.getDbLoginPassword()),
                        "DriverManager.getConnection returned null");
                LOG.fine(() -> String.format("Connected to %s", url));
                CURRENT_STATE = STATE_CONNECTED;
                String sql = "SET @@session.time_zone = '+00:00'";
                LOG.fine(() -> String.format("Executing sql statement: %s", sql));
                try (PreparedStatement ps = CONNECTION.prepareStatement(sql)) {
                    ps.execute();
                    LogHelper.logWarnings(CONNECTION, LOG);
                }
            } finally {
                if (CURRENT_STATE == STATE_CONNECTING) {
                    CURRENT_STATE = STATE_CONNECTION_ERROR;
                    CONNECTION = null;
                    forceCloseAll();
                } else {
                    for (DbConnector d = LATEST; d != null; d = d.previous) {
                        d.connection = CONNECTION;
                    }
                    if (connection == null) {
                        if (null != LATEST) {
                            (previous = LATEST).next = this;
                        }
                        LATEST = this;
                        connection = CONNECTION;
                    }
                }
            }
        }
        return Objects.requireNonNull(connection, "Connection could not be opened");
    }

    /**
     * Closes the current SQL connection dependency. If this is the last DbConnector that currently requires an SQL connection, then the actual SQL DB
     * {@link java.sql.Connection} will be closed after the predetermined delay.
     */
    @Override
    public final void close() {
        synchronized (SYNC_ROOT) {
            if (connection == null) {
                return;
            }
            try {
                if (next == null) {
                    if ((LATEST = previous) != null) {
                        previous = previous.next = null;
                    } else {
                        scheduleClose(CONNECTION_CLOSE_DELAY_MILLISECONDS);
                    }
                } else {
                    if ((next.previous = previous) != null) {
                        previous.next = next;
                        previous = null;
                    }
                    next = null;
                }
            } finally {
                connection = null;
            }
        }
    }

}
