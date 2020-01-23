/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.util;

import java.sql.Connection;
import java.util.Objects;

/**
 * Similar to {@link java.util.function.Consumer}, represents an operation that accepts a single {@link java.sql.Connection}
 * as the input argument, returns no result and may throw an exception.
 *
 * @author erwinel
 */
@FunctionalInterface
public interface DbConnectionConsumer {
    /**
     * Performs this operation using the given {@link java.sql.Connection}.
     * @param c The opened SQL DB {@link java.sql.Connection}.
     * @throws Exception if unable to perform the operation.
     */
    void accept(Connection c) throws Exception;
    
    /**
     * Returns a composed {@code DbConnectionConsumer} that performs, in sequence, this
     * operation followed by the {@code after} operation. If performing either
     * operation throws an exception, it is relayed to the caller of the
     * composed operation.  If performing this operation throws an exception,
     * the {@code after} operation will not be performed.
     * 
     * @param after the operation to perform after this operation.
     * @return a composed {@code DbConnectionConsumer} that performs in sequence this
     * operation followed by the {@code after} operation.
     * @throws NullPointerException if {@code after} is null.
     */
    default DbConnectionConsumer andThen(DbConnectionConsumer after) {
        Objects.requireNonNull(after);
        return (Connection c) -> { accept(c); after.accept(c); };
    }
}
