package scheduler.view.country;

import java.io.IOException;
import java.io.InputStream;
import java.time.DateTimeException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import scheduler.AppResources;
import scheduler.dao.CountryElement;
import scheduler.dao.DataRowState;
import scheduler.util.InternalException;
import scheduler.util.MapHelper;
import scheduler.util.ResourceBundleHelper;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.city.SupportedLocale;

/**
 * Models a supported country.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/cityNames")
public class CountryOptionModel implements CityCountryModel<CountryElement> {

    public static final String PROPERTIES_FILE_CITIES = "scheduler/supportedCities.properties";
    private static final Logger LOG = Logger.getLogger(CountryOptionModel.class.getName());
    private static Locale targetLocale;
    private static ObservableMap<String, CountryOptionModel> countryOptionMap = null;
    private static ObservableList<CountryOptionModel> countryOptions = null;

    static void checkOptionsLoaded() {
        if (null != countryOptions) {
            Locale locale = Locale.getDefault(Locale.Category.DISPLAY);
            if (!targetLocale.toLanguageTag().equals(locale.toLanguageTag())) {
                targetLocale = locale;
                countryOptions.forEach((t) -> t.refresh(ResourceBundleHelper.getBundle(CountryOptionModel.class)));
            }
        }
    }

    public static CountryOptionModel getCountryOption(String regionCode) {
        return getCountryOptionMap().get(regionCode);
    }

    public static ObservableMap<String, CountryOptionModel> getCountryOptionMap() {
        if (null != countryOptionMap) {
            checkOptionsLoaded();
        } else {
            countryOptionMap = FXCollections.observableMap(MapHelper.toMap(getCountryOptions(), (CountryOptionModel c) -> c.getRegionCode()));
        }
        return FXCollections.unmodifiableObservableMap(countryOptionMap);
    }

    static class SupportedCityItem {

        private final String key;
        private final Locale locale;
        private final ZoneId zoneId;

        SupportedCityItem(String key, String value) {
            this.key = key;
            int index = value.indexOf(",");
            assert index > 0 && index < value.length() - 1 : "Invalid language/zone id pair";
            locale = Locale.forLanguageTag(value.substring(0, index));
            assert !locale.getCountry().isEmpty() : "Language tag does not indicate a country";
            try {
                zoneId = ZoneId.of(value.substring(index + 1));
            } catch (DateTimeException ex) {
                LOG.log(Level.SEVERE, String.format("Invalid zone id \"%s\"", value.substring(index + 1)), ex);
                throw new InternalException("Invalid zone ID", ex);
            }

        }
    }

    public static ObservableList<CountryOptionModel> getCountryOptions() {
        if (null != countryOptions) {
            checkOptionsLoaded();
            return FXCollections.unmodifiableObservableList(countryOptions);
        }
        String resourceName = ResourceBundleHelper.getGlobalizationResourceName(CountryOptionModel.class);
        HashMap<String, ResourceBundle> allBundles = MapHelper.toMap(SupportedLocale.values(),
                (l) -> l.getLanguageCode(),
                (l) -> ResourceBundle.getBundle(resourceName, l.toLocale(), CountryOptionModel.class.getClassLoader()));
        targetLocale = Locale.getDefault(Locale.Category.DISPLAY);
        countryOptions = FXCollections.observableArrayList();
        Properties supportedCityProperties = new Properties();
        try (InputStream iStream = AppResources.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE_CITIES)) {
            if (iStream == null) {
                LOG.log(Level.SEVERE, String.format("File \"%s\" not found.", PROPERTIES_FILE_CITIES));
            } else {
                supportedCityProperties.load(iStream);
            }
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, String.format("Error loading resource \"%s\"", PROPERTIES_FILE_CITIES), ex);
        }

        final HashMap<String, ArrayList<String>> cityResourceKeyByRegionCode = new HashMap<>();
        final HashMap<String, Locale> localeByCityResourceKey = new HashMap<>();
        final HashMap<String, ZoneId> zoneIdByCityResourceKey = new HashMap<>();
        supportedCityProperties.stringPropertyNames().forEach((resourceKey) -> {
            String value = supportedCityProperties.getProperty(resourceKey);
            int index = value.indexOf(",");
            assert index > 0 && index < value.length() - 1 : "Invalid language/zone id pair";
            Locale locale = Locale.forLanguageTag(value.substring(0, index));
            assert !locale.getCountry().isEmpty() : "Language tag does not indicate a country";
            try {
                zoneIdByCityResourceKey.put(resourceKey, ZoneId.of(value.substring(index + 1)));
                localeByCityResourceKey.put(resourceKey, locale);
            } catch (DateTimeException ex) {
                LOG.log(Level.SEVERE, String.format("Invalid zone id \"%s\"", value.substring(index + 1)), ex);
                throw new InternalException("Invalid zone ID", ex);
            }
            String regionCode = locale.getCountry();
            if (cityResourceKeyByRegionCode.containsKey(regionCode)) {
                cityResourceKeyByRegionCode.get(regionCode).add(resourceKey);
            } else {
                ArrayList<String> list = new ArrayList<>();
                list.add(resourceKey);
                cityResourceKeyByRegionCode.put(regionCode, list);
            }
        });
        ResourceBundle cityNamesBundle = ResourceBundleHelper.getBundle(CountryOptionModel.class);
        cityResourceKeyByRegionCode.keySet().forEach((regionCode) -> {
            LOG.log(Level.FINER, () -> String.format("Creating country from region code %s", regionCode));
            ArrayList<String> cityResourceKeyList = cityResourceKeyByRegionCode.get(regionCode);
            ObservableList<CityOptionModel> cities = FXCollections.observableArrayList();
            CountryOptionModel country = new CountryOptionModel(regionCode,
                    localeByCityResourceKey.get(cityResourceKeyList.get(0)).getDisplayCountry(), cities);
            cityResourceKeyList.forEach((resourceKey) -> {
                LOG.log(Level.FINER, () -> String.format("Creating city from resource key %s", resourceKey));
                Locale locale = localeByCityResourceKey.get(resourceKey);
                assert allBundles.containsKey(locale.getLanguage()) : "No resource bundle for language";
                assert cityNamesBundle.containsKey(resourceKey) : "No city name mapping for resource key";
                ResourceBundle rb = allBundles.get(locale.getLanguage());
                assert rb.containsKey(resourceKey) : "No native city name mapping for resource key";
                rb.getString(resourceKey);
                cities.add(new CityOptionModel(resourceKey, cityNamesBundle.getString(resourceKey), rb.getString(resourceKey), locale,
                        zoneIdByCityResourceKey.get(resourceKey), country));
            });
            countryOptions.add(country);
        });

        return FXCollections.unmodifiableObservableList(countryOptions);
    }

    private final ReadOnlyStringWrapper name;
    private final ReadOnlyStringWrapper regionCode;
    private final ReadOnlyObjectWrapper<CountryElement> dataObject;
    private final ReadOnlyIntegerWrapper primaryKey;
    private final ReadOnlyListWrapper<CityOptionModel> cities;

    public ObservableList<CityOptionModel> getCities() {
        return cities.get();
    }

    public ReadOnlyListProperty<CityOptionModel> citiesProperty() {
        return cities.getReadOnlyProperty();
    }

    private CountryOptionModel(String regionCode, String name, ObservableList<CityOptionModel> backingList) {
        this.regionCode = new ReadOnlyStringWrapper(regionCode);
        cities = new ReadOnlyListWrapper<>(FXCollections.unmodifiableObservableList(backingList));
        primaryKey = new ReadOnlyIntegerWrapper(Integer.MIN_VALUE);
        this.name = new ReadOnlyStringWrapper(name);
        dataObject = new ReadOnlyObjectWrapper<>(new CountryElement() {
            @Override
            public String getName() {
                return CountryOptionModel.this.getRegionCode();
            }

            @Override
            public int getPrimaryKey() {
                return CountryOptionModel.this.getPrimaryKey();
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
                return null != obj && obj instanceof CountryElement && this == obj;
            }

            @Override
            public int hashCode() {
                return CountryOptionModel.this.hashCode();
            }

            @Override
            public String toString() {
                return CountryOptionModel.this.toString();
            }
        });
    }

    @Override
    public String getName() {
        return name.get();
    }

    @Override
    public ReadOnlyStringProperty nameProperty() {
        return name.getReadOnlyProperty();
    }

    public String getRegionCode() {
        return regionCode.get();
    }

    public ReadOnlyStringProperty regionCodeProperty() {
        return regionCode.getReadOnlyProperty();
    }

    @Override
    public CountryElement getDataObject() {
        return dataObject.get();
    }

    @Override
    public ReadOnlyObjectProperty<CountryElement> dataObjectProperty() {
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

    @Override
    public CountryOptionModel getOptionModel() {
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        return null != obj && obj instanceof CountryOptionModel && this == obj;
    }

    @Override
    public int hashCode() {
        return name.get().hashCode();
    }

    @Override
    public String toString() {
        return name.get();
    }

    private void refresh(ResourceBundle cityNamesBundle) {
        LOG.log(Level.FINER, () -> String.format("Refreshing country with region code %s", regionCode));
        name.set(cityNamesBundle.getString(cities.get(0).getLocale().getDisplayCountry()));
        cities.forEach((t) -> {
            String resourceKey = t.getResourceKey();
            LOG.log(Level.FINER, () -> String.format("Refreshing with from resource key %s", resourceKey));
            assert cityNamesBundle.containsKey(resourceKey) : "No city name mapping for resource key";
            t.refresh(cityNamesBundle);
        });
    }

}
