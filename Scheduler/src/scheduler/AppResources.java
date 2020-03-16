package scheduler;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import scheduler.util.ResourceBundleLoader;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;

/**
 * Gets settings from the "appconfig.properties" file.
 *
 * @author Leonard T. Erwine
 */
@GlobalizationResource("scheduler/App")
public final class AppResources implements AppResourceBundleConstants {

    private static final Logger LOG = Logger.getLogger(AppResources.class.getName());
    private static final Properties APPCONFIG_PROPERTIES;
    private static final HashMap<String, String> CLASSNAME_TO_FXMLNAME;
    public static final String PROPERTYKEY_DBSERVERNAME = "dbServerName";
    public static final String PROPERTYKEY_DBNAME = "dbName";
    public static final String PROPERTYKEY_DBLOGIN = "dbLogin";
    public static final String PROPERTYKEY_DBPASSWORD = "dbPassword";
    public static final String PROPERTYKEY_SUPPORTEDLOCALES = "supportedLocales";
    public static final String PROPERTYKEY_ALTSTRINGPLACEHOLDERORDER = "altStringPlaceholderOrder";
    public static final String PROPERTIES_FILE_APPCONFIG = "scheduler/config.properties";

    static {
        CLASSNAME_TO_FXMLNAME = new HashMap<>();
        APPCONFIG_PROPERTIES = new Properties();
        ClassLoader classLoader = AppResources.class.getClassLoader();
        try (InputStream iStream = classLoader.getResourceAsStream(PROPERTIES_FILE_APPCONFIG)) {
            if (iStream == null) {
                LOG.log(Level.SEVERE, String.format("File \"%s\" not found.", PROPERTIES_FILE_APPCONFIG));
            } else {
                APPCONFIG_PROPERTIES.load(iStream);
            }
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, String.format("Error loading resource \"%s\"", PROPERTIES_FILE_APPCONFIG), ex);
        }
    }

    /**
     * Gets the name of the FXML resource associated with the specified controller {@link java.lang.Class}. This value is specified using the
     * {@link FXMLResource} annotation.
     *
     * @param ctlClass The {@link java.lang.Class} for the target controller.
     * @return The name of the FXML resource associated with the target controller or null if resource name is not specified.
     */
    public static final String getFXMLResourceName(Class<?> ctlClass) {
        synchronized (CLASSNAME_TO_FXMLNAME) {
            String c = ctlClass.getName();
            if (CLASSNAME_TO_FXMLNAME.containsKey(c)) {
                return CLASSNAME_TO_FXMLNAME.get(c);
            }
            Class<FXMLResource> ac = FXMLResource.class;
            String message;
            if (ctlClass.isAnnotationPresent(ac)) {
                String n = ctlClass.getAnnotation(ac).value();
                if (n != null && !n.trim().isEmpty()) {
                    CLASSNAME_TO_FXMLNAME.put(c, n);
                    return n;
                }
                message = String.format("Value not defined for annotation scene.annotations.FXMLResourceName in type %s",
                        ctlClass.getName());
            } else {
                message = String.format("Annotation scene.annotations.FXMLResourceName not present in type %s", ctlClass.getName());
            }
            LOG.logp(Level.SEVERE, ResourceBundleLoader.class.getName(), "getFXMLResourceName", message);
            CLASSNAME_TO_FXMLNAME.put(c, "");
            return "";
        }
    }

    /**
     * Gets the application {@link ResourceBundle}.
     *
     * @return The application {@link ResourceBundle} for the current {@link Locale#defaultDisplayLocale}.
     */
    public static ResourceBundle getResources() {
        return ResourceBundleLoader.getBundle(AppResources.class);
    }

    public static String getResourceString(String key) {
        return ResourceBundleLoader.getResourceString(AppResources.class, key);
    }

    public static Set<String> getPropertyNames() {
        return APPCONFIG_PROPERTIES.stringPropertyNames();
    }

    public static String getProperty(String key, String defaultValue) {
        return APPCONFIG_PROPERTIES.getProperty(key, defaultValue);
    }

    public static String getProperty(String key) {
        return APPCONFIG_PROPERTIES.getProperty(key);
    }

    public static final String getDbServerName() {
        return APPCONFIG_PROPERTIES.getProperty(PROPERTYKEY_DBSERVERNAME, "");
    }

    public static final String getDatabaseName() {
        return APPCONFIG_PROPERTIES.getProperty(PROPERTYKEY_DBNAME, "");
    }

    public static final String getConnectionUrl() {
        String n = getDbServerName();
        if (!n.isEmpty()) {
            String d = getDatabaseName();
            if (!d.isEmpty()) {
                return String.format("jdbc:mysql://%s/%s", getDbServerName(), getDatabaseName());
            }
            LOG.logp(Level.SEVERE, AppResources.class.getName(), "", String.format("Database name not provided in %s", PROPERTIES_FILE_APPCONFIG));
        } else {
            LOG.logp(Level.SEVERE, AppResources.class.getName(), "", String.format("Database server name not provided in %s", PROPERTIES_FILE_APPCONFIG));
        }
        return "";
    }

    public static final String getDbLoginName() {
        return APPCONFIG_PROPERTIES.getProperty(PROPERTYKEY_DBLOGIN, "");
    }

    public static String getDbLoginPassword() {
        return APPCONFIG_PROPERTIES.getProperty(PROPERTYKEY_DBPASSWORD, "");
    }

    private AppResources() {
    }

}
