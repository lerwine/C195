package scheduler.model;

import java.time.ZoneId;
import java.util.Objects;
import scheduler.dao.CityDAO;

/**
 * Interface for objects that contain either partial or complete information from the {@code city} database entity.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface City extends DataObject {

    public static ZoneId getZoneIdOf(City dao) {
        if (null != dao) {
            CityDAO.PredefinedCityElement predefinedElement = dao.getPredefinedElement();
            if (null != predefinedElement) {
                return ZoneId.of(predefinedElement.getZoneId());
            }
            return Country.getZoneIdOf(dao.getCountry());
        }
        return ZoneId.systemDefault();
    }

    public static String toString(City city) {
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

    public static boolean arePropertiesEqual(City a, City b) {
        if (Objects.equals(a, b)) {
            return true;
        }

        return null != b && null != b && a.getName().equalsIgnoreCase(b.getName()) && ModelHelper.areSameRecord(a.getCountry(), b.getCountry());
    }

    public static int compare(City a, City b) {
        if (Objects.equals(a, b)) {
            return 0;
        }
        if (null == a) {
            return 1;
        }
        if (null == b) {
            return -1;
        }

        int result = Country.compare(a.getCountry(), b.getCountry());
        if (result == 0) {
            String x = a.getName();
            String y = b.getName();
            if ((result = x.compareToIgnoreCase(y)) == 0) {
                return x.compareTo(y);
            }
        }
        return result;
    }

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
    Country getCountry();

    CityDAO.PredefinedCityElement getPredefinedElement();

}
