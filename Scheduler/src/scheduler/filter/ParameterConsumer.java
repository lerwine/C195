/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.filter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import scheduler.util.DB;

/**
 *
 * @author erwinel
 */
public interface ParameterConsumer {
    int getParameterCount();
    void setString(String value) throws SQLException;
    void setInt(int value) throws SQLException;
    void setBoolean(boolean value) throws SQLException;
    void setDateTime(LocalDateTime value) throws SQLException;
    public static ParameterConsumer fromPreparedStatement(PreparedStatement ps) {
        return new ParameterConsumer() {
            private int index = 1;
            @Override
            public int getParameterCount() { return index - 1; }
            @Override
            public void setString(String value) throws SQLException {
                ps.setString(index, value);
                index++;
            }
            @Override
            public void setInt(int value) throws SQLException {
                ps.setInt(index, value);
                index++;
            }
            @Override
            public void setBoolean(boolean value) throws SQLException {
                ps.setBoolean(index, value);
                index++;
            }
            @Override
            public void setDateTime(LocalDateTime value) throws SQLException {
                ps.setTimestamp(index, DB.toUtcTimestamp(value));
                index++;
            }
        };
    }
}
