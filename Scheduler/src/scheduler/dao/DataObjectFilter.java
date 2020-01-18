/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author erwinel
 * @param <T> Type of object to filter.
 */
public interface DataObjectFilter<T extends DataObject> {
    int setWhereParameters(PreparedStatement ps, int startIndex) throws SQLException;
    String toWhereClause();
    DataObjectFilter<T> createClone();
}
