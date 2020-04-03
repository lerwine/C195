package scheduler;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import static scheduler.AppResourceBundleConstants.RESOURCEKEY_CONNECTEDTODB;
import static scheduler.AppResourceBundleConstants.RESOURCEKEY_CONNECTINGTODB;
import static scheduler.AppResourceBundleConstants.RESOURCEKEY_LOGGINGIN;
import scheduler.dao.UserDAO;
import scheduler.util.AlertHelper;
import scheduler.util.AnnotationHelper;
import scheduler.util.DbConnector;
import scheduler.util.EventHelper;
import scheduler.util.PwHash;
import scheduler.util.ViewControllerLoader;
import scheduler.view.Login;
import scheduler.view.MainController;
import scheduler.view.task.TaskWaiter;
import scheduler.view.ViewAndController;
import scheduler.view.event.FxmlViewEventType;

/**
 * Main Application class for the Scheduler application. Upon startup, {@link Login#loadInto(Stage)} is called to loadViewAndController the Login form
 * into the scene of the primary stage. To validate credentials, the {@link Login} controller invokes
 * {@link #tryLoginUser(Stage, String, String, Consumer)} After successful authentication, the current user data object is stored in
 * {@link Scheduler#currentUser}, and the view for {@link MainController} is loaded into the primary stage by the {@link LoginTask}.
 *
 * @author Leonard T. Erwine (Student ID 356334)
 */
public final class Scheduler extends Application {

    private static final Logger LOG = Logger.getLogger(Scheduler.class.getName());

    private static UserDAO currentUser = null;

    /**
     * Gets the currently logged in user.
     *
     * @return The {@link UserDAO} object representing the currently logged in user.
     */
    public static UserDAO getCurrentUser() {
        return currentUser;
    }

    /**
     * The application main entry point.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Looks up a user from the database and sets the current logged in user for the application if the password hash matches.
     *
     * @param stage The stage
     * @param userName The login name for the user to look up.
     * @param password The raw password provided by the user.
     * @param onNotSucceeded Handles login failures. The {@link Exception} argument will be null if there were no exceptions and either the login was
     * not found or the password hash did not match.
     */
    public static void tryLoginUser(Stage stage, String userName, String password, Consumer<Throwable> onNotSucceeded) {
        TaskWaiter.startNow(new LoginTask(stage, userName, password, onNotSucceeded));
    }

    @Override
    public void start(Stage stage) throws Exception {
        Login.loadInto(stage);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        DbConnector.forceCloseAll();
        super.stop();
    }

    private static class LoginTask extends TaskWaiter<UserDAO> {

        private final String userName, password;
        private final Consumer<Throwable> onNotSucceeded;

        LoginTask(Stage stage, String userName, String password, Consumer<Throwable> onNotSucceeded) {
            super(stage, AppResources.getResourceString(RESOURCEKEY_CONNECTINGTODB), AppResources.getResourceString(RESOURCEKEY_LOGGINGIN));
            this.userName = userName;
            this.password = password;
            this.onNotSucceeded = onNotSucceeded;
        }

        @Override
        protected void processResult(UserDAO user, Stage owner) {
            if (null == user) {
                if (null != onNotSucceeded) {
                    onNotSucceeded.accept(null);
                }
            } else {
                try {
                    ViewAndController<Parent, MainController> viewAndController = ViewControllerLoader.loadViewAndController(MainController.class);
                    EventHelper.invokeViewLifecycleEventMethods(viewAndController.getController(),
                            viewAndController.toEvent(this, FxmlViewEventType.LOADED, owner));
                    ((Stage) owner).setScene(new Scene(viewAndController.getView()));
                    EventHelper.invokeViewLifecycleEventMethods(viewAndController.getController(),
                            viewAndController.toEvent(this, FxmlViewEventType.BEFORE_SHOW, owner));
                    EventHelper.invokeViewLifecycleEventMethods(viewAndController.getController(),
                            viewAndController.toEvent(this, FxmlViewEventType.SHOWN, owner));
                } catch (IOException ex) {
                    AlertHelper.logAndAlertError(owner, LOG, "Error loading main content", ex);
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
        protected UserDAO getResult(Connection connection) throws SQLException {
            Optional<UserDAO> result;
            LOG.log(Level.INFO, String.format("Looking up %s", userName));
            Platform.runLater(() -> updateMessage(AppResources.getResourceString(RESOURCEKEY_CONNECTEDTODB)));
            result = UserDAO.getFactory().findByUserName(connection, userName);
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
            } else {
                LOG.log(Level.WARNING,"No matching userName found");
            }
            return null;
        }

    }

}
