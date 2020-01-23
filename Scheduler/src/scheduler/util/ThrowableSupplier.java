/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.util;

/**
 *
 * @author erwinel
 * @param <T> the type of results supplied by this supplier.
 * @param <E> the type of exception that could be thrown.
 */
@FunctionalInterface
public interface ThrowableSupplier<T, E extends Throwable> {
    
    /**
     * Gets a result.
     * @return a result
     * @throws E the exception (if any) thrown by the supplier.
     */
    T get() throws E;
}
