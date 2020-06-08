package scheduler.fx;

import com.sun.javafx.scene.control.behavior.OptionalBoolean;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import scheduler.AppResourceKeys;
import scheduler.AppResources;
import scheduler.dao.CityDAO;
import scheduler.dao.CountryDAO;
import scheduler.dao.CustomerDAO;
import scheduler.model.ui.CityModel;
import scheduler.model.ui.CountryModel;
import scheduler.model.ui.CustomerModel;
import scheduler.util.DbConnector;
import static scheduler.util.NodeUtil.collapseNode;
import static scheduler.util.NodeUtil.restoreLabeled;
import scheduler.util.ResourceBundleHelper;
import scheduler.util.ViewControllerLoader;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.task.WaitBorderPane;
import scheduler.view.task.WaitTitledPane;

/**
 * FXML Controller class for picking a {@link CustomerModel}.
 * <p>
 * The associated view is {@code /resources/scheduler/fx/CustomerPicker.fxml}.</p>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/fx/CustomerPicker")
@FXMLResource("/scheduler/fx/CustomerPicker.fxml")
public class CustomerPicker extends BorderPane {

    private static final Logger LOG = Logger.getLogger(CustomerPicker.class.getName());

    private ObservableList<CountryModel> countries;
    private ObservableList<CityModel> cities;
    private ObservableList<StatusOption> statusOptions;
    private ObservableList<CustomerModel> allCustomers;
    private ObservableList<CustomerModel> filteredCustomers;
    private CustomerModel selectedCustomer = null;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

//    @FXML // URL location of the FXML file that was given to the FXMLLoader
//    private URL location;
    @FXML // fx:id="countryFilterCheckBox"
    private CheckBox countryFilterCheckBox; // Value injected by FXMLLoader

    @FXML // fx:id="countryComboBox"
    private ComboBox<CountryModel> countryComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="cityFilterCheckBox"
    private CheckBox cityFilterCheckBox; // Value injected by FXMLLoader

    @FXML // fx:id="cityComboBox"
    private ComboBox<CityModel> cityComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="nameSearchTextField"
    private TextField nameSearchTextField; // Value injected by FXMLLoader

    @FXML // fx:id="statusComboBox"
    private ComboBox<StatusOption> statusComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="customersListView"
    private ListView<CustomerModel> customersListView; // Value injected by FXMLLoader

    @FXML // fx:id="nameLabel"
    private Label nameLabel; // Value injected by FXMLLoader

    @FXML // fx:id="address1Label"
    private Label address1Label; // Value injected by FXMLLoader

    @FXML // fx:id="address2Label"
    private Label address2Label; // Value injected by FXMLLoader

    @FXML // fx:id="cityLabel"
    private Label cityLabel; // Value injected by FXMLLoader

    @FXML // fx:id="countryLabel"
    private Label countryLabel; // Value injected by FXMLLoader

    @FXML // fx:id="postalCodeLabel"
    private Label postalCodeLabel; // Value injected by FXMLLoader

    @FXML // fx:id="phoneLabel"
    private Label phoneLabel; // Value injected by FXMLLoader

    @FXML // fx:id="selectCustomerButton"
    private Button selectCustomerButton; // Value injected by FXMLLoader

    @FXML // fx:id="cancelButton"
    private Button cancelButton; // Value injected by FXMLLoader

    @FXML // fx:id="createdLabel"
    private Label createdLabel; // Value injected by FXMLLoader

    @FXML // fx:id="modifiedLabel"
    private Label modifiedLabel; // Value injected by FXMLLoader

    @FXML // fx:id="waitBorderPane"
    private WaitBorderPane waitBorderPane; // Value injected by FXMLLoader

    @SuppressWarnings("LeakingThisInConstructor")
    public CustomerPicker() {
        try {
            ViewControllerLoader.initializeCustomControl(this);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error loading view", ex);
            throw new InternalError("Error loading view", ex);
        }
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    private void initialize() {
        countries = FXCollections.observableArrayList();
        cities = FXCollections.observableArrayList();
        allCustomers = FXCollections.observableArrayList();
        filteredCustomers = FXCollections.observableArrayList();
        statusOptions = FXCollections.observableArrayList(new StatusOption("Any", OptionalBoolean.ANY),
                new StatusOption("Active", OptionalBoolean.ANY), new StatusOption("Inactive", OptionalBoolean.FALSE));

        assert countryFilterCheckBox != null : "fx:id=\"countryFilterCheckBox\" was not injected: check your FXML file 'CustomerPicker.fxml'.";
        assert countryComboBox != null : "fx:id=\"countryComboBox\" was not injected: check your FXML file 'CustomerPicker.fxml'.";
        assert cityFilterCheckBox != null : "fx:id=\"cityFilterCheckBox\" was not injected: check your FXML file 'CustomerPicker.fxml'.";
        assert cityComboBox != null : "fx:id=\"cityComboBox\" was not injected: check your FXML file 'CustomerPicker.fxml'.";
        assert nameSearchTextField != null : "fx:id=\"nameSearchTextField\" was not injected: check your FXML file 'CustomerPicker.fxml'.";
        Objects.requireNonNull(statusComboBox, "fx:id=\"statusComboBox\" was not injected: check your FXML file 'CustomerPicker.fxml'.")
                .setItems(statusOptions);
        statusComboBox.getSelectionModel().select(0);
        Objects.requireNonNull(customersListView, "fx:id=\"customersListView\" was not injected: check your FXML file 'CustomerPicker.fxml'.")
                .getSelectionModel().selectedItemProperty().addListener((observable) -> {
                    customerSelected(customersListView.getSelectionModel().getSelectedItem());
                });
        assert nameLabel != null : "fx:id=\"nameLabel\" was not injected: check your FXML file 'CustomerPicker.fxml'.";
        assert address1Label != null : "fx:id=\"address1Label\" was not injected: check your FXML file 'CustomerPicker.fxml'.";
        assert address2Label != null : "fx:id=\"address2Label\" was not injected: check your FXML file 'CustomerPicker.fxml'.";
        assert cityLabel != null : "fx:id=\"cityLabel\" was not injected: check your FXML file 'CustomerPicker.fxml'.";
        assert countryLabel != null : "fx:id=\"countryLabel\" was not injected: check your FXML file 'CustomerPicker.fxml'.";
        assert postalCodeLabel != null : "fx:id=\"postalCodeLabel\" was not injected: check your FXML file 'CustomerPicker.fxml'.";
        assert phoneLabel != null : "fx:id=\"phoneLabel\" was not injected: check your FXML file 'CustomerPicker.fxml'.";
        assert selectCustomerButton != null : "fx:id=\"selectCustomerButton\" was not injected: check your FXML file 'CustomerPicker.fxml'.";
        assert cancelButton != null : "fx:id=\"cancelButton\" was not injected: check your FXML file 'CustomerPicker.fxml'.";
        assert createdLabel != null : "fx:id=\"createdLabel\" was not injected: check your FXML file 'CustomerPicker.fxml'.";
        assert modifiedLabel != null : "fx:id=\"modifiedLabel\" was not injected: check your FXML file 'CustomerPicker.fxml'.";
        assert waitBorderPane != null : "fx:id=\"waitBorderPane\" was not injected: check your FXML file 'CustomerPicker.fxml'.";
    }

    private void customerSelected(CustomerModel customer) {
        selectedCustomer = customer;
        if (null == customer) {
            nameLabel.setText("");
            address1Label.setText("");
            collapseNode(address2Label);
            cityLabel.setText("");
            countryLabel.setText("");
            postalCodeLabel.setText("");
            phoneLabel.setText("");
            createdLabel.setText("");
            modifiedLabel.setText("");
            selectCustomerButton.setDisable(true);
            return;
        }

        nameLabel.setText(customer.getName());
        address1Label.setText(customer.getAddress1());
        String s = customer.getAddress2().trim();
        if (s.isEmpty()) {
            collapseNode(address2Label);
        } else {
            restoreLabeled(address2Label, s);
        }
        cityLabel.setText(customer.getCityName());
        countryLabel.setText(customer.getCountryName());
        postalCodeLabel.setText(customer.getPostalCode());
        phoneLabel.setText(customer.getPhone());
        createdLabel.setText(ResourceBundleHelper.formatCreatedByOn(customer.getCreatedBy(), customer.getCreateDate()));
        modifiedLabel.setText(ResourceBundleHelper.formatModifiedByOn(customer.getLastModifiedBy(), customer.getLastModifiedDate()));
        selectCustomerButton.setDisable(false);
        waitBorderPane.startNow(createCriticalWaitTitledPane(), new LoadCountriesTask());
    }

    @FXML
    private void cancelButtonClick(ActionEvent event) {
        selectedCustomer = null;
        ((Node) event.getSource()).getScene().getWindow().hide();
    }

    @FXML
    private void nameSearchTextFieldChange(ActionEvent event) {
        filterCustomers();
    }

    synchronized void filterCustomers() {
        String s = nameSearchTextField.getText().trim().toLowerCase();
        CustomerModel c = selectedCustomer;
        filteredCustomers.clear();
        if (s.isEmpty()) {
            allCustomers.forEach((item) -> {
                filteredCustomers.add(item);
            });
        } else {
            allCustomers.stream().filter((CustomerModel item) -> item.getName().toLowerCase().contains(s)).forEach((item) -> {
                filteredCustomers.add(item);
            });
        }
        if (null != c) {
            int pk = c.getPrimaryKey();
            Optional<CustomerModel> matching = filteredCustomers.stream().filter((CustomerModel item) -> item.getPrimaryKey() == pk).findFirst();
            if (matching.isPresent()) {
                customersListView.getSelectionModel().select(matching.get());
            } else {
                customersListView.getSelectionModel().clearSelection();
            }
        }
    }

    @FXML
    private void selectCustomerButtonClick(ActionEvent event) {
        ((Node) event.getSource()).getScene().getWindow().hide();
    }

    @FXML
    private void countryFilterCheckBoxChange(ActionEvent event) {
        if (countryFilterCheckBox.isSelected()) {
            cityFilterCheckBox.setDisable(false);
            countryComboBox.setDisable(false);
            CountryModel country = countryComboBox.getSelectionModel().getSelectedItem();
            if (null != country) {
                waitBorderPane.startNow(createCriticalWaitTitledPane(), new LoadCitiesTask(country.dataObject(),
                        (null == selectedCustomer) ? null : selectedCustomer.dataObject()));
            }
        } else {
            if (cityFilterCheckBox.isSelected()) {
                cityFilterCheckBox.setSelected(false);
            }
            cityFilterCheckBox.setDisable(true);
            countryComboBox.setDisable(true);
            cityComboBox.setDisable(true);
            waitBorderPane.startNow(createCriticalWaitTitledPane(), new LoadCustomersTask(null, null,
                    statusComboBox.getSelectionModel().getSelectedItem().status.get(),
                    (null == selectedCustomer) ? null : selectedCustomer.dataObject()));
        }
    }

    @FXML
    private void countryComboBoxChange(ActionEvent event) {
        if (countryFilterCheckBox.isSelected()) {
            CountryModel country = countryComboBox.getSelectionModel().getSelectedItem();
            if (null != country) {
                waitBorderPane.startNow(createCriticalWaitTitledPane(), new LoadCitiesTask(country.dataObject(),
                        (null == selectedCustomer) ? null : selectedCustomer.dataObject()));
            }
        }
    }

    @FXML
    private void cityFilterCheckBoxChange(ActionEvent event) {
        if (countryFilterCheckBox.isSelected()) {
            CountryModel country = countryComboBox.getSelectionModel().getSelectedItem();
            if (null != country) {
                if (cityFilterCheckBox.isSelected()) {
                    CityModel city = cityComboBox.getSelectionModel().getSelectedItem();
                    if (null != city) {
                        waitBorderPane.startNow(createCriticalWaitTitledPane(), new LoadCustomersTask(country.dataObject(),
                                city.dataObject(), statusComboBox.getSelectionModel().getSelectedItem().status.get(),
                                (null == selectedCustomer) ? null : selectedCustomer.dataObject()));
                    }
                } else {
                    waitBorderPane.startNow(createCriticalWaitTitledPane(), new LoadCustomersTask(country.dataObject(),
                            null, statusComboBox.getSelectionModel().getSelectedItem().status.get(),
                            (null == selectedCustomer) ? null : selectedCustomer.dataObject()));
                }
            }
            cityComboBox.setDisable(!cityFilterCheckBox.isSelected());
        }
    }

    @FXML
    private void cityComboBoxChange(ActionEvent event) {
        if (countryFilterCheckBox.isSelected() && cityFilterCheckBox.isSelected()) {
            CountryModel country = countryComboBox.getSelectionModel().getSelectedItem();
            if (null != country) {
                CityModel city = cityComboBox.getSelectionModel().getSelectedItem();
                if (null != city) {
                    waitBorderPane.startNow(createCriticalWaitTitledPane(), new LoadCustomersTask(country.dataObject(),
                            city.dataObject(), statusComboBox.getSelectionModel().getSelectedItem().status.get(),
                            (null == selectedCustomer) ? null : selectedCustomer.dataObject()));
                }
            }
        }
    }

    @FXML
    private void statusComboBoxChange(ActionEvent event) {
        if (countryFilterCheckBox.isSelected()) {
            CountryModel country = countryComboBox.getSelectionModel().getSelectedItem();
            if (null != country) {
                if (cityFilterCheckBox.isSelected()) {
                    CityModel city = cityComboBox.getSelectionModel().getSelectedItem();
                    if (null != city) {
                        waitBorderPane.startNow(createCriticalWaitTitledPane(), new LoadCustomersTask(country.dataObject(),
                                city.dataObject(), statusComboBox.getSelectionModel().getSelectedItem().status.get(),
                                (null == selectedCustomer) ? null : selectedCustomer.dataObject()));
                        return;
                    }
                }
                waitBorderPane.startNow(createCriticalWaitTitledPane(), new LoadCustomersTask(country.dataObject(),
                        null, statusComboBox.getSelectionModel().getSelectedItem().status.get(),
                        (null == selectedCustomer) ? null : selectedCustomer.dataObject()));
                return;
            }
        }
        waitBorderPane.startNow(createCriticalWaitTitledPane(), new LoadCustomersTask(null, null,
                statusComboBox.getSelectionModel().getSelectedItem().status.get(),
                (null == selectedCustomer) ? null : selectedCustomer.dataObject()));
    }

    private WaitTitledPane createCriticalWaitTitledPane() {
        WaitTitledPane pane = new WaitTitledPane();
        pane.addOnFailAcknowledged((evt) -> getScene().getWindow().hide())
                .addOnCancelAcknowledged((evt) -> getScene().getWindow().hide());
        return pane;
    }

    public static class StatusOption {

        private final ReadOnlyStringWrapper displayText;
        private final ReadOnlyObjectWrapper<OptionalBoolean> status;

        public StatusOption(String name, OptionalBoolean status) {
            this.displayText = new ReadOnlyStringWrapper(name);
            this.status = new ReadOnlyObjectWrapper<>(status);
        }

        public String getDisplayText() {
            return displayText.get();
        }

        public ReadOnlyStringProperty displayTextProperty() {
            return displayText.getReadOnlyProperty();
        }

        public OptionalBoolean getStatus() {
            return status.get();
        }

        public ReadOnlyObjectProperty<OptionalBoolean> statusProperty() {
            return status.getReadOnlyProperty();
        }

        @Override
        public boolean equals(Object obj) {
            return null != obj && obj instanceof StatusOption && this == obj;
        }

        @Override
        public int hashCode() {
            return displayText.get().hashCode();
        }

        @Override
        public String toString() {
            return displayText.get();
        }

    }

    class LoadCitiesTask extends Task<ArrayList<CityDAO>> {

        private final CountryDAO country;
        private final CustomerDAO customer;

        public LoadCitiesTask(CountryDAO country, CustomerDAO customer) {
            updateTitle(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_LOADINGCITIES));
            this.country = Objects.requireNonNull(country);
            this.customer = customer;
        }

        @Override
        protected void succeeded() {
            super.succeeded();
            ArrayList<CityDAO> result = getValue();
            cities.clear();
            result.forEach((item) -> {
                cities.add(new CityModel(item));
            });
            waitBorderPane.startNow(createCriticalWaitTitledPane(), new LoadCustomersTask(country, null, statusComboBox.getSelectionModel().getSelectedItem().status.get(),
                    customer));
        }

        @Override
        protected ArrayList<CityDAO> call() throws Exception {
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
            try (DbConnector dbConnector = new DbConnector()) {
                updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTEDTODB));
                return CityDAO.FACTORY.getByCountry(dbConnector.getConnection(), country.getPrimaryKey());
            }
        }

    }

    class LoadCountriesTask extends Task<ArrayList<CountryDAO>> {

        public LoadCountriesTask() {
            updateTitle(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_LOADINGCOUNTRIES));
        }

        @Override
        protected void succeeded() {
            super.succeeded();
            ArrayList<CountryDAO> result = getValue();
            result.stream().forEach((item) -> {
                countries.add(new CountryModel(item));
            });
            waitBorderPane.startNow(createCriticalWaitTitledPane(), new LoadCustomersTask(null, null, statusComboBox.getSelectionModel().getSelectedItem().status.get(), null));
        }

        @Override
        protected ArrayList<CountryDAO> call() throws Exception {
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
            try (DbConnector dbConnector = new DbConnector()) {
                updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTEDTODB));
                return CountryDAO.FACTORY.getAllCountries(dbConnector.getConnection());
            }
        }

    }

    class LoadCustomersTask extends Task<ArrayList<CustomerDAO>> {

        // PENDING: The value of the field CustomerPicker.LoadCustomersTask.country is not used
        private final CountryDAO country;
        // PENDING: The value of the field CustomerPicker.LoadCustomersTask.city is not used
        private final CityDAO city;
        // PENDING: The value of the field CustomerPicker.LoadCustomersTask.active is not used
        private final OptionalBoolean active;
        private final CustomerDAO customer;

        public LoadCustomersTask(CountryDAO country, CityDAO city, OptionalBoolean active, CustomerDAO customer) {
            updateTitle(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_LOADINGCUSTOMERS));
            this.country = country;
            this.city = city;
            this.active = active;
            this.customer = customer;
        }

        @Override
        protected void succeeded() {
            super.succeeded();
            ArrayList<CustomerDAO> result = getValue();
            allCustomers.clear();

            if (null == customer) {
                result.stream().forEach((item) -> {
                    allCustomers.add(new CustomerModel(item));
                });
                selectedCustomer = null;
            } else {
                int pk = customer.getPrimaryKey();
                Optional<CustomerModel> matching = result.stream().map((item) -> new CustomerModel(item)).filter((CustomerModel item) -> {
                    allCustomers.add(item);
                    return item.getPrimaryKey() == pk;
                }).findFirst();
                selectedCustomer = (matching.isPresent()) ? matching.get() : null;
            }
            filterCustomers();
        }

        @Override
        protected ArrayList<CustomerDAO> call() throws Exception {
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
            try (DbConnector dbConnector = new DbConnector()) {
                updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTEDTODB));
//            switch (active) {
//                case TRUE:
//                    if (null == country) {
//                        return CustomerFilter.byStatus(true).get(connection);
//                    }
//                    if (null == city) {
//                        return CustomerFilter.byCountry(country, true).get(connection);
//                    }
//                    return CustomerFilter.byCity(city, true).get(connection);
//                case FALSE:
//                    if (null == country) {
//                        return CustomerFilter.byStatus(false).get(connection);
//                    }
//                    if (null == city) {
//                        return CustomerFilter.byCountry(country, false).get(connection);
//                    }
//                    return CustomerFilter.byCity(city, false).get(connection);
//            }
//            if (null == country) {
//                return CustomerDAO.FACTORY.getAll(connection);
//            }
//            if (null == city) {
//                return CustomerFilter.byCountry(country).get(connection);
//            }
//            return CustomerFilter.byCity(city).get(connection);
                throw new UnsupportedOperationException();
                // FIXME: Implement scheduler.view.customer.CustomerPicker#getResult(Connection connection)
            }
        }

    }

}
