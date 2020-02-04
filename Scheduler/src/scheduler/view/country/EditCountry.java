package scheduler.view.country;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import scheduler.view.EditItem;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
@GlobalizationResource("scheduler/view/country/EditCountry")
@FXMLResource("/scheduler/view/country/EditCountry.fxml")
public class EditCountry extends EditItem.EditController<CountryModel> {
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
    protected void updateModelAndDao() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
