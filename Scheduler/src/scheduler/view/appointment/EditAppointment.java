package scheduler.view.appointment;

import java.sql.Connection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Optional;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.Observable;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import scheduler.dao.Address;
import scheduler.dao.AppointmentImpl;
import scheduler.dao.AppointmentFactory;
import scheduler.dao.CustomerFactory;
import scheduler.dao.UserFactory;
import scheduler.util.DbConnector;
import scheduler.util.Alerts;
import scheduler.util.DB;
import scheduler.view.EditItem;
import scheduler.view.ItemController;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.customer.CustomerModel;
import scheduler.view.user.UserModel;
import scheduler.view.address.CustomerAddress;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
@GlobalizationResource("scheduler/view/appointment/EditAppointment")
@FXMLResource("/scheduler/view/appointment/EditAppointment.fxml")
public class EditAppointment extends ItemController<AppointmentModel> {
    //<editor-fold defaultstate="collapsed" desc="Fields">
    
    //<editor-fold defaultstate="collapsed" desc="Constants">
    
    //<editor-fold defaultstate="collapsed" desc="Resource keys">

    public static final String RESOURCEKEY_ADDNEWAPPOINTMENT = "addNewAppointment";
//    public static final String RESOURCEKEY_CURRENTTIMEZONE = "currentTimeZone";
    public static final String RESOURCEKEY_CUSTOMER = "customer";
//    public static final String RESOURCEKEY_CUSTOMERNOTFOUND = "customerNotFound";
//    public static final String RESOURCEKEY_DESCRIPTION = "description";
    public static final String RESOURCEKEY_EDITAPPOINTMENT = "editAppointment";
//    public static final String RESOURCEKEY_END = "end";
    public static final String RESOURCEKEY_ENDCANNOTBEBEFORESTART = "endCannotBeBeforeStart";
    public static final String RESOURCEKEY_INVALIDURL = "invalidUrl";
    public static final String RESOURCEKEY_LOCATION = "location";
//    public static final String RESOURCEKEY_POINTOFCONTACT = "pointOfContact";
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
    public static final String RESOURCEKEY_APPOINTMENTCONFLICT = "appointmentConflict";
    public static final String RESOURCEKEY_SAVEANYWAY = "saveAnyway";

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
    private ComboBox<CustomerModel> customerComboBox;

    @FXML // User selection control.
    private ComboBox<UserModel> userComboBox;

    @FXML // Label for displaying customer selection validation message.
    private Label customerValidationLabel;

    @FXML // Label for displaying user selection validation message.
    private Text customerValidationText;
    
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
    private ComboBox<TimeZoneChoice> timeZoneComboBox;

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
    
    private static final Logger LOG = Logger.getLogger(EditAppointment.class.getName());
    
    //<editor-fold defaultstate="collapsed" desc="Observables">
    
    // Items for the customerComboBox control.
    private ObservableList<CustomerModel> customers;
    
    // Items for the userComboBox control.
    private ObservableList<UserModel> users;
    
    // Items for the startHourComboBox and endHourComboBox controls.
    private ObservableList<Integer> hourOptions;
    
    // Items for the startMinuteComboBox and endMinuteComboBox controls.
    private ObservableList<Integer> minuteOptions;
    
    // Items for the timeZoneComboBox control.
    private ObservableList<TimeZoneChoice> timeZones;
    
    // Items for the typeComboBox control.
    private ObservableList<String> types;
    
    // Manages visibility and text of controls according to the selected appointment type.
    private TypeSelectionState typeSelectionState;
    
    // Manages visibility of the {@link #showConflictsHBox} control and the text of the {@link #conflictValidationLabel} control according to appointment schedule conflict results.
    private ConflictLookupState conflictLookupState;

    // Produces the validation message for the phone and location fields.
    private LocationValidation locationValid;
    
    // Produces the validation message for the date range or an empty string if the start and end date range is valid.
    private DateRangeValidation dateRangeValidation;
    
    // Produces the validation message for the virtual meeting URL or an empty string if the virtual meeting URL is valid.
    private UrlValidation urlValidation;
    
    private SimpleRequirementValidation<CustomerModel> customerValid;
    private SimpleRequirementValidation<UserModel> userValid;
    private NonWhiteSpaceValidation titleValid;
    private NonWhiteSpaceValidation contactValid;
    
    // Aggregate binding to indicate whether all controls are valid.
    private BooleanBinding valid;
    private EditItem<AppointmentModel> parent;
    
    //</editor-fold>

    @Override
    public boolean isValid() { return valid.get(); }
    
    @Override
    public BooleanExpression validProperty() { return valid; }
    
    //</editor-fold>
    
    private int currentTimeZoneOffset;
    
    //<editor-fold defaultstate="collapsed" desc="Initialization">
    
    @FXML
    protected void initialize() {
        assert customerComboBox != null : String.format("fx:id=\"customerComboBox\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        assert userComboBox != null : String.format("fx:id=\"userComboBox\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        assert customerValidationText != null : String.format("fx:id=\"customerValidationText\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
//        assert customerValidationLabel != null : String.format("fx:id=\"customerValidationLabel\" was not injected: check your FXML file '%s'.",
//                getFXMLResourceName(getClass()));
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
        
        // Initialize options lists for start and end time combo boxes.
        hourOptions = FXCollections.observableArrayList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23);
        minuteOptions = FXCollections.observableArrayList(0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55);
        
        // Initialize options list for time zone combo box.
        timeZones = FXCollections.observableArrayList();
        TimeZoneChoice.getAllChoices(Locale.getDefault(Locale.Category.DISPLAY)).forEach((TimeZoneChoice c) -> timeZones.add(c));
        
        // Get appointment type options.
        types = FXCollections.observableArrayList(APPOINTMENT_CODE_PHONE, APPOINTMENT_CODE_VIRTUAL, APPOINTMENT_CODE_CUSTOMER,
                APPOINTMENT_CODE_HOME, APPOINTMENT_CODE_GERMANY, APPOINTMENT_CODE_INDIA, APPOINTMENT_CODE_HONDURAS,
                APPOINTMENT_CODE_OTHER);
        
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
        currentTimeZoneOffset = (TimeZone.getTimeZone(ZoneId.systemDefault())).getRawOffset();
        // Get the best match to initially select the time zone.
        String zId = ZoneId.systemDefault().getId();
        Optional<TimeZoneChoice> tz = timeZones.stream().filter((TimeZoneChoice t) -> t.getZoneId().getId().equals(zId)).findFirst();
        if (!tz.isPresent()) {
            String sn = ZoneId.systemDefault().getDisplayName(TextStyle.SHORT, java.util.Locale.ITALY);
            tz = timeZones.stream().filter((TimeZoneChoice t) -> t.getShortName().equals(sn)).findFirst();
            if (!tz.isPresent()) {
                tz = timeZones.stream().filter((TimeZoneChoice t) -> t.getTimeZone().getRawOffset() == currentTimeZoneOffset).findFirst();
            }
        }
        
        timeZoneComboBox.getSelectionModel().select((tz.isPresent()) ? tz.get() : timeZones.get(0));
        typeComboBox.setItems(types);
        typeComboBox.getSelectionModel().select(types.get(0));
        try (DbConnector dep = new DbConnector()) {
            Connection connection = dep.getConnection();
            customers = FXCollections.observableArrayList();
            (new CustomerFactory()).loadByStatus(connection, true).forEach((c) -> customers.add(new CustomerModel(c)));
            users = FXCollections.observableArrayList();
            (new UserFactory()).loadByStatus(connection, UserFactory.STATUS_INACTIVE, true).forEach((u) -> users.add(new UserModel(u)));
        } catch (Exception ex) {
            if (customers == null)
                customers = FXCollections.observableArrayList();
            if (users == null)
                users = FXCollections.observableArrayList();
            LOG.log(Level.SEVERE, null, ex);
        }
        customerComboBox.setItems(customers);
        userComboBox.setItems(users);
        // Initialize validation and control state bindings
        typeSelectionState = new TypeSelectionState();
        dateRangeValidation = new DateRangeValidation();
        conflictLookupState = new ConflictLookupState();
        locationValid = new LocationValidation();
        customerValid = new SimpleRequirementValidation<>(customerComboBox, customerValidationLabel);
        userValid = new SimpleRequirementValidation<>(userComboBox, userValidationLabel);
        titleValid = new NonWhiteSpaceValidation(titleTextField, titleValidationLabel);
        urlValidation = new UrlValidation();
        contactValid = new NonWhiteSpaceValidation(contactTextField, contactValidationLabel);
        valid = customerValid.and(locationValid).and(userValid).and(titleValid).and(contactValid).and(dateRangeValidation.isEmpty())
                .and(conflictLookupState.conflictMessage.isEmpty()).and(urlValidation.isEmpty());
    }
    
    //</editor-fold>

    @Override
    public void accept(EditItem<AppointmentModel> context) {
        parent = context;
//        context.getChildViewManager().setWindowTitle(getResources().getString((context.getTarget().isNewItem()) ? RESOURCEKEY_ADDNEWAPPOINTMENT : RESOURCEKEY_ENDCANNOTBEBEFORESTART));
    }

    @Override
    public Boolean apply(EditItem<AppointmentModel> context) {
        if (!conflictLookupState.test()) {
            String msg = conflictLookupState.conflictMessage.get();
            if (msg.isEmpty())
                return false;
            Optional<ButtonType> response = Alerts.showWarningAlert(getResources().getString(RESOURCEKEY_APPOINTMENTCONFLICT),
                    String.format("%s\n\n%s", msg, getResources().getString(RESOURCEKEY_SAVEANYWAY)), ButtonType.YES, ButtonType.NO);
            if (!response.isPresent() || response.get() != ButtonType.YES)
                return false;
        }
        try {
            try (DbConnector dep = new DbConnector()) {
                if (conflictLookupState.test(dep.getConnection())) {
                    updateModel(context.getTarget()).saveChanges(dep.getConnection());
                    return true;
                }
            }
            String msg = conflictLookupState.conflictMessage.get();
            if (msg.isEmpty())
                return false;
            Optional<ButtonType> response = Alerts.showWarningAlert(getResources().getString(RESOURCEKEY_APPOINTMENTCONFLICT),
                    String.format("%s\n\n%s", msg, getResources().getString(RESOURCEKEY_SAVEANYWAY)), ButtonType.YES, ButtonType.NO);
            if (!response.isPresent() || response.get() != ButtonType.YES)
                return false;
            try (DbConnector dep = new DbConnector()) {
                updateModel(context.getTarget()).saveChanges(dep.getConnection());
                return true;
            }
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    //<editor-fold defaultstate="collapsed" desc="Event handler methods">
    
    @FXML
    void addCustomerClick(ActionEvent event) {
//        CustomerModel customer = EditCustomer.addNew(getViewManager());
//        if (null == customer)
//            return;
//        customers.add(customer);
//        customerComboBox.getSelectionModel().select(customer);
    }

    @FXML
    void addUserClick(ActionEvent event) {
//        UserModel user = EditUser.addNew(getViewManager());
//        if (null == user)
//            return;
//        users.add(user);
//        userComboBox.getSelectionModel().select(user);
    }
    
    @FXML
    void showConflictsButtonClick(ActionEvent event) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private AppointmentModel updateModel(AppointmentModel model) {
        String type = typeComboBox.getValue();
        AppointmentImpl row = model.getDataObject();
        row.setContact(contactTextField.getText());
        row.setCustomer(customerComboBox.getValue().getDataObject());
        row.setDescription(descriptionTextArea.getText());
        row.setEnd(DB.toUtcTimestamp(dateRangeValidation.endValidation.selectedDateTime.get()));
        row.setStart(DB.toUtcTimestamp(dateRangeValidation.startValidation.selectedDateTime.get()));
        row.setTitle(titleTextField.getText());
        row.setType(type);
        row.setUser(userComboBox.getValue().getDataObject());
        if (type.equalsIgnoreCase(APPOINTMENT_CODE_OTHER))
            row.setLocation(locationTextArea.getText());
        else if (type.equalsIgnoreCase(APPOINTMENT_CODE_PHONE)) {
            row.setLocation(phoneTextField.getText());
        } else {
            row.setLocation("");
            if (type.equalsIgnoreCase(APPOINTMENT_CODE_VIRTUAL)) {
                row.setUrl(urlTextField.getText());
                model.refreshFromDAO();
                return model;
            }
        }
        row.setUrl("");
        model.refreshFromDAO();
        return model;
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
        final ObjectProperty<CustomerModel> selectedCustomerProperty;
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
                    CustomerModel c = selectedCustomerProperty.get();
                    if (t.equals(APPOINTMENT_CODE_CUSTOMER) && c != null) {
                        CustomerAddress<? extends Address> a = c.getAddress();
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
                restoreNode(locationTextArea);
            else
                collapseNode(locationTextArea);
        }
        
        /**
         * Update the {@link #phoneTextField} control display status.
         * @param value
         *              If {@code true}, then the {@link #phoneTextField} control will be made visible; otherwise, it will be hidden and collapsed.
         */
        final void phoneChanged(boolean value) {
            if (value)
                restoreNode(phoneTextField);
            else
                collapseNode(phoneTextField);
            
        }
        
        /**
         * Updates the display status for the {@link #urlLabel} and {@link #urlTextField} controls.
         * @param value
         *              If {@code true}, then the {@link #urlLabel} and {@link #urlLabel} controls will be made visible; otherwise, they will be hidden and collapsed.
         */
        final void virtualChanged(boolean value) {
            if (value) {
                restoreNode(urlLabel);
                restoreNode(urlTextField);
            }
            else {
                collapseNode(urlLabel);
                collapseNode(urlTextField);
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
                collapseNode(implicitLocationLabel);
            else
                restoreLabeled(implicitLocationLabel, value);
        }
        
        /**
         * Update the text and/or display status for the {@link #locationLabel} control.
         * @param value
         *              If empty, then the {@link #locationLabel} control will be hidden and collapsed;
         *              otherwise, it will be made visible and its text updated accordingly.
         */
        final void locationLabelTextChanged(String value) {
            if (value == null || value.trim().isEmpty())
                collapseNode(locationLabel);
            else
                restoreLabeled(locationLabel, value);
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
        final ObjectProperty<CustomerModel> selectedCustomerProperty;
        final ObjectProperty<UserModel> selectedUserProperty;
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
            dateRangeValidation.startValidation.selectedDateTime.addListener((Observable observable) -> rangeChanged());
            dateRangeValidation.endValidation.selectedDateTime.addListener((Observable observable) -> rangeChanged());
            selectedCustomerProperty.addListener((Observable observable) -> customerConflictCount.set(0));
            selectedUserProperty.addListener((Observable observable) -> userConflictCount.set(0));
            conflictMessage.addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                messageChanged(newValue);
            });
            messageChanged(conflictMessage.get());
        }
        final void messageChanged(String value) {
            if (value.isEmpty())
                collapseNode(showConflictsHBox);
            else {
                restoreNode(showConflictsHBox);
                conflictValidationLabel.setText(value);
            }
        }
        void rangeChanged() {
            customerConflictCount.set(0);
            userConflictCount.set(0);
        }
        
        @Deprecated
        boolean test() { throw new RuntimeException("This has been deprecated"); }
        
        /**
         * This is invoked when the user clicks the "Save" button, to see if there are no customer or user scheduling conflicts.
         * @return
         *          {@code true} if selected date ranges are valid, a customer and user is selected, and there are no scheduling conflicts;
         *          otherwise, {@code false} to indicate that the "save" should be aborted.
         */
        boolean test(Connection connection) throws Exception {
            LocalDateTime start = dateRangeValidation.startValidation.selectedDateTime.get();
            if (start != null) {
                LocalDateTime end = dateRangeValidation.endValidation.selectedDateTime.get();
                if (end != null && start.compareTo(end) <= 0) {
                    CustomerModel c = selectedCustomerProperty.get();
                    if (c != null) {
                        UserModel u = selectedUserProperty.get();
                        if (u != null) {
                            AppointmentFactory factory = new AppointmentFactory();
                            int cc = factory.countByCustomer(connection, c.getDataObject().getPrimaryKey(), start, end);
                            int uc = factory.countByUser(connection, u.getDataObject().getPrimaryKey(), start, end);
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
    
    private class SimpleRequirementValidation<T> extends BooleanBinding {
        final ObjectProperty<T> valueProperty;
        final Label validationLabel;
        SimpleRequirementValidation(ComboBox<T> target, Label validationLabel) {
            valueProperty = target.valueProperty();
            this.validationLabel = validationLabel;
            super.bind(valueProperty);
            super.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                onValidChanged(newValue);
            });
            onValidChanged(get());
        }
        private void onValidChanged(boolean value) {
            if (value)
                collapseNode(validationLabel);
            else
                restoreNode(validationLabel);
        }
        @Override
        protected boolean computeValue() { return valueProperty.get() != null; }
        @Override
        public ObservableList<?> getDependencies() { return FXCollections.singletonObservableList(valueProperty); }
        @Override
        public void dispose() { super.unbind(valueProperty); }
    }
    
    private class NonWhiteSpaceValidation extends BooleanBinding {
        final StringProperty fieldTextProperty;
        final Label validationLabel;
        NonWhiteSpaceValidation(TextField target, Label validationLabel) {
            fieldTextProperty = target.textProperty();
            this.validationLabel = validationLabel;
            super.bind(fieldTextProperty);
            super.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                onValidChanged(newValue);
            });
            onValidChanged(get());
        }
        private void onValidChanged(boolean value) {
            if (value)
                collapseNode(validationLabel);
            else
                restoreNode(validationLabel);
        }
        @Override
        protected boolean computeValue() {
            return !fieldTextProperty.get().trim().isEmpty();
        }
        @Override
        public ObservableList<?> getDependencies() { return FXCollections.singletonObservableList(fieldTextProperty); }
        @Override
        public void dispose() { super.unbind(fieldTextProperty); }
    }
    
    private class LocationValidation extends BooleanBinding {
        private final StringProperty locationProperty;
        private final StringProperty phoneProperty;
        LocationValidation() {
            this.locationProperty = locationTextArea.textProperty();
            this.phoneProperty = phoneTextField.textProperty();
            super.bind(locationProperty, phoneProperty, typeSelectionState.explicitLocation, typeSelectionState.phone);
            super.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                if (newValue)
                    collapseNode(locationValidationLabel);
                else
                    restoreLabeled(locationValidationLabel, getResources().getString(EditItem.RESOURCEKEY_REQUIRED));
            });
        }
        
        @Override
        protected boolean computeValue() {
            String l = locationProperty.get();
            String p = phoneProperty.get();
            boolean e = typeSelectionState.explicitLocation.get();
            if (typeSelectionState.phone.get())
                return !p.trim().isEmpty();
            return !(e && l.trim().isEmpty());
        }
        
        @Override
        public ObservableList<?> getDependencies() { return FXCollections.observableArrayList(locationProperty, phoneProperty, typeSelectionState.explicitLocation, typeSelectionState.phone); }
        @Override
        public void dispose() { super.unbind(locationProperty, phoneProperty, typeSelectionState.explicitLocation, typeSelectionState.phone); }
    }
    
    /**
     * Produces the resource key of the validation message for a date/time selection or an empty string if the entire date and time has been selected.
     */
    private class DateValidation extends StringBinding {
        final ObjectProperty<LocalDate> dateProperty;
        final ObjectProperty<Integer> hourProperty;
        final ObjectProperty<Integer> minuteProperty;
        final ObjectProperty<TimeZoneChoice> timeZoneProperty;
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
                    TimeZoneChoice z = timeZoneProperty.get();
                    if (d == null || h < 0 || h > 23 || m < 0 || m > 59)
                        return null;
                    return ZonedDateTime.of(d, LocalTime.of(h, m, 0), ((z == null) ? timeZones.get(0) : z).getZoneId());
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
                return EditItem.RESOURCEKEY_REQUIRED;
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
     * Validation binding for start and end date/time.
     * This validates the selections for the {@link #endDatePicker}, {@link #endHourComboBox}, {@link #endMinuteComboBox},
     * {@link #endDatePicker}, {@link #endHourComboBox}, and {@link #endMinuteComboBox}controls,
     * producing the resource key of the validation message for the date range or an empty string if the start/end
     * date/time range is valid.
     */
    private class DateRangeValidation extends StringBinding {
        /**
         * Validation binding for the range start date/time.
         * This validates the selections for the {@link #startDatePicker}, {@link #startHourComboBox},
         * and {@link #startMinuteComboBox} controls, producing the resource key of the validation message
         * for the start date/time or an empty string if all start date/time selections are valid.
         */
        final DateValidation startValidation;
        
        /**
         * Validation binding for the range end date/time.
         * This validates the selections for the {@link #endDatePicker}, {@link #endHourComboBox},
         * and {@link #endMinuteComboBox} controls, producing the resource key of the validation message
         * for the start date/time or an empty string if all start date/time selections are valid.
         */
        final DateValidation endValidation;
        
        DateRangeValidation() {
            startValidation = new DateValidation(startDatePicker, startHourComboBox, startMinuteComboBox);
            endValidation = new DateValidation(endDatePicker, endHourComboBox, endMinuteComboBox);
            super.bind(startValidation.selectedDateTime, endValidation.selectedDateTime, endValidation);
            startValidation.addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                startValidationMessageChanged(newValue);
            });
            super.addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                endValidationMessageChanged(newValue);
            });
            startValidationMessageChanged(startValidation.get());
            endValidationMessageChanged(get());
        }
        final void startValidationMessageChanged(String value) {
            if (value.isEmpty())
                collapseNode(startValidationLabel);
            else
                restoreLabeled(startValidationLabel, getResources().getString(value));
        }
        final void endValidationMessageChanged(String value) {
            if (value.isEmpty())
                collapseNode(endValidationLabel);
            else
                restoreLabeled(endValidationLabel, getResources().getString(value));
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
    
    private class UrlValidation extends StringBinding {
        final StringProperty urlTextProperty;
        UrlValidation() {
            urlTextProperty = urlTextField.textProperty();
            super.bind(urlTextProperty, typeSelectionState.virtual);
            super.addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                if (newValue.isEmpty())
                    collapseNode(urlValidationLabel);
                else
                    restoreLabeled(urlValidationLabel, getResources().getString(newValue));
            });
        }
        @Override
        protected String computeValue() {
            String text = urlTextProperty.get();
            if (typeSelectionState.virtual.get()) {
                if (text.trim().isEmpty())
                    return EditItem.RESOURCEKEY_REQUIRED;
                URL url;
                try {
                    url = new URL(text);
                    if (url.getHost() == null || url.getHost().trim().isEmpty())
                        return RESOURCEKEY_INVALIDURL;
                } catch (MalformedURLException ex) {
                    return RESOURCEKEY_INVALIDURL;
                }
            }
            
            return "";
        }
        @Override
        public ObservableList<?> getDependencies() { return FXCollections.observableArrayList(urlTextProperty, typeSelectionState.virtual); }
        @Override
        public void dispose() { super.unbind(urlTextProperty, typeSelectionState.virtual); }
    }
    
    //</editor-fold>
}