package view.login;

import concurrent.SqlConnectionTask;
import concurrent.TaskWaiter;
import java.sql.SQLException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.db.UserRow;
import view.annotations.FXMLResource;
import view.annotations.GlobalizationResource;
import util.Alerts;
import util.Bindings;

/**
 * FXML Controller class for the application login screen.
 * @author webmaster
 */
@GlobalizationResource("view/login/LoginScene")
@FXMLResource("/view/login/LoginScene.fxml")
public class LoginScene extends view.Controller {
    private static final Logger LOG = Logger.getLogger(LoginScene.class.getName());

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
        scheduler.App app = scheduler.App.CURRENT.get();
        languageComboBox.setItems(app.getAllLanguages());
        languageComboBox.getSelectionModel().select(Locale.getDefault(Locale.Category.DISPLAY));
        valid = new Validation();
        valid.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            loginButton.setDisable(!newValue);
        });
        loginButton.setDisable(!valid.get());
        
    }
    
    //</editor-fold>
    
    @FXML
    void loginButtonClick(ActionEvent event) {
        LOG.entering(getClass().getName(), "loginButtonClick");
         try {
            if (scheduler.App.CURRENT.get().tryLoginUser(userNameTextField.getText(), passwordField.getText()))
                view.RootController.setAsRootStageScene();
            else
                Alerts.showErrorAlert(currentResourceBundle.getString(RESOURCEKEY_LOGINERROR), currentResourceBundle.getString(RESOURCEKEY_INVALIDCREDENTIALS));
        } catch (SQLException ex) {
            Alerts.showErrorAlert(currentResourceBundle.getString(RESOURCEKEY_LOGINERROR), currentResourceBundle.getString(RESOURCEKEY_VALIDATIONERROR));
            LOG.log(Level.SEVERE, "Login Exception", ex);
        }
        LOG.exiting(getClass().getName(), "loginButtonClick");
    }

    @FXML
    void exitButtonClick(ActionEvent event) { scheduler.App.CURRENT.get().getPrimaryStage().hide(); }
    
    private class Validation extends BooleanBinding {
        private final BooleanBinding languageValid;
        private final BooleanBinding userNameValid;
        private final BooleanBinding passwordValid;
        
        Validation() {
            languageValid = languageComboBox.valueProperty().isNotNull();
            userNameValid = Bindings.notNullOrWhiteSpace(userNameTextField.textProperty());
            passwordValid = Bindings.notNullOrWhiteSpace(passwordField.textProperty());
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
            if (newValue)
                collapseNode(passwordValidationLabel);
            else
                restoreNode(passwordValidationLabel);
        }

        private void userNameValidationChanged(Boolean newValue) {
            if (newValue)
                collapseNode(userNameValidationLabel);
            else
                restoreNode(userNameValidationLabel);
        }

        private void selectedLanaguageChanged(Locale newValue) {
            languageComboBox.getButtonCell().setItem(newValue);
            if (newValue == null)
                return;
            // Change the current application language;
            scheduler.App.setCurrentLocale(newValue);
            // Load resource bundle for new language
            currentResourceBundle = ResourceBundle.getBundle(getGlobalizationResourceName(LoginScene.class), newValue);
            // Set window title
            scheduler.App.CURRENT.get().getPrimaryStage().setTitle(currentResourceBundle.getString(RESOURCEKEY_APPOINTMENTSCHEDULERLOGIN));
            // Update field labels and button text.
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
        public ObservableList<?> getDependencies() { return FXCollections.observableArrayList(languageValid, userNameValid, passwordValid); }

        @Override
        public void dispose() { super.unbind(languageValid, userNameValid, passwordValid); }
    }
    
}
