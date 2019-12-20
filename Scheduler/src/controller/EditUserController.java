package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.db.DataRow;
import model.db.UserRow;
import scheduler.InvalidArgumentException;
import model.annotations.ResourceKey;
import model.annotations.ResourceName;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
@ResourceName(EditUserController.RESOURCE_NAME)
public class EditUserController extends ItemControllerBase<UserRow> {
    /**
     * The name of the globalization resource bundle for this controller.
     */
    public static final String RESOURCE_NAME = "globalization/editUser";

    /**
     * The path of the View associated with this controller.
     */
    public static final String VIEW_PATH = "/view/EditUser.fxml";
    
    private String returnViewPath;

    @FXML
    private VBox outerPane;

    @FXML
    @ResourceKey("userName")
    private Label userNameLabel;

    @FXML
    private TextField userNameTextField;

    @FXML
    @ResourceKey("userNameCannotBeEmpty")
    private Label userNameErrorMessage;

    @FXML
    private CheckBox changePasswordCheckBox;

    @FXML
    private PasswordField passwordTextField;
    
    @FXML
    @ResourceKey("confirmPassword")
    private Label confirmLabel;

    @FXML
    private PasswordField confirmTextField;
    
    @FXML
    @ResourceKey("passwordCannotBeEmpty")
    private Label passwordErrorMessage;

    @FXML
    @ResourceKey("activeState")
    private Label activeLabel;

    @FXML
    private ComboBox<Short> activeComboBox;
    
    private ObservableList<Short> userActiveStateOptions;
    
    /**
     * Initializes the controller class.
     * @param url The URL of the associated view.
     * @param rb The resources provided by the {@link javafx.fxml.FXMLLoader}
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);
        userActiveStateOptions = FXCollections.observableArrayList(UserRow.STATE_USER, UserRow.STATE_ADMIN, UserRow.STATE_INACTIVE);
        activeComboBox.setCellFactory((p) -> new ListCell<Short>() {
            @Override
            protected void updateItem(Short a, boolean bln) {
                super.updateItem(a, bln);
                ResourceBundle rb = ResourceBundle.getBundle(RESOURCE_NAME, scheduler.App.getCurrentLocale());
                switch (a) {
                    case 1:
                        setText(rb.getString("normalUser"));
                        break;
                    case 2:
                        setText(rb.getString("adminstrativeUser"));
                        break;
                    default:
                        setText(rb.getString("inactive"));
                        break;
                }
            }
        });
        activeComboBox.setItems(userActiveStateOptions);
    }    

    @FXML
    void userNameChanged(ActionEvent event) { validateUserName(); }
    
    @FXML
    void saveChangesButtonClick(ActionEvent event) {
        ResourceBundle rb;
        if (validateUserName()) {
            if (!validatePassword()) {
                rb = ResourceBundle.getBundle(RESOURCE_NAME, scheduler.App.getCurrentLocale());
                Alert alert = new Alert(Alert.AlertType.ERROR,
                        String.format(rb.getString("fieldValidationFailed"), rb.getString("password")),
                        ButtonType.OK);
                alert.initStyle(StageStyle.UTILITY);
                alert.setTitle(rb.getString("validationError"));
                alert.showAndWait();
                return;
            }
        } else {
            rb = ResourceBundle.getBundle(RESOURCE_NAME, scheduler.App.getCurrentLocale());
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    String.format(rb.getString("fieldValidationFailed"), rb.getString("userName")),
                    ButtonType.OK);
            alert.initStyle(StageStyle.UTILITY);
            alert.setTitle(rb.getString("validationError"));
            alert.showAndWait();
            return;
        }
        (new Alert(Alert.AlertType.INFORMATION, "saveChangesButtonClick not implemented", ButtonType.OK)).showAndWait();
    }
    
    @FXML
    void cancelButtonClick(ActionEvent event) {
        (new Alert(Alert.AlertType.INFORMATION, "cancelButtonClick not implemented", ButtonType.OK)).showAndWait();
    }
    
    @FXML
    void changePasswordCheckChanged(ActionEvent event) {
        if (getModel().getRowState() == DataRow.ROWSTATE_NEW)
            return;
        
        boolean value = changePasswordCheckBox.isSelected();
        passwordTextField.setVisible(value);
        confirmLabel.setVisible(value);
        confirmTextField.setVisible(value);
        validatePassword();
    }
    
    @FXML
    void passwordChanged(ActionEvent event) {
        (new Alert(Alert.AlertType.INFORMATION, "passwordChanged not implemented", ButtonType.OK)).showAndWait();
    }

    private boolean validateUserName() {
        String s = userNameLabel.getText();
        if (s.trim().isEmpty())
            userNameErrorMessage.setText(ResourceBundle.getBundle(RESOURCE_NAME, scheduler.App.getCurrentLocale())
                    .getString("userNameCannotBeEmpty"));
        else {
            throw new RuntimeException("Method not impelmented");
        }
        userNameErrorMessage.setVisible(true);
        return false;
    }
    
    private boolean validatePassword() {
        if (getModel().getRowState() != DataRow.ROWSTATE_NEW && !changePasswordCheckBox.isSelected()) {
            passwordErrorMessage.setText("");
            passwordErrorMessage.setVisible(false);
            return true;
        }
            ResourceBundle rb = ResourceBundle.getBundle(RESOURCE_NAME, scheduler.App.getCurrentLocale());
        String s = passwordTextField.getText();
        if (s.trim().isEmpty())
            passwordErrorMessage.setText(ResourceBundle.getBundle(RESOURCE_NAME, scheduler.App.getCurrentLocale())
                    .getString("passwordCannotBeEmpty"));
        else {
            if (confirmTextField.getText().equals(s)) {
                passwordErrorMessage.setText("");
                passwordErrorMessage.setVisible(false);
                return true;
            }
            passwordErrorMessage.setText(ResourceBundle.getBundle(RESOURCE_NAME, scheduler.App.getCurrentLocale())
                    .getString("passwordMismatch"));
        }
        passwordErrorMessage.setVisible(true);
        return false;
    }
    
    public static void setCurrentScene(Stage sourceStage, UserRow model, String returnViewPath) throws InvalidArgumentException {
        if (model == null)
            throw new InvalidArgumentException("model", "Model cannot be null");
        if (model.getRowState() == UserRow.ROWSTATE_DELETED)
            throw new InvalidArgumentException("model", "Model was already deleted");
        scheduler.App.setScene(sourceStage, VIEW_PATH, RESOURCE_NAME, (Stage stage, ResourceBundle rb, EditUserController controller) -> {
            String s = model.getUserName();
            if (s == null)
                s = "";
            controller.returnViewPath = returnViewPath;
            if (controller.setModel(model)) {
                stage.setTitle(String.format(rb.getString("editUser"), s));
                controller.changePasswordCheckBox.setText(String.format(rb.getString("changePassword"), s));
                controller.changePasswordCheckBox.setDisable(false);
                controller.changePasswordCheckBox.setSelected(false);
                controller.confirmLabel.setVisible(false);
                controller.confirmTextField.setVisible(false);
                controller.passwordTextField.setVisible(false);
                controller.passwordErrorMessage.setVisible(false);
            } else {
                stage.setTitle(rb.getString("addNewUser"));
                controller.changePasswordCheckBox.setText(String.format(rb.getString("password"), s));
                controller.changePasswordCheckBox.setSelected(true);
                controller.changePasswordCheckBox.setDisable(true);
                controller.passwordTextField.setVisible(true);
                controller.confirmLabel.setVisible(true);
                controller.confirmTextField.setVisible(true);
            }
            controller.userNameTextField.setText(s);
            controller.validateUserName();
        });
    }

    @Override
    void saveChangesClick(ActionEvent event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    void cancelClick(ActionEvent event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
