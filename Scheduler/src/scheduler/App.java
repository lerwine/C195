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
import scheduler.util.Alerts;
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
    
    //<editor-fold defaultstate="collapsed" desc="Resource bundle keys">
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Appointment Scheduler"}.
     */
    public static final String RESOURCEKEY_APPOINTMENTSCHEDULER = "appointmentScheduler";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "File "%s" not found."}.
     */
    public static final String RESOURCEKEY_FILENOTFOUND = "fileNotFound";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Phone Conference"}.
     */
    public static final String RESOURCEKEY_APPOINTMENTTYPE_PHONE = "appointmentType_phone";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Virtual Meeting"}.
     */
    public static final String RESOURCEKEY_APPOINTMENTTYPE_VIRTUAL = "appointmentType_virtual";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Customer Site"}.
     */
    public static final String RESOURCEKEY_APPOINTMENTTYPE_CUSTOMER = "appointmentType_customer";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Home Office"}.
     */
    public static final String RESOURCEKEY_APPOINTMENTTYPE_HOME = "appointmentType_home";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Germany Office"}.
     */
    public static final String RESOURCEKEY_APPOINTMENTTYPE_GERMANY = "appointmentType_germany";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "India Office"}.
     */
    public static final String RESOURCEKEY_APPOINTMENTTYPE_INDIA = "appointmentType_india";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Honduras Office"}.
     */
    public static final String RESOURCEKEY_APPOINTMENTTYPE_HONDURAS = "appointmentType_honduras";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Other in-person meeting"}.
     */
    public static final String RESOURCEKEY_APPOINTMENTTYPE_OTHER = "appointmentType_other";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "FXML Loader Error"}.
     */
    public static final String RESOURCEKEY_FXMLLOADERERRORTITLE = "fxmlLoaderErrorTitle";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Error loading login scene content..."}.
     */
    public static final String RESOURCEKEY_FXMLLOADERERRORMESSAGE = "fxmlLoaderErrorMessage";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Nothing selected"}.
     */
    public static final String RESOURCEKEY_NOTHINGSELECTED = "nothingSelected";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "No item was selected."}.
     */
    public static final String RESOURCEKEY_NOITEMWASSELECTED = "noItemWasSelected";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Confirm Delete"}.
     */
    public static final String RESOURCEKEY_CONFIRMDELETE = "confirmDelete";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Working"}.
     */
    public static final String RESOURCEKEY_WORKING = "working";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Please wait..."}.
     */
    public static final String RESOURCEKEY_PLEASEWAIT = "pleaseWait";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Abort"}.
     */
    public static final String RESOURCEKEY_ABORT = "abort";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Cancel"}.
     */
    public static final String RESOURCEKEY_CANCEL = "cancel";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Connecting to database"}.
     */
    public static final String RESOURCEKEY_CONNECTINGTODB = "connectingToDb";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Logging in..."}.
     */
    public static final String RESOURCEKEY_LOGGINGIN = "loggingIn";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Connected to database"}.
     */
    public static final String RESOURCEKEY_CONNECTEDTODB = "connectedToDb";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Database access error"}.
     */
    public static final String RESOURCEKEY_DBACCESSERROR = "dbAccessError";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Error reading data from database. See logs for details."}.
     */
    public static final String RESOURCEKEY_DBREADERROR = "dbReadError";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Getting appointments"}.
     */
    public static final String RESOURCEKEY_GETTINGAPPOINTMENTS = "gettingAppointments";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Delete"}.
     */
    public static final String RESOURCEKEY_DELETE = "delete";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "This action cannot be undone!..."}.
     */
    public static final String RESOURCEKEY_AREYOUSUREDELETE = "areYouSureDelete";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Unexpected Error"}.
     */
    public static final String RESOURCEKEY_UNEXPECTEDERRORTITLE = "unexpectedErrorTitle";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "An unexpected error has occurred."}.
     */
    public static final String RESOURCEKEY_UNEXPECTEDERRORHEADING = "unexpectedErrorHeading";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "See application logs for technical details."}.
     */
    public static final String RESOURCEKEY_UNEXPECTEDERRORDETAILS = "unexpectedErrorDetails";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Type:"}.
     */
    public static final String RESOURCEKEY_TYPE = "type";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Message:"}.
     */
    public static final String RESOURCEKEY_MESSAGE = "message";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Error Code:"}.
     */
    public static final String RESOURCEKEY_ERRORCODE = "errorCode";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "State:"}.
     */
    public static final String RESOURCEKEY_STATE = "state";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Related Exceptions:"}.
     */
    public static final String RESOURCEKEY_RELATEDEXCEPTIONS = "relatedExceptions";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Stack Trace:"}.
     */
    public static final String RESOURCEKEY_STACKTRACE = "stackTrace";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Caused By:"}.
     */
    public static final String RESOURCEKEY_CAUSEDBY = "causedBy";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Warning"}.
     */
    public static final String RESOURCEKEY_WARNING = "warning";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Delete Failure"}.
     */
    public static final String RESOURCEKEY_DELETEFAILURE = "deleteFailure";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Deleting Record"}.
     */
    public static final String RESOURCEKEY_DELETINGRECORD = "deletingRecord";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Error deleting record from database..."}.
     */
    public static final String RESOURCEKEY_ERRORDELETINGFROMDB = "errorDeletingFromDb";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "A database access error occurred while trying to save changes to the database..."}.
     */
    public static final String RESOURCEKEY_ERRORSAVINGCHANGES = "errorSavingChanges";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Unable to delete the record from the database..."}.
     */
    public static final String RESOURCEKEY_DELETEDEPENDENCYERROR = "deleteDependencyError";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Unable to save the record to the database..."}.
     */
    public static final String RESOURCEKEY_SAVEDEPENDENCYERROR = "saveDependencyError";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Record Save Failure"}.
     */
    public static final String RESOURCEKEY_SAVEFAILURE = "saveFailure";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Load Error"}.
     */
    public static final String RESOURCEKEY_LOADERRORTITLE = "loadErrorTitle";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Unexpected error trying to load child window..."}.
     */
    public static final String RESOURCEKEY_LOADERRORMESSAGE = "loadErrorMessage";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Saving Changes"}.
     */
    public static final String RESOURCEKEY_SAVINGCHANGES = "savingChanges";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Log Message:"}.
     */
    public static final String RESOURCEKEY_LOGMESSAGE = "logMessage";
    
    //</editor-fold>
    
    /**
     * The name of the general application globalization resource bundle.
     */
    public static final String GLOBALIZATION_RESOURCE_NAME = "scheduler/App";
    
    //<editor-fold defaultstate="collapsed" desc="currentUser property">
    
    private static UserFactory.UserImpl currentUser = null;
    
    public static UserFactory.UserImpl getCurrentUser() { return currentUser; }
    
    //</editor-fold>
    
    private static final Logger LOG = Logger.getLogger(App.class.getName());
    
    //<editor-fold defaultstate="collapsed" desc="App Lifecycle Members">
    
    /**
     * The application main entry point.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage stage) throws Exception {
        AppConfig.refresh();
        LoginScene.loadInto(stage);
        stage.show();
    }
    
    @Override
    public void stop() throws Exception {
        DbConnector.forceClose();
        super.stop();
    }

    //</editor-fold>

    private static ResourceBundle resources;
    
    public static ResourceBundle getResources() { return resources; }
    
    public static String getResourceString(String key) { return resources.getString(key); }
    
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
            } else {
                try {
                    SchedulerController.load((Stage)owner, MainController.class, (Parent v, MainController c) -> {
                        ((Stage)owner).setScene(new Scene(v));
                    });
                } catch (IOException ex) {
                    Alerts.logAndAlert(LOG, getClass(), "processResult", "Error loading main content", ex);
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
