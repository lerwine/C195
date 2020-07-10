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
import java.util.TimeZone;
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
import scheduler.util.PropertyBindable;

/**
 * Contains static methods for getting supported locale and address definitions. {@link SupportedCountryDefinition}, {@link SupportedCityDefinition}
 * and {@link CorporateAddress} objects are de-serialized from the {@code scheduler/StaticAddresses.xml} resource.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@XmlRootElement(name = PredefinedData.ELEMENT_NAME, namespace = PredefinedData.NAMESPACE_URI)
@XmlType(name = PredefinedData.ELEMENT_NAME, namespace = PredefinedData.NAMESPACE_URI, factoryMethod = "createInstanceJAXB")
@XmlAccessorType(XmlAccessType.FIELD)
public class PredefinedData {

//    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(PredefinedData.class.getName()), Level.FINER);
    private static final Logger LOG = Logger.getLogger(PredefinedData.class.getName());

    public static final String ELEMENT_NAME = "definitions";
    public static final String NAMESPACE_URI = "urn:Erwine.Leonard.T:C195:StaticAddresses.xsd";
    static final String ADDRESSES_XML_FILE = "scheduler/StaticAddresses.xml";

    private static Map<String, SupportedCountryDefinition> COUNTRY_MAP;
    private static Map<String, CorporateAddress> ADDRESS_MAP;

    private static void CheckLoadAddresses() {
        if (null != COUNTRY_MAP) {
            return;
        }
        HashMap<String, SupportedCountryDefinition> countryMap = new HashMap<>();
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
                result.countries.forEach((t) -> {
                    PredefinedCountry predefinedCountry = (PredefinedCountry) t;
                    predefinedCountry.locale = Locale.forLanguageTag(predefinedCountry.languageTag);
                    predefinedCountry.displayLocale = sl;
                    predefinedCountry.name = CountryProperties.getCountryAndLanguageDisplayText(predefinedCountry.locale);
                    String countryMapKey = predefinedCountry.locale.getCountry();
                    countryMap.put(countryMapKey, t);
                    List<SupportedCityDefinition> cities = t.getCities();
                    if (null == cities) {
                        predefinedCountry.cities = Collections.emptyList();
                    } else if (!cities.isEmpty()) {
                        cities.forEach((u) -> {
                            PredefinedCity predefinedCity = (PredefinedCity) u;
                            predefinedCity.country = t;
                            predefinedCity.timeZone = TimeZone.getTimeZone(ZoneId.of(predefinedCity.zoneIdText));
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
     * Looks up a {@link SupportedCountryDefinition} by its country/region code.
     *
     * @param regionCode The county/region code to look up.
     * @return The {@link SupportedCountryDefinition} with the specified {@code regionCode} or {@code null} if none was found
     */
    public static SupportedCountryDefinition getSupportedCountryDefinition(String regionCode) {
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
    public static Map<String, SupportedCountryDefinition> getSupportedCountryDefinitionMap() {
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

    @SuppressWarnings("unused")
    private static PredefinedData createInstanceJAXB() {
        return new PredefinedData();
    }

    @XmlElement(name = SupportedCountryDefinition.ELEMENT_NAME, namespace = PredefinedData.NAMESPACE_URI)
    private List<SupportedCountryDefinition> countries;

    private PredefinedData() {
        countries = new ArrayList<>();
    }

    public static abstract class PredefinedCountry extends PropertyBindable implements CountryProperties {

        public static final String PROP_DATAACCESSOBJECT = "dataAccessObject";

        @XmlAttribute
        private String languageTag;

        @XmlTransient
        private String name;

        @XmlTransient
        private Locale locale;

        private SupportedLocale displayLocale;

        @XmlElement(name = SupportedCityDefinition.ELEMENT_NAME, namespace = PredefinedData.NAMESPACE_URI)
        private List<SupportedCityDefinition> cities;

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
                name = CountryProperties.getCountryAndLanguageDisplayText(locale);
                firePropertyChange(CountryDAO.PROP_NAME, oldName, name);
            }
            return name;
        }

        @Override
        public Locale getLocale() {
            return locale;
        }

        /**
         * Gets a list of {@link SupportedCityDefinition SupportedCityDefinitions} for the current country.
         *
         * @return A {@link List} of {@link SupportedCityDefinition SupportedCityDefinitions} for the current country.
         */
        public List<SupportedCityDefinition> getCities() {
            return cities;
        }

    }

    public static abstract class PredefinedCity implements CityProperties {

        @XmlAttribute(name = "zoneId")
        private String zoneIdText;

        @XmlTransient
        private TimeZone timeZone;

        @XmlElement(name = CorporateAddress.ELEMENT_NAME, namespace = PredefinedData.NAMESPACE_URI)
        private List<CorporateAddress> addresses;

        @XmlTransient
        private SupportedCountryDefinition country;

        protected PredefinedCity() {

        }

        public TimeZone getTimeZone() {
            return timeZone;
        }

        @Override
        public SupportedCountryDefinition getCountry() {
            return country;
        }

        /**
         * Gets a list of {@link CorporateAddress CorporateAddresses} for the current country.
         *
         * @return A {@link List} of {@link CorporateAddress CorporateAddresses} for the current country.
         */
        public List<CorporateAddress> getAddresses() {
            return addresses;
        }

    }

    public static abstract class PredefinedAddress implements AddressProperties {

        @XmlTransient
        private SupportedCityDefinition city;

        protected PredefinedAddress() {

        }

        @Override
        public SupportedCityDefinition getCity() {
            return city;
        }

    }

}
