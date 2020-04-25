package scheduler.model.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.StringProperty;
import scheduler.dao.DataRowState;
import scheduler.dao.schema.DbColumn;
import scheduler.model.Address;
import static scheduler.util.Values.asNonNullAndTrimmed;

/**
 * Represents a data row from the "address" database table.
 * <dl>
 * <dt>{@link scheduler.dao.AddressDAO}</dt><dd>Data access object.</dd>
 * <dt>{@link scheduler.model.ui.AddressItem}</dt><dd>UI Model with JavaFX properties.</dd>
 * </dl>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface AddressRowData extends Address, RowData {

    public static String toString(AddressRowData address) throws SQLException, ClassNotFoundException {
        if (null == address) {
            return "";
        }

        String cityZipCountry = address.getPostalCode();
        CityRowData city;
        if (null == cityZipCountry || (cityZipCountry = cityZipCountry.trim()).isEmpty()) {
            if (null == (city = address.getCity())) {
                cityZipCountry = "";
            } else if ((cityZipCountry = CityRowData.toString(city).trim()).isEmpty()) {
                cityZipCountry = CountryRowData.toString(city.getCountry()).trim();
            } else {
                String country = CountryRowData.toString(city.getCountry()).trim();
                if (!country.isEmpty()) {
                    cityZipCountry = String.format("%s, %s", cityZipCountry, country);
                }
            }
        } else if (null != (city = address.getCity())) {
            String cityName = city.getName();
            String country = CountryRowData.toString(city.getCountry()).trim();
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
     * Creates a read-only AddressElement object from object values.
     *
     * @param pk The value of the primary key.
     * @param address1Value The first line of the current address.
     * @param address2Value The second line of the current address.
     * @param cityValue The {@link CityRowData} of the current address.
     * @param postalCodeValue The postal code for the current address.
     * @param phoneValue The phone number associated with the current address.
     * @return The read-only AddressElement object.
     */
    public static AddressRowData of(int pk, String address1Value, String address2Value, CityRowData cityValue,
            String postalCodeValue, String phoneValue) {
        return new AddressRowData() {
            private final ReadOnlyIntegerWrapper primaryKey = new ReadOnlyIntegerWrapper(pk);
            private final ReadOnlyObjectWrapper<DataRowState> rowState = new ReadOnlyObjectWrapper<>(DataRowState.UNMODIFIED);
            private final ReadOnlyStringWrapper address1 = new ReadOnlyStringWrapper(asNonNullAndTrimmed(address1Value));
            private final ReadOnlyStringWrapper address2 = new ReadOnlyStringWrapper(asNonNullAndTrimmed(address2Value));
            private final ReadOnlyObjectWrapper<CityRowData> city = new ReadOnlyObjectWrapper<>(Objects.requireNonNull(cityValue));
            private final ReadOnlyStringWrapper postalCode = new ReadOnlyStringWrapper(asNonNullAndTrimmed(postalCodeValue));
            private final ReadOnlyStringWrapper phone = new ReadOnlyStringWrapper(asNonNullAndTrimmed(phoneValue));

            @Override
            public String getAddress1() {
                return address1.get();
            }

            @SuppressWarnings("unused")
            public ReadOnlyStringProperty address1Property() {
                return address1.getReadOnlyProperty();
            }

            @Override
            public String getAddress2() {
                return address2.get();
            }

            @SuppressWarnings("unused")
            public void setAddress2(String value) {
                address2.set(value);
            }

            @SuppressWarnings("unused")
            public StringProperty address2Property() {
                return address2;
            }

            @Override
            public CityRowData getCity() {
                return city.get();
            }

            @SuppressWarnings("unused")
            public ReadOnlyObjectProperty<CityRowData> cityProperty() {
                return city.getReadOnlyProperty();
            }

            @Override
            public String getPostalCode() {
                return postalCode.get();
            }

            @SuppressWarnings("unused")
            public ReadOnlyStringProperty postalCodeProperty() {
                return postalCode.getReadOnlyProperty();
            }

            @Override
            public String getPhone() {
                return phone.get();
            }

            @SuppressWarnings("unused")
            public ReadOnlyStringProperty phoneProperty() {
                return phone.getReadOnlyProperty();
            }

            @Override
            public int getPrimaryKey() {
                return primaryKey.get();
            }

            @SuppressWarnings("unused")
            public ReadOnlyIntegerProperty primaryKeyProperty() {
                return primaryKey.getReadOnlyProperty();
            }

            @Override
            public DataRowState getRowState() {
                return rowState.get();
            }

            @SuppressWarnings("unused")
            public ReadOnlyObjectProperty<DataRowState> rowStateProperty() {
                return rowState.getReadOnlyProperty();
            }

            @Override
            public boolean equals(Object obj) {
                return null != obj && obj instanceof AddressRowData && AddressRowData.areEqual(this, (AddressRowData) obj);
            }

            @Override
            public int hashCode() {
                return pk;
            }
        };
    }

    /**
     * Creates a read-only AddressElement object from a result set.
     *
     * @param resultSet The data retrieved from the database.
     * @return The read-only AddressElement object.
     * @throws SQLException if not able to read data from the {@link ResultSet}.
     */
    public static AddressRowData of(ResultSet resultSet) throws SQLException {
        String address1 = resultSet.getString(DbColumn.ADDRESS1.toString());
        if (resultSet.wasNull()) {
            address1 = "";
        }
        String address2 = resultSet.getString(DbColumn.ADDRESS2.toString());
        if (resultSet.wasNull()) {
            address2 = "";
        }
        String postalCode = resultSet.getString(DbColumn.POSTAL_CODE.toString());
        if (resultSet.wasNull()) {
            postalCode = "";
        }
        String phone = resultSet.getString(DbColumn.PHONE.toString());
        if (resultSet.wasNull()) {
            phone = "";
        }
        return of(resultSet.getInt(DbColumn.ADDRESS_ID.toString()), address1, address2, CityRowData.of(resultSet), postalCode, phone);
    }

    public static boolean areEqual(AddressRowData a, AddressRowData b) {
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
            case MODIFIED:
            case UNMODIFIED:
                switch (b.getRowState()) {
                    case MODIFIED:
                    case UNMODIFIED:
                        return true;
                    default:
                        return false;
                }
            case NEW:
                return b.getRowState() == DataRowState.NEW && a.getAddress1().equalsIgnoreCase(b.getAddress1())
                        && a.getAddress2().equalsIgnoreCase(b.getAddress2())
                        && CityRowData.areEqual(a.getCity(), b.getCity())
                        && a.getPostalCode().equalsIgnoreCase(b.getPostalCode())
                        && a.getPhone().equalsIgnoreCase(b.getPhone());
            default:
                return b.getRowState() == DataRowState.DELETED;
        }
    }

    @Override
    public CityRowData getCity();

}
