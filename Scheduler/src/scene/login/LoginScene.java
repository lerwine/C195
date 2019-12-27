package scene.login;

import java.io.IOException;
import scene.home.HomeScene;
import scheduler.util;
import java.net.URL;
import java.sql.SQLException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.StageStyle;
import scheduler.App;
import scheduler.InvalidOperationException;

/**
 * FXML Controller class for the application login screen.
 * @author webmaster
 */
public class LoginScene implements Initializable {

    //<editor-fold defaultstate="collapsed" desc="Fields">
    
    //<editor-fold defaultstate="collapsed" desc="Constants">
    
    /**
     * The name of the globalization resource bundle for this controller.
     */
    public static final String RESOURCE_NAME = "scene/login/LoginScene";
    
    /**
     * The path of the View associated with this controller.
     */
    public static final String VIEW_PATH = "/scene/login/LoginScene.fxml";

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
    private PasswordField passwordTextField;
    
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
    
    private boolean successful;

    /**
     * Get the value of successful
     *
     * @return the value of successful
     */
    public boolean isSuccessful() { return successful; }

    //</editor-fold>
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Initialization">
    
    public LoginScene() {
        successful = false;
    }
    
    /**
     * Initializes the controller class.
     * @param url The URL of the associated view.
     * @param rb The resources provided by the {@link javafx.fxml.FXMLLoader}
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        successful = false;
        currentResourceBundle = rb;
        languageComboBox.setItems(scheduler.App.getCurrent().getAllLanguages());
        languageComboBox.getSelectionModel().select(scheduler.App.getCurrent().getCurrentLocale());
        valid = new Validation();
    }
    
    //</editor-fold>
    
    @FXML
    void loginButtonClick(ActionEvent event) {
        try {
            // Change to home view if user could be successfully logged in.
            if (App.getCurrent().tryLoginUser(userNameTextField.getText(), passwordTextField.getText())) {
                successful = true;
                
                ResourceBundle rb = ResourceBundle.getBundle(scene.home.HomeScene.RESOURCE_NAME, scheduler.App.getCurrent().getCurrentLocale());
                // Create new FXML loader with the resource path URL of the new fxml page and the resource bundle for the current language.
                FXMLLoader loader = new FXMLLoader(App.class.getResource(scene.home.HomeScene.VIEW_PATH), rb, null);
                // Set window title
                scheduler.App.getCurrent().getRootStage().setTitle(rb.getString("appointmentScheduler"));
                try {
                    scheduler.App.getCurrent().getRootStage().setScene(new Scene(loader.load()));
                } catch (IOException ex) {
                    Logger.getLogger(LoginScene.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, currentResourceBundle.getString("invalidCredentials"), ButtonType.OK);
                alert.initStyle(StageStyle.UTILITY);
                alert.setTitle(currentResourceBundle.getString("loginError"));
                alert.showAndWait();
            }
        } catch (InvalidOperationException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, currentResourceBundle.getString("validationError"), ButtonType.OK);
            alert.initStyle(StageStyle.UTILITY);
            alert.setTitle(currentResourceBundle.getString("loginError"));
            alert.showAndWait();
            Logger.getLogger(LoginScene.class.getName()).log(Level.SEVERE, "Login Exception", ex);
        } catch (SQLException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, currentResourceBundle.getString("dbAccessError"), ButtonType.OK);
            alert.initStyle(StageStyle.UTILITY);
            alert.setTitle(currentResourceBundle.getString("loginError"));
            alert.showAndWait();
            Logger.getLogger(LoginScene.class.getName()).log(Level.SEVERE, "Login Exception", ex);
        }
    }

    @FXML
    void exitButtonClick(ActionEvent event) { scheduler.App.getCurrent().getRootStage().hide(); }
    
    private class Validation extends BooleanBinding {
        private final BooleanBinding languageValid;
        private final BooleanBinding userNameValid;
        private final BooleanBinding passwordValid;
        
        Validation() {
            languageValid = languageComboBox.valueProperty().isNotNull();
            userNameValid = util.notNullOrWhiteSpace(userNameTextField.textProperty());
            passwordValid = util.notNullOrWhiteSpace(passwordTextField.textProperty());
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
            super.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                loginButton.setDisable(!newValue);
            });
            selectedLanaguageChanged(languageComboBox.getSelectionModel().getSelectedItem());
            userNameValidationChanged(userNameValid.get());
            passwordValidationChanged(passwordValid.get());
            loginButton.setDisable(!get());
        }

        private void passwordValidationChanged(Boolean newValue) {
            if (newValue)
                util.collapseControlVertical(passwordValidationLabel);
            else
                util.restoreControlVertical(passwordValidationLabel);
        }

        private void userNameValidationChanged(Boolean newValue) {
            if (newValue)
                util.collapseControlVertical(userNameValidationLabel);
            else
                util.restoreControlVertical(userNameValidationLabel);
        }

        private void selectedLanaguageChanged(Locale newValue) {
            languageComboBox.getButtonCell().setItem(newValue);
            if (newValue == null)
                return;
            // Change the current application language;
            App.getCurrent().setCurrentLocale(newValue);
            // Load resource bundle for new language
            currentResourceBundle = ResourceBundle.getBundle(RESOURCE_NAME, newValue);
            // Set window title
            scheduler.App.getCurrent().getRootStage().setTitle(currentResourceBundle.getString("appointmentSchedulerLogin"));
            // Update field labels and button text.
            userNameLabel.setText(currentResourceBundle.getString("userName"));
            passwordLabel.setText(currentResourceBundle.getString("password"));
            loginButton.setText(currentResourceBundle.getString("login"));
            exitButton.setText(currentResourceBundle.getString("exit"));
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
