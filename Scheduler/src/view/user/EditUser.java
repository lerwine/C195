package view.user;

import view.ItemController;
import javafx.beans.Observable;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.db.UserRow;
import view.annotations.FXMLResource;
import view.annotations.GlobalizationResource;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
@GlobalizationResource("view/user/EditUser")
@FXMLResource("/view/user/EditUser.fxml")
public class EditUser extends ItemController<UserRow> {
    @FXML
    private TextField userNameTextField;

    @FXML
    private Label userNameErrorMessageLabel;

    @FXML
    private CheckBox changePasswordCheckBox;

    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Label confirmLabel;

    @FXML
    private PasswordField confirmPasswordField;
    
    @FXML
    private Label passwordErrorMessageLabel;

    @FXML
    private ComboBox<Short> activeComboBox;
    
    private ObservableList<Short> userActiveStateOptions;
    
    private final ReadOnlyStringWrapper originalUserName = new ReadOnlyStringWrapper();
    
    public String getOriginalUserName() { return originalUserName.get(); }

    public ReadOnlyStringProperty originalUserNameProperty() { return originalUserName.getReadOnlyProperty(); }
    
    private final ReadOnlyBooleanWrapper userNameDuplicateInDb = new ReadOnlyBooleanWrapper();

    public boolean isUserNameDuplicateInDb() { return userNameDuplicateInDb.get(); }

    public ReadOnlyBooleanProperty userNameDuplicateInDbProperty() { return userNameDuplicateInDb.getReadOnlyProperty(); }

    private UserNameValidator userNameErrorMessage;
    
    private PasswordValidator passwordErrorMessage;
    
    private BooleanBinding valid;
    
    @Override
    public boolean isValid() { return valid.get(); }

    @Override
    public BooleanExpression validProperty() { return valid; }
    
    @FXML // This method is called by the FXMLLoader when initialization is complete
    @Override
    protected void initialize() {
        super.initialize();
        userActiveStateOptions = FXCollections.observableArrayList(UserRow.STATE_USER, UserRow.STATE_ADMIN, UserRow.STATE_INACTIVE);
        activeComboBox.setCellFactory((p) -> new ListCell<Short>() {
            @Override
            protected void updateItem(Short a, boolean bln) {
                super.updateItem(a, bln);
                
                switch (a) {
                    case 1:
                        setText(getResources().getString("normalUser"));
                        break;
                    case 2:
                        setText(getResources().getString("adminstrativeUser"));
                        break;
                    default:
                        setText(getResources().getString("inactive"));
                        break;
                }
            }
        });
        activeComboBox.setItems(userActiveStateOptions);
        newRowProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            onIsNewRowChanged(newValue);
        });
        onIsNewRowChanged(isNewRow());
        userNameErrorMessage = new UserNameValidator();
        passwordErrorMessage = new PasswordValidator();
        valid = userNameErrorMessage.isEmpty().and(passwordErrorMessage.isEmpty());
    }

    private void onIsNewRowChanged(boolean value) {
        if (value) {
            changePasswordCheckBox.setText(getResources().getString("password"));
            changePasswordCheckBox.setSelected(true);
            changePasswordCheckBox.setDisable(true);
            passwordField.setVisible(true);
            confirmLabel.setVisible(true);
            confirmPasswordField.setVisible(true);
            passwordErrorMessage.onChangePasswordCheckCheckChanged(passwordErrorMessage.changePasswordProperty.get());
            passwordErrorMessage.onPasswordMessageChanged(passwordErrorMessage.get());
        } else {
            changePasswordCheckBox.setSelected(false);
            changePasswordCheckBox.setDisable(false);
            confirmLabel.setVisible(false);
            passwordField.setVisible(false);
            confirmPasswordField.setVisible(false);
            passwordErrorMessageLabel.setVisible(false);
        }
    }
    
    public static UserRow addNew() {
        return showAndWait(EditUser.class, 640, 480, (SetContentContext<EditUser> context) -> {
            EditUser controller = context.getController();
            controller.setModel(new UserRow());
            controller.originalUserName.set("");
            context.getStage().setTitle(context.getResources().getString("addNewUser"));
        }, (SetContentContext<EditUser> context) -> {
            EditUser controller = context.getController();
            return (controller.isCanceled()) ? null : controller.getModel();
        });
    }

    public static boolean edit(UserRow row) {
        return showAndWait(EditUser.class, 640, 480, (SetContentContext<EditUser> context) -> {
            EditUser controller = context.getController();
            controller.setModel(row);
            context.getStage().setTitle(context.getResources().getString("editUser"));
            controller.originalUserName.set(row.getUserName());
            controller.userNameTextField.setText(row.getUserName());
        }, (SetContentContext<EditUser> context) -> {
            return !context.getController().isCanceled();
        });
    }

    @Override
    protected boolean saveChanges() {
        (new Alert(Alert.AlertType.INFORMATION, "saveChangesButtonClick not implemented", ButtonType.OK)).showAndWait();
        return false;
    }

    private class UserNameValidator extends StringBinding {
        private final StringProperty userNameProperty;

        UserNameValidator() {
            userNameProperty = userNameTextField.textProperty();
            super.bind(userNameProperty, userNameDuplicateInDb);
            userNameProperty.addListener((Observable observable) -> {
                userNameDuplicateInDb.set(false);
            });
            super.addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                onUserNameMessageChanged(newValue);
            });
            onUserNameMessageChanged(get());
        }
        
        private void onUserNameMessageChanged(String value) {
            if (value.isEmpty())
                collapseControl(userNameErrorMessageLabel);
            else
                restoreControl(userNameErrorMessageLabel, getResources().getString(value));
        }
        
        @Override
        protected String computeValue() {
            String n = userNameProperty.get();
            boolean b = userNameDuplicateInDb.get();
            if (n.trim().isEmpty())
                return "userNameCannotBeEmpty";
            else
                return (b) ? "userNameInUse" : "";
        }
            
        @Override
        public ObservableList<?> getDependencies() { return FXCollections.observableArrayList(userNameProperty, userNameDuplicateInDb); }

        @Override
        public void dispose() { super.unbind(userNameProperty, userNameDuplicateInDb); }
    }
    
    private class PasswordValidator extends StringBinding {
        private final StringProperty passwordProperty;
        private final StringProperty confirmProperty;
        private final BooleanProperty changePasswordProperty;
        PasswordValidator() {
            passwordProperty = passwordField.textProperty();
            confirmProperty = confirmPasswordField.textProperty();
            changePasswordProperty = changePasswordCheckBox.selectedProperty();
            super.bind(passwordProperty, confirmProperty, changePasswordProperty);
            super.addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                onPasswordMessageChanged(newValue);
            });
            changePasswordProperty.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                onChangePasswordCheckCheckChanged(newValue);
            });
            onChangePasswordCheckCheckChanged(changePasswordProperty.get());
            onPasswordMessageChanged(get());
        }
        
        final void onPasswordMessageChanged(String value) {
            if (value.isEmpty())
                collapseControl(passwordErrorMessageLabel);
            else
                restoreControl(passwordErrorMessageLabel, getResources().getString(value));
        }
        
        final void onChangePasswordCheckCheckChanged(boolean value) {
            confirmPasswordField.setDisable(!value);
            confirmPasswordField.setDisable(!value);
        }
    
        @Override
        protected String computeValue() {
            String p = passwordProperty.get();
            String c = confirmProperty.get();
            if (changePasswordProperty.get()) {
                if (p.trim().isEmpty())
                    return "passwordCannotBeEmpty";
                return (p.equals(c)) ? "" : "passwordMismatch";
            }
            return "";
        }
            
        @Override
        public ObservableList<?> getDependencies() { return FXCollections.observableArrayList(passwordProperty, confirmProperty, changePasswordProperty); }

        @Override
        public void dispose() { super.unbind(passwordProperty, confirmProperty, changePasswordProperty); }
    }
}
