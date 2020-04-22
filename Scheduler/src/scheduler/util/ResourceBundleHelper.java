package scheduler.util;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import static scheduler.AppResourceKeys.RESOURCEKEY_CREATEDBYON;
import static scheduler.AppResourceKeys.RESOURCEKEY_MODIFIEDBYON;
import scheduler.AppResources;

/**
 * Caches loaded resource bundles, reloading them if the default Display Locale has changed.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public final class ResourceBundleHelper {

    private static final ResourceBundleHelper INSTANCE;
    private static final Logger LOG;

    static {
        LOG = Logger.getLogger(ResourceBundleHelper.class.getName());
        INSTANCE = new ResourceBundleHelper();
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
        ResourceBundle result = INSTANCE.getBundle(resourceClass, Locale.getDefault(), getGlobalizationResourceName(resourceClass));
        if (null == result) {
            return empty();
        }
        return result;
    }

    public static final ResourceBundle getBundle(Class<?> resourceClass, Locale locale) {
        ResourceBundle result = INSTANCE.getBundle(resourceClass, (null == locale) ? Locale.getDefault() : locale,
                getGlobalizationResourceName(resourceClass));
        if (null == result) {
            return empty();
        }
        return result;
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

    public static String formatCreatedByOn(String createdBy, LocalDateTime createDate) {
        return formatResourceString(AppResources.class, RESOURCEKEY_CREATEDBYON, createdBy, Objects.requireNonNull(createDate));
    }

    public static String formatModifiedByOn(String modifiedBy, LocalDateTime lastModifiedDate) {
        return formatResourceString(AppResources.class, RESOURCEKEY_MODIFIEDBYON, modifiedBy, Objects.requireNonNull(lastModifiedDate));
    }

    private final HashMap<String, String> classNameToResourceName;
    private final HashMap<String, ResourceBundle> resourceBundleByClassName;

    private ResourceBundleHelper() {
        classNameToResourceName = new HashMap<>();
        resourceBundleByClassName = new HashMap<>();
    }

    private synchronized ResourceBundle getBundle(Class<?> resourceClass, Locale locale, String baseName) {
        if (null == resourceClass || baseName.isEmpty()) {
            return null;
        }

        ResourceBundle result;
        if (locale.toLanguageTag().equals(Locale.getDefault().toLanguageTag())) {
            HashMap<String, ResourceBundle> map = resourceBundleByClassName;
            if (map.containsKey(baseName)) {
                return map.get(baseName);
            }
            try {
                result = ResourceBundle.getBundle(baseName, locale, resourceClass.getClassLoader());
            } catch (MissingResourceException ex) {
                LOG.log(Level.SEVERE, "Error loading resource bundle", ex);
                map.put(baseName, null);
                return null;
            }
            map.put(baseName, result);
        } else {
            try {
                result = ResourceBundle.getBundle(baseName, locale, resourceClass.getClassLoader());
            } catch (MissingResourceException ex) {
                LOG.log(Level.SEVERE, "Error loading resource bundle", ex);
                return null;
            }
        }
        return result;
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

}
