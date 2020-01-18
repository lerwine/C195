/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author erwinel
 * @param <R> The result type.
 */
@FunctionalInterface
public interface ResultSetFunction<R> {
    /**
     * Applies this function to the given argument.
     *
     * @param rs the result set.
     * @return the function result
     * @throws java.sql.SQLException if unable to create the result.
     */
    R apply(ResultSet rs) throws SQLException;
}
