package scene.user;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import model.db.UserRow;
import scene.annotations.FXMLResource;
import scene.annotations.GlobalizationResource;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
@GlobalizationResource("scene/user/ManageUsers")
@FXMLResource("/scene/user/ManageUsers.fxml")
public class ManageUsers extends scene.ListingController {
    @FXML
    private TableView<UserRow> usersTableView;

    @FXML
    private TableColumn<UserRow, String> userNameTableColumn;

    @FXML
    private TableColumn<UserRow, Short> statusTableColumn;

    @FXML
    private TableColumn<UserRow, LocalDateTime> createDateTableColumn;

    @FXML
    private TableColumn<UserRow, String> createdByTableColumn;

    @FXML
    private TableColumn<UserRow, LocalDateTime> lastUpdateTableColumn;

    @FXML
    private TableColumn<UserRow, String> lastUpdateByTableColumn;
    
    private java.lang.Runnable closeWindow;
    
    //private String returnViewPath;

    /**
     * Initializes the controller class.
     * @param url The URL of the associated view.
     * @param rb The resources provided by the {@link javafx.fxml.FXMLLoader}
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }
    
    public static void setAsRootContent() {
        setAsRootContent(ManageUsers.class, (scene.Controller.SetContentContext<ManageUsers> context) -> {
            context.getStage().setTitle(context.getResourceBundle().getString("manageUsers"));
            scheduler.Util.showErrorAlert("Not Implemented", "Need to initialize user list");
        });
    }
}
