package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.db.CountryRow;
import scheduler.InvalidArgumentException;
import model.annotations.ResourceKey;
import model.annotations.ResourceName;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
@ResourceName(EditCountryController.RESOURCE_NAME)
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
    @ResourceKey("name")
    private Label nameLabel;
    
    @FXML
    private TextField nameTextField;
    
    @FXML
    @ResourceKey("nameCannotBeEmpty")
    private Label nameError;
    
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
    
    public static void setCurrentScene(Stage sourceStage, CountryRow model, String returnViewPath) throws InvalidArgumentException {
        if (model == null)
            throw new InvalidArgumentException("model", "Model cannot be null");
        if (model.getRowState() == CountryRow.ROWSTATE_DELETED)
            throw new InvalidArgumentException("model", "Model was already deleted");
        scheduler.App.setScene(sourceStage, VIEW_PATH, (Stage stage, EditCountryController controller) -> {
            controller.returnViewPath = returnViewPath;
            ResourceBundle rb = ResourceBundle.getBundle(RESOURCE_NAME, scheduler.App.getCurrentLocale());
            if (controller.setModel(model)) {
                stage.setTitle(rb.getString("editCountry"));
            } else {
                stage.setTitle(rb.getString("addNewCountry"));
            }
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
