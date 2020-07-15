package scheduler.view.appointment;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
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
import scheduler.model.AppointmentType;
import scheduler.model.CorporateAddress;
import scheduler.model.Customer;
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
import static scheduler.util.NodeUtil.restoreNode;
import scheduler.util.Tuple;
import scheduler.view.EditItem;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.annotations.ModelEditor;
import static scheduler.view.appointment.EditAppointmentResourceKeys.*;
import scheduler.view.appointment.edit.AppointmentConflictsController;
import scheduler.view.appointment.edit.DateRangeController;
import scheduler.view.appointment.edit.TypeContextController;
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

    static {
        INT2_FORMAT = NumberFormat.getIntegerInstance();
        INT2_FORMAT.setMinimumIntegerDigits(2);
        INT2_FORMAT.setMaximumIntegerDigits(2);
        INTN_FORMAT = NumberFormat.getIntegerInstance();
    }

    private static BinarySelective<LocalDateTime, String> calculateEndDateTime(LocalDateTime start, BinarySelective<Integer, String> hour, BinarySelective<Integer, String> minute) {
        if (null == start) {
            return BinarySelective.ofSecondary(hour.toSecondary(minute.toSecondary("* Required")));
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
            BinarySelective<Integer, String> minute, boolean isAm) {
        if (null == date) {
            return BinarySelective.ofSecondary(hour.toSecondary(minute.toSecondary("* Required")));
        }

        return hour.map(
                (hv) -> minute.map(
                        (mv) -> BinarySelective.ofPrimary(date.atTime((isAm) ? ((hv == 12) ? 0 : hv) : ((hv > 12) ? hv + 12 : 12), mv)),
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

    public static AppointmentModel editNew(PartialCustomerModel<? extends Customer> customer, PartialUserModel<? extends User> user,
            Window parentWindow, boolean keepOpen) throws IOException {
        AppointmentModel.Factory factory = AppointmentModel.FACTORY;
        AppointmentModel model = factory.createNew(factory.getDaoFactory().createNew());
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
    private final EventHandler<CustomerSuccessEvent> onCustomerDeleted;
    private final EventHandler<UserSuccessEvent> onUserDeleted;
    private Optional<Boolean> showActiveCustomers;
    private Optional<Boolean> showActiveUsers;
    private boolean editingUserOptions;
//    private ReadOnlyObjectProperty<CustomerModel> selectedCustomer;
//    private ReadOnlyObjectProperty<UserModel> selectedUser;
//    private StringBinding normalizedTitleBinding;
//    private StringBinding normalizedDescriptionBinding;
    private DateRangeController dateRangeController;
    private TypeContextController typeContextController;
//    private AppointmentConflictsController appointmentConflictsController;
//    private StringBinding titleValidationMessage;

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
    private StringBinding normalizedTitleBinding;
    private StringBinding titleValidationMessage;

    private StringBinding normalizedDescriptionBinding;
    private AppointmentConflictsController appointmentConflictsController;

    public EditAppointment() {
        windowTitle = new ReadOnlyStringWrapper(this, "windowTitle", "");
        valid = new ReadOnlyBooleanWrapper(this, "valid", false);
        modified = new ReadOnlyBooleanWrapper(this, "modified", true);
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
        assert userComboBox != null : "fx:id=\"userComboBox\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert checkConflictsButton != null : "fx:id=\"checkConflictsButton\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert showConflictsButton != null : "fx:id=\"showConflictsButton\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert startDatePicker != null : "fx:id=\"startDatePicker\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert startHourTextField != null : "fx:id=\"startHourTextField\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert startMinuteTextField != null : "fx:id=\"startMinuteTextField\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert amPmComboBox != null : "fx:id=\"amPmComboBox\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert startValidationLabel != null : "fx:id=\"startValidationLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert durationHourTextField != null : "fx:id=\"durationHourTextField\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert durationMinuteTextField != null : "fx:id=\"durationMinuteTextField\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert durationValidationLabel != null : "fx:id=\"durationValidationLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert locationLabel != null : "fx:id=\"locationLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert includeRemoteCheckBox != null : "fx:id=\"includeRemoteCheckBox\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert corporateLocationComboBox != null : "fx:id=\"corporateLocationComboBox\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert locationTextArea != null : "fx:id=\"locationTextArea\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert phoneTextField != null : "fx:id=\"phoneTextField\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert implicitLocationLabel != null : "fx:id=\"implicitLocationLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert locationValidationLabel != null : "fx:id=\"locationValidationLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert typeComboBox != null : "fx:id=\"typeComboBox\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert contactTextField != null : "fx:id=\"contactTextField\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert urlTextField != null : "fx:id=\"urlTextField\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert urlValidationLabel != null : "fx:id=\"urlValidationLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert descriptionTextArea != null : "fx:id=\"descriptionTextArea\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert customerValidationLabel != null : "fx:id=\"customerValidationLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert userValidationLabel != null : "fx:id=\"userValidationLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert contactValidationLabel != null : "fx:id=\"contactValidationLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert dropdownOptionsBorderPane != null : "fx:id=\"dropdownOptionsBorderPane\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert dropdownOptionsLabel != null : "fx:id=\"dropdownOptionsLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert dropdownOptionsActiveRadioButton != null : "fx:id=\"dropdownOptionsActiveRadioButton\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert dropdownOptions != null : "fx:id=\"dropdownOptions\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert dropdownOptionsInactiveRadioButton != null : "fx:id=\"dropdownOptionsInactiveRadioButton\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert dropdownOptionsAllRadioButton != null : "fx:id=\"dropdownOptionsAllRadioButton\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert appointmentConflictsBorderPane != null : "fx:id=\"appointmentConflictsBorderPane\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert conflictingAppointmentsTableView != null : "fx:id=\"conflictingAppointmentsTableView\" was not injected: check your FXML file 'EditAppointment.fxml'.";

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
        customerValidationLabel.visibleProperty().bind(customerComboBox.getSelectionModel().selectedItemProperty().isNull());
        userComboBox.setItems(userModelList);
        userValidationLabel.visibleProperty().bind(userComboBox.getSelectionModel().selectedItemProperty().isNull());

        normalizedDescriptionBinding = BindingHelper.asNonNullAndWsNormalizedMultiLine(descriptionTextArea.textProperty());

        if (model.isNewRow()) {
            windowTitle.set(resources.getString(RESOURCEKEY_ADDNEWAPPOINTMENT));
        } else {
            windowTitle.set(resources.getString(RESOURCEKEY_EDITAPPOINTMENT));
        }

        waitBorderPane.startNow(new ItemsLoadTask());
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

    public AppointmentModel getModel() {
        return model;
    }

    public ResourceBundle getResources() {
        return resources;
    }

    public ComboBox<CustomerModel> getCustomerComboBox() {
        return customerComboBox;
    }

    public Label getCustomerValidationLabel() {
        return customerValidationLabel;
    }

    public ComboBox<UserModel> getUserComboBox() {
        return userComboBox;
    }

    public Label getUserValidationLabel() {
        return userValidationLabel;
    }

    public TextField getContactTextField() {
        return contactTextField;
    }

    public Label getContactValidationLabel() {
        return contactValidationLabel;
    }

    public ComboBox<AppointmentType> getTypeComboBox() {
        return typeComboBox;
    }

    public DatePicker getStartDatePicker() {
        return startDatePicker;
    }

    public TextField getStartHourTextField() {
        return startHourTextField;
    }

    public TextField getStartMinuteTextField() {
        return startMinuteTextField;
    }

    public ComboBox<Boolean> getAmPmComboBox() {
        return amPmComboBox;
    }

    public Button getShowConflictsButton() {
        return showConflictsButton;
    }

    public Button getCheckConflictsButton() {
        return checkConflictsButton;
    }

    public Label getStartValidationLabel() {
        return startValidationLabel;
    }

    public TextField getDurationHourTextField() {
        return durationHourTextField;
    }

    public TextField getDurationMinuteTextField() {
        return durationMinuteTextField;
    }

    public Label getDurationValidationLabel() {
        return durationValidationLabel;
    }

    public Label getLocationLabel() {
        return locationLabel;
    }

    public CheckBox getIncludeRemoteCheckBox() {
        return includeRemoteCheckBox;
    }

    public ComboBox<CorporateAddress> getCorporateLocationComboBox() {
        return corporateLocationComboBox;
    }

    public TextArea getLocationTextArea() {
        return locationTextArea;
    }

    public TextField getPhoneTextField() {
        return phoneTextField;
    }

    public Label getImplicitLocationLabel() {
        return implicitLocationLabel;
    }

    public Label getLocationValidationLabel() {
        return locationValidationLabel;
    }

    public TextField getUrlTextField() {
        return urlTextField;
    }

    public Label getUrlValidationLabel() {
        return urlValidationLabel;
    }

    public DateRangeController getDateRangeController() {
        return dateRangeController;
    }

    public TypeContextController getTypeContextController() {
        return typeContextController;
    }

    public BorderPane getAppointmentConflictsBorderPane() {
        return appointmentConflictsBorderPane;
    }

    public AppointmentConflictsController getAppointmentConflictsController() {
        return appointmentConflictsController;
    }

    @Override
    public void applyChanges() {
        LOG.info("Applying changes");
        model.setTitle(normalizedTitleBinding.get());
        model.setContact(typeContextController.getNormalizedContact());
        model.setUrl(typeContextController.getParsedUrl().toPrimary(""));
        model.setDescription(normalizedDescriptionBinding.get());
        model.setCustomer(appointmentConflictsController.getSelectedCustomer());
        model.setUser(appointmentConflictsController.getSelectedUser());
        model.setType(typeContextController.getSelectedType());
        model.setStart(dateRangeController.getStartDateTime());
        model.setEnd(dateRangeController.getEndDateTime());
        switch (typeContextController.getSelectedType()) {
            case CORPORATE_LOCATION:
                model.setLocation(typeContextController.getSelectedCorporateLocation().getName());
                break;
            case CUSTOMER_SITE:
                model.setLocation(appointmentConflictsController.getSelectedCustomer().getMultiLineAddress());
                break;
            case PHONE:
                model.setLocation(typeContextController.getNormalizedPhone());
                break;
            default:
                model.setLocation(typeContextController.getNormalizedLocation());
                break;
        }
    }

    private synchronized void onCustomersLoaded(List<CustomerDAO> customerDaoList) {
        LOG.info("Invoked scheduler.view.appointment.EditAppointment#customerDaoList");
        CustomerModel selectedItem = customerComboBox.getSelectionModel().getSelectedItem();
        customerModelList.clear();
        if (null != customerDaoList && !customerDaoList.isEmpty()) {
            customerDaoList.forEach((t) -> customerModelList.add(CustomerModel.FACTORY.createNew(t)));
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
            userDaoList.forEach((t) -> userModelList.add(UserModel.FACTORY.createNew(t)));
        }
        if (null != selectedItem) {
            int cpk = selectedItem.getPrimaryKey();
            userModelList.stream().filter((t) -> t.getPrimaryKey() == cpk).findFirst().ifPresent((t)
                    -> userComboBox.getSelectionModel().select(t));
        }
    }

    private synchronized void onAppointmentsLoaded(List<AppointmentDAO> appointmentList) {
        LOG.info("Invoked scheduler.view.appointment.EditAppointment#onAppointmentsLoaded");
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

        dateRangeController = new DateRangeController(this);
        appointmentConflictsController = new AppointmentConflictsController(this, appointmentList);
        typeContextController = new TypeContextController(this);

//        contactTextField.setText(model.getContact());
//        
//        SingleSelectionModel<AppointmentType> typeSelectionModel = typeComboBox.getSelectionModel();
//        AppointmentType type = model.getType();
//        typeSelectionModel.select(type);
//        
//        LocalDateTime startDateTime = model.getStart();
//        startDatePicker.setValue(startDateTime.toLocalDate());
//        switch (type) {
//            case CORPORATE_LOCATION:
//                String name = model.getLocation();
//                corporateLocationList.stream().filter((t) -> t.getName().equals(name)).findFirst().ifPresent((t) -> corporateLocationComboBox.getSelectionModel().select(t));
//                break;
//            case PHONE:
//                phoneTextField.setText(model.getLocation());
//                break;
//            case CUSTOMER_SITE:
//                break;
//            default:
//                locationTextArea.setText(model.getLocation());
//                break;
//        }
//        urlTextField.setText(model.getUrl());
//        descriptionTextArea.setText(model.getDescription());
//        typeContextController = new TypeContextController(this);
//        dateRangeController = new DateRangeController(this);
//        customerValidationLabel.visibleProperty().bind(typeContextController.customerInvalidProperty());
//        userValidationLabel.visibleProperty().bind(selectedUser.isNull());
//        startValidationLabel.textProperty().bind(dateRangeController.startValidationMessageProperty());
//        startValidationLabel.visibleProperty().bind(dateRangeController.startValidationMessageProperty().isNotEmpty());
//        durationValidationLabel.textProperty().bind(dateRangeController.durationValidationMessageProperty());
//        durationValidationLabel.visibleProperty().bind(dateRangeController.durationValidationMessageProperty().isNotEmpty());
//        urlValidationLabel.textProperty().bind(typeContextController.urlValidationMessageProperty());
//        urlValidationLabel.visibleProperty().bind(typeContextController.urlValidationMessageProperty().isNotEmpty());
//        typeContextController.locationInvalidProperty().addListener((observable, oldValue, newValue) -> {
//            if (newValue) {
//                restoreNode(locationValidationLabel);
//            } else {
//                collapseNode(locationValidationLabel);
//            }
//        });
//        if (typeContextController.isLocationInvalid()) {
//            collapseNode(locationValidationLabel);
//        }
//        appointmentConflictsController = new AppointmentConflictsController(selectedCustomer, selectedUser, dateRangeController.startDateTimeProperty(),
//                dateRangeController.endDateTimeProperty(), resources, appointmentList, (model.isNewRow()) ? Optional.empty() : Optional.of(model.getPrimaryKey()));
        CustomerModel.FACTORY.addEventHandler(CustomerSuccessEvent.DELETE_SUCCESS, new WeakEventHandler<>(onCustomerDeleted));
        UserModel.FACTORY.addEventHandler(UserSuccessEvent.DELETE_SUCCESS, new WeakEventHandler<>(onUserDeleted));
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

    private class ItemsLoadTask extends Task<List<AppointmentDAO>> {

        private final Customer appointmentCustomer;
        private final User appointmentUser;

        private ItemsLoadTask() {
            updateTitle(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
            PartialCustomerModel<? extends Customer> customer = model.getCustomer();
            appointmentCustomer = (null == customer) ? null : customer.dataObject();
            PartialUserModel<? extends User> user = model.getUser();
            appointmentUser = (null == user) ? null : user.dataObject();
        }

        @Override
        protected void succeeded() {
            LOG.info("Invoked scheduler.view.appointment.EditAppointment.ItemsLoadTask#succeeded");
            EditAppointment.this.onAppointmentsLoaded(getValue());
            super.succeeded();
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

    }

    private class DateRangeValidator {

        private final ObjectBinding<BinarySelective<Integer, String>> parsedStartHour;
        private final ObjectBinding<BinarySelective<Integer, String>> parsedStartMinute;
        private final ObjectBinding<BinarySelective<LocalDateTime, String>> startDateTimeBinding;
        private final ReadOnlyObjectWrapper<LocalDateTime> startDateTimeValue;
        private final ObjectBinding<BinarySelective<Integer, String>> parsedDurationHour;
        private final ObjectBinding<BinarySelective<Integer, String>> parsedDurationMinute;
        private final ObjectBinding<BinarySelective<LocalDateTime, String>> endDateTimeBinding;
        private final ReadOnlyObjectWrapper<LocalDateTime> endDateTimeValue;
        private final ReadOnlyObjectWrapper<Tuple<LocalDateTime, LocalDateTime>> range;
        private final ReadOnlyBooleanWrapper valid;

        public DateRangeValidator() {
            LocalDateTime rangeStart = model.getStart();
            if (null != rangeStart) {
                startDatePicker.setValue(rangeStart.toLocalDate());
                int hv = rangeStart.getHour();
                if (hv < 12) {
                    amPmComboBox.getSelectionModel().select(true);
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
            startDateTimeValue = new ReadOnlyObjectWrapper<>(this, "startDateTimeValue", null);
            parsedDurationHour = Bindings.createObjectBinding(() -> calculateHour(durationHourTextField.getText(), 0, 256),
                    durationHourTextField.textProperty());
            parsedDurationMinute = Bindings.createObjectBinding(() -> calculateMinute(durationMinuteTextField.getText()),
                    durationMinuteTextField.textProperty());
            endDateTimeBinding = Bindings.createObjectBinding(() -> calculateEndDateTime(startDateTimeValue.get(), parsedDurationHour.get(),
                    parsedDurationMinute.get()), startDateTimeValue, parsedDurationHour, parsedDurationMinute);
            endDateTimeValue = new ReadOnlyObjectWrapper<>(this, "endDateTimeValue", null);
            range = new ReadOnlyObjectWrapper<>(this, "range", null);
            valid = new ReadOnlyBooleanWrapper(this, "valid", false);
            startDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> checkStartChange());
            startHourTextField.textProperty().addListener((observable, oldValue, newValue) -> checkStartChange());
            startMinuteTextField.textProperty().addListener((observable, oldValue, newValue) -> checkStartChange());
            amPmComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> checkStartChange());
            startDateTimeValue.addListener((observable, oldValue, newValue) -> checkEndChange(Optional.of(newValue)));
            durationHourTextField.textProperty().addListener((observable, oldValue, newValue) -> checkEndChange(Optional.empty()));
            durationMinuteTextField.textProperty().addListener((observable, oldValue, newValue) -> checkEndChange(Optional.empty()));
            endDateTimeValue.addListener((observable, oldValue, newValue) -> checkRangeChange(startDateTimeValue.get(), endDateTimeValue.get()));
        }

        private synchronized void checkStartChange() {
            startDateTimeBinding.get().accept(
                    (t) -> {
                        if (!t.equals(startDateTimeValue.get())) {
                            startDateTimeValue.set(t);
                        }
                        if (!startValidationLabel.getText().isEmpty()) {
                            startValidationLabel.setText("");
                        }
                        if (startValidationLabel.isVisible()) {
                            startValidationLabel.setVisible(false);
                        }
                    },
                    (t) -> {
                        if (null != startDateTimeValue.getValue()) {
                            startDateTimeValue.setValue(null);
                        }
                        if (!t.equals(startValidationLabel.getText())) {
                            startValidationLabel.setText(t);
                        }
                        if (!startValidationLabel.isVisible()) {
                            startValidationLabel.setVisible(true);
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
                        boolean c = null != endDateTimeValue.get();
                        if (c) {
                            endDateTimeValue.set(null);
                        }
                        if (!t.equals(durationValidationLabel.getText())) {
                            durationValidationLabel.setText(t);
                        }
                        if (!durationValidationLabel.isVisible()) {
                            durationValidationLabel.setVisible(true);
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
}
