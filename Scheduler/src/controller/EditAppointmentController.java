package controller;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
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
import javafx.scene.control.ListView;
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
    
    private boolean customerNotValid = false;
    
    private void onCustomerChanged() {
        customerNotValid = customerComboBox.getSelectionModel().getSelectedItem() == null;
        if (customerNotValid)
            restoreLabeledVertical(customerValidationLabel, currentResourceBundle.getString("required"));
        else
            collapseLabeledVertical(customerValidationLabel);
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
    
    private boolean userNotValid = false;
    
    private void onUserChanged() {
        userNotValid = userComboBox.getSelectionModel().getSelectedItem() == null;
        if (userNotValid)
            restoreLabeledVertical(userValidationLabel, currentResourceBundle.getString("required"));
        else
            collapseLabeledVertical(userValidationLabel);
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
    
    private boolean titleNotValid = false;
    
    void titleChanged(String newValue) {
        titleNotValid = newValue == null || newValue.trim().isEmpty();
        if (titleNotValid)
            restoreLabeledVertical(titleValidationLabel, currentResourceBundle.getString("required"));
        else
            collapseLabeledVertical(titleValidationLabel);
    }
    
    @FXML
    @ResourceKey("required")
    private Label titleValidationLabel;
    
    @FXML
    @ResourceKey("start")
    private Label startLabel;
    
    @FXML
    private DatePicker startDatePicker;
    
    private boolean startNotValid = false;
    
    private LocalDateTime getStartDateTime() {
        LocalDate date = startDatePicker.getValue();
        Integer hour = startHourComboBox.getSelectionModel().getSelectedItem();
        Integer minutes = startMinuteComboBox.getSelectionModel().getSelectedItem();
        if (date == null || hour == null || minutes == null)
            return null;
        return LocalDateTime.of(date, LocalTime.of(hour, minutes));
    }
    
    @FXML
    void startChanged(ActionEvent event) { onStartChanged(); }
    
    private void onStartChanged() {
        LocalDateTime start = getStartDateTime();
        if (start == null) {
            startNotValid = true;
            restoreLabeledVertical(startValidationLabel, currentResourceBundle.getString("required"));
        } else {
            LocalDateTime end = getEndDateTime();
            startNotValid = end != null && end.compareTo(start) < 0;
            if (startNotValid)
                restoreLabeledVertical(startValidationLabel, currentResourceBundle.getString("endCannotBeBeforeStart"));
            else
                collapseLabeledVertical(startValidationLabel);
        }
    }
    
    private ObservableList<Integer> hourOptions;
    
    private ObservableList<Integer> minuteOptions;
    
    @FXML
    private ComboBox<Integer> startHourComboBox;
    
    @FXML
    private ComboBox<Integer> startMinuteComboBox;
    
    @FXML
    @ResourceKey("required")
    private Label startValidationLabel;
    
    @FXML
    @ResourceKey("end")
    private Label endLabel;
    
    @FXML
    private DatePicker endDatePicker;
    
    private boolean endNotValid = false;
    
    private LocalDateTime getEndDateTime() {
        LocalDate date = endDatePicker.getValue();
        Integer hour = endHourComboBox.getSelectionModel().getSelectedItem();
        Integer minutes = endMinuteComboBox.getSelectionModel().getSelectedItem();
        if (date == null || hour == null || minutes == null)
            return null;
        return LocalDateTime.of(date, LocalTime.of(hour, minutes));
    }
    
    @FXML
    void endChanged(ActionEvent event) { onEndChanged(); }
    
    void onEndChanged() {
        LocalDateTime end = getEndDateTime();
        if (end == null) {
            endNotValid = true;
            restoreLabeledVertical(endValidationLabel, currentResourceBundle.getString("required"));
        } else {
            endNotValid = false;
            collapseLabeledVertical(endValidationLabel);
            LocalDateTime start = getStartDateTime();
            if (start != null) {
                startNotValid = end.compareTo(start) < 0;
                if (startNotValid)
                    restoreLabeledVertical(startValidationLabel, currentResourceBundle.getString("endCannotBeBeforeStart"));
                else
                    collapseLabeledVertical(startValidationLabel);
            }
        }
    }
    
    @FXML
    private ComboBox<Integer> endHourComboBox;
    
    @FXML
    private ComboBox<Integer> endMinuteComboBox;
    
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
    void timeZoneChanged(ActionEvent event) { onTimeZoneChanged(); }
    
    void onTimeZoneChanged() {
        TimeZone selectedTimeZone = timeZoneComboBox.getSelectionModel().getSelectedItem();
    }
    
    @FXML
    @ResourceKey("type")
    private Label typeLabel;
    
    private ObservableList<AppointmentType> types;
    
    @FXML
    private ComboBox<AppointmentType> typeComboBox;
    
    @FXML
    void typeChanged(ActionEvent event) { onTypeChanged(); }
    
    void onTypeChanged() {
        AppointmentType selectedType = typeComboBox.getSelectionModel().getSelectedItem();
        if (selectedType.isLocationRequired()) {
            restoreControlVertical(endLabel);
            restoreLabeledVertical(endLabel, "");
            locationTextArea.setDisable(false);
            locationChanged(locationTextArea.getText());
        } else {
            locationTextArea.setDisable(true);
            locationTextArea.setText(selectedType.getDisplayText());
            collapseLabeledVertical(locationValidationLabel);
        }
        if (selectedType.isPhoneUrl()) {
            restoreLabeledVertical(urlLabel, currentResourceBundle.getString("phoneNumber"));
            restoreControlVertical(urlTextField);
            urlChanged(urlTextField.getText());
        } else if (selectedType.isUrlRequired()) {
            restoreLabeledVertical(urlLabel, currentResourceBundle.getString("meetingUrl"));
            restoreControlVertical(urlTextField);
            urlChanged(urlTextField.getText());
        } else {
            collapseLabeledVertical(urlLabel);
            collapseControlVertical(urlTextField);
            collapseLabeledVertical(urlValidationLabel);
        }
    }
    
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
    @ResourceKey("pointOfContact")
    private Label contactLabel;
    
    @FXML
    @ResourceKey("location")
    private Label locationLabel;
    
    @FXML
    private TextField contactTextField;
    
    void contactChanged(String newValue) {
        
    }
    
    @FXML
    private TextArea locationTextArea;
    
    void locationChanged(String newValue) {
        
    }
    
    @FXML
    @ResourceKey("required")
    private Label contactValidationLabel;
    
    @FXML
    @ResourceKey("locationRequired")
    private Label locationValidationLabel;
    
    @FXML
    @ResourceKey("meetingUrl")
    private Label urlLabel;
    
    @FXML
    private TextField urlTextField;
    
    void urlChanged(String newValue) {
        
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
        currentResourceBundle = rb;
        
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
        hourOptions = FXCollections.observableArrayList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23);
        minuteOptions = FXCollections.observableArrayList(0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55);
        
        // Initialize options list for time zone combo box.
        ArrayList<TimeZone> tzArr = new ArrayList<>();
        Arrays.stream(TimeZone.getAvailableIDs()).forEach((String id) -> {
            tzArr.add(TimeZone.getTimeZone(id));
        });
        timeZones = FXCollections.observableArrayList(tzArr);
        // Get appointment type options.
        types = FXCollections.observableArrayList(scheduler.App.getAppointmentTypes());

        customerComboBox.setItems(customers);
        userComboBox.setItems(users);
        LocalDateTime date = LocalDateTime.now().plusDays(1);
        startDatePicker.setValue(date.toLocalDate());
        startHourComboBox.setItems(hourOptions);
        startHourComboBox.getSelectionModel().select(date.getHour());
        startMinuteComboBox.setItems(minuteOptions);
        startMinuteComboBox.getSelectionModel().select(0);
        date = date.plusHours(1);
        endHourComboBox.setItems(hourOptions);
        endHourComboBox.getSelectionModel().select(date.getHour());
        endMinuteComboBox.setItems(minuteOptions);
        endMinuteComboBox.getSelectionModel().select(0);
        timeZoneComboBox.setItems(timeZones);
        String tzId = TimeZone.getDefault().getID();
        Optional<TimeZone> tz = timeZones.stream().filter((TimeZone t) -> t.getID().equals(tzId)).findFirst();
        timeZoneComboBox.getSelectionModel().select((tz.isPresent()) ? tz.get() : timeZones.get(0));
        typeComboBox.setItems(types);
        typeComboBox.getSelectionModel().select(types.get(0));
        titleTextField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            titleChanged(newValue);
        });
        locationTextArea.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            locationChanged(newValue);
        });
        contactTextField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            contactChanged(newValue);
        });
        urlTextField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            urlChanged(newValue);
        });
        
        onCustomerChanged();
        onUserChanged();
        contactChanged(contactTextField.getText());
        titleChanged(titleTextField.getText());
        onTypeChanged();
        onEndChanged();
        onStartChanged();
        onTimeZoneChanged();
    }
    
    ResourceBundle currentResourceBundle;
    
    public static void setCurrentScene(Stage sourceStage, AppointmentRow model, String returnViewPath) throws InvalidArgumentException {
        if (model == null)
            throw new InvalidArgumentException("model", "Model cannot be null");
        if (model.getRowState() == AppointmentRow.ROWSTATE_DELETED)
            throw new InvalidArgumentException("model", "Model was already deleted");
        scheduler.App.setScene(sourceStage, VIEW_PATH, RESOURCE_NAME, (Stage stage, ResourceBundle rb, EditAppointmentController controller) -> {
            controller.returnViewPath = returnViewPath;
            controller.applyModel(model, stage, rb);
        });
    }
    
    private void applyModel(AppointmentRow model, Stage stage, ResourceBundle rb) {
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
}
