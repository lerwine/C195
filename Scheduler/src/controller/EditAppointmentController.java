package controller;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.TimeZone;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
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
    @ResourceKey("user")
    private Label userLabel;
    
    @FXML
    private ComboBox<String> customerComboBox;
    
    @FXML
    @ResourceKey("add")
    private Button addCustomerButton;

    @FXML
    private ComboBox<String> userComboBox;
    
    @FXML
    @ResourceKey("add")
    private Button addUserButton;
    
    @FXML
    private Label customerValidationLabel;
    
    @FXML
    private Label userValidationLabel;

    @FXML
    @ResourceKey("title")
    private Label titleLabel;
    
    @FXML
    private TextField titleTextField;
    
    @FXML
    @ResourceKey("required")
    private Label titleValidationLabel;
    
    @FXML
    @ResourceKey("start")
    private Label startLabel;
    
    @FXML
    private DatePicker startDatePicker;
    
    @FXML
    private ComboBox<String> startHourComboBox;
    
    @FXML
    private ComboBox<String> startMinuteComboBox;
    
    @FXML
    @ResourceKey("required")
    private Label startValidationLabel;
    
    @FXML
    @ResourceKey("end")
    private Label endLabel;
    
    @FXML
    private DatePicker endDatePicker;
    
    @FXML
    private ComboBox<String> endHourComboBox;
    
    @FXML
    private ComboBox<String> endMinuteComboBox;
    
    @FXML
    @ResourceKey("required")
    private Label endValidationLabel;
    
    @FXML
    private Label dateTimeValidationLabel;
    
    @FXML
    @ResourceKey("show")
    private Button showConflictsButton;
    
    @FXML
    @ResourceKey("timeZone")
    private Label timeZoneLabel;
    
    @FXML
    private ComboBox<TimeZone> timeZoneComboBox;
    
    @FXML
    @ResourceKey("type")
    private Label typeLabel;
    
    @FXML
    private ComboBox<String> typeComboBox;
    
    @FXML
    @ResourceKey("currentTimeZone")
    private Label currentTimeZoneLabel;
    
    @FXML
    private Label currentTimeZoneValue;
    
    @FXML
    @ResourceKey("location")
    private Label locationLabel;
    
    @FXML
    @ResourceKey("pointOfContact")
    private Label contactLabel;
    
    @FXML
    private TextField locationTextField;
    
    @FXML
    private TextField contactTextField;
    
    @FXML
    @ResourceKey("locationRequired")
    private Label locationValidationLabel;
    
    @FXML
    @ResourceKey("required")
    private Label contactValidationLabel;
    
    @FXML
    private Label urlLabel;
    
    @FXML
    private TextField urlTextField;
    
    @FXML
    private Label urlValidationLabel;
    
    @FXML
    @ResourceKey("description")
    private Label descriptionLabel;
    
    @FXML
    private TextArea descriptionTextArea;
    
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
    
    static void restore(Node sourceNode, ControllerState state) {
        scheduler.App.changeScene(sourceNode, VIEW_PATH, (Stage stage, EditAppointmentController controller) -> {
            controller.returnViewPath = state.returnViewPath;
            ResourceBundle rb = ResourceBundle.getBundle(RESOURCE_NAME, scheduler.App.getCurrentLocale());
            if (controller.setModel(state.model)) {
                stage.setTitle(rb.getString("editAppointment"));
            } else {
                stage.setTitle(rb.getString("addNewAppointment"));
            }
        });
    }
    
    private ObservableList<TimeZone> timeZones;
    
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
            ArrayList<TimeZone> tz = new ArrayList<>();
            Arrays.stream(TimeZone.getAvailableIDs()).forEach((String id) -> {
                tz.add(TimeZone.getTimeZone(id));
            });
            controller.timeZones = FXCollections.observableArrayList(tz);
            controller.timeZoneComboBox.setItems(controller.timeZones);
        });
    }

    @FXML
    void addCustomerClick(ActionEvent event) {
    }
    
    @FXML
    void addUserClick(ActionEvent event) {
        
    }
    
    @FXML
    void customerChanged(ActionEvent event) {
        
    }
    
    @FXML
    void userChanged(ActionEvent event) {
        
    }
    
    @FXML
    void startChanged(ActionEvent event) {
        
    }
    
    @FXML
    void endChanged(ActionEvent event) {
        
    }
    
    @FXML
    void locationChanged(ActionEvent event) {
        
    }
    
    @FXML
    void urlChanged(ActionEvent event) {
        
    }
    
    @FXML
    void contactChanged(ActionEvent event) {
        
    }
    
    @FXML
    @Override
    void saveChangesClick(ActionEvent event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @FXML
    @Override
    void cancelClick(ActionEvent event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    static class ControllerState {
        private AppointmentRow model;
        private model.db.CustomerRow customerRow;
        private model.db.UserRow userRow;
        private String customerValidation;
        private String userValidation;
        private String titleValue;
        private String titleValidation;
        private LocalDateTime startDateTime;
        private LocalDateTime endDateTime;
        private String startValidation;
        private String endValidation;
        private String dateTimeValidation;
        private boolean showConflictsButton;
        private TimeZone timeZone;
        private String type;
        private String currentTimeZone;
        private String locationText;
        private String contactText;
        private String locationValidation;
        private String contactValidation;
        private String urlText;
        private String urlValidation;
        private String description;
        private String returnViewPath;
        ControllerState(EditAppointmentController controller) {
            this.model = controller.getModel();
            /*
            this.customerRow;
            this.userRow;
            this.customerValidation;
            this.userValidation;
            this.titleValue;
            this.titleValidation;
            this.startDateTime;
            this.endDateTime;
            this.startValidation;
            this.endValidation;
            this.dateTimeValidation;
            this.showConflictsButton;
            this.timeZone;
            this.type;
            this.currentTimeZone;
            this.locationText;
            this.contactText;
            this.locationValidation;
            this.contactValidation;
            this.urlText;
            this.urlValidation;
            this.description;
            this.returnViewPath;
            */
        }
    }
}
