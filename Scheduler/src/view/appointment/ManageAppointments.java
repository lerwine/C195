package view.appointment;

import com.mysql.jdbc.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import model.db.AppointmentRow;
import view.annotations.FXMLResource;
import view.annotations.GlobalizationResource;
import model.db.AppointmentsFilter;
import model.db.DataRow;
import scheduler.SqlConnectionDependency;
import scheduler.Util;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
@GlobalizationResource("view/appointment/ManageAppointments")
@FXMLResource("/view/appointment/ManageAppointments.fxml")
public class ManageAppointments extends view.ListingController {
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
//    public static final String RESOURCEKEY_EDIT = "edit";
//    public static final String RESOURCEKEY_DELETE = "delete";
//    public static final String RESOURCEKEY_CREATEDON = "createdOn";
//    public static final String RESOURCEKEY_CREATEDBY = "createdBy";
//    public static final String RESOURCEKEY_UPDATEDON = "updatedOn";
//    public static final String RESOURCEKEY_UPDATEDBY = "updatedBy";

    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="FXMLLoader Injections">
    
    @FXML
    private Label headingLabel;
    
    //</editor-fold>
    
    private AppointmentsFilter currentFilter;
    
    private final ChangeListener<? super view.Controller> controllerChangeListener;
    
    private final ChangeListener<? super AppointmentRow> appointmentAddedListener;

    @SuppressWarnings("Convert2Lambda")
    public ManageAppointments() {
        controllerChangeListener = new ChangeListener<view.Controller>() {
            @Override
            public void changed(ObservableValue<? extends view.Controller> observable, view.Controller oldValue, view.Controller newValue) {
                if (oldValue != null && oldValue == ManageAppointments.this) {
                    view.RootController rootCtl = view.RootController.getCurrent();
                    rootCtl.currentContentControllerProperty().removeListener(controllerChangeListener);
                    rootCtl.appointmentAddedProperty().removeListener(appointmentAddedListener);
                }
            }
        };
        appointmentAddedListener = new ChangeListener<AppointmentRow>() {
            @Override
            public void changed(ObservableValue<? extends AppointmentRow> observable, AppointmentRow oldValue, AppointmentRow newValue) {
                if (newValue != null && (currentFilter == null || currentFilter.test(newValue)))
                    getItemsList().add(newValue);
            }
        };
    }
    
    @FXML // This method is called by the FXMLLoader when initialization is complete
    @Override
    protected void initialize() {
        assert headingLabel != null : String.format("fx:id=\"headingLabel\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
    }

    @SuppressWarnings("UseSpecificCatch")
    public static void setAsRootContent() {
        setAsRootContent(ManageAppointments.class, (ContentChangeContext<ManageAppointments> context) -> {
            context.setWindowTitle(context.getResources().getString(RESOURCEKEY_MANAGEAPPOINTMENTS));
            ManageAppointments controller = context.getController();
            collapseNode(controller.headingLabel);
            ObservableList<AppointmentRow> apptList;
            try {
                apptList = SqlConnectionDependency.get((Connection connection) -> {
                    try {
                        return AppointmentRow.getAll(connection);
                    } catch (SQLException ex) {
                        Logger.getLogger(ManageAppointments.class.getName()).log(Level.SEVERE, null, ex);
                        throw new RuntimeException("Error getting appointments by filter", ex);
                    }
                });
            } catch (Exception ex) {
                Logger.getLogger(ManageAppointments.class.getName()).log(Level.SEVERE, null, ex);
                Util.showErrorAlert("Database access error", "Error reading data from database. See logs for details.");
                apptList = FXCollections.observableArrayList();
            }
            ObservableList<AppointmentRow> itemsList = controller.getItemsList();
            itemsList.clear();
            for (AppointmentRow a : apptList)
                itemsList.add(a);
        }, (ContentChangeContext<ManageAppointments> context) -> {
            view.RootController rootCtl = view.RootController.getCurrent();
            ManageAppointments c = context.getController();
            rootCtl.currentContentControllerProperty().addListener(c.controllerChangeListener);
            rootCtl.appointmentAddedProperty().addListener(c.appointmentAddedListener);
        });
    }

    @SuppressWarnings("UseSpecificCatch")
    public static void setAsRootContent(AppointmentsFilter filter) {
        setAsRootContent(ManageAppointments.class, (ContentChangeContext<ManageAppointments> context) -> {
            ResourceBundle rb = context.getResources();
            context.setWindowTitle(filter.getWindowTitle(rb));
            ManageAppointments controller = context.getController();
            controller.currentFilter = filter;
            String subHeading = filter.getSubHeading(rb);
            if (subHeading.isEmpty())
                collapseNode(controller.headingLabel);
            else
                controller.headingLabel.setText(subHeading);
            ObservableList<AppointmentRow> apptList;
            try {
                apptList = SqlConnectionDependency.get((Connection connection) -> {
                    try {
                        return AppointmentRow.getByFilter(connection, filter);
                    } catch (SQLException ex) {
                        Logger.getLogger(ManageAppointments.class.getName()).log(Level.SEVERE, null, ex);
                        throw new RuntimeException("Error getting appointments by filter", ex);
                    }
                });
            } catch (Exception ex) {
                Logger.getLogger(ManageAppointments.class.getName()).log(Level.SEVERE, null, ex);
                Util.showErrorAlert("Database access error", "Error reading data from database. See logs for details.");
                apptList = FXCollections.observableArrayList();
            }
            ObservableList<AppointmentRow> itemsList = controller.getItemsList();
            itemsList.clear();
            for (AppointmentRow a : apptList)
                itemsList.add(a);
        });
    }
    
    @Override
    protected void onAddNewItem() {
        view.RootController.getCurrent().addNewAppointment();
    }

    @Override
    protected void onEditItem(DataRow item) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void onDeleteItem(DataRow item) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}