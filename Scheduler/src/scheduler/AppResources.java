package scheduler;

import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.util.HashMap;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import scheduler.util.AnnotationHelper;
import scheduler.util.ResourceBundleHelper;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.city.SupportedLocale;

// CURRENT: Define business hours - Alert for scheduling an appointment outside business hours
// TODO: Test alert for scheduling overlapping appointments
// CURRENT: Report for number of appointment types by month
// CURRENT: Report for the schedule for each consultant
// CURRENT: Create additional report
// CURRENT: recording timestamps for user log-ins in a .txt file

/**
 * Gets settings from the {@code /resources/scheduler/config.properties} file.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/App")
public final class AppResources implements AppResourceKeys {
    public static final String FXMLPROPERTYNAME_CONTROLLER = "scheduler.Controller";

    private static final Logger LOG = Logger.getLogger(AppResources.class.getName());
    private static final Properties APPCONFIG_PROPERTIES;
    private static final HashMap<String, String> CLASSNAME_TO_FXMLNAME;
    public static final String PROPERTYKEY_BUSINESSHOURSSTART = "businessHoursStart";
    public static final String PROPERTYKEY_BUSINESSHOURSDURATION = "businessHoursDuration";
    public static final String PROPERTYKEY_DBSERVERNAME = "dbServerName";
    public static final String PROPERTYKEY_DBNAME = "dbName";
    public static final String PROPERTYKEY_DBLOGIN = "dbLogin";
    public static final String PROPERTYKEY_DBPASSWORD = "dbPassword";
    public static final String PROPERTYKEY_APPOINTMENTALERTLEADTIME = "appointmentAlertLeadTime";
    public static final String PROPERTIES_FILE_APPCONFIG = "scheduler/config.properties";
    
    private static final Locale ORIGINAL_LOCALE;
    private static final Locale DEFAULT_LOCALE;
    private static SupportedLocale currentLocale;

    static {
        ORIGINAL_LOCALE = Locale.getDefault();
        SupportedLocale locale = SupportedLocale.fromLocale(ORIGINAL_LOCALE);
        DEFAULT_LOCALE = (ORIGINAL_LOCALE.getLanguage().equals(locale.getLanguageCode())) ? ORIGINAL_LOCALE : locale.toLocale();
        currentLocale = locale;
    }
    
    /**
     * Get the value of currentLocale
     *
     * @return the value of currentLocale
     */
    public static SupportedLocale getCurrentLocale() {
        return currentLocale;
    }
    
    public static void setCurrentLocale(SupportedLocale currentLocale) {
        if (Scheduler.getCurrentUser() != null)
            throw new IllegalStateException("Cannot change locale after user is loggged in");
        if (AppResources.currentLocale != currentLocale) {
            AppResources.currentLocale = currentLocale;
            if (SupportedLocale.fromLocale(DEFAULT_LOCALE) == AppResources.currentLocale)
                Locale.setDefault(DEFAULT_LOCALE);
            else
                Locale.setDefault(AppResources.currentLocale.toLocale());
        }
    }

    static {
        CLASSNAME_TO_FXMLNAME = new HashMap<>();
        APPCONFIG_PROPERTIES = new Properties();
        ClassLoader classLoader = AppResources.class.getClassLoader();
        try (InputStream iStream = classLoader.getResourceAsStream(PROPERTIES_FILE_APPCONFIG)) {
            if (iStream == null) {
                LOG.log(Level.SEVERE, String.format("Resource \"%s\" not found.", PROPERTIES_FILE_APPCONFIG));
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
     * @return The name of the FXML resource associated with the target controller or an empty string if resource name is not specified.
     */
    public static final String getFXMLResourceName(Class<?> ctlClass) {
        synchronized (CLASSNAME_TO_FXMLNAME) {
            String c = ctlClass.getName();
            if (CLASSNAME_TO_FXMLNAME.containsKey(c)) {
                return CLASSNAME_TO_FXMLNAME.get(c);
            }
            String n = AnnotationHelper.getFXMLResourceName(ctlClass);
            CLASSNAME_TO_FXMLNAME.put(c, n);
            return n;
        }
    }

    /**
     * Gets the application {@link ResourceBundle}.
     *
     * @return The application {@link ResourceBundle} for the current {@link Locale#defaultDisplayLocale}.
     */
    public static ResourceBundle getResources() {
        return ResourceBundleHelper.getBundle(AppResources.class);
    }

    public static String getResourceString(String key) {
        return ResourceBundleHelper.getResourceString(AppResources.class, key);
    }

    public static final LocalTime getBusinessHoursStart() throws ParseException {
        String s = APPCONFIG_PROPERTIES.getProperty(PROPERTYKEY_BUSINESSHOURSSTART, "");
        try {
            return (LocalTime)DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).parse(s);
        } catch (DateTimeParseException ex) {
            throw new ParseException(ex.getMessage(), ex.getErrorIndex());
        }
    }

    public static final int getBusinessHoursDuration() throws ParseException {
        String s = APPCONFIG_PROPERTIES.getProperty(PROPERTYKEY_BUSINESSHOURSDURATION, "");
        return NumberFormat.getIntegerInstance().parse(s).intValue();
    }

    public static final int getAppointmentAlertLeadTime() throws ParseException {
        String s = APPCONFIG_PROPERTIES.getProperty(PROPERTYKEY_APPOINTMENTALERTLEADTIME, "");
        return NumberFormat.getIntegerInstance().parse(s).intValue();
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
            LOG.log(Level.SEVERE, String.format("Database name not provided in %s", PROPERTIES_FILE_APPCONFIG));
        } else {
            LOG.log(Level.SEVERE, String.format("Database server name not provided in %s", PROPERTIES_FILE_APPCONFIG));
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
