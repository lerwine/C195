package controller;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import model.annotations.ResourceKey;
import model.annotations.ResourceName;
import model.db.AppointmentRow;
import model.db.UserRow;
import scheduler.InvalidArgumentException;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
@ResourceName(ManageAppointmentsController.RESOURCE_NAME)
public class ManageAppointmentsController extends ControllerBase {
    /**
     * The name of the globalization resource bundle for this controller.
     */
    public static final String RESOURCE_NAME = "globalization/manageAppointments";

    /**
     * The path of the View associated with this controller.
     */
    public static final String VIEW_PATH = "/view/ManageAppointments.fxml";
    
    @FXML
    @ResourceKey("loadingData")
    private Label headingLabel;
    
    @FXML
    private TableView<AppointmentRow> todayAndFutureAppointmenstTableView;
    
    @FXML
    @ResourceKey("editAppointment")
    private MenuItem editAppointmentMenuItem;

    @FXML
    @ResourceKey("title")
    private TableColumn<AppointmentRow, String> titleTableColumn;
    
    @FXML
    @ResourceKey("start")
    private TableColumn<AppointmentRow, LocalDateTime> startTableColumn;
    
    @FXML
    @ResourceKey("end")
    private TableColumn<AppointmentRow, LocalDateTime> endTableColumn;
    
    @FXML
    @ResourceKey("type")
    private TableColumn<AppointmentRow, String> typeTableColumn;
    
    @FXML
    @ResourceKey("customer")
    private TableColumn<AppointmentRow, model.Customer> customerTableColumn;
    
    @FXML
    @ResourceKey("user")
    private TableColumn<AppointmentRow, model.User> userTableColumn;
    
    private String returnViewPath;
    
    private Stage currentStage;

    /**
     * Initializes the controller class.
     * @param url The URL of the associated view.
     * @param rb The resources provided by the {@link javafx.fxml.FXMLLoader}
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);
    }   

    public static void setCurrentScene(Node sourceNode, String returnViewPath) throws InvalidArgumentException {
        setCurrentScene(sourceNode, returnViewPath, null);
    }

    public static void setCurrentScene(Node sourceNode, String returnViewPath, UserRow user) throws InvalidArgumentException {
        if (user != null) {
            if (user.getRowState() == UserRow.ROWSTATE_DELETED)
                throw new InvalidArgumentException("user", "User was already deleted");
            if (user.getRowState() == UserRow.ROWSTATE_NEW)
                throw new InvalidArgumentException("user", "User has not been saved");
        }
        scheduler.App.changeScene(sourceNode, VIEW_PATH, (Stage stage, ManageAppointmentsController controller) -> {
            controller.currentStage = stage;
            ResourceBundle rb = ResourceBundle.getBundle(RESOURCE_NAME, scheduler.App.getCurrentLocale());
            if (user == null) {
                stage.setTitle(rb.getString("manageAppointments"));
            } else {
                stage.setTitle(String.format(rb.getString("appointmentsForUser"), user.getUserName()));
            }
            controller.returnViewPath = returnViewPath;
        });
    }
}
