package scheduler.view.customer;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import static scheduler.AppResourceBundleConstants.RESOURCEKEY_CONNECTINGTODB;
import static scheduler.AppResourceBundleConstants.RESOURCEKEY_LOADINGUSERS;
import scheduler.AppResources;
import scheduler.dao.AddressElement;
import scheduler.dao.AppointmentDAO;
import scheduler.dao.CityDAO;
import scheduler.dao.CountryDAO;
import scheduler.dao.CustomerDAO;
import scheduler.util.AlertHelper;
import static scheduler.util.NodeUtil.bindCssCollapse;
import static scheduler.util.NodeUtil.collapseNode;
import scheduler.view.EditItem;
import scheduler.view.MainController;
import scheduler.view.address.AddressModel;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.FxmlViewEventHandling;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.annotations.HandlesFxmlViewEvent;
import scheduler.view.appointment.AppointmentModel;
import scheduler.view.appointment.AppointmentModelFilter;
import scheduler.view.city.CityModelImpl;
import scheduler.view.country.CityCountryModelImpl;
import static scheduler.view.customer.EditCustomerResourceKeys.*    ;
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

    @FXML // fx:id="rootSplitPane"
    private SplitPane rootSplitPane; // Value injected by FXMLLoader

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

    @FXML // fx:id="cityLabel"
    private Label cityLabel; // Value injected by FXMLLoader

    @FXML // fx:id="cityZipCountryLabel"
    private Label cityZipCountryLabel; // Value injected by FXMLLoader

    @FXML // fx:id="cityComboBox"
    private ComboBox<CityModelImpl> cityComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="countryValueLabel"
    private Label countryValueLabel; // Value injected by FXMLLoader

    @FXML // fx:id="countryComboBox"
    private ComboBox<CityCountryModelImpl> countryComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="addressValidationLabel"
    private Label addressValidationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="postalCodeLabel"
    private Label postalCodeLabel; // Value injected by FXMLLoader

    @FXML // fx:id="postalCodeTextField"
    private TextField postalCodeTextField; // Value injected by FXMLLoader

    @FXML // fx:id="postalCodeValidationLabel"
    private Label postalCodeValidationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="countryValidationLabel"
    private Label countryValidationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="cityValidationLabel"
    private Label cityValidationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="phoneNumberValueLabel"
    private Label phoneNumberValueLabel; // Value injected by FXMLLoader

    @FXML // fx:id="phoneNumberTextField"
    private TextField phoneNumberTextField; // Value injected by FXMLLoader

    @FXML // fx:id="appointmentsVBox"
    private VBox appointmentsVBox; // Value injected by FXMLLoader

    @FXML // fx:id="appointmentFilterComboBox"
    private ComboBox<AppointmentFilterItem> appointmentFilterComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="appointmentsTableView"
    private TableView<AppointmentModel> appointmentsTableView; // Value injected by FXMLLoader

    private ObservableList<String> unavailableNames;

    private ObservableList<AppointmentModel> customerAppointments;

    private ObservableList<AppointmentFilterItem> filterOptions;
    
    private ReadOnlyObjectWrapper<AddressModel<? extends AddressElement>> selectedAddress;
    private SingleSelectionModel<CityModelImpl> citySelectionModel;

    public AddressModel<? extends AddressElement> getSelectedAddress() {
        return selectedAddress.get();
    }

    public ReadOnlyObjectProperty<AddressModel<? extends AddressElement>> selectedAddressProperty() {
        return selectedAddress.getReadOnlyProperty();
    }

    @FXML
    void addAppointmentButtonAction(ActionEvent event) {

    }

    @FXML
    void createNewButtonAction(ActionEvent event) {

    }

    @FXML
    void selectExistingButtonAction(ActionEvent event) {

    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert rootSplitPane != null : "fx:id=\"rootSplitPane\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert nameTextField != null : "fx:id=\"nameTextField\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert nameValidationLabel != null : "fx:id=\"nameValidationLabel\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert activeTrueRadioButton != null : "fx:id=\"activeTrueRadioButton\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert activeToggleGroup != null : "fx:id=\"activeToggleGroup\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert activeFalseRadioButton != null : "fx:id=\"activeFalseRadioButton\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert addressValueLabel != null : "fx:id=\"addressValueLabel\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert address1TextField != null : "fx:id=\"address1TextField\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert address2TextField != null : "fx:id=\"address2TextField\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert cityLabel != null : "fx:id=\"cityLabel\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert cityZipCountryLabel != null : "fx:id=\"cityZipCountryLabel\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert cityComboBox != null : "fx:id=\"cityComboBox\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert countryValueLabel != null : "fx:id=\"countryValueLabel\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert countryComboBox != null : "fx:id=\"countryComboBox\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert addressValidationLabel != null : "fx:id=\"addressValidationLabel\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert postalCodeLabel != null : "fx:id=\"postalCodeLabel\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert postalCodeTextField != null : "fx:id=\"postalCodeTextField\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert postalCodeValidationLabel != null : "fx:id=\"postalCodeValidationLabel\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert countryValidationLabel != null : "fx:id=\"countryValidationLabel\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert cityValidationLabel != null : "fx:id=\"cityValidationLabel\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert phoneNumberValueLabel != null : "fx:id=\"phoneNumberValueLabel\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert phoneNumberTextField != null : "fx:id=\"phoneNumberTextField\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert appointmentsVBox != null : "fx:id=\"appointmentsVBox\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert appointmentFilterComboBox != null : "fx:id=\"appointmentFilterComboBox\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert appointmentsTableView != null : "fx:id=\"appointmentsTableView\" was not injected: check your FXML file 'EditCustomer.fxml'.";

        unavailableNames = FXCollections.observableArrayList();
        customerAppointments = FXCollections.observableArrayList();
        filterOptions = FXCollections.observableArrayList();
        selectedAddress = new ReadOnlyObjectWrapper<>(null);
        if (getModel().isNewItem()) {
//            changePasswordCheckBox.setSelected(true);
//            changePasswordCheckBox.setDisable(true);
//            appointmentListingVBox.setVisible(false);
            collapseNode(appointmentsVBox);
            rootSplitPane.setDividerPosition(0, 1.0);
        } else {
            appointmentsTableView.setItems(customerAppointments);
        }

        nameValidationLabel.textProperty().bind(getNameValidationMessage());
        nameValidationLabel.visibleProperty().bind(getNameValidationMessage().isNotEmpty());
        bindCssCollapse(nameValidationLabel, getNameValidationMessage().isEmpty());
        addressValueLabel.textProperty().bind(getSelectedAddressLines());
        addressValueLabel.visibleProperty().bind(getSelectedAddressLines().isNotEmpty());
        bindCssCollapse(addressValueLabel, getSelectedAddressLines().isEmpty());
        address1TextField.visibleProperty().bind(selectedAddress.isNull());
        bindCssCollapse(address1TextField, selectedAddress.isNotNull());
        address2TextField.visibleProperty().bind(selectedAddress.isNull());
        bindCssCollapse(address2TextField, selectedAddress.isNotNull());
        addressValidationLabel.textProperty().bind(getAddressLinesValidationMessage());
        addressValidationLabel.visibleProperty().bind(getAddressLinesValidationMessage().isNotEmpty());
        bindCssCollapse(addressValidationLabel, getAddressLinesValidationMessage().isEmpty());
        cityLabel.textProperty().bind(getCityLabelText());
        cityZipCountryLabel.textProperty().bind(getSelectedCityZipCountry());
        cityZipCountryLabel.visibleProperty().bind(getSelectedCityZipCountry().isNotEmpty());
        bindCssCollapse(cityZipCountryLabel, getSelectedCityZipCountry().isEmpty());
        // CURRENT: Bind cityComboBox
        // CURRENT: Bind cityValidationLabel
        // CURRENT: Bind postalLabel
        // CURRENT: Bind postalCodeTextField
        // CURRENT: Bind postalCodeTextField
        // CURRENT: Bind postalCodeValidationLabel
        // etc
    }

    public StringBinding getCityLabelText() {
        return Bindings.createStringBinding(() -> {
            AddressModel<? extends AddressElement> model = selectedAddress.get();
            return (null == model) ? getResourceString(RESOURCEKEY_CITY) : getResourceString(RESOURCEKEY_CITYZIPCOUNTRY);
        }, selectedAddress);
    }
    
    public StringBinding getSelectedCityZipCountry() {
        return Bindings.createStringBinding(() -> {
            AddressModel<? extends AddressElement> model = selectedAddress.get();
            return (null == model) ? "" : model.getCityZipCountry();
        }, selectedAddress);
    }
    
    public StringBinding getCityValidationMessage() {
        return Bindings.createStringBinding(() -> {
            AddressModel<? extends AddressElement> model = selectedAddress.get();
            if (null == model && null == citySelectionModel.getSelectedItem()) {
                return getResourceString(RESOURCEKEY_CITYMUSTBESELECTED);
            }
            return "";
        }, citySelectionModel.selectedItemProperty(), selectedAddress);
    }
    
    public StringBinding getSelectedAddressLines() {
        return Bindings.createStringBinding(() -> {
            AddressModel<? extends AddressElement> model = selectedAddress.get();
            return (null == model) ? "" : model.getAddressLines();
        }, selectedAddress);
    }
    
    public StringBinding getAddressLinesValidationMessage() {
        return Bindings.createStringBinding(() -> {
            AddressModel<? extends AddressElement> model = selectedAddress.get();
            if (null == model) {
                String a = address1TextField.getText();
                if (address2TextField.getText().trim().isEmpty() && a.isEmpty())
                return getResourceString(RESOURCEKEY_ADDRESSCANNOTBEEMPTY);
            }
            return "";
        }, address1TextField.textProperty(), address2TextField.textProperty(), selectedAddress);
    }
    
    public StringBinding getNameValidationMessage() {
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

    @Override
    protected ItemModel.ModelFactory<CustomerDAO, CustomerModelImpl> getFactory() {
        return CustomerModelImpl.getFactory();
    }

    @Override
    protected BooleanExpression getValidationExpression() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.view.customer.EditCustomer#getValidationExpression
    }

    @HandlesFxmlViewEvent(FxmlViewEventHandling.BEFORE_SHOW)
    private void onBeforeShow(FxmlViewEvent<SplitPane> event) {
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
    }

    @Override
    protected void updateModel(CustomerModelImpl model) {
        if (!getValidationExpression().get()) {
            throw new IllegalStateException();
        }
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.view.customer.EditCustomer#updateModel
    }

    private class AppointmentFilterItem {

        private final ReadOnlyStringWrapper text;

        public String getText() {
            return text.get();
        }

        public ReadOnlyStringProperty textProperty() {
            return text.getReadOnlyProperty();
        }
        private final ReadOnlyObjectWrapper<AppointmentModelFilter> modelFilter;

        public AppointmentModelFilter getModelFilter() {
            return modelFilter.get();
        }

        public ReadOnlyObjectProperty<AppointmentModelFilter> modelFilterProperty() {
            return modelFilter.getReadOnlyProperty();
        }

        AppointmentFilterItem(String text, AppointmentModelFilter modelFilter) {
            this.text = new ReadOnlyStringWrapper(this, "text", text);
            this.modelFilter = new ReadOnlyObjectWrapper<>(this, "modelFilter", modelFilter);
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
        private List<CityDAO> cities;
        private List<CountryDAO> countries;

        private InitialLoadTask(Stage owner) {
            super(owner, AppResources.getResourceString(RESOURCEKEY_CONNECTINGTODB),
                    AppResources.getResourceString(RESOURCEKEY_LOADINGUSERS));
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
        }

        @Override
        protected void processException(Throwable ex, Stage stage) {
            AlertHelper.showErrorAlert(stage, LOG, ex);
            stage.close();
        }

        @Override
        protected List<AppointmentDAO> getResult(Connection connection) throws SQLException {
            CustomerDAO.FactoryImpl uf = CustomerDAO.getFactory();
            customers = uf.load(connection, uf.getAllItemsFilter());
            CityDAO.FactoryImpl tf = CityDAO.getFactory();
            cities = tf.load(connection, tf.getAllItemsFilter());
            CountryDAO.FactoryImpl nf = CountryDAO.getFactory();
            countries = nf.load(connection, nf.getAllItemsFilter());
            if (!filterOptions.isEmpty()) {
                AppointmentDAO.FactoryImpl af = AppointmentDAO.getFactory();
                return af.load(connection, filterOptions.get(0).getModelFilter().getDaoFilter());
            }
            return null;
        }

    }

}
