package scheduler.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

/**
 * Represents a data row from the country data table.
 *
 * @author Leonard T. Erwine (Student ID 356334)
 */
public interface Country extends DataObject {

    /**
     * Gets the name of the current country.
     *
     * @return The name of the current country.
     */
    String getName();

    public static String toString(Country country) {
        if (null != country) {
            String n = country.getName();
            return (null == n) ? "" : n;
        }
        return "";
    }

    /**
     * Creates a read-only Country object from object values.
     *
     * @param pk The value of the primary key.
     * @param name The name of the country.
     * @return The read-only Country object.
     */
    public static Country of(int pk, String name) {
        Objects.requireNonNull(name, "Name cannot be null");
        return new Country() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public int getPrimaryKey() {
                return pk;
            }

            @Override
            public DataRowState getRowState() {
                return DataRowState.UNMODIFIED;
            }

        };
    }

    /**
     * Creates a read-only Country object from a result set.
     *
     * @param resultSet The data retrieved from the database.
     * @return The read-only Country object.
     * @throws SQLException if not able to read data from the {@link ResultSet}.
     */
    public static Country of(ResultSet resultSet) throws SQLException {
        // TODO: Implement this
        throw new UnsupportedOperationException();
    }
}
