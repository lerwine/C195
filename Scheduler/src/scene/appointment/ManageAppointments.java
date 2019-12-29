package scene.appointment;

import com.mysql.jdbc.Connection;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import model.db.AppointmentRow;
import scene.annotations.FXMLResource;
import scene.annotations.GlobalizationResource;
import model.db.AppointmentsFilter;
import scheduler.SqlConnectionDependency;
import scheduler.Util;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
@GlobalizationResource("scene/appointment/ManageAppointments")
@FXMLResource("/scene/appointment/ManageAppointments.fxml")
public class ManageAppointments extends scene.ListingController {
    @FXML
    private Label headingLabel;
    
    private final ObservableList<AppointmentRow> appointments = FXCollections.observableArrayList();
    
    @FXML
    private TableView<AppointmentRow> todayAndFutureAppointmenstTableView;
    
    @FXML
    private MenuItem editAppointmentMenuItem;

    @FXML
    private TableColumn<AppointmentRow, String> titleTableColumn;
    
    @FXML
    private TableColumn<AppointmentRow, LocalDateTime> startTableColumn;
    
    @FXML
    private TableColumn<AppointmentRow, LocalDateTime> endTableColumn;
    
    @FXML
    private TableColumn<AppointmentRow, String> typeTableColumn;
    
    @FXML
    private TableColumn<AppointmentRow, model.Customer> customerTableColumn;
    
    @FXML
    private TableColumn<AppointmentRow, model.User> userTableColumn;
    
    /**
     * Initializes the controller class.
     * @param url The URL of the associated view.
     * @param rb The resources provided by the {@link javafx.fxml.FXMLLoader}
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        todayAndFutureAppointmenstTableView.setItems(appointments);
    }

    @SuppressWarnings("UseSpecificCatch")
    public static void setAsRootContent() {
        setAsRootContent(ManageAppointments.class, (SetContentContext<ManageAppointments> context) -> {
            context.getStage().setTitle(context.getResourceBundle().getString("manageAppointments"));
            ManageAppointments controller = context.getController();
            Util.collapseLabeledVertical(controller.headingLabel);
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
            controller.appointments.clear();
            for (AppointmentRow a : apptList)
                controller.appointments.add(a);
        });
    }

    @SuppressWarnings("UseSpecificCatch")
    public static void setAsRootContent(AppointmentsFilter filter) {
        setAsRootContent(ManageAppointments.class, (SetContentContext<ManageAppointments> context) -> {
            ResourceBundle rb = context.getResourceBundle();
            context.getStage().setTitle(filter.getWindowTitle(rb));
            ManageAppointments controller = context.getController();
            String subHeading = filter.getSubHeading(rb);
            if (subHeading.isEmpty())
                Util.collapseLabeledVertical(controller.headingLabel);
            else
                Util.restoreLabeledVertical(controller.headingLabel, subHeading);
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
            controller.appointments.clear();
            for (AppointmentRow a : apptList)
                controller.appointments.add(a);
        });
    }
}
