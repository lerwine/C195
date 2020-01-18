/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

/**
 *
 * @author erwinel
 * @param <T> the type of the argument to the function.
 * @param <R> the type of the result of the function.
 * @param <E> the type of exception that can be thrown.
 */
@FunctionalInterface
public interface ThrowableFunction<T, R, E extends Throwable> {
    /**
     * Applies this function to the given arguments.
     * @param t the function argument.
     * @return the function result.
     * @throws E if exception was thrown while applying this function.
     */
    R apply(T t) throws E;
}
