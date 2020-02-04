package scheduler.view.appointment;

import java.io.IOException;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.Window;
import scheduler.dao.AppointmentFactory;
import scheduler.dao.AppointmentImpl;
import scheduler.filter.ModelFilter;
import scheduler.util.Alerts;
import scheduler.view.CrudAction;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.ListingController;
import scheduler.view.MainController;
import scheduler.view.TaskWaiter;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
@GlobalizationResource("scheduler/view/appointment/ManageAppointments")
@FXMLResource("/scheduler/view/appointment/ManageAppointments.fxml")
public class ManageAppointments extends ListingController<AppointmentModel> {
    //<editor-fold defaultstate="collapsed" desc="Resource keys">

//    public static final String RESOURCEKEY_CUSTOMER = "customer";
//    public static final String RESOURCEKEY_END = "end";
//    public static final String RESOURCEKEY_START = "start";
//    public static final String RESOURCEKEY_TITLE = "title";
//    public static final String RESOURCEKEY_TYPE = "type";
//    public static final String RESOURCEKEY_USER = "user";
//    public static final String RESOURCEKEY_NEW = "new";
    public static final String RESOURCEKEY_APPOINTMENTSFORUSER = "appointmentsForUser";
    public static final String RESOURCEKEY_MANAGEAPPOINTMENTS = "manageAppointments";
    public static final String RESOURCEKEY_APPOINTMENTSFORCUSTOMER = "appointmentsForCustomer";
    public static final String RESOURCEKEY_CURRENTANDFUTUREAPPOINTMENTS = "currentAndFutureAppointments";
    public static final String RESOURCEKEY_INRANGE = "inRange";
    public static final String RESOURCEKEY_ONORAFTER = "onOrAfter";
    public static final String RESOURCEKEY_ONORBEFORE = "onOrBefore";
    public static final String RESOURCEKEY_ONDATE = "onDate";
    public static final String RESOURCEKEY_APPOINTMENTSINRANGE = "appointmentsInRange";
    public static final String RESOURCEKEY_APPOINTMENTSONORAFTER = "appointmentsOnOrAfter";
    public static final String RESOURCEKEY_APPOINTMENTSONORBEFORE = "appointmentsOnOrBefore";
    public static final String RESOURCEKEY_APPOINTMENTSONDATE = "appointmentsOnDate";
    public static final String RESOURCEKEY_CURRENTANDFUTURE = "currentAndFuture";
    public static final String RESOURCEKEY_LOADINGAPPOINTMENTS = "loadingAppointments";
    public static final String RESOURCEKEY_ERRORLOADINGAPPOINTMENTS = "errorLoadingAppointments";
    public static final String RESOURCEKEY_DBACCESSERROR = "dbAccessError";
//    public static final String RESOURCEKEY_EDIT = "edit";
//    public static final String RESOURCEKEY_DELETE = "delete";
//    public static final String RESOURCEKEY_CREATEDON = "createdOn";
//    public static final String RESOURCEKEY_CREATEDBY = "createdBy";
//    public static final String RESOURCEKEY_UPDATEDON = "updatedOn";
//    public static final String RESOURCEKEY_UPDATEDBY = "updatedBy";
    
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
    
    public static void loadInto(MainController mc, Stage stage, AppointmentsViewOptions filter) throws IOException {
        loadInto(ManageAppointments.class, mc, stage, filter);
    }
    
    @Override
    protected void onFilterChanged(Stage owner) {
        TaskWaiter.execute(new AppointmentsLoadTask(owner));
    }

    private class AppointmentsLoadTask extends ItemsLoadTask<AppointmentImpl> {
        AppointmentsLoadTask(Stage owner) {
            super(owner, getResourceString(RESOURCEKEY_LOADINGAPPOINTMENTS));
        }
        
        @Override
        protected Iterable<AppointmentImpl> getResult(Connection connection, ModelFilter<AppointmentModel> filter) throws Exception {
            LOG.log(Level.INFO, "Invoking AppointmentsLoadTask.getResult");
            return (new AppointmentFactory()).load(connection, filter);
        }

        @Override
        protected AppointmentModel toModel(AppointmentImpl result) { return new AppointmentModel(result); }
        
        @Override
        protected void processNullResult(Window owner) {
            LOG.log(Level.SEVERE, String.format("\"%s\" operation returned null", getTitle()));
            Alerts.showErrorAlert(getResourceString(RESOURCEKEY_DBACCESSERROR), getResourceString(RESOURCEKEY_ERRORLOADINGAPPOINTMENTS));
        }
        
        @Override
        protected void onItemsLoaded(ModelFilter<AppointmentModel> filter, Window owner) {
             super.onItemsLoaded(filter, owner);
             ((Stage)owner).setTitle(((AppointmentsViewOptions)filter).getWindowTitle(getResources()));
        }
        
        @Override
        protected void processException(Throwable ex, Window owner) {
            super.processException(ex, owner);
            Alerts.showErrorAlert(getResourceString(RESOURCEKEY_DBACCESSERROR), getResourceString(RESOURCEKEY_ERRORLOADINGAPPOINTMENTS));
        }
        
    }
    
    @Override
    protected void onAddNewItem(Event event) {
        getMainController().addNewAppointment(event);
    }

    @Override
    protected CrudAction<AppointmentModel> onEditItem(Event event, AppointmentModel item) {
        return getMainController().editAppointment(event, item);
    }

    @Override
    protected void onDeleteItem(Event event, AppointmentModel item) {
        getMainController().deleteAppointment(event, item);
    }

}
