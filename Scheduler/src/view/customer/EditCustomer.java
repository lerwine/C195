package view.customer;

import view.ItemController;
import javafx.beans.binding.BooleanExpression;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import model.db.AddressRow;
import model.db.CustomerRow;
import view.annotations.FXMLResource;
import view.annotations.GlobalizationResource;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
@GlobalizationResource("view/customer/EditCustomer")
@FXMLResource("/view/customer/EditCustomer.fxml")
public class EditCustomer extends ItemController<CustomerRow> {
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
    private ComboBox<AddressRow> addressComboBox;
    
    @FXML
    private Label cityLabel;
    
    @FXML
    private Label countryLabel;
    
    public static CustomerRow addNew() {
        return showAndWait(EditCustomer.class, 640, 480, (SetContentContext<EditCustomer> context) -> {
            EditCustomer controller = context.getController();
            controller.setModel(new CustomerRow());
            context.getStage().setTitle(context.getResources().getString("addNewCustomer"));
        }, (SetContentContext<EditCustomer> context) -> {
            EditCustomer controller = context.getController();
            return (controller.isCanceled()) ? null : controller.getModel();
        });
    }

    public static boolean edit(CustomerRow row) {
        return showAndWait(EditCustomer.class, 640, 480, (SetContentContext<EditCustomer> context) -> {
            EditCustomer controller = context.getController();
            controller.setModel(row);
            context.getStage().setTitle(context.getResources().getString("editCustomer"));
        }, (SetContentContext<EditCustomer> context) -> {
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
