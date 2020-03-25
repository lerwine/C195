package scheduler.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import scheduler.dao.dml.SelectColumnList;
import scheduler.dao.dml.WhereStatement;
import scheduler.view.ItemModel;

/**
 * Interface for an object that reads {@link DataObjectImpl} objects from the database.
 *
 * @author Leonard T. Erwine (Student ID 356334)
 * @param <T> The type of {@link DataObjectImpl} that will be read from the database.
 * @param <U> The type of {@link ItemModel} that will be created from the data access object.
 */
public interface RecordReader<T extends DataObjectImpl, U extends ItemModel<T>> {
    
    /**
     * Gets the message to display while data is being loaded from the database.
     *
     * @return The message to display while data is being loaded from the database.
     */
    String getLoadingMessage();

    /**
     * Gets the object that is used to generate the SQL WHERE clause.
     * 
     * @return The {@link WhereStatement} that will generate the SQL WHERE clause or {@code null} if there will be no WHERE clause.
     */
    WhereStatement<T, U> getWhereStatement();

    /**
     * Gets the {@link DataObjectImpl.Factory} responsible for creating the result {@link DataObjectImpl} objects.
     *
     * @return The {@link DataObjectImpl.Factory} responsible for creating the result {@link DataObjectImpl} objects.
     */
    DataObjectImpl.Factory<T, U> getFactory();

    /**
     * Sets the parameterized values that correspond to place-holders in {@link #getWhereStatement()}.
     *
     * @param ps The {@link PreparedStatement} to initialize.
     * @param index The first parameter index to use.
     * @return The next sequential parameter index after the last parameter index used in this implementation.
     * @throws SQLException if unable to set parameterized value.
     */
    int apply(PreparedStatement ps, int index) throws SQLException;

    /**
     * Reads {@link DataObjectImpl} objects from the database.
     *
     * @param connection The {@link Connection} to use to retrieve data from the database.
     * @return The {@link DataObjectImpl} objects loaded from the database.
     * @throws SQLException if unable to read data from the database.
     */
    default ArrayList<T> get(Connection connection) throws SQLException {
        DataObjectImpl.Factory<T, U> f = getFactory();
        SelectColumnList dml = f.getSelectColumns();
        StringBuilder sb = dml.getSelectQuery();
        WhereStatement<T, U> whereStatement = getWhereStatement();
        if (null != whereStatement) {
            sb.append(" WHERE ");
            whereStatement.appendSqlStatement(sb);
        }
        Logger.getLogger(getClass().getName()).log(Level.INFO, String.format("Executing query \"%s\"", sb.toString()));
        ArrayList<T> result = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sb.toString())) {
            apply(ps, 1);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(f.fromResultSet(rs, dml));
                }
            }
        }
        return result;
    }
    
}
