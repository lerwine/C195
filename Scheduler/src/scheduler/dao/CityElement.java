package scheduler.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import scheduler.dao.schema.DbColumn;
import static scheduler.util.Values.asNonNullAndTrimmed;

/**
 * Represents a data row from the "city" database table.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface CityElement extends DataElement {

    public static String toString(CityElement city) throws SQLException, ClassNotFoundException {
        if (null != city) {
            String n = city.getName();
            String country = CountryElement.toString(city.getCountry()).trim();
            if (null == n || (n = n.trim()).isEmpty()) {
                return country;
            }
            return (country.isEmpty()) ? n : String.format("%s, %s", n, country);
        }
        return "";
    }

    /**
     * Creates a read-only CityElement object from object values.
     *
     * @param pk The value of the primary key.
     * @param nameValue The name of the city.
     * @param country The country of the city.
     * @return The read-only CityElement object.
     */
    public static CityElement of(int pk, String nameValue, CountryElement country) {
        Objects.requireNonNull(country);
        return new CityElement() {
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
            public CountryElement getCountry() {
                return country;
            }

            @Override
            public DataRowState getRowState() {
                return DataRowState.UNMODIFIED;
            }

        };
    }

    /**
     * Creates a read-only CityElement object from a result set.
     *
     * @param resultSet The data retrieved from the database.
     * @return The read-only CityElement object.
     * @throws SQLException if not able to read data from the {@link ResultSet}.
     */
    public static CityElement of(ResultSet resultSet) throws SQLException {
        String name = asNonNullAndTrimmed(resultSet.getString(DbColumn.CITY_NAME.toString()));
        return of(resultSet.getInt(DbColumn.CITY_ID.toString()), name, CountryElement.of(resultSet));
    }

    public static boolean areEqual(CityElement a, CityElement b) {
        if (null == a) {
            return null == b;
        }
        if (null == b) {
            return false;
        }
        if (a == b || a.getPrimaryKey() != b.getPrimaryKey()) {
            return true;
        }
        switch (a.getRowState()) {
            case DELETED:
                return b.getRowState() == DataRowState.DELETED;
            case NEW:
                return b.getRowState() == DataRowState.NEW && a.getName().equalsIgnoreCase(b.getName())
                        && CountryElement.areEqual(a.getCountry(), b.getCountry());
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
     * Gets the name of the current city. This corresponds to the "city" database column.
     *
     * @return The name of the current city.
     */
    String getName();

    /**
     * Gets the {@link CountryElement} for the current city. This corresponds to the "country" data row referenced by the "countryId" database column.
     *
     * @return The {@link CountryElement} for the current city.
     */
    CountryElement getCountry();
}
