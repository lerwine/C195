package view.country;

import view.ItemController;
import javafx.beans.binding.BooleanExpression;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.db.CountryRow;
import view.annotations.FXMLResource;
import view.annotations.GlobalizationResource;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
@GlobalizationResource("view/country/EditCountry")
@FXMLResource("/view/country/EditCountry.fxml")
public class EditCountry extends ItemController<CountryRow> {
    private String name;
    
    @FXML
    private Label nameLabel;
    
    @FXML
    private TextField nameTextField;
    
    @FXML
    private Label nameError;
    
    public static CountryRow addNew() {
        return showAndWait(EditCountry.class, 640, 480, (SetContentContext<EditCountry> context) -> {
            EditCountry controller = context.getController();
            controller.setModel(new CountryRow());
            context.getStage().setTitle(context.getResources().getString("addNewCountry"));
        }, (SetContentContext<EditCountry> context) -> {
            EditCountry controller = context.getController();
            return (controller.isCanceled()) ? null : controller.getModel();
        });
    }

    public static boolean edit(CountryRow row) {
        return showAndWait(EditCountry.class, 640, 480, (SetContentContext<EditCountry> context) -> {
            EditCountry controller = context.getController();
            controller.setModel(row);
            context.getStage().setTitle(context.getResources().getString("editCountry"));
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
