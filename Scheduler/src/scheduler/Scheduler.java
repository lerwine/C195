package scheduler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import static scheduler.AppResourceKeys.RESOURCEKEY_CONNECTEDTODB;
import static scheduler.AppResourceKeys.RESOURCEKEY_CONNECTINGTODB;
import static scheduler.AppResourceKeys.RESOURCEKEY_LOGGINGIN;
import scheduler.dao.UserDAO;
import scheduler.util.DbConnector;
import scheduler.util.EventHelper;
import scheduler.util.PwHash;
import scheduler.util.ViewControllerLoader;
import scheduler.view.Login;
import scheduler.view.MainController;
import scheduler.view.ViewAndController;
import scheduler.view.event.FxmlViewControllerEvent;
import scheduler.view.event.FxmlViewEventType;
import scheduler.view.task.TaskWaiter;

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

    private static UserDAO currentUser = null;
    private static final String PROPERTY_MAINCONTROLLER = "scheduler.view.MainController";

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

    public static MainController getMainController(Scene scene) {
        Parent root = scene.getRoot();
        if (null != root && root instanceof Pane) {
            ObservableMap<Object, Object> properties = root.getProperties();
            if (properties.containsKey(PROPERTY_MAINCONTROLLER)) {
                Object controller = properties.get(PROPERTY_MAINCONTROLLER);
                if (null != controller && controller instanceof MainController) {
                    return (MainController) controller;
                }
            }
        }
        Window window = scene.getWindow();
        if (null != window) {
            Window owner = ((Stage) window).getOwner();
            if (null != owner && null != (scene = owner.getScene())) {
                return getMainController(scene);
            }
        }
        throw new IllegalStateException("Cannot find main controller");
    }

    public static MainController getMainController(Node node) {
        Scene scene = node.getScene();
        if (null != scene) {
            return getMainController(scene);
        }
        throw new IllegalStateException("Node is not part of a Scene");
    }

    /**
     * Looks up a user from the database and sets the current logged in user for the application if the password hash matches.
     *
     * @param stage The stage
     * @param logPath The path for login logging.
     * @param loginView The view for the login.
     * @param userName The login name for the user to look up.
     * @param password The raw password provided by the user.
     * @param onNotSucceeded Handles login failures. The {@link Exception} argument will be null if there were no exceptions and either the login was
     * not found or the password hash did not match.
     */
    public static void tryLoginUser(Stage stage, String logPath, BorderPane loginView, String userName, String password, Consumer<Throwable> onNotSucceeded) {
        TaskWaiter.startNow(new LoginTask(stage, logPath, loginView, userName, password, onNotSucceeded));
    }

    @Override
    public void start(Stage stage) throws Exception {
        // Load login view and controller.
        ViewAndController<BorderPane, Login> loginViewAndController = ViewControllerLoader.loadViewAndController(Login.class);
        // Store log path
        HostServices services = getHostServices();
        loginViewAndController.getController().setLogPath(services.resolveURI(services.getDocumentBase(), "log.txt"));
        
        // Load main view and controller
        ViewAndController<StackPane, MainController> mainViewAndController = ViewControllerLoader.loadViewAndController(MainController.class);
        StackPane mainView = mainViewAndController.getView();
        MainController mainController = mainViewAndController.getController();
        
        // Bind extents of login view to main view extents.
        loginViewAndController.getView().setPrefSize(mainView.getPrefWidth(), mainView.getPrefHeight());
        loginViewAndController.getView().setMaxSize(mainView.getMaxWidth(), mainView.getMaxHeight());
        
        EventHelper.fireFxmlViewEvent(loginViewAndController.getController(),
                loginViewAndController.toEvent(this, FxmlViewEventType.LOADED, stage));
        stage.setScene(new Scene(mainView));
        mainView.getProperties().put(PROPERTY_MAINCONTROLLER, mainController);
        ObservableList<Node> children = mainView.getChildren();
        BorderPane loginView = loginViewAndController.getView();
        children.add(loginViewAndController.getView());
        loginView.minWidthProperty().bind(mainView.widthProperty());
        loginView.prefWidthProperty().bind(mainView.widthProperty());
        loginView.minHeightProperty().bind(mainView.heightProperty());
        loginView.prefHeightProperty().bind(mainView.heightProperty());
        EventHelper.fireFxmlViewEvent(loginViewAndController.getController(),
                loginViewAndController.toEvent(this, FxmlViewEventType.BEFORE_SHOW, stage));
        stage.setOnHidden((event) -> {
            EventHelper.fireFxmlViewEvent(mainController, mainViewAndController.toEvent(this, FxmlViewEventType.UNLOADED, stage));
        });
        stage.show();
        EventHelper.fireFxmlViewEvent(loginViewAndController.getController(),
                loginViewAndController.toEvent(this, FxmlViewEventType.SHOWN, stage));
    }

    @Override
    public void stop() throws Exception {
        DbConnector.forceCloseAll();
        super.stop();
    }

    private static class LoginTask extends TaskWaiter<UserDAO> {

        private final String userName, password;
        private final Consumer<Throwable> onNotSucceeded;
        private final MainController mainController;
        private final StackPane mainPane;
        private final BorderPane loginView;
        private final String logPath;

        LoginTask(Stage stage, String logPath, BorderPane loginView, String userName, String password, Consumer<Throwable> onNotSucceeded) {
            super(stage, AppResources.getResourceString(RESOURCEKEY_CONNECTINGTODB), AppResources.getResourceString(RESOURCEKEY_LOGGINGIN));
            this.logPath = Objects.requireNonNull(logPath);
            this.loginView = Objects.requireNonNull(loginView);
            this.userName = Objects.requireNonNull(userName);
            this.password = Objects.requireNonNull(password);
            mainPane = (StackPane) stage.getScene().getRoot();
            mainController = (MainController) (mainPane.getProperties().get(PROPERTY_MAINCONTROLLER));
            this.onNotSucceeded = onNotSucceeded;
        }

        @Override
        protected void processResult(UserDAO user, Stage owner) {
            if (null == user) {
                if (null != onNotSucceeded) {
                    onNotSucceeded.accept(null);
                }
            } else {
                
                try (FileWriter writer = new FileWriter(new File(new URL(logPath).toURI()), true)) {
                    try(PrintWriter pw = new PrintWriter(writer)) {
                        pw.printf("[%s]: %s logged in.", DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now()), user.getUserName());
                        pw.println();
                        pw.flush();
                        writer.flush();
                    }
                } catch (IOException | URISyntaxException ex) {
                    Logger.getLogger(Scheduler.class.getName()).log(Level.SEVERE, "Error writing to log", ex);
                }
                EventHelper.fireFxmlViewEvent(mainController,
                        new FxmlViewControllerEvent<>(this, FxmlViewEventType.LOADED, mainPane, mainController, owner));
                loginView.minWidthProperty().unbind();
                loginView.prefWidthProperty().unbind();
                loginView.minHeightProperty().unbind();
                loginView.prefHeightProperty().unbind();
                mainPane.getChildren().remove(loginView);
                EventHelper.fireFxmlViewEvent(mainController,
                        new FxmlViewControllerEvent<>(this, FxmlViewEventType.BEFORE_SHOW, mainPane, mainController, owner));
                EventHelper.fireFxmlViewEvent(mainController,
                        new FxmlViewControllerEvent<>(this, FxmlViewEventType.SHOWN, mainPane, mainController, owner));
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
                LOG.log(Level.WARNING, "No matching userName found");
            }
            return null;
        }

    }

}
