package controller;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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
import model.db.CustomerRow;
import model.db.UserRow;
import scheduler.SqlConnectionDependency;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
public class EditAppointmentController extends ItemControllerBase<AppointmentRow> {
    /**
     * The name of the globalization resource bundle for this controller.
     */
    public static final String RESOURCE_NAME = "globalization/editAppointment";

    /**
     * The path of the View associated with this controller.
     */
    public static final String VIEW_PATH = "/view/EditAppointment.fxml";

    private ValidationManager validation;
    
    private ObservableList<CustomerRow> customers;
    
    @FXML
    private ComboBox<CustomerRow> customerComboBox;
    
    @FXML
    void customerChanged(ActionEvent event) { validation.onCustomerChanged(); }
    
    @FXML
    void addCustomerClick(ActionEvent event) {
    }
    
    private ObservableList<UserRow> users;
    
    @FXML
    private ComboBox<UserRow> userComboBox;
    
    @FXML
    void userChanged(ActionEvent event) { validation.onUserChanged(); }
    
    @FXML
    void addUserClick(ActionEvent event) {
        
    }
    
    @FXML
    private Label customerValidationLabel;
    
    @FXML
    private Label userValidationLabel;

    @FXML
    private TextField titleTextField;
    
    @FXML
    private Label titleValidationLabel;
    
    @FXML
    private DatePicker startDatePicker;
    
    private LocalDateTime getStartDateTime() {
        LocalDate date = startDatePicker.getValue();
        Integer hour = startHourComboBox.getSelectionModel().getSelectedItem();
        Integer minutes = startMinuteComboBox.getSelectionModel().getSelectedItem();
        if (date == null || hour == null || minutes == null)
            return null;
        return LocalDateTime.of(date, LocalTime.of(hour, minutes));
    }
    
    @FXML
    void startChanged(ActionEvent event) { validation.onStartChanged(); }
    
    private ObservableList<Integer> hourOptions;
    
    private ObservableList<Integer> minuteOptions;
    
    @FXML
    private ComboBox<Integer> startHourComboBox;
    
    @FXML
    private ComboBox<Integer> startMinuteComboBox;
    
    @FXML
    private Label startValidationLabel;
    
    @FXML
    private DatePicker endDatePicker;
    
    private LocalDateTime getEndDateTime() {
        LocalDate date = endDatePicker.getValue();
        Integer hour = endHourComboBox.getSelectionModel().getSelectedItem();
        Integer minutes = endMinuteComboBox.getSelectionModel().getSelectedItem();
        if (date == null || hour == null || minutes == null)
            return null;
        return LocalDateTime.of(date, LocalTime.of(hour, minutes));
    }
    
    @FXML
    void endChanged(ActionEvent event) { validation.onEndChanged(); }
    
    @FXML
    private ComboBox<Integer> endHourComboBox;
    
    @FXML
    private ComboBox<Integer> endMinuteComboBox;
    
    @FXML
    private Label endValidationLabel;
    
    private ObservableList<TimeZone> timeZones;
    
    @FXML
    private ComboBox<TimeZone> timeZoneComboBox;
    
    @FXML
    void timeZoneChanged(ActionEvent event) { onTimeZoneChanged(); }
    
    void onTimeZoneChanged() {
        TimeZone selectedTimeZone = timeZoneComboBox.getSelectionModel().getSelectedItem();
    }
    
    private ObservableList<AppointmentType> types;
    
    @FXML
    private ComboBox<AppointmentType> typeComboBox;
    
    @FXML
    void typeChanged(ActionEvent event) { validation.onTypeChanged(); }
    
    @FXML
    private Label currentTimeZoneLabel;
    
    @FXML
    private Label currentTimeZoneValue;
    
    @FXML
    private Label dateTimeValidationLabel;
    
    @FXML
    private Button showConflictsButton;
    
    @FXML
    private Label contactLabel;
    
    @FXML
    private Label locationLabel;
    
    @FXML
    private TextField contactTextField;
    
    @FXML
    private TextArea locationTextArea;
    
    @FXML
    private Label contactValidationLabel;
    
    @FXML
    private Label locationValidationLabel;
    
    @FXML
    private Label urlLabel;
    
    @FXML
    private TextField urlTextField;
    
    @FXML
    private Label urlValidationLabel;
    
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
        validation = new ValidationManager();
        
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
            validation.validateTitle(newValue);
        });
        locationTextArea.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            validation.validateLocation(newValue);
        });
        contactTextField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            validation.validateContact(newValue);
        });
        urlTextField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            validation.validateUrl(newValue);
        });
        
        validation.revalidateAll();
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
    
    private class ValidationManager {
        private final ReadOnlyBooleanWrapper customerValid;

        public boolean isCustomerValid() { return customerValid.get(); }

        public ReadOnlyBooleanProperty customerValidProperty() { return customerValid.getReadOnlyProperty(); }
        
        private final ReadOnlyBooleanWrapper userValid;

        public boolean isUserValid() { return userValid.get(); }

        public ReadOnlyBooleanProperty userValidProperty() { return userValid.getReadOnlyProperty(); }
        
        private final ReadOnlyBooleanWrapper titleValid;

        public boolean isTitleValid() { return titleValid.get(); }

        public ReadOnlyBooleanProperty titleValidProperty() { return titleValid.getReadOnlyProperty(); }
        
        private final ReadOnlyBooleanWrapper startValid;

        public boolean isStartValid() { return startValid.get(); }

        public ReadOnlyBooleanProperty startValidProperty() { return startValid.getReadOnlyProperty(); }
        
        private final ReadOnlyStringWrapper startMessage;

        public String getStartMessage() { return startMessage.get(); }

        public ReadOnlyStringProperty startMessageProperty() { return startMessage.getReadOnlyProperty(); }
        
        private final ReadOnlyBooleanWrapper endValid;

        public boolean isEndValid() { return endValid.get(); }

        public ReadOnlyBooleanProperty endValidProperty() { return endValid.getReadOnlyProperty(); }
        
        private final ReadOnlyBooleanWrapper contactValid;

        public boolean isContactValid() { return contactValid.get(); }

        public ReadOnlyBooleanProperty contactValidProperty() { return contactValid.getReadOnlyProperty(); }
        
        private final ReadOnlyBooleanWrapper locationValid;

        public boolean isLocationValid() { return locationValid.get(); }

        public ReadOnlyBooleanProperty locationValidProperty() { return locationValid.getReadOnlyProperty(); }
        
        private final ReadOnlyBooleanWrapper urlValid;

        public boolean isUrlValid() { return urlValid.get(); }

        public ReadOnlyBooleanProperty urlValidProperty() { return urlValid.getReadOnlyProperty(); }
        
        private final ReadOnlyStringWrapper urlMessage;

        public String getUrlMessage() { return urlMessage.get(); }

        public ReadOnlyStringProperty urlMessageProperty() { return urlMessage.getReadOnlyProperty(); }
        
        ValidationManager() {
            customerValid = new ReadOnlyBooleanWrapper(false);
            customerValid.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                if (newValue)
                    util.collapseLabeledVertical(customerValidationLabel);
                else
                    util.restoreLabeledVertical(customerValidationLabel, currentResourceBundle.getString("required"));
            });
            userValid = new ReadOnlyBooleanWrapper(false);
            userValid.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                if (newValue)
                    util.collapseLabeledVertical(userValidationLabel);
                else
                    util.restoreLabeledVertical(userValidationLabel, currentResourceBundle.getString("required"));
            });
            
            titleValid = new ReadOnlyBooleanWrapper(false);
            titleValid.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                if (newValue)
                    util.collapseLabeledVertical(titleValidationLabel);
                else
                    util.restoreLabeledVertical(titleValidationLabel, currentResourceBundle.getString("required"));
            });
            startValid = new ReadOnlyBooleanWrapper(false);
            startMessage = new ReadOnlyStringWrapper(currentResourceBundle.getString("required"));
            startValid.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                if (newValue)
                    util.collapseLabeledVertical(startValidationLabel);
                else
                    util.restoreLabeledVertical(startValidationLabel, startMessage.getValue());
            });
            endValid = new ReadOnlyBooleanWrapper(false);
            endValid.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                if (newValue)
                    util.collapseLabeledVertical(endValidationLabel);
                else
                    util.restoreLabeledVertical(endValidationLabel, currentResourceBundle.getString("required"));
            });
            contactValid = new ReadOnlyBooleanWrapper();
            contactValid.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                if (newValue)
                    util.collapseLabeledVertical(contactValidationLabel);
                else
                    util.restoreLabeledVertical(contactValidationLabel, currentResourceBundle.getString("required"));
            });
            locationValid = new ReadOnlyBooleanWrapper();
            locationValid.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                if (newValue)
                    util.collapseLabeledVertical(locationValidationLabel);
                else
                    util.restoreLabeledVertical(locationValidationLabel, currentResourceBundle.getString("required"));
            });
            urlValid = new ReadOnlyBooleanWrapper();
            urlMessage = new ReadOnlyStringWrapper(currentResourceBundle.getString("required"));
            urlValid.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                if (newValue)
                    util.collapseLabeledVertical(urlValidationLabel);
                else
                    util.restoreLabeledVertical(urlValidationLabel, urlMessage.getValue());
            });
        }
        
        void revalidateAll() {
            onCustomerChanged();
            onUserChanged();
            validateTitle(titleTextField.getText());
            onStartChanged();
            onEndChanged();
            onTypeChanged();
            validateContact(contactTextField.getText());
        }
        
        void onCustomerChanged() { customerValid.set(customerComboBox.getSelectionModel().getSelectedItem() != null); }
    
        void onUserChanged() { userValid.set(userComboBox.getSelectionModel().getSelectedItem() != null); }

        void validateTitle(String newValue) { titleValid.set(newValue != null && !newValue.trim().isEmpty()); }

        void onStartChanged() {
            LocalDateTime start = getStartDateTime();
            if (start == null)
                startMessage.set(currentResourceBundle.getString("required"));
            else if (isEndValid() && start.compareTo(getEndDateTime()) > 0)
                startMessage.set(currentResourceBundle.getString("endCannotBeBeforeStart"));
            else {
                startValid.set(true);
                return;
            }
            startValid.set(false);
        }

        void onEndChanged() {
            LocalDateTime end = getEndDateTime();
            if (end == null) {
                endValid.set(false);
                return;
            }
            endValid.set(true);
            if (isStartValid()) {
                if (end.compareTo(getStartDateTime()) < 0) {
                    startMessage.set(currentResourceBundle.getString("endCannotBeBeforeStart"));
                    startValid.set(false);
                } else
                    startValid.set(true);
            }
        }
    
        void onTypeChanged() {
            AppointmentType selectedType = typeComboBox.getSelectionModel().getSelectedItem();
            typeComboBox.getButtonCell().setItem(selectedType);
            if (selectedType.isExplicitLocation()) {
                urlValid.set(true);
                util.restoreControlVertical(locationLabel);
                util.restoreControlVertical(locationTextArea);
                util.collapseControlVertical(urlLabel);
                util.collapseControlVertical(urlTextField);
                validateLocation(locationTextArea.getText());
            } else {
                locationValid.set(true);
                util.collapseControlVertical(locationLabel);
                util.collapseControlVertical(locationTextArea);
                if (selectedType.isExplicitUrl())
                    util.restoreLabeledVertical(urlLabel, currentResourceBundle.getString("meetingUrl"));
                else if (selectedType.isPhoneUrl())
                    util.restoreLabeledVertical(urlLabel, currentResourceBundle.getString("phoneNumber"));
                else {
                    urlValid.set(true);
                    util.collapseControlVertical(urlLabel);
                    util.collapseControlVertical(urlTextField);
                    return;
                }
                util.restoreControlVertical(urlTextField);
                validateUrl(urlTextField.getText());
            }
        }

        void validateLocation(String newValue) {
            if (typeComboBox.getSelectionModel().getSelectedItem().isExplicitLocation())
                locationValid.set(newValue != null && !newValue.trim().isEmpty());
        }

        void validateUrl(String newValue) {
            AppointmentType selectedType = typeComboBox.getSelectionModel().getSelectedItem();
            if (selectedType.isExplicitUrl()) {
                if (newValue == null || newValue.trim().isEmpty())
                    urlMessage.set(currentResourceBundle.getString("required"));
                else {
                    try {
                        URL url = new URL(newValue);
                        if (url.getHost() != null && !url.getHost().trim().isEmpty()) {
                            urlValid.set(true);
                            return;
                        }
                    } catch (MalformedURLException ex) {
                        Logger.getLogger(EditAppointmentController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    urlMessage.set(currentResourceBundle.getString("invalidUrl"));
                }
                urlValid.set(false);
            }
        }

        void validateContact(String newValue) { contactValid.set(newValue != null && !newValue.trim().isEmpty()); }
    }
}
