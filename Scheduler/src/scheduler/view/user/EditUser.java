package scheduler.view.user;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.PasswordField;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import scheduler.AppResourceKeys;
import static scheduler.AppResourceKeys.RESOURCEKEY_CONNECTEDTODB;
import static scheduler.AppResourceKeys.RESOURCEKEY_INACTIVE;
import static scheduler.AppResourceKeys.RESOURCEKEY_LOADINGUSERS;
import scheduler.AppResources;
import scheduler.dao.AppointmentDAO;
import scheduler.dao.UserDAO;
import scheduler.model.UserStatus;
import scheduler.model.ui.AppointmentModel;
import scheduler.model.ui.FxRecordModel;
import scheduler.model.ui.UserModel;
import scheduler.util.DbConnector;
import static scheduler.util.NodeUtil.bindCssCollapse;
import static scheduler.util.NodeUtil.collapseNode;
import scheduler.util.PwHash;
import scheduler.view.EditItem;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.annotations.ModelEditor;
import scheduler.view.appointment.AppointmentModelFilter;
import static scheduler.view.customer.EditCustomerResourceKeys.RESOURCEKEY_LOADINGAPPOINTMENTS;
import scheduler.view.task.WaitBorderPane;
import scheduler.view.task.WaitTitledPane;
import static scheduler.view.user.EditUserResourceKeys.*;

/**
 * FXML Controller class for editing a {@link UserModel}.
 * <p>
 * The associated view is {@code /resources/scheduler/view/user/EditUser.fxml}.</p>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/view/user/EditUser")
@FXMLResource("/scheduler/view/user/EditUser.fxml")
public final class EditUser extends SplitPane implements EditItem.ModelEditor<UserDAO, UserModel> {

    private static final Logger LOG = Logger.getLogger(EditUser.class.getName());

    public static UserModel editNew(Window parentWindow, boolean keepOpen) throws IOException {
        UserModel.Factory factory = UserModel.getFactory();
        return EditItem.showAndWait(parentWindow, EditUser.class, factory.createNew(factory.getDaoFactory().createNew()), keepOpen);
    }

    public static UserModel edit(UserModel model, Window parentWindow) throws IOException {
        return EditItem.showAndWait(parentWindow, EditUser.class, model, false);
    }

    private final ReadOnlyBooleanWrapper valid;
    private final ReadOnlyBooleanWrapper modified;
    private final ReadOnlyStringWrapper windowTitle;

    @ModelEditor
    private UserModel model;

    @ModelEditor
    private WaitBorderPane waitBorderPane;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // fx:id="userEditSplitPane"
    private SplitPane userEditSplitPane; // Value injected by FXMLLoader

    @FXML // fx:id="userNameTextField"
    private TextField userNameTextField; // Value injected by FXMLLoader

    @FXML // fx:id="userNameErrorMessageLabel"
    private Label userNameErrorMessageLabel; // Value injected by FXMLLoader

    @FXML // fx:id="changePasswordCheckBox"
    private CheckBox changePasswordCheckBox; // Value injected by FXMLLoader

    @FXML // fx:id="passwordField"
    private PasswordField passwordField; // Value injected by FXMLLoader

    @FXML // fx:id="passwordErrorMessageLabel"
    private Label passwordErrorMessageLabel; // Value injected by FXMLLoader

    @FXML // fx:id="confirmLabel"
    private Label confirmLabel; // Value injected by FXMLLoader

    @FXML // fx:id="confirmPasswordField"
    private PasswordField confirmPasswordField; // Value injected by FXMLLoader

    @FXML // fx:id="activeComboBox"
    private ComboBox<UserStatus> activeComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="appointmentListingVBox"
    private VBox appointmentListingVBox; // Value injected by FXMLLoader

    @FXML // fx:id="appointmentsFilterComboBox"
    private ComboBox<String> appointmentsFilterComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="appointmentsTableView"
    private TableView<AppointmentModel> appointmentsTableView; // Value injected by FXMLLoader

    private ObservableList<String> unavailableUserNames;

    private ObservableList<UserStatus> userActiveStateOptions;

    private ObservableList<AppointmentModel> userAppointments;

    private ObservableList<AppointmentFilterItem> filterOptions;

    public EditUser() {
        windowTitle = new ReadOnlyStringWrapper("");
        valid = new ReadOnlyBooleanWrapper(false);
        modified = new ReadOnlyBooleanWrapper(false);
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    private void initialize() {
        assert userEditSplitPane != null : "fx:id=\"userEditSplitPane\" was not injected: check your FXML file 'EditUser.fxml'.";
        assert userNameTextField != null : "fx:id=\"userNameTextField\" was not injected: check your FXML file 'EditUser.fxml'.";
        assert userNameErrorMessageLabel != null : "fx:id=\"userNameErrorMessageLabel\" was not injected: check your FXML file 'EditUser.fxml'.";
        assert changePasswordCheckBox != null : "fx:id=\"changePasswordCheckBox\" was not injected: check your FXML file 'EditUser.fxml'.";
        assert passwordField != null : "fx:id=\"passwordField\" was not injected: check your FXML file 'EditUser.fxml'.";
        assert passwordErrorMessageLabel != null : "fx:id=\"passwordErrorMessageLabel\" was not injected: check your FXML file 'EditUser.fxml'.";
        assert confirmLabel != null : "fx:id=\"confirmLabel\" was not injected: check your FXML file 'EditUser.fxml'.";
        assert confirmPasswordField != null : "fx:id=\"confirmPasswordField\" was not injected: check your FXML file 'EditUser.fxml'.";
        assert activeComboBox != null : "fx:id=\"activeComboBox\" was not injected: check your FXML file 'EditUser.fxml'.";
        assert appointmentListingVBox != null : "fx:id=\"appointmentListingVBox\" was not injected: check your FXML file 'EditUser.fxml'.";
        assert appointmentsFilterComboBox != null : "fx:id=\"appointmentsFilterComboBox\" was not injected: check your FXML file 'EditUser.fxml'.";
        assert appointmentsTableView != null : "fx:id=\"appointmentsTableView\" was not injected: check your FXML file 'EditUser.fxml'.";

        userActiveStateOptions = FXCollections.observableArrayList(UserStatus.values());
        unavailableUserNames = FXCollections.observableArrayList();
        userAppointments = FXCollections.observableArrayList();
        filterOptions = FXCollections.observableArrayList();
        activeComboBox.setCellFactory((p) -> new ListCell<UserStatus>() {
            @Override
            protected void updateItem(UserStatus a, boolean bln) {
                super.updateItem(a, bln);

                switch (a) {
                    case NORMAL:
                        setText(resources.getString(RESOURCEKEY_NORMALUSER));
                        break;
                    case ADMIN:
                        setText(resources.getString(RESOURCEKEY_ADMINISTRATIVEUSER));
                        break;
                    default:
                        setText(resources.getString(RESOURCEKEY_INACTIVE));
                        break;
                }
            }
        });

        activeComboBox.setItems(userActiveStateOptions);
        if (model.isNewRow()) {
            changePasswordCheckBox.setSelected(true);
            changePasswordCheckBox.setDisable(true);
            appointmentListingVBox.setVisible(false);
            collapseNode(appointmentListingVBox);
            userEditSplitPane.setDividerPosition(0, 1.0);
        } else {
            appointmentsTableView.setItems(userAppointments);
        }

        userNameErrorMessageLabel.visibleProperty().bind(getUserNameValidationMessage().isNotEmpty());
        bindCssCollapse(userNameErrorMessageLabel, getUserNameValidationMessage().isEmpty());

        passwordField.visibleProperty().bind(changePasswordCheckBox.selectedProperty());
        bindCssCollapse(passwordField, changePasswordCheckBox.selectedProperty().not());

        confirmLabel.visibleProperty().bind(changePasswordCheckBox.selectedProperty());
        bindCssCollapse(confirmLabel, changePasswordCheckBox.selectedProperty().not());

        confirmPasswordField.visibleProperty().bind(changePasswordCheckBox.selectedProperty());
        bindCssCollapse(confirmPasswordField, changePasswordCheckBox.selectedProperty().not());

        passwordErrorMessageLabel.textProperty().bind(getPasswordValidationMessage());
        passwordErrorMessageLabel.visibleProperty().bind(getPasswordValidationMessage().isNotEmpty());
        bindCssCollapse(passwordErrorMessageLabel, getPasswordValidationMessage().isEmpty());

        LocalDate today = LocalDate.now();
        UserDAO dao = model.dataObject();
        if (dao.isExisting()) {
            filterOptions.add(new AppointmentFilterItem(resources.getString(RESOURCEKEY_CURRENTANDFUTURE),
                    AppointmentModelFilter.of(today, null, dao)));
            filterOptions.add(new AppointmentFilterItem(resources.getString(RESOURCEKEY_CURRENTAPPOINTMENTS),
                    AppointmentModelFilter.of(today, today.plusDays(1), dao)));
            filterOptions.add(new AppointmentFilterItem(resources.getString(RESOURCEKEY_PASTAPPOINTMENTS),
                    AppointmentModelFilter.of(null, today, dao)));
            filterOptions.add(new AppointmentFilterItem(resources.getString(RESOURCEKEY_ALLAPPOINTMENTS), AppointmentModelFilter.of(dao)));
        }
        WaitTitledPane pane = new WaitTitledPane();
        pane.addOnFailAcknowledged((evt) -> getScene().getWindow().hide())
                .addOnCancelAcknowledged((evt) -> getScene().getWindow().hide());
        waitBorderPane.startNow(pane, new InitialLoadTask());
        windowTitle.set(resources.getString((model.isNewRow()) ? RESOURCEKEY_ADDNEWUSER : RESOURCEKEY_EDITUSER));
    }

    public boolean applyChangesToModel() {
        model.setUserName(userNameTextField.getText());
        model.setStatus(activeComboBox.getSelectionModel().getSelectedItem());
        if (changePasswordCheckBox.isSelected()) {
            PwHash pw = new PwHash(passwordField.getText(), true);
            model.setPassword(pw.getEncodedHash());
        }
        return true;
    }

    private StringBinding getUserNameValidationMessage() {
        return Bindings.createStringBinding(() -> {
            String n = userNameTextField.getText().trim().toLowerCase();
            if (n.isEmpty()) {
                return resources.getString(RESOURCEKEY_USERNAMECANNOTBEEMPTY);
            }
            if (unavailableUserNames.contains(n)) {
                return resources.getString(RESOURCEKEY_USERNAMEINUSE);
            }
            return "";
        }, userNameTextField.textProperty(), unavailableUserNames);
    }

    private StringBinding getPasswordValidationMessage() {
        return Bindings.createStringBinding(() -> {
            String p = passwordField.getText();
            String c = confirmPasswordField.getText();
            if (changePasswordCheckBox.isSelected()) {
                if (p.trim().isEmpty()) {
                    return resources.getString(RESOURCEKEY_PASSWORDCANNOTBEEMPTY);
                }
                if (!p.equals(c)) {
                    return resources.getString(RESOURCEKEY_PASSWORDMISMATCH);
                }
            }
            return "";
        }, passwordField.textProperty(), confirmPasswordField.textProperty(), changePasswordCheckBox.selectedProperty());
    }

    @Override
    public boolean isValid() {
        return valid.get();
    }

    @Override
    public ReadOnlyBooleanProperty validProperty() {
        return valid.getReadOnlyProperty();
    }

    @Override
    public boolean isModified() {
        return modified.get();
    }

    @Override
    public ReadOnlyBooleanProperty modifiedProperty() {
        return modified.getReadOnlyProperty();
    }

    @Override
    public String getWindowTitle() {
        return windowTitle.get();
    }

    @Override
    public ReadOnlyStringProperty windowTitleProperty() {
        return windowTitle.getReadOnlyProperty();
    }

    @Override
    public FxRecordModel.ModelFactory<UserDAO, UserModel> modelFactory() {
        return UserModel.getFactory();
    }

    @Override
    public void onNewModelSaved() {
        throw new UnsupportedOperationException("Not supported yet."); // CURRENT: Implement scheduler.view.user.EditUser#applyEditMode
    }

    @Override
    public void updateModel() {
        model.setUserName(userNameTextField.getText().trim());
        model.setPassword(passwordField.getText());
        model.setStatus(activeComboBox.getSelectionModel().getSelectedItem());
    }

    private class AppointmentFilterItem {

        private final ReadOnlyStringWrapper text;

        @SuppressWarnings("unused")
        public String getText() {
            return text.get();
        }

        @SuppressWarnings("unused")
        public ReadOnlyStringProperty textProperty() {
            return text.getReadOnlyProperty();
        }
        private final ReadOnlyObjectWrapper<AppointmentModelFilter> modelFilter;

        public AppointmentModelFilter getModelFilter() {
            return modelFilter.get();
        }

        @SuppressWarnings("unused")
        public ReadOnlyObjectProperty<AppointmentModelFilter> modelFilterProperty() {
            return modelFilter.getReadOnlyProperty();
        }

        AppointmentFilterItem(String text, AppointmentModelFilter modelFilter) {
            this.text = new ReadOnlyStringWrapper(this, "text", text);
            this.modelFilter = new ReadOnlyObjectWrapper<>(this, "modelFilter", modelFilter);
        }

        @Override
        public int hashCode() {
            return text.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return null != obj && obj instanceof AppointmentFilterItem && text.equals(((AppointmentFilterItem) obj).text);
        }

        @Override
        public String toString() {
            return text.get();
        }

    }

    private class InitialLoadTask extends Task<List<AppointmentDAO>> {

        private List<UserDAO> users;

        private InitialLoadTask() {
            updateTitle(AppResources.getResourceString(RESOURCEKEY_LOADINGUSERS));
        }

        @Override
        protected void succeeded() {
            super.succeeded();
            List<AppointmentDAO> result = getValue();
            if (null != users && !users.isEmpty()) {
                if (model.isNewRow()) {
                    users.forEach((t) -> unavailableUserNames.add(t.getUserName().toLowerCase()));
                } else {
                    int pk = model.getPrimaryKey();
                    users.forEach((t) -> {
                        if (t.getPrimaryKey() != pk) {
                            unavailableUserNames.add(t.getUserName().toLowerCase());
                        }
                    });
                }
            }
            if (null != result && !result.isEmpty()) {
                result.forEach((t) -> {
                    userAppointments.add(new AppointmentModel(t));
                });
            }
        }

        @Override
        protected List<AppointmentDAO> call() throws Exception {
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
            try (DbConnector dbConnector = new DbConnector()) {
                updateMessage(AppResources.getResourceString(RESOURCEKEY_CONNECTEDTODB));
                UserDAO.FactoryImpl uf = UserDAO.FACTORY;
                users = uf.load(dbConnector.getConnection(), uf.getAllItemsFilter());
                if (!filterOptions.isEmpty()) {
                    updateMessage(AppResources.getResourceString(RESOURCEKEY_LOADINGAPPOINTMENTS));
                    AppointmentDAO.FactoryImpl af = AppointmentDAO.FACTORY;
                    return af.load(dbConnector.getConnection(), filterOptions.get(0).getModelFilter().getDaoFilter());
                }
            }
            return null;
        }

    }

}
