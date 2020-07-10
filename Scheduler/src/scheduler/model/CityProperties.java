package scheduler.model;

import java.util.Objects;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface CityProperties {

    public static final int MAX_LENGTH_NAME = 50;

    /**
     * The name of the 'name' property.
     */
    public static final String PROP_NAME = "name";

    /**
     * The name of the 'country' property.
     */
    public static final String PROP_COUNTRY = "country";

    public static String toString(CityProperties city) {
        if (null != city) {
            String n = city.getName();
            String country = CountryProperties.toString(city.getCountry()).trim();
            if (null == n || (n = n.trim()).isEmpty()) {
                return country;
            }
            return (country.isEmpty()) ? n : String.format("%s, %s", n, country);
        }
        return "";
    }

    public static int compare(CityProperties a, CityProperties b) {
        if (Objects.equals(a, b)) {
            return 0;
        }
        if (null == a) {
            return 1;
        }
        if (null == b) {
            return -1;
        }

        int result = CountryProperties.compare(a.getCountry(), b.getCountry());
        if (result == 0) {
            String x = a.getName();
            String y = b.getName();
            if ((result = x.compareToIgnoreCase(y)) == 0) {
                return x.compareTo(y);
            }
        }
        return result;
    }

    public static boolean arePropertiesEqual(CityProperties a, CityProperties b) {
        if (null == a) {
            return null == b;
        }

        return null != b && (a == b || (a.getName().equalsIgnoreCase(b.getName())
                && CountryProperties.arePropertiesEqual(a.getCountry(), b.getCountry())));
    }

    /**
     * Gets the name of the current city. This corresponds to the first part of the text in the "city" database column.
     *
     * @return The name of the current city.
     */
    String getName();

    /**
     * Gets the {@link CountryProperties} for the current city. This corresponds to the "country" data row referenced by the "countryId" database
     * column.
     *
     * @return The {@link CountryProperties} for the current city.
     */
    CountryProperties getCountry();

}
