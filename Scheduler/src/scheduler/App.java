package scheduler;

import java.io.IOException;
import java.util.Locale;
import scheduler.util.DbConnector;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.stage.Window;
import scheduler.dao.UserFactory;
import scheduler.util.PwHash;
import scheduler.view.MainController;
import scheduler.view.SchedulerController;
import scheduler.view.TaskWaiter;
import scheduler.view.login.LoginScene;

/**
 * Application class for Scheduler
 * @author Leonard T. Erwine
 */
public class App extends Application {
    
    //<editor-fold defaultstate="collapsed" desc="Resource keys">

    public static final String RESOURCEKEY_APPOINTMENTSCHEDULER = "appointmentScheduler";
    public static final String RESOURCEKEY_FILENOTFOUND = "fileNotFound";
    public static final String RESOURCEKEY_APPOINTMENTTYPE_PHONE = "appointmentType_phone";
    public static final String RESOURCEKEY_APPOINTMENTTYPE_VIRTUAL = "appointmentType_virtual";
    public static final String RESOURCEKEY_APPOINTMENTTYPE_CUSTOMER = "appointmentType_customer";
    public static final String RESOURCEKEY_APPOINTMENTTYPE_HOME = "appointmentType_home";
    public static final String RESOURCEKEY_APPOINTMENTTYPE_GERMANY = "appointmentType_germany";
    public static final String RESOURCEKEY_APPOINTMENTTYPE_INDIA = "appointmentType_india";
    public static final String RESOURCEKEY_APPOINTMENTTYPE_HONDURAS = "appointmentType_honduras";
    public static final String RESOURCEKEY_APPOINTMENTTYPE_OTHER = "appointmentType_other";
    public static final String RESOURCEKEY_FXMLLOADERERRORTITLE = "fxmlLoaderErrorTitle";
    public static final String RESOURCEKEY_FXMLLOADERERRORMESSAGE = "fxmlLoaderErrorMessage";
    public static final String RESOURCEKEY_NOTHINGSELECTED = "nothingSelected";
    public static final String RESOURCEKEY_NOITEMWASSELECTED = "noItemWasSelected";
    public static final String RESOURCEKEY_CONFIRMDELETE = "confirmDelete";
    public static final String RESOURCEKEY_AREYOUSUREDELETE = "areYouSureDelete";
    public static final String RESOURCEKEY_WORKING = "working";
    public static final String RESOURCEKEY_PLEASEWAIT = "pleaseWait";
    public static final String RESOURCEKEY_ABORT = "abort";
    public static final String RESOURCEKEY_CANCEL = "cancel";
    public static final String RESOURCEKEY_CONNECTINGTODB = "connectingToDb";
    public static final String RESOURCEKEY_LOGGINGIN = "loggingIn";
    public static final String RESOURCEKEY_CONNECTEDTODB = "connectedToDb";
    public static final String RESOURCEKEY_DBACCESSERROR = "dbAccessError";
    public static final String RESOURCEKEY_DBREADERROR = "dbReadError";
    public static final String RESOURCEKEY_GETTINGAPPOINTMENTS = "gettingAppointments";

    //</editor-fold>
    
    /**
     * The name of the general application globalization resource bundle.
     */
    public static final String GLOBALIZATION_RESOURCE_NAME = "scheduler/App";
    
//    /**
//     * The current application instance.
//     */
//    private static App current;
//    
//    public static final App getCurrent() { return current; }
    
    //<editor-fold defaultstate="collapsed" desc="Globalization Properties">

//    //<editor-fold defaultstate="collapsed" desc="allLanguages property">
//    
//    private ObservableList<Locale> allLanguages;
//    
//    /**
//     * Gets a list of {@link java.util.Locale} objects representing languages supported by the application.
//     * 
//     * @return
//     *          A list of {@link java.util.Locale} objects representing languages supported by the application.
//     */
//    public ObservableList<Locale> getAllLanguages() { return allLanguages; }
    
    //</editor-fold>
    
//    //<editor-fold defaultstate="collapsed" desc="originalDisplayLocale property">
//    
//    // Tracks the original locale settings at the time the app is started, so it can be restored when the app ends
//    private final Locale originalDisplayLocale;
//    
//    /**
//     * Gets the original display {@link java.util.Locale} at application start-up.
//     * 
//     * @return
//     *          The original display {@link java.util.Locale} at application start-up.
//     */
//    public Locale getOriginalDisplayLocale() { return originalDisplayLocale; }
//    
//    //</editor-fold>
    
//    //<editor-fold defaultstate="collapsed" desc="originalFormatLocale property">
//    
//    private final Locale originalFormatLocale;
//    
//    /**
//     * Gets the original format {@link java.util.Locale} at application start-up.
//     * 
//     * @return
//     *          The original format {@link java.util.Locale} at application start-up.
//     */
//    public Locale getOriginalFormatLocale() { return originalFormatLocale; }
//    
//    //</editor-fold>
    
//    //<editor-fold defaultstate="collapsed" desc="resources property">
//    
//    private ResourceBundle resources;
//    
//    /**
//     * Gets the application-global resource bundle for the current language.
//     * 
//     * @return
//     *          The application-global resource bundle for the current language.
//     */
//    public ResourceBundle getResources() { return resources; }
//    
//    //</editor-fold>
    
//    //<editor-fold defaultstate="collapsed" desc="appointmentTypes property">
//    
//    private final AppointmentTypes appointmentTypes;
//    
//    /**
//     * Gets an observable map that maps the appointment type codes to the locale-specific display value.
//     * 
//     * @return
//     *          An observable map that maps the appointment type codes to the locale-specific display value.
//     */
//    public ObservableMap<String, String> getAppointmentTypes() { return appointmentTypes; }
//    
//    //</editor-fold>
    
//    //<editor-fold defaultstate="collapsed" desc="fullTimeFormatter property">
//    
//    private DateTimeFormatter fullTimeFormatter;
//    
//    /**
//     * Gets the current locale-specific formatter for full time strings.
//     * 
//     * @return
//     *          The current locale-specific formatter for full time strings.
//     */
//    public DateTimeFormatter getFullTimeFormatter() { return fullTimeFormatter; }
//    
//    //</editor-fold>
//    
//    //<editor-fold defaultstate="collapsed" desc="fullDateFormatter property">
//    
//    private DateTimeFormatter fullDateFormatter;
//    
//    /**
//     * Gets the current locale-specific formatter for full date strings.
//     * 
//     * @return
//     *          The current locale-specific formatter for full date strings.
//     */
//    public DateTimeFormatter getFullDateFormatter() { return fullDateFormatter; }
//    
//    //</editor-fold>
//    
//    //<editor-fold defaultstate="collapsed" desc="shortDateTimeFormatter property">
//    
//    private DateTimeFormatter shortDateTimeFormatter;
//    
//    /**
//     * Gets the current locale-specific formatter for short date/time strings.
//     * 
//     * @return
//     *          The current locale-specific formatter for short date/time strings.
//     */
//    public DateTimeFormatter getShortDateTimeFormatter() { return shortDateTimeFormatter; }
//    
//    //</editor-fold>
//    
//    //<editor-fold defaultstate="collapsed" desc="fullDateTimeFormatter property">
//    
//    private DateTimeFormatter fullDateTimeFormatter;
//    
//    /**
//     * Gets the current locale-specific formatter for full date/time strings.
//     * 
//     * @return
//     *          The current locale-specific formatter for full date/time strings.
//     */
//    public DateTimeFormatter getFullDateTimeFormatter() { return fullDateTimeFormatter; }
//    
//    //</editor-fold>
//    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="currentUser property">
    
    private static UserFactory.UserImpl currentUser = null;
    
    public static UserFactory.UserImpl getCurrentUser() { return currentUser; }
    
    //</editor-fold>
    
    private static final Logger LOG;
    
    static {
        LOG = Logger.getLogger(App.class.getName());
    }
    
//    private void setResources(ResourceBundle bundle) {
////        resources = bundle;
//        appointmentTypes.load(bundle);
////        Locale locale = Locale.getDefault(Locale.Category.DISPLAY);
////        fullTimeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.FULL).withLocale(locale).withZone(ZoneId.systemDefault());
////        fullDateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(locale).withZone(ZoneId.systemDefault());
////        shortDateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withLocale(locale).withZone(ZoneId.systemDefault());
////        fullDateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL).withLocale(locale).withZone(ZoneId.systemDefault());
//    }
    
//    public App() {
//        // Store the original locale settings so they can be restored when app ends
////        originalDisplayLocale = Locale.getDefault(Locale.Category.DISPLAY);
////        originalFormatLocale = Locale.getDefault(Locale.Category.FORMAT);
//        appointmentTypes = new AppointmentTypes();
//    }
    
    //<editor-fold defaultstate="collapsed" desc="App Lifecycle Members">
    
    /**
     * The application main entry point.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    @SuppressWarnings("UseSpecificCatch")
    public void start(Stage stage) throws Exception {
//        current = this;
        // Ensure app config is freshly loaded.
        AppConfig.refresh();
        LoginScene.loadInto(stage);
//        allLanguages = new AllLanguages(AppConfig.getLanguages());
//        SchedulerController.ViewControllerFactory.loadInto(LoginScene.class, stage, true);
    }
    
    @Override
    public void stop() throws Exception {
        DbConnector.forceClose();
        // Resotre original locale settings
//        Locale.setDefault(Locale.Category.DISPLAY, originalDisplayLocale);
//        Locale.setDefault(Locale.Category.FORMAT, originalFormatLocale);
        super.stop();
    }

    //</editor-fold>

    private static ResourceBundle resources;
    
    public static ResourceBundle getResources() { return resources; }
    
    private static class LoginTask extends TaskWaiter<UserFactory.UserImpl> {
        private final String userName, password;
        private final Consumer<Throwable> onNotSucceeded;
        LoginTask(Stage stage, String userName, String password, Consumer<Throwable> onNotSucceeded) {
            this(ResourceBundle.getBundle(GLOBALIZATION_RESOURCE_NAME, Locale.getDefault(Locale.Category.DISPLAY)), stage, userName, password, onNotSucceeded);
        }
        private LoginTask(ResourceBundle rb, Stage stage, String userName, String password, Consumer<Throwable> onNotSucceeded) {
            super(stage, rb.getString(RESOURCEKEY_CONNECTINGTODB), rb.getString(RESOURCEKEY_LOGGINGIN));
            App.resources = rb;
            this.userName = userName;
            this.password = password;
            this.onNotSucceeded = onNotSucceeded;
        }

        @Override
        protected void processResult(UserFactory.UserImpl user, Window owner) {
            if (null == user) {
                if (null != onNotSucceeded)
                    onNotSucceeded.accept(null);
            } else if (Platform.isFxApplicationThread()) {
                try {
                    SchedulerController.load((Stage)owner, MainController.class, (Parent v, MainController c) -> {
                        ((Stage)owner).setScene(new Scene(v));
                    });
                } catch (IOException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            }
        }

        @Override
        protected void processException(Throwable ex, Window owner) {
                if (null != onNotSucceeded)
                    onNotSucceeded.accept(ex);
        }

        @Override
        protected UserFactory.UserImpl getResult() throws Exception {
            Optional<UserFactory.UserImpl> result;
            LOG.log(Level.INFO, String.format("Looking up %s", userName));
            try (DbConnector dep = new DbConnector()) {
                Platform.runLater(() -> updateMessage(ResourceBundle.getBundle(GLOBALIZATION_RESOURCE_NAME).getString(RESOURCEKEY_CONNECTEDTODB)));
                result = (new UserFactory()).findByUserName(dep.getConnection(), userName);
            }
            if (result.isPresent()) {
                // The password string stored in the database is a base-64 string that contains a cryptographic hash of the password
                // along with the cryptographic seed. A hash will be created from password argument using the same cryptographic seed
                // as the stored password. If the password is correct, then the hash values will match.
                PwHash hash = new PwHash(result.get().getPassword(), false);
                if (hash.test(password)) {
                    LOG.log(Level.INFO, "Password matched");
                    currentUser = result.get();
                    return result.get();
                }
                LOG.log(Level.WARNING, "Password mismatch");
            } else
                LOG.log(Level.WARNING, "No matching userName found");
            return null;
        }
        
    }
    
    /**
     * Looks up a user from the database and sets the current logged in user for the application if the password hash matches.
     * @param stage The stage
     * @param userName The login name for the user to look up.
     * @param password The raw password provided by the user.
     * @param onNotSucceeded Handles login failures. The {@link Exception} argument will be null if there were no exceptions
     * and either the login was not found or the password hash did not match.
     */
    public static void tryLoginUser(Stage stage, String userName, String password, Consumer<Throwable> onNotSucceeded) {
        TaskWaiter.execute(new LoginTask(stage, userName, password, onNotSucceeded));
    }
      
}
