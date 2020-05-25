package scheduler.model;

import java.sql.SQLException;
import java.util.Objects;
import scheduler.dao.AddressDAO;

/**
 * Interface for objects that contain either partial or complete information from the {@code address} database entity.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface Address extends DataObject {

    public static int compare(Address a, Address b) {
        if (Objects.equals(a, b)) {
            return 0;
        }
        if (null == a) {
            return 1;
        }
        if (null == b) {
            return -1;
        }

        int result = City.compare(a.getCity(), b.getCity());
        if (result == 0) {
            String x = a.getAddress1();
            String y = b.getAddress1();
            if ((result = x.compareToIgnoreCase(y)) == 0 && (result = x.compareTo(y)) == 0) {
                x = a.getAddress2();
                y = b.getAddress2();
                if ((result = x.compareToIgnoreCase(y)) == 0 && (result = x.compareTo(y)) == 0) {
                    x = a.getPostalCode();
                    y = b.getPostalCode();
                    if ((result = x.compareToIgnoreCase(y)) == 0 && (result = x.compareTo(y)) == 0) {
                        x = a.getPhone();
                        y = b.getPhone();
                        if ((result = x.compareToIgnoreCase(y)) == 0) {
                            return x.compareTo(y);
                        }
                    }
                }
            }
        }
        return result;
    }

    public static boolean arePropertiesEqual(Address a, Address b) {
        if (Objects.equals(a, b)) {
            return true;
        }

        return null != b && null != b && a.getAddress1().equalsIgnoreCase(b.getAddress1()) && a.getAddress2().equalsIgnoreCase(b.getAddress2())
                && ModelHelper.areSameRecord(a.getCity(), b.getCity()) && a.getPostalCode().equalsIgnoreCase(b.getPostalCode())
                && a.getPhone().equalsIgnoreCase(b.getPhone());
    }

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
    City getCity();

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

    AddressDAO.PredefinedAddressElement getPredefinedElement();

}
