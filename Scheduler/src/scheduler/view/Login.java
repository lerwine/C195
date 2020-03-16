package scheduler.view;

import java.io.IOException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;
import scheduler.App;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.util.Alerts;
import scheduler.util.ResourceBundleLoader;
import scheduler.util.ValueBindings;
import static scheduler.util.NodeUtil.collapseNode;
import static scheduler.util.NodeUtil.restoreNode;

/**
 * FXML Controller class for the application login screen.
 *
 * @author Leonard T. Erwine
 */
@GlobalizationResource("scheduler/view/Login")
@FXMLResource("/scheduler/view/Login.fxml")
public final class Login extends SchedulerController {

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

    public static void loadInto(Stage stage) throws IOException {
        // Populate list of Locale objects.
        ObservableList<ResourceBundleLoader.SupportedLocale> languages = FXCollections.observableArrayList();
        ResourceBundleLoader.getSupportedLocales().forEach((t) -> languages.add(t));

        SchedulerController.load(stage, Login.class, (Parent v, Login c) -> {
            c.languageComboBox.setItems(languages);
            stage.setScene(new Scene(v));
        });
    }

    /**
     * The {@link ComboBox} that lets the user select their preferred language.
     */
    @FXML
    private ComboBox<ResourceBundleLoader.SupportedLocale> languageComboBox;

    /**
     * The {@link Label} for the User Name {@link TextField}.
     */
    @FXML
    private Label userNameLabel;

    /**
     * The {@link TextField} where the user provides the User Name.
     */
    @FXML
    private TextField userNameTextField;

    /**
     * The {@link Label} for the User Name validation message.
     */
    @FXML
    private Label userNameValidationLabel;

    /**
     * The {@link Label} for the {@link PasswordField}.
     */
    @FXML
    private Label passwordLabel;

    /**
     * The {@link PasswordField} where the user provides the password.
     */
    @FXML
    private PasswordField passwordField;

    /**
     * The {@link Label} for the Password validation message.
     */
    @FXML
    private Label passwordValidationLabel;

    /**
     * The {@link Button} which begins the login attempt.
     */
    @FXML
    private Button loginButton;

    /**
     * The {@link Button} which cancels login and closes the application.
     */
    @FXML
    private Button exitButton;

    private ResourceBundle currentResourceBundle;

    private Validation valid;

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert languageComboBox != null : "fx:id=\"languageComboBox\" was not injected: check your FXML file 'LoginScene.fxml'.";
        assert userNameLabel != null : "fx:id=\"userNameLabel\" was not injected: check your FXML file 'LoginScene.fxml'.";
        assert userNameTextField != null : "fx:id=\"userNameTextField\" was not injected: check your FXML file 'LoginScene.fxml'.";
        assert userNameValidationLabel != null : "fx:id=\"userNameValidationLabel\" was not injected: check your FXML file 'LoginScene.fxml'.";
        assert passwordLabel != null : "fx:id=\"passwordLabel\" was not injected: check your FXML file 'LoginScene.fxml'.";
        assert passwordField != null : "fx:id=\"passwordField\" was not injected: check your FXML file 'LoginScene.fxml'.";
        assert passwordValidationLabel != null : "fx:id=\"passwordValidationLabel\" was not injected: check your FXML file 'LoginScene.fxml'.";
        assert loginButton != null : "fx:id=\"loginButton\" was not injected: check your FXML file 'LoginScene.fxml'.";
        assert exitButton != null : "fx:id=\"exitButton\" was not injected: check your FXML file 'LoginScene.fxml'.";
        currentResourceBundle = getResources();
    }

    @Override
    protected void onBeforeShow(Node currentView, Stage stage) {
        stage.setTitle(currentResourceBundle.getString(RESOURCEKEY_APPOINTMENTSCHEDULERLOGIN));
        languageComboBox.getSelectionModel().select(ResourceBundleLoader.getSupportedLocales().filter((l) -> l.isCurrent()).findFirst().get());
        valid = new Validation();
        valid.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            loginButton.setDisable(!newValue);
        });
        loginButton.setDisable(!valid.get());
        super.onBeforeShow(currentView, stage);
    }

    @FXML
    void loginButtonClick(ActionEvent event) {
        App.tryLoginUser((Stage) userNameTextField.getScene().getWindow(), userNameTextField.getText(), passwordField.getText(), (ex) -> {
            if (ex == null) {
                Alerts.showErrorAlert(currentResourceBundle.getString(RESOURCEKEY_LOGINERROR), currentResourceBundle.getString(RESOURCEKEY_INVALIDCREDENTIALS));
            } else {
                LOG.logp(Level.SEVERE, getClass().getName(), "loginButtonClick", "Error logging in user", ex);
                Alerts.showErrorAlert(currentResourceBundle.getString(RESOURCEKEY_LOGINERROR), currentResourceBundle.getString(RESOURCEKEY_VALIDATIONERROR));
            }
        });
    }

    @FXML
    void exitButtonClick(ActionEvent event) {
        languageComboBox.getScene().getWindow().hide();
    }

    private class Validation extends BooleanBinding {

        private final BooleanBinding languageValid;
        private final BooleanBinding userNameValid;
        private final BooleanBinding passwordValid;

        Validation() {
            languageValid = languageComboBox.valueProperty().isNotNull();
            userNameValid = ValueBindings.notNullOrWhiteSpace(userNameTextField.textProperty());
            passwordValid = ValueBindings.notNullOrWhiteSpace(passwordField.textProperty());
            super.bind(languageValid, userNameValid, passwordValid);
            languageComboBox.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends ResourceBundleLoader.SupportedLocale> observable, ResourceBundleLoader.SupportedLocale oldValue, ResourceBundleLoader.SupportedLocale newValue) -> {
                selectedLanaguageChanged(newValue);
            });
            userNameValid.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                userNameValidationChanged(newValue);
            });
            passwordValid.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                passwordValidationChanged(newValue);
            });
            selectedLanaguageChanged(languageComboBox.getSelectionModel().getSelectedItem());
            userNameValidationChanged(userNameValid.get());
            passwordValidationChanged(passwordValid.get());
        }

        private void passwordValidationChanged(Boolean newValue) {
            if (newValue) {
                collapseNode(passwordValidationLabel);
            } else {
                restoreNode(passwordValidationLabel);
            }
        }

        private void userNameValidationChanged(Boolean newValue) {
            if (newValue) {
                collapseNode(userNameValidationLabel);
            } else {
                restoreNode(userNameValidationLabel);
            }
        }

        private void selectedLanaguageChanged(ResourceBundleLoader.SupportedLocale newValue) {
            languageComboBox.getButtonCell().setItem(newValue);
            if (newValue == null) {
                return;
            }
            // Change the current application language;
            newValue.setCurrent();
            // Load resource bundle for new language
            currentResourceBundle = ResourceBundleLoader.getBundle(Login.class);
            // Update field labels and button text.
            Scene scene = languageComboBox.getScene();
            if (null != scene) {
                Window window = (Stage) scene.getWindow();
                if (null != window && window instanceof Stage) {
                    ((Stage) window).setTitle(currentResourceBundle.getString(RESOURCEKEY_APPOINTMENTSCHEDULERLOGIN));
                }
            }
            userNameLabel.setText(currentResourceBundle.getString(RESOURCEKEY_USERNAME));
            passwordLabel.setText(currentResourceBundle.getString(RESOURCEKEY_PASSWORD));
            loginButton.setText(currentResourceBundle.getString(RESOURCEKEY_LOGIN));
            exitButton.setText(currentResourceBundle.getString(RESOURCEKEY_EXIT));
        }

        @Override
        protected boolean computeValue() {
            boolean l = languageValid.get();
            boolean u = userNameValid.get();
            return passwordValid.get() && u && l;
        }

        @Override
        public ObservableList<?> getDependencies() {
            return FXCollections.observableArrayList(languageValid, userNameValid, passwordValid);
        }

        @Override
        public void dispose() {
            super.unbind(languageValid, userNameValid, passwordValid);
        }
    }

}
