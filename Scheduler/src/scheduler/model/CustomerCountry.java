package scheduler.model;

import java.util.Objects;

/**
 * Interface for objects that contain either partial or complete information from the {@code country} database entity.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface CustomerCountry extends Country, DataObject {

    public static String toString(CustomerCountry country) {
        if (null != country) {
            String n = country.getName();
            return (null == n) ? "" : n;
        }
        return "";
    }

    public static boolean arePropertiesEqual(CustomerCountry a, CustomerCountry b) {
        if (null == a) {
            return null == b;
        }

        return null != b && (a == b || (a.getName().equalsIgnoreCase(b.getName()) && Objects.equals(a.getLocale(), b.getLocale())));
    }

    public static int compare(CustomerCountry a, CustomerCountry b) {
        if (Objects.equals(a, b)) {
            return 0;
        }
        if (null == a) {
            return 1;
        }
        if (null == b) {
            return -1;
        }

        String x = a.getName();
        String y = b.getName();
        int result = x.compareToIgnoreCase(y);
        if (result == 0) {
            return x.compareTo(y);
        }
        return result;
    }

}
