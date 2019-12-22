package scheduler;

import com.mysql.jdbc.Connection;
import controller.EditAppointmentController;
import controller.LoginScreenController;
import java.io.IOException;
import java.sql.SQLException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.db.UserRow;

/**
 * Application class for Scheduler
 * @author Leonard T. Erwine
 */
public class App extends Application {
    //<editor-fold defaultstate="collapsed" desc="Overloaded changeScene methods">

    /**
     * Sets the {@link javafx.scene.Scene} for the {@link javafx.stage.Stage} of the specified {@link javafx.scene.Node}
     * from the FXML resource at a given path and sets up its controller.
     * @param <T>
     * @param eventSource The {@link javafx.scene.Node} from the event source that initiated the scene change.
     * @param path The path of the FXML resource to load.
     * @param bundleName The name of the resource bundle to load.
     * @param setupController The delegate method that sets up the controller.
     */
    
    public static <T> void changeScene(Node eventSource, String path, String bundleName, StageBundleAndControllerCallback<T> setupController) {
        App.setScene((Stage)eventSource.getScene().getWindow(), path, bundleName, setupController);
    }
    
    /**
     * Sets the {@link javafx.scene.Scene} for the {@link javafx.stage.Stage} of the specified {@link javafx.scene.Node}
     * from the FXML resource at a given path and sets up its controller.
     * @param <T>
     * @param eventSource The {@link javafx.scene.Node} from the event source that initiated the scene change.
     * @param path The path of the FXML resource to load.
     * @param bundleName The name of the resource bundle to load.
     * @param setupController The delegate method that sets up the controller.
     */
    public static <T> void changeScene(Node eventSource, String path, String bundleName, java.util.function.BiConsumer<ResourceBundle, T> setupController) {
        App.setScene((Stage)eventSource.getScene().getWindow(), path, bundleName, setupController);
    }
    
    /**
     * Sets the {@link javafx.scene.Scene} for the {@link javafx.stage.Stage} of the specified {@link javafx.scene.Node}
     * from the FXML resource at a given path.
     * @param eventSource The {@link javafx.scene.Node} from the event source that initiated the scene change.
     * @param path The path of the FXML resource to load.
     * @param bundleName The name of the resource bundle to load.
     */
    public static void changeScene(Node eventSource, String path, String bundleName) {
        setScene((Stage)eventSource.getScene().getWindow(), path, bundleName);
    }
    
    /**
     * Sets the {@link javafx.scene.Scene} for the specified {@link javafx.stage.Stage} from the FXML resource at a given path
     * and sets up its controller.
     * @param <T> The type of controller to set up.
     * @param stage The target {@link javafx.stage.Stage}.
     * @param path The path of the FXML resource to load.
     * @param bundleName The name of the resource bundle to load.
     * @param setupController The delegate method that sets up the controller.
     */
    public static <T> void setScene(Stage stage, String path, String bundleName, StageBundleAndControllerCallback<T> setupController) {
        ResourceBundle rb = (bundleName == null || bundleName.trim().isEmpty()) ? App.getAppResourceBundle() :
            ResourceBundle.getBundle(bundleName, scheduler.App.getCurrentLocale());
        // Create new FXML loader with the resource path URL of the new fxml page and the resource bundle for the current language.
        FXMLLoader loader = new FXMLLoader(App.class.getResource(path), rb);
        
        try {
            stage.setScene(new Scene(loader.load()));
        } catch (IOException ex) {
            getLogger().log(Level.SEVERE, null, ex);
            return;
        }
        
        // Call the consumer to for controller setup.
        setupController.accept(stage, rb, loader.getController());
        
        stage.show();
    }
    
    /**
     * Sets the {@link javafx.scene.Scene} for the specified {@link javafx.stage.Stage} from the FXML resource at a given path
     * and sets up its controller.
     * @param <T> The type of controller to set up.
     * @param stage The target {@link javafx.stage.Stage}.
     * @param path The path of the FXML resource to load.
     * @param bundleName The name of the resource bundle to load.
     * @param setupController The delegate method that sets up the controller.
     */
    public static <T> void setScene(Stage stage, String path, String bundleName, java.util.function.BiConsumer<ResourceBundle, T> setupController) {
        ResourceBundle rb = (bundleName == null || bundleName.trim().isEmpty()) ? App.getAppResourceBundle() :
            ResourceBundle.getBundle(bundleName, scheduler.App.getCurrentLocale());
        // Create new FXML loader with the resource path URL of the new fxml page and the resource bundle for the current language.
        FXMLLoader loader = new FXMLLoader(App.class.getResource(path), rb);
        
        try {
            stage.setScene(new Scene(loader.load()));
        } catch (IOException ex) {
            getLogger().log(Level.SEVERE, null, ex);
            return;
        }
        
        // Call the consumer to for controller setup.
        setupController.accept(rb, loader.getController());
        
        stage.show();
    }
    
    /**
     * Sets the {@link javafx.scene.Scene} for the specified {@link javafx.stage.Stage} from the FXML resource at a given path.
     * @param stage The target {@link javafx.stage.Stage}.
     * @param path The path of the FXML resource to load.
     * @param bundleName The name of the resource bundle to load.
     */
    public static void setScene(Stage stage, String path, String bundleName) {
        // Create new FXML loader with the resource path URL of the new fxml page and the resource bundle for the current language.
        FXMLLoader loader = new FXMLLoader(App.class.getResource(path), (bundleName == null || bundleName.trim().isEmpty()) ? App.getAppResourceBundle() :
            ResourceBundle.getBundle(bundleName, scheduler.App.getCurrentLocale()));
        
        try {
            stage.setScene(new Scene(loader.load()));
        } catch (IOException ex) {
            getLogger().log(Level.SEVERE, null, ex);
            return;
        }
        
        stage.show();
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="App Lifecycle Members">
    
    // Tracks the original locale settings at the time the app is started, so it can be restored when the app ends
    private Locale originalDisplayLocale;
    private Locale originalFormatLocale;
    
    private static Logger getLogger() { return Logger.getLogger(App.class.getName()); }
    
    @Override
    public void start(Stage stage) throws Exception {
        // Store the original locale settings so they can be restored when app ends
        currentLocale = originalDisplayLocale = Locale.getDefault(Locale.Category.DISPLAY);
        originalFormatLocale = Locale.getDefault(Locale.Category.FORMAT);
        AppConfig.refresh();
        // Set initial scene to the login screen
        LoginScreenController.setCurrentScene(stage);
    }
    
    @Override
    public void stop() throws Exception {
        SqlConnectionDependency.forceClose();
        // Resotre original locale settings
        Locale.setDefault(Locale.Category.DISPLAY, originalDisplayLocale);
        Locale.setDefault(Locale.Category.FORMAT, originalFormatLocale);
        super.stop();
    }
    
    /**
     * The app main entry point.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Globalization Members">
    
    /**
     * The name of the general application globalization resource bundle.
     */
    public static final String RESOURCE_NAME = "globalization/app";
    private static ResourceBundle appResourceBundle;
    
    /**
     * Gets the current "app" resource bundle for the current language.
     * @return The current "app" resource bundle for the current language.
     */
    public static ResourceBundle getAppResourceBundle() { return appResourceBundle; }
    
    /**
     * Gets a 'File "%s" not found' formatted message in the current language.
     * @param fileName - The name of the file to place into the message.
     * @return The formatted 'File "%s" not found' message in the current language.
     */
    public static String getFileNotFoundMessage(String fileName) {
        return String.format(getAppResourceBundle().getString("fileNotFound"), fileName);
    }
    
    // This contains the current locale.
    private static Locale currentLocale;
    // These next 4 are locale-specific formatters for date and/or time strings.
    private static DateTimeFormatter shortDateTimeFormatter;
    private static DateTimeFormatter fullDateTimeFormatter;
    private static DateTimeFormatter fullDateFormatter;
    private static DateTimeFormatter fullTimeFormatter;

    /**
     * Gets the current {@link Locale}.
     * @return The current {@link Locale}.
     */
    public static Locale getCurrentLocale() { return currentLocale; }
    
    private static HashMap<String, String> appointmentTypes;
    
    /**
     * Gets the human-readable, locale-specific dislay text for the specified appointment type code.
     * @param code The appointment type code that is stored in the database.
     * @return The current locale-specific formatter for full time strings.
     */
    public static String getAppointmentTypeDisplay(String code) {
        if (code == null || (code = code.trim()).isEmpty())
            return "";
        String lc = code.toLowerCase();
        return (appointmentTypes.containsKey(lc)) ? appointmentTypes.get(lc) : code;
    }

    /**
     * Sets the current {@link Locale}.
     * @param locale The new app {@link Locale}.
     */
    public static void setCurrentLocale(Locale locale) {
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
        Stream.of(EditAppointmentController.APPOINTMENT_CODE_PHONE, EditAppointmentController.APPOINTMENT_CODE_VIRTUAL, EditAppointmentController.APPOINTMENT_CODE_CUSTOMER,
                EditAppointmentController.APPOINTMENT_CODE_HOME, EditAppointmentController.APPOINTMENT_CODE_GERMANY, EditAppointmentController.APPOINTMENT_CODE_INDIA,
                EditAppointmentController.APPOINTMENT_CODE_HONDURAS, EditAppointmentController.APPOINTMENT_CODE_OTHER).forEach((String key) -> {
            appointmentTypes.put(key, appResourceBundle.getString("appointmentType_" + key));
        });
    }
    
    /**
     * Gets the current locale-specific formatter for full time strings.
     * @return  The current locale-specific formatter for full time strings.
     */
    public static DateTimeFormatter getFullTimeFormatter() { return fullTimeFormatter; }
    
    /**
     * Gets the current locale-specific formatter for full date strings.
     * @return The current locale-specific formatter for full date strings.
     */
    public static DateTimeFormatter getFullDateFormatter() { return fullDateFormatter; }
    
    /**
     * Gets the current locale-specific formatter for short date/time strings.
     * @return The current locale-specific formatter for short date/time strings.
     */
    public static DateTimeFormatter getShortDateTimeFormatter() { return shortDateTimeFormatter; }
    
    /**
     * Gets the current locale-specific formatter for full date/time strings.
     * @return The current locale-specific formatter for full date/time strings.
     */
    public static DateTimeFormatter getFullDateTimeFormatter() { return fullDateTimeFormatter; }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Members for tracking the current User">
    
    /**
     * Stores the currently logged in user.
     */
    private static Optional<UserRow> currentUser = Optional.empty();
    
    /**
     * Gets the currently logged in user.
     * @return The currently logged in user.
     */
    public static Optional<UserRow> getCurrentUser() { return currentUser; }
    
    /**
     * Sets the currently logged in user if a user name and password match.
     * @param userName The user's login name.
     * @param password The user's actual password (not password hash).
     * @return {@code true} if a matching {@link UserRow#userName} is found, and the password hash matches the stored value; otherwise {@code false}.
     * @throws InvalidOperationException
     * @throws java.sql.SQLException
     */
    public static boolean trySetCurrentUser(String userName, String password) throws InvalidOperationException, SQLException {
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
            getLogger().log(Level.WARNING, "No matching userName found");
        return false;
    }
    
    //</editor-fold>
}
