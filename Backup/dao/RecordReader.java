package scheduler.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import scheduler.view.ItemModel;

/**
 * Interface for an object that reads {@link DataObjectImpl} objects from the database.
 *
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
 * @param <T> The type of {@link DataObjectImpl} that will be read from the database.
 */
public interface RecordReader<T extends DataObjectImpl> {

    /**
     * Gets the message to display while data is being loaded from the database.
     *
     * @return The message to display while data is being loaded from the database.
     */
    String getLoadingMessage();

    /**
     * Gets the WHERE clause sub-expression for filtering results.
     *
     * @return The WHERE clause sub-expression for filtering results.
     */
    String getSqlFilterExpr();

    /**
     * Gets the {@link DataObjectImpl.Factory} responsible for creating the result {@link DataObjectImpl} objects.
     *
     * @return The {@link DataObjectImpl.Factory} responsible for creating the result {@link DataObjectImpl} objects.
     */
    DataObjectImpl.Factory<T, ? extends ItemModel<T>> getFactory();

    /**
     * Sets the parameterized values that correspond to place-holders in {@link #getSqlFilterExpr()}.
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
        DataObjectImpl.Factory<T, ? extends ItemModel<T>> f = getFactory();
        StringBuilder sb = new StringBuilder();
        sb.append(f.getBaseSelectQuery());
        String w = getSqlFilterExpr();
        if (null != w && !(w = w.trim()).isEmpty()) {
            sb.append(" WHERE ").append(w);
        }
        Logger.getLogger(getClass().getName()).log(Level.INFO, String.format("Executing query \"%s\"", sb.toString()));
        ArrayList<T> result = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sb.toString())) {
            apply(ps, 1);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(f.fromResultSet(rs));
                }
            }
        }
        return result;
    }
}
