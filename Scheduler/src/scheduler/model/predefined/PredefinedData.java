package scheduler.model.predefined;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import scheduler.dao.CityDAO;
import scheduler.dao.CountryDAO;
import scheduler.model.ui.CityItem;
import scheduler.model.ui.CountryItem;
import scheduler.util.ResourceBundleHelper;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.model.ui.CityModel;
import scheduler.model.ui.CountryModel;

/**
 * Contains static methods for getting pre-defined location and time zone information.
 * 
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/cityNames")
@XmlRootElement(name = "definitions", namespace = PredefinedData.NAMESPACE_URI)
@XmlAccessorType(XmlAccessType.FIELD)
public class PredefinedData {

    static final String NAMESPACE_URI = "urn:Erwine.Leonard.T:C195:StaticAddresses.xsd";
    static final String XML_FILE = "scheduler/StaticAddresses.xml";
    private static ObservableMap<String, PredefinedCountry> COUNTRY_MAP;
    private static ObservableMap<String, PredefinedCity> CITY_MAP;
    private static ObservableMap<String, PredefinedAddress> ADDRESS_MAP;
    private static ResourceBundle resources;

    private static void CheckLoad() {
        if (null != COUNTRY_MAP) {
            return;
        }
        ObservableMap<String, PredefinedCountry> countryMap = FXCollections.observableHashMap();
        ObservableMap<String, PredefinedCity> cityMap = FXCollections.observableHashMap();
        ObservableMap<String, PredefinedAddress> addressMap = FXCollections.observableHashMap();
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
                    ObservableList<PredefinedCity> cl = FXCollections.observableArrayList();
                    PredefinedCountry n = new PredefinedCountry(t, FXCollections.unmodifiableObservableList(cl));
                    countryMap.put(n.getRegionCode(), n);
                    t.getCities().forEach((u) -> {
                        ObservableList<PredefinedAddress> al = FXCollections.observableArrayList();
                        PredefinedCity c = new PredefinedCity(u, n, FXCollections.unmodifiableObservableList(al));
                        cityMap.put(u.getKey(), c);
                        cl.add(c);
                        u.getAddresses().forEach((s) -> {
                            PredefinedAddress a = new PredefinedAddress(s, c);
                            addressMap.put(s.getKey(), a);
                            al.add(a);
                        });
                    });
                });
            }
        } catch (IOException | JAXBException ex) {
            Logger.getLogger(PredefinedData.class.getName()).log(Level.SEVERE, String.format("Error loading resource \"%s\"", XML_FILE), ex);
        }
    }

    public static PredefinedCountry lookupCountry(String regionCode) {
        CheckLoad();
        return (null != regionCode && COUNTRY_MAP.containsKey(regionCode)) ? COUNTRY_MAP.get(regionCode) : null;
    }
    
    public static PredefinedCity lookupCity(String key) {
        CheckLoad();
        return (null != key && CITY_MAP.containsKey(key)) ? CITY_MAP.get(key) : null;
    }
    
    public static PredefinedAddress lookupAddress(String key) {
        CheckLoad();
        return (null != key && ADDRESS_MAP.containsKey(key)) ? ADDRESS_MAP.get(key) : null;
    }
    
    public static ObservableMap<String, PredefinedCountry> getCountryMap() {
        CheckLoad();
        return COUNTRY_MAP;
    }

    public static ObservableMap<String, PredefinedCity> getCityMap() {
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

    public static Stream<? extends CityItem> getCityOptions(Collection<CityDAO> values) {
        if (null == values || values.isEmpty()) {
            return CITY_MAP.values().stream();
        }
        HashMap<String, CityDAO> map = new HashMap<>();
        values.forEach((t) -> {
            map.put(t.getName(), t);
        });
        return CITY_MAP.values().stream().map((t) -> (map.containsKey(t.getResourceKey())) ? new CityModel(map.get(t.getResourceKey())) : t);
    }

    public static Stream<? extends CountryItem> getCountryOptions(Collection<CountryDAO> values) {
        if (null == values || values.isEmpty()) {
            return COUNTRY_MAP.values().stream();
        }
        HashMap<String, CountryDAO> map = new HashMap<>();
        values.forEach((t) -> {
            map.put(t.getName(), t);
        });
        return COUNTRY_MAP.values().stream().map((t) -> (map.containsKey(t.getRegionCode())) ? new CountryModel(map.get(t.getRegionCode())) : t);
    }

    @XmlElement(name = CountryElement.ELEMENT_NAME, namespace = PredefinedData.NAMESPACE_URI)
    private List<CountryElement> countries;

    /**
     * Internal: not intended to be instantiated directly.
     */
    public PredefinedData() {
        countries = new ArrayList<>();
    }
}
