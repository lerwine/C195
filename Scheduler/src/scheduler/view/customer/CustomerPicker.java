package scheduler.view.customer;

import com.sun.javafx.scene.control.behavior.OptionalBoolean;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import static scheduler.AppResourceKeys.RESOURCEKEY_CONNECTEDTODB;
import static scheduler.AppResourceKeys.RESOURCEKEY_UNEXPECTEDERRORDETAILS;
import static scheduler.AppResourceKeys.RESOURCEKEY_UNEXPECTEDERRORHEADING;
import scheduler.AppResources;
import scheduler.dao.CityDAO;
import scheduler.dao.CountryDAO;
import scheduler.dao.CustomerDAO;
import static scheduler.util.NodeUtil.collapseNode;
import static scheduler.util.NodeUtil.restoreLabeled;
import scheduler.util.ResourceBundleHelper;
import scheduler.view.ErrorDetailDialog;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.FxmlViewEventHandling;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.annotations.HandlesFxmlViewEvent;
import scheduler.view.city.CityModel;
import scheduler.view.country.CountryModel;
import scheduler.view.event.FxmlViewEvent;
import scheduler.view.task.TaskWaiter;

/**
 * FXML Controller class for picking a {@link CustomerModel}.
 * <p>
 * The associated view is {@code /resources/scheduler/view/appointment/CustomerPicker.fxml}.</p>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/view/appointment/CustomerPicker")
@FXMLResource("/scheduler/view/appointment/CustomerPicker.fxml")
public class CustomerPicker {

    private static final Logger LOG = Logger.getLogger(CustomerPicker.class.getName());

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "All"}.
     */
    public static final String RESOURCEKEY_ALL = "all";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Active"}.
     */
    public static final String RESOURCEKEY_ACTIVE = "active";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Inactive"}.
     */
    public static final String RESOURCEKEY_INACTIVE = "inactive";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "CountryDAO"}.
     */
    public static final String RESOURCEKEY_COUNTRY = "country";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "CityDAO"}.
     */
    public static final String RESOURCEKEY_CITY = "city";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Name:"}.
     */
    public static final String RESOURCEKEY_NAMELABEL = "nameLabel";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Select"}.
     */
    public static final String RESOURCEKEY_SELECT = "select";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Cancel"}.
     */
    public static final String RESOURCEKEY_CANCEL = "cancel";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "New"}.
     */
    public static final String RESOURCEKEY_NEW = "new";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Name"}.
     */
    public static final String RESOURCEKEY_NAME = "name";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Phone"}.
     */
    public static final String RESOURCEKEY_PHONE = "phone";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Created On"}.
     */
    public static final String RESOURCEKEY_CREATEDON = "createdOn";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Created By"}.
     */
    public static final String RESOURCEKEY_CREATEDBY = "createdBy";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Address"}.
     */
    public static final String RESOURCEKEY_ADDRESS = "address";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Postal Code"}.
     */
    public static final String RESOURCEKEY_POSTALCODE = "postalCode";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Updated On"}.
     */
    public static final String RESOURCEKEY_UPDATEDON = "updatedOn";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Updated By"}.
     */
    public static final String RESOURCEKEY_UPDATEDBY = "updatedBy";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "CountryDAO:"}.
     */
    public static final String RESOURCEKEY_COUNTRYLABEL = "countryLabel";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "CityDAO:"}.
     */
    public static final String RESOURCEKEY_CITYLABEL = "cityLabel";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Modified"}.
     */
    public static final String RESOURCEKEY_MODIFIED = "modified";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Created"}.
     */
    public static final String RESOURCEKEY_CREATED = "created";

    public static CustomerModel pickCustomer(Stage parent) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(parent);
//        try {
//            CustomerPicker ctrl = loadViewAndController(parent, CustomerPicker.class, (Parent v, CustomerPicker c) -> {
//                stage.setScene(new Scene(v));
//            });
//            stage.showAndWait();
//            return ctrl.selectedCustomer;
//        } catch (IOException ex) {
//            AlertHelper.logAndAlertError(parent, LOG, CustomerPicker.class, "pickCustomer", String.format("Error loading FXML for %s", CustomerPicker.class.getName()), ex);
//        }
//        return null;
        throw new UnsupportedOperationException();
        // TODO: Implement scheduler.view.customer.CustomerPicker#pickCustomer(Stage parent)
    }
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

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
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
    }

    void customerSelected(CustomerModel customer) {
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
    }

    @HandlesFxmlViewEvent(FxmlViewEventHandling.BEFORE_SHOW)
    protected void onBeforeShow(FxmlViewEvent<? extends Parent> event) {
        TaskWaiter.startNow(new LoadCountriesTask(event.getStage()));
    }

    @FXML
    void cancelButtonClick(ActionEvent event) {
        selectedCustomer = null;
        ((Node) event.getSource()).getScene().getWindow().hide();
    }

    @FXML
    void nameSearchTextFieldChange(ActionEvent event) {
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
    void selectCustomerButtonClick(ActionEvent event) {
        ((Node) event.getSource()).getScene().getWindow().hide();
    }

    @FXML
    void countryFilterCheckBoxChange(ActionEvent event) {
        if (countryFilterCheckBox.isSelected()) {
            cityFilterCheckBox.setDisable(false);
            countryComboBox.setDisable(false);
            CountryModel country = countryComboBox.getSelectionModel().getSelectedItem();
            if (null != country) {
                TaskWaiter.startNow(new LoadCitiesTask(event, country.getDataObject(),
                        (null == selectedCustomer) ? null : selectedCustomer.getDataObject()));
            }
        } else {
            if (cityFilterCheckBox.isSelected()) {
                cityFilterCheckBox.setSelected(false);
            }
            cityFilterCheckBox.setDisable(true);
            countryComboBox.setDisable(true);
            cityComboBox.setDisable(true);
            TaskWaiter.startNow(new LoadCustomersTask((Stage) ((Node) event.getSource()).getScene().getWindow(), null, null,
                    statusComboBox.getSelectionModel().getSelectedItem().status.get(),
                    (null == selectedCustomer) ? null : selectedCustomer.getDataObject()));
        }
    }

    @FXML
    void countryComboBoxChange(ActionEvent event) {
        if (countryFilterCheckBox.isSelected()) {
            CountryModel country = countryComboBox.getSelectionModel().getSelectedItem();
            if (null != country) {
                TaskWaiter.startNow(new LoadCitiesTask(event, country.getDataObject(),
                        (null == selectedCustomer) ? null : selectedCustomer.getDataObject()));
            }
        }
    }

    @FXML
    void cityFilterCheckBoxChange(ActionEvent event) {
        if (countryFilterCheckBox.isSelected()) {
            CountryModel country = countryComboBox.getSelectionModel().getSelectedItem();
            if (null != country) {
                if (cityFilterCheckBox.isSelected()) {
                    CityModel city = cityComboBox.getSelectionModel().getSelectedItem();
                    if (null != city) {
                        TaskWaiter.startNow(new LoadCustomersTask((Stage) ((Node) event.getSource()).getScene().getWindow(), country.getDataObject(),
                                city.getDataObject(), statusComboBox.getSelectionModel().getSelectedItem().status.get(),
                                (null == selectedCustomer) ? null : selectedCustomer.getDataObject()));
                    }
                } else {
                    TaskWaiter.startNow(new LoadCustomersTask((Stage) ((Node) event.getSource()).getScene().getWindow(), country.getDataObject(),
                            null, statusComboBox.getSelectionModel().getSelectedItem().status.get(),
                            (null == selectedCustomer) ? null : selectedCustomer.getDataObject()));
                }
            }
            cityComboBox.setDisable(!cityFilterCheckBox.isSelected());
        }
    }

    @FXML
    void cityComboBoxChange(ActionEvent event) {
        if (countryFilterCheckBox.isSelected() && cityFilterCheckBox.isSelected()) {
            CountryModel country = countryComboBox.getSelectionModel().getSelectedItem();
            if (null != country) {
                CityModel city = cityComboBox.getSelectionModel().getSelectedItem();
                if (null != city) {
                    TaskWaiter.startNow(new LoadCustomersTask((Stage) ((Node) event.getSource()).getScene().getWindow(), country.getDataObject(),
                            city.getDataObject(), statusComboBox.getSelectionModel().getSelectedItem().status.get(),
                            (null == selectedCustomer) ? null : selectedCustomer.getDataObject()));
                }
            }
        }
    }

    @FXML
    void statusComboBoxChange(ActionEvent event) {
        if (countryFilterCheckBox.isSelected()) {
            CountryModel country = countryComboBox.getSelectionModel().getSelectedItem();
            if (null != country) {
                if (cityFilterCheckBox.isSelected()) {
                    CityModel city = cityComboBox.getSelectionModel().getSelectedItem();
                    if (null != city) {
                        TaskWaiter.startNow(new LoadCustomersTask((Stage) ((Node) event.getSource()).getScene().getWindow(), country.getDataObject(),
                                city.getDataObject(), statusComboBox.getSelectionModel().getSelectedItem().status.get(),
                                (null == selectedCustomer) ? null : selectedCustomer.getDataObject()));
                        return;
                    }
                }
                TaskWaiter.startNow(new LoadCustomersTask((Stage) ((Node) event.getSource()).getScene().getWindow(), country.getDataObject(),
                        null, statusComboBox.getSelectionModel().getSelectedItem().status.get(),
                        (null == selectedCustomer) ? null : selectedCustomer.getDataObject()));
                return;
            }
        }
        TaskWaiter.startNow(new LoadCustomersTask((Stage) ((Node) event.getSource()).getScene().getWindow(), null, null,
                statusComboBox.getSelectionModel().getSelectedItem().status.get(),
                (null == selectedCustomer) ? null : selectedCustomer.getDataObject()));
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

    class LoadCitiesTask extends TaskWaiter<ArrayList<CityDAO>> {

        private final CountryDAO country;
        private final CustomerDAO customer;

        public LoadCitiesTask(ActionEvent event, CountryDAO country, CustomerDAO customer) {
            super((Stage) ((Node) event.getSource()).getScene().getWindow(), AppResources.getResourceString(AppResources.RESOURCEKEY_LOADINGCITIES));
            this.country = Objects.requireNonNull(country);
            this.customer = customer;
        }

        @Override
        protected void processResult(ArrayList<CityDAO> result, Stage stage) {
            cities.clear();
            result.forEach((item) -> {
                cities.add(new CityModel(item));
            });
            TaskWaiter.startNow(new LoadCustomersTask(stage, country, null, statusComboBox.getSelectionModel().getSelectedItem().status.get(),
                    customer));
        }

        @Override
        protected void processException(Throwable ex, Stage stage) {
            ErrorDetailDialog.logShowAndWait(LOG, AppResources.getResourceString(RESOURCEKEY_UNEXPECTEDERRORHEADING), stage, ex,
                    AppResources.getResourceString(RESOURCEKEY_UNEXPECTEDERRORDETAILS));
        }

        @Override
        protected ArrayList<CityDAO> getResult(Connection connection) throws SQLException {
            updateMessage(AppResources.getResourceString(RESOURCEKEY_CONNECTEDTODB));
            return CityDAO.getFactory().getByCountry(connection, country.getPrimaryKey());
        }

    }

    class LoadCountriesTask extends TaskWaiter<ArrayList<CountryDAO>> {

        public LoadCountriesTask(Stage stage) {
            super(stage, AppResources.getResourceString(AppResources.RESOURCEKEY_LOADINGCOUNTRIES));
        }

        @Override
        protected void processResult(ArrayList<CountryDAO> result, Stage stage) {
            result.stream().forEach((item) -> {
                countries.add(new CountryModel(item));
            });
            TaskWaiter.startNow(new LoadCustomersTask(stage, null, null, statusComboBox.getSelectionModel().getSelectedItem().status.get(), null));
        }

        @Override
        protected void processException(Throwable ex, Stage stage) {
            ErrorDetailDialog.logShowAndWait(LOG, AppResources.getResourceString(RESOURCEKEY_UNEXPECTEDERRORHEADING), stage, ex,
                    AppResources.getResourceString(RESOURCEKEY_UNEXPECTEDERRORDETAILS));
        }

        @Override
        protected ArrayList<CountryDAO> getResult(Connection connection) throws SQLException {
            updateMessage(AppResources.getResourceString(RESOURCEKEY_CONNECTEDTODB));
            return CountryDAO.getFactory().getAllCountries(connection);
        }

    }

    class LoadCustomersTask extends TaskWaiter<ArrayList<CustomerDAO>> {

        // PENDING: The value of the field CustomerPicker.LoadCustomersTask.country is not used
        private final CountryDAO country;
        // PENDING: The value of the field CustomerPicker.LoadCustomersTask.city is not used
        private final CityDAO city;
        // PENDING: The value of the field CustomerPicker.LoadCustomersTask.active is not used
        private final OptionalBoolean active;
        private final CustomerDAO customer;

        public LoadCustomersTask(Stage stage, CountryDAO country, CityDAO city, OptionalBoolean active, CustomerDAO customer) {
            super(stage, AppResources.getResourceString(AppResources.RESOURCEKEY_LOADINGCUSTOMERS));
            this.country = country;
            this.city = city;
            this.active = active;
            this.customer = customer;
        }

        @Override
        protected void processResult(ArrayList<CustomerDAO> result, Stage stage) {
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
        protected void processException(Throwable ex, Stage stage) {
            ErrorDetailDialog.logShowAndWait(LOG, AppResources.getResourceString(RESOURCEKEY_UNEXPECTEDERRORHEADING), stage, ex,
                    AppResources.getResourceString(RESOURCEKEY_UNEXPECTEDERRORDETAILS));
        }

        @Override
        protected ArrayList<CustomerDAO> getResult(Connection connection) throws SQLException {
            updateMessage(AppResources.getResourceString(RESOURCEKEY_CONNECTEDTODB));
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
//                return CustomerDAO.getFactory().getAll(connection);
//            }
//            if (null == city) {
//                return CustomerFilter.byCountry(country).get(connection);
//            }
//            return CustomerFilter.byCity(city).get(connection);
            throw new UnsupportedOperationException();
            // TODO: Implement scheduler.view.customer.CustomerPicker#getResult(Connection connection)
        }

    }

}
