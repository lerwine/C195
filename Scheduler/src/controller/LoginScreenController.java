package controller;

import scheduler.util;
import java.net.URL;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import scheduler.App;
import scheduler.InvalidOperationException;

/**
 * FXML Controller class for the application login screen.
 * @author webmaster
 */
public class LoginScreenController implements Initializable {

    //<editor-fold defaultstate="collapsed" desc="Fields">
    
    //<editor-fold defaultstate="collapsed" desc="Constants">
    
    /**
     * The name of the globalization resource bundle for this controller.
     */
    public static final String RESOURCE_NAME = "globalization/loginScreen";
    
    /**
     * The path of the View associated with this controller.
     */
    public static final String VIEW_PATH = "/view/LoginScreen.fxml";
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
    
    private final scheduler.App.StageManager stageManager;
    
    //<editor-fold defaultstate="collapsed" desc="Validation Bindings">
    
    private LanguageValidation languageValid;
    
    private UserNameValidation userNameValid;
    
    private PasswordValidation passwordValid;
    
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
    
    public LoginScreenController(scheduler.App.StageManager stageManager) {
        this.stageManager = stageManager;
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
        languageValid = new LanguageValidation();
        userNameValid = new UserNameValidation();
        passwordValid = new PasswordValidation();
        languageValid.and(userNameValid).and(passwordValid).addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue,
                Boolean newValue) -> {
            loginButton.setDisable(!newValue);
        });
    }
    
    /**
     * Changes the scene of the root stage to the login screen.
     * @param stageManager
     */
    public static void setCurrentScene(scheduler.App.StageManager stageManager) {
        stageManager.setSceneWithControllerFactory(VIEW_PATH, RESOURCE_NAME, (Class<?> c) -> new LoginScreenController(stageManager));
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Event Handlers">
    
    @FXML
    void loginButtonClick(ActionEvent event) {
        try {
            // Change to home view if user could be successfully logged in.
            if (App.getCurrent().tryLoginUser(userNameTextField.getText(), passwordTextField.getText())) {
                successful = true;
                if (stageManager.isRoot())
                    HomeScreenController.setCurrentScene(stageManager);
                else
                    ((Button)event.getSource()).getScene().getWindow().hide();
            }
            else {
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
            Logger.getLogger(LoginScreenController.class.getName()).log(Level.SEVERE, "Login Exception", ex);
        } catch (SQLException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, currentResourceBundle.getString("dbAccessError"), ButtonType.OK);
            alert.initStyle(StageStyle.UTILITY);
            alert.setTitle(currentResourceBundle.getString("loginError"));
            alert.showAndWait();
            Logger.getLogger(LoginScreenController.class.getName()).log(Level.SEVERE, "Login Exception", ex);
        }
    }

    @FXML
    void exitButtonClick(ActionEvent event) { ((Button)event.getSource()).getScene().getWindow().hide(); }
    
    //<editor-fold defaultstate="collapsed" desc="Validation Binding Classes">
    
    /**
     * Validates that a language is selected.
     * Also changes culture-specific text to reflect the language selection change.
     */
    private class LanguageValidation extends BooleanBinding {
        private final ReadOnlyObjectProperty<Locale> selectedItemProperty;
        LanguageValidation() {
            // Bind to the selectedItem property of the language dropdown.
            selectedItemProperty = languageComboBox.getSelectionModel().selectedItemProperty();
            super.bind(selectedItemProperty);
        }
        
        @Override
        protected boolean computeValue() {
            Locale value = selectedItemProperty.get();
            languageComboBox.getButtonCell().setItem(value);
            if (value == null)
                return false;
            // Change the current application language;
            App.getCurrent().setCurrentLocale(value);
            // Load resource bundle for new language
            currentResourceBundle = ResourceBundle.getBundle(RESOURCE_NAME, value);
            // Set window title
            stageManager.setWindowTitle(currentResourceBundle.getString("appointmentSchedulerLogin"));
            // Update field labels and button text.
            userNameLabel.setText(currentResourceBundle.getString("userName"));
            passwordLabel.setText(currentResourceBundle.getString("password"));
            loginButton.setText(currentResourceBundle.getString("login"));
            exitButton.setText(currentResourceBundle.getString("exit"));
            return true;
        }
    }
    
    /**
     * Validates that the user name is not empty.
     * Also updates visibility of the corresponding validation label.
     */
    private class UserNameValidation extends BooleanBinding {
        private final StringProperty textProperty;
        UserNameValidation() {
            textProperty = userNameTextField.textProperty();
            super.bind(textProperty);
            super.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                if (newValue)
                    util.restoreLabeledVertical(userNameValidationLabel, currentResourceBundle.getString("emptyUserName"));
                else
                    util.collapseLabeledVertical(userNameValidationLabel);
            });
        }
        
        @Override
        protected boolean computeValue() {
            String value = textProperty.get();
            return value != null && !value.trim().isEmpty();
        }
    }
    
    /**
     * Validates that the password is not empty.
     * Also updates visibility of the corresponding validation label.
     */
    private class PasswordValidation extends BooleanBinding {
        private final StringProperty textProperty;
        PasswordValidation() {
            textProperty = passwordTextField.textProperty();
            super.bind(textProperty);
            super.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                if (newValue)
                    util.restoreLabeledVertical(passwordValidationLabel, currentResourceBundle.getString("emptyPassword"));
                else
                    util.collapseLabeledVertical(passwordValidationLabel);
            });
        }
        
        @Override
        protected boolean computeValue() {
            String value = textProperty.get();
            return value != null && !value.trim().isEmpty();
        }
    }
    
    //</editor-fold>
    
    //</editor-fold>
}
