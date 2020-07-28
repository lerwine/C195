package scheduler;

import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import scheduler.util.AnnotationHelper;
import scheduler.util.ResourceBundleHelper;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;

// PENDING: (TODO) Test alert for scheduling overlapping appointments
/**
 * Gets data from resources intended to be used throughout the application.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/App")
public final class AppResources {

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
    public static final String PROPERTYKEY_APPOINTMENTCHECKFREQUENCY = "appointmentCheckFrequency";
    public static final String PROPERTIES_FILE_APPCONFIG = "scheduler/config.properties";

    private static final Locale ORIGINAL_LOCALE;
    private static SupportedLocale currentLocale;

    static {
        ORIGINAL_LOCALE = Locale.getDefault();
        String s = ORIGINAL_LOCALE.toLanguageTag();
        Optional<SupportedLocale> locale = Arrays.stream(SupportedLocale.values()).filter((l) -> l.toString().equals(s)).findFirst();
        if (locale.isPresent()) {
            currentLocale = locale.get();
        } else {
            Locale.setDefault((currentLocale = SupportedLocale.values()[0]).getLocale());
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
     * Get the value of currentLocale
     *
     * @return the value of currentLocale
     */
    public static SupportedLocale getCurrentLocale() {
        return currentLocale;
    }

    public static void setCurrentLocale(SupportedLocale newLocale) {
        if (Scheduler.getCurrentUser() != null) {
            throw new IllegalStateException("Cannot change locale after user is loggged in");
        }
        if (currentLocale != Objects.requireNonNull(newLocale)) {
            currentLocale = newLocale;
            Locale.setDefault(currentLocale.getLocale());
        }
    }

    /**
     * Gets the name of the FXML resource associated with the specified controller {@link java.lang.Class}. This value is specified using the {@link FXMLResource} annotation.
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

    /**
     * Gets the start of business hours. This is parsed from the {@link PROPERTYKEY_BUSINESSHOURSSTART "businessHoursStart"} setting in the
     * {@link PROPERTIES_FILE_APPCONFIG scheduler/config.properties} file.
     *
     * @return The start of business hours.
     * @throws ParseException if unable to parse the time string for the {@link PROPERTYKEY_BUSINESSHOURSSTART "businessHoursStart"} setting in the
     * {@link PROPERTIES_FILE_APPCONFIG scheduler/config.properties} file.
     */
    public static final LocalTime getBusinessHoursStart() throws ParseException {
        String s = APPCONFIG_PROPERTIES.getProperty(PROPERTYKEY_BUSINESSHOURSSTART, "");
        try {
            return LocalTime.parse(s);
        } catch (DateTimeParseException ex) {
            throw new ParseException(ex.getMessage(), ex.getErrorIndex());
        }
    }

    /**
     * Gets the length of the business day in minutes. This is parsed from the {@link PROPERTYKEY_BUSINESSHOURSDURATION "businessHoursDuration"} setting in the
     * {@link PROPERTIES_FILE_APPCONFIG scheduler/config.properties} file.
     *
     * @return The length of the business day in minutes.
     * @throws ParseException if unable to parse the integer string for the {@link PROPERTYKEY_BUSINESSHOURSDURATION "businessHoursDuration"} setting in the
     * {@link PROPERTIES_FILE_APPCONFIG scheduler/config.properties} file.
     */
    public static final int getBusinessHoursDuration() throws ParseException {
        String s = APPCONFIG_PROPERTIES.getProperty(PROPERTYKEY_BUSINESSHOURSDURATION, "");
        return NumberFormat.getIntegerInstance().parse(s).intValue();
    }

    /**
     * Gets the lead time for impending appointment alerts in minutes. This is parsed from the {@link PROPERTYKEY_APPOINTMENTALERTLEADTIME "appointmentAlertLeadTime"} setting in
     * the {@link PROPERTIES_FILE_APPCONFIG scheduler/config.properties} file.
     *
     * @return The lead time for impending appointment alerts in minutes.
     * @throws ParseException if unable to parse the integer string for the {@link PROPERTYKEY_APPOINTMENTALERTLEADTIME "appointmentAlertLeadTime"} setting in the
     * {@link PROPERTIES_FILE_APPCONFIG scheduler/config.properties} file.
     */
    public static final int getAppointmentAlertLeadTime() throws ParseException {
        String s = APPCONFIG_PROPERTIES.getProperty(PROPERTYKEY_APPOINTMENTALERTLEADTIME, "");
        return NumberFormat.getIntegerInstance().parse(s).intValue();
    }

    /**
     * Gets the interval, in seconds, to check for impending appointments. This is parsed from the {@link PROPERTYKEY_APPOINTMENTCHECKFREQUENCY "appointmentCheckFrequency"} setting
     * in the {@link PROPERTIES_FILE_APPCONFIG scheduler/config.properties} file.
     *
     * @return The interval, in seconds, to check for impending appointments.
     * @throws ParseException if unable to parse the integer string for the {@link PROPERTYKEY_APPOINTMENTCHECKFREQUENCY "appointmentCheckFrequency"} setting in the
     * {@link PROPERTIES_FILE_APPCONFIG scheduler/config.properties} file.
     */
    public static final int getAppointmentCheckFrequency() throws ParseException {
        String s = APPCONFIG_PROPERTIES.getProperty(PROPERTYKEY_APPOINTMENTCHECKFREQUENCY, "");
        return NumberFormat.getIntegerInstance().parse(s).intValue();
    }

    /**
     * Gets the database server name. This is read from the {@link PROPERTYKEY_DBSERVERNAME "dbServerName"} setting in the
     * {@link PROPERTIES_FILE_APPCONFIG scheduler/config.properties} file.
     *
     * @return The database server name.
     */
    public static final String getDbServerName() {
        return APPCONFIG_PROPERTIES.getProperty(PROPERTYKEY_DBSERVERNAME, "");
    }

    /**
     * Gets the name of the database. This is read from the {@link PROPERTYKEY_DBNAME "dbName"} setting in the {@link PROPERTIES_FILE_APPCONFIG scheduler/config.properties} file.
     *
     * @return The name of the database.
     */
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

    /**
     * Gets the login credential for the database connection. This is read from the {@link PROPERTYKEY_DBLOGIN "dbLogin"} setting in the
     * {@link PROPERTIES_FILE_APPCONFIG scheduler/config.properties} file.
     *
     * @return The login credential for the database connection.
     */
    public static final String getDbLoginName() {
        return APPCONFIG_PROPERTIES.getProperty(PROPERTYKEY_DBLOGIN, "");
    }

    /**
     * Gets the password credential for the database connection. This is read from the {@link PROPERTYKEY_DBPASSWORD "dbPassword"} setting in the
     * {@link PROPERTIES_FILE_APPCONFIG scheduler/config.properties} file.
     *
     * @return The password credential for the database connection.
     */
    public static String getDbLoginPassword() {
        return APPCONFIG_PROPERTIES.getProperty(PROPERTYKEY_DBPASSWORD, "");
    }

    private AppResources() {
    }

}
