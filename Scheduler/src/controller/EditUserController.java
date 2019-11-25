/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javafx.scene.control.TitledPane;
import javax.persistence.EntityManager;
import model.entity.User;
import utils.InvalidOperationException;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
public class EditUserController extends ItemControllerBase<User> {
    /**
     * The path of the View associated with this controller.
     */
    public static final String VIEW_PATH = "/view/EditUser.fxml";
    
    @FXML
    private TitledPane mainTitledPane;

    @FXML
    private Label userNameLabel;

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
    private Label activeLabel;

    @FXML
    private ComboBox activeComboBox;
    
    private ObservableList<Short> userActiveStateOptions;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);
        rb = scheduler.Context.getMessagesRB();
        userNameLabel.setText(rb.getString("userName") + ":");
        activeLabel.setText(rb.getString("activeState") + ":");
        changePasswordCheckBox.setText(rb.getString("password") + ":");
        confirmLabel.setText(rb.getString("confirmPassword") + ":");
        userActiveStateOptions = FXCollections.observableArrayList(User.STATE_USER, User.STATE_ADMIN, User.STATE_INACTIVE);
        activeComboBox.setCellFactory((p) -> new ListCell<Short>() {
            @Override
            protected void updateItem(Short a, boolean bln) {
                super.updateItem(a, bln);
                switch (a) {
                    case 1:
                        setText(scheduler.Context.getMessage("normalUser"));
                        break;
                    case 2:
                        setText(scheduler.Context.getMessage("adminstrativeUser"));
                        break;
                    default:
                        setText(scheduler.Context.getMessage("inactive"));
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
        if (validateUserName()) {
            if (!validatePassword()) {
                utils.NotificationHelper.showNotificationDialog("validationWarning", "fieldValidationFailed",
                        new Object[] { scheduler.Context.getMessage("password") }, "fieldValidationInstruct", Alert.AlertType.ERROR);
                return;
            }
        } else {
            utils.NotificationHelper.showNotificationDialog("validationWarning", "fieldValidationFailed",
                    new Object[] { scheduler.Context.getMessage("userName") }, "fieldValidationInstruct", Alert.AlertType.ERROR);
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
        if (getModel().getPrimaryKey() == null)
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
    
    @Override
    protected void applyModelAsNew(User model) {
        String s = model.getUserName();
        mainTitledPane.setText(scheduler.Context.getMessage("addNewUser") + ":");
        userNameTextField.setText((s == null) ? "" : s);
        changePasswordCheckBox.setSelected(true);
        changePasswordCheckBox.setDisable(true);
        passwordTextField.setVisible(true);
        confirmLabel.setVisible(true);
        confirmTextField.setVisible(true);
        validateUserName();
    }

    @Override
    protected void applyModelAsEdit(User model) {
        ResourceBundle rb = scheduler.Context.getMessagesRB();
        String s = model.getUserName();
        if (s == null)
            s = "";
        mainTitledPane.setText(String.format(rb.getString("editUser") + ":", s));
        userNameTextField.setText(s);
        changePasswordCheckBox.setDisable(false);
        changePasswordCheckBox.setText(rb.getString("changePassword") + ":");
        changePasswordCheckBox.setSelected(false);
        passwordTextField.setVisible(false);
        passwordErrorMessage.setVisible(false);
        confirmLabel.setVisible(false);
        confirmTextField.setVisible(false);
        validateUserName();
    }
    
    private boolean validateUserName() {
        String s = userNameLabel.getText();
        if (s.trim().isEmpty())
            userNameErrorMessage.setText(scheduler.Context.getMessage("emptyUserName"));
        else {
            scheduler.Context.EmDependency dep = new scheduler.Context.EmDependency();
            EntityManager em;
            try {
                em = dep.open();
                try {
                    Integer id = getModel().getUserId();
                    if (id != null) {
                        List<User> user = (List<User>)em.createNamedQuery(User.NAMED_QUERY_BY_USERNAME_AVAIL)
                                    .setParameter(User.PARAMETER_NAME_USERNAME, s)
                                    .setParameter(User.PARAMETER_NAME_USERID, id).getResultList();
                        if (user.isEmpty()) {
                            userNameErrorMessage.setText("");
                            userNameErrorMessage.setVisible(false);
                            return true;
                        }
                    } else if (!scheduler.Context.getUserByUserName(em, s).isPresent()) {
                        userNameErrorMessage.setText("");
                        userNameErrorMessage.setVisible(false);
                        return true;
                    }
                } finally { dep.close(); }
                userNameErrorMessage.setText(scheduler.Context.getMessage("userNameNotAvailable"));
            } catch (InvalidOperationException ex) {
                Logger.getLogger(EditUserController.class.getName()).log(Level.SEVERE, null, ex);
                userNameErrorMessage.setText(scheduler.Context.getMessage("dbAccessError"));
            }
        }
        userNameErrorMessage.setVisible(true);
        return false;
    }
    
    private boolean validatePassword() {
        if (getModel().getPrimaryKey() != null && !changePasswordCheckBox.isSelected()) {
            passwordErrorMessage.setText("");
            passwordErrorMessage.setVisible(false);
            return true;
        }
        String s = passwordTextField.getText();
        if (s.trim().isEmpty())
            passwordErrorMessage.setText(scheduler.Context.getMessage("emptyPassword"));
        else {
            if (confirmTextField.getText().equals(s)) {
                passwordErrorMessage.setText("");
                passwordErrorMessage.setVisible(false);
                return true;
            }
            passwordErrorMessage.setText(scheduler.Context.getMessage("passwordMismatch"));
        }
        passwordErrorMessage.setVisible(true);
        return false;
    }
}
