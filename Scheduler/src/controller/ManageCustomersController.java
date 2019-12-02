package controller;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.FormatStyle;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.db.CustomerRow;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
public class ManageCustomersController implements Initializable {
    /**
     * The path of the View associated with this controller.
     */
    public static final String VIEW_PATH = "/view/ManageCustomers.fxml";

    @FXML
    private TableView<CustomerRow> customersTableView;

    @FXML
    private TableColumn<CustomerRow, Integer> customerIdTableColumn;

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

    /**
     * Initializes the controller class.
     * @param url The URL of the associated view.
     * @param rb The resources provided by the {@link javafx.fxml.FXMLLoader}
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        customerIdTableColumn.setCellValueFactory(new PropertyValueFactory<>(CustomerRow.PROP_PRIMARYKEY));
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
                    setText(item.format(scheduler.App.getDateTimeFormatter(FormatStyle.SHORT)));
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
                    setText(item.format(scheduler.App.getDateTimeFormatter(FormatStyle.SHORT)));
            }
        });
        lastUpdateByTableColumn.setCellValueFactory(new PropertyValueFactory<>(CustomerRow.PROP_LASTUPDATEBY));
    }    
    
}
