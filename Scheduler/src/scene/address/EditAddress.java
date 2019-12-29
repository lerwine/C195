package scene.address;

import scene.ItemController;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.BooleanExpression;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.db.AddressRow;
import model.db.CityRow;
import scene.annotations.FXMLResource;
import scene.annotations.GlobalizationResource;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
@GlobalizationResource("scene/address/EditAddress")
@FXMLResource("/scene/address/EditAddress.fxml")
public class EditAddress extends ItemController<AddressRow> {
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
    
    /**
     * Initializes the controller class.
     * @param url The URL of the associated view.
     * @param rb The resources provided by the {@link javafx.fxml.FXMLLoader}
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);
    }
    
    public static AddressRow addNew() {
        return showAndWait(EditAddress.class, 640, 480, (SetContentContext<EditAddress> context) -> {
            EditAddress controller = context.getController();
            controller.setModel(new AddressRow());
            context.getStage().setTitle(context.getResourceBundle().getString("addNewAddress"));
        }, (SetContentContext<EditAddress> context) -> {
            EditAddress controller = context.getController();
            return (controller.isCanceled()) ? null : controller.getModel();
        });
    }

    public static boolean edit(AddressRow row) {
        return showAndWait(EditAddress.class, 640, 480, (SetContentContext<EditAddress> context) -> {
            EditAddress controller = context.getController();
            controller.setModel(row);
            context.getStage().setTitle(context.getResourceBundle().getString("editAddress"));
        }, (SetContentContext<EditAddress> context) -> {
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
