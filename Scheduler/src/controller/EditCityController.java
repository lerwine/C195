package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.db.CityRow;
import model.db.CountryRow;
import scheduler.InvalidArgumentException;
import model.annotations.ResourceKey;
import model.annotations.ResourceName;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
@ResourceName(EditCityController.RESOURCE_NAME)
public class EditCityController extends ItemControllerBase<CityRow> {
    /**
     * The name of the globalization resource bundle for this controller.
     */
    public static final String RESOURCE_NAME = "globalization/editCity";

    /**
     * The path of the View associated with this controller.
     */
    public static final String VIEW_PATH = "/view/EditCity.fxml";

    private int countryId;
    
    @FXML
    @ResourceKey("name")
    private Label nameLabel;
    
    @FXML
    private TextField nameTextField;
    
    @FXML
    @ResourceKey("nameCannotBeEmpty")
    private Label nameError;
    
    @FXML
    @ResourceKey("country")
    private Label countryLabel;
    
    @FXML
    private ComboBox<CountryRow> countryComboBox;
    
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
    
    public static void setCurrentScene(Stage sourceStage, CityRow model, String returnViewPath) throws InvalidArgumentException {
        if (model == null)
            throw new InvalidArgumentException("model", "Model cannot be null");
        if (model.getRowState() == CityRow.ROWSTATE_DELETED)
            throw new InvalidArgumentException("model", "Model was already deleted");
        scheduler.App.setScene(sourceStage, VIEW_PATH, (Stage stage, EditCityController controller) -> {
            controller.returnViewPath = returnViewPath;
            ResourceBundle rb = ResourceBundle.getBundle(RESOURCE_NAME, scheduler.App.getCurrentLocale());
            if (controller.setModel(model)) {
                stage.setTitle(rb.getString("editCity"));
            } else {
                stage.setTitle(rb.getString("addNewCity"));
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
