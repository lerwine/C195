package scheduler.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import scheduler.dao.AddressDAO;
import scheduler.dao.CityDAO;
import scheduler.dao.CountryDAO;
import scheduler.dao.event.AddressDaoEvent;
import scheduler.dao.event.CityDaoEvent;
import scheduler.dao.event.CountryDaoEvent;
import scheduler.dao.event.DbChangeType;
import scheduler.util.ResourceBundleHelper;
import scheduler.view.annotations.GlobalizationResource;

// TODO: Add a listener method that updates associated DAO objects when they're updated.
/**
 * Contains static methods for getting pre-defined location and time zone information.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/cityNames")
@XmlRootElement(name = "definitions", namespace = PredefinedData.NAMESPACE_URI)
@XmlAccessorType(XmlAccessType.FIELD)
public class PredefinedData {

    public static final String NAMESPACE_URI = "urn:Erwine.Leonard.T:C195:StaticAddresses.xsd";
    static final String XML_FILE = "scheduler/StaticAddresses.xml";
    private static ObservableMap<String, CountryDAO.PredefinedCountryElement> COUNTRY_MAP;
    private static ObservableMap<String, CityDAO.PredefinedCityElement> CITY_MAP;
    private static ObservableMap<String, AddressDAO.PredefinedAddressElement> ADDRESS_MAP;
    private static ResourceBundle resources;

    private static void CheckLoad() {
        if (null != COUNTRY_MAP) {
            return;
        }
        ObservableMap<String, CountryDAO.PredefinedCountryElement> countryMap = FXCollections.observableHashMap();
        ObservableMap<String, CityDAO.PredefinedCityElement> cityMap = FXCollections.observableHashMap();
        ObservableMap<String, AddressDAO.PredefinedAddressElement> addressMap = FXCollections.observableHashMap();
        COUNTRY_MAP = FXCollections.unmodifiableObservableMap(countryMap);
        CITY_MAP = FXCollections.unmodifiableObservableMap(cityMap);
        ADDRESS_MAP = FXCollections.unmodifiableObservableMap(addressMap);
        resources = ResourceBundleHelper.getBundle(PredefinedData.class);
        try (InputStream stream = PredefinedData.class.getClassLoader().getResourceAsStream(XML_FILE)) {
            if (stream == null) {
                Logger.getLogger(PredefinedData.class.getName()).log(Level.SEVERE, String.format("File \"%s\" not found.", XML_FILE));
            } else {
                JAXBContext jaxbContext = JAXBContext.newInstance(PredefinedData.class);
                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                PredefinedData result = (PredefinedData) unmarshaller.unmarshal(stream);
                result.countries.forEach((t) -> {
                    PredefinedCountry predefinedCountry = (PredefinedCountry) t;
                    predefinedCountry.locale = Locale.forLanguageTag(t.getLanguageTag());
                    String countryMapKey = predefinedCountry.locale.getCountry();
                    countryMap.put(countryMapKey, t);
                    predefinedCountry.dataAccessObject = CountryDAO.getPredefinedDAO(t);
                    List<CityDAO.PredefinedCityElement> cities = t.getCities();
                    if (null == cities) {
                        predefinedCountry.cities = Collections.emptyList();
                    } else if (!cities.isEmpty()) {
                        cities.forEach((u) -> {
                            PredefinedCity predefinedCity = (PredefinedCity) u;
                            predefinedCity.country = t;
                            cityMap.put(u.getKey(), u);
                            predefinedCity.dataAccessObject = CityDAO.getPredefinedDAO(u);
                            List<AddressDAO.PredefinedAddressElement> addresses = u.getAddresses();
                            if (null == addresses) {
                                predefinedCity.addresses = Collections.emptyList();
                            } else if (!addresses.isEmpty()) {
                                addresses.forEach((s) -> {
                                    PredefinedAddress a = (PredefinedAddress) s;
                                    a.city = u;
                                    addressMap.put(s.getKey(), s);
                                    a.dataAccessObject = AddressDAO.getPredefinedDAO(s);
                                });
                            }
                        });
                    }
                });
            }
        } catch (IOException | JAXBException ex) {
            Logger.getLogger(PredefinedData.class.getName()).log(Level.SEVERE, String.format("Error loading resource \"%s\"", XML_FILE), ex);
        }
    }

    public static CountryDAO tryGetCountry(String regionCode) {
        CheckLoad();
        if (null != regionCode && COUNTRY_MAP.containsKey(regionCode)) {
            return COUNTRY_MAP.get(regionCode).getDataAccessObject();
        }
        return null;
    }

    public static CountryDAO lookupCountry(String regionCode) {
        CheckLoad();
        if (null != regionCode && COUNTRY_MAP.containsKey(regionCode)) {
            CountryDAO.PredefinedCountryElement pd = COUNTRY_MAP.get(regionCode);
            CountryDAO dataAccessObject = pd.getDataAccessObject();
            if (null == dataAccessObject) {
                (dataAccessObject = new CountryDAO()).setPredefinedElement(pd);
            }
            return dataAccessObject;
        }
        throw new IllegalArgumentException("Unknown region code");
    }

    public static CityDAO tryGetCity(String key) {
        CheckLoad();
        if (null != key && CITY_MAP.containsKey(key)) {
            return CITY_MAP.get(key).getDataAccessObject();
        }
        return null;
    }

    public static CityDAO lookupCity(String key) {
        CheckLoad();
        if (null != key && CITY_MAP.containsKey(key)) {
            CityDAO.PredefinedCityElement pd = CITY_MAP.get(key);
            CityDAO dataAccessObject = pd.getDataAccessObject();
            if (null == dataAccessObject) {
                (dataAccessObject = new CityDAO()).setPredefinedElement(pd);
            }
            return dataAccessObject;
        }
        throw new IllegalArgumentException("Unknown city resource key");
    }

    public static AddressDAO tryGetAddress(String key) {
        CheckLoad();
        if (null != key && ADDRESS_MAP.containsKey(key)) {
            return ADDRESS_MAP.get(key).getDataAccessObject();
        }
        return null;
    }

    public static AddressDAO lookupAddress(String key) {
        CheckLoad();
        if (null != key && ADDRESS_MAP.containsKey(key)) {
            AddressDAO.PredefinedAddressElement pd = ADDRESS_MAP.get(key);
            AddressDAO dataAccessObject = pd.getDataAccessObject();
            if (null == dataAccessObject) {
                (dataAccessObject = new AddressDAO()).setPredefinedElement(pd);
            }
            return dataAccessObject;
        }
        throw new IllegalArgumentException("Unknown address resource key");
    }

    public static ObservableMap<String, AddressDAO.PredefinedAddressElement> getAddressMap() {
        CheckLoad();
        return ADDRESS_MAP;
    }

    public static ObservableMap<String, CountryDAO.PredefinedCountryElement> getCountryMap() {
        CheckLoad();
        return COUNTRY_MAP;
    }

    public static ObservableMap<String, CityDAO.PredefinedCityElement> getCityMap() {
        CheckLoad();
        return CITY_MAP;
    }

    public static String getCityDisplayName(String key) {
        CheckLoad();
        if (resources.containsKey(key)) {
            return resources.getString(key);
        }
        return null;
    }

    public static Stream<CityDAO> getCityOptions(Collection<CityDAO> values) {
        if (null != values && !values.isEmpty()) {
            HashMap<String, CityDAO> map = new HashMap<>();
            values.forEach((t) -> {
                CityDAO.PredefinedCityElement pd = t.getPredefinedElement();
                if (null != pd) {
                    map.put(pd.getKey(), t);
                }
            });
            if (!map.isEmpty()) {
                return CITY_MAP.values().stream().map((CityDAO.PredefinedCityElement t) -> {
                    String key = t.getKey();
                    if (map.containsKey(key)) {
                        return map.get(key);
                    }
                    CityDAO dataAccessObject = t.getDataAccessObject();
                    if (null == dataAccessObject) {
                        (dataAccessObject = new CityDAO()).setPredefinedElement(t);
                    }
                    return dataAccessObject;
                });
            }
        }
        return CITY_MAP.values().stream().map((CityDAO.PredefinedCityElement t) -> {
            CityDAO dataAccessObject = t.getDataAccessObject();
            if (null == dataAccessObject) {
                (dataAccessObject = new CityDAO()).setPredefinedElement(t);
            }
            return dataAccessObject;
        });
    }

    public static Stream<CountryDAO> getCountryOptions(Collection<CountryDAO> values) {
        if (null != values && !values.isEmpty()) {
            HashMap<String, CountryDAO> map = new HashMap<>();
            values.forEach((t) -> {
                CountryDAO.PredefinedCountryElement pd = t.getPredefinedElement();
                if (null != pd) {
                    map.put(pd.getLocale().getCountry(), t);
                }
            });
            if (!map.isEmpty()) {
                return COUNTRY_MAP.values().stream().map((CountryDAO.PredefinedCountryElement t) -> {
                    String key = t.getLocale().getCountry();
                    if (map.containsKey(key)) {
                        return map.get(key);
                    }
                    CountryDAO dataAccessObject = t.getDataAccessObject();
                    if (null == dataAccessObject) {
                        (dataAccessObject = new CountryDAO()).setPredefinedElement(t);
                    }
                    return dataAccessObject;
                });
            }
        }
        return COUNTRY_MAP.values().stream().map((CountryDAO.PredefinedCountryElement t) -> {
            CountryDAO dataAccessObject = t.getDataAccessObject();
            if (null == dataAccessObject) {
                (dataAccessObject = new CountryDAO()).setPredefinedElement(t);
            }
            return dataAccessObject;
        });
    }

    public static void onCountryDaoEvent(CountryDaoEvent event) {
        CountryDAO changedDao = event.getTarget();
        CountryDAO.PredefinedCountryElement pde = changedDao.getPredefinedElement();
        if (null != pde) {
            CountryDAO dao = pde.getDataAccessObject();
            if (event.getChangeType() == DbChangeType.DELETED) {
                dao.resetRowState();
            }
            if (!Objects.equals(dao, changedDao)) {
                CountryDAO.getFactory().synchronize(changedDao, dao);
            }
        }
    }

    public static void onCityDaoEvent(CityDaoEvent event) {
        CityDAO changedDao = event.getTarget();
        CityDAO.PredefinedCityElement pde = changedDao.getPredefinedElement();
        if (null != pde) {
            CityDAO dao = pde.getDataAccessObject();
            if (event.getChangeType() == DbChangeType.DELETED) {
                dao.resetRowState();
            }
            if (!Objects.equals(dao, changedDao)) {
                CityDAO.getFactory().synchronize(changedDao, dao);
            }
        }
    }

    public static void onAddressDaoEvent(AddressDaoEvent event) {
        AddressDAO changedDao = event.getTarget();
        AddressDAO.PredefinedAddressElement pde = changedDao.getPredefinedElement();
        if (null != pde) {
            AddressDAO dao = pde.getDataAccessObject();
            if (event.getChangeType() == DbChangeType.DELETED) {
                dao.resetRowState();
            }
            if (!Objects.equals(dao, changedDao)) {
                AddressDAO.getFactory().synchronize(changedDao, dao);
            }
        }
    }

    @XmlElement(name = CountryDAO.PredefinedCountryElement.ELEMENT_NAME, namespace = PredefinedData.NAMESPACE_URI)
    private List<CountryDAO.PredefinedCountryElement> countries;

    /**
     * Internal: not intended to be instantiated directly.
     */
    public PredefinedData() {
        countries = new ArrayList<>();
    }

    public static abstract class PredefinedCountry {

        @XmlTransient
        private CountryDAO dataAccessObject;

        @XmlElement(name = CityDAO.PredefinedCityElement.ELEMENT_NAME, namespace = PredefinedData.NAMESPACE_URI)
        private List<CityDAO.PredefinedCityElement> cities;

        @XmlTransient
        private Locale locale;

        public Locale getLocale() {
            return locale;
        }

        public abstract String getLanguageTag();

        public abstract String getDefaultZoneId();

        public List<CityDAO.PredefinedCityElement> getCities() {
            return cities;
        }

        public synchronized CountryDAO getDataAccessObject() {
            return dataAccessObject;
        }

    }

    public static abstract class PredefinedCity {

        @XmlTransient
        private CityDAO dataAccessObject;

        @XmlElement(name = AddressDAO.PredefinedAddressElement.ELEMENT_NAME, namespace = PredefinedData.NAMESPACE_URI)
        private List<AddressDAO.PredefinedAddressElement> addresses;

        @XmlTransient
        private CountryDAO.PredefinedCountryElement country;

        public CountryDAO.PredefinedCountryElement getCountry() {
            return country;
        }

        public CityDAO getDataAccessObject() {
            return dataAccessObject;
        }

        public List<AddressDAO.PredefinedAddressElement> getAddresses() {
            return addresses;
        }

    }

    public static abstract class PredefinedAddress {

        @XmlTransient
        private AddressDAO dataAccessObject;

        @XmlTransient
        private CityDAO.PredefinedCityElement city;

        public CityDAO.PredefinedCityElement getCity() {
            return city;
        }

        public synchronized AddressDAO getDataAccessObject() {
            return dataAccessObject;
        }

    }
}
