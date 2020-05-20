package scheduler.model.ui;

import java.time.ZoneId;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import scheduler.dao.ICityDAO;
import scheduler.model.City;
import scheduler.model.predefined.PredefinedCity;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface CityItem extends City, FxModel {

    ReadOnlyStringProperty nameProperty();

    @Override
    public CountryItem getCountry();

    ReadOnlyProperty<? extends CountryItem> countryProperty();

    ReadOnlyStringProperty countryNameProperty();

    ZoneId getZoneId();

    ReadOnlyObjectProperty<ZoneId> zoneIdProperty();

    String getLanguage();

    ReadOnlyStringProperty languageProperty();

    ReadOnlyObjectProperty<PredefinedCity> predefinedDataProperty();

    /**
     * Gets the backing {@link ICityDAO} data access object.
     *
     * @return The backing {@link ICityDAO} data access object.
     */
    @Override
    ICityDAO getDataObject();

    /**
     * Gets the {@link ReadOnlyProperty} that contains the backing {@link ICityDAO} data access object.
     *
     * @return The {@link ReadOnlyProperty} that contains the backing {@link ICityDAO} data access object.
     */
    @Override
    ReadOnlyObjectProperty<? extends ICityDAO> dataObjectProperty();

}
