package scheduler.view.customer;

import scheduler.model.ui.CustomerModel;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import scheduler.AppResourceKeys;
import scheduler.AppResources;
import static scheduler.Scheduler.getMainController;
import scheduler.dao.AddressDAO;
import scheduler.dao.AppointmentDAO;
import scheduler.dao.CityDAO;
import scheduler.dao.CountryDAO;
import scheduler.dao.CustomerDAO;
import scheduler.dao.filter.AppointmentFilter;
import scheduler.model.City;
import scheduler.model.Country;
import scheduler.model.predefined.PredefinedData;
import scheduler.model.ui.AddressItem;
import scheduler.model.ui.CityItem;
import scheduler.model.ui.CountryItem;
import scheduler.model.ui.FxRecordModel;
import scheduler.util.DbConnector;
import scheduler.util.MapHelper;
import static scheduler.util.NodeUtil.collapseNode;
import static scheduler.util.NodeUtil.restoreLabeled;
import static scheduler.util.NodeUtil.restoreNode;
import scheduler.view.EditItem;
import scheduler.fx.ErrorDetailControl;
import scheduler.model.ui.AddressModel;
import scheduler.fx.AddressPicker;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.annotations.ModelEditor;
import scheduler.model.ui.AppointmentModel;
import scheduler.view.appointment.AppointmentModelFilter;
import static scheduler.view.customer.EditCustomerResourceKeys.*;
import scheduler.view.task.WaitBorderPane;

/**
 * FXML Controller class for editing a {@link CustomerModel}.
 * <p>
 * The associated view is {@code /resources/scheduler/view/customer/EditCustomer.fxml}.</p>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/view/customer/EditCustomer")
@FXMLResource("/scheduler/view/customer/EditCustomer.fxml")
public final class EditCustomer extends StackPane implements EditItem.ModelEditor<CustomerDAO, CustomerModel> {

    private static final Logger LOG = Logger.getLogger(EditCustomer.class.getName());

    public static CustomerModel editNew(AddressItem address, Window parentWindow, boolean keepOpen) throws IOException {
        CustomerModel.Factory factory = CustomerModel.getFactory();
        CustomerModel model = factory.createNew(factory.getDaoFactory().createNew());
        if (null != address)
            model.setAddress(address);
        return EditItem.showAndWait(parentWindow, EditCustomer.class, model, keepOpen);
    }

    public static CustomerModel edit(CustomerModel model, Window parentWindow) throws IOException {
        return EditItem.showAndWait(parentWindow, EditCustomer.class, model, false);
    }

    private final ReadOnlyBooleanWrapper valid;

    private final SimpleBooleanProperty addressValid;

    private final ReadOnlyStringWrapper windowTitle;

    private final ObservableList<String> unavailableNames;

    private final ObservableList<AppointmentModel> customerAppointments;

    private final ObservableList<CityItem> allCities;

    private final ObservableList<CityItem> cityOptions;

    private final ObservableList<CountryItem> allCountries;

    private final ObservableList<AppointmentFilterItem> filterOptions;

    private final SimpleObjectProperty<AddressItem> selectedAddress;

    @ModelEditor
    private CustomerModel model;

    @ModelEditor
    private WaitBorderPane waitBorderPane;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // fx:id="editSplitPane"
    private SplitPane editSplitPane; // Value injected by FXMLLoader

    @FXML // fx:id="nameTextField"
    private TextField nameTextField; // Value injected by FXMLLoader

    @FXML // fx:id="nameValidationLabel"
    private Label nameValidationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="activeTrueRadioButton"
    private RadioButton activeTrueRadioButton; // Value injected by FXMLLoader

    @FXML // fx:id="activeToggleGroup"
    private ToggleGroup activeToggleGroup; // Value injected by FXMLLoader

    @FXML // fx:id="activeFalseRadioButton"
    private RadioButton activeFalseRadioButton; // Value injected by FXMLLoader

    @FXML // fx:id="addressValueLabel"
    private Label addressValueLabel; // Value injected by FXMLLoader

    @FXML // fx:id="address1TextField"
    private TextField address1TextField; // Value injected by FXMLLoader

    @FXML // fx:id="address2TextField"
    private TextField address2TextField; // Value injected by FXMLLoader

    @FXML // fx:id="addressValidationLabel"
    private Label addressValidationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="cityLabel"
    private Label cityLabel; // Value injected by FXMLLoader

    @FXML // fx:id="cityZipCountryLabel"
    private Label cityZipCountryLabel; // Value injected by FXMLLoader

    @FXML // fx:id="cityComboBox"
    private ComboBox<CityItem> cityComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="cityValidationLabel"
    private Label cityValidationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="postalCodeLabel"
    private Label postalCodeLabel; // Value injected by FXMLLoader

    @FXML // fx:id="postalCodeTextField"
    private TextField postalCodeTextField; // Value injected by FXMLLoader

    @FXML // fx:id="phoneNumberValueLabel"
    private Label phoneNumberValueLabel; // Value injected by FXMLLoader

    @FXML // fx:id="phoneNumberTextField"
    private TextField phoneNumberTextField; // Value injected by FXMLLoader

    @FXML // fx:id="countryLabel"
    private Label countryLabel; // Value injected by FXMLLoader

    @FXML // fx:id="countryComboBox"
    private ComboBox<CountryItem> countryComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="countryValidationLabel"
    private Label countryValidationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="existingAddressButton"
    private Button existingAddressButton; // Value injected by FXMLLoader

    @FXML // fx:id="newAddressButton"
    private Button newAddressButton; // Value injected by FXMLLoader

    @FXML // fx:id="appointmentsVBox"
    private VBox appointmentsVBox; // Value injected by FXMLLoader

    @FXML // fx:id="appointmentFilterComboBox"
    private ComboBox<AppointmentFilterItem> appointmentFilterComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="appointmentsTableView"
    private TableView<AppointmentModel> appointmentsTableView; // Value injected by FXMLLoader

    @FXML // ResourceBundle that was given to the FXMLLoader
    private AddressPicker addressPicker;

    public EditCustomer() {
        this.valid = new ReadOnlyBooleanWrapper(false);
        this.windowTitle = new ReadOnlyStringWrapper();
        selectedAddress = new SimpleObjectProperty<>(this, "selectedAddress", null);
        selectedAddress.addListener(this::onSelectedAddressChanged);
        addressValid = new SimpleBooleanProperty(false);
        addressValid.addListener(this::onAddressValidChanged);
        unavailableNames = FXCollections.observableArrayList();
        customerAppointments = FXCollections.observableArrayList();
        filterOptions = FXCollections.observableArrayList();
        allCities = FXCollections.observableArrayList();
        cityOptions = FXCollections.observableArrayList();
        allCountries = FXCollections.observableArrayList();
    }

    @FXML
    void onAddButtonAction(ActionEvent event) {
        getMainController().addNewAppointment(model, null, getScene().getWindow(), false);
    }

    @FXML
    void onAppointmentFilterComboBoxAction(ActionEvent event) {
        waitBorderPane.startNow(new AppointmentReloadTask());
    }

    @FXML
    void onCountryComboBoxAction(ActionEvent event) {
        cityComboBox.getSelectionModel().clearSelection();
        cityOptions.clear();
        CountryItem selectedItem = countryComboBox.getValue();
        if (null != selectedItem) {
            String regionCode = selectedItem.getPredefinedData().getRegionCode();
            allCities.stream().filter((CityItem t) -> {
                CountryItem m = t.getCountry();
                return null != m && m.getPredefinedData().getRegionCode().equals(regionCode);
            }).forEach((t) -> cityOptions.add(t));
        }
        onAddressComponentChanged(address1TextField.getText(), address1TextField.getText(), cityComboBox.getValue(), countryComboBox.getValue());
    }

    @FXML
    void onCityComboBoxAction(ActionEvent event) {
        onAddressComponentChanged(address1TextField.getText(), address1TextField.getText(), cityComboBox.getValue(), countryComboBox.getValue());
    }

    @FXML
    void onDeleteAppointmentMenuItemAction(ActionEvent event) {
        AppointmentModel item = appointmentsTableView.getSelectionModel().getSelectedItem();
        if (null != item) {
            getMainController().deleteAppointment(item);
        }
    }

    @FXML
    void onEditAppointmentMenuItemAction(ActionEvent event) {
        AppointmentModel item = appointmentsTableView.getSelectionModel().getSelectedItem();
        if (null != item) {
            getMainController().editAppointment(item, getScene().getWindow());
        }
    }

    @FXML
    void onExistingAddressButtonAction(ActionEvent event) {
        addressPicker.PickAddress(waitBorderPane, (t) -> {
            if (null != t) {
                selectedAddress.set(t);
            }
        });
    }

    @FXML
    void onNewAddressButtonAction(ActionEvent event) {
        selectedAddress.set(null);
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    private void initialize() {
        assert editSplitPane != null : "fx:id=\"editSplitPane\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert nameTextField != null : "fx:id=\"nameTextField\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert nameValidationLabel != null : "fx:id=\"nameValidationLabel\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert activeTrueRadioButton != null : "fx:id=\"activeTrueRadioButton\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert activeToggleGroup != null : "fx:id=\"activeToggleGroup\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert activeFalseRadioButton != null : "fx:id=\"activeFalseRadioButton\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert addressValueLabel != null : "fx:id=\"addressValueLabel\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert address1TextField != null : "fx:id=\"address1TextField\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert address2TextField != null : "fx:id=\"address2TextField\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert addressValidationLabel != null : "fx:id=\"addressValidationLabel\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert cityLabel != null : "fx:id=\"cityLabel\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert cityZipCountryLabel != null : "fx:id=\"cityZipCountryLabel\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert cityComboBox != null : "fx:id=\"cityComboBox\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert cityValidationLabel != null : "fx:id=\"cityValidationLabel\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert postalCodeLabel != null : "fx:id=\"postalCodeLabel\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert postalCodeTextField != null : "fx:id=\"postalCodeTextField\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert phoneNumberValueLabel != null : "fx:id=\"phoneNumberValueLabel\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert phoneNumberTextField != null : "fx:id=\"phoneNumberTextField\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert countryLabel != null : "fx:id=\"countryLabel\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert countryComboBox != null : "fx:id=\"countryComboBox\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert countryValidationLabel != null : "fx:id=\"countryValidationLabel\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert existingAddressButton != null : "fx:id=\"existingAddressButton\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert newAddressButton != null : "fx:id=\"newAddressButton\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert appointmentsVBox != null : "fx:id=\"appointmentsVBox\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert appointmentFilterComboBox != null : "fx:id=\"appointmentFilterComboBox\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert appointmentsTableView != null : "fx:id=\"appointmentsTableView\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert addressPicker != null : "fx:id=\"addressPicker\" was not injected: check your FXML file 'EditCustomer.fxml'.";

        countryComboBox.setItems(allCountries);
        cityComboBox.setItems(cityOptions);
        appointmentFilterComboBox.setItems(filterOptions);

        LocalDate today = LocalDate.now();
        CustomerDAO dao = model.getDataObject();
        if (model.isNewItem()) {
            collapseNode(appointmentsVBox);
            editSplitPane.setDividerPosition(0, 1.0);
            windowTitle.set(resources.getString(RESOURCEKEY_ADDNEWCUSTOMER));
        } else {
            appointmentsTableView.setItems(customerAppointments);
            appointmentFilterComboBox.setItems(filterOptions);
            countryComboBox.setItems(allCountries);
            windowTitle.set(resources.getString(RESOURCEKEY_EDITCUSTOMER));
            filterOptions.add(new AppointmentFilterItem(resources.getString(RESOURCEKEY_CURRENTANDFUTURE),
                    AppointmentModelFilter.of(today, null, dao)));
            filterOptions.add(new AppointmentFilterItem(resources.getString(RESOURCEKEY_CURRENTAPPOINTMENTS),
                    AppointmentModelFilter.of(today, today.plusDays(1), dao)));
            filterOptions.add(new AppointmentFilterItem(resources.getString(RESOURCEKEY_PASTAPPOINTMENTS),
                    AppointmentModelFilter.of(null, today, dao)));
            filterOptions.add(new AppointmentFilterItem(resources.getString(RESOURCEKEY_ALLAPPOINTMENTS), AppointmentModelFilter.of(dao)));
            appointmentFilterComboBox.getSelectionModel().selectFirst();
            appointmentFilterComboBox.setOnAction(this::onAppointmentFilterComboBoxAction);
            selectedAddress.set(model.getAddress());
        }
        nameTextField.setText(model.getName());
        activeToggleGroup.selectToggle((model.isActive()) ? activeTrueRadioButton : activeFalseRadioButton);
        nameTextField.textProperty().addListener(this::onNameChanged);
        address1TextField.textProperty().addListener(this::onAddress1Changed);
        address2TextField.textProperty().addListener(this::onAddress2Changed);
        waitBorderPane.startNow(new InitialLoadTask());
        onSelectedAddressChanged(selectedAddress, null, selectedAddress.get());
        onNameChanged(nameTextField.textProperty());
    }

    // CURRENT: Update model from listeners
    public boolean applyChangesToModel() {
        model.setName(nameTextField.getText());
        model.setActive(activeTrueRadioButton.isSelected());
        AddressItem address = selectedAddress.get();
        if (null == address) {
            AddressModel addressModel = new AddressModel(AddressDAO.getFactory().createNew());
            addressModel.setAddress1(address1TextField.getText());
            addressModel.setAddress2(address2TextField.getText());
            addressModel.setCity(cityComboBox.getValue());
            addressModel.setPostalCode(postalCodeTextField.getText());
            addressModel.setPhone(phoneNumberTextField.getText());
            model.setAddress(addressModel);
        } else {
            model.setAddress(address);
        }
        return true;
    }

    private void onAddressValidChanged(Observable observable) {
        valid.set(((SimpleBooleanProperty) observable).get() && !nameTextField.getText().trim().isEmpty());
    }

    private void onNameChanged(Observable observable) {
        if (((StringProperty) observable).get().trim().isEmpty()) {
            valid.set(false);
            restoreNode(nameValidationLabel);
        } else {
            collapseNode(nameValidationLabel);
            valid.set(addressValid.get());
        }
    }

    private void onAddress1Changed(Observable observable) {
        onAddressComponentChanged(((StringProperty) observable).get(), address2TextField.getText(), cityComboBox.getValue(), countryComboBox.getValue());
    }

    private void onAddress2Changed(Observable observable) {
        onAddressComponentChanged(address1TextField.getText(), ((StringProperty) observable).get(), cityComboBox.getValue(), countryComboBox.getValue());
    }

    private void onAddressComponentChanged(String addr1, String addr2, CityItem city, CountryItem country) {
        boolean isValid;
        if (addr1.trim().isEmpty() && addr2.trim().isEmpty()) {
            restoreNode(addressValidationLabel);
            isValid = false;
        } else {
            collapseNode(addressValidationLabel);
            isValid = true;
        }
        if (null == country) {
            restoreLabeled(cityValidationLabel, resources.getString(RESOURCEKEY_COUNTRYNOTSELECTED));
            restoreNode(countryValidationLabel);
            isValid = false;
        } else {
            collapseNode(countryValidationLabel);
            if (null == city) {
                isValid = false;
                restoreLabeled(cityValidationLabel, resources.getString(RESOURCEKEY_CITYMUSTBESELECTED));
            } else {
                collapseNode(cityValidationLabel);
            }
        }
        valid.set(isValid);
    }

    private void onSelectedAddressChanged(ObservableValue<? extends AddressItem> observable, AddressItem oldValue, AddressItem newValue) {
        if (null == newValue) {
            restoreNode(address1TextField);
            restoreNode(address2TextField);
            collapseNode(addressValueLabel);
            cityLabel.setText(resources.getString(RESOURCEKEY_CITY));
            restoreNode(cityComboBox);
            collapseNode(cityZipCountryLabel);
            GridPane.setMargin(postalCodeLabel, new Insets(16, 0, 0, 0));
            restoreNode(postalCodeLabel);
            GridPane.setMargin(postalCodeTextField, new Insets(16, 0, 0, 0));
            restoreNode(postalCodeTextField);
            collapseNode(phoneNumberValueLabel);
            restoreNode(phoneNumberTextField);
            GridPane.setMargin(countryLabel, new Insets(16, 0, 0, 0));
            restoreNode(countryLabel);
            GridPane.setMargin(countryComboBox, new Insets(16, 0, 0, 0));
            restoreNode(countryComboBox);
            onAddressComponentChanged(address1TextField.getText(), address2TextField.getText(), cityComboBox.getValue(), countryComboBox.getValue());
            newAddressButton.setDisable(true);
        } else {
            collapseNode(address1TextField);
            collapseNode(address2TextField);
            restoreLabeled(addressValueLabel, String.format("%s%n%s", newValue.getAddress1(), newValue.getAddress2()).trim());
            cityLabel.setText(resources.getString(RESOURCEKEY_CITYZIPCOUNTRY));
            collapseNode(cityComboBox);
            restoreLabeled(cityZipCountryLabel, model.getCityZipCountry());
            GridPane.setMargin(postalCodeLabel, Insets.EMPTY);
            collapseNode(postalCodeLabel);
            GridPane.setMargin(postalCodeTextField, Insets.EMPTY);
            collapseNode(postalCodeTextField);
            restoreLabeled(phoneNumberValueLabel, newValue.getPhone());
            collapseNode(phoneNumberTextField);
            GridPane.setMargin(countryLabel, Insets.EMPTY);
            collapseNode(countryLabel);
            GridPane.setMargin(countryComboBox, Insets.EMPTY);
            collapseNode(countryComboBox);
            collapseNode(addressValidationLabel);
            collapseNode(countryValidationLabel);
            collapseNode(cityValidationLabel);
            addressValid.set(true);
            newAddressButton.setDisable(false);
        }
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
    public String getWindowTitle() {
        return windowTitle.get();
    }

    @Override
    public ReadOnlyStringProperty windowTitleProperty() {
        return windowTitle.getReadOnlyProperty();
    }

    @Override
    public FxRecordModel.ModelFactory<CustomerDAO, CustomerModel> modelFactory() {
        return CustomerModel.getFactory();
    }

    @Override
    public boolean isChanged() {
        throw new UnsupportedOperationException("Not supported yet."); // CURRENT: Implement scheduler.view.customer.EditCustomer#isChanged
    }

    @Override
    public ReadOnlyBooleanProperty changedProperty() {
        throw new UnsupportedOperationException("Not supported yet."); // CURRENT: Implement scheduler.view.customer.EditCustomer#changedProperty
    }

    @Override
    public void onEditNew() {
        throw new UnsupportedOperationException("Not supported yet."); // CURRENT: Implement scheduler.view.customer.EditCustomer#onEditNew
    }

    @Override
    public void onEditExisting(boolean isInitialize) {
        throw new UnsupportedOperationException("Not supported yet."); // CURRENT: Implement scheduler.view.customer.EditCustomer#onEditExisting
    }

    private class AppointmentFilterItem {

        private final ReadOnlyStringWrapper text;
        private final ReadOnlyObjectWrapper<AppointmentModelFilter> modelFilter;

        AppointmentFilterItem(String text, AppointmentModelFilter modelFilter) {
            this.text = new ReadOnlyStringWrapper(this, "text", text);
            this.modelFilter = new ReadOnlyObjectWrapper<>(this, "modelFilter", modelFilter);
        }

        public String getText() {
            return text.get();
        }

        public ReadOnlyStringProperty textProperty() {
            return text.getReadOnlyProperty();
        }

        public AppointmentModelFilter getModelFilter() {
            return modelFilter.get();
        }

        public ReadOnlyObjectProperty<AppointmentModelFilter> modelFilterProperty() {
            return modelFilter.getReadOnlyProperty();
        }

        @Override
        public int hashCode() {
            return text.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return null != obj && obj instanceof AppointmentFilterItem && text.equals(((AppointmentFilterItem) obj).text);
        }

        @Override
        public String toString() {
            return text.get();
        }

    }

    private class InitialLoadTask extends Task<List<AppointmentDAO>> {

        private List<CustomerDAO> customers;
        private HashMap<String, CityDAO> cities;
        private HashMap<String, CountryDAO> countries;
        private final AppointmentFilter filter;

        private InitialLoadTask() {
            updateTitle(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_LOADINGUSERS));
            AppointmentFilterItem filterItem = (filterOptions.isEmpty()) ? null : appointmentFilterComboBox.getValue();
            filter = (null == filterItem) ? null : filterItem.getModelFilter().getDaoFilter();
        }

        @Override
        protected void succeeded() {
            List<AppointmentDAO> result = getValue();
            if (null != customers && !customers.isEmpty()) {
                if (model.isNewItem()) {
                    customers.stream().map((t) -> t.getName().toLowerCase()).forEach(unavailableNames::add);
                } else {
                    int pk = model.getPrimaryKey();
                    customers.stream().filter((t) -> t.getPrimaryKey() != pk).map((t) -> t.getName().toLowerCase()).forEach(unavailableNames::add);
                }
            }
            if (null != result && !result.isEmpty()) {
                result.stream().map((t) -> new AppointmentModel(t)).forEach(customerAppointments::add);
            }
            if (null != cities && !cities.isEmpty()) {
                PredefinedData.getCityOptions(cities.values()).sorted(City::compare).forEach(allCities::add);
            } else {
                PredefinedData.getCityMap().values().stream().sorted(City::compare).forEach(allCities::add);
            }
            if (null != countries && !countries.isEmpty()) {
                PredefinedData.getCountryOptions(countries.values()).sorted(Country::compare).forEach(allCountries::add);
            } else {
                PredefinedData.getCountryMap().values().stream().sorted(Country::compare).forEach(allCountries::add);
            }
        }

        @Override
        protected void failed() {
            ErrorDetailControl.logShowAndWait(LOG, AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_DBACCESSERROR), (Stage) getScene().getWindow(),
                    getException());
            getScene().getWindow().hide();
        }

        @Override
        protected List<AppointmentDAO> call() throws Exception {
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
            try (DbConnector dbConnector = new DbConnector()) {
                updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTEDTODB));
                CustomerDAO.FactoryImpl uf = CustomerDAO.getFactory();
                customers = uf.load(dbConnector.getConnection(), uf.getAllItemsFilter());
                updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_LOADINGCITIES));
                CityDAO.FactoryImpl tf = CityDAO.getFactory();
                cities = MapHelper.toMap(tf.load(dbConnector.getConnection(), tf.getAllItemsFilter()), CityDAO::getName);
                updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_LOADINGCOUNTRIES));
                CountryDAO.FactoryImpl nf = CountryDAO.getFactory();
                countries = MapHelper.toMap(nf.load(dbConnector.getConnection(), nf.getAllItemsFilter()), CountryDAO::getName);
                if (null != filter) {
                    updateMessage(resources.getString(RESOURCEKEY_LOADINGAPPOINTMENTS));
                    AppointmentDAO.FactoryImpl af = AppointmentDAO.getFactory();
                    return af.load(dbConnector.getConnection(), filter);
                }
            }
            return null;
        }

    }

    private class AppointmentReloadTask extends Task<List<AppointmentDAO>> {

        private final AppointmentFilter filter;

        private AppointmentReloadTask() {
            updateTitle(AppResources.getResourceString(RESOURCEKEY_LOADINGAPPOINTMENTS));
            AppointmentFilterItem filterItem = (filterOptions.isEmpty()) ? null : appointmentFilterComboBox.getValue();
            filter = (null == filterItem) ? null : filterItem.getModelFilter().getDaoFilter();
        }

        @Override
        protected void succeeded() {
            List<AppointmentDAO> result = getValue();
            customerAppointments.clear();
            if (null != result && !result.isEmpty()) {
                result.forEach((t) -> {
                    customerAppointments.add(new AppointmentModel(t));
                });
            }
        }

        @Override
        protected void failed() {
            ErrorDetailControl.logShowAndWait(LOG, AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_DBACCESSERROR), (Stage) getScene().getWindow(),
                    getException());
            getScene().getWindow().hide();
        }

        @Override
        protected List<AppointmentDAO> call() throws Exception {
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
            try (DbConnector dbConnector = new DbConnector()) {
                updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTEDTODB));
                AppointmentDAO.FactoryImpl af = AppointmentDAO.getFactory();
                return af.load(dbConnector.getConnection(), filter);
            }
        }

    }

}
