package scheduler.view.country;

import javafx.beans.binding.BooleanExpression;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import scheduler.view.EditItem;
import scheduler.dao.CountryImpl;
import scheduler.view.ItemController;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
@GlobalizationResource("scheduler/view/country/EditCountry")
@FXMLResource("/scheduler/view/country/EditCountry.fxml")
public class EditCountry extends ItemController<CountryModel> {
    //<editor-fold defaultstate="collapsed" desc="Resource bundle keys">

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Add New Country"}.
     */
    public static final String RESOURCEKEY_ADDNEWCOUNTRY = "addNewCountry";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Edit Country"}.
     */
    public static final String RESOURCEKEY_EDITCOUNTRY = "editCountry";

    //</editor-fold>
    
    @FXML
    private Label nameLabel;
    
    @FXML
    private TextField nameTextField;
    
    @FXML
    private Label nameError;
    
    @Override
    public boolean isValid() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public BooleanExpression validProperty() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void accept(EditItem<CountryModel> context) {
//        context.getChildViewManager().setWindowTitle(getResources().getString((context.getTarget().isNewItem()) ? RESOURCEKEY_ADDNEWCOUNTRY : RESOURCEKEY_EDITCOUNTRY));
    }

    @Override
    public Boolean apply(EditItem<CountryModel> t) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
