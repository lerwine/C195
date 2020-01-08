package view.customer;

import java.time.LocalDateTime;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import model.db.CustomerRow;
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
public class ManageCustomers extends ListingController<CustomerRow> {
    //<editor-fold defaultstate="collapsed" desc="Resource keys">

//    public static final String RESOURCEKEY_ACTIVE = "active";
//    public static final String RESOURCEKEY_ADDRESS = "address";
//    public static final String RESOURCEKEY_CREATEDBY = "createdBy";
//    public static final String RESOURCEKEY_CREATEDON = "createdOn";
//    public static final String RESOURCEKEY_CUSTOMERNAME = "customerName";
    public static final String RESOURCEKEY_MANAGECUSTOMERS = "manageCustomers";
//    public static final String RESOURCEKEY_UPDATEDBY = "updatedBy";
//    public static final String RESOURCEKEY_UPDATEDON = "updatedOn";

    //</editor-fold>
    
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
    protected void onEditItem(CustomerRow item) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void onDeleteItem(CustomerRow item) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
