package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.db.CountryRow;
import scheduler.InvalidArgumentException;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
public class EditCountryController extends ItemControllerBase<CountryRow> {
    /**
     * The name of the globalization resource bundle for this controller.
     */
    public static final String RESOURCE_NAME = "globalization/editCountry";

    /**
     * The path of the View associated with this controller.
     */
    public static final String VIEW_PATH = "/view/EditCountry.fxml";

    private String name;
    
    @FXML
    private Label nameLabel;
    
    @FXML
    private TextField nameTextField;
    
    @FXML
    private Label nameError;
    
    private final scheduler.App.StageManager stageManager;
    
    public EditCountryController(scheduler.App.StageManager stageManager) {
        this.stageManager = stageManager;
    }
    
    /**
     * Initializes the controller class.
     * @param url The URL of the associated view.
     * @param rb The resources provided by the {@link javafx.fxml.FXMLLoader}
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);
    }
    
    public static void setCurrentScene(scheduler.App.StageManager stageManager, CountryRow model) throws InvalidArgumentException {
        if (model == null)
            throw new InvalidArgumentException("model", "Model cannot be null");
        if (model.getRowState() == CountryRow.ROWSTATE_DELETED)
            throw new InvalidArgumentException("model", "Model was already deleted");
        stageManager.setSceneWithControllerFactory(VIEW_PATH, RESOURCE_NAME, (Class<?> c) -> new EditCountryController(stageManager), (ResourceBundle rb, EditCountryController controller) -> {
            if (controller.setModel(model))
                stageManager.setWindowTitle(rb.getString("editCountry"));
            else
                stageManager.setWindowTitle(rb.getString("addNewCountry"));
        });
    }

    @Override
    void saveChangesClick(ActionEvent event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    void cancelClick(ActionEvent event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
