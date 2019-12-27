package scheduler;

import com.mysql.jdbc.Connection;
import scene.appointment.EditAppointment;
import java.io.IOException;
import java.sql.SQLException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.db.UserRow;

/**
 * Application class for Scheduler
 * @author Leonard T. Erwine
 */
public class App extends Application {
    
    private static App current;
    
    public static App getCurrent() { return current; }
    
    private Stage rootStage;
    
    public Stage getRootStage() { return rootStage; }
    
    private final Logger logger;
    
    public App() {
        logger = Logger.getLogger(App.class.getName());
    }
    
    //<editor-fold defaultstate="collapsed" desc="App Lifecycle Members">
    
    /**
     * The app main entry point.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage stage) throws Exception {
        current = this;
        rootStage = stage;
        // Store the original locale settings so they can be restored when app ends
        originalDisplayLocale = Locale.getDefault(Locale.Category.DISPLAY);
        originalFormatLocale = Locale.getDefault(Locale.Category.FORMAT);
        
        // Ensure app config is freshly loaded.
        AppConfig.refresh();
        
        // Get IDs languages supported by the app
        String[] languageIds = AppConfig.getLanguages();
        
        // Attempt to find a match for the current display language amongst the languages supported by the app.
        final String lt = originalDisplayLocale.toLanguageTag();
        // First look for one that is an exact match with the language tag.
        Optional<String> cl = Arrays.stream(languageIds).filter((String id) -> id.equals(lt)).findFirst();
        if (!cl.isPresent()) {
            // Look for one that matches the ISO3 language code.
            final String iso3 = originalDisplayLocale.getISO3Language();
            cl = Arrays.stream(languageIds).filter((String id) -> id.equals(iso3)).findFirst();
            if (!cl.isPresent()) {
                // Look for one that matches the ISO2 language code.
                final String ln = originalDisplayLocale.getLanguage();
                cl = Arrays.stream(languageIds).filter((String id) -> id.equals(ln)).findFirst();
            }
        }
        
        // Populate list of Locale objects.
        ObservableList<Locale> languages = FXCollections.observableArrayList();
        Locale toSelect;
        if (cl.isPresent()) {
            for (String n : AppConfig.getLanguages())
                languages.add((n.equals(cl.get())) ? originalDisplayLocale : new Locale(n));
            toSelect = originalDisplayLocale;
        } else {
            for (String n : AppConfig.getLanguages())
                languages.add(new Locale(n));
            toSelect = languages.get(0);
        }
        
        // Make language Locale list read-only.
        allLanguages = FXCollections.unmodifiableObservableList(languages);
        
        setCurrentLocale(toSelect);
        
        ResourceBundle rb = ResourceBundle.getBundle(scene.login.LoginScene.RESOURCE_NAME, getCurrentLocale());
        // Create new FXML loader with the resource path URL of the new fxml page and the resource bundle for the current language.
        FXMLLoader loader = new FXMLLoader(App.class.getResource(scene.login.LoginScene.VIEW_PATH), rb, null);
        
        try {
            stage.setScene(new Scene(loader.load()));
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void stop() throws Exception {
        SqlConnectionDependency.forceClose();
        // Resotre original locale settings
        Locale.setDefault(Locale.Category.DISPLAY, originalDisplayLocale);
        Locale.setDefault(Locale.Category.FORMAT, originalFormatLocale);
        super.stop();
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Globalization Members">
    
    private ObservableList<Locale> allLanguages;
    
    public ObservableList<Locale> getAllLanguages() { return allLanguages; }
    
    // Tracks the original locale settings at the time the app is started, so it can be restored when the app ends
    private Locale originalDisplayLocale;
    private Locale originalFormatLocale;
    
    /**
     * The name of the general application globalization resource bundle.
     */
    public static final String RESOURCE_NAME = "scheduler/App";
    
    private ResourceBundle appResourceBundle;
    
    /**
     * Gets the current "app" resource bundle for the current language.
     * @return The current "app" resource bundle for the current language.
     */
    public ResourceBundle getAppResourceBundle() { return appResourceBundle; }
    
    /**
     * Gets a 'File "%s" not found' formatted message in the current language.
     * @param fileName - The name of the file to place into the message.
     * @return The formatted 'File "%s" not found' message in the current language.
     */
    public String getFileNotFoundMessage(String fileName) {
        return String.format(getAppResourceBundle().getString("fileNotFound"), fileName);
    }
    
    // This contains the current locale.
    private Locale currentLocale;
    // These next 4 are locale-specific formatters for date and/or time strings.
    private DateTimeFormatter shortDateTimeFormatter;
    private DateTimeFormatter fullDateTimeFormatter;
    private DateTimeFormatter fullDateFormatter;
    private DateTimeFormatter fullTimeFormatter;

    /**
     * Gets the current {@link Locale}.
     * @return The current {@link Locale}.
     */
    public Locale getCurrentLocale() { return currentLocale; }
    
    private HashMap<String, String> appointmentTypes;
    
    /**
     * Gets the human-readable, locale-specific display text for the specified appointment type code.
     * @param code The appointment type code that is stored in the database.
     * @return The current locale-specific formatter for full time strings.
     */
    public String getAppointmentTypeDisplay(String code) {
        if (code == null || (code = code.trim()).isEmpty())
            return "";
        String lc = code.toLowerCase();
        return (appointmentTypes.containsKey(lc)) ? appointmentTypes.get(lc) : code;
    }

    /**
     * Sets the current {@link Locale} and initializes app information that contains locale-specific information.
     * @param locale The new app {@link Locale}.
     */
    public void setCurrentLocale(Locale locale) {
        // Set default locale so all controls will be displayed appropriate for the current locale.
        Locale.setDefault(Locale.Category.DISPLAY, locale);
        Locale.setDefault(Locale.Category.FORMAT, locale);
        // Save the new locale.
        currentLocale = locale;
        appResourceBundle = ResourceBundle.getBundle(RESOURCE_NAME, locale);
        fullTimeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.FULL).withLocale(locale).withZone(ZoneId.systemDefault());
        fullDateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(locale).withZone(ZoneId.systemDefault());
        shortDateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withLocale(locale).withZone(ZoneId.systemDefault());
        fullDateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL).withLocale(locale).withZone(ZoneId.systemDefault());
        appointmentTypes = new HashMap<>();
        Stream.of(EditAppointment.APPOINTMENT_CODE_PHONE, EditAppointment.APPOINTMENT_CODE_VIRTUAL, EditAppointment.APPOINTMENT_CODE_CUSTOMER,
                EditAppointment.APPOINTMENT_CODE_HOME, EditAppointment.APPOINTMENT_CODE_GERMANY, EditAppointment.APPOINTMENT_CODE_INDIA,
                EditAppointment.APPOINTMENT_CODE_HONDURAS, EditAppointment.APPOINTMENT_CODE_OTHER).forEach((String key) -> {
            appointmentTypes.put(key, appResourceBundle.getString("appointmentType_" + key));
        });
    }
    
    /**
     * Gets the current locale-specific formatter for full time strings.
     * @return  The current locale-specific formatter for full time strings.
     */
    public DateTimeFormatter getFullTimeFormatter() { return fullTimeFormatter; }
    
    /**
     * Gets the current locale-specific formatter for full date strings.
     * @return The current locale-specific formatter for full date strings.
     */
    public DateTimeFormatter getFullDateFormatter() { return fullDateFormatter; }
    
    /**
     * Gets the current locale-specific formatter for short date/time strings.
     * @return The current locale-specific formatter for short date/time strings.
     */
    public DateTimeFormatter getShortDateTimeFormatter() { return shortDateTimeFormatter; }
    
    /**
     * Gets the current locale-specific formatter for full date/time strings.
     * @return The current locale-specific formatter for full date/time strings.
     */
    public DateTimeFormatter getFullDateTimeFormatter() { return fullDateTimeFormatter; }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Members for tracking the current User">
    
    /**
     * Stores the currently logged in user.
     */
    private Optional<UserRow> currentUser = Optional.empty();
    
    /**
     * Gets the currently logged in user.
     * @return The currently logged in user.
     */
    public Optional<UserRow> getCurrentUser() { return currentUser; }
    
    /**
     * Sets the currently logged in user if a user name and password match.
     * @param userName The user's login name.
     * @param password The user's actual password (not password hash).
     * @return {@code true} if a matching {@link UserRow#userName} is found, and the password hash matches the stored value; otherwise {@code false}.
     * @throws InvalidOperationException
     * @throws java.sql.SQLException
     */
    public boolean tryLoginUser(String userName, String password) throws InvalidOperationException, SQLException {
        Optional<UserRow> user = SqlConnectionDependency.get((Connection connection) -> {
            try {
                return UserRow.getByUserName(connection, userName);
            } catch (SQLException ex) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
                throw new RuntimeException("Error getting user by username", ex);
            }
        });
        if (user.isPresent()) {
            // The password string stored in the database is a base-64 string that contains a cryptographic hash of the password
            // along with the cryptographic seed. A hash will be created from password argument using the same cryptographic seed
            // as the stored password. If the password is correct, then the hash values will match.
            if (user.get().getPasswordHash().test(password)) {
                currentUser = user;
                return true;
            }
        } else
            logger.log(Level.WARNING, "No matching userName found");
        return false;
    }
    
    //</editor-fold>
}
