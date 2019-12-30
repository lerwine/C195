package view.appointment;

import view.ItemController;
import controls.AppointmentTypeListCell;
import controls.AppointmentTypeListCellFactory;
import controls.CustomerListCell;
import controls.CustomerListCellFactory;
import controls.TimeZoneListCell;
import controls.TimeZoneListCellFactory;
import controls.UserListCell;
import controls.UserListCellFactory;
import controls.ZeroPadDigitListCell;
import controls.ZeroPadDigitListCellFactory;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.StageStyle;
import model.db.AppointmentRow;
import model.db.CustomerRow;
import model.db.UserRow;
import view.annotations.FXMLResource;
import view.annotations.GlobalizationResource;
import scheduler.SqlConnectionDependency;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
@GlobalizationResource("view/appointment/EditAppointment")
@FXMLResource("/view/appointment/EditAppointment.fxml")
public class EditAppointment extends ItemController<AppointmentRow> {
    //<editor-fold defaultstate="collapsed" desc="Fields">
    
    //<editor-fold defaultstate="collapsed" desc="Constants">
    
    //<editor-fold defaultstate="collapsed" desc="Resource keys">

//    public static final String RESOURCEKEY_ADD = "add";
    public static final String RESOURCEKEY_ADDNEWAPPOINTMENT = "addNewAppointment";
//    public static final String RESOURCEKEY_CURRENTTIMEZONE = "currentTimeZone";
    public static final String RESOURCEKEY_CUSTOMER = "customer";
//    public static final String RESOURCEKEY_CUSTOMERNOTFOUND = "customerNotFound";
//    public static final String RESOURCEKEY_DESCRIPTION = "description";
    public static final String RESOURCEKEY_EDITAPPOINTMENT = "editAppointment";
//    public static final String RESOURCEKEY_END = "end";
    public static final String RESOURCEKEY_ENDCANNOTBEBEFORESTART = "endCannotBeBeforeStart";
//    public static final String RESOURCEKEY_INVALIDURL = "invalidUrl";
    public static final String RESOURCEKEY_LOCATION = "location";
//    public static final String RESOURCEKEY_POINTOFCONTACT = "pointOfContact";
    public static final String RESOURCEKEY_REQUIRED = "required";
//    public static final String RESOURCEKEY_SHOW = "show";
//    public static final String RESOURCEKEY_START = "start";
//    public static final String RESOURCEKEY_TIMERANGE = "timeRange";
//    public static final String RESOURCEKEY_TIMEZONE = "timeZone";
//    public static final String RESOURCEKEY_TITLE = "title";
//    public static final String RESOURCEKEY_TYPE = "type";
//    public static final String RESOURCEKEY_USER = "user";
//    public static final String RESOURCEKEY_USERNOTFOUND = "userNotFound";
    public static final String RESOURCEKEY_PHONENUMBER = "phoneNumber";
//    public static final String RESOURCEKEY_MEETINGURL = "meetingUrl";
//    public static final String RESOURCEKEY_CREATED = "created";
//    public static final String RESOURCEKEY_BY = "by";
//    public static final String RESOURCEKEY_UPDATED = "updated";
//    public static final String RESOURCEKEY_SAVE = "save";
//    public static final String RESOURCEKEY_CANCEL = "cancel";
    public static final String RESOURCEKEY_INVALIDHOUR = "invalidHour";
    public static final String RESOURCEKEY_INVALIDMINUTE = "invalidMinute";
    public static final String RESOURCEKEY_CONFLICTCUSTOMERNUSERN = "conflictCustomerNUserN";
    public static final String RESOURCEKEY_CONFLICTCUSTOMERNUSER1 = "conflictCustomerNUser1";
    public static final String RESOURCEKEY_CONFLICTCUSTOMER1USERN = "conflictCustomer1UserN";
    public static final String RESOURCEKEY_CONFLICTCUSTOMER1USER1 = "conflictCustomer1User1";
    public static final String RESOURCEKEY_CONFLICTCUSTOMERN = "conflictCustomerN";
    public static final String RESOURCEKEY_CONFLICTCUSTOMER1 = "conflictCustomer1";
    public static final String RESOURCEKEY_CONFLICTUSERN = "conflictUserN";
    public static final String RESOURCEKEY_CONFLICTUSER1 = "conflictUser1";

    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Appointment type codes">
    
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
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="FXMLLoader Injections">
    
    @FXML // Customer selection control
    private ComboBox<CustomerRow> customerComboBox;

    @FXML // User selection control.
    private ComboBox<UserRow> userComboBox;

    @FXML // Label for displaying customer selection validation message.
    private Label customerValidationLabel;

    @FXML // Label for displaying user selection validation message.
    private Label userValidationLabel;

    @FXML // Control for the appointment title.
    private TextField titleTextField;

    @FXML // Label for displaying appointment title validation message.
    private Label titleValidationLabel;

    @FXML // Control for selecting the appointment start date.
    private DatePicker startDatePicker;

    @FXML // Control for selecting the appointment start hour.
    private ComboBox<Integer> startHourComboBox;

    @FXML // Control for selecting the appointment start minute.
    private ComboBox<Integer> startMinuteComboBox;

    @FXML // Label for displaying appointment start date validation message.
    private Label startValidationLabel;

    @FXML // Control for selecting the appointment end date.
    private DatePicker endDatePicker;

    @FXML // Control for selecting the appointment end hour.
    private ComboBox<Integer> endHourComboBox;

    @FXML // Control for selecting the appointment end minute.
    private ComboBox<Integer> endMinuteComboBox;

    @FXML // Label for displaying appointment end date or date range validation message.
    private Label endValidationLabel;

    @FXML // Control for selecting the time zone for the appointment start and end.
    private ComboBox<TimeZone> timeZoneComboBox;

    @FXML // Field label that gets hidden when the user selects the default time zone.
    private Label currentTimeZoneLabel;

    @FXML // Label for displaying the selected time, converted to the default time zone.
    private Label currentTimeZoneValue;

    @FXML // Label for displaying customer and/or user appointment conflict counts.
    private Label conflictValidationLabel;

    @FXML // Container control that holds the validation message and a button the user can use to view the individual conflicts.
    private HBox showConflictsHBox;

    @FXML // Field label for phone number control as well as explicit and implicit location.
    private Label locationLabel;

    @FXML // Appointment type selection control.
    private ComboBox<String> typeComboBox;

    @FXML // Explicit location input control.
    private TextArea locationTextArea;

    @FXML // Phone number input control.
    private TextField phoneTextField;

    @FXML // Label to contain the implicit location (Customer's address).
    private Label implicitLocationLabel;
            
    @FXML // Label for displaying phone or explicit location validation message.
    private Label locationValidationLabel;

    @FXML // Field label for the Meeting URL control.
    private Label urlLabel;

    @FXML // Meeting URL input control.
    private TextField urlTextField;

    @FXML // Label for displaying URL validation message.
    private Label urlValidationLabel;
    
    @FXML // Point-of-Contact input control.
    private TextField contactTextField;

    @FXML // Label for displaying point-of-contact validation message.
    private Label contactValidationLabel;

    @FXML // Appointment description input control.
    private TextArea descriptionTextArea;

    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Observables">
    
    // Items for the customerComboBox control.
    private ObservableList<CustomerRow> customers;
    
    // Items for the userComboBox control.
    private ObservableList<UserRow> users;
    
    // Items for the startHourComboBox and endHourComboBox controls.
    private ObservableList<Integer> hourOptions;
    
    // Items for the startMinuteComboBox and endMinuteComboBox controls.
    private ObservableList<Integer> minuteOptions;
    
    // Items for the timeZoneComboBox control.
    private ObservableList<TimeZone> timeZones;
    
    // Items for the typeComboBox control.
    private ObservableList<String> types;
    
    // Manages visibility and text of controls according to the selected appointment type.
    private TypeSelectionState typeSelectionState;
    
    // Manages visibility of the {@link #showConflictsHBox} control and the text of the {@link #conflictValidationLabel} control according to appointment schedule conflict results.
    private ConflictLookupState conflictLookupState;

    // Produces the validation message for the date range or null if the start and end date range is valid.
    private DateRangeValidation dateRangeValidation;
    
    // Aggregate binding to indicate whether all controls are valid.
    private BooleanBinding valid;
    
    //</editor-fold>

    @Override
    public boolean isValid() { return valid.get(); }
    
    @Override
    public BooleanExpression validProperty() { return valid; }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Initialization">
    
    @FXML
    @Override
    protected void initialize() {
        assert customerComboBox != null : String.format("fx:id=\"customerComboBox\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        assert userComboBox != null : String.format("fx:id=\"userComboBox\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        assert customerValidationLabel != null : String.format("fx:id=\"customerValidationLabel\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        assert userValidationLabel != null : String.format("fx:id=\"userValidationLabel\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        assert titleTextField != null : String.format("fx:id=\"titleTextField\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        assert titleValidationLabel != null : String.format("fx:id=\"titleValidationLabel\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        assert startDatePicker != null : String.format("fx:id=\"startDatePicker\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        assert startHourComboBox != null : String.format("fx:id=\"startHourComboBox\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        assert startMinuteComboBox != null : String.format("fx:id=\"startMinuteComboBox\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        assert startValidationLabel != null : String.format("fx:id=\"startValidationLabel\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        assert endDatePicker != null : String.format("fx:id=\"endDatePicker\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        assert endHourComboBox != null : String.format("fx:id=\"endHourComboBox\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        assert endMinuteComboBox != null : String.format("fx:id=\"endMinuteComboBox\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        assert endValidationLabel != null : String.format("fx:id=\"endValidationLabel\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        assert timeZoneComboBox != null : String.format("fx:id=\"timeZoneComboBox\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        assert currentTimeZoneLabel != null : String.format("fx:id=\"currentTimeZoneLabel\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        assert currentTimeZoneValue != null : String.format("fx:id=\"currentTimeZoneValue\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        assert conflictValidationLabel != null : String.format("fx:id=\"dateTimeValidationLabel\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        assert showConflictsHBox != null : String.format("fx:id=\"showConflictsHBox\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        assert locationLabel != null : String.format("fx:id=\"locationLabel\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        assert typeComboBox != null : String.format("fx:id=\"typeComboBox\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        assert locationTextArea != null : String.format("fx:id=\"locationTextArea\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        assert phoneTextField != null : String.format("fx:id=\"phoneTextField\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        assert implicitLocationLabel != null : String.format("fx:id=\"implicitLocationLabel\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        assert locationValidationLabel != null : String.format("fx:id=\"locationValidationLabel\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        assert urlLabel != null : String.format("fx:id=\"urlLabel\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        assert urlTextField != null : String.format("fx:id=\"urlTextField\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        assert urlValidationLabel != null : String.format("fx:id=\"urlValidationLabel\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        assert contactTextField != null : String.format("fx:id=\"contactTextField\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        assert contactValidationLabel != null : String.format("fx:id=\"contactValidationLabel\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        assert descriptionTextArea != null : String.format("fx:id=\"descriptionTextArea\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        
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
        
        // Initialize validation and control state bindings
        typeSelectionState = new TypeSelectionState();
        conflictLookupState = new ConflictLookupState();
        dateRangeValidation = new DateRangeValidation();
        valid = dateRangeValidation.isEmpty().and(conflictLookupState.conflictMessage.isEmpty());
        // Add listener to disable the "Save" button when the valid binding returns false.
        valid.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            saveChangesButton.setDisable(!newValue);
        });
    }
    
    /**
     * Adds a new appointment to the database.
     * @return
     *          The @{link model.db.AppointmentRow} object containing appointment that was added or @{code null} if no appointment was added.
     */
    public static AppointmentRow addNew() {
        return showAndWait(EditAppointment.class, 800, 600, (SetContentContext<EditAppointment> context) -> {
            EditAppointment controller = context.getController();
            setCollections(controller);
            controller.setModel(new AppointmentRow());
            context.getStage().setTitle(context.getResources().getString(RESOURCEKEY_ADDNEWAPPOINTMENT));
        }, (SetContentContext<EditAppointment> context) -> {
            EditAppointment controller = context.getController();
            return (controller.isCanceled()) ? null : controller.getModel();
        });
    }

    /**
     * Edits the specified appointment.
     * @param row
     *              The appointment to be edited.
     * @return
     *          {@code true} if the changes were saved; otherwise {@code false} if the changes were discarded.
     */
    public static boolean edit(AppointmentRow row) {
        return showAndWait(EditAppointment.class, 800, 600, (SetContentContext<EditAppointment> context) -> {
            EditAppointment controller = context.getController();
            setCollections(controller);
            controller.setModel(row);
            context.getStage().setTitle(context.getResources().getString(RESOURCEKEY_EDITAPPOINTMENT));
        }, (SetContentContext<EditAppointment> context) -> {
            return !context.getController().isCanceled();
        });
    }
    
    private static boolean setCollections(EditAppointment controller) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Loading data from database");
        alert.initStyle(StageStyle.UTILITY);
        alert.setTitle("Initializing");
        alert.show();
        // Open a new SQL connection dependency.
        SqlConnectionDependency dep;
        try {
            dep = new SqlConnectionDependency(true);
            try {
                controller.customers = FXCollections.observableArrayList(CustomerRow.getActive(dep.getconnection()));
                controller.users = FXCollections.observableArrayList(UserRow.getActive(dep.getconnection()));
            } finally {
                dep.close();
            }
            return false;
        } catch (SQLException ex) {
            Logger.getLogger(EditAppointment.class.getName()).log(Level.SEVERE, null, ex);
        } finally { alert.close(); }
        scheduler.Util.showErrorAlert("Database Error", "An unexpected error occurred while accessing the database. See logs for more information");
        return true;
    }

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Event handler methods">
    
    @FXML
    void addCustomerClick(ActionEvent event) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @FXML
    void addUserClick(ActionEvent event) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @FXML
    void showConflictsButtonClick(ActionEvent event) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected boolean saveChanges() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="State management classes">
    
    /**
     * Manages visibility and text of controls according to the selected appointment type.
     * The {@link #locationTextArea} control is made visible when the selected appointment type is for an explicitly defined location.
     * The {@link #phoneTextField} control is made visible when the selected appointment type is for a phone meeting.
     * The {@link #urlLabel} and {@link #urlTextField} controls are made visible when the selected appointment type is for a virtual (online) meeting.
     * The {@link #implicitLocationLabel} control is made visible when the selected appointment type indicates it is at the customer's location.
     * The {@link #locationLabel} control is made visible when either the {@link #locationTextArea}, {@link #phoneTextField} or {@link #implicitLocationLabel} are visible.
     */
    private class TypeSelectionState {
        /**
         * The currently selected appointment type from the {@link #typeComboBox} control.
         */
        final ObjectProperty<String> selectedTypeProperty;
        /**
         * The currently selected customer from the {@link #customerComboBox} control.
         */
        final ObjectProperty<CustomerRow> selectedCustomerProperty;
        /**
         * Indicates whether the selected appointment type is for an explicitly defined location ({@link #selectedTypeProperty} == {@link #APPOINTMENT_CODE_OTHER}).
         */
        final BooleanBinding explicitLocation;
        /**
         * Indicates whether the selected appointment type is for a phone meeting ({@link #selectedTypeProperty} == {@link #APPOINTMENT_CODE_PHONE}).
         */
        final BooleanBinding phone;
        /**
         * Indicates whether the selected appointment type is for an online (virtual) meeting ({@link #selectedTypeProperty} == {@link #APPOINTMENT_CODE_VIRTUAL}).
         */
        final BooleanBinding virtual;
        /**
         * The text for the "location" label or blank if the location label should not be displayed.
         */
        final StringBinding locationLabelText;
        /**
         * The text for the implicit location text or blank if the explicit location label should not be displayed.
         */
        final StringBinding implicitLocationText;
        
        TypeSelectionState() {
            // Get control properties to be bound to.
            selectedTypeProperty = typeComboBox.valueProperty();
            selectedCustomerProperty = customerComboBox.valueProperty();
            // Set up boolean bindings
            explicitLocation = selectedTypeProperty.isEqualTo(APPOINTMENT_CODE_OTHER);
            phone = selectedTypeProperty.isEqualTo(APPOINTMENT_CODE_PHONE);
            virtual = selectedTypeProperty.isEqualTo(APPOINTMENT_CODE_VIRTUAL);
            // Create binding for implicit location text (customer address).
            implicitLocationText = new StringBinding() {
                { super.bind(selectedTypeProperty, selectedCustomerProperty); }
                
                @Override
                protected String computeValue() {
                    // If appointment type is for an appointment at the customer's location, return the customer's address;
                    // otherwise, return an emtpty string to indicate that the implicit location text label should not be shown.
                    String t = selectedTypeProperty.get();
                    CustomerRow c = selectedCustomerProperty.get();
                    if (t.equals(APPOINTMENT_CODE_CUSTOMER) && c != null) {
                        model.Address a = c.getAddress();
                        if (a != null && !(t = a.toString().trim()).isEmpty())
                            return t;
                    }
                    return "";
                }
                @Override
                public ObservableList<?> getDependencies() { return FXCollections.observableArrayList(selectedTypeProperty, selectedCustomerProperty); }
                @Override
                public void dispose() { super.unbind(selectedTypeProperty, selectedCustomerProperty); }
            };
            // Create binding for location field label.
            locationLabelText = new StringBinding() {
                { super.bind(phone, explicitLocation, implicitLocationText); }
                @Override
                protected String computeValue() {
                    // If the location is the customer location or an explicit location, return "Location".
                    // Else, if the appointment type is for a phone meeting, return "Phone number";
                    // otherwise, return an emtpty string to indicate that the location field label should not be shown.
                    boolean e = explicitLocation.get();
                    String i = implicitLocationText.get();
                    return (phone.get()) ? getResources().getString(RESOURCEKEY_PHONENUMBER) : ((e || !i.isEmpty()) ? getResources().getString(RESOURCEKEY_LOCATION) : "");
                }
                @Override
                public ObservableList<?> getDependencies() { return FXCollections.observableArrayList(phone, explicitLocation, implicitLocationText); }
                @Override
                public void dispose() { super.unbind(phone, explicitLocation, implicitLocationText); }
            };
            // Create listener to update control state when the explicit location appointment type indicator has changed.
            explicitLocation.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) ->
                    explicitLocationChanged(newValue));
            // Create listener to update control state when the phone appointment type indicator has changed.
            phone.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) ->
                    phoneChanged(newValue));
            // Create listener to update control state when the virtual appointment type indicator has changed.
            virtual.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) ->
                    virtualChanged(newValue));
            // Create listener to update control state when the implicit location text has changed.
            implicitLocationText.addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) ->
                    implicitLocationTextChanged(newValue));
            // Create listener to update control state when the texst for the location field label has changed.
            locationLabelText.addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) ->
                    locationLabelTextChanged(newValue));
            // Call the methods that handle changes, to initialize all control states.
            explicitLocationChanged(explicitLocation.get());
            phoneChanged(phone.get());
            virtualChanged(virtual.get());
            implicitLocationTextChanged(implicitLocationText.get());
            locationLabelTextChanged(locationLabelText.get());
        }
        
        /**
         * Update the {@link #locationTextArea} control display status.
         * @param value
         *              If {@code true}, then the {@link #locationTextArea} control will be made visible; otherwise, it will be hidden and collapsed.
         */
        final void explicitLocationChanged(boolean value) {
            if (value)
                restoreControl(locationTextArea);
            else
                collapseControl(locationTextArea);
        }
        
        /**
         * Update the {@link #phoneTextField} control display status.
         * @param value
         *              If {@code true}, then the {@link #phoneTextField} control will be made visible; otherwise, it will be hidden and collapsed.
         */
        final void phoneChanged(boolean value) {
            if (value)
                restoreControl(phoneTextField);
            else
                collapseControl(phoneTextField);
            
        }
        
        /**
         * Updates the display status for the {@link #urlLabel} and {@link #urlTextField} controls.
         * @param value
         *              If {@code true}, then the {@link #urlLabel} and {@link #urlLabel} controls will be made visible; otherwise, they will be hidden and collapsed.
         */
        final void virtualChanged(boolean value) {
            if (value) {
                restoreControl(urlLabel);
                restoreControl(urlTextField);
            }
            else {
                collapseControl(urlLabel);
                collapseControl(urlTextField);
            }
        }
        
        /**
         * Update the text and/or display status for the {@link #implicitLocationLabel} control.
         * @param value
         *              If empty, then the {@link #implicitLocationLabel} control will be hidden and collapsed;
         *              otherwise, it will be made visible and its text updated accordingly.
         */
        final void implicitLocationTextChanged(String value) {
            if (value == null || value.trim().isEmpty())
                collapseControl(implicitLocationLabel);
            else
                restoreControl(implicitLocationLabel, value);
        }
        
        /**
         * Update the text and/or display status for the {@link #locationLabel} control.
         * @param value
         *              If empty, then the {@link #locationLabel} control will be hidden and collapsed;
         *              otherwise, it will be made visible and its text updated accordingly.
         */
        final void locationLabelTextChanged(String value) {
            if (value == null || value.trim().isEmpty())
                collapseControl(locationLabel);
            else
                restoreControl(locationLabel, value);
        }
    }
    
    /**
     * Manages visibility of the {@link #showConflictsHBox} control and the text of the {@link #conflictValidationLabel} control according to appointment schedule conflict results.
     * The text of the {@link #conflictValidationLabel} control is updated to contain a verbal explanation of the conflict counts.
     * The {@link #showConflictsHBox} control is made visible when there are one or more customer or user scheduling conflicts found.
     */
    private class ConflictLookupState {
        final SimpleIntegerProperty customerConflictCount;
        final SimpleIntegerProperty userConflictCount;
        final ObjectProperty<CustomerRow> selectedCustomerProperty;
        final ObjectProperty<UserRow> selectedUserProperty;
        final StringBinding conflictMessage;
        ConflictLookupState() {
            customerConflictCount = new SimpleIntegerProperty(0);
            userConflictCount = new SimpleIntegerProperty(0);
            selectedCustomerProperty = customerComboBox.valueProperty();
            selectedUserProperty = userComboBox.valueProperty();
            conflictMessage = new StringBinding() {
                { super.bind(customerConflictCount, userConflictCount);}
                @Override
                protected String computeValue() {
                    int c = customerConflictCount.get();
                    int u = userConflictCount.get();
                    if (u == 1) {
                        if (c == 1)
                            return getResources().getString(RESOURCEKEY_CONFLICTCUSTOMER1USERN);
                        return (c > 1) ? String.format(getResources().getString(RESOURCEKEY_CONFLICTCUSTOMERNUSER1), c) : getResources().getString(RESOURCEKEY_CONFLICTUSER1);
                    }
                    if (u > 1){
                        if (c == 1)
                            return String.format(getResources().getString(RESOURCEKEY_CONFLICTCUSTOMER1USERN), u);
                        return (c > 1) ? String.format(getResources().getString(RESOURCEKEY_CONFLICTCUSTOMERNUSERN), c, u) : String.format(getResources().getString(RESOURCEKEY_CONFLICTUSERN), u);
                    }
                    if (c == 1)
                        return getResources().getString(RESOURCEKEY_CONFLICTCUSTOMER1);
                    return (c > 0) ? String.format(getResources().getString(RESOURCEKEY_CONFLICTCUSTOMERN), c) : "";
                }
                @Override
                public ObservableList<?> getDependencies() { return FXCollections.observableArrayList(customerConflictCount, userConflictCount); }
                @Override
                public void dispose() { super.unbind(customerConflictCount, userConflictCount); }
            };
            dateRangeValidation.startValidation.selectedDateTime.addListener((observable) -> rangeChanged());
            dateRangeValidation.endValidation.selectedDateTime.addListener((observable) -> rangeChanged());
            selectedCustomerProperty.addListener((observable) -> customerConflictCount.set(0));
            selectedUserProperty.addListener((observable) -> userConflictCount.set(0));
        }
        void rangeChanged() {
            customerConflictCount.set(0);
            userConflictCount.set(0);
        }
        /**
         * This is invoked when the user clicks the "Save" button, to see if there are no customer or user scheduling conflicts.
         * @return
         *          {@code true} if selected date ranges are valid, a customer and user is selected, and there are no scheduling conflicts;
         *          otherwise, {@code false} to indicate that the "save" should be aborted.
         */
        boolean test() {
            LocalDateTime start = dateRangeValidation.startValidation.selectedDateTime.get();
            if (start != null) {
                LocalDateTime end = dateRangeValidation.endValidation.selectedDateTime.get();
                if (end != null && start.compareTo(end) <= 0) {
                    CustomerRow c = selectedCustomerProperty.get();
                    if (c != null) {
                        UserRow u = selectedUserProperty.get();
                        if (u != null) {
                            int cc = AppointmentRow.getCountByCustomer(c.getPrimaryKey(), start, end);
                            int uc = AppointmentRow.getCountByUser(u.getPrimaryKey(), start, end);
                            customerConflictCount.set(cc);
                            userConflictCount.set(uc);
                            return cc == 0 && uc == 0;
                        }
                    }
                }
            }
            customerConflictCount.set(0);
            userConflictCount.set(0);
            return false;
        }
    }
    
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Field Validation classes">
    
    /**
     * Produces the resource key of the validation message for a date/time selection or an empty string if the entire date and time has been selected.
     */
    private class DateValidation extends StringBinding {
        final ObjectProperty<LocalDate> dateProperty;
        final ObjectProperty<Integer> hourProperty;
        final ObjectProperty<Integer> minuteProperty;
        final ObjectProperty<TimeZone> timeZoneProperty;
        final ObjectBinding<ZonedDateTime> zonedDateTime;
        final ObjectBinding<LocalDateTime> selectedDateTime;
        DateValidation(DatePicker datePicker, ComboBox<Integer> hourComboBox, ComboBox<Integer> minuteComboBox) {
            dateProperty = datePicker.valueProperty();
            hourProperty = hourComboBox.valueProperty();
            minuteProperty = minuteComboBox.valueProperty();
            timeZoneProperty = timeZoneComboBox.valueProperty();
            zonedDateTime = new ObjectBinding<ZonedDateTime>() {
                { super.bind(dateProperty, hourProperty, minuteProperty, timeZoneProperty); }
                @Override
                protected ZonedDateTime computeValue() {
                    LocalDate d = dateProperty.get();
                    int h = hourProperty.get();
                    int m = minuteProperty.get();
                    TimeZone z = timeZoneProperty.get();
                    if (d == null || h < 0 || h > 23 || m < 0 || m > 59)
                        return null;
                    return ZonedDateTime.of(d, LocalTime.of(h, m, 0), ((z == null) ? TimeZone.getDefault() : z).toZoneId());
                }
                @Override
                public ObservableList<?> getDependencies() { return FXCollections.observableArrayList(dateProperty, hourProperty, minuteProperty, timeZoneProperty); }
                @Override
                public void dispose() { super.unbind(dateProperty, hourProperty, minuteProperty, timeZoneProperty); }
            };
            selectedDateTime = new ObjectBinding<LocalDateTime>() {
                { super.bind(zonedDateTime); }
                @Override
                protected LocalDateTime computeValue() {
                    ZonedDateTime d = zonedDateTime.get();
                    return (d == null) ? null : d.toLocalDateTime();
                }
                @Override
                public ObservableList<?> getDependencies() { return FXCollections.singletonObservableList(zonedDateTime); }
                @Override
                public void dispose() { super.unbind(zonedDateTime); }
            };
            super.bind(dateProperty, hourProperty, minuteProperty);
        }
        
        @Override
        protected String computeValue() {
            int h = hourProperty.get();
            int m = minuteProperty.get();
            if (dateProperty.get() == null)
                return RESOURCEKEY_REQUIRED;
            if (h < 0 || h > 23)
                return RESOURCEKEY_INVALIDHOUR;
            return (m < 0 || m > 59) ? RESOURCEKEY_INVALIDMINUTE : "";
        }
        @Override
        public ObservableList<?> getDependencies() { return FXCollections.observableArrayList(dateProperty, hourProperty, minuteProperty); }
        @Override
        public void dispose() { super.unbind(dateProperty, hourProperty, minuteProperty); }
    }
    
    /**
     * Produces the resource key of the validation message for the date range or an empty string if the start and end date range is valid.
     */
    private class DateRangeValidation extends StringBinding {
        // The resource key of the validation message for the start date.
        final DateValidation startValidation;
        // The resource key of the validation message for the end date.
        final DateValidation endValidation;
        
        DateRangeValidation() {
            startValidation = new DateValidation(startDatePicker, startHourComboBox, startMinuteComboBox);
            endValidation = new DateValidation(endDatePicker, endHourComboBox, endMinuteComboBox);
            super.bind(startValidation.selectedDateTime, endValidation.selectedDateTime, endValidation);
        }
        @Override
        protected String computeValue() {
            LocalDateTime s = startValidation.selectedDateTime.get();
            LocalDateTime e = endValidation.selectedDateTime.get();
            String m = endValidation.get();
            return (m.isEmpty() && s != null && s.compareTo(e) > 0) ? RESOURCEKEY_ENDCANNOTBEBEFORESTART : m;
        }
        @Override
        public ObservableList<?> getDependencies() { return FXCollections.observableArrayList(startValidation.selectedDateTime, endValidation.selectedDateTime, endValidation); }
        @Override
        public void dispose() { super.unbind(startValidation.selectedDateTime, endValidation.selectedDateTime, endValidation); }
    }
    
    //</editor-fold>
}