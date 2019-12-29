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
import scene.annotations.FXMLResource;
import scene.annotations.GlobalizationResource;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
@GlobalizationResource("scene/customer/ManageCustomers")
@FXMLResource("/scene/customer/ManageCustomers.fxml")
public class ManageCustomers extends scene.ListingController {
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
    
    public static void setAsRootContent() {
        setAsRootContent(ManageCustomers.class, (scene.Controller.SetContentContext<ManageCustomers> context) -> {
            context.getStage().setTitle(context.getResourceBundle().getString("manageAppointments"));
            scheduler.Util.showErrorAlert("Not Implemented", "Need to initialize appointments list");
        });
    }
}
