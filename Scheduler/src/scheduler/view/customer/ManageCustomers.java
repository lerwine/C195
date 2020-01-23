package scheduler.view.customer;

import java.time.LocalDateTime;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import scheduler.dao.CustomerImpl;
import scheduler.util.Alerts;
import scheduler.view.ListingController;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
@GlobalizationResource("view/customer/ManageCustomers")
@FXMLResource("/view/customer/ManageCustomers.fxml")
public class ManageCustomers extends ListingController<CustomerModel> {
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Manage Customers"}.
     */
    public static final String RESOURCEKEY_MANAGECUSTOMERS = "manageCustomers";

    @FXML
    private TableColumn<CustomerModel, String> customerNameTableColumn;

    @FXML
    private TableColumn<CustomerModel, Integer> addressTableColumn;

    @FXML
    private TableColumn<CustomerModel, Boolean> activeTableColumn;

    @FXML
    private TableColumn<CustomerModel, LocalDateTime> createDateTableColumn;

    @FXML
    private TableColumn<CustomerModel, String> createdByTableColumn;

    @FXML
    private TableColumn<CustomerModel, LocalDateTime> lastUpdateTableColumn;

    @FXML
    private TableColumn<CustomerModel, String> lastUpdateByTableColumn;
    
    public static void setAsRootContent() {
        setAsRootContent(ManageCustomers.class, (scheduler.view.SchedulerController.ContentChangeContext<ManageCustomers> context) -> {
            context.setWindowTitle(context.getResources().getString(RESOURCEKEY_MANAGECUSTOMERS));
            Alerts.showErrorAlert("Not Implemented", "Need to initialize appointments list");
        });
    }

    @Override
    protected void onAddNewItem(Event event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void onEditItem(Event event, CustomerModel item) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void onDeleteItem(Event event, CustomerModel item) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
