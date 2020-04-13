package scheduler.view.appointment;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableObjectValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import scheduler.AppResources;
import scheduler.Scheduler;
import scheduler.dao.AppointmentDAO;
import scheduler.dao.AppointmentType;
import scheduler.dao.CustomerDAO;
import scheduler.dao.CustomerElement;
import scheduler.dao.UserDAO;
import scheduler.dao.UserElement;
import scheduler.dao.UserStatus;
import scheduler.dao.filter.AppointmentFilter;
import scheduler.dao.filter.ComparisonOperator;
import scheduler.dao.filter.UserFilter;
import scheduler.util.AlertHelper;
import static scheduler.util.NodeUtil.bindCssCollapse;
import scheduler.view.EditItem;
import scheduler.view.MainController;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.FxmlViewEventHandling;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.annotations.HandlesFxmlViewEvent;
import static scheduler.view.appointment.EditAppointmentResourceKeys.*;
import scheduler.view.customer.CustomerModel;
import scheduler.view.customer.CustomerModelImpl;
import scheduler.view.event.FxmlViewEvent;
import scheduler.view.model.ItemModel;
import scheduler.view.task.TaskWaiter;
import scheduler.view.user.UserModel;
import scheduler.view.user.UserModelImpl;

/**
 * FXML Controller class for editing an {@link AppointmentModel}.
 * <p>
 * The associated view is {@code /resources/scheduler/view/appointment/EditAppointment.fxml}.</p>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/view/appointment/EditAppointment")
@FXMLResource("/scheduler/view/appointment/EditAppointment.fxml")
public final class EditAppointment extends EditItem.EditController<AppointmentDAO, AppointmentModel> {

    private static final Logger LOG = Logger.getLogger(EditAppointment.class.getName());

    public static AppointmentModel editNew(MainController mainController, Stage stage) throws IOException {
        return editNew(EditAppointment.class, mainController, stage);
    }

    public static AppointmentModel edit(AppointmentModel model, MainController mainController, Stage stage) throws IOException {
        return edit(model, EditAppointment.class, mainController, stage);
    }

    @FXML // Label for displaying customer selection validation message.
    private Label customerValidationLabel;

    @FXML // Label for displaying user selection validation message.
    private Label userValidationLabel;

    @FXML // CustomerDAO selection control
    private ComboBox<CustomerModelImpl> customerComboBox;

    @FXML // UserDAO selection control.
    private ComboBox<UserModelImpl> userComboBox;

    @FXML // Label for displaying appointment title validation message.
    private Label titleValidationLabel;

    @FXML // Control for the appointment title.
    private TextField titleTextField;

    @FXML // Label for displaying appointment start date validation message.
    private Label startValidationLabel;

    @FXML // fx:id="showConflictsButton"
    private Button showConflictsButton; // Value injected by FXMLLoader

    @FXML // Control for selecting the appointment start date.
    private DatePicker startDatePicker;

    @FXML // Control for selecting the appointment start hour.
    private Spinner<Integer> startHourSpinner;

    @FXML // Control for selecting the appointment start minute.
    private Spinner<Integer> startMinuteSpinner;

    @FXML // Control for selecting the appointment start minute.
    private Spinner<Boolean> amPmSpinner;

    @FXML // Label for displaying appointment duration validation message.
    private Label durationValidationLabel;

    @FXML // Control for selecting the appointment duration (end time).
    private Spinner<Integer> durationHourSpinner;

    @FXML // Control for selecting the appointment duration (end time).
    private Spinner<Integer> durationMinuteSpinner;

    @FXML // Control for selecting the time zone for the appointment start and end.
    private ComboBox<TimeZone> timeZoneComboBox;

    @FXML // Field label that gets hidden when the user selects the default time zone.
    private Label currentTimeZoneLabel;

    @FXML // Label for displaying the selected time, converted to the default time zone.
    private Label currentTimeZoneValue;

    @FXML // Field label for phone number control as well as explicit and implicit location.
    private Label locationLabel;

    @FXML // Label for displaying phone or explicit location validation message.
    private Label locationValidationLabel;

    @FXML // Explicit location input control.
    private TextArea locationTextArea;

    @FXML // Phone number input control.
    private TextField phoneTextField;

    @FXML // Label to contain the implicit location (CustomerDAO's address, etc).
    private Label implicitLocationLabel;

    @FXML // AppointmentDAO type selection control.
    private ComboBox<AppointmentType> typeComboBox;

    @FXML // Label for displaying point-of-contact validation message.
    private Label contactValidationLabel;

    @FXML // Point-of-Contact input control.
    private TextField contactTextField;

    @FXML // Field label for the Meeting URL control.
    private Label urlLabel;

    @FXML // Label for displaying URL validation message.
    private Label urlValidationLabel;

    @FXML // fx:id="urlTextField"
    private TextField urlTextField; // Value injected by FXMLLoader

    @FXML // AppointmentDAO description input control.
    private TextArea descriptionTextArea;

    @FXML // fx:id="conflictsBorderPane"
    private BorderPane conflictsBorderPane; // Value injected by FXMLLoader

    @FXML // fx:id="conflictingAppointmentsTableView"
    private TableView<AppointmentModel> conflictingAppointmentsTableView; // Value injected by FXMLLoader

    @FXML // fx:id="dropdownOptionsBorderPane"
    private BorderPane dropdownOptionsBorderPane; // Value injected by FXMLLoader

    @FXML // fx:id="dropdownOptionsInactiveRadioButton"
    private RadioButton dropdownOptionsInactiveRadioButton; // Value injected by FXMLLoader

    @FXML // fx:id="dropdownOptions"
    private ToggleGroup dropdownOptions; // Value injected by FXMLLoader

    @FXML // fx:id="dropdownOptionsLabel"
    private Label dropdownOptionsLabel; // Value injected by FXMLLoader

    @FXML // fx:id="dropdownOptionsActiveRadioButton"
    private RadioButton dropdownOptionsActiveRadioButton; // Value injected by FXMLLoader

    @FXML // fx:id="dropdownOptionsAllRadioButton"
    private RadioButton dropdownOptionsAllRadioButton; // Value injected by FXMLLoader

    // Items for the customerComboBox control.
    private ObservableList<CustomerModelImpl> customerModelList;

    // Items for the userComboBox control.
    private ObservableList<UserModelImpl> userModelList;

    // Items for the typeComboBox control.
    private ObservableList<AppointmentType> types;

    private ObservableList<AppointmentModel> otherAppointments;

    // Items for the conflictingAppointmentsTableView control.
    private ObservableList<AppointmentModel> conflictingAppointments;

    private SingleSelectionModel<AppointmentType> typeSelectionModel;
    private SingleSelectionModel<CustomerModelImpl> customerSelectionModel;
    private SingleSelectionModel<UserModelImpl> userSelectionModel;
    private StartDateTimeProperty startDateTime;
    private DurationProperty duration;
    private DateTimeFormatter formatter;
    private ReadOnlyStringWrapper customerAddress;
    private Optional<Boolean> showActiveCustomers;
    private Optional<Boolean> showActiveUsers;
    private boolean editingUserOptions;

    @FXML
    void closeConflictsBorderPaneButtonClick(ActionEvent event) {
        conflictsBorderPane.setVisible(false);
    }

    @FXML
    void showConflictsButtonClick(ActionEvent event) {
        conflictsBorderPane.setVisible(true);
    }

    @FXML
    void customerDropDownOptionsButtonClick(ActionEvent event) {
        editingUserOptions = false;
        if (showActiveCustomers.isPresent()) {
            dropdownOptions.selectToggle((showActiveCustomers.get()) ? dropdownOptionsActiveRadioButton : dropdownOptionsInactiveRadioButton);
        } else {
            dropdownOptions.selectToggle(dropdownOptionsAllRadioButton);
        }
        dropdownOptionsLabel.setText("Customers to Show");
        dropdownOptionsBorderPane.setVisible(true);
    }

    @FXML
    void userDropDownOptionsButtonClick(ActionEvent event) {
        editingUserOptions = true;
        if (showActiveUsers.isPresent()) {
            dropdownOptions.selectToggle((showActiveUsers.get()) ? dropdownOptionsActiveRadioButton : dropdownOptionsInactiveRadioButton);
        } else {
            dropdownOptions.selectToggle(dropdownOptionsAllRadioButton);
        }
        dropdownOptionsLabel.setText("Users to Show");
        dropdownOptionsBorderPane.setVisible(true);
    }

    @FXML
    void dropdownOptionsCancelButtonClick(ActionEvent event) {
        dropdownOptionsBorderPane.setVisible(false);
    }

    @FXML
    void dropdownOptionsOkButtonClick(ActionEvent event) {
        if (editingUserOptions) {
            TaskWaiter.startNow(new UserReloadTask((Stage) ((Button) event.getSource()).getScene().getWindow()));
        } else {
            TaskWaiter.startNow(new CustomerReloadTask((Stage) ((Button) event.getSource()).getScene().getWindow()));
        }
        dropdownOptionsBorderPane.setVisible(false);
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    protected void initialize() {
        assert customerValidationLabel != null : "fx:id=\"customerValidationLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert userValidationLabel != null : "fx:id=\"userValidationLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert customerComboBox != null : "fx:id=\"customerComboBox\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert userComboBox != null : "fx:id=\"userComboBox\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert titleValidationLabel != null : "fx:id=\"titleValidationLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert titleTextField != null : "fx:id=\"titleTextField\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert startValidationLabel != null : "fx:id=\"startValidationLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert showConflictsButton != null : "fx:id=\"showConflictsButton\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert startDatePicker != null : "fx:id=\"startDatePicker\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert startHourSpinner != null : "fx:id=\"startHourSpinner\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert startMinuteSpinner != null : "fx:id=\"startMinuteSpinner\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert amPmSpinner != null : "fx:id=\"amPmSpinner\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert durationValidationLabel != null : "fx:id=\"durationValidationLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert durationHourSpinner != null : "fx:id=\"durationHourSpinner\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert durationMinuteSpinner != null : "fx:id=\"durationMinuteSpinner\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert timeZoneComboBox != null : "fx:id=\"timeZoneComboBox\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert currentTimeZoneLabel != null : "fx:id=\"currentTimeZoneLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert currentTimeZoneValue != null : "fx:id=\"currentTimeZoneValue\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert locationLabel != null : "fx:id=\"locationLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert locationValidationLabel != null : "fx:id=\"locationValidationLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert locationTextArea != null : "fx:id=\"locationTextArea\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert phoneTextField != null : "fx:id=\"phoneTextField\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert implicitLocationLabel != null : "fx:id=\"implicitLocationLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert typeComboBox != null : "fx:id=\"typeComboBox\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert contactValidationLabel != null : "fx:id=\"contactValidationLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert contactTextField != null : "fx:id=\"contactTextField\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert urlLabel != null : "fx:id=\"urlLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert urlValidationLabel != null : "fx:id=\"urlValidationLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert urlTextField != null : "fx:id=\"urlTextField\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert descriptionTextArea != null : "fx:id=\"descriptionTextArea\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert conflictsBorderPane != null : "fx:id=\"conflictsBorderPane\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert conflictingAppointmentsTableView != null : "fx:id=\"conflictingAppointmentsTableView\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert dropdownOptionsBorderPane != null : "fx:id=\"dropdownOptionsBorderPane\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert dropdownOptionsInactiveRadioButton != null : "fx:id=\"dropdownOptionsInactiveRadioButton\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert dropdownOptions != null : "fx:id=\"dropdownOptions\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert dropdownOptionsLabel != null : "fx:id=\"dropdownOptionsLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert dropdownOptionsActiveRadioButton != null : "fx:id=\"dropdownOptionsActiveRadioButton\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert dropdownOptionsAllRadioButton != null : "fx:id=\"dropdownOptionsAllRadioButton\" was not injected: check your FXML file 'EditAppointment.fxml'.";

        customerModelList = FXCollections.observableArrayList();
        userModelList = FXCollections.observableArrayList();
        otherAppointments = FXCollections.observableArrayList();
        conflictingAppointments = FXCollections.observableArrayList();
        showActiveCustomers = Optional.of(true);
        showActiveUsers = Optional.of(true);

        formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);
        startDateTime = new StartDateTimeProperty(this, "startDateTime", startDatePicker, startHourSpinner, startMinuteSpinner, amPmSpinner,
                timeZoneComboBox);
        duration = new DurationProperty(this, "duration", startDateTime, durationHourSpinner, durationMinuteSpinner);

        // Get appointment type options.
        types = FXCollections.observableArrayList(AppointmentType.values());
        typeComboBox.setItems(types);
        typeSelectionModel = typeComboBox.getSelectionModel();
        typeSelectionModel.select(AppointmentType.OTHER);
        
        // Set validation and label bindings
        titleValidationLabel.visibleProperty().bind(getTitleIsEmpty());
        customerAddress = new ReadOnlyStringWrapper("");
        customerSelectionModel = customerComboBox.getSelectionModel();
        customerValidationLabel.visibleProperty().bind(getCustomerNotSelected());
        userSelectionModel = userComboBox.getSelectionModel();
        userValidationLabel.visibleProperty().bind(getUserNotSelected());
        contactValidationLabel.visibleProperty().bind(getPointOfContactNotValid());
        startValidationLabel.textProperty().bind(getStartValidationMessage());
        startValidationLabel.visibleProperty().bind(getStartValidationMessage().isNotEmpty());
        durationValidationLabel.textProperty().bind(duration.getValidationMessage());
        durationValidationLabel.visibleProperty().bind(duration.getValidationMessage().isNotEmpty());
        currentTimeZoneLabel.visibleProperty().bind(getCurrentTimeZoneText().isNotEmpty());
        currentTimeZoneValue.textProperty().bind(getCurrentTimeZoneText());
        locationLabel.textProperty().bind(Bindings.createStringBinding(() -> {
            return (typeSelectionModel.getSelectedItem() == AppointmentType.PHONE) ? getResourceString(RESOURCEKEY_PHONENUMBER)
                    : getResourceString(RESOURCEKEY_LOCATIONLABELTEXT);
        }, typeSelectionModel.selectedItemProperty()));
        locationLabel.visibleProperty().bind(Bindings.createBooleanBinding(() -> typeSelectionModel.getSelectedItem() != AppointmentType.VIRTUAL,
                typeSelectionModel.selectedItemProperty()));
        bindCssCollapse(locationLabel, Bindings.createBooleanBinding(() -> typeSelectionModel.getSelectedItem() == AppointmentType.VIRTUAL,
                typeSelectionModel.selectedItemProperty()));
        locationValidationLabel.visibleProperty().bind(getLocationNotValid());
        locationTextArea.visibleProperty().bind(Bindings.createBooleanBinding(() -> typeSelectionModel.getSelectedItem() == AppointmentType.OTHER,
                typeSelectionModel.selectedItemProperty()));
        bindCssCollapse(locationTextArea, Bindings.createBooleanBinding(() -> typeSelectionModel.getSelectedItem() != AppointmentType.OTHER,
                typeSelectionModel.selectedItemProperty()));
        phoneTextField.visibleProperty().bind(Bindings.createBooleanBinding(() -> typeSelectionModel.getSelectedItem() == AppointmentType.PHONE,
                typeSelectionModel.selectedItemProperty()));
        bindCssCollapse(phoneTextField, Bindings.createBooleanBinding(() -> typeSelectionModel.getSelectedItem() != AppointmentType.PHONE,
                typeSelectionModel.selectedItemProperty()));
        implicitLocationLabel.visibleProperty().bind(Bindings.createBooleanBinding(() -> {
            switch (typeSelectionModel.getSelectedItem()) {
                case OTHER:
                case PHONE:
                case VIRTUAL:
                    return false;
                default:
                    return true;
            }
        }, typeSelectionModel.selectedItemProperty()));
        urlValidationLabel.textProperty().bind(getUrlValidationMessage());
        urlValidationLabel.visibleProperty().bind(getUrlValidationMessage().isNotEmpty());
        startDateTime.addListener((observable) -> resetConflictingAppointments(((StartDateTimeProperty) observable).get(), duration.get()));
        duration.addListener((observable) -> resetConflictingAppointments(startDateTime.get(), ((DurationProperty) observable).get()));
        showConflictsButton.visibleProperty().bind(Bindings.createBooleanBinding(() -> !conflictingAppointments.isEmpty(), conflictingAppointments));
    }

    public IntegerBinding getCustomerConflictCount() {
        return Bindings.createIntegerBinding(() -> {
            CustomerModelImpl selectedItem = customerSelectionModel.getSelectedItem();
            if (null != selectedItem) {
                CustomerDAO dataObject = selectedItem.getDataObject();
                if (dataObject.isExisting()) {
                    int pk = dataObject.getPrimaryKey();
                    return (int) conflictingAppointments.stream().filter((t) -> t.getCustomer().getPrimaryKey() == pk).count();
                }
            }
            return 0;
        }, conflictingAppointments);
    }

    public IntegerBinding getUserConflictCount() {
        return Bindings.createIntegerBinding(() -> {
            UserModelImpl selectedItem = userSelectionModel.getSelectedItem();
            if (null != selectedItem) {
                UserDAO dataObject = selectedItem.getDataObject();
                if (dataObject.isExisting()) {
                    int pk = dataObject.getPrimaryKey();
                    return (int) conflictingAppointments.stream().filter((t) -> t.getCustomer().getPrimaryKey() == pk).count();
                }
            }
            return 0;
        }, conflictingAppointments);
    }

    public StringBinding getUrlValidationMessage() {
        return Bindings.createStringBinding(() -> {
            String url = urlTextField.getText().trim();
            AppointmentType t = typeSelectionModel.getSelectedItem();
            if (url.isEmpty()) {
                return (t == AppointmentType.VIRTUAL) ? getResourceString(RESOURCEKEY_REQUIRED) : "";
            }
            try {
                URI uri = new URI(url);
                if (null == uri.getHost() || null == uri.getScheme()) {
                    return getResourceString(RESOURCEKEY_INVALIDURL);
                }
            } catch (URISyntaxException ex) {
                LOG.log(Level.FINER, "Caught URI syntax exception", ex);
                return getResourceString(RESOURCEKEY_INVALIDURL);
            }
            return "";
        }, typeSelectionModel.selectedItemProperty(), urlTextField.textProperty());
    }

    public BooleanBinding getLocationNotValid() {
        return Bindings.createBooleanBinding(() -> {
            String locationText = locationTextArea.getText();
            String phoneText = phoneTextField.getText();
            switch (typeSelectionModel.getSelectedItem()) {
                case PHONE:
                    return phoneText.trim().isEmpty();
                case OTHER:
                    return locationText.trim().isEmpty();
                default:
                    return false;
            }
        }, typeSelectionModel.selectedItemProperty(), locationTextArea.textProperty(), phoneTextField.textProperty());
    }

    public StringBinding getCurrentTimeZoneText() {
        return Bindings.createStringBinding(() -> {
            LocalDateTime s = startDateTime.get().withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
            Duration d = duration.get();
            if (null != s && null != d) {
                LocalDateTime e = s.plus(d);
                return String.format(getResourceString(RESOURCEKEY_TIMERANGE), s.format(formatter), e.format(formatter));
            }
            return "";
        }, startDateTime, duration);
    }

    /**
     * Creates a binding that indicates whether the title text field is empty.
     *
     * @return A new {@link BooleanBinding} that returns {@code true} if the {@link #titleTextField} is empty.
     */
    public BooleanBinding getTitleIsEmpty() {
        return Bindings.createBooleanBinding(() -> titleTextField.getText().trim().isEmpty(), titleTextField.textProperty());
    }

    /**
     * Creates a binding that indicates whether the customer drop-down has no selection.
     *
     * @return A new {@link BooleanBinding} that returns {@code true} if the {@link #customerComboBox} has no selection.
     */
    public BooleanBinding getCustomerNotSelected() {
        return Bindings.createBooleanBinding(() -> null == customerSelectionModel.getSelectedItem(),
                customerSelectionModel.selectedItemProperty());
    }

    /**
     * Gets the multi-line customer address text.
     *
     * @return The value of the {@link CustomerModelImpl#multiLineAddress} binding or an empty string if no customer is selected.
     */
    public String getCustomerAddress() {
        return customerAddress.get();
    }

    /**
     * Gets the property that contains the multi-line customer address text. This will be bound to {@link CustomerModelImpl#multiLineAddress} if a customer is selected; otherwise,
     * this will contain an empty string.
     *
     * @return The {@link ReadOnlyStringProperty} that contains the multi-line customer address text.
     */
    public ReadOnlyStringProperty customerAddressProperty() {
        return customerAddress.getReadOnlyProperty();
    }

    /**
     * Creates a binding that returns the effective location text.
     *
     * @return A new {@link StringBinding} that returns the effective location.
     */
    public StringBinding getEffectiveLocation() {
        return Bindings.createStringBinding(() -> {
            AppointmentType t = typeSelectionModel.getSelectedItem();
            String l = locationTextArea.getText().trim();
            String a = customerAddress.get();
            String p = phoneTextField.getText().trim();
            String u = urlTextField.getText().trim();
            switch (t) {
                case CORPORATE_HQ_MEETING:
                    return AppResources.getProperty(AppResources.RESOURCEKEY_APPOINTMENTTYPE_HQ);
                case CUSTOMER_SITE:
                    return (a.isEmpty()) ? AppResources.getProperty(AppResources.RESOURCEKEY_APPOINTMENTTYPE_CUSTOMER)
                            : String.format("%s%n%s", AppResources.getProperty(AppResources.RESOURCEKEY_APPOINTMENTTYPE_CUSTOMER), a);
                case GERMANY_SITE_MEETING:
                    return AppResources.getProperty(AppResources.RESOURCEKEY_APPOINTMENTTYPE_GERMANY);
                case GUATEMALA_SITE_MEETING:
                    return AppResources.getProperty(AppResources.RESOURCEKEY_APPOINTMENTTYPE_GUATEMALA);
                case INDIA_SITE_MEETING:
                    return AppResources.getProperty(AppResources.RESOURCEKEY_APPOINTMENTTYPE_INDIA);
                case PHONE:
                    return (p.isEmpty()) ? AppResources.getProperty(AppResources.RESOURCEKEY_APPOINTMENTTYPE_PHONE)
                            : String.format("%s%n%s", AppResources.getProperty(AppResources.RESOURCEKEY_APPOINTMENTTYPE_PHONE), p);
                case VIRTUAL:
                    return (u.isEmpty()) ? AppResources.getProperty(AppResources.RESOURCEKEY_APPOINTMENTTYPE_VIRTUAL)
                            : String.format("%s%n%s", AppResources.getProperty(AppResources.RESOURCEKEY_APPOINTMENTTYPE_VIRTUAL), u);
                default:
                    return l;
            }
        }, typeSelectionModel.selectedItemProperty(), locationTextArea.textProperty(), customerAddress, phoneTextField.textProperty(),
                urlTextField.textProperty());
    }

    /**
     * Creates a binding that indicates whether the user drop-down has no selection.
     *
     * @return A new {@link BooleanBinding} that returns {@code true} if the {@link #userComboBox} has no selection.
     */
    public BooleanBinding getUserNotSelected() {
        return Bindings.createBooleanBinding(() -> null == userSelectionModel.getSelectedItem(),
                userSelectionModel.selectedItemProperty());
    }

    /**
     * Creates a binding that indicates whether the point-of-contact is required and is empty.
     *
     * @return A new {@link BooleanBinding} that returns {@code true} if {@link AppointmentType#OTHER} is selected in the {@link #typeComboBox} and the {@link #contactTextField} is
     * empty.
     */
    public BooleanBinding getPointOfContactNotValid() {
        return Bindings.createBooleanBinding(() -> {
            AppointmentType t = typeSelectionModel.getSelectedItem();
            return contactTextField.getText().trim().isEmpty() && t == AppointmentType.OTHER;
        }, contactTextField.textProperty(), typeSelectionModel.selectedItemProperty());
    }

    public StringBinding getStartValidationMessage() {
        StringBinding conflictMessage = getConflictMessage();
        StringBinding startValidationMessage = startDateTime.getValidationMessage();
        StringBinding durationValidationMessage = duration.getValidationMessage();
        return Bindings.createStringBinding(() -> {
            String c = conflictMessage.get();
            String s = startValidationMessage.get();
            String d = durationValidationMessage.get();
            if (s.isEmpty()) {
                return (d.isEmpty()) ? c : "";
            }
            return s;
        }, conflictMessage, startValidationMessage, durationValidationMessage);
    }

    public StringBinding getConflictMessage() {
        IntegerBinding customerConflictCount = getCustomerConflictCount();
        IntegerBinding userConflictCount = getUserConflictCount();
        return Bindings.createStringBinding(() -> {
            int c = customerConflictCount.get();
            int u = userConflictCount.get();
            if (c == 1) {
                if (u == 1) {
                    return getResourceString(RESOURCEKEY_CONFLICTCUSTOMER1USER1);
                }
                if (u > 1) {
                    return String.format(getResourceString(RESOURCEKEY_CONFLICTCUSTOMER1USERN), u);
                }
                return getResourceString(RESOURCEKEY_CONFLICTCUSTOMER1);
            }
            if (c > 1) {
                if (u == 1) {
                    return String.format(getResourceString(RESOURCEKEY_CONFLICTCUSTOMERNUSER1), c);
                }
                if (u > 1) {
                    return String.format(getResourceString(RESOURCEKEY_CONFLICTCUSTOMERNUSERN), c, u);
                }
                return String.format(getResourceString(RESOURCEKEY_CONFLICTCUSTOMERN), c);
            }
            if (u == 1) {
                return getResourceString(RESOURCEKEY_CONFLICTUSER1);
            }
            if (u > 1) {
                return String.format(getResourceString(RESOURCEKEY_CONFLICTUSERN), u);
            }
            return "";
        }, customerConflictCount, userConflictCount);
    }

    @HandlesFxmlViewEvent(FxmlViewEventHandling.BEFORE_SHOW)
    protected void onBeforeShow(FxmlViewEvent<? extends Parent> event) {
        TaskWaiter.startNow(new ItemsLoadTask(event.getStage()));
        AppointmentModel model = this.getModel();
        typeSelectionModel.select(model.getType());
        titleTextField.setText(model.getTitle());
        contactTextField.setText(model.getContact());
        ZoneId zoneId = AppointmentModel.getZoneId(model);
        LocalDateTime start = model.getStart();
        if (zoneId.equals(ZoneId.systemDefault()))
            startDateTime.set(ZonedDateTime.of(start, zoneId));
        else
            startDateTime.set(ZonedDateTime.of(start, ZoneId.systemDefault()).withZoneSameInstant(zoneId));
        duration.set(Duration.between(start, model.getEnd()));
        
        switch (typeSelectionModel.getSelectedItem()) {
            case OTHER:
                locationTextArea.setText(model.getLocation());
                break;
            case PHONE:
                phoneTextField.setText(model.getLocation());
                break;
        }
        urlTextField.setText(model.getUrl());
        descriptionTextArea.setText(model.getDescription());
    }

    @Override
    protected BooleanExpression getValidationExpression() {
        return getTitleIsEmpty().or(getCustomerNotSelected()).or(getUserNotSelected()).or(getPointOfContactNotValid()).or(getLocationNotValid()).not()
                .and(startDateTime.getValidationMessage().isEmpty()).and(duration.getValidationMessage().isEmpty())
                .and(getUrlValidationMessage().isEmpty());
    }

    @Override
    protected ItemModel.ModelFactory<AppointmentDAO, AppointmentModel> getFactory() {
        return AppointmentModel.getFactory();
    }

    private void onCustomerChanged(CustomerModelImpl customer) {
        customerAddress.unbind();

        if (null == customer) {
            customerAddress.set("");
        } else {
            customerAddress.bind(customer.getMultiLineAddress());
        }
        TaskWaiter.startNow(new AppointmentReloadTask(customer, userSelectionModel.getSelectedItem()));
    }

    private void onUserChanged(UserModelImpl user) {
        TaskWaiter.startNow(new AppointmentReloadTask(customerSelectionModel.getSelectedItem(), user));
    }

    private void resetConflictingAppointments(ZonedDateTime start, Duration duration) {
        conflictingAppointments.clear();
        CustomerModelImpl selectedCustomer = customerSelectionModel.getSelectedItem();
        UserModelImpl selectedUser = userSelectionModel.getSelectedItem();
        Stream<AppointmentModel> stream;
        if (null != selectedCustomer) {
            int cpk = selectedCustomer.getPrimaryKey();
            if (null != selectedUser) {
                int upk = selectedUser.getPrimaryKey();
                stream = otherAppointments.stream().filter((t) -> t.getCustomer().getPrimaryKey() == cpk || t.getUser().getPrimaryKey() == upk);
            } else {
                stream = otherAppointments.stream().filter((t) -> t.getCustomer().getPrimaryKey() == cpk);
            }
        } else {
            if (null == selectedUser) {
                return;
            }
            int upk = selectedUser.getPrimaryKey();
            stream = otherAppointments.stream().filter((t) -> t.getUser().getPrimaryKey() == upk);
        }
        LocalDateTime s = start.toLocalDateTime();
        LocalDateTime e = s.plusHours(duration.toHours()).plusMinutes(duration.toMinutes());
        stream.forEach((t) -> {
            if (t.getStart().compareTo(e) > 0 && t.getEnd().compareTo(s) < 0) {
                conflictingAppointments.add(t);
            }
        });
    }

    @Override
    protected void updateModel(AppointmentModel model) {
        if (!getValidationExpression().get()) {
            throw new IllegalStateException();
        }
        model.setTitle(titleTextField.getText().trim());
        model.setCustomer(customerSelectionModel.getSelectedItem());
        model.setUser(userSelectionModel.getSelectedItem());
        model.setUrl(urlTextField.getText().trim());
        AppointmentType type = typeSelectionModel.getSelectedItem();
        model.setType(type);
        LocalDateTime s = startDateTime.get().withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
        model.setStart(s);
        model.setEnd(s.plus(duration.get()));
        switch (type) {
            case PHONE:
                model.setLocation(phoneTextField.getText().trim());
                break;
            case OTHER:
                model.setLocation(locationTextArea.getText().trim());
                break;
            default:
                model.setLocation("");
                break;
        }
        model.setDescription(descriptionTextArea.getText().trim());
        model.setContact(contactTextField.getText().trim());
    }

    private class AppointmentReloadTask extends TaskWaiter<List<AppointmentDAO>> {

        private final CustomerDAO customer;
        private final UserDAO user;
        private final AppointmentDAO toEdit;

        private AppointmentReloadTask(CustomerModelImpl selectedCustomer, UserModelImpl selectedUser) {
            super((Stage) customerComboBox.getScene().getWindow());
            customer = (null == selectedCustomer) ? null : selectedCustomer.getDataObject();
            user = (null == selectedUser) ? null : selectedUser.getDataObject();
            toEdit = getModel().getDataObject();
        }

        @Override
        protected void processResult(List<AppointmentDAO> result, Stage stage) {
            otherAppointments.clear();
            if (null != result && !result.isEmpty()) {
                if (toEdit.isExisting()) {
                    int pk = toEdit.getPrimaryKey();
                    result.forEach((t) -> {
                        if (t.getPrimaryKey() != pk) {
                            otherAppointments.add(new AppointmentModel(t));
                        }
                    });
                } else {
                    result.forEach((t) -> {
                        otherAppointments.add(new AppointmentModel(t));
                    });
                }
            }
            resetConflictingAppointments(startDateTime.get(), duration.get());
        }

        @Override
        protected void processException(Throwable ex, Stage stage) {
            AlertHelper.showErrorAlert(stage, LOG, ex);
        }

        @Override
        protected List<AppointmentDAO> getResult(Connection connection) throws SQLException {
            AppointmentDAO.FactoryImpl af = AppointmentDAO.getFactory();
            if (null != customer && customer.isExisting()) {
                if (null != user && user.isExisting()) {
                    return af.load(connection, AppointmentFilter.of(customer, user, null, null));
                }
                return af.load(connection, AppointmentFilter.of(customer, null, null, null));
            }

            if (null != user && user.isExisting()) {
                return af.load(connection, AppointmentFilter.of(null, user, null, null));
            }
            return null;
        }

    }

    private class CustomerReloadTask extends TaskWaiter<List<CustomerDAO>> {

        private final Optional<Boolean> loadOption;

        private CustomerReloadTask(Stage owner) {
            super(owner, AppResources.getResourceString(AppResources.RESOURCEKEY_CONNECTINGTODB),
                    AppResources.getResourceString(AppResources.RESOURCEKEY_LOADINGCUSTOMERS));
            loadOption = showActiveCustomers;
        }

        @Override
        protected void processResult(List<CustomerDAO> result, Stage stage) {
            CustomerModelImpl selectedItem = customerSelectionModel.getSelectedItem();
            customerModelList.clear();
            if (null != result && !result.isEmpty()) {
                result.forEach((t) -> customerModelList.add(new CustomerModelImpl(t)));
            }
            if (null != selectedItem) {
                int pk = selectedItem.getPrimaryKey();
                Optional<CustomerModelImpl> matching = customerModelList.stream().filter((t) -> t.getPrimaryKey() == pk).findFirst();
                if (matching.isPresent()) {
                    customerSelectionModel.select(matching.get());
                } else {
                    customerSelectionModel.clearSelection();
                }
            }
        }

        @Override
        protected void processException(Throwable ex, Stage stage) {
            AlertHelper.showErrorAlert(stage, LOG, ex);
            stage.close();
        }

        @Override
        protected List<CustomerDAO> getResult(Connection connection) throws SQLException {
            CustomerDAO.FactoryImpl cf = CustomerDAO.getFactory();
            if (loadOption.isPresent()) {
                return cf.load(connection, cf.getActiveStatusFilter(loadOption.get()));
            }
            return cf.load(connection, cf.getAllItemsFilter());
        }

    }

    private class UserReloadTask extends TaskWaiter<List<UserDAO>> {

        private final Optional<Boolean> loadOption;

        private UserReloadTask(Stage owner) {
            super(owner, AppResources.getResourceString(AppResources.RESOURCEKEY_CONNECTINGTODB),
                    AppResources.getResourceString(AppResources.RESOURCEKEY_LOADINGUSERS));
            loadOption = showActiveUsers;
        }

        @Override
        protected void processResult(List<UserDAO> result, Stage stage) {
            UserModelImpl selectedItem = userSelectionModel.getSelectedItem();
            userModelList.clear();
            if (null != result && !result.isEmpty()) {
                result.forEach((t) -> userModelList.add(new UserModelImpl(t)));
            }
            if (null != selectedItem) {
                int pk = selectedItem.getPrimaryKey();
                Optional<UserModelImpl> matching = userModelList.stream().filter((t) -> t.getPrimaryKey() == pk).findFirst();
                if (matching.isPresent()) {
                    userSelectionModel.select(matching.get());
                } else {
                    userSelectionModel.clearSelection();
                }
            }
        }

        @Override
        protected void processException(Throwable ex, Stage stage) {
            AlertHelper.showErrorAlert(stage, LOG, ex);
            stage.close();
        }

        @Override
        protected List<UserDAO> getResult(Connection connection) throws SQLException {
            UserDAO.FactoryImpl uf = UserDAO.getFactory();
            if (loadOption.isPresent()) {
                if (loadOption.get()) {
                    return uf.load(connection, uf.getActiveUsersFilter());
                }
                return uf.load(connection, UserFilter.of(UserFilter.expressionOf(UserStatus.INACTIVE, ComparisonOperator.EQUALS)));
            }
            return uf.load(connection, uf.getAllItemsFilter());
        }

    }

    private class ItemsLoadTask extends TaskWaiter<List<AppointmentDAO>> {

        private List<CustomerDAO> customerDaoList;
        private List<UserDAO> userDaoList;
        private final AppointmentDAO toEdit;
        private final Optional<Boolean> customerLoadOption;
        private final Optional<Boolean> userLoadOption;

        private ItemsLoadTask(Stage owner) {
            super(owner, AppResources.getResourceString(AppResources.RESOURCEKEY_CONNECTINGTODB),
                    AppResources.getResourceString(AppResources.RESOURCEKEY_INITIALIZING));
            customerDaoList = null;
            userDaoList = null;
            toEdit = getModel().getDataObject();
            customerLoadOption = showActiveCustomers;
            userLoadOption = showActiveUsers;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void processResult(List<AppointmentDAO> result, Stage owner) {
            if (null != customerDaoList && !customerDaoList.isEmpty()) {
                customerDaoList.forEach((t) -> customerModelList.add(new CustomerModelImpl(t)));
            }
            if (null != userDaoList && !userDaoList.isEmpty()) {
                userDaoList.forEach((t) -> userModelList.add(new UserModelImpl(t)));
            }

            if (null != result && !result.isEmpty()) {
                if (toEdit.isExisting()) {
                    int pk = toEdit.getPrimaryKey();
                    result.forEach((t) -> {
                        if (t.getPrimaryKey() != pk) {
                            otherAppointments.add(new AppointmentModel(t));
                        }
                    });
                } else {
                    result.forEach((t) -> {
                        otherAppointments.add(new AppointmentModel(t));
                    });
                }
            }
            customerComboBox.setItems(customerModelList);
            userComboBox.setItems(userModelList);
            CustomerModel<? extends CustomerElement> customer = getModel().getCustomer();
            if (null != customer) {
                int cpk = customer.getPrimaryKey();
                customerModelList.stream().filter((t) -> t.getPrimaryKey() == cpk).findFirst().ifPresent((t)
                        -> customerComboBox.getSelectionModel().select(t));
            }
            UserModel<? extends UserElement> user = getModel().getUser();
            int upk = (null == user) ? Scheduler.getCurrentUser().getPrimaryKey() : user.getPrimaryKey();
            userModelList.stream().filter((t) -> t.getPrimaryKey() == upk).findFirst().ifPresent((t)
                    -> userSelectionModel.select(t));
            customerSelectionModel.selectedItemProperty().addListener((observable) -> {
                onCustomerChanged(((ObservableObjectValue<CustomerModelImpl>) observable).get());
            });
            userSelectionModel.selectedItemProperty().addListener((observable) -> {
                onUserChanged(((ObservableObjectValue<UserModelImpl>) observable).get());
            });
            resetConflictingAppointments(startDateTime.get(), duration.get());
        }

        @Override
        protected void processException(Throwable ex, Stage owner) {
            AlertHelper.showErrorAlert(owner, LOG, ex);
            owner.close();
        }

        @Override
        protected List<AppointmentDAO> getResult(Connection connection) throws SQLException {
            CustomerDAO.FactoryImpl cf = CustomerDAO.getFactory();
            UserDAO.FactoryImpl uf = UserDAO.getFactory();
            AppointmentDAO.FactoryImpl af = AppointmentDAO.getFactory();
            if (customerLoadOption.isPresent()) {
                customerDaoList = cf.load(connection, cf.getActiveStatusFilter(customerLoadOption.get()));
            } else {
                customerDaoList = cf.load(connection, cf.getAllItemsFilter());
            }
            if (userLoadOption.isPresent()) {
                if (userLoadOption.get()) {
                    userDaoList = uf.load(connection, uf.getActiveUsersFilter());
                } else {
                    userDaoList = uf.load(connection, UserFilter.of(UserFilter.expressionOf(UserStatus.INACTIVE, ComparisonOperator.EQUALS)));
                }
            } else {
                userDaoList = uf.load(connection, uf.getAllItemsFilter());
            }

            if (null != customerDaoList && null != userDaoList && !(customerDaoList.isEmpty() || userDaoList.isEmpty())) {
                CustomerElement customer = toEdit.getCustomer();
                UserElement user = toEdit.getUser();
                if (null != customer && customer.isExisting()) {
                    if (null != user && user.isExisting()) {
                        return af.load(connection, AppointmentFilter.of(customer, user, null, null));
                    }
                    return af.load(connection, AppointmentFilter.of(customer, null, null, null));
                }
                if (null != user && user.isExisting()) {
                    return af.load(connection, AppointmentFilter.of(null, user, null, null));
                }
            }
            return null;
        }

    }

}
