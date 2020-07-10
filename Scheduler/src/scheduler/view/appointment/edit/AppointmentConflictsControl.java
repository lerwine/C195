package scheduler.view.appointment.edit;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
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
import static scheduler.util.NodeUtil.collapseNode;
import static scheduler.util.NodeUtil.restoreNode;
import scheduler.util.Tuple;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import static scheduler.view.appointment.EditAppointmentResourceKeys.*;
import scheduler.view.task.WaitBorderPane;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/view/appointment/EditAppointment")
@FXMLResource("/scheduler/view/appointment/edit/AppointmentConflicts.fxml")
public class AppointmentConflictsControl extends BorderPane {

    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(AppointmentConflictsControl.class.getName()), Level.FINER);
//    private static final Logger LOG = Logger.getLogger(AppointmentConflictsControl.class.getName());

    private final ReadOnlyObjectWrapper<ConflictCheckStatus> conflictCheckStatus;
    private final ReadOnlyStringWrapper conflictMessage;
    private final SimpleObjectProperty<CustomerModel> selectedCustomer;
    private final SimpleObjectProperty<UserModel> selectedUser;
    private final SimpleObjectProperty<ZonedAppointmentTimeSpan> selectedTimeSpan;
    private final ObservableList<AppointmentModel> allAppointments;
    private final ObservableList<AppointmentModel> conflictingAppointments;
    private final ObjectBinding<Tuple<CustomerModel, UserModel>> participantsBinding;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // fx:id="conflictingAppointmentsTableView"
    private TableView<AppointmentModel> conflictingAppointmentsTableView; // Value injected by FXMLLoader
    private Tuple<CustomerModel, UserModel> checkedParticipants;

    public AppointmentConflictsControl() {
        conflictCheckStatus = new ReadOnlyObjectWrapper<>(ConflictCheckStatus.NOT_CHECKED);
        conflictMessage = new ReadOnlyStringWrapper("");
        selectedCustomer = new SimpleObjectProperty<>();
        selectedUser = new SimpleObjectProperty<>();
        selectedTimeSpan = new SimpleObjectProperty<>();
        allAppointments = FXCollections.observableArrayList();
        conflictingAppointments = FXCollections.observableArrayList();
        participantsBinding = Tuple.createBinding(selectedCustomer, selectedUser);
    }

    @FXML
    void onCloseConflictsBorderPaneButtonAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onCloseConflictsBorderPaneButtonAction", event);
        collapseNode(this);
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert conflictingAppointmentsTableView != null : "fx:id=\"conflictingAppointmentsTableView\" was not injected: check your FXML file 'AppointmentConflicts.fxml'.";
        selectedCustomer.addListener((observable, oldValue, newValue) -> onParticipantsChanged(participantsBinding.get()));
        selectedUser.addListener((observable, oldValue, newValue) -> onParticipantsChanged(participantsBinding.get()));
        selectedTimeSpan.addListener((observable, oldValue, newValue) -> {
            onTimeSpanChanged(newValue);
        });
        collapseNode(this);
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

    public CustomerModel getSelectedCustomer() {
        return selectedCustomer.get();
    }

    public void setSelectedCustomer(CustomerModel value) {
        selectedCustomer.set(value);
    }

    public ObjectProperty<CustomerModel> selectedCustomerProperty() {
        return selectedCustomer;
    }

    public UserModel getSelectedUser() {
        return selectedUser.get();
    }

    public void setSelectedUser(UserModel value) {
        selectedUser.set(value);
    }

    public ObjectProperty<UserModel> selectedUserProperty() {
        return selectedUser;
    }

    public ZonedAppointmentTimeSpan getSelectedTimeSpan() {
        return selectedTimeSpan.get();
    }

    public void setSelectedTimeSpan(ZonedAppointmentTimeSpan value) {
        selectedTimeSpan.set(value);
    }

    public ObjectProperty<ZonedAppointmentTimeSpan> selectedTimeSpanProperty() {
        return selectedTimeSpan;
    }

    public synchronized boolean initializeConflictCheckData(Tuple<CustomerModel, UserModel> participants, List<AppointmentDAO> appointments) {
        UserModel currentUser;
        CustomerModel currentCustomer = selectedCustomer.get();
        if (null == currentCustomer || null == (currentUser = selectedUser.get())) {
            if (!allAppointments.isEmpty()) {
                allAppointments.clear();
                conflictingAppointments.clear();
            }
            if (!conflictMessage.get().isEmpty()) {
                conflictMessage.set("");
            }
            if (conflictCheckStatus.get() != ConflictCheckStatus.NO_CONFLICT) {
                conflictCheckStatus.set(ConflictCheckStatus.NO_CONFLICT);
            }
        } else if (Objects.equals(currentCustomer, participants.getValue1()) && Objects.equals(currentUser, participants.getValue2())) {
            checkedParticipants = participants;
            if (!allAppointments.isEmpty()) {
                allAppointments.clear();
                conflictingAppointments.clear();
            }
            if (null != appointments) {
                if (!appointments.isEmpty()) {
                    appointments.stream().map((t) -> new AppointmentModel(t)).sorted(AppointmentModel::compareByDates).forEachOrdered((t) -> allAppointments.add(t));
                }
                ZonedAppointmentTimeSpan timeSpan = selectedTimeSpan.get();
                if (null == timeSpan) {
                    if (!conflictMessage.get().isEmpty()) {
                        conflictMessage.set("");
                    }
                    if (conflictCheckStatus.get() != ConflictCheckStatus.NO_CONFLICT) {
                        conflictCheckStatus.set(ConflictCheckStatus.NO_CONFLICT);
                    }
                } else {
                    updateConflictingAppointments(currentCustomer, currentUser, selectedTimeSpan.get());
                }
                return true;
            }
            String message = resources.getString(RESOURCEKEY_CONFLICTDATASTALE);
            if (!message.equals(conflictMessage.get())) {
                conflictMessage.set(message);
            }
            if (conflictCheckStatus.get() != ConflictCheckStatus.NOT_CHECKED) {
                conflictCheckStatus.set(ConflictCheckStatus.NOT_CHECKED);
            }
        }
        return false;
    }

    private synchronized void onParticipantsChanged(Tuple<CustomerModel, UserModel> participants) {
        checkedParticipants = null;
        if (!allAppointments.isEmpty()) {
            allAppointments.clear();
            conflictingAppointments.clear();
        }
        if (null == participants.getValue1() || null == participants.getValue2()) {
            if (!conflictMessage.get().isEmpty()) {
                conflictMessage.set("");
            }
            if (conflictCheckStatus.get() != ConflictCheckStatus.NO_CONFLICT) {
                conflictCheckStatus.set(ConflictCheckStatus.NO_CONFLICT);
            }
        } else {
            String message = resources.getString(RESOURCEKEY_CONFLICTDATASTALE);
            if (!message.equals(conflictMessage.get())) {
                conflictMessage.set(message);
            }
            if (conflictCheckStatus.get() != ConflictCheckStatus.NOT_CHECKED) {
                conflictCheckStatus.set(ConflictCheckStatus.NOT_CHECKED);
            }
        }
    }

    private synchronized void onTimeSpanChanged(ZonedAppointmentTimeSpan timeSpan) {
        Tuple<CustomerModel, UserModel> participants;
        if (null == timeSpan) {
            if (!conflictingAppointments.isEmpty()) {
                conflictingAppointments.clear();
            }
            if (conflictCheckStatus.get() == ConflictCheckStatus.HAS_CONFLICT) {
                conflictCheckStatus.set(ConflictCheckStatus.NO_CONFLICT);
                if (!conflictMessage.get().isEmpty()) {
                    conflictMessage.set("");
                }
            }
        } else if (null != (participants = checkedParticipants)) {
            CustomerModel currentCustomer = selectedCustomer.get();
            UserModel currentUser;
            if (null != currentCustomer && null != (currentUser = selectedUser.get())
                    && Objects.equals(currentCustomer, participants.getValue1()) && Objects.equals(currentUser, participants.getValue2())) {
                updateConflictingAppointments(currentCustomer, currentUser, timeSpan);
            }
        }
    }

    private void updateConflictingAppointments(CustomerModel customer, UserModel user, ZonedAppointmentTimeSpan timeSpan) {
        if (!allAppointments.isEmpty()) {
            if (!conflictingAppointments.isEmpty()) {
                conflictingAppointments.clear();
            }
            LocalDateTime start = timeSpan.toZonedStartDateTime().withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
            LocalDateTime end = timeSpan.toZonedEndDateTime().withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
            allAppointments.stream().filter((a) -> a.getStart().compareTo(end) < 0 && a.getEnd().compareTo(start) > 0).forEach((t) -> conflictingAppointments.add(t));
            if (!allAppointments.isEmpty()) {
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
                return;
            }
        }
        if (conflictCheckStatus.get() != ConflictCheckStatus.NO_CONFLICT) {
            conflictCheckStatus.set(ConflictCheckStatus.NO_CONFLICT);
        }
        if (!conflictMessage.get().isEmpty()) {
            conflictMessage.set("");
        }
    }

    public void startConflictCheck(WaitBorderPane waitBorderPane) {
        Tuple<CustomerModel, UserModel> checkParams = participantsBinding.get();
        Objects.requireNonNull(checkParams.getValue1());
        Objects.requireNonNull(checkParams.getValue2());
        waitBorderPane.startNow(new CheckConflictsTask(checkParams));
    }

    public void showConflicts() {
        restoreNode(this);
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
                filter = AppointmentFilter.of(AppointmentFilter.expressionOf(participants.getValue1(), participants.getValue2()));
                updateMessage(filter.getLoadingMessage());
                return AppointmentDAO.FACTORY.load(dbConnector.getConnection(), filter);
            }
        }

        @Override
        protected void succeeded() {
            super.succeeded();
            initializeConflictCheckData(participants, getValue());
        }

        @Override
        protected void cancelled() {
            super.cancelled();
            initializeConflictCheckData(participants, null);
        }

        @Override
        protected void failed() {
            super.failed();
            initializeConflictCheckData(participants, null);
        }

    }
}
