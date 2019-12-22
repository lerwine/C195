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
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.StringProperty;
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

    /**
     * Code for Phone Conference appointments, where the phone number is encoded into the URL field.
     */
    public static final String APPOINTMENT_CODE_PHONE = "phone";
    /**
     * Code for Virtual Meetings specified in the URL field.
     */
    public static final String APPOINTMENT_CODE_VIRTUAL = "virtual";
    /**
     * Code for appointments where the implicit location is at the Customer Site.
     */
    public static final String APPOINTMENT_CODE_CUSTOMER = "customer";
    /**
     * Code for appointments where the implicit location is at the Home Office.
     */
    public static final String APPOINTMENT_CODE_HOME = "home";
    /**
     * CCode for appointments where the implicit location is at the Germany Office.
     */
    public static final String APPOINTMENT_CODE_GERMANY = "germany";
    /**
     * Code for appointments where the implicit location is at the India Office.
     */
    public static final String APPOINTMENT_CODE_INDIA = "india";
    /**
     * Code for appointments where the implicit location is at the Honduras Office.
     */
    public static final String APPOINTMENT_CODE_HONDURAS = "honduras";
    /**
     * Code for appointments at other explicit physical locations.
     */
    public static final String APPOINTMENT_CODE_OTHER = "other";
    
    private ObservableList<CustomerRow> customers;
    
    @FXML
    private ComboBox<CustomerRow> customerComboBox;
    
    @FXML
    void addCustomerClick(ActionEvent event) {
    }
    
    private ObservableList<UserRow> users;
    
    @FXML
    private ComboBox<UserRow> userComboBox;
    
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
    
    private ObservableList<String> types;
    
    @FXML
    private ComboBox<String> typeComboBox;
    
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
    
    private TypeBindings typeBindings;
    
    private ValidationBindings validationBindings;
    
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
        types = FXCollections.observableArrayList(APPOINTMENT_CODE_PHONE, APPOINTMENT_CODE_VIRTUAL, APPOINTMENT_CODE_CUSTOMER,
                APPOINTMENT_CODE_HOME, APPOINTMENT_CODE_GERMANY, APPOINTMENT_CODE_INDIA, APPOINTMENT_CODE_HONDURAS,
                APPOINTMENT_CODE_OTHER);

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
        onTimeZoneChanged();
        typeBindings = new TypeBindings();
        validationBindings = new ValidationBindings();
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
    
    private class TypeBindings {
        private final BooleanBinding phone;
        private final BooleanBinding virtual;
        private final BooleanBinding explicitPhysicalLocation;
        private final BooleanBinding implicitPhysicalLocation;
        private final BooleanBinding showUrl;
        private final BooleanBinding valid;
        TypeBindings() {
            ReadOnlyObjectProperty<String> selectedItemProperty = typeComboBox.getSelectionModel().selectedItemProperty();
            phone = selectedItemProperty.isEqualTo(APPOINTMENT_CODE_PHONE);
            phone.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            });
            virtual = selectedItemProperty.isEqualTo(APPOINTMENT_CODE_VIRTUAL);
            virtual.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            });
            explicitPhysicalLocation = selectedItemProperty.isEqualTo(APPOINTMENT_CODE_OTHER);
            explicitPhysicalLocation.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            });
            implicitPhysicalLocation = selectedItemProperty.isEqualTo(APPOINTMENT_CODE_HOME)
                    .or(selectedItemProperty.isEqualTo(APPOINTMENT_CODE_GERMANY))
                    .or(selectedItemProperty.isEqualTo(APPOINTMENT_CODE_INDIA)).or(selectedItemProperty.isEqualTo(APPOINTMENT_CODE_HONDURAS));
            implicitPhysicalLocation.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            });
            showUrl = phone.or(virtual);
            showUrl.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            });
            valid = showUrl.or(explicitPhysicalLocation).or(implicitPhysicalLocation);
        }
    }
    
    private class ValidationBindings {
        final BooleanBinding customerValid;
        final BooleanBinding userValid;
        final BooleanBinding titleValid;
        final ObjectBinding<LocalDateTime> startDateTime;
        final ObjectBinding<LocalDateTime> endDateTime;
        final StringBinding startValidationMessage;
        final StringBinding endValidationMessage;
        final BooleanBinding contactValid;
        final BooleanBinding locationValid;
        final StringBinding urlValidationMessage;
        final BooleanBinding valid;
        
        ValidationBindings() {
            customerValid = customerComboBox.getSelectionModel().selectedItemProperty().isNotNull();
            userValid = userComboBox.getSelectionModel().selectedItemProperty().isNotNull();
            titleValid = scheduler.util.notNullOrWhiteSpace(titleTextField.textProperty());
            startDateTime = scheduler.util.asLocalDateTime(startDatePicker.valueProperty(), startHourComboBox.getSelectionModel().selectedItemProperty(),
                    startMinuteComboBox.getSelectionModel().selectedItemProperty());
            endDateTime = scheduler.util.asLocalDateTime(endDatePicker.valueProperty(), endHourComboBox.getSelectionModel().selectedItemProperty(),
                    endMinuteComboBox.getSelectionModel().selectedItemProperty());
            startValidationMessage = new StringBinding() {
                { super.bind(startDateTime, endDateTime); }
                
                @Override
                protected String computeValue() {
                    LocalDateTime d = startDateTime.get();
                    if (d == null)
                        return currentResourceBundle.getString("required");
                    LocalDateTime e = endDateTime.get();
                    if (e != null && d.compareTo(e) > 0)
                        return currentResourceBundle.getString("endCannotBeBeforeStart");
                    return "";
                }
                
                @Override
                public void dispose() { super.unbind(startDateTime, endDateTime); }
            };
            endValidationMessage = new StringBinding() {
                { super.bind(endDateTime); }
                
                @Override
                protected String computeValue() {
                    LocalDateTime d = startDateTime.get();
                    if (d == null)
                        return currentResourceBundle.getString("required");
                    return "";
                }
                
                @Override
                public void dispose() { super.unbind(startDateTime); }
            };
            contactValid = scheduler.util.notNullOrWhiteSpace(contactTextField.textProperty());
            locationValid = scheduler.util.notNullOrWhiteSpace(locationTextArea.textProperty()).or(typeBindings.explicitPhysicalLocation.not());
            urlValidationMessage = new StringBinding() {
                private final StringProperty url;
                {
                    url = urlTextField.textProperty();
                    super.bind(url, typeBindings.phone, typeBindings.virtual);
                }
                
                @Override
                protected String computeValue() {
                    if (typeBindings.phone.get()) {
                        String s = url.get();
                        if (s == null || s.trim().isEmpty())
                            return currentResourceBundle.getString("required");
                    } else if (typeBindings.virtual.get()) {
                        String s = url.get();
                        if (s == null || s.trim().isEmpty())
                            return currentResourceBundle.getString("required");
                        try {
                            URL u = new URL(s);
                            if (u.getHost() != null && !u.getHost().trim().isEmpty())
                                return "";
                        } catch (MalformedURLException ex) {
                            Logger.getLogger(EditAppointmentController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        return currentResourceBundle.getString("invalidUrl");
                    }
                    
                    return "";
                }
                
                @Override
                public void dispose() { super.unbind(url, typeBindings.phone, typeBindings.virtual); }
            };
            valid = customerValid.and(userValid).and(titleValid).and(startValidationMessage.isEmpty()).and(endValidationMessage.isEmpty())
                    .and(contactValid).and(locationValid).and(urlValidationMessage.isEmpty());
            
            customerValid.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            });
            userValid.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            });
            titleValid.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            });
            startValidationMessage.addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            });
            endValidationMessage.addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            });
            contactValid.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            });
            locationValid.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            });
            urlValidationMessage.addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            });
        }
    }
}
