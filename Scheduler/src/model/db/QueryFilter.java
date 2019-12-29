/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 *
 * @author Leonard T. Erwine
 * @param <R>
 */
public interface QueryFilter<R extends DataRow> {
    String getWindowTitle(ResourceBundle b);
    String getSubHeading(ResourceBundle b);
    String getSqlQueryString();
    void setStatementValues(PreparedStatement ps) throws SQLException;
}
