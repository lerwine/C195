package scheduler.model;

import java.io.IOException;
import java.io.InputStream;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import scheduler.AppResources;
import scheduler.SupportedLocale;
import scheduler.dao.CountryDAO;
import scheduler.util.LogHelper;
import scheduler.util.PropertyBindable;

/**
 * Contains static methods for getting pre-defined location and time zone information.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@XmlRootElement(name = PredefinedData.ELEMENT_NAME, namespace = PredefinedData.NAMESPACE_URI)
@XmlType(name = PredefinedData.ELEMENT_NAME, namespace = PredefinedData.NAMESPACE_URI, factoryMethod = "createInstanceJAXB")
@XmlAccessorType(XmlAccessType.FIELD)
public class PredefinedData {

    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(PredefinedData.class.getName()), Level.FINER);

    public static final String ELEMENT_NAME = "definitions";
    public static final String NAMESPACE_URI = "urn:Erwine.Leonard.T:C195:StaticAddresses.xsd";
    static final String ADDRESSES_XML_FILE = "scheduler/StaticAddresses.xml";

    private static Map<String, CorporateCountry> COUNTRY_MAP;
    private static Map<String, CorporateAddress> ADDRESS_MAP;

    public static String getLocaleCountryDisplayName(Locale locale, Locale displayLocale) {
        if (null != locale) {
            String c = locale.getDisplayCountry(displayLocale);
            if (!c.isEmpty()) {
                String d = locale.getDisplayLanguage(displayLocale);
                if (d.isEmpty()) {
                    return c;
                }
                String v = locale.getDisplayVariant(displayLocale);
                if (!(v.isEmpty() && (v = locale.getDisplayScript(displayLocale)).isEmpty())) {
                    return String.format("%s (%s, %s)", c, d, v);
                }
                return String.format("%s (%s)", c, d);
            }
        }
        return "";
    }

    private static void CheckLoadAddresses() {
        if (null != COUNTRY_MAP) {
            return;
        }
        HashMap<String, CorporateCountry> countryMap = new HashMap<>();
        HashMap<String, CorporateAddress> addressMap = new HashMap<>();
        COUNTRY_MAP = Collections.unmodifiableMap(countryMap);
        ADDRESS_MAP = Collections.unmodifiableMap(addressMap);
        try (InputStream stream = PredefinedData.class.getClassLoader().getResourceAsStream(ADDRESSES_XML_FILE)) {
            if (stream == null) {
                LOG.log(Level.SEVERE, String.format("File \"%s\" not found.", ADDRESSES_XML_FILE));
            } else {
                JAXBContext jaxbContext = JAXBContext.newInstance(PredefinedData.class);
                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                PredefinedData result = (PredefinedData) unmarshaller.unmarshal(stream);
                SupportedLocale sl = AppResources.getCurrentLocale();
                Locale displayLocale = sl.getLocale();
                result.countries.forEach((t) -> {
                    PredefinedCountry predefinedCountry = (PredefinedCountry) t;
                    predefinedCountry.locale = Locale.forLanguageTag(predefinedCountry.languageTag);
                    predefinedCountry.displayLocale = sl;
                    predefinedCountry.name = getLocaleCountryDisplayName(predefinedCountry.locale, displayLocale);
                    String countryMapKey = predefinedCountry.locale.getCountry();
                    countryMap.put(countryMapKey, t);
                    List<CorporateCity> cities = t.getCities();
                    if (null == cities) {
                        predefinedCountry.cities = Collections.emptyList();
                    } else if (!cities.isEmpty()) {
                        cities.forEach((u) -> {
                            PredefinedCity predefinedCity = (PredefinedCity) u;
                            predefinedCity.country = t;
                            predefinedCity.zoneId = ZoneId.of(predefinedCity.zoneIdText);
                            List<CorporateAddress> addresses = u.getAddresses();
                            if (null == addresses) {
                                predefinedCity.addresses = Collections.emptyList();
                            } else if (!addresses.isEmpty()) {
                                addresses.forEach((s) -> {
                                    ((PredefinedAddress) s).city = u;
                                    addressMap.put(s.getName(), s);
                                });
                            }
                        });
                    }
                });
            }
        } catch (IOException | JAXBException ex) {
            LOG.log(Level.SEVERE, String.format("Error loading resource \"%s\"", ADDRESSES_XML_FILE), ex);
        }
    }

    /**
     * Looks up a {@link CorporateCountry} by its country/region code.
     *
     * @param regionCode The county/region code to look up.
     * @return The {@link CorporateCountry} with the specified {@code regionCode} or {@code null} if none was found
     */
    public static CorporateCountry getCorporateCountry(String regionCode) {
        CheckLoadAddresses();
        if (null != regionCode && COUNTRY_MAP.containsKey(regionCode)) {
            return COUNTRY_MAP.get(regionCode);
        }
        return null;
    }

    /**
     * Gets a {@link Map} that maps country codes to a {@link List} of {@link ZoneId}s for that country.
     *
     * @return A {@link Map} that maps country codes to a {@link List} of {@link ZoneId}s for that country.
     */
    public static Map<String, CorporateCountry> getCorporateCountryMap() {
        CheckLoadAddresses();
        return COUNTRY_MAP;
    }

    /**
     * Looks up a {@link CorporateAddress} by its friendly name.
     *
     * @param name The name of the {@link CorporateAddress} to look up.
     * @return The {@link CorporateAddress} with the specified {@code name} or {@code null} if none was found
     */
    public static CorporateAddress getCorporateAddress(String name) {
        CheckLoadAddresses();
        if (null != name && ADDRESS_MAP.containsKey(name)) {
            return ADDRESS_MAP.get(name);
        }
        return null;
    }

    /**
     * Gets a {@link Map} that maps country codes to a {@link List} of {@link ZoneId}s for that country.
     *
     * @return A {@link Map} that maps country codes to a {@link List} of {@link ZoneId}s for that country.
     */
    public static Map<String, CorporateAddress> getCorporateAddressMap() {
        CheckLoadAddresses();
        return ADDRESS_MAP;
    }

    private static PredefinedData createInstanceJAXB() {
        return new PredefinedData();
    }

    @XmlElement(name = CorporateCountry.ELEMENT_NAME, namespace = PredefinedData.NAMESPACE_URI)
    private List<CorporateCountry> countries;

    private PredefinedData() {
        countries = new ArrayList<>();
    }

    public static abstract class PredefinedCountry extends PropertyBindable implements CountryProperties {

        @XmlAttribute
        private String languageTag;

        @XmlTransient
        private String name;

        @XmlTransient
        private Locale locale;

        private SupportedLocale displayLocale;

        @XmlElement(name = CorporateCity.ELEMENT_NAME, namespace = PredefinedData.NAMESPACE_URI)
        private List<CorporateCity> cities;

        protected PredefinedCountry() {

        }

        protected PredefinedCountry(Locale locale) {
            languageTag = (this.locale = locale).toLanguageTag();
            this.cities = Collections.emptyList();
        }

        @Override
        public String getName() {
            if (!AppResources.getCurrentLocale().equals(displayLocale)) {
                String oldName = name;
                name = getLocaleCountryDisplayName(locale, (displayLocale = AppResources.getCurrentLocale()).getLocale());
                firePropertyChange(CountryDAO.PROP_NAME, oldName, name);
            }
            return name;
        }

        @Override
        public Locale getLocale() {
            return locale;
        }

        public List<CorporateCity> getCities() {
            return cities;
        }

    }

    public static abstract class PredefinedCity implements CityProperties {

        @XmlAttribute(name = "zoneId")
        private String zoneIdText;

        @XmlTransient
        private ZoneId zoneId;

        @XmlElement(name = CorporateAddress.ELEMENT_NAME, namespace = PredefinedData.NAMESPACE_URI)
        private List<CorporateAddress> addresses;

        @XmlTransient
        private CorporateCountry country;

        protected PredefinedCity() {

        }

        @Override
        public ZoneId getZoneId() {
            return zoneId;
        }

        @Override
        public CorporateCountry getCountry() {
            return country;
        }

        public List<CorporateAddress> getAddresses() {
            return addresses;
        }

    }

    public static abstract class PredefinedAddress implements AddressProperties {

        @XmlTransient
        private CorporateCity city;

        protected PredefinedAddress() {

        }

        @Override
        public CorporateCity getCity() {
            return city;
        }

    }

}
