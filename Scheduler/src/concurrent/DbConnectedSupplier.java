/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrent;

import java.sql.Connection;

/**
 * Functionally similar to {@link java.util.function.Supplier},
 * but with a single argument, which is a successfully opened
 * {@link java.sql.Connection}.
 *
 * @param <T> the type of results supplied by this supplier
 * @author erwinel
 */
@FunctionalInterface
public interface DbConnectedSupplier<T> {
    /**
     * Gets a result object.
     *
     * @param c A successfully opened {@link java.sql.Connection}.
     * @return The result object.
     * @throws Exception if unable to compute the result.
     */
    T get(Connection c) throws Exception;
}
