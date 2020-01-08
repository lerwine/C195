/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.sql.Connection;

/**
 * Similar to {@link java.util.concurrent.Callable} or {@link java.util.function.Function},
 * represents a task that returns a result using a {@link java.sql.Connection}
 * as the input argument and may throw an exception.
 * 
 * @author erwinel
 * @param <R>
 */
@FunctionalInterface
public interface DbConnectedCallable<R> {
    /**
     * Computes a result using an opened SQL DB {@link java.sql.Connection}.
     * 
     * @param c The opened SQL DB {@link java.sql.Connection}.
     * @return The computed result.
     * @throws Exception if unable to compute a result.
     */
    R call(Connection c) throws Exception;
    
}
