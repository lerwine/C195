/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrent;

import java.sql.Connection;
import java.util.Objects;

/**
 *
 * @author erwinel
 */
@FunctionalInterface
public interface DbConnectedBiFunction<T, U, R> {
    R apply(T t, U u, Connection connection) throws Exception;
    default <V> DbConnectedBiFunction<T, U, V> andThen(DbConnectedFunction<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return (T t, U u, Connection c) -> {
            R r = DbConnectedBiFunction.this.apply(t, u, c);
            return after.apply(r, c);
        };
    }
}
