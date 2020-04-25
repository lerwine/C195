package scheduler.view.country;

import java.time.ZoneId;
import java.util.Collection;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Stream;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import scheduler.dao.CityDAO;
import scheduler.dao.DataRowState;
import scheduler.observables.ChildPropertyWrapper;
import scheduler.util.MapHelper;
import scheduler.view.city.CityModel;
import scheduler.view.city.CityModelImpl;
import scheduler.model.db.CityRowData;
import scheduler.model.db.CountryRowData;
import scheduler.model.ui.CityDbItem;

/**
 * Models a supported city.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @deprecated 
 */
public class CityOptionModel implements CityModel<CityRowData> {

    private static ObservableMap<String, CityOptionModel> cityOptionMap = null;

    public static CityOptionModel getCityOption(String resourceKey) {
        return getCityOptionMap().get(resourceKey);
    }

    public static ObservableMap<String, CityOptionModel> getCityOptionMap() {
        if (null != cityOptionMap) {
            CountryOptionModel.checkOptionsLoaded();
        } else {
            cityOptionMap = FXCollections.observableMap(MapHelper.toMap(CountryOptionModel.getCountryOptions().stream().flatMap((t) -> t.getCities().stream()),
                    (CityOptionModel c) -> c.getResourceKey()));
        }
        return FXCollections.unmodifiableObservableMap(cityOptionMap);
    }

    public static boolean matchesCode(CityModel<? extends CityRowData> model, String regionCode, String resourceKey) {
        if (null != model && null != regionCode) {
            CityOptionModel optionModel = model.getOptionModel();
            if (null != optionModel) {
                return resourceKey.equals(optionModel.getResourceKey())
                        && regionCode.equals(((CountryOptionModel) optionModel.getCountry()).getRegionCode());
            }
        }
        return false;
    }

    public static boolean matchesCode(CityDAO dao, String regionCode, String resourceKey) {
        if (null != dao && null != dao) {
            if (resourceKey.equals(dao.getName())) {
                CountryRowData country = dao.getCountry();
                if (null != country)
                    return regionCode.equals(country.getName());
            }
        }
        return false;
    }

    public static Stream<CityDbItem<? extends CityRowData>> getCityOptions(Collection<CityDAO> dataObjects) {
//        return CountryOptionModel.getCountryOptions().stream().flatMap((t) -> t.getCities().stream()).map((t) -> {
//            String resourceKey = t.getResourceKey();
//            String regionCode = ((CountryOptionModel) t.getCountry()).getRegionCode();
//            Optional<CityDAO> matching = dataObjects.stream().filter((CityDAO u) -> matchesCode(u, regionCode, resourceKey)).findFirst();
//            if (matching.isPresent()) {
//                return (CityItem<? extends CityRowData>) new CityModelImpl(matching.get());
//            }
//            return (CityItem<? extends CityRowData>) t;
//        });
        throw new UnsupportedOperationException();
    }
    
    private final ReadOnlyStringWrapper resourceKey;
    private final ReadOnlyStringWrapper name;
    private final ReadOnlyStringWrapper nativeName;
    private final ReadOnlyStringWrapper nativeCountryName;
    private final ReadOnlyObjectWrapper<CityCountryModel<? extends CountryRowData>> country;
    private final ChildPropertyWrapper<String, CityCountryModel<? extends CountryRowData>> countryName;
    private final ReadOnlyObjectWrapper<CityRowData> dataObject;
    private final ReadOnlyIntegerWrapper primaryKey;
    private final ReadOnlyObjectWrapper<Locale> locale;
    private final ReadOnlyObjectWrapper<ZoneId> zoneId;

    CityOptionModel(String resourceKey, String name, String nativeName, Locale locale, ZoneId zoneId,
            CountryOptionModel country) {
        this.resourceKey = new ReadOnlyStringWrapper(this, "resourceKey", resourceKey);
        primaryKey = new ReadOnlyIntegerWrapper(this, "primaryKey", Integer.MIN_VALUE);
        this.name = new ReadOnlyStringWrapper(this, "name", name);
        this.nativeName = new ReadOnlyStringWrapper(this, "nativeName", nativeName);
        this.nativeCountryName = new ReadOnlyStringWrapper(this, "nativeCountryName", locale.getDisplayCountry(locale));
        this.country = new ReadOnlyObjectWrapper<>(this, "country", country);
        countryName = new ChildPropertyWrapper<>(this, "countryName", this.country, (t) -> t.nameProperty());
        this.locale = new ReadOnlyObjectWrapper<>(this, "locale", locale);
        this.zoneId = new ReadOnlyObjectWrapper<>(this, "zoneId", zoneId);
        this.dataObject = new ReadOnlyObjectWrapper<>(this, "dataObject", new CityRowData() {
            @Override
            public String getName() {
                return CityOptionModel.this.getResourceKey();
            }

            @Override
            public CountryRowData getCountry() {
                return CityOptionModel.this.getCountry().getDataObject();
            }

            @Override
            public int getPrimaryKey() {
                return CityOptionModel.this.getPrimaryKey();
            }

            @Override
            public DataRowState getRowState() {
                return DataRowState.UNMODIFIED;
            }

            @Override
            public boolean isExisting() {
                return false;
            }

            @Override
            public boolean equals(Object obj) {
                return null != obj && obj instanceof CityRowData && this == obj;
            }

            @Override
            public int hashCode() {
                return CityOptionModel.this.hashCode();
            }

            @Override
            public String toString() {
                return CityOptionModel.this.toString();
            }

        });
    }

    public String getResourceKey() {
        return resourceKey.get();
    }

    public ReadOnlyStringProperty resourceKeyProperty() {
        return resourceKey.getReadOnlyProperty();
    }

    @Override
    public String getName() {
        return name.get();
    }

    public String getRegionCode() {
        return ((CountryOptionModel)country.get()).getRegionCode();
    }

    @Override
    public ReadOnlyStringProperty nameProperty() {
        return name.getReadOnlyProperty();
    }

    public String getNativeName() {
        return nativeName.get();
    }

    public ReadOnlyStringProperty nativeNameProperty() {
        return nativeName.getReadOnlyProperty();
    }

    public String getNativeCountryName() {
        return nativeCountryName.get();
    }

    public ReadOnlyStringProperty nativeCountryNameProperty() {
        return nativeCountryName.getReadOnlyProperty();
    }

    @Override
    public CityCountryModel<? extends CountryRowData> getCountry() {
        return country.get();
    }

    @Override
    public ReadOnlyObjectProperty<CityCountryModel<? extends CountryRowData>> countryProperty() {
        return country.getReadOnlyProperty();
    }

    @Override
    public String getCountryName() {
        return countryName.get();
    }

    @Override
    public ChildPropertyWrapper<String, CityCountryModel<? extends CountryRowData>> countryNameProperty() {
        return countryName;
    }

    @Override
    public CityRowData getDataObject() {
        return dataObject.get();
    }

    @Override
    public ReadOnlyObjectProperty<CityRowData> dataObjectProperty() {
        return dataObject.getReadOnlyProperty();
    }

    @Override
    public int getPrimaryKey() {
        return primaryKey.get();
    }

    @Override
    public ReadOnlyIntegerProperty primaryKeyProperty() {
        return primaryKey.getReadOnlyProperty();
    }

    public Locale getLocale() {
        return locale.get();
    }

    public ReadOnlyObjectProperty<Locale> localeProperty() {
        return locale.getReadOnlyProperty();
    }

    public ZoneId getZoneId() {
        return zoneId.get();
    }

    public ReadOnlyObjectProperty<ZoneId> zoneIdProperty() {
        return zoneId.getReadOnlyProperty();
    }

    @Override
    public CityOptionModel getOptionModel() {
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        return null != obj && obj instanceof CityOptionModel && this == obj;
    }

    @Override
    public int hashCode() {
        return resourceKey.get().hashCode();
    }

    @Override
    public String toString() {
        return name.get();
    }

    void refresh(ResourceBundle cityNamesBundle) {
        name.set(cityNamesBundle.getString(resourceKey.get()));
    }

}
