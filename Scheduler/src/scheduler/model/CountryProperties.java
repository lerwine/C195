package scheduler.model;

import java.util.Locale;
import java.util.Objects;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface CountryProperties {

    public static String toString(CountryProperties country) {
        if (null != country) {
            String n = country.getName();
            return (null == n) ? "" : n;
        }
        return "";
    }

    public static boolean arePropertiesEqual(CountryProperties a, CountryProperties b) {
        if (null == a) {
            return null == b;
        }

        return null != b && (a == b || (a.getName().equalsIgnoreCase(b.getName()) && Objects.equals(a.getLocale(), b.getLocale())));
    }

    public static int compare(CountryProperties a, CountryProperties b) {
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

    /**
     * Gets the name of the current country.
     *
     * @return The name of the current country.
     */
    String getName();

    /**
     * Gets the {@link Locale} for the current country.
     *
     * @return The {@link Locale} for the current country.
     */
    Locale getLocale();

}
