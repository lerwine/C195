package scheduler.model.db;

import scheduler.model.Country;
import scheduler.model.RelatedRecord;

/**
 * Represents a data row from the country data table.
 * <dl>
 * <dt>{@link scheduler.dao.CountryDAO}</dt><dd>Data access object.</dd>
 * <dt>{@link scheduler.model.ui.CountryItem}</dt><dd>UI Model with JavaFX properties.</dd>
 * </dl>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface CountryRowData extends Country, RelatedRecord {

    public static String toString(Country country) {
        if (null != country) {
            String n = country.getName();
            return (null == n) ? "" : n;
        }
        return "";
    }

}
