package view.user;

import java.time.LocalDateTime;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import scheduler.dao.UserImpl;
import util.Alerts;
import view.ListingController;
import view.annotations.FXMLResource;
import view.annotations.GlobalizationResource;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
@GlobalizationResource("view/user/ManageUsers")
@FXMLResource("/view/user/ManageUsers.fxml")
public class ManageUsers extends ListingController<UserImpl> {
    //<editor-fold defaultstate="collapsed" desc="Resource keys">

    public static final String RESOURCEKEY_MANAGEUSERS = "manageUsers";
//    public static final String RESOURCEKEY_CREATEDBY = "createdBy";
//    public static final String RESOURCEKEY_CREATEDON = "createdOn";
//    public static final String RESOURCEKEY_UPDATEDBY = "updatedBy";
//    public static final String RESOURCEKEY_UPDATEDON = "updatedOn";

    //</editor-fold>
    
    @FXML
    private TableView<UserImpl> usersTableView;

    @FXML
    private TableColumn<UserImpl, String> userNameTableColumn;

    @FXML
    private TableColumn<UserImpl, Short> statusTableColumn;

    @FXML
    private TableColumn<UserImpl, LocalDateTime> createDateTableColumn;

    @FXML
    private TableColumn<UserImpl, String> createdByTableColumn;

    @FXML
    private TableColumn<UserImpl, LocalDateTime> lastUpdateTableColumn;

    @FXML
    private TableColumn<UserImpl, String> lastUpdateByTableColumn;
    
    //private String returnViewPath;

    public static void setAsRootContent() {
        setAsRootContent(ManageUsers.class, (view.SchedulerController.ContentChangeContext<ManageUsers> context) -> {
            context.setWindowTitle(context.getResources().getString(RESOURCEKEY_MANAGEUSERS));
            Alerts.showErrorAlert("Not Implemented", "Need to initialize user list");
        });
    }

    @Override
    protected void onAddNewItem() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void onEditItem(UserImpl item) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void onDeleteItem(UserImpl item) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
