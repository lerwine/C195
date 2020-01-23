package scheduler.view.customer;

import javafx.beans.binding.BooleanExpression;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import scheduler.dao.AddressImpl;
import scheduler.view.EditItem;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.dao.CustomerImpl;
import scheduler.view.address.AddressModel;
/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
@GlobalizationResource("view/customer/EditCustomer")
@FXMLResource("/view/customer/EditCustomer.fxml")
public class EditCustomer extends scheduler.view.SchedulerController implements scheduler.view.ItemController<CustomerModel> {
    //<editor-fold defaultstate="collapsed" desc="Resource keys">

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Add New Customer"}.
     */
    public static final String RESOURCEKEY_ADDNEWCUSTOMER = "addNewCustomer";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "EditS Customer"}.
     */
    public static final String RESOURCEKEY_EDITCUSTOMER = "editCustomer";

    //</editor-fold>
    
    @FXML
    private Label nameLabel;
    
    @FXML
    private TextField nameTextField;
    
    @FXML
    private Label nameError;
    
    @FXML
    private Label activeLabel;
    
    @FXML
    private RadioButton activeYesRadioButton;
    
    @FXML
    private RadioButton activeNoRadioButton;
    
    @FXML
    private Label addressLabel;
    
    @FXML
    private ComboBox<AddressModel> addressComboBox;
    
    @FXML
    private Label cityLabel;
    
    @FXML
    private Label countryLabel;
    
    public static CustomerModel addNew() {
        EditItem.ShowAndWaitResult<CustomerModel> result = EditItem.showAndWait(EditCustomer.class, new CustomerModel(new CustomerImpl()), 640, 480);
        return (result.isSuccessful()) ? result.getTarget() : null;
    }

    public static boolean edit(CustomerModel row) {
        EditItem.ShowAndWaitResult<CustomerModel> result = EditItem.showAndWait(EditCustomer.class, row, 640, 480);
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
    public void accept(EditItem<CustomerModel> context) {
        context.setWindowTitle(getResources().getString((context.isNewRow().get()) ? RESOURCEKEY_ADDNEWCUSTOMER : RESOURCEKEY_EDITCUSTOMER));
    }

    @Override
    public Boolean apply(EditItem<CustomerModel> t) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
