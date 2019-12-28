package scene.user;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import model.db.UserRow;
import scheduler.InvalidArgumentException;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
public class ManageUsers implements Initializable {
    /**
     * The name of the globalization resource bundle for this controller.
     */
    public static final String GLOBALIZATION_RESOURCE_NAME = "scene/user/ManageUsers";

    /**
     * The path of the View associated with this controller.
     */
    public static final String FXML_RESOURCE_NAME = "/scene/user/ManageUsers.fxml";

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
    
    public static void setAsRootStageScene() {
        scheduler.App.getCurrent().changeRootStageScene(GLOBALIZATION_RESOURCE_NAME, FXML_RESOURCE_NAME, (ResourceBundle rb, Stage stage) -> {
            stage.setTitle(rb.getString("manageUsers"));
        });
    }
}
