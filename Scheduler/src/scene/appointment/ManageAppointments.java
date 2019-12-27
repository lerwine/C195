package scene.appointment;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import model.db.AppointmentRow;
import model.db.UserRow;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
public class ManageAppointments implements Initializable {
    /**
     * The name of the globalization resource bundle for this controller.
     */
    public static final String RESOURCE_NAME = "scene/appointment/ManageAppointments";

    /**
     * The path of the View associated with this controller.
     */
    public static final String VIEW_PATH = "/scene/appointment/ManageAppointments.fxml";

    @FXML
    private Label headingLabel;
    
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
    
    private java.lang.Runnable closeWindow;
    
    /**
     * Initializes the controller class.
     * @param url The URL of the associated view.
     * @param rb The resources provided by the {@link javafx.fxml.FXMLLoader}
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public static void show(UserRow row) {
        ManageAppointments controller = new ManageAppointments();
        scheduler.util.showAndWait(controller, RESOURCE_NAME, VIEW_PATH, 640, 480, (rb, stage) -> {
            controller.closeWindow = () -> stage.hide();
            stage.setTitle(rb.getString("appointmentsForUser"));
        });
    }
    
    public static void show() {
        ManageAppointments controller = new ManageAppointments();
        scheduler.util.showAndWait(controller, RESOURCE_NAME, VIEW_PATH, 640, 480, (rb, stage) -> {
            controller.closeWindow = () -> stage.hide();
            stage.setTitle(rb.getString("manageAppointments"));
        });
    }
}
