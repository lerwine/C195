package scheduler.fx;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import scheduler.AppResourceKeys;
import scheduler.AppResources;
import scheduler.dao.AddressDAO;
import scheduler.dao.CityDAO;
import scheduler.dao.CountryDAO;
import scheduler.dao.DataRowState;
import scheduler.dao.PartialAddressDAO;
import scheduler.dao.PartialCityDAO;
import scheduler.dao.PartialCountryDAO;
import static scheduler.fx.AddressPickerResourceKeys.*;
import scheduler.model.ModelHelper.AddressHelper;
import scheduler.model.ModelHelper.CityHelper;
import scheduler.model.ModelHelper.CountryHelper;
import scheduler.model.fx.PartialAddressModel;
import scheduler.model.fx.PartialCityModel;
import scheduler.model.fx.PartialCountryModel;
import scheduler.util.DbConnector;
import static scheduler.util.NodeUtil.clearAndSelectEntity;
import static scheduler.util.NodeUtil.collapseNode;
import static scheduler.util.NodeUtil.restoreNode;
import scheduler.util.Triplet;
import scheduler.util.ViewControllerLoader;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.task.WaitBorderPane;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/fx/AddressPicker")
@FXMLResource("/scheduler/fx/AddressPicker.fxml")
public class AddressPicker extends BorderPane {

    private static final Logger LOG = Logger.getLogger(AddressPicker.class.getName());

    private final ObjectProperty<PartialAddressModel<? extends PartialAddressDAO>> selectedAddress;
    private final ObservableList<PartialCityModel<? extends PartialCityDAO>> allCities;
    private final ObservableList<PartialCityModel<? extends PartialCityDAO>> cityOptions;
    private final ObservableList<PartialCountryModel<? extends PartialCountryDAO>> allCountries;
    private final ObservableList<PartialAddressModel<? extends PartialAddressDAO>> allAddresses;
    private final ObservableList<PartialAddressModel<? extends PartialAddressDAO>> addressOptions;
    private ObjectBinding<PartialAddressModel<? extends PartialAddressDAO>> candidateAddress;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // fx:id="selectButton"
    private Button selectButton; // Value injected by FXMLLoader

    @FXML // fx:id="countryListView"
    private ListView<PartialCountryModel<? extends PartialCountryDAO>> countryListView; // Value injected by FXMLLoader

    @FXML // fx:id="cityListView"
    private ListView<PartialCityModel<? extends PartialCityDAO>> cityListView; // Value injected by FXMLLoader

    @FXML // fx:id="addressesTableView"
    private TableView<PartialAddressModel<? extends PartialAddressDAO>> addressesTableView; // Value injected by FXMLLoader

    @FXML // fx:id="addressesPlaceHolderLabel"
    private Label addressesPlaceHolderLabel; // Value injected by FXMLLoader

    @SuppressWarnings("LeakingThisInConstructor")
    public AddressPicker() {
        selectedAddress = new SimpleObjectProperty<>(null);
        allCountries = FXCollections.observableArrayList();
        allCities = FXCollections.observableArrayList();
        cityOptions = FXCollections.observableArrayList();
        allAddresses = FXCollections.observableArrayList();
        addressOptions = FXCollections.observableArrayList();
        try {
            ViewControllerLoader.initializeCustomControl(this);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error loading view", ex);
            throw new InternalError("Error loading view", ex);
        }
    }

    @FXML
    private void onCancelButtonAction(ActionEvent event) {
        collapseNode(this);
    }

    @FXML
    private void onSelectButtonAction(ActionEvent event) {
        selectedAddress.set(candidateAddress.get());
        collapseNode(this);

    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    private void initialize() {
        assert selectButton != null : "fx:id=\"selectButton\" was not injected: check your FXML file 'AddressPicker.fxml'.";
        assert countryListView != null : "fx:id=\"countryListView\" was not injected: check your FXML file 'AddressPicker.fxml'.";
        assert cityListView != null : "fx:id=\"cityListView\" was not injected: check your FXML file 'AddressPicker.fxml'.";
        assert addressesTableView != null : "fx:id=\"addressesTableView\" was not injected: check your FXML file 'AddressPicker.fxml'.";
        assert addressesPlaceHolderLabel != null : "fx:id=\"addressesPlaceHolderLabel\" was not injected: check your FXML file 'AddressPicker.fxml'.";

        collapseNode(this);

        ObjectBinding<PartialCountryModel<? extends PartialCountryDAO>> selectedCountry = Bindings.select(countryListView.selectionModelProperty(), "selectedItem");
        ObjectBinding<PartialCityModel<? extends PartialCityDAO>> selectedCity = Bindings.select(cityListView.selectionModelProperty(), "selectedItem");
        candidateAddress = Bindings.select(addressesTableView.selectionModelProperty(), "selectedItem");
        StringBinding placeHolderTextBinding = Bindings.when(selectedCountry.isNull())
                .then(resources.getString(RESOURCEKEY_COUNTRYNOTSELECTED))
                .otherwise(Bindings.when((selectedCity.isNull()))
                        .then(resources.getString(RESOURCEKEY_CITYNOTSELECTED))
                        .otherwise(resources.getString(RESOURCEKEY_NOADDRESSESTOSHOW)));
        addressesPlaceHolderLabel.textProperty().bind(placeHolderTextBinding);
        selectedCountry.addListener(this::onSelectedCountryChanged);
        selectedCity.addListener(this::onSelectedCityChanged);
        selectButton.disableProperty().bind(candidateAddress.isNull());
        countryListView.setItems(allCountries);
        cityListView.setItems(cityOptions);
    }

    public PartialAddressModel<? extends PartialAddressDAO> getSelectedAddress() {
        return selectedAddress.get();
    }

    public void setSelectedAddress(PartialAddressModel<? extends PartialAddressDAO> value) {
        selectedAddress.set(value);
    }

    public ObjectProperty<PartialAddressModel<? extends PartialAddressDAO>> selectedAddressProperty() {
        return selectedAddress;
    }

    private void onSelectedCountryChanged(ObservableValue<? extends PartialCountryModel<? extends PartialCountryDAO>> observable,
            PartialCountryModel<? extends PartialCountryDAO> oldValue, PartialCountryModel<? extends PartialCountryDAO> newValue) {
        addressesTableView.getSelectionModel().clearSelection();
        cityListView.getSelectionModel().clearSelection();
        cityOptions.clear();
        addressOptions.clear();
        if (null != newValue) {
            int pk = newValue.getPrimaryKey();
            allCities.stream().filter((t) -> {
                PartialCountryModel<? extends PartialCountryDAO> c = t.getCountry();
                return null != c && c.getPrimaryKey() == pk;
            }).forEach((t) -> cityOptions.add(t));
        }
    }

    private void onSelectedCityChanged(ObservableValue<? extends PartialCityModel<? extends PartialCityDAO>> observable,
            PartialCityModel<? extends PartialCityDAO> oldValue, PartialCityModel<? extends PartialCityDAO> newValue) {
        addressesTableView.getSelectionModel().clearSelection();
        addressOptions.clear();
        if (null != newValue) {
            int pk = newValue.getPrimaryKey();
            allAddresses.stream().filter((t) -> {
                PartialCityModel<? extends PartialCityDAO> c = t.getCity();
                return null != c && c.getPrimaryKey() == pk;
            }).forEach((t) -> addressOptions.add(t));
        }
    }

    public synchronized void PickAddress(WaitBorderPane waitBorderPane) {
        restoreNode(this);
        addressesTableView.getSelectionModel().clearSelection();
        cityListView.getSelectionModel().clearSelection();
        countryListView.getSelectionModel().clearSelection();

        allCountries.clear();
        allCities.clear();
        cityOptions.clear();
        allAddresses.clear();
        addressOptions.clear();

        waitBorderPane.startNow(new InitialLoadTask());
    }

    private void loadOptions(List<AddressDAO> addresses, List<CityDAO> cities, List<CountryDAO> countries) {
        PartialAddressModel<? extends PartialAddressDAO> addr = selectedAddress.get();
        PartialCityModel<? extends PartialCityDAO> selectedCity;
        if (null == addr) {
            selectedCity = null;
            addresses.forEach((t) -> allAddresses.add(AddressHelper.createModel(t)));
        } else {
            selectedCity = addr.getCity();
            if (addr.getRowState() == DataRowState.NEW) {
                addresses.forEach((t) -> allAddresses.add(AddressHelper.createModel(t)));
            } else {
                final int pk = addr.getPrimaryKey();
                addresses.forEach((t) -> allAddresses.add((pk == t.getPrimaryKey()) ? addr : AddressHelper.createModel(t)));
            }
        }

        PartialCountryModel<? extends PartialCountryDAO> selectedCountry;
        if (null == selectedCity) {
            selectedCountry = null;
            cities.forEach((t) -> allCities.add(CityHelper.createModel(t)));
        } else {
            selectedCountry = selectedCity.getCountry();
            if (selectedCity.getRowState() == DataRowState.NEW) {
                cities.forEach((t) -> allCities.add(CityHelper.createModel(t)));
            } else {
                final int pk = selectedCity.getPrimaryKey();
                cities.forEach((t) -> allCities.add((pk == t.getPrimaryKey()) ? selectedCity : CityHelper.createModel(t)));
            }
        }

        if (null == selectedCountry || selectedCountry.getRowState() == DataRowState.NEW) {
            countries.forEach((t) -> allCountries.add(CountryHelper.createModel(t)));
        } else {
            final int pk = selectedCountry.getPrimaryKey();
            countries.forEach((t) -> allCountries.add((t.getPrimaryKey() == pk) ? selectedCountry : CountryHelper.createModel(t)));
            clearAndSelectEntity(countryListView, selectedCountry);
            clearAndSelectEntity(cityListView, selectedCity);
            clearAndSelectEntity(addressesTableView, addr);
        }
    }

    private class InitialLoadTask extends Task<Triplet<List<AddressDAO>, List<CityDAO>, List<CountryDAO>>> {

        private InitialLoadTask() {
            updateTitle(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_LOADINGADDRESSES));
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
        }

        @Override
        protected Triplet<List<AddressDAO>, List<CityDAO>, List<CountryDAO>> call() throws Exception {
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
            try (DbConnector dbConnector = new DbConnector()) {
                updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTEDTODB));
                AddressDAO.FactoryImpl af = AddressDAO.FACTORY;
                CityDAO.FactoryImpl cf = CityDAO.FACTORY;
                CountryDAO.FactoryImpl nf = CountryDAO.FACTORY;
                Connection connection = dbConnector.getConnection();
                return Triplet.of(
                        af.load(connection, af.getAllItemsFilter()),
                        cf.load(connection, cf.getAllItemsFilter()),
                        nf.load(connection, nf.getAllItemsFilter())
                );
            }
        }

        @Override
        protected void succeeded() {
            Triplet<List<AddressDAO>, List<CityDAO>, List<CountryDAO>> result = getValue();
            loadOptions(result.getValue1(), result.getValue2(), result.getValue3());
        }

    }

}
