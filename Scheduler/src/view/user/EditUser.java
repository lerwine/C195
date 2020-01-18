package view.user;

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
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import scheduler.dao.User;
import view.EditItem;
import scheduler.dao.UserImpl;
import view.annotations.FXMLResource;
import view.annotations.GlobalizationResource;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
@GlobalizationResource("view/user/EditUser")
@FXMLResource("/view/user/EditUser.fxml")
public class EditUser extends view.SchedulerController implements view.ItemController<UserImpl> {
    //<editor-fold defaultstate="collapsed" desc="Resource keys">

//    public static final String RESOURCEKEY_ACTIVESTATE = "activeState";
    public static final String RESOURCEKEY_ADDNEWUSER = "addNewUser";
    public static final String RESOURCEKEY_ADMINSTRATIVEUSER = "adminstrativeUser";
//    public static final String RESOURCEKEY_CHANGEPASSWORD = "changePassword";
//    public static final String RESOURCEKEY_CONFIRMPASSWORD = "confirmPassword";
    public static final String RESOURCEKEY_EDITUSER = "editUser";
    public static final String RESOURCEKEY_FIELDVALIDATIONFAILED = "fieldValidationFailed";
    public static final String RESOURCEKEY_INACTIVE = "inactive";
    public static final String RESOURCEKEY_NORMALUSER = "normalUser";
    public static final String RESOURCEKEY_PASSWORD = "password";
    public static final String RESOURCEKEY_PASSWORDCANNOTBEEMPTY = "passwordCannotBeEmpty";
    public static final String RESOURCEKEY_PASSWORDMISMATCH = "passwordMismatch";
//    public static final String RESOURCEKEY_USERNAME = "userName";
    public static final String RESOURCEKEY_USERNAMECANNOTBEEMPTY = "userNameCannotBeEmpty";
    public static final String RESOURCEKEY_VALIDATIONERROR = "validationError";
//    public static final String RESOURCEKEY_BY = "by";
//    public static final String RESOURCEKEY_CANCEL = "cancel";
//    public static final String RESOURCEKEY_CREATED = "created";
//    public static final String RESOURCEKEY_SAVE = "save";
//    public static final String RESOURCEKEY_UPDATED = "updated";
    public static final String RESOURCEKEY_USERNAMEINUSE = "userNameInUse";

    //</editor-fold>
    
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
    protected void initialize() {
        userActiveStateOptions = FXCollections.observableArrayList(User.STATUS_USER, User.STATUS_ADMIN, User.STATUS_INACTIVE);
        activeComboBox.setCellFactory((p) -> new ListCell<Short>() {
            @Override
            protected void updateItem(Short a, boolean bln) {
                super.updateItem(a, bln);
                
                switch (a) {
                    case 1:
                        setText(getResources().getString(RESOURCEKEY_NORMALUSER));
                        break;
                    case 2:
                        setText(getResources().getString(RESOURCEKEY_ADMINSTRATIVEUSER));
                        break;
                    default:
                        setText(getResources().getString(RESOURCEKEY_INACTIVE));
                        break;
                }
            }
        });
        activeComboBox.setItems(userActiveStateOptions);
        userNameErrorMessage = new UserNameValidator();
        passwordErrorMessage = new PasswordValidator();
        valid = userNameErrorMessage.isEmpty().and(passwordErrorMessage.isEmpty());
    }

    public static UserImpl addNew() {
        EditItem.ShowAndWaitResult<UserImpl> result = EditItem.showAndWait(EditUser.class, new UserImpl(), 640, 480);
        return (result.isSuccessful()) ? result.getTarget() : null;
    }

    public static boolean edit(UserImpl row) {
        EditItem.ShowAndWaitResult<UserImpl> result = EditItem.showAndWait(EditUser.class, row, 640, 480);
        return result.isSuccessful();
    }

    @Override
    public void accept(EditItem<UserImpl> context) {
        context.setWindowTitle(getResources().getString((context.isNewRow().get()) ? RESOURCEKEY_ADDNEWUSER : RESOURCEKEY_EDITUSER));
        if (context.isNewRow().get()) {
            originalUserName.set("");
            changePasswordCheckBox.setText(getResources().getString(RESOURCEKEY_PASSWORD));
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
            originalUserName.set(context.getTarget().getUserName());
            userNameTextField.setText(context.getTarget().getUserName());
        }
    }

    @Override
    public Boolean apply(EditItem<UserImpl> t) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
                collapseNode(userNameErrorMessageLabel);
            else
                restoreLabeled(userNameErrorMessageLabel, getResources().getString(value));
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
                collapseNode(passwordErrorMessageLabel);
            else
                restoreLabeled(passwordErrorMessageLabel, getResources().getString(value));
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
