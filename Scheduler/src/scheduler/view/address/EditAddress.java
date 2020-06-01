package scheduler.view.address;

import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import scheduler.AppResourceKeys;
import scheduler.AppResources;
import scheduler.dao.AddressDAO;
import scheduler.dao.CityDAO;
import scheduler.dao.CountryDAO;
import scheduler.dao.CustomerDAO;
import scheduler.dao.ICityDAO;
import scheduler.dao.ICountryDAO;
import scheduler.model.ui.AddressModel;
import scheduler.model.ui.CityItem;
import scheduler.model.ui.CityModel;
import scheduler.model.ui.CountryItem;
import scheduler.model.ui.CustomerModel;
import scheduler.model.ui.FxRecordModel;
import scheduler.observables.BindingHelper;
import scheduler.util.DbConnector;
import scheduler.util.Triplet;
import scheduler.util.Tuple;
import scheduler.view.EditItem;
import static scheduler.view.address.EditAddressResourceKeys.*;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.annotations.ModelEditor;
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

    private static final Object TARGET_CITY_KEY = new Object();

    public static AddressModel editNew(CityModel city, Window parentWindow, boolean keepOpen) throws IOException {
        AddressModel.Factory factory = AddressModel.getFactory();
        EditAddress control = new EditAddress();
        if (null != city) {
            control.getProperties().put(TARGET_CITY_KEY, city);
        }
        return EditItem.showAndWait(parentWindow, EditAddress.class, factory.createNew(factory.getDaoFactory().createNew()), keepOpen);
    }

    public static AddressModel edit(AddressModel model, Window parentWindow) throws IOException {
        return EditItem.showAndWait(parentWindow, EditAddress.class, model, false);
    }

    private final ReadOnlyBooleanWrapper valid;
    private final ReadOnlyBooleanWrapper modified;
    private final ReadOnlyStringWrapper windowTitle;
    private final ObservableList<CountryItem<? extends ICountryDAO>> countryOptions;
    private final ObservableList<CityItem<? extends ICityDAO>> cityOptions;
    private final ObservableList<CustomerModel> itemList;

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
    private ListView<CountryItem<? extends ICountryDAO>> countryListView; // Value injected by FXMLLoader

    @FXML // fx:id="cityListView"
    private ListView<CityItem<? extends ICityDAO>> cityListView; // Value injected by FXMLLoader

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
    private StringBinding normalizedAddress1;
    private StringBinding normalizedAddress2;
    private ObjectBinding<Object> selectedCity;
    private StringBinding normalizedPostalCode;
    private StringBinding normalizedPhone;

    public EditAddress() {
        windowTitle = new ReadOnlyStringWrapper("");
        valid = new ReadOnlyBooleanWrapper(false);
        modified = new ReadOnlyBooleanWrapper(false);
        countryOptions = FXCollections.observableArrayList();
        cityOptions = FXCollections.observableArrayList();
        itemList = FXCollections.observableArrayList();
    }

    @FXML
    void onEditCityButtonAction(ActionEvent event) {
        // FIXME: Implement scheduler.view.address.EditAddress#ActionEvent
    }

    @FXML
    void onNewCityButtonAction(ActionEvent event) {
        // FIXME: Implement scheduler.view.address.EditAddress#onNewCityButtonAction
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
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

        normalizedAddress1 = BindingHelper.asNonNullAndWsNormalized(address1TextField.textProperty());
        normalizedAddress2 = BindingHelper.asNonNullAndWsNormalized(address2TextField.textProperty());
        ObjectBinding<Object> selectedCountry = Bindings.select(countryListView.selectionModelProperty(), "selectedItem");
        selectedCity = Bindings.select(cityListView.selectionModelProperty(), "selectedItem");
        normalizedPostalCode = BindingHelper.asNonNullAndWsNormalized(postalCodeTextField.textProperty());
        normalizedPhone = BindingHelper.asNonNullAndWsNormalized(phoneTextField.textProperty());
        BooleanBinding addressValid = normalizedAddress1.isNotEmpty().or(normalizedAddress2.isNotEmpty());
        addressValidationLabel.visibleProperty().bind(addressValid.not());
        
        WaitTitledPane pane = new WaitTitledPane();
        pane.addOnFailAcknowledged((evt) -> getScene().getWindow().hide())
                .addOnCancelAcknowledged((evt) -> getScene().getWindow().hide());
        if (model.isNewRow()) {
            windowTitle.set(resources.getString(RESOURCEKEY_ADDNEWADDRESS));
            waitBorderPane.startNow(pane, new ItemsLoadTask());
        } else {
            windowTitle.set(resources.getString(RESOURCEKEY_EDITADDRESS));
            waitBorderPane.startNow(pane, new CustomersLoadTask());
        }
        // FIXME: Create individual validators
    }

    @Override
    public FxRecordModel.ModelFactory<AddressDAO, AddressModel> modelFactory() {
        return AddressModel.getFactory();
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
        throw new UnsupportedOperationException("Not supported yet.");
        // FIXME: Implement scheduler.view.address.EditAddress#applyEditMode
    }

    @Override
    public void updateModel() {
        throw new UnsupportedOperationException("Not supported yet.");
        // FIXME: Implement scheduler.view.address.EditAddress#updateModel
    }

    private void initializeCountriesAndCities(List<CountryDAO> countryDaoList, List<CityDAO> cityDaoList, CityItem<? extends ICityDAO> targetCity) {
        // FIXME: Implement scheduler.view.address.EditAddress#initializeCountriesAndCities
    }

    private class CustomersLoadTask extends Task<Triplet<List<CustomerDAO>, List<CountryDAO>, List<CityDAO>>> {

        private final AddressDAO dao;

        private CustomersLoadTask() {
            updateTitle(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_LOADINGCUSTOMERS));
            dao = model.dataObject();
        }

        @Override
        protected void succeeded() {
            Triplet<List<CustomerDAO>, List<CountryDAO>, List<CityDAO>> result = getValue();
            initializeCountriesAndCities(result.getValue2(), result.getValue3(), model.getCity());
            List<CustomerDAO> customerDaoList = result.getValue1();
            if (null != customerDaoList && !customerDaoList.isEmpty()) {
                CustomerModel.Factory factory = CustomerModel.getFactory();
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

    private class ItemsLoadTask extends Task<Tuple<List<CountryDAO>, List<CityDAO>>> {

        private final AddressDAO dao;

        private ItemsLoadTask() {
            updateTitle(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_LOADINGCUSTOMERS));
            dao = model.dataObject();
        }

        @Override
        protected void succeeded() {
            CityItem<? extends ICityDAO> targetCity = model.getCity();
            if (null == targetCity) {
                ObservableMap<Object, Object> properties = getProperties();
                if (properties.containsKey(TARGET_CITY_KEY)) {
                    targetCity = (CityModel) properties.get(TARGET_CITY_KEY);
                    properties.remove(TARGET_CITY_KEY);
                }
            }
            Tuple<List<CountryDAO>, List<CityDAO>> result = getValue();
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
