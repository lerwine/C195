/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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
import model.db.DataRow;
import model.db.User;
import scheduler.Messages;

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
    
    private final StringProperty asdf = new SimpleStringProperty();

    public String getAsdf() {
        return asdf.get();
    }

    public void setAsdf(String value) {
        asdf.set(value);
    }

    public StringProperty asdfProperty() {
        return asdf;
    }

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
     * @param url The URL of the associated view.
     * @param rb The resources provided by the {@link javafx.fxml.FXMLLoader}
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);
        userNameLabel.setText(Messages.current().getUserName() + ":");
        activeLabel.setText(Messages.current().getActiveState() + ":");
        changePasswordCheckBox.setText(Messages.current().getPassword() + ":");
        confirmLabel.setText(Messages.current().getConfirmPassword() + ":");
        userActiveStateOptions = FXCollections.observableArrayList(User.STATE_USER, User.STATE_ADMIN, User.STATE_INACTIVE);
        activeComboBox.setCellFactory((p) -> new ListCell<Short>() {
            @Override
            protected void updateItem(Short a, boolean bln) {
                super.updateItem(a, bln);
                switch (a) {
                    case 1:
                        setText(Messages.current().getNormalUser());
                        break;
                    case 2:
                        setText(Messages.current().getAdminstrativeUser());
                        break;
                    default:
                        setText(Messages.current().getInactive());
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
                Messages.current().notifyFieldValidationFailed(Messages.current().getPassword());
                return;
            }
        } else {
                Messages.current().notifyFieldValidationFailed(Messages.current().getUserName());
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
    
    @Override
    protected void applyModelAsNew(User model) {
        String s = model.getUserName();
        mainTitledPane.setText(Messages.current().getAddNewUser() + ":");
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
        String s = model.getUserName();
        if (s == null)
            s = "";
        mainTitledPane.setText(Messages.current().getEditUser(s) + ":");
        userNameTextField.setText(s);
        changePasswordCheckBox.setDisable(false);
        changePasswordCheckBox.setText(Messages.current().getChangePassword() + ":");
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
            userNameErrorMessage.setText(Messages.current().getEmptyUserName());
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
        String s = passwordTextField.getText();
        if (s.trim().isEmpty())
            passwordErrorMessage.setText(Messages.current().getEmptyPassword());
        else {
            if (confirmTextField.getText().equals(s)) {
                passwordErrorMessage.setText("");
                passwordErrorMessage.setVisible(false);
                return true;
            }
            passwordErrorMessage.setText(Messages.current().getPasswordMismatch());
        }
        passwordErrorMessage.setVisible(true);
        return false;
    }
}
