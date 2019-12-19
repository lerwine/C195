package controller;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
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
import model.AppointmentType;
import model.db.AppointmentRow;
import scheduler.InvalidArgumentException;
import model.annotations.ResourceKey;
import model.annotations.ResourceName;
import model.db.CustomerRow;
import model.db.UserRow;
import scheduler.SqlConnectionDependency;

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
    
    private ObservableList<CustomerRow> customers;
    
    @FXML
    private ComboBox<CustomerRow> customerComboBox;
    
    @FXML
    void customerChanged(ActionEvent event) { onCustomerChanged(); }
    
    private void onCustomerChanged() {
        CustomerRow customer = customerComboBox.getSelectionModel().getSelectedItem();
    }
    
    @FXML
    @ResourceKey("add")
    private Button addCustomerButton;

    @FXML
    void addCustomerClick(ActionEvent event) {
    }
    
    private ObservableList<UserRow> users;
    
    @FXML
    private ComboBox<UserRow> userComboBox;
    
    @FXML
    void userChanged(ActionEvent event) { onUserChanged(); }
    
    private void onUserChanged() {
        UserRow user = userComboBox.getSelectionModel().getSelectedItem();
    }
    
    @FXML
    @ResourceKey("add")
    private Button addUserButton;
    
    @FXML
    void addUserClick(ActionEvent event) {
        
    }
    
    @FXML
    private Label customerValidationLabel;
    
    @FXML
    private Label userValidationLabel;

    @FXML
    @ResourceKey("title")
    private Label titleLabel;
    
    @FXML
    private TextField titleTextField;
    
    void titleChanged(String oldValue, String newValue) {
        
    }
    
    @FXML
    @ResourceKey("required")
    private Label titleValidationLabel;
    
    @FXML
    @ResourceKey("start")
    private Label startLabel;
    
    @FXML
    private DatePicker startDatePicker;
    
    @FXML
    void startChanged(ActionEvent event) { onStartChanged(); }
    
    private void onStartChanged() {
        LocalDate date = startDatePicker.getValue();
        String hour = startHourComboBox.getSelectionModel().getSelectedItem();
        String minutes = startMinuteComboBox.getSelectionModel().getSelectedItem();
    }
    
    private ObservableList<String> hourOptions;
    
    private ObservableList<String> minuteOptions;
    
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
    void endChanged(ActionEvent event) {
        
    }
    
    @FXML
    private ComboBox<String> endHourComboBox;
    
    @FXML
    private ComboBox<String> endMinuteComboBox;
    
    @FXML
    @ResourceKey("required")
    private Label endValidationLabel;
    
    @FXML
    @ResourceKey("timeZone")
    private Label timeZoneLabel;
    
    private ObservableList<TimeZone> timeZones;
    
    @FXML
    private ComboBox<TimeZone> timeZoneComboBox;
    
    @FXML
    @ResourceKey("type")
    private Label typeLabel;
    
    private ObservableList<AppointmentType> types;
    
    @FXML
    private ComboBox<AppointmentType> typeComboBox;
    
    @FXML
    @ResourceKey("currentTimeZone")
    private Label currentTimeZoneLabel;
    
    @FXML
    private Label currentTimeZoneValue;
    
    @FXML
    private Label dateTimeValidationLabel;
    
    @FXML
    @ResourceKey("show")
    private Button showConflictsButton;
    
    @FXML
    @ResourceKey("location")
    private Label locationLabel;
    
    @FXML
    @ResourceKey("pointOfContact")
    private Label contactLabel;
    
    @FXML
    private TextField locationTextField;
    
    void locationChanged(String oldValue, String newValue) {
        
    }
    
    @FXML
    private TextField contactTextField;
    
    void contactChanged(String oldValue, String newValue) {
        
    }
    
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
    
    void urlChanged(String oldValue, String newValue) {
        
    }
    
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

        // Open a new SQL connection dependency.
        SqlConnectionDependency dep;
        try {
            dep = new SqlConnectionDependency(true);
            try {
                // Load active customers
                customers = FXCollections.observableArrayList(CustomerRow.getActive(dep.getconnection()));
                // Load active users
                users = FXCollections.observableArrayList(UserRow.getActive(dep.getconnection()));
            } finally {
                dep.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(EditAppointmentController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // Initialize options lists for start and end time combo boxes.
        hourOptions = FXCollections.observableArrayList("00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14",
                "15", "16", "17", "18", "19", "20", "21", "22", "23");
        minuteOptions = FXCollections.observableArrayList("00", "05", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55");
        
        // Initialize options list for time zone combo box.
        ArrayList<TimeZone> tz = new ArrayList<>();
        Arrays.stream(TimeZone.getAvailableIDs()).forEach((String id) -> {
            tz.add(TimeZone.getTimeZone(id));
        });
        timeZones = FXCollections.observableArrayList(tz);
        
        // Get appointment type options.
        types = FXCollections.observableArrayList(scheduler.App.getAppointmentTypes());

        customerComboBox.setItems(customers);
        userComboBox.setItems(users);
        startHourComboBox.setItems(hourOptions);
        endHourComboBox.setItems(hourOptions);
        startMinuteComboBox.setItems(minuteOptions);
        endMinuteComboBox.setItems(minuteOptions);
        timeZoneComboBox.setItems(timeZones);
        typeComboBox.setItems(types);
        
        titleTextField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            titleChanged(oldValue, newValue);
        });
        locationTextField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            locationChanged(oldValue, newValue);
        });
        contactTextField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            contactChanged(oldValue, newValue);
        });
        urlTextField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            urlChanged(oldValue, newValue);
        });
    }
    
    static void restore(Node sourceNode, ControllerState state) {
        scheduler.App.changeScene(sourceNode, VIEW_PATH, (Stage stage, EditAppointmentController controller) -> {
            controller.returnViewPath = state.returnViewPath;
            controller.applyModel(state.model, stage);
        });
    }

    public static void setCurrentScene(Node sourceNode, AppointmentRow model, String returnViewPath) throws InvalidArgumentException {
        if (model == null)
            throw new InvalidArgumentException("model", "Model cannot be null");
        if (model.getRowState() == AppointmentRow.ROWSTATE_DELETED)
            throw new InvalidArgumentException("model", "Model was already deleted");
        scheduler.App.changeScene(sourceNode, VIEW_PATH, (Stage stage, EditAppointmentController controller) -> {
            controller.returnViewPath = returnViewPath;
            controller.applyModel(model, stage);
        });
    }
    
    private void applyModel(AppointmentRow model, Stage stage) {
        ResourceBundle rb = ResourceBundle.getBundle(RESOURCE_NAME, scheduler.App.getCurrentLocale());
        if (setModel(model)) {
            stage.setTitle(rb.getString("editAppointment"));
        } else {
            stage.setTitle(rb.getString("addNewAppointment"));
        }
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
