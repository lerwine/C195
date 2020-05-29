package scheduler.model.ui;

import java.time.ZoneId;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import scheduler.dao.CityDAO;
import scheduler.dao.ICityDAO;
import scheduler.dao.ICountryDAO;
import scheduler.model.CustomerCity;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> Type of object for database access.
 */
public interface CityItem<T extends ICityDAO> extends CustomerCity, FxDbModel<T> {

    public static CityItem<? extends ICityDAO> createModel(ICityDAO t) {
        if (null == t) {
            return null;
        }
        if (t instanceof CityDAO) {
            return new CityModel((CityDAO) t);
        }

        return new RelatedCity(t);
    }

    ReadOnlyStringProperty nameProperty();

    @Override
    public CountryItem<? extends ICountryDAO> getCountry();

    ReadOnlyProperty<? extends CountryItem<? extends ICountryDAO>> countryProperty();

    String getCountryName();

    ReadOnlyStringProperty countryNameProperty();

    ReadOnlyObjectProperty<ZoneId> zoneIdProperty();

    String getTimeZoneDisplay();

    ReadOnlyStringProperty timeZoneDisplayProperty();

    String getLanguage();

    ReadOnlyStringProperty languageProperty();

    /**
     * Gets the backing {@link ICityDAO} data access object.
     *
     * @return The backing {@link ICityDAO} data access object.
     */
    @Override
    T dataObject();

}
