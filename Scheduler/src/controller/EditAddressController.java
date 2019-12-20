package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.db.AddressRow;
import model.db.CityRow;
import scheduler.InvalidArgumentException;
import model.annotations.ResourceKey;
import model.annotations.ResourceName;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
@ResourceName(EditAddressController.RESOURCE_NAME)
public class EditAddressController extends ItemControllerBase<AddressRow> {
    /**
     * The name of the globalization resource bundle for this controller.
     */
    public static final String RESOURCE_NAME = "globalization/editAddress";

    /**
     * The path of the View associated with this controller.
     */
    public static final String VIEW_PATH = "/view/EditAddress.fxml";

    @FXML
    @ResourceKey("address")
    private Label addressLabel;
    
    @FXML
    private TextField address1TextField;
    
    @FXML
    private TextField address2TextField;
    
    @FXML
    @ResourceKey("addressCannotBeEmpty")
    private Label address1Error;
    
    @FXML
    @ResourceKey("postalCode")
    private Label postalCodeLabel;
    
    @FXML
    private TextField postalCodeTextField;
    
    @FXML
    @ResourceKey("postalCodeCannotBeEmpty")
    private Label postalCodeError;
    
    @FXML
    @ResourceKey("phoneNumber")
    private Label phoneLabel;
    
    @FXML
    private TextField phoneTextField;
    
    @FXML
    @ResourceKey("city")
    private ComboBox<CityRow> cityComboBox;
    
    private String returnViewPath;
    
    /**
     * Initializes the controller class.
     * @param url The URL of the associated view.
     * @param rb The resources provided by the {@link javafx.fxml.FXMLLoader}
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);
    }
    
    public static void setCurrentScene(Node sourceNode, AddressRow model, String returnViewPath) throws InvalidArgumentException {
        if (model == null)
            throw new InvalidArgumentException("model", "Model cannot be null");
        if (model.getRowState() == AddressRow.ROWSTATE_DELETED)
            throw new InvalidArgumentException("model", "Model was already deleted");
        scheduler.App.changeScene(sourceNode, VIEW_PATH, (Stage stage, EditAddressController controller) -> {
            controller.returnViewPath = returnViewPath;
            ResourceBundle rb = ResourceBundle.getBundle(RESOURCE_NAME, scheduler.App.getCurrentLocale());
            if (controller.setModel(model)) {
                stage.setTitle(rb.getString("editAddress"));
            } else {
                stage.setTitle(rb.getString("addNewAddress"));
            }
        });
    }

    @Override
    void saveChangesClick(ActionEvent event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    void cancelClick(ActionEvent event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
