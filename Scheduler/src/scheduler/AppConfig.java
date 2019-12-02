/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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
            Messages.current().notifyPropertyLoadError(PROPERTIES_FILE_APPCONFIG);
            throw new InternalException(String.format("File \"{0}\" not found.", PROPERTIES_FILE_APPCONFIG));
        }
        try { properties.load(iStream); }
        finally { iStream.close(); }
    }
    
    public static String getDbServerName() { return properties.getProperty("dbServerName", DEFAULT_SERVER_NAME); }
    
    public static String getDatabaseName() { return properties.getProperty("dbName", DEFAULT_DATABASE_NAME); }
    
    public static String getConnectionUrl() {
        return String.format("jdbc:mysql://{0}/{1}", getDbServerName(), getDatabaseName());
    }
    
    public static String getDbLoginName() { return properties.getProperty("dbLogin", DEFAULT_DATABASE_NAME); }
    
    public static String getDbLoginPassword() { return properties.getProperty("dbPassword", DEFAULT_DATABASE_PASSWORD); }
}
