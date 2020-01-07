/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrent;

import java.util.Objects;

/**
 *
 * @author erwinel
 * @param <T>
 */
public class SqlConnectionTaskImpl<T> extends SqlConnectionTask<T> {
    private final DbConnectedSupplier<T> supplier;

    public SqlConnectionTaskImpl(DbConnectedSupplier<T> supplier) {
        super();
        Objects.requireNonNull(supplier);
        this.supplier = supplier;
    }
    
    @Override
    protected T call() throws Exception {
        return super.fromConnectedSupplier(supplier);
    }
    
}
