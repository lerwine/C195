package scheduler.model;

import scheduler.model.predefined.PredefinedCity;


/**
 * Interface for objects that contain either partial or complete information from the {@code city} database entity.
 * 
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface City extends DataModel {

    public static boolean arePropertiesEqual(City a, City b) {
        if (null == a) {
            return null == b;
        }
        if (a == b) {
            return true;
        }
        return null != b && a.getName().equalsIgnoreCase(b.getName()) && ModelHelper.areSameRecord(a.getCountry(), b.getCountry());
    }
    
    public static int compare(City a, City b) {
        if (null == a)
            return (null == b) ? 0 : 1;
        if (null == b)
            return -1;
        int result = Country.compare(a.getCountry(), b.getCountry());
        if (result == 0) {
            String x = a.getName();
            String y = b.getName();
            if ((result = x.compareToIgnoreCase(y)) == 0)
                return x.compareTo(y);
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
    
    PredefinedCity asPredefinedData();
    
}
