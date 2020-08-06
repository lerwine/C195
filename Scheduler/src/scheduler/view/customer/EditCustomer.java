package scheduler.view.customer;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import scheduler.AppResourceKeys;
import scheduler.AppResources;
import scheduler.Scheduler;
import scheduler.dao.AddressDAO;
import scheduler.dao.AppointmentDAO;
import scheduler.dao.CityDAO;
import scheduler.dao.CountryDAO;
import scheduler.dao.CustomerDAO;
import scheduler.dao.DataAccessObject;
import scheduler.dao.DataRowState;
import scheduler.dao.PartialAddressDAO;
import scheduler.dao.PartialCityDAO;
import scheduler.dao.PartialCountryDAO;
import scheduler.dao.filter.AppointmentFilter;
import scheduler.events.AddressOpRequestEvent;
import scheduler.events.AppointmentEvent;
import scheduler.events.AppointmentFailedEvent;
import scheduler.events.AppointmentOpRequestEvent;
import scheduler.events.AppointmentSuccessEvent;
import scheduler.events.CitySuccessEvent;
import scheduler.events.CountrySuccessEvent;
import scheduler.events.CustomerEvent;
import scheduler.events.ModelFailedEvent;
import scheduler.model.ModelHelper;
import scheduler.model.ModelHelper.AppointmentHelper;
import scheduler.model.ModelHelper.CityHelper;
import scheduler.model.ModelHelper.CountryHelper;
import scheduler.model.fx.AddressModel;
import scheduler.model.fx.AppointmentModel;
import scheduler.model.fx.CityModel;
import scheduler.model.fx.CountryModel;
import scheduler.model.fx.CustomerModel;
import scheduler.model.fx.EntityModel;
import scheduler.model.fx.PartialAddressModel;
import scheduler.model.fx.PartialCityModel;
import scheduler.model.fx.PartialCountryModel;
import scheduler.observables.BindingHelper;
import scheduler.util.AlertHelper;
import scheduler.util.DbConnector;
import scheduler.util.LogHelper;
import static scheduler.util.NodeUtil.clearAndSelect;
import static scheduler.util.NodeUtil.clearAndSelectEntity;
import static scheduler.util.NodeUtil.collapseNode;
import static scheduler.util.NodeUtil.isInShownWindow;
import static scheduler.util.NodeUtil.restoreNode;
import scheduler.util.Quadruplet;
import scheduler.util.ThrowableConsumer;
import scheduler.util.Tuple;
import scheduler.util.WeakEventHandlingReference;
import scheduler.view.EditItem;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.annotations.ModelEditor;
import scheduler.view.appointment.AppointmentModelFilter;
import scheduler.view.appointment.EditAppointment;
import scheduler.view.city.EditCity;
import scheduler.view.country.EditCountry;
import static scheduler.view.customer.EditCustomerResourceKeys.*;
import scheduler.view.task.WaitBorderPane;
import scheduler.view.task.WaitTitledPane;

/**
 * FXML Controller class for editing a {@link CustomerModel}.
 * <h3>Event Handling</h3>
 * <h4>SCHEDULER_APPOINTMENT_OP_REQUEST</h4>
 * <dl>
 * <dt>{@link #appointmentsTableView} &#123; {@link scheduler.fx.ItemEditTableCellFactory#onItemActionRequest} &#125; (creates) {@link AppointmentOpRequestEvent} &#123;</dt>
 * <dd>{@link javafx.event.Event#eventType} = {@link AppointmentOpRequestEvent#APPOINTMENT_OP_REQUEST "SCHEDULER_APPOINTMENT_OP_REQUEST"} &larr;
 * {@link scheduler.events.OperationRequestEvent#OP_REQUEST_EVENT "SCHEDULER_OP_REQUEST_EVENT"} &larr; {@link scheduler.events.ModelEvent#MODEL_EVENT_TYPE "SCHEDULER_MODEL_EVENT"}
 * </dd>
 * </dl>
 * &#125; (fires) {@link #onItemActionRequest(AppointmentOpRequestEvent)}
 * <dl>
 * <dt>SCHEDULER_APPOINTMENT_EDIT_REQUEST {@link AppointmentOpRequestEvent} &#123; {@link javafx.event.Event#eventType} = {@link AppointmentOpRequestEvent#EDIT_REQUEST} &#125;</dt>
 * <dd>&rarr; {@link EditAppointment#edit(AppointmentModel, javafx.stage.Window) EditAppointment.edit}(({@link AppointmentModel}) {@link scheduler.events.ModelEvent#getEntityModel()},
 * {@link javafx.stage.Window}) (creates) {@link scheduler.events.AppointmentEvent#APPOINTMENT_EVENT_TYPE "SCHEDULER_APPOINTMENT_EVENT"} &rArr;
 * {@link scheduler.model.fx.AppointmentModel.Factory}</dd>
 * <dt>SCHEDULER_APPOINTMENT_DELETE_REQUEST {@link AppointmentOpRequestEvent} &#123; {@link javafx.event.Event#eventType} = {@link AppointmentOpRequestEvent#DELETE_REQUEST}
 * &#125;</dt>
 * <dd>&rarr; {@link scheduler.dao.AppointmentDAO.DeleteTask#DeleteTask(scheduler.model.fx.AppointmentModel, boolean) new AppointmentDAO.DeleteTask}({@link AppointmentOpRequestEvent},
 * {@code false}) (creates) {@link scheduler.events.AppointmentEvent#APPOINTMENT_EVENT_TYPE "SCHEDULER_APPOINTMENT_EVENT"} &rArr;
 * {@link scheduler.model.fx.AppointmentModel.Factory}</dd>
 * </dl>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/view/customer/EditCustomer")
@FXMLResource("/scheduler/view/customer/EditCustomer.fxml")
public final class EditCustomer extends VBox implements EditItem.ModelEditorController<CustomerModel> {
    //<editor-fold defaultstate="collapsed" desc="Static Members">
    
    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(EditCustomer.class.getName()), Level.FINE);
//    private static final Logger LOG = Logger.getLogger(EditCustomer.class.getName());
    
    public static void editNew(PartialAddressModel<? extends PartialAddressDAO> address, Window parentWindow, boolean keepOpen, Consumer<CustomerModel> beforeShow) throws IOException {
        CustomerModel model = CustomerModel.FACTORY.getDaoFactory().createNew().cachedModel(true);
        if (null != address) {
            model.setAddress(address);
        }
        if (null != beforeShow) {
            beforeShow.accept(model);
        }
        EditItem.showAndWait(parentWindow, EditCustomer.class, model, keepOpen);
    }
    
    public static void editNew(PartialAddressModel<? extends PartialAddressDAO> address, Window parentWindow, boolean keepOpen) throws IOException {
        editNew(address, parentWindow, keepOpen, null);
    }
    
    public static void edit(CustomerModel model, Window parentWindow, ThrowableConsumer<Stage, IOException> beforeShow) throws IOException {
        EditItem.showAndWait(parentWindow, EditCustomer.class, model, false, beforeShow);
    }
    
    public static void edit(CustomerModel model, Window parentWindow) throws IOException {
        edit(model, parentWindow, null);
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Instance Fields">
    private final ReadOnlyBooleanWrapper valid;
    private final ReadOnlyBooleanWrapper modified;
    private final ReadOnlyStringWrapper windowTitle;
    private final ReadOnlyIntegerWrapper addressCustomerCount;
    private final ReadOnlyObjectWrapper<AddressModel> selectedAddress;
    private final ObservableList<String> unavailableNames;
    private final ObservableList<AppointmentModel> customerAppointments;
    private final ObservableList<CityModel> allCities;
    private final ObservableList<CityModel> cityOptions;
    private final ObservableList<CountryModel> allCountries;
    private final ObservableList<AppointmentFilterItem> filterOptions;
    private final WeakEventHandlingReference<AppointmentSuccessEvent> appointmentInsertEventHandler;
    private final WeakEventHandlingReference<AppointmentSuccessEvent> appointmentUpdateEventHandler;
    private final WeakEventHandlingReference<AppointmentSuccessEvent> appointmentDeleteEventHandler;
    private final WeakEventHandlingReference<CountrySuccessEvent> countryInsertEventHandler;
    private final WeakEventHandlingReference<CountrySuccessEvent> countryDeleteEventHandler;
    private final WeakEventHandlingReference<CitySuccessEvent> cityInsertEventHandler;
    private final WeakEventHandlingReference<CitySuccessEvent> cityUpdateEventHandler;
    private final WeakEventHandlingReference<CitySuccessEvent> cityDeleteEventHandler;
    private final WeakEventHandlingReference<AddressOpRequestEvent> addressDeleteEventHandler;
    private ReadOnlyObjectProperty<AppointmentFilterItem> selectedFilter;
    private StringBinding normalizedName;
    private StringBinding normalizedAddress1;
    private StringBinding normalizedAddress2;
    private ReadOnlyObjectProperty<CountryModel> selectedCountry;
    private ReadOnlyObjectProperty<CityModel> selectedCity;
    private StringBinding normalizedPostalCode;
    private StringBinding normalizedPhone;
    private ObjectBinding<CityModel> modelCity;
    private BooleanBinding addressChanged;
    private BooleanBinding changedBinding;
    private BooleanBinding validityBinding;

    @ModelEditor
    private CustomerModel model;

    @ModelEditor
    private WaitBorderPane waitBorderPane;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // fx:id="nameTextField"
    private TextField nameTextField; // Value injected by FXMLLoader

    @FXML // fx:id="nameValidationLabel"
    private Label nameValidationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="activeTrueRadioButton"
    private RadioButton activeTrueRadioButton; // Value injected by FXMLLoader

    @FXML // fx:id="activeToggleGroup"
    private ToggleGroup activeToggleGroup; // Value injected by FXMLLoader

    @FXML // fx:id="activeFalseRadioButton"
    private RadioButton activeFalseRadioButton; // Value injected by FXMLLoader

    @FXML // fx:id="address1TextField"
    private TextField address1TextField; // Value injected by FXMLLoader

    @FXML // fx:id="address2TextField"
    private TextField address2TextField; // Value injected by FXMLLoader

    @FXML // fx:id="addressValidationLabel"
    private Label addressValidationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="cityComboBox"
    private ComboBox<CityModel> cityComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="cityValidationLabel"
    private Label cityValidationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="postalCodeTextField"
    private TextField postalCodeTextField; // Value injected by FXMLLoader

    @FXML // fx:id="phoneNumberTextField"
    private TextField phoneNumberTextField; // Value injected by FXMLLoader

    @FXML // fx:id="countryComboBox"
    private ComboBox<CountryModel> countryComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="countryValidationLabel"
    private Label countryValidationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="newCityButton"
    private Button newCityButton; // Value injected by FXMLLoader

    @FXML // fx:id="appointmentFilterComboBox"
    private ComboBox<AppointmentFilterItem> appointmentFilterComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="appointmentsTableView"
    private TableView<AppointmentModel> appointmentsTableView; // Value injected by FXMLLoader

    @FXML // fx:id="addAppointmentButtonBar"
    private ButtonBar addAppointmentButtonBar; // Value injected by FXMLLoader
    private BooleanBinding nameValid;
    private BooleanBinding addressValid;
    private BooleanBinding cityInvalid;
    private StringBinding cityValidationMessage;
    private BooleanBinding modificationBinding;

    //</editor-fold>
    public EditCustomer() {
        addressCustomerCount = new ReadOnlyIntegerWrapper(this, "addressCustomerCount", 0);
        selectedAddress = new ReadOnlyObjectWrapper<>(this, "selectedAddress", new AddressDAO().cachedModel(true));
        windowTitle = new ReadOnlyStringWrapper(this, "windowTitle", "");
        valid = new ReadOnlyBooleanWrapper(this, "valid", false);
        modified = new ReadOnlyBooleanWrapper(this, "modified", true);
        unavailableNames = FXCollections.observableArrayList();
        customerAppointments = FXCollections.observableArrayList();
        filterOptions = FXCollections.observableArrayList();
        allCities = FXCollections.observableArrayList();
        cityOptions = FXCollections.observableArrayList();
        allCountries = FXCollections.observableArrayList();
        appointmentInsertEventHandler = WeakEventHandlingReference.create(this::onAppointmentInserted);
        appointmentUpdateEventHandler = WeakEventHandlingReference.create(this::onAppointmentUpdated);
        appointmentDeleteEventHandler = WeakEventHandlingReference.create(this::onAppointmentDeleted);
        countryInsertEventHandler = WeakEventHandlingReference.create(this::onCountryInserted);
        countryDeleteEventHandler = WeakEventHandlingReference.create(this::onCountryDeleted);
        cityInsertEventHandler = WeakEventHandlingReference.create(this::onCityInserted);
        cityUpdateEventHandler = WeakEventHandlingReference.create(this::onCityUpdated);
        cityDeleteEventHandler = WeakEventHandlingReference.create(this::onCityDeleted);
        addressDeleteEventHandler = WeakEventHandlingReference.create(this::onAddressDeleteRequest);
    }
    
    //<editor-fold defaultstate="collapsed" desc="FXML Event Handler Methods">
    
    @ModelEditor
    private void onModelInserted(CustomerEvent event) {
        LOG.entering(LOG.getName(), "onModelInserted", event);
        AppointmentModel.FACTORY.addEventHandler(AppointmentSuccessEvent.INSERT_SUCCESS, appointmentInsertEventHandler.getWeakEventHandler());
        AppointmentModel.FACTORY.addEventHandler(AppointmentSuccessEvent.UPDATE_SUCCESS, appointmentUpdateEventHandler.getWeakEventHandler());
        AppointmentModel.FACTORY.addEventHandler(AppointmentSuccessEvent.DELETE_SUCCESS, appointmentDeleteEventHandler.getWeakEventHandler());
        AddressModel.FACTORY.addEventHandler(AddressOpRequestEvent.DELETE_REQUEST, addressDeleteEventHandler.getWeakEventHandler());
        restoreNode(appointmentFilterComboBox);
        restoreNode(appointmentsTableView);
        restoreNode(addAppointmentButtonBar);
        windowTitle.set(resources.getString(RESOURCEKEY_EDITCUSTOMER));
        initializeEditMode();
        appointmentFilterComboBox.setOnAction(this::onAppointmentFilterComboBoxAction);
        LOG.exiting(LOG.getName(), "onModelInserted");
    }
    
    @FXML
    private void onNewAppointmentButtonAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onNewAppointmentButtonAction", event);
        try {
            EditAppointment.editNew(model, Scheduler.getCurrentUser().cachedModel(true), getScene().getWindow());
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error opening child window", ex);
        }
        LOG.exiting(LOG.getName(), "onNewAppointmentButtonAction");
    }
    
    @FXML
    @SuppressWarnings("incomplete-switch")
    private void onAppointmentsTableViewTableViewKeyReleased(KeyEvent event) {
        LOG.entering(LOG.getName(), "onAppointmentsTableViewTableViewKeyReleased", event);
        if (!(event.isAltDown() || event.isControlDown() || event.isMetaDown() || event.isShiftDown() || event.isShortcutDown())) {
            AppointmentModel item;
            switch (event.getCode()) {
                case DELETE:
                    item = appointmentsTableView.getSelectionModel().getSelectedItem();
                    if (null != item) {
                        deleteItem(item);
                    }
                    break;
                case ENTER:
                    item = appointmentsTableView.getSelectionModel().getSelectedItem();
                    if (null != item) {
                        editItem(item);
                    }
                    break;
            }
        }
        LOG.exiting(LOG.getName(), "onAppointmentsTableViewTableViewKeyReleased");
    }
    
    @FXML
    private void onDeleteAppointmentMenuItemAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onDeleteAppointmentMenuItemAction", event);
        AppointmentModel item = appointmentsTableView.getSelectionModel().getSelectedItem();
        if (null != item) {
            deleteItem(item);
        }
        LOG.exiting(LOG.getName(), "onDeleteAppointmentMenuItemAction");
    }
    
    @FXML
    private void onEditAppointmentMenuItemAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onEditAppointmentMenuItemAction", event);
        AppointmentModel item = appointmentsTableView.getSelectionModel().getSelectedItem();
        if (null != item) {
            editItem(item);
        }
        LOG.exiting(LOG.getName(), "onEditAppointmentMenuItemAction");
    }
    
    @FXML
    private void onItemActionRequest(AppointmentOpRequestEvent event) {
        LOG.entering(LOG.getName(), "onItemActionRequest", event);
        if (event.isEdit()) {
            editItem(event.getEntityModel());
        } else {
            deleteItem(event.getEntityModel());
        }
        LOG.exiting(LOG.getName(), "onItemActionRequest");
    }
    
    @FXML
    private void onNewCityButtonAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onNewCityButtonAction", event);
        try {
            EditCity.editNew(selectedCountry.get(), getScene().getWindow(), false);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error loading city edit window", ex);
        }
        LOG.exiting(LOG.getName(), "onNewCityButtonAction");
    }
    
    @FXML
    private void onNewCountryButtonAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onNewCountryButtonAction", event);
        try {
            EditCountry.editNew(getScene().getWindow(), false);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error loading city edit window", ex);
        }
        LOG.exiting(LOG.getName(), "onNewCountryButtonAction");
    }
    
    @FXML
    private void onAppointmentFilterComboBoxAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onAppointmentFilterComboBoxAction", event);
        waitBorderPane.startNow(new AppointmentReloadTask());
        LOG.exiting(LOG.getName(), "onAppointmentFilterComboBoxAction");
    }
    
    //</editor-fold>
    
    @FXML // This method is called by the FXMLLoader when initialization is complete
    private void initialize() {
        LOG.entering(LOG.getName(), "initialize");
        //<editor-fold defaultstate="collapsed" desc="Assertion checks">
        
        assert nameTextField != null : "fx:id=\"nameTextField\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert nameValidationLabel != null : "fx:id=\"nameValidationLabel\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert activeTrueRadioButton != null : "fx:id=\"activeTrueRadioButton\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert activeToggleGroup != null : "fx:id=\"activeToggleGroup\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert activeFalseRadioButton != null : "fx:id=\"activeFalseRadioButton\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert address1TextField != null : "fx:id=\"address1TextField\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert address2TextField != null : "fx:id=\"address2TextField\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert addressValidationLabel != null : "fx:id=\"addressValidationLabel\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert cityComboBox != null : "fx:id=\"cityComboBox\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert cityValidationLabel != null : "fx:id=\"cityValidationLabel\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert postalCodeTextField != null : "fx:id=\"postalCodeTextField\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert phoneNumberTextField != null : "fx:id=\"phoneNumberTextField\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert countryComboBox != null : "fx:id=\"countryComboBox\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert countryValidationLabel != null : "fx:id=\"countryValidationLabel\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert newCityButton != null : "fx:id=\"newCityButton\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert appointmentFilterComboBox != null : "fx:id=\"appointmentFilterComboBox\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert appointmentsTableView != null : "fx:id=\"appointmentsTableView\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert addAppointmentButtonBar != null : "fx:id=\"addAppointmentButtonBar\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        
        //</editor-fold>
        
        cityComboBox.setItems(cityOptions);
        countryComboBox.setItems(allCountries);
        appointmentFilterComboBox.setItems(filterOptions);
        appointmentsTableView.setItems(customerAppointments);

        selectedFilter = appointmentFilterComboBox.getSelectionModel().selectedItemProperty();
        selectedCountry = countryComboBox.getSelectionModel().selectedItemProperty();
        selectedCity = cityComboBox.getSelectionModel().selectedItemProperty();

        // Binding which returns a trimmed string from the customer name text field with all whitespace characters normalized to a single space character.
        // The value from this binding will be used to determine if the customer name field is valid,
        // and as the value to store in the customer model object when it is saved.
        normalizedName = BindingHelper.asNonNullAndWsNormalized(nameTextField.textProperty());

        // Binding which returns true when a non-empty value is contained in the customer name text field.
        // The value from this binding determines the visibility of the user name validation label, and also used as part of the overall validation binding.
        nameValid = normalizedName.isNotEmpty();
        
        // Makes the name validation error label visible when the name text field is empty or contains only non-whitspace characters.
        nameValidationLabel.visibleProperty().bind(nameValid.not());

        // Binding which returns a trimmed string from the first address line text field with all whitespace characters normalized to a single space character.
        // The value from this binding will be used as the value to store in the customer model object.
        normalizedAddress1 = BindingHelper.asNonNullAndWsNormalized(address1TextField.textProperty());
        
        // Binding which returns a trimmed string from the second address line text field with all whitespace characters normalized to a single space character.
        // The value from this binding will be used as the value to store in the customer model object.
        normalizedAddress2 = BindingHelper.asNonNullAndWsNormalized(address2TextField.textProperty());

        // Binding which returns true when a non-empty value is contained in either the first or second address line text field.
        // The value from this binding determines the visibility of the address validation label, and also used as part of the overall validation binding.
        addressValid = normalizedAddress1.isNotEmpty().or(normalizedAddress2.isNotEmpty());

        // Makes the address validation error label visible when both the first and second address line text fields are empty or contain only non-whitspace characters.
        addressValidationLabel.visibleProperty().bind(addressValid.not());

        // Disables the city combo box when no country is selected.
        cityComboBox.disableProperty().bind(selectedCountry.isNull());

        // Disables the 'new city' button when no country is selected.
        newCityButton.disableProperty().bind(selectedCountry.isNull());

        // Makes the country validation error label visible when no country is selected.
        countryValidationLabel.visibleProperty().bind(selectedCountry.isNull());

        // Calculates the validation message to display for the city combo box.
        cityValidationMessage = Bindings.createStringBinding(() -> {
            PartialCityModel<? extends PartialCityDAO> c = selectedCity.get();
            PartialCountryModel<? extends PartialCountryDAO> n = selectedCountry.get();
            if (null == n) {
                return "Country must be selected, first";
            }
            return (null == c) ? "* Required" : "";
        }, selectedCity, selectedCountry);

        // If the city validation message binding returns a non-empty string, then the the city is not valid.
        cityInvalid = cityValidationMessage.isNotEmpty();

        // Bind the result of the city validation message to the city validation error label.
        cityValidationLabel.textProperty().bind(cityValidationMessage);

        // Makes the city validation error label visible when the validation message is not empty.
        cityValidationLabel.visibleProperty().bind(cityInvalid);

        // Binding which returns a trimmed string from the postal code text field with all whitespace characters normalized to a single space character.
        // The value from this binding will be used to determine if the postal code is valid,
        // and as the value to store in the related address model object when it is saved.
        normalizedPostalCode = BindingHelper.asNonNullAndWsNormalized(postalCodeTextField.textProperty());
        
        // Binding which returns a trimmed string from the phone number text field with all whitespace characters normalized to a single space character.
        // The value from this binding will be used to determine if the phone number is valid,
        // and as the value to store in the related address model object when it is saved.
        normalizedPhone = BindingHelper.asNonNullAndWsNormalized(phoneNumberTextField.textProperty());

        // A binding which returns the nested city model of the customer's address.
        modelCity = Bindings.select(model.addressProperty(), AddressModel.PROP_CITY);

        // Returns true if any address-related properties are different than the address model associated with the customer model.
        addressChanged = normalizedAddress1.isNotEqualTo(BindingHelper.asNonNullAndWsNormalized(Bindings.selectString(selectedAddress,
                AddressModel.PROP_ADDRESS1)))
                .or(normalizedAddress2.isNotEqualTo(BindingHelper.asNonNullAndWsNormalized(Bindings.selectString(selectedAddress,
                        AddressModel.PROP_ADDRESS2))))
                .or(Bindings.createBooleanBinding(() -> !ModelHelper.areSameRecord(selectedCity.get(), modelCity.get()), selectedCity, modelCity))
                .or(normalizedPostalCode.isNotEqualTo(BindingHelper.asNonNullAndWsNormalized(Bindings.selectString(selectedAddress,
                        AddressModel.PROP_POSTALCODE))))
                .or(normalizedPhone.isNotEqualTo(BindingHelper.asNonNullAndWsNormalized(Bindings.selectString(selectedAddress,
                        AddressModel.PROP_PHONE))));

        // Returns true if there are any changes that need to be saved to the model.
        changedBinding = model.rowStateProperty().isEqualTo(DataRowState.NEW)
                .or(normalizedName.isNotEqualTo(BindingHelper.asNonNullAndWsNormalized(model.nameProperty())))
                .or(addressChanged).or(activeTrueRadioButton.selectedProperty().isNotEqualTo(model.activeProperty()));

        // Returns true if all fields are valid. This binding is used to calculate the overall binding, which determines whether the "save" button can be enabled.
        validityBinding = nameValid.and(addressValid).and(cityInvalid.not());

        selectedCountry.addListener(this::onSelectedCountryChanged);

        postalCodeTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            LOG.entering("scheduler.view.customer.EditCustomer.postalCodeTextField#text", "changed", new Object[]{oldValue, newValue});
            modified.set(changedBinding.get());
            LOG.exiting("scheduler.view.customer.EditCustomer.postalCodeTextField#text", "changed");
        });
        phoneNumberTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            LOG.entering("scheduler.view.customer.EditCustomer.phoneNumberTextField#text", "changed", new Object[]{oldValue, newValue});
            modified.set(changedBinding.get());
            LOG.exiting("scheduler.view.customer.EditCustomer.phoneNumberTextField#text", "changed");
        });

        valid.set(validityBinding.get());
        validityBinding.addListener((observable, oldValue, newValue) -> valid.set(newValue));
        nameTextField.setText(model.getName());
        activeToggleGroup.selectToggle((model.isActive()) ? activeTrueRadioButton : activeFalseRadioButton);
        onSelectedCountryChanged(selectedCountry, null, selectedCountry.get());

        WaitTitledPane pane = WaitTitledPane.create();
        pane.addOnFailAcknowledged((evt) -> getScene().getWindow().hide())
                .addOnCancelAcknowledged((evt) -> getScene().getWindow().hide());
        if (model.isNewRow()) {
            collapseNode(appointmentFilterComboBox);
            collapseNode(appointmentsTableView);
            collapseNode(addAppointmentButtonBar);
            windowTitle.set(resources.getString(RESOURCEKEY_ADDNEWCUSTOMER));
            waitBorderPane.startNow(pane, new NewDataLoadTask());
        } else {
            initializeEditMode();
            waitBorderPane.startNow(pane, new EditDataLoadTask());
        }
        LOG.exiting(LOG.getName(), "initialize");
    }

    private void initializeEditMode() {
        LocalDate today = LocalDate.now();
        CustomerDAO dao = model.dataObject();
        filterOptions.add(new AppointmentFilterItem(resources.getString(RESOURCEKEY_CURRENTANDFUTURE), AppointmentModelFilter.of(today, null, dao)));
        filterOptions.add(new AppointmentFilterItem(resources.getString(RESOURCEKEY_CURRENTAPPOINTMENTS),
                AppointmentModelFilter.of(today, today.plusDays(1), dao)));
        filterOptions.add(new AppointmentFilterItem(resources.getString(RESOURCEKEY_PASTAPPOINTMENTS), AppointmentModelFilter.of(null, today, dao)));
        filterOptions.add(new AppointmentFilterItem(resources.getString(RESOURCEKEY_ALLAPPOINTMENTS), AppointmentModelFilter.of(dao)));
        appointmentFilterComboBox.getSelectionModel().selectFirst();
        modified.set(false);
        modificationBinding = normalizedName.isNotEqualTo(model.nameProperty()).or(selectedAddress.isNotEqualTo(model.getAddress()))
                .or(activeTrueRadioButton.selectedProperty().isNotEqualTo(model.activeProperty()));
        modificationBinding.addListener((observable, oldValue, newValue) -> modified.set(newValue));
        selectedFilter.addListener((observable, oldValue, newValue) -> waitBorderPane.startNow(new AppointmentReloadTask()));
        windowTitle.set(resources.getString(RESOURCEKEY_EDITCUSTOMER));
    }

    private void editItem(AppointmentModel item) {
        try {
            EditAppointment.edit(item, getScene().getWindow());
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error opening child window", ex);
        }
    }

    private void deleteItem(AppointmentModel target) {
        AppointmentOpRequestEvent deleteRequestEvent = new AppointmentOpRequestEvent(target, this, true);
        Event.fireEvent(target.dataObject(), deleteRequestEvent);
        Stage stage = (Stage) getScene().getWindow();
        if (deleteRequestEvent.isCanceled()) {
            AlertHelper.showWarningAlert(stage, deleteRequestEvent.getCancelMessage(), ButtonType.OK);
        } else {
            AlertHelper.showWarningAlert(stage, LOG,
                    AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONFIRMDELETE),
                    AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO).ifPresent((t) -> {
                if (t == ButtonType.YES) {
                    DataAccessObject.DeleteDaoTask<AppointmentDAO, AppointmentModel> task = AppointmentModel.FACTORY.createDeleteTask(target);
                    task.setOnSucceeded((e) -> {
                        AppointmentEvent appointmentEvent = (AppointmentEvent) task.getValue();
                        if (null != appointmentEvent && appointmentEvent instanceof AppointmentFailedEvent) {
                            scheduler.util.AlertHelper.showWarningAlert(getScene().getWindow(), "Delete Failure",
                                    ((ModelFailedEvent<AppointmentDAO, AppointmentModel>) appointmentEvent).getMessage(), ButtonType.OK);
                        }
                    });
                    waitBorderPane.startNow(task);
                }
            });
        }

    }

    private void onSelectedCountryChanged(ObservableValue<? extends PartialCountryModel<? extends PartialCountryDAO>> observable,
            PartialCountryModel<? extends PartialCountryDAO> oldValue, PartialCountryModel<? extends PartialCountryDAO> newValue) {
        LOG.entering(LOG.getName(), "onSelectedCountryChanged", new Object[]{oldValue, newValue});
        cityComboBox.getSelectionModel().clearSelection();
        cityOptions.clear();
        if (null != newValue) {
            CityHelper.matchesCountry(newValue.getPrimaryKey(), allCities).forEach((t) -> cityOptions.add(t));
        }
        LOG.exiting(LOG.getName(), "onSelectedCountryChanged");
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
    public String getWindowTitle() {
        return windowTitle.get();
    }

    @Override
    public ReadOnlyStringProperty windowTitleProperty() {
        return windowTitle.getReadOnlyProperty();
    }

    @Override
    public EntityModel.EntityModelFactory<CustomerDAO, CustomerModel> modelFactory() {
        return CustomerModel.FACTORY;
    }

    private void loadData(int sameAddr, Tuple<AddressDAO, List<CityDAO>> addressAndCities, List<CountryDAO> countries) {
        addressCustomerCount.set(sameAddr);
        PartialAddressModel<? extends PartialAddressDAO> addrItem = model.getAddress();
        AddressModel address;
        if (null != addrItem && addrItem instanceof AddressModel) {
            address = (AddressModel) addrItem;
        } else {
            address = addressAndCities.getValue1().cachedModel(true);
        }
        selectedAddress.set(address);
        PartialCityModel<? extends PartialCityDAO> city;
        PartialCountryModel<? extends PartialCountryDAO> country;
        city = address.getCity();
        if (null != city) {
            country = city.getCountry();
        } else {
            country = null;
        }
        address1TextField.setText(address.getAddress1());
        address2TextField.setText(address.getAddress2());
        postalCodeTextField.setText(address.getPostalCode());
        phoneNumberTextField.setText(address.getPhone());

        allCountries.clear();
        allCities.clear();
        cityOptions.clear();
        countries.forEach((t) -> allCountries.add(t.cachedModel(true)));
        addressAndCities.getValue2().forEach((t) -> allCities.add(t.cachedModel(true)));
        allCountries.sort(CountryHelper::compare);
        allCities.sort(CityHelper::compare);
        clearAndSelectEntity(countryComboBox, country);
        clearAndSelectEntity(cityComboBox, city);

        CountryDAO.FACTORY.addEventFilter(CountrySuccessEvent.INSERT_SUCCESS, countryInsertEventHandler.getWeakEventHandler());
        CountryDAO.FACTORY.addEventFilter(CountrySuccessEvent.DELETE_SUCCESS, countryDeleteEventHandler.getWeakEventHandler());
        CityDAO.FACTORY.addEventFilter(CitySuccessEvent.INSERT_SUCCESS, cityInsertEventHandler.getWeakEventHandler());
        CityDAO.FACTORY.addEventFilter(CitySuccessEvent.UPDATE_SUCCESS, cityUpdateEventHandler.getWeakEventHandler());
        CityDAO.FACTORY.addEventFilter(CitySuccessEvent.DELETE_SUCCESS, cityDeleteEventHandler.getWeakEventHandler());
    }

    @Override
    public boolean applyChanges() {
        model.setName(nameTextField.getText().trim());
        if (model.getRowState() == DataRowState.NEW || null == model.getAddress() || addressChanged.get()) {
            AddressModel addr = selectedAddress.get();
            addr.setAddress1(normalizedAddress1.get());
            addr.setAddress2(normalizedAddress2.get());
            addr.setCity(selectedCity.get());
            addr.setPostalCode(normalizedPostalCode.get());
            addr.setPhone(normalizedPhone.get());
            model.setAddress(selectedAddress.get());
        }
        model.setActive(activeTrueRadioButton.isSelected());
        return true;
    }

    private void onAppointmentInserted(AppointmentSuccessEvent event) {
        LOG.entering(LOG.getName(), "onAppointmentAdded", event);
        if (isInShownWindow(this) && model.getRowState() != DataRowState.NEW) {
            AppointmentModel entityModel = event.getEntityModel();
            AppointmentFilterItem filter = selectedFilter.get();
            if ((null == filter) ? entityModel.getCustomer().getPrimaryKey() == model.getPrimaryKey() : filter.getModelFilter().test(entityModel)) {
                customerAppointments.add(entityModel);
                customerAppointments.sort(AppointmentHelper::compareByDates);
            }
        }
        LOG.exiting(LOG.getName(), "onAppointmentInserted");
    }

    private void onAppointmentUpdated(AppointmentSuccessEvent event) {
        LOG.entering(LOG.getName(), "onAppointmentUpdated", event);
        if (isInShownWindow(this) && model.getRowState() != DataRowState.NEW) {
            AppointmentFilterItem filter = selectedFilter.get();
            AppointmentModel entityModel = event.getEntityModel();
            AppointmentModel m = AppointmentModel.FACTORY.find(customerAppointments, entityModel).orElse(null);
            if (null != m) {
                if (m != entityModel) {
                    customerAppointments.remove(m);
                    customerAppointments.add(entityModel);
                    customerAppointments.sort(AppointmentHelper::compareByDates);
                }
                if ((null == filter) ? entityModel.getCustomer().getPrimaryKey() != model.getPrimaryKey() : !filter.getModelFilter().test(entityModel)) {
                    customerAppointments.remove(entityModel);
                }
            } else if ((null == filter) ? entityModel.getCustomer().getPrimaryKey() == model.getPrimaryKey()
                    : filter.getModelFilter().test(entityModel)) {
                customerAppointments.add(entityModel);
                customerAppointments.sort(AppointmentHelper::compareByDates);
            }
        }
        LOG.exiting(LOG.getName(), "onAppointmentUpdated");
    }

    private void onAppointmentDeleted(AppointmentSuccessEvent event) {
        LOG.entering(LOG.getName(), "onAppointmentDeleted", event);
        if (isInShownWindow(this)) {
            AppointmentModel.FACTORY.find(customerAppointments, event.getEntityModel()).ifPresent((t) -> {
                customerAppointments.remove(t);
            });
        }
        LOG.exiting(LOG.getName(), "onAppointmentDeleted");
    }

    private void onCountryInserted(CountrySuccessEvent event) {
        LOG.entering(LOG.getName(), "onCountryInserted", event);
        if (isInShownWindow(this)) {
            CountryModel entityModel = event.getEntityModel();
            allCountries.add(entityModel);
            allCountries.sort(CountryHelper::compare);
            clearAndSelect(countryComboBox, entityModel);
        }
        LOG.exiting(LOG.getName(), "onCountryInserted");
    }

    private void onCountryDeleted(CountrySuccessEvent event) {
        LOG.entering(LOG.getName(), "onCountryDeleted", event);
        if (isInShownWindow(this)) {
            CountryModel deletedCountry = ModelHelper.findByPrimaryKey(event.getEntityModel().getPrimaryKey(), allCountries).orElse(null);
            if (null != deletedCountry) {
                CountryModel currentCountry = selectedCountry.get();
                if (null != currentCountry && currentCountry == deletedCountry) {
                    countryComboBox.getSelectionModel().clearSelection();
                }
                allCountries.remove(deletedCountry);
            }
        }
        LOG.exiting(LOG.getName(), "onCountryDeleted");
    }

    private void onCityInserted(CitySuccessEvent event) {
        LOG.entering(LOG.getName(), "onCityInserted", event);
        if (isInShownWindow(this)) {
            CityModel entityModel = event.getEntityModel();
            allCities.add(entityModel);
            allCities.sort(CityHelper::compare);
            CountryModel countryModel = selectedCountry.get();
            if (null != countryModel && countryModel.getPrimaryKey() == entityModel.getCountry().getPrimaryKey()) {
                cityOptions.add(entityModel);
                cityOptions.sort(CityHelper::compare);
                clearAndSelect(cityComboBox, entityModel);
            }
        }
        LOG.exiting(LOG.getName(), "onCityInserted");
    }

    private void onCityUpdated(CitySuccessEvent event) {
        LOG.entering(LOG.getName(), "onCityUpdated", event);
        if (isInShownWindow(this)) {
            int pk = event.getEntityModel().getPrimaryKey();
            CityModel entityModel = ModelHelper.findByPrimaryKey(pk, cityOptions).orElse(null);
            if (null != entityModel && selectedCountry.get().getPrimaryKey() != entityModel.getCountry().getPrimaryKey()) {
                CityModel currentCity = selectedCity.get();
                if (null != currentCity && currentCity == entityModel) {
                    cityComboBox.getSelectionModel().clearSelection();
                }
                cityOptions.remove(entityModel);
            } else if (null != (entityModel = ModelHelper.findByPrimaryKey(pk, allCities).orElse(null))) {
                CountryModel currentCountry = selectedCountry.get();
                if (null != currentCountry && currentCountry.getPrimaryKey() == entityModel.getCountry().getPrimaryKey()) {
                    cityOptions.add(entityModel);
                }
            }
        }
        LOG.exiting(LOG.getName(), "onCityUpdated");
    }

    private void onCityDeleted(CitySuccessEvent event) {
        LOG.entering(LOG.getName(), "onCityDeleted", event);
        if (isInShownWindow(this)) {
            int pk = event.getEntityModel().getPrimaryKey();
            CityModel entityModel = ModelHelper.findByPrimaryKey(pk, cityOptions).orElse(null);
            if (null != entityModel) {
                CityModel currentCity = selectedCity.get();
                if (null != currentCity && currentCity == entityModel) {
                    cityComboBox.getSelectionModel().clearSelection();
                }
                cityOptions.remove(entityModel);
                allCities.remove(entityModel);
            } else {
                ModelHelper.findByPrimaryKey(pk, allCities).ifPresent((t) -> allCities.remove(t));
            }
        }
        LOG.exiting(LOG.getName(), "onCityDeleted");
    }

    private void onAddressDeleteRequest(AddressOpRequestEvent event) {
        LOG.entering(LOG.getName(), "onAddressDeleteRequest", event);
        AddressModel currentAddress = selectedAddress.get();
        if (isInShownWindow(this) && currentAddress.isExisting() && event.getEntityModel().getPrimaryKey() == selectedAddress.get().getPrimaryKey()) {
            event.setCancelMessage("Cannot delete the current address");
        }
        LOG.exiting(LOG.getName(), "onAddressDeleteRequest");
    }

    private class AppointmentFilterItem {

        private final ReadOnlyStringWrapper text;
        private final ReadOnlyObjectWrapper<AppointmentModelFilter> modelFilter;

        AppointmentFilterItem(String text, AppointmentModelFilter modelFilter) {
            this.text = new ReadOnlyStringWrapper(this, "text", text);
            this.modelFilter = new ReadOnlyObjectWrapper<>(this, "modelFilter", modelFilter);
        }

        @SuppressWarnings("unused")
        public String getText() {
            return text.get();
        }

        @SuppressWarnings("unused")
        public ReadOnlyStringProperty textProperty() {
            return text.getReadOnlyProperty();
        }

        public AppointmentModelFilter getModelFilter() {
            return modelFilter.get();
        }

        @SuppressWarnings("unused")
        public ReadOnlyObjectProperty<AppointmentModelFilter> modelFilterProperty() {
            return modelFilter.getReadOnlyProperty();
        }

        @Override
        public int hashCode() {
            return text.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return null != obj && obj instanceof AppointmentFilterItem && text.equals(((AppointmentFilterItem) obj).text);
        }

        @Override
        public String toString() {
            return text.get();
        }

    }

    private class EditDataLoadTask extends Task<Quadruplet<List<AppointmentDAO>, Tuple<List<CustomerDAO>, Integer>, Tuple<AddressDAO, List<CityDAO>>, List<CountryDAO>>> {

        private final AppointmentFilter filter;
        private final PartialAddressDAO address;
        private final CustomerDAO dao;

        private EditDataLoadTask() {
            updateTitle(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_LOADINGUSERS));
            AppointmentFilterItem filterItem = selectedFilter.get();
            dao = model.dataObject();
            address = dao.getAddress();
            filter = (null == filterItem) ? null : filterItem.getModelFilter().getDaoFilter();
        }

        @Override
        protected void succeeded() {
            LOG.entering("scheduler.view.customer.EditCustomer.EditDataLoadTask", "succeeded");
            Quadruplet<
                    List<AppointmentDAO>, Tuple<List<CustomerDAO>, Integer>, Tuple<AddressDAO, List<CityDAO>>, List<CountryDAO>> result = getValue();
            List<CustomerDAO> allCustomers = result.getValue2().getValue1();
            int sameAddr = result.getValue2().getValue2();
            if (null != allCustomers && !allCustomers.isEmpty()) {
                int pk = model.getPrimaryKey();
                allCustomers.stream().filter((t) -> t.getPrimaryKey() != pk).map((t) -> t.getName().toLowerCase()).forEach(unavailableNames::add);
            }
            if (null != result.getValue1() && !result.getValue1().isEmpty()) {
                result.getValue1().stream().map((t) -> t.cachedModel(true)).forEach(customerAppointments::add);
                customerAppointments.sort(AppointmentHelper::compareByDates);
            }
            appointmentFilterComboBox.setOnAction(EditCustomer.this::onAppointmentFilterComboBoxAction);
            waitBorderPane.startNow(new AppointmentReloadTask());
            loadData(sameAddr, result.getValue3(), result.getValue4());
            AppointmentModel.FACTORY.addEventHandler(AppointmentSuccessEvent.INSERT_SUCCESS, appointmentInsertEventHandler.getWeakEventHandler());
            AppointmentModel.FACTORY.addEventHandler(AppointmentSuccessEvent.UPDATE_SUCCESS, appointmentUpdateEventHandler.getWeakEventHandler());
            AppointmentModel.FACTORY.addEventHandler(AppointmentSuccessEvent.DELETE_SUCCESS, appointmentDeleteEventHandler.getWeakEventHandler());
            AddressModel.FACTORY.addEventHandler(AddressOpRequestEvent.DELETE_REQUEST, addressDeleteEventHandler.getWeakEventHandler());
            LOG.exiting("scheduler.view.customer.EditCustomer.EditDataLoadTask", "succeeded");
        }

        @Override
        protected Quadruplet<List<AppointmentDAO>, Tuple<List<CustomerDAO>, Integer>, Tuple<AddressDAO, List<CityDAO>>, List<CountryDAO>> call()
                throws Exception {
            LOG.entering("scheduler.view.customer.EditCustomer.EditDataLoadTask", "call");
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
            try (DbConnector dbConnector = new DbConnector()) {
                updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTEDTODB));
                CustomerDAO.FactoryImpl uf = CustomerDAO.FACTORY;
                AddressDAO addressDAO;
                if (null == address) {
                    addressDAO = new AddressDAO();
                } else if (address instanceof AddressDAO) {
                    addressDAO = (AddressDAO) address;
                } else {
                    addressDAO = AddressDAO.FACTORY.loadByPrimaryKey(dbConnector.getConnection(), address.getPrimaryKey()).get();
                }

                int sameAddr = (addressDAO.getRowState() == DataRowState.NEW) ? 0
                        : uf.countByAddress(dbConnector.getConnection(), addressDAO.getPrimaryKey());
                List<CustomerDAO> customers = uf.load(dbConnector.getConnection(), uf.getAllItemsFilter());
                updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_LOADINGCITIES));
                CityDAO.FactoryImpl tf = CityDAO.FACTORY;
                List<CityDAO> cities = tf.load(dbConnector.getConnection(), tf.getAllItemsFilter());
                updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_LOADINGCOUNTRIES));
                CountryDAO.FactoryImpl nf = CountryDAO.FACTORY;
                List<CountryDAO> countries = nf.load(dbConnector.getConnection(), nf.getAllItemsFilter());
                updateMessage(resources.getString(RESOURCEKEY_LOADINGAPPOINTMENTS));
                AppointmentDAO.FactoryImpl af = AppointmentDAO.FACTORY;
                LOG.exiting("scheduler.view.customer.EditCustomer.EditDataLoadTask", "call");
                return Quadruplet.of(af.load(dbConnector.getConnection(), (null != filter) ? filter : af.getAllItemsFilter()),
                        Tuple.of(customers, sameAddr), Tuple.of(addressDAO, cities), countries);
            }
        }

    }

    private class NewDataLoadTask extends Task<Quadruplet<List<CustomerDAO>, Integer, Tuple<AddressDAO, List<CityDAO>>, List<CountryDAO>>> {

        private final PartialAddressDAO address;

        private NewDataLoadTask() {
            address = model.dataObject().getAddress();
            updateTitle(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_LOADINGUSERS));
        }

        @Override
        protected void succeeded() {
            LOG.entering(getClass().getName(), "succeeded");
            Quadruplet<List<CustomerDAO>, Integer, Tuple<AddressDAO, List<CityDAO>>, List<CountryDAO>> result = getValue();
            if (null != result.getValue1() && !result.getValue1().isEmpty()) {
                result.getValue1().stream().map((t) -> t.getName().toLowerCase()).forEach(unavailableNames::add);
            }
            loadData(result.getValue2(), result.getValue3(), result.getValue4());
            LOG.exiting(getClass().getName(), "succeeded");
        }

        @Override
        protected Quadruplet<List<CustomerDAO>, Integer, Tuple<AddressDAO, List<CityDAO>>, List<CountryDAO>> call() throws Exception {
            LOG.entering(getClass().getName(), "call");
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
            try (DbConnector dbConnector = new DbConnector()) {
                updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTEDTODB));
                CustomerDAO.FactoryImpl uf = CustomerDAO.FACTORY;
                List<CustomerDAO> customers = uf.load(dbConnector.getConnection(), uf.getAllItemsFilter());
                AddressDAO addressDAO;
                if (null == address) {
                    addressDAO = new AddressDAO();
                } else if (address instanceof AddressDAO) {
                    addressDAO = (AddressDAO) address;
                } else {
                    addressDAO = AddressDAO.FACTORY.loadByPrimaryKey(dbConnector.getConnection(), address.getPrimaryKey()).get();
                }

                int sameAddr = (addressDAO.getRowState() == DataRowState.NEW) ? 0
                        : uf.countByAddress(dbConnector.getConnection(), addressDAO.getPrimaryKey());
                updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_LOADINGCITIES));
                CityDAO.FactoryImpl tf = CityDAO.FACTORY;
                List<CityDAO> cities = tf.load(dbConnector.getConnection(), tf.getAllItemsFilter());
                updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_LOADINGCOUNTRIES));
                CountryDAO.FactoryImpl nf = CountryDAO.FACTORY;
                List<CountryDAO> countries = nf.load(dbConnector.getConnection(), nf.getAllItemsFilter());
                LOG.exiting(getClass().getName(), "call");
                return Quadruplet.of(customers, sameAddr, Tuple.of(addressDAO, cities), countries);
            }
        }

    }

    private class AppointmentReloadTask extends Task<List<AppointmentDAO>> {

        private final AppointmentFilter filter;

        private AppointmentReloadTask() {
            updateTitle(AppResources.getResourceString(RESOURCEKEY_LOADINGAPPOINTMENTS));
            AppointmentFilterItem filterItem = selectedFilter.get();
            filter = (null == filterItem) ? null : filterItem.getModelFilter().getDaoFilter();
        }

        @Override
        protected void succeeded() {
            LOG.entering(getClass().getName(), "succeeded");
            List<AppointmentDAO> result = getValue();
            customerAppointments.clear();
            if (null != result && !result.isEmpty()) {
                result.forEach((t) -> {
                    customerAppointments.add(t.cachedModel(true));
                });
                customerAppointments.sort(AppointmentHelper::compareByDates);
            }
            LOG.exiting(getClass().getName(), "succeeded");
        }

        @Override
        protected List<AppointmentDAO> call() throws Exception {
            LOG.entering(getClass().getName(), "call");
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
            try (DbConnector dbConnector = new DbConnector()) {
                updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTEDTODB));
                AppointmentDAO.FactoryImpl af = AppointmentDAO.FACTORY;
                LOG.exiting(getClass().getName(), "call");
                return af.load(dbConnector.getConnection(), filter);
            }
        }

    }

}
