package scheduler.model;

import java.util.Locale;
import java.util.Objects;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface CountryProperties {

    public static String getCountryDisplayText(Locale locale) {
        if (null != locale) {
            return locale.getDisplayCountry();
        }
        return "";
    }

    public static String getCountryAndLanguageDisplayText(Locale locale) {
        if (null != locale) {
            String c = locale.getDisplayCountry();
            if (!c.isEmpty()) {
                String d = locale.getDisplayLanguage();
                if (d.isEmpty()) {
                    return c;
                }
                String v = locale.getDisplayVariant();
                if (!(v.isEmpty() && (v = locale.getDisplayScript()).isEmpty())) {
                    return String.format("%s (%s, %s)", c, d, v);
                }
                return String.format("%s (%s)", c, d);
            }
        }
        return "";
    }

    public static String getLanguageDisplayText(Locale locale) {
        if (null != locale) {
            String d = locale.getDisplayLanguage();
            if (!d.isEmpty()) {
                String v = locale.getDisplayVariant();
                String s = locale.getDisplayScript();
                if (v.isEmpty()) {
                    return (s.isEmpty()) ? d : String.format("%s (%s)", d, s);
                }
                return (s.isEmpty()) ? String.format("%s (%s)", d, v) : String.format("%s (%s, %s)", d, v, s);
            }
        }
        return "";
    }

    public static String toString(CountryProperties country) {
        if (null != country) {
            String n = country.getName();
            return (null == n) ? "" : n;
        }
        return "";
    }

    public static boolean arePropertiesEqual(CountryProperties a, CountryProperties b) {
        if (null == a) {
            return null == b;
        }

        return null != b && (a == b || Objects.equals(a.getLocale(), b.getLocale()));
    }

    public static int compare(CountryProperties a, CountryProperties b) {
        if (Objects.equals(a, b)) {
            return 0;
        }
        if (null == a) {
            return 1;
        }
        if (null == b) {
            return -1;
        }

        String x = a.getName();
        String y = b.getName();
        int result = x.compareToIgnoreCase(y);
        if (result == 0) {
            return x.compareTo(y);
        }
        return result;
    }

    /**
     * Gets the name of the current country.
     *
     * @return The name of the current country.
     */
    String getName();

    /**
     * Gets the {@link Locale} for the current country.
     *
     * @return The {@link Locale} for the current country.
     */
    Locale getLocale();

}
