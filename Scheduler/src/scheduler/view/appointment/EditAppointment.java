package scheduler.view.appointment;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
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
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
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
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import scheduler.dao.AppointmentDAO;
import scheduler.dao.CustomerDAO;
import scheduler.dao.DataRowState;
import scheduler.dao.UserDAO;
import scheduler.events.AppointmentEvent;
import scheduler.events.CustomerSuccessEvent;
import scheduler.events.UserSuccessEvent;
import scheduler.model.AppointmentType;
import scheduler.model.CorporateAddress;
import scheduler.model.Customer;
import scheduler.model.User;
import scheduler.model.ui.AppointmentModel;
import scheduler.model.ui.CustomerModel;
import scheduler.model.ui.EntityModel;
import scheduler.model.ui.PartialCustomerModel;
import scheduler.model.ui.PartialUserModel;
import scheduler.model.ui.UserModel;
import scheduler.observables.BindingHelper;
import scheduler.util.BinarySelective;
import scheduler.util.LogHelper;
import static scheduler.util.NodeUtil.collapseNode;
import static scheduler.util.NodeUtil.restoreNode;
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
        return EditItem.showAndWait(parentWindow, EditAppointment_old.class, model, keepOpen);
    }

    public static AppointmentModel edit(AppointmentModel model, Window parentWindow) throws IOException {
        return EditItem.showAndWait(parentWindow, EditAppointment_old.class, model, false);
    }

    private static int parseInteger(String s) throws ParseException {
        NumberFormat fmt = NumberFormat.getIntegerInstance();
        if (s.isEmpty()) {
            throw new ParseException(INVALID_NUMBER, 0);
        }

        Matcher m = INT_PATTERN.matcher(s);
        if (!m.find()) {
            throw new ParseException(INVALID_NUMBER, 0);
        } else if (m.end() < s.length()) {
            throw new ParseException(INVALID_NUMBER, m.end());
        }
        return fmt.parse(s).intValue();
    }

    private final ReadOnlyBooleanWrapper valid;
    private final ReadOnlyBooleanWrapper modified;
    private final ReadOnlyStringWrapper windowTitle;
    private final ObservableList<CustomerModel> customerModelList;
    private final ObservableList<CorporateAddress> corporateLocationList;
    private final ObservableList<CorporateAddress> remoteLocationList;
    private final ObservableList<UserModel> userModelList;
    private final EventHandler<CustomerSuccessEvent> onCustomerDeleted;
    private final EventHandler<UserSuccessEvent> onUserDeleted;
    private Optional<Boolean> showActiveCustomers;
    private Optional<Boolean> showActiveUsers;
    private boolean editingUserOptions;
    private ReadOnlyObjectProperty<CustomerModel> selectedCustomer;
    private ReadOnlyObjectProperty<UserModel> selectedUser;
    private ReadOnlyObjectProperty<Boolean> amPm;
    private ReadOnlyObjectProperty<CorporateAddress> selectedCorporateLocation;
    private ReadOnlyObjectProperty<AppointmentType> selectedType;
    private StringBinding normalizedTitleBinding;
    private StringBinding normalizedPhoneBinding;
    private StringBinding normalizedLocationBinding;
    private StringBinding normalizedContactBinding;
    private StringBinding normalizedUrlBinding;
    private StringBinding normalizedDescriptionBinding;
    private ObjectBinding<BinarySelective<LocalDateTime, String>> startDateTimeParseBinding;
    private ObjectBinding<BinarySelective<LocalDateTime, String>> endDateTimeParseBinding;
    private StringBinding startValidationMessageBinding;
    private StringBinding durationValidationMessageBinding;

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

    @FXML // fx:id="lowerLeftVBox"
    private VBox lowerLeftVBox; // Value injected by FXMLLoader

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

    public EditAppointment() {
        windowTitle = new ReadOnlyStringWrapper(this, "windowTitle", "");
        valid = new ReadOnlyBooleanWrapper(this, "valid", false);
        modified = new ReadOnlyBooleanWrapper(this, "modified", true);
        corporateLocationList = FXCollections.observableArrayList();
        remoteLocationList = FXCollections.observableArrayList();
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
        initializeEditMode();
        CustomerModel.FACTORY.addEventHandler(CustomerSuccessEvent.DELETE_SUCCESS, new WeakEventHandler<>(onCustomerDeleted));
        UserModel.FACTORY.addEventHandler(UserSuccessEvent.DELETE_SUCCESS, new WeakEventHandler<>(onUserDeleted));
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
            waitBorderPane.startNow(new UserReloadTask());
        } else {
            if (dropdownOptionsInactiveRadioButton.isSelected()) {
                showActiveCustomers = Optional.of(false);
            } else if (dropdownOptionsAllRadioButton.isSelected()) {
                showActiveCustomers = Optional.empty();
            } else {
                showActiveCustomers = Optional.of(true);
            }
            waitBorderPane.startNow(new CustomerReloadTask());
        }
        collapseNode(dropdownOptionsBorderPane);
    }

    @FXML
    void onIncludeRemoteCheckBoxAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onIncludeRemoteCheckBoxAction", event);
        if (includeRemoteCheckBox.isSelected()) {
            remoteLocationList.forEach((t) -> {
                if (!corporateLocationList.contains(t)) {
                    corporateLocationList.add(t);
                }
            });
        } else {
            if (remoteLocationList.contains(corporateLocationComboBox.getValue())) {
                corporateLocationComboBox.getSelectionModel().clearSelection();
            }
            remoteLocationList.forEach((t) -> corporateLocationList.remove(t));
        }
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
        assert lowerLeftVBox != null : "fx:id=\"lowerLeftVBox\" was not injected: check your FXML file 'EditAppointment.fxml'.";
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

        customerComboBox.setItems(customerModelList);
        userComboBox.setItems(userModelList);
        amPmComboBox.setItems(FXCollections.observableArrayList(Boolean.TRUE, Boolean.FALSE));
        corporateLocationComboBox.setItems(corporateLocationList);
        typeComboBox.setItems(FXCollections.observableArrayList(AppointmentType.values()));
        
        selectedCustomer = customerComboBox.getSelectionModel().selectedItemProperty();
        selectedUser = userComboBox.getSelectionModel().selectedItemProperty();
        amPm = amPmComboBox.getSelectionModel().selectedItemProperty();
        selectedCorporateLocation = corporateLocationComboBox.getSelectionModel().selectedItemProperty();
        selectedType = typeComboBox.getSelectionModel().selectedItemProperty();

        normalizedTitleBinding = BindingHelper.asNonNullAndWsNormalized(titleTextField.textProperty());
        normalizedPhoneBinding = BindingHelper.asNonNullAndWsNormalized(phoneTextField.textProperty());
        normalizedLocationBinding = BindingHelper.asNonNullAndWsNormalizedMultiLine(locationTextArea.textProperty());
        normalizedContactBinding = BindingHelper.asNonNullAndWsNormalized(contactTextField.textProperty());
        normalizedUrlBinding = BindingHelper.asTrimmedAndNotNull(urlTextField.textProperty());
        normalizedDescriptionBinding = BindingHelper.asNonNullAndWsNormalizedMultiLine(descriptionTextArea.textProperty());
        startDateTimeParseBinding = Bindings.createObjectBinding(() -> {
            LocalDate date = startDatePicker.getValue();
            String hourText = startHourTextField.getText();
            String minuteText = startMinuteTextField.getText();
            boolean isAm = amPm.get();
            if (null == date) {
                return BinarySelective.ofSecondary("* Required");
            }
            if (null == hourText || (hourText = hourText.trim()).isEmpty()) {
                return BinarySelective.ofSecondary("* Hour required");
            }
            if (null == minuteText || (minuteText = minuteText.trim()).isEmpty()) {
                return BinarySelective.ofSecondary("* Minute required");
            }
            int hourValue;
            int minuteValue;
            try {
                hourValue = parseInteger(hourText);
            } catch (ParseException | NullPointerException ex) {
                LOG.log(Level.WARNING, "Error parsing start hour", ex);
                return BinarySelective.ofSecondary("Invalid hour value");
            }
            if (hourValue < 1 || hourValue > 12) {
                return BinarySelective.ofSecondary("Invalid hour value");
            }
            try {
                minuteValue = parseInteger(minuteText);
            } catch (ParseException | NullPointerException ex) {
                LOG.log(Level.WARNING, "Error parsing start minute", ex);
                return BinarySelective.ofSecondary("Invalid minute value");
            }
            if (minuteValue < 0 || minuteValue > 59) {
                return BinarySelective.ofSecondary("Invalid minute value");
            }
            if (isAm) {
                if (hourValue == 12) {
                    hourValue = 0;
                }
            } else if (hourValue < 12) {
                hourValue += 12;
            }
            return BinarySelective.ofPrimary(date.atTime(hourValue, minuteValue));
        }, startDatePicker.valueProperty(), startHourTextField.textProperty(), startMinuteTextField.textProperty(), amPm);
        endDateTimeParseBinding = Bindings.createObjectBinding(() -> {
            BinarySelective<LocalDateTime, String> start = startDateTimeParseBinding.get();
            String hourText = durationHourTextField.getText();
            String minuteText = durationMinuteTextField.getText();
            if (null == hourText || (hourText = hourText.trim()).isEmpty()) {
                return BinarySelective.ofSecondary("* Hour required");
            }
            if (null == minuteText || (minuteText = minuteText.trim()).isEmpty()) {
                return BinarySelective.ofSecondary("* Minute required");
            }
            int hourValue;
            int minuteValue;
            try {
                hourValue = parseInteger(hourText);
            } catch (ParseException | NullPointerException ex) {
                LOG.log(Level.WARNING, "Error parsing end hour", ex);
                return BinarySelective.ofSecondary("Invalid hour value");
            }
            if (hourValue < 0) {
                return BinarySelective.ofSecondary("Invalid hour value");
            }
            try {
                minuteValue = parseInteger(minuteText);
            } catch (ParseException | NullPointerException ex) {
                LOG.log(Level.WARNING, "Error parsing end minute", ex);
                return BinarySelective.ofSecondary("Invalid minute value");
            }
            if (minuteValue < 0 || minuteValue > 59) {
                return BinarySelective.ofSecondary("Invalid minute value");
            }
            return start.map((LocalDateTime s) -> {
                if (hourValue > 0) {
                    return BinarySelective.ofPrimary((minuteValue > 0) ? s.plusMinutes(minuteValue) : s);
                }
                return BinarySelective.ofPrimary(((minuteValue > 0) ? s.plusMinutes(minuteValue) : s).plusHours(hourValue));
            }, (s) -> BinarySelective.ofSecondary(""));
        }, startDateTimeParseBinding, durationHourTextField.textProperty(), durationMinuteTextField.textProperty());
        
        titleTextField.textProperty().addListener(this::onTitleChanged);
        selectedCustomer.addListener(this::onSelectedCustomerChanged);
        selectedUser.addListener(this::onSelectedUserChanged);
        startDatePicker.valueProperty().addListener(this::onStartDateChanged);
        startHourTextField.textProperty().addListener(this::onStartHourChanged);
        startMinuteTextField.textProperty().addListener(this::onStartMinuteChanged);
        amPm.addListener(this::onAmChanged);
        durationHourTextField.textProperty().addListener(this::onDurationHourChanged);
        durationMinuteTextField.textProperty().addListener(this::onDurationMinuteChanged);
        selectedCorporateLocation.addListener(this::onSelectedCorporationChanged);
        locationTextArea.textProperty().addListener(this::onLocationChanged);
        phoneTextField.textProperty().addListener(this::onPhoneChanged);
        selectedType.addListener(this::onSelectedTypeChanged);
        contactTextField.textProperty().addListener(this::onContactChanged);
        urlTextField.textProperty().addListener(this::onUrlChanged);
        descriptionTextArea.textProperty().addListener(this::onDescriptionChanged);
        
        titleValidationLabel.visibleProperty().bind(normalizedTitleBinding.isEmpty());
        startValidationMessageBinding = Bindings.createStringBinding(() -> startDateTimeParseBinding.get().toSecondary(""), startDateTimeParseBinding);
        startValidationLabel.textProperty().bind(startValidationMessageBinding);
        startValidationLabel.visibleProperty().bind(startValidationMessageBinding.isNotEmpty());
        durationValidationMessageBinding = Bindings.createStringBinding(() -> endDateTimeParseBinding.get().toSecondary(""), endDateTimeParseBinding);
        durationValidationLabel.textProperty().bind(durationValidationMessageBinding);
        durationValidationLabel.visibleProperty().bind(durationValidationMessageBinding.isNotEmpty());
        customerValidationLabel.visibleProperty().bind(selectedCustomer.isNull());
        userValidationLabel.visibleProperty().bind(selectedUser.isNull());
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
        model.setContact(normalizedContactBinding.get());
        model.setUrl(normalizedUrlBinding.get());
        model.setDescription(normalizedDescriptionBinding.get());
        model.setCustomer(selectedCustomer.get());
        model.setUser(selectedUser.get());
        AppointmentType type = selectedType.get();
        model.setType(type);
        model.setStart(startDateTimeParseBinding.get().getPrimary());
        model.setEnd(endDateTimeParseBinding.get().getPrimary());
        switch (type) {
            case CORPORATE_LOCATION:
                model.setLocation(selectedCorporateLocation.get().getName());
                break;
            case CUSTOMER_SITE:
                model.setLocation(selectedCustomer.get().getMultiLineAddress());
                break;
            case PHONE:
                model.setLocation(normalizedPhoneBinding.get());
                break;
            default:
                model.setLocation(normalizedLocationBinding.get());
                break;
        }
    }

    private void initializeEditMode() {
        throw new UnsupportedOperationException("Not supported yet."); // FIXME: Implement scheduler.view.appointment.EditAppointment#initializeEditMode
    }

    private void onTitleChanged(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        throw new UnsupportedOperationException("Not supported yet."); // FIXME: Implement scheduler.view.appointment.EditAppointment#onTitleChanged
    }

    private void onSelectedCustomerChanged(ObservableValue<? extends CustomerModel> observable, CustomerModel oldValue, CustomerModel newValue) {
        throw new UnsupportedOperationException("Not supported yet."); // FIXME: Implement scheduler.view.appointment.EditAppointment#onSelectedCustomerChanged
    }

    private void onSelectedUserChanged(ObservableValue<? extends UserModel> observable, UserModel oldValue, UserModel newValue) {
        throw new UnsupportedOperationException("Not supported yet."); // FIXME: Implement scheduler.view.appointment.EditAppointment#onSelectedUserChanged
    }

    private void onStartDateChanged(ObservableValue<? extends LocalDate> observable, LocalDate oldValue, LocalDate newValue) {
        throw new UnsupportedOperationException("Not supported yet."); // FIXME: Implement scheduler.view.appointment.EditAppointment#onStartDateChanged
    }

    private void onStartHourChanged(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        throw new UnsupportedOperationException("Not supported yet."); // FIXME: Implement scheduler.view.appointment.EditAppointment#onStartHourChanged
    }

    private void onStartMinuteChanged(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        throw new UnsupportedOperationException("Not supported yet."); // FIXME: Implement scheduler.view.appointment.EditAppointment#onStartMinuteChanged
    }

    private void onAmChanged(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        throw new UnsupportedOperationException("Not supported yet."); // FIXME: Implement scheduler.view.appointment.EditAppointment#onAmChanged
    }

    private void onDurationHourChanged(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        throw new UnsupportedOperationException("Not supported yet."); // FIXME: Implement scheduler.view.appointment.EditAppointment#onDurationHourChanged
    }

    private void onDurationMinuteChanged(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        throw new UnsupportedOperationException("Not supported yet."); // FIXME: Implement scheduler.view.appointment.EditAppointment#onDurationMinuteChanged
    }

    private void onSelectedCorporationChanged(ObservableValue<? extends CorporateAddress> observable, CorporateAddress oldValue, CorporateAddress newValue) {
        throw new UnsupportedOperationException("Not supported yet."); // FIXME: Implement scheduler.view.appointment.EditAppointment#onSelectedCorporationChanged
    }

    private void onLocationChanged(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        throw new UnsupportedOperationException("Not supported yet."); // FIXME: Implement scheduler.view.appointment.EditAppointment#onLocationChanged
    }

    private void onPhoneChanged(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        throw new UnsupportedOperationException("Not supported yet."); // FIXME: Implement scheduler.view.appointment.EditAppointment#onPhoneChanged
    }

    private void onSelectedTypeChanged(ObservableValue<? extends AppointmentType> observable, AppointmentType oldValue, AppointmentType newValue) {
        throw new UnsupportedOperationException("Not supported yet."); // FIXME: Implement scheduler.view.appointment.EditAppointment#onSelectedTypeChanged
    }

    private void onContactChanged(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        throw new UnsupportedOperationException("Not supported yet."); // FIXME: Implement scheduler.view.appointment.EditAppointment#onContactChanged
    }

    private void onUrlChanged(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        throw new UnsupportedOperationException("Not supported yet."); // FIXME: Implement scheduler.view.appointment.EditAppointment#onUrlChanged
    }

    private void onDescriptionChanged(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        throw new UnsupportedOperationException("Not supported yet."); // FIXME: Implement scheduler.view.appointment.EditAppointment#onDescriptionChanged
    }

    private static class CustomerReloadTask extends Task<List<CustomerDAO>> {

        private CustomerReloadTask() {
        }

        @Override
        protected List<CustomerDAO> call() throws Exception {
            throw new UnsupportedOperationException("Not supported yet."); // FIXME: Implement scheduler.view.appointment.EditAppointment.CustomerReloadTask#call
        }
    }

    private static class UserReloadTask extends Task<List<UserDAO>> {

        private UserReloadTask() {
        }

        @Override
        protected List<UserDAO> call() throws Exception {
            throw new UnsupportedOperationException("Not supported yet."); // FIXME: Implement scheduler.view.appointment.EditAppointment.UserReloadTask#call
        }
    }

}
