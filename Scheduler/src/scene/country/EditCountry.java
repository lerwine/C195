package scene.country;

import scene.ItemController;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.BooleanExpression;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.db.CountryRow;
import scene.annotations.FXMLResource;
import scene.annotations.GlobalizationResource;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
@GlobalizationResource("scene/country/EditCountry")
@FXMLResource("/scene/country/EditCountry.fxml")
public class EditCountry extends ItemController<CountryRow> {
    private String name;
    
    @FXML
    private Label nameLabel;
    
    @FXML
    private TextField nameTextField;
    
    @FXML
    private Label nameError;
    
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
        return showAndWait(EditCountry.class, 640, 480, (SetContentContext<EditCountry> context) -> {
            EditCountry controller = context.getController();
            controller.setModel(new CountryRow());
            context.getStage().setTitle(context.getResourceBundle().getString("addNewCountry"));
        }, (SetContentContext<EditCountry> context) -> {
            EditCountry controller = context.getController();
            return (controller.isCanceled()) ? null : controller.getModel();
        });
    }

    public static boolean edit(CountryRow row) {
        return showAndWait(EditCountry.class, 640, 480, (SetContentContext<EditCountry> context) -> {
            EditCountry controller = context.getController();
            controller.setModel(row);
            context.getStage().setTitle(context.getResourceBundle().getString("editCountry"));
        }, (SetContentContext<EditCountry> context) -> {
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
