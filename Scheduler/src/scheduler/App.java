package scheduler;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
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
import scheduler.dao.UserImpl;
import scheduler.util.Alerts;
import scheduler.util.PwHash;
import scheduler.view.MainController;
import scheduler.view.SchedulerController;
import scheduler.view.TaskWaiter;
import scheduler.view.Login;

/**
 * Main Application class for Scheduler.
 * Upon startup, {@link Login#loadInto(javafx.stage.Stage)} is called to load the Login form into the scene of the primary stage
 * After successful authentication, the current user data object is stored in {@link App#currentUser}, and the view for {@link MainController} is loaded into the primary stage using
 * {@link SchedulerController#load(javafx.stage.Stage, java.lang.Class, java.util.function.BiConsumer)}.
 * @author Leonard T. Erwine
 */
public final class App extends Application implements AppConstants {

    private static final Logger LOG = Logger.getLogger(App.class.getName());

    //<editor-fold defaultstate="collapsed" desc="currentUser property">
    private static UserImpl currentUser = null;

    /**
     * Gets the currently logged in user.
     * @return The {@link UserImpl} object representing the currently logged in user.
     */
    public static UserImpl getCurrentUser() {
        return currentUser;
    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="App Lifecycle Members">
    /**
     * The application main entry point.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        AppConfig.refresh();
        Login.loadInto(stage);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        DbConnector.forceCloseAll();
        super.stop();
    }

    //</editor-fold>
    private static ResourceBundle resources;

    /**
     * Gets the application {@link ResourceBundle}.
     * @return The application {@link ResourceBundle} for the current {@link Locale#defaultDisplayLocale}.
     */
    public static ResourceBundle getResources() {
        return resources;
    }

    public static String getResourceString(String key) {
        return resources.getString(key);
    }

    private static class LoginTask extends TaskWaiter<UserImpl> {

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
        protected void processResult(UserImpl user, Stage owner) {
            if (null == user) {
                if (null != onNotSucceeded) {
                    onNotSucceeded.accept(null);
                }
            } else {
                try {
                    SchedulerController.load((Stage) owner, MainController.class, (Parent v, MainController c) -> {
                        ((Stage) owner).setScene(new Scene(v));
                    });
                } catch (IOException ex) {
                    Alerts.logAndAlertError(LOG, getClass(), "processResult", "Error loading main content", ex);
                }
            }
        }

        @Override
        protected void processException(Throwable ex, Stage owner) {
            if (null != onNotSucceeded) {
                onNotSucceeded.accept(ex);
            }
        }

        @Override
        protected UserImpl getResult(Connection connection) throws SQLException {
            Optional<UserImpl> result;
            LOG.logp(Level.INFO, getClass().getName(), "getResult", String.format("Looking up %s", userName));
            Platform.runLater(() -> updateMessage(ResourceBundle.getBundle(GLOBALIZATION_RESOURCE_NAME).getString(RESOURCEKEY_CONNECTEDTODB)));
            result = UserImpl.getFactory().findByUserName(connection, userName);
            if (result.isPresent()) {
                // The password string stored in the database is a base-64 string that contains a cryptographic hash of the password
                // along with the cryptographic seed. A hash will be created from password argument using the same cryptographic seed
                // as the stored password. If the password is correct, then the hash values will match.
                PwHash hash = new PwHash(result.get().getPassword(), false);
                if (hash.test(password)) {
                    LOG.logp(Level.INFO, getClass().getName(), "getResult", "Password matched");
                    currentUser = result.get();
                    return result.get();
                }
                LOG.logp(Level.WARNING, getClass().getName(), "getResult", "Password mismatch");
            } else {
                LOG.logp(Level.WARNING, getClass().getName(), "getResult", "No matching userName found");
            }
            return null;
        }

    }

    /**
     * Looks up a user from the database and sets the current logged in user for the application if the password hash matches.
     *
     * @param stage The stage
     * @param userName The login name for the user to look up.
     * @param password The raw password provided by the user.
     * @param onNotSucceeded Handles login failures. The {@link Exception} argument will be null if there were no exceptions and either the login was not found or the password hash
     * did not match.
     */
    public static void tryLoginUser(Stage stage, String userName, String password, Consumer<Throwable> onNotSucceeded) {
        TaskWaiter.execute(new LoginTask(stage, userName, password, onNotSucceeded));
    }

}
