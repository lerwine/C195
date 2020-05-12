package scheduler.view.appointment;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import static scheduler.AppResourceKeys.RESOURCEKEY_CONNECTEDTODB;
import static scheduler.AppResourceKeys.RESOURCEKEY_CONNECTINGTODB;
import static scheduler.AppResourceKeys.RESOURCEKEY_DBREADERROR;
import static scheduler.AppResourceKeys.RESOURCEKEY_LOADINGAPPOINTMENTS;
import scheduler.AppResources;
import scheduler.dao.AppointmentDAO;
import scheduler.dao.CustomerDAO;
import scheduler.dao.UserDAO;
import scheduler.dao.filter.AppointmentFilter;
import scheduler.model.Customer;
import scheduler.model.ModelHelper;
import scheduler.model.User;
import scheduler.view.ErrorDetailControl;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import static scheduler.view.appointment.EditAppointmentResourceKeys.*;
import scheduler.view.customer.CustomerModel;
import scheduler.view.task.TaskWaiter;
import scheduler.view.user.UserModel;

// TODO: Move to /scheduler/fx
/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/view/appointment/EditAppointment")
@FXMLResource("/scheduler/fx/AppointmentConflicts.fxml")
public class AppointmentConflicts {

    private static final Logger LOG = Logger.getLogger(AppointmentConflicts.class.getName());

    private EditAppointment parentController;
    private DateRange dateRangeController;
    private ObservableList<AppointmentModel> allAppointments;
    private ObservableList<AppointmentModel> conflictingAppointments;
    private Customer currentCustomer;
    private User currentUser;
    private ZonedAppointmentTimeSpan currentRange;
    private SimpleStringProperty conflictMessage;
    private boolean conflictCheckingCurrent = false;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // fx:id="rootBorderPane"
    private BorderPane rootBorderPane; // Value injected by FXMLLoader

    @FXML // fx:id="conflictingAppointmentsTableView"
    private TableView<AppointmentModel> conflictingAppointmentsTableView; // Value injected by FXMLLoader

    @FXML
    private void onCloseConflictsBorderPaneButtonAction(ActionEvent event) {
        rootBorderPane.setVisible(false);
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert rootBorderPane != null : "fx:id=\"rootBorderPane\" was not injected: check your FXML file 'AppointmentConflicts.fxml'.";
        assert conflictingAppointmentsTableView != null : "fx:id=\"conflictingAppointmentsTableView\" was not injected: check your FXML file 'AppointmentConflicts.fxml'.";
        currentCustomer = null;
        currentUser = null;
        currentRange = null;
        currentUser = null;
        conflictMessage = new SimpleStringProperty();
        allAppointments = FXCollections.observableArrayList();
        conflictingAppointments = FXCollections.observableArrayList();
    }

    public String getConflictMessage() {
        return conflictMessage.get();
    }

    EditAppointment getParentController() {
        return parentController;
    }

    @FXML
    private void onShowConflictsButtonAction(ActionEvent event) {
        rootBorderPane.setVisible(true);
    }

    private void onCheckConflictsButtonAction(ActionEvent event) {
        TaskWaiter.startNow(new AppointmentReloadTask());
    }

    void initializeConflicts(List<AppointmentDAO> appointments, EditAppointment parentController) {
        if (null != this.parentController) {
            throw new IllegalStateException();
        }
        this.parentController = parentController;
        dateRangeController = parentController.getDateRangeController();
        currentCustomer = parentController.getCustomer();
        currentUser = parentController.getUser();
        currentRange = dateRangeController.getTimeSpan();
        dateRangeController.setConflictsBinding(this, this::onCheckConflictsButtonAction, this::onShowConflictsButtonAction);
        accept(appointments);
    }

    private void refreshFromDateRange() {
        conflictingAppointments.clear();
        if (null == currentRange || allAppointments.isEmpty()) {
            return;
        }
        LocalDateTime start = currentRange.toZonedStartDateTime().withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime end = currentRange.toZonedEndDateTime().withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();

        Stream<AppointmentModel> filtered = allAppointments.stream().filter((a) -> a.getStart().compareTo(end) < 0 && a.getEnd().compareTo(start) > 0);
        conflictingAppointments.clear();
        if (null == currentCustomer) {
            if (null == currentUser) {
                return;
            }
            filtered = filtered.filter((t) -> ModelHelper.areSameRecord(currentUser, t.getUser()));
        } else if (null == currentUser) {
            filtered = filtered.filter((t) -> ModelHelper.areSameRecord(currentCustomer, t.getCustomer()));
        }
        filtered.forEach((t) -> conflictingAppointments.add(t));
    }

    private void updateConflictMessage() {
        if (null == currentRange) {
            conflictMessage.set("");
            if (null != dateRangeController) {
                dateRangeController.onConflictStateChanged();
            }
            return;
        }
        if (conflictingAppointments.isEmpty()) {
            if ((null != currentCustomer || null != currentUser) && !conflictCheckingCurrent) {
                conflictMessage.set(resources.getString(RESOURCEKEY_CONFLICTDATASTALE));
            } else {
                conflictMessage.set("");
            }
            if (null != dateRangeController) {
                dateRangeController.onConflictStateChanged();
            }
            return;
        }

        int cc = (null == currentCustomer) ? 0 : (int) conflictingAppointments.stream().filter((t)
                -> ModelHelper.areSameRecord(currentCustomer, t.getCustomer())).count();
        int uc = (null == currentCustomer) ? 0 : (int) conflictingAppointments.stream().filter((t)
                -> ModelHelper.areSameRecord(currentUser, t.getUser())).count();

        switch (cc) {
            case 0:
                switch (uc) {
                    case 0:
                        conflictMessage.set("");
                        break;
                    case 1:
                        conflictMessage.set(resources.getString(RESOURCEKEY_CONFLICTUSER1));
                        break;
                    default:
                        conflictMessage.set(resources.getString(RESOURCEKEY_CONFLICTUSERN));
                        break;
                }
                break;
            case 1:
                switch (uc) {
                    case 0:
                        conflictMessage.set(resources.getString(RESOURCEKEY_CONFLICTCUSTOMER1));
                        break;
                    case 1:
                        conflictMessage.set(resources.getString(RESOURCEKEY_CONFLICTCUSTOMER1USER1));
                        break;
                    default:
                        conflictMessage.set(resources.getString(RESOURCEKEY_CONFLICTCUSTOMER1USERN));
                        break;
                }
                break;
            default:
                switch (uc) {
                    case 0:
                        conflictMessage.set(resources.getString(RESOURCEKEY_CONFLICTCUSTOMERN));
                        break;
                    case 1:
                        conflictMessage.set(resources.getString(RESOURCEKEY_CONFLICTCUSTOMERNUSER1));
                        break;
                    default:
                        conflictMessage.set(resources.getString(RESOURCEKEY_CONFLICTCUSTOMERNUSERN));
                        break;
                }
                break;
        }
        if (null != dateRangeController) {
            dateRangeController.onConflictStateChanged();
        }
    }

    private void accept(List<AppointmentDAO> appointments) {
        currentCustomer = parentController.getCustomer();
        currentUser = parentController.getUser();
        conflictCheckingCurrent = true;
        allAppointments.clear();
        conflictingAppointments.clear();
        if (null == appointments || appointments.isEmpty()) {
            conflictMessage.set("");
            if (null != dateRangeController) {
                dateRangeController.onConflictStateChanged();
            }
            return;
        }
        appointments.stream().map((t) -> new AppointmentModel(t)).sorted(AppointmentModel::compareByDates)
                .forEachOrdered((t) -> allAppointments.add(t));
        refreshFromDateRange();
        updateConflictMessage();
        if (null != dateRangeController) {
            dateRangeController.onConflictStateChanged();
        }
    }

    void onCustomerChanged(CustomerModel value) {
        if (!ModelHelper.areSameRecord(value, currentCustomer)) {
            currentCustomer = value;
            conflictCheckingCurrent = false;
            conflictingAppointments.clear();
            updateConflictMessage();
            if (null != dateRangeController) {
                dateRangeController.onConflictStateChanged();
            }
        }
    }

    void onUserChanged(UserModel value) {
        if (!ModelHelper.areSameRecord(value, this.currentUser)) {
            currentUser = value;
            conflictCheckingCurrent = false;
            conflictingAppointments.clear();
            updateConflictMessage();
            if (null != dateRangeController) {
                dateRangeController.onConflictStateChanged();
            }
        }
    }

    void onTimeSpanChanged(Optional<ZonedAppointmentTimeSpan> value) {
        if (null != value && value.isPresent()) {
            ZonedAppointmentTimeSpan ts = value.get();
            if (null != ts) {
                if (!ts.equals(currentRange)) {
                    currentRange = ts;
                    refreshFromDateRange();
                    updateConflictMessage();
                    if (null != dateRangeController) {
                        dateRangeController.onConflictStateChanged();
                    }
                    return;
                }
            }
        }
        if (null != currentRange) {
            currentRange = null;
            refreshFromDateRange();
            updateConflictMessage();
            if (null != dateRangeController) {
                dateRangeController.onConflictStateChanged();
            }
        }
    }

    boolean isConflictCheckingCurrent() {
        return conflictCheckingCurrent && (null != currentCustomer || null != currentUser);
    }

    boolean hasConflicts() {
        return !conflictingAppointments.isEmpty();
    }

    private class AppointmentReloadTask extends TaskWaiter<List<AppointmentDAO>> {

        private final CustomerDAO customer;
        private final UserDAO user;

        private AppointmentReloadTask() {
            super((Stage) conflictingAppointmentsTableView.getScene().getWindow(), AppResources.getResourceString(RESOURCEKEY_CONNECTINGTODB),
                    AppResources.getResourceString(RESOURCEKEY_LOADINGAPPOINTMENTS));
            CustomerModel sc = parentController.getCustomer();
            UserModel su = parentController.getUser();
            customer = (null == sc) ? null : sc.getDataObject();
            user = (null == su) ? null : su.getDataObject();
        }

        @Override
        protected void processResult(List<AppointmentDAO> result, Stage stage) {
            accept(result);
        }

        @Override
        protected void processException(Throwable ex, Stage stage) {
            ErrorDetailControl.logShowAndWait(LOG, AppResources.getResourceString(RESOURCEKEY_DBREADERROR), stage, ex);
        }

        @Override
        protected List<AppointmentDAO> getResult(Connection connection) throws SQLException {
            updateMessage(AppResources.getResourceString(RESOURCEKEY_CONNECTEDTODB));
            AppointmentDAO.FactoryImpl af = AppointmentDAO.getFactory();
            if (null != customer && customer.isExisting()) {
                if (null != user && user.isExisting()) {
                    return af.load(connection, AppointmentFilter.of(customer, user, null, null));
                }
                return af.load(connection, AppointmentFilter.of(customer, null, null, null));
            }

            if (null != user && user.isExisting()) {
                return af.load(connection, AppointmentFilter.of(null, user, null, null));
            }
            return null;
        }

    }

}
