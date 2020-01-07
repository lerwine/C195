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
 * Functionally similar to {@link java.util.function.Function},
 * but with an additional argument, which is a successfully opened
 * {@link java.sql.Connection}.
 *
 * @author erwinel
 * @param <T> The type of the input to the function.
 * @param <R> The type of the result of the function.
 */
@FunctionalInterface
public interface DbConnectedFunction<T, R> {
    /**
     * Applies this function to the given argument.
     * 
     * @param t The function argument.
     * @param c A successfully opened {@link java.sql.Connection}.
     * @return The function result
     * @throws Exception if unable to calculate the result.
     */
    R apply(T t, Connection c) throws Exception;
    
    default <V> DbConnectedFunction<V, R> compose(DbConnectedFunction<? super V, ? extends T> before) {
        Objects.requireNonNull(before);
        return (V v, Connection connection) -> apply(before.apply(v, connection), connection);
    }
    
    default <V> DbConnectedFunction<T, V> andThen(DbConnectedFunction<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return (T t, Connection connection) -> after.apply(apply(t, connection), connection);
    }
}
