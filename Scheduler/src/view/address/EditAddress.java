package view.address;

import view.SchedulerController;
import view.EditItem;
import javafx.beans.binding.BooleanExpression;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import scheduler.dao.AddressImpl;
import scheduler.dao.City;
import view.annotations.FXMLResource;
import view.annotations.GlobalizationResource;
import view.city.AddressCity;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
@GlobalizationResource("view/address/EditAddress")
@FXMLResource("/view/address/EditAddress.fxml")
public class EditAddress extends SchedulerController implements view.ItemController<AddressImpl> {
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
    
    public static AddressImpl addNew() {
        EditItem.ShowAndWaitResult<AddressImpl> result = EditItem.showAndWait(EditAddress.class, new AddressImpl(), 640, 480);
        return (result.isSuccessful()) ? result.getTarget() : null;
    }

    public static boolean edit(AddressImpl row) {
        EditItem.ShowAndWaitResult<AddressImpl> result = EditItem.showAndWait(EditAddress.class, row, 640, 480);
        return result.isSuccessful();
    }

    @Override
    public void accept(EditItem<AddressImpl> context) {
        context.setWindowTitle(getResources().getString((context.isNewRow().get()) ? RESOURCEKEY_ADDNEWADDRESS : RESOURCEKEY_EDITADDRESS));
    }

    @Override
    public Boolean apply(EditItem<AddressImpl> context) {
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
