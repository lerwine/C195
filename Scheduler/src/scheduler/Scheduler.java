package scheduler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import static scheduler.AppResourceKeys.RESOURCEKEY_CONNECTEDTODB;
import static scheduler.AppResourceKeys.RESOURCEKEY_LOGGINGIN;
import scheduler.dao.UserDAO;
import scheduler.model.PredefinedData;
import scheduler.util.DbConnector;
import scheduler.util.LogHelper;
import static scheduler.util.NodeUtil.bindExtents;
import static scheduler.util.NodeUtil.unbindExtents;
import scheduler.util.PwHash;
import scheduler.util.StageManager;
import scheduler.util.ViewControllerLoader;
import scheduler.view.Login;
import scheduler.view.MainController;
import scheduler.view.Overview;
import scheduler.view.ViewAndController;
import scheduler.view.task.WaitBorderPane;

/**
 * Main Application class for the Scheduler application.
 * <p>
 * Upon startup, {@link MainController} is loaded as the root node a the {@link Scene} of the primary {@link Stage}. It will not be completely initialized until the consultant is
 * successfully logged in. The {@link Login} custom control is appended as the last child node of the view for the main controller, which masks the entire window until the login is
 * successful. To validate credentials, the {@link Login} control invokes
 * {@link LoginBorderPane#tryLoginUser(scheduler.Scheduler.LoginBorderPane, java.lang.String, java.lang.String)}. After successful authentication, the current consultant data
 * object is stored in {@link Scheduler#currentUser}, the {@link Login} control is removed, and the {@link MainController} is completed.</p>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public final class Scheduler extends Application {

    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(Scheduler.class.getName()), Level.FINE);
    private static final String LOG_FILE_PATH = "log.txt";
    private static Scheduler currentApp = null;
    private static UserDAO currentUser = null;

    /**
     * Gets the currently logged in consultant.
     *
     * @return The {@link UserDAO} object representing the currently logged in consultant.
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
        if (null != app && null != app.mainController) {
            return app.mainController;
        }
        throw new IllegalStateException();
    }

    /**
     * The application main entry point.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    private MainController mainController;

    @Override
    public void init() throws Exception {
        currentApp = this;
        super.init();
    }

    @Override
    public void start(Stage stage) throws Exception {
        StageManager.setPrimaryStage(stage);
        stage.setTitle(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_APPOINTMENTSCHEDULER));
        StackPane stackPane = new StackPane();
        stackPane.setMaxHeight(Region.USE_PREF_SIZE);
        stackPane.setMaxWidth(Region.USE_PREF_SIZE);
        stackPane.setMinHeight(Region.USE_PREF_SIZE);
        stackPane.setMinWidth(Region.USE_PREF_SIZE);
        stackPane.setPrefHeight(600.0);
        stackPane.setPrefWidth(900.0);
        stackPane.getStylesheets().add("/scheduler/defaultStyles.css");
        stage.setScene(new Scene(stackPane));
//        // Load main view and controller
//        ViewAndController<StackPane, MainController> mainViewAndController = ViewControllerLoader.loadViewAndController(MainController.class);
//        StackPane mainView = mainViewAndController.getView();
//        mainController = mainViewAndController.getController();

        // Add login view under root of scene.
        Login login = new Login();
        try {
            ViewControllerLoader.initializeCustomControl(login);
        } catch (IOException ex) {
            throw new InternalError("Error loading view", ex);
        }
        // Bind extents of login view to main view extents.
        login.setPrefSize(stackPane.getPrefWidth(), stackPane.getPrefHeight());
        ObservableList<Node> children = stackPane.getChildren();
        children.add(login);
        children.add(((LoginBorderPane) login).waitBorderPane);
        bindExtents(login, stackPane);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        if (null != currentUser) {
            // Stop the background timer for appointment alert checking
            AppointmentAlertManager.INSTANCE.stop();

            // Record consultant logout to file
            File logFile = new File(LOG_FILE_PATH);
            LOG.fine(() -> String.format("Logging logout timestamp to %s", logFile.getAbsolutePath()));
            try (FileWriter writer = new FileWriter(logFile, true)) {
                try (PrintWriter pw = new PrintWriter(writer)) {
                    pw.printf("[%s]: %s logged out.%n", DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now()), currentUser.getUserName());
                    pw.flush();
                    writer.flush();
                }
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, "Error writing to log", ex);
            }
        }
        DbConnector.forceCloseAll();
        super.stop();
    }

    public static abstract class LoginBorderPane extends BorderPane {

        private final WaitBorderPane waitBorderPane = new WaitBorderPane();

        /**
         * Looks up a consultant from the database and sets the current logged in consultant for the application if the password hash matches.
         *
         * @param loginView The view for the login.
         * @param userName The login name for the consultant to look up.
         * @param password The raw password provided by the consultant.
         */
        protected void tryLoginUser(LoginBorderPane loginView, String userName, String password) {
            waitBorderPane.startNow(new LoginTask(loginView, userName, password, this::onLoginFailure));
        }

        /**
         * This gets called when a login has failed.
         */
        protected abstract void onLoginFailure();

        protected abstract SupportedLocale getSelectedLanguage();
    }

    // Task that connects to DB in background and validates consultant credials
    private static class LoginTask extends Task<UserDAO> {

        private final String userName, password;
        private final Runnable onNotSucceeded;
        private final LoginBorderPane loginView;

        LoginTask(LoginBorderPane loginView, String userName, String password, Runnable onNotSucceeded) {
            updateTitle(AppResources.getResourceString(RESOURCEKEY_LOGGINGIN));
            this.loginView = Objects.requireNonNull(loginView);
            this.userName = Objects.requireNonNull(userName);
            this.password = Objects.requireNonNull(password);
            this.onNotSucceeded = Objects.requireNonNull(onNotSucceeded);
        }

        @Override
        protected void succeeded() {
            super.succeeded();
            UserDAO user = getValue();
            if (null == user) {
                onNotSucceeded.run();
            } else {
                // Record consultant logout to file
                File logFile = new File(LOG_FILE_PATH);
                LOG.fine(() -> String.format("Logging login timestamp to %s", logFile.getAbsolutePath()));
                try (FileWriter writer = new FileWriter(logFile, true)) {
                    try (PrintWriter pw = new PrintWriter(writer)) {
                        pw.printf("[%s]: %s logged in.%n", DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now()), user.getUserName());
                        pw.flush();
                        writer.flush();
                    }
                } catch (IOException ex) {
                    LOG.log(Level.SEVERE, "Error writing to log", ex);
                }

                unbindExtents(loginView);
                Scene scene = loginView.getScene();

                // Load main view and controller
                ViewAndController<StackPane, MainController> mainViewAndController;
                try {
                    mainViewAndController = ViewControllerLoader.loadViewAndController(MainController.class);
                } catch (IOException ex) {
                    LOG.log(Level.SEVERE, "Error loading main view and controller", ex);
                    return;
                }
                StackPane mainView = mainViewAndController.getView();
                scene.setRoot(mainView);
                currentApp.mainController = mainViewAndController.getController();
                // Update window title
                ((Stage) scene.getWindow()).setTitle(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_APPOINTMENTSCHEDULER));

                // Start the background timer for appointment alert checking
                AppointmentAlertManager.INSTANCE.start();
                // Set the initial content view
                getMainController().replaceContent(Overview.loadIntoMainContent());
            }
        }

        @Override
        protected UserDAO call() throws Exception {
            Optional<UserDAO> result;
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
            try (DbConnector dbConnector = new DbConnector()) {
                LOG.fine(() -> String.format("Looking up %s", userName));
                Platform.runLater(() -> updateMessage(AppResources.getResourceString(RESOURCEKEY_CONNECTEDTODB)));
                result = UserDAO.FACTORY.findByUserName(dbConnector.getConnection(), userName);
                if (result.isPresent()) {
                    // The password string stored in the database is a base-64 string that contains a cryptographic hash of the password
                    // along with the cryptographic seed. A hash will be created from password argument using the same cryptographic seed
                    // as the stored password. If the password is correct, then the hash values will match.
                    PwHash hash = new PwHash(result.get().getPassword(), false);
                    if (hash.test(password)) {
                        LOG.fine("Password matched");
                        AppResources.setCurrentLocale(loginView.getSelectedLanguage());
                        currentUser = result.get();
                        Platform.runLater(() -> updateMessage(AppResources.getResourceString(RESOURCEKEY_LOGGINGIN)));
                        PredefinedData.ensureDatabaseEntries(dbConnector.getConnection());
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
