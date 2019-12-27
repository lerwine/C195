package scene.city;

import scene.ItemControllerBase;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.db.CityRow;
import model.db.CountryRow;
import scheduler.InvalidArgumentException;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
public class EditCity extends ItemControllerBase<CityRow> {
    /**
     * The name of the globalization resource bundle for this controller.
     */
    public static final String RESOURCE_NAME = "scene/city/EditCity";

    /**
     * The path of the View associated with this controller.
     */
    public static final String VIEW_PATH = "/scene/city/EditCity.fxml";

    private int countryId;
    
    @FXML
    private Label nameLabel;
    
    @FXML
    private TextField nameTextField;
    
    @FXML
    private Label nameError;
    
    @FXML
    private Label countryLabel;
    
    @FXML
    private ComboBox<CountryRow> countryComboBox;
    
    private java.lang.Runnable closeWindow;
    
    private boolean dialogResult = false;
    
    /**
     * Initializes the controller class.
     * @param url The URL of the associated view.
     * @param rb The resources provided by the {@link javafx.fxml.FXMLLoader}
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);
    }
    
    public static CityRow addNew() {
        EditCity controller = new EditCity();
        scheduler.util.showAndWait(controller, RESOURCE_NAME, VIEW_PATH, 640, 480, (rb, stage) -> {
            controller.closeWindow = () -> stage.hide();
            controller.setModel(new CityRow());
            stage.setTitle(rb.getString("addNewCity"));
        });
        return (controller.dialogResult) ? controller.getModel() : null;
    }

    public static boolean edit(CityRow row) {
        EditCity controller = new EditCity();
        scheduler.util.showAndWait(controller, RESOURCE_NAME, VIEW_PATH, 640, 480, (rb, stage) -> {
            controller.closeWindow = () -> stage.hide();
            controller.setModel(row);
            stage.setTitle(rb.getString("editCity"));
        });
        return controller.dialogResult;
    }

    @Override
    protected void saveChangesClick(ActionEvent event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void cancelClick(ActionEvent event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
