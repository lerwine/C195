package scheduler.view.appointment;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.App;
import scheduler.dao.AppointmentImpl;
import scheduler.dao.AppointmentFactory;
import scheduler.filter.ModelFilter;
import scheduler.util.Alerts;
import scheduler.view.MainController;
import scheduler.view.SchedulerController;
import scheduler.view.TaskWaiter;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
@GlobalizationResource("scheduler/view/appointment/ManageAppointments")
@FXMLResource("/scheduler/view/appointment/ManageAppointments.fxml")
public class ManageAppointments extends scheduler.view.ListingController<AppointmentModel> {
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

    @Override
    protected void setItemsFilter(ModelFilter<AppointmentModel> value) {
        assert value == null || value instanceof AppointmentsViewOptions : "Invalid items filter type";
        super.setItemsFilter(value); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void onItemsFilterChanged() {
        super.onItemsFilterChanged();
        AppointmentsViewOptions options = (AppointmentsViewOptions)getItemsFilter();
        getViewManager().setWindowTitle(options.getWindowTitle(getResources()));
        String heading = options.getHeadingText(getResources());
        if (heading.isEmpty())
            collapseNode(headingLabel);
        else
            restoreLabeled(headingLabel, heading);
        
        TaskWaiter.callAsync(getViewManager(), getResources().getString(RESOURCEKEY_LOADINGAPPOINTMENTS),
            (Connection c) -> (new AppointmentFactory()).load(c, options),
            (ArrayList<AppointmentImpl> apptList) -> {
                ObservableList<AppointmentModel> itemsList = getItemsList();
                itemsList.clear();
                apptList.forEach((a) -> {
                    itemsList.add(new AppointmentModel(a));
                });
            }, (Exception ex) -> {
                getItemsList().clear();
                LOG.log(Level.SEVERE, "Error loading items", ex);
                ResourceBundle rb = App.getCurrent().getResources();
                Alerts.showErrorAlert(rb.getString(App.RESOURCEKEY_DBACCESSERROR), rb.getString(App.RESOURCEKEY_DBREADERROR));
            });
    }

    @Override
    protected void onApplied(Parent currentView, SchedulerController oldController, Parent oldView) {
        getMainController().appointmentAddedProperty().addListener(appointmentAddedListener);
        super.onApplied(currentView, oldController, oldView);
        onItemsFilterChanged();
    }

    @Override
    protected void onUnloading(SchedulerController newController, Parent newParent) {
        getMainController().appointmentAddedProperty().removeListener(appointmentAddedListener);
        super.onUnloading(newController, newParent);
    }

    private final ChangeListener<? super MainController.CrudAction<AppointmentModel>> appointmentAddedListener;
    
    //<editor-fold defaultstate="collapsed" desc="Initialization">
    
    @SuppressWarnings("Convert2Lambda")
    public ManageAppointments() {
        appointmentAddedListener = new ChangeListener<MainController.CrudAction<AppointmentModel>>() {
            @Override
            public void changed(ObservableValue<? extends MainController.CrudAction<AppointmentModel>> observable,
                MainController.CrudAction<AppointmentModel> oldValue, MainController.CrudAction<AppointmentModel> newValue) {
                if (newValue != null && getItemsFilter().test(newValue.getModel())) {
                    if (newValue.isDelete())
                        removeListItemByPrimaryKey(newValue.getModel().getDataObject().getPrimaryKey());
                    else if (newValue.isAdd() || !updateListItem(newValue.getModel()))
                        getItemsList().add(newValue.getModel());
                }
            }
        };
    }
    
    @FXML // This method is called by the FXMLLoader when initialization is complete
    @Override
    protected void initialize() {
        super.initialize();
        assert headingLabel != null : String.format("fx:id=\"headingLabel\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
    }
    
    //</editor-fold>
    
    @Override
    protected void onAddNewItem(Event event) {
        MainController.getCurrent().addNewAppointment(event);
    }

    @Override
    protected void onEditItem(Event event, AppointmentModel item) {
        MainController.getCurrent().editAppointment(event, item);
    }

    @Override
    protected void onDeleteItem(Event event, AppointmentModel item) {
        MainController.getCurrent().deleteAppointment(event, item);
    }
    
}
