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
 * Represents a data row from the "address" database table. Table definition: <code>CREATE TABLE `address` (
 *   `addressId` int(10) NOT NULL AUTO_INCREMENT,
 *   `address` varchar(50) NOT NULL,
 *   `address2` varchar(50) NOT NULL,
 *   `cityId` int(10) NOT NULL,
 *   `postalCode` varchar(10) NOT NULL,
 *   `phone` varchar(20) NOT NULL,
 *   `createDate` datetime NOT NULL,
 *   `createdBy` varchar(40) NOT NULL,
 *   `lastUpdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 *   `lastUpdateBy` varchar(40) NOT NULL,
 *   PRIMARY KEY (`addressId`),
 *   KEY `cityId` (`cityId`),
 *   CONSTRAINT `address_ibfk_1` FOREIGN KEY (`cityId`) REFERENCES `city` (`cityId`)
 * ) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;</code>
 *
 * @author Leonard T. Erwine (Student ID 356334)
 * @param <T> The type of {@link City} data access object.
 */
public interface Address<T extends City> extends DataObject {

    /**
     * Gets the first line of the current address. Column definition: <code>`address` varchar(50) NOT NULL</code>
     *
     * @return the first line of the current address.
     */
    String getAddress1();

    /**
     * Gets the second line of the current address. Column definition: <code>`address2` varchar(50) NOT NULL</code>
     *
     * @return the second line of the current address.
     */
    String getAddress2();

    /**
     * Gets the {@link City} for the current address. This corresponds to the "city" data row referenced by the "cityId" database column. Column
     * definition: <code>`cityId` int(10) NOT NULL</code> Key constraint definition:
     * <code>CONSTRAINT `address_ibfk_1` FOREIGN KEY (`cityId`) REFERENCES `city` (`cityId`)</code>
     *
     * @return The {@link City} for the current address.
     */
    T getCity();

    /**
     * Gets the postal code for the current address. Column definition: <code>`postalCode` varchar(10) NOT NULL</code>
     *
     * @return the postal code for the current address.
     */
    String getPostalCode();

    /**
     * Gets the phone number associated with the current address. Column definition: <code>`phone` varchar(20) NOT NULL</code>
     *
     * @return the phone number associated with the current address.
     */
    String getPhone();

    public static String toString(Address address) throws SQLException, ClassNotFoundException {
        if (null == address) {
            return "";
        }

        String cityZipCountry = address.getPostalCode();
        City city;
        if (null == cityZipCountry || (cityZipCountry = cityZipCountry.trim()).isEmpty()) {
            if (null == (city = address.getCity())) {
                cityZipCountry = "";
            } else if ((cityZipCountry = City.toString(city).trim()).isEmpty()) {
                cityZipCountry = Country.toString(city.getCountry()).trim();
            } else {
                String country = Country.toString(city.getCountry()).trim();
                if (!country.isEmpty()) {
                    cityZipCountry = String.format("%s, %s", cityZipCountry, country);
                }
            }
        } else if (null != (city = address.getCity())) {
            String cityName = city.getName();
            String country = Country.toString(city.getCountry()).trim();
            if (null == cityName || (cityName = cityName.trim()).isEmpty()) {
                if (!country.isEmpty()) {
                    cityZipCountry = String.format("%s, %s", cityZipCountry, cityName);
                }
            } else {
                if (country.isEmpty()) {
                    cityZipCountry = String.format("%s %s", cityName, cityZipCountry);
                } else {
                    cityZipCountry = String.format("%s %s, %s", cityName, cityZipCountry, country);
                }
            }
        } else {
            cityZipCountry = "";
        }
        StringBuilder sb = new StringBuilder();
        String s = address.getAddress1();
        if (null != s && !(s = s.trim()).isEmpty()) {
            sb.append(s);
        }
        s = address.getAddress2();
        if (null != s && !(s = s.trim()).isEmpty()) {
            if (sb.length() > 0) {
                sb.append("\n").append(s);
            } else {
                sb.append(s);
            }
        }
        if (!cityZipCountry.isEmpty()) {
            if (sb.length() > 0) {
                sb.append("\n").append(cityZipCountry);
            } else {
                sb.append(cityZipCountry);
            }
        }
        s = address.getPhone();
        if (null != s && !(s = s.trim()).isEmpty()) {
            if (sb.length() > 0) {
                sb.append("\n").append(s);
            } else {
                sb.append(s);
            }
        }
        return sb.toString();
    }

    /**
     * Creates a read-only Address object from object values.
     *
     * @param <T> The type of {@link City} data access object.
     * @param pk The value of the primary key.
     * @param address1 The first line of the current address.
     * @param address2 The second line of the current address.
     * @param city The {@link City} of the current address.
     * @param postalCode The postal code for the current address.
     * @param phone The phone number associated with the current address.
     * @return The read-only Address object.
     */
    public static <T extends City> Address<T> of(int pk, String address1, String address2, T city, String postalCode, String phone) {
        Objects.requireNonNull(address1, "Address line 1 cannot be null");
        Objects.requireNonNull(address2, "Address line 2 cannot be null");
        Objects.requireNonNull(postalCode, "Postal Code cannot be null");
        Objects.requireNonNull(phone, "Phone cannot be null");
        return new Address<T>() {
            @Override
            public String getAddress1() {
                return address1;
            }

            @Override
            public String getAddress2() {
                return address2;
            }

            @Override
            public T getCity() {
                return city;
            }

            @Override
            public String getPostalCode() {
                return postalCode;
            }

            @Override
            public String getPhone() {
                return phone;
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
     * Creates a read-only Address object from a result set.
     *
     * @param resultSet The data retrieved from the database.
     * @param columns The {@link TableColumnList} that created the current lookup query.
     * @return The read-only Address object.
     * @throws SQLException if not able to read data from the {@link ResultSet}.
     */
    public static Address of(ResultSet resultSet, TableColumnList<? extends ColumnReference> columns) throws SQLException {
        Optional<Integer> id = columns.tryGetInt(resultSet, DbName.ADDRESS_ID);
        if (id.isPresent()) {
            return of(id.get(), columns.getString(resultSet, DbColumn.ADDRESS1, ""), columns.getString(resultSet, DbColumn.ADDRESS2, ""),
                    City.of(resultSet, columns), columns.getString(resultSet, DbColumn.POSTAL_CODE, ""),
                    columns.getString(resultSet, DbName.PHONE, ""));
        }

        return null;
    }
}
