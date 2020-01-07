/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrent;

import java.sql.Connection;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Functionally similar to {@link java.util.function.Consumer},
 * but with an additional argument, which is a successfully opened
 * {@link java.sql.Connection}.
 * 
 * @author erwinel
 * @param <T> the type of the input to the operation.
 */
@FunctionalInterface
public interface DbConnectedConsumer<T> {
    /**
     * Performs this operation on the given argument.
     * 
     * @param t The input argument.
     * @param c A successfully opened {@link java.sql.Connection}.
     * @throws Exception if unable to perform the operation.
     */
    void accept(T t, Connection c) throws Exception;
    
    /**
     * Gets a composed {@link concurrent.DbConnectedConsumer} that first performs the current operation,
     * then if no exception is thrown, performs the {@code after} operation.
     * 
     * @param after
     * @return A composed {@link concurrent.DbConnectedConsumer} that performs the current operation, then the {@code after} operation.
     * @throws Exception if unable to perform the current or {@code after} operation.
     */
    default DbConnectedConsumer<T> andThen(DbConnectedConsumer<T> after) throws Exception {
        Objects.requireNonNull(after);
        return (T t, Connection c) -> { accept(t, c); after.accept(t, c); };
    }
}
