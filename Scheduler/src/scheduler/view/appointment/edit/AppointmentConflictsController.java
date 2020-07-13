package scheduler.view.appointment.edit;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
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
import scheduler.util.Tuple;
import static scheduler.view.appointment.EditAppointmentResourceKeys.RESOURCEKEY_CONFLICTCUSTOMER1;
import static scheduler.view.appointment.EditAppointmentResourceKeys.RESOURCEKEY_CONFLICTCUSTOMER1USER1;
import static scheduler.view.appointment.EditAppointmentResourceKeys.RESOURCEKEY_CONFLICTCUSTOMER1USERN;
import static scheduler.view.appointment.EditAppointmentResourceKeys.RESOURCEKEY_CONFLICTCUSTOMERN;
import static scheduler.view.appointment.EditAppointmentResourceKeys.RESOURCEKEY_CONFLICTCUSTOMERNUSER1;
import static scheduler.view.appointment.EditAppointmentResourceKeys.RESOURCEKEY_CONFLICTCUSTOMERNUSERN;
import static scheduler.view.appointment.EditAppointmentResourceKeys.RESOURCEKEY_CONFLICTUSER1;
import static scheduler.view.appointment.EditAppointmentResourceKeys.RESOURCEKEY_CONFLICTUSERN;
import scheduler.view.task.WaitBorderPane;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @todo Implement {@code scheduler.view.appointment.edit.AppointmentConflictsController}
 */
public class AppointmentConflictsController {

    private static final Logger LOG = Logger.getLogger(AppointmentConflictsController.class.getName());

    private final ResourceBundle resources;
    private final ReadOnlyObjectWrapper<Tuple<CustomerModel, UserModel>> currentLookup;
    private final ReadOnlyObjectWrapper<Tuple<LocalDateTime, LocalDateTime>> currentRange;
    private final ReadOnlyObjectWrapper<ConflictCheckStatus> conflictCheckStatus;
    private final ReadOnlyStringWrapper conflictMessage;
    private final ObservableList<AppointmentModel> allAppointments;
    private final ObservableList<AppointmentModel> conflictingAppointments;
    private CheckConflictsTask currentTask = null;

    public AppointmentConflictsController(ReadOnlyObjectProperty<CustomerModel> selectedCustomer, ReadOnlyObjectProperty<UserModel> selectedUser,
            ReadOnlyObjectProperty<LocalDateTime> startDateTime, ReadOnlyObjectProperty<LocalDateTime> endDateTime, ResourceBundle resources) {
        this.resources = resources;
        CustomerModel customer = selectedCustomer.get();
        if (null == customer) {
            currentLookup = new ReadOnlyObjectWrapper<>(null);
        } else {
            UserModel user = selectedUser.get();
            currentLookup = new ReadOnlyObjectWrapper<>((null == user) ? null : Tuple.of(customer, user));
        }
        LocalDateTime end = endDateTime.get();
        if (null == end) {
            currentRange = new ReadOnlyObjectWrapper<>(null);
        } else {
            LocalDateTime start = startDateTime.get();
            currentRange = new ReadOnlyObjectWrapper<>((null == start) ? null : Tuple.of(start, end));
        }
        conflictCheckStatus = new ReadOnlyObjectWrapper<>(ConflictCheckStatus.NOT_CHECKED);
        allAppointments = FXCollections.observableArrayList();
        conflictingAppointments = FXCollections.observableArrayList();
        conflictMessage = new ReadOnlyStringWrapper("");
        selectedCustomer.addListener((observable, oldValue, newValue) -> {
            onContextChanged(newValue, selectedUser.get());
        });
        selectedUser.addListener((observable, oldValue, newValue) -> {
            onContextChanged(selectedCustomer.get(), newValue);
        });
        startDateTime.addListener((observable, oldValue, newValue) -> {
            onRangeChanged(newValue, endDateTime.get());
        });
        endDateTime.addListener((observable, oldValue, newValue) -> {
            onRangeChanged(startDateTime.get(), newValue);
        });
    }

    private synchronized void onContextChanged(CustomerModel customer, UserModel user) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.view.appointment.edit.AppointmentConflictsController#onContextChanged
    }

    private synchronized void onRangeChanged(LocalDateTime start, LocalDateTime end) {
        Tuple<LocalDateTime, LocalDateTime> range = currentRange.get();
        Tuple<CustomerModel, UserModel> lookup = currentLookup.get();
        if (null != start || null != end) {
            if (null == range || !(start.equals(range.getValue2()) && end.equals(range.getValue2()))) {
                currentRange.set(Tuple.of(start, end));
                if (null != lookup && !allAppointments.isEmpty()) {
                    updateConflictingAppointments(lookup);
                }
            }
        } else if (null != range) {
            currentRange.set(null);
            if (conflictCheckStatus.get() == ConflictCheckStatus.HAS_CONFLICT) {
                conflictCheckStatus.set(ConflictCheckStatus.NO_CONFLICT);
                if (!conflictingAppointments.isEmpty()) {
                    conflictingAppointments.clear();
                }
            }
        }
    }

    public Tuple<CustomerModel, UserModel> getCurrentLookup() {
        return currentLookup.get();
    }

    public ReadOnlyObjectProperty<Tuple<CustomerModel, UserModel>> currentLookupProperty() {
        return currentLookup.getReadOnlyProperty();
    }

    public synchronized void startConflictCheck(WaitBorderPane waitBorderPane) {
        if (null != currentTask) {
            currentTask.cancel(true);
            currentTask = null;
        }
        Tuple<CustomerModel, UserModel> checkParams = currentLookup.get();
        if (null != checkParams) {
            currentTask = new CheckConflictsTask(checkParams);
            waitBorderPane.startNow(currentTask);
        }
    }

    private synchronized void initializeConflictCheckData(CheckConflictsTask task) {
        if (null != currentTask && currentTask != task) {
            return;
        }
        conflictingAppointments.clear();
        allAppointments.clear();
        currentTask = null;
        List<AppointmentDAO> appointments;
        try {
            if (null == (appointments = task.get())) {
                appointments = Collections.emptyList();
            }
        } catch (InterruptedException | ExecutionException ex) {
            LOG.log(Level.SEVERE, "Error loading appointment", ex);
            conflictCheckStatus.set(ConflictCheckStatus.NOT_CHECKED);
            conflictMessage.set("Error loading appointment");
            return;
        }
        if (appointments.isEmpty()) {
            conflictCheckStatus.set(ConflictCheckStatus.NO_CONFLICT);
            conflictMessage.set("");
        }
        appointments.stream().map((t) -> AppointmentModel.FACTORY.createNew(t)).sorted(AppointmentModel::compareByDates).forEachOrdered((t) -> allAppointments.add(t));
        updateConflictingAppointments(task.participants);
    }

    private void updateConflictingAppointments(Tuple<CustomerModel, UserModel> participants) {
        Tuple<LocalDateTime, LocalDateTime> range = currentRange.get();
        if (!conflictingAppointments.isEmpty()) {
            conflictingAppointments.clear();
        }
        if (null == range) {
            conflictCheckStatus.set(ConflictCheckStatus.NO_CONFLICT);
            conflictMessage.set("");
        }
        LocalDateTime start = range.getValue1();
        LocalDateTime end = range.getValue2();
        allAppointments.stream().filter((a) -> a.getStart().compareTo(end) < 0 && a.getEnd().compareTo(start) > 0).forEach((t) -> conflictingAppointments.add(t));
        if (conflictingAppointments.isEmpty()) {
            conflictCheckStatus.set(ConflictCheckStatus.NO_CONFLICT);
            conflictMessage.set("");
            return;
        }
        CustomerModel customer = participants.getValue1();
        UserModel user = participants.getValue2();
        int customerCount = (int) conflictingAppointments.stream().filter((t) -> ModelHelper.areSameRecord(customer, t.getCustomer())).count();
        int userCount = (int) conflictingAppointments.stream().filter((t) -> ModelHelper.areSameRecord(user, t.getUser())).count();
        String message;
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
        if (!message.equals(conflictMessage.get())) {
            conflictMessage.set(message);
        }
        if (conflictCheckStatus.get() != ConflictCheckStatus.HAS_CONFLICT) {
            conflictCheckStatus.set(ConflictCheckStatus.HAS_CONFLICT);
        }
    }

    private class CheckConflictsTask extends Task<List<AppointmentDAO>> {

        private final Tuple<CustomerModel, UserModel> participants;

        private CheckConflictsTask(Tuple<CustomerModel, UserModel> participants) {
            this.participants = participants;
        }

        @Override
        protected List<AppointmentDAO> call() throws Exception {
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
            initializeConflictCheckData(this);
        }

        @Override
        protected void cancelled() {
            super.cancelled();
            initializeConflictCheckData(this);
        }

        @Override
        protected void failed() {
            super.failed();
            initializeConflictCheckData(this);
        }

    }
}
