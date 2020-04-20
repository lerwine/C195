package scheduler.view.customer;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import static scheduler.AppResourceBundleConstants.RESOURCEKEY_CONNECTEDTODB;
import static scheduler.AppResourceBundleConstants.RESOURCEKEY_CONNECTINGTODB;
import static scheduler.AppResourceBundleConstants.RESOURCEKEY_LOADINGCITIES;
import static scheduler.AppResourceBundleConstants.RESOURCEKEY_LOADINGCOUNTRIES;
import static scheduler.AppResourceBundleConstants.RESOURCEKEY_LOADINGUSERS;
import scheduler.AppResources;
import static scheduler.Scheduler.getMainController;
import scheduler.dao.AddressDAO;
import scheduler.dao.AddressElement;
import scheduler.dao.AppointmentDAO;
import scheduler.dao.CityDAO;
import scheduler.dao.CityElement;
import scheduler.dao.CountryDAO;
import scheduler.dao.CountryElement;
import scheduler.dao.CustomerDAO;
import scheduler.dao.DataAccessObject;
import scheduler.dao.DataRowState;
import scheduler.dao.event.DataObjectEvent;
import scheduler.dao.filter.AppointmentFilter;
import scheduler.util.AlertHelper;
import scheduler.util.MapHelper;
import static scheduler.util.NodeUtil.bindCollapsible;
import static scheduler.util.NodeUtil.bindCollapsibleMessage;
import static scheduler.util.NodeUtil.collapseNode;
import static scheduler.util.NodeUtil.restoreLabeled;
import static scheduler.util.NodeUtil.restoreNode;
import scheduler.util.ViewControllerLoader;
import scheduler.view.EditItem;
import scheduler.view.MainController;
import scheduler.view.ViewAndController;
import scheduler.view.address.AddressModelImpl;
import scheduler.view.address.AddressPicker;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.annotations.HandlesDataObjectEvent;
import scheduler.view.annotations.HandlesFxmlViewEvent;
import scheduler.view.appointment.AppointmentModel;
import scheduler.view.appointment.AppointmentModelFilter;
import scheduler.view.city.CityModel;
import scheduler.view.city.CityModelImpl;
import scheduler.view.country.CityCountryModel;
import scheduler.view.country.CityOptionModel;
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

    private ObservableList<String> unavailableNames;

    private ObservableList<AppointmentModel> customerAppointments;

    private ObservableList<CityModel<? extends CityElement>> allCities;

    private ObservableList<CityModel<? extends CityElement>> cityOptions;

    private ObservableList<CityCountryModel<? extends CountryElement>> allCountries;

    private ObservableList<AppointmentFilterItem> filterOptions;

    private SimpleObjectProperty<AddressModelImpl> selectedAddress;

    private SingleSelectionModel<CityModel<? extends CityElement>> citySelectionModel;

    private SingleSelectionModel<CityCountryModel<? extends CountryElement>> countrySelectionModel;

    private AddressPicker addressPicker;

    private StringBinding nameValidationMessage;

    private BooleanBinding addressValid;

    private BooleanBinding countryValid;

    private StringBinding cityValidationMessage;

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

    @FXML // fx:id="countryLabel"
    private Label countryLabel; // Value injected by FXMLLoader

    @FXML // fx:id="countryComboBox"
    private ComboBox<CityCountryModel<? extends CountryElement>> countryComboBox; // Value injected by FXMLLoader

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
    private SingleSelectionModel<AppointmentFilterItem> appointmentFilterSelectionModel;
    private TableView.TableViewSelectionModel<AppointmentModel> appointmentsSelectionModel;

    @FXML
    void onAddButtonAction(ActionEvent event) {
        Scene scene = ((ComboBox)event.getSource()).getScene();
        MainController mainController = getMainController(scene);
        Stage stage = (Stage)scene.getWindow();
        mainController.addNewAppointment(stage, getModel().getDataObject(), null);
    }

    @FXML
    void onAppointmentFilterComboBoxAction(ActionEvent event) {
        TaskWaiter.startNow(new AppointmentReloadTask((Stage)((ComboBox)event.getSource()).getScene().getWindow()));
    }

    @FXML
    void onCountryComboBoxAction(ActionEvent event) {
        citySelectionModel.clearSelection();
        cityOptions.clear();
        CityCountryModel<? extends CountryElement> selectedItem = countrySelectionModel.getSelectedItem();
        if (null != selectedItem) {
            String regionCode = selectedItem.getOptionModel().getRegionCode();
            allCities.stream().filter((CityModel<? extends CityElement> t) -> {
                CityCountryModel<? extends CountryElement> m = t.getCountry();
                return null != m && m.getOptionModel().getRegionCode().equals(regionCode);
            }).forEach((t) -> cityOptions.add(t));
        }
    }

    @FXML
    void onDeleteAppointmentMenuItemAction(ActionEvent event) {
        AppointmentModel item = appointmentsSelectionModel.getSelectedItem();
        if (null != item) {
            MainController mainController = getMainController(appointmentsTableView);
            Stage stage = (Stage) appointmentsTableView.getScene().getWindow();
            mainController.deleteAppointment(stage, item);
        }
    }

    @FXML
    void onEditAppointmentMenuItemAction(ActionEvent event) {
        AppointmentModel item = appointmentsSelectionModel.getSelectedItem();
        if (null != item) {
            MainController mainController = getMainController(appointmentsTableView);
            Stage stage = (Stage) appointmentsTableView.getScene().getWindow();
            mainController.editAppointment(stage, item);
        }
    }

    @FXML
    void onExistingAddressButtonAction(ActionEvent event) {
        addressPicker.PickAddress((Stage) ((Button) event.getSource()).getScene().getWindow(), (t, u) -> {
            if (null != u) {
                selectedAddress.set(u);
            }
        });
    }

    @FXML
    void onNewAddressButtonAction(ActionEvent event) {
        selectedAddress.set(null);
    }

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
        assert countryLabel != null : "fx:id=\"countryLabel\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert countryComboBox != null : "fx:id=\"countryComboBox\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert countryValidationLabel != null : "fx:id=\"countryValidationLabel\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert existingAddressButton != null : "fx:id=\"existingAddressButton\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert newAddressButton != null : "fx:id=\"newAddressButton\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert appointmentsVBox != null : "fx:id=\"appointmentsVBox\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert appointmentFilterComboBox != null : "fx:id=\"appointmentFilterComboBox\" was not injected: check your FXML file 'EditCustomer.fxml'.";
        assert appointmentsTableView != null : "fx:id=\"appointmentsTableView\" was not injected: check your FXML file 'EditCustomer.fxml'.";

        unavailableNames = FXCollections.observableArrayList();
        customerAppointments = FXCollections.observableArrayList();
        filterOptions = FXCollections.observableArrayList();
        allCities = FXCollections.observableArrayList();
        cityOptions = FXCollections.observableArrayList();
        allCountries = FXCollections.observableArrayList();
        selectedAddress = new SimpleObjectProperty<>(this, "selectedAddress", null);
        selectedAddress.addListener(this::onSelectedAddressChanged);

        countryComboBox.setItems(allCountries);
        cityComboBox.setItems(cityOptions);
        appointmentFilterComboBox.setItems(filterOptions);

        countrySelectionModel = countryComboBox.getSelectionModel();
        citySelectionModel = cityComboBox.getSelectionModel();
        appointmentFilterSelectionModel = appointmentFilterComboBox.getSelectionModel();
        appointmentsSelectionModel = appointmentsTableView.getSelectionModel();
        
        nameValidationMessage = bindCollapsibleMessage(nameValidationLabel, () -> {
            String n = nameTextField.getText().trim().toLowerCase();
            if (n.isEmpty()) {
                return getResourceString(RESOURCEKEY_NAMECANNOTBEEMPTY);
            }
            if (unavailableNames.contains(n)) {
                return getResourceString(RESOURCEKEY_NAMEINUSE);
            }
            return "";
        }, nameTextField.textProperty());

        addressValid = bindCollapsible(addressValidationLabel, () -> {
            String a1 = address1TextField.getText().trim();
            String a2 = address2TextField.getText().trim();
            return null != selectedAddress.get() || !(a1.trim().isEmpty() && a2.trim().isEmpty());
        }, address1TextField.textProperty(), address2TextField.textProperty(), selectedAddress);

        countryValid = bindCollapsible(countryValidationLabel, () -> {
            CityCountryModel<? extends CountryElement> selectedItem = countrySelectionModel.getSelectedItem();
            LOG.info(String.format("Calculated countryValid=%s", null != selectedAddress.get() || null != selectedItem));
            return null != selectedAddress.get() || null != selectedItem;
        }, countrySelectionModel.selectedItemProperty(), selectedAddress);

        cityValidationMessage = bindCollapsibleMessage(cityValidationLabel, () -> {
            CityCountryModel<? extends CountryElement> selectedCountry = countrySelectionModel.getSelectedItem();
            CityModel<? extends CityElement> selectedCity = citySelectionModel.getSelectedItem();
            LOG.info(String.format("Calculated cityValid=%s", null != selectedAddress.get() || null != selectedCity));
            if (null == selectedAddress.get() && null == selectedCity) {
                if (null == selectedCountry) {
                    LOG.info("Country not selected");
                    return getResourceString(RESOURCEKEY_COUNTRYNOTSELECTED);
                }
                LOG.info("City not selected");
                return getResourceString(RESOURCEKEY_CITYMUSTBESELECTED);
            }
            return "";
        }, citySelectionModel.selectedItemProperty(), countrySelectionModel.selectedItemProperty(), selectedAddress);
    }

    @HandlesDataObjectEvent
    private void onDataObjectEvent(DataObjectEvent<? extends DataAccessObject> event) {
        DataAccessObject dao = event.getDataObject();
        switch (event.getChangeAction()) {
            case CREATED:
                if (dao instanceof CityDAO) {
                    onCityAdded((CityDAO) dao);
                } else if (dao instanceof CountryDAO) {
                    onCountryAdded((CountryDAO) dao);
                } else if (dao instanceof AppointmentDAO && !getModel().isNewItem()) {
                    onAppointmentInserted((AppointmentDAO)dao);
                }
                break;
            case DELETED:
                if (dao instanceof AddressDAO) {
                    AddressModelImpl address = selectedAddress.get();
                    if (null != address && address.getPrimaryKey() == dao.getPrimaryKey()) {
                        selectedAddress = null;
                    }
                } else if (dao instanceof CityDAO) {
                    onCityDeleted((CityDAO) dao);
                } else if (dao instanceof CountryDAO) {
                    onCountryDeleted((CountryDAO) dao);
                } else if (dao instanceof AppointmentDAO && !getModel().isNewItem()) {
                    int pk = dao.getPrimaryKey();
                    Optional<AppointmentModel> appt = customerAppointments.stream().filter((t) -> t.getPrimaryKey() == pk).findFirst();
                    appt.ifPresent((t) -> customerAppointments.remove(t));
                }
                break;
            default:
                if (dao instanceof AppointmentDAO && !getModel().isNewItem()) {
                    onAppointmentModified((AppointmentDAO)dao);
                }
                break;
        }
    }

    private void onAppointmentInserted(AppointmentDAO dao) {
        if (appointmentFilterSelectionModel.getSelectedItem().getModelFilter().getDaoFilter().test(dao)) {
            customerAppointments.add(new AppointmentModel(dao));
        }
    }
    
    private void onAppointmentModified(AppointmentDAO dao) {
        int pk = dao.getPrimaryKey();
        Optional<AppointmentModel> appt = customerAppointments.stream().filter((t) -> t.getPrimaryKey() == pk).findFirst();
        if (appointmentFilterSelectionModel.getSelectedItem().getModelFilter().getDaoFilter().test(dao)) {
            if (appt.isPresent())
                customerAppointments.set(customerAppointments.indexOf(appt.get()), new AppointmentModel(dao));
            else
                customerAppointments.add(new AppointmentModel(dao));
        } else {
            appt.ifPresent((t) -> customerAppointments.remove(t));
        }
    }
    
    private void onCountryAdded(CountryDAO dao) {
        String regionCode = dao.getName();
        allCountries.stream().filter((CityCountryModel<? extends CountryElement> t) -> {
            CountryOptionModel m = t.getOptionModel();
            return m.getRegionCode().equals(regionCode);
        }).findFirst().ifPresent((t) -> {
            CityCountryModel<? extends CountryElement> selectedCountry = countrySelectionModel.getSelectedItem();
            CityModel<? extends CityElement> selectedCity = citySelectionModel.getSelectedItem();
            int i = allCountries.indexOf(t);
            CountryModel model = new CountryModel(dao);
            allCountries.set(i, model);
            if (null != selectedCountry && selectedCountry.getOptionModel().getRegionCode().equals(regionCode)) {
                countrySelectionModel.select(model);
                if (null != selectedCity) {
                    citySelectionModel.select(selectedCity);
                }
            }
        });
    }

    private void onCountryDeleted(CountryDAO dao) {
        String regionCode = dao.getName();
        allCountries.stream().filter((CityCountryModel<? extends CountryElement> t) -> {
            CountryOptionModel m = t.getOptionModel();
            return m.getRegionCode().equals(regionCode);
        }).findFirst().ifPresent((t) -> {
            CityCountryModel<? extends CountryElement> selectedCountry = countrySelectionModel.getSelectedItem();
            CityModel<? extends CityElement> selectedCity = citySelectionModel.getSelectedItem();
            int i = allCountries.indexOf(t);
            CountryOptionModel optionModel = t.getOptionModel();
            allCountries.set(i, optionModel);
            if (null != selectedCountry && selectedCountry.getOptionModel().getRegionCode().equals(regionCode)) {
                countrySelectionModel.select(optionModel);
                if (null != selectedCity) {
                    citySelectionModel.select(selectedCity);
                }
            }
        });
    }

    private void onCityAdded(CityDAO dao) {
        String resourceKey = dao.getName();
        String regionCode = dao.getCountry().getName();
        allCities.stream().filter((CityModel<? extends CityElement> t) -> {
            CityOptionModel m = t.getOptionModel();
            return m.getResourceKey().equals(resourceKey) && m.getCountry().getOptionModel().getRegionCode().equals(regionCode);
        }).findFirst().ifPresent((t) -> {
            int i = allCities.indexOf(t);
            CityModelImpl model = new CityModelImpl(dao);
            allCities.set(i, model);
            i = cityOptions.indexOf(t);
            if (i >= 0) {
                CityModel<? extends CityElement> selectedItem = citySelectionModel.getSelectedItem();
                if (null != selectedItem && selectedItem == t) {
                    cityOptions.set(i, model);
                    citySelectionModel.select(model);
                } else {
                    cityOptions.set(i, model);
                }
            }
        });
    }

    private void onCityDeleted(CityDAO dao) {
        String resourceKey = dao.getName();
        String regionCode = dao.getCountry().getName();
        allCities.stream().filter((CityModel<? extends CityElement> t) -> {
            CityOptionModel m = t.getOptionModel();
            return m.getResourceKey().equals(resourceKey) && m.getCountry().getOptionModel().getRegionCode().equals(regionCode);
        }).findFirst().ifPresent((t) -> {
            int i = allCities.indexOf(t);
            CityOptionModel optionModel = t.getOptionModel();
            allCities.set(i, optionModel);
            i = cityOptions.indexOf(t);
            if (i >= 0) {
                CityModel<? extends CityElement> selectedItem = citySelectionModel.getSelectedItem();
                if (null != selectedItem && selectedItem == t) {
                    cityOptions.set(i, optionModel);
                    citySelectionModel.select(optionModel);
                } else {
                    cityOptions.set(i, optionModel);
                }
            }
        });
    }

    private void onSelectedAddressChanged(ObservableValue<? extends AddressModelImpl> observable, AddressModelImpl oldValue, AddressModelImpl newValue) {
        if (null == newValue) {
            restoreNode(address1TextField);
            restoreNode(address2TextField);
            collapseNode(addressValueLabel);
            cityLabel.setText(getResourceString(RESOURCEKEY_CITY));
            restoreNode(cityComboBox);
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
        } else {
            collapseNode(address1TextField);
            collapseNode(address2TextField);
            restoreLabeled(addressValueLabel, newValue.getAddressLines());
            cityLabel.setText(getResourceString(RESOURCEKEY_CITYZIPCOUNTRY));
            collapseNode(cityComboBox);
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
        }
    }

    @Override
    protected ItemModel.ModelFactory<CustomerDAO, CustomerModelImpl> getFactory() {
        return CustomerModelImpl.getFactory();
    }

    @Override
    protected BooleanExpression getValidationExpression() {
        return addressValid.and(countryValid).and(cityValidationMessage.isEmpty()).and(nameValidationMessage.isEmpty());
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
                if (getModel().isNewItem()) {
                    collapseNode(appointmentsVBox);
                    editSplitPane.setDividerPosition(0, 1.0);
                } else {
                    appointmentsTableView.setItems(customerAppointments);
                    appointmentFilterComboBox.setItems(filterOptions);
                    countryComboBox.setItems(allCountries);
                }
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
                    appointmentFilterSelectionModel.selectFirst();
                    appointmentFilterComboBox.setOnAction(this::onAppointmentFilterComboBoxAction);
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
            AddressDAO a = (AddressDAO) address;
            CityElement city = address.getCity();
            if (!city.isExisting()) {
                CityDAO c = new CityDAO();
                c.setName(city.getName());
                CountryElement e = city.getCountry();
                if (e.isExisting()) {
                    c.setCountry(e);
                } else {
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
        private final AppointmentFilter filter;

        private InitialLoadTask(Stage owner) {
            super(owner, AppResources.getResourceString(RESOURCEKEY_CONNECTINGTODB),
                    AppResources.getResourceString(RESOURCEKEY_LOADINGUSERS));
            AppointmentFilterItem filterItem = (filterOptions.isEmpty()) ? null : appointmentFilterSelectionModel.getSelectedItem();
            filter = (null == filterItem) ? null : filterItem.getModelFilter().getDaoFilter();
        }

        @Override
        protected List<AppointmentDAO> getResult(Connection connection) throws SQLException {
            updateMessage(AppResources.getResourceString(RESOURCEKEY_CONNECTEDTODB));
            CustomerDAO.FactoryImpl uf = CustomerDAO.getFactory();
            customers = uf.load(connection, uf.getAllItemsFilter());
            updateMessage(AppResources.getResourceString(RESOURCEKEY_LOADINGCITIES));
            CityDAO.FactoryImpl tf = CityDAO.getFactory();
            cities = MapHelper.toMap(tf.load(connection, tf.getAllItemsFilter()), (t) -> t.getName());
            updateMessage(AppResources.getResourceString(RESOURCEKEY_LOADINGCOUNTRIES));
            CountryDAO.FactoryImpl nf = CountryDAO.getFactory();
            countries = MapHelper.toMap(nf.load(connection, nf.getAllItemsFilter()), (t) -> t.getName());
            if (null != filter) {
                updateMessage(AppResources.getResourceString(RESOURCEKEY_LOADINGAPPOINTMENTS));
                AppointmentDAO.FactoryImpl af = AppointmentDAO.getFactory();
                return af.load(connection, filter);
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

    private class AppointmentReloadTask extends TaskWaiter<List<AppointmentDAO>> {

        private final AppointmentFilter filter;

        private AppointmentReloadTask(Stage owner) {
            super(owner, AppResources.getResourceString(RESOURCEKEY_CONNECTINGTODB),
                    AppResources.getResourceString(RESOURCEKEY_LOADINGAPPOINTMENTS));
            AppointmentFilterItem filterItem = (filterOptions.isEmpty()) ? null : appointmentFilterSelectionModel.getSelectedItem();
            filter = (null == filterItem) ? null : filterItem.getModelFilter().getDaoFilter();
        }

        @Override
        protected List<AppointmentDAO> getResult(Connection connection) throws SQLException {
            updateMessage(AppResources.getResourceString(RESOURCEKEY_CONNECTEDTODB));
            AppointmentDAO.FactoryImpl af = AppointmentDAO.getFactory();
            return af.load(connection, filter);
        }

        @Override
        protected void processResult(List<AppointmentDAO> result, Stage stage) {
            customerAppointments.clear();
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

    }

}
