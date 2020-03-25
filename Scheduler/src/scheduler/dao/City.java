package scheduler.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;
import scheduler.dao.dml.ColumnReference;
import scheduler.dao.dml.TableColumnList;
import scheduler.dao.schema.DbColumn;
import scheduler.dao.schema.DbName;

/**
 * Represents a data row from the "city" database table. Table definition: <code>CREATE TABLE `city` (
 *   `cityId` int(10) NOT NULL AUTO_INCREMENT,
 *   `city` varchar(50) NOT NULL,
 *   `countryId` int(10) NOT NULL,
 *   `createDate` datetime NOT NULL,
 *   `createdBy` varchar(40) NOT NULL,
 *   `lastUpdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 *   `lastUpdateBy` varchar(40) NOT NULL,
 *   PRIMARY KEY (`cityId`),
 *   KEY `countryId` (`countryId`),
 *   CONSTRAINT `city_ibfk_1` FOREIGN KEY (`countryId`) REFERENCES `country` (`countryId`)
 * ) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;</code>
 *
 * @author Leonard T. Erwine (Student ID 356334)
 * @param <T> The type of {@link Country} data access object.
 */
public interface City<T extends Country> extends DataObject {

    /**
     * Gets the name of the current city. This corresponds to the "city" database column.
     *
     * @return The name of the current city.
     */
    String getName();

    /**
     * Gets the {@link Country} for the current city. This corresponds to the "country" data row referenced by the "countryId" database column.
     *
     * @return The {@link Country} for the current city.
     */
    T getCountry();

    public static String toString(City<? extends Country> city) throws SQLException, ClassNotFoundException {
        if (null != city) {
            String n = city.getName();
            String country = Country.toString(city.getCountry()).trim();
            if (null == n || (n = n.trim()).isEmpty()) {
                return country;
            }
            return (country.isEmpty()) ? n : String.format("%s, %s", n, country);
        }
        return "";
    }

    /**
     * Creates a read-only City object from object values.
     *
     * @param <T> The type of {@link Country} data access object.
     * @param pk The value of the primary key.
     * @param name The name of the city.
     * @param country The country of the city.
     * @return The read-only City object.
     */
    public static <T extends Country> City<T> of(int pk, String name, T country) {
        Objects.requireNonNull(name, "Name cannot be null");
        return new City<T>() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public int getPrimaryKey() {
                return pk;
            }

            @Override
            public T getCountry() {
                return country;
            }

            @Override
            public DataRowState getRowState() {
                return DataRowState.UNMODIFIED;
            }

        };
    }

    /**
     * Creates a read-only City object from a result set.
     *
     * @param resultSet The data retrieved from the database.
     * @param columns The {@link TableColumnList} that created the current lookup query.
     * @return The read-only City object.
     * @throws SQLException if not able to read data from the {@link ResultSet}.
     */
    public static City<? extends Country> of(ResultSet resultSet, TableColumnList<? extends ColumnReference> columns) throws SQLException {
        Optional<Integer> id = columns.tryGetInt(resultSet, DbName.ADDRESS_ID);
        if (id.isPresent()) {
            return of(id.get(), columns.getString(resultSet, DbColumn.CITY_NAME, ""), Country.of(resultSet, columns));
        }
        return null;
    }
}
