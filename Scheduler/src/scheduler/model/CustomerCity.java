package scheduler.model;

import java.util.Objects;

/**
 * Interface for objects that contain either partial or complete information from the {@code city} database entity.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface CustomerCity extends City, DataObject {

    public static String toString(CustomerCity city) {
        if (null != city) {
            String n = city.getName();
            String country = CustomerCountry.toString(city.getCountry()).trim();
            if (null == n || (n = n.trim()).isEmpty()) {
                return country;
            }
            return (country.isEmpty()) ? n : String.format("%s, %s", n, country);
        }
        return "";
    }

    public static boolean arePropertiesEqual(CustomerCity a, CustomerCity b) {
        if (null == a) {
            return null == b;
        }

        return null != b && (a == b || (a.getName().equalsIgnoreCase(b.getName()) && ModelHelper.areSameRecord(a.getCountry(), b.getCountry()) &&
                Objects.equals(a.getZoneId(), b.getZoneId())));
    }

    public static int compare(CustomerCity a, CustomerCity b) {
        if (Objects.equals(a, b)) {
            return 0;
        }
        if (null == a) {
            return 1;
        }
        if (null == b) {
            return -1;
        }

        int result = CustomerCountry.compare(a.getCountry(), b.getCountry());
        if (result == 0) {
            String x = a.getName();
            String y = b.getName();
            if ((result = x.compareToIgnoreCase(y)) == 0) {
                return x.compareTo(y);
            }
        }
        return result;
    }

    @Override
    public CustomerCountry getCountry();

}
