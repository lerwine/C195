/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.filter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Abstract interface for creating SQL statements.
 * @author erwinel
 * @param <T> The target type for the builder.
 */
public interface SqlStatementBuilder<T extends AutoCloseable> extends AutoCloseable {
    int length();
    String getSql();
    SqlStatementBuilder appendSql(String sql);
    ParameterConsumer finalizeSql() throws SQLException;
    T getResult() throws SQLException;
    
    public static SqlStatementBuilder<PreparedStatement> fromConnection(Connection connection) {
        return new SqlStatementBuilder<PreparedStatement>() {
            private StringBuilder stringBuilder = new StringBuilder();
            private PreparedStatement result = null;
            
            @Override
            public int length() { return stringBuilder.length(); }
            
            @Override
            public String getSql() {
                
                return stringBuilder.toString();
            }

            @Override
            public SqlStatementBuilder appendSql(String sql) {
                assert null == result : "SQL has been finalized";
                stringBuilder.append(sql);
                return this;
            }

            @Override
            public ParameterConsumer finalizeSql() throws SQLException {
                assert null == result : "SQL has been already been finalized";
                String s = getSql();
                result = connection.prepareStatement(s);
                return ParameterConsumer.fromPreparedStatement(result);
            }

            @Override
            public PreparedStatement getResult() throws SQLException {
                if (null == result)
                    finalizeSql();
                return result;
            }

            @Override
            public void close() throws Exception {
                if (null != result)
                    result.close();
                stringBuilder = null;
            }
            
        };
    }
}
