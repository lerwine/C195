package scheduler.view.appointment;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.Window;
import scheduler.App;
import scheduler.dao.AppointmentImpl;
import scheduler.dao.DataObjectImpl;
import scheduler.dao.LookupFilter;
import scheduler.util.Alerts;
import scheduler.view.EditItem;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.ListingController;

/**
 * FXML Controller class for listing {@link AppointmentModel} items.
 *
 * @author Leonard T. Erwine
 */
@GlobalizationResource("scheduler/view/appointment/ManageAppointments")
@FXMLResource("/scheduler/view/appointment/ManageAppointments.fxml")
public final class ManageAppointments extends ListingController<AppointmentImpl, AppointmentModel> {

    //<editor-fold defaultstate="collapsed" desc="Resource bundle keys">
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Add New Appointment"}.
     */
    public static final String RESOURCEKEY_ADDNEWAPPOINTMENT = "addNewAppointment";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Current Time Zone:"}.
     */
    public static final String RESOURCEKEY_CURRENTTIMEZONE = "currentTimeZone";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Customer:"}.
     */
    public static final String RESOURCEKEY_CUSTOMER = "customer";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Customer not found. Click "%s" button to add new customer."}.
     */
    public static final String RESOURCEKEY_CUSTOMERNOTFOUND = "customerNotFound";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Description:"}.
     */
    public static final String RESOURCEKEY_DESCRIPTION = "description";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Edit Appointment"}.
     */
    public static final String RESOURCEKEY_EDITAPPOINTMENT = "editAppointment";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "End:"}.
     */
    public static final String RESOURCEKEY_END = "end";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "End cannot be before Start."}.
     */
    public static final String RESOURCEKEY_ENDCANNOTBEBEFORESTART = "endCannotBeBeforeStart";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Invalid URL format"}.
     */
    public static final String RESOURCEKEY_INVALIDURL = "invalidUrl";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Location:"}.
     */
    public static final String RESOURCEKEY_LOCATION = "location";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Point of Contact:"}.
     */
    public static final String RESOURCEKEY_POINTOFCONTACT = "pointOfContact";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Show"}.
     */
    public static final String RESOURCEKEY_SHOW = "show";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Start:"}.
     */
    public static final String RESOURCEKEY_START = "start";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "%s to %s"}.
     */
    public static final String RESOURCEKEY_TIMERANGE = "timeRange";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Time Zone:"}.
     */
    public static final String RESOURCEKEY_TIMEZONE = "timeZone";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Title:"}.
     */
    public static final String RESOURCEKEY_TITLE = "title";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Type:"}.
     */
    public static final String RESOURCEKEY_TYPE = "type";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "User:"}.
     */
    public static final String RESOURCEKEY_USER = "user";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "User not found. Click "%s" button to add new user."}.
     */
    public static final String RESOURCEKEY_USERNOTFOUND = "userNotFound";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Phone Number:"}.
     */
    public static final String RESOURCEKEY_PHONENUMBER = "phoneNumber";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Meeting URL:"}.
     */
    public static final String RESOURCEKEY_MEETINGURL = "meetingUrl";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Invalid hour value"}.
     */
    public static final String RESOURCEKEY_INVALIDHOUR = "invalidHour";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Invalid minute value"}.
     */
    public static final String RESOURCEKEY_INVALIDMINUTE = "invalidMinute";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "This conflicts with %d customer appointments and %d user appointments..."}.
     */
    public static final String RESOURCEKEY_CONFLICTCUSTOMERNUSERN = "conflictCustomerNUserN";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "This conflicts with %d customer appointments and 1 user appointment..."}.
     */
    public static final String RESOURCEKEY_CONFLICTCUSTOMERNUSER1 = "conflictCustomerNUser1";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "This conflicts with 1 customer appointment and %d user appointments..."}.
     */
    public static final String RESOURCEKEY_CONFLICTCUSTOMER1USERN = "conflictCustomer1UserN";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "This conflicts with 1 customer appointment and 1 user appointment..."}.
     */
    public static final String RESOURCEKEY_CONFLICTCUSTOMER1USER1 = "conflictCustomer1User1";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "This conflicts with %d customer appointments..."}.
     */
    public static final String RESOURCEKEY_CONFLICTCUSTOMERN = "conflictCustomerN";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "This conflicts with 1 customer appointment..."}.
     */
    public static final String RESOURCEKEY_CONFLICTCUSTOMER1 = "conflictCustomer1";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "This conflicts with %d user appointments..."}.
     */
    public static final String RESOURCEKEY_CONFLICTUSERN = "conflictUserN";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "This conflicts with 1 user appointment..."}.
     */
    public static final String RESOURCEKEY_CONFLICTUSER1 = "conflictUser1";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Conflicts Found"}.
     */
    public static final String RESOURCEKEY_CONFLICTSFOUND = "conflictsFound";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Appointment Conflict"}.
     */
    public static final String RESOURCEKEY_APPOINTMENTCONFLICT = "appointmentConflict";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Loading Appointments"}.
     */
    public static final String RESOURCEKEY_LOADINGAPPOINTMENTS = "loadingAppointments";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "There was an error loading appointments..."}.
     */
    public static final String RESOURCEKEY_ERRORLOADINGAPPOINTMENTS = "errorLoadingAppointments";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Manage Appointments"}.
     */
    public static final String RESOURCEKEY_MANAGEAPPOINTMENTS = "manageAppointments";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Current and Future Appointments"}.
     */
    public static final String RESOURCEKEY_CURRENTANDFUTURE = "currentAndFuture";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Appointments for User \"%s\""}.
     */
    public static final String RESOURCEKEY_APPOINTMENTSFORUSER = "appointmentsForUser";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Appointments for Customer \"%s\""}.
     */
    public static final String RESOURCEKEY_APPOINTMENTSFORCUSTOMER = "appointmentsForCustomer";

    //</editor-fold>
    private static final Logger LOG = Logger.getLogger(ManageAppointments.class.getName());

    //<editor-fold defaultstate="collapsed" desc="FXMLLoader Injections">
    @FXML
    private Label headingLabel;

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Initialization">
    public ManageAppointments() {
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    @Override
    protected void initialize() {
        super.initialize();
        assert headingLabel != null : String.format("fx:id=\"headingLabel\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
    }

    //</editor-fold>
//    public static void setContent(MainController mc, Stage stage, AppointmentsViewOptions filter) throws IOException {
//        throw new UnsupportedOperationException("Not implemented");
////        ListingController.setContent(ManageAppointments.class, mc, stage, filter);
//    }
//    @Override
//    protected void onFilterChanged(Stage owner) {
//        TaskWaiter.execute(new AppointmentsLoadTask(owner));
//    }
    @Override
    protected void onDeleteItem(Event event, AppointmentModel item) {
        try {
            getMainController().deleteAppointment(event, item, (connection) -> "");
        } catch (SQLException ex) {
            Logger.getLogger(ManageAppointments.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ManageAppointments.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected AppointmentModel toModel(AppointmentImpl result) {
        try {
            return new AppointmentModel(result);
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(ManageAppointments.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    protected LookupFilter<AppointmentImpl, AppointmentModel> getDefaultFilter() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected DataObjectImpl.Factory<AppointmentImpl> getDaoFactory() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private class AppointmentsLoadTask extends ItemsLoadTask {

        AppointmentsLoadTask(Stage owner) {
            super(owner, getResourceString(RESOURCEKEY_LOADINGAPPOINTMENTS));
        }

//        @Override
//        protected Iterable<AppointmentFactory.AppointmentImpl> getResult(Connection connection, ModelFilter<AppointmentModel> filter) throws Exception {
//            LOG.log(Level.INFO, "Invoking AppointmentsLoadTask.getResult");
//            return (new AppointmentFactory()).load(connection, filter);
//        }
        @Override
        protected void processNullResult(Window owner) {
            LOG.log(Level.SEVERE, String.format("\"%s\" operation returned null", getTitle()));
            Alerts.showErrorAlert(App.getResourceString(App.RESOURCEKEY_DBACCESSERROR), getResourceString(RESOURCEKEY_ERRORLOADINGAPPOINTMENTS));
        }

//        @Override
//        protected void onItemsLoaded(ModelFilter<AppointmentModel> filter, Window owner) {
//             super.onItemsLoaded(filter, owner);
//             ((Stage)owner).setTitle(((AppointmentsViewOptions)filter).getWindowTitle(getResources()));
//        }
        @Override
        protected void processException(Throwable ex, Window owner) {
            super.processException(ex, owner);
            Alerts.showErrorAlert(App.getResourceString(App.RESOURCEKEY_DBACCESSERROR), getResourceString(RESOURCEKEY_ERRORLOADINGAPPOINTMENTS));
        }

    }

    @Override
    protected void onAddNewItem(Event event) {
        getMainController().addNewAppointment(event);
    }

    @Override
    protected EditItem.ShowAndWaitResult<AppointmentModel> onEditItem(Event event, AppointmentModel item) {
        return getMainController().editAppointment(event, item);
    }

}
