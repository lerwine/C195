package scheduler.view.appointment;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.WeakEventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Window;
import scheduler.AppResourceKeys;
import scheduler.AppResources;
import scheduler.Scheduler;
import scheduler.dao.AppointmentDAO;
import scheduler.dao.CustomerDAO;
import scheduler.dao.DataRowState;
import scheduler.dao.UserDAO;
import scheduler.dao.filter.AppointmentFilter;
import scheduler.dao.filter.ComparisonOperator;
import scheduler.dao.filter.UserFilter;
import scheduler.events.AppointmentEvent;
import scheduler.events.CustomerSuccessEvent;
import scheduler.events.UserSuccessEvent;
import static scheduler.model.Appointment.MAX_LENGTH_TITLE;
import static scheduler.model.Appointment.MAX_LENGTH_URL;
import scheduler.model.AppointmentType;
import static scheduler.model.AppointmentType.CORPORATE_LOCATION;
import static scheduler.model.AppointmentType.CUSTOMER_SITE;
import scheduler.model.CorporateAddress;
import scheduler.model.Customer;
import scheduler.model.ModelHelper;
import scheduler.model.PredefinedData;
import scheduler.model.User;
import scheduler.model.UserStatus;
import scheduler.model.fx.AppointmentModel;
import scheduler.model.fx.CustomerModel;
import scheduler.model.fx.EntityModel;
import scheduler.model.fx.PartialCustomerModel;
import scheduler.model.fx.PartialUserModel;
import scheduler.model.fx.UserModel;
import scheduler.observables.BindingHelper;
import scheduler.util.BinarySelective;
import scheduler.util.DbConnector;
import scheduler.util.LogHelper;
import static scheduler.util.NodeUtil.collapseNode;
import static scheduler.util.NodeUtil.restoreLabeled;
import static scheduler.util.NodeUtil.restoreNode;
import static scheduler.util.NodeUtil.setErrorMessage;
import static scheduler.util.NodeUtil.setWarningMessage;
import scheduler.util.Tuple;
import scheduler.view.EditItem;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.annotations.ModelEditor;
import static scheduler.view.appointment.EditAppointmentResourceKeys.*;
import scheduler.view.task.WaitBorderPane;

/**
 * FXML Controller class for editing an {@link AppointmentModel}.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/view/appointment/EditAppointment")
@FXMLResource("/scheduler/view/appointment/EditAppointment.fxml")
public class EditAppointment extends StackPane implements EditItem.ModelEditorController<AppointmentDAO, AppointmentModel, AppointmentEvent> {

    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(EditAppointment.class.getName()), Level.FINER);
//    private static final Logger LOG = Logger.getLogger(EditAppointment.class.getName());
    private static final Pattern INT_PATTERN = Pattern.compile("^\\s*\\d{1,9}\\s*");
    private static final String INVALID_NUMBER = "Invalid number";
    public static final NumberFormat INTN_FORMAT;
    public static final NumberFormat INT2_FORMAT;
    public static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL, FormatStyle.FULL);

    static {
        INT2_FORMAT = NumberFormat.getIntegerInstance();
        INT2_FORMAT.setMinimumIntegerDigits(2);
        INT2_FORMAT.setMaximumIntegerDigits(2);
        INTN_FORMAT = NumberFormat.getIntegerInstance();
    }

    private static BinarySelective<LocalDateTime, String> calculateEndDateTime(LocalDateTime start, BinarySelective<Integer, String> hour, BinarySelective<Integer, String> minute) {
        if (null == start) {
            return BinarySelective.ofSecondary(hour.toSecondary(minute.toSecondary("")));
        }
        return hour.map(
                (u) -> minute.map(
                        (v) -> BinarySelective.ofPrimary(
                                (u > 0)
                                        ? ((v > 0) ? start.plusMinutes(v) : start).plusHours(u)
                                        : ((v > 0) ? start.plusMinutes(v) : start)
                        ),
                        (v) -> BinarySelective.ofSecondary(v)
                ),
                (u) -> BinarySelective.ofSecondary(u)
        );
    }

    private static BinarySelective<LocalDateTime, String> calculateDateTime(LocalDate date, BinarySelective<Integer, String> hour,
            BinarySelective<Integer, String> minute, boolean isPm) {
        if (null == date) {
            return BinarySelective.ofSecondary(hour.toSecondary(minute.toSecondary("* Required")));
        }

        return hour.map(
                (hv) -> minute.map(
                        (mv) -> BinarySelective.ofPrimary(date.atTime((isPm) ? ((hv > 12) ? hv + 12 : 12) : ((hv == 12) ? 0 : hv), mv)),
                        (mm) -> BinarySelective.ofSecondary(mm)
                ),
                (hm) -> BinarySelective.ofSecondary(hm)
        );
    }

    private static BinarySelective<Integer, String> calculateHour(String text, int minValue, int maxValue) {
        String trimmed;
        if (null == text || (trimmed = text.trim()).isEmpty()) {
            return BinarySelective.ofSecondary("");
        }
        try {
            Matcher m = INT_PATTERN.matcher(trimmed);
            if (!m.find()) {
                throw new ParseException("Invalid hour number", text.length() - trimmed.length());
            } else if (m.end() < trimmed.length()) {
                throw new ParseException("Invalid hour number", m.end() + (text.length() - trimmed.length()));
            }
            Number parse = INTN_FORMAT.parse(trimmed);
            int i = parse.intValue();
            if (i < minValue || i > maxValue) {
                return BinarySelective.ofSecondary("Hour out of range");
            }
            return BinarySelective.ofPrimary(i);
        } catch (ParseException ex) {
            int i = ex.getErrorOffset();
            return BinarySelective.ofSecondary((i > 0 && i < trimmed.length()) ? String.format("Invalid hour format at position %d", i)
                    : "Invalid hour number");
        }
    }

    private static BinarySelective<Integer, String> calculateMinute(String text) {
        String trimmed;
        if (null == text || (trimmed = text.trim()).isEmpty()) {
            return BinarySelective.ofSecondary("");
        }
        try {
            Matcher m = INT_PATTERN.matcher(trimmed);
            if (!m.find()) {
                throw new ParseException("Invalid minute fnumber", text.length() - trimmed.length());
            } else if (m.end() < trimmed.length()) {
                throw new ParseException("Invalid nminute fumber", m.end() + (text.length() - trimmed.length()));
            }
            Number parse = INTN_FORMAT.parse(trimmed);
            int i = parse.intValue();
            if (i < 0 || i > 59) {
                return BinarySelective.ofSecondary("Minute out of range");
            }
            return BinarySelective.ofPrimary(i);
        } catch (ParseException ex) {
            int i = ex.getErrorOffset();
            return BinarySelective.ofSecondary((i > 0 && i < trimmed.length()) ? String.format("Invalid minute format at position %d", i)
                    : "Invalid minute number");
        }
    }

    private static Optional<String> calculateEffectiveLocation(AppointmentType type, String location, String phone, CustomerModel customer, CorporateAddress corporateAddress) {
        switch (type) {
            case CORPORATE_LOCATION:
                if (null == corporateAddress) {
                    return Optional.empty();
                }
                return Optional.of(corporateAddress.toMultiLineAddress());
            case CUSTOMER_SITE:
                return Optional.of((null == customer) ? "" : customer.getMultiLineAddress());
            case PHONE:
                if (phone.isEmpty()) {
                    return Optional.empty();
                }
                return Optional.of(phone);
            case VIRTUAL:
                return Optional.of(location);
            default:
                if (location.isEmpty()) {
                    return Optional.empty();
                }
                return Optional.of(location);
        }
    }

    private static BinarySelective<String, String> calculateURL(AppointmentType type, String text) {
        if (null == text || (text = text.trim()).isEmpty()) {
            if (type == AppointmentType.VIRTUAL) {
                return BinarySelective.ofSecondary("* Required");
            }
            return BinarySelective.ofPrimaryNullable(null);
        }
        URI uri;
        try {
            uri = new URI(text);
        } catch (URISyntaxException ex) {
            LOG.log(Level.WARNING, String.format("Error parsing url %s", text), ex);
            text = ex.getMessage();
            return BinarySelective.ofSecondary((null == text || text.trim().isEmpty()) ? "Invalid URL" : text);
        }
        URL url;
        try {
            url = uri.toURL();
        } catch (MalformedURLException | IllegalArgumentException ex) {
            LOG.log(Level.WARNING, String.format("Error converting uri %s", text), ex);
            text = ex.getMessage();
            return BinarySelective.ofSecondary((null == text || text.trim().isEmpty()) ? "Invalid URL" : text);
        }
        if ((text = url.toString()).length() > MAX_LENGTH_URL) {
            return BinarySelective.ofSecondary("URL too long");
        }
        return BinarySelective.ofPrimary(text);
    }

    public static AppointmentModel editNew(PartialCustomerModel<? extends Customer> customer, PartialUserModel<? extends User> user,
            Window parentWindow, boolean keepOpen) throws IOException {
        AppointmentModel model = AppointmentDAO.FACTORY.createNew().cachedModel(true);
        if (null != customer) {
            model.setCustomer(customer);
        }
        if (null != user) {
            model.setUser(user);
        }
        return EditItem.showAndWait(parentWindow, EditAppointment.class, model, keepOpen);
    }

    public static AppointmentModel edit(AppointmentModel model, Window parentWindow) throws IOException {
        return EditItem.showAndWait(parentWindow, EditAppointment.class, model, false);
    }

    private final ReadOnlyBooleanWrapper valid;
    private final ReadOnlyBooleanWrapper modified;
    private final ReadOnlyStringWrapper windowTitle;
    private final ObservableList<CustomerModel> customerModelList;
    private final ObservableList<UserModel> userModelList;
    private final TypeContextController typeContext;
    private final EventHandler<CustomerSuccessEvent> onCustomerDeleted;
    private final EventHandler<UserSuccessEvent> onUserDeleted;
    private StringBinding normalizedTitleBinding;
    private StringBinding titleValidationMessage;
    private StringBinding normalizedDescriptionBinding;
    private Optional<Boolean> showActiveCustomers;
    private Optional<Boolean> showActiveUsers;
    private boolean editingUserOptions;

    @ModelEditor
    private AppointmentModel model;

    @ModelEditor
    private WaitBorderPane waitBorderPane;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // fx:id="titleTextField"
    private TextField titleTextField; // Value injected by FXMLLoader

    @FXML // fx:id="titleValidationLabel"
    private Label titleValidationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="customerComboBox"
    private ComboBox<CustomerModel> customerComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="userComboBox"
    private ComboBox<UserModel> userComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="checkConflictsButton"
    private Button checkConflictsButton; // Value injected by FXMLLoader

    @FXML // fx:id="showConflictsButton"
    private Button showConflictsButton; // Value injected by FXMLLoader

    @FXML // fx:id="startDatePicker"
    private DatePicker startDatePicker; // Value injected by FXMLLoader

    @FXML // fx:id="startHourTextField"
    private TextField startHourTextField; // Value injected by FXMLLoader

    @FXML // fx:id="startMinuteTextField"
    private TextField startMinuteTextField; // Value injected by FXMLLoader

    @FXML // fx:id="amPmComboBox"
    private ComboBox<Boolean> amPmComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="startValidationLabel"
    private Label startValidationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="durationHourTextField"
    private TextField durationHourTextField; // Value injected by FXMLLoader

    @FXML // fx:id="durationMinuteTextField"
    private TextField durationMinuteTextField; // Value injected by FXMLLoader

    @FXML // fx:id="endDateTimeLabel"
    private Label endDateTimeLabel; // Value injected by FXMLLoader

    @FXML // fx:id="durationValidationLabel"
    private Label durationValidationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="locationLabel"
    private Label locationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="includeRemoteCheckBox"
    private CheckBox includeRemoteCheckBox; // Value injected by FXMLLoader

    @FXML // fx:id="corporateLocationComboBox"
    private ComboBox<CorporateAddress> corporateLocationComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="locationTextArea"
    private TextArea locationTextArea; // Value injected by FXMLLoader

    @FXML // fx:id="phoneTextField"
    private TextField phoneTextField; // Value injected by FXMLLoader

    @FXML // fx:id="implicitLocationLabel"
    private Label implicitLocationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="locationValidationLabel"
    private Label locationValidationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="typeComboBox"
    private ComboBox<AppointmentType> typeComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="contactTextField"
    private TextField contactTextField; // Value injected by FXMLLoader

    @FXML // fx:id="urlTextField"
    private TextField urlTextField; // Value injected by FXMLLoader

    @FXML // fx:id="urlValidationLabel"
    private Label urlValidationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="descriptionTextArea"
    private TextArea descriptionTextArea; // Value injected by FXMLLoader

    @FXML // fx:id="customerValidationLabel"
    private Label customerValidationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="userValidationLabel"
    private Label userValidationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="contactValidationLabel"
    private Label contactValidationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="dropdownOptionsBorderPane"
    private BorderPane dropdownOptionsBorderPane; // Value injected by FXMLLoader

    @FXML // fx:id="dropdownOptionsLabel"
    private Label dropdownOptionsLabel; // Value injected by FXMLLoader

    @FXML // fx:id="dropdownOptionsActiveRadioButton"
    private RadioButton dropdownOptionsActiveRadioButton; // Value injected by FXMLLoader

    @FXML // fx:id="dropdownOptions"
    private ToggleGroup dropdownOptions; // Value injected by FXMLLoader

    @FXML // fx:id="dropdownOptionsInactiveRadioButton"
    private RadioButton dropdownOptionsInactiveRadioButton; // Value injected by FXMLLoader

    @FXML // fx:id="dropdownOptionsAllRadioButton"
    private RadioButton dropdownOptionsAllRadioButton; // Value injected by FXMLLoader

    @FXML // fx:id="appointmentConflicts"
    private BorderPane appointmentConflictsBorderPane; // Value injected by FXMLLoader

    @FXML // fx:id="conflictingAppointmentsTableView"
    private TableView<AppointmentModel> conflictingAppointmentsTableView; // Value injected by FXMLLoader

    public EditAppointment() {
        windowTitle = new ReadOnlyStringWrapper(this, "windowTitle", "");
        valid = new ReadOnlyBooleanWrapper(this, "valid", false);
        modified = new ReadOnlyBooleanWrapper(this, "modified", true);
        typeContext = new TypeContextController();
        customerModelList = FXCollections.observableArrayList();
        userModelList = FXCollections.observableArrayList();
        showActiveCustomers = Optional.of(true);
        showActiveUsers = Optional.of(true);
        onCustomerDeleted = (CustomerSuccessEvent event) -> {
            LOG.entering(LOG.getName(), "onCustomerDeleted", event);
            if (model.getRowState() != DataRowState.NEW) {
                CustomerDAO dao = event.getDataAccessObject();
                int pk = dao.getPrimaryKey();
                customerModelList.stream().filter((t) -> t.getPrimaryKey() == pk).findFirst().ifPresent((t) -> customerModelList.remove(t));
            }
        };
        onUserDeleted = (UserSuccessEvent event) -> {
            LOG.entering(LOG.getName(), "onUserDeleted", event);
            if (model.getRowState() != DataRowState.NEW) {
                UserDAO dao = event.getDataAccessObject();
                int pk = dao.getPrimaryKey();
                customerModelList.stream().filter((t) -> t.getPrimaryKey() == pk).findFirst().ifPresent((t) -> customerModelList.remove(t));
            }
        };
    }

    @ModelEditor
    private void onModelInserted(AppointmentEvent event) {
        LOG.entering(LOG.getName(), "onModelInserted", event);
        windowTitle.set(resources.getString(RESOURCEKEY_EDITAPPOINTMENT));
    }

    @FXML
    void onCloseConflictsBorderPaneButtonAction(ActionEvent event) {
        collapseNode(appointmentConflictsBorderPane);
    }

    @FXML
    void onCustomerDropDownOptionsButtonAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onCustomerDropDownOptionsButtonAction", event);
        editingUserOptions = false;
        if (showActiveCustomers.isPresent()) {
            dropdownOptions.selectToggle((showActiveCustomers.get()) ? dropdownOptionsActiveRadioButton : dropdownOptionsInactiveRadioButton);
        } else {
            dropdownOptions.selectToggle(dropdownOptionsAllRadioButton);
        }
        dropdownOptionsLabel.setText(resources.getString(RESOURCEKEY_CUSTOMERSTOSHOW));
        restoreNode(dropdownOptionsBorderPane);
        dropdownOptionsBorderPane.minWidthProperty().bind(widthProperty());
        dropdownOptionsBorderPane.prefWidthProperty().bind(widthProperty());
        dropdownOptionsBorderPane.minHeightProperty().bind(heightProperty());
        dropdownOptionsBorderPane.prefHeightProperty().bind(heightProperty());
    }

    @FXML
    void onDropdownOptionsCancelButtonAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onDropdownOptionsCancelButtonAction", event);
        dropdownOptionsBorderPane.minWidthProperty().unbind();
        dropdownOptionsBorderPane.prefWidthProperty().unbind();
        dropdownOptionsBorderPane.minHeightProperty().unbind();
        dropdownOptionsBorderPane.prefHeightProperty().unbind();
        collapseNode(dropdownOptionsBorderPane);
    }

    @FXML
    void onDropdownOptionsOkButtonAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onDropdownOptionsOkButtonAction", event);
        dropdownOptionsBorderPane.minWidthProperty().unbind();
        dropdownOptionsBorderPane.prefWidthProperty().unbind();
        dropdownOptionsBorderPane.minHeightProperty().unbind();
        dropdownOptionsBorderPane.prefHeightProperty().unbind();
        if (editingUserOptions) {
            if (dropdownOptionsInactiveRadioButton.isSelected()) {
                showActiveUsers = Optional.of(false);
            } else if (dropdownOptionsAllRadioButton.isSelected()) {
                showActiveUsers = Optional.empty();
            } else {
                showActiveUsers = Optional.of(true);
            }
            waitBorderPane.startNow(new UserReloadTask(null));
        } else {
            if (dropdownOptionsInactiveRadioButton.isSelected()) {
                showActiveCustomers = Optional.of(false);
            } else if (dropdownOptionsAllRadioButton.isSelected()) {
                showActiveCustomers = Optional.empty();
            } else {
                showActiveCustomers = Optional.of(true);
            }
            waitBorderPane.startNow(new CustomerReloadTask(null));
        }
        collapseNode(dropdownOptionsBorderPane);
    }

    @FXML
    void onUserDropDownOptionsButtonAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onUserDropDownOptionsButtonAction", event);
        editingUserOptions = true;
        if (showActiveUsers.isPresent()) {
            dropdownOptions.selectToggle((showActiveUsers.get()) ? dropdownOptionsActiveRadioButton : dropdownOptionsInactiveRadioButton);
        } else {
            dropdownOptions.selectToggle(dropdownOptionsAllRadioButton);
        }
        dropdownOptionsLabel.setText(resources.getString(RESOURCEKEY_USERSTOSHOW));
        restoreNode(dropdownOptionsBorderPane);
        dropdownOptionsBorderPane.minWidthProperty().bind(widthProperty());
        dropdownOptionsBorderPane.prefWidthProperty().bind(widthProperty());
        dropdownOptionsBorderPane.minHeightProperty().bind(heightProperty());
        dropdownOptionsBorderPane.prefHeightProperty().bind(heightProperty());
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert titleTextField != null : "fx:id=\"titleTextField\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert titleValidationLabel != null : "fx:id=\"titleValidationLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert customerComboBox != null : "fx:id=\"customerComboBox\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert customerValidationLabel != null : "fx:id=\"customerValidationLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert userComboBox != null : "fx:id=\"userComboBox\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert userValidationLabel != null : "fx:id=\"userValidationLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert contactTextField != null : "fx:id=\"contactTextField\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert contactValidationLabel != null : "fx:id=\"contactValidationLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert typeComboBox != null : "fx:id=\"typeComboBox\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert startDatePicker != null : "fx:id=\"startDatePicker\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert startHourTextField != null : "fx:id=\"startHourTextField\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert startMinuteTextField != null : "fx:id=\"startMinuteTextField\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert amPmComboBox != null : "fx:id=\"amPmComboBox\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert showConflictsButton != null : "fx:id=\"showConflictsButton\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert checkConflictsButton != null : "fx:id=\"checkConflictsButton\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert startValidationLabel != null : "fx:id=\"startValidationLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert durationHourTextField != null : "fx:id=\"durationHourTextField\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert durationMinuteTextField != null : "fx:id=\"durationMinuteTextField\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert endDateTimeLabel != null : "fx:id=\"endDateTimeLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert durationValidationLabel != null : "fx:id=\"durationValidationLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert locationLabel != null : "fx:id=\"locationLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert includeRemoteCheckBox != null : "fx:id=\"includeRemoteCheckBox\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert corporateLocationComboBox != null : "fx:id=\"corporateLocationComboBox\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert locationTextArea != null : "fx:id=\"locationTextArea\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert phoneTextField != null : "fx:id=\"phoneTextField\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert implicitLocationLabel != null : "fx:id=\"implicitLocationLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert locationValidationLabel != null : "fx:id=\"locationValidationLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert urlTextField != null : "fx:id=\"urlTextField\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert urlValidationLabel != null : "fx:id=\"urlValidationLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert descriptionTextArea != null : "fx:id=\"descriptionTextArea\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert dropdownOptionsBorderPane != null : "fx:id=\"dropdownOptionsBorderPane\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert dropdownOptionsLabel != null : "fx:id=\"dropdownOptionsLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert dropdownOptionsActiveRadioButton != null : "fx:id=\"dropdownOptionsActiveRadioButton\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert dropdownOptions != null : "fx:id=\"dropdownOptions\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert dropdownOptionsInactiveRadioButton != null : "fx:id=\"dropdownOptionsInactiveRadioButton\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert dropdownOptionsAllRadioButton != null : "fx:id=\"dropdownOptionsAllRadioButton\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert appointmentConflictsBorderPane != null : "fx:id=\"appointmentConflictsBorderPane\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert conflictingAppointmentsTableView != null : "fx:id=\"conflictingAppointmentsTableView\" was not injected: check your FXML file 'EditAppointment.fxml'.";

        typeContext.initialize();

        titleTextField.setText(model.getTitle());
        normalizedTitleBinding = BindingHelper.asNonNullAndWsNormalized(titleTextField.textProperty());
        titleValidationMessage = Bindings.when(normalizedTitleBinding.isEmpty())
                .then("* Required")
                .otherwise(
                        Bindings.when(normalizedTitleBinding.length().greaterThan(MAX_LENGTH_TITLE))
                                .then("Title too long")
                                .otherwise("")
                );
        titleValidationLabel.visibleProperty().bind(titleValidationMessage.isNotEmpty());

        customerComboBox.setItems(customerModelList);
        userComboBox.setItems(userModelList);

        descriptionTextArea.setText(model.getDescription());
        normalizedDescriptionBinding = BindingHelper.asNonNullAndWsNormalizedMultiLine(descriptionTextArea.textProperty());

        typeContext.valid.addListener((observable, oldValue, newValue) -> {
            onValidityChanged(titleValidationMessage.get().isEmpty(), newValue);
        });
        titleTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            onValidityChanged(titleValidationMessage.get().isEmpty(), typeContext.valid.get());
        });
        onValidityChanged(titleValidationMessage.get().isEmpty(), typeContext.valid.get());
        if (model.isNewRow()) {
            windowTitle.set(resources.getString(RESOURCEKEY_ADDNEWAPPOINTMENT));
        } else {
            windowTitle.set(resources.getString(RESOURCEKEY_EDITAPPOINTMENT));
        }

        InitializationTask task = new InitializationTask();
        waitBorderPane.startNow(task);
        CustomerModel.FACTORY.addEventHandler(CustomerSuccessEvent.DELETE_SUCCESS, new WeakEventHandler<>(onCustomerDeleted));
        UserModel.FACTORY.addEventHandler(UserSuccessEvent.DELETE_SUCCESS, new WeakEventHandler<>(onUserDeleted));
    }

    @Override
    public EntityModel.EntityModelFactory<AppointmentDAO, AppointmentModel, AppointmentEvent, ? extends AppointmentEvent> modelFactory() {
        return AppointmentModel.FACTORY;
    }

    @Override
    public String getWindowTitle() {
        return windowTitle.get();
    }

    @Override
    public ReadOnlyStringProperty windowTitleProperty() {
        return windowTitle.getReadOnlyProperty();
    }

    @Override
    public boolean isValid() {
        return valid.get();
    }

    @Override
    public ReadOnlyBooleanProperty validProperty() {
        return valid.getReadOnlyProperty();
    }

    @Override
    public boolean isModified() {
        return modified.get();
    }

    @Override
    public ReadOnlyBooleanProperty modifiedProperty() {
        return modified.getReadOnlyProperty();
    }

    @Override
    public void applyChanges() {
        LOG.info("Applying changes");
        model.setTitle(normalizedTitleBinding.get());
        model.setContact(typeContext.normalizedContact.get());
        model.setUrl(typeContext.parsedUrl.get().toPrimary(""));
        model.setDescription(normalizedDescriptionBinding.get());
        model.setCustomer(typeContext.appointmentConflicts.selectedCustomer.get());
        model.setUser(typeContext.appointmentConflicts.selectedUser.get());
        model.setType(typeContext.selectedType.get());
        model.setStart(typeContext.appointmentConflicts.dateRange.startDateTimeValue.get());
        model.setEnd(typeContext.appointmentConflicts.dateRange.endDateTimeValue.get());
        switch (typeContext.selectedType.get()) {
            case CORPORATE_LOCATION:
                model.setLocation(typeContext.selectedCorporateLocation.get().getName());
                break;
            case CUSTOMER_SITE:
                model.setLocation(typeContext.appointmentConflicts.selectedCustomer.get().getMultiLineAddress());
                break;
            case PHONE:
                model.setLocation(typeContext.normalizedPhone.get());
                break;
            default:
                model.setLocation(typeContext.normalizedLocation.get());
                break;
        }
    }

    private synchronized void onCustomersLoaded(List<CustomerDAO> customerDaoList) {
        LOG.info("Invoked scheduler.view.appointment.EditAppointment#customerDaoList");
        CustomerModel selectedItem = customerComboBox.getSelectionModel().getSelectedItem();
        customerModelList.clear();
        if (null != customerDaoList && !customerDaoList.isEmpty()) {
            customerDaoList.forEach((t) -> customerModelList.add(t.cachedModel(true)));
        }
        if (null != selectedItem) {
            int cpk = selectedItem.getPrimaryKey();
            customerModelList.stream().filter((t) -> t.getPrimaryKey() == cpk).findFirst().ifPresent((t)
                    -> customerComboBox.getSelectionModel().select(t));
        }
    }

    private synchronized void onUsersLoaded(List<UserDAO> userDaoList) {
        LOG.info("Invoked scheduler.view.appointment.EditAppointment#onUsersLoaded");
        UserModel selectedItem = userComboBox.getSelectionModel().getSelectedItem();
        if (null != userDaoList && !userDaoList.isEmpty()) {
            userDaoList.forEach((t) -> userModelList.add(t.cachedModel(true)));
        }
        if (null != selectedItem) {
            int cpk = selectedItem.getPrimaryKey();
            userModelList.stream().filter((t) -> t.getPrimaryKey() == cpk).findFirst().ifPresent((t)
                    -> userComboBox.getSelectionModel().select(t));
        }
    }

    private synchronized void onValidityChanged(boolean titleValid, boolean contextValid) {
        boolean v = titleValid && contextValid;
        if (v != valid.get()) {
            valid.set(v);
        }
    }

    private class CustomerReloadTask extends Task<List<CustomerDAO>> {

        private final Optional<Boolean> loadOption;
        private final Connection existingConnection;

        private CustomerReloadTask(Connection existingConnection) {
            updateTitle(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_LOADINGCUSTOMERS));
            loadOption = showActiveCustomers;
            this.existingConnection = existingConnection;
        }

        @Override
        protected void succeeded() {
            LOG.info("Task succeeded");
            Optional<Boolean> currentOption = showActiveCustomers;
            if ((currentOption.isPresent()) ? loadOption.isPresent() && currentOption.get().equals(loadOption.get()) : !loadOption.get()) {
                EditAppointment.this.onCustomersLoaded(getValue());
            }
            super.succeeded();
        }

        @Override
        protected List<CustomerDAO> call() throws Exception {
            LOG.info("Invoked call");
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
            List<CustomerDAO> result;
            if (null == existingConnection) {
                try (DbConnector dbConnector = new DbConnector()) {
                    updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTEDTODB));
                    result = getCustomers(dbConnector.getConnection());
                }
            } else {
                result = getCustomers(existingConnection);
            }
            if (null == result) {
                LOG.info("Returning a null result");
            } else {
                LOG.info(() -> String.format("Returning %d users", result.size()));
            }
            return result;
        }

        private List<CustomerDAO> getCustomers(final Connection connection) throws SQLException {
            List<CustomerDAO> result;
            CustomerDAO.FactoryImpl cf = CustomerDAO.FACTORY;
            if (loadOption.isPresent()) {
                result = cf.load(connection, cf.getActiveStatusFilter(loadOption.get()));
            } else {
                result = cf.load(connection, cf.getAllItemsFilter());
            }
            return result;
        }

    }

    private class UserReloadTask extends Task<List<UserDAO>> {

        private final Optional<Boolean> loadOption;
        private final Connection existingConnection;

        private UserReloadTask(Connection existingConnection) {
            updateTitle(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_LOADINGUSERS));
            loadOption = showActiveUsers;
            this.existingConnection = existingConnection;
        }

        @Override
        protected void succeeded() {
            LOG.info("Invoked scheduler.view.appointment.EditAppointment.UserReloadTask#succeeded");
            Optional<Boolean> currentOption = showActiveUsers;
            if ((currentOption.isPresent()) ? loadOption.isPresent() && currentOption.get().equals(loadOption.get()) : !loadOption.get()) {
                EditAppointment.this.onUsersLoaded(getValue());
            }
            super.succeeded();
        }

        @Override
        protected List<UserDAO> call() throws Exception {
            LOG.info("Invoked scheduler.view.appointment.EditAppointment.UserReloadTask#call");
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
            List<UserDAO> result;
            if (null == existingConnection) {
                try (DbConnector dbConnector = new DbConnector()) {
                    updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTEDTODB));
                    Connection connection = dbConnector.getConnection();
                    result = getUsers(connection);
                }
            } else {
                result = getUsers(existingConnection);
            }
            if (null == result) {
                LOG.info("scheduler.view.appointment.EditAppointment.UserReloadTask#call: Returning a null result");
            } else {
                LOG.info(() -> String.format("scheduler.view.appointment.UserReloadTask.ItemsLoadTask#call: returning %d users", result.size()));
            }
            return result;
        }

        private List<UserDAO> getUsers(Connection connection) throws SQLException {
            List<UserDAO> result;
            UserDAO.FactoryImpl uf = UserDAO.FACTORY;
            if (loadOption.isPresent()) {
                if (loadOption.get()) {
                    result = uf.load(connection, uf.getActiveUsersFilter());
                } else {
                    result = uf.load(connection, UserFilter.of(UserFilter.expressionOf(UserStatus.INACTIVE, ComparisonOperator.EQUALS)));
                }
            } else {
                result = uf.load(connection, uf.getAllItemsFilter());
            }
            return result;
        }

    }

    private class InitializationTask extends Task<List<AppointmentDAO>> {

        private final Customer appointmentCustomer;
        private final User appointmentUser;

        private InitializationTask() {
            updateTitle(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
            PartialCustomerModel<? extends Customer> customer = model.getCustomer();
            appointmentCustomer = (null == customer) ? null : customer.dataObject();
            PartialUserModel<? extends User> user = model.getUser();
            appointmentUser = (null == user) ? null : user.dataObject();
        }

        @Override
        protected synchronized List<AppointmentDAO> call() throws Exception {
            LOG.info("Invoked scheduler.view.appointment.EditAppointment.ItemsLoadTask#call");
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));

            List<AppointmentDAO> result;
            try (DbConnector dbConnector = new DbConnector()) {
                updateMessage(AppResources.getResourceString(RESOURCEKEY_LOADINGCUSTOMERS));
                LOG.fine("Running CustomerReloadTask");
                CustomerReloadTask customerReloadTask = new CustomerReloadTask(dbConnector.getConnection());
                customerReloadTask.run();
                updateMessage(AppResources.getResourceString(RESOURCEKEY_LOADINGUSERS));
                LOG.fine("Running UserReloadTask");
                UserReloadTask userReloadTask = new UserReloadTask(dbConnector.getConnection());
                userReloadTask.run();
                AppointmentDAO.FactoryImpl af = AppointmentDAO.FACTORY;
                LOG.fine("Loading appointments");
                updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_LOADINGAPPOINTMENTS));

                if (null != appointmentCustomer && null != appointmentUser) {
                    result = af.load(dbConnector.getConnection(), AppointmentFilter.of(appointmentCustomer, appointmentUser, null, null));
                } else {
                    result = null;
                }
            }
            LOG.info(() -> (null == result)
                    ? "scheduler.view.appointment.EditAppointment.ItemsLoadTask#call: Returning a null result"
                    : String.format("scheduler.view.appointment.EditAppointment.ItemsLoadTask#call: returning %d appointments", result.size()));
            return result;
        }

        @Override
        protected void succeeded() {
            LOG.info("Invoked scheduler.view.appointment.EditAppointment.ItemsLoadTask#succeeded");
            typeContext.appointmentConflicts.initialize(this);
            super.succeeded();
        }

        @Override
        protected void cancelled() {
            typeContext.appointmentConflicts.initialize(this);
            super.cancelled();
        }

        @Override
        protected void failed() {
            typeContext.appointmentConflicts.initialize(this);
            super.failed();
        }

    }

    private class DateRangeController {

        private final ReadOnlyObjectWrapper<LocalDateTime> startDateTimeValue;
        private final ReadOnlyStringWrapper startValidationMessage;
        private final ReadOnlyObjectWrapper<LocalDateTime> endDateTimeValue;
        private final ReadOnlyObjectWrapper<Tuple<LocalDateTime, LocalDateTime>> range;
        private final ReadOnlyBooleanWrapper valid;
        private ObjectBinding<BinarySelective<Integer, String>> parsedStartHour;
        private ObjectBinding<BinarySelective<Integer, String>> parsedStartMinute;
        private ObjectBinding<BinarySelective<LocalDateTime, String>> startDateTimeBinding;
        private ObjectBinding<BinarySelective<Integer, String>> parsedDurationHour;
        private ObjectBinding<BinarySelective<Integer, String>> parsedDurationMinute;
        private ObjectBinding<BinarySelective<LocalDateTime, String>> endDateTimeBinding;

        DateRangeController() {
            startValidationMessage = new ReadOnlyStringWrapper(this, "startValidationMessage", "");
            startDateTimeValue = new ReadOnlyObjectWrapper<>(this, "startDateTimeValue", null);
            endDateTimeValue = new ReadOnlyObjectWrapper<>(this, "endDateTimeValue", null);
            range = new ReadOnlyObjectWrapper<>(this, "range", null);
            valid = new ReadOnlyBooleanWrapper(this, "valid", false);
        }

        private void initialize() {
            LocalDateTime rangeStart = model.getStart();
            if (null != rangeStart) {
                startDatePicker.setValue(rangeStart.toLocalDate());
                amPmComboBox.setItems(FXCollections.observableArrayList(Boolean.FALSE, Boolean.TRUE));
                int hv = rangeStart.getHour();
                if (hv < 12) {
                    amPmComboBox.getSelectionModel().select(false);
                    startHourTextField.setText(INTN_FORMAT.format((hv > 0) ? hv : 12));
                } else {
                    amPmComboBox.getSelectionModel().select(true);
                    startHourTextField.setText(INTN_FORMAT.format((hv > 12) ? 12 : hv - 12));
                }
                startMinuteTextField.setText(INT2_FORMAT.format(rangeStart.getMinute()));
                LocalDateTime rangeEnd = model.getEnd();
                if (null != rangeEnd) {
                    long h = Duration.between(rangeStart, rangeEnd).toMinutes();
                    long m = h % 60;
                    durationHourTextField.setText(INTN_FORMAT.format(h - m));
                    durationMinuteTextField.setText(INT2_FORMAT.format(m));
                }
            }
            parsedStartHour = Bindings.createObjectBinding(() -> calculateHour(startHourTextField.getText(), 1, 12), startHourTextField.textProperty());
            parsedStartMinute = Bindings.createObjectBinding(() -> calculateMinute(startMinuteTextField.getText()), startMinuteTextField.textProperty());
            startDateTimeBinding = Bindings.createObjectBinding(() -> calculateDateTime(startDatePicker.getValue(), parsedStartHour.get(),
                    parsedStartMinute.get(), amPmComboBox.getSelectionModel().getSelectedItem()), startDatePicker.valueProperty(), parsedStartHour,
                    parsedStartMinute, amPmComboBox.getSelectionModel().selectedItemProperty());
            parsedDurationHour = Bindings.createObjectBinding(() -> calculateHour(durationHourTextField.getText(), 0, 256),
                    durationHourTextField.textProperty());
            parsedDurationMinute = Bindings.createObjectBinding(() -> calculateMinute(durationMinuteTextField.getText()),
                    durationMinuteTextField.textProperty());
            endDateTimeBinding = Bindings.createObjectBinding(() -> calculateEndDateTime(startDateTimeValue.get(), parsedDurationHour.get(),
                    parsedDurationMinute.get()), startDateTimeValue, parsedDurationHour, parsedDurationMinute);
            startDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> checkStartChange());
            startHourTextField.textProperty().addListener((observable, oldValue, newValue) -> checkStartChange());
            startMinuteTextField.textProperty().addListener((observable, oldValue, newValue) -> checkStartChange());
            amPmComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> checkStartChange());
            startDateTimeValue.addListener((observable, oldValue, newValue) -> checkEndChange(Optional.of(newValue)));
            durationHourTextField.textProperty().addListener((observable, oldValue, newValue) -> checkEndChange(Optional.empty()));
            durationMinuteTextField.textProperty().addListener((observable, oldValue, newValue) -> checkEndChange(Optional.empty()));
            endDateTimeValue.addListener((observable, oldValue, newValue) -> checkRangeChange(startDateTimeValue.get(), endDateTimeValue.get()));
            checkStartChange();
            checkEndChange(Optional.empty());
        }

        private synchronized void checkStartChange() {
            startDateTimeBinding.get().accept(
                    (t) -> {
                        if (!t.equals(startDateTimeValue.get())) {
                            startDateTimeValue.set(t);
                        }
                        if (!startValidationMessage.get().isEmpty()) {
                            startValidationMessage.set("");
                        }
                    },
                    (t) -> {
                        if (null != startDateTimeValue.getValue()) {
                            startDateTimeValue.setValue(null);
                        }
                        if (!t.equals(startValidationMessage.get())) {
                            startValidationMessage.set(t);
                        }
                    }
            );
        }

        private synchronized void checkEndChange(Optional<LocalDateTime> startChange) {
            endDateTimeBinding.get().accept(
                    (t) -> {
                        boolean c = !t.equals(endDateTimeValue.get());
                        if (c) {
                            endDateTimeValue.set(t);
                            String s = DATETIME_FORMAT.format(t);
                            if (!endDateTimeLabel.getText().equals(s)) {
                                endDateTimeLabel.setText(s);
                            }
                        }
                        if (!durationValidationLabel.getText().isEmpty()) {
                            durationValidationLabel.setText("");
                        }
                        if (durationValidationLabel.isVisible()) {
                            durationValidationLabel.setVisible(false);
                        }
                        if (!c) {
                            startChange.ifPresent((u) -> checkRangeChange(u, t));
                        }
                    },
                    (t) -> {
                        if (!endDateTimeLabel.getText().isEmpty()) {
                            endDateTimeLabel.setText("");
                        }
                        boolean c = !t.isEmpty();
                        if (durationValidationLabel.isVisible() != c) {
                            durationValidationLabel.setVisible(c);
                        }
                        c = null != endDateTimeValue.get();
                        if (c) {
                            endDateTimeValue.set(null);
                        }
                        if (!t.equals(durationValidationLabel.getText())) {
                            durationValidationLabel.setText(t);
                        }
                        if (!c) {
                            startChange.ifPresent((u) -> checkRangeChange(u, null));
                        }
                    }
            );
        }

        private synchronized void checkRangeChange(LocalDateTime start, LocalDateTime end) {
            Tuple<LocalDateTime, LocalDateTime> oldValue = range.get();
            if (null == start || null == end) {
                if (null != oldValue) {
                    range.set(null);
                    valid.set(false);
                }
            } else if (null == oldValue) {
                range.set(Tuple.of(start, end));
                valid.set(true);
            } else if (!(start.equals(oldValue.getValue1()) && end.equals(oldValue.getValue2()))) {
                range.set(Tuple.of(start, end));
            }
        }

    }

    enum ConflictCheckStatus {
        NOT_CHECKED,
        HAS_CONFLICT,
        NO_CONFLICT
    }

    private class AppointmentConflictsController {

        private final ReadOnlyObjectWrapper<Tuple<CustomerModel, UserModel>> currentParticipants;
        private final ReadOnlyObjectWrapper<ConflictCheckStatus> conflictCheckStatus;
        private final ReadOnlyStringWrapper conflictMessage;
        private final ObservableList<AppointmentModel> allAppointments;
        private final ObservableList<AppointmentModel> conflictingAppointments;
        private final DateRangeController dateRange;
        private final ReadOnlyBooleanWrapper valid;
        private LoadParticipantsAppointmentsTask currentTask = null;
        private ReadOnlyObjectProperty<CustomerModel> selectedCustomer;
        private ReadOnlyObjectProperty<UserModel> selectedUser;

        private AppointmentConflictsController() {
            currentParticipants = new ReadOnlyObjectWrapper<>(this, "currentParticipants", null);
            conflictCheckStatus = new ReadOnlyObjectWrapper<>(this, "conflictCheckStatus", ConflictCheckStatus.NO_CONFLICT);
            conflictMessage = new ReadOnlyStringWrapper("");
            allAppointments = FXCollections.observableArrayList();
            conflictingAppointments = FXCollections.observableArrayList();
            dateRange = new DateRangeController();
            valid = new ReadOnlyBooleanWrapper(this, "valid", false);
            conflictMessage.addListener((observable, oldValue, newValue) -> {
                onStartMessageChanged(dateRange.startValidationMessage.get(), newValue);
            });
            dateRange.startValidationMessage.addListener((observable, oldValue, newValue) -> {
                onStartMessageChanged(newValue, conflictMessage.get());
            });
            currentParticipants.addListener((observable, oldValue, newValue) -> {
                onValidityChanged(null != newValue, dateRange.valid.get());
            });
            dateRange.valid.addListener((observable, oldValue, newValue) -> {
                onValidityChanged(null != currentParticipants.get(), newValue);
            });
        }

        private void initialize() {
            dateRange.initialize();

            final SingleSelectionModel<CustomerModel> customerSelectionModel = customerComboBox.getSelectionModel();
            selectedCustomer = customerSelectionModel.selectedItemProperty();
            selectedUser = userComboBox.getSelectionModel().selectedItemProperty();
            CustomerModel customer = selectedCustomer.get();
            customerValidationLabel.setVisible(null == customer);
            UserModel user = selectedUser.get();
            userValidationLabel.setVisible(null == user);
            if (null != customer && null != user) {
                currentParticipants.set(Tuple.of(customer, user));
                conflictCheckStatus.set(ConflictCheckStatus.NOT_CHECKED);
            }
            selectedCustomer.addListener((observable, oldValue, newValue) -> {
                if (null == newValue) {
                    if (!customerValidationLabel.isVisible()) {
                        customerValidationLabel.setVisible(true);
                    }
                } else if (customerValidationLabel.isVisible()) {
                    customerValidationLabel.setVisible(false);
                }
                onParticipantsChanged(newValue, selectedUser.get());
            });
            selectedUser.addListener((observable, oldValue, newValue) -> {
                if (null == newValue) {
                    if (!userValidationLabel.isVisible()) {
                        userValidationLabel.setVisible(true);
                    }
                } else if (userValidationLabel.isVisible()) {
                    userValidationLabel.setVisible(false);
                }
                onParticipantsChanged(selectedCustomer.get(), newValue);
            });
        }

        private void initialize(Task<List<AppointmentDAO>> task) {
            PartialCustomerModel<? extends Customer> customer = model.getCustomer();
            if (null != customer) {
                int cpk = customer.getPrimaryKey();
                customerModelList.stream().filter((t) -> t.getPrimaryKey() == cpk).findFirst().ifPresent((t)
                        -> customerComboBox.getSelectionModel().select(t));
            }
            PartialUserModel<? extends User> user = model.getUser();
            int upk = (null == user) ? Scheduler.getCurrentUser().getPrimaryKey() : user.getPrimaryKey();
            userModelList.stream().filter((t) -> t.getPrimaryKey() == upk).findFirst().ifPresent((t)
                    -> userComboBox.getSelectionModel().select(t));
            onAppointmentsLoaded(task);
            checkConflictsButton.setDisable(conflictCheckStatus.get() != ConflictCheckStatus.NOT_CHECKED);
            showConflictsButton.setDisable(conflictingAppointments.isEmpty());
            dateRange.range.addListener((observable, oldValue, newValue) -> onRangeChanged(newValue));
            conflictCheckStatus.addListener((observable, oldValue, newValue) -> {
                checkConflictsButton.setDisable(newValue != ConflictCheckStatus.NOT_CHECKED);
            });
            onStartMessageChanged(dateRange.startValidationMessage.get(), conflictMessage.get());
        }

        private void onAppointmentsLoaded(Task<List<AppointmentDAO>> task) {
            if (checkCurrentTask(task)) {
                return;
            }
            List<AppointmentDAO> appointments;
            try {
                appointments = task.get();
            } catch (InterruptedException | ExecutionException ex) {
                LOG.log(Level.SEVERE, "Error getting task result", ex);
                return;
            }
            allAppointments.clear();
            conflictingAppointments.clear();
            if (null != appointments && !appointments.isEmpty()) {
                LOG.fine("Creating appointment models");
                if (model.getRowState() != DataRowState.NEW) {
                    int pk = model.getPrimaryKey();
                    appointments.stream().filter(t -> t.getPrimaryKey() != pk).map((t) -> t.cachedModel(true)).sorted(AppointmentModel::compareByDates)
                            .forEachOrdered((t) -> allAppointments.add(t));
                } else {
                    appointments.stream().map((t) -> t.cachedModel(true)).sorted(AppointmentModel::compareByDates).forEachOrdered((t) -> allAppointments.add(t));
                }
            }
            if (null != dateRange.range.get() && null != currentParticipants.get()) {
                updateConflictingAppointments();
            }
        }

        private synchronized void startLoadParticipantsAppointments(WaitBorderPane waitBorderPane) {
            if (null != currentTask && !currentTask.isDone()) {
                currentTask.cancel(true);
                currentTask = null;
            }
            Tuple<CustomerModel, UserModel> checkParams = currentParticipants.get();
            if (null != checkParams) {
                currentTask = new LoadParticipantsAppointmentsTask(checkParams);
                waitBorderPane.startNow(currentTask);
            }
        }

        private synchronized boolean checkCurrentTask(Task<List<AppointmentDAO>> task) {
            if (Objects.equals(currentTask, task)) {
                currentTask = null;
            } else if (null != currentTask) {
                return true;
            }
            return false;
        }

        private synchronized void onParticipantsChanged(CustomerModel customer, UserModel user) {
            Tuple<CustomerModel, UserModel> lookup = currentParticipants.get();
            String message;
            ConflictCheckStatus checkStatus;
            if (null == customer || null == user) {
                if (null == lookup) {
                    LOG.fine("Participants not actually changed; nothing else to do");
                    return;
                }
                LOG.fine("Customer and/or user not defined; Setting current participants to null");
                currentParticipants.set(null);
                checkStatus = ConflictCheckStatus.NO_CONFLICT;
                message = "";
            } else if (null != lookup && lookup.getValue1().equals(customer) && lookup.getValue2().equals(user)) {
                LOG.fine("Participant values not changed; nothing else to do");
                return;
            } else {
                LOG.fine("Participants changed; setting status to NOT_CHECKED");
                currentParticipants.set(Tuple.of(customer, user));
                checkStatus = ConflictCheckStatus.NOT_CHECKED;
                message = (null == dateRange.range.get()) ? "" : resources.getString(RESOURCEKEY_CONFLICTDATASTALE);
            }
            if (!conflictMessage.get().equals(message)) {
                conflictMessage.set(message);
            }
            if (conflictCheckStatus.get() != checkStatus) {
                conflictCheckStatus.set(checkStatus);
            }
            if (null != currentTask && !currentTask.isDone()) {
                LOG.fine("Superceding existing task");
                currentTask.cancel(true);
                currentTask = null;
            }
        }

        private synchronized void onRangeChanged(Tuple<LocalDateTime, LocalDateTime> range) {
            if (null == range) {
                LOG.fine("Start and/or end not defined");
                if (conflictCheckStatus.get() == ConflictCheckStatus.HAS_CONFLICT) {
                    conflictCheckStatus.set(ConflictCheckStatus.NO_CONFLICT);
                    if (!conflictMessage.get().isEmpty()) {
                        conflictMessage.set("");
                    }
                    conflictingAppointments.clear();
                }
            } else {
                if (allAppointments.isEmpty()) {
                    LOG.fine("Range changed, with no appointments; nothing else to do");
                } else {
                    Tuple<CustomerModel, UserModel> lookup = currentParticipants.get();
                    if (null == lookup) {
                        LOG.fine("Range changed, with no customer and/or user selection; nothing else to do");
                        if (conflictCheckStatus.get() == ConflictCheckStatus.HAS_CONFLICT) {
                            conflictCheckStatus.set(ConflictCheckStatus.NO_CONFLICT);
                            if (!conflictMessage.get().isEmpty()) {
                                conflictMessage.set("");
                            }
                        }
                    } else if (conflictCheckStatus.get() != ConflictCheckStatus.NOT_CHECKED) {
                        updateConflictingAppointments();
                    } else {
                        LOG.fine("Range changed, with status NOT_CHECKED; nothing else to do");
                    }
                }
            }
        }

        private synchronized void updateConflictingAppointments() {
            Tuple<LocalDateTime, LocalDateTime> range = dateRange.range.get();
            LOG.fine("Resetting conflicting appointments list");
            conflictingAppointments.clear();
            LocalDateTime start = range.getValue1();
            LocalDateTime end = range.getValue2();
            allAppointments.stream().filter((a) -> a.getStart().compareTo(end) < 0 && a.getEnd().compareTo(start) > 0).forEachOrdered((t) -> conflictingAppointments.add(t));
            String message;
            ConflictCheckStatus checkStatus;
            if (conflictingAppointments.isEmpty()) {
                LOG.fine("No conflicting appointments");
                message = "";
                checkStatus = ConflictCheckStatus.NO_CONFLICT;
                conflictMessage.set("");
            } else {
                LOG.fine(() -> String.format("%d conflicting appointments", conflictingAppointments.size()));
                checkStatus = ConflictCheckStatus.HAS_CONFLICT;
                Tuple<CustomerModel, UserModel> participants = currentParticipants.get();
                CustomerModel customer = participants.getValue1();
                UserModel user = participants.getValue2();
                int customerCount = (int) conflictingAppointments.stream().filter((t) -> ModelHelper.areSameRecord(customer, t.getCustomer())).count();
                LOG.fine(() -> String.format("%d conflicting appointments for customer", customerCount));
                int userCount = (int) conflictingAppointments.stream().filter((t) -> ModelHelper.areSameRecord(user, t.getUser())).count();
                LOG.fine(() -> String.format("%d conflicting appointments for user", customerCount));
                switch (customerCount) {
                    case 0:
                        switch (userCount) {
                            case 1:
                                message = resources.getString(RESOURCEKEY_CONFLICTUSER1);
                                break;
                            default:
                                message = String.format(resources.getString(RESOURCEKEY_CONFLICTUSERN), userCount);
                                break;
                        }
                        break;
                    case 1:
                        switch (userCount) {
                            case 0:
                                message = resources.getString(RESOURCEKEY_CONFLICTCUSTOMER1);
                                break;
                            case 1:
                                message = resources.getString(RESOURCEKEY_CONFLICTCUSTOMER1USER1);
                                break;
                            default:
                                message = String.format(resources.getString(RESOURCEKEY_CONFLICTCUSTOMER1USERN), userCount);
                                break;
                        }
                        break;
                    default:
                        switch (userCount) {
                            case 0:
                                message = String.format(resources.getString(RESOURCEKEY_CONFLICTCUSTOMERN), customerCount);
                                break;
                            case 1:
                                message = String.format(resources.getString(RESOURCEKEY_CONFLICTCUSTOMERNUSER1), customerCount);
                                break;
                            default:
                                message = String.format(resources.getString(RESOURCEKEY_CONFLICTCUSTOMERNUSERN), customerCount, userCount);
                                break;
                        }
                        break;
                }
            }
            if (!message.equals(conflictMessage.get())) {
                conflictMessage.set(message);
            }
            if (conflictCheckStatus.get() != checkStatus) {
                conflictCheckStatus.set(checkStatus);
            }
        }

        private synchronized void onStartMessageChanged(String errorMessage, String warningMessage) {
            if (errorMessage.isEmpty()) {
                if (warningMessage.isEmpty()) {
                    startValidationLabel.setText("");
                    startValidationLabel.setVisible(false);
                }
                setWarningMessage(startValidationLabel, warningMessage);
            } else {
                setErrorMessage(startValidationLabel, errorMessage);
            }
            startValidationLabel.setVisible(true);
        }

        private synchronized void onValidityChanged(boolean hasParticipants, boolean hasRange) {
            boolean v = hasParticipants && hasRange;
            if (v != valid.get()) {
                valid.set(v);
            }
        }

    }

    private class LoadParticipantsAppointmentsTask extends Task<List<AppointmentDAO>> {

        private final Tuple<CustomerModel, UserModel> participants;

        private LoadParticipantsAppointmentsTask(Tuple<CustomerModel, UserModel> participants) {
            this.participants = participants;
        }

        @Override
        protected List<AppointmentDAO> call() throws Exception {
            LOG.fine("Task started");
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
            AppointmentFilter filter;
            try (DbConnector dbConnector = new DbConnector()) {
                if (isCancelled()) {
                    return null;
                }
                filter = AppointmentFilter.of(AppointmentFilter.expressionOf(participants.getValue1(), participants.getValue2()));
                updateMessage(filter.getLoadingMessage());
                return AppointmentDAO.FACTORY.load(dbConnector.getConnection(), filter);
            }
        }

        @Override
        protected void succeeded() {
            typeContext.appointmentConflicts.onAppointmentsLoaded(this);
            super.succeeded();
        }

        @Override
        protected void cancelled() {
            typeContext.appointmentConflicts.onAppointmentsLoaded(this);
            super.cancelled();
        }

        @Override
        protected void failed() {
            typeContext.appointmentConflicts.onAppointmentsLoaded(this);
            super.failed();
        }

    }

    private class TypeContextController {

        private final ReadOnlyBooleanWrapper valid;
        private final AppointmentConflictsController appointmentConflicts;
        private final ObservableList<CorporateAddress> corporateLocationList;
        private final ObservableList<CorporateAddress> remoteLocationList;
        private StringBinding normalizedContact;
        private ReadOnlyObjectProperty<AppointmentType> selectedType;
        private ReadOnlyObjectProperty<CorporateAddress> selectedCorporateLocation;
        private StringBinding normalizedLocation;
        private StringBinding normalizedPhone;
        private ObjectBinding<BinarySelective<String, String>> parsedUrl;

        private TypeContextController() {
            this.valid = new ReadOnlyBooleanWrapper(this, "valid", false);
            corporateLocationList = FXCollections.observableArrayList();
            remoteLocationList = FXCollections.observableArrayList();
            PredefinedData.getCorporateAddressMap().values().forEach((t) -> {
                if (t.isSatelliteOffice()) {
                    remoteLocationList.add(t);
                } else {
                    corporateLocationList.add(t);
                }
            });
            appointmentConflicts = new AppointmentConflictsController();
        }

        private void initialize() {
            appointmentConflicts.initialize();
            contactTextField.setText(model.getContact());
            StringProperty contactText = contactTextField.textProperty();
            normalizedContact = BindingHelper.asNonNullAndWsNormalized(contactText);

            typeComboBox.setItems(FXCollections.observableArrayList(AppointmentType.values()));
            SingleSelectionModel<AppointmentType> typeSelectionModel = typeComboBox.getSelectionModel();
            typeSelectionModel.select(model.getType());
            selectedType = typeSelectionModel.selectedItemProperty();

            corporateLocationComboBox.setItems(corporateLocationList);
            SingleSelectionModel<CorporateAddress> corporateLocationSelectionModel = corporateLocationComboBox.getSelectionModel();
            selectedCorporateLocation = corporateLocationSelectionModel.selectedItemProperty();

            StringProperty locationText = locationTextArea.textProperty();
            normalizedLocation = BindingHelper.asNonNullAndWsNormalizedMultiLine(locationText);

            StringProperty phoneText = phoneTextField.textProperty();
            normalizedPhone = BindingHelper.asNonNullAndWsNormalizedMultiLine(phoneText);

            urlTextField.setText(model.getUrl());
            parsedUrl = Bindings.createObjectBinding(() -> calculateURL(selectedType.get(), urlTextField.textProperty().get()), selectedType, urlTextField.textProperty());

            String location = model.getLocation();
            switch (selectedType.get()) {
                case CORPORATE_LOCATION:
                    restoreNode(corporateLocationComboBox);
                    collapseNode(locationTextArea);
                    CorporateAddress cl = corporateLocationList.stream().filter((t) -> t.getName().equals(location)).findFirst().orElseGet(() -> {
                        CorporateAddress r = remoteLocationList.stream().filter((u) -> u.getName().equals(location)).findFirst().orElse(null);
                        if (null != r) {
                            includeRemoteCheckBox.setSelected(true);
                            remoteLocationList.forEach((u) -> corporateLocationList.add(u));
                        }
                        return r;
                    });
                    locationValidationLabel.setVisible(null == cl);
                    if (locationValidationLabel.isVisible()) {
                        restoreLabeled(implicitLocationLabel, "(corporate location)");
                    } else {
                        restoreLabeled(implicitLocationLabel, cl.toMultiLineAddress());
                        corporateLocationSelectionModel.select(cl);
                        collapseNode(locationValidationLabel);
                    }
                    break;
                case CUSTOMER_SITE:
                    locationValidationLabel.setVisible(false);
                    collapseNode(locationValidationLabel);
                    collapseNode(locationTextArea);
                    CustomerModel cm = appointmentConflicts.selectedCustomer.get();
                    if (null == cm) {
                        restoreLabeled(implicitLocationLabel, "(customer site)");
                    } else {
                        restoreLabeled(implicitLocationLabel, cm.getMultiLineAddress());
                    }
                    break;
                case PHONE:
                    collapseNode(locationTextArea);
                    restoreNode(phoneTextField);
                    locationLabel.setText(resources.getString(RESOURCEKEY_PHONENUMBER));
                    phoneTextField.setText(location);
                    locationValidationLabel.setVisible(normalizedPhone.get().isEmpty());
                    if (!locationValidationLabel.isVisible()) {
                        collapseNode(locationValidationLabel);
                    }
                    break;
                case VIRTUAL:
                    locationValidationLabel.setVisible(false);
                    collapseNode(locationValidationLabel);
                    break;
                default:
                    locationTextArea.setText(location);
                    locationValidationLabel.setVisible(normalizedLocation.get().isEmpty());
                    if (!locationValidationLabel.isVisible()) {
                        collapseNode(locationValidationLabel);
                    }
                    contactValidationLabel.setVisible(normalizedContact.get().isEmpty());
                    break;
            }
            parsedUrl.get().accept((t) -> {
                urlValidationLabel.setText("");
                urlValidationLabel.setVisible(false);
            }, (t) -> {
                urlValidationLabel.setText(t);
                urlValidationLabel.setVisible(true);
            });
            appointmentConflicts.selectedCustomer.addListener((observable, oldValue, newValue) -> onContextSensitiveChange());
            contactText.addListener((observable, oldValue, newValue) -> onContextSensitiveChange());
            selectedCorporateLocation.addListener((observable, oldValue, newValue) -> onContextSensitiveChange());
            locationText.addListener((observable, oldValue, newValue) -> onContextSensitiveChange());
            phoneText.addListener((observable, oldValue, newValue) -> onContextSensitiveChange());
            urlTextField.textProperty().addListener((observable, oldValue, newValue) -> onUrlChanged());
            appointmentConflicts.valid.addListener((observable, oldValue, newValue) -> {
                onValidityChanged(contactValidationLabel.isVisible(), locationValidationLabel.isVisible(), urlValidationLabel.isVisible(), newValue);
            });
            contactValidationLabel.visibleProperty().addListener((observable, oldValue, newValue) -> {
                onValidityChanged(newValue, locationValidationLabel.isVisible(), urlValidationLabel.isVisible(), appointmentConflicts.valid.get());
            });
            locationValidationLabel.visibleProperty().addListener((observable, oldValue, newValue) -> {
                onValidityChanged(contactValidationLabel.isVisible(), newValue, urlValidationLabel.isVisible(), appointmentConflicts.valid.get());
            });
            urlValidationLabel.visibleProperty().addListener((observable, oldValue, newValue) -> {
                onValidityChanged(contactValidationLabel.isVisible(), locationValidationLabel.isVisible(), newValue, appointmentConflicts.valid.get());
            });
            valid.set(appointmentConflicts.valid.get() && !(contactValidationLabel.isVisible() || locationValidationLabel.isVisible() || urlValidationLabel.isVisible()));
            selectedType.addListener(this::onTypeChanged);
        }

        private synchronized void onContextSensitiveChange() {
            boolean wasInvalid = locationValidationLabel.isVisible();
            switch (selectedType.get()) {
                case CORPORATE_LOCATION:
                    CorporateAddress cl = selectedCorporateLocation.get();
                    locationValidationLabel.setVisible(null == cl);
                    if (locationValidationLabel.isVisible()) {
                        restoreLabeled(implicitLocationLabel, "(corporate location)");
                    } else {
                        restoreLabeled(implicitLocationLabel, cl.toMultiLineAddress());
                    }
                    break;
                case CUSTOMER_SITE:
                    CustomerModel cm = appointmentConflicts.selectedCustomer.get();
                    if (null == cm) {
                        restoreLabeled(implicitLocationLabel, "(customer site)");
                    } else {
                        restoreLabeled(implicitLocationLabel, cm.getMultiLineAddress());
                    }
                    break;
                case PHONE:
                    locationValidationLabel.setVisible(normalizedPhone.get().isEmpty());
                    break;
                case VIRTUAL:
                    onUrlChanged();
                    break;
                default:
                    locationValidationLabel.setVisible(normalizedLocation.get().isEmpty());
                    contactValidationLabel.setVisible(normalizedContact.get().isEmpty());
                    break;
            }
            if (wasInvalid != locationValidationLabel.isVisible()) {
                if (wasInvalid) {
                    collapseNode(locationValidationLabel);
                } else {
                    restoreNode(locationValidationLabel);
                }
            }
        }

        private synchronized void onValidityChanged(boolean rangesAreValid, boolean contactIsInvalid, boolean locationIsInvalid, boolean urlIsInvalid) {
            boolean v = rangesAreValid && !(contactIsInvalid || locationIsInvalid || urlIsInvalid);
            if (v != valid.get()) {
                valid.set(v);
            }
        }

        private synchronized void onUrlChanged() {
            parsedUrl.get().accept((t) -> {
                urlValidationLabel.setText("");
                urlValidationLabel.setVisible(false);
            }, (t) -> {
                urlValidationLabel.setText(t);
                urlValidationLabel.setVisible(true);
            });
        }

        private synchronized void onTypeChanged(ObservableValue<? extends AppointmentType> observable, AppointmentType oldValue, AppointmentType newValue) {
            if (oldValue == newValue) {
                return;
            }
            switch (oldValue) {
                case CORPORATE_LOCATION:
                    if (newValue == AppointmentType.CUSTOMER_SITE) {
                        onContextSensitiveChange();
                        return;
                    }
                    collapseNode(corporateLocationComboBox);
                    collapseNode(implicitLocationLabel);
                    break;
                case CUSTOMER_SITE:
                    if (newValue == AppointmentType.CORPORATE_LOCATION) {
                        restoreNode(corporateLocationComboBox);
                        onContextSensitiveChange();
                        return;
                    }
                    collapseNode(implicitLocationLabel);
                    break;
                case PHONE:
                    collapseNode(phoneTextField);
                    locationLabel.setText(resources.getString(RESOURCEKEY_LOCATIONLABELTEXT));
                    break;
                case VIRTUAL:
                    onUrlChanged();
                    if (newValue == AppointmentType.OTHER) {
                        onContextSensitiveChange();
                        return;
                    }
                    collapseNode(locationTextArea);
                    break;
                default:
                    if (newValue == AppointmentType.VIRTUAL) {
                        locationValidationLabel.setVisible(false);
                        contactValidationLabel.setVisible(false);
                        onContextSensitiveChange();
                        return;
                    }
                    contactValidationLabel.setVisible(false);
                    collapseNode(locationTextArea);
                    break;
            }
            switch (newValue) {
                case CORPORATE_LOCATION:
                    contactValidationLabel.setVisible(false);
                    restoreNode(corporateLocationComboBox);
                    break;
                case CUSTOMER_SITE:
                    contactValidationLabel.setVisible(false);
                    locationValidationLabel.setVisible(false);
                    break;
                case PHONE:
                    contactValidationLabel.setVisible(false);
                    locationLabel.setText(resources.getString(RESOURCEKEY_PHONENUMBER));
                    restoreNode(phoneTextField);
                    break;
                case VIRTUAL:
                    locationValidationLabel.setVisible(false);
                    contactValidationLabel.setVisible(false);
                    restoreNode(locationTextArea);
                    onUrlChanged();
                    break;
                default:
                    restoreNode(locationTextArea);
                    break;
            }
            onContextSensitiveChange();
        }

    }
}
