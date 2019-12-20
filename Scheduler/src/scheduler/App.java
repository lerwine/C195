package scheduler;

import com.mysql.jdbc.Connection;
import controller.LoginScreenController;
import java.io.IOException;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
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
import model.AppointmentType;
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
     * @param setupController The delegate method that sets up the controller.
     */
    
    public static <T> void changeScene(Node eventSource, String path, java.util.function.BiConsumer<Stage, T> setupController) {
        App.setScene((Stage)eventSource.getScene().getWindow(), path, setupController);
    }
    
    /**
     * Sets the {@link javafx.scene.Scene} for the {@link javafx.stage.Stage} of the specified {@link javafx.scene.Node}
     * from the FXML resource at a given path and sets up its controller.
     * @param <T>
     * @param eventSource The {@link javafx.scene.Node} from the event source that initiated the scene change.
     * @param path The path of the FXML resource to load.
     * @param setupController The delegate method that sets up the controller.
     */
    public static <T> void changeScene(Node eventSource, String path, java.util.function.Consumer<T> setupController) {
        App.setScene((Stage)eventSource.getScene().getWindow(), path, setupController);
    }
    
    /**
     * Sets the {@link javafx.scene.Scene} for the {@link javafx.stage.Stage} of the specified {@link javafx.scene.Node}
     * from the FXML resource at a given path.
     * @param eventSource The {@link javafx.scene.Node} from the event source that initiated the scene change.
     * @param path The path of the FXML resource to load.
     */
    public static void changeScene(Node eventSource, String path) {
        setScene((Stage)eventSource.getScene().getWindow(), path);
    }
    
    /**
     * Sets the {@link javafx.scene.Scene} for the specified {@link javafx.stage.Stage} from the FXML resource at a given path
     * and sets up its controller.
     * @param <T> The type of controller to set up.
     * @param stage The target {@link javafx.stage.Stage}.
     * @param path The path of the FXML resource to load.
     * @param setupController The delegate method that sets up the controller.
     */
    public static <T> void setScene(Stage stage, String path, java.util.function.BiConsumer<Stage, T> setupController) {
        // Create new FXML loader with the resource path URL of the new fxml page and the resource bundle for the current language.
        FXMLLoader loader = new FXMLLoader(App.class.getResource(path), App.getAppResourceBundle());
        
        try {
            stage.setScene(new Scene(loader.load()));
        } catch (IOException ex) {
            getLogger().log(Level.SEVERE, null, ex);
            return;
        }
        
        // Call the consumer to for controller setup.
        setupController.accept(stage, loader.getController());
        
        stage.show();
    }
    
    /**
     * Sets the {@link javafx.scene.Scene} for the specified {@link javafx.stage.Stage} from the FXML resource at a given path
     * and sets up its controller.
     * @param <T> The type of controller to set up.
     * @param stage The target {@link javafx.stage.Stage}.
     * @param path The path of the FXML resource to load.
     * @param setupController The delegate method that sets up the controller.
     */
    public static <T> void setScene(Stage stage, String path, java.util.function.Consumer<T> setupController) {
        // Create new FXML loader with the resource path URL of the new fxml page and the resource bundle for the current language.
        FXMLLoader loader = new FXMLLoader(App.class.getResource(path), App.getAppResourceBundle());
        
        try {
            stage.setScene(new Scene(loader.load()));
        } catch (IOException ex) {
            getLogger().log(Level.SEVERE, null, ex);
            return;
        }
        
        // Call the consumer to for controller setup.
        setupController.accept(loader.getController());
        
        stage.show();
    }
    
    /**
     * Sets the {@link javafx.scene.Scene} for the specified {@link javafx.stage.Stage} from the FXML resource at a given path.
     * @param stage The target {@link javafx.stage.Stage}.
     * @param path The path of the FXML resource to load.
     */
    public static void setScene(Stage stage, String path) {
        // Create new FXML loader with the resource path URL of the new fxml page and the resource bundle for the current language.
        FXMLLoader loader = new FXMLLoader(App.class.getResource(path), App.getAppResourceBundle());
        
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
    public static ResourceBundle getAppResourceBundle() {
        if (appResourceBundle == null)
            appResourceBundle = ResourceBundle.getBundle(RESOURCE_NAME, getCurrentLocale());
        return appResourceBundle;
    }
    
    /**
     * Gets a 'File "%s" not found' formatted message in the current language.
     * @param fileName - The name of the file to place into the message.
     * @return The formatted 'File "%s" not found' message in the current language.
     */
    public static String getFileNotFoundMessage(String fileName) {
        return String.format(getAppResourceBundle().getString("fileNotFound"), fileName);
    }
    
    /**
     * Reads appointment types from the app resource bundle for the current language.
     * @return Appointment types from the app resource bundle for the current language.
     */
    public static ArrayList<AppointmentType> getAppointmentTypes() {
        ArrayList<AppointmentType> appointmentTypes = new ArrayList<>();
            ResourceBundle rb = getAppResourceBundle();
            Stream.of(AppointmentType.APPOINTMENT_CODE_PHONE, AppointmentType.APPOINTMENT_CODE_VIRTUAL, AppointmentType.APPOINTMENT_CODE_CUSTOMER,
                    AppointmentType.APPOINTMENT_CODE_HOME, AppointmentType.APPOINTMENT_CODE_GERMANY, AppointmentType.APPOINTMENT_CODE_INDIA,
                    AppointmentType.APPOINTMENT_CODE_HONDURAS, AppointmentType.APPOINTMENT_CODE_OTHER).forEach((String key) -> {
                appointmentTypes.add(new AppointmentType(key, rb.getString("appointmentType_" + key)));
            });
        return appointmentTypes;
    }
    
    // This contains the current locale.
    private static Locale currentLocale;
    private static DateTimeFormatter shortDateTimeFormatter;
    private static DateTimeFormatter fullDateTimeFormatter;
    private static DateTimeFormatter fullDateFormatter;
    private static DateTimeFormatter fullTimeFormatter;
    /**
     * Gets the current {@link Locale}.
     * @return The current {@link Locale}.
     */
    public static Locale getCurrentLocale() { return currentLocale; }
    
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
        fullTimeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.FULL).withLocale(locale).withZone(ZoneId.systemDefault());
        fullDateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(locale).withZone(ZoneId.systemDefault());
        shortDateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withLocale(locale).withZone(ZoneId.systemDefault());
        fullDateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL).withLocale(locale).withZone(ZoneId.systemDefault());
    }
    
    public static DateTimeFormatter getFullTimeFormatter() { return fullTimeFormatter; }
    
    public static DateTimeFormatter getFullDateFormatter() { return fullDateFormatter; }
    
    public static DateTimeFormatter getShortDateTimeFormatter() { return shortDateTimeFormatter; }
    
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
            try {
                // Create a password hash object for password hash comparison.
                PwHash pwHash = new PwHash(user.get().getPassword(), false);
                // See if the provided password, when hashed using the same seed as the stored hash is a match.
                if (pwHash.test(password)) {
                    currentUser = user;
                    return true;
                }
                getLogger().log(Level.WARNING, "Password hash check failed");
            } catch (InvalidArgumentException ex) {
                getLogger().log(Level.SEVERE, null, ex);
            }
        } else
            getLogger().log(Level.WARNING, "No matching userName found");
        return false;
    }
    
    //</editor-fold>
}
