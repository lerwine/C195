package scene.user;

import scene.ItemControllerBase;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.StageStyle;
import model.db.DataRow;
import model.db.UserRow;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
public class EditUser extends ItemControllerBase<UserRow> {
    /**
     * The name of the globalization resource bundle for this controller.
     */
    public static final String GLOBALIZATION_RESOURCE_NAME = "scene/user/EditUser";

    /**
     * The path of the View associated with this controller.
     */
    public static final String FXML_RESOURCE_NAME = "/scene/user/EditUser.fxml";

    @FXML
    private VBox outerPane;

    @FXML
    private TextField userNameTextField;

    @FXML
    private Label userNameErrorMessage;

    @FXML
    private CheckBox changePasswordCheckBox;

    @FXML
    private PasswordField passwordTextField;
    
    @FXML
    private Label confirmLabel;

    @FXML
    private PasswordField confirmTextField;
    
    @FXML
    private Label passwordErrorMessage;

    @FXML
    private ComboBox<Short> activeComboBox;
    
    private ObservableList<Short> userActiveStateOptions;
    
    private java.lang.Runnable closeWindow;
    
    private boolean dialogResult = false;
    
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
                ResourceBundle rb = ResourceBundle.getBundle(GLOBALIZATION_RESOURCE_NAME, scheduler.App.getCurrent().getCurrentLocale());
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
    
    public static UserRow addNew() {
        EditUser controller = new EditUser();
        scheduler.App.showAndWait(GLOBALIZATION_RESOURCE_NAME, FXML_RESOURCE_NAME, controller, 640, 480, (rb, stage) -> {
            controller.closeWindow = () -> stage.hide();
            controller.setModel(new UserRow());
            stage.setTitle(rb.getString("addNewUser"));
            controller.changePasswordCheckBox.setText(rb.getString("password"));
            controller.changePasswordCheckBox.setSelected(true);
            controller.changePasswordCheckBox.setDisable(true);
            controller.passwordTextField.setVisible(true);
            controller.confirmLabel.setVisible(true);
            controller.confirmTextField.setVisible(true);
        });
        return (controller.dialogResult) ? controller.getModel() : null;
    }

    public static boolean edit(UserRow row) {
        EditUser controller = new EditUser();
        scheduler.App.showAndWait(GLOBALIZATION_RESOURCE_NAME, FXML_RESOURCE_NAME, controller, 640, 480, (rb, stage) -> {
            controller.closeWindow = () -> stage.hide();
            controller.setModel(row);
            stage.setTitle(String.format(rb.getString("editUser"), row.getUserName()));
            controller.changePasswordCheckBox.setDisable(false);
            controller.changePasswordCheckBox.setSelected(false);
            controller.confirmLabel.setVisible(false);
            controller.confirmTextField.setVisible(false);
            controller.passwordTextField.setVisible(false);
            controller.passwordErrorMessage.setVisible(false);
            controller.userNameTextField.setText(row.getUserName());
        });
        return controller.dialogResult;
    }

    @FXML
    void userNameChanged(ActionEvent event) { validateUserName(); }
    
    @FXML
    void saveChangesButtonClick(ActionEvent event) {
        ResourceBundle rb;
        if (validateUserName()) {
            if (!validatePassword()) {
                rb = ResourceBundle.getBundle(GLOBALIZATION_RESOURCE_NAME, scheduler.App.getCurrent().getCurrentLocale());
                Alert alert = new Alert(Alert.AlertType.ERROR,
                        String.format(rb.getString("fieldValidationFailed"), rb.getString("password")),
                        ButtonType.OK);
                alert.initStyle(StageStyle.UTILITY);
                alert.setTitle(rb.getString("validationError"));
                alert.showAndWait();
                return;
            }
        } else {
            rb = ResourceBundle.getBundle(GLOBALIZATION_RESOURCE_NAME, scheduler.App.getCurrent().getCurrentLocale());
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
        String s = userNameTextField.getText();
        if (s.trim().isEmpty())
            userNameErrorMessage.setText(ResourceBundle.getBundle(GLOBALIZATION_RESOURCE_NAME, scheduler.App.getCurrent().getCurrentLocale())
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
            ResourceBundle rb = ResourceBundle.getBundle(GLOBALIZATION_RESOURCE_NAME, scheduler.App.getCurrent().getCurrentLocale());
        String s = passwordTextField.getText();
        if (s.trim().isEmpty())
            passwordErrorMessage.setText(ResourceBundle.getBundle(GLOBALIZATION_RESOURCE_NAME, scheduler.App.getCurrent().getCurrentLocale())
                    .getString("passwordCannotBeEmpty"));
        else {
            if (confirmTextField.getText().equals(s)) {
                passwordErrorMessage.setText("");
                passwordErrorMessage.setVisible(false);
                return true;
            }
            passwordErrorMessage.setText(ResourceBundle.getBundle(GLOBALIZATION_RESOURCE_NAME, scheduler.App.getCurrent().getCurrentLocale())
                    .getString("passwordMismatch"));
        }
        passwordErrorMessage.setVisible(true);
        return false;
    }
    
    @Override
    protected void saveChangesClick(ActionEvent event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void cancelClick(ActionEvent event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
