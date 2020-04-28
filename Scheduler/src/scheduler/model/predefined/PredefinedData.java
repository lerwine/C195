package scheduler.model.predefined;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
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
    private static ResourceBundle resources;

    private static void CheckLoad() {
        if (null != COUNTRY_MAP) {
            return;
        }
        ObservableMap<String, PredefinedCountry> countryMap = FXCollections.observableHashMap();
        ObservableMap<String, PredefinedCity> cityMap = FXCollections.observableHashMap();
        COUNTRY_MAP = FXCollections.unmodifiableObservableMap(countryMap);
        CITY_MAP = FXCollections.unmodifiableObservableMap(cityMap);
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
                        u.getAddresses().forEach((s) -> al.add(new PredefinedAddress(s, c)));
                    });
                });
            }
        } catch (IOException | JAXBException ex) {
            Logger.getLogger(PredefinedData.class.getName()).log(Level.SEVERE, String.format("Error loading resource \"%s\"", XML_FILE), ex);
        }
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

    public static Stream<CityItem> getCityOptions(Collection<CityDAO> values) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.model.predefined.PredefinedData#getCityOptions
    }

    public static Stream<CountryItem> getCountryOptions(Collection<CountryDAO> values) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.model.predefined.PredefinedData#getCountryOptions
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
