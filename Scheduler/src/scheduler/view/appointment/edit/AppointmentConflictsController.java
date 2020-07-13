package scheduler.view.appointment.edit;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import scheduler.AppResourceKeys;
import scheduler.AppResources;
import scheduler.dao.AppointmentDAO;
import scheduler.dao.filter.AppointmentFilter;
import scheduler.model.ModelHelper;
import scheduler.model.ui.AppointmentModel;
import scheduler.model.ui.CustomerModel;
import scheduler.model.ui.UserModel;
import scheduler.util.DbConnector;
import scheduler.util.LogHelper;
import scheduler.util.Tuple;
import static scheduler.view.appointment.EditAppointmentResourceKeys.*;
import scheduler.view.task.WaitBorderPane;

/**
 * Loads and manages a list of conflicting appointments.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class AppointmentConflictsController {

    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(AppointmentConflictsController.class.getName()), Level.FINER);
//    private static final Logger LOG = Logger.getLogger(AppointmentConflictsController.class.getName());

    private final ResourceBundle resources;
    private final ReadOnlyObjectWrapper<Tuple<CustomerModel, UserModel>> currentParticipants;
    private final ReadOnlyObjectWrapper<Tuple<LocalDateTime, LocalDateTime>> currentRange;
    private final ReadOnlyObjectWrapper<ConflictCheckStatus> conflictCheckStatus;
    private final ReadOnlyStringWrapper conflictMessage;
    private final ObservableList<AppointmentModel> allAppointments;
    private final ObservableList<AppointmentModel> conflictingAppointments;
    private LoadParticipantsAppointmentsTask currentTask = null;
    private Optional<Integer> primaryKey;

    /**
     * Creates a new {@code AppointmentConflictsController}.
     *
     * @param selectedCustomer The currently selected {@link CustomerModel}.
     * @param selectedUser The currently selected {@link UserModel}.
     * @param startDateTime The inclusive start of the {@link LocalDateTime} range.
     * @param endDateTime The exclusive start of the {@link LocalDateTime} range.
     * @param resources The {@link ResourceBundle} to use.
     * @param appointments Initially loaded appointments.
     * @param primaryKey The current appointment primary key or {@link Optional#EMPTY} if the current appointment has not yet been saved to the database.
     */
    public AppointmentConflictsController(ReadOnlyObjectProperty<CustomerModel> selectedCustomer, ReadOnlyObjectProperty<UserModel> selectedUser,
            ReadOnlyObjectProperty<LocalDateTime> startDateTime, ReadOnlyObjectProperty<LocalDateTime> endDateTime, ResourceBundle resources, List<AppointmentDAO> appointments,
            Optional<Integer> primaryKey) {
        this.resources = resources;
        this.primaryKey = primaryKey;
        allAppointments = FXCollections.observableArrayList();
        conflictingAppointments = FXCollections.observableArrayList();
        conflictMessage = new ReadOnlyStringWrapper("");
        if (null != appointments && !appointments.isEmpty()) {
            LOG.fine("Creating appointment models");
            if (primaryKey.isPresent()) {
                int pk = primaryKey.get();
                appointments.stream().filter(t -> t.getPrimaryKey() != pk).map((t) -> AppointmentModel.FACTORY.createNew(t)).sorted(AppointmentModel::compareByDates)
                        .forEachOrdered((t) -> allAppointments.add(t));
            } else {
                appointments.stream().map((t) -> AppointmentModel.FACTORY.createNew(t)).sorted(AppointmentModel::compareByDates).forEachOrdered((t) -> allAppointments.add(t));
            }
        }
        CustomerModel customer = selectedCustomer.get();
        UserModel user = selectedUser.get();
        LocalDateTime start = startDateTime.get();
        LocalDateTime end = endDateTime.get();
        if (null == customer || null == user) {
            currentParticipants = new ReadOnlyObjectWrapper<>(null);
            if (null == start || null == end) {
                currentRange = new ReadOnlyObjectWrapper<>(null);
            } else {
                currentRange = new ReadOnlyObjectWrapper<>(Tuple.of(start, end));
            }
            conflictCheckStatus = new ReadOnlyObjectWrapper<>(ConflictCheckStatus.NO_CONFLICT);
        } else {
            currentParticipants = new ReadOnlyObjectWrapper<>(Tuple.of(customer, user));
            conflictCheckStatus = new ReadOnlyObjectWrapper<>(ConflictCheckStatus.NOT_CHECKED);
            if (null == start || null == end) {
                currentRange = new ReadOnlyObjectWrapper<>(null);
            } else {
                currentRange = new ReadOnlyObjectWrapper<>(Tuple.of(start, end));
                updateConflictingAppointments();
            }
        }
        selectedCustomer.addListener((observable, oldValue, newValue) -> onParticipantsChanged(newValue, selectedUser.get()));
        selectedUser.addListener((observable, oldValue, newValue) -> onParticipantsChanged(selectedCustomer.get(), newValue));
        startDateTime.addListener((observable, oldValue, newValue) -> onRangeChanged(newValue, endDateTime.get()));
        endDateTime.addListener((observable, oldValue, newValue) -> onRangeChanged(startDateTime.get(), newValue));
    }

    public ConflictCheckStatus getConflictCheckStatus() {
        return conflictCheckStatus.get();
    }

    public ReadOnlyObjectProperty<ConflictCheckStatus> conflictCheckStatusProperty() {
        return conflictCheckStatus.getReadOnlyProperty();
    }

    public String getConflictMessage() {
        return conflictMessage.get();
    }

    public ReadOnlyStringProperty conflictMessageProperty() {
        return conflictMessage.getReadOnlyProperty();
    }

    public Tuple<CustomerModel, UserModel> getCurrentParticipants() {
        return currentParticipants.get();
    }

    public ReadOnlyObjectProperty<Tuple<CustomerModel, UserModel>> currentParticipantsProperty() {
        return currentParticipants.getReadOnlyProperty();
    }

    public Tuple<LocalDateTime, LocalDateTime> getCurrentRange() {
        return currentRange.get();
    }

    public ReadOnlyObjectProperty<Tuple<LocalDateTime, LocalDateTime>> currentRangeProperty() {
        return currentRange.getReadOnlyProperty();
    }

    public synchronized void startLoadParticipantsAppointments(WaitBorderPane waitBorderPane) {
        if (null != currentTask) {
            currentTask.cancel(true);
            currentTask = null;
        }
        Tuple<CustomerModel, UserModel> checkParams = currentParticipants.get();
        if (null != checkParams) {
            currentTask = new LoadParticipantsAppointmentsTask(checkParams);
            waitBorderPane.startNow(currentTask);
        }
    }

    private synchronized void onRangeChanged(LocalDateTime start, LocalDateTime end) {
        Tuple<LocalDateTime, LocalDateTime> range = currentRange.get();
        if (null == start || null == end) {
            if (null == range) {
                LOG.fine("Range not actually changed; nothing else to do");
                return;
            }
            LOG.fine("Start and/or end not defined; Setting current range to null");
            currentRange.set(null);
            if (conflictCheckStatus.get() == ConflictCheckStatus.HAS_CONFLICT) {
                conflictCheckStatus.set(ConflictCheckStatus.NO_CONFLICT);
                if (!conflictMessage.get().isEmpty()) {
                    conflictMessage.set("");
                }
                conflictingAppointments.clear();
            }
        } else if (null != range && range.getValue1().equals(start) && range.getValue1().equals(end)) {
            LOG.fine("Range values not actually changed; nothing else to do");
            return;
        } else {
            currentRange.set(Tuple.of(start, end));
            if (allAppointments.isEmpty()) {
                LOG.fine("Range changed, with no appointments; nothing else to do");
            } else {
                Tuple<CustomerModel, UserModel> lookup = currentParticipants.get();
                if (null == lookup) {
                    LOG.fine("Range changed, with no customer and/or user selection; nothing else to do");
                    if (conflictCheckStatus.get() == ConflictCheckStatus.HAS_CONFLICT) {
                        conflictCheckStatus.set(ConflictCheckStatus.NO_CONFLICT);
                        if (!conflictMessage.get().isEmpty()) {
                            conflictMessage.set("");
                        }
                    }
                } else if (conflictCheckStatus.get() != ConflictCheckStatus.NOT_CHECKED) {
                    updateConflictingAppointments();
                } else {
                    LOG.fine("Range changed, with status NOT_CHECKED; nothing else to do");
                }
            }
        }

        if (null != currentTask) {
            LOG.fine("Superceding existing task");
            currentTask.cancel(true);
            currentTask = null;
        }
    }

    private synchronized void onParticipantsChanged(CustomerModel customer, UserModel user) {
        Tuple<CustomerModel, UserModel> lookup = currentParticipants.get();
        String message;
        ConflictCheckStatus checkStatus;
        if (null == customer || null == user) {
            if (null == lookup) {
                LOG.fine("Participants not actually changed; nothing else to do");
                return;
            }
            LOG.fine("Customer and/or user not defined; Setting current participants to null");
            currentParticipants.set(null);
            checkStatus = ConflictCheckStatus.NO_CONFLICT;
            message = "";
        } else if (null != lookup && lookup.getValue1().equals(customer) && lookup.getValue2().equals(user)) {
            LOG.fine("Participant values not changed; nothing else to do");
            return;
        } else {
            LOG.fine("Participants changed; setting status to NOT_CHECKED");
            currentParticipants.set(Tuple.of(customer, user));
            checkStatus = ConflictCheckStatus.NOT_CHECKED;
            message = (null == currentRange.get()) ? "" : resources.getString(RESOURCEKEY_CONFLICTDATASTALE);
        }
        if (!conflictMessage.get().equals(message)) {
            conflictMessage.set(message);
        }
        if (conflictCheckStatus.get() != checkStatus) {
            conflictCheckStatus.set(checkStatus);
        }
    }

    private synchronized void onAppointmentsLoaded(LoadParticipantsAppointmentsTask task) {
        if (null == currentTask || currentTask != task) {
            LOG.fine("Detected task supercession");
            return;
        }
        conflictingAppointments.clear();
        allAppointments.clear();
        currentTask = null;
        List<AppointmentDAO> appointments;
        try {
            if (null == (appointments = task.get())) {
                LOG.warning("Task returned null");
                appointments = Collections.emptyList();
            }
        } catch (InterruptedException | ExecutionException ex) {
            LOG.log(Level.SEVERE, "Error loading appointments", ex);
            conflictCheckStatus.set(ConflictCheckStatus.NOT_CHECKED);
            conflictMessage.set("Error loading appointments");
            return;
        }
        if (!appointments.isEmpty()) {
            LOG.fine("Creating appointment models");
            if (primaryKey.isPresent()) {
                int pk = primaryKey.get();
                appointments.stream().filter((t) -> t.getPrimaryKey() != pk).map((t) -> AppointmentModel.FACTORY.createNew(t)).sorted(AppointmentModel::compareByDates)
                        .forEachOrdered((t) -> allAppointments.add(t));
            } else {
                appointments.stream().map((t) -> AppointmentModel.FACTORY.createNew(t)).sorted(AppointmentModel::compareByDates).forEachOrdered((t) -> allAppointments.add(t));
            }
        }
        if (allAppointments.isEmpty()) {
            LOG.fine("No appointments loaded");
            conflictCheckStatus.set(ConflictCheckStatus.NO_CONFLICT);
            conflictMessage.set("");
        } else {
            updateConflictingAppointments();
        }
    }

    private void updateConflictingAppointments() {
        Tuple<LocalDateTime, LocalDateTime> range = currentRange.get();
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
        }
        if (!message.equals(conflictMessage.get())) {
            conflictMessage.set(message);
        }
        if (conflictCheckStatus.get() != checkStatus) {
            conflictCheckStatus.set(checkStatus);
        }
    }

    public void enterEditMode(int primaryKey) {
        this.primaryKey = Optional.of(primaryKey);
    }

    private class LoadParticipantsAppointmentsTask extends Task<List<AppointmentDAO>> {

        private final Tuple<CustomerModel, UserModel> participants;

        private LoadParticipantsAppointmentsTask(Tuple<CustomerModel, UserModel> participants) {
            this.participants = participants;
        }

        @Override
        protected List<AppointmentDAO> call() throws Exception {
            LOG.fine("Task started");
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
            AppointmentFilter filter;
            try (DbConnector dbConnector = new DbConnector()) {
                if (isCancelled()) {
                    return null;
                }
                filter = AppointmentFilter.of(AppointmentFilter.expressionOf(participants.getValue1(), participants.getValue2()));
                updateMessage(filter.getLoadingMessage());
                return AppointmentDAO.FACTORY.load(dbConnector.getConnection(), filter);
            }
        }

        @Override
        protected void succeeded() {
            super.succeeded();
            LOG.fine("Task succeeded");
            onAppointmentsLoaded(this);
        }

        @Override
        protected void cancelled() {
            super.cancelled();
            LOG.fine("Task cancelled");
            onAppointmentsLoaded(this);
        }

        @Override
        protected void failed() {
            super.failed();
            LOG.fine("Task failed");
            onAppointmentsLoaded(this);
        }

    }
}
