package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.db.CustomerRow;
import scheduler.InvalidArgumentException;
import model.annotations.ResourceKey;
import model.annotations.ResourceName;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
@ResourceName(EditCustomerController.RESOURCE_NAME)
public class EditCustomerController extends ItemControllerBase<CustomerRow> {
    /**
     * The name of the globalization resource bundle for this controller.
     */
    public static final String RESOURCE_NAME = "globalization/editCustomer";

    /**
     * The path of the View associated with this controller.
     */
    public static final String VIEW_PATH = "/view/EditCustomer.fxml";

    @FXML
    @ResourceKey("name")
    private Label nameLabel;
    
    @FXML
    private TextField nameTextField;
    
    @FXML
    @ResourceKey("nameCannotBeEmpty")
    private Label nameError;
    
    @FXML
    @ResourceKey("active")
    private Label activeLabel;
    
    @FXML
    @ResourceKey("yes")
    private RadioButton activeYesRadioButton;
    
    @FXML
    @ResourceKey("no")
    private RadioButton activeNoRadioButton;
    
    @FXML
    @ResourceKey("address")
    private Label addressLabel;
    
    @FXML
    private ComboBox addressComboBox;
    
    @FXML
    @ResourceKey("city")
    private Label cityLabel;
    
    @FXML
    @ResourceKey("country")
    private Label countryLabel;
    
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
    
    public static void setCurrentScene(Node sourceNode, CustomerRow model, String returnViewPath) throws InvalidArgumentException {
        if (model == null)
            throw new InvalidArgumentException("model", "Model cannot be null");
        if (model.getRowState() == CustomerRow.ROWSTATE_DELETED)
            throw new InvalidArgumentException("model", "Model was already deleted");
        scheduler.App.changeScene(sourceNode, VIEW_PATH, (Stage stage, EditCustomerController controller) -> {
            controller.returnViewPath = returnViewPath;
            ResourceBundle rb = ResourceBundle.getBundle(RESOURCE_NAME, scheduler.App.getCurrentLocale());
            if (controller.setModel(model)) {
                stage.setTitle(rb.getString("editCustomer"));
            } else {
                stage.setTitle(rb.getString("addNewCustomer"));
            }
        });
    }
}
