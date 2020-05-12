package scheduler.view;

import com.sun.javafx.event.EventHandlerManager;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import scheduler.AppResourceKeys;
import static scheduler.AppResourceKeys.RESOURCEKEY_CHECKINGDEPENDENCIES;
import static scheduler.AppResourceKeys.RESOURCEKEY_COMPLETINGOPERATION;
import static scheduler.AppResourceKeys.RESOURCEKEY_DELETEFAILURE;
import static scheduler.AppResourceKeys.RESOURCEKEY_DELETINGRECORD;
import static scheduler.AppResourceKeys.RESOURCEKEY_ERRORDELETINGFROMDB;
import scheduler.AppResources;
import scheduler.Scheduler;
import scheduler.dao.AddressDAO;
import scheduler.dao.AppointmentDAO;
import scheduler.dao.CityDAO;
import scheduler.dao.CountryDAO;
import scheduler.dao.CustomerDAO;
import scheduler.dao.DataAccessObject;
import scheduler.dao.UserDAO;
import scheduler.dao.event.AddressDaoEvent;
import scheduler.dao.event.AppointmentDaoEvent;
import scheduler.dao.event.CityDaoEvent;
import scheduler.dao.event.CountryDaoEvent;
import scheduler.dao.event.CustomerDaoEvent;
import scheduler.dao.event.DaoChangeAction;
import scheduler.dao.event.DataObjectEvent;
import scheduler.dao.event.DataObjectEventListener;
import scheduler.dao.event.UserDaoEvent;
import scheduler.dao.filter.DaoFilter;
import scheduler.fx.AppointmentAlert;
import scheduler.fx.HelpContent;
import scheduler.model.db.CustomerRowData;
import scheduler.model.db.UserRowData;
import scheduler.model.ui.AddressModel;
import scheduler.model.ui.FxRecordModel;
import scheduler.util.AlertHelper;
import scheduler.util.EventHelper;
import static scheduler.util.NodeUtil.bindExtents;
import static scheduler.view.MainResourceKeys.*;
import scheduler.view.address.EditAddress;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
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
import scheduler.view.report.AppointmentTypesByMonth;
import scheduler.view.report.AppointmentsByRegion;
import scheduler.view.report.ConsultantSchedule;
import scheduler.view.task.TaskWaiter;
import scheduler.view.task.WaitBorderPane;
import scheduler.view.user.EditUser;
import scheduler.view.user.ManageUsers;
import scheduler.view.user.UserModel;

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
public final class MainController implements EventTarget {

    private static final Logger LOG = Logger.getLogger(MainController.class.getName());

    public static void startBusyTaskNow(Task<?> task) {
        Scheduler.getMainController().waitBorderPane.startNow(task);
    }

    public static void scheduleBusyTask(Task<?> task, long delay, TimeUnit unit) {
        Scheduler.getMainController().waitBorderPane.schedule(task, delay, unit);
    }

    private Node contentView;

    private EventHelper<DataObjectEventListener<? extends DataAccessObject>, DataObjectEvent<? extends DataAccessObject>> daoEventHelper;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // fx:id="rootStackPane"
    private StackPane rootStackPane; // Value injected by FXMLLoader

    @FXML // fx:id="contentVBox"
    private VBox contentVBox; // Value injected by FXMLLoader

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

    @FXML // fx:id="typesByMonthMenuItem"
    private MenuItem typesByMonthMenuItem; // Value injected by FXMLLoader

    @FXML // fx:id="consultantScheduleMenuItem"
    private MenuItem consultantScheduleMenuItem; // Value injected by FXMLLoader

    @FXML // fx:id="byRegionMenuItem"
    private MenuItem byRegionMenuItem; // Value injected by FXMLLoader

    @FXML // fx:id="manageCustomersMenuItem"
    private MenuItem manageCustomersMenuItem; // Value injected by FXMLLoader

    @FXML // fx:id="manageUsersMenuItem"
    private MenuItem manageUsersMenuItem; // Value injected by FXMLLoader

    @FXML // fx:id="manageAddressesMenuItem"
    private MenuItem manageAddressesMenuItem; // Value injected by FXMLLoader

    @FXML // fx:id="helpContent"
    private HelpContent helpContent; // Value injected by FXMLLoader

    @FXML // fx:id="waitBorderPane"
    private WaitBorderPane waitBorderPane; // Value injected by FXMLLoader

    @FXML // fx:id="appointmentAlert"
    private AppointmentAlert appointmentAlert; // Value injected by FXMLLoader

    private final EventHandlerManager eventHandlerManager;

    public MainController() {
        this.eventHandlerManager = new EventHandlerManager(this);
    }

    @FXML
    void onAllAppointmentsMenuItemAction(ActionEvent event) {
        ManageAppointments.loadIntoMainContent(AppointmentModel.getFactory().getAllItemsFilter());
    }

    @FXML
    void onByRegionMenuItemAction(ActionEvent event) {
        replaceContent(new AppointmentsByRegion());
    }

    @FXML
    void onConsultantScheduleMenuItemAction(ActionEvent event) {
        replaceContent(new ConsultantSchedule());
    }

    @FXML
    void onTypesByMonthMenuItemAction(ActionEvent event) {
        replaceContent(new AppointmentTypesByMonth());
    }

    @FXML
    void onManageCustomersMenuItemAction(ActionEvent event) {
        ManageCustomers.loadIntoMainContent(CustomerModel.getFactory().getDefaultFilter());
    }

    @FXML
    void onManageUsersMenuItemAction(ActionEvent event) {
        ManageUsers.loadIntoMainContent(UserModel.getFactory().getDefaultFilter());
    }

    @FXML
    void onNewAddressMenuItem(ActionEvent event) {
        addNewAddress((Stage) contentVBox.getScene().getWindow());
    }

    @FXML
    void onNewAppointmentMenuItemAction(ActionEvent event) {
        addNewAppointment(null, null);
    }

    @FXML
    void onNewCustomerMenuItemAction(ActionEvent event) {
        addNewCustomer((Stage) contentVBox.getScene().getWindow());
    }

    @FXML
    void onNewUserMenuItemAction(ActionEvent event) {
        addNewUser((Stage) contentVBox.getScene().getWindow());
    }

    @FXML
    void onManageAddressesMenuItemAction(ActionEvent event) {
        ManageCountries.loadIntoMainContent();
    }

    @FXML
    void onMyCurrentAndFutureAppointmentsMenuItemAction(ActionEvent event) {
        ManageAppointments.loadIntoMainContent(AppointmentModelFilter.myCurrentAndFuture());
    }

    @FXML
    void onOverviewMenuItemAction(ActionEvent event) {
        replaceContent(new Overview());
    }

    @FXML
    void onWeeklyCalendarMenuItemAction(ActionEvent event) {
        ByWeek.loadIntoMainContent(LocalDate.now());
    }

    @FXML
    void onMonthlyCalendarMenuItemAction(ActionEvent event) {
        ByMonth.loadIntoMainContent(LocalDate.now());
    }

    @FXML
    private void initialize() {
        assert rootStackPane != null : "fx:id=\"rootStackPane\" was not injected: check your FXML file 'MainView.fxml'.";
        assert contentVBox != null : "fx:id=\"contentVBox\" was not injected: check your FXML file 'MainView.fxml'.";
        assert overviewMenu != null : "fx:id=\"overviewMenu\" was not injected: check your FXML file 'MainView.fxml'.";
        assert weeklyCalendarMenuItem != null : "fx:id=\"weeklyCalendarMenuItem\" was not injected: check your FXML file 'MainView.fxml'.";
        assert monthlyCalendarMenuItem != null : "fx:id=\"monthlyCalendarMenuItem\" was not injected: check your FXML file 'MainView.fxml'.";
        assert myCurrentAndFutureAppointmentsMenuItem != null : "fx:id=\"myCurrentAndFutureAppointmentsMenuItem\" was not injected: check your FXML file 'MainView.fxml'.";
        assert allAppointmentsMenuItem != null : "fx:id=\"allAppointmentsMenuItem\" was not injected: check your FXML file 'MainView.fxml'.";
        assert typesByMonthMenuItem != null : "fx:id=\"typesByMonthMenuItem\" was not injected: check your FXML file 'MainView.fxml'.";
        assert consultantScheduleMenuItem != null : "fx:id=\"consultantScheduleMenuItem\" was not injected: check your FXML file 'MainView.fxml'.";
        assert byRegionMenuItem != null : "fx:id=\"byRegionMenuItem\" was not injected: check your FXML file 'MainView.fxml'.";
        assert manageCustomersMenuItem != null : "fx:id=\"manageCustomersMenuItem\" was not injected: check your FXML file 'MainView.fxml'.";
        assert manageUsersMenuItem != null : "fx:id=\"manageUsersMenuItem\" was not injected: check your FXML file 'MainView.fxml'.";
        assert manageAddressesMenuItem != null : "fx:id=\"manageAddressesMenuItem\" was not injected: check your FXML file 'MainView.fxml'.";
        assert helpContent != null : "fx:id=\"helpContent\" was not injected: check your FXML file 'MainView.fxml'.";
        assert waitBorderPane != null : "fx:id=\"waitBorderPane\" was not injected: check your FXML file 'MainView.fxml'.";
        assert appointmentAlert != null : "fx:id=\"appointmentAlert\" was not injected: check your FXML file 'MainView.fxml'.";

        daoEventHelper = new EventHelper<>("onDataObjectEvent");
        bindExtents(helpContent, rootStackPane);
        bindExtents(waitBorderPane, rootStackPane);
        bindExtents(appointmentAlert, rootStackPane);
    }

    public synchronized void replaceContent(Node newContent) {
        Node oldView = contentView;
        contentView = newContent;
        if (null != oldView) {
            contentVBox.getChildren().remove(oldView);
        }
        if (null != newContent) {
            VBox.setVgrow(newContent, Priority.ALWAYS);
            contentVBox.getChildren().add(newContent);
        }

        onContentReplaced(oldView, contentView);
    }

    private MenuItem getAssociatedMenuItem(Object controller) {
        if (null != controller) {
            if (controller instanceof Overview) {
                return overviewMenu;
            }
            if (controller instanceof ByWeek) {
                return weeklyCalendarMenuItem;
            }
            if (controller instanceof ByMonth) {
                return monthlyCalendarMenuItem;
            }
            if (controller instanceof ConsultantSchedule) {
                return consultantScheduleMenuItem;
            }
            if (controller instanceof AppointmentsByRegion) {
                return byRegionMenuItem;
            }
            if (controller instanceof AppointmentTypesByMonth) {
                return typesByMonthMenuItem;
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

    private void onContentReplaced(Node oldNode, Node newNode) {
        MenuItem menuItem;
        if (null != oldNode) {
            if (oldNode instanceof Overview) {
                overviewMenu.setDisable(false);
            } else if (null != (menuItem = getAssociatedMenuItem(oldNode))) {
                menuItem.setDisable(false);
            }
        }
        if (null != newNode) {
            if (newNode instanceof Overview) {
                overviewMenu.setDisable(true);
            } else if (null != (menuItem = getAssociatedMenuItem(newNode))) {
                menuItem.setDisable(true);
            }
        }
    }

    public <T extends Node> T showHelp(String title, String fxmlResourceName, String bundleBaseName) throws IOException {
        return helpContent.show((null == title || title.trim().isEmpty()) ? resources.getString(RESOURCEKEY_SCHEDULERHELP) : title, fxmlResourceName,
                bundleBaseName);
    }

    public <T extends Node> T showHelp(String fxmlResourceName, String bundleBaseName) throws IOException {
        return helpContent.show(resources.getString(RESOURCEKEY_SCHEDULERHELP), fxmlResourceName, bundleBaseName);
    }

    public <T extends Iterable<Text>> void showHelp(String title, T source) {
        helpContent.show((null == title || title.trim().isEmpty()) ? resources.getString(RESOURCEKEY_SCHEDULERHELP) : title, source);
    }

    public void showHelp(String title, Stream<Text> source) {
        helpContent.show((null == title || title.trim().isEmpty()) ? resources.getString(RESOURCEKEY_SCHEDULERHELP) : title, source);
    }

    public void showHelp(String title, Node source) {
        helpContent.show((null == title || title.trim().isEmpty()) ? resources.getString(RESOURCEKEY_SCHEDULERHELP) : title, source);
    }

    public <T extends Iterable<Text>> void showHelp(T source) {
        helpContent.show(resources.getString(RESOURCEKEY_SCHEDULERHELP), source);
    }

    public void showHelp(Stream<Text> source) {
        helpContent.show(resources.getString(RESOURCEKEY_SCHEDULERHELP), source);
    }

    public void showHelp(Node source) {
        helpContent.show(resources.getString(RESOURCEKEY_SCHEDULERHELP), source);
    }

    public void hideHelp() {
        helpContent.hide();
    }

    /**
     * Opens an {@link EditItem} window to edit a new {@link AppointmentModel}.
     *
     * @param customer The customer to initially select or {@code null} for no initial selection.
     * @param user The user to initially select or {@code null} for no initial selection.
     * @return The newly added {@link AppointmentModel} or {@code null} if the operation was canceled.
     */
    public AppointmentModel addNewAppointment(CustomerRowData customer, UserRowData user) {
        AppointmentModel result;
        try {
            result = EditAppointment.editNew(customer, user);
        } catch (IOException ex) {
            ErrorDetailControl.logShowAndWait(LOG, resources.getString(RESOURCEKEY_ERRORLOADINGNEWAPPOINTMENTWINDOW), ex);
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
     * @param item The {@link AppointmentModel} to be edited.
     */
    public void editAppointment(AppointmentModel item) {
        AppointmentModel result;
        try {
            result = EditAppointment.edit(item);
        } catch (IOException ex) {
            ErrorDetailControl.logShowAndWait(LOG, resources.getString(RESOURCEKEY_ERRORLOADINGAPPOINTMENTEDITWINDOW), ex);
            return;
        }
        if (null != result) {
            onAppointmentUpdated(result);
        }
    }

    /**
     * Deletes an {@link AppointmentModel} item after confirming with user.
     *
     * @param item The {@link AppointmentModel} to be deleted.
     */
    public void deleteAppointment(AppointmentModel item) {
        Optional<ButtonType> response = AlertHelper.showWarningAlert((Stage) contentView.getScene().getWindow(), LOG,
                AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONFIRMDELETE),
                AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO);
        if (response.isPresent() && response.get() == ButtonType.YES) {
            TaskWaiter.startNow(new DeleteTask<>(item, (Stage) contentView.getScene().getWindow(), AppointmentModel.getFactory(),
                    this::onAppointmentDeleted));
        }
    }

    /**
     * Opens an {@link EditItem} window to edit a new {@link CustomerModel}.
     *
     * @param stage The current {@link Stage}.
     * @return The newly added {@link CustomerModel} or {@code null} if the operation was canceled.
     */
    // TODO: Remove stage parameter
    public CustomerModel addNewCustomer(Stage stage) {
        CustomerModel result;
        try {
            result = EditCustomer.editNew();
        } catch (IOException ex) {
            ErrorDetailControl.logShowAndWait(LOG, resources.getString(RESOURCEKEY_ERRORLOADINGNEWCUSTOMERWINDOW), stage, ex);
            return null;
        }
        if (null != result) {
            CustomerDAO dataObject = result.getDataObject();
            Event.fireEvent(dataObject, new CustomerDaoEvent(this, DaoChangeAction.CREATED, dataObject));
        }
        return result;
    }

    /**
     * Opens an {@link EditItem} window to edit a {@link CustomerModel}.
     *
     * @param stage The current {@link Stage}.
     * @param item The {@link CustomerModel} to be edited.
     */
    // TODO: Remove stage parameter
    public void editCustomer(Stage stage, CustomerModel item) {
        CustomerModel result;
        try {
            result = EditCustomer.edit(item);
        } catch (IOException ex) {
            ErrorDetailControl.logShowAndWait(LOG, resources.getString(RESOURCEKEY_ERRORLOADINGCUSTOMEREDITWINDOW), stage, ex);
            return;
        }
        if (null != result) {
            CustomerDAO dataObject = result.getDataObject();
            Event.fireEvent(dataObject, new CustomerDaoEvent(this, DaoChangeAction.UPDATED, dataObject));
        }
    }

    /**
     * Deletes a {@link CustomerModel} item after confirming with user.
     *
     * @param stage The current {@link Stage}.
     * @param item The {@link CustomerModel} to be deleted.
     */
    // TODO: Remove stage parameter
    public void deleteCustomer(Stage stage, CustomerModel item) {
        Optional<ButtonType> response = AlertHelper.showWarningAlert((Stage) contentView.getScene().getWindow(), LOG,
                AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONFIRMDELETE),
                AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO);
        if (response.isPresent() && response.get() == ButtonType.YES) {
            TaskWaiter.startNow(new DeleteTask<>(item, (Stage) contentView.getScene().getWindow(), CustomerModel.getFactory(),
                    (t) -> {
                        CustomerDAO dataObject = t.getDataObject();
                        Event.fireEvent(dataObject, new CustomerDaoEvent(this, DaoChangeAction.DELETED, dataObject));
                    }));
        }
    }

    /**
     * Opens an {@link EditItem} window to edit a {@link CountryModel}.
     *
     * @param stage The current {@link Stage}.
     * @param item The {@link CountryModel} to be edited.
     */
    // TODO: Remove stage parameter
    public void openCountry(Stage stage, CountryModel item) {
        CountryModel result;
        try {
            result = EditCountry.edit(item);
        } catch (IOException ex) {
            ErrorDetailControl.logShowAndWait(LOG, resources.getString(RESOURCEKEY_ERRORLOADINGCOUNTRYEDITWINDOW), stage, ex);
            return;
        }
        if (null != result) {
            CountryDAO dataObject = result.getDataObject();
            Event.fireEvent(dataObject, new CountryDaoEvent(this, DaoChangeAction.UPDATED, dataObject));
        }
    }

    /**
     * Deletes a {@link CountryModel} item after confirming with user.
     *
     * @param stage The current {@link Stage}.
     * @param item The {@link CountryModel} to be deleted.
     */
    // TODO: Remove stage parameter
    public void deleteCountry(Stage stage, CountryModel item) {
        Optional<ButtonType> response = AlertHelper.showWarningAlert((Stage) contentView.getScene().getWindow(), LOG,
                AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONFIRMDELETE),
                AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO);
        if (response.isPresent() && response.get() == ButtonType.YES) {
            TaskWaiter.startNow(new DeleteTask<>(item, (Stage) contentView.getScene().getWindow(), CountryModel.getFactory(),
                    (t) -> {
                        CountryDAO dataObject = t.getDataObject();
                        Event.fireEvent(dataObject, new CountryDaoEvent(this, DaoChangeAction.DELETED, dataObject));
                    }));
        }
    }

    /**
     * Opens an {@link EditItem} window to edit a {@link CityModel}.
     *
     * @param stage The current {@link Stage}.
     * @param item The {@link CityModel} to be edited.
     */
    // TODO: Remove stage parameter
    public void openCity(Stage stage, CityModel item) {
        CityModel result;
        try {
            result = EditCity.edit(item);
        } catch (IOException ex) {
            ErrorDetailControl.logShowAndWait(LOG, resources.getString(RESOURCEKEY_ERRORLOADINGCITYEDITWINDOW), stage, ex);
            return;
        }
        if (null != result) {
            CityDAO dataObject = result.getDataObject();
            Event.fireEvent(dataObject, new CityDaoEvent(this, DaoChangeAction.UPDATED, dataObject));
        }
    }

    /**
     * Deletes a {@link CityModel} item after confirming with user.
     *
     * @param stage The current {@link Stage}.
     * @param item The {@link CityModel} to be deleted.
     */
    // TODO: Remove stage parameter
    public void deleteCity(Stage stage, CityModel item) {
        Optional<ButtonType> response = AlertHelper.showWarningAlert((Stage) contentView.getScene().getWindow(), LOG,
                AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONFIRMDELETE),
                AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO);
        if (response.isPresent() && response.get() == ButtonType.YES) {
            TaskWaiter.startNow(new DeleteTask<>(item, (Stage) contentView.getScene().getWindow(), CityModel.getFactory(),
                    (t) -> {
                        CityDAO dataObject = t.getDataObject();
                        Event.fireEvent(dataObject, new CityDaoEvent(this, DaoChangeAction.DELETED, dataObject));
                    }));
        }
    }

    /**
     * Opens an {@link EditItem} window to edit a new {@link AddressModel}.
     *
     * @param stage The current {@link Stage}.
     * @return The newly added {@link AddressModel} or {@code null} if the operation was canceled.
     */
    // TODO: Remove stage parameter
    public AddressModel addNewAddress(Stage stage) {
        AddressModel result;
        try {
            result = EditAddress.editNew();
        } catch (IOException ex) {
            ErrorDetailControl.logShowAndWait(LOG, resources.getString(RESOURCEKEY_ERRORLOADINGNEWADDRESSWINDOW), stage, ex);
            return null;
        }
        if (null != result) {
            AddressDAO dataObject = result.getDataObject();
            Event.fireEvent(dataObject, new AddressDaoEvent(this, DaoChangeAction.CREATED, dataObject));
        }
        return result;
    }

    /**
     * Opens an {@link EditItem} window to edit an {@link AddressModel}.
     *
     * @param stage The current {@link Stage}.
     * @param item The {@link AddressModel} to be edited.
     */
    // TODO: Remove stage parameter
    public void editAddress(Stage stage, AddressModel item) {
        AddressModel result;
        try {
            result = EditAddress.edit(item);
        } catch (IOException ex) {
            ErrorDetailControl.logShowAndWait(LOG, resources.getString(RESOURCEKEY_ERRORLOADINGADDRESSEDITWINDOW), stage, ex);
            return;
        }
        if (null != result) {
            AddressDAO dataObject = result.getDataObject();
            Event.fireEvent(dataObject, new AddressDaoEvent(this, DaoChangeAction.UPDATED, dataObject));
        }
    }

    /**
     * Deletes an {@link AddressModel} item after confirming with user.
     *
     * @param stage The current {@link Stage}.
     * @param item The {@link AddressModel} to be deleted.
     */
    // TODO: Remove stage parameter
    public void deleteAddress(Stage stage, AddressModel item) {
        Optional<ButtonType> response = AlertHelper.showWarningAlert((Stage) contentView.getScene().getWindow(), LOG,
                AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONFIRMDELETE),
                AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO);
        if (response.isPresent() && response.get() == ButtonType.YES) {
            TaskWaiter.startNow(new DeleteTask<>(item, (Stage) contentView.getScene().getWindow(), AddressModel.getFactory(),
                    (t) -> {
                        AddressDAO dataObject = t.getDataObject();
                        Event.fireEvent(dataObject, new AddressDaoEvent(this, DaoChangeAction.DELETED, dataObject));
                    }));
        }
    }

    /**
     * Opens an {@link EditItem} window to edit a new {@link UserModel}.
     *
     * @param stage The current {@link Stage}.
     * @return The newly added {@link UserModel} or {@code null} if the operation was canceled.
     */
    // TODO: Remove stage parameter
    public UserModel addNewUser(Stage stage) {
        UserModel result;
        try {
            result = EditUser.editNew();
        } catch (IOException ex) {
            ErrorDetailControl.logShowAndWait(LOG, resources.getString(RESOURCEKEY_ERRORLOADINGNEWUSERWINDOW), stage, ex);
            return null;
        }
        if (null != result) {
            UserDAO dataObject = result.getDataObject();
            Event.fireEvent(dataObject, new UserDaoEvent(this, DaoChangeAction.CREATED, dataObject));
        }
        return result;
    }

    /**
     * Opens an {@link EditItem} window to edit a {@link UserModel}.
     *
     * @param stage The current {@link Stage}.
     * @param item The {@link UserModel} to be edited.
     */
    // TODO: Remove stage parameter
    public void editUser(Stage stage, UserModel item) {
        UserModel result;
        try {
            result = EditUser.edit(item);
        } catch (IOException ex) {
            ErrorDetailControl.logShowAndWait(LOG, resources.getString(RESOURCEKEY_ERRORLOADINGUSEREDITWINDOW), stage, ex);
            return;
        }
        if (null != result) {
            UserDAO dataObject = result.getDataObject();
            Event.fireEvent(dataObject, new UserDaoEvent(this, DaoChangeAction.UPDATED, dataObject));
        }
    }

    /**
     * Deletes a {@link UserModel} item after confirming with user.
     *
     * @param stage The current {@link Stage}.
     * @param item The {@link UserModel} to be deleted.
     */
    // TODO: Remove stage parameter
    public void deleteUser(Stage stage, UserModel item) {
        Optional<ButtonType> response = AlertHelper.showWarningAlert((Stage) contentView.getScene().getWindow(), LOG,
                AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONFIRMDELETE),
                AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO);
        if (response.isPresent() && response.get() == ButtonType.YES) {
            TaskWaiter.startNow(new DeleteTask<>(item, (Stage) contentView.getScene().getWindow(), UserModel.getFactory(),
                    (t) -> {
                        UserDAO dataObject = t.getDataObject();
                        Event.fireEvent(dataObject, new UserDaoEvent(this, DaoChangeAction.DELETED, dataObject));
                    }));
        }
    }

    public void addDaoEventListener(DataObjectEventListener<? extends DataAccessObject> listener) {
        daoEventHelper.addListener(listener);
    }

    public void removeDaoEventListener(DataObjectEventListener<? extends DataAccessObject> listener) {
        daoEventHelper.removeListener(listener);
    }

    private void onAppointmentCreated(AppointmentModel item) {
        AppointmentDAO dataObject = item.getDataObject();
        // TODO: Notify AppointmentAlert?
        Event.fireEvent(dataObject, new AppointmentDaoEvent(this, DaoChangeAction.CREATED, dataObject));
    }

    private void onAppointmentUpdated(AppointmentModel item) {
        AppointmentDAO dataObject = item.getDataObject();
        Event.fireEvent(dataObject, new AppointmentDaoEvent(this, DaoChangeAction.UPDATED, dataObject));
        // TODO: Notify AppointmentAlert?
    }

    private void onAppointmentDeleted(AppointmentModel item) {
        AppointmentDAO dataObject = item.getDataObject();
        Event.fireEvent(dataObject, new AppointmentDaoEvent(this, DaoChangeAction.DELETED, dataObject));
        // TODO: Notify AppointmentAlert
    }

    public <T extends DataObjectEvent<? extends DataAccessObject>> void addDaoEventHandler(EventType<T> type, EventHandler<? super T> eventHandler) {
        eventHandlerManager.addEventHandler(type, eventHandler);
    }

    public <T extends DataObjectEvent<? extends DataAccessObject>> void addDaoEventFilter(EventType<T> type, EventHandler<? super T> eventHandler) {
        eventHandlerManager.addEventFilter(type, eventHandler);
    }

    public <T extends DataObjectEvent<? extends DataAccessObject>> void removeDaoEventHandler(EventType<T> type, EventHandler<? super T> eventHandler) {
        eventHandlerManager.removeEventHandler(type, eventHandler);
    }

    public <T extends DataObjectEvent<? extends DataAccessObject>> void removeDaoEventFilter(EventType<T> type, EventHandler<? super T> eventHandler) {
        eventHandlerManager.removeEventFilter(type, eventHandler);
    }

    @Override
    public EventDispatchChain buildEventDispatchChain(EventDispatchChain tail) {
        return tail.append(eventHandlerManager);
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
            ErrorDetailControl.logShowAndWait(LOG, AppResources.getResourceString(RESOURCEKEY_DELETEFAILURE), owner, ex,
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
