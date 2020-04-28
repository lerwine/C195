package scheduler.model.db;

import scheduler.model.City;
import scheduler.model.RelatedRecord;

/**
 * Represents a data row from the "city" database table.
 * <dl>
 * <dt>{@link scheduler.dao.CityDAO}</dt><dd>Data access object.</dd>
 * <dt>{@link scheduler.model.ui.CityItem}</dt><dd>UI Model with JavaFX properties.</dd>
 * </dl>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface CityRowData extends City, RelatedRecord {

    public static String toString(City city) {
        if (null != city) {
            String n = city.getName();
            String country = CountryRowData.toString(city.getCountry()).trim();
            if (null == n || (n = n.trim()).isEmpty()) {
                return country;
            }
            return (country.isEmpty()) ? n : String.format("%s, %s", n, country);
        }
        return "";
    }

    @Override
    public CountryRowData getCountry();

}
