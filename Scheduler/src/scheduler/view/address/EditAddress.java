package scheduler.view.address;

import java.io.IOException;
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
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
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
import scheduler.dao.PartialCityDAO;
import scheduler.dao.PartialCountryDAO;
import scheduler.events.AddressEvent;
import scheduler.events.CitySuccessEvent;
import scheduler.events.CustomerEvent;
import scheduler.events.CustomerFailedEvent;
import scheduler.events.CustomerOpRequestEvent;
import scheduler.events.CustomerSuccessEvent;
import scheduler.model.City;
import scheduler.model.CityProperties;
import scheduler.model.Country;
import scheduler.model.CountryProperties;
import scheduler.model.ModelHelper;
import scheduler.model.fx.AddressModel;
import scheduler.model.fx.CityModel;
import scheduler.model.fx.CountryModel;
import scheduler.model.fx.CustomerModel;
import scheduler.model.fx.EntityModel;
import scheduler.model.fx.PartialCityModel;
import scheduler.model.fx.PartialCountryModel;
import scheduler.observables.BindingHelper;
import scheduler.util.AlertHelper;
import scheduler.util.DbConnector;
import scheduler.util.LogHelper;
import static scheduler.util.NodeUtil.collapseNode;
import static scheduler.util.NodeUtil.restoreNode;
import scheduler.util.ThrowableConsumer;
import scheduler.util.Triplet;
import scheduler.util.Tuple;
import scheduler.util.WeakEventHandlingReference;
import scheduler.view.EditItem;
import static scheduler.view.address.EditAddressResourceKeys.*;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.annotations.ModelEditor;
import scheduler.view.city.EditCity;
import scheduler.view.customer.EditCustomer;
import scheduler.view.task.WaitBorderPane;
import scheduler.view.task.WaitTitledPane;

/**
 * FXML Controller class for editing an {@link AddressModel}.
 * <h3>Event Handling</h3>
 * <h4>SCHEDULER_CUSTOMER_OP_REQUEST</h4>
 * <dl>
 * <dt>{@link #customersTableView} &#123; {@link scheduler.fx.ItemEditTableCellFactory#onItemActionRequest} &#125; (creates) {@link CustomerOpRequestEvent} &#123;</dt>
 * <dd>{@link javafx.event.Event#eventType} = {@link CustomerOpRequestEvent#CUSTOMER_OP_REQUEST "SCHEDULER_CUSTOMER_OP_REQUEST"} &larr;
 * {@link scheduler.events.OperationRequestEvent#OP_REQUEST_EVENT "SCHEDULER_OP_REQUEST_EVENT"} &larr; {@link scheduler.events.ModelEvent#MODEL_EVENT_TYPE "SCHEDULER_MODEL_EVENT"}
 * </dd>
 * </dl>
 * &#125; (fires) {@link #onItemActionRequest(CustomerOpRequestEvent)}
 * <dl>
 * <dt>SCHEDULER_CUSTOMER_EDIT_REQUEST {@link CustomerOpRequestEvent} &#123; {@link javafx.event.Event#eventType} = {@link CustomerOpRequestEvent#EDIT_REQUEST} &#125;</dt>
 * <dd>&rarr; {@link EditCustomer#edit(CustomerModel, javafx.stage.Window) EditCustomer.edit}(({@link CustomerModel}) {@link scheduler.events.ModelEvent#getEntityModel()},
 * {@link javafx.stage.Window}) (creates) {@link scheduler.events.CustomerEvent#CUSTOMER_EVENT_TYPE "SCHEDULER_CUSTOMER_EVENT"} &rArr;
 * {@link scheduler.model.fx.CustomerModel.Factory}</dd>
 * <dt>SCHEDULER_CUSTOMER_EDIT_REQUEST {@link CustomerOpRequestEvent} &#123; {@link javafx.event.Event#eventType} = {@link CustomerOpRequestEvent#DELETE_REQUEST} &#125;</dt>
 * <dd>&rarr; {@link scheduler.dao.CustomerDAO.DeleteTask#DeleteTask(scheduler.model.fx.CustomerModel, boolean) new CustomerDAO.DeleteTask}({@link CustomerOpRequestEvent},
 * {@code false}) (creates) {@link scheduler.events.CustomerEvent#CUSTOMER_EVENT_TYPE "SCHEDULER_CUSTOMER_EVENT"} &rArr; {@link scheduler.model.fx.CustomerModel.Factory}</dd>
 * </dl>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/view/address/EditAddress")
@FXMLResource("/scheduler/view/address/EditAddress.fxml")
public final class EditAddress extends VBox implements EditItem.ModelEditorController<AddressDAO, AddressModel, AddressEvent> {

    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(EditAddress.class.getName()), Level.FINER);
//    private static final Logger LOG = Logger.getLogger(EditAddress.class.getName());

    private static final Object TARGET_CITY_KEY = new Object();

    public static void editNew(PartialCityModel<? extends PartialCityDAO> city, Window parentWindow, boolean keepOpen, Consumer<AddressModel> beforeShow) throws IOException {
        AddressModel model = AddressDAO.FACTORY.createNew().cachedModel(true);
        EditAddress control = new EditAddress();
        if (null != city) {
            model.setCity(city);
        }
        if (null != beforeShow) {
            beforeShow.accept(model);
        }
        EditItem.showAndWait(parentWindow, control, model, keepOpen);
    }

    public static void editNew(CityModel city, Window parentWindow, boolean keepOpen) throws IOException {
        editNew(city, parentWindow, keepOpen, null);
    }

    public static void edit(AddressModel model, Window parentWindow, ThrowableConsumer<Stage, IOException> beforeShow) throws IOException {
        EditItem.showAndWait(parentWindow, EditAddress.class, model, false, beforeShow);
    }

    public static void edit(AddressModel model, Window parentWindow) throws IOException {
        edit(model, parentWindow, null);
    }

    //<editor-fold defaultstate="collapsed" desc="Instance Fields">
    private final WeakEventHandlingReference<CustomerSuccessEvent> customerInsertEventHandler;
    private final WeakEventHandlingReference<CustomerSuccessEvent> customerUpdateEventHandler;
    private final WeakEventHandlingReference<CustomerSuccessEvent> customerDeleteEventHandler;
    private final WeakEventHandlingReference<CitySuccessEvent> cityInsertEventHandler;
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
    private BooleanBinding showEditCityControls;
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

    //</editor-fold>
    public EditAddress() {
        windowTitle = new ReadOnlyStringWrapper(this, "windowTitle", "");
        valid = new ReadOnlyBooleanWrapper(this, "valid", false);
        modified = new ReadOnlyBooleanWrapper(this, "modified", true);
        editingCity = new SimpleBooleanProperty(this, "editingCity", false);
        countryOptions = FXCollections.observableArrayList();
        allCities = FXCollections.observableArrayList();
        cityOptions = FXCollections.observableArrayList();
        itemList = FXCollections.observableArrayList();
        customerInsertEventHandler = WeakEventHandlingReference.create(this::onCustomerInserted);
        customerUpdateEventHandler = WeakEventHandlingReference.create(this::onCustomerUpdated);
        customerDeleteEventHandler = WeakEventHandlingReference.create(this::onCustomerDeleted);
        cityInsertEventHandler = WeakEventHandlingReference.create(this::onCityInserted);
    }

    @ModelEditor
    private void onModelInserted(AddressEvent event) {
        LOG.entering(LOG.getName(), "onModelInserted", event);
        editingCity.set(false);
        restoreNode(customersHeadingLabel);
        restoreNode(customersTableView);
        restoreNode(newCustomerButtonBar);
        initializeEditMode();
        CustomerModel.FACTORY.addEventHandler(CustomerSuccessEvent.INSERT_SUCCESS, customerInsertEventHandler.getWeakEventHandler());
        CustomerModel.FACTORY.addEventHandler(CustomerSuccessEvent.UPDATE_SUCCESS, customerUpdateEventHandler.getWeakEventHandler());
        CustomerModel.FACTORY.addEventHandler(CustomerSuccessEvent.DELETE_SUCCESS, customerDeleteEventHandler.getWeakEventHandler());
    }

    @Override
    public void applyChanges() {
        model.setAddress1(normalizedAddress1.get());
        model.setAddress2(normalizedAddress2.get());
        model.setCity(selectedCity.get());
        model.setPostalCode(normalizedPostalCode.get());
        model.setPhone(normalizedPhone.get());
    }

    @FXML
    private void onCustomerDeleteMenuItemAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onCustomerDeleteMenuItemAction", event);
        CustomerModel item = customersTableView.getSelectionModel().getSelectedItem();
        if (null != item) {
            onDelete(item);
        }
    }

    @FXML
    private void onCustomerEditMenuItemAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onCustomerEditMenuItemAction", event);
        CustomerModel item = customersTableView.getSelectionModel().getSelectedItem();
        if (null != item) {
            onEdit(item);
        }
    }

    @FXML
    @SuppressWarnings("incomplete-switch")
    private void onCustomersTableViewKeyReleased(KeyEvent event) {
        LOG.entering(LOG.getName(), "onCustomersTableViewKeyReleased", event);
        if (!(event.isAltDown() || event.isControlDown() || event.isMetaDown() || event.isShiftDown() || event.isShortcutDown())) {
            CustomerModel item;
            switch (event.getCode()) {
                case DELETE:
                    item = customersTableView.getSelectionModel().getSelectedItem();
                    if (null != item) {
                        onDelete(item);
                    }
                    break;
                case ENTER:
                    item = customersTableView.getSelectionModel().getSelectedItem();
                    if (null != item) {
                        onEdit(item);
                    }
                    break;
            }
        }
    }

    @FXML
    private void onEditCityButtonAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onEditCityButtonAction", event);
        editingCity.set(true);
    }

    private void onEdit(CustomerModel item) {
        try {
            EditCustomer.edit(item, getScene().getWindow());
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error opening child window", ex);
        }
    }

    private void onDelete(CustomerModel item) {
        CustomerOpRequestEvent deleteRequestEvent = new CustomerOpRequestEvent(item, this, true);
        Event.fireEvent(item.dataObject(), deleteRequestEvent);
        Stage stage = (Stage) getScene().getWindow();
        if (deleteRequestEvent.isCanceled()) {
            AlertHelper.showWarningAlert(stage, deleteRequestEvent.getCancelMessage(), ButtonType.OK);
        } else {
            AlertHelper.showWarningAlert(stage, LOG,
                    AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONFIRMDELETE),
                    AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO).ifPresent((t) -> {
                if (t == ButtonType.YES) {
                    CustomerDAO.DeleteTask task = new CustomerDAO.DeleteTask(item, false);
                    task.setOnSucceeded((e) -> {
                        CustomerEvent customerEvent = (CustomerEvent) task.getValue();
                        if (null != customerEvent && customerEvent instanceof CustomerFailedEvent) {
                            scheduler.util.AlertHelper.showWarningAlert(getScene().getWindow(), "Delete Failure",
                                    ((CustomerFailedEvent) customerEvent).getMessage(), ButtonType.OK);
                        }
                    });
                    waitBorderPane.startNow(task);
                }
            });
        }
    }

    @FXML
    private void onItemActionRequest(CustomerOpRequestEvent event) {
        LOG.entering(LOG.getName(), "onItemActionRequest", event);
        if (event.isEdit()) {
            onEdit(event.getEntityModel());
        } else {
            onDelete(event.getEntityModel());
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
    private void onNewCustomerButtonAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onNewCustomerButtonAction", event);
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
            PartialCityModel<? extends PartialCityDAO> c = selectedCity.get();
            PartialCountryModel<? extends PartialCountryDAO> n = selectedCountry.get();
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
    public EntityModel.EntityModelFactory<AddressDAO, AddressModel> modelFactory() {
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

    private void initializeCountriesAndCities(List<CountryDAO> countryDaoList, List<CityDAO> cityDaoList, PartialCityModel<? extends PartialCityDAO> targetCity) {
        PartialCountryModel<? extends PartialCountryDAO> countryItem = (null == targetCity) ? null : targetCity.getCountry();
        if (null != countryDaoList && !countryDaoList.isEmpty()) {
            if (null != countryItem && countryItem instanceof CountryModel) {
                int pk = countryItem.getPrimaryKey();
                countryDaoList.forEach((t) -> {
                    if (t.getPrimaryKey() == pk) {
                        countryOptions.add((CountryModel) countryItem);
                    } else {
                        countryOptions.add(t.cachedModel(true));
                    }
                });
            } else {
                countryDaoList.forEach((t) -> countryOptions.add(t.cachedModel(true)));
            }
        }
        if (null != cityDaoList && !cityDaoList.isEmpty()) {
            if (null != targetCity && targetCity instanceof CityModel) {
                int pk = targetCity.getPrimaryKey();
                cityDaoList.forEach((t) -> {
                    if (t.getPrimaryKey() == pk) {
                        allCities.add((CityModel) targetCity);
                    } else {
                        allCities.add(t.cachedModel(true));
                    }
                });
            } else {
                cityDaoList.forEach((t) -> allCities.add(t.cachedModel(true)));
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
        CityModel.FACTORY.addEventHandler(CitySuccessEvent.INSERT_SUCCESS, cityInsertEventHandler.getWeakEventHandler());
        onShowEditCityControlsChanged(showEditCityControls, false, showEditCityControls.get());
    }

    private void onCustomerInserted(CustomerSuccessEvent event) {
        LOG.entering(LOG.getName(), "onCustomerAdded", event);
        CustomerModel entityModel = event.getEntityModel();
        if (entityModel.getAddress().getPrimaryKey() == model.getPrimaryKey()) {
            itemList.add(entityModel);
        }
    }

    private void onCustomerUpdated(CustomerSuccessEvent event) {
        LOG.entering(LOG.getName(), "onCustomerUpdated", event);
        if (model.getRowState() != DataRowState.NEW) {
            CustomerModel entityModel = event.getEntityModel();
            int pk = entityModel.getPrimaryKey();
            CustomerModel m = itemList.stream().filter((t) -> t.getPrimaryKey() == pk).findFirst().orElse(null);
            if (null != m) {
                if (entityModel.getAddress().getPrimaryKey() != model.getPrimaryKey()) {
                    itemList.remove(m);
                }
            } else if (entityModel.getAddress().getPrimaryKey() == model.getPrimaryKey()) {
                itemList.add(entityModel);
            }
        }
    }

    private void onCustomerDeleted(CustomerSuccessEvent event) {
        LOG.entering(LOG.getName(), "onCustomerDeleted", event);
        CustomerModel.FACTORY.find(itemList, event.getEntityModel()).ifPresent(itemList::remove);
    }

    private void onCityInserted(CitySuccessEvent event) {
        CityModel cityModel = event.getEntityModel();
        CountryModel oldCountry = selectedCountry.get();
        CountryModel newCountry;
        CityModel newCity = event.getEntityModel();
        allCities.add(newCity);
        allCities.sort(CityProperties::compare);
        int pk = cityModel.getCountry().getPrimaryKey();
        if (null == oldCountry || oldCountry.getPrimaryKey() != pk) {
            newCountry = countryOptions.stream().filter((t) -> t.getPrimaryKey() == pk).findAny().orElseGet(() -> {
                PartialCountryModel<? extends PartialCountryDAO> rm = cityModel.getCountry();
                if (rm instanceof CountryModel) {
                    CountryModel nc = (CountryModel) rm;
                    countryOptions.add(nc);
                    countryOptions.sort(CountryProperties::compare);
                    return nc;
                }
                return null;
            });
            if (null == newCountry) {
                return;
            }
            countryListView.getSelectionModel().clearSelection();
            countryListView.getSelectionModel().select(newCountry);
        }
        cityListView.getSelectionModel().select(cityModel);
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
            addCountryOption(value.cachedModel(true), city);
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

        private EditDataLoadTask() {
            updateTitle(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_LOADINGCUSTOMERS));
            dao = model.dataObject();
        }

        @Override
        protected void succeeded() {
            Triplet<List<CustomerDAO>, List<CountryDAO>, List<CityDAO>> result = getValue();
            initializeCountriesAndCities(result.getValue2(), result.getValue3(), model.getCity());
            List<CustomerDAO> customerDaoList = result.getValue1();
            if (null != customerDaoList && !customerDaoList.isEmpty()) {
                customerDaoList.forEach((t) -> {
                    itemList.add(t.cachedModel(true));
                });
            }
            CustomerModel.FACTORY.addEventHandler(CustomerSuccessEvent.INSERT_SUCCESS, customerInsertEventHandler.getWeakEventHandler());
            CustomerModel.FACTORY.addEventHandler(CustomerSuccessEvent.UPDATE_SUCCESS, customerUpdateEventHandler.getWeakEventHandler());
            CustomerModel.FACTORY.addEventHandler(CustomerSuccessEvent.DELETE_SUCCESS, customerDeleteEventHandler.getWeakEventHandler());
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
            PartialCityModel<? extends PartialCityDAO> targetCity;
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

}
