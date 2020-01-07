/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrent;

import java.sql.Connection;

/**
 * Functionally similar to {@link java.util.function.BooleanSupplier},
 * but with a single argument, which is a successfully opened
 * {@link java.sql.Connection}.
 *
 * @author erwinel
 */
@FunctionalInterface
public interface DbConnectedBooleanSupplier {
    /**
     * Gets a boolean result.
     * 
     * @param c A successfully opened {@link java.sql.Connection}.
     * @return A boolean value.
     * @throws Exception if unable to compute the result.
     */
    boolean getAsBoolean(Connection c) throws Exception;
}
