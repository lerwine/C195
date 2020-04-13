package scheduler.view.customer;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import static scheduler.AppResourceBundleConstants.RESOURCEKEY_CONNECTINGTODB;
import static scheduler.AppResourceBundleConstants.RESOURCEKEY_LOADINGUSERS;
import scheduler.AppResources;
import scheduler.dao.AddressDAO;
import scheduler.dao.AddressElement;
import scheduler.dao.AppointmentDAO;
import scheduler.dao.CityDAO;
import scheduler.dao.CityElement;
import scheduler.dao.CountryDAO;
import scheduler.dao.CountryElement;
import scheduler.dao.CustomerDAO;
import scheduler.dao.DataRowState;
import scheduler.util.AlertHelper;
import scheduler.util.MapHelper;
import static scheduler.util.NodeUtil.bindCssCollapse;
import static scheduler.util.NodeUtil.collapseNode;
import scheduler.util.ViewControllerLoader;
import scheduler.view.EditItem;
import scheduler.view.MainController;
import scheduler.view.ViewAndController;
import scheduler.view.address.AddressModel;
import scheduler.view.address.AddressModelImpl;
import scheduler.view.address.AddressPicker;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.annotations.HandlesFxmlViewEvent;
import scheduler.view.appointment.AppointmentModel;
import scheduler.view.appointment.AppointmentModelFilter;
import scheduler.view.city.CityModel;
import scheduler.view.city.CityModelImpl;
import scheduler.view.country.CityCountryModel;
import scheduler.view.country.CountryModel;
import scheduler.view.country.CountryOptionModel;
import static scheduler.view.customer.EditCustomerResourceKeys.*;
import scheduler.view.event.FxmlViewEvent;
import scheduler.view.model.ItemModel;
import scheduler.view.task.TaskWaiter;

/**
 * FXML Controller class for editing a {@link CustomerModelImpl}.
 * <p>
 * The associated view is {@code /resources/scheduler/view/customer/EditCustomer.fxml}.</p>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/view/customer/EditCustomer")
@FXMLResource("/scheduler/view/customer/EditCustomer.fxml")
public final class EditCustomer extends EditItem.EditController<CustomerDAO, CustomerModelImpl> {

    private static final Logger LOG = Logger.getLogger(EditCustomer.class.getName());

    public static CustomerModelImpl editNew(MainController mainController, Stage stage) throws IOException {
        return editNew(EditCustomer.class, mainController, stage);
    }

    public static CustomerModelImpl edit(CustomerModelImpl model, MainController mainController, Stage stage) throws IOException {
        return edit(model, EditCustomer.class, mainController, stage);
    }

    @FXML // fx:id="rootStackPane"
    private StackPane rootStackPane; // Value injected by FXMLLoader

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
    private ComboBox<CityModel<? extends CityElement>> cityComboBox; // Value injected by FXMLLoader

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

    @FXML // fx:id="countryValueLabel"
    private Label countryValueLabel; // Value injected by FXMLLoader

    @FXML // fx:id="countryComboBox"
    private ComboBox<CityCountryModel<? extends CountryElement>> countryComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="countryValidationLabel"
    private Label countryValidationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="createNewButton"
    private Button createNewButton; // Value injected by FXMLLoader

    @FXML // fx:id="appointmentsVBox"
    private VBox appointmentsVBox; // Value injected by FXMLLoader

    @FXML // fx:id="appointmentFilterComboBox"
    private ComboBox<AppointmentFilterItem> appointmentFilterComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="appointmentsTableView"
    private TableView<AppointmentModel> appointmentsTableView; // Value injected by FXMLLoader

    private ObservableList<String> unavailableNames;

    private ObservableList<AppointmentModel> customerAppointments;

    private ObservableList<CityModel<? extends CityElement>> allCities;

    private ObservableList<CityCountryModel<? extends CountryElement>> allCountries;

    private ObservableList<AppointmentFilterItem> filterOptions;

    private SimpleObjectProperty<AddressModelImpl> selectedAddress;

    private SingleSelectionModel<CityModel<? extends CityElement>> citySelectionModel;

    private SingleSelectionModel<CityCountryModel<? extends CountryElement>> countrySelectionModel;
    private AddressPicker addressPicker;

    @FXML // This method is called by the FXMLLoader when initialization is complete
    private void initialize() {
        assert rootStackPane != null : "fx:id=\"rootStackPane\" was not injected: check your FXML file 'EditCustomer.fxml'.";
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
        assert countryValueLabel != null : "fx:id=\"countryValueLabel\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert countryComboBox != null : "fx:id=\"countryComboBox\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert countryValidationLabel != null : "fx:id=\"countryValidationLabel\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert createNewButton != null : "fx:id=\"createNewButton\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert appointmentsVBox != null : "fx:id=\"appointmentsVBox\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert appointmentFilterComboBox != null : "fx:id=\"appointmentFilterComboBox\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert appointmentsTableView != null : "fx:id=\"appointmentsTableView\" was not injected: check your FXML file 'EditCustomer.fxml'.";

        unavailableNames = FXCollections.observableArrayList();
        customerAppointments = FXCollections.observableArrayList();
        filterOptions = FXCollections.observableArrayList();
        selectedAddress = new SimpleObjectProperty<>(this, "selectedAddress", null);
        countrySelectionModel = countryComboBox.getSelectionModel();
        citySelectionModel = cityComboBox.getSelectionModel();
        if (getModel().isNewItem()) {
            collapseNode(appointmentsVBox);
            editSplitPane.setDividerPosition(0, 1.0);
        } else {
            appointmentsTableView.setItems(customerAppointments);
            appointmentFilterComboBox.setItems(filterOptions);
            countryComboBox.setItems(allCountries);
        }

        nameValidationLabel.textProperty().bind(getNameValidationMessage());
        nameValidationLabel.visibleProperty().bind(getNameValidationMessage().isNotEmpty());
        bindCssCollapse(nameValidationLabel, getNameValidationMessage().isEmpty());

        addressValueLabel.textProperty().bind(Bindings.createStringBinding(() -> {
            AddressModel<? extends AddressElement> model = selectedAddress.get();
            return (null == model) ? "" : model.getAddressLines();
        }, selectedAddress));
        addressValueLabel.visibleProperty().bind(selectedAddress.isNotNull());
        bindCssCollapse(addressValueLabel, selectedAddress.isNull());

        address1TextField.visibleProperty().bind(selectedAddress.isNull());
        bindCssCollapse(address1TextField, selectedAddress.isNotNull());

        address2TextField.visibleProperty().bind(selectedAddress.isNull());
        bindCssCollapse(address2TextField, selectedAddress.isNotNull());

        addressValidationLabel.visibleProperty().bind(isAddressLineValid().not());
        bindCssCollapse(addressValidationLabel, isAddressLineValid());

        cityLabel.textProperty().bind(Bindings.createStringBinding(() -> {
            AddressModel<? extends AddressElement> model = selectedAddress.get();
            return (null == model) ? getResourceString(RESOURCEKEY_CITY) : getResourceString(RESOURCEKEY_CITYZIPCOUNTRY);
        }, selectedAddress));

        cityZipCountryLabel.textProperty().bind(Bindings.createStringBinding(() -> {
            AddressModel<? extends AddressElement> model = selectedAddress.get();
            return (null == model) ? "" : model.getCityZipCountry();
        }, selectedAddress));
        cityZipCountryLabel.visibleProperty().bind(selectedAddress.isNotNull());
        bindCssCollapse(cityZipCountryLabel, selectedAddress.isNull());

        cityComboBox.itemsProperty().bind(Bindings.createObjectBinding(() -> {
            CityCountryModel<? extends CountryElement> c = countrySelectionModel.getSelectedItem();
            if (null != selectedAddress.get() || null == c) {
                return FXCollections.emptyObservableList();
            }
            ObservableList<CityModel<? extends CityElement>> result = FXCollections.observableArrayList();
            int pk = c.getPrimaryKey();
            allCities.stream().filter((CityModel<? extends CityElement> t) -> {
                CityCountryModel<? extends CountryElement> m = t.getCountry();
                return null != m && m.getPrimaryKey() == pk;
            }).forEach((t) -> result.add(t));
            return FXCollections.unmodifiableObservableList(result);
        }, countrySelectionModel.selectedItemProperty(), selectedAddress));
        cityComboBox.visibleProperty().bind(selectedAddress.isNull().and(countrySelectionModel.selectedItemProperty().isNotNull()));
        bindCssCollapse(cityComboBox, selectedAddress.isNotNull());

        cityValidationLabel.visibleProperty().bind(isCitySelected().not());
        bindCssCollapse(cityValidationLabel, isCitySelected());

        postalCodeLabel.visibleProperty().bind(selectedAddress.isNull());
        bindCssCollapse(postalCodeLabel, selectedAddress.isNotNull());

        postalCodeTextField.visibleProperty().bind(selectedAddress.isNull());
        bindCssCollapse(postalCodeTextField, selectedAddress.isNotNull());

        phoneNumberValueLabel.textProperty().bind(Bindings.createStringBinding(() -> {
            AddressModel<? extends AddressElement> model = selectedAddress.get();
            return (null == model) ? "" : model.getPhone();
        }, selectedAddress));

        phoneNumberValueLabel.visibleProperty().bind(selectedAddress.isNotNull());
        bindCssCollapse(phoneNumberValueLabel, selectedAddress.isNull());

        countryValueLabel.textProperty().bind(Bindings.createStringBinding(() -> {
            AddressModel<? extends AddressElement> model = selectedAddress.get();
            return (null == model) ? "" : model.getCountryName();
        }, selectedAddress));

        countryValueLabel.visibleProperty().bind(selectedAddress.isNotNull());
        bindCssCollapse(countryValueLabel, selectedAddress.isNull());

        countryComboBox.visibleProperty().bind(selectedAddress.isNull());
        bindCssCollapse(countryComboBox, selectedAddress.isNotNull());

        countryValidationLabel.visibleProperty().bind(isCountrySelected().not());
        bindCssCollapse(countryValidationLabel, isCountrySelected());

        createNewButton.disableProperty().bind(selectedAddress.isNull());
    }

    @FXML
    void createNewButtonAction(ActionEvent event) {
        selectedAddress.set(null);
    }

    @FXML
    void selectExistingButtonAction(ActionEvent event) {
        addressPicker.PickAddress((Stage)((Button)event.getSource()).getScene().getWindow(), (t, u) -> {
            if (null != u)
                selectedAddress.set(u);
        });
    }

    private StringBinding getNameValidationMessage() {
        return Bindings.createStringBinding(() -> {
            String n = nameTextField.getText().trim().toLowerCase();
            if (n.isEmpty()) {
                return getResourceString(RESOURCEKEY_NAMECANNOTBEEMPTY);
            }
            if (unavailableNames.contains(n)) {
                return getResourceString(RESOURCEKEY_NAMEINUSE);
            }
            return "";
        }, nameTextField.textProperty(), unavailableNames);
    }

    private BooleanBinding isAddressLineValid() {
        return selectedAddress.isNotNull()
                .or(address1TextField.textProperty().isNotEmpty())
                .or(address2TextField.textProperty().isNotEmpty());
    }

    private BooleanBinding isCitySelected() {
        return selectedAddress.isNotNull()
                .or(citySelectionModel.selectedItemProperty().isNotNull());
    }

    private BooleanBinding isCountrySelected() {
        return selectedAddress.isNotNull()
                .or(countrySelectionModel.selectedItemProperty().isNotNull());
    }

    @Override
    protected ItemModel.ModelFactory<CustomerDAO, CustomerModelImpl> getFactory() {
        return CustomerModelImpl.getFactory();
    }

    @Override
    protected BooleanExpression getValidationExpression() {
        return isAddressLineValid().and(isCitySelected()).and(isCountrySelected()).and(getNameValidationMessage().isEmpty());
    }

    @SuppressWarnings("incomplete-switch")
    @HandlesFxmlViewEvent
    private void onFxmlViewEvent(FxmlViewEvent<SplitPane> event) {
        switch (event.getType()) {
            case LOADED:
                ViewAndController<? extends Parent, AddressPicker> vc;

                try {
                    vc = ViewControllerLoader.loadViewAndController(AddressPicker.class);
                    addressPicker = vc.getController();
                    scheduler.util.EventHelper.fireFxmlViewEvent(addressPicker, event);
                    rootStackPane.getChildren().add(vc.getView());
                } catch (IOException ex) {
                    AlertHelper.showErrorAlert(event.getStage(), LOG, ex);
                }
                break;
            case BEFORE_SHOW:
                LocalDate today = LocalDate.now();
                CustomerDAO dao = getModel().getDataObject();
                if (dao.isExisting()) {
                    filterOptions.add(new AppointmentFilterItem(getResourceString(RESOURCEKEY_CURRENTANDFUTURE),
                            AppointmentModelFilter.of(today, null, dao)));
                    filterOptions.add(new AppointmentFilterItem(getResourceString(RESOURCEKEY_CURRENTAPPOINTMENTS),
                            AppointmentModelFilter.of(today, today.plusDays(1), dao)));
                    filterOptions.add(new AppointmentFilterItem(getResourceString(RESOURCEKEY_PASTAPPOINTMENTS),
                            AppointmentModelFilter.of(null, today, dao)));
                    filterOptions.add(new AppointmentFilterItem(getResourceString(RESOURCEKEY_ALLAPPOINTMENTS), AppointmentModelFilter.of(dao)));
                }
                TaskWaiter.startNow(new InitialLoadTask(event.getStage()));
                break;
        }
        scheduler.util.EventHelper.fireFxmlViewEvent(addressPicker, event);
    }

    @Override
    protected void save(CustomerDAO dao, Connection connection) throws SQLException {
        AddressElement address = dao.getAddress();
        if (address.getRowState() == DataRowState.NEW) {
            AddressDAO a = (AddressDAO)address;
            CityElement city = address.getCity();
            if (!city.isExisting()) {
                CityDAO c = new CityDAO();
                c.setName(city.getName());
                CountryElement e = city.getCountry();
                if (e.isExisting())
                    c.setCountry(e);
                else {
                    CountryDAO n = new CountryDAO();
                    n.setName(e.getName());
                    CountryDAO.getFactory().save(n, connection);
                    c.setCountry(n);
                }
                CityDAO.getFactory().save(c, connection);
                a.setCity(c);
            }
            AddressDAO.getFactory().save(a, connection);
        }
        super.save(dao, connection);
    }

    @Override
    protected void updateModel(CustomerModelImpl model) {
        if (!getValidationExpression().get()) {
            throw new IllegalStateException();
        }
        model.setName(nameTextField.getText());
        model.setActive(activeTrueRadioButton.isSelected());
        AddressModelImpl address = selectedAddress.get();
        if (null == address) {
            address = new AddressModelImpl(AddressDAO.getFactory().createNew());
            address.setAddress1(address1TextField.getText());
            address.setAddress2(address2TextField.getText());
            address.setCity(citySelectionModel.getSelectedItem());
            address.setPostalCode(postalCodeTextField.getText());
            address.setPhone(phoneNumberTextField.getText());
        }
        model.setAddress(address);
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

    private class InitialLoadTask extends TaskWaiter<List<AppointmentDAO>> {

        private List<CustomerDAO> customers;
        private HashMap<String, CityDAO> cities;
        private HashMap<String, CountryDAO> countries;

        private InitialLoadTask(Stage owner) {
            super(owner, AppResources.getResourceString(RESOURCEKEY_CONNECTINGTODB),
                    AppResources.getResourceString(RESOURCEKEY_LOADINGUSERS));
        }

        @Override
        protected List<AppointmentDAO> getResult(Connection connection) throws SQLException {
            CustomerDAO.FactoryImpl uf = CustomerDAO.getFactory();
            customers = uf.load(connection, uf.getAllItemsFilter());
            CityDAO.FactoryImpl tf = CityDAO.getFactory();
            cities = MapHelper.toMap(tf.load(connection, tf.getAllItemsFilter()), (t) -> t.getName());
            CountryDAO.FactoryImpl nf = CountryDAO.getFactory();
            countries = MapHelper.toMap(nf.load(connection, nf.getAllItemsFilter()), (t) -> t.getName());
            if (!filterOptions.isEmpty()) {
                AppointmentDAO.FactoryImpl af = AppointmentDAO.getFactory();
                return af.load(connection, filterOptions.get(0).getModelFilter().getDaoFilter());
            }
            return null;
        }

        @Override
        protected void processResult(List<AppointmentDAO> result, Stage stage) {
            if (null != customers && !customers.isEmpty()) {
                if (getModel().isNewItem()) {
                    customers.forEach((t) -> unavailableNames.add(t.getName().toLowerCase()));
                } else {
                    int pk = getModel().getPrimaryKey();
                    customers.forEach((t) -> {
                        if (t.getPrimaryKey() != pk) {
                            unavailableNames.add(t.getName().toLowerCase());
                        }
                    });
                }
            }
            if (null != result && !result.isEmpty()) {
                result.forEach((t) -> {
                    customerAppointments.add(new AppointmentModel(t));
                });
            }
            if (null != cities && !cities.isEmpty()) {
                CountryOptionModel.getCountryOptions().stream().flatMap((t) -> t.getCities().stream()).forEach((t) -> {
                    String k = t.getResourceKey();
                    if (cities.containsKey(k)) {
                        allCities.add(new CityModelImpl(cities.get(k)));
                    } else {
                        allCities.add(t);
                    }
                });
            } else {
                CountryOptionModel.getCountryOptions().stream().flatMap((t) -> t.getCities().stream()).forEach((t) -> allCities.add(t));
            }
            if (null != countries && !countries.isEmpty()) {
                CountryOptionModel.getCountryOptions().forEach((t) -> {
                    String k = t.getRegionCode();
                    if (countries.containsKey(k)) {
                        allCountries.add(new CountryModel(countries.get(k)));
                    } else {
                        allCountries.add(t);
                    }
                });
            } else {
                CountryOptionModel.getCountryOptions().forEach((t) -> allCountries.add(t));
            }
        }

        @Override
        protected void processException(Throwable ex, Stage stage) {
            AlertHelper.showErrorAlert(stage, LOG, ex);
            stage.close();
        }

    }

}
