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
import scheduler.dao.CountryDbRecord;
import scheduler.dao.DbRecord;
import scheduler.dao.DbRecordBase;
import scheduler.model.Country;
import scheduler.model.ui.CountryItem;
import scheduler.model.ui.IFxRecordModel;
import scheduler.util.DB;

/**
 * Represents a pre-defined countries that are supported by the application.
 * <p>
 * This also specifies the {@link Locale} to be used for customers associated with the specified country.</p>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class PredefinedCountry extends PredefinedItem implements IFxRecordModel<CountryDbRecord>, CountryItem, Country {

    private static final Logger LOG = Logger.getLogger(PredefinedCountry.class.getName());

    private final ReadOnlyStringWrapper name;
    private final ReadOnlyObjectWrapper<Locale> locale;
    private final ReadOnlyListWrapper<PredefinedCity> cities;
    private final ReadOnlyStringWrapper regionCode;
    private final ReadOnlyStringWrapper language;
    private final ReadOnlyObjectWrapper<ZoneId> zoneId;
    private final ReadOnlyObjectWrapper<CountryDbRecord> dataObject;
    private final PredefinedDataProperty<PredefinedCountry> predefinedData;
    private final ReadOnlyObjectWrapper<LocalDateTime> createDate;
    private final ReadOnlyStringWrapper createdBy;
    private final ReadOnlyObjectWrapper<LocalDateTime> lastModifiedDate;
    private final ReadOnlyStringWrapper lastModifiedBy;

    PredefinedCountry(CountryElement source, ObservableList<PredefinedCity> cities) {
        PlaceHolderDAO dao = new PlaceHolderDAO();
        LOG.info(String.format("Parsing country %s", source.getLanguageTag()));
        Locale l = Locale.forLanguageTag(source.getLanguageTag());
        name = new ReadOnlyStringWrapper(this, "name", l.getDisplayCountry());
        locale = new ReadOnlyObjectWrapper<>(this, "locale", l);
        zoneId = new ReadOnlyObjectWrapper<>(this, "defaultZoneId", ZoneId.of(source.getDefaultZoneId()));
        regionCode = new ReadOnlyStringWrapper(this, "regionCode", l.getCountry());
        this.cities = new ReadOnlyListWrapper<>(this, "cities", cities);
        language = new ReadOnlyStringWrapper(this, "language", l.getDisplayLanguage());
        dataObject = new ReadOnlyObjectWrapper<>(this, "dataObject", dao);
        predefinedData = new PredefinedDataProperty<>(this);
        createDate = new ReadOnlyObjectWrapper<>(this, "createDate", DB.toLocalDateTime(dao.getCreateDate()));
        createdBy = new ReadOnlyStringWrapper(this, "createdBy", dao.getCreatedBy());
        lastModifiedDate = new ReadOnlyObjectWrapper<>(this, "lastModifiedDate", DB.toLocalDateTime(dao.getLastModifiedDate()));
        lastModifiedBy = new ReadOnlyStringWrapper(this, "lastModifiedBy", dao.getLastModifiedBy());
        dataObject.addListener(this::dataObjectChanged);
    }

    @Override
    protected void onDataObjectChanged(DbRecord newValue) {
        LocalDateTime d = DB.toLocalDateTime(newValue.getCreateDate());
        if (!d.equals(createDate.get())) {
            createDate.set(d);
        }
        String s = newValue.getCreatedBy();
        if (!s.equals(createdBy.get())) {
            createdBy.set(s);
        }
        d = DB.toLocalDateTime(newValue.getLastModifiedDate());
        if (!d.equals(lastModifiedDate.get())) {
            lastModifiedDate.set(d);
        }
        s = newValue.getLastModifiedBy();
        if (!s.equals(lastModifiedBy.get())) {
            lastModifiedBy.set(s);
        }
    }

    @Override
    protected void onDaoPropertyChanged(DbRecord dao, String propertyName) {
        switch (propertyName) {
            case DbRecordBase.PROP_CREATEDATE:
                createDate.set(DB.toLocalDateTime(dao.getCreateDate()));
                break;
            case DbRecordBase.PROP_CREATEDBY:
                createdBy.set(dao.getCreatedBy());
                break;
            case DbRecordBase.PROP_LASTMODIFIEDBY:
                lastModifiedBy.set(dao.getLastModifiedBy());
                break;
            case DbRecordBase.PROP_LASTMODIFIEDDATE:
                lastModifiedDate.set(DB.toLocalDateTime(dao.getLastModifiedDate()));
                break;
        }
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
        return createDate.getReadOnlyProperty();
    }

    @Override
    public String getCreatedBy() {
        return createdBy.get();
    }

    @Override
    public ReadOnlyStringProperty createdByProperty() {
        return createdBy.getReadOnlyProperty();
    }

    @Override
    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate.get();
    }

    @Override
    public ReadOnlyObjectProperty<LocalDateTime> lastModifiedDateProperty() {
        return lastModifiedDate.getReadOnlyProperty();
    }

    @Override
    public String getLastModifiedBy() {
        return lastModifiedBy.get();
    }

    @Override
    public ReadOnlyStringProperty lastModifiedByProperty() {
        return lastModifiedBy.getReadOnlyProperty();
    }

    @Override
    public CountryDbRecord getDataObject() {
        return dataObject.get();
    }

    @Override
    public ReadOnlyObjectProperty<? extends CountryDbRecord> dataObjectProperty() {
        return dataObject;
    }

    @Override
    public ReadOnlyObjectProperty<PredefinedCountry> predefinedDataProperty() {
        return predefinedData;
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
