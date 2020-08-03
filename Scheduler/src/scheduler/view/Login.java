package scheduler.view;

import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import scheduler.AppResources;
import scheduler.Scheduler;
import scheduler.SupportedLocale;
import scheduler.util.AlertHelper;
import scheduler.util.LogHelper;
import scheduler.util.NodeUtil;
import static scheduler.util.NodeUtil.clearAndSelect;
import scheduler.util.ParentWindowChangeListener;
import scheduler.util.ResourceBundleHelper;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;

/**
 * FXML Controller class for the application login screen.
 * <p>
 * The associated view is {@code /resources/scheduler/view/Login.fxml}.</p>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/view/Login")
@FXMLResource("/scheduler/view/Login.fxml")
public final class Login extends Scheduler.LoginBorderPane {

    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(Login.class.getName()), Level.FINE);
//    private static final Logger LOG = Logger.getLogger(Login.class.getName());

    public static final String RESOURCEKEY_APPOINTMENTSCHEDULERLOGIN = "appointmentSchedulerLogin";
    public static final String RESOURCEKEY_DBACCESSERROR = "dbAccessError";
    public static final String RESOURCEKEY_EXIT = "exit";
    public static final String RESOURCEKEY_INVALIDCREDENTIALS = "invalidCredentials";
    public static final String RESOURCEKEY_LOGIN = "login";
    public static final String RESOURCEKEY_LOGINERROR = "loginError";
    public static final String RESOURCEKEY_PASSWORD = "password";
    public static final String RESOURCEKEY_USERNAME = "userName";
    public static final String RESOURCEKEY_VALIDATIONERROR = "validationError";
    public static final String RESOURCEKEY_EMPTYUSERNAME = "emptyUserName";
    public static final String RESOURCEKEY_EMPTYPASSWORD = "emptyPassword";

    private final ParentWindowChangeListener stageChangeHandler;
    private ObjectBinding<SupportedLocale> selectedLanguage;

    // Currently selected resource bundle
    private ObjectBinding<ResourceBundle> resourceBundle;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // fx:id="languageComboBox"
    private ComboBox<SupportedLocale> languageComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="userNameLabel"
    private Label userNameLabel; // Value injected by FXMLLoader

    @FXML // fx:id="userNameTextField"
    private TextField userNameTextField; // Value injected by FXMLLoader

    @FXML // fx:id="userNameValidationLabel"
    private Label userNameValidationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="passwordLabel"
    private Label passwordLabel; // Value injected by FXMLLoader

    @FXML // fx:id="passwordField"
    private PasswordField passwordField; // Value injected by FXMLLoader

    @FXML // fx:id="passwordValidationLabel"
    private Label passwordValidationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="loginButton"
    private Button loginButton; // Value injected by FXMLLoader

    @FXML // fx:id="exitButton"
    private Button exitButton; // Value injected by FXMLLoader
    private BooleanBinding userNameValid;
    private BooleanBinding passwordValid;

    public Login() {
        stageChangeHandler = new ParentWindowChangeListener(sceneProperty());
    }

    @FXML
    private void onExitButtonAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onExitButtonAction", event);
        getScene().getWindow().hide();
        LOG.exiting(LOG.getName(), "onExitButtonAction");
    }

    @FXML
    private void onLoginButtonAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onLoginButtonAction", event);
        tryLoginUser(this, userNameTextField.getText(), passwordField.getText());
        LOG.exiting(LOG.getName(), "onLoginButtonAction");
    }

    @Override
    protected SupportedLocale getSelectedLanguage() {
        return selectedLanguage.get();
    }

    private ObjectBinding<SupportedLocale> initializeLanguageBindings() {
        LOG.entering(LOG.getName(), "initializeLanguageBindings");
        selectedLanguage = Bindings.select(languageComboBox.selectionModelProperty(), "selectedItem");
        // Create binding which returns a resource bundle for the selected language, or the resource bundle loaded with the controller if no language
        // is selected in the languageComboBox.
        resourceBundle = Bindings.createObjectBinding(() -> {
            LOG.entering(LogHelper.toLambdaSourceClass(LOG, "initializeLanguageBindings", "resourceBundle"), "computeValue");
            // The dependent binding (selectedLanguage) returns the currently selected item from languageComboBox.
            SupportedLocale l = selectedLanguage.get();
            if (null == l) {
                // If nothing is selected in the languageComboBox, then we'll return the ResourceBundle that was loaded with the current
                // custom control (Login).
                return resources;
            }
            // Load the resource bundle for the selected locale.
            ResourceBundle result = ResourceBundleHelper.getBundle(Login.class, l.getLocale());
            LOG.exiting(LogHelper.toLambdaSourceClass(LOG, "initializeLanguageBindings", "resourceBundle"), "computeValue", result);
            return result;
        }, selectedLanguage);

        // Add a listener so we can update the Stage title right away when the language changes.
        resourceBundle.addListener((observable, oldValue, newValue) -> onResourceBundleChanged(newValue));

        // Bind the text property of userNameLabel to a StringBinding that returns the label text in the currently selected language.
        userNameLabel.textProperty().bind(Bindings.createStringBinding(() -> {
            LOG.entering(LogHelper.toLambdaSourceClass(LOG, "initializeLanguageBindings", "userNameLabel#text"), "computeValue");
            LOG.finer("Calculating text for userNameLabel");
            String result = resourceBundle.get().getString(RESOURCEKEY_USERNAME);
            LOG.exiting(LogHelper.toLambdaSourceClass(LOG, "initializeLanguageBindings", "userNameLabel#text"), "computeValue", result);
            return result;
        }, resourceBundle));
        passwordLabel.textProperty().bind(Bindings.createStringBinding(() -> {
            LOG.entering(LogHelper.toLambdaSourceClass(LOG, "initializeLanguageBindings", "passwordLabel#text"), "computeValue");
            return resourceBundle.get().getString(RESOURCEKEY_PASSWORD);
        }, resourceBundle));
        loginButton.textProperty().bind(Bindings.createStringBinding(() -> {
            LOG.entering(LogHelper.toLambdaSourceClass(LOG, "initializeLanguageBindings", "loginButton#text"), "computeValue");
            String result = resourceBundle.get().getString(RESOURCEKEY_LOGIN);
            LOG.exiting(LogHelper.toLambdaSourceClass(LOG, "initializeLanguageBindings", "loginButton#text"), "computeValue", result);
            return result;
        }, resourceBundle));
        exitButton.textProperty().bind(Bindings.createStringBinding(() -> {
            LOG.entering(LogHelper.toLambdaSourceClass(LOG, "initializeLanguageBindings", "exitButton#text"), "computeValue");
            String result = resourceBundle.get().getString(RESOURCEKEY_EXIT);
            LOG.exiting(LogHelper.toLambdaSourceClass(LOG, "initializeLanguageBindings", "exitButton#text"), "computeValue", result);
            return result;
        }, resourceBundle));

        LOG.exiting(LOG.getName(), "initializeLanguageBindings", selectedLanguage);
        return selectedLanguage;
    }

    private void onResourceBundleChanged(ResourceBundle newValue) {
        LOG.entering(LOG.getName(), "onResourceBundleChanged", newValue);
        Stage stage = stageChangeHandler.getCurrentStage();
        if (null != stage) {
            LOG.finer("Setting stage title");
            stage.setTitle(newValue.getString(RESOURCEKEY_APPOINTMENTSCHEDULERLOGIN));
        }
        LOG.exiting(LOG.getName(), "onResourceBundleChanged");
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    private void initialize() {
        LOG.entering(LOG.getName(), "initialize");
        assert languageComboBox != null : "fx:id=\"languageComboBox\" was not injected: check your FXML file 'Login.fxml'.";
        assert userNameLabel != null : "fx:id=\"userNameLabel\" was not injected: check your FXML file 'Login.fxml'.";
        assert userNameTextField != null : "fx:id=\"userNameTextField\" was not injected: check your FXML file 'Login.fxml'.";
        assert userNameValidationLabel != null : "fx:id=\"userNameValidationLabel\" was not injected: check your FXML file 'Login.fxml'.";
        assert passwordLabel != null : "fx:id=\"passwordLabel\" was not injected: check your FXML file 'Login.fxml'.";
        assert passwordField != null : "fx:id=\"passwordField\" was not injected: check your FXML file 'Login.fxml'.";
        assert passwordValidationLabel != null : "fx:id=\"passwordValidationLabel\" was not injected: check your FXML file 'Login.fxml'.";
        assert loginButton != null : "fx:id=\"loginButton\" was not injected: check your FXML file 'Login.fxml'.";
        assert exitButton != null : "fx:id=\"exitButton\" was not injected: check your FXML file 'Login.fxml'.";

        stageChangeHandler.currentStageProperty().addListener((observable, oldValue, newValue) -> {
            LOG.entering(LogHelper.toLambdaSourceClass(LOG, "new", "stageChangeHandler#currentStage"), "change", new Object[]{oldValue, newValue});
            if (null != newValue && null != resourceBundle) {
                LOG.fine("Setting stage title");
                newValue.setTitle(resourceBundle.get().getString(RESOURCEKEY_APPOINTMENTSCHEDULERLOGIN));
            }
            LOG.exiting(LogHelper.toLambdaSourceClass(LOG, "new", "stageChangeHandler#currentStage"), "change");
        });

        ObservableList<SupportedLocale> languages = FXCollections.observableArrayList(SupportedLocale.values());
        languageComboBox.setItems(languages);
        clearAndSelect(languageComboBox, AppResources.getCurrentLocale());

        selectedLanguage = initializeLanguageBindings();

        userNameValid = Bindings.createBooleanBinding(() -> {
            LOG.entering(LogHelper.toLambdaSourceClass(LOG, "initialize", "userNameValid"), "computeValue");
            String s = userNameTextField.getText();
            boolean result = null != s && !s.trim().isEmpty();
            LOG.exiting(LogHelper.toLambdaSourceClass(LOG, "initialize", "userNameValid"), "computeValue", result);
            return result;
        }, userNameTextField.textProperty());

        userNameValidationLabel.textProperty().bind(Bindings.when(userNameValid).then("").otherwise(Bindings.createStringBinding(() -> {
            LOG.entering(LogHelper.toLambdaSourceClass(LOG, "initialize", "userNameValidationLabel#text"), "computeValue");
            String result = resourceBundle.get().getString(RESOURCEKEY_EMPTYUSERNAME);
            LOG.exiting(LogHelper.toLambdaSourceClass(LOG, "initialize", "userNameValidationLabel#text"), "computeValue", result);
            return result;
        }, resourceBundle)));

        NodeUtil.bindCssCollapse(userNameValidationLabel, userNameValid);

        passwordValid = Bindings.createBooleanBinding(() -> {
            LOG.entering(LogHelper.toLambdaSourceClass(LOG, "initialize", "passwordValid"), "computeValue");
            String s = passwordField.getText();
            boolean result = null != s && !s.trim().isEmpty();
            LOG.exiting(LogHelper.toLambdaSourceClass(LOG, "initialize", "passwordValid"), "computeValue", result);
            return result;
        }, passwordField.textProperty());

        passwordValidationLabel.textProperty().bind(Bindings.when(passwordValid).then("").otherwise(Bindings.createStringBinding(() -> {
            LOG.entering(LogHelper.toLambdaSourceClass(LOG, "initialize", "passwordValidationLabel#text"), "computeValue");
            String result = resourceBundle.get().getString(RESOURCEKEY_EMPTYPASSWORD);
            LOG.exiting(LogHelper.toLambdaSourceClass(LOG, "initialize", "passwordValidationLabel#text"), "computeValue", result);
            return result;
        }, resourceBundle)));

        NodeUtil.bindCssCollapse(passwordValidationLabel, passwordValid);

        loginButton.disableProperty().bind(userNameValid.and(passwordValid).not().or(selectedLanguage.isNull()));
        LOG.exiting(LOG.getName(), "initialize");
    }

    @Override
    protected void onLoginFailure() {
        ResourceBundle rb = resourceBundle.get();
        AlertHelper.showErrorAlert(getScene().getWindow(), rb.getString(RESOURCEKEY_LOGINERROR), rb.getString(RESOURCEKEY_INVALIDCREDENTIALS));
    }

}
