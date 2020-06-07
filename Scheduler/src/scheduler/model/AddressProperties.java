package scheduler.model;

import java.sql.SQLException;
import java.util.Objects;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface AddressProperties {

    public static final int MAX_LENGTH_ADDRESS1 = 50;

    /**
     * The name of the 'address1' property.
     */
    public static final String PROP_ADDRESS1 = "address1";

    public static final int MAX_LENGTH_ADDRESS2 = 50;

    /**
     * The name of the 'address2' property.
     */
    public static final String PROP_ADDRESS2 = "address2";

    /**
     * The name of the 'city' property.
     */
    public static final String PROP_CITY = "city";

    public static final int MAX_LENGTH_POSTALCODE = 10;

    /**
     * The name of the 'postalCode' property.
     */
    public static final String PROP_POSTALCODE = "postalCode";

    public static final int MAX_LENGTH_PHONE = 20;

    /**
     * The name of the 'phone' property.
     */
    public static final String PROP_PHONE = "phone";

    public static String toString(AddressProperties address) throws SQLException, ClassNotFoundException {
        if (null == address) {
            return "";
        }

        String cityZipCountry = address.getPostalCode();
        CityProperties city;
        if (null == cityZipCountry || (cityZipCountry = cityZipCountry.trim()).isEmpty()) {
            if (null == (city = address.getCity())) {
                cityZipCountry = "";
            } else if ((cityZipCountry = CityProperties.toString(city).trim()).isEmpty()) {
                cityZipCountry = CountryProperties.toString(city.getCountry()).trim();
            } else {
                String country = CountryProperties.toString(city.getCountry()).trim();
                if (!country.isEmpty()) {
                    cityZipCountry = String.format("%s, %s", cityZipCountry, country);
                }
            }
        } else if (null != (city = address.getCity())) {
            String cityName = city.getName();
            String country = CountryProperties.toString(city.getCountry()).trim();
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

    public static int compare(AddressProperties a, AddressProperties b) {
        if (Objects.equals(a, b)) {
            return 0;
        }
        if (null == a) {
            return 1;
        }
        if (null == b) {
            return -1;
        }

        int result = CityProperties.compare(a.getCity(), b.getCity());
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

    public static boolean arePropertiesEqual(AddressProperties a, AddressProperties b) {
        if (null == a) {
            return null == b;
        }

        return null != b && (a == b || (a.getAddress1().equalsIgnoreCase(b.getAddress1()) && a.getAddress2().equalsIgnoreCase(b.getAddress2())
                && CityProperties.arePropertiesEqual(a.getCity(), b.getCity()) && a.getPostalCode().equalsIgnoreCase(b.getPostalCode())
                && a.getPhone().equalsIgnoreCase(b.getPhone())));
    }

    /**
     * Gets the first line of the current address.
     *
     * @return the first line of the current address.
     */
    String getAddress1();

    /**
     * Gets the second line of the current address.
     *
     * @return the second line of the current address.
     */
    String getAddress2();

    /**
     * Gets the {@link CityProperties} for the current address.
     *
     * @return The {@link CityProperties} for the current address.
     */
    CityProperties getCity();

    /**
     * Gets the postal code for the current address.
     *
     * @return the postal code for the current address.
     */
    String getPostalCode();

    /**
     * Gets the phone number associated with the current address.
     *
     * @return the phone number associated with the current address.
     */
    String getPhone();

}
