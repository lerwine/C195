package scene.appointment;

import scene.ItemController;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.StageStyle;
import model.db.AppointmentRow;
import model.db.CustomerRow;
import model.db.UserRow;
import scene.annotations.FXMLResource;
import scene.annotations.GlobalizationResource;
import scheduler.SqlConnectionDependency;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
@GlobalizationResource("scene/appointment/EditAppointment")
@FXMLResource("/scene/appointment/EditAppointment.fxml")
public class EditAppointment extends ItemController<AppointmentRow> {
    //<editor-fold defaultstate="collapsed" desc="Fields">
    
    //<editor-fold defaultstate="collapsed" desc="Constants">
    
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
    
    private DateValidation dateRangeValid;
    
    private LocationAndPhoneValidator locationAndPhoneValid;
    
    private UrlValidator urlValid;
    
    private BooleanBinding valid;
    
    //</editor-fold>
    
    @Override
    public boolean isValid() { return valid.get(); }
    
    @Override
    public BooleanExpression validProperty() { return valid; }
    
    ResourceBundle currentResourceBundle;
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Initialization">
    
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
        startDateTime = scheduler.Util.asLocalDateTime(startDatePicker.valueProperty(),
            startHourComboBox.valueProperty(), startMinuteComboBox.valueProperty());
        endDateTime = scheduler.Util.asLocalDateTime(endDatePicker.valueProperty(),
            endHourComboBox.valueProperty(), endMinuteComboBox.valueProperty());
        
        // Initialize validation bindings
        typeBindings = new TypeBindings();
        customerValid = customerComboBox.valueProperty().isNotNull();
        customerValid.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (newValue)
                scheduler.Util.collapseControlVertical(customerValidationLabel);
            else
                scheduler.Util.restoreControlVertical(customerValidationLabel);
        });
        userValid = userComboBox.valueProperty().isNotNull();
        userValid.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (newValue)
                scheduler.Util.collapseControlVertical(userValidationLabel);
            else
                scheduler.Util.restoreControlVertical(userValidationLabel);
        });
        titleValid = scheduler.Util.notNullOrWhiteSpace(titleTextField.textProperty());
        titleValid.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (newValue)
                scheduler.Util.collapseControlVertical(titleValidationLabel);
            else
                scheduler.Util.restoreControlVertical(titleValidationLabel);
        });
        contactValid = scheduler.Util.notNullOrWhiteSpace(contactTextField.textProperty());
        contactValid.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (newValue)
                scheduler.Util.collapseControlVertical(contactValidationLabel);
            else
                scheduler.Util.restoreControlVertical(contactValidationLabel);
        });
        dateRangeValid = new DateValidation();
        locationAndPhoneValid = new LocationAndPhoneValidator();
        urlValid = new UrlValidator();
        valid = typeBindings.valid.and(customerValid).and(userValid).and(titleValid).and(contactValid).and(dateRangeValid).and(locationAndPhoneValid).and(urlValid);
    }
    
    public static AppointmentRow addNew() {
        return showAndWait(EditAppointment.class, 800, 600, (SetContentContext<EditAppointment> context) -> {
            EditAppointment controller = context.getController();
            setCollections(controller);
            controller.setModel(new AppointmentRow());
            context.getStage().setTitle(context.getResourceBundle().getString("addNewAppointment"));
        }, (SetContentContext<EditAppointment> context) -> {
            EditAppointment controller = context.getController();
            return (controller.isCanceled()) ? null : controller.getModel();
        });
    }

    public static boolean edit(AppointmentRow row) {
        return showAndWait(EditAppointment.class, 800, 600, (SetContentContext<EditAppointment> context) -> {
            EditAppointment controller = context.getController();
            setCollections(controller);
            controller.setModel(row);
            context.getStage().setTitle(context.getResourceBundle().getString("editAppointment"));
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

    @Override
    protected boolean saveChanges() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    
    //<editor-fold defaultstate="collapsed" desc="Validators">
    
    /**
     *
     */
    private class TypeBindings {
        private final ObjectProperty<String> selectedItem;
        private final BooleanBinding phone;
        private final BooleanBinding virtual;
        private final BooleanBinding explicitPhysicalLocation;
        private final BooleanBinding implicitPhysicalLocation;
        private final BooleanBinding valid;
        
        TypeBindings() {
            selectedItem = typeComboBox.valueProperty();
            phone = selectedItem.isEqualTo(APPOINTMENT_CODE_PHONE);
            virtual = selectedItem.isEqualTo(APPOINTMENT_CODE_VIRTUAL);
            explicitPhysicalLocation = selectedItem.isEqualTo(APPOINTMENT_CODE_OTHER);
            implicitPhysicalLocation = selectedItem.isEqualTo(APPOINTMENT_CODE_HOME)
                    .or(selectedItem.isEqualTo(APPOINTMENT_CODE_GERMANY))
                    .or(selectedItem.isEqualTo(APPOINTMENT_CODE_INDIA)).or(selectedItem.isEqualTo(APPOINTMENT_CODE_HONDURAS));
            valid = phone.or(virtual).or(explicitPhysicalLocation).or(implicitPhysicalLocation);
        }
    }
    
    
    private class DateValidation extends BooleanBinding {
        final ObjectProperty<CustomerRow> selectedCustomer;
        final ObjectProperty<UserRow> selectedUser;
        final StringBinding endValidationMessage;
        final StringBinding scheduleConflictMessage;
        final BooleanBinding startValid;
        DateValidation() {
            selectedCustomer = customerComboBox.valueProperty();
            selectedUser = userComboBox.valueProperty();
            startValid = startDateTime.isNotNull();
            endValidationMessage = new StringBinding() {
                { super.bind(startDateTime, endDateTime); }
                
                @Override
                protected String computeValue() {
                    LocalDateTime s = startDateTime.get();
                    LocalDateTime e = endDateTime.get();
                    if (e == null)
                        return "required";
                    return (s != null && s.compareTo(e) > 0) ? "endCannotBeBeforeStart" : "";
                }
                
                @Override
                public ObservableList<?> getDependencies() { return FXCollections.observableArrayList(startDateTime, endDateTime); }

                @Override
                public void dispose() { super.unbind(startDateTime, endDateTime); }
            };
            scheduleConflictMessage = new StringBinding() {
                { super.bind(startDateTime, endDateTime, selectedCustomer, selectedUser); }
                
                @Override
                protected String computeValue() {
                    LocalDateTime s = startDateTime.get();
                    LocalDateTime e = endDateTime.get();
                    CustomerRow c = selectedCustomer.get();
                    UserRow u = selectedUser.get();
                    if (s == null || e == null || s.compareTo(e) > 0)
                        return "";
                    boolean customerConflict;
                    boolean userConflict;
                    if (c != null) {
                        final int id = c.getPrimaryKey();
                        customerConflict = currentAndFuture.stream().anyMatch((AppointmentRow r) -> r.getCustomerId() == id && r.getStart().compareTo(e) <= 0 && r.getEnd().compareTo(s) >= 0);
                    } else
                        customerConflict = false;
                    if (u != null) {
                        final int id = u.getPrimaryKey();
                        userConflict = currentAndFuture.stream().anyMatch((AppointmentRow r) -> r.getUserId() == id && r.getStart().compareTo(e) <= 0 && r.getEnd().compareTo(s) >= 0);
                    } else
                        userConflict = false;
                    if (customerConflict)
                        return (userConflict) ? "conflictsWithBoth" : "conflictsWithCustomer";
                    return (userConflict) ? "conflictsWithUser" : "";
                }
                
                @Override
                public ObservableList<?> getDependencies() { return FXCollections.observableArrayList(startDateTime, endDateTime, selectedCustomer, selectedUser); }

                @Override
                public void dispose() { super.unbind(startDateTime, endDateTime, selectedCustomer, selectedUser); }
            };
            super.bind(startValid, endValidationMessage);
            startValid.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                startValidChanged(newValue);
            });
            endValidationMessage.addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                endMessageChanged(newValue);
            });
            scheduleConflictMessage.addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                conflictMessageChanged(newValue);
            });
            startValidChanged(startValid.get());
            endMessageChanged(endValidationMessage.get());
            conflictMessageChanged(scheduleConflictMessage.get());
        }

        private void startValidChanged(Boolean newValue) {
            if (newValue)
                scheduler.Util.collapseControlVertical(startValidationLabel);
            else
                scheduler.Util.restoreControlVertical(startValidationLabel);
        }
        
        private void endMessageChanged(String newValue) {
            if (newValue.isEmpty())
                scheduler.Util.collapseLabeledVertical(endValidationLabel);
            else
                scheduler.Util.restoreLabeledVertical(endValidationLabel, currentResourceBundle.getString(newValue));
        }

        private void conflictMessageChanged(String newValue) {
            if (newValue.isEmpty())
                scheduler.Util.collapseLabeledVertical(dateTimeValidationLabel);
            else
                scheduler.Util.restoreLabeledVertical(dateTimeValidationLabel, currentResourceBundle.getString(newValue));
        }

        @Override
        protected boolean computeValue() {
            String s = endValidationMessage.get();
            return startValid.get() && s.isEmpty();
        }
                
        @Override
        public ObservableList<?> getDependencies() { return FXCollections.observableArrayList(startValid, endValidationMessage); }

        @Override
        public void dispose() { super.unbind(startValid, endValidationMessage); }
    }
    
    private class LocationAndPhoneValidator extends BooleanBinding {
        final StringProperty locationProperty;
        final StringProperty phoneProperty;
        final BooleanBinding locationValid;
        final BooleanBinding phoneValid;
        final StringBinding locationLabelText;
        LocationAndPhoneValidator() {
            locationProperty = locationTextArea.textProperty();
            phoneProperty = phoneTextField.textProperty();
            locationValid = typeBindings.explicitPhysicalLocation.not().or(scheduler.Util.notNullOrWhiteSpace(locationProperty));
            phoneValid = typeBindings.phone.not().or(scheduler.Util.notNullOrWhiteSpace(phoneProperty));
            locationLabelText = new StringBinding() {
                { super.bind(typeBindings.explicitPhysicalLocation, typeBindings.phone); }
                
                @Override
                protected String computeValue() {
                    boolean explicitPhysicalLocation = typeBindings.explicitPhysicalLocation.get();
                    boolean phone = typeBindings.phone.get();
                    return (explicitPhysicalLocation) ? "location" : ((phone) ? "phoneNumber" : "");
                }
                
                @Override
                public ObservableList<?> getDependencies() { return FXCollections.observableArrayList(typeBindings.explicitPhysicalLocation, typeBindings.phone); }

                @Override
                public void dispose() { super.unbind(typeBindings.explicitPhysicalLocation, typeBindings.phone); }

            };
            super.bind(locationValid, phoneValid);
            super.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                isValidChanged(newValue);
            });
            typeBindings.explicitPhysicalLocation.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                explicitLocationChanged(newValue);
            });
            typeBindings.phone.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                phoneTypeChanged(newValue);
            });
            locationLabelText.addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                locationLabelChanged(newValue);
            });
            locationLabelChanged(locationLabelText.get());
            explicitLocationChanged(typeBindings.explicitPhysicalLocation.get());
            phoneTypeChanged(typeBindings.phone.get());
            isValidChanged(get());
        }

        private void locationLabelChanged(String newValue) {
            if (newValue.isEmpty())
                scheduler.Util.collapseLabeledVertical(locationLabel);
            else
                scheduler.Util.restoreLabeledVertical(locationLabel, currentResourceBundle.getString(newValue));
        }

        private void phoneTypeChanged(Boolean newValue) {
            if (newValue)
                scheduler.Util.restoreControlVertical(phoneTextField);
            else
                scheduler.Util.collapseControlVertical(phoneTextField);
        }

        private void explicitLocationChanged(Boolean newValue) {
            if (newValue)
                scheduler.Util.restoreControlVertical(locationTextArea);
            else
                scheduler.Util.collapseControlVertical(locationTextArea);
        }

        private void isValidChanged(Boolean newValue) {
            if (newValue)
                scheduler.Util.collapseControlVertical(locationValidationLabel);
            else
                scheduler.Util.restoreControlVertical(locationValidationLabel);
        }

        @Override
        protected boolean computeValue() {
            boolean isLocationValid = locationValid.get();
            boolean isPhoneValid = phoneValid.get();
            return isLocationValid && isPhoneValid;
        }
                
        @Override
        public ObservableList<?> getDependencies() { return FXCollections.observableArrayList(locationValid, phoneValid); }

        @Override
        public void dispose() { super.unbind(locationValid, phoneValid); }
    }
    
    /**
     *
     */
    private class UrlValidator extends BooleanBinding {
        StringProperty urlProperty;
        BooleanBinding urlNotEmpty;
        UrlValidator()
        {
            urlProperty = urlTextField.textProperty();
            urlNotEmpty = scheduler.Util.notNullOrWhiteSpace(urlProperty);
            super.bind(urlProperty, typeBindings.virtual);
            StringBinding messageBinding = new StringBinding() {
                { super.bind(urlValid, urlNotEmpty); }
                
                @Override
                protected String computeValue() {
                    boolean notEmpty = urlNotEmpty.get();
                    return (urlValid.get()) ? ((notEmpty) ? "" : "required") : "invalidUrl";
                }
                
                @Override
                public ObservableList<?> getDependencies() { return FXCollections.observableArrayList(urlValid, urlNotEmpty); }

                @Override
                public void dispose() { super.unbind(urlValid, urlNotEmpty); }

            };
            messageBinding.addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                if (newValue.isEmpty())
                    scheduler.Util.collapseLabeledVertical(urlValidationLabel);
                else
                    scheduler.Util.restoreLabeledVertical(urlValidationLabel, newValue);
            });
            typeBindings.virtual.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                if (newValue) {
                    scheduler.Util.restoreControlVertical(urlTextField);
                    scheduler.Util.restoreControlVertical(urlLabel);
                } else {
                    scheduler.Util.collapseControlVertical(urlLabel);
                    scheduler.Util.collapseControlVertical(urlTextField);
                }
            });
        }
        
        @Override
        protected boolean computeValue() {
            boolean virtual = typeBindings.virtual.get();
            String text = urlProperty.get();
            if (!virtual)
                return true;
            if (text == null || text.trim().isEmpty())
                return false;
            try {
                URL url = new URL(text);
                if (url.getHost() != null && !url.getHost().trim().isEmpty())
                    return true;
            } catch (MalformedURLException ex) {
                Logger.getLogger(EditAppointment.class.getName()).log(Level.SEVERE, null, ex);
            }
            return false;
        }
        
        @Override
        public ObservableList<?> getDependencies() { return FXCollections.observableArrayList(urlProperty, typeBindings.virtual); }
        
        @Override
        public void dispose() { super.unbind(urlProperty, typeBindings.virtual); }
    }
    
    //</editor-fold>
    
    //</editor-fold>
}