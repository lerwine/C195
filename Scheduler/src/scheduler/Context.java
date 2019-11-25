/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler;

import java.io.IOException;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;
import javax.persistence.QueryTimeoutException;
import model.entity.User;
import utils.InvalidArgumentException;
import utils.InvalidOperationException;
import utils.PwHash;

/**
 * Contains contextual application information such as the current user, culture and database connection management.
 * @author Leonard T. Erwine
 */
public class Context {
    private static final Context INSTANCE = new Context();
    
    private Locale currentLocale;
    private Formatters formatters;
    private Optional<ResourceBundle> messagesRB;
    private User currentUser;
    @PersistenceUnit
    private EntityManagerFactory emf;
    private Stage currentStage;
    
    private Optional<EmDependency> latestEmDependency = Optional.empty();
    
    private Context() {
        currentLocale = Locale.getDefault();
        formatters = new Formatters();
        messagesRB = Optional.empty();
    }
    
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
    
    /**
     * Gets the currently logged in user.
     * @return The currently logged in user.
     */
    public static User getCurrentUser() { return INSTANCE.currentUser; }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="User Lookup Methods">
    
    /**
     * Finds a user by the user name.
     * @param em        An {@link EntityManager} object used to retrieve data from the database.
     * @param userName  The user's login name.
     * @return The user that was found or empty if no user was found.
     */
    public static Optional<User> getUserByUserName(EntityManager em, String userName) {
        List<User> user = (List<User>)em.createNamedQuery(User.NAMED_QUERY_BY_USERNAME)
                    .setParameter(User.PARAMETER_NAME_USERNAME, userName).getResultList();
        if (!user.isEmpty()) {
            if (user.size() == 1)
                return Optional.of(user.get(0));
            Logger.getLogger(Context.class.getName()).log(Level.SEVERE,
                    "The following records share the same login {0}: {1}", new Object[] { userName,
                        user.stream().map((u) -> u.getUserId().toString()).reduce((p, n) -> p + ", " + n) });
        }
        
        return Optional.empty();
    }
    
    /**
     * Finds a user by the primary key value.
     * @param em        An {@link EntityManager} object used to retrieve data from the database.
     * @param userId    The database primary key value.
     * @return The user that was found or empty if no user was found.
     */
    public static Optional<User> getUserByUserId(EntityManager em, int userId) {
        List<User> user = (List<User>)em.createNamedQuery(User.NAMED_QUERY_BY_ID)
                    .setParameter(User.PARAMETER_NAME_USERID, userId).getResultList();
        if (user.isEmpty())
            return Optional.empty();
        return Optional.of(user.get(0));
    }
    
    /**
     * Sets the currently logged in user if a user name and password match.
     * @param em        An {@link EntityManager} object used to retrieve data from the database.
     * @param userName  The user's login name.
     * @param password  The user's actual password (not password hash).
     * @return {@code true} if the user was logged in; otherwise {@code false}.
     */
    public static boolean trySetCurrentUser(EntityManager em, String userName, String password) {
        Optional<User> user;
        try {
            user = getUserByUserName(em, userName);
        } catch (QueryTimeoutException | NonUniqueResultException ex) {
            utils.NotificationHelper.showNotificationDialog("authentication", "authError", "dbAccessError",
                    Alert.AlertType.ERROR);
            Logger.getLogger(Context.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        if (user.isPresent()) {
            User u = user.get();
            try {
                // Check if we got a user from the DB and if the password hash matches.
                if ((new PwHash(u.getPassword(), false)).test(password)) {
                    INSTANCE.currentUser = u;
                    return true;
                }
            } catch (InvalidArgumentException ex) {
                Logger.getLogger(Context.class.getName()).log(Level.WARNING, null, ex);
            }
        } else {
            try {
                Logger.getLogger(Context.class.getName()).log(Level.SEVERE, "Hash: {0}", (new PwHash(password, true)).toString());
            } catch (InvalidArgumentException ex) {
                Logger.getLogger(Context.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        utils.NotificationHelper.showNotificationDialog("authentication", "authError", "invalidCredentials",
                Alert.AlertType.WARNING);
        return false;
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
    
    /**
     * Class used to open an SQL connection dependency through an {@link EntityManagerFactory}
     * which will only be open while there is an active dependency.
     */
    public static class EmDependency {
        private EntityManager em;
        private Optional<EmDependency> previous;
        private Optional<EmDependency> next;
        
        /**
         * Creates a new unopened EmDependency instance.
         */
        public EmDependency() {
            previous = Optional.empty();
            next = Optional.empty();
        }
        
        /**
         * Opens a new SQL connection dependency and returns an {@link EntityManager}.
         * @return An {@link EntityManager}.
         * @throws InvalidOperationException
         */
        public EntityManager open() throws InvalidOperationException {
            if (previous.isPresent() || next.isPresent())
                throw new InvalidOperationException("Entity manager dependency is already open.");
            if ((previous = INSTANCE.latestEmDependency).isPresent()) {
                EmDependency d = previous.get();
                if (d == this)
                    throw new InvalidOperationException("Entity manager dependency is already open.");
                INSTANCE.latestEmDependency = d.next = Optional.of(this);
            } else {
                INSTANCE.emf = Persistence.createEntityManagerFactory("SchedulerPU");
                INSTANCE.latestEmDependency = Optional.of(this);
            }
            em = INSTANCE.emf.createEntityManager();
            return em;
        }
        
        /**
         * Closes the SQL dependency and closes the {@link EntityManager} that was returned by the
         * {@link #open(utils.SchedulerContext)} method.
         * If this was the last remaining dependency, then the associated {@link EntityManagerFactory} will be closed as well.
         */
        public void close() {
            em.close();
            em = null;
            if (next.isPresent()) {
                if ((next.get().previous = previous).isPresent()) {
                    previous.get().next = next;
                    previous = Optional.empty();
                }
                next = Optional.empty();
            } else if ((INSTANCE.latestEmDependency = previous).isPresent())
                previous = previous.get().next = Optional.empty();
            else
                INSTANCE.emf.close();
        }
    }
}
