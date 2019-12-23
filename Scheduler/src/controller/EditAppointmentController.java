package controller;

import controller.bindings.NonNullLabeledBinding;
import controller.bindings.NonWhiteSpaceLabeledBinding;
import controller.cell.AppointmentTypeListCell;
import controller.cell.AppointmentTypeListCellFactory;
import controller.cell.CustomerListCell;
import controller.cell.CustomerListCellFactory;
import controller.cell.TimeZoneListCell;
import controller.cell.TimeZoneListCellFactory;
import controller.cell.UserListCell;
import controller.cell.UserListCellFactory;
import controller.cell.ZeroPadDigitListCell;
import controller.cell.ZeroPadDigitListCellFactory;
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
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleStringProperty;
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
    //<editor-fold defaultstate="collapsed" desc="Fields">
    
    //<editor-fold defaultstate="collapsed" desc="Constants">
    
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
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="JavaFX Controls">
    
    private ObservableList<CustomerRow> customers;
    
    @FXML
    private ComboBox<CustomerRow> customerComboBox;
    
    @FXML
    private ComboBox<UserRow> userComboBox;
    
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
    
    @FXML
    private ComboBox<Integer> startHourComboBox;
    
    @FXML
    private ComboBox<Integer> startMinuteComboBox;
    
    @FXML
    private Label startValidationLabel;
    
    @FXML
    private DatePicker endDatePicker;
    
    @FXML
    private ComboBox<Integer> endHourComboBox;
    
    @FXML
    private ComboBox<Integer> endMinuteComboBox;
    
    @FXML
    private Label endValidationLabel;
    
    @FXML
    private ComboBox<TimeZone> timeZoneComboBox;
    
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
    private TextField phoneTextField;
    
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
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Observables">
    
    private ObservableList<UserRow> users;
    
    private ObservableList<Integer> hourOptions;
    
    private ObservableList<Integer> minuteOptions;
    
    private ObservableList<TimeZone> timeZones;
    
    private ObservableList<AppointmentRow> currentAndFuture;
    
    private ObjectBinding<LocalDateTime> startDateTime;
    
    private ObjectBinding<LocalDateTime> endDateTime;
    
    private ObservableList<String> types;
    
    private TypeBindings typeBindings;
    
    private BooleanBinding customerValid;
    
    private BooleanBinding userValid;
    
    private BooleanBinding titleValid;
    
    private BooleanBinding contactValid;
    
    private DateRangeValidator dateRangeValid;
    
    private LocationValidator locationValid;
    
    private PhoneValidator phoneValidator;
    
    private UrlValidator urlValid;
    
    private BooleanBinding valid;
    
    //</editor-fold>
    
    ResourceBundle currentResourceBundle;
    
    private final scheduler.App.StageManager stageManager;
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Initialization">
    
    public EditAppointmentController(scheduler.App.StageManager stageManager) {
        this.stageManager = stageManager;
    }
    
    /**
     * Initializes the controller class.
     * @param url The URL of the associated view.
     * @param rb The resources provided by the {@link javafx.fxml.FXMLLoader}
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        currentResourceBundle = rb;
        super.initialize(url, rb);
        
        // Configure combo box cells
        customerComboBox.setCellFactory(new CustomerListCellFactory<>());
        customerComboBox.setButtonCell(new CustomerListCell<>());
        userComboBox.setCellFactory(new UserListCellFactory<>());
        userComboBox.setButtonCell(new UserListCell<>());
        startHourComboBox.setCellFactory(new ZeroPadDigitListCellFactory());
        startHourComboBox.setButtonCell(new ZeroPadDigitListCell());
        startMinuteComboBox.setCellFactory(new ZeroPadDigitListCellFactory());
        startMinuteComboBox.setButtonCell(new ZeroPadDigitListCell());
        endHourComboBox.setCellFactory(new ZeroPadDigitListCellFactory());
        endHourComboBox.setButtonCell(new ZeroPadDigitListCell());
        endMinuteComboBox.setCellFactory(new ZeroPadDigitListCellFactory());
        endMinuteComboBox.setButtonCell(new ZeroPadDigitListCell());
        timeZoneComboBox.setCellFactory(new TimeZoneListCellFactory());
        timeZoneComboBox.setButtonCell(new TimeZoneListCell());
        typeComboBox.setCellFactory(new AppointmentTypeListCellFactory());
        typeComboBox.setButtonCell(new AppointmentTypeListCell());
        
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
        startDateTime = scheduler.util.asLocalDateTime(startDatePicker.valueProperty(),
            startHourComboBox.getSelectionModel().selectedItemProperty(), startMinuteComboBox.getSelectionModel().selectedItemProperty());
        endDateTime = scheduler.util.asLocalDateTime(endDatePicker.valueProperty(),
            endHourComboBox.getSelectionModel().selectedItemProperty(), endMinuteComboBox.getSelectionModel().selectedItemProperty());
        
        // Initialize validation bindings
        typeBindings = new TypeBindings();
        customerValid = new NonNullLabeledBinding<>(customerComboBox.getSelectionModel().selectedItemProperty(), customerValidationLabel, currentResourceBundle.getString("required"));
        userValid = new NonNullLabeledBinding<>(userComboBox.getSelectionModel().selectedItemProperty(), userValidationLabel, currentResourceBundle.getString("required"));
        titleValid = new NonWhiteSpaceLabeledBinding(titleTextField.textProperty(), titleValidationLabel, currentResourceBundle.getString("required"));
        contactValid = new NonWhiteSpaceLabeledBinding(contactTextField.textProperty(), contactValidationLabel, currentResourceBundle.getString("required"));
        dateRangeValid = new DateRangeValidator();
        locationValid = new LocationValidator();
        phoneValidator = new PhoneValidator();
        urlValid = new UrlValidator();
        valid = typeBindings.valid.and(customerValid).and(userValid).and(titleValid).and(contactValid).and(dateRangeValid).and(locationValid).and(phoneValidator).and(urlValid);
        
        // Add aggregate validation binding.
        valid.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            getSaveChangesButton().setDisable(!newValue);
        });
    }
    
    public static void setCurrentScene(scheduler.App.StageManager stageManager, AppointmentRow model) throws InvalidArgumentException {
        if (model == null)
            throw new InvalidArgumentException("model", "Model cannot be null");
        if (model.getRowState() == AppointmentRow.ROWSTATE_DELETED)
            throw new InvalidArgumentException("model", "Model was already deleted");
        stageManager.setSceneWithControllerFactory(VIEW_PATH, RESOURCE_NAME, (Class<?> c) -> new EditAppointmentController(stageManager), (ResourceBundle rb, EditAppointmentController controller) -> {
            controller.applyModel(model, rb);
        });
    }
    
    private void applyModel(AppointmentRow model, ResourceBundle rb) {
        if (setModel(model))
            stageManager.setWindowTitle(rb.getString("editAppointment"));
        else
            stageManager.setWindowTitle(rb.getString("addNewAppointment"));
    }
    
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Event Handlers">
    
    @FXML
    void addCustomerClick(ActionEvent event) {
    }

    @FXML
    void addUserClick(ActionEvent event) {

    }
    
    @FXML
    void showConflictsButtonClick(ActionEvent event) {

    }
    
    @FXML
    @Override
    void saveChangesClick(ActionEvent event) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @FXML
    @Override
    void cancelClick(ActionEvent event) {
        if (stageManager.isRoot())
            HomeScreenController.setCurrentScene(stageManager);
        else
            stageManager.getStage().hide();
    }
    
    //<editor-fold defaultstate="collapsed" desc="Validators">
    
    /**
     *
     */
    private class TypeBindings {
        private final ReadOnlyObjectProperty<String> selectedItem;
        private final BooleanBinding phone;
        private final BooleanBinding virtual;
        private final BooleanBinding explicitPhysicalLocation;
        private final BooleanBinding implicitPhysicalLocation;
        private final BooleanBinding valid;
        
        TypeBindings() {
            selectedItem = typeComboBox.getSelectionModel().selectedItemProperty();
            SimpleStringProperty item = new SimpleStringProperty(selectedItem.get());
            selectedItem.addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                item.set(newValue);
            });
            phone = item.isEqualTo(APPOINTMENT_CODE_PHONE);
            virtual = item.isEqualTo(APPOINTMENT_CODE_VIRTUAL);
            explicitPhysicalLocation = item.isEqualTo(APPOINTMENT_CODE_OTHER);
            implicitPhysicalLocation = item.isEqualTo(APPOINTMENT_CODE_HOME)
                    .or(item.isEqualTo(APPOINTMENT_CODE_GERMANY))
                    .or(item.isEqualTo(APPOINTMENT_CODE_INDIA)).or(item.isEqualTo(APPOINTMENT_CODE_HONDURAS));
            valid = phone.or(virtual).or(explicitPhysicalLocation).or(implicitPhysicalLocation);
        }
    }
    
    /**
     *
     */
    private class DateRangeValidator extends BooleanBinding {
        private final ReadOnlyBooleanWrapper noScheduleConflict;
        private final BooleanBinding startValid;
        private final BooleanBinding endValid;
        private final ReadOnlyObjectProperty<CustomerRow> selectedCustomer;
        private final ReadOnlyObjectProperty<UserRow> selectedUser;
        
        DateRangeValidator()
        {
            selectedCustomer = customerComboBox.getSelectionModel().selectedItemProperty();
            selectedUser = userComboBox.getSelectionModel().selectedItemProperty();
            startValid = startDateTime.isNotNull();
            endValid = endDateTime.isNotNull();
            noScheduleConflict = new ReadOnlyBooleanWrapper(true);
            super.bind(startDateTime, endDateTime, selectedCustomer, selectedUser);
        }
        
        
        @Override
        protected boolean computeValue() {
            LocalDateTime end = endDateTime.get();
            LocalDateTime start = startDateTime.get();
            if (end == null) {
                scheduler.util.collapseLabeledVertical(dateTimeValidationLabel);
                scheduler.util.collapseControlVertical(showConflictsButton);
                scheduler.util.restoreLabeledVertical(endValidationLabel, currentResourceBundle.getString("required"));
                if (start == null)
                    scheduler.util.restoreLabeledVertical(startValidationLabel, currentResourceBundle.getString("required"));
                else
                    scheduler.util.collapseLabeledVertical(startValidationLabel);
                noScheduleConflict.set(true);
                return false;
            }
            if (start == null) {
                scheduler.util.collapseLabeledVertical(dateTimeValidationLabel);
                scheduler.util.collapseControlVertical(showConflictsButton);
                scheduler.util.collapseLabeledVertical(endValidationLabel);
                scheduler.util.restoreLabeledVertical(startValidationLabel, currentResourceBundle.getString("required"));
                noScheduleConflict.set(true);
                return false;
            }
            scheduler.util.collapseLabeledVertical(startValidationLabel);
            if (start.compareTo(end) > 0) {
                scheduler.util.collapseLabeledVertical(dateTimeValidationLabel);
                scheduler.util.collapseControlVertical(showConflictsButton);
                scheduler.util.restoreLabeledVertical(endValidationLabel, currentResourceBundle.getString("endCannotBeBeforeStart"));
                noScheduleConflict.set(true);
                return false;
            }
            
            CustomerRow customer = selectedCustomer.get();
            boolean userConflict, customerConflict;
            if (customer == null)
                customerConflict = false;
            else {
                final int id = customer.getPrimaryKey();
                customerConflict = currentAndFuture.stream().anyMatch((AppointmentRow r) -> r.getCustomerId() == id && r.getStart().compareTo(end) <= 0 && r.getEnd().compareTo(start) >= 0);
            }
            UserRow user = selectedUser.get();
            if (user == null)
                userConflict = false;
            else {
                final int id = user.getPrimaryKey();
                userConflict = currentAndFuture.stream().anyMatch((AppointmentRow r) -> r.getUserId() == id && r.getStart().compareTo(end) <= 0 && r.getEnd().compareTo(start) >= 0);
            }
            if (userConflict)
                scheduler.util.restoreLabeledVertical(dateTimeValidationLabel,
                        currentResourceBundle.getString((customerConflict) ? "conflictsWithBoth" : "conflictsWithUser"));
            else if (customerConflict)
                scheduler.util.restoreLabeledVertical(dateTimeValidationLabel,
                        currentResourceBundle.getString("conflictsWithCustomer"));
            else {
                noScheduleConflict.set(true);
                scheduler.util.collapseLabeledVertical(dateTimeValidationLabel);
                scheduler.util.collapseControlVertical(showConflictsButton);
                return false;
            }
            scheduler.util.restoreControlVertical(showConflictsButton);
            noScheduleConflict.set(false);
            return true;
        }
        
        @Override
        public ObservableList<?> getDependencies() { return FXCollections.observableArrayList(startDateTime, endDateTime, selectedCustomer, selectedUser); }
        
        @Override
        public void dispose() { super.unbind(startDateTime, endDateTime, selectedCustomer, selectedUser); }
    }
    
    /**
     *
     */
    private class LocationValidator extends BooleanBinding {
        StringProperty locationProperty;
        
        LocationValidator()
        {
            locationProperty = locationTextArea.textProperty();
            typeBindings.explicitPhysicalLocation.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                explicitPhysicalLocationChange(newValue);
            });
            boolean changed = typeBindings.explicitPhysicalLocation.get();
            explicitPhysicalLocationChange(changed);
            if (changed)
                locationChange(locationProperty.get());
            super.bind(locationProperty, typeBindings.explicitPhysicalLocation);
        }
        
        private boolean locationChange(String text) {
            if (text == null || text.trim().isEmpty()) {
                scheduler.util.restoreLabeledVertical(locationValidationLabel, currentResourceBundle.getString("required"));
                return false;
            }
            scheduler.util.collapseLabeledVertical(locationValidationLabel);
            return true;
        }
        
        private void explicitPhysicalLocationChange(boolean value) {
            if (value) {
                scheduler.util.restoreLabeledVertical(locationLabel, currentResourceBundle.getString("location"));
                scheduler.util.restoreControlVertical(locationTextArea);
                locationChange(locationProperty.get());
            } else {
                if (!typeBindings.phone.get()) {
                    scheduler.util.collapseLabeledVertical(locationLabel);
                    scheduler.util.collapseLabeledVertical(locationValidationLabel);
                }
                scheduler.util.collapseControlVertical(locationTextArea);
            }
        }
        
        @Override
        protected boolean computeValue() {
            if (typeBindings.explicitPhysicalLocation.get())
                return locationChange(locationProperty.get());
            return true;
        }
        
        @Override
        public ObservableList<?> getDependencies() { return FXCollections.observableArrayList(locationProperty, typeBindings.explicitPhysicalLocation); }
        
        @Override
        public void dispose() { super.unbind(locationProperty, typeBindings.explicitPhysicalLocation); }
    }
    
    /**
     *
     */
    private class PhoneValidator extends BooleanBinding {
        StringProperty phoneProperty;
        
        PhoneValidator()
        {
            phoneProperty = phoneTextField.textProperty();
            typeBindings.phone.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                showPhoneChanged(newValue);
            });
            boolean showPhone = typeBindings.phone.get();
            showPhoneChanged(showPhone);
            if (showPhone)
                phoneTextChanged(phoneProperty.get());
            super.bind(phoneProperty, typeBindings.phone, typeBindings.virtual);
        }
        
        private void showPhoneChanged(boolean value) {
            if (value) {
                scheduler.util.restoreLabeledVertical(locationLabel, currentResourceBundle.getString("phoneNumber"));
                scheduler.util.restoreControlVertical(phoneTextField);
                phoneTextChanged(phoneProperty.get());
            } else {
                if (!typeBindings.explicitPhysicalLocation.get()) {
                    scheduler.util.collapseLabeledVertical(locationLabel);
                    scheduler.util.collapseLabeledVertical(locationValidationLabel);
                }
                scheduler.util.collapseControlVertical(phoneTextField);
            }
        }
        
        private boolean phoneTextChanged(String text) {
            if (text == null || text.trim().isEmpty()) {
                scheduler.util.restoreLabeledVertical(locationValidationLabel, currentResourceBundle.getString("required"));
                return false;
            }
            scheduler.util.collapseLabeledVertical(locationValidationLabel);
            return true;
        }
        
        @Override
        protected boolean computeValue() {
            if (typeBindings.phone.get())
                return phoneTextChanged(phoneProperty.get());
            return true;
        }
        
        @Override
        public ObservableList<?> getDependencies() { return FXCollections.observableArrayList(phoneProperty, typeBindings.phone, typeBindings.virtual); }
        
        @Override
        public void dispose() { super.unbind(phoneProperty, typeBindings.phone, typeBindings.virtual); }
    }
    
    /**
     *
     */
    private class UrlValidator extends BooleanBinding {
        StringProperty urlProperty;
        
        UrlValidator()
        {
            urlProperty = urlTextField.textProperty();
            typeBindings.virtual.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                virtualChanged(newValue);
            });
            boolean showUrl = typeBindings.virtual.get();
            virtualChanged(showUrl);
            if (showUrl)
                urlChanged(urlProperty.get());
            super.bind(urlProperty, typeBindings.virtual);
        }
        
        private void virtualChanged(boolean value) {
            if (value) {
                scheduler.util.restoreControlVertical(urlTextField);
                scheduler.util.restoreControlVertical(urlLabel);
                urlChanged(urlProperty.get());
            } else {
                scheduler.util.collapseControlVertical(urlLabel);
                scheduler.util.collapseControlVertical(urlTextField);
                scheduler.util.collapseControlVertical(urlValidationLabel);
            }
        }
        
        private boolean urlChanged(String text) {
            if (text == null || text.trim().isEmpty()) {
                scheduler.util.restoreLabeledVertical(urlValidationLabel, currentResourceBundle.getString("required"));
                return false;
            }
            try {
                URL url = new URL(text);
                if (url.getHost() != null && !url.getHost().trim().isEmpty()) {
                    scheduler.util.collapseLabeledVertical(urlValidationLabel);
                    return true;
                }
            } catch (MalformedURLException ex) {
                Logger.getLogger(EditAppointmentController.class.getName()).log(Level.SEVERE, null, ex);
            }
            scheduler.util.restoreLabeledVertical(urlValidationLabel, currentResourceBundle.getString("invalidUrl"));
            return false;
        }
        
        @Override
        protected boolean computeValue() {
            if (typeBindings.virtual.get())
                return urlChanged(urlProperty.get());
            return true;
        }
        
        @Override
        public ObservableList<?> getDependencies() { return FXCollections.observableArrayList(urlProperty, typeBindings.virtual); }
        
        @Override
        public void dispose() { super.unbind(urlProperty, typeBindings.virtual); }
    }
    
    //</editor-fold>
    
    //</editor-fold>
}