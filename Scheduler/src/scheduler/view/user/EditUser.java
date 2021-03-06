package scheduler.view.user;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.ObjectProperty;
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
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.SingleSelectionModel;
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
import scheduler.dao.filter.AppointmentFilter;
import scheduler.events.AppointmentEvent;
import scheduler.events.AppointmentFailedEvent;
import scheduler.events.AppointmentOpRequestEvent;
import scheduler.events.AppointmentSuccessEvent;
import scheduler.events.ModelFailedEvent;
import scheduler.events.UserEvent;
import scheduler.model.ModelHelper;
import scheduler.model.ModelHelper.AppointmentHelper;
import scheduler.model.UserStatus;
import scheduler.model.fx.AppointmentModel;
import scheduler.model.fx.EntityModel;
import scheduler.model.fx.UserModel;
import scheduler.observables.BindingHelper;
import scheduler.util.AlertHelper;
import scheduler.util.DbConnector;
import static scheduler.util.NodeUtil.clearAndSelect;
import static scheduler.util.NodeUtil.collapseNode;
import static scheduler.util.NodeUtil.isInShownWindow;
import static scheduler.util.NodeUtil.restoreNode;
import scheduler.util.PwHash;
import scheduler.util.ThrowableConsumer;
import scheduler.util.WeakEventHandlingReference;
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
 * <h3>Event Handling</h3>
 * <h4>SCHEDULER_APPOINTMENT_OP_REQUEST</h4>
 * <dl>
 * <dt>{@link #appointmentsTableView} &#123; {@link scheduler.fx.ItemEditTableCellFactory#onItemActionRequest} &#125; (creates) {@link AppointmentOpRequestEvent} &#123;</dt>
 * <dd>{@link javafx.event.Event#eventType} = {@link AppointmentOpRequestEvent#APPOINTMENT_OP_REQUEST "SCHEDULER_APPOINTMENT_OP_REQUEST"} &larr;
 * {@link scheduler.events.OperationRequestEvent#OP_REQUEST_EVENT "SCHEDULER_OP_REQUEST_EVENT"} &larr; {@link scheduler.events.ModelEvent#MODEL_EVENT_TYPE "SCHEDULER_MODEL_EVENT"}
 * </dd>
 * </dl>
 * &#125; (fires) {@link #onItemActionRequest(AppointmentOpRequestEvent)}
 * <dl>
 * <dt>SCHEDULER_APPOINTMENT_EDIT_REQUEST {@link AppointmentOpRequestEvent} &#123; {@link javafx.event.Event#eventType} = {@link AppointmentOpRequestEvent#EDIT_REQUEST} &#125;</dt>
 * <dd>&rarr; null {@link EditAppointment#edit(AppointmentModel, javafx.stage.Window) EditAppointment.edit}(({@link AppointmentModel}) {@link scheduler.events.ModelEvent#getEntityModel()},
 * {@link javafx.stage.Window}) (creates) {@link scheduler.events.AppointmentEvent#APPOINTMENT_EVENT_TYPE "SCHEDULER_APPOINTMENT_EVENT"} &rArr;
 * {@link scheduler.model.fx.AppointmentModel.Factory}</dd>
 * <dt>SCHEDULER_APPOINTMENT_DELETE_REQUEST {@link AppointmentOpRequestEvent} &#123; {@link javafx.event.Event#eventType} = {@link AppointmentOpRequestEvent#DELETE_REQUEST}
 * &#125;</dt>
 * <dd>&rarr; null {@link scheduler.dao.AppointmentDAO.DeleteTask#DeleteTask(scheduler.model.fx.AppointmentModel, boolean) new AppointmentDAO.DeleteTask}({@link AppointmentOpRequestEvent},
 * {@code false}) (creates) {@link scheduler.events.AppointmentEvent#APPOINTMENT_EVENT_TYPE "SCHEDULER_APPOINTMENT_EVENT"} &rArr;
 * {@link scheduler.model.fx.AppointmentModel.Factory}</dd>
 * </dl>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/view/user/EditUser")
@FXMLResource("/scheduler/view/user/EditUser.fxml")
public final class EditUser extends VBox implements EditItem.ModelEditorController<UserModel> {

//    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(EditUser.class.getName()), Level.FINER);
    private static final Logger LOG = Logger.getLogger(EditUser.class.getName());

    public static void editNew(Window parentWindow, boolean keepOpen, Consumer<UserModel> beforeShow) throws IOException {
        UserModel model = UserDAO.FACTORY.createNew().cachedModel(true);
        if (null != beforeShow) {
            beforeShow.accept(model);
        }
        EditItem.showAndWait(parentWindow, EditUser.class, model, keepOpen);
    }

    public static void editNew(Window parentWindow, boolean keepOpen) throws IOException {
        editNew(parentWindow, keepOpen, null);
    }

    public static void edit(UserModel model, Window parentWindow, ThrowableConsumer<Stage, IOException> beforeShow) throws IOException {
        EditItem.showAndWait(parentWindow, EditUser.class, model, false, beforeShow);
    }

    public static void edit(UserModel model, Window parentWindow) throws IOException {
        edit(model, parentWindow, null);
    }

    //<editor-fold defaultstate="collapsed" desc="Instance Fields">
    private final ReadOnlyBooleanWrapper valid;
    private final ReadOnlyBooleanWrapper modified;
    private final ReadOnlyStringWrapper windowTitle;
    private final ObservableList<String> unavailableUserNames;
    private final ObservableList<UserStatus> userActiveStateOptions;
    private final ObservableList<AppointmentModel> userAppointments;
    private final ObservableList<AppointmentFilterItem> filterOptions;
    private final WeakEventHandlingReference<AppointmentSuccessEvent> appointmentInsertEventHandler;
    private final WeakEventHandlingReference<AppointmentSuccessEvent> appointmentUpdateEventHandler;
    private final WeakEventHandlingReference<AppointmentSuccessEvent> appointmentDeleteEventHandler;
    private ReadOnlyObjectProperty<AppointmentFilterItem> selectedFilter;
    private StringBinding normalizedUserName;
    private StringBinding passwordErrorMessage;
    private BooleanBinding userNameInvalid;
    private StringBinding userNameErrorMessage;
    private BooleanBinding passwordInvalid;
    private BooleanBinding validationBinding;
    private ObjectBinding<UserStatus> selectedStatus;
    private BooleanBinding modificationBinding;
    private StringExpression titleBinding;

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

    @FXML // fx:id="passwordLabel"
    private Label passwordLabel; // Value injected by FXMLLoader

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
    private ComboBox<AppointmentFilterItem> appointmentsFilterComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="appointmentsTableView"
    private TableView<AppointmentModel> appointmentsTableView; // Value injected by FXMLLoader

    @FXML // fx:id="newAppointmentButtonBar"
    private ButtonBar newAppointmentButtonBar; // Value injected by FXMLLoader

    //</editor-fold>
    public EditUser() {
        windowTitle = new ReadOnlyStringWrapper(this, "windowTitle", "");
        valid = new ReadOnlyBooleanWrapper(this, "valid", false);
        modified = new ReadOnlyBooleanWrapper(this, "modified", true);
        userActiveStateOptions = FXCollections.observableArrayList(UserStatus.values());
        unavailableUserNames = FXCollections.observableArrayList();
        userAppointments = FXCollections.observableArrayList();
        filterOptions = FXCollections.observableArrayList();
        appointmentInsertEventHandler = WeakEventHandlingReference.create(this::onAppointmentInserted);
        appointmentUpdateEventHandler = WeakEventHandlingReference.create(this::onAppointmentUpdated);
        appointmentDeleteEventHandler = WeakEventHandlingReference.create(this::onAppointmentDeleted);
    }

    @ModelEditor
    private void onModelInserted(UserEvent event) {
        LOG.entering(getClass().getName(), "onModelInserted", event);
        restoreNode(appointmentsFilterComboBox);
        restoreNode(appointmentsTableView);
        restoreNode(newAppointmentButtonBar);
        collapseNode(passwordLabel);
        restoreNode(changePasswordCheckBox);
    changePasswordCheckBox.setSelected(false);
        initEditMode();
        LOG.exiting(getClass().getName(), "onModelInserted");
    }

    @FXML
    @SuppressWarnings("incomplete-switch")
    private void onAppointmentsTableViewTableViewKeyReleased(KeyEvent event) {
        LOG.entering(getClass().getName(), "onAppointmentsTableViewTableViewKeyReleased", event);
        if (!(event.isAltDown() || event.isControlDown() || event.isMetaDown() || event.isShiftDown() || event.isShortcutDown())) {
            AppointmentModel item;
            switch (event.getCode()) {
                case DELETE:
                    item = appointmentsTableView.getSelectionModel().getSelectedItem();
                    if (null != item) {
                        deleteAppointment(item);
                    }
                    break;
                case ENTER:
                    item = appointmentsTableView.getSelectionModel().getSelectedItem();
                    if (null != item) {
                        editAppointment(item);
                    }
                    break;
            }
        }
        LOG.exiting(getClass().getName(), "onAppointmentsTableViewTableViewKeyReleased");
    }

    @FXML
    private void onDeleteAppointmentMenuItemAction(ActionEvent event) {
        LOG.entering(getClass().getName(), "onDeleteAppointmentMenuItemAction", event);
        AppointmentModel item = appointmentsTableView.getSelectionModel().getSelectedItem();
        if (null != item) {
            deleteAppointment(item);
        }
        LOG.exiting(getClass().getName(), "onDeleteAppointmentMenuItemAction");
    }

    @FXML
    private void onEditAppointmentMenuItemAction(ActionEvent event) {
        LOG.entering(getClass().getName(), "onEditAppointmentMenuItemAction", event);
        AppointmentModel item = appointmentsTableView.getSelectionModel().getSelectedItem();
        if (null != item) {
            editAppointment(item);
        }
        LOG.exiting(getClass().getName(), "onEditAppointmentMenuItemAction");
    }

    @FXML
    private void onItemActionRequest(AppointmentOpRequestEvent event) {
        LOG.entering(getClass().getName(), "onItemActionRequest", event);
        if (event.isEdit()) {
            try {
                EditAppointment.edit(event.getEntityModel(), getScene().getWindow());
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, "Error opening child window", ex);
            }
        } else {
            deleteAppointment(event.getEntityModel());
        }
        LOG.exiting(getClass().getName(), "onItemActionRequest");
    }

    @FXML
    private void onNewAppointmentButtonAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onNewAppointmentButtonAction", event);
        try {
            EditAppointment.editNew(null, model, getScene().getWindow());
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error opening child window", ex);
        }
        LOG.exiting(LOG.getName(), "onNewAppointmentButtonAction");
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    private void initialize() {
        LOG.entering(getClass().getName(), "initialize");
        assert userNameTextField != null : "fx:id=\"userNameTextField\" was not injected: check your FXML file 'EditUser.fxml'.";
        assert userNameErrorMessageLabel != null : "fx:id=\"userNameErrorMessageLabel\" was not injected: check your FXML file 'EditUser.fxml'.";
        assert changePasswordCheckBox != null : "fx:id=\"changePasswordCheckBox\" was not injected: check your FXML file 'EditUser.fxml'.";
        assert passwordLabel != null : "fx:id=\"passwordLabel\" was not injected: check your FXML file 'EditUser.fxml'.";
        assert passwordField != null : "fx:id=\"passwordField\" was not injected: check your FXML file 'EditUser.fxml'.";
        assert passwordErrorMessageLabel != null : "fx:id=\"passwordErrorMessageLabel\" was not injected: check your FXML file 'EditUser.fxml'.";
        assert confirmLabel != null : "fx:id=\"confirmLabel\" was not injected: check your FXML file 'EditUser.fxml'.";
        assert confirmPasswordField != null : "fx:id=\"confirmPasswordField\" was not injected: check your FXML file 'EditUser.fxml'.";
        assert activeComboBox != null : "fx:id=\"activeComboBox\" was not injected: check your FXML file 'EditUser.fxml'.";
        assert appointmentsFilterComboBox != null : "fx:id=\"appointmentsFilterComboBox\" was not injected: check your FXML file 'EditUser.fxml'.";
        assert appointmentsTableView != null : "fx:id=\"appointmentsTableView\" was not injected: check your FXML file 'EditUser.fxml'.";

        userNameTextField.setText(model.getUserName());
        activeComboBox.setItems(userActiveStateOptions);
        clearAndSelect(activeComboBox, model.getStatus());
        appointmentsTableView.setItems(userAppointments);
        appointmentsFilterComboBox.setItems(filterOptions);

        selectedFilter = appointmentsFilterComboBox.getSelectionModel().selectedItemProperty();
        
        normalizedUserName = BindingHelper.asNonNullAndWsNormalized(userNameTextField.textProperty());
        userNameErrorMessage = Bindings.createStringBinding(() -> {
            String n = normalizedUserName.get();
            if (n.isEmpty()) {
                return resources.getString(RESOURCEKEY_USERNAMECANNOTBEEMPTY);
            }
            if (unavailableUserNames.contains(n.toLowerCase())) {
                return resources.getString(RESOURCEKEY_USERNAMEINUSE);
            }
            return "";
        }, normalizedUserName, unavailableUserNames);
        userNameInvalid = userNameErrorMessage.isNotEmpty();
        userNameErrorMessageLabel.visibleProperty().bind(userNameInvalid);

        passwordErrorMessage = Bindings.when(changePasswordCheckBox.selectedProperty())
                .then(Bindings.createStringBinding(() -> {
                    String p = passwordField.getText();
                    String c = confirmPasswordField.getText();
                    if (p.trim().isEmpty()) {
                        return resources.getString(RESOURCEKEY_PASSWORDCANNOTBEEMPTY);
                    }
                    if (!p.equals(c)) {
                        return resources.getString(RESOURCEKEY_PASSWORDMISMATCH);
                    }
                    return "";
                }, passwordField.textProperty(), confirmPasswordField.textProperty()))
                .otherwise("");

        passwordInvalid = passwordErrorMessage.isNotEmpty();
        selectedStatus = Bindings.select(activeComboBox.selectionModelProperty(), "selectedItem");
        validationBinding = userNameInvalid.or(passwordInvalid).not();
        valid.set(validationBinding.get());
        validationBinding.addListener((observable, oldValue, newValue) -> valid.set(newValue));
        WaitTitledPane pane = WaitTitledPane.create();
        pane.addOnFailAcknowledged((evt) -> getScene().getWindow().hide())
                .addOnCancelAcknowledged((evt) -> getScene().getWindow().hide());
        if (model.isNewRow()) {
            passwordErrorMessageLabel.visibleProperty().bind(passwordInvalid);
            passwordErrorMessageLabel.textProperty().bind(passwordErrorMessage);
            waitBorderPane.startNow(pane, new LoadExistingUsersTask());
            changePasswordCheckBox.setSelected(true);
            collapseNode(appointmentsFilterComboBox);
            collapseNode(appointmentsTableView);
            collapseNode(newAppointmentButtonBar);
            windowTitle.set(resources.getString(RESOURCEKEY_ADDNEWUSER));
        } else {
            collapseNode(passwordLabel);
            restoreNode(changePasswordCheckBox);
            waitBorderPane.startNow(pane, new InitialLoadTask());
            initEditMode();
        }
        LOG.exiting(getClass().getName(), "initialize");
    }
    
    private void editAppointment(AppointmentModel item) {
        try {
            EditAppointment.edit(item, getScene().getWindow());
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error opening child window", ex);
        }
    }

    private void deleteAppointment(AppointmentModel target) {
        AppointmentOpRequestEvent deleteRequestEvent = new AppointmentOpRequestEvent(target, this, true);
        Event.fireEvent(target.dataObject(), deleteRequestEvent);
        Stage stage = (Stage) getScene().getWindow();
        if (deleteRequestEvent.isCanceled()) {
            AlertHelper.showWarningAlert(stage, deleteRequestEvent.getCancelMessage(), ButtonType.OK);
        } else {
            AlertHelper.showWarningAlert(stage, LOG,
                    AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONFIRMDELETE),
                    AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO).ifPresent((t) -> {
                if (t == ButtonType.YES) {
                    DataAccessObject.DeleteDaoTask<AppointmentDAO, AppointmentModel> task = AppointmentModel.FACTORY.createDeleteTask(target);
                    task.setOnSucceeded((e) -> {
                        AppointmentEvent appointmentEvent = (AppointmentEvent) task.getValue();
                        if (null != appointmentEvent && appointmentEvent instanceof AppointmentFailedEvent) {
                            scheduler.util.AlertHelper.showWarningAlert(getScene().getWindow(), "Delete Failure",
                                    ((ModelFailedEvent<AppointmentDAO, AppointmentModel>) appointmentEvent).getMessage(), ButtonType.OK);
                        }
                    });
                    waitBorderPane.startNow(task);
                }
            });
        }
    }

    private void changePasswordCheckBoxChanged(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        LOG.entering(getClass().getName(), "changePasswordCheckBoxChanged", new Object[]{oldValue, newValue});
        if (newValue) {
            restoreNode(passwordField);
            restoreNode(confirmLabel);
            restoreNode(confirmPasswordField);
            restoreNode(passwordErrorMessageLabel);
            passwordErrorMessageLabel.visibleProperty().bind(passwordInvalid);
            passwordErrorMessageLabel.textProperty().bind(passwordErrorMessage);
        } else {
            passwordErrorMessageLabel.visibleProperty().unbind();
            passwordErrorMessageLabel.textProperty().unbind();
            collapseNode(passwordField);
            collapseNode(confirmLabel);
            collapseNode(confirmPasswordField);
            collapseNode(passwordErrorMessageLabel);
        }
        LOG.exiting(getClass().getName(), "changePasswordCheckBoxChanged");
    }

    private void initEditMode() {
        titleBinding = Bindings.format(resources.getString(RESOURCEKEY_EDITUSER), normalizedUserName);
        windowTitle.bind(titleBinding);
        LocalDate today = LocalDate.now();
        UserDAO dao = model.dataObject();
        filterOptions.add(new AppointmentFilterItem(resources.getString(RESOURCEKEY_CURRENTANDFUTURE),
                AppointmentModelFilter.of(today, null, dao)));
        filterOptions.add(new AppointmentFilterItem(resources.getString(RESOURCEKEY_CURRENTAPPOINTMENTS),
                AppointmentModelFilter.of(today, today.plusDays(1), dao)));
        filterOptions.add(new AppointmentFilterItem(resources.getString(RESOURCEKEY_PASTAPPOINTMENTS),
                AppointmentModelFilter.of(null, today, dao)));
        filterOptions.add(new AppointmentFilterItem(resources.getString(RESOURCEKEY_ALLAPPOINTMENTS), AppointmentModelFilter.of(dao)));
        appointmentsFilterComboBox.getSelectionModel().selectFirst();
        changePasswordCheckBoxChanged(changePasswordCheckBox.selectedProperty(), false, changePasswordCheckBox.isSelected());
        changePasswordCheckBox.selectedProperty().addListener(this::changePasswordCheckBoxChanged);
        modified.set(false);
        modificationBinding = model.rowStateProperty().isEqualTo(DataRowState.NEW)
                .or(changePasswordCheckBox.selectedProperty().or(selectedStatus.isNotEqualTo(model.getStatus())))
                .or(normalizedUserName.isNotEqualTo(BindingHelper.asNonNullAndWsNormalized(model.userNameProperty())));
        modificationBinding.addListener((observable, oldValue, newValue) -> modified.set(newValue));
        selectedFilter.addListener((observable, oldValue, newValue) -> waitBorderPane.startNow(new AppointmentReloadTask()));
        AppointmentModel.FACTORY.addEventHandler(AppointmentSuccessEvent.INSERT_SUCCESS, appointmentInsertEventHandler.getWeakEventHandler());
        AppointmentModel.FACTORY.addEventHandler(AppointmentSuccessEvent.UPDATE_SUCCESS, appointmentUpdateEventHandler.getWeakEventHandler());
        AppointmentModel.FACTORY.addEventHandler(AppointmentSuccessEvent.DELETE_SUCCESS, appointmentDeleteEventHandler.getWeakEventHandler());
    }

    //<editor-fold defaultstate="collapsed" desc="Properties">
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

    //</editor-fold>
    @Override
    public EntityModel.EntityModelFactory<UserDAO, UserModel> modelFactory() {
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

    @Override
    public boolean applyChanges() {
        model.setUserName(userNameTextField.getText());
        model.setStatus(activeComboBox.getSelectionModel().getSelectedItem());
        if (changePasswordCheckBox.isSelected()) {
            PwHash pw = new PwHash(passwordField.getText(), true);
            model.setPassword(pw.getEncodedHash());
        }
        return true;
    }

    private synchronized void onAppointmentInserted(AppointmentSuccessEvent event) {
        LOG.entering(getClass().getName(), "onAppointmentInserted", event);
        if (isInShownWindow(this) && model.getRowState() != DataRowState.NEW) {
            AppointmentModel appointment = event.getEntityModel();
            AppointmentFilterItem filter = selectedFilter.get();
            if ((null == filter) ? appointment.getCustomer().getPrimaryKey() == model.getPrimaryKey() : filter.getModelFilter().test(appointment)) {
                userAppointments.add(appointment);
            }
        }
        LOG.exiting(getClass().getName(), "onAppointmentInserted");
    }

    private synchronized void onAppointmentUpdated(AppointmentSuccessEvent event) {
        LOG.entering(getClass().getName(), "onAppointmentUpdated", event);
        if (isInShownWindow(this) && model.getRowState() != DataRowState.NEW) {
            AppointmentModel appointment = event.getEntityModel();
            AppointmentFilterItem filter = selectedFilter.get();
            AppointmentModel m = ModelHelper.findByPrimaryKey(appointment.getPrimaryKey(), userAppointments).orElse(null);
            if (null != m) {
                if ((null == filter) ? appointment.getCustomer().getPrimaryKey() != model.getPrimaryKey() : !filter.getModelFilter().test(m)) {
                    userAppointments.remove(m);
                }
            } else if ((null == filter) ? appointment.getCustomer().getPrimaryKey() == model.getPrimaryKey() : filter.getModelFilter().test(appointment)) {
                userAppointments.add(appointment);
            }
        }
        LOG.exiting(getClass().getName(), "onAppointmentUpdated");
    }

    private void onAppointmentDeleted(AppointmentSuccessEvent event) {
        LOG.entering(getClass().getName(), "onAppointmentDeleted", event);
        if (isInShownWindow(this) && model.getRowState() != DataRowState.NEW) {
            AppointmentModel.FACTORY.find(userAppointments, event.getEntityModel()).ifPresent(userAppointments::remove);
        }
        LOG.exiting(getClass().getName(), "onAppointmentDeleted");
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
            LOG.entering("scheduler.view.user.EditUser.InitialLoadTask", "succeeded");
            super.succeeded();
            List<AppointmentDAO> result = getValue();
            if (null != result && !result.isEmpty()) {
                result.forEach((t) -> {
                    userAppointments.add(t.cachedModel(true));
                });
            }
            loadUsers(users);
            AppointmentModel.FACTORY.addEventHandler(AppointmentSuccessEvent.INSERT_SUCCESS, appointmentInsertEventHandler.getWeakEventHandler());
            AppointmentModel.FACTORY.addEventHandler(AppointmentSuccessEvent.UPDATE_SUCCESS, appointmentUpdateEventHandler.getWeakEventHandler());
            AppointmentModel.FACTORY.addEventHandler(AppointmentSuccessEvent.DELETE_SUCCESS, appointmentDeleteEventHandler.getWeakEventHandler());
            LOG.exiting("scheduler.view.user.EditUser.InitialLoadTask", "succeeded");
        }

        @Override
        protected List<AppointmentDAO> call() throws Exception {
            LOG.entering("scheduler.view.user.EditUser.InitialLoadTask", "call");
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
            try (DbConnector dbConnector = new DbConnector()) {
                updateMessage(AppResources.getResourceString(RESOURCEKEY_CONNECTEDTODB));
                UserDAO.FactoryImpl uf = UserDAO.FACTORY;
                users = uf.load(dbConnector.getConnection(), uf.getAllItemsFilter());
                if (!filterOptions.isEmpty()) {
                    updateMessage(AppResources.getResourceString(RESOURCEKEY_LOADINGAPPOINTMENTS));
                    AppointmentDAO.FactoryImpl af = AppointmentDAO.FACTORY;
                    LOG.exiting("scheduler.view.user.EditUser.InitialLoadTask", "call");
                    return af.load(dbConnector.getConnection(), filterOptions.get(0).getModelFilter().getDaoFilter());
                }
            }
            LOG.exiting("scheduler.view.user.EditUser.InitialLoadTask", "call");
            return null;
        }

    }

    private class LoadExistingUsersTask extends Task<List<UserDAO>> {

        private LoadExistingUsersTask() {
            updateTitle(AppResources.getResourceString(RESOURCEKEY_LOADINGUSERS));
        }

        @Override
        protected void succeeded() {
            LOG.entering("scheduler.view.user.EditUser.LoadExistingUsersTask", "succeeded");
            super.succeeded();
            loadUsers(getValue());
            LOG.exiting("scheduler.view.user.EditUser.LoadExistingUsersTask", "succeeded");
        }

        @Override
        protected List<UserDAO> call() throws Exception {
            LOG.entering("scheduler.view.user.EditUser.LoadExistingUsersTask", "call");
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
            try (DbConnector dbConnector = new DbConnector()) {
                updateMessage(AppResources.getResourceString(RESOURCEKEY_CONNECTEDTODB));
                UserDAO.FactoryImpl uf = UserDAO.FACTORY;
                LOG.exiting("scheduler.view.user.EditUser.LoadExistingUsersTask", "call");
                return uf.load(dbConnector.getConnection(), uf.getAllItemsFilter());
            }
        }

    }

    private class AppointmentReloadTask extends Task<List<AppointmentDAO>> {

        private final AppointmentFilter filter;

        private AppointmentReloadTask() {
            updateTitle(AppResources.getResourceString(RESOURCEKEY_LOADINGAPPOINTMENTS));
            AppointmentFilterItem filterItem = selectedFilter.get();
            filter = (null == filterItem) ? null : filterItem.getModelFilter().getDaoFilter();
        }

        @Override
        protected void succeeded() {
            LOG.entering(getClass().getName(), "succeeded");
            List<AppointmentDAO> result = getValue();
            userAppointments.clear();
            if (null != result && !result.isEmpty()) {
                result.forEach((t) -> {
                    userAppointments.add(t.cachedModel(true));
                });
                userAppointments.sort(AppointmentHelper::compareByDates);
            }
            LOG.exiting(getClass().getName(), "succeeded");
        }

        @Override
        protected List<AppointmentDAO> call() throws Exception {
            LOG.entering(getClass().getName(), "call");
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
            try (DbConnector dbConnector = new DbConnector()) {
                updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTEDTODB));
                AppointmentDAO.FactoryImpl af = AppointmentDAO.FACTORY;
                LOG.exiting(getClass().getName(), "call");
                return af.load(dbConnector.getConnection(), filter);
            }
        }

    }

}
