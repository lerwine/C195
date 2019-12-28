package scene.customer;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import model.db.CustomerRow;
import scheduler.InvalidArgumentException;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
public class ManageCustomers implements Initializable {
    /**
     * The name of the globalization resource bundle for this controller.
     */
    public static final String GLOBALIZATION_RESOURCE_NAME = "globalization/manageCustomers";

    /**
     * The path of the View associated with this controller.
     */
    public static final String FXML_RESOURCE_NAME = "/view/ManageCustomers.fxml";

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
    
    private java.lang.Runnable closeWindow;
    
    /**
     * Initializes the controller class.
     * @param url The URL of the associated view.
     * @param rb The resources provided by the {@link javafx.fxml.FXMLLoader}
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }
    
    public static void setAsRootStageScene() {
        scheduler.App.getCurrent().changeRootStageScene(GLOBALIZATION_RESOURCE_NAME, FXML_RESOURCE_NAME, (ResourceBundle rb, Stage stage) -> {
            stage.setTitle(rb.getString("manageCustomers"));
        });
    }
}
