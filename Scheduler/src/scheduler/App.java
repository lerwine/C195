package scheduler;

import com.mysql.jdbc.Connection;
import controller.LoginScreenController;
import java.io.IOException;
import java.io.InputStream;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.HashMap;
import java.util.Locale;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    
    public static <T> void changeScene(Node eventSource, String path, java.util.function.BiConsumer<Stage, T> setupController) {
        changeScene((Stage)eventSource.getScene().getWindow(), path, setupController);
    }
    
    public static <T> void changeScene(Node eventSource, String path, java.util.function.Consumer<T> setupController) {
        changeScene((Stage)eventSource.getScene().getWindow(), path, setupController);
    }
    
    public static void changeScene(Node eventSource, String path) {
        changeScene((Stage)eventSource.getScene().getWindow(), path);
    }
    
    public static <T> void changeScene(Stage stage, String path, java.util.function.BiConsumer<Stage, T> setupController) {
        // Create new FXML loader with the resource path URL of the new fxml page and the resource bundle for the current language.
        FXMLLoader loader = new FXMLLoader(App.class.getResource(path), Messages.current().resourceBundle());
        
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
    
    public static <T> void changeScene(Stage stage, String path, java.util.function.Consumer<T> setupController) {
        // Create new FXML loader with the resource path URL of the new fxml page and the resource bundle for the current language.
        FXMLLoader loader = new FXMLLoader(App.class.getResource(path), Messages.current().resourceBundle());
        
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
    
    public static void changeScene(Stage stage, String path) {
        // Create new FXML loader with the resource path URL of the new fxml page and the resource bundle for the current language.
        FXMLLoader loader = new FXMLLoader(App.class.getResource(path), Messages.current().resourceBundle());
        
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
        Messages.setCurrent(new Messages(currentLocale));
        AppConfig.refresh();
        // Set initial scene to the login screen
        changeScene(stage, LoginScreenController.VIEW_PATH, (LoginScreenController controller) -> {
            controller.setCurrentStage(stage);
        });
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
    
    // This contains the current locale.
    private static Locale currentLocale;
    // This contains the formatters cache for the current locale.
    private static Formatters formatters = new Formatters();
    
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
        // Create a new, empty formatters cache object.
        formatters = new Formatters();
        // Set the new messages resource.
        Messages.setCurrent(new Messages(locale));
    }
    
    /**
     * Gets the current localized, {@link Locale}-specific Date formatter for the specified {@link FormatStyle}.
     * @param style The style of Date formatter to return.
     * @return The current localized, {@link Locale}-specific Date formatter for the specified {@link FormatStyle}.
     */
    public static DateTimeFormatter getDateFormatter(FormatStyle style) {
        TemporalFormatters fmt;
        if (formatters.temporal.containsKey(style)) {
            fmt = formatters.temporal.get(style);
            if (fmt.date.isPresent())
                return fmt.date.get();
        } else {
            fmt = new TemporalFormatters();
            formatters.temporal.put(style, fmt);
        }
        DateTimeFormatter result = DateTimeFormatter.ofLocalizedDate(style).withLocale(currentLocale);
        fmt.date = Optional.of(result);
        return result;
    }
    
    /**
     * Gets the current localized, {@link Locale}-specific time formatter for the specified {@link FormatStyle}.
     * @param style The style of Time formatter to return.
     * @return The current {@link Locale}-specific time formatter for the specified {@link FormatStyle}.
     */
    public static DateTimeFormatter getTimeFormatter(FormatStyle style) {
        TemporalFormatters fmt;
        if (formatters.temporal.containsKey(style)) {
            fmt = formatters.temporal.get(style);
            if (fmt.time.isPresent())
                return fmt.time.get();
        } else {
            fmt = new TemporalFormatters();
            formatters.temporal.put(style, fmt);
        }
        DateTimeFormatter result = DateTimeFormatter.ofLocalizedTime(style).withLocale(currentLocale);
        fmt.time = Optional.of(result);
        return result;
    }
    
    /**
     * Gets the current localized, {@link Locale}-specific Date/Time formatter for the specified {@link FormatStyle}.
     * @param style The style of Date/Time formatter to return.
     * @return The current localized, {@link Locale}-specific Date/Time formatter for the specified {@link FormatStyle}.
     */
    public static DateTimeFormatter getDateTimeFormatter(FormatStyle style) {
        TemporalFormatters fmt;
        if (formatters.temporal.containsKey(style)) {
            fmt = formatters.temporal.get(style);
            if (fmt.dateTime.isPresent())
                return fmt.dateTime.get();
        } else {
            fmt = new TemporalFormatters();
            formatters.temporal.put(style, fmt);
        }
        DateTimeFormatter result = DateTimeFormatter.ofLocalizedDateTime(style).withLocale(currentLocale);
        fmt.dateTime = Optional.of(result);
        return result;
    }
    
    /**
     * Gets the current {@link Locale}-specific general numeric value formatter.
     * @return The current {@link Locale}-specific general numeric value formatter.
     */
    public static NumberFormat getNumericFormatter() {
        if (formatters.value.isPresent())
            return formatters.value.get();
        NumberFormat result = NumberFormat.getInstance(currentLocale);
        formatters.value = Optional.of(result);
        return result;
    }
    
    /**
     * Gets the current {@link Locale}-specific floating-point number value formatter.
     * @return The current {@link Locale}-specific floating-point number value formatter.
     */
    public static NumberFormat getNumberFormatter() {
        if (formatters.number.isPresent())
            return formatters.number.get();
        NumberFormat result = NumberFormat.getNumberInstance(currentLocale);
        formatters.number = Optional.of(result);
        return result;
    }
    
    /**
     * Gets the current {@link Locale}-specific currency value formatter.
     * @return The current {@link Locale}-specific currency value formatter.
     */
    public static NumberFormat getCurrencyFormatter() {
        if (formatters.currency.isPresent())
            return formatters.currency.get();
        NumberFormat result = NumberFormat.getCurrencyInstance(currentLocale);
        formatters.currency = Optional.of(result);
        return result;
    }
    
    /**
     * Gets the current {@link Locale}-specific integer value formatter.
     * @return The current {@link Locale}-specific integer value formatter.
     */
    public static NumberFormat getIntegerFormatter() {
        if (formatters.integer.isPresent())
            return formatters.integer.get();
        NumberFormat result = NumberFormat.getIntegerInstance(currentLocale);
        formatters.integer = Optional.of(result);
        return result;
    }
    
    /**
     * Gets the current {@link Locale}-specific percentage value formatter.
     * @return The current {@link Locale}-specific percentage value formatter.
     */
    public static NumberFormat getPercentageFormatter() {
        if (formatters.percentage.isPresent())
            return formatters.percentage.get();
        NumberFormat result = NumberFormat.getPercentInstance(currentLocale);
        formatters.percentage = Optional.of(result);
        return result;
    }
    
    /**
     * A class that is used internally by {@link App} methods as a cache of formatters for the current {@link Locale}.
     */
    static class Formatters {
        /**
         * Cached temporal formatters indexed by their {@link FormatStyle}.
         */
        private HashMap<FormatStyle, TemporalFormatters> temporal = new HashMap<>();
        /**
         * The cached {@link NumberFormat} for general numeric values.
         */
        private Optional<NumberFormat> value = Optional.empty();
        /**
         * The cached {@link NumberFormat} for floating-point number values.
         */
        private Optional<NumberFormat> number = Optional.empty();
        /**
         * The cached {@link NumberFormat} for currency values.
         */
        private Optional<NumberFormat> currency = Optional.empty();
        /**
         * The cached {@link NumberFormat} for integer values.
         */
        private Optional<NumberFormat> integer = Optional.empty();
        /**
         * The cached {@link NumberFormat} for percentage values.
         */
        private Optional<NumberFormat> percentage = Optional.empty();
        
    }
    
    /**
     * A class that is used internally in {@link Formatters} fields as a cache of temporal formatters for the current {@link Locale}.
     */
    static class TemporalFormatters {
        /**
         * The cached {@link DateTimeFormatter} for formatting date strings.
         */
        private Optional<DateTimeFormatter> date = Optional.empty();
        /**
         * The cached {@link DateTimeFormatter} for formatting date/time strings.
         */
        private Optional<DateTimeFormatter> dateTime = Optional.empty();
        /**
         * The cached {@link DateTimeFormatter} for formatting time strings.
         */
        private Optional<DateTimeFormatter> time = Optional.empty();
    }
    
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
