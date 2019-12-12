package scheduler;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Leonard T. Erwine
 */
public class AppConfig {
    private static Properties properties;
    
    private static final String PROPERTIES_FILE_APPCONFIG = "appconfig.properties";
    private static final String DEFAULT_SERVER_NAME = "3.227.166.251";
    private static final String DEFAULT_DATABASE_NAME = "U03vHM";
    private static final String DEFAULT_DATABASE_PASSWORD = "53688096290";
    
    public static void refresh() throws IOException {
        properties = new Properties();
        // Loading properties file from the classpath
        InputStream iStream = AppConfig.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE_APPCONFIG);
        if(iStream == null) {
            Logger.getLogger(AppConfig.class.getName()).log(Level.WARNING,
                    App.getAppResourceBundle().getString("fileNotFound"), PROPERTIES_FILE_APPCONFIG);
            throw new InternalException(String.format("File \"%s\" not found.", PROPERTIES_FILE_APPCONFIG));
        }
        try { properties.load(iStream); }
        finally { iStream.close(); }
    }
    
    public static String getDbServerName() { return properties.getProperty("dbServerName", DEFAULT_SERVER_NAME); }
    
    public static String getDatabaseName() { return properties.getProperty("dbName", DEFAULT_DATABASE_NAME); }
    
    public static String getConnectionUrl() {
        return String.format("jdbc:mysql://%s/%s", getDbServerName(), getDatabaseName());
    }
    
    public static String getDbLoginName() { return properties.getProperty("dbLogin", DEFAULT_DATABASE_NAME); }
    
    public static String getDbLoginPassword() { return properties.getProperty("dbPassword", DEFAULT_DATABASE_PASSWORD); }
}
