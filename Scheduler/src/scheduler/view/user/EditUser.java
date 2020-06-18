package scheduler.view.user;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.WeakEventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import scheduler.AppResourceKeys;
import static scheduler.AppResourceKeys.RESOURCEKEY_CONNECTEDTODB;
import static scheduler.AppResourceKeys.RESOURCEKEY_LOADINGUSERS;
import scheduler.AppResources;
import scheduler.dao.AppointmentDAO;
import scheduler.dao.DataAccessObject;
import scheduler.dao.DataRowState;
import scheduler.dao.UserDAO;
import scheduler.events.AppointmentEvent;
import scheduler.events.DbOperationType;
import scheduler.events.EventEvaluationStatus;
import scheduler.events.UserEvent;
import scheduler.model.UserStatus;
import scheduler.model.ui.AppointmentModel;
import scheduler.model.ui.FxRecordModel;
import scheduler.model.ui.UserModel;
import scheduler.observables.BindingHelper;
import scheduler.util.AlertHelper;
import scheduler.util.DbConnector;
import scheduler.util.LogHelper;
import static scheduler.util.NodeUtil.collapseNode;
import static scheduler.util.NodeUtil.restoreNode;
import scheduler.util.PwHash;
import scheduler.view.EditItem;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.annotations.ModelEditor;
import scheduler.view.appointment.AppointmentModelFilter;
import scheduler.view.appointment.EditAppointment;
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
public final class EditUser extends VBox implements EditItem.ModelEditor<UserDAO, UserModel, UserEvent> {

    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(EditUser.class.getName()), Level.FINER);

    public static UserModel editNew(Window parentWindow, boolean keepOpen) throws IOException {
        UserModel.Factory factory = UserModel.FACTORY;
        return EditItem.showAndWait(parentWindow, EditUser.class, factory.createNew(factory.getDaoFactory().createNew()), keepOpen);
    }

    public static UserModel edit(UserModel model, Window parentWindow) throws IOException {
        return EditItem.showAndWait(parentWindow, EditUser.class, model, false);
    }

    private final ReadOnlyBooleanWrapper valid;
    private final ReadOnlyBooleanWrapper modified;
    private final ReadOnlyStringWrapper windowTitle;
    private final ObservableList<String> unavailableUserNames;
    private final ObservableList<UserStatus> userActiveStateOptions;
    private final ObservableList<AppointmentModel> userAppointments;
    private final ObservableList<AppointmentFilterItem> filterOptions;
    private ObjectBinding<AppointmentFilterItem> selectedFilter;
    private StringBinding normalizedUserName;
    private BooleanBinding validationBinding;
    private ObjectBinding<UserStatus> selectedStatus;
    private BooleanBinding modificationBinding;

    @ModelEditor
    private UserModel model;

    @ModelEditor
    private WaitBorderPane waitBorderPane;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

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

    @FXML // fx:id="appointmentsFilterComboBox"
    private ComboBox<String> appointmentsFilterComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="appointmentsTableView"
    private TableView<AppointmentModel> appointmentsTableView; // Value injected by FXMLLoader

    public EditUser() {
        windowTitle = new ReadOnlyStringWrapper(this, "", "");
        valid = new ReadOnlyBooleanWrapper(this, "", false);
        modified = new ReadOnlyBooleanWrapper(this, "modified", true);
        userActiveStateOptions = FXCollections.observableArrayList(UserStatus.values());
        unavailableUserNames = FXCollections.observableArrayList();
        userAppointments = FXCollections.observableArrayList();
        filterOptions = FXCollections.observableArrayList();
        addEventHandler(UserEvent.INSERTING_EVENT_TYPE, this::onUserUpdating);
        addEventHandler(UserEvent.UPDATING_EVENT_TYPE, this::onUserUpdating);
        addEventHandler(UserEvent.INSERTED_EVENT_TYPE, this::onUserInserted);
    }

    private void onUserUpdating(UserEvent event) {
        if (event.getStatus() != EventEvaluationStatus.EVALUATING) {
            return;
        }
        model.setUserName(userNameTextField.getText());
        model.setStatus(activeComboBox.getSelectionModel().getSelectedItem());
        if (changePasswordCheckBox.isSelected()) {
            PwHash pw = new PwHash(passwordField.getText(), true);
            model.setPassword(pw.getEncodedHash());
        }
    }

    private void onUserInserted(UserEvent event) {
        changePasswordCheckBox.setDisable(false);
        changePasswordCheckBox.setSelected(false);
        restoreNode(appointmentsFilterComboBox);
        restoreNode(appointmentsTableView);
        initEditMode();
    }

    @FXML
    @SuppressWarnings("incomplete-switch")
    private void onAppointmentsTableViewTableViewKeyReleased(KeyEvent event) {
        if (!(event.isAltDown() || event.isControlDown() || event.isMetaDown() || event.isShiftDown() || event.isShortcutDown())) {
            AppointmentModel item;
            switch (event.getCode()) {
                case DELETE:
                    item = appointmentsTableView.getSelectionModel().getSelectedItem();
                    if (null != item) {
                        onItemActionRequest(new AppointmentEvent(item, event.getSource(), this, DbOperationType.DELETE_REQUEST));
                    }
                    break;
                case ENTER:
                    item = appointmentsTableView.getSelectionModel().getSelectedItem();
                    if (null != item) {
                        onItemActionRequest(new AppointmentEvent(item, event.getSource(), this, DbOperationType.EDIT_REQUEST));
                    }
                    break;
            }
        }
    }

    @FXML
    private void onDeleteAppointmentMenuItemAction(ActionEvent event) {
        AppointmentModel item = appointmentsTableView.getSelectionModel().getSelectedItem();
        if (null != item) {
            onItemActionRequest(new AppointmentEvent(item, event.getSource(), this, DbOperationType.DELETE_REQUEST));
        }
    }

    @FXML
    private void onEditAppointmentMenuItemAction(ActionEvent event) {
        AppointmentModel item = appointmentsTableView.getSelectionModel().getSelectedItem();
        if (null != item) {
            onItemActionRequest(new AppointmentEvent(item, event.getSource(), this, DbOperationType.EDIT_REQUEST));
        }
    }

    @FXML
    @SuppressWarnings("incomplete-switch")
    private void onItemActionRequest(AppointmentEvent event) {
        AppointmentModel item;
        if (event.isConsumed() || null == (item = event.getModel())) {
            return;
        }
        switch (event.getOperation()) {
            case EDIT_REQUEST:
                try {
                    EditAppointment.edit(item, getScene().getWindow());
                } catch (IOException ex) {
                    LOG.log(Level.SEVERE, "Error opening child window", ex);
                }
                event.consume();
                break;
            case DELETE_REQUEST:
                Optional<ButtonType> response = AlertHelper.showWarningAlert((Stage) getScene().getWindow(), LOG,
                        AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONFIRMDELETE),
                        AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO);
                if (response.isPresent() && response.get() == ButtonType.YES) {
                    waitBorderPane.startNow(new DataAccessObject.DeleteTask<>(event));
                }
                event.consume();
                break;
        }
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    private void initialize() {
        assert userNameTextField != null : "fx:id=\"userNameTextField\" was not injected: check your FXML file 'EditUser.fxml'.";
        assert userNameErrorMessageLabel != null : "fx:id=\"userNameErrorMessageLabel\" was not injected: check your FXML file 'EditUser.fxml'.";
        assert changePasswordCheckBox != null : "fx:id=\"changePasswordCheckBox\" was not injected: check your FXML file 'EditUser.fxml'.";
        assert passwordField != null : "fx:id=\"passwordField\" was not injected: check your FXML file 'EditUser.fxml'.";
        assert passwordErrorMessageLabel != null : "fx:id=\"passwordErrorMessageLabel\" was not injected: check your FXML file 'EditUser.fxml'.";
        assert confirmLabel != null : "fx:id=\"confirmLabel\" was not injected: check your FXML file 'EditUser.fxml'.";
        assert confirmPasswordField != null : "fx:id=\"confirmPasswordField\" was not injected: check your FXML file 'EditUser.fxml'.";
        assert activeComboBox != null : "fx:id=\"activeComboBox\" was not injected: check your FXML file 'EditUser.fxml'.";
        assert appointmentsFilterComboBox != null : "fx:id=\"appointmentsFilterComboBox\" was not injected: check your FXML file 'EditUser.fxml'.";
        assert appointmentsTableView != null : "fx:id=\"appointmentsTableView\" was not injected: check your FXML file 'EditUser.fxml'.";

        activeComboBox.setItems(userActiveStateOptions);
        appointmentsTableView.setItems(userAppointments);

        selectedFilter = Bindings.select(appointmentsFilterComboBox.selectionModelProperty(), "selectedItem");

        normalizedUserName = BindingHelper.asNonNullAndWsNormalized(userNameTextField.textProperty());
        StringBinding userNameErrorMessage = Bindings.createStringBinding(() -> {
            String n = normalizedUserName.get();
            if (n.isEmpty()) {
                return resources.getString(RESOURCEKEY_USERNAMECANNOTBEEMPTY);
            }
            if (unavailableUserNames.contains(n.toLowerCase())) {
                return resources.getString(RESOURCEKEY_USERNAMEINUSE);
            }
            return "";
        }, normalizedUserName, unavailableUserNames);
        BooleanBinding userNameInvalid = userNameErrorMessage.isNotEmpty();
        userNameErrorMessageLabel.visibleProperty().bind(userNameInvalid);

        StringBinding passwordErrorMessage = Bindings.when(changePasswordCheckBox.selectedProperty())
                .then(Bindings.createStringBinding(() -> {
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
                }, passwordField.textProperty(), confirmPasswordField.textProperty()))
                .otherwise("");

        BooleanBinding passwordInvalid = passwordErrorMessage.isNotEmpty();
        passwordErrorMessageLabel.visibleProperty().bind(passwordInvalid);
        passwordErrorMessageLabel.textProperty().bind(passwordErrorMessage);
        changePasswordCheckBox.selectedProperty().addListener(this::changePasswordCheckBoxChanged);
        userNameTextField.textProperty().addListener((observable, oldValue, newValue) -> updateValidation());
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> updateValidation());
        confirmPasswordField.textProperty().addListener((observable, oldValue, newValue) -> updateValidation());
        selectedStatus = Bindings.select(activeComboBox.selectionModelProperty(), "selectedItem");
        validationBinding = userNameInvalid.or(passwordInvalid).not();
        modificationBinding = model.rowStateProperty().isEqualTo(DataRowState.NEW)
                .or(changePasswordCheckBox.selectedProperty().or(selectedStatus.isNotEqualTo(model.getStatus())))
                .or(normalizedUserName.isNotEqualTo(BindingHelper.asNonNullAndWsNormalized(model.userNameProperty())));

        AppointmentModel.FACTORY.addEventHandler(AppointmentEvent.INSERTED_EVENT_TYPE, new WeakEventHandler<>(this::onAppointmentAdded));
        AppointmentModel.FACTORY.addEventHandler(AppointmentEvent.UPDATED_EVENT_TYPE, new WeakEventHandler<>(this::onAppointmentUpdated));
        AppointmentModel.FACTORY.addEventHandler(AppointmentEvent.DELETED_EVENT_TYPE, new WeakEventHandler<>(this::onAppointmentDeleted));

        WaitTitledPane pane = new WaitTitledPane();
        pane.addOnFailAcknowledged((evt) -> getScene().getWindow().hide())
                .addOnCancelAcknowledged((evt) -> getScene().getWindow().hide());
        if (model.isNewRow()) {
            waitBorderPane.startNow(pane, new LoadExistingUsersTask());
            changePasswordCheckBox.setSelected(true);
            changePasswordCheckBox.setDisable(true);
            collapseNode(appointmentsFilterComboBox);
            collapseNode(appointmentsTableView);
            windowTitle.set(resources.getString(RESOURCEKEY_ADDNEWUSER));
        } else {
            waitBorderPane.startNow(pane, new InitialLoadTask());
            initEditMode();
        }
        changePasswordCheckBoxChanged(changePasswordCheckBox.selectedProperty(), false, changePasswordCheckBox.isSelected());
    }

    private void updateValidation() {
        valid.set(validationBinding.get());
        modified.set(modificationBinding.get());
    }

    private void changePasswordCheckBoxChanged(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        if (newValue) {
            restoreNode(passwordField);
            restoreNode(confirmLabel);
            restoreNode(confirmPasswordField);
            restoreNode(passwordErrorMessageLabel);
        } else {
            collapseNode(passwordField);
            collapseNode(confirmLabel);
            collapseNode(confirmPasswordField);
            collapseNode(passwordErrorMessageLabel);
        }
        updateValidation();
    }

    private void initEditMode() {
        windowTitle.bind(Bindings.format(resources.getString(RESOURCEKEY_EDITUSER), normalizedUserName));
        LocalDate today = LocalDate.now();
        UserDAO dao = model.dataObject();
        filterOptions.add(new AppointmentFilterItem(resources.getString(RESOURCEKEY_CURRENTANDFUTURE),
                AppointmentModelFilter.of(today, null, dao)));
        filterOptions.add(new AppointmentFilterItem(resources.getString(RESOURCEKEY_CURRENTAPPOINTMENTS),
                AppointmentModelFilter.of(today, today.plusDays(1), dao)));
        filterOptions.add(new AppointmentFilterItem(resources.getString(RESOURCEKEY_PASTAPPOINTMENTS),
                AppointmentModelFilter.of(null, today, dao)));
        filterOptions.add(new AppointmentFilterItem(resources.getString(RESOURCEKEY_ALLAPPOINTMENTS), AppointmentModelFilter.of(dao)));
    }

    private void onAppointmentAdded(AppointmentEvent event) {
        LOG.info(() -> String.format("%s event handled", event.getEventType().getName()));
        if (model.getRowState() != DataRowState.NEW) {
            AppointmentDAO dao = event.getDataAccessObject();
            // XXX: Check to see if we need to get/set model
            AppointmentFilterItem filter = selectedFilter.get();
            if ((null == filter) ? dao.getCustomer().getPrimaryKey() == model.getPrimaryKey() : filter.getModelFilter().getDaoFilter().test(dao)) {
                userAppointments.add(new AppointmentModel(dao));
            }
        }
    }

    private void onAppointmentUpdated(AppointmentEvent event) {
        LOG.info(() -> String.format("%s event handled", event.getEventType().getName()));
        if (model.getRowState() != DataRowState.NEW) {
            AppointmentDAO dao = event.getDataAccessObject();
            // XXX: Check to see if we need to get/set model
            AppointmentFilterItem filter = selectedFilter.get();
            int pk = dao.getPrimaryKey();
            AppointmentModel m = userAppointments.stream().filter((t) -> t.getPrimaryKey() == pk).findFirst().orElse(null);
            if (null != m) {
                if ((null == filter) ? dao.getCustomer().getPrimaryKey() != model.getPrimaryKey() : !filter.getModelFilter().test(m)) {
                    userAppointments.remove(m);
                }
            } else if ((null == filter) ? dao.getCustomer().getPrimaryKey() == model.getPrimaryKey() : filter.getModelFilter().getDaoFilter().test(dao)) {
                userAppointments.add(new AppointmentModel(dao));
            }
        }
    }

    private void onAppointmentDeleted(AppointmentEvent event) {
        LOG.info(() -> String.format("%s event handled", event.getEventType().getName()));
        if (model.getRowState() != DataRowState.NEW) {
            AppointmentDAO dao = event.getDataAccessObject();
            // XXX: Check to see if we need to get/set model
            int pk = dao.getPrimaryKey();
            userAppointments.stream().filter((t) -> t.getPrimaryKey() == pk).findFirst().ifPresent((t) -> userAppointments.remove(t));
        }
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
    public FxRecordModel.ModelFactory<UserDAO, UserModel, UserEvent> modelFactory() {
        return UserModel.FACTORY;
    }

    private void loadUsers(List<UserDAO> users) {
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
    }

    private class AppointmentFilterItem {

        private final ReadOnlyStringWrapper text;
        private final ReadOnlyObjectWrapper<AppointmentModelFilter> modelFilter;

        AppointmentFilterItem(String text, AppointmentModelFilter modelFilter) {
            this.text = new ReadOnlyStringWrapper(this, "text", text);
            this.modelFilter = new ReadOnlyObjectWrapper<>(this, "modelFilter", modelFilter);
        }

        @SuppressWarnings("unused")
        public String getText() {
            return text.get();
        }

        @SuppressWarnings("unused")
        public ReadOnlyStringProperty textProperty() {
            return text.getReadOnlyProperty();
        }

        public AppointmentModelFilter getModelFilter() {
            return modelFilter.get();
        }

        @SuppressWarnings("unused")
        public ReadOnlyObjectProperty<AppointmentModelFilter> modelFilterProperty() {
            return modelFilter.getReadOnlyProperty();
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
            if (null != result && !result.isEmpty()) {
                result.forEach((t) -> {
                    userAppointments.add(new AppointmentModel(t));
                });
            }
            loadUsers(users);
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

    private class LoadExistingUsersTask extends Task<List<UserDAO>> {

        private LoadExistingUsersTask() {
            updateTitle(AppResources.getResourceString(RESOURCEKEY_LOADINGUSERS));
        }

        @Override
        protected void succeeded() {
            super.succeeded();
            loadUsers(getValue());
        }

        @Override
        protected List<UserDAO> call() throws Exception {
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
            try (DbConnector dbConnector = new DbConnector()) {
                updateMessage(AppResources.getResourceString(RESOURCEKEY_CONNECTEDTODB));
                UserDAO.FactoryImpl uf = UserDAO.FACTORY;
                return uf.load(dbConnector.getConnection(), uf.getAllItemsFilter());
            }
        }

    }

}
