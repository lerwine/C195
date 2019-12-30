package view.customer;

import java.time.LocalDateTime;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import model.db.CustomerRow;
import model.db.DataRow;
import view.annotations.FXMLResource;
import view.annotations.GlobalizationResource;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
@GlobalizationResource("view/customer/ManageCustomers")
@FXMLResource("/view/customer/ManageCustomers.fxml")
public class ManageCustomers extends view.ListingController {
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
        setAsRootContent(ManageCustomers.class, (view.Controller.SetContentContext<ManageCustomers> context) -> {
            context.getStage().setTitle(context.getResources().getString("manageAppointments"));
            scheduler.Util.showErrorAlert("Not Implemented", "Need to initialize appointments list");
        });
    }

    @Override
    protected void onAddNewItem() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void onEditItem(DataRow item) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void onDeleteItem(DataRow item) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
