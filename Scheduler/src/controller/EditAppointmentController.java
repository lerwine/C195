package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.db.AppointmentRow;
import scheduler.InvalidArgumentException;
import model.annotations.ResourceKey;
import model.annotations.ResourceName;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
@ResourceName(EditAppointmentController.RESOURCE_NAME)
public class EditAppointmentController extends ItemControllerBase<AppointmentRow> {
    /**
     * The name of the globalization resource bundle for this controller.
     */
    public static final String RESOURCE_NAME = "globalization/editAppointment";

    /**
     * The path of the View associated with this controller.
     */
    public static final String VIEW_PATH = "/view/EditAppointment.fxml";

    @FXML
    @ResourceKey("customer")
    private Label customerLabel;
    
    @FXML
    private ComboBox customerComboBox;
    
    @FXML
    @ResourceKey("user")
    private Label userLabel;
    
    @FXML
    private ComboBox userComboBox;
    
    @FXML
    @ResourceKey("title")
    private Label titleLabel;
    
    @FXML
    private TextField titleTextField;
    
    @FXML
    @ResourceKey("titleCannotBeEmpty")
    private Label titleErrorLabel;
    
    @FXML
    @ResourceKey("description")
    private Label descriptionLabel;
    
    @FXML
    private TextField descriptionTextField;
    
    @FXML
    @ResourceKey("location")
    private Label locationLabel;
    
    @FXML
    private TextField locationTextField;
    
    @FXML
    @ResourceKey("locationCannotBeEmpty")
    private Label locationErrorLabel;
    
    @FXML
    @ResourceKey("pointOfContact")
    private Label contactLabel;
    
    @FXML
    private TextField contactTextField;
    
    @FXML
    @ResourceKey("pointOfContactCannotBeEmpty")
    private Label contactErrorLabel;
    
    @FXML
    @ResourceKey("type")
    private Label typeLabel;
    
    @FXML
    private TextField typeTextField;
    
    @FXML
    private Label urlLabel;
    
    @FXML
    private TextField urlTextField;
    
    @FXML
    private Label urlErrorLabel;
    
    @FXML
    @ResourceKey("timeZone")
    private Label timeZoneLabel;
    
    @FXML
    private ComboBox timeZoneComboBox;
    
    @FXML
    @ResourceKey("hours12")
    private RadioButton hours12RadioButton;
    
    @FXML
    @ResourceKey("hours24")
    private RadioButton hours24RadioButton;
    
    @FXML
    @ResourceKey("start")
    private Label startLabel;
    
    @FXML
    private DatePicker startDatePicker;
    
    @FXML
    private ComboBox startHourComboBox;
    
    @FXML
    private ComboBox startMinuteComboBox;
    
    @FXML
    private RadioButton startAmRadioButton;
    
    @FXML
    private RadioButton startPmRadioButton;
    
    @FXML
    @ResourceKey("end")
    private Label endLabel;
    
    @FXML
    private DatePicker endDatePicker;
    
    @FXML
    private ComboBox endHourComboBox;
    
    @FXML
    private ComboBox endMinuteComboBox;
    
    @FXML
    private RadioButton endAmRadioButton;
    
    @FXML
    private RadioButton endPmRadioButton;
    
    @FXML
    @ResourceKey("endCannotBeBeforeStart")
    private Label endErrorLabel;
    
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
    
    public static void setCurrentScene(Node sourceNode, AppointmentRow model, String returnViewPath) throws InvalidArgumentException {
        if (model == null)
            throw new InvalidArgumentException("model", "Model cannot be null");
        if (model.getRowState() == AppointmentRow.ROWSTATE_DELETED)
            throw new InvalidArgumentException("model", "Model was already deleted");
        scheduler.App.changeScene(sourceNode, VIEW_PATH, (Stage stage, EditAppointmentController controller) -> {
            controller.returnViewPath = returnViewPath;
            ResourceBundle rb = ResourceBundle.getBundle(RESOURCE_NAME, scheduler.App.getCurrentLocale());
            if (controller.setModel(model)) {
                stage.setTitle(rb.getString("editAppointment"));
            } else {
                stage.setTitle(rb.getString("addNewAppointment"));
            }
        });
    }
}
