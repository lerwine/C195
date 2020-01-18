package view.customer;

import java.time.LocalDateTime;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import scheduler.dao.CustomerImpl;
import util.Alerts;
import view.ListingController;
import view.annotations.FXMLResource;
import view.annotations.GlobalizationResource;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
@GlobalizationResource("view/customer/ManageCustomers")
@FXMLResource("/view/customer/ManageCustomers.fxml")
public class ManageCustomers extends ListingController<CustomerImpl> {
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Manage Customers"}.
     */
    public static final String RESOURCEKEY_MANAGECUSTOMERS = "manageCustomers";

    @FXML
    private TableColumn<CustomerImpl, String> customerNameTableColumn;

    @FXML
    private TableColumn<CustomerImpl, Integer> addressTableColumn;

    @FXML
    private TableColumn<CustomerImpl, Boolean> activeTableColumn;

    @FXML
    private TableColumn<CustomerImpl, LocalDateTime> createDateTableColumn;

    @FXML
    private TableColumn<CustomerImpl, String> createdByTableColumn;

    @FXML
    private TableColumn<CustomerImpl, LocalDateTime> lastUpdateTableColumn;

    @FXML
    private TableColumn<CustomerImpl, String> lastUpdateByTableColumn;
    
    public static void setAsRootContent() {
        setAsRootContent(ManageCustomers.class, (view.SchedulerController.ContentChangeContext<ManageCustomers> context) -> {
            context.setWindowTitle(context.getResources().getString(RESOURCEKEY_MANAGECUSTOMERS));
            Alerts.showErrorAlert("Not Implemented", "Need to initialize appointments list");
        });
    }

    @Override
    protected void onAddNewItem() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void onEditItem(CustomerImpl item) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void onDeleteItem(CustomerImpl item) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
