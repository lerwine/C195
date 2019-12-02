package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.db.AddressRow;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
public class EditAddressController extends ItemControllerBase<AddressRow> {
    /**
     * The path of the View associated with this controller.
     */
    public static final String VIEW_PATH = "/view/EditAddress.fxml";

    @FXML
    private Label address1Label;
    
    @FXML
    private TextField address1TextField;
    
    @FXML
    private Label address1Error;
    
    @FXML
    private Label address2Label;
    
    @FXML
    private TextField address2TextField;
    
    @FXML
    private Label postalCodeLabel;
    
    @FXML
    private TextField postalCodeTextField;
    
    @FXML
    private Label postalCodeError;
    
    @FXML
    private Label phoneLabel;
    
    @FXML
    private TextField phoneTextField;
    
    @FXML
    private ComboBox cityComboBox;
    
    /**
     * Initializes the controller class.
     * @param url The URL of the associated view.
     * @param rb The resources provided by the {@link javafx.fxml.FXMLLoader}
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);
    }    

    @Override
    protected void applyModelAsNew(AddressRow model) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void applyModelAsEdit(AddressRow model) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
