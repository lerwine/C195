package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.db.AddressRow;
import model.db.CityRow;
import scheduler.InvalidArgumentException;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
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
    
    private final scheduler.App.StageManager stageManager;
    
    public EditAddressController(scheduler.App.StageManager stageManager) {
        this.stageManager = stageManager;
    }
    
    /**
     * Initializes the controller class.
     * @param url The URL of the associated view.
     * @param rb The resources provided by the {@link javafx.fxml.FXMLLoader}
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);
    }
    
    public static void setCurrentScene(scheduler.App.StageManager stageManager, AddressRow model) throws InvalidArgumentException {
        if (model == null)
            throw new InvalidArgumentException("model", "Model cannot be null");
        if (model.getRowState() == AddressRow.ROWSTATE_DELETED)
            throw new InvalidArgumentException("model", "Model was already deleted");
        stageManager.setSceneWithControllerFactory(VIEW_PATH, RESOURCE_NAME, (Class<?> c) -> new EditAddressController(stageManager), (ResourceBundle rb, EditAddressController controller) -> {
            if (controller.setModel(model))
                stageManager.setWindowTitle(rb.getString("editAddress"));
            else
                stageManager.setWindowTitle(rb.getString("addNewAddress"));
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
