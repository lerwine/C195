package scheduler.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import scheduler.dao.schema.DbColumn;
import static scheduler.util.Values.asNonNullAndTrimmed;

/**
 * Represents a data row from the country data table.
 *
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
 */
public interface CountryElement extends DataElement {

    public static boolean areEqual(CountryElement a, CountryElement b) {
        if (null == a)
            return null == b;
        if (null == b)
            return false;
        if (a == b || a.getPrimaryKey() != b.getPrimaryKey())
            return true;
        switch (a.getRowState()) {
            case DELETED:
                return b.getRowState() == DataRowState.DELETED;
            case NEW:
                return b.getRowState() == DataRowState.NEW && a.getName().equalsIgnoreCase(b.getName());
            default:
                switch (b.getRowState()) {
                    case MODIFIED:
                    case UNMODIFIED:
                        return true;
                    default:
                        return false;
                }
        }
    }

    /**
     * Gets the name of the current country.
     *
     * @return The name of the current country.
     */
    String getName();

    public static String toString(CountryElement country) {
        if (null != country) {
            String n = country.getName();
            return (null == n) ? "" : n;
        }
        return "";
    }

    /**
     * Creates a read-only CountryElement object from object values.
     *
     * @param pk The value of the primary key.
     * @param nameValue The name of the country.
     * @return The read-only CountryElement object.
     */
    public static CountryElement of(int pk, String nameValue) {
        return new CountryElement() {
            private final String name = asNonNullAndTrimmed(nameValue);
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
     * Creates a read-only CountryElement object from a result set.
     *
     * @param resultSet The data retrieved from the database.
     * @return The read-only CountryElement object.
     * @throws SQLException if not able to read data from the {@link ResultSet}.
     */
    public static CountryElement of(ResultSet resultSet) throws SQLException {
        String name = resultSet.getString(DbColumn.COUNTRY_NAME.toString());
        if (resultSet.wasNull())
            name = "";
        return of(resultSet.getInt(DbColumn.COUNTRY_ID.toString()), name);
    }
}
