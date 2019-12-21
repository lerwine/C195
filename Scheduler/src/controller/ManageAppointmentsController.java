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
    
    private String returnViewPath;
    
    private Stage currentStage;

    /**
     * Initializes the controller class.
     * @param url The URL of the associated view.
     * @param rb The resources provided by the {@link javafx.fxml.FXMLLoader}
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }   

    public static void setCurrentScene(Stage stage, String returnViewPath) throws InvalidArgumentException {
        setCurrentScene(stage, returnViewPath, null);
    }

    public static void setCurrentScene(Stage sourceStage, String returnViewPath, UserRow user) throws InvalidArgumentException {
        if (user != null) {
            if (user.getRowState() == UserRow.ROWSTATE_DELETED)
                throw new InvalidArgumentException("user", "User was already deleted");
            if (user.getRowState() == UserRow.ROWSTATE_NEW)
                throw new InvalidArgumentException("user", "User has not been saved");
        }
        scheduler.App.setScene(sourceStage, VIEW_PATH, RESOURCE_NAME, (Stage stage, ResourceBundle rb, ManageAppointmentsController controller) -> {
            controller.currentStage = stage;
            if (user == null) {
                stage.setTitle(rb.getString("manageAppointments"));
            } else {
                stage.setTitle(String.format(rb.getString("appointmentsForUser"), user.getUserName()));
            }
            controller.returnViewPath = returnViewPath;
        });
    }
}
