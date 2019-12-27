package scene.address;

import scene.ItemControllerBase;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.db.AddressRow;
import model.db.CityRow;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
public class EditAddress extends ItemControllerBase<AddressRow> {
    /**
     * The name of the globalization resource bundle for this controller.
     */
    public static final String RESOURCE_NAME = "scene/address/EditAddress";

    /**
     * The path of the View associated with this controller.
     */
    public static final String VIEW_PATH = "/scene/address/EditAddress.fxml";

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
    
    private java.lang.Runnable closeWindow;
    
    private boolean dialogResult = false;
    
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
        EditAddress controller = new EditAddress();
        scheduler.util.showAndWait(controller, RESOURCE_NAME, VIEW_PATH, 640, 480, (rb, stage) -> {
            controller.closeWindow = () -> stage.hide();
            controller.setModel(new AddressRow());
            stage.setTitle(rb.getString("addNewAddress"));
        });
        return (controller.dialogResult) ? controller.getModel() : null;
    }

    public static boolean edit(AddressRow row) {
        EditAddress controller = new EditAddress();
        scheduler.util.showAndWait(controller, RESOURCE_NAME, VIEW_PATH, 640, 480, (rb, stage) -> {
            controller.closeWindow = () -> stage.hide();
            controller.setModel(row);
            stage.setTitle(rb.getString("editAddress"));
        });
        return controller.dialogResult;
    }

    @Override
    protected void saveChangesClick(ActionEvent event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void cancelClick(ActionEvent event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
