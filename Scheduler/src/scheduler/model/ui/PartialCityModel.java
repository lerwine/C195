package scheduler.model.ui;

import java.util.TimeZone;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import scheduler.dao.CityDAO;
import scheduler.model.City;
import scheduler.dao.PartialCityDAO;
import scheduler.dao.PartialCountryDAO;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> Type of object for database access.
 */
public interface PartialCityModel<T extends PartialCityDAO> extends City, PartialEntityModel<T> {

    /**
     * The name of the 'countryName' property.
     */
    public static final String PROP_COUNTRYNAME = "countryName";
    /**
     * The name of the 'language' property.
     */
    public static final String PROP_LANGUAGE = "language";
    /**
     * The name of the 'timeZoneDisplay' property.
     */
    public static final String PROP_TIMEZONEDISPLAY = "timeZoneDisplay";

    public static PartialCityModel<? extends PartialCityDAO> createModel(PartialCityDAO t) {
        if (null == t) {
            return null;
        }
        if (t instanceof CityDAO) {
            return new CityModel((CityDAO) t);
        }

        return new PartialCityModelImpl(t);
    }

    ReadOnlyStringProperty nameProperty();

    @Override
    public PartialCountryModel<? extends PartialCountryDAO> getCountry();

    ReadOnlyProperty<? extends PartialCountryModel<? extends PartialCountryDAO>> countryProperty();

    String getCountryName();

    ReadOnlyStringProperty countryNameProperty();

    ReadOnlyObjectProperty<TimeZone> timeZoneProperty();

    String getTimeZoneDisplay();

    ReadOnlyStringProperty timeZoneDisplayProperty();

    String getLanguage();

    ReadOnlyStringProperty languageProperty();

    /**
     * Gets the backing {@link PartialCityDAO} data access object.
     *
     * @return The backing {@link PartialCityDAO} data access object.
     */
    @Override
    T dataObject();

}
