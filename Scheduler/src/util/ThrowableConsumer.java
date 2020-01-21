/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.Objects;

/**
 * Defines an operation that accepts a single argument and may throw an exception.
 * @author erwinel
 * @param <T> The type of the input to the operation.
 * @param <E> The type of {@link Throwable} that can be thrown.
 */
@FunctionalInterface
public interface ThrowableConsumer<T, E extends Throwable> {
    
    /**
     * Performs the operation on the given argument.
     * @param t The input argument.
     * @throws E The exception that was thrown during the operation, if any.
     */
    void accept(T t) throws E;
    
    /**
     * Creates a composed {@link ThrowableConsumer} that executes another {@link ThrowableConsumer} after the current operation.
     * @param after The following operation to perform.
     * @return A composed {@link ThrowableConsumer} that executes another {@link ThrowableConsumer} after the current operation.
     */
    default ThrowableConsumer<T, E> andThen(ThrowableConsumer<? super T, E> after) {
        Objects.requireNonNull(after);
        return (T t) -> { accept(t); after.accept(t); };
    }
}
