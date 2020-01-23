package scheduler.view.user;

import java.time.LocalDateTime;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import scheduler.dao.UserImpl;
import scheduler.util.Alerts;
import scheduler.view.ListingController;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
@GlobalizationResource("view/user/ManageUsers")
@FXMLResource("/view/user/ManageUsers.fxml")
public class ManageUsers extends ListingController<UserModel> {
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
        setAsRootContent(ManageUsers.class, (scheduler.view.SchedulerController.ContentChangeContext<ManageUsers> context) -> {
            context.setWindowTitle(context.getResources().getString(RESOURCEKEY_MANAGEUSERS));
            Alerts.showErrorAlert("Not Implemented", "Need to initialize user list");
        });
    }

    @Override
    protected void onAddNewItem(Event event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void onEditItem(Event event, UserModel item) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void onDeleteItem(Event event, UserModel item) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
