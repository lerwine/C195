package scene.country;

import scene.ItemControllerBase;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.db.CountryRow;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
public class EditCountry extends ItemControllerBase<CountryRow> {
    /**
     * The name of the globalization resource bundle for this controller.
     */
    public static final String RESOURCE_NAME = "scene/country/EditCountry";

    /**
     * The path of the View associated with this controller.
     */
    public static final String VIEW_PATH = "/scene/country/EditCountry.fxml";

    private String name;
    
    @FXML
    private Label nameLabel;
    
    @FXML
    private TextField nameTextField;
    
    @FXML
    private Label nameError;
    
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
    
    public static CountryRow addNew() {
        EditCountry controller = new EditCountry();
        scheduler.util.showAndWait(controller, RESOURCE_NAME, VIEW_PATH, 640, 480, (rb, stage) -> {
            controller.closeWindow = () -> stage.hide();
            controller.setModel(new CountryRow());
            stage.setTitle(rb.getString("addNewCountry"));
        });
        return (controller.dialogResult) ? controller.getModel() : null;
    }

    public static boolean edit(CountryRow row) {
        EditCountry controller = new EditCountry();
        scheduler.util.showAndWait(controller, RESOURCE_NAME, VIEW_PATH, 640, 480, (rb, stage) -> {
            controller.closeWindow = () -> stage.hide();
            controller.setModel(row);
            stage.setTitle(rb.getString("editCountry"));
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
