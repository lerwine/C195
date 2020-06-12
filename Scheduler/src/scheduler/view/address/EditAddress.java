package scheduler.view.address;

import java.io.IOException;
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
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.WeakEventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import scheduler.AppResourceKeys;
import scheduler.AppResources;
import scheduler.dao.AddressDAO;
import scheduler.dao.CityDAO;
import scheduler.dao.CountryDAO;
import scheduler.dao.CustomerDAO;
import scheduler.dao.DataRowState;
import scheduler.dao.ICityDAO;
import scheduler.dao.ICountryDAO;
import scheduler.model.City;
import scheduler.model.Country;
import scheduler.model.CountryProperties;
import scheduler.model.ModelHelper;
import scheduler.model.ui.AddressModel;
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
import scheduler.util.Triplet;
import scheduler.util.Tuple;
import scheduler.view.EditItem;
import static scheduler.view.address.EditAddressResourceKeys.*;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.annotations.ModelEditor;
import scheduler.view.city.EditCity;
import scheduler.view.customer.EditCustomer;
import scheduler.view.event.ActivityType;
import scheduler.view.event.CustomerEvent;
import scheduler.view.event.ModelItemEvent;
import scheduler.view.task.WaitBorderPane;
import scheduler.view.task.WaitTitledPane;

/**
 * FXML Controller class for editing an {@link AddressModel}.
 * <p>
 * The associated view is {@code /resources/scheduler/view/address/EditAddress.fxml}.</p>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/view/address/EditAddress")
@FXMLResource("/scheduler/view/address/EditAddress.fxml")
public final class EditAddress extends VBox implements EditItem.ModelEditor<AddressDAO, AddressModel> {

    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(EditAddress.class.getName()), Level.FINER);

    private static final Object TARGET_CITY_KEY = new Object();

    public static AddressModel editNew(CityModel city, Window parentWindow, boolean keepOpen) throws IOException {
        AddressModel.Factory factory = AddressModel.FACTORY;
        EditAddress control = new EditAddress();
        if (null != city) {
            control.getProperties().put(TARGET_CITY_KEY, city);
        }
        return EditItem.showAndWait(parentWindow, control, factory.createNew(factory.getDaoFactory().createNew()), keepOpen);
    }

    public static AddressModel edit(AddressModel model, Window parentWindow) throws IOException {
        return EditItem.showAndWait(parentWindow, EditAddress.class, model, false);
    }

    private final ReadOnlyBooleanWrapper valid;
    private final ReadOnlyBooleanWrapper modified;
    private final ReadOnlyStringWrapper windowTitle;
    private final ObservableList<CountryModel> countryOptions;
    private final ObservableList<CityModel> allCities;
    private final ObservableList<CityModel> cityOptions;
    private final ObservableList<CustomerModel> itemList;
    private final SimpleBooleanProperty editingCity;
    private StringBinding normalizedAddress1;
    private StringBinding normalizedAddress2;
    private ObjectBinding<CountryModel> selectedCountry;
    private ObjectBinding<CityModel> selectedCity;
    private StringBinding normalizedPostalCode;
    private StringBinding normalizedPhone;
    private BooleanBinding changedBinding;
    private BooleanBinding validityBinding;

    @ModelEditor
    private AddressModel model;

    @ModelEditor
    private WaitBorderPane waitBorderPane;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // fx:id="address1TextField"
    private TextField address1TextField; // Value injected by FXMLLoader

    @FXML // fx:id="addressValidationLabel"
    private Label addressValidationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="address2TextField"
    private TextField address2TextField; // Value injected by FXMLLoader

    @FXML // fx:id="countryCitySplitPane"
    private SplitPane countryCitySplitPane; // Value injected by FXMLLoader

    @FXML // fx:id="countryListView"
    private ListView<CountryModel> countryListView; // Value injected by FXMLLoader

    @FXML // fx:id="cityListView"
    private ListView<CityModel> cityListView; // Value injected by FXMLLoader

    @FXML // fx:id="countryCityValueLabel"
    private Label countryCityValueLabel; // Value injected by FXMLLoader

    @FXML // fx:id="editCityButton"
    private Button editCityButton; // Value injected by FXMLLoader

    @FXML // fx:id="cityValidationLabel"
    private Label cityValidationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="newCityButton"
    private Button newCityButton; // Value injected by FXMLLoader

    @FXML // fx:id="postalCodeTextField"
    private TextField postalCodeTextField; // Value injected by FXMLLoader

    @FXML // fx:id="phoneTextField"
    private TextField phoneTextField; // Value injected by FXMLLoader

    @FXML // fx:id="customersHeadingLabel"
    private Label customersHeadingLabel; // Value injected by FXMLLoader

    @FXML // fx:id="customersTableView"
    private TableView<CustomerModel> customersTableView; // Value injected by FXMLLoader

    @FXML // fx:id="newCustomerButtonBar"
    private ButtonBar newCustomerButtonBar; // Value injected by FXMLLoader
    private BooleanBinding showEditCityControls;

    public EditAddress() {
        windowTitle = new ReadOnlyStringWrapper(this, "windowTitle", "");
        valid = new ReadOnlyBooleanWrapper(this, "valid", false);
        modified = new ReadOnlyBooleanWrapper(this, "modified", true);
        editingCity = new SimpleBooleanProperty(this, "editingCity", false);
        countryOptions = FXCollections.observableArrayList();
        allCities = FXCollections.observableArrayList();
        cityOptions = FXCollections.observableArrayList();
        itemList = FXCollections.observableArrayList();
    }

    @FXML
    private void onCustomerDeleteMenuItemAction(ActionEvent event) {
        CustomerModel item = customersTableView.getSelectionModel().getSelectedItem();
        if (null != item) {
            onItemActionRequest(new CustomerEvent(item, event.getSource(), this, ActivityType.DELETE_REQUEST));
        }
    }

    @FXML
    private void onCustomerEditMenuItemAction(ActionEvent event) {
        CustomerModel item = customersTableView.getSelectionModel().getSelectedItem();
        if (null != item) {
            onItemActionRequest(new CustomerEvent(item, event.getSource(), this, ActivityType.EDIT_REQUEST));
        }
    }

    @FXML
    @SuppressWarnings("incomplete-switch")
    private void onCustomersTableViewKeyReleased(KeyEvent event) {
        if (!(event.isAltDown() || event.isControlDown() || event.isMetaDown() || event.isShiftDown() || event.isShortcutDown())) {
            CustomerModel item;
            switch (event.getCode()) {
                case DELETE:
                    item = customersTableView.getSelectionModel().getSelectedItem();
                    if (null != item) {
                        onItemActionRequest(new CustomerEvent(item, event.getSource(), this, ActivityType.DELETE_REQUEST));
                    }
                    break;
                case ENTER:
                    item = customersTableView.getSelectionModel().getSelectedItem();
                    if (null != item) {
                        onItemActionRequest(new CustomerEvent(item, event.getSource(), this, ActivityType.EDIT_REQUEST));
                    }
                    break;
            }
        }
    }

    @FXML
    private void onEditCityButtonAction(ActionEvent event) {
        editingCity.set(true);
    }

    @FXML
    private void onItemActionRequest(CustomerEvent event) {
        CustomerModel item;
        if (event.isConsumed() || null == (item = event.getState().getModel())) {
            return;
        }
        switch (event.getActivity()) {
            case EDIT_REQUEST:
                try {
                    EditCustomer.edit(item, getScene().getWindow());
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
            CountryModel sn = countryOptions.stream().filter((t) -> t.getPrimaryKey() == pk).findFirst().orElse(null);
            if (null != sn) {
                addCountryOption(sn, c);
            } else {
                waitBorderPane.startNow(new GetCountryModelTask(n.getPrimaryKey(), c));
            }
        }
    }

    @FXML
    private void onNewCustomerButtonAction(ActionEvent event) {
        try {
            EditCustomer.editNew(model, getScene().getWindow(), true);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error loading customer edit window", ex);
        }
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    private void initialize() {
        assert address1TextField != null : "fx:id=\"address1TextField\" was not injected: check your FXML file 'EditAddress.fxml'.";
        assert addressValidationLabel != null : "fx:id=\"addressValidationLabel\" was not injected: check your FXML file 'EditAddress.fxml'.";
        assert address2TextField != null : "fx:id=\"address2TextField\" was not injected: check your FXML file 'EditAddress.fxml'.";
        assert countryCitySplitPane != null : "fx:id=\"countryCitySplitPane\" was not injected: check your FXML file 'EditAddress.fxml'.";
        assert countryListView != null : "fx:id=\"countryListView\" was not injected: check your FXML file 'EditAddress.fxml'.";
        assert cityListView != null : "fx:id=\"cityListView\" was not injected: check your FXML file 'EditAddress.fxml'.";
        assert countryCityValueLabel != null : "fx:id=\"countryCityValueLabel\" was not injected: check your FXML file 'EditAddress.fxml'.";
        assert editCityButton != null : "fx:id=\"editCityButton\" was not injected: check your FXML file 'EditAddress.fxml'.";
        assert cityValidationLabel != null : "fx:id=\"cityValidationLabel\" was not injected: check your FXML file 'EditAddress.fxml'.";
        assert newCityButton != null : "fx:id=\"newCityButton\" was not injected: check your FXML file 'EditAddress.fxml'.";
        assert postalCodeTextField != null : "fx:id=\"postalCodeTextField\" was not injected: check your FXML file 'EditAddress.fxml'.";
        assert phoneTextField != null : "fx:id=\"phoneTextField\" was not injected: check your FXML file 'EditAddress.fxml'.";
        assert customersHeadingLabel != null : "fx:id=\"customersHeadingLabel\" was not injected: check your FXML file 'EditAddress.fxml'.";
        assert customersTableView != null : "fx:id=\"customersTableView\" was not injected: check your FXML file 'EditAddress.fxml'.";
        assert newCustomerButtonBar != null : "fx:id=\"newCustomerButtonBar\" was not injected: check your FXML file 'EditAddress.fxml'.";

        countryListView.setItems(countryOptions);
        cityListView.setItems(cityOptions);
        customersTableView.setItems(itemList);

        normalizedAddress1 = BindingHelper.asNonNullAndWsNormalized(address1TextField.textProperty());
        address1TextField.textProperty().addListener((observable, oldValue, newValue) -> updateValidation());

        normalizedAddress2 = BindingHelper.asNonNullAndWsNormalized(address2TextField.textProperty());
        address2TextField.textProperty().addListener((observable, oldValue, newValue) -> updateValidation());
        BooleanBinding addressValid = normalizedAddress1.isNotEmpty().or(normalizedAddress2.isNotEmpty());
        addressValidationLabel.visibleProperty().bind(addressValid.not());

        selectedCountry = Bindings.select(countryListView.selectionModelProperty(), "selectedItem");
        selectedCountry.addListener(this::onSelectedCountryChanged);
        selectedCity = Bindings.select(cityListView.selectionModelProperty(), "selectedItem");
        selectedCity.addListener(this::onSelectedCityChanged);
        StringBinding cityValidationMessage = Bindings.createStringBinding(() -> {
            CityItem<? extends ICityDAO> c = selectedCity.get();
            CountryItem<? extends ICountryDAO> n = selectedCountry.get();
            String result;
            if (null == n) {
                result = "Country must be selected, first";
            } else {
                result = (null == c) ? "* Required" : "";
            }
            LOG.info(() -> String.format("cityValidationMessage changing to %s", LogHelper.toLogText(result)));
            return result;
        }, selectedCity, selectedCountry);
        BooleanBinding cityInvalid = cityValidationMessage.isNotEmpty();
        cityValidationLabel.textProperty().bind(cityValidationMessage);
        cityValidationLabel.visibleProperty().bind(cityInvalid);
        showEditCityControls = cityInvalid.or(editingCity);
        showEditCityControls.addListener(this::onShowEditCityControlsChanged);
        countryCityValueLabel.textProperty().bind(Bindings.when(selectedCity.isNull())
                .then("")
                .otherwise(Bindings.format("%s, %s", Bindings.selectString(selectedCity, City.PROP_NAME),
                        Bindings.selectString(selectedCountry, Country.PROP_NAME)))
        );
        normalizedPostalCode = BindingHelper.asNonNullAndWsNormalized(postalCodeTextField.textProperty());
        postalCodeTextField.textProperty().addListener((observable, oldValue, newValue) -> modified.set(changedBinding.get()));
        normalizedPhone = BindingHelper.asNonNullAndWsNormalized(phoneTextField.textProperty());
        phoneTextField.textProperty().addListener((observable, oldValue, newValue) -> modified.set(changedBinding.get()));

        changedBinding = model.rowStateProperty().isEqualTo(DataRowState.NEW)
                .or(normalizedAddress1.isNotEqualTo(BindingHelper.asNonNullAndWsNormalized(model.address1Property())))
                .or(normalizedAddress2.isNotEqualTo(BindingHelper.asNonNullAndWsNormalized(model.address2Property())))
                .or(Bindings.createBooleanBinding(() -> !ModelHelper.areSameRecord(selectedCity.get(), model.getCity()), selectedCity, model.cityProperty()))
                .or(normalizedPostalCode.isNotEqualTo(BindingHelper.asNonNullAndWsNormalized(model.postalCodeProperty())))
                .or(normalizedPhone.isNotEqualTo(BindingHelper.asNonNullAndWsNormalized(model.phoneProperty())));
        validityBinding = addressValid.and(cityInvalid.not());

        address1TextField.setText(model.getAddress1());
        address2TextField.setText(model.getAddress2());
        postalCodeTextField.setText(model.getPostalCode());
        phoneTextField.setText(model.getPhone());

        CustomerModel.FACTORY.addEventHandler(CustomerEvent.INSERTED_EVENT_TYPE, new WeakEventHandler<>(this::onCustomerAdded));
        CustomerModel.FACTORY.addEventHandler(CustomerEvent.UPDATED_EVENT_TYPE, new WeakEventHandler<>(this::onCustomerUpdated));
        CustomerModel.FACTORY.addEventHandler(CustomerEvent.DELETED_EVENT_TYPE, new WeakEventHandler<>(this::onCustomerDeleted));

        WaitTitledPane pane = new WaitTitledPane();
        pane.addOnFailAcknowledged((evt) -> getScene().getWindow().hide())
                .addOnCancelAcknowledged((evt) -> getScene().getWindow().hide());
        if (model.isNewRow()) {
            collapseNode(customersHeadingLabel);
            collapseNode(customersTableView);
            collapseNode(newCustomerButtonBar);
            LOG.info(() -> "Setting editingCity to true");
            editingCity.set(true);
            windowTitle.set(resources.getString(RESOURCEKEY_ADDNEWADDRESS));
            waitBorderPane.startNow(pane, new NewDataLoadTask());
        } else {
            initializeEditMode();
            waitBorderPane.startNow(pane, new EditDataLoadTask());
        }
    }

    private void initializeEditMode() {
        windowTitle.set(resources.getString(RESOURCEKEY_EDITADDRESS));
    }

    private void onCustomerAdded(CustomerEvent event) {
        LOG.info(() -> String.format("%s event handled", event.getEventType().getName()));
        if (model.getRowState() != DataRowState.NEW) {
            CustomerDAO dao = event.getDataAccessObject();
            // XXX: See if we need to get/set model
            if (dao.getAddress().getPrimaryKey() == model.getPrimaryKey()) {
                itemList.add(new CustomerModel(dao));
            }
        }
    }

    private void onCustomerUpdated(CustomerEvent event) {
        LOG.info(() -> String.format("%s event handled", event.getEventType().getName()));
        if (model.getRowState() != DataRowState.NEW) {
            CustomerDAO dao = event.getDataAccessObject();
            // XXX: See if we need to get/set model
            int pk = dao.getPrimaryKey();
            CustomerModel m = itemList.stream().filter((t) -> t.getPrimaryKey() == pk).findFirst().orElse(null);
            if (null != m) {
                CustomerModel.FACTORY.updateItem(m, dao);
                if (dao.getAddress().getPrimaryKey() != model.getPrimaryKey()) {
                    itemList.remove(m);
                }
            } else if (dao.getAddress().getPrimaryKey() == model.getPrimaryKey()) {
                itemList.add(new CustomerModel(dao));
            }
        }
    }

    private void onCustomerDeleted(CustomerEvent event) {
        LOG.info(() -> String.format("%s event handled", event.getEventType().getName()));
        if (model.getRowState() != DataRowState.NEW) {
            CustomerDAO dao = event.getDataAccessObject();
            // XXX: See if we need to get/set model
            int pk = dao.getPrimaryKey();
            itemList.stream().filter((t) -> t.getPrimaryKey() == pk).findFirst().ifPresent((t) -> itemList.remove(t));
        }
    }

    private void onShowEditCityControlsChanged(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        LOG.fine(() -> String.format("showEditCityControls changed from %s to %s", oldValue, newValue));
        if (newValue) {
            collapseNode(countryCityValueLabel);
            collapseNode(editCityButton);
            restoreNode(countryCitySplitPane);
            restoreNode(newCityButton);
        } else {
            collapseNode(countryCitySplitPane);
            collapseNode(newCityButton);
            restoreNode(countryCityValueLabel);
            restoreNode(editCityButton);
        }
    }

    private void addCountryOption(CountryModel country, CityModel city) {
        countryOptions.add(country);
        countryOptions.sort(CountryProperties::compare);
        countryListView.getSelectionModel().select(country);
        cityListView.getSelectionModel().select(city);
    }

    @Override
    public FxRecordModel.ModelFactory<AddressDAO, AddressModel> modelFactory() {
        return AddressModel.FACTORY;
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
    public void onNewModelSaved() {
        editingCity.set(false);
        restoreNode(customersHeadingLabel);
        restoreNode(customersTableView);
        restoreNode(newCustomerButtonBar);
        initializeEditMode();
    }

    @Override
    public void updateModel() {
        model.setAddress1(normalizedAddress1.get());
        model.setAddress2(normalizedAddress2.get());
        model.setCity(selectedCity.get());
        model.setPostalCode(normalizedPostalCode.get());
        model.setPhone(normalizedPhone.get());
    }

    private void updateValidation() {
        modified.set(changedBinding.get());
        valid.set(validityBinding.get());
    }

    private void onSelectedCountryChanged(ObservableValue<? extends CountryModel> observable, CountryModel oldValue, CountryModel newValue) {
        LOG.fine(() -> String.format("selectedCountry changed from %s to %s", oldValue, newValue));
        cityListView.getSelectionModel().clearSelection();
        cityOptions.clear();
        if (null != newValue) {
            int pk = newValue.getPrimaryKey();
            allCities.filtered((t) -> t.getCountry().getPrimaryKey() == pk).forEach((t) -> cityOptions.add(t));
        }
        updateValidation();
    }

    private void onSelectedCityChanged(ObservableValue<? extends CityModel> observable, CityModel oldValue, CityModel newValue) {
        LOG.fine(() -> String.format("selectedCity changed from %s to %s", oldValue, newValue));
        updateValidation();
    }

    private void initializeCountriesAndCities(List<CountryDAO> countryDaoList, List<CityDAO> cityDaoList, CityItem<? extends ICityDAO> targetCity) {
        CountryModel.Factory nf = CountryModel.FACTORY;
        CountryItem<? extends ICountryDAO> countryItem = (null == targetCity) ? null : targetCity.getCountry();
        if (null != countryDaoList && !countryDaoList.isEmpty()) {
            if (null != countryItem && countryItem instanceof CountryModel) {
                int pk = countryItem.getPrimaryKey();
                countryDaoList.forEach((t) -> {
                    if (t.getPrimaryKey() == pk) {
                        countryOptions.add((CountryModel) countryItem);
                    } else {
                        countryOptions.add(nf.createNew(t));
                    }
                });
            } else {
                countryDaoList.forEach((t) -> countryOptions.add(nf.createNew(t)));
            }
        }
        CityModel.Factory cf = CityModel.FACTORY;
        if (null != cityDaoList && !cityDaoList.isEmpty()) {
            if (null != targetCity && targetCity instanceof CityModel) {
                int pk = targetCity.getPrimaryKey();
                cityDaoList.forEach((t) -> {
                    if (t.getPrimaryKey() == pk) {
                        allCities.add((CityModel) targetCity);
                    } else {
                        allCities.add(cf.createNew(t));
                    }
                });
            } else {
                cityDaoList.forEach((t) -> allCities.add(cf.createNew(t)));
            }
        }
        if (null != countryItem) {
            if (countryItem.getRowState() != DataRowState.NEW) {
                int npk = countryItem.getPrimaryKey();
                CountryModel nm = countryOptions.stream().filter((t) -> t.getPrimaryKey() == npk).findFirst().orElse(null);
                if (null != nm) {
                    LOG.fine(() -> String.format("Selectimg country %s", nm));
                    countryListView.getSelectionModel().select(nm);
                    if (null != targetCity && targetCity.getRowState() != DataRowState.NEW) {
                        int cpk = targetCity.getPrimaryKey();
                        CityModel cm = cityOptions.stream().filter((t) -> t.getPrimaryKey() == cpk).findFirst().orElse(null);
                        if (null != cm) {
                            LOG.fine(() -> String.format("Selectimg city %s", cm));
                            cityListView.getSelectionModel().select(cm);
                        }
                    }
                }
            }
        }
        onShowEditCityControlsChanged(showEditCityControls, false, showEditCityControls.get());
    }

    private class GetCountryModelTask extends Task<CountryDAO> {

        private final int countryPk;
        private final CityModel city;

        public GetCountryModelTask(int countryPk, CityModel city) {
            this.countryPk = countryPk;
            this.city = city;
        }

        @Override
        protected void succeeded() {
            CountryDAO value = getValue();
            addCountryOption((null == value) ? null : new CountryModel(value), city);
            super.succeeded();
        }

        @Override
        protected CountryDAO call() throws Exception {
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
            try (DbConnector dbConnector = new DbConnector()) {
                updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTEDTODB));
                return CountryDAO.FACTORY.loadByPrimaryKey(dbConnector.getConnection(), countryPk).orElse(null);
            }
        }
    }

    private class EditDataLoadTask extends Task<Triplet<List<CustomerDAO>, List<CountryDAO>, List<CityDAO>>> {

        private final AddressDAO dao;
        private final Optional<Integer> countryPk;
        private final Optional<Integer> cityPk;

        private EditDataLoadTask() {
            updateTitle(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_LOADINGCUSTOMERS));
            dao = model.dataObject();
            CityItem<? extends ICityDAO> cm = model.getCity();
            CountryItem<? extends ICountryDAO> nm;
            if (null == cm) {
                nm = null;
            } else {
                nm = cm.getCountry();
            }
            if (null != nm && nm.getRowState() != DataRowState.NEW) {
                if (nm instanceof CountryModel) {
                    countryPk = Optional.empty();
                } else {
                    countryPk = Optional.of(nm.getPrimaryKey());
                }
                if (null != cm && cm.getRowState() != DataRowState.NEW && !(cm instanceof CityModel)) {
                    cityPk = Optional.of(cm.getPrimaryKey());
                    return;
                }
            } else {
                countryPk = Optional.of(nm.getPrimaryKey());
            }
            cityPk = Optional.empty();
        }

        @Override
        protected void succeeded() {
            Triplet<List<CustomerDAO>, List<CountryDAO>, List<CityDAO>> result = getValue();
            initializeCountriesAndCities(result.getValue2(), result.getValue3(), model.getCity());
            List<CustomerDAO> customerDaoList = result.getValue1();
            if (null != customerDaoList && !customerDaoList.isEmpty()) {
                CustomerModel.Factory factory = CustomerModel.FACTORY;
                customerDaoList.forEach((t) -> {
                    itemList.add(factory.createNew(t));
                });
            }
            super.succeeded();
        }

        @Override
        protected Triplet<List<CustomerDAO>, List<CountryDAO>, List<CityDAO>> call() throws Exception {
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
            try (DbConnector dbConnector = new DbConnector()) {
                updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTEDTODB));
                CountryDAO.FactoryImpl nf = CountryDAO.FACTORY;
                CityDAO.FactoryImpl tf = CityDAO.FACTORY;
                CustomerDAO.FactoryImpl cf = CustomerDAO.FACTORY;
                return Triplet.of(cf.load(dbConnector.getConnection(), cf.getByAddressFilter(dao)),
                        nf.getAllCountries(dbConnector.getConnection()),
                        tf.load(dbConnector.getConnection(), tf.getAllItemsFilter()));
            }
        }

    }

    private class NewDataLoadTask extends Task<Tuple<List<CountryDAO>, List<CityDAO>>> {

        private NewDataLoadTask() {
            updateTitle(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_LOADINGCUSTOMERS));
        }

        @Override
        protected void succeeded() {
            Tuple<List<CountryDAO>, List<CityDAO>> result = getValue();
            ObservableMap<Object, Object> properties = EditAddress.this.getProperties();
            CityItem<? extends ICityDAO> targetCity;
            if (properties.containsKey(TARGET_CITY_KEY)) {
                targetCity = (CityModel) properties.get(TARGET_CITY_KEY);
                properties.remove(TARGET_CITY_KEY);
            } else {
                targetCity = model.getCity();
            }

            initializeCountriesAndCities(result.getValue1(), result.getValue2(), targetCity);
            super.succeeded();
        }

        @Override
        protected Tuple<List<CountryDAO>, List<CityDAO>> call() throws Exception {
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
            try (DbConnector dbConnector = new DbConnector()) {
                updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTEDTODB));
                CountryDAO.FactoryImpl nf = CountryDAO.FACTORY;
                CityDAO.FactoryImpl cf = CityDAO.FACTORY;
                return Tuple.of(nf.getAllCountries(dbConnector.getConnection()),
                        cf.load(dbConnector.getConnection(), cf.getAllItemsFilter()));
            }
        }

    }

    private class DeleteTask extends Task<Void> {

        private final CustomerEvent event;

        DeleteTask(CustomerEvent event) {
            updateTitle(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_DELETINGRECORD));
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
            this.event = event;
        }

        @Override
        protected void succeeded() {
            super.succeeded();
            ModelItemEvent.State state = event.getState();
            if (!state.isSucceeded()) {
                AlertHelper.showWarningAlert(getScene().getWindow(), LOG, state.getSummaryTitle(), state.getDetailMessage());
            }
        }

        @Override
        protected void failed() {
            event.setUnsuccessful("Operation failed", "Delete operation encountered an unexpected error");
            super.failed();
        }

        @Override
        protected void cancelled() {
            event.setUnsuccessful("Operation canceled", "Delete operation was canceled");
            super.cancelled();
        }

        @Override
        protected Void call() throws Exception {
            try (DbConnector connector = new DbConnector()) {
                updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTEDTODB));
                CustomerDAO.FACTORY.delete(event, connector.getConnection());
            }
            return null;
        }
    }

}
