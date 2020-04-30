package scheduler.model.predefined;

import java.time.ZoneId;
import java.util.Locale;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import scheduler.model.db.CountryRowData;
import scheduler.model.ui.CountryItem;

/**
 * Represents a pre-defined countries that are supported by the application.
 * <p>
 * This also specifies the {@link Locale} to be used for customers associated with the specified country.</p>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class PredefinedCountry extends PredefinedItem implements CountryItem, CountryRowData {

    private final ReadOnlyStringWrapper name;
    private final ReadOnlyObjectWrapper<Locale> locale;
    private final ReadOnlyObjectWrapper<ZoneId> defaultZoneId;
    private final ReadOnlyListWrapper<PredefinedCity> cities;
    private final ReadOnlyStringWrapper regionCode;

    @Override
    public String getName() {
        return name.get();
    }

    @Override
    public ReadOnlyStringProperty nameProperty() {
        return name.getReadOnlyProperty();
    }

    /**
     * Gets the language and region associated with the country definition.
     * 
     * @return The language and region associated with the country definition. 
     */
    public Locale getLocale() {
        return locale.get();
    }

    public ReadOnlyObjectProperty<Locale> localeProperty() {
        return locale.getReadOnlyProperty();
    }

    /**
     * Gets the default time zone to use in cases where the city name does not match a pre-defined city name.
     * 
     * @return The default {@link ZoneId} to use in cases where the city name does not match a
     * pre-defined city name.
     */
    public ZoneId getDefaultZoneId() {
        return defaultZoneId.get();
    }

    public ReadOnlyObjectProperty<ZoneId> defaultZoneIdProperty() {
        return defaultZoneId.getReadOnlyProperty();
    }

    /**
     * Gets a list of cities associated with the current country.
     * 
     * @return A list of cities associated with the current country.
     */
    public ObservableList<PredefinedCity> getCities() {
        return cities.get();
    }

    public ReadOnlyListProperty<PredefinedCity> citiesProperty() {
        return cities.getReadOnlyProperty();
    }
    private static final Logger LOG = Logger.getLogger(PredefinedCountry.class.getName());

    PredefinedCountry(CountryElement source, ObservableList<PredefinedCity> cities) {
        LOG.info(String.format("Parsing country %s", source.getLanguageTag()));
        Locale l = Locale.forLanguageTag(source.getLanguageTag());
        name = new ReadOnlyStringWrapper(this, "name", l.getDisplayCountry());
        locale = new ReadOnlyObjectWrapper<>(this, "locale", l);
        defaultZoneId = new ReadOnlyObjectWrapper<>(this, "defaultZoneId", ZoneId.of(source.getDefaultZoneId()));
        regionCode = new ReadOnlyStringWrapper(this, "regionCode", l.getCountry());
        this.cities = new ReadOnlyListWrapper<>(this, "cities", cities);

    }

    @Override
    public PredefinedCountry asPredefinedData() {
        return this;
    }

    public String getRegionCode() {
        return regionCode.get();
    }

    public ReadOnlyStringProperty regionCodeProperty() {
        return regionCode.getReadOnlyProperty();
    }

}
