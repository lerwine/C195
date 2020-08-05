package scheduler.view.appointment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import scheduler.AppResourceKeys;
import scheduler.AppResources;
import scheduler.dao.AppointmentDAO;
import scheduler.dao.DataRowState;
import scheduler.dao.filter.AppointmentFilter;
import scheduler.model.ModelHelper;
import scheduler.model.fx.AppointmentModel;
import scheduler.model.fx.CustomerModel;
import scheduler.model.fx.UserModel;
import scheduler.util.AlertHelper;
import scheduler.util.DbConnector;
import scheduler.util.LogHelper;
import static scheduler.util.NodeUtil.clearAndSelectEntity;
import static scheduler.util.NodeUtil.collapseNode;
import static scheduler.util.NodeUtil.restoreNode;
import static scheduler.util.NodeUtil.setErrorMessage;
import static scheduler.util.NodeUtil.setWarningMessage;
import scheduler.util.Tuple;
import scheduler.view.EditItem;
import static scheduler.view.appointment.EditAppointmentResourceKeys.*;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public final class AppointmentConflictsController {

    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(AppointmentConflictsController.class.getName()), Level.FINER);
//    private static final Logger LOG = Logger.getLogger(AppointmentConflictsController.class.getName());

    private final EditAppointment editAppointmentControl;
    private final ReadOnlyObjectWrapper<Tuple<CustomerModel, UserModel>> currentParticipants;
    private final ReadOnlyObjectWrapper<ConflictCheckStatus> conflictCheckStatus;
    private final ReadOnlyStringWrapper conflictMessage;
    private final ObservableList<AppointmentModel> allAppointments;
    private final ObservableList<AppointmentModel> conflictingAppointments;
    private final DateRangeController dateRange;
    private BooleanBinding valid;
    private LoadParticipantsAppointmentsTask currentTask = null;
    private ReadOnlyObjectProperty<CustomerModel> selectedCustomer;
    private ReadOnlyObjectProperty<UserModel> selectedUser;
    private ResourceBundle resources;
    private BooleanBinding modified;

    AppointmentConflictsController(EditAppointment editAppointmentControl) {
        this.editAppointmentControl = editAppointmentControl;
        currentParticipants = new ReadOnlyObjectWrapper<>(this, "currentParticipants", null);
        conflictCheckStatus = new ReadOnlyObjectWrapper<>(this, "conflictCheckStatus", ConflictCheckStatus.NO_CONFLICT);
        conflictMessage = new ReadOnlyStringWrapper(this, "conflictMessage", "");
        allAppointments = FXCollections.observableArrayList();
        conflictingAppointments = FXCollections.observableArrayList();
        dateRange = new DateRangeController(editAppointmentControl);
    }

    public BooleanBinding modifiedBinding() {
        return modified;
    }
    
    public ReadOnlyObjectProperty<CustomerModel> selectedCustomerProperty() {
        return selectedCustomer;
    }

    public ReadOnlyObjectProperty<UserModel> selectedUserProperty() {
        return selectedUser;
    }

    public LocalDateTime getStartDateTimeValue() {
        return dateRange.getStartDateTimeValue();
    }

    public String getStartValidationMessage() {
        return dateRange.getStartValidationMessage();
    }

    public LocalDateTime getEndDateTimeValue() {
        return dateRange.getEndDateTimeValue();
    }

    public boolean isWithinBusinessHours() {
        return dateRange.isWithinBusinessHours();
    }

    public String getConflictMessage() {
        return conflictMessage.get();
    }

    ConflictCheckStatus getConflictCheckStatus() {
        return conflictCheckStatus.get();
    }

    public boolean isValid() {
        return valid.get();
    }

    public BooleanBinding validBinding() {
        return valid;
    }

    void initialize() {
        LOG.entering(LOG.getName(), "initialize");
        AppointmentModel model = editAppointmentControl.getModel();
        dateRange.initialize();
        resources = editAppointmentControl.getResources();
        editAppointmentControl.getConflictingAppointmentsTableView().setItems(conflictingAppointments);
        final SingleSelectionModel<CustomerModel> customerSelectionModel = editAppointmentControl.getCustomerComboBox().getSelectionModel();
        selectedCustomer = customerSelectionModel.selectedItemProperty();
        selectedUser = editAppointmentControl.getUserComboBox().getSelectionModel().selectedItemProperty();
        CustomerModel customer = selectedCustomer.get();
        Label customerValidationLabel = editAppointmentControl.getCustomerValidationLabel();
        customerValidationLabel.setVisible(null == customer);
        UserModel user = selectedUser.get();
        Label userValidationLabel = editAppointmentControl.getUserValidationLabel();
        userValidationLabel.setVisible(null == user);
        if (null != customer && null != user) {
            currentParticipants.set(Tuple.of(customer, user));
            conflictCheckStatus.set(ConflictCheckStatus.NOT_CHECKED);
        }
        selectedCustomer.addListener((observable, oldValue, newValue) -> {
            LOG.entering(LogHelper.toLambdaSourceClass(LOG, "initialize", "selectedCustomer#value"), "changed", new Object[]{oldValue, newValue});
            if (null == newValue) {
                editAppointmentControl.getCustomerEditButton().setDisable(true);
                if (!customerValidationLabel.isVisible()) {
                    customerValidationLabel.setVisible(true);
                }
            } else {
                editAppointmentControl.getCustomerEditButton().setDisable(false);
                if (customerValidationLabel.isVisible()) {
                    customerValidationLabel.setVisible(false);
                }
            }
            onParticipantsChanged(newValue, selectedUser.get());
            LOG.exiting(LogHelper.toLambdaSourceClass(LOG, "initialize", "selectedCustomer#value"), "changed");
        });
        selectedUser.addListener((observable, oldValue, newValue) -> {
            LOG.entering(LogHelper.toLambdaSourceClass(LOG, "initialize", "selectedUser#value"), "changed", new Object[]{oldValue, newValue});
            if (null == newValue) {
                if (!userValidationLabel.isVisible()) {
                    userValidationLabel.setVisible(true);
                }
            } else if (userValidationLabel.isVisible()) {
                userValidationLabel.setVisible(false);
            }
            onParticipantsChanged(selectedCustomer.get(), newValue);
            LOG.exiting(LogHelper.toLambdaSourceClass(LOG, "initialize", "selectedUser#value"), "changed");
        });
        conflictMessage.addListener((observable, oldValue, newValue) -> {
            LOG.entering(LogHelper.toLambdaSourceClass(LOG, "<init>", "conflictMessage#value"), "change", new Object[]{oldValue, newValue});
            onStartMessageChanged(dateRange.getStartValidationMessage(), newValue, dateRange.isWithinBusinessHours());
            LOG.exiting(LogHelper.toLambdaSourceClass(LOG, "<init>", "conflictMessage#value"), "change");
        });
        dateRange.startValidationMessageProperty().addListener((observable, oldValue, newValue) -> {
            LOG.entering(LogHelper.toLambdaSourceClass(LOG, "<init>", "dateRange#startValidationMessage#value"), "change", new Object[]{oldValue, newValue});
            onStartMessageChanged(newValue, conflictMessage.get(), dateRange.isWithinBusinessHours());
            LOG.exiting(LogHelper.toLambdaSourceClass(LOG, "<init>", "dateRange#startValidationMessage#value"), "change");
        });
        editAppointmentControl.getCheckConflictsButton().setOnAction(this::onCheckConflictsButtonAction);
        editAppointmentControl.getShowConflictsButton().setOnAction(this::onShowConflictsButtonAction);
        editAppointmentControl.getHideConflictsButton().setOnAction(this::onHideConflictsButtonAction);
        valid = currentParticipants.isNotNull().and(dateRange.validProperty());
        valid.addListener((observable, oldValue, newValue) -> {
            LOG.info(String.format("valid changed from %s to %s", oldValue, newValue));
        });
        modified = ModelHelper.sameRecordBinding(selectedCustomer, model.customerProperty()).and(ModelHelper.sameRecordBinding(selectedUser, model.userProperty())).not()
                .or(dateRange.modifiedBinding());
        LOG.exiting(LOG.getName(), "initialize");
    }

    void initialize(Task<List<AppointmentDAO>> task) {
        LOG.entering(LOG.getName(), "initialize", task);
        AppointmentModel model = editAppointmentControl.getModel();
        clearAndSelectEntity(editAppointmentControl.getCustomerComboBox(), model.getCustomer());
        clearAndSelectEntity(editAppointmentControl.getUserComboBox(), model.getUser());
        onAppointmentsLoaded(task, false);
        editAppointmentControl.getCheckConflictsButton().setDisable(conflictCheckStatus.get() != ConflictCheckStatus.NOT_CHECKED);
        dateRange.rangeProperty().addListener((observable, oldValue, newValue) -> onRangeChanged(newValue, dateRange.isWithinBusinessHours()));
        conflictCheckStatus.addListener((observable, oldValue, newValue) -> {
            editAppointmentControl.getCheckConflictsButton().setDisable(newValue != ConflictCheckStatus.NOT_CHECKED);
        });
        onStartMessageChanged(dateRange.getStartValidationMessage(), conflictMessage.get(), dateRange.isWithinBusinessHours());
        LOG.exiting(LOG.getName(), "initialize");
    }

    boolean canSave() {
        Optional<ButtonType> answer;
        switch (conflictCheckStatus.get()) {
            case HAS_CONFLICT:
                if (dateRange.isWithinBusinessHours()) {
                    answer = AlertHelper.showWarningAlert(editAppointmentControl.getScene().getWindow(), LOG, "Appointment Conflict",
                            String.format("%s%n%nSave changes, anyway?", conflictMessage.get()), ButtonType.YES, ButtonType.NO);
                } else {
                    answer = AlertHelper.showWarningAlert(editAppointmentControl.getScene().getWindow(), LOG, "Appointment Conflict",
                            String.format("Appointment occurs outside normal business hours.%n%n%s%n%nSave changes, anyway?", conflictMessage.get()), ButtonType.YES, ButtonType.NO);
                }
                break;
            case CHECK_ERROR:
                if (dateRange.isWithinBusinessHours()) {
                    answer = AlertHelper.showWarningAlert(editAppointmentControl.getScene().getWindow(), LOG, "Appointment Conflict",
                            String.format("There was an error that occurred while checking for conflicts.%n%nAttempt to save, anyway?"), ButtonType.YES, ButtonType.NO);
                } else {
                    answer = AlertHelper.showWarningAlert(editAppointmentControl.getScene().getWindow(), LOG, "Appointment Conflict",
                            String.format("Appointment occurs outside normal business hours.%n%nThere was an error that occurred while checking for conflicts.%n%nAttempt to save, anyway?"), ButtonType.YES, ButtonType.NO);
                }
                break;
            case NOT_CHECKED:
                if (null != currentTask && !currentTask.isDone()) {
                    currentTask.cancel(true);
                }
                editAppointmentControl.getCheckConflictsButton().setDisable(true);
                currentTask = new LoadParticipantsAppointmentsTask(currentParticipants.get(), true);
                editAppointmentControl.getWaitBorderPane().startNow(currentTask);
                return false;
            default:
                if (dateRange.isWithinBusinessHours()) {
                    return true;
                }
                answer = AlertHelper.showWarningAlert(editAppointmentControl.getScene().getWindow(), LOG, "Extended Appointment Schedule",
                        String.format("Appointment occurs outside normal business hours.%n%nSave changes, anyway?"), ButtonType.YES, ButtonType.NO);
                break;
        }
        return answer.orElse(ButtonType.NO) == ButtonType.YES;
    }

    private synchronized void onCheckConflictsButtonAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onCheckConflictsButtonAction", event);
        Tuple<CustomerModel, UserModel> p = currentParticipants.get();
        if (null != p) {
            if (null != currentTask && !currentTask.isDone()) {
                currentTask.cancel(true);
            }
            editAppointmentControl.getCheckConflictsButton().setDisable(true);
            currentTask = new LoadParticipantsAppointmentsTask(p, false);
            editAppointmentControl.getWaitBorderPane().startNow(currentTask);
        }
        LOG.exiting(LOG.getName(), "onCheckConflictsButtonAction");
    }

    private synchronized void onShowConflictsButtonAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onShowConflictsButtonAction", event);
        if (!conflictingAppointments.isEmpty()) {
            restoreNode(editAppointmentControl.getAppointmentConflictsBorderPane());
        }
        LOG.exiting(LOG.getName(), "onShowConflictsButtonAction");
    }

    private synchronized void onHideConflictsButtonAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onHideConflictsButtonAction", event);
        collapseNode(editAppointmentControl.getAppointmentConflictsBorderPane());
        LOG.exiting(LOG.getName(), "onHideConflictsButtonAction");
    }

    void onAppointmentsLoaded(Task<List<AppointmentDAO>> task, boolean saveMode) {
        LOG.entering(LOG.getName(), "onAppointmentsLoaded", new Object[]{task, saveMode});
        if (checkCurrentTask(task)) {
            return;
        }
        List<AppointmentDAO> appointments;
        try {
            appointments = task.get();
        } catch (InterruptedException | ExecutionException ex) {
            LOG.log(Level.SEVERE, "Error getting task result", ex);
            allAppointments.clear();
            conflictingAppointments.clear();
            if (conflictCheckStatus.get() != ConflictCheckStatus.CHECK_ERROR) {
                conflictCheckStatus.set(ConflictCheckStatus.CHECK_ERROR);
            }
            editAppointmentControl.getShowConflictsButton().setDisable(true);
            if (saveMode) {
                EditItem<AppointmentModel, EditAppointment> editWindowRoot = editAppointmentControl.getEditWindowRoot();
                ActionEvent event = new ActionEvent(editWindowRoot.getSaveChangesButton(), editWindowRoot);
                editWindowRoot.onSaveButtonAction(event);
            }
            LOG.exiting(LOG.getName(), "onAppointmentsLoaded");
            return;
        }
        allAppointments.clear();
        conflictingAppointments.clear();
        if (null != appointments && !appointments.isEmpty()) {
            LOG.fine("Creating appointment models");
            AppointmentModel model = editAppointmentControl.getModel();
            if (model.getRowState() != DataRowState.NEW) {
                int pk = model.getPrimaryKey();
                appointments.stream().filter(t -> t.getPrimaryKey() != pk).map((t) -> t.cachedModel(true)).sorted(ModelHelper.AppointmentHelper::compareByDates)
                        .forEachOrdered((t) -> allAppointments.add(t));
            } else {
                appointments.stream().map((t) -> t.cachedModel(true)).sorted(ModelHelper.AppointmentHelper::compareByDates).forEachOrdered((t) -> allAppointments.add(t));
            }
        }
        if (null != dateRange.getRange() && null != currentParticipants.get()) {
            updateConflictingAppointments();
        } else {
            editAppointmentControl.getShowConflictsButton().setDisable(true);
            if (conflictCheckStatus.get() != ConflictCheckStatus.NO_CONFLICT) {
                conflictCheckStatus.set(ConflictCheckStatus.NO_CONFLICT);
            }
        }
        if (saveMode) {
            EditItem<AppointmentModel, EditAppointment> editWindowRoot = editAppointmentControl.getEditWindowRoot();
            ActionEvent event = new ActionEvent(editWindowRoot.getSaveChangesButton(), editWindowRoot);
            editWindowRoot.onSaveButtonAction(event);
        }
        LOG.exiting(LOG.getName(), "onAppointmentsLoaded");
    }

    private synchronized boolean checkCurrentTask(Task<List<AppointmentDAO>> task) {
        if (Objects.equals(currentTask, task)) {
            currentTask = null;
        } else if (null != currentTask) {
            return true;
        }
        return false;
    }

    private synchronized void onParticipantsChanged(CustomerModel customer, UserModel user) {
        LOG.entering(LOG.getName(), "onParticipantsChanged", new Object[]{customer, user});
        Tuple<CustomerModel, UserModel> lookup = currentParticipants.get();
        String message;
        ConflictCheckStatus checkStatus;
        if (null == customer || null == user) {
            if (null == lookup) {
                LOG.fine("Participants not actually changed; nothing else to do");
                LOG.exiting(LOG.getName(), "onParticipantsChanged");
                return;
            }
            LOG.fine("Customer and/or user not defined; Setting current participants to null");
            currentParticipants.set(null);
            checkStatus = ConflictCheckStatus.NO_CONFLICT;
            message = "";
        } else if (null != lookup && lookup.getValue1().equals(customer) && lookup.getValue2().equals(user)) {
            LOG.fine("Participant values not changed; nothing else to do");
            LOG.exiting(LOG.getName(), "onParticipantsChanged");
            return;
        } else {
            LOG.fine("Participants changed; setting status to NOT_CHECKED");
            currentParticipants.set(Tuple.of(customer, user));
            checkStatus = ConflictCheckStatus.NOT_CHECKED;
            message = (null == dateRange.getRange()) ? "" : resources.getString(RESOURCEKEY_CONFLICTDATASTALE);
        }
        editAppointmentControl.getShowConflictsButton().setDisable(true);
        if (!conflictMessage.get().equals(message)) {
            conflictMessage.set(message);
        }
        if (conflictCheckStatus.get() != checkStatus) {
            conflictCheckStatus.set(checkStatus);
        }
        if (null != currentTask && !currentTask.isDone()) {
            LOG.fine("Superceding existing task");
            currentTask.cancel(true);
            currentTask = null;
        }
        LOG.exiting(LOG.getName(), "onParticipantsChanged");
    }

    private synchronized void onRangeChanged(Tuple<LocalDateTime, LocalDateTime> range, boolean isWithinBusinessHours) {
        LOG.entering(LOG.getName(), "onRangeChanged", new Object[]{range, isWithinBusinessHours});
        if (null == range) {
            LOG.fine("Start and/or end not defined");
            switch (conflictCheckStatus.get()) {
                case CHECK_ERROR:
                case HAS_CONFLICT:
                    conflictCheckStatus.set(ConflictCheckStatus.NO_CONFLICT);
                    if (!conflictMessage.get().isEmpty()) {
                        conflictMessage.set("");
                    }
                    conflictingAppointments.clear();
                    break;
            }
        } else {
            if (allAppointments.isEmpty()) {
                LOG.fine("Range changed, with no appointments; nothing else to do");
            } else {
                Tuple<CustomerModel, UserModel> lookup = currentParticipants.get();
                if (null == lookup) {
                    LOG.fine("Range changed, with no customer and/or user selection; nothing else to do");
                    switch (conflictCheckStatus.get()) {
                        case CHECK_ERROR:
                        case HAS_CONFLICT:
                            conflictCheckStatus.set(ConflictCheckStatus.NO_CONFLICT);
                            if (!conflictMessage.get().isEmpty()) {
                                conflictMessage.set("");
                            }
                            break;
                        case NOT_CHECKED:
                            LOG.fine("Range changed, with status NOT_CHECKED; nothing else to do");
                            break;
                        default:
                            updateConflictingAppointments();
                            break;
                    }
                }
            }
            onStartMessageChanged(dateRange.getStartValidationMessage(), conflictMessage.get(), isWithinBusinessHours);
        }
        LOG.exiting(LOG.getName(), "onRangeChanged");
    }

    private synchronized void updateConflictingAppointments() {
        Tuple<LocalDateTime, LocalDateTime> range = dateRange.getRange();
        LOG.fine("Resetting conflicting appointments list");
        conflictingAppointments.clear();
        LocalDateTime start = range.getValue1();
        LocalDateTime end = range.getValue2();
        allAppointments.stream().filter((a) -> a.getStart().compareTo(end) < 0 && a.getEnd().compareTo(start) > 0).forEachOrdered((t) -> conflictingAppointments.add(t));
        String message;
        ConflictCheckStatus checkStatus;
        if (conflictingAppointments.isEmpty()) {
            LOG.fine("No conflicting appointments");
            message = "";
            checkStatus = ConflictCheckStatus.NO_CONFLICT;
            conflictMessage.set("");
            editAppointmentControl.getShowConflictsButton().setDisable(true);
        } else {
            LOG.fine(() -> String.format("%d conflicting appointments", conflictingAppointments.size()));
            checkStatus = ConflictCheckStatus.HAS_CONFLICT;
            Tuple<CustomerModel, UserModel> participants = currentParticipants.get();
            CustomerModel customer = participants.getValue1();
            UserModel user = participants.getValue2();
            int customerCount = (int) conflictingAppointments.stream().filter((t) -> ModelHelper.areSameRecord(customer, t.getCustomer())).count();
            LOG.fine(() -> String.format("%d conflicting appointments for customer", customerCount));
            int userCount = (int) conflictingAppointments.stream().filter((t) -> ModelHelper.areSameRecord(user, t.getUser())).count();
            LOG.fine(() -> String.format("%d conflicting appointments for user", customerCount));
            switch (customerCount) {
                case 0:
                    switch (userCount) {
                        case 1:
                            message = resources.getString(RESOURCEKEY_CONFLICTUSER1);
                            break;
                        default:
                            message = String.format(resources.getString(RESOURCEKEY_CONFLICTUSERN), userCount);
                            break;
                    }
                    break;
                case 1:
                    switch (userCount) {
                        case 0:
                            message = resources.getString(RESOURCEKEY_CONFLICTCUSTOMER1);
                            break;
                        case 1:
                            message = resources.getString(RESOURCEKEY_CONFLICTCUSTOMER1USER1);
                            break;
                        default:
                            message = String.format(resources.getString(RESOURCEKEY_CONFLICTCUSTOMER1USERN), userCount);
                            break;
                    }
                    break;
                default:
                    switch (userCount) {
                        case 0:
                            message = String.format(resources.getString(RESOURCEKEY_CONFLICTCUSTOMERN), customerCount);
                            break;
                        case 1:
                            message = String.format(resources.getString(RESOURCEKEY_CONFLICTCUSTOMERNUSER1), customerCount);
                            break;
                        default:
                            message = String.format(resources.getString(RESOURCEKEY_CONFLICTCUSTOMERNUSERN), customerCount, userCount);
                            break;
                    }
                    break;
            }
            editAppointmentControl.getShowConflictsButton().setDisable(false);
        }
        if (!message.equals(conflictMessage.get())) {
            conflictMessage.set(message);
        }
        if (conflictCheckStatus.get() != checkStatus) {
            conflictCheckStatus.set(checkStatus);
        }
    }

    private synchronized void onStartMessageChanged(String errorMessage, String warningMessage, boolean isWithinBusinessHours) {
        LOG.entering(LOG.getName(), "onStartMessageChanged", new Object[]{errorMessage, warningMessage, isWithinBusinessHours});
        Label startValidationLabel = editAppointmentControl.getStartValidationLabel();
        if (errorMessage.isEmpty()) {
            if (warningMessage.isEmpty()) {
                if (isWithinBusinessHours) {
                    startValidationLabel.setText("");
                    startValidationLabel.setVisible(false);
                    return;
                }
                setWarningMessage(startValidationLabel, String.format("This appointment occurs outside business hours of %s to %s", dateRange.getBusinessHoursStart(),
                        dateRange.getBusinessHoursEnd()));
            } else {
                setWarningMessage(startValidationLabel, warningMessage);
            }
        } else {
            setErrorMessage(startValidationLabel, errorMessage);
        }
        startValidationLabel.setVisible(true);
        LOG.exiting(LOG.getName(), "onStartMessageChanged");
    }

    private class LoadParticipantsAppointmentsTask extends Task<List<AppointmentDAO>> {

        private final Tuple<CustomerModel, UserModel> participants;
        private final boolean saveMode;

        private LoadParticipantsAppointmentsTask(Tuple<CustomerModel, UserModel> participants, boolean saveMode) {
            this.participants = participants;
            this.saveMode = saveMode;
        }

        @Override
        protected List<AppointmentDAO> call() throws Exception {
            LOG.entering(getClass().getName(), "call");
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
            AppointmentFilter filter;
            try (DbConnector dbConnector = new DbConnector()) {
                if (isCancelled()) {
                    return null;
                }
                filter = AppointmentFilter.of(AppointmentFilter.expressionOf(participants.getValue1(), participants.getValue2()));
                updateMessage(filter.getLoadingMessage());
                LOG.exiting(getClass().getName(), "call");
                return AppointmentDAO.FACTORY.load(dbConnector.getConnection(), filter);
            }
        }

        @Override
        protected void succeeded() {
            LOG.entering(getClass().getName(), "succeeded");
            onAppointmentsLoaded(this, saveMode);
            super.succeeded();
            LOG.exiting(getClass().getName(), "succeeded");
        }

        @Override
        protected void cancelled() {
            LOG.entering(getClass().getName(), "cancelled");
            onAppointmentsLoaded(this, saveMode);
            super.cancelled();
            LOG.exiting(getClass().getName(), "cancelled");
        }

        @Override
        protected void failed() {
            LOG.entering(getClass().getName(), "failed");
            onAppointmentsLoaded(this, saveMode);
            super.failed();
            LOG.exiting(getClass().getName(), "failed");
        }

    }

}
