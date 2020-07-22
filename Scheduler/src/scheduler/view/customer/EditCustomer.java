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
public final class EditCustomer extends VBox implements EditItem.ModelEditorController<CustomerDAO, CustomerModel> {

    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(EditCustomer.class.getName()), Level.FINER);
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
    private ObjectBinding<AppointmentFilterItem> selectedFilter;
    private StringBinding normalizedName;
    private StringBinding normalizedAddress1;
    private StringBinding normalizedAddress2;
    private ObjectBinding<CountryModel> selectedCountry;
    private ObjectBinding<CityModel> selectedCity;
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
        updateValidation();
    }

    @FXML
    private void onAddAppointmentButtonAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onAddAppointmentButtonAction", event);
        try {
            EditAppointment.editNew(model, null, getScene().getWindow(), false);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error opening child window", ex);
        }
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
    }

    @FXML
    private void onDeleteAppointmentMenuItemAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onDeleteAppointmentMenuItemAction", event);
        AppointmentModel item = appointmentsTableView.getSelectionModel().getSelectedItem();
        if (null != item) {
            deleteItem(item);
        }
    }

    @FXML
    private void onEditAppointmentMenuItemAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onEditAppointmentMenuItemAction", event);
        AppointmentModel item = appointmentsTableView.getSelectionModel().getSelectedItem();
        if (null != item) {
            editItem(item);
        }
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

    @FXML
    private void onItemActionRequest(AppointmentOpRequestEvent event) {
        LOG.entering(LOG.getName(), "onItemActionRequest", event);
        if (event.isEdit()) {
            try {
                EditAppointment.edit(event.getEntityModel(), getScene().getWindow());
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, "Error opening child window", ex);
            }
        } else {
            deleteItem(event.getEntityModel());
        }
    }

    @FXML
    private void onNewCityButtonAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onNewCityButtonAction", event);
        try {
            EditCity.editNew(selectedCountry.get(), getScene().getWindow(), false);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error loading city edit window", ex);
        }
    }

    @FXML
    private void onNewCountryButtonAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onNewCountryButtonAction", event);
        try {
            EditCountry.editNew(getScene().getWindow(), false);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error loading city edit window", ex);
        }
    }

    private void onAppointmentFilterComboBoxAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onAppointmentFilterComboBoxAction", event);
        waitBorderPane.startNow(new AppointmentReloadTask());
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    private void initialize() {
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

        cityComboBox.setItems(cityOptions);
        countryComboBox.setItems(allCountries);
        appointmentFilterComboBox.setItems(filterOptions);
        appointmentsTableView.setItems(customerAppointments);

        selectedFilter = Bindings.<AppointmentFilterItem>select(appointmentFilterComboBox.selectionModelProperty(), "selectedItem");
        selectedCountry = Bindings.<CountryModel>select(countryComboBox.selectionModelProperty(), "selectedItem");
        selectedCity = Bindings.<CityModel>select(cityComboBox.selectionModelProperty(), "selectedItem");

        normalizedName = BindingHelper.asNonNullAndWsNormalized(nameTextField.textProperty());
        nameTextField.textProperty().addListener((observable, oldValue, newValue) -> updateValidation());
        BooleanBinding nameValid = nameTextField.textProperty().isNotEmpty();
        nameValidationLabel.visibleProperty().bind(nameValid.not());

        normalizedAddress1 = BindingHelper.asNonNullAndWsNormalized(address1TextField.textProperty());
        address1TextField.textProperty().addListener((observable, oldValue, newValue) -> updateValidation());

        normalizedAddress2 = BindingHelper.asNonNullAndWsNormalized(address2TextField.textProperty());
        address2TextField.textProperty().addListener((observable, oldValue, newValue) -> updateValidation());
        BooleanBinding addressValid = normalizedAddress1.isNotEmpty().or(normalizedAddress2.isNotEmpty());
        addressValidationLabel.visibleProperty().bind(addressValid.not());

        cityComboBox.disableProperty().bind(selectedCountry.isNull());
        newCityButton.disableProperty().bind(selectedCountry.isNull());
        countryValidationLabel.visibleProperty().bind(selectedCountry.isNull());
        selectedCountry.addListener(this::onSelectedCountryChanged);
        selectedCity.addListener(this::onSelectedCityChanged);
        StringBinding cityValidationMessage = Bindings.createStringBinding(() -> {
            PartialCityModel<? extends PartialCityDAO> c = selectedCity.get();
            PartialCountryModel<? extends PartialCountryDAO> n = selectedCountry.get();
            if (null == n) {
                return "Country must be selected, first";
            }
            return (null == c) ? "* Required" : "";
        }, selectedCity, selectedCountry);
        BooleanBinding cityInvalid = cityValidationMessage.isNotEmpty();
        cityValidationLabel.textProperty().bind(cityValidationMessage);
        cityValidationLabel.visibleProperty().bind(cityInvalid);

        normalizedPostalCode = BindingHelper.asNonNullAndWsNormalized(postalCodeTextField.textProperty());
        postalCodeTextField.textProperty().addListener((observable, oldValue, newValue) -> modified.set(changedBinding.get()));
        normalizedPhone = BindingHelper.asNonNullAndWsNormalized(phoneNumberTextField.textProperty());
        phoneNumberTextField.textProperty().addListener((observable, oldValue, newValue) -> modified.set(changedBinding.get()));

        modelCity = Bindings.select(model.addressProperty(), AddressModel.PROP_CITY);
        addressChanged = normalizedAddress1.isNotEqualTo(BindingHelper.asNonNullAndWsNormalized(Bindings.selectString(selectedAddress,
                AddressModel.PROP_ADDRESS1)))
                .or(normalizedAddress2.isNotEqualTo(BindingHelper.asNonNullAndWsNormalized(Bindings.selectString(selectedAddress,
                        AddressModel.PROP_ADDRESS2))))
                .or(Bindings.createBooleanBinding(() -> !ModelHelper.areSameRecord(selectedCity.get(), modelCity.get()), selectedCity, modelCity))
                .or(normalizedPostalCode.isNotEqualTo(BindingHelper.asNonNullAndWsNormalized(Bindings.selectString(selectedAddress,
                        AddressModel.PROP_POSTALCODE))))
                .or(normalizedPhone.isNotEqualTo(BindingHelper.asNonNullAndWsNormalized(Bindings.selectString(selectedAddress,
                        AddressModel.PROP_PHONE))));
        changedBinding = model.rowStateProperty().isEqualTo(DataRowState.NEW)
                .or(normalizedName.isNotEqualTo(BindingHelper.asNonNullAndWsNormalized(model.nameProperty())))
                .or(addressChanged).or(activeTrueRadioButton.selectedProperty().isNotEqualTo(model.activeProperty()));
        validityBinding = nameValid.and(addressValid).and(cityInvalid.not());

        nameTextField.setText(model.getName());
        activeToggleGroup.selectToggle((model.isActive()) ? activeTrueRadioButton : activeFalseRadioButton);
        onSelectedCountryChanged(selectedCountry, null, selectedCountry.get());
        onSelectedCityChanged(selectedCity, null, selectedCity.get());

        WaitTitledPane pane = new WaitTitledPane();
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
        windowTitle.set(resources.getString(RESOURCEKEY_EDITCUSTOMER));
    }

    private void onSelectedCountryChanged(ObservableValue<? extends PartialCountryModel<? extends PartialCountryDAO>> observable,
            PartialCountryModel<? extends PartialCountryDAO> oldValue, PartialCountryModel<? extends PartialCountryDAO> newValue) {
        LOG.fine(() -> String.format("Country selection changed from %s to %s", LogHelper.toLogText(oldValue), LogHelper.toLogText(newValue)));
        cityComboBox.getSelectionModel().clearSelection();
        cityOptions.clear();
        if (null != newValue) {
            CityHelper.matchesCountry(newValue.getPrimaryKey(), allCities).forEach((t) -> cityOptions.add(t));
        }
        updateValidation();
    }

    private void onSelectedCityChanged(ObservableValue<? extends PartialCityModel<? extends PartialCityDAO>> observable, PartialCityModel<? extends PartialCityDAO> oldValue,
            PartialCityModel<? extends PartialCityDAO> newValue) {
        updateValidation();
    }

    private void updateValidation() {
        modified.set(changedBinding.get());
        valid.set(validityBinding.get());
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
        if (null != country && country.getRowState() != DataRowState.NEW) {
            ModelHelper.findByPrimaryKey(country.getPrimaryKey(), allCountries).ifPresent((t) -> {
                countryComboBox.getSelectionModel().select(t);
                if (null != city && city.getRowState() != DataRowState.NEW) {
                    ModelHelper.findByPrimaryKey(city.getPrimaryKey(), cityOptions).ifPresent((u) -> {
                        cityComboBox.getSelectionModel().select(u);
                    });
                }
            });
        }

        cityComboBox.setOnAction((event) -> updateValidation());
        countryComboBox.setOnAction((event) -> updateValidation());
        CountryDAO.FACTORY.addEventFilter(CountrySuccessEvent.INSERT_SUCCESS, countryInsertEventHandler.getWeakEventHandler());
        CountryDAO.FACTORY.addEventFilter(CountrySuccessEvent.DELETE_SUCCESS, countryDeleteEventHandler.getWeakEventHandler());
        CityDAO.FACTORY.addEventFilter(CitySuccessEvent.INSERT_SUCCESS, cityInsertEventHandler.getWeakEventHandler());
        CityDAO.FACTORY.addEventFilter(CitySuccessEvent.UPDATE_SUCCESS, cityUpdateEventHandler.getWeakEventHandler());
        CityDAO.FACTORY.addEventFilter(CitySuccessEvent.DELETE_SUCCESS, cityDeleteEventHandler.getWeakEventHandler());
        updateValidation();
    }

    @Override
    public void applyChanges() {
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
    }

    private void onAppointmentDeleted(AppointmentSuccessEvent event) {
        LOG.entering(LOG.getName(), "onAppointmentDeleted", event);
        if (isInShownWindow(this)) {
            AppointmentModel.FACTORY.find(customerAppointments, event.getEntityModel()).ifPresent((t) -> {
                customerAppointments.remove(t);
            });
        }
    }

    private void onCountryInserted(CountrySuccessEvent event) {
        LOG.entering(LOG.getName(), "onCountryInserted", event);
        if (isInShownWindow(this)) {
            CountryModel entityModel = event.getEntityModel();
            allCountries.add(entityModel);
            allCountries.sort(CountryHelper::compare);
            countryComboBox.getSelectionModel().select(entityModel);
        }
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
                cityComboBox.getSelectionModel().select(entityModel);
            }
        }
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
    }

    private void onAddressDeleteRequest(AddressOpRequestEvent event) {
        LOG.entering(LOG.getName(), "onAddressDeleteRequest", event);
        AddressModel currentAddress = selectedAddress.get();
        if (isInShownWindow(this) && currentAddress.isExisting() && event.getEntityModel().getPrimaryKey() == selectedAddress.get().getPrimaryKey()) {
            event.setCancelMessage("Cannot delete the current address");
        }
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
        }

        @Override
        protected Quadruplet<List<AppointmentDAO>, Tuple<List<CustomerDAO>, Integer>, Tuple<AddressDAO, List<CityDAO>>, List<CountryDAO>> call()
                throws Exception {
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
            Quadruplet<List<CustomerDAO>, Integer, Tuple<AddressDAO, List<CityDAO>>, List<CountryDAO>> result = getValue();
            if (null != result.getValue1() && !result.getValue1().isEmpty()) {
                result.getValue1().stream().map((t) -> t.getName().toLowerCase()).forEach(unavailableNames::add);
            }
            loadData(result.getValue2(), result.getValue3(), result.getValue4());
        }

        @Override
        protected Quadruplet<List<CustomerDAO>, Integer, Tuple<AddressDAO, List<CityDAO>>, List<CountryDAO>> call() throws Exception {
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
            List<AppointmentDAO> result = getValue();
            customerAppointments.clear();
            if (null != result && !result.isEmpty()) {
                result.forEach((t) -> {
                    customerAppointments.add(t.cachedModel(true));
                });
                customerAppointments.sort(AppointmentHelper::compareByDates);
            }
        }

        @Override
        protected List<AppointmentDAO> call() throws Exception {
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
            try (DbConnector dbConnector = new DbConnector()) {
                updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTEDTODB));
                AppointmentDAO.FactoryImpl af = AppointmentDAO.FACTORY;
                return af.load(dbConnector.getConnection(), filter);
            }
        }

    }

}
