package scheduler.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import scheduler.App;
import scheduler.view.annotations.FXMLResource;

/**
 *
 * @author lerwi
 */
public class ResourceBundleLoader {

    private static final Logger LOG = Logger.getLogger(ResourceBundleLoader.class.getName());
    private static final HashMap<String, String> BUNDLE_NAME_MAP;
    private static final HashMap<String, ResourceBundle> BUNDLE_CACHE = new HashMap<>();
    private static final HashMap<CacheKey, ResourceBundle> MERGED_BUNDLE_CACHE = new HashMap<>();
    private static Locale cachedLocale = null;

    static {
        BUNDLE_NAME_MAP = new HashMap<>();
        BUNDLE_NAME_MAP.put(App.class.getName(), App.GLOBALIZATION_RESOURCE_NAME);
    }

    public static String getResourceString(Class<?> ctlClass, String key) {
        return getBundle(ctlClass).getString(key);
    }
    
    /**
     * Gets the name of the FXML resource associated with the specified controller {@link java.lang.Class}. This value is specified using the
     * {@link FXMLResource} annotation.
     *
     * @param <C> The type of controller.
     * @param ctlClass The {@link java.lang.Class} for the target controller.
     * @return The name of the FXML resource associated with the target controller or null if resource name is not specified.
     */
    public static final String getFXMLResourceName(Class<?> ctlClass) {
        synchronized (BUNDLE_NAME_MAP) {
            String c = ctlClass.getName();
            if (BUNDLE_NAME_MAP.containsKey(c)) {
                return BUNDLE_NAME_MAP.get(c);
            }
            Class<FXMLResource> ac = FXMLResource.class;
            String message;
            if (ctlClass.isAnnotationPresent(ac)) {
                String n = ctlClass.getAnnotation(ac).value();
                if (n != null && !n.trim().isEmpty()) {
                    BUNDLE_NAME_MAP.put(c, n);
                    return n;
                }
                message = String.format("Value not defined for annotation scene.annotations.FXMLResourceName in type %s",
                        ctlClass.getName());
            } else {
                message = String.format("Annotation scene.annotations.FXMLResourceName not present in type %s", ctlClass.getName());
            }
            LOG.logp(Level.SEVERE, ResourceBundleLoader.class.getName(), "getFXMLResourceName", message);
            BUNDLE_NAME_MAP.put(c, "");
            return "";
        }
    }

    private static void checkLocale() {
        Locale locale = Locale.getDefault(Locale.Category.DISPLAY);
        if (null == cachedLocale) {
            cachedLocale = locale;
        } else if (!locale.toLanguageTag().equals(locale.toLanguageTag())) {
            cachedLocale = locale;
            BUNDLE_CACHE.clear();
            MERGED_BUNDLE_CACHE.clear();
        }
    }

    public static final ResourceBundle getAppBundle() {
        synchronized (BUNDLE_CACHE) {
            checkLocale();
            return getBundle(App.class, App.GLOBALIZATION_RESOURCE_NAME);
        }
    }

    private static ResourceBundle getBundle(Class<?> resourceClass, String baseName) {
        if (null == resourceClass || baseName.isEmpty()) {
            return null;
        }
        if (BUNDLE_CACHE.containsKey(baseName)) {
            return BUNDLE_CACHE.get(baseName);
        }
        ResourceBundle result;
        try {
            result = ResourceBundle.getBundle(baseName, cachedLocale, resourceClass.getClassLoader());
        } catch (MissingResourceException ex) {
            LOG.logp(Level.SEVERE, ResourceBundleLoader.class.getName(), "getBundle", "Error loading resource bundle", ex);
            BUNDLE_CACHE.put(baseName, null);
            return null;
        }
        BUNDLE_CACHE.put(baseName, result);
        return result;
    }

    public static final ResourceBundle getBundle(Class<?> resourceClass) {
        ResourceBundle result;
        synchronized (BUNDLE_CACHE) {
            checkLocale();
            result = getBundle(resourceClass, getFXMLResourceName(resourceClass));
            if (null == result) {
                result = getBundle(App.class, App.GLOBALIZATION_RESOURCE_NAME);
            }
        }
        return result;
    }

    public static final ResourceBundle getMergedBundle(Class<?> target, Class<?> parent, Class<?>... ancestors) {
        ResourceBundle result;

        synchronized (BUNDLE_CACHE) {
            checkLocale();
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
            if (MERGED_BUNDLE_CACHE.containsKey(key)) {
                result = MERGED_BUNDLE_CACHE.get(key);
            } else {
                result = getBundle(targetAndAncestors[l], key.names[l]);
                while (--l >= 0) {
                    ResourceBundle r = getBundle(targetAndAncestors[l], key.names[l]);
                    if (null != r) {
                        result = (null == result) ? r : new MergedResourceBundle(r, result);
                    }
                }
                MERGED_BUNDLE_CACHE.put(key, result);
            }
            if (null == result) {
                result = getBundle(App.class, App.GLOBALIZATION_RESOURCE_NAME);
            }
        }

        return result;
    }

    private ResourceBundleLoader() {
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
                names[n] = getFXMLResourceName(c);
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
