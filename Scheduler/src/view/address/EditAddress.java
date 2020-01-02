package view.address;

import view.Controller;
import view.EditItem;
import javafx.beans.binding.BooleanExpression;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.db.AddressRow;
import model.db.CityRow;
import view.annotations.FXMLResource;
import view.annotations.GlobalizationResource;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
@GlobalizationResource("view/address/EditAddress")
@FXMLResource("/view/address/EditAddress.fxml")
public class EditAddress extends Controller implements view.ItemController<AddressRow> {
    //<editor-fold defaultstate="collapsed" desc="Resource keys">

    public static final String RESOURCEKEY_ADDNEWADDRESS = "addNewAddress";
    public static final String RESOURCEKEY_EDITADDRESS = "editAddress";
//    public static final String RESOURCEKEY_ADDRESS = "address";
//    public static final String RESOURCEKEY_ADDRESSCANNOTBEEMPTY = "addressCannotBeEmpty";
//    public static final String RESOURCEKEY_CITY = "city";
//    public static final String RESOURCEKEY_COUNTRY = "country";
//    public static final String RESOURCEKEY_PHONENUMBER = "phoneNumber";
//    public static final String RESOURCEKEY_POSTALCODE = "postalCode";
//    public static final String RESOURCEKEY_POSTALCODECANNOTBEEMPTY = "postalCodeCannotBeEmpty";
//    public static final String RESOURCEKEY_BY = "by";
//    public static final String RESOURCEKEY_CANCEL = "cancel";
//    public static final String RESOURCEKEY_CREATED = "created";
//    public static final String RESOURCEKEY_SAVE = "save";
//    public static final String RESOURCEKEY_UPDATED = "updated";

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
    private ComboBox<CityRow> cityComboBox;
    
    //</editor-fold>
    
    public static AddressRow addNew() {
        EditItem.ShowAndWaitResult<AddressRow> result = EditItem.showAndWait(EditAddress.class, new AddressRow(), 640, 480);
        return (result.isSuccessful()) ? result.getTarget() : null;
    }

    public static boolean edit(AddressRow row) {
        EditItem.ShowAndWaitResult<AddressRow> result = EditItem.showAndWait(EditAddress.class, row, 640, 480);
        return result.isSuccessful();
    }

    @Override
    public void accept(EditItem<AddressRow> context) {
        context.setWindowTitle(getResources().getString((context.isNewRow().get()) ? RESOURCEKEY_ADDNEWADDRESS : RESOURCEKEY_EDITADDRESS));
    }

    @Override
    public Boolean apply(EditItem<AddressRow> context) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isValid() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public BooleanExpression validProperty() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
