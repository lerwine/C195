/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.view.appointment;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Pair;
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
import scheduler.observables.BindingHelper;
import scheduler.observables.OptionalBinding;
import scheduler.util.NodeUtil;
import scheduler.view.ErrorDetailDialog;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import static scheduler.view.appointment.EditAppointmentResourceKeys.*;
import scheduler.view.customer.CustomerModel;
import scheduler.view.task.TaskWaiter;
import scheduler.view.user.UserModel;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/view/appointment/EditAppointment")
@FXMLResource("/scheduler/view/appointment/AppointmentConflicts.fxml")
public class AppointmentConflicts {

    private static final Logger LOG = Logger.getLogger(AppointmentConflicts.class.getName());

    private EditAppointment parentController;
    private DateRange dateRangeController;
    private ObservableList<AppointmentModel> allAppointments;
    private ObservableList<AppointmentModel> conflictingAppointments;
    private SimpleObjectProperty<Customer> currentCustomer;
    private SimpleObjectProperty<User> currentUser;
//    private ReadOnlyObjectProperty<CustomerModel> selectedCustomer;
//    private ReadOnlyObjectProperty<UserModel> selectedUser;
    private SimpleStringProperty conflictMessage;
//    private OptionalBinding<String> conflictMessageBinding;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // fx:id="rootBorderPane"
    private BorderPane rootBorderPane; // Value injected by FXMLLoader

    @FXML // fx:id="conflictingAppointmentsTableView"
    private TableView<AppointmentModel> conflictingAppointmentsTableView; // Value injected by FXMLLoader
    //private ObjectBinding<Pair<LocalDateTime, Duration>> selectedDateRange;

    @FXML
    void onCloseConflictsBorderPaneButtonAction(ActionEvent event) {
        rootBorderPane.setVisible(false);
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert rootBorderPane != null : "fx:id=\"rootBorderPane\" was not injected: check your FXML file 'AppointmentConflicts.fxml'.";
        assert conflictingAppointmentsTableView != null : "fx:id=\"conflictingAppointmentsTableView\" was not injected: check your FXML file 'AppointmentConflicts.fxml'.";
        currentCustomer = new SimpleObjectProperty<>(this, "currentCustomer", null);
        currentUser = new SimpleObjectProperty<>(this, "currentUser", null);
        conflictMessage = new SimpleStringProperty(this, "conflictMessage", "");
        allAppointments = FXCollections.observableArrayList();
        conflictingAppointments = FXCollections.observableArrayList();
    }

    EditAppointment getParentController() {
        return parentController;
    }

    void onParentListsLoaded(EditAppointment parentController, List<AppointmentDAO> appointments) {
        if (null != parentController)
            throw new IllegalStateException();
        this.parentController = parentController;
//        this.selectedCustomer = parentController.getCustomerSelectionModel().selectedItemProperty();
//        this.selectedUser = parentController.getUserSelectionModel().selectedItemProperty();
        this.dateRangeController = parentController.getDateRangeController();
        
    }

    
    @FXML
    private void onShowConflictsButtonAction(ActionEvent event) {
        rootBorderPane.setVisible(true);
    }

    private void onCheckConflictsButtonAction(ActionEvent event) {
        TaskWaiter.startNow(new AppointmentReloadTask());
    }

    void initializeConflicts(List<AppointmentDAO> appointments, EditAppointment parentController, DateRange dateRangeController) {
//        this.selectedCustomer = customerSelectionModel.selectedItemProperty();
//        this.selectedUser = userSelectionModel.selectedItemProperty();
        this.parentController = parentController;
        this.dateRangeController = dateRangeController;
        accept(appointments);
//        ObjectBinding<Pair<CustomerModel, UserModel>> selectedCustomerAndUser = Bindings.createObjectBinding(() -> {
//            return new Pair<>(selectedCustomer.get(), selectedUser.get());
//        }, selectedCustomer, selectedUser);
//        
//        conflictMessageBinding = BindingHelper.createOptionalBinding(() -> {
//            String m = conflictMessage.get();
//            Customer cc = currentCustomer.get();
//            Pair<CustomerModel, UserModel> s = selectedCustomerAndUser.get();
//            User cu = currentUser.get();
//            if (null == s.getKey()) {
//                if (null == s.getValue()) {
//                    return Optional.of("");
//                }
//                if (!ModelHelper.areSameRecord(s.getValue(), cu)) {
//                    return Optional.empty();
//                }
//            } else if (!ModelHelper.areSameRecord(s.getKey(), cc) || null != s.getValue() && !ModelHelper.areSameRecord(s.getValue(), cu)) {
//                return Optional.empty();
//            }
//            return Optional.of(m);
//        }, conflictMessage, currentCustomer, currentUser, selectedCustomerAndUser);

//        selectedDateRange.addListener((observable, oldValue, newValue) -> {
//            onDateRangeChanged(newValue.getKey(), newValue.getValue());
//        });
//        selectedCustomerAndUser.addListener((observable, oldValue, newValue) -> {
//            onCustomerOrUserChanged(newValue.getKey(), newValue.getValue());
//        });
//        dateRange.setConflictsBinding(conflictMessageBinding, this::onCheckConflictsButtonAction, this::onShowConflictsButtonAction);

    }

    private void onCustomerOrUserChanged(CustomerModel customer, UserModel user) {
        if (ModelHelper.areSameRecord(customer, currentCustomer.get())) {
            if (ModelHelper.areSameRecord(user, currentUser.get())) {
                return;
            }
            if (null != user) {
                conflictingAppointments.clear();
                return;
            }
            currentUser.set(null);
        } else if (null == customer) {
            if (!ModelHelper.areSameRecord(user, this.currentUser.get())) {
                currentCustomer.set(null);
                conflictingAppointments.clear();
                return;
            }
            if (null == user) {
                return;
            }
            currentCustomer.set(null);
        } else {
            if (null == user) {
                currentUser.set(null);
            }
            conflictingAppointments.clear();
            return;
        }
//        Pair<LocalDateTime, Duration> dr = selectedDateRange.get();
//        onDateRangeChanged(dr.getKey(), dr.getValue());

    }

    private void onDateRangeChanged(LocalDateTime start, Duration duration) {
        conflictingAppointments.clear();
        if (null == start || null == duration) {
            conflictMessage.set("");
            return;
        }
        LocalDateTime end = start.plus(duration);
        Stream<AppointmentModel> filtered = allAppointments.stream().filter((a) -> a.getStart().compareTo(end) < 0 && a.getEnd().compareTo(start) > 0);
        //.forEach((t) -> conflictingAppointments.add(t));
        Customer customer = currentCustomer.get();
        User user = currentUser.get();
        if (null == customer) {
            if (null == user) {
                conflictMessage.set("");
                return;
            }
            filtered = filtered.filter((t) -> ModelHelper.areSameRecord(user, t.getUser()));
        } else if (null == user) {
            filtered = filtered.filter((t) -> ModelHelper.areSameRecord(customer, t.getCustomer()));
        }
        filtered.forEach((t) -> conflictingAppointments.add(t));
        if (conflictingAppointments.isEmpty()) {
            conflictMessage.set("");
        } else {
            int cc = (null == customer) ? 0 : (int) conflictingAppointments.stream().filter((t)
                    -> ModelHelper.areSameRecord(customer, t.getCustomer())).count();
            int uc = (null == customer) ? 0 : (int) conflictingAppointments.stream().filter((t)
                    -> ModelHelper.areSameRecord(user, t.getUser())).count();

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
        }
    }

    private void accept(List<AppointmentDAO> appointments) {
        currentCustomer.set(parentController.getCustomer());
        currentUser.set(parentController.getUser());
        allAppointments.clear();
        conflictingAppointments.clear();
        if (null == appointments || appointments.isEmpty()) {
            conflictMessage.set("");
            return;
        }
        appointments.stream().map((t) -> new AppointmentModel(t)).sorted(AppointmentModel::compareByDates)
                .forEachOrdered((t) -> allAppointments.add(t));
//        Pair<LocalDateTime, Duration> dr = selectedDateRange.get();
//        onDateRangeChanged(dr.getKey(), dr.getValue());
    }

    void onCustomerChanged(CustomerModel value) {
//        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.view.appointment.AppointmentConflicts#onCustomerChanged
    }

    void onUserChanged(UserModel value) {
//        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.view.appointment.AppointmentConflicts#onUserChanged
    }

    void onTimeSpanChanged(Optional<ZonedAppointmentTimeSpan> get) {
//        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.view.appointment.AppointmentConflicts#onTimeSpanChanged
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
            ErrorDetailDialog.logShowAndWait(LOG, AppResources.getResourceString(RESOURCEKEY_DBREADERROR), stage, ex);
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
