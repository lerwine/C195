package scheduler.view.customer;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import scheduler.AppResourceKeys;
import scheduler.AppResources;
import scheduler.dao.AppointmentDAO;
import scheduler.dao.CityDAO;
import scheduler.dao.CountryDAO;
import scheduler.dao.CustomerDAO;
import scheduler.dao.DataRowState;
import scheduler.dao.IAddressDAO;
import scheduler.dao.filter.AppointmentFilter;
import scheduler.fx.AddressPicker;
import scheduler.model.ui.AddressItem;
import scheduler.model.ui.AppointmentModel;
import scheduler.model.ui.CustomerModel;
import scheduler.model.ui.FxRecordModel;
import scheduler.util.AlertHelper;
import scheduler.util.DbConnector;
import scheduler.util.MapHelper;
import static scheduler.util.NodeUtil.collapseNode;
import scheduler.util.Quadruplet;
import scheduler.util.Triplet;
import scheduler.view.EditItem;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.annotations.ModelEditor;
import scheduler.view.appointment.AppointmentModelFilter;
import scheduler.view.appointment.EditAppointment;
import static scheduler.view.customer.EditCustomerResourceKeys.*;
import scheduler.view.event.ItemActionRequestEvent;
import scheduler.view.task.WaitBorderPane;
import scheduler.view.task.WaitTitledPane;

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

    public static CustomerModel editNew(AddressItem<? extends IAddressDAO> address, Window parentWindow, boolean keepOpen) throws IOException {
        CustomerModel.Factory factory = CustomerModel.getFactory();
        CustomerModel model = factory.createNew(factory.getDaoFactory().createNew());
        if (null != address) {
            model.setAddress(address);
        }
        return EditItem.showAndWait(parentWindow, EditCustomer.class, model, keepOpen);
    }

    public static CustomerModel edit(CustomerModel model, Window parentWindow) throws IOException {
        return EditItem.showAndWait(parentWindow, EditCustomer.class, model, false);
    }

    private final ReadOnlyBooleanWrapper valid;
    private final ReadOnlyBooleanWrapper modified;
    private final ReadOnlyStringWrapper windowTitle;
    private final ObservableList<String> unavailableNames;
    private final ObservableList<AppointmentModel> customerAppointments;
    private final ObservableList<CityDAO> allCities;
    private final ObservableList<CityDAO> cityOptions;
    private final ObservableList<CountryDAO> allCountries;
    private final ObservableList<AppointmentFilterItem> filterOptions;
    private AddressPicker addressPicker;

    @ModelEditor
    private CustomerModel model;

    @ModelEditor
    private WaitBorderPane waitBorderPane;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

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
    private ComboBox<CityDAO> cityComboBox; // Value injected by FXMLLoader

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
    private ComboBox<CountryDAO> countryComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="countryValidationLabel"
    private Label countryValidationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="existingAddressButton"
    private Button existingAddressButton; // Value injected by FXMLLoader

    @FXML // fx:id="newAddressButton"
    private Button newAddressButton; // Value injected by FXMLLoader

    @FXML // fx:id="appointmentFilterComboBox"
    private ComboBox<AppointmentFilterItem> appointmentFilterComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="appointmentsTableView"
    private TableView<AppointmentModel> appointmentsTableView; // Value injected by FXMLLoader

    public EditCustomer() {
        windowTitle = new ReadOnlyStringWrapper("");
        valid = new ReadOnlyBooleanWrapper(false);
        modified = new ReadOnlyBooleanWrapper(false);
        unavailableNames = FXCollections.observableArrayList();
        customerAppointments = FXCollections.observableArrayList();
        filterOptions = FXCollections.observableArrayList();
        allCities = FXCollections.observableArrayList();
        cityOptions = FXCollections.observableArrayList();
        allCountries = FXCollections.observableArrayList();
        addressPicker = new AddressPicker();
        getChildren().add(addressPicker);
    }

    @FXML
    void onAddButtonAction(ActionEvent event) {
        try {
            EditAppointment.editNew(model, null, getScene().getWindow(), false);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error opening child window", ex);
        }
    }

    @FXML
    void onDeleteAppointmentMenuItemAction(ActionEvent event) {
        AppointmentModel item = appointmentsTableView.getSelectionModel().getSelectedItem();
        if (null != item) {
            Optional<ButtonType> response = AlertHelper.showWarningAlert((Stage) getScene().getWindow(), LOG,
                    AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONFIRMDELETE),
                    AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO);
            if (response.isPresent() && response.get() == ButtonType.YES) {
                waitBorderPane.startNow(new DeleteTask(item, getScene().getWindow()));
            }
        }
    }

    @FXML
    void onEditAppointmentMenuItemAction(ActionEvent event) {
        AppointmentModel item = appointmentsTableView.getSelectionModel().getSelectedItem();
        if (null != item) {
            try {
                EditAppointment.edit(item, getScene().getWindow());
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, "Error opening child window", ex);
            }
        }
    }

    @FXML
    void onExistingAddressButtonAction(ActionEvent event) {
        // FIXME: Implement picker result
        addressPicker.PickAddress(waitBorderPane, (t) -> {
//            if (null != t) {
//                selectedAddress.set(t);
//            }
        });
    }

    @FXML
    void onItemActionRequest(ItemActionRequestEvent<CustomerModel> event) {

    }

    @FXML
    void onNewAddressButtonAction(ActionEvent event) {
//        selectedAddress.set(null);
    }

    void onAppointmentFilterComboBoxAction(ActionEvent event) {
        waitBorderPane.startNow(new AppointmentReloadTask());
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
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
        assert appointmentFilterComboBox != null : "fx:id=\"appointmentFilterComboBox\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert appointmentsTableView != null : "fx:id=\"appointmentsTableView\" was not injected: check your FXML file 'EditCustomer.fxml'.";

        countryComboBox.setItems(allCountries);
        cityComboBox.setItems(cityOptions);
        appointmentFilterComboBox.setItems(filterOptions);

        LocalDate today = LocalDate.now();
        CustomerDAO dao = model.dataObject();
        if (model.isNewRow()) {
            collapseNode(appointmentFilterComboBox);
            collapseNode(appointmentsTableView);
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
        }
        nameTextField.setText(model.getName());
        activeToggleGroup.selectToggle((model.isActive()) ? activeTrueRadioButton : activeFalseRadioButton);
        WaitTitledPane pane = new WaitTitledPane();
        pane.addOnFailAcknowledged((evt) -> getScene().getWindow().hide())
                .addOnCancelAcknowledged((evt) -> getScene().getWindow().hide());
        waitBorderPane.startNow(pane, new EditDataLoadTask());
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
    public boolean isModified() {
        return modified.get();
    }

    @Override
    public ReadOnlyBooleanProperty modifiedProperty() {
        return modified.getReadOnlyProperty();
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
    public void onNewModelSaved() {
        throw new UnsupportedOperationException("Not supported yet."); // CURRENT: Implement scheduler.view.customer.EditCustomer#applyEditMode
    }

    @Override
    public void updateModel() {
        model.setName(nameTextField.getText().trim());
        model.setActive(activeTrueRadioButton.isSelected());
        // CURRENT: Finish implementing EditCustomer#updateMmodel
        //model.setAddress( );
    }

    private void loadData(List<CityDAO> cities, List<CountryDAO> countries) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.view.customer.EditCustomer.NewDataLoadTask#loadData
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

    private class EditDataLoadTask extends Task<Quadruplet<List<AppointmentDAO>, List<CustomerDAO>, List<CityDAO>, List<CountryDAO>>> {

        private final AppointmentFilter filter;

        private EditDataLoadTask() {
            updateTitle(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_LOADINGUSERS));
            AppointmentFilterItem filterItem = (filterOptions.isEmpty()) ? null : appointmentFilterComboBox.getValue();
            filter = (null == filterItem) ? null : filterItem.getModelFilter().getDaoFilter();
        }

        @Override
        protected void succeeded() {
            Quadruplet<List<AppointmentDAO>, List<CustomerDAO>, List<CityDAO>, List<CountryDAO>> result = getValue();
            if (null != result.getValue2() && !result.getValue2().isEmpty()) {
                int pk = model.getPrimaryKey();
                result.getValue2().stream().filter((t) -> t.getPrimaryKey() != pk).map((t) -> t.getName().toLowerCase()).forEach(unavailableNames::add);
            }
            if (null != result.getValue1() && !result.getValue1().isEmpty()) {
                result.getValue1().stream().map((t) -> new AppointmentModel(t)).forEach(customerAppointments::add);
            }
            loadData(result.getValue3(), result.getValue4());
        }

        @Override
        protected Quadruplet<List<AppointmentDAO>, List<CustomerDAO>, List<CityDAO>, List<CountryDAO>> call() throws Exception {
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
            try (DbConnector dbConnector = new DbConnector()) {
                updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTEDTODB));
                CustomerDAO.FactoryImpl uf = CustomerDAO.FACTORY;
                List<CustomerDAO> customers = uf.load(dbConnector.getConnection(), uf.getAllItemsFilter());
                updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_LOADINGCITIES));
                CityDAO.FactoryImpl tf = CityDAO.FACTORY;
                List<CityDAO> cities = tf.load(dbConnector.getConnection(), tf.getAllItemsFilter());
                updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_LOADINGCOUNTRIES));
                CountryDAO.FactoryImpl nf = CountryDAO.FACTORY;
                List<CountryDAO> countries = nf.load(dbConnector.getConnection(), nf.getAllItemsFilter());
                        updateMessage(resources.getString(RESOURCEKEY_LOADINGAPPOINTMENTS));
                AppointmentDAO.FactoryImpl af = AppointmentDAO.FACTORY;
                return Quadruplet.of(af.load(dbConnector.getConnection(), (null != filter) ? filter : af.getAllItemsFilter()),
                        customers, cities, countries);
            }
        }

    }

    private class NewDataLoadTask extends Task<Triplet<List<CustomerDAO>, List<CityDAO>, List<CountryDAO>>> {

        private NewDataLoadTask() {
            updateTitle(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_LOADINGUSERS));
        }

        @Override
        protected void succeeded() {
            Triplet<List<CustomerDAO>, List<CityDAO>, List<CountryDAO>> result = getValue();
            if (null != result.getValue1() && !result.getValue1().isEmpty()) {
                result.getValue1().stream().map((t) -> t.getName().toLowerCase()).forEach(unavailableNames::add);
            }
            loadData(result.getValue2(), result.getValue3());
        }

        @Override
        protected Triplet<List<CustomerDAO>, List<CityDAO>, List<CountryDAO>> call() throws Exception {
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
            try (DbConnector dbConnector = new DbConnector()) {
                updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTEDTODB));
                CustomerDAO.FactoryImpl uf = CustomerDAO.FACTORY;
                List<CustomerDAO> customers = uf.load(dbConnector.getConnection(), uf.getAllItemsFilter());
                updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_LOADINGCITIES));
                CityDAO.FactoryImpl tf = CityDAO.FACTORY;
                List<CityDAO> cities = tf.load(dbConnector.getConnection(), tf.getAllItemsFilter());
                updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_LOADINGCOUNTRIES));
                CountryDAO.FactoryImpl nf = CountryDAO.FACTORY;
                List<CountryDAO> countries = nf.load(dbConnector.getConnection(), nf.getAllItemsFilter());
                return Triplet.of(customers, cities, countries);
            }
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
        protected List<AppointmentDAO> call() throws Exception {
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
            try (DbConnector dbConnector = new DbConnector()) {
                updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTEDTODB));
                AppointmentDAO.FactoryImpl af = AppointmentDAO.FACTORY;
                return af.load(dbConnector.getConnection(), filter);
            }
        }

    }

    private class DeleteTask extends Task<String> {

        private final AppointmentModel model;
        private final Window parentWindow;
        private final AppointmentDAO dao;

        DeleteTask(AppointmentModel model, Window parentWindow) {
            updateTitle(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_DELETINGRECORD));
            dao = model.dataObject();
            this.model = model;
            this.parentWindow = parentWindow;
        }

        @Override
        protected void succeeded() {
            super.succeeded();
            String message = getValue();
            if (null != message && !message.trim().isEmpty()) {
                AlertHelper.showWarningAlert(parentWindow, LOG, AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_DELETEFAILURE), message);
            }
        }

        @Override
        protected String call() throws Exception {
            try (DbConnector connector = new DbConnector()) {
                updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CHECKINGDEPENDENCIES));
                String message = AppointmentDAO.FACTORY.getDeleteDependencyMessage(model.dataObject(), connector.getConnection());
                if (null != message && !message.trim().isEmpty()) {
                    return message;
                }
                updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_COMPLETINGOPERATION));
                AppointmentDAO.FACTORY.delete(dao, connector.getConnection());
                if (dao.getRowState() == DataRowState.DELETED) {
                    AppointmentModel.getFactory().updateItem(model, dao);
                }
            }
            return null;
        }
    }

}
