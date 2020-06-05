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
import scheduler.dao.IAddressDAO;
import scheduler.dao.ICityDAO;
import scheduler.dao.ICountryDAO;
import static scheduler.fx.AddressPickerResourceKeys.*;
import scheduler.model.ui.AddressItem;
import scheduler.model.ui.CityItem;
import scheduler.model.ui.CountryItem;
import scheduler.util.DbConnector;
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

    private final ObjectProperty<AddressItem<? extends IAddressDAO>> selectedAddress;
    private final ObservableList<CityItem<? extends ICityDAO>> allCities;
    private final ObservableList<CityItem<? extends ICityDAO>> cityOptions;
    private final ObservableList<CountryItem<? extends ICountryDAO>> allCountries;
    private final ObservableList<AddressItem<? extends IAddressDAO>> allAddresses;
    private final ObservableList<AddressItem<? extends IAddressDAO>> addressOptions;
    private ObjectBinding<AddressItem<? extends IAddressDAO>> candidateAddress;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // fx:id="selectButton"
    private Button selectButton; // Value injected by FXMLLoader

    @FXML // fx:id="countryListView"
    private ListView<CountryItem<? extends ICountryDAO>> countryListView; // Value injected by FXMLLoader

    @FXML // fx:id="cityListView"
    private ListView<CityItem<? extends ICityDAO>> cityListView; // Value injected by FXMLLoader

    @FXML // fx:id="addressesTableView"
    private TableView<AddressItem<? extends IAddressDAO>> addressesTableView; // Value injected by FXMLLoader

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
    void onCancelButtonAction(ActionEvent event) {
        collapseNode(this);
    }

    @FXML
    void onSelectButtonAction(ActionEvent event) {
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

        ObjectBinding<CountryItem<? extends ICountryDAO>> selectedCountry = Bindings.select(countryListView.selectionModelProperty(), "selectedItem");
        ObjectBinding<CityItem<? extends ICityDAO>> selectedCity = Bindings.select(cityListView.selectionModelProperty(), "selectedItem");
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

    public AddressItem<? extends IAddressDAO> getSelectedAddress() {
        return selectedAddress.get();
    }

    public void setSelectedAddress(AddressItem<? extends IAddressDAO> value) {
        selectedAddress.set(value);
    }

    public ObjectProperty<AddressItem<? extends IAddressDAO>> selectedAddressProperty() {
        return selectedAddress;
    }

    private void onSelectedCountryChanged(ObservableValue<? extends CountryItem<? extends ICountryDAO>> observable,
            CountryItem<? extends ICountryDAO> oldValue, CountryItem<? extends ICountryDAO> newValue) {
        addressesTableView.getSelectionModel().clearSelection();
        cityListView.getSelectionModel().clearSelection();
        cityOptions.clear();
        addressOptions.clear();
        if (null != newValue) {
            int pk = newValue.getPrimaryKey();
            allCities.stream().filter((t) -> {
                CountryItem<? extends ICountryDAO> c = t.getCountry();
                return null != c && c.getPrimaryKey() == pk;
            }).forEach((t) -> cityOptions.add(t));
        }
    }

    private void onSelectedCityChanged(ObservableValue<? extends CityItem<? extends ICityDAO>> observable,
            CityItem<? extends ICityDAO> oldValue, CityItem<? extends ICityDAO> newValue) {
        addressesTableView.getSelectionModel().clearSelection();
        addressOptions.clear();
        if (null != newValue) {
            int pk = newValue.getPrimaryKey();
            allAddresses.stream().filter((t) -> {
                CityItem<? extends ICityDAO> c = t.getCity();
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
        AddressItem<? extends IAddressDAO> addr = selectedAddress.get();
        CityItem<? extends ICityDAO> selectedCity;
        if (null == addr) {
            selectedCity = null;
            addresses.forEach((t) -> allAddresses.add(AddressItem.createModel(t)));
        } else {
            selectedCity = addr.getCity();
            if (addr.getRowState() == DataRowState.NEW) {
                addresses.forEach((t) -> allAddresses.add(AddressItem.createModel(t)));
            } else {
                final int pk = addr.getPrimaryKey();
                addresses.forEach((t) -> allAddresses.add((pk == t.getPrimaryKey()) ? addr : AddressItem.createModel(t)));
            }
        }

        CountryItem<? extends ICountryDAO> selectedCountry;
        if (null == selectedCity) {
            selectedCountry = null;
            cities.forEach((t) -> allCities.add(CityItem.createModel(t)));
        } else {
            selectedCountry = selectedCity.getCountry();
            if (selectedCity.getRowState() == DataRowState.NEW) {
                cities.forEach((t) -> allCities.add(CityItem.createModel(t)));
            } else {
                final int pk = selectedCity.getPrimaryKey();
                cities.forEach((t) -> allCities.add((pk == t.getPrimaryKey()) ? selectedCity : CityItem.createModel(t)));
            }
        }

        if (null == selectedCountry || selectedCountry.getRowState() == DataRowState.NEW) {
            countries.forEach((t) -> allCountries.add(CountryItem.createModel(t)));
        } else {
            final int pk = selectedCountry.getPrimaryKey();
            countries.forEach((t) -> allCountries.add((t.getPrimaryKey() == pk) ? selectedCountry : CountryItem.createModel(t)));
            countryListView.getSelectionModel().select(selectedCountry);
            if (null != selectedCity && selectedCity.getRowState() != DataRowState.NEW) {
                cityListView.getSelectionModel().select(selectedCity);
                if (null != addr && addr.getRowState() != DataRowState.NEW) {
                    addressesTableView.getSelectionModel().select(addr);
                }
            }
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
