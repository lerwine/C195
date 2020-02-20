package scheduler;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Gets settings from the "appconfig.properties" file.
 *
 * @author Leonard T. Erwine
 */
public class AppConfig {
    //<editor-fold defaultstate="collapsed" desc="Property keys">

    public static final String PROPERTYKEY_DBSERVERNAME = "dbServerName";
    public static final String PROPERTYKEY_DBNAME = "dbName";
    public static final String PROPERTYKEY_DBLOGIN = "dbLogin";
    public static final String PROPERTYKEY_DBPASSWORD = "dbPassword";
    public static final String PROPERTYKEY_LANGUAGES = "languages";

    //</editor-fold>
    private static Properties properties;
    private static final Pattern PATTERN_WHITESPACE = Pattern.compile("[\\r\\n\\s]+", 0);
    private static final String PROPERTIES_FILE_APPCONFIG = "appconfig.properties";
    private static final String DEFAULT_SERVER_NAME = "3.227.166.251";
    private static final String DEFAULT_DATABASE_NAME = "U03vHM";
    private static final String DEFAULT_DATABASE_PASSWORD = "53688096290";

    public static void refresh() throws IOException {
        properties = new Properties();
        // Loading properties file from the classpath
        InputStream iStream = AppConfig.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE_APPCONFIG);
        if (iStream == null) {
            Logger.getLogger(AppConfig.class.getName()).log(Level.WARNING,
                    String.format("%s not found", PROPERTIES_FILE_APPCONFIG));
            throw new InternalException(String.format("File \"%s\" not found.", PROPERTIES_FILE_APPCONFIG));
        }
        try {
            properties.load(iStream);
        } finally {
            iStream.close();
        }
    }

    public static String getDbServerName() {
        return properties.getProperty(PROPERTYKEY_DBSERVERNAME, DEFAULT_SERVER_NAME);
    }

    public static String getDatabaseName() {
        return properties.getProperty(PROPERTYKEY_DBNAME, DEFAULT_DATABASE_NAME);
    }

    public static String getConnectionUrl() {
        return String.format("jdbc:mysql://%s/%s", getDbServerName(), getDatabaseName());
    }

    public static String getDbLoginName() {
        return properties.getProperty(PROPERTYKEY_DBLOGIN, DEFAULT_DATABASE_NAME);
    }

    public static String getDbLoginPassword() {
        return properties.getProperty(PROPERTYKEY_DBPASSWORD, DEFAULT_DATABASE_PASSWORD);
    }

    public static String[] getLanguages() {
        String s = properties.getProperty(PROPERTYKEY_LANGUAGES);
        if (s == null || (s = s.trim()).isEmpty()) {
            return new String[0];
        }
        return PATTERN_WHITESPACE.split(s);
    }
}
