package scheduler.view.city;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import scheduler.dao.CountryImpl;
import scheduler.view.EditItem;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
@GlobalizationResource("scheduler/view/city/EditCity")
@FXMLResource("/scheduler/view/city/EditCity.fxml")
public class EditCity extends EditItem.EditController<CityModel> {
    //<editor-fold defaultstate="collapsed" desc="Resource bundle keys">

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Add New City"}.
     */
    public static final String RESOURCEKEY_ADDNEWCITY = "addNewCity";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Edit City"}.
     */
    public static final String RESOURCEKEY_EDITCITY = "editCity";

    //</editor-fold>
    
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
    private ComboBox<CountryImpl> countryComboBox;

    @Override
    protected void updateModelAndDao() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
