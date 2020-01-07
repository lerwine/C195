/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrent;

import java.sql.Connection;

/**
 * Functionally similar to {@link java.util.function.Predicate},
 * but with a seconds argument, which is a successfully opened
 * {@link java.sql.Connection}.
 *
 * @author erwinel
 * @param <T> The type of the input to the predicate
 */
@FunctionalInterface
public interface DbConnectedPredicate<T> {
    /**
     * Evaluates this predicate on the given argument.
     * 
     * @param t The input argument
     * @param c A successfully opened {@link java.sql.Connection}.
     * @return {@code true} if the input argument matches the predicate; otherwise, {@code false}.
     * @throws Exception if unable to compute the result.
     */
    boolean test(T t, Connection c) throws Exception;
}
