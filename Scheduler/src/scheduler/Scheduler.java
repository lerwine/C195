package scheduler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventDispatchChain;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import static scheduler.AppResourceKeys.RESOURCEKEY_CONNECTEDTODB;
import static scheduler.AppResourceKeys.RESOURCEKEY_LOGGINGIN;
import static scheduler.util.NodeUtil.bindExtents;
import static scheduler.util.NodeUtil.unbindExtents;
import scheduler.dao.UserDAO;
import scheduler.util.DbConnector;
import scheduler.util.PwHash;
import scheduler.util.ThrowableConsumer;
import scheduler.util.ViewControllerLoader;
import scheduler.util.StageManager;
import scheduler.view.Login;
import scheduler.view.MainController;
import scheduler.view.Overview;
import scheduler.view.ViewAndController;

/**
 * Main Application class for the Scheduler application.
 * <p>
 * Upon startup, a temporary {@link StackPane} is created as the root node a the {@link Scene} of the primary {@link Stage}. The view for
 * {@link MainController} is added, and then the {@link Login} view is added over top of it. To validate credentials, the {@link Login} controller
 * invokes {@link #tryLoginUser(Stage, String, String, Consumer)}. After successful authentication, the current user data object is stored in
 * {@link Scheduler#currentUser}, and the view for {@link MainController} is removed from its temporary parent {@link StackPane} and becomes the root
 * node a new permanent {@link Scene} for the primary {@link Stage}.</p>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public final class Scheduler extends Application {

    private static final Logger LOG = Logger.getLogger(Scheduler.class.getName());
    private static final String PROPERTY_MAINCONTROLLER = "scheduler.view.MainController";
    private static Scheduler currentApp = null;
    private static UserDAO currentUser = null;

    /**
     * Gets the currently logged in user.
     *
     * @return The {@link UserDAO} object representing the currently logged in user.
     */
    public static UserDAO getCurrentUser() {
        return currentUser;
    }

    public static String resolveUri(String uri) {
        HostServices services = currentApp.getHostServices();
        return services.resolveURI(services.getDocumentBase(), uri);
    }

    public static MainController getMainController() {
        Scheduler app = currentApp;
        if (null != app) {
            ViewAndController<StackPane, MainController> viewAndController = app.mainViewAndController;
            if (null != viewAndController) {
                return viewAndController.getController();
            }
        }
        throw new IllegalStateException();
    }

    public static EventDispatchChain buildMainControllerEventDispatchChain(EventDispatchChain tail) {
        Scheduler app = currentApp;
        if (null != app) {
            ViewAndController<StackPane, MainController> viewAndController = app.mainViewAndController;
            if (null != viewAndController) {
                return viewAndController.getController().buildEventDispatchChain(tail);
            }
        }
        return tail;
    }

    /**
     * The application main entry point.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    private ViewAndController<StackPane, MainController> mainViewAndController;

    @Override
    public void init() throws Exception {
        currentApp = this;
        super.init();
    }

    @Override
    public void start(Stage stage) throws Exception {
        StageManager.setPrimaryStage(stage);
        stage.setTitle(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_APPOINTMENTSCHEDULER));
        // Load main view and controller
        mainViewAndController = ViewControllerLoader.loadViewAndController(MainController.class);
        StackPane mainView = mainViewAndController.getView();
        MainController mainController = mainViewAndController.getController();

        Login login = new Login();
        // Bind extents of login view to main view extents.
        login.setPrefSize(mainView.getPrefWidth(), mainView.getPrefHeight());
        login.setMaxSize(mainView.getMaxWidth(), mainView.getMaxHeight());

        stage.setScene(new Scene(mainView));
        mainView.getProperties().put(PROPERTY_MAINCONTROLLER, mainController);
        ObservableList<Node> children = mainView.getChildren();
        children.add(1, login);
        bindExtents(login, mainView);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        if (null != currentUser) {
            // TODO: Call appointmentAlert.stop();
            HostServices services = getHostServices();
            String logUri = services.resolveURI(services.getCodeBase(), "log.txt");
            LOG.info(String.format("Loggin logout timestamp to %s", logUri));
            try (FileWriter writer = new FileWriter(new File(new URL(logUri).toURI()), true)) {
                try (PrintWriter pw = new PrintWriter(writer)) {
                    pw.printf("[%s]: %s logged out.", DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now()), currentUser.getUserName());
                    pw.println();
                    pw.flush();
                    writer.flush();
                }
            } catch (IOException | URISyntaxException ex) {
                LOG.log(Level.SEVERE, "Error writing to log", ex);
            }
        }
        DbConnector.forceCloseAll();
        super.stop();
    }

    public static abstract class LoginBorderPane extends BorderPane {

        @SuppressWarnings("LeakingThisInConstructor")
        protected LoginBorderPane() {
            if (null != currentUser) {
                throw new IllegalStateException("Login controller cannot be instantiated after user is logged in");
            }
            try {
                ViewControllerLoader.initializeCustomControl(this);
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, "Error loading view", ex);
                throw new InternalError("Error loading view", ex);
            }
        }

        /**
         * Looks up a user from the database and sets the current logged in user for the application if the password hash matches.
         *
         * @param loginView The view for the login.
         * @param userName The login name for the user to look up.
         * @param password The raw password provided by the user.
         */
        protected void tryLoginUser(LoginBorderPane loginView, String userName, String password) {
            MainController.startBusyTaskNow(new LoginTask(loginView, userName, password, this::onLoginFailure));
        }

        /**
         * This gets called when a login has failed.
         *
         * @param reason The reason for login failure. If this is {@code null}, then the login name was not found or the password hash did not match.
         */
        protected abstract void onLoginFailure(Throwable reason);
    }

    private static class LoginTask extends Task<UserDAO> {

        private final String userName, password;
        private final Consumer<Throwable> onNotSucceeded;
        private final MainController mainController;
        private final StackPane mainPane;
        private final BorderPane loginView;

        LoginTask(LoginBorderPane loginView, String userName, String password, Consumer<Throwable> onNotSucceeded) {
            updateTitle(AppResources.getResourceString(RESOURCEKEY_LOGGINGIN));
            this.loginView = Objects.requireNonNull(loginView);
            this.userName = Objects.requireNonNull(userName);
            this.password = Objects.requireNonNull(password);
            this.onNotSucceeded = Objects.requireNonNull(onNotSucceeded);
            mainPane = (StackPane) loginView.getScene().getRoot();
            mainController = (MainController) (mainPane.getProperties().get(PROPERTY_MAINCONTROLLER));
        }

        @Override
        protected void succeeded() {
            super.succeeded();
            UserDAO user = getValue();
            if (null == user) {
                onNotSucceeded.accept(null);
            } else {
                HostServices services = currentApp.getHostServices();
                String logUri = services.resolveURI(services.getCodeBase(), "log.txt");
                LOG.info(String.format("Loggin login timestamp to %s", logUri));
                try (FileWriter writer = new FileWriter(new File(new URL(logUri).toURI()), true)) {
                    try (PrintWriter pw = new PrintWriter(writer)) {
                        pw.printf("[%s]: %s logged in.", DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now()), user.getUserName());
                        pw.println();
                        pw.flush();
                        writer.flush();
                    }
                } catch (IOException | URISyntaxException ex) {
                    LOG.log(Level.SEVERE, "Error writing to log", ex);
                }
                unbindExtents(loginView);
                mainPane.getChildren().remove(loginView);
                mainController.replaceContent(new Overview());
            }
        }

        @Override
        protected void failed() {
            super.failed();
            onNotSucceeded.accept(getException());
        }

        @Override
        protected UserDAO call() throws Exception {
            Optional<UserDAO> result;
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
            try (DbConnector dbConnector = new DbConnector()) {
                LOG.log(Level.INFO, String.format("Looking up %s", userName));
                Platform.runLater(() -> updateMessage(AppResources.getResourceString(RESOURCEKEY_CONNECTEDTODB)));
                result = UserDAO.getFactory().findByUserName(dbConnector.getConnection(), userName);
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
                    LOG.log(Level.WARNING, "No matching userName found");
                }
            }
            return null;
        }

    }

}
