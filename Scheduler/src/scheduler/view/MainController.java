package scheduler.view;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import static scheduler.AppResourceBundleConstants.RESOURCEKEY_CHECKINGDEPENDENCIES;
import static scheduler.AppResourceBundleConstants.RESOURCEKEY_COMPLETINGOPERATION;
import scheduler.AppResources;
import static scheduler.Scheduler.getCurrentUser;
import scheduler.dao.AppointmentDAO;
import scheduler.dao.AppointmentElement;
import scheduler.dao.AppointmentType;
import scheduler.dao.CustomerElement;
import scheduler.dao.DataAccessObject;
import scheduler.dao.UserDAO;
import scheduler.dao.UserElement;
import scheduler.dao.event.DaoChangeAction;
import scheduler.dao.event.DataObjectEvent;
import scheduler.dao.event.DataObjectEventListener;
import scheduler.dao.filter.AppointmentFilter;
import scheduler.dao.filter.DaoFilter;
import scheduler.util.AlertHelper;
import scheduler.util.DB;
import scheduler.util.DbConnector;
import scheduler.util.EventHelper;
import static scheduler.util.NodeUtil.setBorderedNode;
import static scheduler.util.NodeUtil.setLeftControlLabel;
import static scheduler.util.NodeUtil.setLeftLabeledControl;
import scheduler.util.ViewControllerLoader;
import static scheduler.view.MainResourceKeys.*;
import scheduler.view.address.AddressModelImpl;
import scheduler.view.address.EditAddress;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.FxmlViewEventHandling;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.annotations.HandlesFxmlViewEvent;
import scheduler.view.appointment.AppointmentModel;
import scheduler.view.appointment.AppointmentModelFilter;
import scheduler.view.appointment.ByMonth;
import scheduler.view.appointment.ByWeek;
import scheduler.view.appointment.EditAppointment;
import scheduler.view.appointment.ManageAppointments;
import scheduler.view.city.CityModelImpl;
import scheduler.view.city.EditCity;
import scheduler.view.country.CountryModel;
import scheduler.view.country.EditCountry;
import scheduler.view.country.ManageCountries;
import scheduler.view.customer.CustomerModelImpl;
import scheduler.view.customer.EditCustomer;
import scheduler.view.customer.ManageCustomers;
import scheduler.view.event.FxmlViewControllerEventListener;
import scheduler.view.event.FxmlViewEvent;
import scheduler.view.model.ItemModel;
import scheduler.view.task.TaskWaiter;
import scheduler.view.user.EditUser;
import scheduler.view.user.ManageUsers;
import scheduler.view.user.UserModelImpl;

/**
 * FXML Controller class for main application content.
 * <p>
 * This controller will remain active from the time the user is logged in until the application exits.</p>
 * <p>
 * All data object create, update and delete operations should be initiated through this controller. This allows dynamically loaded views to be
 * notified of changes, if necessary.</p>
 * <p>
 * The associated view is {@code /resources/scheduler/view/MainView.fxml}.</p>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/view/Main")
@FXMLResource("/scheduler/view/MainView.fxml")
public final class MainController {

    private static final String NODE_PROPERTYNAME_ALERT_MODEL = "scheduler.view.MainController.AppointmentAlerts.model";
    private static final String NODE_PROPERTYNAME_ALERT_TITLE = "scheduler.view.MainController.AppointmentAlerts.title";
    private static final String NODE_PROPERTYNAME_ALERT_START = "scheduler.view.MainController.AppointmentAlerts.start";
    private static final String NODE_PROPERTYNAME_ALERT_END = "scheduler.view.MainController.AppointmentAlerts.end";
    private static final String NODE_PROPERTYNAME_ALERT_TYPE = "scheduler.view.MainController.AppointmentAlerts.type";
    private static final String NODE_PROPERTYNAME_ALERT_CUSTOMER = "scheduler.view.MainController.AppointmentAlerts.customer";
    private static final String NODE_PROPERTYNAME_ALERT_LOCATION = "scheduler.view.MainController.AppointmentAlerts.location";
    private static final Logger LOG = Logger.getLogger(MainController.class.getName());

    private EventHelper<DataObjectEventListener<? extends DataAccessObject>, DataObjectEvent<? extends DataAccessObject>> daoEventHelper;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

//    @FXML // URL location of the FXML file that was given to the FXMLLoader
//    private URL location;
    @FXML // fx:id="overviewMenu"
    private Menu overviewMenu; // Value injected by FXMLLoader

    @FXML // fx:id="weeklyCalendarMenuItem"
    private MenuItem weeklyCalendarMenuItem; // Value injected by FXMLLoader

    @FXML // fx:id="monthlyCalendarMenuItem"
    private MenuItem monthlyCalendarMenuItem; // Value injected by FXMLLoader

    @FXML // fx:id="myCurrentAndFutureAppointmentsMenuItem"
    private MenuItem myCurrentAndFutureAppointmentsMenuItem; // Value injected by FXMLLoader

    @FXML // fx:id="allAppointmentsMenuItem"
    private MenuItem allAppointmentsMenuItem; // Value injected by FXMLLoader

    @FXML // fx:id="manageCustomersMenuItem"
    private MenuItem manageCustomersMenuItem; // Value injected by FXMLLoader

    @FXML // fx:id="manageUsersMenuItem"
    private MenuItem manageUsersMenuItem; // Value injected by FXMLLoader

    @FXML // fx:id="manageAddressesMenuItem"
    private MenuItem manageAddressesMenuItem; // Value injected by FXMLLoader

    @FXML // fx:id="contentPane"
    private StackPane contentPane; // Value injected by FXMLLoader

    @FXML // fx:id="appointmentAlertBorderPane"
    private BorderPane appointmentAlertBorderPane; // Value injected by FXMLLoader

    @FXML // fx:id="appointmentAlertsVBox"
    private VBox appointmentAlertsVBox; // Value injected by FXMLLoader

    private Object contentController;
    private AppointmentAlertManager appointmentAlerts;

    public StackPane getContentPane() {
        return contentPane;
    }

    @FXML
    void onAllAppointmentsMenuItemAction(ActionEvent event) {
        Stage stage = (Stage) contentPane.getScene().getWindow();
        try {
            ManageAppointments.loadInto(MainController.this, stage, AppointmentModel.getFactory().getAllItemsFilter());
        } catch (IOException ex) {
            AlertHelper.showErrorAlert(stage, LOG, resources.getString(RESOURCEKEY_APPOINTMENTLOADERROR), ex);
        }
    }

    @FXML
    void onManageCustomersMenuItemAction(ActionEvent event) {
        Stage stage = (Stage) contentPane.getScene().getWindow();
        try {
            ManageCustomers.loadInto(MainController.this, stage, CustomerModelImpl.getFactory().getAllItemsFilter());
        } catch (IOException ex) {
            AlertHelper.showErrorAlert(stage, LOG, resources.getString(RESOURCEKEY_CUSTOMERLOADERROR), ex);
        }
    }

    @FXML
    void onManageUsersMenuItemAction(ActionEvent event) {
        Stage stage = (Stage) contentPane.getScene().getWindow();
        try {
            ManageUsers.loadInto(MainController.this, stage, UserModelImpl.getFactory().getAllItemsFilter());
        } catch (IOException ex) {
            AlertHelper.showErrorAlert(stage, LOG, resources.getString(RESOURCEKEY_USERLOADERROR), ex);
        }
    }

    @FXML
    void onNewAddressMenuItem(ActionEvent event) {
        addNewAddress((Stage) contentPane.getScene().getWindow());
    }

    @FXML
    void onNewAppointmentMenuItemAction(ActionEvent event) {
        addNewAppointment((Stage) contentPane.getScene().getWindow(), null, null);
    }

    @FXML
    void onNewCustomerMenuItemAction(ActionEvent event) {
        addNewCustomer((Stage) contentPane.getScene().getWindow());
    }

    @FXML
    void onNewUserMenuItemAction(ActionEvent event) {
        addNewUser((Stage) contentPane.getScene().getWindow());
    }

    @FXML
    void onManageAddressesMenuItemAction(ActionEvent event) {
        Stage stage = (Stage) contentPane.getScene().getWindow();
        try {
            ManageCountries.loadInto(MainController.this, stage, CountryModel.getFactory().getAllItemsFilter());
        } catch (IOException ex) {
            AlertHelper.showErrorAlert(stage, LOG, resources.getString(RESOURCEKEY_COUNTRYLOADERROR), ex);
        }
    }

    @FXML
    void onMyCurrentAndFutureAppointmentsMenuItemAction(ActionEvent event) {
        Stage stage = (Stage) contentPane.getScene().getWindow();
        try {
            ManageAppointments.loadInto(MainController.this, stage, AppointmentModelFilter.myCurrentAndFuture());
        } catch (IOException ex) {
            AlertHelper.showErrorAlert(stage, LOG, resources.getString(RESOURCEKEY_APPOINTMENTLOADERROR), ex);
        }
    }

    @FXML
    void onOverviewMenuItemAction(ActionEvent event) {
        Stage stage = (Stage) contentPane.getScene().getWindow();
        try {
            Overview.loadInto(this, stage);
        } catch (IOException ex) {
            AlertHelper.showErrorAlert(stage, LOG, resources.getString(RESOURCEKEY_OVERVIEWLOADERROR), ex);
        }
    }

    @FXML
    void onWeeklyCalendarMenuItemAction(ActionEvent event) {
        Stage stage = (Stage) contentPane.getScene().getWindow();
        try {
            ByWeek.loadInto(this, stage, LocalDate.now());
        } catch (IOException ex) {
            AlertHelper.showErrorAlert(stage, LOG, resources.getString(RESOURCEKEY_CALENDARLOADERROR), ex);
        }
    }

    @FXML
    void onMonthlyCalendarMenuItemAction(ActionEvent event) {
        Stage stage = (Stage) contentPane.getScene().getWindow();
        try {
            ByMonth.loadInto(this, stage, LocalDate.now());
        } catch (IOException ex) {
            AlertHelper.showErrorAlert(stage, LOG, resources.getString(RESOURCEKEY_CALENDARLOADERROR), ex);
        }
    }

    @FXML
    void onDismissAllAppointmentAlerts(ActionEvent event) {
        appointmentAlerts.dismissAll();
    }

    @FXML
    private void initialize() {
        assert contentPane != null : "fx:id=\"contentPane\" was not injected: check your FXML file 'MainView.fxml'.";
        daoEventHelper = new EventHelper<>("onDataObjectEvent");
    }

    /**
     * Loads a view and controller for the {@link #contentPane}.
     *
     * @param <T> The type of controller that will be instantiated.
     * @param controllerClass The controller class that will be used to load the FXML view.
     * @param loadEventListener An object that can listen for FXML load events. This object can implement {@link FxmlViewControllerEventListener} or
     * use the {@link scheduler.view.annotations.HandlesFxmlViewEvent} annotation to handle view/controller life-cycle events.
     * @return The instantiated controller.
     * @throws IOException if not able to load the FXML resource.
     */
    @SuppressWarnings("incomplete-switch")
    public <T> T loadContent(Class<T> controllerClass, Object loadEventListener) throws IOException {
        Object oldController = contentController;
        return ViewControllerLoader.replacePaneContent(this, contentPane, controllerClass,
                (FxmlViewControllerEventListener<Parent, T>) (event) -> {
                    switch (event.getType()) {
                        case LOADED:
                            event.getStage().setTitle(AppResources.getResourceString(AppResources.RESOURCEKEY_APPOINTMENTSCHEDULER));
                            break;
                        case SHOWN:
                            contentController = event.getController();
                            onControllerReplaced(oldController, contentController);
                            break;
                    }
                    EventHelper.fireFxmlViewEvent(loadEventListener, event);
                });
    }

    /**
     * Loads a view and controller for the {@link #contentPane}.
     *
     * @param <T> The type of controller that will be instantiated.
     * @param controllerClass The controller class that will be used to load the FXML view.
     * @return The instantiated controller.
     * @throws IOException if not able to load the FXML resource.
     */
    public <T> T loadContent(Class<T> controllerClass) throws IOException {
        return loadContent(controllerClass, null);
    }

    @HandlesFxmlViewEvent(FxmlViewEventHandling.SHOWN)
    private void onShown(FxmlViewEvent<? extends Parent> event) {
        event.getStage().setTitle(AppResources.getResourceString(AppResources.RESOURCEKEY_APPOINTMENTSCHEDULER));
        try {
            Overview.loadInto(this, event.getStage());
        } catch (IOException ex) {
            AlertHelper.showErrorAlert(event.getStage(), LOG, resources.getString(RESOURCEKEY_OVERVIEWLOADERROR), ex);
        }
        int leadTime;
        try {
            leadTime = AppResources.getAppointmentAlertLeadTime();
        } catch (ParseException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, "Error getting alert lead time from settings", ex);
            leadTime = 5;
        }
        appointmentAlerts = new AppointmentAlertManager(leadTime);
    }

    @HandlesFxmlViewEvent(FxmlViewEventHandling.UNLOADED)
    private void onUnloaded(FxmlViewEvent<? extends Parent> event) {
        ViewControllerLoader.clearPaneContent(this, contentPane);
        if (null != appointmentAlerts) {
            appointmentAlerts.shutdown();
        }
    }

    private MenuItem getAssociatedMenuItem(Object controller) {
        if (null != controller) {
            if (controller instanceof ByWeek) {
                return weeklyCalendarMenuItem;
            }
            if (controller instanceof ByMonth) {
                return monthlyCalendarMenuItem;
            }
            if (controller instanceof ManageAppointments) {
                ManageAppointments manageAppointments = (ManageAppointments) controller;
                ModelFilter<AppointmentDAO, AppointmentModel, ? extends DaoFilter<AppointmentDAO>> filter = manageAppointments.getFilter();
                if (null != filter) {
                    DaoFilter<AppointmentDAO> daoFilter = filter.getDaoFilter();
                    if (daoFilter.equals(AppointmentModelFilter.myCurrentAndFuture().getDaoFilter())) {
                        return myCurrentAndFutureAppointmentsMenuItem;
                    }
                    if (!daoFilter.isEmpty()) {
                        return null;
                    }
                }
                return allAppointmentsMenuItem;
            }
            if (controller instanceof ManageCustomers) {
                return manageCustomersMenuItem;
            }
            if (controller instanceof ManageUsers) {
                return manageUsersMenuItem;
            }
            if (controller instanceof ManageCountries) {
                return manageAddressesMenuItem;
            }
        }
        return null;
    }

    private void onControllerReplaced(Object oldController, Object newController) {
        MenuItem menuItem;
        if (null != oldController) {
            if (oldController instanceof Overview) {
                overviewMenu.setDisable(false);
            } else if (null != (menuItem = getAssociatedMenuItem(oldController))) {
                menuItem.setDisable(false);
            }
        }
        if (null != newController) {
            if (newController instanceof Overview) {
                overviewMenu.setDisable(true);
            } else if (null != (menuItem = getAssociatedMenuItem(newController))) {
                menuItem.setDisable(true);
            }
        }
    }

    /**
     * Opens an {@link EditItem} window to edit a new {@link AppointmentModel}.
     *
     * @param stage The current {@link Stage}.
     * @param customer The customer to initially select or {@code null} for no initial selection.
     * @param user The user to initially select or {@code null} for no initial selection.
     * @return The newly added {@link AppointmentModel} or {@code null} if the operation was canceled.
     */
    public AppointmentModel addNewAppointment(Stage stage, CustomerElement customer, UserElement user) {
        AppointmentModel result;
        try {
            result = EditAppointment.editNew(this, stage, customer, user);
        } catch (IOException ex) {
            AlertHelper.showErrorAlert(stage, LOG, resources.getString(RESOURCEKEY_ERRORLOADINGNEWAPPOINTMENTWINDOW), ex);
            return null;
        }
        if (null != result) {
            onAppointmentCreated(result);
        }
        return result;
    }

    /**
     * Opens an {@link EditItem} window to edit an {@link AppointmentModel}.
     *
     * @param stage The current {@link Stage}.
     * @param item The {@link AppointmentModel} to be edited.
     */
    public void editAppointment(Stage stage, AppointmentModel item) {
        AppointmentModel result;
        try {
            result = EditAppointment.edit(item, this, stage);
        } catch (IOException ex) {
            AlertHelper.showErrorAlert(stage, LOG, resources.getString(RESOURCEKEY_ERRORLOADINGAPPOINTMENTEDITWINDOW), ex);
            return;
        }
        if (null != result) {
            onAppointmentUpdated(result);
        }
    }

    /**
     * Deletes an {@link AppointmentModel} item after confirming with user.
     *
     * @param stage The current {@link Stage}.
     * @param item The {@link AppointmentModel} to be deleted.
     */
    public void deleteAppointment(Stage stage, AppointmentModel item) {
        Optional<ButtonType> response = AlertHelper.showWarningAlert((Stage) contentPane.getScene().getWindow(), LOG,
                AppResources.getResourceString(AppResources.RESOURCEKEY_CONFIRMDELETE),
                AppResources.getResourceString(AppResources.RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO);
        if (response.isPresent() && response.get() == ButtonType.YES) {
            TaskWaiter.startNow(new DeleteTask<>(item, (Stage) contentPane.getScene().getWindow(), AppointmentModel.getFactory(),
                    this::onAppointmentDeleted));
        }
    }

    /**
     * Opens an {@link EditItem} window to edit a new {@link CustomerModelImpl}.
     *
     * @param stage The current {@link Stage}.
     * @return The newly added {@link CustomerModelImpl} or {@code null} if the operation was canceled.
     */
    public CustomerModelImpl addNewCustomer(Stage stage) {
        CustomerModelImpl result;
        try {
            result = EditCustomer.editNew(this, stage);
        } catch (IOException ex) {
            AlertHelper.showErrorAlert(stage, LOG, resources.getString(RESOURCEKEY_ERRORLOADINGNEWCUSTOMERWINDOW), ex);
            return null;
        }
        if (null != result) {
            fireDaoEvent(this, DaoChangeAction.CREATED, result.getDataObject());
        }
        return result;
    }

    /**
     * Opens an {@link EditItem} window to edit a {@link CustomerModelImpl}.
     *
     * @param stage The current {@link Stage}.
     * @param item The {@link CustomerModelImpl} to be edited.
     */
    public void editCustomer(Stage stage, CustomerModelImpl item) {
        CustomerModelImpl result;
        try {
            result = EditCustomer.edit(item, this, stage);
        } catch (IOException ex) {
            AlertHelper.showErrorAlert(stage, LOG, resources.getString(RESOURCEKEY_ERRORLOADINGCUSTOMEREDITWINDOW), ex);
            return;
        }
        if (null != result) {
            fireDaoEvent(this, DaoChangeAction.UPDATED, result.getDataObject());
        }
    }

    /**
     * Deletes a {@link CustomerModelImpl} item after confirming with user.
     *
     * @param stage The current {@link Stage}.
     * @param item The {@link CustomerModelImpl} to be deleted.
     */
    public void deleteCustomer(Stage stage, CustomerModelImpl item) {
        Optional<ButtonType> response = AlertHelper.showWarningAlert((Stage) contentPane.getScene().getWindow(), LOG,
                AppResources.getResourceString(AppResources.RESOURCEKEY_CONFIRMDELETE),
                AppResources.getResourceString(AppResources.RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO);
        if (response.isPresent() && response.get() == ButtonType.YES) {
            TaskWaiter.startNow(new DeleteTask<>(item, (Stage) contentPane.getScene().getWindow(), CustomerModelImpl.getFactory(),
                    (t) -> fireDaoEvent(this, DaoChangeAction.DELETED, t.getDataObject())));
        }
    }

    /**
     * Opens an {@link EditItem} window to edit a {@link CountryModel}.
     *
     * @param stage The current {@link Stage}.
     * @param item The {@link CountryModel} to be edited.
     */
    public void openCountry(Stage stage, CountryModel item) {
        CountryModel result;
        try {
            result = EditCountry.edit(item, this, stage);
        } catch (IOException ex) {
            AlertHelper.showErrorAlert(stage, LOG, resources.getString(RESOURCEKEY_ERRORLOADINGCOUNTRYEDITWINDOW), ex);
            return;
        }
        if (null != result) {
            fireDaoEvent(this, DaoChangeAction.UPDATED, result.getDataObject());
        }
    }

    /**
     * Deletes a {@link CountryModel} item after confirming with user.
     *
     * @param stage The current {@link Stage}.
     * @param item The {@link CountryModel} to be deleted.
     */
    public void deleteCountry(Stage stage, CountryModel item) {
        Optional<ButtonType> response = AlertHelper.showWarningAlert((Stage) contentPane.getScene().getWindow(), LOG,
                AppResources.getResourceString(AppResources.RESOURCEKEY_CONFIRMDELETE),
                AppResources.getResourceString(AppResources.RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO);
        if (response.isPresent() && response.get() == ButtonType.YES) {
            TaskWaiter.startNow(new DeleteTask<>(item, (Stage) contentPane.getScene().getWindow(), CountryModel.getFactory(),
                    (t) -> fireDaoEvent(this, DaoChangeAction.DELETED, t.getDataObject())));
        }
    }

    /**
     * Opens an {@link EditItem} window to edit a {@link CityModelImpl}.
     *
     * @param stage The current {@link Stage}.
     * @param item The {@link CityModelImpl} to be edited.
     */
    public void openCity(Stage stage, CityModelImpl item) {
        CityModelImpl result;
        try {
            result = EditCity.edit(item, this, stage);
        } catch (IOException ex) {
            AlertHelper.showErrorAlert(stage, LOG, resources.getString(RESOURCEKEY_ERRORLOADINGCITYEDITWINDOW), ex);
            return;
        }
        if (null != result) {
            fireDaoEvent(this, DaoChangeAction.UPDATED, result.getDataObject());
        }
    }

    /**
     * Deletes a {@link CityModelImpl} item after confirming with user.
     *
     * @param stage The current {@link Stage}.
     * @param item The {@link CityModelImpl} to be deleted.
     */
    public void deleteCity(Stage stage, CityModelImpl item) {
        Optional<ButtonType> response = AlertHelper.showWarningAlert((Stage) contentPane.getScene().getWindow(), LOG,
                AppResources.getResourceString(AppResources.RESOURCEKEY_CONFIRMDELETE),
                AppResources.getResourceString(AppResources.RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO);
        if (response.isPresent() && response.get() == ButtonType.YES) {
            TaskWaiter.startNow(new DeleteTask<>(item, (Stage) contentPane.getScene().getWindow(), CityModelImpl.getFactory(),
                    (t) -> fireDaoEvent(this, DaoChangeAction.DELETED, t.getDataObject())));
        }
    }

    /**
     * Opens an {@link EditItem} window to edit a new {@link AddressModelImpl}.
     *
     * @param stage The current {@link Stage}.
     * @return The newly added {@link AddressModelImpl} or {@code null} if the operation was canceled.
     */
    public AddressModelImpl addNewAddress(Stage stage) {
        AddressModelImpl result;
        try {
            result = EditAddress.editNew(this, stage);
        } catch (IOException ex) {
            AlertHelper.showErrorAlert(stage, LOG, resources.getString(RESOURCEKEY_ERRORLOADINGNEWADDRESSWINDOW), ex);
            return null;
        }
        if (null != result) {
            fireDaoEvent(this, DaoChangeAction.CREATED, result.getDataObject());
        }
        return result;
    }

    /**
     * Opens an {@link EditItem} window to edit an {@link AddressModelImpl}.
     *
     * @param stage The current {@link Stage}.
     * @param item The {@link AddressModelImpl} to be edited.
     */
    public void editAddress(Stage stage, AddressModelImpl item) {
        AddressModelImpl result;
        try {
            result = EditAddress.edit(item, this, stage);
        } catch (IOException ex) {
            AlertHelper.showErrorAlert(stage, LOG, resources.getString(RESOURCEKEY_ERRORLOADINGADDRESSEDITWINDOW), ex);
            return;
        }
        if (null != result) {
            fireDaoEvent(this, DaoChangeAction.UPDATED, result.getDataObject());
        }
    }

    /**
     * Deletes an {@link AddressModelImpl} item after confirming with user.
     *
     * @param stage The current {@link Stage}.
     * @param item The {@link AddressModelImpl} to be deleted.
     */
    public void deleteAddress(Stage stage, AddressModelImpl item) {
        Optional<ButtonType> response = AlertHelper.showWarningAlert((Stage) contentPane.getScene().getWindow(), LOG,
                AppResources.getResourceString(AppResources.RESOURCEKEY_CONFIRMDELETE),
                AppResources.getResourceString(AppResources.RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO);
        if (response.isPresent() && response.get() == ButtonType.YES) {
            TaskWaiter.startNow(new DeleteTask<>(item, (Stage) contentPane.getScene().getWindow(), AddressModelImpl.getFactory(),
                    (t) -> fireDaoEvent(this, DaoChangeAction.DELETED, t.getDataObject())));
        }
    }

    /**
     * Opens an {@link EditItem} window to edit a new {@link UserModelImpl}.
     *
     * @param stage The current {@link Stage}.
     * @return The newly added {@link UserModelImpl} or {@code null} if the operation was canceled.
     */
    public UserModelImpl addNewUser(Stage stage) {
        UserModelImpl result;
        try {
            result = EditUser.editNew(this, stage);
        } catch (IOException ex) {
            AlertHelper.showErrorAlert(stage, LOG, resources.getString(RESOURCEKEY_ERRORLOADINGNEWUSERWINDOW), ex);
            return null;
        }
        if (null != result) {
            fireDaoEvent(this, DaoChangeAction.CREATED, result.getDataObject());
        }
        return result;
    }

    /**
     * Opens an {@link EditItem} window to edit a {@link UserModelImpl}.
     *
     * @param stage The current {@link Stage}.
     * @param item The {@link UserModelImpl} to be edited.
     */
    public void editUser(Stage stage, UserModelImpl item) {
        UserModelImpl result;
        try {
            result = EditUser.edit(item, this, stage);
        } catch (IOException ex) {
            AlertHelper.showErrorAlert(stage, LOG, resources.getString(RESOURCEKEY_ERRORLOADINGUSEREDITWINDOW), ex);
            return;
        }
        if (null != result) {
            fireDaoEvent(this, DaoChangeAction.UPDATED, result.getDataObject());
        }
    }

    /**
     * Deletes a {@link UserModelImpl} item after confirming with user.
     *
     * @param stage The current {@link Stage}.
     * @param item The {@link UserModelImpl} to be deleted.
     */
    public void deleteUser(Stage stage, UserModelImpl item) {
        Optional<ButtonType> response = AlertHelper.showWarningAlert((Stage) contentPane.getScene().getWindow(), LOG,
                AppResources.getResourceString(AppResources.RESOURCEKEY_CONFIRMDELETE),
                AppResources.getResourceString(AppResources.RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO);
        if (response.isPresent() && response.get() == ButtonType.YES) {
            TaskWaiter.startNow(new DeleteTask<>(item, (Stage) contentPane.getScene().getWindow(), UserModelImpl.getFactory(),
                    (t) -> fireDaoEvent(this, DaoChangeAction.DELETED, t.getDataObject())));
        }
    }

    public void addDaoEventListener(DataObjectEventListener<? extends DataAccessObject> listener) {
        daoEventHelper.addListener(listener);
    }

    public void removeDaoEventListener(DataObjectEventListener<? extends DataAccessObject> listener) {
        daoEventHelper.removeListener(listener);
    }

    public <T extends DataAccessObject> void fireDaoEvent(Object source, DaoChangeAction action, T dao) {
        DataObjectEvent<T> event = new DataObjectEvent<>(source, action, dao);
        EventHelper.fireDataObjectEvent(contentController, event);
        daoEventHelper.raiseEvent(event);
    }

    private void onAppointmentCreated(AppointmentModel item) {
        appointmentAlerts.onAppointmentUpdate(item);
        fireDaoEvent(this, DaoChangeAction.CREATED, item.getDataObject());
    }

    private void onAppointmentUpdated(AppointmentModel item) {
        appointmentAlerts.onAppointmentUpdate(item);
        fireDaoEvent(this, DaoChangeAction.UPDATED, item.getDataObject());
    }

    private void onAppointmentDeleted(AppointmentModel item) {
        appointmentAlerts.onAppointmentDeleted(item);
        fireDaoEvent(this, DaoChangeAction.DELETED, item.getDataObject());
    }

    private class AppointmentAlertManager {

        private Timer appointmentCheckTimer;
        private final List<Integer> dismissed;
        private final int alertLeadtime;
        private final DateTimeFormatter formatter;

        AppointmentAlertManager(int alertLeadtime) {
            this.alertLeadtime = alertLeadtime;
            dismissed = Collections.synchronizedList(new ArrayList<>());
            appointmentCheckTimer = new Timer();
            appointmentCheckTimer.schedule(new CheckAppointmentsTask(alertLeadtime), 0, 120000);
            formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);
        }

        private FlowPane createNew(AppointmentModel model) {
            FlowPane view = setBorderedNode(new FlowPane());
            view.setPadding(new Insets(8));
            ObservableList<Node> rootChildren = view.getChildren();
            ObservableMap<Object, Object> properties = view.getProperties();
            properties.put(NODE_PROPERTYNAME_ALERT_MODEL, model);
            HBox hBox = new HBox();
            rootChildren.add(hBox);
            ObservableList<Node> children = hBox.getChildren();
            children.add(setLeftControlLabel(new Label(), resources.getString(RESOURCEKEY_TITLE)));
            Label label = setLeftLabeledControl(new Label(), true);
            label.textProperty().bind(model.titleProperty());
            children.add(label);
            properties.put(NODE_PROPERTYNAME_ALERT_TITLE, label.textProperty());

            hBox = new HBox();
            rootChildren.add(hBox);
            children = hBox.getChildren();
            label = setLeftControlLabel(new Label(), resources.getString(RESOURCEKEY_START));
            label.setPadding(new Insets(0, 0, 0, 8));
            children.add(label);
            label = setLeftLabeledControl(new Label(), true);
            label.textProperty().bind(Bindings.createStringBinding(() -> {
                LocalDateTime d = model.getStart();
                return (null == d) ? "" : formatter.format(d);
            }, model.startProperty()));
            children.add(label);
            properties.put(NODE_PROPERTYNAME_ALERT_START, label.textProperty());

            hBox = new HBox();
            rootChildren.add(hBox);
            children = hBox.getChildren();
            label = setLeftControlLabel(new Label(), resources.getString(RESOURCEKEY_END));
            label.setPadding(new Insets(0, 0, 0, 8));
            children.add(label);
            label = setLeftLabeledControl(new Label(), true);
            label.textProperty().bind(Bindings.createStringBinding(() -> {
                LocalDateTime d = model.getEnd();
                return (null == d) ? "" : formatter.format(d);
            }, model.endProperty()));
            children.add(label);
            properties.put(NODE_PROPERTYNAME_ALERT_END, label.textProperty());

            hBox = new HBox();
            rootChildren.add(hBox);
            children = hBox.getChildren();
            label = setLeftControlLabel(new Label(), resources.getString(RESOURCEKEY_TYPE));
            label.setPadding(new Insets(0, 0, 0, 8));
            children.add(label);
            label = setLeftLabeledControl(new Label(), true);
            label.textProperty().bind(Bindings.createStringBinding(() -> AppointmentType.toDisplayText(model.getType()), model.typeProperty()));
            children.add(label);
            properties.put(NODE_PROPERTYNAME_ALERT_TYPE, label.textProperty());

            hBox = new HBox();
            rootChildren.add(hBox);
            children = hBox.getChildren();
            label = setLeftControlLabel(new Label(), resources.getString(RESOURCEKEY_CUSTOMER));
            label.setPadding(new Insets(0, 0, 0, 8));
            children.add(label);
            label = setLeftLabeledControl(new Label(), true);
            label.textProperty().bind(model.customerNameProperty());
            children.add(label);
            properties.put(NODE_PROPERTYNAME_ALERT_CUSTOMER, label.textProperty());

            hBox = new HBox();
            rootChildren.add(hBox);
            children = hBox.getChildren();
            label = setLeftControlLabel(new Label(), resources.getString(RESOURCEKEY_LOCATION));
            label.setPadding(new Insets(0, 0, 0, 8));
            children.add(label);
            label = setLeftLabeledControl(new Label(), true);
            label.textProperty().bind(model.effectiveLocationProperty());
            children.add(label);
            properties.put(NODE_PROPERTYNAME_ALERT_LOCATION, label.textProperty());

            Button button = new Button();
            button.setPadding(new Insets(0, 0, 0, 8));
            rootChildren.add(button);
            button.setText(AppResources.getResourceString(RESOURCEKEY_DISMISS));
            button.setOnAction((event) -> dismiss((FlowPane) ((Button) event.getSource()).getParent()));
            return view;
        }

        private void reBind(FlowPane view, AppointmentModel model) {
            StringProperty stringProperty;
            ObservableMap<Object, Object> properties = view.getProperties();
            if (null == model) {
                properties.remove(NODE_PROPERTYNAME_ALERT_MODEL);
                ((StringProperty) properties.get(NODE_PROPERTYNAME_ALERT_TITLE)).unbind();
                properties.remove(NODE_PROPERTYNAME_ALERT_TITLE);
                ((StringProperty) properties.get(NODE_PROPERTYNAME_ALERT_START)).unbind();
                properties.remove(NODE_PROPERTYNAME_ALERT_START);
                ((StringProperty) properties.get(NODE_PROPERTYNAME_ALERT_END)).unbind();
                properties.remove(NODE_PROPERTYNAME_ALERT_END);
                ((StringProperty) properties.get(NODE_PROPERTYNAME_ALERT_TYPE)).unbind();
                properties.remove(NODE_PROPERTYNAME_ALERT_TYPE);
                ((StringProperty) properties.get(NODE_PROPERTYNAME_ALERT_CUSTOMER)).unbind();
                properties.remove(NODE_PROPERTYNAME_ALERT_CUSTOMER);
                ((StringProperty) properties.get(NODE_PROPERTYNAME_ALERT_LOCATION)).unbind();
                properties.remove(NODE_PROPERTYNAME_ALERT_LOCATION);
            } else {
                properties.put(NODE_PROPERTYNAME_ALERT_MODEL, model);
                stringProperty = (StringProperty) properties.get(NODE_PROPERTYNAME_ALERT_TITLE);
                stringProperty.unbind();
                stringProperty.bind(model.titleProperty());

                stringProperty = (StringProperty) properties.get(NODE_PROPERTYNAME_ALERT_START);
                stringProperty.unbind();
                stringProperty.bind(Bindings.createStringBinding(() -> {
                    LocalDateTime d = model.getStart();
                    return (null == d) ? "" : formatter.format(d);
                }, model.startProperty()));

                stringProperty = (StringProperty) properties.get(NODE_PROPERTYNAME_ALERT_END);
                stringProperty.unbind();
                stringProperty.bind(Bindings.createStringBinding(() -> {
                    LocalDateTime d = model.getEnd();
                    return (null == d) ? "" : formatter.format(d);
                }, model.endProperty()));

                stringProperty = (StringProperty) properties.get(NODE_PROPERTYNAME_ALERT_TYPE);
                stringProperty.unbind();
                stringProperty.bind(Bindings.createStringBinding(() -> AppointmentType.toDisplayText(model.getType()), model.typeProperty()));

                stringProperty = (StringProperty) properties.get(NODE_PROPERTYNAME_ALERT_CUSTOMER);
                stringProperty.unbind();
                stringProperty.bind(model.customerNameProperty());

                stringProperty = (StringProperty) properties.get(NODE_PROPERTYNAME_ALERT_LOCATION);
                stringProperty.unbind();
                stringProperty.bind(model.effectiveLocationProperty());
            }
        }

        private synchronized void dismissAll() {
            ObservableList<Node> itemsViewList = appointmentAlertsVBox.getChildren();
            if (!itemsViewList.isEmpty()) {
                itemsViewList.forEach((t) -> {
                    FlowPane f = (FlowPane) t;
                    ObservableList<Node> children = f.getChildren();
                    ObservableMap<Object, Object> properties = f.getProperties();
                    dismissed.add(((AppointmentModel) properties.get(NODE_PROPERTYNAME_ALERT_MODEL)).getPrimaryKey());
                    reBind(f, null);
                    children.clear();
                });
                itemsViewList.clear();
                appointmentAlertBorderPane.setVisible(false);
            }
        }

        private synchronized void dismiss(FlowPane flowPane) {
            ObservableList<Node> itemsViewList = appointmentAlertsVBox.getChildren();
            dismissed.add(((AppointmentModel) flowPane.getProperties().get(NODE_PROPERTYNAME_ALERT_MODEL)).getPrimaryKey());
            reBind(flowPane, null);
            itemsViewList.remove(flowPane);
            if (itemsViewList.isEmpty()) {
                appointmentAlertBorderPane.setVisible(false);
            }
        }

        private FlowPane getViewNode(int key) {
            Optional<Node> result = appointmentAlertsVBox.getChildren().stream().filter((t)
                    -> ((AppointmentModel) t.getProperties().get(NODE_PROPERTYNAME_ALERT_MODEL)).getPrimaryKey() == key).findFirst();
            return (FlowPane) result.orElse((Node) null);
        }

        private void shutdown() {
            appointmentCheckTimer.cancel();
        }

        private synchronized void onCheckAppointmentsTaskError(Throwable ex) {
            appointmentCheckTimer.cancel();
            try {
                Alert alert = new Alert(Alert.AlertType.ERROR, AppResources.getResourceString(AppResources.RESOURCEKEY_UNEXPECTEDERRORDETAILS));
                alert.initModality(Modality.APPLICATION_MODAL);
                alert.setTitle(resources.getString(RESOURCEKEY_APPOINTMENTLOADERROR));
                try {
                    alert.getDialogPane().setExpandableContent(ErrorDialogDetailController.load(ex, "Error checking impending appointments"));
                } catch (IOException err) {
                    LOG.log(Level.SEVERE, "Error loading exception detail", err);
                }
                alert.showAndWait();
            } finally {
                appointmentCheckTimer = new Timer();
                appointmentCheckTimer.schedule(new CheckAppointmentsTask(alertLeadtime), 0, 120000);
            }
        }

        private synchronized void onPeriodicCheckFinished(List<AppointmentDAO> appointments) {
            ObservableList<Node> itemsViewList = appointmentAlertsVBox.getChildren();
            if (appointments.isEmpty()) {
                itemsViewList.forEach((t) -> {
                    reBind((FlowPane) t, null);
                });
                itemsViewList.clear();
            } else {
                ArrayList<Integer> d = new ArrayList<>();
                dismissed.forEach((i) -> d.add(i));
                dismissed.clear();
                appointments.stream().sorted(AppointmentElement::compareByDates).forEach(new Consumer<AppointmentDAO>() {
                    int index = -1;

                    @Override
                    public void accept(AppointmentDAO t) {
                        int pk = t.getPrimaryKey();
                        if (d.contains(pk)) {
                            dismissed.add(pk);
                        } else if (++index < itemsViewList.size()) {
                            reBind((FlowPane) itemsViewList.get(index), new AppointmentModel(t));
                        } else {
                            itemsViewList.add(createNew(new AppointmentModel(t)));
                        }
                    }
                });
                int e = appointments.size() - dismissed.size();
                while (itemsViewList.size() > e) {
                    reBind((FlowPane) itemsViewList.get(e), null);
                    itemsViewList.remove(e);
                }
            }
            appointmentAlertBorderPane.setVisible(!itemsViewList.isEmpty());
        }

        private synchronized void onAppointmentUpdate(AppointmentModel item) {
            int key = item.getPrimaryKey();
            ObservableList<Node> itemsViewList = appointmentAlertsVBox.getChildren();
            LocalDateTime start = LocalDateTime.now();
            FlowPane view = getViewNode(key);
            if (start.compareTo(item.getEnd()) < 0) {
                LocalDateTime end = start.plusMinutes(alertLeadtime);
                if (end.compareTo(item.getStart()) >= 0) {
                    if (dismissed.contains(key)) {
                        return;
                    }
                    Stream<AppointmentModel> stream = itemsViewList.stream().map((t) -> (AppointmentModel) t.getProperties().get(NODE_PROPERTYNAME_ALERT_MODEL));
                    if (null != view) {
                        stream = stream.filter((t) -> t.getPrimaryKey() != key);
                    }
                    Stream.concat(stream, Stream.of(item)).sorted(AppointmentModel::compareByDates).forEach(new Consumer<AppointmentModel>() {
                        int index = -1;

                        @Override
                        public void accept(AppointmentModel t) {
                            if (++index < itemsViewList.size()) {
                                reBind((FlowPane) itemsViewList.get(index), t);
                            } else {
                                itemsViewList.add(createNew(t));
                            }
                        }
                    });
                    return;
                }
            }
            if (dismissed.contains(key)) {
                dismissed.remove(key);
            } else if (null != view) {
                reBind(view, null);
                itemsViewList.remove(view);
                if (itemsViewList.isEmpty()) {
                    appointmentAlertBorderPane.setVisible(false);
                }
            }
        }

        private synchronized void onAppointmentDeleted(AppointmentModel item) {
            int pk = item.getPrimaryKey();
            if (dismissed.contains(pk)) {
                dismissed.remove(pk);
            } else {
                FlowPane view = getViewNode(pk);
                if (null != view) {
                    ObservableList<Node> itemsViewList = appointmentAlertsVBox.getChildren();
                    reBind(view, null);
                    itemsViewList.remove(view);
                    if (itemsViewList.isEmpty()) {
                        appointmentAlertBorderPane.setVisible(false);
                    }
                }
            }
        }
    }

    private class CheckAppointmentsTask extends TimerTask {

        private final UserDAO user;
        private final AppointmentDAO.FactoryImpl factory;
        private final int alertLeadTime;

        private CheckAppointmentsTask(int alertLeadTime) {
            this.alertLeadTime = alertLeadTime;
            user = Objects.requireNonNull(getCurrentUser());
            factory = AppointmentDAO.getFactory();
        }

        @Override
        public void run() {
            List<AppointmentDAO> appointments;
            try {
                appointments = DbConnector.apply((t) -> {
                    LocalDateTime start = LocalDateTime.now();
                    return factory.load(t, AppointmentFilter.of(AppointmentFilter.expressionOf(DB.toUtcTimestamp(start),
                            DB.toUtcTimestamp(start.plusMinutes(alertLeadTime))).and(AppointmentFilter.expressionOf(user))));
                });
            } catch (SQLException | ClassNotFoundException ex) {
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, "Error checking impending appointments", ex);
                Platform.runLater(() -> appointmentAlerts.onCheckAppointmentsTaskError(ex));
                return;
            }
            Platform.runLater(() -> appointmentAlerts.onPeriodicCheckFinished(appointments));
        }
    }

    private class DeleteTask<D extends DataAccessObject, M extends ItemModel<D>> extends TaskWaiter<String> {

        private final M model;
        private final Consumer<M> onDeleted;
        private final DataAccessObject.DaoFactory<D> factory;

        DeleteTask(M model, Stage stage, ItemModel.ModelFactory<D, M> factory, Consumer<M> onDeleted) {
            super(stage, AppResources.getResourceString(AppResources.RESOURCEKEY_DELETINGRECORD));
            this.model = model;
            this.onDeleted = onDeleted;
            this.factory = factory.getDaoFactory();
        }

        @Override
        protected void processResult(String message, Stage owner) {
            if (null != message && !message.trim().isEmpty()) {
                AlertHelper.showWarningAlert(owner, LOG, AppResources.getResourceString(AppResources.RESOURCEKEY_DELETEFAILURE), message);
            } else if (null != onDeleted) {
                onDeleted.accept(model);
            }
        }

        @Override
        protected void processException(Throwable ex, Stage owner) {
            LOG.log(Level.SEVERE, "Error deleting record", ex);
            AlertHelper.showErrorAlert(owner, LOG, AppResources.getResourceString(AppResources.RESOURCEKEY_DELETEFAILURE),
                    AppResources.getResourceString(AppResources.RESOURCEKEY_ERRORDELETINGFROMDB), ex);
        }

        @Override
        protected String getResult(Connection connection) throws SQLException {
            updateMessage(AppResources.getResourceString(RESOURCEKEY_CHECKINGDEPENDENCIES));
            String message = factory.getDeleteDependencyMessage(model.getDataObject(), connection);
            if (null != message && !message.trim().isEmpty()) {
                return message;
            }
            updateMessage(AppResources.getResourceString(RESOURCEKEY_COMPLETINGOPERATION));
            factory.delete(model.getDataObject(), connection);
            return null;
        }
    }

}
