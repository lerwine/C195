package scheduler.view;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import static scheduler.AppResourceKeys.RESOURCEKEY_CHECKINGDEPENDENCIES;
import static scheduler.AppResourceKeys.RESOURCEKEY_COMPLETINGOPERATION;
import static scheduler.AppResourceKeys.RESOURCEKEY_DELETEFAILURE;
import static scheduler.AppResourceKeys.RESOURCEKEY_DELETINGRECORD;
import static scheduler.AppResourceKeys.RESOURCEKEY_ERRORDELETINGFROMDB;
import scheduler.AppResources;
import scheduler.dao.AppointmentDAO;
import scheduler.dao.DataAccessObject;
import scheduler.dao.event.DaoChangeAction;
import scheduler.dao.event.DataObjectEvent;
import scheduler.dao.event.DataObjectEventListener;
import scheduler.dao.filter.DaoFilter;
import scheduler.util.AlertHelper;
import scheduler.util.EventHelper;
import scheduler.util.ViewControllerLoader;
import static scheduler.view.MainResourceKeys.*;
import scheduler.view.address.AddressModel;
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
import scheduler.view.city.CityModel;
import scheduler.view.city.EditCity;
import scheduler.view.country.CountryModel;
import scheduler.view.country.EditCountry;
import scheduler.view.country.ManageCountries;
import scheduler.view.customer.CustomerModel;
import scheduler.view.customer.EditCustomer;
import scheduler.view.customer.ManageCustomers;
import scheduler.view.event.FxmlViewControllerEventListener;
import scheduler.view.event.FxmlViewEvent;
import scheduler.model.ui.FxRecordModel;
import scheduler.view.task.TaskWaiter;
import scheduler.view.user.EditUser;
import scheduler.view.user.ManageUsers;
import scheduler.view.user.UserModel;
import scheduler.model.db.CustomerRowData;
import scheduler.model.db.UserRowData;

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

    private static final Logger LOG = Logger.getLogger(MainController.class.getName());

    private EventHelper<DataObjectEventListener<? extends DataAccessObject>, DataObjectEvent<? extends DataAccessObject>> daoEventHelper;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    // FIXME: Menu not working
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

    private Object contentController;
    private AppointmentAlert appointmentAlert;
    private HelpContent helpContent;

    public StackPane getContentPane() {
        return contentPane;
    }

    @FXML
    void onAllAppointmentsMenuItemAction(ActionEvent event) {
        Stage stage = (Stage) contentPane.getScene().getWindow();
        try {
            ManageAppointments.loadInto(MainController.this, stage, AppointmentModel.getFactory().getAllItemsFilter());
        } catch (IOException ex) {
            ErrorDetailDialog.logShowAndWait(LOG, resources.getString(RESOURCEKEY_APPOINTMENTLOADERROR), stage, ex);
        }
    }

    @FXML
    void onManageCustomersMenuItemAction(ActionEvent event) {
        Stage stage = (Stage) contentPane.getScene().getWindow();
        try {
            ManageCustomers.loadInto(MainController.this, stage, CustomerModel.getFactory().getAllItemsFilter());
        } catch (IOException ex) {
            ErrorDetailDialog.logShowAndWait(LOG, resources.getString(RESOURCEKEY_CUSTOMERLOADERROR), stage, ex);
        }
    }

    @FXML
    void onManageUsersMenuItemAction(ActionEvent event) {
        Stage stage = (Stage) contentPane.getScene().getWindow();
        try {
            ManageUsers.loadInto(MainController.this, stage, UserModel.getFactory().getAllItemsFilter());
        } catch (IOException ex) {
            ErrorDetailDialog.logShowAndWait(LOG, resources.getString(RESOURCEKEY_USERLOADERROR), stage, ex);
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
            ErrorDetailDialog.logShowAndWait(LOG, resources.getString(RESOURCEKEY_COUNTRYLOADERROR), stage, ex);
        }
    }

    @FXML
    void onMyCurrentAndFutureAppointmentsMenuItemAction(ActionEvent event) {
        Stage stage = (Stage) contentPane.getScene().getWindow();
        try {
            ManageAppointments.loadInto(MainController.this, stage, AppointmentModelFilter.myCurrentAndFuture());
        } catch (IOException ex) {
            ErrorDetailDialog.logShowAndWait(LOG, resources.getString(RESOURCEKEY_APPOINTMENTLOADERROR), stage, ex);
        }
    }

    @FXML
    void onOverviewMenuItemAction(ActionEvent event) {
        Stage stage = (Stage) contentPane.getScene().getWindow();
        try {
            Overview.loadInto(this, stage);
        } catch (IOException ex) {
            ErrorDetailDialog.logShowAndWait(LOG, resources.getString(RESOURCEKEY_OVERVIEWLOADERROR), stage, ex);
        }
    }

    @FXML
    void onWeeklyCalendarMenuItemAction(ActionEvent event) {
        Stage stage = (Stage) contentPane.getScene().getWindow();
        try {
            ByWeek.loadInto(this, stage, LocalDate.now());
        } catch (IOException ex) {
            ErrorDetailDialog.logShowAndWait(LOG, resources.getString(RESOURCEKEY_CALENDARLOADERROR), stage, ex);
        }
    }

    @FXML
    void onMonthlyCalendarMenuItemAction(ActionEvent event) {
        Stage stage = (Stage) contentPane.getScene().getWindow();
        try {
            ByMonth.loadInto(this, stage, LocalDate.now());
        } catch (IOException ex) {
            ErrorDetailDialog.logShowAndWait(LOG, resources.getString(RESOURCEKEY_CALENDARLOADERROR), stage, ex);
        }
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
    public <T> T loadContent(Class<T> controllerClass, Object loadEventListener) throws IOException {
        Object oldController = contentController;
        return ViewControllerLoader.replacePaneContent(this, contentPane, controllerClass,
                (FxmlViewControllerEventListener<Parent, T>) (event) -> {
                    Parent childView;
                    switch (event.getType()) {
                        case LOADED:
                            event.getStage().setTitle(AppResources.getResourceString(AppResources.RESOURCEKEY_APPOINTMENTSCHEDULER));
                            break;
                        case BEFORE_SHOW:
                            childView = event.getView();
                            if (childView instanceof Region) {
                                Region r = (Region) childView;
                                r.prefWidthProperty().bind(contentPane.widthProperty());
                                r.minWidthProperty().bind(contentPane.widthProperty());
                                r.prefHeightProperty().bind(contentPane.heightProperty());
                                r.minHeightProperty().bind(contentPane.heightProperty());
                            }
                            break;
                        case SHOWN:
                            contentController = event.getController();
                            onControllerReplaced(oldController, contentController);
                            break;
                        case UNLOADED:
                            childView = event.getView();
                            if (childView instanceof Region) {
                                Region r = (Region) childView;
                                r.prefWidthProperty().unbind();
                                r.minWidthProperty().unbind();
                                r.prefHeightProperty().unbind();
                                r.minHeightProperty().unbind();
                            }
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
            appointmentAlert = ViewControllerLoader.loadViewAndController(AppointmentAlert.class).getController();
            Overview.loadInto(this, event.getStage());
            helpContent = ViewControllerLoader.loadViewAndController(HelpContent.class).getController();
        } catch (IOException ex) {
            ErrorDetailDialog.logShowAndWait(LOG, resources.getString(RESOURCEKEY_OVERVIEWLOADERROR), event.getStage(), ex);
        }
        if (null != helpContent)
            helpContent.initialize(contentPane);
        if (null != appointmentAlert)
            appointmentAlert.initialize(contentPane);
    }

    @HandlesFxmlViewEvent(FxmlViewEventHandling.UNLOADED)
    private void onUnloaded(FxmlViewEvent<? extends Parent> event) {
        ViewControllerLoader.clearPaneContent(this, contentPane);
        if (null != appointmentAlert)
            appointmentAlert.shutdown();
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

    public <T extends Node> T showHelp(String title, String fxmlResourceName, String bundleBaseName) throws IOException {
        return helpContent.show(title, fxmlResourceName, bundleBaseName);
    }
    
    public <T extends Node> T showHelp(String title, String fxmlResourceName) throws IOException {
        return helpContent.show(title, fxmlResourceName, null);
    }
    
    public <T extends Node> T showHelp(String fxmlResourceName) throws IOException {
        return helpContent.show(null, fxmlResourceName, null);
    }
    
    public <T extends Iterable<Text>> void showHelp(String title, T source) {
        helpContent.show(title, source);
    }
    
    public void showHelp(String title, Stream<Text> source) {
        helpContent.show(title, source);
    }
    
    public void showHelp(String title, Node source) {
        helpContent.show(title, source);
    }
    
    public <T extends Iterable<Text>> void showHelp(T source) {
        helpContent.show(null, source);
    }
    
    public void showHelp(Stream<Text> source) {
        helpContent.show(null, source);
    }
    
    public void showHelp(Node source) {
        helpContent.show(null, source);
    }
    
    public void hideHelp() {
        helpContent.hide();
    }
    
    /**
     * Opens an {@link EditItem} window to edit a new {@link AppointmentModel}.
     *
     * @param stage The current {@link Stage}.
     * @param customer The customer to initially select or {@code null} for no initial selection.
     * @param user The user to initially select or {@code null} for no initial selection.
     * @return The newly added {@link AppointmentModel} or {@code null} if the operation was canceled.
     */
    public AppointmentModel addNewAppointment(Stage stage, CustomerRowData customer, UserRowData user) {
        AppointmentModel result;
        try {
            result = EditAppointment.editNew(this, stage, customer, user);
        } catch (IOException ex) {
            ErrorDetailDialog.logShowAndWait(LOG, resources.getString(RESOURCEKEY_ERRORLOADINGNEWAPPOINTMENTWINDOW), stage, ex);
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
            ErrorDetailDialog.logShowAndWait(LOG, resources.getString(RESOURCEKEY_ERRORLOADINGAPPOINTMENTEDITWINDOW), stage, ex);
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
     * Opens an {@link EditItem} window to edit a new {@link CustomerModel}.
     *
     * @param stage The current {@link Stage}.
     * @return The newly added {@link CustomerModel} or {@code null} if the operation was canceled.
     */
    public CustomerModel addNewCustomer(Stage stage) {
        CustomerModel result;
        try {
            result = EditCustomer.editNew(this, stage);
        } catch (IOException ex) {
            ErrorDetailDialog.logShowAndWait(LOG, resources.getString(RESOURCEKEY_ERRORLOADINGNEWCUSTOMERWINDOW), stage, ex);
            return null;
        }
        if (null != result) {
            fireDaoEvent(this, DaoChangeAction.CREATED, result.getDataObject());
        }
        return result;
    }

    /**
     * Opens an {@link EditItem} window to edit a {@link CustomerModel}.
     *
     * @param stage The current {@link Stage}.
     * @param item The {@link CustomerModel} to be edited.
     */
    public void editCustomer(Stage stage, CustomerModel item) {
        CustomerModel result;
        try {
            result = EditCustomer.edit(item, this, stage);
        } catch (IOException ex) {
            ErrorDetailDialog.logShowAndWait(LOG, resources.getString(RESOURCEKEY_ERRORLOADINGCUSTOMEREDITWINDOW), stage, ex);
            return;
        }
        if (null != result) {
            fireDaoEvent(this, DaoChangeAction.UPDATED, result.getDataObject());
        }
    }

    /**
     * Deletes a {@link CustomerModel} item after confirming with user.
     *
     * @param stage The current {@link Stage}.
     * @param item The {@link CustomerModel} to be deleted.
     */
    public void deleteCustomer(Stage stage, CustomerModel item) {
        Optional<ButtonType> response = AlertHelper.showWarningAlert((Stage) contentPane.getScene().getWindow(), LOG,
                AppResources.getResourceString(AppResources.RESOURCEKEY_CONFIRMDELETE),
                AppResources.getResourceString(AppResources.RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO);
        if (response.isPresent() && response.get() == ButtonType.YES) {
            TaskWaiter.startNow(new DeleteTask<>(item, (Stage) contentPane.getScene().getWindow(), CustomerModel.getFactory(),
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
            ErrorDetailDialog.logShowAndWait(LOG, resources.getString(RESOURCEKEY_ERRORLOADINGCOUNTRYEDITWINDOW), stage, ex);
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
     * Opens an {@link EditItem} window to edit a {@link CityModel}.
     *
     * @param stage The current {@link Stage}.
     * @param item The {@link CityModel} to be edited.
     */
    public void openCity(Stage stage, CityModel item) {
        CityModel result;
        try {
            result = EditCity.edit(item, this, stage);
        } catch (IOException ex) {
            ErrorDetailDialog.logShowAndWait(LOG, resources.getString(RESOURCEKEY_ERRORLOADINGCITYEDITWINDOW), stage, ex);
            return;
        }
        if (null != result) {
            fireDaoEvent(this, DaoChangeAction.UPDATED, result.getDataObject());
        }
    }

    /**
     * Deletes a {@link CityModel} item after confirming with user.
     *
     * @param stage The current {@link Stage}.
     * @param item The {@link CityModel} to be deleted.
     */
    public void deleteCity(Stage stage, CityModel item) {
        Optional<ButtonType> response = AlertHelper.showWarningAlert((Stage) contentPane.getScene().getWindow(), LOG,
                AppResources.getResourceString(AppResources.RESOURCEKEY_CONFIRMDELETE),
                AppResources.getResourceString(AppResources.RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO);
        if (response.isPresent() && response.get() == ButtonType.YES) {
            TaskWaiter.startNow(new DeleteTask<>(item, (Stage) contentPane.getScene().getWindow(), CityModel.getFactory(),
                    (t) -> fireDaoEvent(this, DaoChangeAction.DELETED, t.getDataObject())));
        }
    }

    /**
     * Opens an {@link EditItem} window to edit a new {@link AddressModel}.
     *
     * @param stage The current {@link Stage}.
     * @return The newly added {@link AddressModel} or {@code null} if the operation was canceled.
     */
    public AddressModel addNewAddress(Stage stage) {
        AddressModel result;
        try {
            result = EditAddress.editNew(this, stage);
        } catch (IOException ex) {
            ErrorDetailDialog.logShowAndWait(LOG, resources.getString(RESOURCEKEY_ERRORLOADINGNEWADDRESSWINDOW), stage, ex);
            return null;
        }
        if (null != result) {
            fireDaoEvent(this, DaoChangeAction.CREATED, result.getDataObject());
        }
        return result;
    }

    /**
     * Opens an {@link EditItem} window to edit an {@link AddressModel}.
     *
     * @param stage The current {@link Stage}.
     * @param item The {@link AddressModel} to be edited.
     */
    public void editAddress(Stage stage, AddressModel item) {
        AddressModel result;
        try {
            result = EditAddress.edit(item, this, stage);
        } catch (IOException ex) {
            ErrorDetailDialog.logShowAndWait(LOG, resources.getString(RESOURCEKEY_ERRORLOADINGADDRESSEDITWINDOW), stage, ex);
            return;
        }
        if (null != result) {
            fireDaoEvent(this, DaoChangeAction.UPDATED, result.getDataObject());
        }
    }

    /**
     * Deletes an {@link AddressModel} item after confirming with user.
     *
     * @param stage The current {@link Stage}.
     * @param item The {@link AddressModel} to be deleted.
     */
    public void deleteAddress(Stage stage, AddressModel item) {
        Optional<ButtonType> response = AlertHelper.showWarningAlert((Stage) contentPane.getScene().getWindow(), LOG,
                AppResources.getResourceString(AppResources.RESOURCEKEY_CONFIRMDELETE),
                AppResources.getResourceString(AppResources.RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO);
        if (response.isPresent() && response.get() == ButtonType.YES) {
            TaskWaiter.startNow(new DeleteTask<>(item, (Stage) contentPane.getScene().getWindow(), AddressModel.getFactory(),
                    (t) -> fireDaoEvent(this, DaoChangeAction.DELETED, t.getDataObject())));
        }
    }

    /**
     * Opens an {@link EditItem} window to edit a new {@link UserModel}.
     *
     * @param stage The current {@link Stage}.
     * @return The newly added {@link UserModel} or {@code null} if the operation was canceled.
     */
    public UserModel addNewUser(Stage stage) {
        UserModel result;
        try {
            result = EditUser.editNew(this, stage);
        } catch (IOException ex) {
            ErrorDetailDialog.logShowAndWait(LOG, resources.getString(RESOURCEKEY_ERRORLOADINGNEWUSERWINDOW), stage, ex);
            return null;
        }
        if (null != result) {
            fireDaoEvent(this, DaoChangeAction.CREATED, result.getDataObject());
        }
        return result;
    }

    /**
     * Opens an {@link EditItem} window to edit a {@link UserModel}.
     *
     * @param stage The current {@link Stage}.
     * @param item The {@link UserModel} to be edited.
     */
    public void editUser(Stage stage, UserModel item) {
        UserModel result;
        try {
            result = EditUser.edit(item, this, stage);
        } catch (IOException ex) {
            ErrorDetailDialog.logShowAndWait(LOG, resources.getString(RESOURCEKEY_ERRORLOADINGUSEREDITWINDOW), stage, ex);
            return;
        }
        if (null != result) {
            fireDaoEvent(this, DaoChangeAction.UPDATED, result.getDataObject());
        }
    }

    /**
     * Deletes a {@link UserModel} item after confirming with user.
     *
     * @param stage The current {@link Stage}.
     * @param item The {@link UserModel} to be deleted.
     */
    public void deleteUser(Stage stage, UserModel item) {
        Optional<ButtonType> response = AlertHelper.showWarningAlert((Stage) contentPane.getScene().getWindow(), LOG,
                AppResources.getResourceString(AppResources.RESOURCEKEY_CONFIRMDELETE),
                AppResources.getResourceString(AppResources.RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO);
        if (response.isPresent() && response.get() == ButtonType.YES) {
            TaskWaiter.startNow(new DeleteTask<>(item, (Stage) contentPane.getScene().getWindow(), UserModel.getFactory(),
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
        // TODO: Notify AppointmentAlert
        fireDaoEvent(this, DaoChangeAction.CREATED, item.getDataObject());
    }

    private void onAppointmentUpdated(AppointmentModel item) {
        // TODO: Notify AppointmentAlert
        fireDaoEvent(this, DaoChangeAction.UPDATED, item.getDataObject());
    }

    private void onAppointmentDeleted(AppointmentModel item) {
        // TODO: Notify AppointmentAlert
        fireDaoEvent(this, DaoChangeAction.DELETED, item.getDataObject());
    }

    private class DeleteTask<D extends DataAccessObject, M extends FxRecordModel<D>> extends TaskWaiter<String> {

        private final M model;
        private final Consumer<M> onDeleted;
        private final DataAccessObject.DaoFactory<D> factory;

        DeleteTask(M model, Stage stage, FxRecordModel.ModelFactory<D, M> factory, Consumer<M> onDeleted) {
            super(stage, AppResources.getResourceString(RESOURCEKEY_DELETINGRECORD));
            this.model = model;
            this.onDeleted = onDeleted;
            this.factory = factory.getDaoFactory();
        }

        @Override
        protected void processResult(String message, Stage owner) {
            if (null != message && !message.trim().isEmpty()) {
                AlertHelper.showWarningAlert(owner, LOG, AppResources.getResourceString(RESOURCEKEY_DELETEFAILURE), message);
            } else if (null != onDeleted) {
                onDeleted.accept(model);
            }
        }

        @Override
        protected void processException(Throwable ex, Stage owner) {
            ErrorDetailDialog.logShowAndWait(LOG, AppResources.getResourceString(RESOURCEKEY_DELETEFAILURE), owner, ex,
                    AppResources.getResourceString(RESOURCEKEY_ERRORDELETINGFROMDB));
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
