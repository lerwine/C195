package scheduler.model.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import scheduler.dao.DataRowState;
import scheduler.dao.schema.DbColumn;
import scheduler.model.Country;
import static scheduler.util.Values.asNonNullAndTrimmed;

/**
 * Represents a data row from the country data table.
 * <dl>
 * <dt>{@link scheduler.dao.CountryDAO}</dt><dd>Data access object.</dd>
 * <dt>{@link scheduler.model.ui.CountryItem}</dt><dd>UI Model with JavaFX properties.</dd>
 * </dl>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface CountryRowData extends Country, RowData {

    public static boolean areEqual(CountryRowData a, CountryRowData b) {
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

    public static String toString(CountryRowData country) {
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
    public static CountryRowData of(int pk, String nameValue) {
        return new CountryRowData() {
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
    public static CountryRowData of(ResultSet resultSet) throws SQLException {
        String name = resultSet.getString(DbColumn.COUNTRY_NAME.toString());
        if (resultSet.wasNull())
            name = "";
        return of(resultSet.getInt(DbColumn.COUNTRY_ID.toString()), name);
    }
}
