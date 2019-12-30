package view.city;

import view.ItemController;
import javafx.beans.binding.BooleanExpression;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.db.CityRow;
import model.db.CountryRow;
import view.annotations.FXMLResource;
import view.annotations.GlobalizationResource;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
@GlobalizationResource("view/city/EditCity")
@FXMLResource("/view/city/EditCity.fxml")
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
    
    public static CityRow addNew() {
        return showAndWait(EditCity.class, 640, 480, (SetContentContext<EditCity> context) -> {
            EditCity controller = context.getController();
            controller.setModel(new CityRow());
            context.getStage().setTitle(context.getResources().getString("addNewCity"));
        }, (SetContentContext<EditCity> context) -> {
            EditCity controller = context.getController();
            return (controller.isCanceled()) ? null : controller.getModel();
        });
    }

    public static boolean edit(CityRow row) {
        return showAndWait(EditCity.class, 640, 480, (SetContentContext<EditCity> context) -> {
            EditCity controller = context.getController();
            controller.setModel(row);
            context.getStage().setTitle(context.getResources().getString("editCity"));
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
