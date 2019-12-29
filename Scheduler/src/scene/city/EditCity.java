package scene.city;

import scene.ItemController;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.BooleanExpression;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.db.CityRow;
import model.db.CountryRow;
import scene.annotations.FXMLResource;
import scene.annotations.GlobalizationResource;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
@GlobalizationResource("scene/city/EditCity")
@FXMLResource("/scene/city/EditCity.fxml")
public class EditCity extends ItemController<CityRow> {
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
        return showAndWait(EditCity.class, 640, 480, (SetContentContext<EditCity> context) -> {
            EditCity controller = context.getController();
            controller.setModel(new CityRow());
            context.getStage().setTitle(context.getResourceBundle().getString("addNewCity"));
        }, (SetContentContext<EditCity> context) -> {
            EditCity controller = context.getController();
            return (controller.isCanceled()) ? null : controller.getModel();
        });
    }

    public static boolean edit(CityRow row) {
        return showAndWait(EditCity.class, 640, 480, (SetContentContext<EditCity> context) -> {
            EditCity controller = context.getController();
            controller.setModel(row);
            context.getStage().setTitle(context.getResourceBundle().getString("editCity"));
        }, (SetContentContext<EditCity> context) -> {
            return !context.getController().isCanceled();
        });
    }

    @Override
    public boolean isValid() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public BooleanExpression validProperty() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected boolean saveChanges() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
