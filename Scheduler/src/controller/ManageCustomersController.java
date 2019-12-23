package controller;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.db.CustomerRow;
import scheduler.InvalidArgumentException;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
public class ManageCustomersController implements Initializable {
    /**
     * The name of the globalization resource bundle for this controller.
     */
    public static final String RESOURCE_NAME = "globalization/manageCustomers";

    /**
     * The path of the View associated with this controller.
     */
    public static final String VIEW_PATH = "/view/ManageCustomers.fxml";

    @FXML
    private TableView<CustomerRow> customersTableView;

    @FXML
    private TableColumn<CustomerRow, String> customerNameTableColumn;

    @FXML
    private TableColumn<CustomerRow, Integer> addressTableColumn;

    @FXML
    private TableColumn<CustomerRow, Boolean> activeTableColumn;

    @FXML
    private TableColumn<CustomerRow, LocalDateTime> createDateTableColumn;

    @FXML
    private TableColumn<CustomerRow, String> createdByTableColumn;

    @FXML
    private TableColumn<CustomerRow, LocalDateTime> lastUpdateTableColumn;

    @FXML
    private TableColumn<CustomerRow, String> lastUpdateByTableColumn;
    
    private final scheduler.App.StageManager stageManager;
    
    public ManageCustomersController(scheduler.App.StageManager stageManager) {
        this.stageManager = stageManager;
    }
    
    /**
     * Initializes the controller class.
     * @param url The URL of the associated view.
     * @param rb The resources provided by the {@link javafx.fxml.FXMLLoader}
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        stageManager.setWindowTitle(rb.getString("manageCustomers"));
    }

    public static void setCurrentScene(scheduler.App.StageManager stageManager) throws InvalidArgumentException {
        stageManager.setSceneWithControllerFactory(VIEW_PATH, RESOURCE_NAME, (Class<?> c) -> new ManageCustomersController(stageManager));
    }
}
