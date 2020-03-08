package scheduler;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;

/**
 * Gets settings from the "appconfig.properties" file.
 *
 * @author Leonard T. Erwine
 */
public final class AppConfig extends ResourceBundle implements AppConfigConstants {
    private static final Logger LOG = Logger.getLogger(AppConfig.class.getName());
    private static final AppConfig INSTANCE = new AppConfig();
    private static final Properties APPCONFIG_PROPERTIES;
    private static final Properties SUPPORTED_CITIES;
    private static final Locale[] SUPPORTED_LOCALES;
    private static final boolean[] ALTSTRINGPLACEHOLDERORDER;
    private static final Locale ORIGINAL_EQUIV_DISPLAY_LOCALE;
    private static final Locale ORIGINAL_EQUIV_FORMAT_LOCALE;
    private static final int ORIGINAL_EQUIV_INDEX;

    private Locale formatLocale;
    private Locale displayLocale;
    
    static {
        APPCONFIG_PROPERTIES = new Properties();
        SUPPORTED_CITIES = new Properties();
        ClassLoader classLoader = AppConfig.class.getClassLoader();
        try (InputStream iStream = classLoader.getResourceAsStream(PROPERTIES_FILE_APPCONFIG)) {
            if (iStream == null) {
                LOG.log(Level.SEVERE, String.format("File \"%s\" not found.", PROPERTIES_FILE_APPCONFIG));
            } else {
                APPCONFIG_PROPERTIES.load(iStream);
            }
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, String.format("Error loading resource \"%s\"", PROPERTIES_FILE_APPCONFIG), ex);
        }
        String s = APPCONFIG_PROPERTIES.getProperty(PROPERTYKEY_SUPPORTEDLOCALES, "").trim();
        if (s.isEmpty()) {
            LOG.log(Level.SEVERE, String.format("Resource \"%s\" does not contain a value for key %s", PROPERTIES_FILE_CITIES, PROPERTYKEY_SUPPORTEDLOCALES));
            SUPPORTED_LOCALES = new Locale[0];
            ALTSTRINGPLACEHOLDERORDER = new boolean[0];
            ORIGINAL_EQUIV_DISPLAY_LOCALE = ORIGINAL_EQUIV_FORMAT_LOCALE = null;
            ORIGINAL_EQUIV_INDEX = -1;
        } else {
            ArrayList<Locale> locales = new ArrayList<>();
            Arrays.stream(s.split(",")).map((t) -> t.trim()).filter((t) -> !t.isEmpty()).forEach((t) -> {
                Locale locale = Locale.forLanguageTag(t);
                if (locale.getLanguage().isEmpty())
                    LOG.log(Level.SEVERE, String.format("Resource \"%s\" contains an unknown language tag %s in key %s", PROPERTIES_FILE_CITIES, t, PROPERTYKEY_SUPPORTEDLOCALES));
                else {
                    String tag = locale.toLanguageTag();
                    if (!locales.stream().allMatch((l) -> l.toLanguageTag().equals(tag)))
                        locales.add(locale);
                }
            });
            if (locales.isEmpty()) {
                SUPPORTED_LOCALES = new Locale[0];
                ALTSTRINGPLACEHOLDERORDER = new boolean[0];
                ORIGINAL_EQUIV_DISPLAY_LOCALE = ORIGINAL_EQUIV_FORMAT_LOCALE = null;
                ORIGINAL_EQUIV_INDEX = -1;
                LOG.log(Level.SEVERE, String.format("Resource \"%s\" does not contain any language identifiers for key %s", PROPERTIES_FILE_CITIES, PROPERTYKEY_SUPPORTEDLOCALES));
            } else {
                s = APPCONFIG_PROPERTIES.getProperty(PROPERTYKEY_ALTSTRINGPLACEHOLDERORDER, "").trim();
                List<String> altNames = (s.isEmpty()) ? new ArrayList<>() : Arrays.asList(s.split(","));
                int i = 0;
                while (i < altNames.size()) {
                    if ((s = altNames.get(i).trim()).isEmpty()) {
                        altNames.remove(i);
                    } else {
                        altNames.set(i, s);
                        i++;
                    }
                }
                Locale locale = Locale.getDefault(Locale.Category.DISPLAY);
                SUPPORTED_LOCALES = locales.toArray(new Locale[0]);
                ALTSTRINGPLACEHOLDERORDER = new boolean[SUPPORTED_LOCALES.length];
                for (i = 0; i < SUPPORTED_LOCALES.length; i++)
                    ALTSTRINGPLACEHOLDERORDER[i] = altNames.contains(SUPPORTED_LOCALES[i].getLanguage());
                int index = getBestMatchingLocale(locale);
                if (index < 0) {
                    ORIGINAL_EQUIV_INDEX = 0;
                    ORIGINAL_EQUIV_DISPLAY_LOCALE = ORIGINAL_EQUIV_FORMAT_LOCALE = locales.get(0);
                    Locale.setDefault(ORIGINAL_EQUIV_DISPLAY_LOCALE);
                } else {
                    ORIGINAL_EQUIV_INDEX = index;
                    ORIGINAL_EQUIV_DISPLAY_LOCALE = Locale.getDefault(Locale.Category.DISPLAY);
                    ORIGINAL_EQUIV_FORMAT_LOCALE = Locale.getDefault(Locale.Category.FORMAT);
                }
            }
        }
        try (InputStream iStream = classLoader.getResourceAsStream(PROPERTIES_FILE_CITIES)) {
            if (iStream == null) {
                LOG.log(Level.SEVERE, String.format("File \"%s\" not found.", PROPERTIES_FILE_CITIES));
            } else {
                SUPPORTED_CITIES.load(iStream);
            }
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, String.format("Error loading resource \"%s\"", PROPERTIES_FILE_CITIES), ex);
        }
    }

    private ResourceBundle backingBundle;
    
    /**
     * Gets the application {@link ResourceBundle}.
     * @return The application {@link ResourceBundle} for the current {@link Locale#defaultDisplayLocale}.
     */
    public static ResourceBundle getResources() {
        return INSTANCE;
    }

    public static boolean isAltStringPlaceholderOrder() {
        return INSTANCE.altStringPlaceholderOrder;
    }
    
    public static String getResourceString(String key) {
        return (INSTANCE.backingBundle.containsKey(RESOURCEKEY_ALL)) ? INSTANCE.backingBundle.getString(key) : key;
    }
    
    public static String formatCreatedByOn(String createdBy, LocalDateTime createDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL);
        if (INSTANCE.altStringPlaceholderOrder)
            return String.format(INSTANCE.backingBundle.getString(RESOURCEKEY_CREATEDBYON), formatter.format(createDate), createdBy);
        return String.format(INSTANCE.backingBundle.getString(RESOURCEKEY_CREATEDBYON), createdBy, formatter.format(createDate));
    }
    
    public static String formatModifiedByOn(String modifiedBy, LocalDateTime lastModifiedDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL);
        if (INSTANCE.altStringPlaceholderOrder)
            return String.format(INSTANCE.backingBundle.getString(RESOURCEKEY_CREATEDBYON), formatter.format(lastModifiedDate), modifiedBy);
        return String.format(INSTANCE.backingBundle.getString(RESOURCEKEY_CREATEDBYON), modifiedBy, formatter.format(lastModifiedDate));
    }
    
    public static void setLocale(Locale newValue) {
        Locale.setDefault(Locale.Category.DISPLAY, newValue);
        INSTANCE.checkLocale();
    }
    
    public static final Properties getSupportedCities() {
        return SUPPORTED_CITIES;
    }
    
    public static final Iterator<Pair<Locale, Boolean>> getSupportedLocales() { return new SupportedLocalesIterator(); }
    
    public static final String getDbServerName() {
        return APPCONFIG_PROPERTIES.getProperty(PROPERTYKEY_DBSERVERNAME, "");
    }

    public static final String getDatabaseName() {
        return APPCONFIG_PROPERTIES.getProperty(PROPERTYKEY_DBNAME, "");
    }

    public static final String getConnectionUrl() {
        return String.format("jdbc:mysql://%s/%s", getDbServerName(), getDatabaseName());
    }

    public static final String getDbLoginName() {
        return APPCONFIG_PROPERTIES.getProperty(PROPERTYKEY_DBLOGIN, "");
    }

    public static String getDbLoginPassword() {
        return APPCONFIG_PROPERTIES.getProperty(PROPERTYKEY_DBPASSWORD, "");
    }

    public final Locale getFormatLocale() {
        return formatLocale;
    }

    @Override
    public final Locale getLocale() {
        return displayLocale;
    }

    private boolean altStringPlaceholderOrder;
    
    @Override
    protected Set<String> handleKeySet() {
        return backingBundle.keySet();
    }

    @Override
    public Set<String> keySet() {
        return backingBundle.keySet();
    }

    @Override
    public boolean containsKey(String key) {
        return backingBundle.containsKey(key);
    }

    @Override
    public String getBaseBundleName() {
        return APP_BASE_RESOURCE_NAME;
    }

    @Override
    protected Object handleGetObject(String key) {
        return backingBundle.getObject(key);
    }

    @Override
    public Enumeration<String> getKeys() {
        return backingBundle.getKeys();
    }

    private static class SupportedLocalesIterator implements Iterator<Pair<Locale, Boolean>> {
        private int index = 0;
        @Override
        public boolean hasNext() {
            return index < SUPPORTED_LOCALES.length;
        }
        @Override
        public Pair<Locale, Boolean> next() {
            if (index < SUPPORTED_LOCALES.length) {
                boolean alt = ALTSTRINGPLACEHOLDERORDER[index];
                return new Pair<>(SUPPORTED_LOCALES[index++], alt);
            }
            return null;
        }
    }
    
    private AppConfig() {
        Locale oldDefaultLocale = Locale.getDefault(Locale.Category.DISPLAY);
        Locale currentDefaultLocale;
        int index = getBestMatchingLocale(oldDefaultLocale);
        if (index < 0) {
            currentDefaultLocale = ORIGINAL_EQUIV_DISPLAY_LOCALE;
            formatLocale = ORIGINAL_EQUIV_FORMAT_LOCALE;
            displayLocale = SUPPORTED_LOCALES[ORIGINAL_EQUIV_INDEX];
            altStringPlaceholderOrder = ALTSTRINGPLACEHOLDERORDER[ORIGINAL_EQUIV_INDEX];
            Locale.setDefault(Locale.Category.FORMAT, ORIGINAL_EQUIV_FORMAT_LOCALE);
        } else {
            if (index == ORIGINAL_EQUIV_INDEX) {
                currentDefaultLocale = ORIGINAL_EQUIV_DISPLAY_LOCALE;
                formatLocale = ORIGINAL_EQUIV_FORMAT_LOCALE;
            } else
                currentDefaultLocale = formatLocale = SUPPORTED_LOCALES[index];
            altStringPlaceholderOrder = ALTSTRINGPLACEHOLDERORDER[index];
            displayLocale = SUPPORTED_LOCALES[index];
        }
        if (!currentDefaultLocale.toLanguageTag().equals(oldDefaultLocale.toLanguageTag()))
            Locale.setDefault(Locale.Category.DISPLAY, currentDefaultLocale);
        oldDefaultLocale = Locale.getDefault(Locale.Category.FORMAT);
        if (!formatLocale.toLanguageTag().equals(oldDefaultLocale.toLanguageTag()))
            Locale.setDefault(Locale.Category.FORMAT, formatLocale);
        try {
            backingBundle = ResourceBundle.getBundle(APP_BASE_RESOURCE_NAME, displayLocale, AppConfig.class.getClassLoader());
        } catch (MissingResourceException ex) {
            LOG.log(Level.SEVERE, String.format("Error loading resource bundle %s for %s",
                    APP_BASE_RESOURCE_NAME, getLocale().toLanguageTag()), ex);
        }
    }
    
    public final Locale checkLocale() {
        Locale oldSupportedLocale = displayLocale;
        Locale oldFormatLocale = formatLocale;
        Locale oldDefaultLocale = Locale.getDefault(Locale.Category.DISPLAY);
        int index = getBestMatchingLocale(oldDefaultLocale);
        Locale currentDefaultLocale;
        if (index < 0) {
            currentDefaultLocale = ORIGINAL_EQUIV_DISPLAY_LOCALE;
            formatLocale = ORIGINAL_EQUIV_FORMAT_LOCALE;
            displayLocale = SUPPORTED_LOCALES[ORIGINAL_EQUIV_INDEX];
            altStringPlaceholderOrder = ALTSTRINGPLACEHOLDERORDER[ORIGINAL_EQUIV_INDEX];
        } else {
            displayLocale = SUPPORTED_LOCALES[index];
            altStringPlaceholderOrder = ALTSTRINGPLACEHOLDERORDER[index];
            if (index == ORIGINAL_EQUIV_INDEX) {
                currentDefaultLocale = ORIGINAL_EQUIV_DISPLAY_LOCALE;
                formatLocale = ORIGINAL_EQUIV_FORMAT_LOCALE;
            } else {
                currentDefaultLocale = formatLocale = SUPPORTED_LOCALES[index];
            }
        }
        if (!currentDefaultLocale.toLanguageTag().equals(oldDefaultLocale.toLanguageTag()))
            Locale.setDefault(Locale.Category.DISPLAY, currentDefaultLocale);
        if (!formatLocale.toLanguageTag().equals(Locale.getDefault(Locale.Category.FORMAT).toLanguageTag()))
            Locale.setDefault(Locale.Category.FORMAT, formatLocale);
        if (!displayLocale.toLanguageTag().equals(oldSupportedLocale.toLanguageTag()) ||
                !formatLocale.toLanguageTag().equals(oldFormatLocale.toLanguageTag())) {
            try {
                backingBundle = ResourceBundle.getBundle(APP_BASE_RESOURCE_NAME, displayLocale, AppConfig.class.getClassLoader());
            } catch (MissingResourceException ex) {
                LOG.log(Level.SEVERE, String.format("Error loading resource bundle %s for %s",
                        APP_BASE_RESOURCE_NAME, displayLocale.toLanguageTag()), ex);
            }
        }
        return displayLocale;
    }
    
    private static int getBestMatchingLocale(Locale target) {
        if (null == target)
            return -1;
        Iterator<Pair<Locale, Boolean>> iterator = new SupportedLocalesIterator();
        int index = -1;
        String t = target.toLanguageTag();
        if (t.equals("und"))
            return -1;
        while (iterator.hasNext()) {
            index++;
            Locale l = iterator.next().getKey();
            if (l.toLanguageTag().equals(t))
                return index;
        }
        index = -1;
        t = target.getLanguage();
        String c = target.getCountry();
        iterator = new SupportedLocalesIterator();
        while (iterator.hasNext()) {
            index++;
            Locale l = iterator.next().getKey();
            if (l.getLanguage().equals(t) && l.getCountry().equals(c))
                return index;
        }
        if (c.isEmpty()) {
            index = -1;
            iterator = new SupportedLocalesIterator();
            while (iterator.hasNext()) {
                index++;
                Locale l = iterator.next().getKey();
                if (l.getLanguage().equals(t))
                    return index;
            }
        } else {
            index = -1;
            iterator = new SupportedLocalesIterator();
            while (iterator.hasNext()) {
                index++;
                Locale l = iterator.next().getKey();
                if (l.getLanguage().equals(t) && l.getCountry().isEmpty())
                    return index;
            }
            index = -1;
            iterator = new SupportedLocalesIterator();
            while (iterator.hasNext()) {
                index++;
                Locale l = iterator.next().getKey();
                if (l.getLanguage().equals(t))
                    return index;
            }
            index = -1;
            iterator = new SupportedLocalesIterator();
            while (iterator.hasNext()) {
                index++;
                Locale l = iterator.next().getKey();
                if (l.getCountry().equals(c))
                    return index;
            }
        }
        return -1;
    }
}
