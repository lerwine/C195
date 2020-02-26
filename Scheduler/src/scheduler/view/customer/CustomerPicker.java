/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.view.customer;

import com.sun.javafx.scene.control.behavior.OptionalBoolean;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
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
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import scheduler.App;
import scheduler.dao.CityImpl;
import scheduler.dao.CountryImpl;
import scheduler.dao.CustomerImpl;
import scheduler.util.Alerts;
import scheduler.view.SchedulerController;
import scheduler.view.TaskWaiter;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.city.CityModel;
import scheduler.view.country.CountryModel;

/**
 * FXML Controller class
 *
 * @author lerwi
 */
@GlobalizationResource("scheduler/view/appointment/CustomerPicker")
@FXMLResource("/scheduler/view/appointment/CustomerPicker.fxml")
public class CustomerPicker extends SchedulerController {

    private static final Logger LOG = Logger.getLogger(CustomerPicker.class.getName());
    private ObservableList<CountryModel> countries;
    private ObservableList<CityModel> cities;
    private ObservableList<StatusOption> statusOptions;
    private ObservableList<CustomerModel> allCustomers;
    private ObservableList<CustomerModel> filteredCustomers;
    private CustomerModel selectedCustomer = null;

    //<editor-fold defaultstate="collapsed" desc="Resource bundle keys">
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
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Country"}.
     */
    public static final String RESOURCEKEY_COUNTRY = "country";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "City"}.
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
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Country:"}.
     */
    public static final String RESOURCEKEY_COUNTRYLABEL = "countryLabel";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "City:"}.
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

    //</editor-fold>
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

    public static CustomerModel pickCustomer(Stage parent) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(parent);
        try {
            CustomerPicker ctrl = load(parent, CustomerPicker.class, (Parent v, CustomerPicker c) -> {
                stage.setScene(new Scene(v));
            });
            stage.showAndWait();
            return ctrl.selectedCustomer;
        } catch (IOException ex) {
            Alerts.logAndAlertError(LOG, CustomerPicker.class, "pickCustomer", String.format("Error loading FXML for %s", CustomerPicker.class.getName()), ex);
        }
        return null;
    }
    
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
        createdLabel.setText(App.formatCreatedByOn(customer.getCreatedBy(), customer.getCreateDate()));
        modifiedLabel.setText(App.formatModifiedByOn(customer.getLastModifiedBy(), customer.getLastModifiedDate()));
        selectCustomerButton.setDisable(false);
    }

    @Override
    protected void onBeforeShow(Node currentView, Stage stage) {
        TaskWaiter.execute(new LoadCountriesTask(stage));
        super.onBeforeShow(currentView, stage);
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
                TaskWaiter.execute(new LoadCitiesTask(event, country.getDataObject(),
                        (null == selectedCustomer) ? null : selectedCustomer.getDataObject()));
            }
        } else {
            if (cityFilterCheckBox.isSelected()) {
                cityFilterCheckBox.setSelected(false);
            }
            cityFilterCheckBox.setDisable(true);
            countryComboBox.setDisable(true);
            cityComboBox.setDisable(true);
            TaskWaiter.execute(new LoadCustomersTask((Stage) ((Node) event.getSource()).getScene().getWindow(), null, null,
                    statusComboBox.getSelectionModel().getSelectedItem().status.get(),
                    (null == selectedCustomer) ? null : selectedCustomer.getDataObject()));
        }
    }

    @FXML
    void countryComboBoxChange(ActionEvent event) {
        if (countryFilterCheckBox.isSelected()) {
            CountryModel country = countryComboBox.getSelectionModel().getSelectedItem();
            if (null != country) {
                TaskWaiter.execute(new LoadCitiesTask(event, country.getDataObject(),
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
                        TaskWaiter.execute(new LoadCustomersTask((Stage) ((Node) event.getSource()).getScene().getWindow(), country.getDataObject(),
                                city.getDataObject(), statusComboBox.getSelectionModel().getSelectedItem().status.get(),
                                (null == selectedCustomer) ? null : selectedCustomer.getDataObject()));
                    }
                } else {
                    TaskWaiter.execute(new LoadCustomersTask((Stage) ((Node) event.getSource()).getScene().getWindow(), country.getDataObject(),
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
                    TaskWaiter.execute(new LoadCustomersTask((Stage) ((Node) event.getSource()).getScene().getWindow(), country.getDataObject(),
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
                        TaskWaiter.execute(new LoadCustomersTask((Stage) ((Node) event.getSource()).getScene().getWindow(), country.getDataObject(),
                                city.getDataObject(), statusComboBox.getSelectionModel().getSelectedItem().status.get(),
                                (null == selectedCustomer) ? null : selectedCustomer.getDataObject()));
                        return;
                    }
                }
                TaskWaiter.execute(new LoadCustomersTask((Stage) ((Node) event.getSource()).getScene().getWindow(), country.getDataObject(),
                        null, statusComboBox.getSelectionModel().getSelectedItem().status.get(),
                        (null == selectedCustomer) ? null : selectedCustomer.getDataObject()));
                return;
            }
        }
        TaskWaiter.execute(new LoadCustomersTask((Stage) ((Node) event.getSource()).getScene().getWindow(), null, null,
                statusComboBox.getSelectionModel().getSelectedItem().status.get(),
                (null == selectedCustomer) ? null : selectedCustomer.getDataObject()));
    }

    class LoadCitiesTask extends TaskWaiter<ArrayList<CityImpl>> {

        private final CountryImpl country;
        private final CustomerImpl customer;

        public LoadCitiesTask(ActionEvent event, CountryImpl country, CustomerImpl customer) {
            super((Stage) ((Node) event.getSource()).getScene().getWindow(), App.getResourceString(App.RESOURCEKEY_LOADINGCITIES));
            this.country = Objects.requireNonNull(country);
            this.customer = customer;
        }

        @Override
        protected void processResult(ArrayList<CityImpl> result, Stage stage) {
            cities.clear();
            result.forEach((item) -> {
                cities.add(new CityModel(item));
            });
            TaskWaiter.execute(new LoadCustomersTask(stage, country, null, statusComboBox.getSelectionModel().getSelectedItem().status.get(),
                    customer));
        }

        @Override
        protected void processException(Throwable ex, Stage stage) {
            LOG.logp(Level.SEVERE, getClass().getName(), "processException", "Error getting countries", ex);
            Alerts.showErrorAlert(App.getResourceString(App.RESOURCEKEY_DBACCESSERROR),
                    App.getResourceString(App.RESOURCEKEY_UNEXPECTEDERRORHEADING),
                    App.getResourceString(App.RESOURCEKEY_UNEXPECTEDERRORDETAILS), ex);
        }

        @Override
        protected ArrayList<CityImpl> getResult(Connection connection) throws SQLException {
            return CityImpl.getFactory().getByCountry(connection, country.getPrimaryKey());
        }

    }

    class LoadCountriesTask extends TaskWaiter<ArrayList<CountryImpl>> {

        public LoadCountriesTask(Stage stage) {
            super(stage, App.getResourceString(App.RESOURCEKEY_LOADINGCOUNTRIES));
        }

        @Override
        protected void processResult(ArrayList<CountryImpl> result, Stage stage) {
            result.stream().forEach((item) -> {
                countries.add(new CountryModel(item));
            });
            TaskWaiter.execute(new LoadCustomersTask(stage, null, null, statusComboBox.getSelectionModel().getSelectedItem().status.get(), null));
        }

        @Override
        protected void processException(Throwable ex, Stage stage) {
            LOG.logp(Level.SEVERE, getClass().getName(), "processException", "Error getting countries", ex);
            Alerts.showErrorAlert(App.getResourceString(App.RESOURCEKEY_DBACCESSERROR),
                    App.getResourceString(App.RESOURCEKEY_UNEXPECTEDERRORHEADING),
                    App.getResourceString(App.RESOURCEKEY_UNEXPECTEDERRORDETAILS), ex);
        }

        @Override
        protected ArrayList<CountryImpl> getResult(Connection connection) throws SQLException {
            return CountryImpl.getFactory().getAllCountries(connection);
        }

    }

    class LoadCustomersTask extends TaskWaiter<ArrayList<CustomerImpl>> {

        private final CountryImpl country;
        private final CityImpl city;
        private final OptionalBoolean active;
        private final CustomerImpl customer;

        public LoadCustomersTask(Stage stage, CountryImpl country, CityImpl city, OptionalBoolean active, CustomerImpl customer) {
            super(stage, App.getResourceString(App.RESOURCEKEY_LOADINGCUSTOMERS));
            this.country = country;
            this.city = city;
            this.active = active;
            this.customer = customer;
        }

        @Override
        protected void processResult(ArrayList<CustomerImpl> result, Stage stage) {
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
            LOG.logp(Level.SEVERE, getClass().getName(), "processException", "Error getting countries", ex);
            Alerts.showErrorAlert(App.getResourceString(App.RESOURCEKEY_DBACCESSERROR),
                    App.getResourceString(App.RESOURCEKEY_UNEXPECTEDERRORHEADING),
                    App.getResourceString(App.RESOURCEKEY_UNEXPECTEDERRORDETAILS), ex);
        }

        @Override
        protected ArrayList<CustomerImpl> getResult(Connection connection) throws SQLException {
            switch (active) {
                case TRUE:
                    if (null == country) {
                        return CustomerImpl.getFactory().getByStatus(connection, true);
                    }
                    if (null == city) {
                        return CustomerImpl.getFactory().getByCountry(connection, country.getPrimaryKey(), true);
                    }
                    return CustomerImpl.getFactory().getByCity(connection, city.getPrimaryKey(), true);
                case FALSE:
                    if (null == country) {
                        return CustomerImpl.getFactory().getByStatus(connection, false);
                    }
                    if (null == city) {
                        return CustomerImpl.getFactory().getByCountry(connection, country.getPrimaryKey(), false);
                    }
                    return CustomerImpl.getFactory().getByCity(connection, city.getPrimaryKey(), false);
            }
            if (null == country) {
                return CustomerImpl.getFactory().getAll(connection);
            }
            if (null == city) {
                return CustomerImpl.getFactory().getByCountry(connection, country.getPrimaryKey());
            }
            return CustomerImpl.getFactory().getByCity(connection, city.getPrimaryKey());
        }

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
}
