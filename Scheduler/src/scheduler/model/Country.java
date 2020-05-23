package scheduler.model;

import java.time.ZoneId;
import scheduler.dao.CountryDAO;
import scheduler.dao.ICountryDAO;

/**
 * Interface for objects that contain either partial or complete information from the {@code country} database entity.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface Country extends DataObject {

    public static ZoneId getZoneIdOf(Country dao) {
        if (null != dao) {
            CountryDAO.PredefinedElement predefinedElement = dao.getPredefinedElement();
            if (null != predefinedElement)
                return ZoneId.of(predefinedElement.getDefaultZoneId());
        }
        return ZoneId.systemDefault();
    }
    
    public static String getLanguageOf(Country dao) {
        if (null != dao) {
            CountryDAO.PredefinedElement predefinedElement = dao.getPredefinedElement();
            if (null != predefinedElement)
                return predefinedElement.getLocale().getDisplayLanguage();
        }
        return "";
    }
    
    public static String toString(Country country) {
        if (null != country) {
            String n = country.getName();
            return (null == n) ? "" : n;
        }
        return "";
    }

    public static boolean arePropertiesEqual(Country a, Country b) {
        if (null == a) {
            return null == b;
        }
        if (a == b) {
            return true;
        }
        return null != b && a.getName().equalsIgnoreCase(b.getName());
    }

    public static int compare(Country a, Country b) {
        if (null == a)
            return (null == b) ? 0 : 1;
        if (null == b)
            return -1;
        String x = a.getName();
        String y = b.getName();
        int result = x.compareToIgnoreCase(y);
        if (result == 0)
            return x.compareTo(y);
        return result;
    }

    /**
     * Gets the name of the current country.
     *
     * @return The name of the current country.
     */
    String getName();

    CountryDAO.PredefinedElement getPredefinedElement();
    
}
