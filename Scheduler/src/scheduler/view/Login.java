package scheduler.view;

import java.util.ResourceBundle;
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
import javafx.stage.Window;
import scheduler.AppResources;
import scheduler.Scheduler;
import scheduler.SupportedLocale;
import scheduler.util.AlertHelper;
import scheduler.util.NodeUtil;
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

    private static final Logger LOG = Logger.getLogger(Login.class.getName());

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

    public Login() {
        super();
    }

    @FXML
    private void onExitButtonAction(ActionEvent event) {
        LOG.entering(getClass().getName(), "onExitButtonAction", event);
        getScene().getWindow().hide();
    }

    @FXML
    private void onLoginButtonAction(ActionEvent event) {
        LOG.entering(getClass().getName(), "onLoginButtonAction", event);
        tryLoginUser(this, userNameTextField.getText(), passwordField.getText());
    }

    private ObjectBinding<SupportedLocale> initializeLanguageBindings() {
        ObjectBinding<SupportedLocale> selectedLanguage = Bindings.select(languageComboBox.selectionModelProperty(), "selectedItem");
        // Create binding which returns a resource bundle for the selected language, or the resource bundle loaded with the controller if no language
        // is selected in the languageComboBox.
        resourceBundle = Bindings.createObjectBinding(() -> {
            LOG.fine("Calculating resourceBundle");
            // The dependent binding (selectedLanguage) returns the currently selected item from languageComboBox.
            SupportedLocale l = selectedLanguage.get();
            if (null == l) {
                // If nothing is selected in the languageComboBox, then we'll return the ResourceBundle that was loaded with the current
                // custom control (Login).
                return resources;
            }
            // Load the resource bundle for the selected locale.
            return ResourceBundleHelper.getBundle(Login.class, l.getLocale());
        }, selectedLanguage);

        // Create bindings for the current Stage so we can update the titleProperty when the language changes.
        // Use low-level API to create a nested property binding to the window property of the scene property for the current custom control (Login).
        // The class used by the BindingUtil.ofNestedObject method re-binds to the new nested window property when the scene property changes.
        ObjectBinding<Window> currentWindow = Bindings.when(sceneProperty().isNull()).then((Window) null)
                .otherwise(Bindings.<Window>select(sceneProperty(), "window"));
        // Create an object binding which returns the nested windowProperty as a Stage object.
        ObjectBinding<Stage> currentStage = Bindings.createObjectBinding(() -> {
            LOG.fine("Calculating currentStage");
            Window w = currentWindow.get();
            // return window as a Stage object only if it's not null and it is actually a Stage object.
            return (null != w && w instanceof Stage) ? (Stage) w : null;
        }, currentWindow);

        // Add a listener so the Stage title gets updated right away when the control gets added to a Scene and Stage.
        currentStage.addListener((observable, oldValue, newValue) -> {
            LOG.fine("currentStage changed");
            if (null != newValue) {
                LOG.fine("Setting stage title");
                newValue.setTitle(resourceBundle.get().getString(RESOURCEKEY_APPOINTMENTSCHEDULERLOGIN));
            }
        });

        // Add a listener so we can update the Stage title right away when the language changes.
        resourceBundle.addListener((observable, oldValue, newValue) -> {
            LOG.fine("Resource bundle changed");
            Stage stage = currentStage.get();
            if (null != stage) {
                LOG.fine("Setting stage title");
                stage.setTitle(newValue.getString(RESOURCEKEY_APPOINTMENTSCHEDULERLOGIN));
            }
        });

        // Bind the text property of userNameLabel to a StringBinding that returns the label text in the currently selected language.
        userNameLabel.textProperty().bind(Bindings.createStringBinding(() -> {
            LOG.fine("Calculating text for userNameLabel");
            return resourceBundle.get().getString(RESOURCEKEY_USERNAME);
        }, resourceBundle));
        passwordLabel.textProperty().bind(Bindings.createStringBinding(() -> {
            LOG.fine("Calculating text for passwordLabel");
            return resourceBundle.get().getString(RESOURCEKEY_PASSWORD);
        }, resourceBundle));
        loginButton.textProperty().bind(Bindings.createStringBinding(() -> {
            LOG.fine("Calculating text for loginButton");
            return resourceBundle.get().getString(RESOURCEKEY_LOGIN);
        }, resourceBundle));
        exitButton.textProperty().bind(Bindings.createStringBinding(() -> {
            LOG.fine("Calculating text for exitButton");
            return resourceBundle.get().getString(RESOURCEKEY_EXIT);
        }, resourceBundle));

        return selectedLanguage;
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    private void initialize() {
        assert languageComboBox != null : "fx:id=\"languageComboBox\" was not injected: check your FXML file 'Login.fxml'.";
        assert userNameLabel != null : "fx:id=\"userNameLabel\" was not injected: check your FXML file 'Login.fxml'.";
        assert userNameTextField != null : "fx:id=\"userNameTextField\" was not injected: check your FXML file 'Login.fxml'.";
        assert userNameValidationLabel != null : "fx:id=\"userNameValidationLabel\" was not injected: check your FXML file 'Login.fxml'.";
        assert passwordLabel != null : "fx:id=\"passwordLabel\" was not injected: check your FXML file 'Login.fxml'.";
        assert passwordField != null : "fx:id=\"passwordField\" was not injected: check your FXML file 'Login.fxml'.";
        assert passwordValidationLabel != null : "fx:id=\"passwordValidationLabel\" was not injected: check your FXML file 'Login.fxml'.";
        assert loginButton != null : "fx:id=\"loginButton\" was not injected: check your FXML file 'Login.fxml'.";
        assert exitButton != null : "fx:id=\"exitButton\" was not injected: check your FXML file 'Login.fxml'.";

        ObservableList<SupportedLocale> languages = FXCollections.observableArrayList(SupportedLocale.values());
        languageComboBox.setItems(languages);
        languageComboBox.getSelectionModel().select(AppResources.getCurrentLocale());

        ObjectBinding<SupportedLocale> selectedLanguage = initializeLanguageBindings();

        BooleanBinding userNameValid = Bindings.createBooleanBinding(() -> {
            LOG.fine("Calculating userNameValid");
            String s = userNameTextField.getText();
            return null != s && !s.trim().isEmpty();
        }, userNameTextField.textProperty());

        userNameValidationLabel.textProperty().bind(Bindings.when(userNameValid).then("").otherwise(Bindings.createStringBinding(() -> {
            LOG.fine("Calculating text for userNameValidationLabel");
            return resourceBundle.get().getString(RESOURCEKEY_EMPTYUSERNAME);
        }, resourceBundle)));

        NodeUtil.bindCssCollapse(userNameValidationLabel, userNameValid);

        BooleanBinding passwordValid = Bindings.createBooleanBinding(() -> {
            LOG.fine("Calculating passwordValid");
            String s = passwordField.getText();
            return null != s && !s.trim().isEmpty();
        }, passwordField.textProperty());

        passwordValidationLabel.textProperty().bind(Bindings.when(passwordValid).then("").otherwise(Bindings.createStringBinding(() -> {
            LOG.fine("Calculating text for passwordValidationLabel");
            return resourceBundle.get().getString(RESOURCEKEY_EMPTYPASSWORD);
        }, resourceBundle)));

        NodeUtil.bindCssCollapse(passwordValidationLabel, passwordValid);

        loginButton.disableProperty().bind(userNameValid.and(passwordValid).not().or(selectedLanguage.isNull()));
    }

    @Override
    protected void onLoginFailure() {
        ResourceBundle rb = resourceBundle.get();
        AlertHelper.showErrorAlert(getScene().getWindow(), rb.getString(RESOURCEKEY_LOGINERROR), rb.getString(RESOURCEKEY_INVALIDCREDENTIALS));
    }

}
