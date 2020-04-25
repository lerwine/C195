package scheduler.view.address;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import static scheduler.AppResourceKeys.RESOURCEKEY_CONNECTEDTODB;
import static scheduler.AppResourceKeys.RESOURCEKEY_CONNECTINGTODB;
import static scheduler.AppResourceKeys.RESOURCEKEY_DBREADERROR;
import static scheduler.AppResourceKeys.RESOURCEKEY_LOADINGADDRESSES;
import scheduler.AppResources;
import scheduler.dao.AddressDAO;
import static scheduler.util.NodeUtil.bindCollapsible;
import static scheduler.util.NodeUtil.collapseNode;
import static scheduler.util.NodeUtil.restoreNode;
import scheduler.view.ErrorDetailDialog;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.city.RelatedCityModel;
import scheduler.view.country.CityCountryModelImpl;
import scheduler.view.task.TaskWaiter;
import scheduler.model.db.CityRowData;
import scheduler.model.db.CountryRowData;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/view/address/AddressPicker")
@FXMLResource("/scheduler/view/address/AddressPicker.fxml")
public class AddressPicker {

    private static final Logger LOG = Logger.getLogger(AddressPicker.class.getName());

    private ObservableList<RelatedCityModel> allCities;
    private ObservableList<RelatedCityModel> cityOptions;
    private ObservableList<CityCountryModelImpl> allCountries;
    private ObservableList<AddressModelImpl> allAddresses;
    private ObservableList<AddressModelImpl> addressOptions;
    private SingleSelectionModel<CityCountryModelImpl> countrySelectionModel;
    private SingleSelectionModel<RelatedCityModel> citySelectionModel;
    private TableView.TableViewSelectionModel<AddressModelImpl> addressSelectionModel;
    private BiConsumer<Stage, AddressModelImpl> onClosed;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

//    @FXML // URL location of the FXML file that was given to the FXMLLoader
//    private URL location;
    @FXML // fx:id="rootBorderPane"
    private BorderPane rootBorderPane; // Value injected by FXMLLoader

    @FXML // fx:id="countryComboBox"
    private ComboBox<CityCountryModelImpl> countryComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="countryWarningLabel"
    private Label countryWarningLabel; // Value injected by FXMLLoader

    @FXML // fx:id="cityComboBox"
    private ComboBox<RelatedCityModel> cityComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="cityWarningLabel"
    private Label cityWarningLabel; // Value injected by FXMLLoader

    @FXML // fx:id="addressesTableView"
    private TableView<AddressModelImpl> addressesTableView; // Value injected by FXMLLoader

    @FXML // fx:id="selectButton"
    private Button selectButton; // Value injected by FXMLLoader

    @FXML
    void onCountryComboBoxAction(ActionEvent event) {
        addressSelectionModel.clearSelection();
        citySelectionModel.clearSelection();
        cityOptions.clear();
        addressOptions.clear();
        CityCountryModelImpl selectedItem = countrySelectionModel.getSelectedItem();
        // CURRENT: Find better way to get matching item
//        if (null != selectedItem) {
//            int pk = selectedItem.getPrimaryKey();
//            allCities.stream().filter((t) -> t.getCountry().getPrimaryKey() == pk).forEach((t) -> cityOptions.add(t));
//        }
    }

    @FXML
    void onCityComboBoxAction(ActionEvent event) {
        addressSelectionModel.clearSelection();
        addressOptions.clear();
        RelatedCityModel selectedItem = citySelectionModel.getSelectedItem();
        if (null != selectedItem) {
            int pk = selectedItem.getPrimaryKey();
            allAddresses.stream().filter((t) -> t.getCity().getPrimaryKey() == pk).forEach((t) -> addressOptions.add(t));
        }
    }

    @FXML
    synchronized void onSelectButtonAction(ActionEvent event) {
        close().accept((Stage) ((Button) event.getSource()).getScene().getWindow(), addressSelectionModel.getSelectedItem());
    }

    @FXML
    synchronized void onCancelButtonAction(ActionEvent event) {
        close().accept((Stage) ((Button) event.getSource()).getScene().getWindow(), null);
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert rootBorderPane != null : "fx:id=\"rootBorderPane\" was not injected: check your FXML file 'AddressPicker.fxml'.";
        assert countryComboBox != null : "fx:id=\"countryComboBox\" was not injected: check your FXML file 'AddressPicker.fxml'.";
        assert countryWarningLabel != null : "fx:id=\"countryWarningLabel\" was not injected: check your FXML file 'AddressPicker.fxml'.";
        assert cityComboBox != null : "fx:id=\"cityComboBox\" was not injected: check your FXML file 'AddressPicker.fxml'.";
        assert cityWarningLabel != null : "fx:id=\"cityWarningLabel\" was not injected: check your FXML file 'AddressPicker.fxml'.";
        assert addressesTableView != null : "fx:id=\"addressesTableView\" was not injected: check your FXML file 'AddressPicker.fxml'.";
        assert selectButton != null : "fx:id=\"selectButton\" was not injected: check your FXML file 'AddressPicker.fxml'.";
        allCountries = FXCollections.observableArrayList();
        allCities = FXCollections.observableArrayList();
        cityOptions = FXCollections.observableArrayList();
        addressOptions = FXCollections.observableArrayList();

        rootBorderPane.setVisible(false);
        collapseNode(rootBorderPane);

        countrySelectionModel = countryComboBox.getSelectionModel();
        countryComboBox.setItems(allCountries);

        citySelectionModel = cityComboBox.getSelectionModel();
        cityComboBox.setItems(cityOptions);
        cityComboBox.disableProperty().bind(countrySelectionModel.selectedItemProperty().isNull());

        bindCollapsible(countryWarningLabel, () -> null != countrySelectionModel.getSelectedItem(), countrySelectionModel.selectedItemProperty());

        bindCollapsible(cityWarningLabel, () -> null != citySelectionModel.getSelectedItem(), citySelectionModel.selectedItemProperty());

        addressSelectionModel = addressesTableView.getSelectionModel();
        addressesTableView.disableProperty().bind(citySelectionModel.selectedItemProperty().isNull());

        selectButton.disableProperty().bind(addressSelectionModel.selectedItemProperty().isNull());
    }

    private synchronized BiConsumer<Stage, AddressModelImpl> close() {
        BiConsumer<Stage, AddressModelImpl> c = onClosed;
        onClosed = null;
        rootBorderPane.setVisible(false);
        collapseNode(rootBorderPane);
        return c;
    }

    public synchronized void PickAddress(Stage stage, BiConsumer<Stage, AddressModelImpl> onClosed) {
        if (null != this.onClosed) {
            BiConsumer<Stage, AddressModelImpl> c = this.onClosed;
            this.onClosed = (t, u) -> {
                c.accept(t, u);
                onClosed.accept(t, u);
            };
        } else {
            this.onClosed = onClosed;
            if (null == allAddresses) {
                allAddresses = FXCollections.observableArrayList();
                TaskWaiter.startNow(new InitialLoadTask(stage));
            } else {
                addressSelectionModel.clearSelection();
                citySelectionModel.clearSelection();
                countrySelectionModel.clearSelection();
            }
            restoreNode(rootBorderPane);
            rootBorderPane.setVisible(true);
        }
    }

    private class InitialLoadTask extends TaskWaiter<List<AddressDAO>> {

        private InitialLoadTask(Stage owner) {
            super(owner, AppResources.getResourceString(RESOURCEKEY_CONNECTINGTODB),
                    AppResources.getResourceString(RESOURCEKEY_LOADINGADDRESSES));
        }

        @Override
        protected List<AddressDAO> getResult(Connection connection) throws SQLException {
            updateMessage(AppResources.getResourceString(RESOURCEKEY_CONNECTEDTODB));
            AddressDAO.FactoryImpl factory = AddressDAO.getFactory();
            return factory.load(connection, factory.getAllItemsFilter());
        }

        @Override
        protected void processResult(List<AddressDAO> result, Stage stage) {
            if (null != result && !result.isEmpty()) {
                result.forEach((t) -> {
                    CityRowData city = t.getCity();
                    int cityPk = city.getPrimaryKey();
                    if (!allCities.stream().anyMatch((u) -> u.getPrimaryKey() == cityPk)) {
                        allCities.add(new RelatedCityModel(city));
                        CountryRowData country = city.getCountry();
                        int countryPk = country.getPrimaryKey();
                        if (!allCountries.stream().anyMatch((u) -> u.getPrimaryKey() == countryPk)) {
                            allCountries.add(new CityCountryModelImpl(country));
                        }
                    }
                    allAddresses.add(new AddressModelImpl(t));
                });
            }
        }

        @Override
        protected void processException(Throwable ex, Stage stage) {
            ErrorDetailDialog.logShowAndWait(LOG, AppResources.getResourceString(RESOURCEKEY_DBREADERROR), stage, ex);
            stage.close();
        }

    }

}
