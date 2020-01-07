/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrent;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;
import scheduler.AppConfig;

/**
 *
 * @author erwinel
 */
public class DbConnectableExecutor {
    private static final String DB_DRIVER = "com.mysql.jdbc.Driver";
    private static final Object MONITOR = new Object();
    private static ConnectionReservation LATEST;
    private static Connection CONNECTION = null;
    private static Future<?> SCHEDULED_CLOSE;
    
    public static Callable<?> fromRunnable(DbConnectedRunnable runnable) {
        Objects.requireNonNull(runnable);
        return new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                try (ConnectionReservation c = new ConnectionReservation()) { runnable.run(CONNECTION); }
                return null;
            }
        };
    }
    
    public static <T> Callable<T> fromSupplier(DbConnectedSupplier<T> supplier) {
        Objects.requireNonNull(supplier);
        return new Callable<T>() {
            @Override
            public T call() throws Exception {
                try (ConnectionReservation c = new ConnectionReservation()) { return supplier.get(CONNECTION); }
            }
        };
    }
    
    public static <T> Callable<?> fromConsumer(DbConnectedSupplier<T> supplier, DbConnectedConsumer<T> consumer) {
        return new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                try (ConnectionReservation c = new ConnectionReservation()) { consumer.accept(supplier.get(CONNECTION), CONNECTION); }
                return null;
            }
            
        };
    }
    
    public static <T> Callable<?> fromConsumer(DbConnectedSupplier<T> supplier, Consumer<T> consumer) {
        return new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                T value;
                try (ConnectionReservation c = new ConnectionReservation()) { value = supplier.get(CONNECTION); }
                consumer.accept(value);
                return null;
            }
            
        };
    }
    
    public static <T> Callable<?> fromConsumer(Supplier<T> supplier, DbConnectedConsumer<T> consumer) {
        return new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                T value = supplier.get();
                try (ConnectionReservation c = new ConnectionReservation()) { consumer.accept(value, CONNECTION); }
                return null;
            }
            
        };
    }
    
    public static <T> Callable<?> fromConsumer(T  value, DbConnectedConsumer<T> consumer) {
        return new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                try (ConnectionReservation c = new ConnectionReservation()) { consumer.accept(value, CONNECTION); }
                return null;
            }
            
        };
    }
    
    public static <T, U> Callable<?> fromBiConsumer(DbConnectedSupplier<T> supplier1, DbConnectedSupplier<U> supplier2, DbConnectedBiConsumer<T, U> consumer) {
        return new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                try (ConnectionReservation c = new ConnectionReservation()) { consumer.accept(supplier1.get(CONNECTION), supplier2.get(CONNECTION), CONNECTION); }
                return null;
            }
            
        };
    }
    
    public static <T, U> Callable<?> fromBiConsumer(DbConnectedSupplier<T> supplier1, DbConnectedSupplier<U> supplier2, BiConsumer<T, U> consumer) {
        return new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                T t;
                U u;
                try (ConnectionReservation c = new ConnectionReservation()) { 
                    t = supplier1.get(CONNECTION);
                    u = supplier2.get(CONNECTION);
                }
                consumer.accept(t, u);
                return null;
            }
            
        };
    }
    
    public static <T, U> Callable<?> fromBiConsumer(DbConnectedSupplier<T> supplier1, Supplier<U> supplier2, BiConsumer<T, U> consumer) {
        return new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                T t;
                try (ConnectionReservation c = new ConnectionReservation()) { t = supplier1.get(CONNECTION); }
                consumer.accept(t, supplier2.get());
                return null;
            }
            
        };
    }
    
    public static <T, U> Callable<?> fromBiConsumer(Supplier<T> supplier1, DbConnectedSupplier<U> supplier2, DbConnectedBiConsumer<T, U> consumer) {
        return new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                T t = supplier1.get();
                try (ConnectionReservation c = new ConnectionReservation()) { consumer.accept(t, supplier2.get(CONNECTION), CONNECTION); }
                return null;
            }
            
        };
    }
    
    public static <T, U> Callable<?> fromBiConsumer(Supplier<T> supplier1, DbConnectedSupplier<U> supplier2, BiConsumer<T, U> consumer) {
        return new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                T t = supplier1.get();
                U u;
                try (ConnectionReservation c = new ConnectionReservation()) {  u = supplier2.get(CONNECTION); }
                consumer.accept(t, u);
                return null;
            }
            
        };
    }
    
    public static <T, U> Callable<?> fromBiConsumer(Supplier<T> supplier1, Supplier<U> supplier2, DbConnectedBiConsumer<T, U> consumer) {
        return new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                T t = supplier1.get();
                U u = supplier2.get();
                try (ConnectionReservation c = new ConnectionReservation()) { consumer.accept(t, u, CONNECTION); }
                return null;
            }
            
        };
    }
    
    public static <T, U> Callable<?> fromBiConsumer(T t, DbConnectedSupplier<U> supplier2, DbConnectedBiConsumer<T, U> consumer) {
        return new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                try (ConnectionReservation c = new ConnectionReservation()) { consumer.accept(t, supplier2.get(CONNECTION), CONNECTION); }
                return null;
            }
            
        };
    }
    
    public static <T, U> Callable<?> fromBiConsumer(T t, DbConnectedSupplier<U> supplier2, BiConsumer<T, U> consumer) {
        return new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                U u;
                try (ConnectionReservation c = new ConnectionReservation()) { u = supplier2.get(CONNECTION); }
                consumer.accept(t, u);
                return null;
            }
            
        };
    }
    
    public static <T, U> Callable<?> fromBiConsumer(T t, Supplier<U> supplier2, DbConnectedBiConsumer<T, U> consumer) {
        return new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                U u = supplier2.get();
                try (ConnectionReservation c = new ConnectionReservation()) { consumer.accept(t, u, CONNECTION); }
                return null;
            }
            
        };
    }
    
    public static <T, U> Callable<?> fromBiConsumer(DbConnectedSupplier<T> supplier1, U u, DbConnectedBiConsumer<T, U> consumer) {
        return new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                try (ConnectionReservation c = new ConnectionReservation()) { consumer.accept(supplier1.get(CONNECTION), u, CONNECTION); }
                return null;
            }
            
        };
    }
    
    public static <T, U> Callable<?> fromBiConsumer(DbConnectedSupplier<T> supplier1, U u, BiConsumer<T, U> consumer) {
        return new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                T t;
                try (ConnectionReservation c = new ConnectionReservation()) { t = supplier1.get(CONNECTION); }
                consumer.accept(t, u);
                return null;
            }
            
        };
    }
    
    public static <T, U> Callable<?> fromBiConsumer(Supplier<T> supplier1, U u, DbConnectedBiConsumer<T, U> consumer) {
        return new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                T t = supplier1.get();
                try (ConnectionReservation c = new ConnectionReservation()) { consumer.accept(t, u, CONNECTION); }
                return null;
            }
            
        };
    }
    
    public static <T, U> Callable<?> fromBiConsumer(T t, U u, DbConnectedBiConsumer<T, U> consumer) {
        return new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                try (ConnectionReservation c = new ConnectionReservation()) { consumer.accept(t, u, CONNECTION); }
                return null;
            }
            
        };
    }
    
    public static <T, R> Callable<R> fromFunction(DbConnectedSupplier<T> supplier, DbConnectedFunction<T, R> function) {
        Objects.requireNonNull(supplier);
        Objects.requireNonNull(function);
        return new Callable<R>() {
            @Override
            public R call() throws Exception {
                try (ConnectionReservation c = new ConnectionReservation()) { return function.apply(supplier.get(CONNECTION), CONNECTION); }
            }
        };
    }
    
    public static <T, R> Callable<R> fromFunction(DbConnectedSupplier<T> supplier, Function<T, R> function) {
        Objects.requireNonNull(supplier);
        Objects.requireNonNull(function);
        return new Callable<R>() {
            @Override
            public R call() throws Exception {
                T value;
                try (ConnectionReservation c = new ConnectionReservation()) { value = supplier.get(CONNECTION); }
                return function.apply(value);
            }
        };
    }
    
    public static <T, R> Callable<R> fromFunction(Supplier<T> supplier, DbConnectedFunction<T, R> function) {
        Objects.requireNonNull(supplier);
        Objects.requireNonNull(function);
        return new Callable<R>() {
            @Override
            public R call() throws Exception {
                T value = supplier.get();
                try (ConnectionReservation c = new ConnectionReservation()) { return function.apply(value, CONNECTION); }
            }
        };
    }
    
    public static <T, R> Callable<R> fromFunction(T value, DbConnectedFunction<T, R> function) {
        Objects.requireNonNull(function);
        return new Callable<R>() {
            @Override
            public R call() throws Exception {
                try (ConnectionReservation c = new ConnectionReservation()) { return function.apply(value, CONNECTION); }
            }
        };
    }
    
    public static <T, U, R> Callable<R> fromBiFunction(DbConnectedSupplier<T> supplier1, DbConnectedSupplier<U> supplier2, DbConnectedBiFunction<T, U, R> function) {
        Objects.requireNonNull(supplier1);
        Objects.requireNonNull(supplier2);
        Objects.requireNonNull(function);
        return new Callable<R>() {
            @Override
            public R call() throws Exception {
                try (ConnectionReservation c = new ConnectionReservation()) { return function.apply(supplier1.get(CONNECTION), supplier2.get(CONNECTION), CONNECTION); }
            }
        };
    }
    
    public static <T, U, R> Callable<R> fromBiFunction(DbConnectedSupplier<T> supplier1, DbConnectedSupplier<U> supplier2, BiFunction<T, U, R> function) {
        Objects.requireNonNull(supplier1);
        Objects.requireNonNull(supplier2);
        Objects.requireNonNull(function);
        return new Callable<R>() {
            @Override
            public R call() throws Exception {
                T t;
                U u;
                try (ConnectionReservation c = new ConnectionReservation()) {
                    t = supplier1.get(CONNECTION);
                    u = supplier2.get(CONNECTION);
                }
                return function.apply(t, u);
            }
        };
    }
    
    public static <T, U, R> Callable<R> fromBiFunction(DbConnectedSupplier<T> supplier1, Supplier<U> supplier2, BiFunction<T, U, R> function) {
        Objects.requireNonNull(supplier1);
        Objects.requireNonNull(supplier2);
        Objects.requireNonNull(function);
        return new Callable<R>() {
            @Override
            public R call() throws Exception {
                T t;
                try (ConnectionReservation c = new ConnectionReservation()) {
                    t = supplier1.get(CONNECTION);
                }
                return function.apply(t, supplier2.get());
            }
        };
    }
    
    public static <T, U, R> Callable<R> fromBiFunction(Supplier<T> supplier1, DbConnectedSupplier<U> supplier2, DbConnectedBiFunction<T, U, R> function) {
        Objects.requireNonNull(supplier1);
        Objects.requireNonNull(supplier2);
        Objects.requireNonNull(function);
        return new Callable<R>() {
            @Override
            public R call() throws Exception {
                T t = supplier1.get();
                try (ConnectionReservation c = new ConnectionReservation()) {
                    return function.apply(t,  supplier2.get(CONNECTION), CONNECTION);
                }
            }
        };
    }
    
    public static <T, U, R> Callable<R> fromBiFunction(Supplier<T> supplier1, DbConnectedSupplier<U> supplier2, BiFunction<T, U, R> function) {
        Objects.requireNonNull(supplier1);
        Objects.requireNonNull(supplier2);
        Objects.requireNonNull(function);
        return new Callable<R>() {
            @Override
            public R call() throws Exception {
                T t = supplier1.get();
                U u;
                try (ConnectionReservation c = new ConnectionReservation()) {
                    u = supplier2.get(CONNECTION);
                }
                return function.apply(t, u);
            }
        };
    }
    
    public static <T, U, R> Callable<R> fromBiFunction(Supplier<T> supplier1, Supplier<U> supplier2, DbConnectedBiFunction<T, U, R> function) {
        Objects.requireNonNull(supplier1);
        Objects.requireNonNull(supplier2);
        Objects.requireNonNull(function);
        return new Callable<R>() {
            @Override
            public R call() throws Exception {
                T t = supplier1.get();
                U u = supplier2.get();
                try (ConnectionReservation c = new ConnectionReservation()) {
                    return function.apply(t,  u, CONNECTION);
                }
            }
        };
    }
    
    public static <T, U, R> Callable<R> fromBiFunction(DbConnectedSupplier<T> supplier1, U u, DbConnectedBiFunction<T, U, R> function) {
        Objects.requireNonNull(supplier1);
        Objects.requireNonNull(function);
        return new Callable<R>() {
            @Override
            public R call() throws Exception {
                try (ConnectionReservation c = new ConnectionReservation()) {
                    return function.apply(supplier1.get(CONNECTION), u, CONNECTION);
                }
            }
        };
    }
    
    public static <T, U, R> Callable<R> fromBiFunction(DbConnectedSupplier<T> supplier1, U u, BiFunction<T, U, R> function) {
        Objects.requireNonNull(supplier1);
        Objects.requireNonNull(function);
        return new Callable<R>() {
            @Override
            public R call() throws Exception {
                T t;
                try (ConnectionReservation c = new ConnectionReservation()) {
                    t = supplier1.get(CONNECTION);
                }
                return function.apply(t, u);
            }
        };
    }
    
    public static <T, U, R> Callable<R> fromBiFunction(Supplier<T> supplier1, U u, DbConnectedBiFunction<T, U, R> function) {
        Objects.requireNonNull(supplier1);
        Objects.requireNonNull(function);
        return new Callable<R>() {
            @Override
            public R call() throws Exception {
                T t = supplier1.get();
                try (ConnectionReservation c = new ConnectionReservation()) { return function.apply(t, u, CONNECTION); }
            }
        };
    }
    
    public static <T, U, R> Callable<R> fromBiFunction(T t, DbConnectedSupplier<U> supplier2, DbConnectedBiFunction<T, U, R> function) {
        Objects.requireNonNull(supplier2);
        Objects.requireNonNull(function);
        return new Callable<R>() {
            @Override
            public R call() throws Exception {
                try (ConnectionReservation c = new ConnectionReservation()) { return function.apply(t, supplier2.get(CONNECTION), CONNECTION); }
            }
        };
    }
    
    public static <T, U, R> Callable<R> fromBiFunction(T t, DbConnectedSupplier<U> supplier2, BiFunction<T, U, R> function) {
        Objects.requireNonNull(supplier2);
        Objects.requireNonNull(function);
        return new Callable<R>() {
            @Override
            public R call() throws Exception {
                U u ;
                try (ConnectionReservation c = new ConnectionReservation()) { u = supplier2.get(CONNECTION); }
                return function.apply(t, u);
            }
        };
    }
    
    public static <T, U, R> Callable<R> fromBiFunction(T t, Supplier<U> supplier2, DbConnectedBiFunction<T, U, R> function) {
        Objects.requireNonNull(supplier2);
        Objects.requireNonNull(function);
        return new Callable<R>() {
            @Override
            public R call() throws Exception {
                U u = supplier2.get();
                try (ConnectionReservation c = new ConnectionReservation()) {
                    return function.apply(t,  u, CONNECTION);
                }
            }
        };
    }
    
    public static <T, U, R> Callable<R> fromBiFunction(T t, U u, DbConnectedBiFunction<T, U, R> function) {
        Objects.requireNonNull(function);
        return new Callable<R>() {
            @Override
            public R call() throws Exception {
                try (ConnectionReservation c = new ConnectionReservation()) {
                    return function.apply(t,  u, CONNECTION);
                }
            }
        };
    }
    
    public static <T> Callable<Boolean> fromPredicate(DbConnectedSupplier<T> supplier, DbConnectedPredicate<T> predicate) {
        Objects.requireNonNull(supplier);
        Objects.requireNonNull(predicate);
        return new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                try (ConnectionReservation c = new ConnectionReservation()) { return predicate.test(supplier.get(CONNECTION), CONNECTION); }
            }
        };
    }
    
    public static <T> Callable<Boolean> fromPredicate(DbConnectedSupplier<T> supplier, Predicate<T> predicate) {
        Objects.requireNonNull(supplier);
        Objects.requireNonNull(predicate);
        return new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                T value;
                try (ConnectionReservation c = new ConnectionReservation()) { value = supplier.get(CONNECTION); }
                return predicate.test(value);
            }
        };
    }
    
    public static <T> Callable<Boolean> fromPredicate(Supplier<T> supplier, DbConnectedPredicate<T> predicate) {
        Objects.requireNonNull(supplier);
        Objects.requireNonNull(predicate);
        return new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                T value = supplier.get();
                try (ConnectionReservation c = new ConnectionReservation()) { return predicate.test(value, CONNECTION); }
            }
        };
    }
    
    public static <T> Callable<Boolean> fromPredicate(T value, DbConnectedPredicate<T> predicate) {
        Objects.requireNonNull(predicate);
        return new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                try (ConnectionReservation c = new ConnectionReservation()) { return predicate.test(value, CONNECTION); }
            }
        };
    }
    
    public static <T> Callable<Boolean> fromBooleanSupplier(DbConnectedBooleanSupplier supplier) {
        Objects.requireNonNull(supplier);
        return new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                try (ConnectionReservation c = new ConnectionReservation()) { return supplier.getAsBoolean(CONNECTION); }
            }
        };
    }
    
    private static final Logger LOG = Logger.getLogger(DbConnectableExecutor.class.getName());

    private static void onCloseConnection() throws RuntimeException {
        Connection connection;
        synchronized (MONITOR) {
            if ((connection = CONNECTION) == null || LATEST != null)
                return;
            CONNECTION = null;
            SCHEDULED_CLOSE = null;
        }
        LOG.log(Level.INFO, String.format("Closing database connection"));
        try {
            connection.close();
        } catch (SQLException ex) {
            throw new RuntimeException("Unable to close database connection", ex);
        }
        LOG.log(Level.INFO, String.format("Database connection closed"));
    }
    
    private static class ConnectionReservation implements AutoCloseable {
        private ConnectionReservation previous;
        private ConnectionReservation next;
        private ConnectionReservation() throws Exception {
            next = null;
            synchronized(MONITOR) {
                previous = LATEST;
                LATEST = this;
                if (previous != null) {
                    previous.next = this;
                    return;
                }
                if (SCHEDULED_CLOSE != null) {
                    SCHEDULED_CLOSE.cancel(true);
                    SCHEDULED_CLOSE = null;
                }
                if (CONNECTION == null) {
                    Class.forName(DB_DRIVER);
                    String url = AppConfig.getConnectionUrl();
                    LOG.log(Level.INFO, String.format("Opening database connection to %s", url));
                    CONNECTION = DriverManager.getConnection(url, AppConfig.getDbLoginName(), AppConfig.getDbLoginPassword());
                    LOG.log(Level.INFO, String.format("Connected to database %s", url));
                }
            }
        }

        @Override
        public void close() throws Exception {
            synchronized (MONITOR) {
                if (next != null) {
                    if ((next.previous = previous) != null) {
                        previous.next = next;
                        previous = null;
                    }
                    next = null;
                } else if ((LATEST = previous) != null)
                    previous = previous.next = null;
                else {
                    ScheduledExecutorService svc = Executors.newSingleThreadScheduledExecutor();
                    SCHEDULED_CLOSE = svc.schedule(new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            if (!this.isCancelled())
                                onCloseConnection();
                            return null;
                        }
                    }, 2, TimeUnit.SECONDS);
                }
            }
        }
    }
}
