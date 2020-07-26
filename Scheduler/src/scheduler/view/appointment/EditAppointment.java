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
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
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
import javafx.stage.Stage;
import javafx.stage.Window;
import scheduler.AppResourceKeys;
import scheduler.AppResources;
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
import scheduler.model.ModelHelper.AppointmentHelper;
import scheduler.model.ModelHelper.CustomerHelper;
import scheduler.model.ModelHelper.UserHelper;
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
import static scheduler.util.NodeUtil.clearAndSelectEntity;
import static scheduler.util.NodeUtil.collapseNode;
import static scheduler.util.NodeUtil.isInShownWindow;
import static scheduler.util.NodeUtil.restoreLabeled;
import static scheduler.util.NodeUtil.restoreNode;
import static scheduler.util.NodeUtil.setErrorMessage;
import static scheduler.util.NodeUtil.setWarningMessage;
import scheduler.util.ThrowableConsumer;
import scheduler.util.Tuple;
import scheduler.util.WeakEventHandlingReference;
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
public class EditAppointment extends StackPane implements EditItem.ModelEditorController<AppointmentModel> {

    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(EditAppointment.class.getName()), Level.FINEST);
//    private static final Logger LOG = Logger.getLogger(EditAppointment.class.getName());
    private static final Pattern INT_PATTERN = Pattern.compile("^\\s*\\d{1,9}\\s*");
    private static final String INVALID_NUMBER = "Invalid number";
    public static final NumberFormat INTN_FORMAT;
    public static final NumberFormat INT2_FORMAT;
    public static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL, FormatStyle.FULL).withZone(ZoneId.systemDefault());

    static {
        INT2_FORMAT = NumberFormat.getIntegerInstance();
        INT2_FORMAT.setMinimumIntegerDigits(2);
        INT2_FORMAT.setMaximumIntegerDigits(2);
        INTN_FORMAT = NumberFormat.getIntegerInstance();
    }

    private static BinarySelective<LocalDateTime, String> calculateEndDateTime(LocalDateTime start, BinarySelective<Integer, String> hour, BinarySelective<Integer, String> minute) {
        BinarySelective<LocalDateTime, String> result;
        LOG.entering(LOG.getName(), "calculateEndDateTime", new Object[]{start, hour, minute});
        if (null == start) {
            result = BinarySelective.ofSecondary(hour.toSecondary(minute.toSecondary("")));
        } else {
            result = hour.map(
                    (hv) -> minute.map((mv) -> {
                        if (hv > 0) {
                            if (mv > 0) {
                                return BinarySelective.ofPrimary(start.plusHours(hv).plusMinutes(mv));
                            }
                            return BinarySelective.ofPrimary(start.plusHours(hv));
                        }
                        if (mv > 0) {
                            return BinarySelective.ofPrimary(start.plusMinutes(mv));
                        }
                        return BinarySelective.ofPrimary(start);
                    },
                            (v) -> BinarySelective.ofSecondary(v)
                    ),
                    (u) -> BinarySelective.ofSecondary(u)
            );
        }
        LOG.exiting(LOG.getName(), "calculateEndDateTime", result);
        return result;
    }

    private static BinarySelective<LocalDateTime, String> calculateStartDateTime(LocalDate date, BinarySelective<Integer, String> hour,
            BinarySelective<Integer, String> minute, boolean isPm) {
        LOG.entering(LOG.getName(), "calculateStartDateTime", new Object[]{date, hour, minute, isPm});
        BinarySelective<LocalDateTime, String> result;
        if (null == date) {
            result = BinarySelective.ofSecondary(hour.toSecondary(minute.toSecondary("* Required")));
        } else {
            result = hour.map((hv) -> {
                return minute.map((mv) -> {
                    int h = (isPm) ? ((hv < 12) ? hv + 12 : 12) : ((hv == 12) ? 0 : hv);
                    return BinarySelective.ofPrimary(date.atTime(h, mv));
                },
                        (mm) -> BinarySelective.ofSecondary(mm)
                );
            },
                    (hm) -> BinarySelective.ofSecondary(hm)
            );
        }
        LOG.exiting(LOG.getName(), "calculateStartDateTime", result);
        return result;
    }

    private static BinarySelective<Integer, String> calculateHour(String text, int minValue, int maxValue) {
        LOG.entering(LOG.getName(), "calculateHour", new Object[]{text, minValue, maxValue});
        BinarySelective<Integer, String> result;
        String trimmed;
        if (null == text || (trimmed = text.trim()).isEmpty()) {
            result = BinarySelective.ofSecondary("");
            LOG.exiting(LOG.getName(), "calculateHour", result);
            return result;
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
                result = BinarySelective.ofSecondary("Hour out of range");
            } else {
                result = BinarySelective.ofPrimary(i);
            }
        } catch (ParseException ex) {
            int i = ex.getErrorOffset();
            result = BinarySelective.ofSecondary((i > 0 && i < trimmed.length()) ? String.format("Invalid hour format at position %d", i)
                    : "Invalid hour number");
        }
        LOG.exiting(LOG.getName(), "calculateHour", result);
        return result;
    }

    private static BinarySelective<Integer, String> calculateMinute(String text) {
        LOG.entering(LOG.getName(), "calculateMinute", text);
        BinarySelective<Integer, String> result;
        String trimmed;
        if (null == text || (trimmed = text.trim()).isEmpty()) {
            result = BinarySelective.ofSecondary("");
            LOG.exiting(LOG.getName(), "calculateMinute", result);
            return result;
        }
        try {
            Matcher m = INT_PATTERN.matcher(trimmed);
            if (!m.find()) {
                throw new ParseException("Invalid minute number", text.length() - trimmed.length());
            } else if (m.end() < trimmed.length()) {
                throw new ParseException("Invalid minute number", m.end() + (text.length() - trimmed.length()));
            }
            Number parse = INTN_FORMAT.parse(trimmed);
            int i = parse.intValue();
            if (i < 0 || i > 59) {
                result = BinarySelective.ofSecondary("Minute out of range");
            } else {
                result = BinarySelective.ofPrimary(i);
            }
        } catch (ParseException ex) {
            int i = ex.getErrorOffset();
            result = BinarySelective.ofSecondary((i > 0 && i < trimmed.length()) ? String.format("Invalid minute format at position %d", i)
                    : "Invalid minute number");
        }
        LOG.exiting(LOG.getName(), "calculateMinute", result);
        return result;
    }

    private static BinarySelective<String, String> calculateURL(AppointmentType type, String text) {
        LOG.entering(LOG.getName(), "calculateURL", new Object[]{type, text});
        BinarySelective<String, String> result;
        if (null == text || (text = text.trim()).isEmpty()) {
            if (type == AppointmentType.VIRTUAL) {
                result = BinarySelective.ofSecondary("* Required");
            } else {
                result = BinarySelective.ofPrimaryNullable(null);
            }
            LOG.exiting(LOG.getName(), "calculateURL", result);
            return result;
        }
        URI uri;
        try {
            uri = new URI(text);
        } catch (URISyntaxException ex) {
            LOG.log(Level.WARNING, String.format("Error parsing url %s", text), ex);
            text = ex.getMessage();
            result = BinarySelective.ofSecondary((null == text || text.trim().isEmpty()) ? "Invalid URL" : text);
            LOG.exiting(LOG.getName(), "calculateURL", result);
            return result;
        }
        URL url;
        try {
            url = uri.toURL();
        } catch (MalformedURLException | IllegalArgumentException ex) {
            LOG.log(Level.WARNING, String.format("Error converting uri %s", text), ex);
            text = ex.getMessage();
            result = BinarySelective.ofSecondary((null == text || text.trim().isEmpty()) ? "Invalid URL" : text);
            LOG.exiting(LOG.getName(), "calculateURL", result);
            return result;
        }
        if ((text = url.toString()).length() > MAX_LENGTH_URL) {
            result = BinarySelective.ofSecondary("URL too long");
        } else {
            result = BinarySelective.ofPrimary(text);
        }
        LOG.exiting(LOG.getName(), "calculateURL", result);
        return result;
    }

    public static void editNew(PartialCustomerModel<? extends Customer> customer, PartialUserModel<? extends User> user,
            Window parentWindow, boolean keepOpen, Consumer<AppointmentModel> beforeShow) throws IOException {
        AppointmentModel model = AppointmentDAO.FACTORY.createNew().cachedModel(true);
        if (null != customer) {
            model.setCustomer(customer);
        }
        if (null != user) {
            model.setUser(user);
        }
        if (null != beforeShow) {
            beforeShow.accept(model);
        }
        EditItem.showAndWait(parentWindow, EditAppointment.class, model, keepOpen);
    }

    public static void editNew(PartialCustomerModel<? extends Customer> customer, PartialUserModel<? extends User> user,
            Window parentWindow, boolean keepOpen) throws IOException {
        editNew(customer, user, parentWindow, keepOpen, null);
    }

    public static void edit(AppointmentModel model, Window parentWindow, ThrowableConsumer<Stage, IOException> beforeShow) throws IOException {
        EditItem.showAndWait(parentWindow, EditAppointment.class, model, false, beforeShow);
    }

    public static void edit(AppointmentModel model, Window parentWindow) throws IOException {
        edit(model, parentWindow, null);
    }

    //<editor-fold defaultstate="collapsed" desc="Instance Fields">
    private final ReadOnlyBooleanWrapper valid;
    private final ReadOnlyBooleanWrapper modified;
    private final ReadOnlyStringWrapper windowTitle;
    private final ObservableList<CustomerModel> customerModelList;
    private final ObservableList<UserModel> userModelList;
    private final TypeContextController typeContext;
    private final WeakEventHandlingReference<CustomerSuccessEvent> customerInsertEventHandler;
    private final WeakEventHandlingReference<CustomerSuccessEvent> customerUpdateEventHandler;
    private final WeakEventHandlingReference<CustomerSuccessEvent> customerDeleteEventHandler;
    private final WeakEventHandlingReference<UserSuccessEvent> userInsertEventHandler;
    private final WeakEventHandlingReference<UserSuccessEvent> userUpdateEventHandler;
    private final WeakEventHandlingReference<UserSuccessEvent> userDeleteEventHandler;
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

    @FXML // fx:id="hideConflictsButton"
    private Button hideConflictsButton; // Value injected by FXMLLoader

    //</editor-fold>
    public EditAppointment() {
        windowTitle = new ReadOnlyStringWrapper(this, "windowTitle", "");
        valid = new ReadOnlyBooleanWrapper(this, "valid", false);
        modified = new ReadOnlyBooleanWrapper(this, "modified", true);
        typeContext = new TypeContextController();
        customerModelList = FXCollections.observableArrayList();
        userModelList = FXCollections.observableArrayList();
        showActiveCustomers = Optional.of(true);
        showActiveUsers = Optional.of(true);
        customerUpdateEventHandler = WeakEventHandlingReference.create(this::onCustomerUpdated);
        customerInsertEventHandler = WeakEventHandlingReference.create(this::onCustomerInserted);
        customerDeleteEventHandler = WeakEventHandlingReference.create(this::onCustomerDeleted);
        userUpdateEventHandler = WeakEventHandlingReference.create(this::onUserUpdated);
        userInsertEventHandler = WeakEventHandlingReference.create(this::onUserInserted);
        userDeleteEventHandler = WeakEventHandlingReference.create(this::onUserDeleted);
    }

    @ModelEditor
    private void onModelInserted(AppointmentEvent event) {
        LOG.entering(LOG.getName(), "onModelInserted", event);
        windowTitle.set(resources.getString(RESOURCEKEY_EDITAPPOINTMENT));
        LOG.exiting(LOG.getName(), "onModelInserted");
    }

    @FXML
    private void onCustomerDropDownOptionsButtonAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onCustomerDropDownOptionsButtonAction", event);
        editingUserOptions = false;
        if (showActiveCustomers.orElse(editingUserOptions)) {
            dropdownOptions.selectToggle((showActiveCustomers.get()) ? dropdownOptionsInactiveRadioButton : dropdownOptionsActiveRadioButton);
        } else {
            dropdownOptions.selectToggle(dropdownOptionsAllRadioButton);
        }
        dropdownOptionsLabel.setText(resources.getString(RESOURCEKEY_CUSTOMERSTOSHOW));
        restoreNode(dropdownOptionsBorderPane);
        dropdownOptionsBorderPane.minWidthProperty().bind(widthProperty());
        dropdownOptionsBorderPane.prefWidthProperty().bind(widthProperty());
        dropdownOptionsBorderPane.minHeightProperty().bind(heightProperty());
        dropdownOptionsBorderPane.prefHeightProperty().bind(heightProperty());
        LOG.exiting(LOG.getName(), "onCustomerDropDownOptionsButtonAction");
    }

    @FXML
    private void onDropdownOptionsCancelButtonAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onDropdownOptionsCancelButtonAction", event);
        dropdownOptionsBorderPane.minWidthProperty().unbind();
        dropdownOptionsBorderPane.prefWidthProperty().unbind();
        dropdownOptionsBorderPane.minHeightProperty().unbind();
        dropdownOptionsBorderPane.prefHeightProperty().unbind();
        collapseNode(dropdownOptionsBorderPane);
        LOG.exiting(LOG.getName(), "onDropdownOptionsCancelButtonAction");
    }

    @FXML
    private void onDropdownOptionsOkButtonAction(ActionEvent event) {
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
        LOG.exiting(LOG.getName(), "onDropdownOptionsOkButtonAction");
    }

    @FXML
    private void onUserDropDownOptionsButtonAction(ActionEvent event) {
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
        LOG.exiting(LOG.getName(), "onUserDropDownOptionsButtonAction");
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    private void initialize() {
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
        assert hideConflictsButton != null : "fx:id=\"hideConflictsButton\" was not injected: check your FXML file 'EditAppointment.fxml'.";

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
            onValidityChanged(newValue.isEmpty(), typeContext.valid.get());
        });
        onValidityChanged(titleValidationMessage.get().isEmpty(), typeContext.valid.get());
        if (model.isNewRow()) {
            windowTitle.set(resources.getString(RESOURCEKEY_ADDNEWAPPOINTMENT));
        } else {
            windowTitle.set(resources.getString(RESOURCEKEY_EDITAPPOINTMENT));
        }

        InitializationTask task = new InitializationTask();
        waitBorderPane.startNow(task);
        LOG.exiting(LOG.getName(), "initialize");
    }

    @Override
    public EntityModel.EntityModelFactory<AppointmentDAO, AppointmentModel> modelFactory() {
        return AppointmentModel.FACTORY;
    }

    //<editor-fold defaultstate="collapsed" desc="Properties">
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

    //</editor-fold>
    @Override
    public void applyChanges() {
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
        CustomerModel selectedItem = typeContext.appointmentConflicts.selectedCustomer.get();
        customerModelList.clear();
        if (null != customerDaoList && !customerDaoList.isEmpty()) {
            customerDaoList.forEach((t) -> customerModelList.add(t.cachedModel(true)));
        }
        clearAndSelectEntity(customerComboBox, selectedItem);
    }

    private synchronized void onUsersLoaded(List<UserDAO> userDaoList) {
        UserModel selectedItem = typeContext.appointmentConflicts.selectedUser.get();
        if (null != userDaoList && !userDaoList.isEmpty()) {
            userDaoList.forEach((t) -> userModelList.add(t.cachedModel(true)));
        }
        clearAndSelectEntity(userComboBox, selectedItem);
    }

    private synchronized void onValidityChanged(boolean titleValid, boolean contextValid) {
        LOG.entering(LOG.getName(), "onValidityChanged", new Object[]{titleValid, contextValid});
        boolean v = titleValid && contextValid;
        if (v != valid.get()) {
            valid.set(v);
        }
        LOG.exiting(LOG.getName(), "onValidityChanged");
    }

    private void onCustomerInserted(CustomerSuccessEvent event) {
        LOG.entering(LOG.getName(), "onCustomerInserted", event);
        if (isInShownWindow(this)) {
            CustomerModel entityModel = event.getEntityModel();
            if (entityModel.isActive() == showActiveCustomers.orElse(entityModel.isActive())) {
                customerModelList.add(entityModel);
                customerModelList.sort(CustomerHelper::compare);
                clearAndSelectEntity(customerComboBox, entityModel);
            }
        }
        LOG.exiting(LOG.getName(), "onCustomerInserted");
    }

    private void onCustomerUpdated(CustomerSuccessEvent event) {
        LOG.entering(LOG.getName(), "onCustomerUpdated", event);
        if (isInShownWindow(this)) {
            int pk = event.getEntityModel().getPrimaryKey();
            if (customerModelList.stream().filter((t) -> t.getPrimaryKey() == pk).findAny().map((t) -> {
                if (showActiveUsers.map((u) -> u != t.isActive()).orElse(true)) {
                    CustomerModel selectedItem = typeContext.appointmentConflicts.selectedCustomer.get();
                    if (null != selectedItem && selectedItem == t) {
                        customerComboBox.getSelectionModel().clearSelection();
                        customerModelList.remove(t);
                    }
                }
                return false;
            }).orElse(true)) {
                CustomerModel entityModel = event.getEntityModel();
                if (showActiveUsers.map((t) -> t == entityModel.isActive()).orElse(true)) {
                    customerModelList.add(entityModel);
                    customerModelList.sort(CustomerHelper::compare);
                }
            }
        }
        LOG.exiting(LOG.getName(), "onCustomerUpdated");
    }

    private void onCustomerDeleted(CustomerSuccessEvent event) {
        LOG.entering(LOG.getName(), "onCustomerDeleted", event);
        if (isInShownWindow(this)) {
            int pk = event.getEntityModel().getPrimaryKey();
            customerModelList.stream().filter((t) -> t.getPrimaryKey() == pk).findFirst().ifPresent((t) -> {
                CustomerModel selectedItem = typeContext.appointmentConflicts.selectedCustomer.get();
                if (null != selectedItem && selectedItem == t) {
                    customerComboBox.getSelectionModel().clearSelection();
                }
                customerModelList.remove(t);
            });
        }
        LOG.exiting(LOG.getName(), "onCustomerDeleted");
    }

    private void onUserInserted(UserSuccessEvent event) {
        LOG.entering(LOG.getName(), "onUserInserted", event);
        if (isInShownWindow(this)) {
            UserModel entityModel = event.getEntityModel();
            boolean isActive = entityModel.getStatus() != UserStatus.INACTIVE;
            if (isActive == showActiveUsers.orElse(isActive)) {
                userModelList.add(entityModel);
                userModelList.sort(UserHelper::compare);
                clearAndSelectEntity(userComboBox, entityModel);
            }
        }
        LOG.exiting(LOG.getName(), "onUserInserted");
    }

    private void onUserUpdated(UserSuccessEvent event) {
        LOG.entering(LOG.getName(), "onUserUpdated", event);
        if (isInShownWindow(this)) {
            int pk = event.getEntityModel().getPrimaryKey();
            if (userModelList.stream().filter((t) -> t.getPrimaryKey() == pk).findAny().map((t) -> {
                if (showActiveUsers.map((u) -> u != (t.getStatus() != UserStatus.INACTIVE)).orElse(true)) {
                    UserModel selectedItem = typeContext.appointmentConflicts.selectedUser.get();
                    if (null != selectedItem && selectedItem == t) {
                        userComboBox.getSelectionModel().clearSelection();
                        userModelList.remove(t);
                    }
                }
                return false;
            }).orElse(true)) {
                UserModel entityModel = event.getEntityModel();
                if (showActiveUsers.map((t) -> t == (entityModel.getStatus() != UserStatus.INACTIVE)).orElse(true)) {
                    userModelList.add(entityModel);
                    userModelList.sort(UserHelper::compare);
                }
            }
        }
        LOG.exiting(LOG.getName(), "onUserUpdated");
    }

    private void onUserDeleted(UserSuccessEvent event) {
        LOG.entering(LOG.getName(), "onUserDeleted", event);
        if (isInShownWindow(this)) {
            int pk = event.getEntityModel().getPrimaryKey();
            userModelList.stream().filter((t) -> t.getPrimaryKey() == pk).findFirst().ifPresent((t) -> {
                UserModel selectedItem = typeContext.appointmentConflicts.selectedUser.get();
                if (null != selectedItem && selectedItem == t) {
                    userComboBox.getSelectionModel().clearSelection();
                }
                userModelList.remove(t);
            });
        }
        LOG.exiting(LOG.getName(), "onUserDeleted");
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
            LOG.entering("scheduler.view.appointment.EditAppointment.CustomerReloadTask", "succeeded");
            Optional<Boolean> currentOption = showActiveCustomers;
            if ((currentOption.isPresent()) ? loadOption.isPresent() && currentOption.get().equals(loadOption.get()) : !loadOption.get()) {
                EditAppointment.this.onCustomersLoaded(getValue());
            }
            super.succeeded();
            LOG.exiting("scheduler.view.appointment.EditAppointment.CustomerReloadTask", "succeeded");
        }

        @Override
        protected List<CustomerDAO> call() throws Exception {
            LOG.entering("scheduler.view.appointment.EditAppointment.CustomerReloadTask", "call");
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
            LOG.exiting("scheduler.view.appointment.EditAppointment.CustomerReloadTask", "call");
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
            LOG.entering("scheduler.view.appointment.EditAppointment.UserReloadTask", "succeeded");
            Optional<Boolean> currentOption = showActiveUsers;
            if ((currentOption.isPresent()) ? loadOption.isPresent() && currentOption.get().equals(loadOption.get()) : !loadOption.get()) {
                EditAppointment.this.onUsersLoaded(getValue());
            }
            super.succeeded();
            LOG.exiting("scheduler.view.appointment.EditAppointment.UserReloadTask", "succeeded");
        }

        @Override
        protected List<UserDAO> call() throws Exception {
            LOG.entering("scheduler.view.appointment.EditAppointment.UserReloadTask", "call");
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
            LOG.exiting("scheduler.view.appointment.EditAppointment.UserReloadTask", "call");
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
            LOG.entering("scheduler.view.appointment.EditAppointment.InitializationTask", "call");
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
            LOG.exiting("scheduler.view.appointment.EditAppointment.InitializationTask", "call");
            return result;
        }

        @Override
        protected void succeeded() {
            LOG.entering("scheduler.view.appointment.EditAppointment.InitializationTask", "succeeded");
            typeContext.appointmentConflicts.initialize(this);
            super.succeeded();
            LOG.exiting("scheduler.view.appointment.EditAppointment.InitializationTask", "succeeded");
        }

        @Override
        protected void cancelled() {
            LOG.entering("scheduler.view.appointment.EditAppointment.InitializationTask", "cancelled");
            typeContext.appointmentConflicts.initialize(this);
            super.cancelled();
            LOG.exiting("scheduler.view.appointment.EditAppointment.InitializationTask", "cancelled");
        }

        @Override
        protected void failed() {
            LOG.entering("scheduler.view.appointment.EditAppointment.InitializationTask", "failed");
            typeContext.appointmentConflicts.initialize(this);
            super.failed();
            LOG.exiting("scheduler.view.appointment.EditAppointment.InitializationTask", "failed");
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
            LOG.entering("scheduler.view.appointment.EditAppointment.DateRangeController", "initialize");
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
                    durationHourTextField.setText(INTN_FORMAT.format((h - m) / 60));
                    durationMinuteTextField.setText(INT2_FORMAT.format(m));
                }
            }
            parsedStartHour = Bindings.createObjectBinding(() -> calculateHour(startHourTextField.getText(), 1, 12), startHourTextField.textProperty());
            parsedStartMinute = Bindings.createObjectBinding(() -> calculateMinute(startMinuteTextField.getText()), startMinuteTextField.textProperty());
            startDateTimeBinding = Bindings.createObjectBinding(() -> calculateStartDateTime(startDatePicker.getValue(), parsedStartHour.get(),
                    parsedStartMinute.get(), amPmComboBox.getSelectionModel().getSelectedItem()), startDatePicker.valueProperty(), parsedStartHour,
                    parsedStartMinute, amPmComboBox.getSelectionModel().selectedItemProperty());
            parsedDurationHour = Bindings.createObjectBinding(() -> calculateHour(durationHourTextField.getText(), 0, 256),
                    durationHourTextField.textProperty());
            parsedDurationMinute = Bindings.createObjectBinding(() -> calculateMinute(durationMinuteTextField.getText()),
                    durationMinuteTextField.textProperty());
            endDateTimeBinding = Bindings.createObjectBinding(() -> calculateEndDateTime(startDateTimeValue.get(), parsedDurationHour.get(),
                    parsedDurationMinute.get()), startDateTimeValue, parsedDurationHour, parsedDurationMinute);
            startDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
                LOG.entering("scheduler.view.appointment.EditAppointment.startDatePicker#value", "changed", new Object[]{oldValue, newValue});
                checkStartChange();
                LOG.exiting("scheduler.view.appointment.EditAppointment.startDatePicker#value", "changed");
            });
            startHourTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                LOG.entering("scheduler.view.appointment.EditAppointment.startHourTextField#text", "changed", new Object[]{oldValue, newValue});
                checkStartChange();
                LOG.exiting("scheduler.view.appointment.EditAppointment.startHourTextField#text", "changed");
            });
            startMinuteTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                LOG.entering("scheduler.view.appointment.EditAppointment.startMinuteTextField#text", "changed", new Object[]{oldValue, newValue});
                checkStartChange();
                LOG.exiting("scheduler.view.appointment.EditAppointment.startMinuteTextField#text", "changed");
            });
            amPmComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                LOG.entering("scheduler.view.appointment.EditAppointment.amPmComboBox#value", "changed", new Object[]{oldValue, newValue});
                checkStartChange();
                LOG.exiting("scheduler.view.appointment.EditAppointment.amPmComboBox#value", "changed");
            });
            startDateTimeValue.addListener((observable, oldValue, newValue) -> {
                LOG.entering("scheduler.view.appointment.EditAppointment.startDateTimeValue#value", "changed", new Object[]{oldValue, newValue});
                checkEndChange(Optional.of(newValue));
                LOG.exiting("scheduler.view.appointment.EditAppointment.startDateTimeValue#value", "changed");
            });
            durationHourTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                LOG.entering("scheduler.view.appointment.EditAppointment.durationHourTextField#text", "changed", new Object[]{oldValue, newValue});
                checkEndChange(Optional.empty());
                LOG.exiting("scheduler.view.appointment.EditAppointment.durationHourTextField#text", "changed");
            });
            durationMinuteTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                LOG.entering("scheduler.view.appointment.EditAppointment.durationMinuteTextField#text", "changed", new Object[]{oldValue, newValue});
                checkEndChange(Optional.empty());
                LOG.exiting("scheduler.view.appointment.EditAppointment.durationMinuteTextField#text", "changed");
            });
            endDateTimeValue.addListener((observable, oldValue, newValue) -> {
                LOG.entering("scheduler.view.appointment.EditAppointment.endDateTimeValue#value", "changed", new Object[]{oldValue, newValue});
                checkRangeChange(startDateTimeValue.get(), endDateTimeValue.get());
                LOG.exiting("scheduler.view.appointment.EditAppointment.endDateTimeValue#value", "changed");
            });
            checkStartChange();
            checkEndChange(Optional.empty());
            LOG.exiting("scheduler.view.appointment.EditAppointment.DateRangeController", "initialize");
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
            LOG.entering("scheduler.view.appointment.EditAppointment.AppointmentConflictsController", "initialize");
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
                LOG.entering("scheduler.view.appointment.EditAppointment.AppointmentConflictsController.selectedCustomer#value", "changed", new Object[]{oldValue, newValue});
                if (null == newValue) {
                    if (!customerValidationLabel.isVisible()) {
                        customerValidationLabel.setVisible(true);
                    }
                } else if (customerValidationLabel.isVisible()) {
                    customerValidationLabel.setVisible(false);
                }
                onParticipantsChanged(newValue, selectedUser.get());
                LOG.exiting("scheduler.view.appointment.EditAppointment.AppointmentConflictsController.selectedCustomer#value", "changed");
            });
            selectedUser.addListener((observable, oldValue, newValue) -> {
                LOG.entering("scheduler.view.appointment.EditAppointment.AppointmentConflictsController.selectedUser#value", "changed", new Object[]{oldValue, newValue});
                if (null == newValue) {
                    if (!userValidationLabel.isVisible()) {
                        userValidationLabel.setVisible(true);
                    }
                } else if (userValidationLabel.isVisible()) {
                    userValidationLabel.setVisible(false);
                }
                onParticipantsChanged(selectedCustomer.get(), newValue);
                LOG.exiting("scheduler.view.appointment.EditAppointment.AppointmentConflictsController.selectedUser#value", "changed");
            });
            checkConflictsButton.setOnAction(this::onCheckConflictsButtonAction);
            showConflictsButton.setOnAction(this::onShowConflictsButtonAction);
            hideConflictsButton.setOnAction(this::onHideConflictsButtonAction);
            LOG.exiting("scheduler.view.appointment.EditAppointment.AppointmentConflictsController", "initialize");
        }

        private void initialize(Task<List<AppointmentDAO>> task) {
            clearAndSelectEntity(customerComboBox, model.getCustomer());
            clearAndSelectEntity(userComboBox, model.getUser());
            onAppointmentsLoaded(task);
            checkConflictsButton.setDisable(conflictCheckStatus.get() != ConflictCheckStatus.NOT_CHECKED);
            dateRange.range.addListener((observable, oldValue, newValue) -> onRangeChanged(newValue));
            conflictCheckStatus.addListener((observable, oldValue, newValue) -> {
                checkConflictsButton.setDisable(newValue != ConflictCheckStatus.NOT_CHECKED);
            });
            onStartMessageChanged(dateRange.startValidationMessage.get(), conflictMessage.get());
            CustomerModel.FACTORY.addEventHandler(CustomerSuccessEvent.INSERT_SUCCESS, customerInsertEventHandler.getWeakEventHandler());
            CustomerModel.FACTORY.addEventHandler(CustomerSuccessEvent.UPDATE_SUCCESS, customerUpdateEventHandler.getWeakEventHandler());
            CustomerModel.FACTORY.addEventHandler(CustomerSuccessEvent.DELETE_SUCCESS, customerDeleteEventHandler.getWeakEventHandler());
            UserModel.FACTORY.addEventHandler(UserSuccessEvent.INSERT_SUCCESS, userInsertEventHandler.getWeakEventHandler());
            UserModel.FACTORY.addEventHandler(UserSuccessEvent.UPDATE_SUCCESS, userUpdateEventHandler.getWeakEventHandler());
            UserModel.FACTORY.addEventHandler(UserSuccessEvent.DELETE_SUCCESS, userDeleteEventHandler.getWeakEventHandler());
        }

        private synchronized void onCheckConflictsButtonAction(ActionEvent event) {
            LOG.entering(LOG.getName(), "onCheckConflictsButtonAction", event);
            Tuple<CustomerModel, UserModel> p = currentParticipants.get();
            if (null != p) {
                if (null != currentTask && !currentTask.isDone()) {
                    currentTask.cancel(true);
                }
                checkConflictsButton.setDisable(true);
                currentTask = new LoadParticipantsAppointmentsTask(p);
                waitBorderPane.startNow(currentTask);
            }
            LOG.exiting(LOG.getName(), "onCheckConflictsButtonAction");
        }

        private synchronized void onShowConflictsButtonAction(ActionEvent event) {
            LOG.entering(LOG.getName(), "onShowConflictsButtonAction", event);
            if (!conflictingAppointments.isEmpty()) {
                restoreNode(appointmentConflictsBorderPane);
            }
            LOG.exiting(LOG.getName(), "onShowConflictsButtonAction");
        }

        private synchronized void onHideConflictsButtonAction(ActionEvent event) {
            LOG.entering(LOG.getName(), "onHideConflictsButtonAction", event);
            collapseNode(appointmentConflictsBorderPane);
            LOG.exiting(LOG.getName(), "onHideConflictsButtonAction");
        }

        private synchronized void onAppointmentsLoaded(Task<List<AppointmentDAO>> task) {
            if (checkCurrentTask(task)) {
                return;
            }
            List<AppointmentDAO> appointments;
            try {
                appointments = task.get();
            } catch (InterruptedException | ExecutionException ex) {
                LOG.log(Level.SEVERE, "Error getting task result", ex);
                allAppointments.clear();
                conflictingAppointments.clear();
                if (conflictCheckStatus.get() == ConflictCheckStatus.NO_CONFLICT) {
                    conflictCheckStatus.set(ConflictCheckStatus.NOT_CHECKED);
                }
                showConflictsButton.setDisable(true);
                return;
            }
            allAppointments.clear();
            conflictingAppointments.clear();
            if (null != appointments && !appointments.isEmpty()) {
                LOG.fine("Creating appointment models");
                if (model.getRowState() != DataRowState.NEW) {
                    int pk = model.getPrimaryKey();
                    appointments.stream().filter(t -> t.getPrimaryKey() != pk).map((t) -> t.cachedModel(true)).sorted(AppointmentHelper::compareByDates)
                            .forEachOrdered((t) -> allAppointments.add(t));
                } else {
                    appointments.stream().map((t) -> t.cachedModel(true)).sorted(AppointmentHelper::compareByDates).forEachOrdered((t) -> allAppointments.add(t));
                }
            }
            if (null != dateRange.range.get() && null != currentParticipants.get()) {
                updateConflictingAppointments();
            } else {
                showConflictsButton.setDisable(true);
                if (conflictCheckStatus.get() != ConflictCheckStatus.NO_CONFLICT) {
                    conflictCheckStatus.set(ConflictCheckStatus.NO_CONFLICT);
                }
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
            showConflictsButton.setDisable(true);
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
                showConflictsButton.setDisable(true);
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
                showConflictsButton.setDisable(false);
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
            LOG.entering("scheduler.view.appointment.EditAppointment.LoadParticipantsAppointmentsTask", "call");
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
            AppointmentFilter filter;
            try (DbConnector dbConnector = new DbConnector()) {
                if (isCancelled()) {
                    return null;
                }
                filter = AppointmentFilter.of(AppointmentFilter.expressionOf(participants.getValue1(), participants.getValue2()));
                updateMessage(filter.getLoadingMessage());
                LOG.exiting("scheduler.view.appointment.EditAppointment.LoadParticipantsAppointmentsTask", "call");
                return AppointmentDAO.FACTORY.load(dbConnector.getConnection(), filter);
            }
        }

        @Override
        protected void succeeded() {
            LOG.entering("scheduler.view.appointment.EditAppointment.LoadParticipantsAppointmentsTask", "succeeded");
            typeContext.appointmentConflicts.onAppointmentsLoaded(this);
            super.succeeded();
            LOG.exiting("scheduler.view.appointment.EditAppointment.LoadParticipantsAppointmentsTask", "succeeded");
        }

        @Override
        protected void cancelled() {
            LOG.entering("scheduler.view.appointment.EditAppointment.LoadParticipantsAppointmentsTask", "cancelled");
            typeContext.appointmentConflicts.onAppointmentsLoaded(this);
            super.cancelled();
            LOG.exiting("scheduler.view.appointment.EditAppointment.LoadParticipantsAppointmentsTask", "cancelled");
        }

        @Override
        protected void failed() {
            LOG.entering("scheduler.view.appointment.EditAppointment.LoadParticipantsAppointmentsTask", "failed");
            typeContext.appointmentConflicts.onAppointmentsLoaded(this);
            super.failed();
            LOG.exiting("scheduler.view.appointment.EditAppointment.LoadParticipantsAppointmentsTask", "failed");
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
            valid = new ReadOnlyBooleanWrapper(this, "valid", false);
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
            LOG.entering("scheduler.view.appointment.EditAppointment.TypeContextController", "initialize");
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
            appointmentConflicts.selectedCustomer.addListener((observable, oldValue, newValue) -> {
                LOG.entering("scheduler.view.appointment.EditAppointment.TypeContextController.appointmentConflicts#selectedCustomer", "changed", new Object[]{oldValue, newValue});
                onContextSensitiveChange();
                LOG.exiting("scheduler.view.appointment.EditAppointment.TypeContextController.appointmentConflicts#selectedCustomer", "changed");
            });
            contactText.addListener((observable, oldValue, newValue) -> {
                LOG.entering("scheduler.view.appointment.EditAppointment.contactTextField#text", "changed", new Object[]{oldValue, newValue});
                onContextSensitiveChange();
                LOG.exiting("scheduler.view.appointment.EditAppointment.contactTextField#text", "changed");
            });
            selectedCorporateLocation.addListener((observable, oldValue, newValue) -> {
                LOG.entering("scheduler.view.appointment.EditAppointment.corporateLocationComboBox#value", "changed", new Object[]{oldValue, newValue});
                onContextSensitiveChange();
                LOG.exiting("scheduler.view.appointment.EditAppointment.corporateLocationComboBox#value", "changed");
            });
            locationText.addListener((observable, oldValue, newValue) -> {
                LOG.entering("scheduler.view.appointment.EditAppointment.locationTextArea#text", "changed", new Object[]{oldValue, newValue});
                onContextSensitiveChange();
                LOG.exiting("scheduler.view.appointment.EditAppointment.locationTextArea#text", "changed");
            });
            phoneText.addListener((observable, oldValue, newValue) -> {
                LOG.entering("scheduler.view.appointment.EditAppointment.phoneTextField#text", "changed", new Object[]{oldValue, newValue});
                onContextSensitiveChange();
                LOG.exiting("scheduler.view.appointment.EditAppointment.phoneTextField#text", "changed");
            });
            urlTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                LOG.entering("scheduler.view.appointment.EditAppointment.urlTextField#text", "changed", new Object[]{oldValue, newValue});
                onUrlChanged();
                LOG.exiting("scheduler.view.appointment.EditAppointment.urlTextField#text", "changed");
            });
            appointmentConflicts.valid.addListener((observable, oldValue, newValue) -> {
                LOG.entering("scheduler.view.appointment.EditAppointment.TypeContextController.appointmentConflicts#valid", "changed", new Object[]{oldValue, newValue});
                onValidityChanged(contactValidationLabel.isVisible(), locationValidationLabel.isVisible(), urlValidationLabel.isVisible(), newValue);
                LOG.exiting("scheduler.view.appointment.EditAppointment.TypeContextController.appointmentConflicts#valid", "changed");
            });
            contactValidationLabel.visibleProperty().addListener((observable, oldValue, newValue) -> {
                LOG.entering("scheduler.view.appointment.EditAppointment.contactValidationLabel#visible", "changed", new Object[]{oldValue, newValue});
                onValidityChanged(newValue, locationValidationLabel.isVisible(), urlValidationLabel.isVisible(), appointmentConflicts.valid.get());
                LOG.exiting("scheduler.view.appointment.EditAppointment.contactValidationLabel#visible", "changed");
            });
            locationValidationLabel.visibleProperty().addListener((observable, oldValue, newValue) -> {
                LOG.entering("scheduler.view.appointment.EditAppointment.locationValidationLabel#visible", "changed", new Object[]{oldValue, newValue});
                onValidityChanged(contactValidationLabel.isVisible(), newValue, urlValidationLabel.isVisible(), appointmentConflicts.valid.get());
                LOG.exiting("scheduler.view.appointment.EditAppointment.locationValidationLabel#visible", "changed");
            });
            urlValidationLabel.visibleProperty().addListener((observable, oldValue, newValue) -> {
                LOG.entering("scheduler.view.appointment.EditAppointment.urlValidationLabel#visible", "changed", new Object[]{oldValue, newValue});
                onValidityChanged(contactValidationLabel.isVisible(), locationValidationLabel.isVisible(), newValue, appointmentConflicts.valid.get());
                LOG.exiting("scheduler.view.appointment.EditAppointment.urlValidationLabel#visible", "changed");
            });
            onValidityChanged(contactValidationLabel.isVisible(), locationValidationLabel.isVisible(), urlValidationLabel.isVisible(), appointmentConflicts.valid.get());
            selectedType.addListener(this::onTypeChanged);
            LOG.exiting("scheduler.view.appointment.EditAppointment.TypeContextController", "initialize");
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

        private synchronized void onValidityChanged(boolean contactIsInvalid, boolean locationIsInvalid, boolean urlIsInvalid, boolean rangesAreValid) {
            LOG.entering("scheduler.view.appointment.EditAppointment.TypeContextController.onValidityChanged(boolean, boolean, boolean, boolean)", "onValidityChanged",
                    new Object[]{contactIsInvalid, locationIsInvalid, urlIsInvalid, rangesAreValid});
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
            LOG.entering("scheduler.view.appointment.EditAppointment.TypeContextController", "onTypeChanged");
            if (oldValue == newValue) {
                return;
            }
            switch (oldValue) {
                case CORPORATE_LOCATION:
                    if (newValue == AppointmentType.CUSTOMER_SITE) {
                        onContextSensitiveChange();
                        LOG.exiting("scheduler.view.appointment.EditAppointment.TypeContextController", "onTypeChanged");
                        return;
                    }
                    collapseNode(corporateLocationComboBox);
                    collapseNode(implicitLocationLabel);
                    break;
                case CUSTOMER_SITE:
                    if (newValue == AppointmentType.CORPORATE_LOCATION) {
                        restoreNode(corporateLocationComboBox);
                        onContextSensitiveChange();
                        LOG.exiting("scheduler.view.appointment.EditAppointment.TypeContextController", "onTypeChanged");
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
                        LOG.exiting("scheduler.view.appointment.EditAppointment.TypeContextController", "onTypeChanged");
                        return;
                    }
                    collapseNode(locationTextArea);
                    break;
                default:
                    if (newValue == AppointmentType.VIRTUAL) {
                        locationValidationLabel.setVisible(false);
                        contactValidationLabel.setVisible(false);
                        onContextSensitiveChange();
                        LOG.exiting("scheduler.view.appointment.EditAppointment.TypeContextController", "onTypeChanged");
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
            LOG.exiting("scheduler.view.appointment.EditAppointment.TypeContextController", "onTypeChanged");
        }

    }
}
