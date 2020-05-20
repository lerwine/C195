package scheduler.model.predefined;

import java.time.LocalDateTime;
import java.time.ZoneId;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import scheduler.dao.CityDbRecord;
import scheduler.dao.DataAccessObject;
import scheduler.dao.DbRecord;
import scheduler.dao.ICountryDAO;
import scheduler.model.City;
import scheduler.model.ui.IFxRecordModel;
import scheduler.observables.NestedStringProperty;
import scheduler.util.DB;
import scheduler.model.ui.CityItem;

/**
 * Represents a pre-defined city that is loaded with the application.
 * <p>
 * This also specifies the {@link ZoneId} to be used for customers associated with the specified city</p>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class PredefinedCity extends PredefinedItem<CityDbRecord> implements IFxRecordModel<CityDbRecord>, CityItem<CityDbRecord>, City {

    private final ReadOnlyStringWrapper name;
    private final ReadOnlyStringWrapper resourceKey;
    private final ReadOnlyStringWrapper language;
    private final ReadOnlyObjectWrapper<ZoneId> zoneId;
    private final ReadOnlyObjectWrapper<PredefinedCountry> country;
    private final ReadOnlyListWrapper<PredefinedAddress> addresses;
    private final NestedStringProperty<PredefinedCountry> countryName;
    private final ReadOnlyObjectWrapper<CityDbRecord> dataObject;
    private final PredefinedDataProperty<PredefinedCity> predefinedData;
    private final ReadOnlyObjectWrapper<LocalDateTime> createDate;
    private final ReadOnlyStringWrapper createdBy;
    private final ReadOnlyObjectWrapper<LocalDateTime> lastModifiedDate;
    private final ReadOnlyStringWrapper lastModifiedBy;

    PredefinedCity(CityElement source, PredefinedCountry country, ObservableList<PredefinedAddress> addresses) {
        PlaceHolderDAO dao = new PlaceHolderDAO();
        String key = source.getKey();
        name = new ReadOnlyStringWrapper(this, "name", PredefinedData.getCityDisplayName(key));
        resourceKey = new ReadOnlyStringWrapper(this, "resourceKey", key);
        zoneId = new ReadOnlyObjectWrapper<>(this, "zoneId", ZoneId.of(source.getZoneId()));
        this.country = new ReadOnlyObjectWrapper<>(this, "country", country);
        this.addresses = new ReadOnlyListWrapper<>(this, "addresses", addresses);
        countryName = new NestedStringProperty<>(this, "countryName", this.country, (t) -> t.nameProperty());
        language = new ReadOnlyStringWrapper(this, "language", country.getLanguage());
        predefinedData = new PredefinedDataProperty<>(this);
        dataObject = new ReadOnlyObjectWrapper<>(this, "dataObject", dao);
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
            case DataAccessObject.PROP_CREATEDATE:
                createDate.set(DB.toLocalDateTime(dao.getCreateDate()));
                break;
            case DataAccessObject.PROP_CREATEDBY:
                createdBy.set(dao.getCreatedBy());
                break;
            case DataAccessObject.PROP_LASTMODIFIEDBY:
                lastModifiedBy.set(dao.getLastModifiedBy());
                break;
            case DataAccessObject.PROP_LASTMODIFIEDDATE:
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
     * Gets the resource bundle key to use for getting the name of the city in the current language.
     *
     * @return The resource bundle key to use for getting the name of the city in the current language.
     */
    public String getResourceKey() {
        return resourceKey.get();
    }

    public ReadOnlyStringProperty resourceKeyProperty() {
        return resourceKey.getReadOnlyProperty();
    }

    /**
     * Gets the time zone to use for customers associated with the current city.
     *
     * @return The {@link ZoneId} to use for customers associated with the current city.
     */
    @Override
    public ZoneId getZoneId() {
        return zoneId.get();
    }

    @Override
    public ReadOnlyObjectProperty<ZoneId> zoneIdProperty() {
        return zoneId.getReadOnlyProperty();
    }

    @Override
    public String getLanguage() {
        return language.get();
    }

    @Override
    public ReadOnlyStringProperty languageProperty() {
        return language.getReadOnlyProperty();
    }

    @Override
    public PredefinedCountry getCountry() {
        return country.get();
    }

    @Override
    public ReadOnlyObjectProperty<PredefinedCountry> countryProperty() {
        return country.getReadOnlyProperty();
    }

    /**
     * Gets a list of addresses associated with the current city.
     *
     * @return A list of addresses associated with the current city.
     */
    public ObservableList<PredefinedAddress> getAddresses() {
        return addresses.get();
    }

    public ReadOnlyListProperty<PredefinedAddress> addressesProperty() {
        return addresses.getReadOnlyProperty();
    }

    @Override
    public ReadOnlyStringProperty countryNameProperty() {
        return countryName.getReadOnlyStringProperty();
    }

    @Override
    public PredefinedCity getPredefinedData() {
        return this;
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
    public CityDbRecord getDataObject() {
        return dataObject.get();
    }

    @Override
    public ReadOnlyObjectProperty<? extends CityDbRecord> dataObjectProperty() {
        return dataObject.getReadOnlyProperty();
    }

    @Override
    public ReadOnlyObjectProperty<PredefinedCity> predefinedDataProperty() {
        return predefinedData;
    }

    class PlaceHolderDAO extends BasePlaceHolderDAO implements CityDbRecord {

        @Override
        public ICountryDAO getCountry() {
            return PredefinedCity.this.getCountry().getDataObject();
        }

        @Override
        public String getName() {
            return PredefinedCity.this.getName();
        }

        @Override
        public PredefinedCity getPredefinedData() {
            return PredefinedCity.this;
        }

    }

}
