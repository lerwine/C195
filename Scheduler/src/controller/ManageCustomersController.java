package controller;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.FormatStyle;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.db.CustomerRow;
import scheduler.InvalidArgumentException;
import model.annotations.ResourceKey;
import model.annotations.ResourceName;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
@ResourceName(ManageCustomersController.RESOURCE_NAME)
public class ManageCustomersController extends ControllerBase {
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
    @ResourceKey("customerName")
    private TableColumn<CustomerRow, String> customerNameTableColumn;

    @FXML
    @ResourceKey("address")
    private TableColumn<CustomerRow, Integer> addressTableColumn;

    @FXML
    @ResourceKey("active")
    private TableColumn<CustomerRow, Boolean> activeTableColumn;

    @FXML
    @ResourceKey("createdOn")
    private TableColumn<CustomerRow, LocalDateTime> createDateTableColumn;

    @FXML
    @ResourceKey("createdBy")
    private TableColumn<CustomerRow, String> createdByTableColumn;

    @FXML
    @ResourceKey("updatedOn")
    private TableColumn<CustomerRow, LocalDateTime> lastUpdateTableColumn;

    @FXML
    @ResourceKey("updatedBy")
    private TableColumn<CustomerRow, String> lastUpdateByTableColumn;
    
    private String returnViewPath;

    /**
     * Initializes the controller class.
     * @param url The URL of the associated view.
     * @param rb The resources provided by the {@link javafx.fxml.FXMLLoader}
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);
        customerNameTableColumn.setCellValueFactory(new PropertyValueFactory<>(CustomerRow.PROP_CUSTOMERNAME));
        activeTableColumn.setCellValueFactory(new PropertyValueFactory<>(CustomerRow.PROP_ACTIVE));
        addressTableColumn.setCellValueFactory(new PropertyValueFactory<>(CustomerRow.PROP_ADDRESSID));
        // TODO: Format address
        createDateTableColumn.setCellValueFactory(new PropertyValueFactory<>(CustomerRow.PROP_CREATEDATE));
        createDateTableColumn.setCellFactory(col -> new TableCell<CustomerRow, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty)
                    setText(null);
                else
                    setText(item.format(scheduler.App.getShortDateTimeFormatter()));
            }
        });
        createdByTableColumn.setCellValueFactory(new PropertyValueFactory<>(CustomerRow.PROP_CREATEDBY));
        lastUpdateTableColumn.setCellValueFactory(new PropertyValueFactory<>(CustomerRow.PROP_LASTUPDATE));
        lastUpdateTableColumn.setCellFactory(col -> new TableCell<CustomerRow, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty)
                    setText(null);
                else
                    setText(item.format(scheduler.App.getShortDateTimeFormatter()));
            }
        });
        lastUpdateByTableColumn.setCellValueFactory(new PropertyValueFactory<>(CustomerRow.PROP_LASTUPDATEBY));
    }

    public static void setCurrentScene(Stage sourceStage, String returnViewPath) throws InvalidArgumentException {
        scheduler.App.setScene(sourceStage, VIEW_PATH, (Stage stage, ManageCustomersController controller) -> {
            stage.setTitle(ResourceBundle.getBundle(RESOURCE_NAME, scheduler.App.getCurrentLocale()).getString("manageCustomers"));
            controller.returnViewPath = returnViewPath;
        });
    }
}
