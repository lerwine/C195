package controller;

import java.net.URL;
import java.sql.SQLException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javafx.scene.control.ListCell;
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
    /**
     * The name of the globalization resource bundle for this controller.
     */
    public static final String RESOURCE_NAME = "globalization/loginScreen";

    /**
     * The path of the View associated with this controller.
     */
    public static final String VIEW_PATH = "/view/LoginScreen.fxml";
    
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
     * The {@link Button} which begins the login attempt.
     */
    @FXML
    private Button loginButton;

    /**
     * The {@link Button} which cancels login and closes the application.
     */
    @FXML
    private Button exitButton;
    
    private ObservableList<Locale> locales;
    
    /**
     * Initializes the controller class.
     * @param url The URL of the associated view.
     * @param rb The resources provided by the {@link javafx.fxml.FXMLLoader}
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        locales = FXCollections.observableArrayList(new Locale("en"), new Locale("es"), new Locale("de"), new Locale("hi"));
        int index = -1;
        
        String d = App.getCurrentLocale().toString();
        for (int i = 0; i < locales.size(); i++) {
            if (locales.get(i).toString().equals(d)) {
                index = i;
                break;
            }
        }
        if (index < 0) {
            d = App.getCurrentLocale().getLanguage();
            for (int i = 0; i < locales.size(); i++) {
                if (locales.get(i).getLanguage().equals(d)) {
                    index = i;
                    break;
                }
            }
            if (index < 0) {
                locales.add(0, App.getCurrentLocale());
                index = 0;
            }
        }
        
        languageComboBox.setCellFactory((p) -> new ListCell<Locale>() {
            @Override
            protected void updateItem(Locale t, boolean bln) {
                super.updateItem(t, bln);
                setText((t == null) ? "" : t.getDisplayName(t));
            }
        });
        languageComboBox.setItems(locales);
        languageComboBox.getSelectionModel().select(index);
    }
    
    private Stage currentStage;
    
    public static void setCurrentScene(Node sourceNode) {
        App.changeScene(sourceNode, VIEW_PATH, (Stage stage, LoginScreenController controller) -> {
            controller.currentStage = stage;
            controller.refreshCultureSensitive();
        });
    }
    
    public static void setCurrentScene(Stage stage) {
        App.setScene(stage, VIEW_PATH, (LoginScreenController controller) -> {
            controller.currentStage = stage;
            controller.refreshCultureSensitive();
        });
    }
    
    void refreshCultureSensitive() {
        ResourceBundle rb = ResourceBundle.getBundle(RESOURCE_NAME, App.getCurrentLocale());
        if (currentStage != null)
            currentStage.setTitle(rb.getString("appointmentSchedulerLogin"));
        userNameLabel.setText(rb.getString("userName"));
        passwordLabel.setText(rb.getString("password"));
        loginButton.setText(rb.getString("login"));
        exitButton.setText(rb.getString("exit"));
    }
    
    @FXML
    void languageChanged(ActionEvent event) {
        Object item = languageComboBox.getSelectionModel().getSelectedItem();
        if (item == null || !(item instanceof Locale))
            return;
        App.setCurrentLocale((Locale)item);
        refreshCultureSensitive();
    }
    
    @FXML
    void loginButtonClick(ActionEvent event) {
        ResourceBundle rb;
        try {
            if (App.trySetCurrentUser(userNameTextField.getText(), passwordTextField.getText()))
                HomeScreenController.setCurrentScene((Node)event.getSource());
            else {
                rb = ResourceBundle.getBundle(RESOURCE_NAME, App.getCurrentLocale());
                Alert alert = new Alert(Alert.AlertType.ERROR, rb.getString("invalidCredentials"), ButtonType.OK);
                alert.initStyle(StageStyle.UTILITY);
                alert.setTitle(rb.getString("loginError"));
                alert.showAndWait();
            }
        } catch (InvalidOperationException ex) {
            rb = ResourceBundle.getBundle(RESOURCE_NAME, App.getCurrentLocale());
            Alert alert = new Alert(Alert.AlertType.ERROR, rb.getString("validationError"), ButtonType.OK);
            alert.initStyle(StageStyle.UTILITY);
            alert.setTitle(rb.getString("loginError"));
            alert.showAndWait();
            Logger.getLogger(LoginScreenController.class.getName()).log(Level.SEVERE, "Login Exception", ex);
        } catch (SQLException ex) {
            rb = ResourceBundle.getBundle(RESOURCE_NAME, App.getCurrentLocale());
            Alert alert = new Alert(Alert.AlertType.ERROR, rb.getString("dbAccessError"), ButtonType.OK);
            alert.initStyle(StageStyle.UTILITY);
            alert.setTitle(rb.getString("loginError"));
            alert.showAndWait();
            Logger.getLogger(LoginScreenController.class.getName()).log(Level.SEVERE, "Login Exception", ex);
        }
    }
    
    @FXML
    void exitButtonClick(ActionEvent event) {
        ((Button)event.getSource()).getScene().getWindow().hide();
    }

}
