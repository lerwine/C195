package scheduler.util;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.InputStream;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import scheduler.Scheduler;
import static scheduler.AppResourceBundleConstants.RESOURCEKEY_CREATEDBYON;
import static scheduler.AppResourceBundleConstants.RESOURCEKEY_MODIFIEDBYON;
import scheduler.AppResources;
import static scheduler.AppResources.PROPERTYKEY_ALTSTRINGPLACEHOLDERORDER;
import static scheduler.AppResources.PROPERTYKEY_SUPPORTEDLOCALES;

/**
 * Caches loaded resource bundles, reloading them if the default Display Locale has changed.
 *
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
 */
public final class ResourceBundleLoader {

    private static final ResourceBundleLoader INSTANCE;
    private static final Logger LOG;
    public static final String PROPERTIES_FILE_CITIES = "scheduler/supportedCities.properties";
    public static final String CITYNAMES_BASE_RESOURCE_NAME = "scheduler/cityNames";
    public static final String PROP_CURRENTDISPLAYLOCALE = "currentDisplayLocale";
    public static final String PROP_CURRENTFORMATLOCALE = "currentFormatLocale";

    static {
        LOG = Logger.getLogger(ResourceBundleLoader.class.getName());
        INSTANCE = new ResourceBundleLoader();
    }

    public static String formatResourceString(Class<?> ctlClass, String key, Object... args) {
        return String.format(getResourceString(ctlClass, key), args);
    }

    public static String getResourceString(Class<?> ctlClass, String key) {
        ResourceBundle bundle = getBundle(ctlClass);
        if (bundle.containsKey(key)) {
            return getBundle(ctlClass).getString(key);
        }
        LOG.severe(String.format("Key \"%s\" not found in resource bundle for %s", key, ctlClass.getName()));
        return key;
    }

    public static final ResourceBundle empty() {
        return new ResourceBundle() {
            private final Enumeration<String> keys = new Enumeration<String>() {
                @Override
                public boolean hasMoreElements() {
                    return false;
                }

                @Override
                public String nextElement() {
                    return null;
                }
            };

            @Override
            protected Object handleGetObject(String key) {
                return null;
            }

            @Override
            public Enumeration<String> getKeys() {
                return keys;
            }

        };
    }

    public static final ResourceBundle getBundle(Class<?> resourceClass) {
        ResourceBundle result = INSTANCE.getBundle(resourceClass, getGlobalizationResourceName(resourceClass));
        if (null == result) {
            return empty();
        }
        return result;
    }

    public static final ResourceBundle getMergedBundle(Class<?> target, Class<?> parent, Class<?>... ancestors) {
        ResourceBundle result;

        synchronized (INSTANCE) {
            Class<?>[] targetAndAncestors;
            int l;
            if (null == ancestors) {
                targetAndAncestors = new Class<?>[]{parent, target};
                l = 1;
            } else {
                l = ancestors.length;
                targetAndAncestors = new Class<?>[l + 2];
                targetAndAncestors[0] = target;
                targetAndAncestors[1] = parent;
                System.arraycopy(ancestors, 0, targetAndAncestors, 2, l);
                l++;
            }
            CacheKey key = new CacheKey(targetAndAncestors);
            HashMap<CacheKey, ResourceBundle> map = INSTANCE.mergedBundleCache;
            if (map.containsKey(key)) {
                result = map.get(key);
            } else {
                result = INSTANCE.getBundle(targetAndAncestors[l], key.names[l]);
                while (--l >= 0) {
                    ResourceBundle r = INSTANCE.getBundle(targetAndAncestors[l], key.names[l]);
                    if (null != r) {
                        result = (null == result) ? r : new MergedResourceBundle(r, result);
                    }
                }
                map.put(key, result);
            }
            if (null == result) {
                return empty();
            }
        }

        return result;
    }

    public static Stream<SupportedLocale> getSupportedLocales() {
        return Arrays.stream(INSTANCE.supportedLocales);
    }

    public static Stream<SupportedCountry> getSupportedCountries() {
        return Arrays.stream(INSTANCE.supportedCountries);
    }

    public static final String getGlobalizationResourceName(Class<?> target) {
        HashMap<String, String> map = INSTANCE.classNameToResourceName;
        synchronized (map) {
            String c = target.getName();
            if (map.containsKey(c)) {
                return map.get(c);
            }
            String n = AnnotationHelper.getGlobalizationResourceName(target);
            map.put(c, n);
            return n;
        }
    }

    /**
     * Get the value of currentDisplayLocale
     *
     * @return the value of currentDisplayLocale
     */
    public static Locale getCurrentDisplayLocale() {
        return INSTANCE.currentDisplayLocale;
    }

    /**
     * Get the value of currentFormatLocale
     *
     * @return the value of currentFormatLocale
     */
    public static Locale getCurrentFormatLocale() {
        return INSTANCE.currentFormatLocale;
    }

    public static String formatCreatedByOn(String createdBy, LocalDateTime createDate) {
        return formatResourceString(AppResources.class, RESOURCEKEY_CREATEDBYON, createdBy, Objects.requireNonNull(createDate));
    }

    public static String formatModifiedByOn(String modifiedBy, LocalDateTime lastModifiedDate) {
        return formatResourceString(AppResources.class, RESOURCEKEY_MODIFIEDBYON, modifiedBy, Objects.requireNonNull(lastModifiedDate));
    }

    private final SupportedLocale[] supportedLocales;
    private final HashMap<String, String> classNameToResourceName;
    private final HashMap<String, ResourceBundle> resourceBundleByClassName;
    private final HashMap<CacheKey, ResourceBundle> mergedBundleCache;
    private final Properties supportedCityProperties;
    private final SupportedCountry[] supportedCountries;
    private Locale currentDisplayLocale;
    private Locale currentFormatLocale;
    private transient final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    private ResourceBundleLoader() {
        classNameToResourceName = new HashMap<>();
        supportedCityProperties = new Properties();
        try (InputStream iStream = AppResources.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE_CITIES)) {
            if (iStream == null) {
                LOG.log(Level.SEVERE, String.format("File \"%s\" not found.", PROPERTIES_FILE_CITIES));
            } else {
                supportedCityProperties.load(iStream);
            }
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, String.format("Error loading resource \"%s\"", PROPERTIES_FILE_CITIES), ex);
        }
        supportedCountries = loadCities();
        resourceBundleByClassName = new HashMap<>();
        mergedBundleCache = new HashMap<>();
        ArrayList<Locale> localeList = new ArrayList<>();
        ArrayList<String> usesAlt = new ArrayList<>();
        Arrays.stream(AppResources.getProperty(PROPERTYKEY_ALTSTRINGPLACEHOLDERORDER, "").split(",")).map((t) -> t.trim()).forEach((t) -> {
            if (!t.isEmpty()) {
                usesAlt.add(t);
            }
        });
        Arrays.stream(AppResources.getProperty(PROPERTYKEY_SUPPORTEDLOCALES, "").split(",")).forEach((String t) -> {
            String s = t.trim();
            if (s.isEmpty()) {
                return;
            }
            Locale locale = Locale.forLanguageTag(s);
            String l = locale.toLanguageTag();
            if (s.equals("und")) {
                LOG.log(Level.SEVERE, String.format("Resource property %s in %s contains unknown language tag \"%s\".",
                        PROPERTYKEY_SUPPORTEDLOCALES, AppResources.PROPERTIES_FILE_APPCONFIG, t));
                return;
            }
            if (!localeList.stream().anyMatch((u) -> u.toLanguageTag().equals(l))) {
                localeList.add(locale);
            }
        });
        if (localeList.isEmpty()) {
            supportedLocales = new SupportedLocale[0];
            LOG.log(Level.SEVERE, String.format("Resource property %s in %s contains no language tags.",
                    PROPERTYKEY_SUPPORTEDLOCALES, AppResources.PROPERTIES_FILE_APPCONFIG));
            return;
        }
        Locale originalDefaultLocale = Locale.getDefault(Locale.Category.DISPLAY);
        String originalDefaultTag = originalDefaultLocale.toLanguageTag();
        ArrayList<Locale.LanguageRange> priorityList = new ArrayList<>();
        priorityList.add(new Locale.LanguageRange(originalDefaultTag));
        String originalLanguage = originalDefaultLocale.getLanguage();
        String originalCountry = originalDefaultLocale.getCountry();
        if (originalCountry.isEmpty()) {
            if (!originalDefaultTag.equals(originalLanguage)) {
                priorityList.add(new Locale.LanguageRange(originalLanguage));
            }
            priorityList.add(new Locale.LanguageRange(String.format("%s-*", originalLanguage)));
        } else {
            String filter = String.format("%s-%s", originalLanguage, originalCountry);
            if (!originalDefaultTag.equals(filter)) {
                priorityList.add(new Locale.LanguageRange(filter));
            }
            priorityList.add(new Locale.LanguageRange(String.format("%s-*", originalLanguage)));
            priorityList.add(new Locale.LanguageRange(String.format("*-%s", originalCountry)));
        }
        Locale toSelect = Locale.lookup(priorityList, Arrays.asList(localeList.stream().toArray(Locale[]::new)));
        if (null == toSelect) {
            supportedLocales = localeList.stream().map((Locale t) -> new SupportedLocale(t, t, t)).toArray(SupportedLocale[]::new);
        } else {
            String toMatch = toSelect.toLanguageTag();
            supportedLocales = localeList.stream().map((Locale t) -> (t.toLanguageTag().equals(toMatch))
                    ? new SupportedLocale(t, originalDefaultLocale, Locale.getDefault(Locale.Category.FORMAT))
                    : new SupportedLocale(t, t, t)).toArray(SupportedLocale[]::new);

            for (SupportedLocale l : supportedLocales) {
                if (l.key.equals(toMatch)) {
                    l.setCurrent();
                    break;
                }
            }
        }
        if (null == currentDisplayLocale) {
            supportedLocales[0].setCurrent();
        }

    }

    private synchronized ResourceBundle getBundle(Class<?> resourceClass, String baseName) {
        if (null == resourceClass || baseName.isEmpty()) {
            return null;
        }

        ResourceBundle result;
        HashMap<String, ResourceBundle> map = resourceBundleByClassName;
        if (map.containsKey(baseName)) {
            return map.get(baseName);
        }
        try {
            result = ResourceBundle.getBundle(baseName, currentDisplayLocale, resourceClass.getClassLoader());
        } catch (MissingResourceException ex) {
            LOG.log(Level.SEVERE, "Error loading resource bundle", ex);
            map.put(baseName, null);
            return null;
        }
        map.put(baseName, result);
        return result;
    }

    /**
     * Add PropertyChangeListener.
     *
     * @param listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove PropertyChangeListener.
     *
     * @param listener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    private SupportedCountry[] loadCities() {
        final HashMap<String, Locale> parsedLocales = new HashMap<>();
        final HashMap<String, ZoneId> parsedZoneIds = new HashMap<>();
        final HashMap<String, ArrayList<String>> countryMap = new HashMap<>();
        supportedCityProperties.stringPropertyNames().stream().forEach((t) -> {
            String ltTz = supportedCityProperties.getProperty(t).trim();
            int index = ltTz.indexOf(",");
            if (index < 1 || index >= ltTz.length() - 1) {
                LOG.log(Level.SEVERE, String.format("Invalid language/zone id pair \"%s\"", ltTz));
            } else {
                Locale locale = Locale.forLanguageTag(ltTz.substring(0, index));
                String c = locale.getCountry();
                if (c.isEmpty()) {
                    LOG.log(Level.SEVERE, String.format("Language tag for \"%s\" does not indicate a country", ltTz));
                    return;
                }
                try {
                    ZoneId zoneId = ZoneId.of(ltTz.substring(index + 1));
                    parsedZoneIds.put(t, zoneId);
                } catch (DateTimeException ex) {
                    LOG.log(Level.SEVERE, String.format("Invalid zone id \"%s\"", ltTz.substring(index + 1)), ex);
                    return;
                }
                parsedLocales.put(t, locale);
                if (countryMap.containsKey(c)) {
                    countryMap.get(c).add(t);
                } else {
                    ArrayList<String> a = new ArrayList<>();
                    a.add(t);
                    countryMap.put(c, a);
                }
            }
        });

        return countryMap.keySet().stream().map((ck) -> {
            ArrayList<String> kList = countryMap.get(ck);
            SupportedCity[] arr = new SupportedCity[kList.size()];
            SupportedCountry country = new SupportedCountry(ck, arr);
            for (int i = 0; i < arr.length; i++) {
                String k = kList.get(i);
                arr[i] = new SupportedCity(k, parsedLocales.get(k), parsedZoneIds.get(k), country);
            }
            return country;
        }).toArray(SupportedCountry[]::new);
    }

    private static final class CacheKey {

        private final int hash;
        private final String[] names;
        private final String[] classes;

        CacheKey(Class<?>[] targetAndAncestors) {
            int i = targetAndAncestors.length;
            names = new String[i];
            classes = new String[i];
            for (int n = 0; n < i; n++) {
                Class<?> c = targetAndAncestors[n];
                names[n] = getGlobalizationResourceName(c);
                classes[n] = c.getName();
            }
            hash = 37 * (259 + Arrays.deepHashCode(names)) + Arrays.deepHashCode(classes);
        }

        @Override
        public int hashCode() {
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            final CacheKey other = (CacheKey) obj;
            return Arrays.deepEquals(names, other.names) && Arrays.deepEquals(classes, other.classes);
        }

    }

    public class SupportedLocale {

        private final String key;
        private String displayName;
        private final String localeDisplayName;
        private final Locale displayLocale;
        private final Locale formatLocale;
        private final Locale userLocale;
        private boolean current = false;

        public String getKey() {
            return key;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getLocaleDisplayName() {
            return localeDisplayName;
        }

        public Locale getDisplayLocale() {
            return displayLocale;
        }

        public Locale getUserLocale() {
            return userLocale;
        }

        public Locale getFormatLocale() {
            return formatLocale;
        }

        public boolean isCurrent() {
            return current;
        }

        @Override
        public int hashCode() {
            return key.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return null != obj && obj instanceof SupportedLocale && obj == this;
        }

        @Override
        public String toString() {
            return displayName;
        }

        public void setCurrent() {
            assert null == Scheduler.getCurrentUser() : "Cannot change locale after user is logged in";
            synchronized (ResourceBundleLoader.this) {
                if (!current) {
                    resourceBundleByClassName.clear();
                    mergedBundleCache.clear();
                    Locale oldCurrentFormatLocale = currentFormatLocale;
                    Locale oldCurrentDisplayLocale = currentDisplayLocale;
                    for (SupportedLocale l : supportedLocales) {
                        l.current = false;
                        l.displayName = l.userLocale.getDisplayName(userLocale);
                    }
                    current = true;
                    currentFormatLocale = formatLocale;
                    currentDisplayLocale = displayLocale;
                    Locale.setDefault(Locale.Category.DISPLAY, userLocale);
                    Locale.setDefault(Locale.Category.FORMAT, formatLocale);
                    ResourceBundle rb = getBundle(AppResources.class, CITYNAMES_BASE_RESOURCE_NAME);
                    for (SupportedCountry country : supportedCountries) {
                        country.refresh(rb);
                    }
                    propertyChangeSupport.firePropertyChange(PROP_CURRENTDISPLAYLOCALE, oldCurrentDisplayLocale, currentDisplayLocale);
                    propertyChangeSupport.firePropertyChange(PROP_CURRENTFORMATLOCALE, oldCurrentFormatLocale, currentFormatLocale);
                }
            }
        }

        private SupportedLocale(Locale displayLocale, Locale userLocale, Locale formatLocale) {
            key = (this.displayLocale = displayLocale).toLanguageTag();
            this.formatLocale = formatLocale;
            this.userLocale = userLocale;
            localeDisplayName = userLocale.getDisplayName(userLocale);
            displayName = localeDisplayName;
        }
    }

    public class SupportedCity {

        private final String key;
        private String displayName;
        private final Locale locale;
        private final ZoneId zoneId;
        private final SupportedCountry country;

        public String getKey() {
            return key;
        }

        public String getDisplayName() {
            return displayName;
        }

        public Locale getLocale() {
            return locale;
        }

        public ZoneId getZoneId() {
            return zoneId;
        }

        public SupportedCountry getCountry() {
            return country;
        }

        @Override
        public int hashCode() {
            return key.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return null != obj && obj instanceof SupportedCity && obj == this;
        }

        @Override
        public String toString() {
            return displayName;
        }

        private SupportedCity(String key, Locale locale, ZoneId zoneId, SupportedCountry country) {
            this.key = displayName = key;
            this.locale = locale;
            this.zoneId = zoneId;
            this.country = country;
        }
    }

    public class SupportedCountry implements List<SupportedCity> {

        private final String key;
        private String displayName;
        private final SupportedCity[] cities;

        public String getKey() {
            return key;
        }

        public String getDisplayName() {
            return displayName;
        }

        @Override
        public int hashCode() {
            return key.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return null != obj && obj instanceof SupportedCountry && obj == this;
        }

        @Override
        public String toString() {
            return displayName;
        }

        private SupportedCountry(String key, SupportedCity[] cities) {
            this.key = displayName = key;
            this.cities = cities;
        }

        private void refresh(ResourceBundle cityNames) {
            displayName = cities[0].locale.getDisplayCountry(currentDisplayLocale);
            for (SupportedCity city : cities) {
                String n = (cityNames.containsKey(city.key)) ? cityNames.getString(city.key).trim() : "";
                city.displayName = (n.isEmpty()) ? city.key : n;
            }
        }

        @Override
        public Stream<SupportedCity> stream() {
            return Arrays.stream(cities);
        }

        public Stream<String> getCityKeys() {
            return Arrays.stream(cities).map((t) -> t.key);
        }

        public SupportedCity get(String key) {
            if (null != key) {
                for (SupportedCity item : cities) {
                    if (item.key.equals(key)) {
                        return item;
                    }
                }
            }
            return null;
        }

        @Override
        public int size() {
            return cities.length;
        }

        @Override
        public boolean isEmpty() {
            return cities.length == 0;
        }

        @Override
        public boolean contains(Object o) {
            if (null != o && o instanceof SupportedCity) {
                for (SupportedCity item : cities) {
                    if (item == o) {
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        public Iterator<SupportedCity> iterator() {
            return new Iterator<SupportedCity>() {
                int index = 0;

                @Override
                public boolean hasNext() {
                    return index < cities.length;
                }

                @Override
                public SupportedCity next() {
                    if (index < cities.length) {
                        return cities[index++];
                    }
                    throw new NoSuchElementException();
                }
            };
        }

        @Override
        public Object[] toArray() {
            Object[] result = new Object[cities.length];
            System.arraycopy(cities, 0, result, 0, cities.length);
            return result;
        }

        @Override
        public <T> T[] toArray(T[] a) {
            if (a.length < cities.length) {
                return (T[]) Arrays.copyOf(cities, cities.length, a.getClass());
            }
            System.arraycopy(cities, 0, a, 0, cities.length);
            if (a.length > cities.length) {
                a[cities.length] = null;
            }
            return a;
        }

        @Override
        public boolean add(SupportedCity e) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return Arrays.asList(cities).containsAll(c);
        }

        @Override
        public boolean addAll(Collection<? extends SupportedCity> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int index, Collection<? extends SupportedCity> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public SupportedCity get(int index) {
            return cities[index];
        }

        @Override
        public SupportedCity set(int index, SupportedCity element) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(int index, SupportedCity element) {
            throw new UnsupportedOperationException();
        }

        @Override
        public SupportedCity remove(int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int indexOf(Object o) {
            if (null != o && o instanceof SupportedCity) {
                for (int index = 0; index < cities.length; index++) {
                    if (cities[index] == o) {
                        return index;
                    }
                }
            }
            return -1;
        }

        @Override
        public int lastIndexOf(Object o) {
            if (null != o && o instanceof SupportedCity) {
                for (int index = cities.length - 1; index >= 0; index--) {
                    if (cities[index] == o) {
                        return index;
                    }
                }
            }
            return -1;
        }

        @Override
        public ListIterator<SupportedCity> listIterator() {
            return listIterator(0);
        }

        @Override
        public ListIterator<SupportedCity> listIterator(int index) {
            if (index < 0 || index > cities.length) {
                throw new IndexOutOfBoundsException();
            }

            return new ListIterator<SupportedCity>() {
                int currentIndex = index;

                @Override
                public boolean hasNext() {
                    return currentIndex < cities.length;
                }

                @Override
                public SupportedCity next() {
                    if (currentIndex < cities.length) {
                        return cities[currentIndex++];
                    }
                    throw new NoSuchElementException();
                }

                @Override
                public boolean hasPrevious() {
                    return currentIndex > 0;
                }

                @Override
                public SupportedCity previous() {
                    if (currentIndex > 0) {
                        return cities[currentIndex--];
                    }
                    throw new NoSuchElementException();
                }

                @Override
                public int nextIndex() {
                    return (currentIndex < cities.length) ? currentIndex + 1 : currentIndex;
                }

                @Override
                public int previousIndex() {
                    return (currentIndex > 0) ? currentIndex - 1 : -1;
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }

                @Override
                public void set(SupportedCity e) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public void add(SupportedCity e) {
                    throw new UnsupportedOperationException();
                }

            };
        }

        @Override
        public List<SupportedCity> subList(int fromIndex, int toIndex) {
            if (fromIndex < 0 || toIndex > cities.length || toIndex < fromIndex) {
                throw new IndexOutOfBoundsException();
            }
            return new List<SupportedCity>() {
                final int start = fromIndex;
                final int end = toIndex;

                @Override
                public int size() {
                    return end - start;
                }

                @Override
                public boolean isEmpty() {
                    return end == start;
                }

                @Override
                public boolean contains(Object o) {
                    if (null != o && o instanceof SupportedCity) {
                        for (int i = start; i < end; i++) {
                            if (cities[i] == o) {
                                return true;
                            }
                        }
                    }
                    return false;
                }

                @Override
                public Iterator<SupportedCity> iterator() {
                    return new Iterator<SupportedCity>() {
                        int index = start - 1;

                        @Override
                        public boolean hasNext() {
                            return index < end;
                        }

                        @Override
                        public SupportedCity next() {
                            if (index < end) {
                                return cities[index++];
                            }
                            throw new NoSuchElementException();
                        }
                    };
                }

                @Override
                public Object[] toArray() {
                    int len = end - start;
                    Object[] result = new Object[len];
                    System.arraycopy(cities, start, result, 0, len);
                    return result;
                }

                @Override
                public <T> T[] toArray(T[] a) {
                    int len = end - start;
                    if (a.length < len) {
                        return (T[]) Arrays.copyOfRange(cities, start, end, a.getClass());
                    }
                    System.arraycopy(cities, start, a, 0, len);
                    if (a.length > len) {
                        a[len] = null;
                    }
                    return a;
                }

                @Override
                public boolean add(SupportedCity e) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public boolean remove(Object o) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public boolean containsAll(Collection<?> c) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public boolean addAll(Collection<? extends SupportedCity> c) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public boolean addAll(int index, Collection<? extends SupportedCity> c) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public boolean removeAll(Collection<?> c) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public boolean retainAll(Collection<?> c) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public void clear() {
                    throw new UnsupportedOperationException();
                }

                @Override
                public SupportedCity get(int index) {
                    return cities[(index < 0) ? index : index + start];
                }

                @Override
                public SupportedCity set(int index, SupportedCity element) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public void add(int index, SupportedCity element) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public SupportedCity remove(int index) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public int indexOf(Object o) {
                    if (null != o && o instanceof SupportedCity) {
                        for (int i = start; i < end; i++) {
                            if (cities[i] == o) {
                                return i;
                            }
                        }
                    }
                    return -1;
                }

                @Override
                public int lastIndexOf(Object o) {
                    if (null != o && o instanceof SupportedCity) {
                        for (int i = end - 1; i >= start; i--) {
                            if (cities[i] == o) {
                                return i;
                            }
                        }
                    }
                    return -1;
                }

                @Override
                public ListIterator<SupportedCity> listIterator() {
                    return listIterator(0); 
                }

                @Override
                public ListIterator<SupportedCity> listIterator(int index) {
                    int len = end - start;
                    if (index < 0 || index > len) {
                        throw new IndexOutOfBoundsException();
                    }

                    return new ListIterator<SupportedCity>() {
                        int currentIndex = index;

                        @Override
                        public boolean hasNext() {
                            return currentIndex < len;
                        }

                        @Override
                        public SupportedCity next() {
                            if (currentIndex < len) {
                                return cities[(currentIndex++) + start];
                            }
                            throw new NoSuchElementException();
                        }

                        @Override
                        public boolean hasPrevious() {
                            return currentIndex > 0;
                        }

                        @Override
                        public SupportedCity previous() {
                            if (currentIndex > 0) {
                                return cities[(currentIndex--) + start];
                            }
                            throw new NoSuchElementException();
                        }

                        @Override
                        public int nextIndex() {
                            return (currentIndex < len) ? currentIndex + 1 : currentIndex;
                        }

                        @Override
                        public int previousIndex() {
                            return (currentIndex > 0) ? currentIndex - 1 : -1;
                        }

                        @Override
                        public void remove() {
                            throw new UnsupportedOperationException();
                        }

                        @Override
                        public void set(SupportedCity e) {
                            throw new UnsupportedOperationException();
                        }

                        @Override
                        public void add(SupportedCity e) {
                            throw new UnsupportedOperationException();
                        }

                    };
                }

                @Override
                public List<SupportedCity> subList(int fromIndex, int toIndex) {
                    if (toIndex > end) {
                        throw new IndexOutOfBoundsException();
                    }
                    return SupportedCountry.this.subList(start + fromIndex, start + toIndex);
                }
            };
        }
    }
}
