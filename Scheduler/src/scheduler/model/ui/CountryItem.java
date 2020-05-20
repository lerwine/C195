package scheduler.model.ui;

import java.time.ZoneId;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import scheduler.dao.ICountryDAO;
import scheduler.model.Country;
import scheduler.model.predefined.PredefinedCountry;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> Type of object for database access.
 */
public interface CountryItem<T extends ICountryDAO> extends Country, FxDbModel<T> {

    ReadOnlyStringProperty nameProperty();

    ZoneId getZoneId();

    ReadOnlyObjectProperty<ZoneId> zoneIdProperty();

    String getLanguage();

    ReadOnlyStringProperty languageProperty();

    ReadOnlyObjectProperty<PredefinedCountry> predefinedDataProperty();

    @Override
    T getDataObject();

    @Override
    ReadOnlyObjectProperty<? extends T> dataObjectProperty();

}
