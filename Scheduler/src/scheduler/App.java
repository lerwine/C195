/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler;

import com.mysql.jdbc.Connection;
import controller.LoginScreenController;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.HashMap;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.db.User;
import utils.InvalidArgumentException;
import utils.InvalidOperationException;
import utils.PwHash;

/**
 * Application class for Scheduler
 * @author Leonard T. Erwine
 */
public class App extends Application {
    //<editor-fold defaultstate="collapsed" desc="changeScene">
    
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
        FXMLLoader loader = new FXMLLoader(App.class.getResource(path), currentResourceBundle);
        
        try {
            stage.setScene(new Scene(loader.load()));
        } catch (IOException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        
        // Call the consumer to for controller setup.
        setupController.accept(stage, loader.getController());
        
        stage.show();
    }
    
    public static <T> void changeScene(Stage stage, String path, java.util.function.Consumer<T> setupController) {
        // Create new FXML loader with the resource path URL of the new fxml page and the resource bundle for the current language.
        FXMLLoader loader = new FXMLLoader(App.class.getResource(path), currentResourceBundle);
        
        try {
            stage.setScene(new Scene(loader.load()));
        } catch (IOException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        
        // Call the consumer to for controller setup.
        setupController.accept(loader.getController());
        
        stage.show();
    }
    
    public static void changeScene(Stage stage, String path) {
        // Create new FXML loader with the resource path URL of the new fxml page and the resource bundle for the current language.
        FXMLLoader loader = new FXMLLoader(App.class.getResource(path), currentResourceBundle);
        
        try {
            stage.setScene(new Scene(loader.load()));
        } catch (IOException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        
        stage.show();
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="App Lifecycle">
    
    private Locale originalDisplayLocale;
    private Locale originalFormatLocale;
    
    @Override
    public void start(Stage stage) throws Exception {
        currentLocale = originalDisplayLocale = Locale.getDefault(Locale.Category.DISPLAY);
        originalFormatLocale = Locale.getDefault(Locale.Category.FORMAT);
        changeScene(stage, LoginScreenController.VIEW_PATH, (LoginScreenController controller) -> {
            controller.setCurrentStage(stage);
        });
    }
    
    @Override
    public void stop() throws Exception {
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
    
    //<editor-fold defaultstate="collapsed" desc="Globalization">
    
    private static ResourceBundle currentResourceBundle;
    private static Locale currentLocale;
    private static Formatters formatters = new Formatters();
    
    public static Locale getCurrentLocale() { return currentLocale; }
    
    public static void setCurrentLocale(Locale locale) {
        Locale.setDefault(Locale.Category.DISPLAY, locale);
        Locale.setDefault(Locale.Category.FORMAT, locale);
        currentLocale = locale;
        currentResourceBundle = ResourceBundle.getBundle("Messages", currentLocale);
        formatters = new Formatters();
    }
    
    public static String getMessage(String key){ return getMessagesRB().getString(key); }
    
    public static String[] getMessageArray(String key){ return getMessagesRB().getStringArray(key); }
    
    public static Object getMessageResourceObject(String key){ return getMessagesRB().getObject(key); }
    
    public static ResourceBundle getMessagesRB() { return currentResourceBundle; }
    
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
    
    public static NumberFormat getNumericFormatter() {
        if (formatters.value.isPresent())
            return formatters.value.get();
        NumberFormat result = NumberFormat.getInstance(currentLocale);
        formatters.value = Optional.of(result);
        return result;
    }
    
    public static NumberFormat getNumberFormatter() {
        if (formatters.number.isPresent())
            return formatters.number.get();
        NumberFormat result = NumberFormat.getNumberInstance(currentLocale);
        formatters.number = Optional.of(result);
        return result;
    }
    
    public static NumberFormat getCurrencyFormatter() {
        if (formatters.currency.isPresent())
            return formatters.currency.get();
        NumberFormat result = NumberFormat.getCurrencyInstance(currentLocale);
        formatters.currency = Optional.of(result);
        return result;
    }
    
    public static NumberFormat getIntegerFormatter() {
        if (formatters.integer.isPresent())
            return formatters.integer.get();
        NumberFormat result = NumberFormat.getIntegerInstance(currentLocale);
        formatters.integer = Optional.of(result);
        return result;
    }
    
    public static NumberFormat getPercentageFormatter() {
        if (formatters.percentage.isPresent())
            return formatters.percentage.get();
        NumberFormat result = NumberFormat.getPercentInstance(currentLocale);
        formatters.percentage = Optional.of(result);
        return result;
    }
    
    static class Formatters {
        private HashMap<FormatStyle, TemporalFormatters> temporal = new HashMap<>();
        private Optional<NumberFormat> value = Optional.empty();
        private Optional<NumberFormat> number = Optional.empty();
        private Optional<NumberFormat> currency = Optional.empty();
        private Optional<NumberFormat> integer = Optional.empty();
        private Optional<NumberFormat> percentage = Optional.empty();
        
    }
    
    static class TemporalFormatters {
        private Optional<DateTimeFormatter> date = Optional.empty();
        private Optional<DateTimeFormatter> dateTime = Optional.empty();
        private Optional<DateTimeFormatter> time = Optional.empty();
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Current User">
    
    private static Optional<User> currentUser = Optional.empty();
    
    /**
     * Gets the currently logged in user.
     * @return The currently logged in user.
     */
    public static Optional<User> getCurrentUser() { return currentUser; }
    
    /**
     * Sets the currently logged in user if a user name and password match.
     * @param userName      The user's login name.
     * @param password      The user's actual password (not password hash).
     * @return {@code true} if the user was logged in; otherwise {@code false}.
     * @throws utils.InvalidOperationException
     * @throws java.lang.ClassNotFoundException
     * @throws java.sql.SQLException
     */
    public static boolean trySetCurrentUser(String userName, String password) throws InvalidOperationException, ClassNotFoundException, SQLException {
        SqlConnectionDependency dep = new SqlConnectionDependency();
        Connection connection = dep.start();
        try {
            Optional<User> user = User.getByUserName(connection, userName, false);
            if (user.isPresent()) {
                try {
                    PwHash pwHash = new PwHash(user.get().getPassword(), false);
                    if (pwHash.test(password)) {
                        currentUser = user;
                        return true;
                    }
                    pwHash = new PwHash(password, true);
                    Logger.getLogger(App.class.getName()).log(Level.WARNING, pwHash.toString());
                } catch (InvalidArgumentException ex) {
                    Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } finally { dep.end(); }
        return false;
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="SQL Connection">
    
    private static final String SERVER_NAME = "3.227.166.251";
    private static final String DB_NAME = "U03vHM";
    private static final String DB_URL = "jdbc:mysql://" + SERVER_NAME + "/" + DB_NAME;
    private static final String DB_USER_NAME = "U03vHM";
    private static final String DB_PASSWORD = "53688096290";
    private static final String DB_DRIVER = "com.mysql.jdbc.Driver";
    
    private static Connection connection;
    
    private static Optional<SqlConnectionDependency> latestConnectionDependency = Optional.empty();
    
    /**
     * Class for managing an SQL connection dependency.
     * An SQL connection will be opened when the first dependency is started,
     * and the connection will end when the last dependency is ended.
     */
    public static class SqlConnectionDependency {
        private Connection connection;
        private Optional<SqlConnectionDependency> previous;
        private Optional<SqlConnectionDependency> next;
        
        /**
         * Creates a new unopened SqlConnectionDependency instance.
         */
        public SqlConnectionDependency() {
            previous = Optional.empty();
            next = Optional.empty();
        }
        
        /**
         * Starts a new SQL connection dependency and returns the {@link Connection}.
         * @return An open {@link Connection}.
         * @throws InvalidOperationException
         * @throws java.lang.ClassNotFoundException
         * @throws java.sql.SQLException
         */
        public Connection start() throws InvalidOperationException, ClassNotFoundException, SQLException {
            if (previous.isPresent() || next.isPresent())
                throw new InvalidOperationException("SQL Connection dependency is already open.");
            if ((previous = latestConnectionDependency).isPresent()) {
                SqlConnectionDependency d = previous.get();
                if (d == this)
                    throw new InvalidOperationException("SQL Connection dependency is already open.");
                latestConnectionDependency = d.next = Optional.of(this);
            } else {
                Class.forName(DB_DRIVER);
                connection = (Connection)DriverManager.getConnection(DB_URL, DB_USER_NAME, DB_PASSWORD);
                System.out.println("Connection Successful");
                latestConnectionDependency = Optional.of(this);
            }
            connection = connection;
            return connection;
        }
        
        /**
         * Ends the SQL dependency, indicating that an SQL connection is no longer needed.
         * If this was the last remaining dependency, then the associated {@link Connection} will be closed as well.
         * @throws java.sql.SQLException
         */
        public void end() throws SQLException {
            connection = null;
            if (next.isPresent()) {
                if ((next.get().previous = previous).isPresent()) {
                    previous.get().next = next;
                    previous = Optional.empty();
                }
                next = Optional.empty();
            } else if ((latestConnectionDependency = previous).isPresent())
                previous = previous.get().next = Optional.empty();
            else
                connection.close();
        }
    }
    
    //</editor-fold>
}
