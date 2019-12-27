package controller;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import model.db.AppointmentRow;
import model.db.UserRow;
import scheduler.InvalidArgumentException;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
public class ManageAppointmentsController implements Initializable {
    /**
     * The name of the globalization resource bundle for this controller.
     */
    public static final String RESOURCE_NAME = "globalization/manageAppointments";

    /**
     * The path of the View associated with this controller.
     */
    public static final String VIEW_PATH = "/view/ManageAppointments.fxml";
    
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
    
    private final scheduler.App.StageManager stageManager;
    
    public ManageAppointmentsController(scheduler.App.StageManager stageManager) {
        this.stageManager = stageManager;
    }
    
    /**
     * Initializes the controller class.
     * @param url The URL of the associated view.
     * @param rb The resources provided by the {@link javafx.fxml.FXMLLoader}
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }   

    public static void setCurrentScene(scheduler.App.StageManager stageManager) throws InvalidArgumentException {
        setCurrentScene(stageManager, null);
    }

    public static void setCurrentScene(scheduler.App.StageManager stageManager, UserRow user) throws InvalidArgumentException {
        if (user != null) {
            if (user.getRowState() == UserRow.ROWSTATE_DELETED)
                throw new InvalidArgumentException("user", "User was already deleted");
            if (user.getRowState() == UserRow.ROWSTATE_NEW)
                throw new InvalidArgumentException("user", "User has not been saved");
        }
        stageManager.setSceneWithControllerFactory(VIEW_PATH, RESOURCE_NAME, (Class<?> c) -> new ManageAppointmentsController(stageManager), (ResourceBundle rb, ManageAppointmentsController controller) -> {
            if (user == null)
                stageManager.setWindowTitle(rb.getString("manageAppointments"));
            else
                stageManager.setWindowTitle(String.format(rb.getString("appointmentsForUser"), user.getUserName()));
        });
    }
}
