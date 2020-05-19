package scheduler.model.predefined;

import java.time.LocalDateTime;
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
import scheduler.dao.CountryDAO;
import scheduler.dao.ICountryDAO;
import scheduler.model.Country;
import scheduler.model.ui.CountryItem;
import scheduler.model.ui.IFxRecordModel;

/**
 * Represents a pre-defined countries that are supported by the application.
 * <p>
 * This also specifies the {@link Locale} to be used for customers associated with the specified country.</p>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class PredefinedCountry extends PredefinedItem implements IFxRecordModel<CountryDAO>, CountryItem, Country {

    private static final Logger LOG = Logger.getLogger(PredefinedCountry.class.getName());

    private final ReadOnlyStringWrapper name;
    private final ReadOnlyObjectWrapper<Locale> locale;
    private final ReadOnlyListWrapper<PredefinedCity> cities;
    private final ReadOnlyStringWrapper regionCode;
    private final ReadOnlyStringWrapper language;
    private final ReadOnlyObjectWrapper<ZoneId> zoneId;
    private final ReadOnlyObjectWrapper<CountryDAO> dataObject;

    PredefinedCountry(CountryElement source, ObservableList<PredefinedCity> cities) {
        LOG.info(String.format("Parsing country %s", source.getLanguageTag()));
        Locale l = Locale.forLanguageTag(source.getLanguageTag());
        name = new ReadOnlyStringWrapper(this, "name", l.getDisplayCountry());
        locale = new ReadOnlyObjectWrapper<>(this, "locale", l);
        zoneId = new ReadOnlyObjectWrapper<>(this, "defaultZoneId", ZoneId.of(source.getDefaultZoneId()));
        regionCode = new ReadOnlyStringWrapper(this, "regionCode", l.getCountry());
        this.cities = new ReadOnlyListWrapper<>(this, "cities", cities);
        language = new ReadOnlyStringWrapper(this, "language", l.getDisplayLanguage());
        dataObject = new ReadOnlyObjectWrapper<>(this, "dataObject");
    }

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

    @Override
    public ZoneId getZoneId() {
        return zoneId.get();
    }

    @Override
    public ReadOnlyObjectProperty<ZoneId> zoneIdProperty() {
        return zoneId;
    }

    @Override
    public String getLanguage() {
        return language.get();
    }

    @Override
    public ReadOnlyStringProperty languageProperty() {
        return language.getReadOnlyProperty();
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

    @Override
    public PredefinedCountry getPredefinedData() {
        return this;
    }

    public String getRegionCode() {
        return regionCode.get();
    }

    public ReadOnlyStringProperty regionCodeProperty() {
        return regionCode.getReadOnlyProperty();
    }

    @Override
    public String toString() {
        return name.get();
    }

    @Override
    public ReadOnlyObjectProperty<LocalDateTime> createDateProperty() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.model.predefined.PredefinedCountry#createDateProperty
    }

    @Override
    public ReadOnlyStringProperty createdByProperty() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.model.predefined.PredefinedCountry#createdByProperty
    }

    @Override
    public ReadOnlyObjectProperty<LocalDateTime> lastModifiedDateProperty() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.model.predefined.PredefinedCountry#lastModifiedDateProperty
    }

    @Override
    public ReadOnlyStringProperty lastModifiedByProperty() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.model.predefined.PredefinedCountry#lastModifiedByProperty
    }

    @Override
    public CountryDAO getDataObject() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.model.predefined.PredefinedCountry#getDataObject
    }

    @Override
    public ReadOnlyObjectProperty<? extends CountryDAO> dataObjectProperty() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.model.predefined.PredefinedCountry#dataObjectProperty
    }

    @Override
    public ReadOnlyObjectProperty<PredefinedCountry> predefinedDataProperty() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.model.predefined.PredefinedCountry#predefinedDataProperty
    }

    class PlaceHolderDAO extends BasePlaceHolderDAO implements ICountryDAO {

        @Override
        public String getName() {
            return PredefinedCountry.this.getName();
        }

        @Override
        public PredefinedCountry getPredefinedData() {
            return PredefinedCountry.this;
        }
        
    }
    
}
