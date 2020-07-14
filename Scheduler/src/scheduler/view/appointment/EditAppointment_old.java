package scheduler.view.appointment;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.WeakEventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import scheduler.AppResourceKeys;
import scheduler.AppResources;
import scheduler.Scheduler;
import scheduler.dao.AppointmentDAO;
import scheduler.dao.CustomerDAO;
import scheduler.dao.DataRowState;
import scheduler.dao.PartialCustomerDAO;
import scheduler.dao.UserDAO;
import scheduler.dao.filter.AppointmentFilter;
import scheduler.dao.filter.ComparisonOperator;
import scheduler.dao.filter.UserFilter;
import scheduler.events.AppointmentEvent;
import scheduler.events.AppointmentSuccessEvent;
import scheduler.events.CustomerSuccessEvent;
import scheduler.events.UserSuccessEvent;
import scheduler.model.AppointmentType;
import scheduler.model.CorporateAddress;
import scheduler.model.Customer;
import scheduler.model.ModelHelper;
import scheduler.model.PredefinedData;
import scheduler.model.SupportedCityDefinition;
import scheduler.model.User;
import scheduler.model.UserStatus;
import scheduler.model.fx.AddressModel;
import scheduler.model.fx.AppointmentModel;
import scheduler.model.fx.CustomerModel;
import scheduler.model.fx.EntityModel;
import scheduler.model.fx.PartialCustomerModel;
import scheduler.model.fx.PartialUserModel;
import scheduler.model.fx.UserModel;
import scheduler.observables.BindingHelper;
import scheduler.util.AlertHelper;
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
import scheduler.view.appointment.edit.AppointmentConflictsControl;
import scheduler.view.appointment.edit.DateRangeControl;
import scheduler.view.appointment.edit.ZonedAppointmentTimeSpan;
import scheduler.view.task.WaitBorderPane;
import scheduler.view.task.WaitTitledPane;

/**
 * FXML Controller class for editing an {@link AppointmentModel}.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/view/appointment/EditAppointment")
@FXMLResource("/scheduler/view/appointment/EditAppointment.fxml")
public final class EditAppointment_old extends StackPane implements EditItem.ModelEditorController<AppointmentDAO, AppointmentModel, AppointmentEvent> {

    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(EditAppointment_old.class.getName()), Level.FINER);
//    private static final Logger LOG = Logger.getLogger(EditAppointment_old.class.getName());

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

    @FXML // fx:id="dateRangeControl"
    private DateRangeControl dateRangeControl;

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
    private AppointmentConflictsControl appointmentConflicts; // Value injected by FXMLLoader
    private StringBinding normalizedTitleBinding;
    private StringBinding normalizedPhoneBinding;
    private StringBinding normalizedLocationBinding;
    private StringBinding normalizedContactBinding;
    private StringBinding normalizedUrlBinding;
    private BooleanBinding titleValid;
    private BooleanBinding customerValid;
    private BooleanBinding userValid;
    private BooleanBinding locationValid;
    private BooleanBinding contactValid;
    private BooleanBinding urlValid;
    private BooleanBinding validationBinding;
    private BooleanBinding dateRangeValid;

    public EditAppointment_old() {
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
    private void onCustomerDropDownOptionsButtonAction(ActionEvent event) {
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
    private void onDropdownOptionsCancelButtonAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onDropdownOptionsCancelButtonAction", event);
        dropdownOptionsBorderPane.minWidthProperty().unbind();
        dropdownOptionsBorderPane.prefWidthProperty().unbind();
        dropdownOptionsBorderPane.minHeightProperty().unbind();
        dropdownOptionsBorderPane.prefHeightProperty().unbind();
        collapseNode(dropdownOptionsBorderPane);
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
    private void onIncludeRemoteCheckBoxAction(ActionEvent event) {
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
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    private void initialize() {
        LOG.fine(() -> "Initializing");
        assert titleTextField != null : "fx:id=\"titleTextField\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert titleValidationLabel != null : "fx:id=\"titleValidationLabel\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert customerComboBox != null : "fx:id=\"customerComboBox\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert userComboBox != null : "fx:id=\"userComboBox\" was not injected: check your FXML file 'EditAppointment.fxml'.";
        assert lowerLeftVBox != null : "fx:id=\"lowerLeftVBox\" was not injected: check your FXML file 'EditAppointment.fxml'.";
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
        assert appointmentConflicts != null : "fx:id=\"appointmentConflicts\" was not injected: check your FXML file 'EditAppointment.fxml'.";

        initializeTitleTextField();

        initializeDateRangeControl();

        initializeLocationControls();

        initializeContactTextField();

        initializeUrlTextField();

        descriptionTextArea.setText(model.getDescription());

        initializeTypeComboBox();

        if (model.isNewRow()) {
            windowTitle.set(resources.getString(RESOURCEKEY_ADDNEWAPPOINTMENT));
        } else {
            windowTitle.set(resources.getString(RESOURCEKEY_EDITAPPOINTMENT));
            initializeEditMode();
        }

        WaitTitledPane pane = new WaitTitledPane();
        pane.addOnFailAcknowledged((evt) -> getScene().getWindow().hide())
                .addOnCancelAcknowledged((evt) -> getScene().getWindow().hide());
        waitBorderPane.startNow(pane, new ItemsLoadTask());

    }

    private void initializeTitleTextField() {
        titleTextField.setText(model.getTitle());
        StringProperty textProperty = titleTextField.textProperty();
        normalizedTitleBinding = BindingHelper.asNonNullAndWsNormalized(textProperty);
        StringBinding validationMessageBinding = Bindings.when(normalizedTitleBinding.isEmpty())
                .then("Title cannot be empty.")
                .otherwise(Bindings.when(normalizedTitleBinding.length().greaterThan(scheduler.model.Appointment.MAX_LENGTH_TITLE))
                        .then("Title too long.")
                        .otherwise(""));
        titleValidationLabel.textProperty().bind(validationMessageBinding);
        titleValidationLabel.visibleProperty().bind(validationMessageBinding.isNotEmpty());
        validationBinding = titleValid = validationMessageBinding.isEmpty();
        textProperty.addListener((observable, oldValue, newValue) -> updateValidity());
    }

    private void initializeCustomerComboBox(List<CustomerDAO> customerDaoList) {
        if (null != customerDaoList && !customerDaoList.isEmpty()) {
            customerDaoList.forEach((t) -> customerModelList.add(CustomerModel.FACTORY.createNew(t)));
        }
        customerComboBox.setItems(customerModelList);
        SingleSelectionModel<CustomerModel> selectionModel = customerComboBox.getSelectionModel();
        PartialCustomerModel<? extends Customer> customer = model.getCustomer();
        if (null != customer) {
            int cpk = customer.getPrimaryKey();
            customerModelList.stream().filter((t) -> t.getPrimaryKey() == cpk).findFirst().ifPresent((t)
                    -> selectionModel.select(t));
        }
        ReadOnlyObjectProperty<CustomerModel> selectedItemProperty = selectionModel.selectedItemProperty();
        customerValid = selectedItemProperty.isNotNull();
        validationBinding = validationBinding.and(customerValid);
        selectedItemProperty.addListener((observable, oldValue, newValue) -> {
            appointmentConflicts.setSelectedCustomer(newValue);
            updateValidity();
        });
    }

    private void initializeUserComboBox(List<UserDAO> userDaoList) {
        if (null != userDaoList && !userDaoList.isEmpty()) {
            userDaoList.forEach((t) -> userModelList.add(UserModel.FACTORY.createNew(t)));
        }
        userComboBox.setItems(userModelList);
        SingleSelectionModel<UserModel> selectionModel = userComboBox.getSelectionModel();
        PartialUserModel<? extends User> user = model.getUser();
        int upk = (null == user) ? Scheduler.getCurrentUser().getPrimaryKey() : user.getPrimaryKey();
        userModelList.stream().filter((t) -> t.getPrimaryKey() == upk).findFirst().ifPresent((t)
                -> userComboBox.getSelectionModel().select(t));
        ReadOnlyObjectProperty<UserModel> selectedItemProperty = selectionModel.selectedItemProperty();
        userValid = selectedItemProperty.isNotNull();
        validationBinding = validationBinding.and(userValid);
        selectedItemProperty.addListener((observable, oldValue, newValue) -> {
            appointmentConflicts.setSelectedUser(newValue);
            updateValidity();
        });
    }

    private void initializeDateRangeControl() {
        LocalDateTime start = model.getStart();
        LocalDateTime end = model.getEnd();
        PartialCustomerModel<? extends PartialCustomerDAO> c = model.getCustomer();
        TimeZone selectedTimeZone = TimeZone.getTimeZone(ZoneId.systemDefault());
        Duration duration;
        if (null != start && null != end) {
            duration = Duration.between(start, end);
        } else {
            duration = null;
        }
        dateRangeControl.setDateRange(start, duration, selectedTimeZone);
        ReadOnlyObjectProperty<ZonedAppointmentTimeSpan> timeSpanProperty = dateRangeControl.timeSpanProperty();
        dateRangeValid = timeSpanProperty.isNotNull();
        validationBinding = validationBinding.and(dateRangeValid);
        dateRangeControl.setOnCheckConflictsButtonAction((event) -> appointmentConflicts.startConflictCheck(waitBorderPane));
        dateRangeControl.setOnShowConflictsButtonAction((event) -> appointmentConflicts.showConflicts());
        dateRangeControl.timeSpanProperty().addListener((observable, oldValue, newValue) -> {
            appointmentConflicts.setSelectedTimeSpan(newValue);
            updateValidity();
        });
    }

    private void initializeLocationControls() {
        PredefinedData.getCorporateAddressMap().values().forEach((t) -> {
            if (t.isSatelliteOffice()) {
                remoteLocationList.add(t);
            } else {
                corporateLocationList.add(t);
            }
        });
        corporateLocationComboBox.setItems(corporateLocationList);
        SingleSelectionModel<CorporateAddress> selectionModel = corporateLocationComboBox.getSelectionModel();
        ReadOnlyObjectProperty<CorporateAddress> selectedAddressProperty = selectionModel.selectedItemProperty();
        switch (model.getType()) {
            case CORPORATE_LOCATION:
                selectionModel.select(PredefinedData.getCorporateAddress(model.getLocation()));
                break;
            case PHONE:
                phoneTextField.setText(model.getLocation());
                break;
            case CUSTOMER_SITE:
                break;
            default:
                locationTextArea.setText(model.getLocation());
                break;
        }
        StringProperty phoneProperty = phoneTextField.textProperty();
        StringProperty locationProperty = locationTextArea.textProperty();
        normalizedPhoneBinding = BindingHelper.asNonNullAndWsNormalized(phoneProperty);
        normalizedLocationBinding = BindingHelper.asTrimmedAndNotNull(locationProperty);
        ReadOnlyObjectProperty<AppointmentType> selectedType = typeComboBox.getSelectionModel().selectedItemProperty();
        StringBinding validationMessageBinding = Bindings.when(selectedType.isEqualTo(AppointmentType.CORPORATE_LOCATION))
                .then(Bindings.when(selectedAddressProperty.isNull()).then("Corporate location must be selected.").otherwise(""))
                .otherwise(Bindings.when(selectedType.isEqualTo(AppointmentType.PHONE))
                        .then(Bindings.when(normalizedPhoneBinding.isEmpty()).then("Phone number must be provided.")
                                .otherwise(Bindings.when(normalizedPhoneBinding.length().greaterThan(scheduler.model.Appointment.MAX_LENGTH_LOCATION))
                                        .then("Phone number text too long.")
                                        .otherwise("")))
                        .otherwise(Bindings.when(selectedType.isEqualTo(AppointmentType.OTHER).and(normalizedLocationBinding.isEmpty()))
                                .then("Address must be provided.")
                                .otherwise(Bindings.when(normalizedLocationBinding.length().greaterThan(scheduler.model.Appointment.MAX_LENGTH_LOCATION))
                                        .then("Address text too long.")
                                        .otherwise(""))));
        locationValidationLabel.textProperty().bind(validationMessageBinding);
        locationLabel.textProperty().bind(Bindings.when(selectedType.isEqualTo(AppointmentType.PHONE))
                .then(resources.getString(RESOURCEKEY_PHONENUMBER))
                .otherwise(resources.getString(RESOURCEKEY_LOCATIONLABELTEXT)));

        validationMessageBinding.isEmpty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                collapseNode(locationValidationLabel);
            } else {
                restoreNode(locationValidationLabel);
            }
        });
        selectedType.isEqualTo(AppointmentType.CORPORATE_LOCATION).addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                restoreNode(corporateLocationComboBox);
            } else {
                collapseNode(corporateLocationComboBox);
            }
        });
        selectedType.isEqualTo(AppointmentType.OTHER).or(selectedType.isEqualTo(AppointmentType.VIRTUAL)).addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                restoreNode(locationTextArea);
            } else {
                collapseNode(locationTextArea);
            }
        });
        selectedType.isEqualTo(AppointmentType.PHONE).addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                restoreNode(phoneTextField);
            } else {
                collapseNode(phoneTextField);
            }
        });
        selectedType.isEqualTo(AppointmentType.CORPORATE_LOCATION).or(selectedType.isEqualTo(AppointmentType.CUSTOMER_SITE)).addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                restoreNode(implicitLocationLabel);
            } else {
                collapseNode(implicitLocationLabel);
            }
        });

        implicitLocationLabel.textProperty().bind(Bindings.when(selectedType.isEqualTo(AppointmentType.CORPORATE_LOCATION))
                .then(Bindings.createStringBinding(() -> {
                    CorporateAddress addr = selectedAddressProperty.get();
                    if (null == addr) {
                        return "";
                    }
                    SupportedCityDefinition city = addr.getCity();
                    return String.format("%s%n%s", addr.getName(),
                            AddressModel.calculateMultiLineAddress(AddressModel.calculateAddressLines(addr.getAddress1(), addr.getAddress2()),
                                    AddressModel.calculateCityZipCountry(city.getName(), city.getCountry().getName(), addr.getPostalCode()), addr.getPhone()));
                }, selectedAddressProperty))
                .otherwise(Bindings.selectString(customerComboBox.getSelectionModel().selectedItemProperty(), "multiLineAddress")));

        locationValid = validationMessageBinding.isEmpty();
        validationBinding = validationBinding.and(locationValid);
        selectedAddressProperty.addListener((observable, oldValue, newValue) -> updateValidity());
        phoneProperty.addListener((observable, oldValue, newValue) -> updateValidity());
        locationProperty.addListener((observable, oldValue, newValue) -> updateValidity());
    }

    private void initializeContactTextField() {
        contactTextField.setText(model.getContact());
        StringProperty textProperty = contactTextField.textProperty();
        normalizedContactBinding = BindingHelper.asNonNullAndWsNormalized(textProperty);
        StringBinding validationMessageBinding = Bindings.when(normalizedContactBinding.isEmpty())
                .then(Bindings.when(typeComboBox.getSelectionModel().selectedItemProperty().isEqualTo(AppointmentType.OTHER)).then("Contact cannot be empty.").otherwise(""))
                .otherwise(Bindings.when(normalizedContactBinding.length().greaterThan(scheduler.model.Appointment.MAX_LENGTH_CONTACT))
                        .then("Contact too long.")
                        .otherwise(""));
        contactValidationLabel.textProperty().bind(validationMessageBinding);
        contactValidationLabel.visibleProperty().bind(validationMessageBinding.isEmpty());
        contactValid = validationMessageBinding.isEmpty();
        validationBinding = validationBinding.and(contactValid);
        textProperty.addListener((observable, oldValue, newValue) -> updateValidity());
    }

    private void initializeUrlTextField() {
        urlTextField.setText(model.getUrl());
        StringProperty textProperty = urlTextField.textProperty();
        normalizedUrlBinding = BindingHelper.asTrimmedAndNotNull(textProperty);
        BooleanBinding isVirtualBinding = typeComboBox.getSelectionModel().selectedItemProperty().isEqualTo(AppointmentType.VIRTUAL);

        StringBinding validationMessageBinding = Bindings.createStringBinding(() -> {
            String text = normalizedUrlBinding.get();
            if (isVirtualBinding.get()) {
                if (text.isEmpty()) {
                    return "URL cannot be empty.";
                }
            } else if (!text.isEmpty()) {
                if (text.length() > scheduler.model.Appointment.MAX_LENGTH_URL) {
                    return "URL too long.";
                }
                URI uri;
                try {
                    if (!(uri = new URI(text)).isAbsolute() || uri.isAbsolute()) {
                        uri = null;
                    }
                } catch (URISyntaxException ex) {
                    LOG.log(Level.FINE, "URI parse error", ex);
                    uri = null;
                }
                if (null == uri) {
                    return "Invalid URL format";
                }
            }
            return "";
        }, textProperty, isVirtualBinding);
        urlValidationLabel.textProperty().bind(validationMessageBinding);
        urlValidationLabel.visibleProperty().bind(validationMessageBinding.isEmpty());
        urlValid = validationMessageBinding.isEmpty();
        validationBinding = validationBinding.and(urlValid);
        textProperty.addListener((observable, oldValue, newValue) -> updateValidity());
    }

    private void initializeTypeComboBox() {
        typeComboBox.setItems(FXCollections.observableArrayList(AppointmentType.values()));
        SingleSelectionModel<AppointmentType> typeSelectionModel = typeComboBox.getSelectionModel();
        typeSelectionModel.select(model.getType());
        switch (typeSelectionModel.getSelectedItem()) {
            case CUSTOMER_SITE:
                break;
            case PHONE:
                phoneTextField.setText(model.getLocation());
                break;
            case CORPORATE_LOCATION:
                corporateLocationComboBox.getSelectionModel().select(PredefinedData.getCorporateAddress(model.getLocation()));
                break;
            default:
                locationTextArea.setText(model.getLocation());
                break;
        }
        typeSelectionModel.selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            onAppointmentTypeChanged(newValue);
            updateValidity();
        });
    }

    private void onAppointmentTypeChanged(AppointmentType type) {
        updateValidity();
    }

    private void initializeAppointmentConflicts(List<AppointmentDAO> appointments) {
        if (null == appointments) {
            appointments = Collections.emptyList();
        }
        appointmentConflicts.setSelectedTimeSpan(dateRangeControl.getTimeSpan());
        appointmentConflicts.initializeConflictCheckData(Tuple.of(customerComboBox.getSelectionModel().getSelectedItem(), userComboBox.getSelectionModel().getSelectedItem()),
                appointments);
        appointmentConflicts.conflictMessageProperty().addListener((observable, oldValue, newValue) -> dateRangeControl.setConflictMessage(newValue));
        appointmentConflicts.conflictCheckStatusProperty().addListener((observable, oldValue, newValue) -> dateRangeControl.setConflictCheckStatus(newValue));
        dateRangeControl.setConflictMessage(appointmentConflicts.getConflictMessage());
        dateRangeControl.setConflictCheckStatus(appointmentConflicts.getConflictCheckStatus());
    }

    private void initializeEditMode() {
        windowTitle.set(resources.getString(RESOURCEKEY_EDITAPPOINTMENT));
    }

    public boolean applyChangesToModel() {
        ZonedAppointmentTimeSpan ts = dateRangeControl.getTimeSpan();
        LocalDateTime apptStart = ts.toZonedStartDateTime().withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime apptEnd = ts.toZonedEndDateTime().withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime busStart;
        LocalDateTime busEnd;
        try {
            busStart = apptStart.toLocalDate().atTime(AppResources.getBusinessHoursStart());
            busEnd = busStart.plusHours(AppResources.getBusinessHoursDuration());
        } catch (ParseException ex) {
            LOG.log(Level.SEVERE, "Error getting application-configured business hours", ex);
            AlertHelper.showErrorAlert(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_UNEXPECTEDERRORTITLE),
                    AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_SETTINGSLOADERROR));
            return false;
        }
        Optional<ButtonType> response;
        switch (appointmentConflicts.getConflictCheckStatus()) {
            case NOT_CHECKED:
                if (apptStart.compareTo(busEnd) > 0) {
                    response = AlertHelper.showWarningAlert((Stage) getScene().getWindow(), resources.getString(RESOURCEKEY_NOTCHECKEDTITLE),
                            resources.getString(RESOURCEKEY_NOTCHECKEDOCCURSAFTERBUSHRS), ButtonType.YES, ButtonType.NO);
                } else if (apptEnd.compareTo(busStart) < 0) {
                    response = AlertHelper.showWarningAlert((Stage) getScene().getWindow(), resources.getString(RESOURCEKEY_NOTCHECKEDTITLE),
                            resources.getString(RESOURCEKEY_NOTCHECKEDOCCURSBEFOREBUSHRS), ButtonType.YES, ButtonType.NO);
                } else if (apptStart.compareTo(busStart) < 0) {
                    if (apptEnd.compareTo(busEnd) > 0) {
                        response = AlertHelper.showWarningAlert((Stage) getScene().getWindow(), resources.getString(RESOURCEKEY_NOTCHECKEDTITLE),
                                resources.getString(RESOURCEKEY_NOTCHECKEDOUTSIDEBUSHRS), ButtonType.YES, ButtonType.NO);
                    } else {
                        response = AlertHelper.showWarningAlert((Stage) getScene().getWindow(), resources.getString(RESOURCEKEY_NOTCHECKEDTITLE),
                                resources.getString(RESOURCEKEY_NOTCHECKEDSTARTSBEFOREBUSHRS), ButtonType.YES, ButtonType.NO);
                    }
                } else if (apptEnd.compareTo(busEnd) > 0) {
                    response = AlertHelper.showWarningAlert((Stage) getScene().getWindow(), resources.getString(RESOURCEKEY_NOTCHECKEDTITLE),
                            resources.getString(RESOURCEKEY_NOTCHECKEDENDSAFTERBUSHRS), ButtonType.YES, ButtonType.NO);
                } else {
                    response = AlertHelper.showWarningAlert((Stage) getScene().getWindow(), resources.getString(RESOURCEKEY_NOTCHECKEDTITLE),
                            resources.getString(RESOURCEKEY_NOTCHECKEDMESSAGE), ButtonType.YES, ButtonType.NO);
                }
                break;
            case HAS_CONFLICT:
                if (apptStart.compareTo(busEnd) > 0) {
                    response = AlertHelper.showWarningAlert((Stage) getScene().getWindow(), resources.getString(RESOURCEKEY_SCHEDULINGCONFLICTTITLE),
                            resources.getString(RESOURCEKEY_SCHEDULINGCONFLICTOCCURSAFTERBUSHRS), ButtonType.YES, ButtonType.NO);
                } else if (apptEnd.compareTo(busStart) < 0) {
                    response = AlertHelper.showWarningAlert((Stage) getScene().getWindow(), resources.getString(RESOURCEKEY_SCHEDULINGCONFLICTTITLE),
                            resources.getString(RESOURCEKEY_SCHEDULINGCONFLICTOCCURSBEFOREBUSHRS), ButtonType.YES, ButtonType.NO);
                } else if (apptStart.compareTo(busStart) < 0) {
                    if (apptEnd.compareTo(busEnd) > 0) {
                        response = AlertHelper.showWarningAlert((Stage) getScene().getWindow(), resources.getString(RESOURCEKEY_SCHEDULINGCONFLICTTITLE),
                                resources.getString(RESOURCEKEY_SCHEDULINGCONFLICTOUTSIDEBUSHRS), ButtonType.YES, ButtonType.NO);
                    } else {
                        response = AlertHelper.showWarningAlert((Stage) getScene().getWindow(), resources.getString(RESOURCEKEY_SCHEDULINGCONFLICTTITLE),
                                resources.getString(RESOURCEKEY_SCHEDULINGCONFLICTSTARTSBEFOREBUSHRS), ButtonType.YES, ButtonType.NO);
                    }
                } else if (apptEnd.compareTo(busEnd) > 0) {
                    response = AlertHelper.showWarningAlert((Stage) getScene().getWindow(), resources.getString(RESOURCEKEY_SCHEDULINGCONFLICTTITLE),
                            resources.getString(RESOURCEKEY_SCHEDULINGCONFLICTENDSAFTERBUSHRS), ButtonType.YES, ButtonType.NO);
                } else {
                    response = AlertHelper.showWarningAlert((Stage) getScene().getWindow(), resources.getString(RESOURCEKEY_SCHEDULINGCONFLICTTITLE),
                            resources.getString(RESOURCEKEY_SCHEDULINGCONFLICTMESSAGE), ButtonType.YES, ButtonType.NO);
                }
                break;
            default:
                if (apptStart.compareTo(busEnd) > 0) {
                    response = AlertHelper.showWarningAlert((Stage) getScene().getWindow(), resources.getString(RESOURCEKEY_BUSHREXCTITLE),
                            resources.getString(RESOURCEKEY_BUSHREXCOCCURSAFTERBUSHRS), ButtonType.YES, ButtonType.NO);
                } else if (apptEnd.compareTo(busStart) < 0) {
                    response = AlertHelper.showWarningAlert((Stage) getScene().getWindow(), resources.getString(RESOURCEKEY_BUSHREXCTITLE),
                            resources.getString(RESOURCEKEY_BUSHREXCOCCURSBEFOREBUSHRS), ButtonType.YES, ButtonType.NO);
                } else if (apptStart.compareTo(busStart) < 0) {
                    if (apptEnd.compareTo(busEnd) > 0) {
                        response = AlertHelper.showWarningAlert((Stage) getScene().getWindow(), resources.getString(RESOURCEKEY_BUSHREXCTITLE),
                                resources.getString(RESOURCEKEY_BUSHREXCOUTSIDEBUSHRS), ButtonType.YES, ButtonType.NO);
                    } else {
                        response = AlertHelper.showWarningAlert((Stage) getScene().getWindow(), resources.getString(RESOURCEKEY_BUSHREXCTITLE),
                                resources.getString(RESOURCEKEY_BUSHREXCSTARTSBEFOREBUSHRS), ButtonType.YES, ButtonType.NO);
                    }
                } else if (apptEnd.compareTo(busEnd) > 0) {
                    response = AlertHelper.showWarningAlert((Stage) getScene().getWindow(), resources.getString(RESOURCEKEY_BUSHREXCTITLE),
                            resources.getString(RESOURCEKEY_BUSHREXCENDSAFTERBUSHRS), ButtonType.YES, ButtonType.NO);
                } else {
                    response = Optional.of(ButtonType.YES);
                }
                break;
        }

        return response.isPresent() && response.get() == ButtonType.YES;
    }

    @Override
    public boolean isValid() {
        return valid.get();
    }

    private void updateValidity() {
        if (validationBinding.get()) {
            if (!valid.get()) {
                valid.set(true);
            }
        } else if (valid.get()) {
            valid.set(false);
        }
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
    public String getWindowTitle() {
        return windowTitle.get();
    }

    @Override
    public ReadOnlyStringProperty windowTitleProperty() {
        return windowTitle.getReadOnlyProperty();
    }

    CustomerModel getCustomer() {
        return customerComboBox.getValue();
    }

    UserModel getUser() {
        return userComboBox.getValue();
    }

    @Override
    public EntityModel.EntityModelFactory<AppointmentDAO, AppointmentModel, AppointmentEvent, AppointmentSuccessEvent> modelFactory() {
        return AppointmentModel.FACTORY;
    }

    @Override
    public void applyChanges() {
        LOG.info("Applying changes");
        model.setTitle(normalizedTitleBinding.get());
        model.setContact(normalizedContactBinding.get());
        model.setUrl(normalizedUrlBinding.get());
        model.setDescription(descriptionTextArea.getText());
        model.setCustomer(customerComboBox.getSelectionModel().getSelectedItem());
        model.setUser(userComboBox.getSelectionModel().getSelectedItem());
        AppointmentType type = typeComboBox.getSelectionModel().getSelectedItem();
        model.setType(type);
        ZonedAppointmentTimeSpan ts = dateRangeControl.getTimeSpan();
        model.setStart(ts.toZonedStartDateTime().withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime());
        model.setEnd(ts.toZonedEndDateTime().withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime());
        switch (type) {
            case CORPORATE_LOCATION:
                model.setLocation(corporateLocationComboBox.getSelectionModel().getSelectedItem().getName());
                break;
            case CUSTOMER_SITE:
                model.setLocation(customerComboBox.getSelectionModel().getSelectedItem().getMultiLineAddress());
                break;
            case PHONE:
                model.setLocation(normalizedPhoneBinding.get());
                break;
            default:
                model.setLocation(normalizedLocationBinding.get());
                break;
        }
    }

    private class CustomerReloadTask extends Task<List<CustomerDAO>> {

        private final Optional<Boolean> loadOption;

        private CustomerReloadTask() {
            updateTitle(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_LOADINGCUSTOMERS));
            loadOption = showActiveCustomers;
        }

        @Override
        protected void succeeded() {
            LOG.info("Task succeeded");
            List<CustomerDAO> result = getValue();
            Optional<Boolean> currentOption = showActiveCustomers;
            if ((currentOption.isPresent()) ? loadOption.isPresent() && currentOption.get().equals(loadOption.get()) : !loadOption.get()) {
                SingleSelectionModel<CustomerModel> customerSelectionModel = customerComboBox.getSelectionModel();
                CustomerModel selectedItem = customerSelectionModel.getSelectedItem();
                customerModelList.clear();
                if (null != result && !result.isEmpty()) {
                    result.forEach((t) -> customerModelList.add(CustomerModel.FACTORY.createNew(t)));
                }
                if (null != selectedItem) {
                    int pk = selectedItem.getPrimaryKey();
                    Optional<CustomerModel> matching = customerModelList.stream().filter((t) -> t.getPrimaryKey() == pk).findFirst();
                    if (matching.isPresent()) {
                        customerSelectionModel.select(matching.get());
                    } else {
                        customerSelectionModel.clearSelection();
                    }
                }
            }
            super.succeeded();
        }

        @Override
        protected List<CustomerDAO> call() throws Exception {
            LOG.info("Invoked call");
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
            List<CustomerDAO> result;
            try (DbConnector dbConnector = new DbConnector()) {
                updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTEDTODB));
                CustomerDAO.FactoryImpl cf = CustomerDAO.FACTORY;
                if (loadOption.isPresent()) {
                    result = cf.load(dbConnector.getConnection(), cf.getActiveStatusFilter(loadOption.get()));
                } else {
                    result = cf.load(dbConnector.getConnection(), cf.getAllItemsFilter());
                }
            }
            if (null == result) {
                LOG.info("Returning a null result");
            } else {
                LOG.info(() -> String.format("Returning %d users", result.size()));
            }
            return result;
        }

    }

    private class UserReloadTask extends Task<List<UserDAO>> {

        private final Optional<Boolean> loadOption;

        private UserReloadTask() {
            updateTitle(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_LOADINGUSERS));
            loadOption = showActiveUsers;
        }

        @Override
        protected void succeeded() {
            LOG.info("Invoked scheduler.view.appointment.EditAppointment.UserReloadTask#succeeded");
            List<UserDAO> result = getValue();
            Optional<Boolean> currentOption = showActiveUsers;
            if ((currentOption.isPresent()) ? loadOption.isPresent() && currentOption.get().equals(loadOption.get()) : !loadOption.get()) {
                SingleSelectionModel<UserModel> userSelectionModel = userComboBox.getSelectionModel();
                UserModel selectedItem = userSelectionModel.getSelectedItem();
                userModelList.clear();
                if (null != result && !result.isEmpty()) {
                    result.forEach((t) -> userModelList.add(UserModel.FACTORY.createNew(t)));
                }
                if (null != selectedItem) {
                    int pk = selectedItem.getPrimaryKey();
                    Optional<UserModel> matching = userModelList.stream().filter((t) -> t.getPrimaryKey() == pk).findFirst();
                    if (matching.isPresent()) {
                        userSelectionModel.select(matching.get());
                    } else {
                        userSelectionModel.clearSelection();
                    }
                }
            }
            super.succeeded();
        }

        @Override
        protected List<UserDAO> call() throws Exception {
            LOG.info("Invoked scheduler.view.appointment.EditAppointment.UserReloadTask#call");
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
            List<UserDAO> result;
            try (DbConnector dbConnector = new DbConnector()) {
                updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTEDTODB));
                UserDAO.FactoryImpl uf = UserDAO.FACTORY;
                if (loadOption.isPresent()) {
                    if (loadOption.get()) {
                        result = uf.load(dbConnector.getConnection(), uf.getActiveUsersFilter());
                    } else {
                        result = uf.load(dbConnector.getConnection(), UserFilter.of(UserFilter.expressionOf(UserStatus.INACTIVE, ComparisonOperator.EQUALS)));
                    }
                } else {
                    result = uf.load(dbConnector.getConnection(), uf.getAllItemsFilter());
                }
            }
            if (null == result) {
                LOG.info("scheduler.view.appointment.EditAppointment.UserReloadTask#call: Returning a null result");
            } else {
                LOG.info(() -> String.format("scheduler.view.appointment.UserReloadTask.ItemsLoadTask#call: returning %d users", result.size()));
            }
            return result;
        }

    }

    private class ItemsLoadTask extends Task<List<AppointmentDAO>> {

        private List<CustomerDAO> customerDaoList;
        private List<UserDAO> userDaoList;
        private List<AppointmentDAO> appointments;
        private final Optional<Boolean> customerLoadOption;
        private final Optional<Boolean> userLoadOption;
        private final Customer appointmentCustomer;
        private final User appointmentUser;

        private ItemsLoadTask() {
            updateTitle(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
            customerDaoList = null;
            userDaoList = null;
            PartialCustomerModel<? extends Customer> customer = model.getCustomer();
            appointmentCustomer = (null == customer) ? null : customer.dataObject();
            PartialUserModel<? extends User> user = model.getUser();
            appointmentUser = (null == user) ? null : user.dataObject();
            customerLoadOption = showActiveCustomers;
            userLoadOption = showActiveUsers;
        }

        @Override
        protected void succeeded() {
            LOG.info("Invoked scheduler.view.appointment.EditAppointment.ItemsLoadTask#succeeded");
            initializeCustomerComboBox(customerDaoList);
            initializeUserComboBox(userDaoList);
            initializeAppointmentConflicts(getValue());
            EditAppointment_old.this.validationBinding = titleValid.and(customerValid).and(userValid).and(dateRangeValid).and(locationValid).and(contactValid).and(urlValid);
            onAppointmentTypeChanged(typeComboBox.getValue());
            updateValidity();
            CustomerModel.FACTORY.addEventHandler(CustomerSuccessEvent.DELETE_SUCCESS, new WeakEventHandler<>(EditAppointment_old.this.onCustomerDeleted));
            UserModel.FACTORY.addEventHandler(UserSuccessEvent.DELETE_SUCCESS, new WeakEventHandler<>(EditAppointment_old.this.onUserDeleted));
            super.succeeded();
        }

        @Override
        protected List<AppointmentDAO> call() throws Exception {
            LOG.info("Invoked scheduler.view.appointment.EditAppointment.ItemsLoadTask#call");
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
            List<AppointmentDAO> result;
            try (DbConnector dbConnector = new DbConnector()) {
                CustomerDAO.FactoryImpl cf = CustomerDAO.FACTORY;
                UserDAO.FactoryImpl uf = UserDAO.FACTORY;
                AppointmentDAO.FactoryImpl af = AppointmentDAO.FACTORY;
                updateMessage(AppResources.getResourceString(RESOURCEKEY_LOADINGCUSTOMERS));
                if (customerLoadOption.isPresent()) {
                    customerDaoList = cf.load(dbConnector.getConnection(), cf.getActiveStatusFilter(customerLoadOption.get()));
                } else {
                    customerDaoList = cf.load(dbConnector.getConnection(), cf.getAllItemsFilter());
                }
                LOG.info(() -> String.format("scheduler.view.appointment.EditAppointment.ItemsLoadTask#call: %d customers loaded", customerDaoList.size()));
                updateMessage(AppResources.getResourceString(RESOURCEKEY_LOADINGUSERS));
                if (userLoadOption.isPresent()) {
                    if (userLoadOption.get()) {
                        userDaoList = uf.load(dbConnector.getConnection(), uf.getActiveUsersFilter());
                    } else {
                        userDaoList = uf.load(dbConnector.getConnection(), UserFilter.of(UserFilter.expressionOf(UserStatus.INACTIVE, ComparisonOperator.EQUALS)));
                    }
                } else {
                    userDaoList = uf.load(dbConnector.getConnection(), uf.getAllItemsFilter());
                }
                LOG.info(() -> String.format("scheduler.view.appointment.EditAppointment.ItemsLoadTask#call: %d users loaded", userDaoList.size()));

                updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_LOADINGAPPOINTMENTS));
                if (null != customerDaoList && null != userDaoList && !(customerDaoList.isEmpty() || userDaoList.isEmpty())) {
                    if (null != appointmentCustomer && ModelHelper.existsInDatabase(appointmentCustomer)) {
                        if (null != appointmentUser && ModelHelper.existsInDatabase(appointmentUser)) {
                            result = af.load(dbConnector.getConnection(), AppointmentFilter.of(appointmentCustomer, appointmentUser, null, null));
                        } else {
                            result = af.load(dbConnector.getConnection(), AppointmentFilter.of(appointmentCustomer, null, null, null));
                        }
                    } else if (null != appointmentUser && ModelHelper.existsInDatabase(appointmentUser)) {
                        result = af.load(dbConnector.getConnection(), AppointmentFilter.of(null, appointmentUser, null, null));
                    } else {
                        result = null;
                    }
                } else {
                    result = null;
                }
            }
            if (null == result) {
                LOG.info("scheduler.view.appointment.EditAppointment.ItemsLoadTask#call: Returning a null result");
            } else {
                LOG.info(() -> String.format("scheduler.view.appointment.EditAppointment.ItemsLoadTask#call: returning %d appointments", result.size()));
            }
            return result;
        }

    }

}
