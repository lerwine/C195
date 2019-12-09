package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.Node;
import javafx.stage.Stage;
import model.annotations.ResourceName;
import scheduler.InvalidArgumentException;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
@ResourceName(ManageUsersController.RESOURCE_NAME)
public class ManageUsersController extends ControllerBase {
    /**
     * The name of the globalization resource bundle for this controller.
     */
    public static final String RESOURCE_NAME = "globalization/manageUsers";

    /**
     * The path of the View associated with this controller.
     */
    public static final String VIEW_PATH = "/view/ManageUsers.fxml";
    
    private String returnViewPath;

    /**
     * Initializes the controller class.
     * @param url The URL of the associated view.
     * @param rb The resources provided by the {@link javafx.fxml.FXMLLoader}
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);
    }

    public static void setCurrentScene(Node sourceNode, String returnViewPath) throws InvalidArgumentException {
        scheduler.App.changeScene(sourceNode, VIEW_PATH, (Stage stage, ManageUsersController controller) -> {
            stage.setTitle(ResourceBundle.getBundle(RESOURCE_NAME, scheduler.App.getCurrentLocale()).getString("manageUsers"));
            controller.returnViewPath = returnViewPath;
        });
    }
}
