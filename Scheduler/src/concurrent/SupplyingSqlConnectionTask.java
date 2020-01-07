/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrent;

import java.sql.Connection;
import java.util.Objects;
import java.util.function.Function;

/**
 *
 * @author erwinel
 */
public class SupplyingSqlConnectionTask<T, R> extends SqlConnectionTask<R> {
    private final DbConnectedSupplier<T> supplier;
    private final Function<T, R> function;
    public SupplyingSqlConnectionTask(DbConnectedSupplier<T> supplier, Function<T, R> function) {
        super();
        Objects.requireNonNull(supplier);
        Objects.requireNonNull(function);
        this.supplier = supplier;
        this.function = function;
    }

    @Override
    protected R call() throws Exception {
        return function.apply(super.fromConnectedSupplier(supplier));
    }
}
