package scheduler.view.user;

import java.io.IOException;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.binding.StringBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import scheduler.dao.UserDAO;
import scheduler.dao.UserStatus;
import static scheduler.util.NodeUtil.collapseNode;
import static scheduler.util.NodeUtil.restoreLabeled;
import static scheduler.util.NodeUtil.restoreNode;
import scheduler.util.ValueBindings;
import scheduler.util.Values;
import scheduler.view.EditItem;
import scheduler.view.MainController;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.appointment.AppointmentModel;
import scheduler.view.model.ItemModel;

/**
 * FXML Controller class for editing a {@link UserModelImpl}.
 * <p>
 * The associated view is <a href="file:../../resources/scheduler/view/user/EditUser.fxml">/resources/scheduler/view/user/EditUser.fxml</a>.</p>
 *
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
 */
@GlobalizationResource("scheduler/view/user/EditUser")
@FXMLResource("/scheduler/view/user/EditUser.fxml")
public final class EditUser extends EditItem.EditController<UserDAO, UserModelImpl> {

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Active State:"}.
     */
    public static final String RESOURCEKEY_ACTIVESTATELABELTEXT = "activeStateLabelText";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Appointments:"}.
     */
    public static final String RESOURCEKEY_APPOINTMENTSLABELTEXT = "appointmentsLabelText";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Add New UserDAO"}.
     */
    public static final String RESOURCEKEY_ADDNEWUSER = "addNewUser";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Administrative UserDAO"}.
     */
    public static final String RESOURCEKEY_ADMINISTRATIVEUSER = "administrativeUser";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Change Password:"}.
     */
    public static final String RESOURCEKEY_CHANGEPASSWORDLABELTEXT = "changePasswordLabelText";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Confirm Password:"}.
     */
    public static final String RESOURCEKEY_CONFIRMPASSWORDLABELTEXT = "confirmPasswordLabelText";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Edit UserDAO "%s""}.
     */
    public static final String RESOURCEKEY_EDITUSER = "editUser";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Field "%s" is invalid."}.
     */
    public static final String RESOURCEKEY_FIELDVALIDATIONFAILED = "fieldValidationFailed";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Inactive"}.
     */
    public static final String RESOURCEKEY_INACTIVE = "inactive";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Normal UserDAO"}.
     */
    public static final String RESOURCEKEY_NORMALUSER = "normalUser";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Password:"}.
     */
    public static final String RESOURCEKEY_PASSWORDLABELTEXT = "passwordLabelText";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Password cannot be empty."}.
     */
    public static final String RESOURCEKEY_PASSWORDCANNOTBEEMPTY = "passwordCannotBeEmpty";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Password and confirmation do not match."}.
     */
    public static final String RESOURCEKEY_PASSWORDMISMATCH = "passwordMismatch";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "UserDAO Name:"}.
     */
    public static final String RESOURCEKEY_USERNAMELABELTEXT = "userNameLabelText";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "UserDAO Name cannot be empty."}.
     */
    public static final String RESOURCEKEY_USERNAMECANNOTBEEMPTY = "userNameCannotBeEmpty";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "That user name is already in use."}.
     */
    public static final String RESOURCEKEY_USERNAMEINUSE = "userNameInUse";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Error loading customers...}.
     */
    public static final String RESOURCEKEY_ERRORLOADINGCUSTOMERS = "errorLoadingCustomers";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "That user is referenced in one or more appointments...}.
     */
    public static final String RESOURCEKEY_USERHASAPPOINTMENTS = "userHasAppointments";

    public static UserModelImpl editNew(MainController mainController, Stage stage) throws IOException {
        return editNew(EditUser.class, mainController, stage);
    }

    public static UserModelImpl edit(UserModelImpl model, MainController mainController, Stage stage) throws IOException {
        return edit(model, EditUser.class, mainController, stage);
    }

    @FXML // fx:id="userNameTextField"
    private TextField userNameTextField; // Value injected by FXMLLoader

    @FXML // fx:id="userNameErrorMessageLabel"
    private Label userNameErrorMessageLabel; // Value injected by FXMLLoader

    @FXML // fx:id="changePasswordCheckBox"
    private CheckBox changePasswordCheckBox; // Value injected by FXMLLoader

    @FXML // fx:id="passwordField"
    private PasswordField passwordField; // Value injected by FXMLLoader

    @FXML // fx:id="confirmLabel"
    private Label confirmLabel; // Value injected by FXMLLoader

    @FXML // fx:id="confirmPasswordField"
    private PasswordField confirmPasswordField; // Value injected by FXMLLoader

    @FXML // fx:id="passwordErrorMessageLabel"
    private Label passwordErrorMessageLabel; // Value injected by FXMLLoader

    @FXML // fx:id="activeComboBox"
    private ComboBox<UserStatus> activeComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="appointmentsLabel"
    private Label appointmentsLabel; // Value injected by FXMLLoader

    @FXML // fx:id="appointmentsFilterComboBox"
    private ComboBox<String> appointmentsFilterComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="appointmentsTableView"
    private TableView<AppointmentModel> appointmentsTableView; // Value injected by FXMLLoader

    @FXML // fx:id="addAppointmentButton"
    private Button addAppointmentButton; // Value injected by FXMLLoader

    private ObservableList<UserStatus> userActiveStateOptions;

    private StringBinding normalizedUserName;

    private StringBinding passwordErrorMessageBinding;

    private BooleanExpression validationExpression;

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert userNameTextField != null : "fx:id=\"userNameTextField\" was not injected: check your FXML file 'EditUser.fxml'.";
        assert userNameErrorMessageLabel != null : "fx:id=\"userNameErrorMessageLabel\" was not injected: check your FXML file 'EditUser.fxml'.";
        assert changePasswordCheckBox != null : "fx:id=\"changePasswordCheckBox\" was not injected: check your FXML file 'EditUser.fxml'.";
        assert passwordField != null : "fx:id=\"passwordField\" was not injected: check your FXML file 'EditUser.fxml'.";
        assert confirmLabel != null : "fx:id=\"confirmLabel\" was not injected: check your FXML file 'EditUser.fxml'.";
        assert confirmPasswordField != null : "fx:id=\"confirmPasswordField\" was not injected: check your FXML file 'EditUser.fxml'.";
        assert passwordErrorMessageLabel != null : "fx:id=\"passwordErrorMessageLabel\" was not injected: check your FXML file 'EditUser.fxml'.";
        assert activeComboBox != null : "fx:id=\"activeComboBox\" was not injected: check your FXML file 'EditUser.fxml'.";
        assert appointmentsLabel != null : "fx:id=\"appointmentsLabel\" was not injected: check your FXML file 'EditUser.fxml'.";
        assert appointmentsFilterComboBox != null : "fx:id=\"appointmentsFilterComboBox\" was not injected: check your FXML file 'EditUser.fxml'.";
        assert appointmentsTableView != null : "fx:id=\"appointmentsTableView\" was not injected: check your FXML file 'EditUser.fxml'.";
        assert addAppointmentButton != null : "fx:id=\"addAppointmentButton\" was not injected: check your FXML file 'EditUser.fxml'.";

        userActiveStateOptions = FXCollections.observableArrayList(UserStatus.values());
        activeComboBox.setCellFactory((p) -> new ListCell<UserStatus>() {
            @Override
            protected void updateItem(UserStatus a, boolean bln) {
                super.updateItem(a, bln);

                switch (a) {
                    case NORMAL:
                        setText(getResourceString(RESOURCEKEY_NORMALUSER));
                        break;
                    case ADMIN:
                        setText(getResourceString(RESOURCEKEY_ADMINISTRATIVEUSER));
                        break;
                    default:
                        setText(getResourceString(RESOURCEKEY_INACTIVE));
                        break;
                }
            }
        });
        activeComboBox.setItems(userActiveStateOptions);
        if (getModel().isNewItem()) {
            changePasswordCheckBox.setSelected(true);
            changePasswordCheckBox.setDisable(true);
        } else {
            changePasswordCheckBox.selectedProperty().addListener((observable) -> changePasswordSelectedChanged());
        }
        normalizedUserName = ValueBindings.asNormalized(userNameTextField.textProperty());
        normalizedUserName.isEmpty().addListener((observable) -> userNameEmptyChanged());
        passwordErrorMessageBinding = new StringBinding() {
            {
                super.bind(FXCollections.observableArrayList(changePasswordCheckBox.selectedProperty(), passwordField.textProperty(), confirmPasswordField.textProperty()));
            }

            @Override
            protected String computeValue() {
                String p = passwordField.textProperty().get();
                String c = confirmPasswordField.textProperty().get();
                if (!changePasswordCheckBox.selectedProperty().get()) {
                    if (Values.isNullWhiteSpaceOrEmpty(p)) {
                        return getResourceString(RESOURCEKEY_PASSWORDCANNOTBEEMPTY);
                    }
                    if (!p.equals(c)) {
                        return getResourceString(RESOURCEKEY_PASSWORDMISMATCH);
                    }
                }
                return "";
            }

            @Override
            public void dispose() {
                super.unbind(FXCollections.observableArrayList(changePasswordCheckBox.selectedProperty(), passwordField.textProperty(), confirmPasswordField.textProperty()));
                super.dispose();
            }
        };
        passwordErrorMessageBinding.addListener((observable) -> passwordValidationChanged());
        validationExpression = normalizedUserName.isNotEmpty().and(passwordErrorMessageBinding.isEmpty());
        changePasswordSelectedChanged();
        userNameEmptyChanged();
    }

    private void changePasswordSelectedChanged() {
        if (changePasswordCheckBox.selectedProperty().get()) {
            restoreNode(passwordField);
            restoreNode(confirmLabel);
            restoreNode(confirmPasswordField);
            passwordValidationChanged();
        } else {
            collapseNode(passwordField);
            collapseNode(confirmLabel);
            collapseNode(confirmPasswordField);
            collapseNode(passwordErrorMessageLabel);
        }
    }

    private void userNameEmptyChanged() {
        if (normalizedUserName.isEmpty().get()) {
            restoreNode(userNameErrorMessageLabel);
        } else {
            collapseNode(userNameErrorMessageLabel);
        }
    }

    private void passwordValidationChanged() {
        String message = passwordErrorMessageBinding.get();
        if (message.isEmpty()) {
            collapseNode(passwordErrorMessageLabel);
        } else {
            restoreLabeled(passwordErrorMessageLabel, message);
        }
    }

    @Override
    protected BooleanExpression getValidationExpression() {
        return validationExpression;
    }

    @Override
    protected ItemModel.ModelFactory<UserDAO, UserModelImpl> getFactory() {
        return UserModelImpl.getFactory();
    }

    @Override
    protected void updateModel(UserModelImpl model) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.view.user.EditUser#updateModel
    }

}
