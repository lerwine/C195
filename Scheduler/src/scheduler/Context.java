/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler;

import com.mysql.jdbc.Connection;
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
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.db.User;
import utils.InvalidArgumentException;
import utils.InvalidOperationException;
import utils.PwHash;

/**
 * Contains contextual application information such as the current user, culture and database connection management.
 * @author Leonard T. Erwine
 */
public class Context {
    private static final Context INSTANCE = new Context();

    private Context() {
        currentLocale = Locale.getDefault();
        formatters = new Formatters();
        messagesRB = Optional.empty();
        currentUser = Optional.empty();
    }
    
    //<editor-fold defaultstate="collapsed" desc="FXML View">
    
    private Stage currentStage;
    
    static void setCurrentStage(Stage stage) { INSTANCE.currentStage = stage; }
    
    public static void setWindowTitle(String title) { INSTANCE.currentStage.setTitle(title); }
    
    /**
     * Utility method to initialize the controller and switch scenes.
     *
     * @param <T> The type of controller to initialize.
     * @param eventSource The source Node for the event.
     * @param path The path of the FXML file to load.
     * @param initializeController Function for initializing the controller.
     */
    public static <T> void changeScene(Node eventSource, String path, java.util.function.Consumer<T> initializeController) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Context.class.getResource(path));
        Stage stage = (Stage)eventSource.getScene().getWindow();
        try {
            stage.setScene(new Scene(loader.load()));
            T controller = loader.getController();
            if (initializeController != null)
                initializeController.accept(controller);
        } catch (IOException ex) {
            Logger.getLogger(Context.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        stage.show();
    }
    
    /**
     * Utility method to change switch to another scene.
     *
     * @param eventSource The source node for the event.
     * @param path The path of the FXML file to load.
     */
    public static void changeScene(Node eventSource, String path) {
        changeScene(eventSource, path, null);
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Globalization">
    
    private Locale currentLocale;
    private Formatters formatters;
    private Optional<ResourceBundle> messagesRB;
    
    public static Locale getCurrentLocale() { return INSTANCE.currentLocale; }
    
    public static void setCurrentLocale(Locale locale) {
        Locale.setDefault(Locale.Category.DISPLAY, locale);
        Locale.setDefault(Locale.Category.FORMAT, locale);
        INSTANCE.currentLocale = locale;
        INSTANCE.messagesRB = Optional.empty();
        INSTANCE.formatters = new Formatters();
    }
    
    public static String getMessage(String key){ return getMessagesRB().getString(key); }
    
    public static String[] getMessageArray(String key){ return getMessagesRB().getStringArray(key); }
    
    public static Object getMessageResourceObject(String key){ return getMessagesRB().getObject(key); }
    
    public static ResourceBundle getMessagesRB() {
        if (INSTANCE.messagesRB.isPresent())
            return INSTANCE.messagesRB.get();
        ResourceBundle rb = ResourceBundle.getBundle("Messages", INSTANCE.currentLocale);
        INSTANCE.messagesRB = Optional.of(rb);
        return rb;
    }
    
    public static DateTimeFormatter getDateFormatter(FormatStyle style) {
        TemporalFormatters fmt;
        if (INSTANCE.formatters.temporal.containsKey(style)) {
            fmt = INSTANCE.formatters.temporal.get(style);
            if (fmt.date.isPresent())
                return fmt.date.get();
        } else {
            fmt = new TemporalFormatters();
            INSTANCE.formatters.temporal.put(style, fmt);
        }
        DateTimeFormatter result = DateTimeFormatter.ofLocalizedDate(style).withLocale(INSTANCE.currentLocale);
        fmt.date = Optional.of(result);
        return result;
    }
    
    public static DateTimeFormatter getTimeFormatter(FormatStyle style) {
        TemporalFormatters fmt;
        if (INSTANCE.formatters.temporal.containsKey(style)) {
            fmt = INSTANCE.formatters.temporal.get(style);
            if (fmt.time.isPresent())
                return fmt.time.get();
        } else {
            fmt = new TemporalFormatters();
            INSTANCE.formatters.temporal.put(style, fmt);
        }
        DateTimeFormatter result = DateTimeFormatter.ofLocalizedTime(style).withLocale(INSTANCE.currentLocale);
        fmt.time = Optional.of(result);
        return result;
    }
    
    public static DateTimeFormatter getDateTimeFormatter(FormatStyle style) {
        TemporalFormatters fmt;
        if (INSTANCE.formatters.temporal.containsKey(style)) {
            fmt = INSTANCE.formatters.temporal.get(style);
            if (fmt.dateTime.isPresent())
                return fmt.dateTime.get();
        } else {
            fmt = new TemporalFormatters();
            INSTANCE.formatters.temporal.put(style, fmt);
        }
        DateTimeFormatter result = DateTimeFormatter.ofLocalizedDateTime(style).withLocale(INSTANCE.currentLocale);
        fmt.dateTime = Optional.of(result);
        return result;
    }
    
    public static NumberFormat getNumericFormatter() {
        if (INSTANCE.formatters.value.isPresent())
            return INSTANCE.formatters.value.get();
        NumberFormat result = NumberFormat.getInstance(INSTANCE.currentLocale);
        INSTANCE.formatters.value = Optional.of(result);
        return result;
    }
    
    public static NumberFormat getNumberFormatter() {
        if (INSTANCE.formatters.number.isPresent())
            return INSTANCE.formatters.number.get();
        NumberFormat result = NumberFormat.getNumberInstance(INSTANCE.currentLocale);
        INSTANCE.formatters.number = Optional.of(result);
        return result;
    }
    
    public static NumberFormat getCurrencyFormatter() {
        if (INSTANCE.formatters.currency.isPresent())
            return INSTANCE.formatters.currency.get();
        NumberFormat result = NumberFormat.getCurrencyInstance(INSTANCE.currentLocale);
        INSTANCE.formatters.currency = Optional.of(result);
        return result;
    }
    
    public static NumberFormat getIntegerFormatter() {
        if (INSTANCE.formatters.integer.isPresent())
            return INSTANCE.formatters.integer.get();
        NumberFormat result = NumberFormat.getIntegerInstance(INSTANCE.currentLocale);
        INSTANCE.formatters.integer = Optional.of(result);
        return result;
    }
    
    public static NumberFormat getPercentageFormatter() {
        if (INSTANCE.formatters.percentage.isPresent())
            return INSTANCE.formatters.percentage.get();
        NumberFormat result = NumberFormat.getPercentInstance(INSTANCE.currentLocale);
        INSTANCE.formatters.percentage = Optional.of(result);
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
    
    private Optional<User> currentUser;
    
    /**
     * Gets the currently logged in user.
     * @return The currently logged in user.
     */
    public static Optional<User> getCurrentUser() { return INSTANCE.currentUser; }
    
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
                        INSTANCE.currentUser = user;
                        return true;
                    }
                    pwHash = new PwHash(password, true);
                    Logger.getLogger(Context.class.getName()).log(Level.WARNING, pwHash.toString());
                } catch (InvalidArgumentException ex) {
                    Logger.getLogger(Context.class.getName()).log(Level.SEVERE, null, ex);
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
    
    private Connection connection;
    
    private Optional<SqlConnectionDependency> latestConnectionDependency = Optional.empty();
    
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
            if ((previous = INSTANCE.latestConnectionDependency).isPresent()) {
                SqlConnectionDependency d = previous.get();
                if (d == this)
                    throw new InvalidOperationException("SQL Connection dependency is already open.");
                INSTANCE.latestConnectionDependency = d.next = Optional.of(this);
            } else {
                Class.forName(DB_DRIVER);
                INSTANCE.connection = (Connection)DriverManager.getConnection(DB_URL, DB_USER_NAME, DB_PASSWORD);
                System.out.println("Connection Successful");
                INSTANCE.latestConnectionDependency = Optional.of(this);
            }
            connection = INSTANCE.connection;
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
            } else if ((INSTANCE.latestConnectionDependency = previous).isPresent())
                previous = previous.get().next = Optional.empty();
            else
                INSTANCE.connection.close();
        }
    }
    
    //</editor-fold>
}
