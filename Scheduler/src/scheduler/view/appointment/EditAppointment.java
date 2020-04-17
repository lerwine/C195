package scheduler.view.appointment;

import com.sun.javafx.collections.ImmutableObservableList;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.chrono.Chronology;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
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
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Pair;
import javafx.util.StringConverter;
import javafx.util.converter.LocalDateStringConverter;
import scheduler.AppResources;
import scheduler.Scheduler;
import scheduler.dao.AppointmentDAO;
import scheduler.dao.AppointmentType;
import scheduler.dao.CustomerDAO;
import scheduler.dao.CustomerElement;
import scheduler.dao.DataAccessObject;
import scheduler.dao.UserDAO;
import scheduler.dao.UserElement;
import scheduler.dao.UserStatus;
import scheduler.dao.filter.AppointmentFilter;
import scheduler.dao.filter.ComparisonOperator;
import scheduler.dao.filter.UserFilter;
import scheduler.util.AlertHelper;
import scheduler.util.BinarySelective;
import scheduler.util.DB;
import scheduler.util.LogHelper;
import static scheduler.util.NodeUtil.collapseNode;
import static scheduler.util.NodeUtil.restoreInfoLabel;
import static scheduler.util.NodeUtil.restoreLabeled;
import static scheduler.util.NodeUtil.restoreNode;
import static scheduler.util.NodeUtil.restoreNodeAsNotVisible;
import static scheduler.util.NodeUtil.restoreValidationErrorLabel;
import scheduler.util.TernarySelective;
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

    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(EditAppointment.class.getName()), Level.FINE);

    public static AppointmentModel editNew(MainController mainController, Stage stage) throws IOException {
        return editNew(EditAppointment.class, mainController, stage);
    }

    public static AppointmentModel edit(AppointmentModel model, MainController mainController, Stage stage) throws IOException {
        return edit(model, EditAppointment.class, mainController, stage);
    }

    @FXML // fx:id="titleValidationLabel"
    private Label titleValidationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="titleTextField"
    private TextField titleTextField; // Value injected by FXMLLoader

    @FXML // fx:id="customerValidationLabel"
    private Label customerValidationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="userValidationLabel"
    private Label userValidationLabel; // Value injected by FXMLLoader

    @FXML // CustomerDAO selection control
    private ComboBox<CustomerModelImpl> customerComboBox;

    @FXML // UserDAO selection control.
    private ComboBox<UserModelImpl> userComboBox;

    @FXML // fx:id="startValidationLabel"
    private Label startValidationLabel; // Value injected by FXMLLoader

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

    @FXML // fx:id="durationValidationLabel"
    private Label durationValidationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="durationHourTextField"
    private TextField durationHourTextField; // Value injected by FXMLLoader

    @FXML // fx:id="durationMinuteTextField"
    private TextField durationMinuteTextField; // Value injected by FXMLLoader

    @FXML // Control for selecting the time zone for the appointment start and end.
    private ComboBox<TimeZone> timeZoneComboBox;

    @FXML // fx:id="currentTimeZoneLabel"
    private Label currentTimeZoneLabel; // Value injected by FXMLLoader

    @FXML // fx:id="currentTimeZoneValue"
    private Label currentTimeZoneValue; // Value injected by FXMLLoader

    @FXML // fx:id="locationLabel"
    private Label locationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="locationValidationLabel"
    private Label locationValidationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="locationTextArea"
    private TextArea locationTextArea; // Value injected by FXMLLoader

    @FXML // fx:id="phoneTextField"
    private TextField phoneTextField; // Value injected by FXMLLoader

    @FXML // fx:id="implicitLocationLabel"
    private Label implicitLocationLabel; // Value injected by FXMLLoader

    @FXML // AppointmentDAO type selection control.
    private ComboBox<AppointmentType> typeComboBox;

    @FXML // fx:id="contactValidationLabel"
    private Label contactValidationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="contactTextField"
    private TextField contactTextField; // Value injected by FXMLLoader

    @FXML // fx:id="urlValidationLabel"
    private Label urlValidationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="urlTextField"
    private TextField urlTextField; // Value injected by FXMLLoader

    @FXML // fx:id="descriptionTextArea"
    private TextArea descriptionTextArea; // Value injected by FXMLLoader

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

    private ConflictDependencyValidator conflictDependencyValidator;
    private SingleSelectionModel<AppointmentType> typeSelectionModel;
    private SingleSelectionModel<CustomerModelImpl> customerSelectionModel;
    private SingleSelectionModel<UserModelImpl> userSelectionModel;
    private DateTimeFormatter dateTimeFormatter;
    private NumberFormat numberFormatter;
    private Optional<Boolean> showActiveCustomers;
    private Optional<Boolean> showActiveUsers;
    private boolean editingUserOptions;
    private SingleSelectionModel<TimeZone> timeZoneSelectionModel;
    private SingleSelectionModel<Boolean> amPmSelectionModel;
    private ObservableList<TimeZone> timeZones;
    private StartDateValidationBinding startDateTime;
    private ObjectBinding<BinarySelective<Duration, String>> duration;
    private StringBinding localDateAndTimeBinding;

    @FXML
    private void closeConflictsBorderPaneButtonClick(ActionEvent event) {
        conflictsBorderPane.setVisible(false);
    }

    @FXML
    private void showConflictsButtonClick(ActionEvent event) {
        conflictsBorderPane.setVisible(true);
    }

    @FXML
    private void customerDropDownOptionsButtonClick(ActionEvent event) {
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
    private void userDropDownOptionsButtonClick(ActionEvent event) {
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
    private void dropdownOptionsCancelButtonClick(ActionEvent event) {
        dropdownOptionsBorderPane.setVisible(false);
    }

    @FXML
    private void dropdownOptionsOkButtonClick(ActionEvent event) {
        if (editingUserOptions) {
            TaskWaiter.startNow(new UserReloadTask((Stage) ((Button) event.getSource()).getScene().getWindow()));
        } else {
            TaskWaiter.startNow(new CustomerReloadTask((Stage) ((Button) event.getSource()).getScene().getWindow()));
        }
        dropdownOptionsBorderPane.setVisible(false);
    }

    @FXML
    void onCheckConflictsButtonAction(ActionEvent event) {
        conflictDependencyValidator.startCheckDependencies();
    }

    private void onContactTextFieldChange(String newValue) {
        contactValidationLabel.setVisible(null == newValue || newValue.trim().isEmpty());
    }

    private void updateConflictMesssage() {
        conflictDependencyValidator.refreshConflictingAppointments();

        Pair<Boolean, String> typeAndMessage = startDateTime.getTypeAndMessage();
        if (typeAndMessage.getKey()) {
            restoreValidationErrorLabel(startValidationLabel, typeAndMessage.getValue());
            showConflictsButton.setVisible(false);
            collapseNode(checkConflictsButton);
            restoreNodeAsNotVisible(showConflictsButton);
        } else if (typeAndMessage.getValue().isEmpty()) {
            startValidationLabel.setText("");
            startValidationLabel.setVisible(false);
            collapseNode(checkConflictsButton);
            restoreNodeAsNotVisible(showConflictsButton);
        } else {
            restoreInfoLabel(startValidationLabel, typeAndMessage.getValue());
            if (conflictDependencyValidator.get()) {
                if (duration.get().isPrimary() && (null != customerSelectionModel.getSelectedItem() || null != userSelectionModel.getSelectedItem())) {
                    collapseNode(showConflictsButton);
                    restoreNode(checkConflictsButton);
                } else {
                    collapseNode(checkConflictsButton);
                    restoreNodeAsNotVisible(showConflictsButton);
                }
            } else {
                restoreNode(showConflictsButton);
                collapseNode(checkConflictsButton);
            }
        }
    }

    private void updateDurationMessages() {
        String str = localDateAndTimeBinding.get();
        if (str.isEmpty()) {
            collapseNode(currentTimeZoneLabel);
            collapseNode(currentTimeZoneValue);
        } else {
            restoreNode(currentTimeZoneLabel);
            restoreInfoLabel(currentTimeZoneValue, str);
        }
        BinarySelective<Duration, String> durationOrMessage = duration.get();
        if (durationOrMessage.isPrimary()) {
            durationValidationLabel.setText("");
            durationValidationLabel.setVisible(false);
        } else {
            durationValidationLabel.setVisible(true);
            durationValidationLabel.setText(durationOrMessage.getSecondary());
        };
    }

    private void onLocationControlChange(String newValue) {
        locationValidationLabel.setVisible(typeSelectionModel.getSelectedItem() == AppointmentType.OTHER
                && (null == newValue || newValue.trim().isEmpty()));
    }

    private void onTitleTextFieldChange(String newValue) {
        if (null == newValue || newValue.trim().isEmpty()) {
            titleValidationLabel.setVisible(true);
        } else {
            titleValidationLabel.setVisible(false);
        }

    }

    private void onUrlTextFieldChange(String newValue) {
        urlValidationLabel.setVisible(typeSelectionModel.getSelectedItem() == AppointmentType.VIRTUAL
                && (null == newValue || newValue.trim().isEmpty()));
    }

    private void onCustomerComboBoxChange(CustomerModelImpl newValue) {
        if (null == newValue) {
            customerValidationLabel.setVisible(true);
            if (typeSelectionModel.getSelectedItem() == AppointmentType.CUSTOMER_SITE) {
                restoreLabeled(implicitLocationLabel, AppResources.getProperty(AppResources.RESOURCEKEY_APPOINTMENTTYPE_CUSTOMER));
            } else {
                collapseNode(implicitLocationLabel);
            }
        } else {
            customerValidationLabel.setVisible(false);
            if (typeSelectionModel.getSelectedItem() == AppointmentType.CUSTOMER_SITE) {
                restoreLabeled(implicitLocationLabel, newValue.getMultiLineAddress().get());
            } else {
                collapseNode(implicitLocationLabel);
            }
        }
        updateConflictMesssage();
    }

    private void onTypeComboBoxChange(AppointmentType oldValue, AppointmentType newValue) {
        switch (newValue) {
            case CORPORATE_HQ_MEETING:
                restoreLabeled(locationLabel, getResourceString(RESOURCEKEY_LOCATIONLABELTEXT));
                locationValidationLabel.setText("");
                locationValidationLabel.setVisible(false);
                collapseNode(implicitLocationLabel);
                collapseNode(phoneTextField);
                restoreLabeled(implicitLocationLabel, AppResources.getProperty(AppResources.RESOURCEKEY_APPOINTMENTTYPE_HQ));
                contactValidationLabel.setText("");
                contactValidationLabel.setVisible(false);
                urlValidationLabel.setText("");
                urlValidationLabel.setVisible(false);
                break;
            case CUSTOMER_SITE:
                restoreLabeled(locationLabel, getResourceString(RESOURCEKEY_LOCATIONLABELTEXT));
                locationValidationLabel.setText("");
                locationValidationLabel.setVisible(false);
                collapseNode(implicitLocationLabel);
                collapseNode(phoneTextField);
                contactValidationLabel.setText("");
                contactValidationLabel.setVisible(false);
                if (oldValue == AppointmentType.VIRTUAL) {
                    onUrlTextFieldChange(urlTextField.getText());
                }
                onCustomerComboBoxChange(customerSelectionModel.getSelectedItem());
                return;
            case GERMANY_SITE_MEETING:
                restoreLabeled(locationLabel, getResourceString(RESOURCEKEY_LOCATIONLABELTEXT));
                locationValidationLabel.setText("");
                locationValidationLabel.setVisible(false);
                collapseNode(implicitLocationLabel);
                collapseNode(phoneTextField);
                restoreLabeled(implicitLocationLabel, AppResources.getProperty(AppResources.RESOURCEKEY_APPOINTMENTTYPE_GERMANY));
                contactValidationLabel.setText("");
                contactValidationLabel.setVisible(false);
                break;
            case GUATEMALA_SITE_MEETING:
                restoreLabeled(locationLabel, getResourceString(RESOURCEKEY_LOCATIONLABELTEXT));
                locationValidationLabel.setText("");
                locationValidationLabel.setVisible(false);
                collapseNode(implicitLocationLabel);
                collapseNode(phoneTextField);
                restoreLabeled(implicitLocationLabel, AppResources.getProperty(AppResources.RESOURCEKEY_APPOINTMENTTYPE_GUATEMALA));
                contactValidationLabel.setText("");
                contactValidationLabel.setVisible(false);
                break;
            case INDIA_SITE_MEETING:
                restoreLabeled(locationLabel, getResourceString(RESOURCEKEY_LOCATIONLABELTEXT));
                locationValidationLabel.setText("");
                locationValidationLabel.setVisible(false);
                collapseNode(implicitLocationLabel);
                collapseNode(phoneTextField);
                restoreLabeled(implicitLocationLabel, AppResources.getProperty(AppResources.RESOURCEKEY_APPOINTMENTTYPE_INDIA));
                contactValidationLabel.setText("");
                contactValidationLabel.setVisible(false);
                break;
            case PHONE:
                restoreLabeled(locationLabel, getResourceString(RESOURCEKEY_PHONENUMBER));
                collapseNode(locationTextArea);
                collapseNode(implicitLocationLabel);
                restoreNode(phoneTextField);
                onLocationControlChange(phoneTextField.getText());
                contactValidationLabel.setText("");
                contactValidationLabel.setVisible(false);
                break;
            case VIRTUAL:
                collapseNode(locationLabel);
                locationValidationLabel.setVisible(false);
                collapseNode(locationTextArea);
                collapseNode(phoneTextField);
                collapseNode(implicitLocationLabel);
                contactValidationLabel.setText("");
                contactValidationLabel.setVisible(false);
                onUrlTextFieldChange(urlTextField.getText());
                if (oldValue == AppointmentType.CUSTOMER_SITE) {
                    onCustomerComboBoxChange(customerSelectionModel.getSelectedItem());
                }
                return;
            default:
                restoreLabeled(locationLabel, getResourceString(RESOURCEKEY_LOCATIONLABELTEXT));
                onLocationControlChange(locationTextArea.getText());
                restoreNode(locationTextArea);
                collapseNode(phoneTextField);
                collapseNode(implicitLocationLabel);
                onContactTextFieldChange(contactTextField.getText());
                break;
        }

        urlValidationLabel.setText("");
        urlValidationLabel.setVisible(false);
        if (null != oldValue) {
            switch (oldValue) {
                case CUSTOMER_SITE:
                    onCustomerComboBoxChange(customerSelectionModel.getSelectedItem());
                    break;
                case VIRTUAL:
                    onUrlTextFieldChange(urlTextField.getText());
                    break;
            }
        }
    }

    private void onUserComboBoxChange(UserModelImpl newValue) {
        userValidationLabel.setVisible(null == newValue);
        updateConflictMesssage();
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
        assert startValidationLabel != null : "fx:id=\"startValidationLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert startHourTextField != null : "fx:id=\"startHourTextField\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert startMinuteTextField != null : "fx:id=\"startMinuteTextField\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert amPmComboBox != null : "fx:id=\"amPmComboBox\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert durationHourTextField != null : "fx:id=\"durationHourTextField\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert durationMinuteTextField != null : "fx:id=\"durationMinuteTextField\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert timeZoneComboBox != null : "fx:id=\"timeZoneComboBox\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert durationValidationLabel != null : "fx:id=\"durationValidationLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert currentTimeZoneLabel != null : "fx:id=\"currentTimeZoneLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert currentTimeZoneValue != null : "fx:id=\"currentTimeZoneValue\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert locationLabel != null : "fx:id=\"locationLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
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
        assert conflictsBorderPane != null : "fx:id=\"conflictsBorderPane\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert conflictingAppointmentsTableView != null : "fx:id=\"conflictingAppointmentsTableView\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert dropdownOptionsBorderPane != null : "fx:id=\"dropdownOptionsBorderPane\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert dropdownOptionsInactiveRadioButton != null : "fx:id=\"dropdownOptionsInactiveRadioButton\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert dropdownOptions != null : "fx:id=\"dropdownOptions\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert dropdownOptionsLabel != null : "fx:id=\"dropdownOptionsLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert dropdownOptionsActiveRadioButton != null : "fx:id=\"dropdownOptionsActiveRadioButton\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert dropdownOptionsAllRadioButton != null : "fx:id=\"dropdownOptionsAllRadioButton\" was not injected: check your FXML file 'EditAppointment.fxml'.";

        timeZoneSelectionModel = timeZoneComboBox.getSelectionModel();
        amPmSelectionModel = amPmComboBox.getSelectionModel();
        customerSelectionModel = customerComboBox.getSelectionModel();
        userSelectionModel = userComboBox.getSelectionModel();
        typeSelectionModel = typeComboBox.getSelectionModel();

        customerModelList = FXCollections.observableArrayList();
        userModelList = FXCollections.observableArrayList();
        showActiveCustomers = Optional.of(true);
        showActiveUsers = Optional.of(true);
        dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);
        numberFormatter = NumberFormat.getIntegerInstance();
        timeZones = FXCollections.observableArrayList();
        Arrays.stream(TimeZone.getAvailableIDs()).map((t) -> TimeZone.getTimeZone(t)).sorted((o1, o2) -> {
            return o1.getRawOffset() - o2.getRawOffset();
        }).forEachOrdered((t) -> timeZones.add(t));

        titleTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            onTitleTextFieldChange(newValue);
        });

        customerSelectionModel.selectedItemProperty().addListener((observable) -> {
            onCustomerComboBoxChange(((ObservableObjectValue<CustomerModelImpl>) observable).get());
        });

        userSelectionModel.selectedItemProperty().addListener((observable) -> {
            onUserComboBoxChange(((ObservableObjectValue<UserModelImpl>) observable).get());
        });

        typeSelectionModel.selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            onTypeComboBoxChange(oldValue, newValue);
        });

        conflictDependencyValidator = null;
        conflictDependencyValidator = new ConflictDependencyValidator();

        urlTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            onUrlTextFieldChange(newValue);
        });

        contactTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            onContactTextFieldChange(newValue);
        });

        startDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateConflictMesssage();
            updateDurationMessages();
        });
        startHourTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateConflictMesssage();
            updateDurationMessages();
        });
        startMinuteTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateConflictMesssage();
            updateDurationMessages();
        });
        timeZoneSelectionModel.selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            updateConflictMesssage();
            updateDurationMessages();
        });
        amPmSelectionModel.selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            updateConflictMesssage();
            updateDurationMessages();
        });

        durationHourTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateConflictMesssage();
            updateDurationMessages();
        });
        durationMinuteTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateConflictMesssage();
            updateDurationMessages();
        });

        locationTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
            onLocationControlChange(newValue);
        });

        startDateTime = new StartDateValidationBinding(customerSelectionModel.selectedItemProperty(), userSelectionModel.selectedItemProperty(),
                startDatePicker, startHourTextField.textProperty(), startMinuteTextField.textProperty(),
                timeZoneSelectionModel.selectedItemProperty(), amPmSelectionModel.selectedItemProperty());
        duration = Bindings.createObjectBinding(this::onDurationInvalid,
                durationHourTextField.textProperty(), durationMinuteTextField.textProperty());

        localDateAndTimeBinding = Bindings.createStringBinding(this::onLocalDateAndTimeBinding, startDateTime.zonedDateTimeBinding(), duration);

        amPmComboBox.setItems(FXCollections.observableArrayList(false, true));

        timeZoneComboBox.setItems(timeZones);

        // Get appointment type options.
        typeComboBox.setItems(FXCollections.observableArrayList(AppointmentType.values()));
        typeSelectionModel.select(AppointmentType.OTHER);

    }

    /**
     * This gets called when {@link #localDateAndTimeBinding} is invalidated.
     *
     * @return A {@link String} containing the date and time range in the local time zone.
     */
    private String onLocalDateAndTimeBinding() {
        ZonedDateTime zdt = startDateTime.zonedDateTimeBinding().get();
        BinarySelective<Duration, String> durationOrMsg = duration.get();
        if (null != zdt && durationOrMsg.isPrimary()) {
            LocalDateTime start = zdt.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
            return String.format(getResourceString(RESOURCEKEY_TIMERANGE), dateTimeFormatter.format(start),
                    dateTimeFormatter.format(start.plus(durationOrMsg.getPrimary())));
        }
        return "";
    }

    /**
     * This gets called when {@link #duration} is invalidated.
     *
     * @return A {@link BinarySelective} with a {@link Duration} value if duration is valid; otherwise a secondary {@link String} value with the error
     * message.
     */
    private BinarySelective<Duration, String> onDurationInvalid() {
        String durationHour = durationHourTextField.textProperty().get();
        String durationMinute = durationMinuteTextField.textProperty().get();
        if (null == durationHour || durationHour.trim().isEmpty()) {
            return BinarySelective.ofSecondary(getResourceString(RESOURCEKEY_DURATIONHOURNOTSPECIFIED));
        }
        Number n;
        try {
            n = numberFormatter.parse(durationHour);
        } catch (ParseException ex) {
            Logger.getLogger(EditAppointment.class.getName()).log(Level.SEVERE, null, ex);
            n = null;
        }
        int h;
        if (null == n || (h = n.intValue()) < 1 || h > 12) {
            return BinarySelective.ofSecondary(getResourceString(RESOURCEKEY_INVALIDDURATIONHOUR));
        }
        if (null == durationMinute || durationMinute.trim().isEmpty()) {
            return BinarySelective.ofSecondary(getResourceString(RESOURCEKEY_DURATIONMINUTENOTSPECIFIED));
        }
        try {
            n = numberFormatter.parse(durationMinute);
        } catch (ParseException ex) {
            Logger.getLogger(EditAppointment.class.getName()).log(Level.SEVERE, null, ex);
            n = null;
        }
        int m;
        if (null == n || (m = n.intValue()) < 0 || m > 59) {
            return BinarySelective.ofSecondary(getResourceString(RESOURCEKEY_INVALIDDURATIONMINUTE));
        }
        return BinarySelective.ofPrimary(Duration.ofMinutes((h * 60) + m));
    }

    @HandlesFxmlViewEvent(FxmlViewEventHandling.BEFORE_SHOW)
    protected void onBeforeShow(FxmlViewEvent<? extends Parent> event) {
        TaskWaiter.startNow(new ItemsLoadTask(event.getStage()));
        AppointmentModel model = this.getModel();
        event.getStage().setTitle(getResourceString((model.isNewItem()) ? RESOURCEKEY_ADDNEWAPPOINTMENT : RESOURCEKEY_EDITAPPOINTMENT));

        typeSelectionModel.select(model.getType());
        titleTextField.setText(model.getTitle());
        contactTextField.setText(model.getContact());
        ZoneId z = AppointmentModel.getZoneId(model);
        String id = z.getId();
        Optional<TimeZone> timeZone = timeZones.stream().filter((t) -> t.toZoneId().getId().equals(id)).findFirst();
        timeZoneSelectionModel.select(timeZone.get());
        LocalDateTime start = model.getStart();
        ZonedDateTime zdt;
        if (z.equals(ZoneId.systemDefault())) {
            zdt = ZonedDateTime.of(start, z);
        } else {
            zdt = ZonedDateTime.of(start, ZoneId.systemDefault()).withZoneSameInstant(z);
        }
        startDatePicker.setValue(zdt.toLocalDate());
        int h = zdt.getHour();
        if (h < 12) {
            amPmSelectionModel.select(false);
            startHourTextField.setText(String.format("%d", (h > 0) ? h : 12));
        } else {
            amPmSelectionModel.select(true);
            startHourTextField.setText(String.format("%d", (h > 12) ? h - 12 : 12));
        }
        startMinuteTextField.setText(String.format("%02d", zdt.getMinute()));
        Duration dur = Duration.between(start, model.getEnd());
        if (dur.isNegative()) {
            durationHourTextField.setText("0");
            durationMinuteTextField.setText("00");
        } else {
            long s = dur.getSeconds();
            s = (s - (s % 60)) / 60;
            long m = s % 60;
            durationHourTextField.setText(String.format("%d", (s - m) / 60));
            durationMinuteTextField.setText(String.format("%02d", m));
        }
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
        return startDateTime.dateTimeValidBinding().and(
                contactValidationLabel.visibleProperty()
                        .or(durationValidationLabel.visibleProperty())
                        .or(locationValidationLabel.visibleProperty())
                        .or(titleValidationLabel.visibleProperty())
                        .or(customerValidationLabel.visibleProperty())
                        .or(userValidationLabel.visibleProperty())
                        .or(urlValidationLabel.visibleProperty()).not()
        );
    }

    @Override
    protected ItemModel.ModelFactory<AppointmentDAO, AppointmentModel> getFactory() {
        return AppointmentModel.getFactory();
    }

    @Override
    protected void updateModel(AppointmentModel model) {
        if (!getValidationExpression().get()) {
            throw new IllegalStateException();
        }
        CustomerModelImpl customer = customerSelectionModel.getSelectedItem();
        assert null != customer : "No customer is selected";
        assert customer.getDataObject().isExisting() : "Customer does not exist in database";
        UserModelImpl user = userSelectionModel.getSelectedItem();
        assert null != user : "No user is selected";
        assert user.getDataObject().isExisting() : "User does not exist in database";
        String title = titleTextField.getText().trim();
        assert !title.isEmpty() : "Title is empty";
        AppointmentType type = typeSelectionModel.getSelectedItem();
        assert null != type : "Type is not selected";
        ZonedDateTime zdt = startDateTime.getZonedDateTime();
        if (null == zdt) {
            throw new IllegalStateException("Start date/time is not valid");
        }
        BinarySelective<Duration, String> d = duration.get();
        if (!d.isPrimary()) {
            throw new IllegalStateException("Duration is not valid");
        }
        model.setTitle(title);
        model.setCustomer(customer);
        model.setUser(user);
        model.setUrl(urlTextField.getText().trim());
        model.setType(type);
        LocalDateTime s = zdt.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
        model.setStart(s);
        model.setEnd(s.plus(d.getPrimary()));
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

    /**
     * Validates whether appointments need to be reloaded in order to check for conflicts. Returns {@code true} if appointment data needs to be
     * reloaded.
     * <p>
     * This is instantiated as {@link #conflictDependencyValidator}</p><p>
     * Data can only be reloaded if {@link StartDateValidationBinding#isDateTimeValid()} on {@link #startDateTime} returns true, the
     * {@link BinarySelective} from {@link #duration} returns the primary ({@link Duration}) value, {@link #customerSelectionModel} has a
     * {@link CustomerModelImpl} selected, and {@link #userSelectionModel} has a {@link UserModelImpl} selected.</p>
     */
    private class ConflictDependencyValidator extends BooleanBinding {

        private ObservableList<AppointmentModel> otherAppointments;
        private final ReadOnlyObjectWrapper<CustomerElement> targetCustomer = new ReadOnlyObjectWrapper<>();
        private final ReadOnlyObjectWrapper<UserElement> targetUser = new ReadOnlyObjectWrapper<>();

        ConflictDependencyValidator() {
            assert null == conflictDependencyValidator : "Class was already instantiated";
                
            super.bind(customerSelectionModel.selectedItemProperty(), userSelectionModel.selectedItemProperty(), targetCustomer, targetUser);
        }

        private synchronized void refreshConflictingAppointments() {
            ZonedDateTime zdt = startDateTime.getZonedDateTime();
            BinarySelective<Duration, String> d = duration.get();
            if (get()) {
                if (d.isPrimary() && null != zdt) {
                    LocalDateTime cs = zdt.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
                    LocalDateTime ce = cs.plus(d.getPrimary());
                    Stream<AppointmentModel> stream = otherAppointments.stream().filter((AppointmentModel t) -> {
                        LocalDateTime ts = t.getStart();
                        LocalDateTime te = t.getEnd();
                        return cs.compareTo(te) < 0 && ce.compareTo(ts) >= 0;
                    });
                    ArrayList<AppointmentModel> matching = new ArrayList<>();
                    stream.forEach((t) -> matching.add(t));
                    if (!matching.isEmpty()) {
                        startDateTime.getConflictingAppointments().retainAll(matching);
                        startDateTime.getConflictingAppointments().addAll(matching);
                        return;
                    }
                }
            }
            startDateTime.getConflictingAppointments().clear();
        }

        private synchronized void startCheckDependencies() {
            if (get()) {
                return;
            }
            if (null != customerSelectionModel.getSelectedItem() || null != userSelectionModel.getSelectedItem()) {
                TaskWaiter.startNow(new AppointmentReloadTask());
            }
        }

        @Override
        protected synchronized boolean computeValue() {
            CustomerModelImpl currentCustomer = customerSelectionModel.getSelectedItem();
            UserModelImpl currentUser = userSelectionModel.getSelectedItem();
            CustomerElement c = targetCustomer.get();
            UserElement u = targetUser.get();
            if (null != otherAppointments) {
                if (null == currentUser) {
                    if (null != currentCustomer && null != c && null == u && c.getPrimaryKey() == currentCustomer.getPrimaryKey()) {
                        return true;
                    }
                } else if (u != null && u.getPrimaryKey() == currentUser.getPrimaryKey()
                        && ((currentCustomer == null) ? c == null : c != null && c.getPrimaryKey() == currentCustomer.getPrimaryKey())) {
                    return true;
                }
                otherAppointments = null;
            }
            return false;
        }

        @SuppressWarnings("unchecked")
        @Override
        public ObservableList<?> getDependencies() {
            return FXCollections.unmodifiableObservableList(FXCollections.observableArrayList(customerSelectionModel.selectedItemProperty(),
                    userSelectionModel.selectedItemProperty(), targetCustomer, targetUser));
        }

        @Override
        public void dispose() {
            super.unbind(customerSelectionModel.selectedItemProperty(), userSelectionModel.selectedItemProperty(), targetCustomer, targetUser);
            super.dispose();
        }

        private synchronized void accept(List<AppointmentDAO> appointments, CustomerElement customer, UserElement user) {
            targetCustomer.set(customer);
            targetUser.set(user);
            otherAppointments = FXCollections.observableArrayList();
            if (null != appointments && !appointments.isEmpty()) {
                if (getModel().getDataObject().isExisting()) {
                    int pk = getModel().getPrimaryKey();
                    appointments.forEach((t) -> {
                        if (t.getPrimaryKey() != pk) {
                            otherAppointments.add(new AppointmentModel(t));
                        }
                    });
                } else {
                    appointments.forEach((t) -> {
                        otherAppointments.add(new AppointmentModel(t));
                    });
                }
            }

            updateConflictMesssage();
        }
    }

    private class AppointmentReloadTask extends TaskWaiter<List<AppointmentDAO>> {

        private final CustomerDAO customer;
        private final UserDAO user;

        private AppointmentReloadTask() {
            super((Stage) customerComboBox.getScene().getWindow());
            CustomerModelImpl selectedCustomer = customerSelectionModel.getSelectedItem();
            UserModelImpl selectedUser = userSelectionModel.getSelectedItem();
            customer = (null == selectedCustomer) ? null : selectedCustomer.getDataObject();
            user = (null == selectedUser) ? null : selectedUser.getDataObject();
        }

        @Override
        protected void processResult(List<AppointmentDAO> result, Stage stage) {
            conflictDependencyValidator.accept(result, customer, user);
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
            Optional<Boolean> currentOption = showActiveCustomers;
            if ((currentOption.isPresent()) ? loadOption.isPresent() && currentOption.get().equals(loadOption.get()) : !loadOption.get()) {
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
            Optional<Boolean> currentOption = showActiveUsers;
            if ((currentOption.isPresent()) ? loadOption.isPresent() && currentOption.get().equals(loadOption.get()) : !loadOption.get()) {
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
        private List<AppointmentDAO> appointments;
        private final Optional<Boolean> customerLoadOption;
        private final Optional<Boolean> userLoadOption;
        private final CustomerElement appointmentCustomer;
        private final UserElement appointmentUser;

        private ItemsLoadTask(Stage owner) {
            super(owner, AppResources.getResourceString(AppResources.RESOURCEKEY_CONNECTINGTODB),
                    AppResources.getResourceString(AppResources.RESOURCEKEY_INITIALIZING));
            customerDaoList = null;
            userDaoList = null;
            AppointmentModel model = getModel();
            CustomerModel<? extends CustomerElement> customer = model.getCustomer();
            appointmentCustomer = (null == customer) ? null : customer.getDataObject();
            UserModel<? extends UserElement> user = model.getUser();
            appointmentUser = (null == user) ? null : user.getDataObject();
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

            conflictDependencyValidator.accept(appointments, appointmentCustomer, appointmentUser);
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
                if (null != appointmentCustomer && appointmentCustomer.isExisting()) {
                    if (null != appointmentUser && appointmentUser.isExisting()) {
                        return af.load(connection, AppointmentFilter.of(appointmentCustomer, appointmentUser, null, null));
                    }
                    return af.load(connection, AppointmentFilter.of(appointmentCustomer, null, null, null));
                }
                if (null != appointmentUser && appointmentUser.isExisting()) {
                    return af.load(connection, AppointmentFilter.of(null, appointmentUser, null, null));
                }
            }
            return null;
        }

    }

}
