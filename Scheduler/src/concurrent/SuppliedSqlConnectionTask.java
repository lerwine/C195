package concurrent;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import scheduler.AppConfig;

/**
 *
 * @author erwinel
 * @param <T>
 */
public class SuppliedSqlConnectionTask<T, R> extends SqlConnectionTask<R> {
    private final Supplier<T> supplier;
    private final DbConnectedFunction<T, R> function;
    public SuppliedSqlConnectionTask(Supplier<T> supplier, DbConnectedFunction<T, R> function) {
        super();
        Objects.requireNonNull(supplier);
        Objects.requireNonNull(function);
        this.supplier = supplier;
        this.function = function;
    }

    @Override
    protected R call() throws Exception {
        T suppliedValue = supplier.get();
        return super.fromConnectedSupplier((Connection c) -> {
            return function.apply(suppliedValue, c);
        });
    }
}
