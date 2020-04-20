package scheduler.view.city;

import java.time.ZoneId;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import scheduler.AppResources;
import scheduler.dao.AddressElement;
import scheduler.dao.CityElement;
import scheduler.dao.CountryElement;
import scheduler.dao.CustomerElement;
import scheduler.view.address.AddressModel;
import scheduler.view.country.CityCountryModel;
import scheduler.view.country.CityOptionModel;
import scheduler.view.country.CountryOptionModel;
import scheduler.view.customer.CustomerModel;

/**
 * Represents a locale supported by the application. The string value is a IETF BCP 47 language tag which includes both the language and country.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public enum SupportedLocale {
    /**
     * English (Washington DC, United States)
     */
    EN("en-US", "washington_dc"),
    /**
     * Spanish (Guatemala City, Guatemala)
     */
    ES("es-GT", "guatemala_city"),
    /**
     * German (Frankfurt, Germany)
     */
    DE("de-DE", "frankfurt"),
    /**
     * Hindi (Kolkata, India)
     */
    HI("hi-IN", "kolkata");

    private static SupportedLocale getDefaultLocale(CityCountryModel<? extends CountryElement> country) {
        if (null != country) {
            CountryOptionModel optionModel = country.getOptionModel();
            Optional<SupportedLocale> result = fromRegionCode((null != optionModel) ? optionModel.getRegionCode() : country.getDataObject().getName());
            if (result.isPresent()) {
                return result.get();
            }
        }
        return fromLocale(Locale.getDefault());
    }

    public static SupportedLocale getDefaultLocale(CountryElement country) {
        if (null != country) {
            Optional<SupportedLocale> result = fromRegionCode(country.getName());
            if (result.isPresent()) {
                return result.get();
            }
        }
        return fromLocale(Locale.getDefault());
    }

    public static SupportedLocale getDefaultLocale(CityModel<? extends CityElement> city) {
        if (null != city) {
            CityOptionModel optionModel = city.getOptionModel();
            if (null != optionModel) {
                return SupportedLocale.fromLocale(optionModel.getLocale());
            }
            return getDefaultLocale(city.getCountry());
        }
        return fromLocale(Locale.getDefault());
    }

    public static SupportedLocale getDefaultLocale(CityElement city) {
        if (null != city) {
            CityOptionModel cityOption = CityOptionModel.getCityOption(city.getName());
            if (null != cityOption) {
                return fromLocale(cityOption.getLocale());
            }
            return getDefaultLocale(city.getCountry());
        }
        return fromLocale(Locale.getDefault());
    }

    public static SupportedLocale getDefaultLocale(AddressModel<? extends AddressElement> address) {
        if (null != address) {
            return getDefaultLocale(address.getCity());
        }
        return fromLocale(Locale.getDefault());
    }

    public static SupportedLocale getDefaultLocale(AddressElement address) {
        if (null != address) {
            return getDefaultLocale(address.getCity());
        }
        return fromLocale(Locale.getDefault());
    }

    public static SupportedLocale getDefaultLocale(CustomerModel<? extends CustomerElement> customer) {
        if (null != customer) {
            return getDefaultLocale(customer.getAddress());
        }
        return fromLocale(Locale.getDefault());
    }

    public static SupportedLocale getDefaultLocale(CustomerElement customer) {
        if (null != customer) {
            return getDefaultLocale(customer.getAddress());
        }
        return fromLocale(Locale.getDefault());
    }

    public static Optional<SupportedLocale> fromLanguageAndRegion(String languageCode, String regionCode) {
        return Arrays.stream(values()).filter((t) -> t.languageCode.equalsIgnoreCase(languageCode)
                && t.regionCode.equalsIgnoreCase(regionCode)).findFirst();
    }

    public static Optional<SupportedLocale> fromRegionCode(String regionCode) {
        return Arrays.stream(values()).filter((t) -> t.regionCode.equalsIgnoreCase(regionCode)).findFirst();
    }

    public static Optional<SupportedLocale> fromLanguageCode(String languageCode) {
        return Arrays.stream(values()).filter((t) -> t.languageCode.equalsIgnoreCase(languageCode)).findFirst();
    }

    public static SupportedLocale fromLocale(Locale locale) {
        if (null == locale) {
            locale = Locale.getDefault();
        }
        SupportedLocale[] arr = values();
        String lt = locale.toLanguageTag();
        for (SupportedLocale l : arr) {
            if (l.languageTag.equals(lt)) {
                return l;
            }
        }
        for (SupportedLocale l : arr) {
            if (l.languageTag.equalsIgnoreCase(lt)) {
                return l;
            }
        }
        String t = locale.getLanguage();
        for (SupportedLocale l : arr) {
            if (l.languageCode.equals(t)) {
                return l;
            }
        }
        for (SupportedLocale l : arr) {
            if (l.languageCode.equalsIgnoreCase(t)) {
                return l;
            }
        }
        t = locale.getCountry();
        for (SupportedLocale l : arr) {
            if (l.regionCode.equals(t)) {
                return l;
            }
        }
        for (SupportedLocale l : arr) {
            if (l.regionCode.equalsIgnoreCase(t)) {
                return l;
            }
        }

        if (lt.equals(Locale.getDefault().toLanguageTag())) {
            return arr[0];
        }
        return fromLocale(Locale.getDefault());
    }

    public static String toDisplayCountry(SupportedLocale type) {
        if (null == type) {
            return AppResources.getResourceString(AppResources.RESOURCEKEY_NONE);
        }
        return type.toLocale().getDisplayCountry();
    }

    public static String toDisplayLanguage(SupportedLocale type) {
        if (null == type) {
            return AppResources.getResourceString(AppResources.RESOURCEKEY_NONE);
        }
        return type.toLocale().getDisplayLanguage();
    }

    public static String toNativeDisplayLanguage(SupportedLocale type) {
        if (null == type) {
            return AppResources.getResourceString(AppResources.RESOURCEKEY_NONE);
        }
        Locale locale = type.toLocale();
        return locale.getDisplayLanguage(locale);
    }

    public static String toDisplayCity(SupportedLocale type) {
        if (null != type) {
            CityOptionModel cityOption = CityOptionModel.getCityOption(type.homeOfficeKey);
            if (null != cityOption) {
                return cityOption.getName();
            }
        }
        return AppResources.getResourceString(AppResources.RESOURCEKEY_NONE);
    }

    public static ZoneId toZoneId(SupportedLocale type) {
        if (null != type) {
            CityOptionModel cityOption = CityOptionModel.getCityOption(type.homeOfficeKey);
            if (null != cityOption) {
                return cityOption.getZoneId();
            }
        }
        return null;
    }

    private final String regionCode;
    private final String languageCode;
    private final String languageTag;
    private final String homeOfficeKey;

    private SupportedLocale(String languageTag, String homeOfficeKey) {
        Locale locale = Locale.forLanguageTag(languageTag);
        regionCode = locale.getCountry();
        languageCode = locale.getLanguage();
        this.homeOfficeKey = homeOfficeKey;
        this.languageTag = locale.toLanguageTag();
    }

    public String getRegionCode() {
        return regionCode;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public String getHomeOfficeKey() {
        return homeOfficeKey;
    }

    public Locale toLocale() {
        return Locale.forLanguageTag(languageTag);
    }

    @Override
    public String toString() {
        return languageTag;
    }

}
