package scene;

import controls.ValidatingTextField;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;

/**
 *
 * @author Leonard T. Erwine
 */
public class LoginSceneController implements Initializable {
    public static final String FXML_ABS_NAME = "/scene/login/LoginScene";
    
    @FXML
    private ComboBox<Locale> languageComboBox;
    
    @FXML
    private ValidatingTextField userNameTextField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Button loginButton;
    
    @FXML
    private void loginButtonAction(ActionEvent event) {
    }
    
    @FXML
    private void cancelButtonAction(ActionEvent event) {
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
