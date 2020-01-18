package view.country;

import javafx.beans.binding.BooleanExpression;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import view.EditItem;
import scheduler.dao.CountryImpl;
import view.annotations.FXMLResource;
import view.annotations.GlobalizationResource;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
@GlobalizationResource("view/country/EditCountry")
@FXMLResource("/view/country/EditCountry.fxml")
public class EditCountry extends view.SchedulerController implements view.ItemController<CountryImpl> {
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
    
    public static CountryImpl addNew() {
        EditItem.ShowAndWaitResult<CountryImpl> result = EditItem.showAndWait(EditCountry.class, new CountryImpl(), 640, 480);
        return (result.isSuccessful()) ? result.getTarget() : null;
    }

    public static boolean edit(CountryImpl row) {
        EditItem.ShowAndWaitResult<CountryImpl> result = EditItem.showAndWait(EditCountry.class, row, 640, 480);
        return result.isSuccessful();
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
    public void accept(EditItem<CountryImpl> context) {
        context.setWindowTitle(getResources().getString((context.isNewRow().get()) ? RESOURCEKEY_ADDNEWCOUNTRY : RESOURCEKEY_EDITCOUNTRY));
    }

    @Override
    public Boolean apply(EditItem<CountryImpl> t) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
