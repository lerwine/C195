/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.util;

/**
 * Defines an operation that accepts a two arguments and may throw an exception.
 * @author erwinel
 * @param <T> The type of the first argument.
 * @param <U> The type of the second argument.
 * @param <E> The type of {@link Throwable} that can be thrown.
 */
@FunctionalInterface
public interface ThrowableBiConsumer<T, U, E extends Throwable> {

    /**
     * Performs the operation on the given arguments.
     * @param t The first input argument.
     * @param u The second input argument.
     * @throws E The exception that was thrown during the operation, if any.
     */
    void accept(T t, U u) throws E;
}
