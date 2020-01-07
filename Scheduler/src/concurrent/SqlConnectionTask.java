/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrent;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import scheduler.AppConfig;

/**
 *
 * @author erwinel
 * @param <T>
 */
public abstract class SqlConnectionTask<T> extends Task<T> {
    private static final String DB_DRIVER = "com.mysql.jdbc.Driver";
    private static final Logger LOG = Logger.getLogger(SqlConnectionTask.class.getName());
    private static final Object MONITOR = new Object();
    private static SqlConnectionTask<?> LATEST_TASK = null;
    private static Connection CONNECTION = null;
    private static Future<?> SCHEDULED_SHUTDOWN;
    private final Object monitor;
    private SqlConnectionTask<?> previous_task;
    private SqlConnectionTask<?> next_task;
    private final ReadOnlyBooleanWrapper dbConnected;

    public boolean isDbConnected() { return dbConnected.get(); }

    public ReadOnlyBooleanProperty dbConnectedProperty() { return dbConnected.getReadOnlyProperty(); }
    
    private void setDbConnected(boolean value) {
        Platform.runLater(() -> {
            synchronized(monitor) {
                dbConnected.set(value);
            }
        });
    }
    
    protected SqlConnectionTask() {
        super();
        dbConnected = new ReadOnlyBooleanWrapper(false);
        monitor = new Object();
        previous_task = null;
        next_task = null;
    }
    
    public static <T extends Task<?>> T schedule(T task, long delay, TimeUnit unit) {
        ScheduledExecutorService svc = Executors.newSingleThreadScheduledExecutor();
        try {
            svc.schedule(task, delay, unit);
        } finally {
            svc.shutdown();
        }
        return task;
    }
    
    public static <T extends Task<?>> T execute(T task) {
        ExecutorService svc = Executors.newSingleThreadExecutor();
        try {
            svc.execute(task);
        } finally {
            svc.shutdown();
        }
        return task;
    }
    
    public static SqlConnectionTask<?> fromRunnable(DbConnectedRunnable runnable) {
        Objects.requireNonNull(runnable);
        return new SqlConnectionTaskImpl<>((Connection connection) -> {
            runnable.run(connection);
            return null;
        });
    }
    
    public static SqlConnectionTask<?> run(DbConnectedRunnable runnable) {
        return execute(fromRunnable(runnable));
    }

    public static SqlConnectionTask<?> scheduleRun(DbConnectedRunnable runnable, long delay, TimeUnit unit) {
        return schedule(fromRunnable(runnable), delay, unit);
    }

    public static <T> SqlConnectionTask<T> fromSupplier(DbConnectedSupplier<T> supplier) {
        return new SqlConnectionTaskImpl<>(supplier);
    }
    
    public static <T> SqlConnectionTask<T> get(DbConnectedSupplier<T> supplier) {
        return execute(fromSupplier(supplier));
    }
    
    public static <T> SqlConnectionTask<T> scheduleGet(DbConnectedSupplier<T> supplier, long delay, TimeUnit unit) {
        return schedule(fromSupplier(supplier), delay, unit);
    }
    
    public static <T> SqlConnectionTask<?> fromConsumer(DbConnectedSupplier<T> supplier, DbConnectedConsumer<T> consumer) {
        Objects.requireNonNull(consumer);
        Objects.requireNonNull(supplier);
        return new SqlConnectionTaskImpl<>((Connection connection) -> {
            consumer.accept(supplier.get(connection), connection);
            return null;
        });
    }

    public static <T> SqlConnectionTask<?> fromConsumer(DbConnectedSupplier<T> supplier, Consumer<T> consumer) {
        Objects.requireNonNull(consumer);
        Objects.requireNonNull(supplier);
        return new SupplyingSqlConnectionTask<>(supplier, (T v) -> {
            consumer.accept(v);
            return null;
        });
    }
    
    public static <T> SqlConnectionTask<?> fromConsumer(Supplier<T> supplier, DbConnectedConsumer<T> consumer) {
        Objects.requireNonNull(consumer);
        Objects.requireNonNull(supplier);
        return new SuppliedSqlConnectionTask<>(supplier, (T value, Connection connection) -> {
            consumer.accept(value, connection);
            return null;
        });
    }
    
    public static <T> SqlConnectionTask<?> fromConsumer(T value, DbConnectedConsumer<T> consumer) {
        Objects.requireNonNull(consumer);
        return new SqlConnectionTaskImpl<>((Connection connection) -> {
            consumer.accept(value, connection);
            return null;
        });
    }
    
    public static <T> SqlConnectionTask<?> accept(DbConnectedSupplier<T> supplier, DbConnectedConsumer<T> consumer) {
        return execute(fromConsumer(supplier, consumer));
    }
    
    public static <T> Task<?> accept(DbConnectedSupplier<T> supplier, Consumer<T> consumer) {
        return execute(fromConsumer(supplier, consumer));
    }
    
    public static <T> SqlConnectionTask<?> accept(Supplier<T> supplier, DbConnectedConsumer<T> consumer) {
        return execute(fromConsumer(supplier, consumer));
    }
    
    public static <T> SqlConnectionTask<?> accept(T value, DbConnectedConsumer<T> consumer) {
        return execute(fromConsumer(value, consumer));
    }
    
    public static <T> SqlConnectionTask<?> scheduleAccept(DbConnectedSupplier<T> supplier, DbConnectedConsumer<T> consumer, long delay, TimeUnit unit) {
        return schedule(fromConsumer(supplier, consumer), delay, unit);
    }
    
    public static <T> Task<?> scheduleAccept(DbConnectedSupplier<T> supplier, Consumer<T> consumer, long delay, TimeUnit unit) {
        return schedule(fromConsumer(supplier, consumer), delay, unit);
    }
    
    public static <T> SqlConnectionTask<?> scheduleAccept(Supplier<T> supplier, DbConnectedConsumer<T> consumer, long delay, TimeUnit unit) {
        return schedule(fromConsumer(supplier, consumer), delay, unit);
    }
    
    public static <T> SqlConnectionTask<?> scheduleAccept(T value, DbConnectedConsumer<T> consumer, long delay, TimeUnit unit) {
        return schedule(fromConsumer(value, consumer), delay, unit);
    }
    
    public static <T, R> SqlConnectionTask<R> fromFunction(DbConnectedSupplier<T> supplier, DbConnectedFunction<T, R> function) {
        Objects.requireNonNull(function);
        Objects.requireNonNull(supplier);
        return new SqlConnectionTaskImpl<>((Connection connection) -> {
            return function.apply(supplier.get(connection), connection);
        });
    }
    
    public static <T, R> SqlConnectionTask<R> fromFunction(DbConnectedSupplier<T> supplier, Function<T, R> function) {
        return new SupplyingSqlConnectionTask<>(supplier, function);
    }
    
    public static <T, R> SqlConnectionTask<R> fromFunction(Supplier<T> supplier, DbConnectedFunction<T, R> function) {
        return new SuppliedSqlConnectionTask<>(supplier, function);
    }
    
    public static <T, R> SqlConnectionTask<R> fromFunction(T value, DbConnectedFunction<T, R> function) {
        Objects.requireNonNull(function);
        return new SqlConnectionTaskImpl<>((Connection connection) -> {
            return function.apply(value, connection);
        });
    }
    
    public static <T, R> SqlConnectionTask<R> apply(DbConnectedSupplier<T> supplier, DbConnectedFunction<T, R> function) {
        return execute(fromFunction(supplier, function));
    }
    
    public static <T, R> SqlConnectionTask<R> apply(DbConnectedSupplier<T> supplier, Function<T, R> function) {
        return execute(fromFunction(supplier, function));
    }
    
    public static <T, R> SqlConnectionTask<R> apply(Supplier<T> supplier, DbConnectedFunction<T, R> function) {
        return execute(fromFunction(supplier, function));
    }
    
    public static <T, R> SqlConnectionTask<R> apply(T value, DbConnectedFunction<T, R> function) {
        return execute(fromFunction(value, function));
    }
    
    public static <T, R> SqlConnectionTask<R> scheduleApply(DbConnectedSupplier<T> supplier, DbConnectedFunction<T, R> function, long delay, TimeUnit unit) {
        return schedule(fromFunction(supplier, function), delay, unit);
    }
    
    public static <T, R> SqlConnectionTask<R> scheduleApply(DbConnectedSupplier<T> supplier, Function<T, R> function, long delay, TimeUnit unit) {
        return schedule(fromFunction(supplier, function), delay, unit);
    }
    
    public static <T, R> SqlConnectionTask<R> scheduleApply(Supplier<T> supplier, DbConnectedFunction<T, R> function, long delay, TimeUnit unit) {
        return schedule(fromFunction(supplier, function), delay, unit);
    }
    
    public static <T, R> SqlConnectionTask<R> scheduleApply(T value, DbConnectedFunction<T, R> function, long delay, TimeUnit unit) {
        return schedule(fromFunction(value, function), delay, unit);
    }
    
    public static <T> SqlConnectionTask<Boolean> fromPredicate(DbConnectedSupplier<T> supplier, DbConnectedPredicate<T> predicate) {
        Objects.requireNonNull(predicate);
        Objects.requireNonNull(supplier);
        return new SqlConnectionTaskImpl<>((Connection connection) -> {
            return predicate.test(supplier.get(connection), connection);
        });
    }

    public static <T> SqlConnectionTask<Boolean> fromPredicate(DbConnectedSupplier<T> supplier, Predicate<T> predicate) {
        Objects.requireNonNull(predicate);
        return new SupplyingSqlConnectionTask<>(supplier, (T value) -> predicate.test(value));
    }

    public static <T> SqlConnectionTask<Boolean> fromPredicate(Supplier<T> supplier, DbConnectedPredicate<T> predicate) {
        Objects.requireNonNull(predicate);
        return new SuppliedSqlConnectionTask<>(supplier, (T value, Connection connection) -> predicate.test(value, connection));
    }

    public static <T> SqlConnectionTask<Boolean> fromPredicate(T value, DbConnectedPredicate<T> predicate) {
        Objects.requireNonNull(predicate);
        return new SqlConnectionTaskImpl((Connection connection) -> {
            return predicate.test(value, connection);
        });
    }

    public static <T> SqlConnectionTask<Boolean> test(DbConnectedSupplier<T> supplier, DbConnectedPredicate<T> predicate) {
        return execute(fromPredicate(supplier, predicate));
    }
    
    public static <T> SqlConnectionTask<Boolean> test(DbConnectedSupplier<T> supplier, Predicate<T> predicate) {
        return execute(fromPredicate(supplier, predicate));
    }
    
    public static <T> SqlConnectionTask<Boolean> test(Supplier<T> supplier, DbConnectedPredicate<T> predicate) {
        return execute(fromPredicate(supplier, predicate));
    }
    
    public static <T> SqlConnectionTask<Boolean> test(T value, DbConnectedPredicate<T> predicate) {
        return execute(fromPredicate(value, predicate));
    }
    
    public static <T> SqlConnectionTask<Boolean> scheduleTest(DbConnectedSupplier<T> supplier, DbConnectedPredicate<T> predicate, long delay, TimeUnit unit) {
        return schedule(fromPredicate(supplier, predicate), delay, unit);
    }
    
    public static <T> SqlConnectionTask<Boolean> scheduleTest(DbConnectedSupplier<T> supplier, Predicate<T> predicate, long delay, TimeUnit unit) {
        return schedule(fromPredicate(supplier, predicate), delay, unit);
    }
    
    public static <T> SqlConnectionTask<Boolean> scheduleTest(Supplier<T> supplier, DbConnectedPredicate<T> predicate, long delay, TimeUnit unit) {
        return schedule(fromPredicate(supplier, predicate), delay, unit);
    }
    
    public static <T> SqlConnectionTask<Boolean> scheduleTest(T value, DbConnectedPredicate<T> predicate, long delay, TimeUnit unit) {
        return schedule(fromPredicate(value, predicate), delay, unit);
    }
    
    public static SqlConnectionTask<Boolean> fromBooleanSupplier(DbConnectedBooleanSupplier supplier) {
        Objects.requireNonNull(supplier);
        return new SqlConnectionTaskImpl<>((Connection connection) -> {
            return supplier.getAsBoolean(connection);
        });
    }
    
    public static SqlConnectionTask<Boolean> getAsBoolean(DbConnectedBooleanSupplier supplier) {
        return execute(fromBooleanSupplier(supplier));
    }
    
    public static SqlConnectionTask<Boolean> scheduleGetAsBoolean(DbConnectedBooleanSupplier supplier, long delay, TimeUnit unit) {
        return schedule(fromBooleanSupplier(supplier), delay, unit);
    }
    
    public static void cancelAll() {
        ArrayList<SqlConnectionTask<?>> allTasks = new ArrayList<>();
        synchronized (MONITOR) {
            if (SCHEDULED_SHUTDOWN != null)
                SCHEDULED_SHUTDOWN.cancel(true);
            SqlConnectionTask<?> task = LATEST_TASK;
            if (task != null) {
                LATEST_TASK = null;
                allTasks.add(task);
                while ((task = task.previous_task) != null) {
                    task.next_task = task.next_task.previous_task = null;
                    allTasks.add(task);
                }
            }
        }
        try {
            allTasks.stream().forEach((SqlConnectionTask<?> t) -> {
                if (t.isRunning() && !t.isCancelled())
                    t.cancel(true);
                t.setDbConnected(false);
            });
        } finally {
            onCloseConnection();
        }
    }
    
    protected <V> V fromConnectedSupplier(DbConnectedSupplier<V> supplier) throws Exception {
        synchronized (MONITOR) {
            if (SCHEDULED_SHUTDOWN != null) {
                SCHEDULED_SHUTDOWN.cancel(true);
                SCHEDULED_SHUTDOWN = null;
            }
            if (CONNECTION == null) {
                Class.forName(DB_DRIVER);
                String url = AppConfig.getConnectionUrl();
                LOG.log(Level.INFO, String.format("Opening database connection to %s", url));
                CONNECTION = DriverManager.getConnection(url, AppConfig.getDbLoginName(), AppConfig.getDbLoginPassword());
                LOG.log(Level.INFO, String.format("Connected to database %s", url));
            }
            if ((previous_task = LATEST_TASK) != null)
                previous_task.next_task = this;
        }
        setDbConnected(true);
        try {
            return supplier.get(CONNECTION);
        } finally {
            try {
                synchronized (MONITOR) {
                    if (next_task != null) {
                        if ((next_task.previous_task = previous_task) != null) {
                            previous_task.next_task = next_task;
                            previous_task = null;
                        }
                        next_task = null;
                    } else if ((LATEST_TASK = previous_task) != null)
                        previous_task = previous_task.next_task = null;
                    else {
                        ScheduledExecutorService svc = Executors.newSingleThreadScheduledExecutor();
                        SCHEDULED_SHUTDOWN = svc.schedule(() -> {
                            if (!this.isCancelled())
                                onCloseConnection();
                        }, 2, TimeUnit.SECONDS);
                    }
                }
            } finally {
                setDbConnected(false);
            }
        }
    }

    private static void onCloseConnection() throws RuntimeException {
        Connection connection;
        synchronized (MONITOR) {
            if ((connection = CONNECTION) == null || LATEST_TASK != null)
                return;
            CONNECTION = null;
            SCHEDULED_SHUTDOWN = null;
        }
        LOG.log(Level.INFO, String.format("Closing database connection"));
        try {
            connection.close();
        } catch (SQLException ex) {
            throw new RuntimeException("Unable to close database connection", ex);
        }
        LOG.log(Level.INFO, String.format("Database connection closed"));
    }
}
