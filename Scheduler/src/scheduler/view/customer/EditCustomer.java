package scheduler.view.customer;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
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
import javafx.event.WeakEventHandler;
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
import scheduler.dao.DataRowState;
import scheduler.dao.IAddressDAO;
import scheduler.dao.ICityDAO;
import scheduler.dao.ICountryDAO;
import scheduler.dao.filter.AppointmentFilter;
import scheduler.model.CityProperties;
import scheduler.model.CountryProperties;
import scheduler.model.ModelHelper;
import scheduler.model.ui.AddressItem;
import scheduler.model.ui.AddressModel;
import scheduler.model.ui.AppointmentModel;
import scheduler.model.ui.CityItem;
import scheduler.model.ui.CityModel;
import scheduler.model.ui.CountryItem;
import scheduler.model.ui.CountryModel;
import scheduler.model.ui.CustomerModel;
import scheduler.model.ui.FxRecordModel;
import scheduler.observables.BindingHelper;
import scheduler.util.AlertHelper;
import scheduler.util.DbConnector;
import scheduler.util.LogHelper;
import static scheduler.util.NodeUtil.collapseNode;
import static scheduler.util.NodeUtil.restoreNode;
import scheduler.util.Quadruplet;
import scheduler.util.Tuple;
import scheduler.view.EditItem;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.annotations.ModelEditor;
import scheduler.view.appointment.AppointmentModelFilter;
import scheduler.view.appointment.EditAppointment;
import scheduler.view.city.EditCity;
import scheduler.view.country.EditCountry;
import static scheduler.view.customer.EditCustomerResourceKeys.*;
import scheduler.view.event.DbOperationType;
import scheduler.view.event.AppointmentEvent;
import scheduler.view.event.CustomerEvent;
import scheduler.view.task.WaitBorderPane;
import scheduler.view.task.WaitTitledPane;

/**
 * FXML Controller class for editing a {@link CustomerModel}.
 * <p>
 * The associated view is {@code /resources/scheduler/view/customer/EditCustomer.fxml}.</p>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/view/customer/EditCustomer")
@FXMLResource("/scheduler/view/customer/EditCustomer.fxml")
public final class EditCustomer extends VBox implements EditItem.ModelEditor<CustomerDAO, CustomerModel, CustomerEvent> {

    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(EditCustomer.class.getName()), Level.FINER);

    public static CustomerModel editNew(AddressItem<? extends IAddressDAO> address, Window parentWindow, boolean keepOpen) throws IOException {
        CustomerModel.Factory factory = CustomerModel.FACTORY;
        CustomerModel model = factory.createNew(factory.getDaoFactory().createNew());
        if (null != address) {
            model.setAddress(address);
        }
        return EditItem.showAndWait(parentWindow, EditCustomer.class, model, keepOpen);
    }

    public static CustomerModel edit(CustomerModel model, Window parentWindow) throws IOException {
        return EditItem.showAndWait(parentWindow, EditCustomer.class, model, false);
    }

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

    public EditCustomer() {
        addressCustomerCount = new ReadOnlyIntegerWrapper(this, "addressCustomerCount", 0);
        selectedAddress = new ReadOnlyObjectWrapper<>(this, "selectedAddress", new AddressModel(new AddressDAO()));
        windowTitle = new ReadOnlyStringWrapper(this, "windowTitle", "");
        valid = new ReadOnlyBooleanWrapper(this, "valid", false);
        modified = new ReadOnlyBooleanWrapper(this, "modified", true);
        unavailableNames = FXCollections.observableArrayList();
        customerAppointments = FXCollections.observableArrayList();
        filterOptions = FXCollections.observableArrayList();
        allCities = FXCollections.observableArrayList();
        cityOptions = FXCollections.observableArrayList();
        allCountries = FXCollections.observableArrayList();
    }

    @FXML
    private void onAddAppointmentButtonAction(ActionEvent event) {
        try {
            EditAppointment.editNew(model, null, getScene().getWindow(), false);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error opening child window", ex);
        }
    }

    @FXML
    @SuppressWarnings("incomplete-switch")
    private void onAppointmentsTableViewTableViewKeyReleased(KeyEvent event) {
        if (!(event.isAltDown() || event.isControlDown() || event.isMetaDown() || event.isShiftDown() || event.isShortcutDown())) {
            AppointmentModel item;
            switch (event.getCode()) {
                case DELETE:
                    item = appointmentsTableView.getSelectionModel().getSelectedItem();
                    if (null != item) {
                        onItemActionRequest(new AppointmentEvent(item, event.getSource(), this, DbOperationType.DELETE_REQUEST));
                    }
                    break;
                case ENTER:
                    item = appointmentsTableView.getSelectionModel().getSelectedItem();
                    if (null != item) {
                        onItemActionRequest(new AppointmentEvent(item, event.getSource(), this, DbOperationType.EDIT_REQUEST));
                    }
                    break;
            }
        }
    }

    @FXML
    private void onDeleteAppointmentMenuItemAction(ActionEvent event) {
        AppointmentModel item = appointmentsTableView.getSelectionModel().getSelectedItem();
        if (null != item) {
            onItemActionRequest(new AppointmentEvent(item, event.getSource(), this, DbOperationType.DELETE_REQUEST));
        }
    }

    @FXML
    private void onEditAppointmentMenuItemAction(ActionEvent event) {
        AppointmentModel item = appointmentsTableView.getSelectionModel().getSelectedItem();
        if (null != item) {
            onItemActionRequest(new AppointmentEvent(item, event.getSource(), this, DbOperationType.EDIT_REQUEST));
        }
    }

    @FXML
    @SuppressWarnings("incomplete-switch")
    private void onItemActionRequest(AppointmentEvent event) {
        AppointmentModel item;
        if (event.isConsumed() || null == (item = event.getModel())) {
            return;
        }
        switch (event.getOperation()) {
            case EDIT_REQUEST:
                try {
                    EditAppointment.edit(item, getScene().getWindow());
                } catch (IOException ex) {
                    LOG.log(Level.SEVERE, "Error opening child window", ex);
                }
                event.consume();
                break;
            case DELETE_REQUEST:
                Optional<ButtonType> response = AlertHelper.showWarningAlert((Stage) getScene().getWindow(), LOG,
                        AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONFIRMDELETE),
                        AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO);
                if (response.isPresent() && response.get() == ButtonType.YES) {
                    waitBorderPane.startNow(new DeleteTask(event));
                }
                event.consume();
                break;
        }
    }

    @FXML
    private void onNewCityButtonAction(ActionEvent event) {
        CityModel c;
        try {
            c = EditCity.editNew(selectedCountry.get(), getScene().getWindow(), false);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error loading city edit window", ex);
            c = null;
        }
        if (null != c) {
            allCities.add(c);
            CountryItem<? extends ICountryDAO> n = c.getCountry();
            int pk = n.getPrimaryKey();
            CountryModel sn = allCountries.stream().filter((t) -> t.getPrimaryKey() == pk).findFirst().orElseGet(() -> {
                if (n instanceof CountryModel) {
                    CountryModel cm = (CountryModel) n;
                    allCountries.add(cm);
                    allCountries.sort(CountryProperties::compare);
                    return cm;
                }
                return null;
            });
            countryComboBox.getSelectionModel().select(sn);
            if (null != sn) {
                cityOptions.add(c);
                cityOptions.sort(CityProperties::compare);
                cityComboBox.getSelectionModel().select(c);
            }
        }
    }

    @FXML
    private void onNewCountryButtonAction(ActionEvent event) {
        CountryModel c;
        try {
            c = EditCountry.editNew(getScene().getWindow(), false);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error loading city edit window", ex);
            c = null;
        }
        if (null != c) {
            allCountries.add(c);
            allCountries.sort(CountryProperties::compare);
            countryComboBox.getSelectionModel().select(c);
        }
    }

    private void onAppointmentFilterComboBoxAction(ActionEvent event) {
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
            CityItem<? extends ICityDAO> c = selectedCity.get();
            CountryItem<? extends ICountryDAO> n = selectedCountry.get();
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

        AppointmentModel.FACTORY.addEventHandler(AppointmentEvent.INSERTED_EVENT_TYPE, new WeakEventHandler<>(this::onAppointmentAdded));
        AppointmentModel.FACTORY.addEventHandler(AppointmentEvent.UPDATED_EVENT_TYPE, new WeakEventHandler<>(this::onAppointmentUpdated));
        AppointmentModel.FACTORY.addEventHandler(AppointmentEvent.DELETED_EVENT_TYPE, new WeakEventHandler<>(this::onAppointmentDeleted));

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

        addEventHandler(CustomerEvent.UPDATING_EVENT_TYPE, this::onCustomerUpdate);
        addEventHandler(CustomerEvent.INSERTING_EVENT_TYPE, this::onCustomerUpdate);
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

    private void onCustomerUpdate(CustomerEvent event) {
        AddressModel address = selectedAddress.get();
        int existingCount = addressCustomerCount.get();
        if (address.getRowState() != DataRowState.NEW) {
            IAddressDAO originalAddress = model.dataObject().getAddress();
            if (null != originalAddress && originalAddress.getRowState() != DataRowState.NEW
                    && address.getPrimaryKey() == originalAddress.getPrimaryKey()) {
                if (existingCount < 2) {
                    return;
                }
                existingCount--;
            } else if (existingCount < 1) {
                return;
            }
        } else if (existingCount < 1) {
            return;
        }
        Stage stage = (Stage) getScene().getWindow();
        StringBuilder message = new StringBuilder();
        if (existingCount == 1) {
            message.append("is 1 other customer that shares");
        } else {
            message.append("are ").append(existingCount).append(" other customers that share");
        }

        Optional<ButtonType> response = AlertHelper.showWarningAlert(stage, LOG,
                "Multiple Customers Affected",
                String.format("There %s the same address."
                        + "%nChange address for all customers?%nSelect \"No\" to create a new address for the current customer or "
                        + "\"Cancel\" to abort the save operation.", message), ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
        if (response.isPresent()) {
            if (response.get() == ButtonType.YES) {
                return;
            }
            if (response.get() == ButtonType.NO) {
                selectedAddress.set(new AddressModel(new AddressDAO()));
                return;
            }
        }
        event.consume();
    }

    private void onAppointmentAdded(AppointmentEvent event) {
        LOG.fine(() -> String.format("%s event handled", event.getEventType().getName()));
        if (model.getRowState() != DataRowState.NEW) {
            AppointmentDAO dao = event.getDataAccessObject();
            // XXX: See if we need to get/set model
            AppointmentFilterItem filter = selectedFilter.get();
            if ((null == filter) ? dao.getCustomer().getPrimaryKey() == model.getPrimaryKey() : filter.getModelFilter().getDaoFilter().test(dao)) {
                customerAppointments.add(new AppointmentModel(dao));
            }
        }
    }

    private void onAppointmentUpdated(AppointmentEvent event) {
        LOG.fine(() -> String.format("%s event handled", event.getEventType().getName()));
        if (model.getRowState() != DataRowState.NEW) {
            AppointmentDAO dao = event.getDataAccessObject();
            // XXX: See if we need to get/set model
            AppointmentFilterItem filter = selectedFilter.get();
            int pk = dao.getPrimaryKey();
            AppointmentModel m = customerAppointments.stream().filter((t) -> t.getPrimaryKey() == pk).findFirst().orElse(null);
            if (null != m) {
                if ((null == filter) ? dao.getCustomer().getPrimaryKey() != model.getPrimaryKey() : !filter.getModelFilter().test(m)) {
                    customerAppointments.remove(m);
                }
            } else if ((null == filter) ? dao.getCustomer().getPrimaryKey() == model.getPrimaryKey()
                    : filter.getModelFilter().getDaoFilter().test(dao)) {
                customerAppointments.add(new AppointmentModel(dao));
            }
        }
    }

    private void onAppointmentDeleted(AppointmentEvent event) {
        LOG.fine(() -> String.format("%s event handled", event.getEventType().getName()));
        if (model.getRowState() != DataRowState.NEW) {
            AppointmentDAO dao = event.getDataAccessObject();
            // XXX: See if we need to get/set model
            int pk = dao.getPrimaryKey();
            customerAppointments.stream().filter((t) -> t.getPrimaryKey() == pk).findFirst().ifPresent((t) -> customerAppointments.remove(t));
        }
    }

    private void onSelectedCountryChanged(ObservableValue<? extends CountryItem<? extends ICountryDAO>> observable,
            CountryItem<? extends ICountryDAO> oldValue, CountryItem<? extends ICountryDAO> newValue) {
        LOG.fine(() -> String.format("Country selection changed from %s to %s", LogHelper.toLogText(oldValue), LogHelper.toLogText(newValue)));
        cityComboBox.getSelectionModel().clearSelection();
        cityOptions.clear();
        if (null != newValue) {
            int pk = newValue.getPrimaryKey();
            allCities.filtered((t) -> t.getCountry().getPrimaryKey() == pk).forEach((t) -> cityOptions.add(t));
        }
        updateValidation();
    }

    private void onSelectedCityChanged(ObservableValue<? extends CityItem<? extends ICityDAO>> observable, CityItem<? extends ICityDAO> oldValue,
            CityItem<? extends ICityDAO> newValue) {
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
    public FxRecordModel.ModelFactory<CustomerDAO, CustomerModel, CustomerEvent> modelFactory() {
        return CustomerModel.FACTORY;
    }

    @Override
    public void onNewModelSaved() {
        restoreNode(appointmentFilterComboBox);
        restoreNode(appointmentsTableView);
        restoreNode(addAppointmentButtonBar);
        windowTitle.set(resources.getString(RESOURCEKEY_EDITCUSTOMER));
        initializeEditMode();
        appointmentFilterComboBox.setOnAction(this::onAppointmentFilterComboBoxAction);
        updateValidation();
    }

    @Override
    public void updateModel() {
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

    private void loadData(int sameAddr, Tuple<AddressDAO, List<CityDAO>> addressAndCities, List<CountryDAO> countries) {
        addressCustomerCount.set(sameAddr);
        AddressItem<? extends IAddressDAO> addrItem = model.getAddress();
        AddressModel address;
        if (null != addrItem && addrItem instanceof AddressModel) {
            address = (AddressModel) addrItem;
        } else {
            address = new AddressModel(addressAndCities.getValue1());
        }
        selectedAddress.set(address);
        CityItem<? extends ICityDAO> city;
        CountryItem<? extends ICountryDAO> country;
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
        countries.forEach((t) -> allCountries.add(new CountryModel(t)));
        addressAndCities.getValue2().forEach((t) -> allCities.add(new CityModel(t)));
        if (null != country && country.getRowState() != DataRowState.NEW) {
            int pk = country.getPrimaryKey();
            allCountries.stream().filter((t) -> pk == t.getPrimaryKey()).findFirst().ifPresent((t) -> {
                countryComboBox.getSelectionModel().select(t);
                if (null != city && city.getRowState() != DataRowState.NEW) {
                    int cpk = city.getPrimaryKey();
                    cityOptions.stream().filter((u) -> cpk == u.getPrimaryKey()).findFirst().ifPresent((u) -> {
                        cityComboBox.getSelectionModel().select(u);
                    });
                }
            });
        }

        cityComboBox.setOnAction((event) -> updateValidation());
        countryComboBox.setOnAction((event) -> updateValidation());
        updateValidation();
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
        private final IAddressDAO address;
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
                result.getValue1().stream().map((t) -> new AppointmentModel(t)).forEach(customerAppointments::add);
            }
            appointmentFilterComboBox.setOnAction(EditCustomer.this::onAppointmentFilterComboBoxAction);
            waitBorderPane.startNow(new AppointmentReloadTask());
            loadData(sameAddr, result.getValue3(), result.getValue4());
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

        private final IAddressDAO address;

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
                    customerAppointments.add(new AppointmentModel(t));
                });
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

    // See if this class should be moved or deleted
    private class AddressCustomerLoadTask extends Task<Integer> {

        private final IAddressDAO address;

        private AddressCustomerLoadTask() {
            address = model.dataObject().getAddress();
        }

        @Override
        protected Integer call() throws Exception {
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
            try (DbConnector dbConnector = new DbConnector()) {
                updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTEDTODB));
                CustomerDAO.FactoryImpl uf = CustomerDAO.FACTORY;
                return (null == address || address.getRowState() == DataRowState.NEW) ? 0
                        : uf.countByAddress(dbConnector.getConnection(), address.getPrimaryKey());
            }
        }

    }

    private class DeleteTask extends Task<Void> {

        private final AppointmentEvent event;

        DeleteTask(AppointmentEvent event) {
            updateTitle(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_DELETINGRECORD));
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
            this.event = event;
        }

        @Override
        protected void cancelled() {
            event.setCanceled();
            super.cancelled();
        }

        @Override
        protected void failed() {
            event.setFaulted("Operation failed", "Operation encountered an unexpected error");
            super.failed();
        }

        @Override
        protected void succeeded() {
            super.succeeded();
            switch (event.getStatus()) {
                case CANCELED:
                case EVALUATING:
                case SUCCEEDED:
                    break;
                default:
                    AlertHelper.showWarningAlert(getScene().getWindow(), LOG, event.getSummaryTitle(), event.getDetailMessage());
                    break;
            }
        }

        @Override
        protected Void call() throws Exception {
            try (DbConnector connector = new DbConnector()) {
                updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTEDTODB));
                AppointmentDAO.FACTORY.delete(event, connector.getConnection());
            }
            return null;
        }
    }

}
