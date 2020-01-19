package view.appointment;

import java.sql.Connection;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import view.annotations.FXMLResource;
import view.annotations.GlobalizationResource;
//import model.db.AppointmentsFilter;
import scheduler.App;
import scheduler.dao.AppointmentImpl;
import util.Alerts;
import view.RootController;
import view.SchedulerController;
import view.TaskWaiter;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
@GlobalizationResource("view/appointment/ManageAppointments")
@FXMLResource("/view/appointment/ManageAppointments.fxml")
public class ManageAppointments extends view.ListingController<AppointmentModel> {
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
    
    private AppointmentFilter currentFilter;
    
    private final ChangeListener<? super SchedulerController> controllerChangeListener;
    
    private final ChangeListener<? super RootController.CrudAction<AppointmentImpl>> appointmentAddedListener;
    
    //<editor-fold defaultstate="collapsed" desc="Initialization">
    
    @SuppressWarnings("Convert2Lambda")
    public ManageAppointments() {
        controllerChangeListener = new ChangeListener<SchedulerController>() {
            @Override
            public void changed(ObservableValue<? extends SchedulerController> observable, SchedulerController oldValue, SchedulerController newValue) {
                if (oldValue != null && oldValue == ManageAppointments.this) {
                    RootController rootCtl = RootController.getCurrent();
                    rootCtl.currentContentControllerProperty().removeListener(controllerChangeListener);
                    rootCtl.appointmentAddedProperty().removeListener(appointmentAddedListener);
                }
            }
        };
        appointmentAddedListener = new ChangeListener<RootController.CrudAction<AppointmentImpl>>() {
            @Override
            public void changed(ObservableValue<? extends RootController.CrudAction<AppointmentImpl>> observable,
                RootController.CrudAction<AppointmentImpl> oldValue, RootController.CrudAction<AppointmentImpl> newValue) {
//                if (newValue != null && (currentFilter == null || currentFilter.test(newValue.getRow()))) {
//                    if (newValue.isDelete())
//                        removeListItemByPrimaryKey(newValue.getRow().getPrimaryKey());
//                    else if (newValue.isAdd() || !updateListItem(newValue.getRow()))
//                        getItemsList().add(newValue.getRow());
//                }
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
    }
    
    @FXML // This method is called by the FXMLLoader when initialization is complete
    @Override
    protected void initialize() {
        assert headingLabel != null : String.format("fx:id=\"headingLabel\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
    }
    
    public static void setAsRootContent() {
        setAsRootContent(ManageAppointments.class, (ContentChangeContext<ManageAppointments> context) -> {
            context.setWindowTitle(context.getResources().getString(RESOURCEKEY_MANAGEAPPOINTMENTS));
            ManageAppointments controller = context.getController();
            collapseNode(controller.headingLabel);
        }, (ContentChangeContext<ManageAppointments> context) -> {
            TaskWaiter.callAsync(App.getCurrent().getPrimaryStage(), context.getResources().getString(RESOURCEKEY_LOADINGAPPOINTMENTS),
                    (Connection c) -> {
                        // AppointmentImpl.load(c, null, null);
                        throw new UnsupportedOperationException("Not supported yet.");
                    },
                    (ObservableList<AppointmentImpl> apptList) -> {
                        throw new UnsupportedOperationException("Not supported yet.");
//                        ObservableList<AppointmentImpl> itemsList = context.getController().getItemsList();
//                        itemsList.clear();
//                        apptList.forEach((a) -> {
//                            itemsList.add(a);
//                        });
                    }, (Exception ex) -> {
                        LOG.log(Level.SEVERE, null, ex);
                        ResourceBundle rb = App.getCurrent().getResources();
                        Alerts.showErrorAlert(rb.getString(App.RESOURCEKEY_DBACCESSERROR), rb.getString(App.RESOURCEKEY_DBREADERROR));
                        context.getController().getItemsList().clear();
                    });
            RootController rootCtl = RootController.getCurrent();
            ManageAppointments c = context.getController();
            rootCtl.currentContentControllerProperty().addListener(c.controllerChangeListener);
            rootCtl.appointmentAddedProperty().addListener(c.appointmentAddedListener);
        });
    }
    
    public static void setAsRootContent(view.ModelFilter<AppointmentImpl, AppointmentModel> filter) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    //</editor-fold>
    
    @Override
    protected void onAddNewItem(Event event) {
        RootController.getCurrent().addNewAppointment(event);
    }

    @Override
    protected void onEditItem(Event event, AppointmentModel item) {
        RootController.getCurrent().editAppointment(event, item);
    }

    @Override
    protected void onDeleteItem(Event event, AppointmentModel item) {
        RootController.getCurrent().deleteAppointment(event, item);
    }
    
}
