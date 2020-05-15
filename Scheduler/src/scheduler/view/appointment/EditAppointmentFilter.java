package scheduler.view.appointment;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableObjectValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Pair;
import javafx.util.StringConverter;
import static scheduler.AppResourceKeys.RESOURCEKEY_CONNECTEDTODB;
import static scheduler.AppResourceKeys.RESOURCEKEY_LOADINGCITIES;
import static scheduler.AppResourceKeys.RESOURCEKEY_LOADINGCOUNTRIES;
import static scheduler.AppResourceKeys.RESOURCEKEY_LOADINGCUSTOMERS;
import static scheduler.AppResourceKeys.RESOURCEKEY_LOADINGUSERS;
import scheduler.AppResources;
import static scheduler.AppResources.FXMLPROPERTYNAME_CONTROLLER;
import scheduler.dao.CityDAO;
import scheduler.dao.CountryDAO;
import scheduler.dao.CustomerDAO;
import scheduler.dao.DataAccessObject;
import scheduler.dao.UserDAO;
import scheduler.dao.filter.DateFilterType;
import scheduler.dao.filter.TextFilterType;
import scheduler.model.db.AddressRowData;
import scheduler.observables.StringBindingProperty;
import scheduler.util.AlertHelper;
import static scheduler.util.NodeUtil.clearAndSelect;
import static scheduler.util.NodeUtil.collapseNode;
import static scheduler.util.NodeUtil.restoreErrorLabeled;
import scheduler.util.ViewControllerLoader;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import static scheduler.view.appointment.ManageAppointmentsResourceKeys.*;
import scheduler.view.task.TaskWaiter;

/**
 * FXML Controller class for editing the appointment listing filter.
 * <p>
 * The associated view is {@code /resources/scheduler/view/appointment/EditAppointmentFilter.fxml}.</p>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/view/appointment/ManageAppointments")
@FXMLResource("/scheduler/view/appointment/EditAppointmentFilter.fxml")
public final class EditAppointmentFilter extends BorderPane {

    private static final Logger LOG = Logger.getLogger(EditAppointmentFilter.class.getName());

    private static <T extends DataAccessObject, U extends DataObjectItem<T>> boolean selectItem(ComboBox<U> source, T obj) {
        if (null == obj) {
            return clearAndSelect(source, (t) -> null == t.getValue());
        }
        int id = obj.getPrimaryKey();
        return clearAndSelect(source, (t) -> null != t.getValue() && t.getValue().getPrimaryKey() == id);
    }

    private static <T> boolean selectItem(ComboBox<DateTypeSelectionItem> source, DateFilterType type) {
        if (null == type) {
            return selectItem(source, DateFilterType.NONE);
        }
        return clearAndSelect(source, (t) -> t.getValue().equals(type));
    }

    private static <T extends TextOption> boolean selectItem(ComboBox<T> source, TextFilterType type) {
        if (null == type) {
            return selectItem(source, TextFilterType.NONE);
        }
        return clearAndSelect(source, (t) -> t.getValue().equals(type));
    }

    public static EditAppointmentFilter getController(BorderPane view) {
        if (null != view) {
            ObservableMap<Object, Object> properties = view.getProperties();
            if (properties.containsKey(FXMLPROPERTYNAME_CONTROLLER)) {
                Object value = properties.get(FXMLPROPERTYNAME_CONTROLLER);
                if (null != value && value instanceof EditAppointmentFilter) {
                    return (EditAppointmentFilter) value;
                }
            }
        }
        return null;
    }
    private boolean includeInactiveCustomers = false;
    private boolean includeInactiveUsers = false;
    private FilterOptionState filterOptions;
    private ObservableList<RangeSelectionItem> rangeTypeOptionList;
    private ObservableList<CustomerSelectionItem> customerList;
    private ObservableList<UserSelectionItem> userList;
    private ObservableList<DateTypeSelectionItem> startRangeTypeOptionList;
    private ObservableList<DateTypeSelectionItem> endRangeTypeOptionList;
    private ObservableList<CustomerSelectionItem> filteredCustomerList;
    private ObservableList<CitySelectionItem> filteredCityList;
    private ObservableList<CitySelectionItem> cityList;
    private ObservableList<CountrySelectionItem> countryList;
    private ObservableList<TitleTextSelectionItem> titleTextSearchOptionList;
    private ObservableList<LocationTextSelectionItem> locationTextSearchOptionList;
    private StringBindingProperty dateRangeValidationBinding;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

//    @FXML // URL location of the FXML file that was given to the FXMLLoader
//    private URL location;
    @FXML // fx:id="rootBorderPane"
    private BorderPane rootBorderPane; // Value injected by FXMLLoader

    @FXML // fx:id="searchTypesTabPane"
    private TabPane searchTypesTabPane; // Value injected by FXMLLoader

    @FXML // fx:id="preDefinedRangesTab"
    private Tab preDefinedRangesTab; // Value injected by FXMLLoader

    @FXML // fx:id="customerComboBox"
    private ComboBox<CustomerSelectionItem> customerComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="userComboBox"
    private ComboBox<UserSelectionItem> userComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="rangeTypeComboBox"
    private ComboBox<RangeSelectionItem> rangeTypeComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="customTab"
    private Tab customTab; // Value injected by FXMLLoader

    @FXML // fx:id="dateRadioButton"
    private RadioButton dateRadioButton; // Value injected by FXMLLoader

    @FXML // fx:id="dateGranularity"
    private ToggleGroup dateGranularity; // Value injected by FXMLLoader

    @FXML // fx:id="hourRadioButton"
    private RadioButton hourRadioButton; // Value injected by FXMLLoader

    @FXML // fx:id="minuteRadioButton"
    private RadioButton minuteRadioButton; // Value injected by FXMLLoader

    @FXML // fx:id="startDatePicker"
    private DatePicker startDatePicker; // Value injected by FXMLLoader

    @FXML // fx:id="startHourSpinner"
    private Spinner<Integer> startHourSpinner; // Value injected by FXMLLoader

    @FXML // fx:id="startMinuteSpinner"
    private Spinner<Integer> startMinuteSpinner; // Value injected by FXMLLoader

    @FXML // fx:id="endDatePicker"
    private DatePicker endDatePicker; // Value injected by FXMLLoader

    @FXML // fx:id="endHourSpinner"
    private Spinner<Integer> endHourSpinner; // Value injected by FXMLLoader

    @FXML // fx:id="endMinuteSpinner"
    private Spinner<Integer> endMinuteSpinner; // Value injected by FXMLLoader

    @FXML // fx:id="customCustomerComboBox"
    private ComboBox<CustomerSelectionItem> customCustomerComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="cityComboBox"
    private ComboBox<CitySelectionItem> cityComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="countryComboBox"
    private ComboBox<CountrySelectionItem> countryComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="customUserComboBox"
    private ComboBox<UserSelectionItem> customUserComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="titleTextField"
    private TextField titleTextField; // Value injected by FXMLLoader

    @FXML // fx:id="locationTextField"
    private TextField locationTextField; // Value injected by FXMLLoader

    @FXML // fx:id="startComboBox"
    private ComboBox<DateTypeSelectionItem> startComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="endComboBox"
    private ComboBox<DateTypeSelectionItem> endComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="titleComboBox"
    private ComboBox<TitleTextSelectionItem> titleComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="locationComboBox"
    private ComboBox<LocationTextSelectionItem> locationComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="dateRangeErrorLabel"
    private Label dateRangeErrorLabel; // Value injected by FXMLLoader

    @FXML // fx:id="customerRadioButton"
    private RadioButton customerRadioButton; // Value injected by FXMLLoader

    @FXML // fx:id="customCustomer"
    private ToggleGroup customCustomer; // Value injected by FXMLLoader

    @FXML // fx:id="cityRadioButton"
    private RadioButton cityRadioButton; // Value injected by FXMLLoader

    @FXML // fx:id="countryRadioButton"
    private RadioButton countryRadioButton; // Value injected by FXMLLoader

    @FXML // fx:id="okButton"
    private Button okButton; // Value injected by FXMLLoader

    @FXML // fx:id="lookupOptionsBorderPane"
    private BorderPane lookupOptionsBorderPane; // Value injected by FXMLLoader

    @FXML // fx:id="lookupOptionCustomersCheckBox"
    private CheckBox lookupOptionCustomersCheckBox; // Value injected by FXMLLoader

    @FXML // fx:id="lookupOptionUsersCheckBox"
    private CheckBox lookupOptionUsersCheckBox; // Value injected by FXMLLoader

    @FXML
    private void cancelButtonAction(ActionEvent event) {
        ((Button) event.getSource()).getScene().getWindow().hide();
    }

    @FXML
    private void cityComboBoxChanged(ActionEvent event) {
        CitySelectionItem cityItem = (countryRadioButton.isSelected()) ? null : cityComboBox.getValue();
        CustomerSelectionItem currentCustomer = (customerRadioButton.isSelected()) ? customCustomerComboBox.getValue() : null;
        if (null == cityItem || null == cityItem.getValue()) {
            CountrySelectionItem countryItem = countryComboBox.getValue();
            if (null == countryItem || null == countryItem.getValue()) {
                if (filteredCustomerList.size() < customerList.size()) {
                    filteredCustomerList.clear();
                    filteredCustomerList.addAll(customerList);
                }
            } else {
                int countryId = countryItem.getValue().getPrimaryKey();
                filteredCustomerList.clear();
                filteredCustomerList.addAll(customerList.filtered((t) -> null == t.getValue() || t.getCountryId() == countryId));
            }
        } else {
            int cityId = cityItem.getValue().getPrimaryKey();
            filteredCustomerList.clear();
            filteredCustomerList.addAll(customerList.filtered((t) -> null == t.getValue() || t.getCityId() == cityId));
        }
        if (!selectItem(customCustomerComboBox, (null == currentCustomer) ? null : currentCustomer.getValue())) {
            selectItem(customCustomerComboBox, null);
        }
    }

    @FXML
    private void countryComboBoxChanged(ActionEvent event) {
        CountrySelectionItem countryItem = countryComboBox.getValue();
        CitySelectionItem currentCity = (countryRadioButton.isSelected()) ? null : cityComboBox.getValue();
        CustomerSelectionItem currentCustomer = (customerRadioButton.isSelected()) ? customCustomerComboBox.getValue() : null;
        if (null == countryItem || null == countryItem.getValue()) {
            if (filteredCityList.size() < cityList.size()) {
                filteredCityList.clear();
                filteredCityList.addAll(cityList);
            }
            if (null == currentCity || null == currentCity.getValue()) {
                if (filteredCustomerList.size() < customerList.size()) {
                    filteredCustomerList.clear();
                    filteredCustomerList.addAll(customerList);
                }
            }
        } else {
            int countryId = countryItem.getValue().getPrimaryKey();
            filteredCityList.clear();
            filteredCityList.addAll(cityList.filtered((t) -> null == t.getValue() || t.getCountryId() == countryId));
            filteredCustomerList.clear();
            if (null == currentCity || null == currentCity.getValue()) {
                filteredCustomerList.addAll(customerList.filtered((t) -> null == t.getValue() || t.getCountryId() == countryId));
            } else {
                int cityId = currentCity.getValue().getPrimaryKey();
                filteredCustomerList.addAll(customerList.filtered((t) -> null == t.getValue() || t.getCityId() == cityId));
            }
        }
        if (!selectItem(cityComboBox, (null == currentCity) ? null : currentCity.getValue())) {
            selectItem(cityComboBox, null);
        }
        if (!selectItem(customCustomerComboBox, (null == currentCustomer) ? null : currentCustomer.getValue())) {
            selectItem(customCustomerComboBox, null);
        }
    }

    @FXML
    private void customerRadioButtonChanged(ActionEvent event) {
        if (countryRadioButton.isSelected()) {
            selectItem(customCustomerComboBox, null);
            selectItem(cityComboBox, null);
            customCustomerComboBox.setDisable(true);
            cityComboBox.setDisable(true);
            countryComboBox.setDisable(false);
        } else if (cityRadioButton.isSelected()) {
            selectItem(customCustomerComboBox, null);
            customCustomerComboBox.setDisable(true);
            cityComboBox.setDisable(false);
            countryComboBox.setDisable(false);
        } else {
            customCustomerComboBox.setDisable(false);
            cityComboBox.setDisable(false);
            countryComboBox.setDisable(false);
        }
    }

    @FXML
    private void endComboBoxChanged(ActionEvent event) {
        DateTypeSelectionItem opt = endComboBox.getValue();
        if (null == opt || opt.getValue().equals(DateFilterType.NONE)) {
            endDatePicker.setDisable(true);
            endHourSpinner.setDisable(true);
            endMinuteSpinner.setDisable(true);
            opt = startComboBox.getValue();
            if (null != opt && opt.getValue().equals(DateFilterType.ON)) {
                selectItem(startComboBox, DateFilterType.INCLUSIVE);
            }
        } else {
            endDatePicker.setDisable(false);
            if (minuteRadioButton.isSelected()) {
                endHourSpinner.setDisable(false);
                endMinuteSpinner.setDisable(false);
            } else {
                endMinuteSpinner.setDisable(true);
                endHourSpinner.setDisable(!hourRadioButton.isSelected());
            }
        }
    }

    @FXML
    private void locationComboBoxChanged(ActionEvent event) {
        LocationTextSelectionItem opt = locationComboBox.getSelectionModel().getSelectedItem();
        locationTextField.setDisable(null == opt || opt.getValue().equals(TextFilterType.NONE));
    }

    @FXML
    private void lookupOptionsButtonClick(ActionEvent event) {
        lookupOptionCustomersCheckBox.setSelected(includeInactiveCustomers);
        lookupOptionUsersCheckBox.setSelected(includeInactiveUsers);
        lookupOptionsBorderPane.setVisible(true);
    }

    @FXML
    private void lookupOptionsCancelClick(ActionEvent event) {
        lookupOptionsBorderPane.setVisible(false);
    }

    @FXML
    private void lookupOptionsOkClick(ActionEvent event) {
        Stage stage;
        if (lookupOptionCustomersCheckBox.isSelected() != includeInactiveCustomers) {
            includeInactiveCustomers = lookupOptionCustomersCheckBox.isSelected();
            if (lookupOptionUsersCheckBox.isSelected() != includeInactiveUsers) {
                includeInactiveUsers = lookupOptionUsersCheckBox.isSelected();
                TaskWaiter.startNow(new ReloadCustomersAndUsersTask((Stage) ((Button) event.getSource()).getScene().getWindow(),
                        includeInactiveCustomers, includeInactiveUsers));
            } else {
                stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                CustomerDAO.FactoryImpl cf = CustomerDAO.getFactory();
                cf.loadAsync((this.includeInactiveCustomers) ? cf.getAllItemsFilter() : cf.getActiveStatusFilter(true),
                        (t) -> importCustomers(t), (t) -> AlertHelper.logAndAlertDbError(stage, LOG,
                                resources.getString(RESOURCEKEY_ERRORLOADINGDATA), "Error loading reloading customers", t));
            }
        } else if (lookupOptionUsersCheckBox.isSelected() != includeInactiveUsers) {
            includeInactiveUsers = lookupOptionUsersCheckBox.isSelected();
            stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            UserDAO.FactoryImpl uf = UserDAO.getFactory();
            uf.loadAsync((this.includeInactiveUsers) ? uf.getAllItemsFilter() : uf.getAllItemsFilter(), (t) -> importUsers(t),
                    (t) -> AlertHelper.logAndAlertDbError(stage, LOG, resources.getString(RESOURCEKEY_ERRORLOADINGDATA),
                            "Error loading reloading users", t));
        }
        lookupOptionsBorderPane.setVisible(false);
    }

    @FXML
    private void okButtonAction(ActionEvent event) {
        ((Button) event.getSource()).getScene().getWindow().hide();
    }

    @FXML
    private void searchTypesTabSelectionChanged(ActionEvent event) {
        if (((Tab) event.getSource()).isSelected()) {
            if (customTab.isSelected()) {
                RangeSelectionItem item = rangeTypeComboBox.getValue();
                if (null != item) {
                    switch (item.getValue()) {
                        case CURRENT:
                            selectItem(startComboBox, DateFilterType.ON);
                            selectItem(endComboBox, DateFilterType.NONE);
                            startDatePicker.setValue(LocalDate.now());
                            if (startHourSpinner.getValue() > 0) {
                                startHourSpinner.decrement(startHourSpinner.getValue());
                            }
                            if (startMinuteSpinner.getValue() > 0) {
                                startMinuteSpinner.decrement(startMinuteSpinner.getValue());
                            }
                            break;
                        case CURRENT_AND_FUTURE:
                            selectItem(startComboBox, DateFilterType.INCLUSIVE);
                            selectItem(endComboBox, DateFilterType.NONE);
                            startDatePicker.setValue(LocalDate.now());
                            if (startHourSpinner.getValue() > 0) {
                                startHourSpinner.decrement(startHourSpinner.getValue());
                            }
                            if (startMinuteSpinner.getValue() > 0) {
                                startMinuteSpinner.decrement(startMinuteSpinner.getValue());
                            }
                            break;
                        case CURRENT_AND_PAST:
                            selectItem(startComboBox, DateFilterType.NONE);
                            selectItem(endComboBox, DateFilterType.INCLUSIVE);
                            endDatePicker.setValue(LocalDate.now());
                            if (endHourSpinner.getValue() > 0) {
                                endHourSpinner.decrement(endHourSpinner.getValue());
                            }
                            if (endMinuteSpinner.getValue() > 0) {
                                endMinuteSpinner.decrement(endMinuteSpinner.getValue());
                            }
                            break;
                        case FUTURE:
                            selectItem(startComboBox, DateFilterType.EXCLUSIVE);
                            selectItem(endComboBox, DateFilterType.NONE);
                            startDatePicker.setValue(LocalDate.now());
                            if (startHourSpinner.getValue() > 0) {
                                startHourSpinner.decrement(startHourSpinner.getValue());
                            }
                            if (startMinuteSpinner.getValue() > 0) {
                                startMinuteSpinner.decrement(startMinuteSpinner.getValue());
                            }
                            break;
                        case PAST:
                            selectItem(startComboBox, DateFilterType.NONE);
                            selectItem(endComboBox, DateFilterType.EXCLUSIVE);
                            endDatePicker.setValue(LocalDate.now());
                            if (endHourSpinner.getValue() > 0) {
                                endHourSpinner.decrement(endHourSpinner.getValue());
                            }
                            if (endMinuteSpinner.getValue() > 0) {
                                endMinuteSpinner.decrement(endMinuteSpinner.getValue());
                            }
                            break;
                        default:
                            selectItem(startComboBox, DateFilterType.NONE);
                            selectItem(endComboBox, DateFilterType.NONE);
                            break;
                    }
                }
                dateRadioButton.setSelected(true);
                customerRadioButton.setSelected(true);
                CustomerSelectionItem customer = customerComboBox.getValue();
                if (!selectItem(customCustomerComboBox, (null == customer) ? null : customer.getValue())) {
                    int cityId = customer.getCityId();
                    Optional<CitySelectionItem> existing = cityComboBox.getItems().stream().filter((t) -> null != t.getValue() && t.getValue().getPrimaryKey() == cityId).findFirst();
                    if (existing.isPresent()) {
                        selectItem(cityComboBox, existing.get().getValue());
                    } else {
                        CitySelectionItem city = cityList.stream().filter((t)
                                -> null != t.getValue() && t.getValue().getPrimaryKey() == cityId).findFirst().get();
                        int countryId = city.getCountryId();
                        selectItem(countryComboBox, countryComboBox.getItems().stream().filter((t)
                                -> null != t.getValue() && t.getValue().getPrimaryKey() == countryId).findFirst().get().getValue());
                        selectItem(cityComboBox, city.getValue());
                    }
                    selectItem(customCustomerComboBox, customer.getValue());
                }
                UserSelectionItem user = userComboBox.getValue();
                selectItem(customUserComboBox, (null == user) ? null : user.getValue());
                selectItem(titleComboBox, TextFilterType.NONE);
                selectItem(locationComboBox, TextFilterType.NONE);
            }
        }
    }

    @FXML
    private void startComboBoxChanged(ActionEvent event) {
        DateTypeSelectionItem opt = startComboBox.getSelectionModel().getSelectedItem();
        if (null == opt || opt.getValue().equals(DateFilterType.NONE)) {
            startDatePicker.setDisable(true);
            startHourSpinner.setDisable(true);
            startMinuteSpinner.setDisable(true);
            dateRadioButton.setDisable(true);
            hourRadioButton.setDisable(true);
            minuteRadioButton.setDisable(true);
        } else {
            startDatePicker.setDisable(false);
            dateRadioButton.setDisable(false);
            hourRadioButton.setDisable(false);
            minuteRadioButton.setDisable(false);
            if (minuteRadioButton.isSelected()) {
                startHourSpinner.setDisable(false);
                startMinuteSpinner.setDisable(false);
            } else {
                startMinuteSpinner.setDisable(true);
                startHourSpinner.setDisable(!hourRadioButton.isSelected());
            }
            if (opt.getValue().equals(DateFilterType.ON)) {
                opt = endComboBox.getValue();
                if (null != opt && !opt.getValue().equals(DateFilterType.NONE)) {
                    selectItem(endComboBox, DateFilterType.NONE);
                }
            }
        }
    }

    @FXML
    private void timeRadioButtonChanged(ActionEvent event) {
        DateTypeSelectionItem opt = startComboBox.getSelectionModel().getSelectedItem();
        if (null == opt || opt.getValue().equals(DateFilterType.NONE)) {
            return;
        }
        if (minuteRadioButton.isSelected()) {
            startMinuteSpinner.setDisable(false);
            endMinuteSpinner.setDisable(false);
        } else {
            startMinuteSpinner.setDisable(true);
            endMinuteSpinner.setDisable(true);
            if (hourRadioButton.isSelected()) {
                startHourSpinner.setDisable(false);
                endHourSpinner.setDisable(false);
            } else {
                startHourSpinner.setDisable(true);
                endHourSpinner.setDisable(true);
            }
        }
    }

    @FXML
    private void titleComboBoxChanged(ActionEvent event) {
        LocationTextSelectionItem opt = locationComboBox.getSelectionModel().getSelectedItem();
        locationTextField.setDisable(null == opt || opt.getValue().equals(TextFilterType.NONE));
    }
    
    @SuppressWarnings("LeakingThisInConstructor")
    public EditAppointmentFilter() {
        try {
            ViewControllerLoader.initializeCustomControl(this);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error loading view", ex);
            throw new InternalError("Error loading view", ex);
        }
    }

    private void initializePredefined(FilterOptionState inputState, RangeOptionValue type) {
        searchTypesTabPane.getSelectionModel().select(preDefinedRangesTab);
        clearAndSelect(rangeTypeComboBox, (t) -> t.getValue().equals(type));
        selectItem(customerComboBox, inputState.getCustomer());
        selectItem(userComboBox, inputState.getUser());
    }

    private void initializeCustom(FilterOptionState inputState) {
        searchTypesTabPane.getSelectionModel().select(customTab);
        selectItem(startComboBox, inputState.getStartOption());
        selectItem(endComboBox, inputState.getEndOption());
        LocalDateTime dt = inputState.getStartDateTime();
        if (null != dt) {
            startDatePicker.setValue(dt.toLocalDate());
            LocalDateTime ed = inputState.getEndDateTime();
            if (null != ed) {
                endDatePicker.setValue(ed.toLocalDate());
                if (dt.getMinute() > 0 || ed.getMinute() > 0) {
                    minuteRadioButton.setSelected(true);
                    if (dt.getHour() > 0) {
                        startHourSpinner.increment(dt.getHour());
                    }
                    if (dt.getMinute() > 0) {
                        startMinuteSpinner.increment(dt.getMinute());
                    }
                    if (ed.getHour() > 0) {
                        endHourSpinner.increment(ed.getHour());
                    }
                    if (ed.getMinute() > 0) {
                        endMinuteSpinner.increment(ed.getMinute());
                    }
                } else if (dt.getHour() > 0 || ed.getHour() > 0) {
                    hourRadioButton.setSelected(true);
                    if (dt.getHour() > 0) {
                        startHourSpinner.increment(dt.getHour());
                    }
                    if (ed.getHour() > 0) {
                        endHourSpinner.increment(ed.getHour());
                    }
                }
            } else if (dt.getMinute() > 0) {
                minuteRadioButton.setSelected(true);
                if (dt.getHour() > 0) {
                    startHourSpinner.increment(dt.getHour());
                }
                if (dt.getMinute() > 0) {
                    startMinuteSpinner.increment(dt.getMinute());
                }
            } else if (dt.getHour() > 0) {
                hourRadioButton.setSelected(true);
                if (dt.getHour() > 0) {
                    startHourSpinner.increment(dt.getHour());
                }
            }
        } else {
            dt = inputState.getEndDateTime();
            if (null != dt) {
                endDatePicker.setValue(dt.toLocalDate());
                if (dt.getMinute() > 0) {
                    minuteRadioButton.setSelected(true);
                    if (dt.getHour() > 0) {
                        endHourSpinner.increment(dt.getHour());
                    }
                    if (dt.getMinute() > 0) {
                        endMinuteSpinner.increment(dt.getMinute());
                    }
                } else if (dt.getHour() > 0) {
                    hourRadioButton.setSelected(true);
                    if (dt.getHour() > 0) {
                        endHourSpinner.increment(dt.getHour());
                    }
                }
            }
        }

        if (null != inputState.getCustomer()) {
            customerRadioButton.setSelected(true);
            selectItem(customCustomerComboBox, inputState.getCustomer());
        } else if (null != inputState.getCity()) {
            cityRadioButton.setSelected(true);
            selectItem(cityComboBox, inputState.getCity());
        } else {
            countryRadioButton.setSelected(true);
            selectItem(countryComboBox, inputState.getCountry());
        }
        selectItem(customUserComboBox, inputState.getUser());
        selectItem(titleComboBox, inputState.getTitleOption());

    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    private void initialize() {
        assert rootBorderPane != null : "fx:id=\"rootBorderPane\" was not injected: check your FXML file 'EditAppointmentFilter.fxml'.";
        assert searchTypesTabPane != null : "fx:id=\"searchTypesTabPane\" was not injected: check your FXML file 'EditAppointmentFilter.fxml'.";
        assert preDefinedRangesTab != null : "fx:id=\"preDefinedRangesTab\" was not injected: check your FXML file 'EditAppointmentFilter.fxml'.";
        assert customerComboBox != null : "fx:id=\"customerComboBox\" was not injected: check your FXML file 'EditAppointmentFilter.fxml'.";
        assert userComboBox != null : "fx:id=\"userComboBox\" was not injected: check your FXML file 'EditAppointmentFilter.fxml'.";
        assert rangeTypeComboBox != null : "fx:id=\"rangeTypeComboBox\" was not injected: check your FXML file 'EditAppointmentFilter.fxml'.";
        assert customTab != null : "fx:id=\"customTab\" was not injected: check your FXML file 'EditAppointmentFilter.fxml'.";
        assert dateRadioButton != null : "fx:id=\"dateRadioButton\" was not injected: check your FXML file 'EditAppointmentFilter.fxml'.";
        assert dateGranularity != null : "fx:id=\"dateGranularity\" was not injected: check your FXML file 'EditAppointmentFilter.fxml'.";
        assert hourRadioButton != null : "fx:id=\"hourRadioButton\" was not injected: check your FXML file 'EditAppointmentFilter.fxml'.";
        assert minuteRadioButton != null : "fx:id=\"minuteRadioButton\" was not injected: check your FXML file 'EditAppointmentFilter.fxml'.";
        assert startDatePicker != null : "fx:id=\"startDatePicker\" was not injected: check your FXML file 'EditAppointmentFilter.fxml'.";
        assert startHourSpinner != null : "fx:id=\"startHourSpinner\" was not injected: check your FXML file 'EditAppointmentFilter.fxml'.";
        assert startMinuteSpinner != null : "fx:id=\"startMinuteSpinner\" was not injected: check your FXML file 'EditAppointmentFilter.fxml'.";
        assert endDatePicker != null : "fx:id=\"endDatePicker\" was not injected: check your FXML file 'EditAppointmentFilter.fxml'.";
        assert endHourSpinner != null : "fx:id=\"endHourSpinner\" was not injected: check your FXML file 'EditAppointmentFilter.fxml'.";
        assert endMinuteSpinner != null : "fx:id=\"endMinuteSpinner\" was not injected: check your FXML file 'EditAppointmentFilter.fxml'.";
        assert customCustomerComboBox != null : "fx:id=\"customCustomerComboBox\" was not injected: check your FXML file 'EditAppointmentFilter.fxml'.";
        assert cityComboBox != null : "fx:id=\"cityComboBox\" was not injected: check your FXML file 'EditAppointmentFilter.fxml'.";
        assert countryComboBox != null : "fx:id=\"countryComboBox\" was not injected: check your FXML file 'EditAppointmentFilter.fxml'.";
        assert customUserComboBox != null : "fx:id=\"customUserComboBox\" was not injected: check your FXML file 'EditAppointmentFilter.fxml'.";
        assert titleTextField != null : "fx:id=\"titleTextField\" was not injected: check your FXML file 'EditAppointmentFilter.fxml'.";
        assert locationTextField != null : "fx:id=\"locationTextField\" was not injected: check your FXML file 'EditAppointmentFilter.fxml'.";
        assert startComboBox != null : "fx:id=\"startComboBox\" was not injected: check your FXML file 'EditAppointmentFilter.fxml'.";
        assert endComboBox != null : "fx:id=\"endComboBox\" was not injected: check your FXML file 'EditAppointmentFilter.fxml'.";
        assert titleComboBox != null : "fx:id=\"titleComboBox\" was not injected: check your FXML file 'EditAppointmentFilter.fxml'.";
        assert locationComboBox != null : "fx:id=\"locationComboBox\" was not injected: check your FXML file 'EditAppointmentFilter.fxml'.";
        assert dateRangeErrorLabel != null : "fx:id=\"dateRangeErrorLabel\" was not injected: check your FXML file 'EditAppointmentFilter.fxml'.";
        assert customerRadioButton != null : "fx:id=\"customerRadioButton\" was not injected: check your FXML file 'EditAppointmentFilter.fxml'.";
        assert customCustomer != null : "fx:id=\"customCustomer\" was not injected: check your FXML file 'EditAppointmentFilter.fxml'.";
        assert cityRadioButton != null : "fx:id=\"cityRadioButton\" was not injected: check your FXML file 'EditAppointmentFilter.fxml'.";
        assert countryRadioButton != null : "fx:id=\"countryRadioButton\" was not injected: check your FXML file 'EditAppointmentFilter.fxml'.";
        assert okButton != null : "fx:id=\"okButton\" was not injected: check your FXML file 'EditAppointmentFilter.fxml'.";
        assert lookupOptionsBorderPane != null : "fx:id=\"lookupOptionsBorderPane\" was not injected: check your FXML file 'EditAppointmentFilter.fxml'.";
        assert lookupOptionCustomersCheckBox != null : "fx:id=\"lookupOptionCustomersCheckBox\" was not injected: check your FXML file 'EditAppointmentFilter.fxml'.";
        assert lookupOptionUsersCheckBox != null : "fx:id=\"lookupOptionUsersCheckBox\" was not injected: check your FXML file 'EditAppointmentFilter.fxml'.";

        rootBorderPane.getProperties().put(FXMLPROPERTYNAME_CONTROLLER, this);
        rootBorderPane.setVisible(false);
        collapseNode(rootBorderPane);

        rangeTypeOptionList = FXCollections.observableArrayList(new RangeSelectionItem(RangeOptionValue.CURRENT_AND_FUTURE),
                new RangeSelectionItem(RangeOptionValue.CURRENT), new RangeSelectionItem(RangeOptionValue.PAST),
                new RangeSelectionItem(RangeOptionValue.CURRENT_AND_PAST), new RangeSelectionItem(RangeOptionValue.ALL));
        customerList = FXCollections.observableArrayList();
        filteredCustomerList = FXCollections.observableArrayList();
        userList = FXCollections.observableArrayList();
        cityList = FXCollections.observableArrayList();
        filteredCityList = FXCollections.observableArrayList();
        countryList = FXCollections.observableArrayList();

        startRangeTypeOptionList = FXCollections.observableArrayList(new DateTypeSelectionItem(DateFilterType.NONE, true), new DateTypeSelectionItem(DateFilterType.ON, true),
                new DateTypeSelectionItem(DateFilterType.INCLUSIVE, true), new DateTypeSelectionItem(DateFilterType.EXCLUSIVE, true));
        endRangeTypeOptionList = FXCollections.observableArrayList(new DateTypeSelectionItem(DateFilterType.NONE, true), new DateTypeSelectionItem(DateFilterType.EXCLUSIVE, true),
                new DateTypeSelectionItem(DateFilterType.INCLUSIVE, true));
        titleTextSearchOptionList = FXCollections.observableArrayList(new TitleTextSelectionItem(TextFilterType.NONE),
                new TitleTextSelectionItem(TextFilterType.STARTS_WITH), new TitleTextSelectionItem(TextFilterType.CONTAINS),
                new TitleTextSelectionItem(TextFilterType.EQUALS), new TitleTextSelectionItem(TextFilterType.ENDS_WITH));
        locationTextSearchOptionList = FXCollections.observableArrayList(new LocationTextSelectionItem(TextFilterType.NONE),
                new LocationTextSelectionItem(TextFilterType.STARTS_WITH), new LocationTextSelectionItem(TextFilterType.CONTAINS),
                new LocationTextSelectionItem(TextFilterType.EQUALS), new LocationTextSelectionItem(TextFilterType.ENDS_WITH));

        StringConverter<Integer> zeroPadConverter = new StringConverter<Integer>() {
            @Override
            public String toString(Integer object) {
                return String.format("%02d", object);
            }

            @Override
            public Integer fromString(String string) {
                return Integer.parseInt(string);
            }
        };
        SpinnerValueFactory.IntegerSpinnerValueFactory factory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 0, 1);
        factory.setConverter(zeroPadConverter);
        startHourSpinner.setValueFactory(factory);
        endHourSpinner.setValueFactory(factory);
        factory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0, 5);
        factory.setConverter(zeroPadConverter);
        startMinuteSpinner.setValueFactory(factory);
        endMinuteSpinner.setValueFactory(factory);
        rangeTypeComboBox.setItems(rangeTypeOptionList);
        customerComboBox.setItems(customerList);
        userComboBox.setItems(userList);
        startComboBox.setItems(startRangeTypeOptionList);
        endComboBox.setItems(endRangeTypeOptionList);
        customCustomerComboBox.setItems(filteredCustomerList);
        cityComboBox.setItems(filteredCityList);
        countryComboBox.setItems(countryList);
        customUserComboBox.setItems(userList);
        titleComboBox.setItems(titleTextSearchOptionList);
        locationComboBox.setItems(locationTextSearchOptionList);

        dateRangeValidationBinding = new DateRangeValidationMessageBindingProperty();
        dateRangeValidationBinding.addListener((observable) -> {
            String message = ((StringBinding) observable).get();
            if (null == message || message.trim().isEmpty()) {
                collapseNode(dateRangeErrorLabel);
            } else {
                restoreErrorLabeled(dateRangeErrorLabel, message);
            }
        });

    }

    public FilterOptionState getFilterOptions() {
        return filterOptions;
    }

    public void show() {
        // TODO: Start InitializeTask if it hasn't already been run.
        // TODO: Implement show();
    }

    public void hide() {
        // TODO: Implement hide();
    }

    private void importCustomers(List<CustomerDAO> result) {
        CustomerSelectionItem currentCustomer = customerComboBox.getValue();
        CustomerSelectionItem currentCustomCustomer = customCustomerComboBox.getValue();
        customerList.clear();
        customerList.add(new CustomerSelectionItem(null));
        result.forEach((t) -> {
            customerList.add(new CustomerSelectionItem(t));
        });
        if (null == currentCustomer || null == currentCustomer.getValue() || !selectItem(customerComboBox, currentCustomer.getValue())) {
            selectItem(customerComboBox, null);
        }
        filteredCustomerList.clear();
        CitySelectionItem cityItem = cityComboBox.getValue();
        if (null != cityItem && null != cityItem.getValue()) {
            int cityId = cityItem.getValue().getPrimaryKey();
            filteredCustomerList.addAll(customerList.filtered((t) -> null == t.getValue() || t.getCityId() == cityId));
        } else {
            CountrySelectionItem countryItem = countryComboBox.getValue();
            if (null != countryItem && null != countryItem.getValue()) {
                int countryId = countryItem.getValue().getPrimaryKey();
                filteredCustomerList.addAll(customerList.filtered((t) -> null == t.getValue() || t.getCountryId() == countryId));
            } else {
                filteredCustomerList.addAll(customerList);
            }
        }
        if (null == currentCustomCustomer || null == currentCustomCustomer.getValue() || !selectItem(customCustomerComboBox, currentCustomCustomer.getValue())) {
            selectItem(customCustomerComboBox, null);
        }
    }

    private void importUsers(List<UserDAO> result) {
        UserSelectionItem currentUser = userComboBox.getValue();
        UserSelectionItem currentCustomUser = customUserComboBox.getValue();
        userList.clear();
        userList.add(new UserSelectionItem(null));
        result.forEach((t) -> {
            userList.add(new UserSelectionItem(t));
        });
        if (null == currentUser || null == currentUser.getValue() || !selectItem(userComboBox, currentUser.getValue())) {
            selectItem(userComboBox, null);
        }
        if (null == currentCustomUser || null == currentCustomUser.getValue() || !selectItem(customUserComboBox, currentCustomUser.getValue())) {
            selectItem(customUserComboBox, null);
        }
    }

    private class OptionItems {

        private List<CustomerDAO> customers;
        private List<UserDAO> users;
        private List<CityDAO> cities;
        private List<CountryDAO> countries;

    }

    private class ReloadCustomersAndUsersTask extends TaskWaiter<Pair<List<CustomerDAO>, List<UserDAO>>> {

        private final boolean includeInactiveCustomers;
        private final boolean includeInactiveUsers;

        public ReloadCustomersAndUsersTask(Stage stage, boolean includeInactiveCustomers, boolean includeInactiveUsers) {
            super(stage, resources.getString(RESOURCEKEY_LOADINGDATA), resources.getString(RESOURCEKEY_INITIALIZING));
            this.includeInactiveCustomers = includeInactiveCustomers;
            this.includeInactiveUsers = includeInactiveUsers;
        }

        @Override
        protected void processResult(Pair<List<CustomerDAO>, List<UserDAO>> result, Stage stage) {
            importCustomers(result.getKey());
            importUsers(result.getValue());
        }

        @Override
        protected void processException(Throwable ex, Stage stage) {
            AlertHelper.logAndAlertDbError(stage, LOG, resources.getString(RESOURCEKEY_ERRORLOADINGDATA),
                    "Error loading reloading customers and  users", ex);
        }

        @Override
        protected Pair<List<CustomerDAO>, List<UserDAO>> getResult(Connection connection) throws SQLException {
            updateMessage(AppResources.getResourceString(RESOURCEKEY_CONNECTEDTODB));
            CustomerDAO.FactoryImpl cf = CustomerDAO.getFactory();
            UserDAO.FactoryImpl uf = UserDAO.getFactory();
            return new Pair<>(
                    cf.load(connection, (this.includeInactiveCustomers) ? cf.getActiveStatusFilter(true) : cf.getAllItemsFilter()),
                    uf.load(connection, (this.includeInactiveUsers) ? uf.getAllItemsFilter() : uf.getActiveUsersFilter())
            );
        }

    }

    private final class InitializeTask extends TaskWaiter<OptionItems> {

        private final FilterOptionState inputState;

        private InitializeTask(Stage stage, FilterOptionState filter) {
            super(stage, resources.getString(RESOURCEKEY_LOADINGDATA), resources.getString(RESOURCEKEY_INITIALIZING));
            this.inputState = filter;
        }

        @Override
        protected void processResult(OptionItems result, Stage stage) {
            userList.add(new UserSelectionItem(null));
            customerList.add(new CustomerSelectionItem(null));
            filteredCustomerList.add(customerList.get(0));
            result.customers.stream().map((t) -> new CustomerSelectionItem(t)).forEach((t) -> {
                customerList.add(t);
                filteredCustomerList.add(t);
            });
            result.users.forEach((t) -> userList.add(new UserSelectionItem(t)));
            result.countries.forEach((t) -> countryList.add(new CountrySelectionItem(t)));
            result.cities.stream().map((t) -> new CitySelectionItem(t)).forEach((t) -> {
                cityList.add(t);
                filteredCityList.add(t);
            });

            if (null != inputState.getCity() || null != inputState.getCountry() || !(inputState.getTitleOption().equals(TextFilterType.NONE)
                    && inputState.getLocationOption().equals(TextFilterType.NONE))) {
                initializeCustom(inputState);
            } else {
                switch (inputState.getEndOption()) {
                    case EXCLUSIVE:
                        switch (inputState.getStartOption()) {
                            case ON:
                            case INCLUSIVE:
                            case EXCLUSIVE:
                                initializeCustom(inputState);
                                break;
                            default:
                                if (inputState.getEndDateTime().equals(LocalDateTime.now().toLocalDate().atStartOfDay())) {
                                    initializePredefined(inputState, RangeOptionValue.PAST);
                                } else {
                                    initializeCustom(inputState);
                                }
                                break;
                        }
                        break;
                    case INCLUSIVE:
                        switch (inputState.getStartOption()) {
                            case ON:
                            case INCLUSIVE:
                                initializeCustom(inputState);
                                break;
                            default:
                                if (inputState.getEndDateTime().equals(LocalDateTime.now().toLocalDate().atStartOfDay())) {
                                    initializePredefined(inputState, RangeOptionValue.CURRENT_AND_PAST);
                                } else {
                                    initializeCustom(inputState);
                                }
                                break;
                        }
                        break;
                    default:
                        switch (inputState.getStartOption()) {
                            case ON:
                                if (inputState.getStartDateTime().equals(LocalDateTime.now().toLocalDate().atStartOfDay())) {
                                    initializePredefined(inputState, RangeOptionValue.CURRENT);
                                } else {
                                    initializeCustom(inputState);
                                }
                                break;
                            case INCLUSIVE:
                                if (inputState.getStartDateTime().equals(LocalDateTime.now().toLocalDate().atStartOfDay())) {
                                    initializePredefined(inputState, RangeOptionValue.CURRENT_AND_FUTURE);
                                } else {
                                    initializeCustom(inputState);
                                }
                                break;
                            case EXCLUSIVE:
                                if (inputState.getStartDateTime().equals(LocalDateTime.now().toLocalDate().atStartOfDay())) {
                                    initializePredefined(inputState, RangeOptionValue.FUTURE);
                                } else {
                                    initializeCustom(inputState);
                                }
                                break;
                            default:
                                initializePredefined(inputState, RangeOptionValue.ALL);
                                break;
                        }
                        break;
                }
            }
        }

        @Override
        protected void processException(Throwable ex, Stage stage) {
            AlertHelper.logAndAlertDbError(stage, LOG, resources.getString(RESOURCEKEY_ERRORLOADINGDATA),
                    "Error loading appointment filter data", ex);
        }

        @Override
        protected OptionItems getResult(Connection connection) throws SQLException {
            OptionItems result = new OptionItems();
            updateMessage(AppResources.getResourceString(RESOURCEKEY_LOADINGCUSTOMERS));
            result.customers = CustomerDAO.getFactory().load(connection, CustomerDAO.getFactory().getActiveStatusFilter(true));
            updateMessage(AppResources.getResourceString(RESOURCEKEY_LOADINGUSERS));
            result.users = UserDAO.getFactory().load(connection, UserDAO.getFactory().getActiveUsersFilter());
            updateMessage(AppResources.getResourceString(RESOURCEKEY_LOADINGCITIES));
            result.cities = CityDAO.getFactory().load(connection, CityDAO.getFactory().getAllItemsFilter());
            updateMessage(AppResources.getResourceString(RESOURCEKEY_LOADINGCOUNTRIES));
            result.countries = CountryDAO.getFactory().load(connection, CountryDAO.getFactory().getAllItemsFilter());
            return result;
        }
    }

    private final class DateRangeValidationMessageBindingProperty extends StringBindingProperty {

        private final ObservableBooleanValue predefinedSelected;
        private final ObservableBooleanValue includeHour;
        private final ObservableBooleanValue includeMinute;
        private final ObservableObjectValue<DateTypeSelectionItem> startOption;
        private final ObservableObjectValue<LocalDate> pickedStart;
        private final ObservableObjectValue<Integer> startHour;
        private final ObservableObjectValue<Integer> startMinute;
        private final ObservableObjectValue<DateTypeSelectionItem> endOption;
        private final ObservableObjectValue<LocalDate> pickedEnd;
        private final ObservableObjectValue<Integer> endHour;
        private final ObservableObjectValue<Integer> endMinute;

        DateRangeValidationMessageBindingProperty() {
            super(EditAppointmentFilter.this, "dateRangeValidationMessage", preDefinedRangesTab.selectedProperty(),
                    hourRadioButton.selectedProperty(), minuteRadioButton.selectedProperty(),
                    startComboBox.getSelectionModel().selectedItemProperty(), startDatePicker.valueProperty(), startHourSpinner.valueProperty(),
                    startMinuteSpinner.valueProperty(), endComboBox.getSelectionModel().selectedItemProperty(), endDatePicker.valueProperty(),
                    endHourSpinner.valueProperty(), endMinuteSpinner.valueProperty());
            predefinedSelected = preDefinedRangesTab.selectedProperty();
            includeHour = hourRadioButton.selectedProperty();
            includeMinute = minuteRadioButton.selectedProperty();
            startOption = startComboBox.getSelectionModel().selectedItemProperty();
            pickedStart = startDatePicker.valueProperty();
            startHour = startHourSpinner.valueProperty();
            startMinute = startMinuteSpinner.valueProperty();
            endOption = endComboBox.getSelectionModel().selectedItemProperty();
            pickedEnd = endDatePicker.valueProperty();
            endHour = endHourSpinner.valueProperty();
            endMinute = endMinuteSpinner.valueProperty();
        }

        @SuppressWarnings("incomplete-switch")
        @Override
        protected String computeValue() {
            boolean includeM = includeMinute.get();
            boolean includeH = includeHour.get() || includeM;
            DateTypeSelectionItem startOpt = startOption.get();
            LocalDate startVal = pickedStart.get();
            int startH = startHour.get();
            int startM = startMinute.get();
            DateTypeSelectionItem endOpt = endOption.get();
            LocalDate endVal = pickedEnd.get();
            int endH = endHour.get();
            int endM = endMinute.get();

            if (predefinedSelected.get()) {
                return null;
            }

            if (null == startOpt || null == startVal || null == endOpt || null == endVal) {
                return "";
            }
            switch (startOpt.getValue()) {
                case EXCLUSIVE:
                    switch (endOpt.getValue()) {
                        case EXCLUSIVE:
                        case INCLUSIVE:
                            if (startVal.atTime((includeH) ? startH : 0, (includeM) ? startM : 0, 0, 0)
                                    .compareTo(endVal.atTime((includeH) ? endH : 0, (includeM) ? endM : 0, 0, 0)) >= 0) {
                                return resources.getString(RESOURCEKEY_STARTMUSTBEBEFOREEND);
                            }
                            break;
                    }
                    break;
                case INCLUSIVE:
                case ON:
                    switch (endOpt.getValue()) {
                        case EXCLUSIVE:
                            if (startVal.atTime((includeH) ? startH : 0, (includeM) ? startM : 0, 0, 0)
                                    .compareTo(endVal.atTime((includeH) ? endH : 0, (includeM) ? endM : 0, 0, 0)) >= 0) {
                                return resources.getString(RESOURCEKEY_STARTMUSTBEBEFOREEND);
                            }
                            break;
                        case INCLUSIVE:
                            if (startVal.atTime((includeH) ? startH : 0, (includeM) ? startM : 0, 0, 0)
                                    .compareTo(endVal.atTime((includeH) ? endH : 0, (includeM) ? endM : 0, 0, 0)) > 0) {
                                return resources.getString(RESOURCEKEY_STARTCANNOTBEAFTEREND);
                            }
                            break;
                    }
                    break;
            }
            return "";
        }
    }

    private enum RangeOptionValue {
        CURRENT,
        CURRENT_AND_FUTURE,
        FUTURE,
        PAST,
        CURRENT_AND_PAST,
        ALL
    }

    private final class RangeSelectionItem {

        private final ReadOnlyStringWrapper text;
        private final ReadOnlyObjectWrapper<RangeOptionValue> value;

        public RangeOptionValue getValue() {
            return value.get();
        }

        public ReadOnlyObjectProperty<RangeOptionValue> valueProperty() {
            return value.getReadOnlyProperty();
        }

        public String getText() {
            return text.get();
        }

        public ReadOnlyStringProperty textProperty() {
            return text.getReadOnlyProperty();
        }

        public RangeSelectionItem(RangeOptionValue value) {
            this.value = new ReadOnlyObjectWrapper<>(Objects.requireNonNull(value));

            switch (value) {
                case CURRENT:
                    text = new ReadOnlyStringWrapper(resources.getString(RESOURCEKEY_CURRENTAPPOINTMENTS));
                    break;
                case CURRENT_AND_FUTURE:
                    text = new ReadOnlyStringWrapper(resources.getString(RESOURCEKEY_CURRENTANDFUTUREAPPOINTMENTS));
                    break;
                case FUTURE:
                    text = new ReadOnlyStringWrapper(resources.getString(RESOURCEKEY_FUTUREAPPOINTMENTS));
                    break;
                case PAST:
                    text = new ReadOnlyStringWrapper(resources.getString(RESOURCEKEY_PASTAPPOINTMENTS));
                    break;
                case CURRENT_AND_PAST:
                    text = new ReadOnlyStringWrapper(resources.getString(RESOURCEKEY_CURRENTANDPASTAPPOINTMENTS));
                    break;
                default:
                    text = new ReadOnlyStringWrapper(resources.getString(RESOURCEKEY_ALLAPPOINTMENTS));
                    break;
            }
        }

        @Override
        public int hashCode() {
            return value.get().hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return null != obj && obj instanceof RangeSelectionItem && ((RangeSelectionItem) obj).value.get().equals(value.get());
        }

        @Override
        public String toString() {
            return text.get();
        }

    }

    private final class DateTypeSelectionItem {

        private final ReadOnlyStringWrapper text;
        private final ReadOnlyObjectWrapper<DateFilterType> value;

        public DateFilterType getValue() {
            return value.get();
        }

        public ReadOnlyObjectProperty<DateFilterType> valueProperty() {
            return value.getReadOnlyProperty();
        }

        public String getText() {
            return text.get();
        }

        public ReadOnlyStringProperty textProperty() {
            return text.getReadOnlyProperty();
        }

        public DateTypeSelectionItem(DateFilterType value, boolean isStartDate) {
            this.value = new ReadOnlyObjectWrapper<>(Objects.requireNonNull(value));

            switch (value) {
                case INCLUSIVE:
                    text = new ReadOnlyStringWrapper(resources.getString((isStartDate) ? RESOURCEKEY_OCCURSONORAFTER : RESOURCEKEY_OCCURSONORBEFORE));
                    break;
                case EXCLUSIVE:
                    text = new ReadOnlyStringWrapper(resources.getString((isStartDate) ? RESOURCEKEY_OCCURSAFTER : RESOURCEKEY_OCCURSBEFORE));
                    break;
                case ON:
                    text = new ReadOnlyStringWrapper(resources.getString(RESOURCEKEY_OCCURSON));
                    break;
                default:
                    text = new ReadOnlyStringWrapper(resources.getString(RESOURCEKEY_NONE));
                    break;
            }
        }

        @Override
        public int hashCode() {
            return value.get().hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return null != obj && obj instanceof DateTypeSelectionItem && ((DateTypeSelectionItem) obj).value.get().equals(value.get());
        }

        @Override
        public String toString() {
            return text.get();
        }

    }

    private final class TitleTextSelectionItem extends TextOption {

        private final ReadOnlyStringWrapper text;

        @Override
        public String getText() {
            return text.get();
        }

        @Override
        public ReadOnlyStringProperty textProperty() {
            return text.getReadOnlyProperty();
        }

        public TitleTextSelectionItem(TextFilterType value) {
            super(value);
            switch (value) {
                case EQUALS:
                    text = new ReadOnlyStringWrapper(resources.getString(RESOURCEKEY_TITLEISEQUALTO));
                    break;
                case STARTS_WITH:
                    text = new ReadOnlyStringWrapper(resources.getString(RESOURCEKEY_TITLESTARTSWITH));
                    break;
                case ENDS_WITH:
                    text = new ReadOnlyStringWrapper(resources.getString(RESOURCEKEY_TITLEENDSWITH));
                    break;
                case CONTAINS:
                    text = new ReadOnlyStringWrapper(resources.getString(RESOURCEKEY_TITLECONTAINS));
                    break;
                default:
                    text = new ReadOnlyStringWrapper(resources.getString(RESOURCEKEY_ANYTITLEOPTION));
                    break;
            }
        }

    }

    private final class LocationTextSelectionItem extends TextOption {

        private final ReadOnlyStringWrapper text;

        @Override
        public String getText() {
            return text.get();
        }

        @Override
        public ReadOnlyStringProperty textProperty() {
            return text.getReadOnlyProperty();
        }

        public LocationTextSelectionItem(TextFilterType value) {
            super(value);
            switch (value) {
                case EQUALS:
                    text = new ReadOnlyStringWrapper(resources.getString(RESOURCEKEY_LOCATIONISEQUALTO));
                    break;
                case STARTS_WITH:
                    text = new ReadOnlyStringWrapper(resources.getString(RESOURCEKEY_LOCATIONSTARTSWITH));
                    break;
                case ENDS_WITH:
                    text = new ReadOnlyStringWrapper(resources.getString(RESOURCEKEY_LOCATIONENDSWITH));
                    break;
                case CONTAINS:
                    text = new ReadOnlyStringWrapper(resources.getString(RESOURCEKEY_LOCATIONCONTAINS));
                    break;
                default:
                    text = new ReadOnlyStringWrapper(resources.getString(RESOURCEKEY_ANYLOCATIONOPTION));
                    break;
            }
        }

    }

    private abstract class TextOption extends SelectionItem<TextFilterType> {

        protected TextOption(TextFilterType value) {
            super(Objects.requireNonNull(value));
        }

        @Override
        public int hashCode() {
            return getValue().hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return null != obj && obj instanceof TextOption && ((TextOption) obj).getValue().equals(getValue());
        }
    }

    private final class CustomerSelectionItem extends DataObjectItem<CustomerDAO> {

        private final ReadOnlyStringWrapper text;
        private final ReadOnlyIntegerWrapper cityId;
        private final ReadOnlyIntegerWrapper countryId;

        public CustomerSelectionItem(CustomerDAO customer) {
            super(customer);
            if (null == customer) {
                text = new ReadOnlyStringWrapper(resources.getString(RESOURCEKEY_ANY));
                cityId = new ReadOnlyIntegerWrapper(-1);
                countryId = new ReadOnlyIntegerWrapper(-1);
            } else {
                text = new ReadOnlyStringWrapper(customer.getName());
                AddressRowData addr = customer.getAddress();
                cityId = new ReadOnlyIntegerWrapper(addr.getCity().getPrimaryKey());
                countryId = new ReadOnlyIntegerWrapper(addr.getCity().getCountry().getPrimaryKey());
            }
        }

        @Override
        public String getText() {
            return text.get();
        }

        @Override
        public ReadOnlyStringProperty textProperty() {
            return text.getReadOnlyProperty();
        }

        public int getCityId() {
            return cityId.get();
        }

        public ReadOnlyIntegerProperty cityIdProperty() {
            return cityId.getReadOnlyProperty();
        }

        public int getCountryId() {
            return countryId.get();
        }

        public ReadOnlyIntegerProperty countryIdProperty() {
            return countryId.getReadOnlyProperty();
        }

    }

    private final class CitySelectionItem extends DataObjectItem<CityDAO> {

        private final ReadOnlyStringWrapper text;

        @Override
        public String getText() {
            return text.get();
        }

        @Override
        public ReadOnlyStringProperty textProperty() {
            return text.getReadOnlyProperty();
        }
        private final ReadOnlyIntegerWrapper countryId;

        public int getCountryId() {
            return countryId.get();
        }

        public ReadOnlyIntegerProperty countryIdProperty() {
            return countryId.getReadOnlyProperty();
        }

        public CitySelectionItem(CityDAO city) {
            super(city);
            if (null == city) {
                text = new ReadOnlyStringWrapper(resources.getString(RESOURCEKEY_ANY));
                countryId = new ReadOnlyIntegerWrapper(-1);
            } else {
                text = new ReadOnlyStringWrapper(city.getName());
                countryId = new ReadOnlyIntegerWrapper(city.getCountry().getPrimaryKey());
            }
        }
    }

    private final class CountrySelectionItem extends DataObjectItem<CountryDAO> {

        private final ReadOnlyStringWrapper text;

        @Override
        public String getText() {
            return text.get();
        }

        @Override
        public ReadOnlyStringProperty textProperty() {
            return text.getReadOnlyProperty();
        }

        public CountrySelectionItem(CountryDAO country) {
            super(country);
            text = new ReadOnlyStringWrapper((null == country) ? resources.getString(RESOURCEKEY_ANY) : country.getName());
        }

    }

    private final class UserSelectionItem extends DataObjectItem<UserDAO> {

        private final ReadOnlyStringWrapper text;

        @Override
        public String getText() {
            return text.get();
        }

        @Override
        public ReadOnlyStringProperty textProperty() {
            return text.getReadOnlyProperty();
        }

        public UserSelectionItem(UserDAO user) {
            super(user);
            text = new ReadOnlyStringWrapper((null == user) ? resources.getString(RESOURCEKEY_ANY) : user.getUserName());
        }

    }

    private abstract class DataObjectItem<T extends DataAccessObject> extends SelectionItem<T> {

        protected DataObjectItem(T value) {
            super(value);
        }

        @Override
        public int hashCode() {
            return (null == getValue()) ? -1 : getValue().getPrimaryKey();
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean equals(Object obj) {
            if (null != obj && obj.getClass().equals(getClass())) {
                if (null == getValue()) {
                    return null == ((DataObjectItem<T>) obj).getValue();
                }
                return null != ((DataObjectItem<T>) obj).getValue() && ((DataObjectItem<T>) obj).getValue().getPrimaryKey() == getValue().getPrimaryKey();
            }
            return false;
        }

    }

    private abstract class SelectionItem<T> {

        private final ReadOnlyObjectWrapper<T> value;

        public T getValue() {
            return value.get();
        }

        public ReadOnlyObjectProperty<T> valueProperty() {
            return value.getReadOnlyProperty();
        }

        public abstract String getText();

        public abstract ReadOnlyStringProperty textProperty();

        protected SelectionItem(T value) {
            this.value = new ReadOnlyObjectWrapper<>(value);
        }

        @Override
        public String toString() {
            return getText();
        }
    }
}
