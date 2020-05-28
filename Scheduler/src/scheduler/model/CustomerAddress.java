package scheduler.model;

import java.sql.SQLException;
import java.util.Objects;

/**
 * Interface for objects that contain either partial or complete information from the {@code address} database entity.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface CustomerAddress extends Address, DataObject {

    public static int compare(CustomerAddress a, CustomerAddress b) {
        if (Objects.equals(a, b)) {
            return 0;
        }
        if (null == a) {
            return 1;
        }
        if (null == b) {
            return -1;
        }

        int result = CustomerCity.compare(a.getCity(), b.getCity());
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

    public static boolean arePropertiesEqual(CustomerAddress a, CustomerAddress b) {
        if (null == a) {
            return null == b;
        }

        return null != b && (a == b || (a.getAddress1().equalsIgnoreCase(b.getAddress1()) && a.getAddress2().equalsIgnoreCase(b.getAddress2())
                && ModelHelper.areSameRecord(a.getCity(), b.getCity()) && a.getPostalCode().equalsIgnoreCase(b.getPostalCode())
                && a.getPhone().equalsIgnoreCase(b.getPhone())));
    }

    public static String toString(CustomerAddress address) throws SQLException, ClassNotFoundException {
        if (null == address) {
            return "";
        }

        String cityZipCountry = address.getPostalCode();
        CustomerCity city;
        if (null == cityZipCountry || (cityZipCountry = cityZipCountry.trim()).isEmpty()) {
            if (null == (city = address.getCity())) {
                cityZipCountry = "";
            } else if ((cityZipCountry = CustomerCity.toString(city).trim()).isEmpty()) {
                cityZipCountry = CustomerCountry.toString(city.getCountry()).trim();
            } else {
                String country = CustomerCountry.toString(city.getCountry()).trim();
                if (!country.isEmpty()) {
                    cityZipCountry = String.format("%s, %s", cityZipCountry, country);
                }
            }
        } else if (null != (city = address.getCity())) {
            String cityName = city.getName();
            String country = CustomerCountry.toString(city.getCountry()).trim();
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

    @Override
    public CustomerCity getCity();

}
