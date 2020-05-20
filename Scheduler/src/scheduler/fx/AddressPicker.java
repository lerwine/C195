package scheduler.fx;

import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import scheduler.AppResourceKeys;
import scheduler.AppResources;
import scheduler.dao.AddressDAO;
import scheduler.dao.ICityDAO;
import scheduler.dao.ICountryDAO;
import static scheduler.fx.AddressPickerResourceKeys.*;
import scheduler.model.ui.AddressModel;
import scheduler.model.ui.RelatedCity;
import scheduler.model.ui.RelatedCountry;
import scheduler.util.DbConnector;
import static scheduler.util.NodeUtil.collapseNode;
import static scheduler.util.NodeUtil.restoreLabeled;
import static scheduler.util.NodeUtil.restoreNode;
import scheduler.util.ViewControllerLoader;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.task.WaitBorderPane;
import scheduler.model.ui.CountryItem;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/fx/AddressPicker")
@FXMLResource("/scheduler/fx/AddressPicker.fxml")
public class AddressPicker extends BorderPane {

    private static final Logger LOG = Logger.getLogger(AddressPicker.class.getName());

    private final ObservableList<RelatedCity> allCities;
    private final ObservableList<RelatedCity> cityOptions;
    private final ObservableList<CountryItem<? extends ICountryDAO>> allCountries;
    private final ObservableList<AddressModel> addressOptions;
    private ObservableList<AddressModel> allAddresses;
    private Consumer<AddressModel> onClosed;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // fx:id="countryComboBox"
    private ComboBox<CountryItem<? extends ICountryDAO>> countryComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="countryWarningLabel"
    private Label countryWarningLabel; // Value injected by FXMLLoader

    @FXML // fx:id="cityComboBox"
    private ComboBox<RelatedCity> cityComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="cityWarningLabel"
    private Label cityWarningLabel; // Value injected by FXMLLoader

    @FXML // fx:id="addressesTableView"
    private TableView<AddressModel> addressesTableView; // Value injected by FXMLLoader

    @FXML // fx:id="selectButton"
    private Button selectButton; // Value injected by FXMLLoader

    @SuppressWarnings("LeakingThisInConstructor")
    public AddressPicker() {
        allCountries = FXCollections.observableArrayList();
        allCities = FXCollections.observableArrayList();
        cityOptions = FXCollections.observableArrayList();
        addressOptions = FXCollections.observableArrayList();
        try {
            ViewControllerLoader.initializeCustomControl(this);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error loading view", ex);
            throw new InternalError("Error loading view", ex);
        }
    }

    @FXML
    private void onCountryComboBoxAction(ActionEvent event) {
        addressesTableView.getSelectionModel().clearSelection();
        cityComboBox.getSelectionModel().clearSelection();
        cityOptions.clear();
        addressOptions.clear();
        CountryItem<? extends ICountryDAO> selectedItem = countryComboBox.getValue();
        if (null == selectedItem) {
            restoreLabeled(cityWarningLabel, resources.getString(RESOURCEKEY_COUNTRYNOTSELECTED));
            restoreNode(countryWarningLabel);
        } else {
            collapseNode(countryWarningLabel);
            restoreLabeled(cityWarningLabel, resources.getString(RESOURCEKEY_CITYNOTSELECTED));
            int pk = selectedItem.getPrimaryKey();
            allCities.stream().filter((t) -> t.getCountry().getPrimaryKey() == pk).forEach((t) -> cityOptions.add(t));
        }
    }

    @FXML
    private void onCityComboBoxAction(ActionEvent event) {
        addressesTableView.getSelectionModel().clearSelection();
        addressOptions.clear();
        RelatedCity selectedItem = cityComboBox.getValue();
        if (null != selectedItem) {
            int pk = selectedItem.getPrimaryKey();
            allAddresses.stream().filter((t) -> t.getCity().getPrimaryKey() == pk).forEach((t) -> addressOptions.add(t));
        }
    }

    @FXML
    private synchronized void onSelectButtonAction(ActionEvent event) {
        close().accept(addressesTableView.getSelectionModel().getSelectedItem());
    }

    @FXML
    private synchronized void onCancelButtonAction(ActionEvent event) {
        close().accept(null);
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    private void initialize() {
        assert countryComboBox != null : "fx:id=\"countryComboBox\" was not injected: check your FXML file 'AddressPicker.fxml'.";
        assert countryWarningLabel != null : "fx:id=\"countryWarningLabel\" was not injected: check your FXML file 'AddressPicker.fxml'.";
        assert cityComboBox != null : "fx:id=\"cityComboBox\" was not injected: check your FXML file 'AddressPicker.fxml'.";
        assert cityWarningLabel != null : "fx:id=\"cityWarningLabel\" was not injected: check your FXML file 'AddressPicker.fxml'.";
        assert addressesTableView != null : "fx:id=\"addressesTableView\" was not injected: check your FXML file 'AddressPicker.fxml'.";
        assert selectButton != null : "fx:id=\"selectButton\" was not injected: check your FXML file 'AddressPicker.fxml'.";

        collapseNode(this);

        countryComboBox.setItems(allCountries);
        cityComboBox.setItems(cityOptions);
        addressesTableView.getSelectionModel().selectedItemProperty().addListener(this::onAddressChanged);
    }

    @SuppressWarnings("unchecked")
    private void onAddressChanged(Observable observable) {
        selectButton.setDisable(null == ((ReadOnlyObjectProperty<AddressModel>) observable).get());
    }

    private synchronized Consumer<AddressModel> close() {
        Consumer<AddressModel> c = onClosed;
        onClosed = null;
        collapseNode(this);
        return c;
    }

    public synchronized void PickAddress(WaitBorderPane waitBorderPane, Consumer<AddressModel> onClosed) {
        if (null != this.onClosed) {
            Consumer<AddressModel> c = this.onClosed;
            this.onClosed = (t) -> {
                c.accept(t);
                onClosed.accept(t);
            };
        } else {
            this.onClosed = onClosed;
            if (null == allAddresses) {
                allAddresses = FXCollections.observableArrayList();
                waitBorderPane.startNow(new InitialLoadTask());
            } else {
                addressesTableView.getSelectionModel().clearSelection();
                cityComboBox.getSelectionModel().clearSelection();
                countryComboBox.getSelectionModel().clearSelection();
            }
            restoreNode(this);
        }
    }

    private class InitialLoadTask extends Task<List<AddressDAO>> {

        private InitialLoadTask() {
            updateTitle(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_LOADINGADDRESSES));
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
        }

        @Override
        protected List<AddressDAO> call() throws Exception {
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
            try (DbConnector dbConnector = new DbConnector()) {
                updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTEDTODB));
                AddressDAO.FactoryImpl factory = AddressDAO.getFactory();
                return factory.load(dbConnector.getConnection(), factory.getAllItemsFilter());
            }
        }

        @Override
        protected void succeeded() {
            List<AddressDAO> result = getValue();
            if (null != result && !result.isEmpty()) {
                result.forEach((t) -> {
                    ICityDAO city = t.getCity();
                    String rk = city.getPredefinedData().getResourceKey();
                    if (!allCities.stream().anyMatch((u) -> u.getPredefinedData().getResourceKey().equals(rk))) {
                        allCities.add(new RelatedCity(city));
                        ICountryDAO country = city.getCountry();
                        String rc = country.getPredefinedData().getRegionCode();
                        if (!allCountries.stream().anyMatch((u) -> country.getPredefinedData().getRegionCode().equals(rc))) {
                            allCountries.add(new RelatedCountry(country));
                        }
                    }
                    allAddresses.add(new AddressModel(t));
                });
            }
        }

        @Override
        protected void failed() {
            ErrorDetailControl.logShowAndWait(LOG, AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_DBACCESSERROR), (Stage) getScene().getWindow(),
                    getException());
            getScene().getWindow().hide();
        }

    }

}
