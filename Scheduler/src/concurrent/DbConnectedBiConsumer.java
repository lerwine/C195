/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrent;

import java.sql.Connection;
import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * Functionally similar to {@link java.util.function.BiConsumer},
 * but with an additional argument, which is a successfully opened
 * {@link java.sql.Connection}.
 *
 * @author erwinel
 */
@FunctionalInterface
public interface DbConnectedBiConsumer<T, U> {
    
    void accept(T t, U u, Connection connection);
    
    default DbConnectedBiConsumer<T, U> andThen(DbConnectedBiConsumer<? super T, ? super U> after) {
        Objects.requireNonNull(after);

        return (l, r, connection) -> {
            accept(l, r, connection);
            after.accept(l, r, connection);
        };
    }
}
