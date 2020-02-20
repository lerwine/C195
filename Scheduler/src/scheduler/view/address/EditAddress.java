package scheduler.view.address;

import javafx.beans.binding.BooleanExpression;
import scheduler.view.EditItem;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import scheduler.dao.AddressImpl;
import scheduler.dao.City;
import scheduler.dao.DataObjectImpl.Factory;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.city.CityReferenceModel;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
@GlobalizationResource("scheduler/view/address/EditAddress")
@FXMLResource("/scheduler/view/address/EditAddress.fxml")
public final class EditAddress extends EditItem.EditController<AddressImpl, AddressModel> {
    
    //<editor-fold defaultstate="collapsed" desc="Resource bundle keys">
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Add New Address"}.
     */
    public static final String RESOURCEKEY_ADDNEWADDRESS = "addNewAddress";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Address:"}.
     */
    public static final String RESOURCEKEY_ADDRESS = "address";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Address cannot be empty."}.
     */
    public static final String RESOURCEKEY_ADDRESSCANNOTBEEMPTY = "addressCannotBeEmpty";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "City:"}.
     */
    public static final String RESOURCEKEY_CITY = "city";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Country:"}.
     */
    public static final String RESOURCEKEY_COUNTRY = "country";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Edit Address:"}.
     */
    public static final String RESOURCEKEY_EDITADDRESS = "editAddress";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Phone Number:"}.
     */
    public static final String RESOURCEKEY_PHONENUMBER = "phoneNumber";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Postal Code:"}.
     */
    public static final String RESOURCEKEY_POSTALCODE = "postalCode";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Postal Code cannot be empty."}.
     */
    public static final String RESOURCEKEY_POSTALCODECANNOTBEEMPTY = "postalCodeCannotBeEmpty";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "That address is referenced by one or more customers and cannot be deleted."}.
     */
    public static final String RESOURCEKEY_ADDRESSHASCUSTOMERS = "addressHasCustomers";

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
    private ComboBox<CityReferenceModel<? extends City>> cityComboBox;
    
    //</editor-fold>

    @FXML // This method is called by the FXMLLoader when initialization is complete
    protected void initialize() {
        
    }
    
    @Override
    protected BooleanExpression getValidationExpression() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected Factory<AddressImpl, AddressModel> getDaoFactory() { return AddressImpl.getFactory(); }

}
