package scheduler.view.address;

import scheduler.view.EditItem;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import scheduler.dao.City;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.city.AddressCity;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
@GlobalizationResource("scheduler/view/address/EditAddress")
@FXMLResource("/scheduler/view/address/EditAddress.fxml")
public class EditAddress extends EditItem.EditController<AddressModel> {
    //<editor-fold defaultstate="collapsed" desc="Resource keys">

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Add New Address"}.
     */
    public static final String RESOURCEKEY_ADDNEWADDRESS = "addNewAddress";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Edit Address"}.
     */
    public static final String RESOURCEKEY_EDITADDRESS = "editAddress";

    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="FXMLLoader Injections">
    
    @FXML
    private TextField address1TextField;
    
    @FXML
    private TextField address2TextField;
    
    @FXML
    private Label address1Error;
    
    @FXML
    private TextField postalCodeTextField;
    
    @FXML
    private Label postalCodeError;
    
    @FXML
    private TextField phoneTextField;
    
    @FXML
    private ComboBox<AddressCity<? extends City>> cityComboBox;
    
    //</editor-fold>

    @Override
    protected void updateModelAndDao() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
