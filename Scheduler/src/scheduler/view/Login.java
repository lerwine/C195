package scheduler.view;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
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
import scheduler.AppConfig;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.util.Alerts;
import scheduler.util.ValueBindings;
import scheduler.view.SchedulerController;

/**
 * FXML Controller class for the application login screen.
 *
 * @author Leonard T. Erwine
 */
@GlobalizationResource("scheduler/view/login/LoginScene")
@FXMLResource("/scheduler/view/login/LoginScene.fxml")
public final class Login extends SchedulerController {

    private static final Logger LOG = Logger.getLogger(Login.class.getName());

    //<editor-fold defaultstate="collapsed" desc="Fields">
    //<editor-fold defaultstate="collapsed" desc="Resource keys">
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

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="JavaFX Controls">
    /**
     * The {@link ComboBox} that lets the user select their preferred language.
     */
    @FXML
    private ComboBox<Locale> languageComboBox;

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

    //</editor-fold>
    private ResourceBundle currentResourceBundle;

    //<editor-fold defaultstate="collapsed" desc="Validation Bindings">
    private Validation valid;

    //</editor-fold>
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Initialization">
    public static void loadInto(Stage stage) throws IOException {
        String[] languageIds = AppConfig.getLanguages();
        // Attempt to find a match for the current display language amongst the languages supported by the app.
        final String lt = Locale.getDefault(Locale.Category.DISPLAY).toLanguageTag();
        // First look for one that is an exact match with the language tag.
        Optional<String> cl = Arrays.stream(languageIds).filter((String id) -> id.equals(lt)).findFirst();
        if (!cl.isPresent()) {
            // Look for one that matches the ISO3 language code.
            final String iso3 = Locale.getDefault(Locale.Category.DISPLAY).getISO3Language();
            cl = Arrays.stream(languageIds).filter((String id) -> id.equals(iso3)).findFirst();
            if (!cl.isPresent()) {
                // Look for one that matches the ISO2 language code.
                final String ln = Locale.getDefault(Locale.Category.DISPLAY).getLanguage();
                cl = Arrays.stream(languageIds).filter((String id) -> id.equals(ln)).findFirst();
            }
        }

        // Populate list of Locale objects.
        ObservableList<Locale> languages = FXCollections.observableArrayList();
        if (cl.isPresent()) {
            for (String n : languageIds) {
                languages.add((n.equals(cl.get())) ? Locale.getDefault(Locale.Category.DISPLAY) : new Locale(n));
            }
        } else {
            for (String n : languageIds) {
                languages.add(new Locale(n));
            }
            Locale toSelect = languages.get(0);
            Locale.setDefault(Locale.Category.DISPLAY, toSelect);
            Locale.setDefault(Locale.Category.FORMAT, toSelect);
        }

        SchedulerController.load(stage, Login.class, (Parent v, Login c) -> {
            c.languageComboBox.setItems(languages);
            stage.setScene(new Scene(v));
        });
    }

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
        languageComboBox.getSelectionModel().select(Locale.getDefault(Locale.Category.DISPLAY));
        valid = new Validation();
        valid.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            loginButton.setDisable(!newValue);
        });
        loginButton.setDisable(!valid.get());
        super.onBeforeShow(currentView, stage);
    }

    //</editor-fold>
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
            languageComboBox.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Locale> observable, Locale oldValue, Locale newValue) -> {
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

        private void selectedLanaguageChanged(Locale newValue) {
            languageComboBox.getButtonCell().setItem(newValue);
            if (newValue == null) {
                return;
            }
            // Change the current application language;
            Locale.setDefault(Locale.Category.DISPLAY, newValue);
            Locale.setDefault(Locale.Category.FORMAT, newValue);
            // Load resource bundle for new language
            currentResourceBundle = ResourceBundle.getBundle(getGlobalizationResourceName(Login.class), newValue);
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
