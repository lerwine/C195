package scheduler.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
import scheduler.dao.AddressDAO;
import scheduler.dao.CityDAO;
import scheduler.dao.CountryDAO;
import scheduler.util.ResourceBundleHelper;
import scheduler.view.annotations.GlobalizationResource;

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
    private static ObservableMap<String, CountryDAO.PredefinedElement> COUNTRY_MAP;
    private static ObservableMap<String, CityDAO.PredefinedElement> CITY_MAP;
    private static ObservableMap<String, AddressDAO.PredefinedElement> ADDRESS_MAP;
    private static ResourceBundle resources;

    private static void CheckLoad() {
        if (null != COUNTRY_MAP) {
            return;
        }
        ObservableMap<String, CountryDAO.PredefinedElement> countryMap = FXCollections.observableHashMap();
        ObservableMap<String, CityDAO.PredefinedElement> cityMap = FXCollections.observableHashMap();
        ObservableMap<String, AddressDAO.PredefinedElement> addressMap = FXCollections.observableHashMap();
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
                    countryMap.put((((PredefinedCountry) t).locale = Locale.forLanguageTag(t.getLanguageTag())).getCountry(), t);
                    t.getCities().forEach((u) -> {
                        ((PredefinedCity) u).country = t;
                        cityMap.put(u.getKey(), u);
                        u.getAddresses().forEach((s) -> {
                            ((PredefinedAddress) s).city = u;
                            addressMap.put(s.getKey(), s);
                        });
                    });
                });
            }
        } catch (IOException | JAXBException ex) {
            Logger.getLogger(PredefinedData.class.getName()).log(Level.SEVERE, String.format("Error loading resource \"%s\"", XML_FILE), ex);
        }
    }

    public static CountryDAO lookupCountry(String regionCode) {
        CheckLoad();
        if (null != regionCode && COUNTRY_MAP.containsKey(regionCode)) {
            CountryDAO.PredefinedElement pd = COUNTRY_MAP.get(regionCode);
            CountryDAO dataAccessObject = pd.getDataAccessObject();
            if (null == dataAccessObject) {
                (dataAccessObject = new CountryDAO()).setPredefinedElement(pd);
            }
            return dataAccessObject;
        }
        return null;
    }

    public static CityDAO lookupCity(String key) {
        CheckLoad();
        if (null != key && CITY_MAP.containsKey(key)) {
            CityDAO.PredefinedElement pd = CITY_MAP.get(key);
            CityDAO dataAccessObject = pd.getDataAccessObject();
            if (null == dataAccessObject) {
                (dataAccessObject = new CityDAO()).setPredefinedElement(pd);
            }
            return dataAccessObject;
        }
        return null;
    }

    public static AddressDAO lookupAddress(String key) {
        CheckLoad();
        if (null != key && ADDRESS_MAP.containsKey(key)) {
            AddressDAO.PredefinedElement pd = ADDRESS_MAP.get(key);
            AddressDAO dataAccessObject = pd.getDataAccessObject();
            if (null == dataAccessObject) {
                (dataAccessObject = new AddressDAO()).setPredefinedElement(pd);
            }
            return dataAccessObject;
        }
        return null;
    }

    public static ObservableMap<String, CountryDAO.PredefinedElement> getCountryMap() {
        CheckLoad();
        return COUNTRY_MAP;
    }

    public static ObservableMap<String, CityDAO.PredefinedElement> getCityMap() {
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
                CityDAO.PredefinedElement pd = t.getPredefinedElement();
                if (null != pd) {
                    map.put(pd.getKey(), t);
                }
            });
            if (!map.isEmpty()) {
                return CITY_MAP.values().stream().map((CityDAO.PredefinedElement t) -> {
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
        return CITY_MAP.values().stream().map((CityDAO.PredefinedElement t) -> {
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
                CountryDAO.PredefinedElement pd = t.getPredefinedElement();
                if (null != pd) {
                    map.put(pd.getLocale().getCountry(), t);
                }
            });
            if (!map.isEmpty()) {
                return COUNTRY_MAP.values().stream().map((CountryDAO.PredefinedElement t) -> {
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
        return COUNTRY_MAP.values().stream().map((CountryDAO.PredefinedElement t) -> {
            CountryDAO dataAccessObject = t.getDataAccessObject();
            if (null == dataAccessObject) {
                (dataAccessObject = new CountryDAO()).setPredefinedElement(t);
            }
            return dataAccessObject;
        });
    }

    @XmlElement(name = CountryDAO.PredefinedElement.ELEMENT_NAME, namespace = PredefinedData.NAMESPACE_URI)
    private List<CountryDAO.PredefinedElement> countries;

    /**
     * Internal: not intended to be instantiated directly.
     */
    public PredefinedData() {
        countries = new ArrayList<>();
    }

    public static abstract class PredefinedCountry {

        private Locale locale;

        public Locale getLocale() {
            return locale;
        }

        public abstract String getLanguageTag();

        public abstract String getDefaultZoneId();

        public abstract List<CityDAO.PredefinedElement> getCities();

        public abstract CountryDAO getDataAccessObject();
    }

    public static abstract class PredefinedCity {

        private CountryDAO.PredefinedElement country;

        public CountryDAO.PredefinedElement getCountry() {
            return country;
        }

        public abstract CityDAO getDataAccessObject();

    }

    public static abstract class PredefinedAddress {

        private CityDAO.PredefinedElement city;

        public CityDAO.PredefinedElement getCity() {
            return city;
        }

        public abstract AddressDAO getDataAccessObject();

    }
}
