package scheduler;

import com.mysql.jdbc.Connection;
import controller.EditAppointmentController;
import controller.LoginScreenController;
import java.io.IOException;
import java.sql.SQLException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.db.UserRow;

/**
 * Application class for Scheduler
 * @author Leonard T. Erwine
 */
public class App extends Application {
    
    private static App current;
    
    public static App getCurrent() { return current; }
    
    private StageManager rootStageManager;
    
    public StageManager getRootStageManager() { return rootStageManager; }
    
    private final Logger logger;
    
    public App() {
        logger = Logger.getLogger(App.class.getName());
    }
    
    //<editor-fold defaultstate="collapsed" desc="App Lifecycle Members">
    
    /**
     * The app main entry point.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage stage) throws Exception {
        current = this;
        rootStageManager = new StageManager(stage);
        rootStageManager.root.set(true);
        // Store the original locale settings so they can be restored when app ends
        originalDisplayLocale = Locale.getDefault(Locale.Category.DISPLAY);
        originalFormatLocale = Locale.getDefault(Locale.Category.FORMAT);
        
        // Ensure app config is freshly loaded.
        AppConfig.refresh();
        
        // Get IDs languages supported by the app
        String[] languageIds = AppConfig.getLanguages();
        
        // Attempt to find a match for the current display language amongst the languages supported by the app.
        final String lt = originalDisplayLocale.toLanguageTag();
        // First look for one that is an exact match with the language tag.
        Optional<String> cl = Arrays.stream(languageIds).filter((String id) -> id.equals(lt)).findFirst();
        if (!cl.isPresent()) {
            // Look for one that matches the ISO3 language code.
            final String iso3 = originalDisplayLocale.getISO3Language();
            cl = Arrays.stream(languageIds).filter((String id) -> id.equals(iso3)).findFirst();
            if (!cl.isPresent()) {
                // Look for one that matches the ISO2 language code.
                final String ln = originalDisplayLocale.getLanguage();
                cl = Arrays.stream(languageIds).filter((String id) -> id.equals(ln)).findFirst();
            }
        }
        
        // Populate list of Locale objects.
        ObservableList<Locale> languages = FXCollections.observableArrayList();
        Locale toSelect;
        if (cl.isPresent()) {
            for (String n : AppConfig.getLanguages())
                languages.add((n.equals(cl.get())) ? originalDisplayLocale : new Locale(n));
            toSelect = originalDisplayLocale;
        } else {
            for (String n : AppConfig.getLanguages())
                languages.add(new Locale(n));
            toSelect = languages.get(0);
        }
        
        // Make language Locale list read-only.
        allLanguages = FXCollections.unmodifiableObservableList(languages);
        
        setCurrentLocale(toSelect);
        
        // Set initial scene to the login screen
        LoginScreenController.setCurrentScene(rootStageManager);
    }
    
    @Override
    public void stop() throws Exception {
        SqlConnectionDependency.forceClose();
        // Resotre original locale settings
        Locale.setDefault(Locale.Category.DISPLAY, originalDisplayLocale);
        Locale.setDefault(Locale.Category.FORMAT, originalFormatLocale);
        super.stop();
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Globalization Members">
    
    private ObservableList<Locale> allLanguages;
    
    public ObservableList<Locale> getAllLanguages() { return allLanguages; }
    
    // Tracks the original locale settings at the time the app is started, so it can be restored when the app ends
    private Locale originalDisplayLocale;
    private Locale originalFormatLocale;
    
    /**
     * The name of the general application globalization resource bundle.
     */
    public static final String RESOURCE_NAME = "globalization/app";
    
    private ResourceBundle appResourceBundle;
    
    /**
     * Gets the current "app" resource bundle for the current language.
     * @return The current "app" resource bundle for the current language.
     */
    public ResourceBundle getAppResourceBundle() { return appResourceBundle; }
    
    /**
     * Gets a 'File "%s" not found' formatted message in the current language.
     * @param fileName - The name of the file to place into the message.
     * @return The formatted 'File "%s" not found' message in the current language.
     */
    public String getFileNotFoundMessage(String fileName) {
        return String.format(getAppResourceBundle().getString("fileNotFound"), fileName);
    }
    
    // This contains the current locale.
    private Locale currentLocale;
    // These next 4 are locale-specific formatters for date and/or time strings.
    private DateTimeFormatter shortDateTimeFormatter;
    private DateTimeFormatter fullDateTimeFormatter;
    private DateTimeFormatter fullDateFormatter;
    private DateTimeFormatter fullTimeFormatter;

    /**
     * Gets the current {@link Locale}.
     * @return The current {@link Locale}.
     */
    public Locale getCurrentLocale() { return currentLocale; }
    
    private HashMap<String, String> appointmentTypes;
    
    /**
     * Gets the human-readable, locale-specific display text for the specified appointment type code.
     * @param code The appointment type code that is stored in the database.
     * @return The current locale-specific formatter for full time strings.
     */
    public String getAppointmentTypeDisplay(String code) {
        if (code == null || (code = code.trim()).isEmpty())
            return "";
        String lc = code.toLowerCase();
        return (appointmentTypes.containsKey(lc)) ? appointmentTypes.get(lc) : code;
    }

    /**
     * Sets the current {@link Locale} and initializes app information that contains locale-specific information.
     * @param locale The new app {@link Locale}.
     */
    public void setCurrentLocale(Locale locale) {
        // Set default locale so all controls will be displayed appropriate for the current locale.
        Locale.setDefault(Locale.Category.DISPLAY, locale);
        Locale.setDefault(Locale.Category.FORMAT, locale);
        // Save the new locale.
        currentLocale = locale;
        appResourceBundle = ResourceBundle.getBundle(RESOURCE_NAME, locale);
        fullTimeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.FULL).withLocale(locale).withZone(ZoneId.systemDefault());
        fullDateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(locale).withZone(ZoneId.systemDefault());
        shortDateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withLocale(locale).withZone(ZoneId.systemDefault());
        fullDateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL).withLocale(locale).withZone(ZoneId.systemDefault());
        appointmentTypes = new HashMap<>();
        Stream.of(EditAppointmentController.APPOINTMENT_CODE_PHONE, EditAppointmentController.APPOINTMENT_CODE_VIRTUAL, EditAppointmentController.APPOINTMENT_CODE_CUSTOMER,
                EditAppointmentController.APPOINTMENT_CODE_HOME, EditAppointmentController.APPOINTMENT_CODE_GERMANY, EditAppointmentController.APPOINTMENT_CODE_INDIA,
                EditAppointmentController.APPOINTMENT_CODE_HONDURAS, EditAppointmentController.APPOINTMENT_CODE_OTHER).forEach((String key) -> {
            appointmentTypes.put(key, appResourceBundle.getString("appointmentType_" + key));
        });
    }
    
    /**
     * Gets the current locale-specific formatter for full time strings.
     * @return  The current locale-specific formatter for full time strings.
     */
    public DateTimeFormatter getFullTimeFormatter() { return fullTimeFormatter; }
    
    /**
     * Gets the current locale-specific formatter for full date strings.
     * @return The current locale-specific formatter for full date strings.
     */
    public DateTimeFormatter getFullDateFormatter() { return fullDateFormatter; }
    
    /**
     * Gets the current locale-specific formatter for short date/time strings.
     * @return The current locale-specific formatter for short date/time strings.
     */
    public DateTimeFormatter getShortDateTimeFormatter() { return shortDateTimeFormatter; }
    
    /**
     * Gets the current locale-specific formatter for full date/time strings.
     * @return The current locale-specific formatter for full date/time strings.
     */
    public DateTimeFormatter getFullDateTimeFormatter() { return fullDateTimeFormatter; }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Members for tracking the current User">
    
    /**
     * Stores the currently logged in user.
     */
    private Optional<UserRow> currentUser = Optional.empty();
    
    /**
     * Gets the currently logged in user.
     * @return The currently logged in user.
     */
    public Optional<UserRow> getCurrentUser() { return currentUser; }
    
    /**
     * Sets the currently logged in user if a user name and password match.
     * @param userName The user's login name.
     * @param password The user's actual password (not password hash).
     * @return {@code true} if a matching {@link UserRow#userName} is found, and the password hash matches the stored value; otherwise {@code false}.
     * @throws InvalidOperationException
     * @throws java.sql.SQLException
     */
    public boolean tryLoginUser(String userName, String password) throws InvalidOperationException, SQLException {
        Optional<UserRow> user = SqlConnectionDependency.get((Connection connection) -> {
            try {
                return UserRow.getByUserName(connection, userName);
            } catch (SQLException ex) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
                throw new RuntimeException("Error getting user by username", ex);
            }
        });
        if (user.isPresent()) {
            // The password string stored in the database is a base-64 string that contains a cryptographic hash of the password
            // along with the cryptographic seed. A hash will be created from password argument using the same cryptographic seed
            // as the stored password. If the password is correct, then the hash values will match.
            if (user.get().getPasswordHash().test(password)) {
                currentUser = user;
                return true;
            }
        } else
            logger.log(Level.WARNING, "No matching userName found");
        return false;
    }
    
    //</editor-fold>
    
    public class StageManager {
        private final ReadOnlyBooleanWrapper root;

        public boolean isRoot() { return root.get(); }

        public ReadOnlyBooleanProperty rootProperty() { return root.getReadOnlyProperty(); }
        
        private final ReadOnlyObjectWrapper<Stage> stage;

        public Stage getStage() { return stage.get(); }

        public ReadOnlyObjectProperty<Stage> stageProperty() { return stage.getReadOnlyProperty(); }
        
        private final StringProperty windowTitle;

        public String getWindowTitle() { return windowTitle.get(); }

        public void setWindowTitle(String value) { windowTitle.set(value); }

        public StringProperty windowTitleProperty() { return windowTitle; }
        
        public StageManager(Stage stage) {
            windowTitle = new SimpleStringProperty();
            stage.titleProperty().bindBidirectional(windowTitle);
            root = new ReadOnlyBooleanWrapper(false);
            this.stage = new ReadOnlyObjectWrapper<>(stage);
        }

        //<editor-fold defaultstate="collapsed" desc="Overloaded setScene methods">

        /**
         * Sets the {@link javafx.scene.Scene} for the specified {@link javafx.stage.Stage} from the FXML resource at a given path
         * and sets up its controller.
         * @param <T> The type of controller to set up.
         * @param path The path of the FXML resource to load.
         * @param bundleName The base name of the resource bundle to load.
         * @param setupController The delegate method that sets up the controller.
         */
        public <T> void setScene(String path, String bundleName, java.util.function.BiConsumer<ResourceBundle, T> setupController) {
            setSceneWithControllerFactory(path, bundleName, null, setupController);
        }

        /**
         * Sets the {@link javafx.scene.Scene} for the specified {@link javafx.stage.Stage} from the FXML resource at a given path
         * and sets up its controller.
         * @param <T> The type of controller to set up.
         * @param path The path of the FXML resource to load.
         * @param bundleName The base name of the resource bundle to load.
         * @param controllerFactory Creates the controller.
         * @param setupController The delegate method that sets up the controller.
         */
        public <T> void setSceneWithControllerFactory(String path, String bundleName, Callback<Class<?>, Object> controllerFactory, java.util.function.BiConsumer<ResourceBundle, T> setupController) {
            ResourceBundle rb = (bundleName == null || bundleName.trim().isEmpty()) ? getAppResourceBundle() :
                ResourceBundle.getBundle(bundleName, getCurrentLocale());
            // Create new FXML loader with the resource path URL of the new fxml page and the resource bundle for the current language.
            FXMLLoader loader = new FXMLLoader(App.class.getResource(path), rb, null, controllerFactory);

            try {
                stage.get().setScene(new Scene(loader.load()));
            } catch (IOException ex) {
                logger.log(Level.SEVERE, null, ex);
                return;
            }

            // Call the consumer to for controller setup.
            setupController.accept(rb, loader.getController());

            stage.get().show();
        }

        /**
         * Sets the {@link javafx.scene.Scene} for the specified {@link javafx.stage.Stage} from the FXML resource at a given path
         * and sets up its controller.
         * @param <T> The type of controller to set up.
         * @param path The path of the FXML resource to load.
         * @param bundleName The base name of the resource bundle to load.
         * @param setupController The delegate method that sets up the controller.
         */
        public <T> void setScene(String path, String bundleName, java.util.function.Consumer<T> setupController) {
            setSceneWithControllerFactory(path, bundleName, null, setupController);
        }

        /**
         * Sets the {@link javafx.scene.Scene} for the specified {@link javafx.stage.Stage} from the FXML resource at a given path
         * and sets up its controller.
         * @param <T> The type of controller to set up.
         * @param path The path of the FXML resource to load.
         * @param bundleName The base name of the resource bundle to load.
         * @param controllerFactory Creates the controller.
         * @param setupController The delegate method that sets up the controller.
         */
        public <T> void setSceneWithControllerFactory(String path, String bundleName, Callback<Class<?>, Object> controllerFactory, java.util.function.Consumer<T> setupController) {
            ResourceBundle rb = (bundleName == null || bundleName.trim().isEmpty()) ? getAppResourceBundle() :
                ResourceBundle.getBundle(bundleName, getCurrentLocale());
            // Create new FXML loader with the resource path URL of the new fxml page and the resource bundle for the current language.
            FXMLLoader loader = new FXMLLoader(App.class.getResource(path), rb);

            try {
                stage.get().setScene(new Scene(loader.load()));
            } catch (IOException ex) {
                logger.log(Level.SEVERE, null, ex);
                return;
            }

            // Call the consumer to for controller setup.
            setupController.accept(loader.getController());

            stage.get().show();
        }

        /**
         * Sets the {@link javafx.scene.Scene} for the specified {@link javafx.stage.Stage} from the FXML resource at a given path.
         * @param path The path of the FXML resource to load.
         * @param bundleName The base name of the resource bundle to load.
         */
        public void setScene(String path, String bundleName) {
            setSceneWithControllerFactory(path, bundleName, null);
        }

        /**
         * Sets the {@link javafx.scene.Scene} for the specified {@link javafx.stage.Stage} from the FXML resource at a given path.
         * @param path The path of the FXML resource to load.
         * @param bundleName The base name of the resource bundle to load.
         * @param controllerFactory Creates the controller.
         */
        public void setSceneWithControllerFactory(String path, String bundleName, Callback<Class<?>, Object> controllerFactory) {
            // Create new FXML loader with the resource path URL of the new fxml page and the resource bundle for the current language.
            FXMLLoader loader = new FXMLLoader(App.class.getResource(path),
                    (bundleName == null || bundleName.trim().isEmpty()) ? getAppResourceBundle() : ResourceBundle.getBundle(bundleName, getCurrentLocale()),
                    null, controllerFactory);

            try {
                stage.get().setScene(new Scene(loader.load()));
            } catch (IOException ex) {
                logger.log(Level.SEVERE, null, ex);
                return;
            }

            stage.get().show();
        }

        //</editor-fold>
    }
}
