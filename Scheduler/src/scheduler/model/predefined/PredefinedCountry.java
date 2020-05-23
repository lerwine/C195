package scheduler.model.predefined;

import java.sql.Connection;
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
import scheduler.dao.CountryDbRecord;
import scheduler.dao.ICountryDAO;
import scheduler.model.ui.CountryItem;
import scheduler.model.ui.IFxRecordModel;

/**
 * Represents a pre-defined countries that are supported by the application.
 * <p>
 * This also specifies the {@link Locale} to be used for customers associated with the specified country.</p>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class PredefinedCountry extends PredefinedItem<CountryDbRecord> implements IFxRecordModel<CountryDbRecord>, CountryItem<CountryDbRecord> {

    private static final Logger LOG = Logger.getLogger(PredefinedCountry.class.getName());

    private final ReadOnlyStringWrapper name;
    private final ReadOnlyObjectWrapper<Locale> locale;
    private final ReadOnlyListWrapper<PredefinedCity> cities;
    private final ReadOnlyStringWrapper regionCode;
    private final ReadOnlyStringWrapper language;
    private final ReadOnlyObjectWrapper<ZoneId> zoneId;
    private final PredefinedDataProperty<PredefinedCountry> predefinedData;
    private final ReadOnlyObjectProperty<LocalDateTime> createDate;
    private final ReadOnlyStringProperty createdBy;
    private final ReadOnlyObjectProperty<LocalDateTime> lastModifiedDate;
    private final ReadOnlyStringProperty lastModifiedBy;

    PredefinedCountry(CountryElement source, ObservableList<PredefinedCity> cities) {
        setDataObject(new PlaceHolderDAO());
        createDate = createReadOnlyDaoDateTimeProperty("createDate", (t) -> t.getCreateDate());
        createdBy = createReadOnlyDaoStringProperty("createdBy", (t) -> t.getCreatedBy());
        lastModifiedDate = createReadOnlyDaoDateTimeProperty("lastModifiedDate", (t) -> t.getLastModifiedDate());
        lastModifiedBy = createReadOnlyDaoStringProperty("lastModifiedBy", (t) -> t.getLastModifiedBy());
        LOG.info(String.format("Parsing country %s", source.getLanguageTag()));
        Locale l = Locale.forLanguageTag(source.getLanguageTag());
        name = new ReadOnlyStringWrapper(this, "name", l.getDisplayCountry());
        locale = new ReadOnlyObjectWrapper<>(this, "locale", l);
        zoneId = new ReadOnlyObjectWrapper<>(this, "defaultZoneId", ZoneId.of(source.getDefaultZoneId()));
        regionCode = new ReadOnlyStringWrapper(this, "regionCode", l.getCountry());
        this.cities = new ReadOnlyListWrapper<>(this, "cities", cities);
        language = new ReadOnlyStringWrapper(this, "language", l.getDisplayLanguage());
        predefinedData = new PredefinedDataProperty<>(this);
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
    public LocalDateTime getCreateDate() {
        return createDate.get();
    }

    @Override
    public ReadOnlyObjectProperty<LocalDateTime> createDateProperty() {
        return createDate;
    }

    @Override
    public String getCreatedBy() {
        return createdBy.get();
    }

    @Override
    public ReadOnlyStringProperty createdByProperty() {
        return createdBy;
    }

    @Override
    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate.get();
    }

    @Override
    public ReadOnlyObjectProperty<LocalDateTime> lastModifiedDateProperty() {
        return lastModifiedDate;
    }

    @Override
    public String getLastModifiedBy() {
        return lastModifiedBy.get();
    }

    @Override
    public ReadOnlyStringProperty lastModifiedByProperty() {
        return lastModifiedBy;
    }

    @Override
    public ReadOnlyObjectProperty<PredefinedCountry> predefinedDataProperty() {
        return predefinedData;
    }

    public static CountryDAO save(Connection connection, ICountryDAO source) {
        throw new UnsupportedOperationException("Not supported yet."); // CURRENT: Implement scheduler.model.predefined.PredefinedCountry#save
    }

    class PlaceHolderDAO extends BasePlaceHolderDAO implements CountryDbRecord {

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
