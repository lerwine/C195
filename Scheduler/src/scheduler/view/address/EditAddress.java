package scheduler.view.address;

import scheduler.view.SchedulerController;
import scheduler.view.EditItem;
import javafx.beans.binding.BooleanExpression;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import scheduler.dao.AddressImpl;
import scheduler.dao.City;
import scheduler.view.ItemController;
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
public class EditAddress extends ItemController<AddressModel> {
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
    public void accept(EditItem<AddressModel> context) {
//        context.getChildViewManager().setWindowTitle(getResources().getString((context.getTarget().isNewItem()) ? RESOURCEKEY_ADDNEWADDRESS : RESOURCEKEY_EDITADDRESS));
    }

    @Override
    public Boolean apply(EditItem<AddressModel> context) {
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
