package view.address;

import view.EditItemController;
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
public class EditAddress extends EditItemController<AddressRow> {
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
        return showAndWait(EditAddress.class, 640, 480, (ContentChangeContext<EditAddress> context) -> {
            EditAddress controller = context.getController();
            controller.setModel(new AddressRow());
            context.setWindowTitle(context.getResources().getString(RESOURCEKEY_ADDNEWADDRESS));
        }, (ContentChangeContext<EditAddress> context) -> {
            EditAddress controller = context.getController();
            return (controller.isCanceled()) ? null : controller.getModel();
        });
    }

    public static boolean edit(AddressRow row) {
        return showAndWait(EditAddress.class, 640, 480, (ContentChangeContext<EditAddress> context) -> {
            EditAddress controller = context.getController();
            controller.setModel(row);
            context.setWindowTitle(context.getResources().getString(RESOURCEKEY_EDITADDRESS));
        }, (ContentChangeContext<EditAddress> context) -> {
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
