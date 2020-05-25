package scheduler.view;

import com.sun.javafx.event.EventHandlerManager;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Level;
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
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Window;
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
import scheduler.dao.event.DataObjectEvent;
import scheduler.dao.event.UserDaoEvent;
import scheduler.fx.AppointmentAlert;
import scheduler.fx.ErrorDetailControl;
import scheduler.fx.HelpContent;
import scheduler.model.PredefinedData;
import scheduler.model.ui.AppointmentModel;
import scheduler.model.ui.CustomerModel;
import scheduler.model.ui.FxRecordModel;
import scheduler.model.ui.UserModel;
import scheduler.util.AlertHelper;
import scheduler.util.DbConnector;
import scheduler.util.LogHelper;
import static scheduler.util.NodeUtil.bindExtents;
import static scheduler.view.MainResourceKeys.*;
import scheduler.view.address.EditAddress;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.appointment.AppointmentModelFilter;
import scheduler.view.appointment.ByMonth;
import scheduler.view.appointment.ByWeek;
import scheduler.view.appointment.EditAppointment;
import scheduler.view.appointment.ManageAppointments;
import scheduler.view.country.ManageCountries;
import scheduler.view.customer.EditCustomer;
import scheduler.view.customer.ManageCustomers;
import scheduler.view.report.AppointmentTypesByMonth;
import scheduler.view.report.AppointmentsByRegion;
import scheduler.view.report.ConsultantSchedule;
import scheduler.view.task.WaitBorderPane;
import scheduler.view.user.EditUser;
import scheduler.view.user.ManageUsers;

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

    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(MainController.class.getName()), Level.FINER);

    public static void startBusyTaskNow(Task<?> task) {
        Scheduler.getMainController().waitBorderPane.startNow(task);
    }

    public static void scheduleBusyTask(Task<?> task, long delay, TimeUnit unit) {
        Scheduler.getMainController().waitBorderPane.schedule(task, delay, unit);
    }

    private Node contentView;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // fx:id="rootStackPane"
    private StackPane rootStackPane; // Value injected by FXMLLoader

    @FXML // fx:id="contentVBox"
    private VBox contentVBox; // Value injected by FXMLLoader

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
        eventHandlerManager = new EventHandlerManager(this);
        // Add handlers that will fire a separate generic event for each specificDbObject event.
        eventHandlerManager.addEventHandler(AddressDaoEvent.ANY_ADDRESS_EVENT, this::onAddressDaoEvent);
        eventHandlerManager.addEventHandler(AppointmentDaoEvent.ANY_APPOINTMENT_EVENT, this::onAppointmentDaoEvent);
        eventHandlerManager.addEventHandler(CityDaoEvent.ANY_CITY_EVENT, this::onCityDaoEvent);
        eventHandlerManager.addEventHandler(CountryDaoEvent.ANY_COUNTRY_EVENT, this::onCountryDaoEvent);
    }

    @FXML
    private void onAllAppointmentsMenuItemAction(ActionEvent event) {
        ManageAppointments.loadIntoMainContent(AppointmentModel.getFactory().getAllItemsFilter());
    }

    @FXML
    private void onByRegionMenuItemAction(ActionEvent event) {
        replaceContent(new AppointmentsByRegion());
    }

    @FXML
    private void onConsultantScheduleMenuItemAction(ActionEvent event) {
        replaceContent(new ConsultantSchedule());
    }

    @FXML
    private void onTypesByMonthMenuItemAction(ActionEvent event) {
        replaceContent(new AppointmentTypesByMonth());
    }

    @FXML
    private void onManageCustomersMenuItemAction(ActionEvent event) {
        ManageCustomers.loadIntoMainContent(CustomerModel.getFactory().getDefaultFilter());
    }

    @FXML
    private void onManageUsersMenuItemAction(ActionEvent event) {
        ManageUsers.loadIntoMainContent(UserModel.getFactory().getDefaultFilter());
    }

    @FXML
    private void onNewAddressMenuItem(ActionEvent event) {
        try {
            EditAddress.editNew(null, contentView.getScene().getWindow(), true);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error opening child window", ex);
        }
    }

    @FXML
    private void onNewAppointmentMenuItemAction(ActionEvent event) {
        try {
            EditAppointment.editNew(null, null, contentView.getScene().getWindow(), true);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error opening child window", ex);
        }
    }

    @FXML
    private void onNewCustomerMenuItemAction(ActionEvent event) {
        try {
            EditCustomer.editNew(null, contentView.getScene().getWindow(), true);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error opening child window", ex);
        }
    }

    @FXML
    private void onNewUserMenuItemAction(ActionEvent event) {
        try {
            EditUser.editNew(contentView.getScene().getWindow(), true);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error opening child window", ex);
        }
    }

    @FXML
    private void onManageAddressesMenuItemAction(ActionEvent event) {
        ManageCountries.loadIntoMainContent();
    }

    @FXML
    private void onMyCurrentAndFutureAppointmentsMenuItemAction(ActionEvent event) {
        ManageAppointments.loadIntoMainContent(AppointmentModelFilter.myCurrentAndFuture());
    }

    @FXML
    private void onOverviewMenuItemAction(ActionEvent event) {
        replaceContent(new Overview());
    }

    @FXML
    private void onWeeklyCalendarMenuItemAction(ActionEvent event) {
        ByWeek.loadIntoMainContent(LocalDate.now());
    }

    @FXML
    private void onMonthlyCalendarMenuItemAction(ActionEvent event) {
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
    }

    private void onAddressDaoEvent(AddressDaoEvent event) {
        LOG.fine(() -> String.format("%s event handled", event.getEventType().getName()));
        PredefinedData.onAddressDaoEvent(event);
    }

    private void onAppointmentDaoEvent(AppointmentDaoEvent event) {
        LOG.fine(() -> String.format("%s event handled", event.getEventType().getName()));
        // TODO: Add handler for AppointmentAlert
    }

    private void onCityDaoEvent(CityDaoEvent event) {
        LOG.fine(() -> String.format("%s event handled", event.getEventType().getName()));
        PredefinedData.onCityDaoEvent(event);
    }

    private void onCountryDaoEvent(CountryDaoEvent event) {
        LOG.fine(() -> String.format("%s event handled", event.getEventType().getName()));
        PredefinedData.onCountryDaoEvent(event);
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
     * Registers a {@link DataObjectEvent} handler in the {@code EventHandlerManager}.
     * <dl>
     * <dt>{@link AddressDAO}</dt><dd>{@link AddressDaoEvent#ANY_ADDRESS_EVENT}, {@link AddressDaoEvent#ADDRESS_DAO_INSERT},
     * {@link AddressDaoEvent#ADDRESS_DAO_UPDATE}, {@link AddressDaoEvent#ADDRESS_DAO_DELETE}</dd>
     * <dt>{@link AppointmentDAO}</dt><dd>{@link AppointmentDaoEvent#ANY_APPOINTMENT_EVENT}, {@link AppointmentDaoEvent#APPOINTMENT_DAO_INSERT},
     * {@link AppointmentDaoEvent#APPOINTMENT_DAO_UPDATE}, {@link AppointmentDaoEvent#APPOINTMENT_DAO_DELETE}</dd>
     * <dt>{@link CityDAO}</dt><dd>{@link CityDaoEvent#ANY_CITY_EVENT}, {@link CityDaoEvent#CITY_DAO_INSERT}, {@link CityDaoEvent#CITY_DAO_UPDATE},
     * {@link CityDaoEvent#CITY_DAO_DELETE}</dd>
     * <dt>{@link CountryDAO}</dt><dd>{@link CountryDaoEvent#ANY_COUNTRY_EVENT}, {@link CountryDaoEvent#COUNTRY_DAO_INSERT},
     * {@link CountryDaoEvent#COUNTRY_DAO_UPDATE}, {@link CountryDaoEvent#COUNTRY_DAO_DELETE}</dd>
     * <dt>{@link CustomerDAO}</dt><dd>{@link CustomerDaoEvent#ANY_CUSTOMER_EVENT}, {@link CustomerDaoEvent#CUSTOMER_DAO_INSERT},
     * {@link CustomerDaoEvent#CUSTOMER_DAO_UPDATE}, {@link CustomerDaoEvent#CUSTOMER_DAO_DELETE}</dd>
     * <dt>{@link UserDAO}</dt><dd>{@link UserDaoEvent#ANY_USER_EVENT}, {@link UserDaoEvent#USER_DAO_INSERT}, {@link UserDaoEvent#USER_DAO_UPDATE},
     * {@link UserDaoEvent#USER_DAO_DELETE}</dd>
     * <dt>{@link DataAccessObject}</dt><dd>{@link DataObjectEvent#ANY_DAO_EVENT}, {@link DataObjectEvent#ANY_DAO_INSERT},
     * {@link DataObjectEvent#ANY_DAO_UPDATE}, {@link DataObjectEvent#ANY_DAO_DELETE}</dd>
     * </dl>
     *
     * @param <T> The type of {@link Event}.
     * @param type The {@link EventType}.
     * @param eventHandler The {@link EventHandler}.
     */
    public <T extends DataObjectEvent<? extends DataAccessObject>> void addDaoEventHandler(EventType<T> type, EventHandler<T> eventHandler) {
        eventHandlerManager.addEventHandler(type, eventHandler);
    }

    /**
     * Registers a {@link DataObjectEvent} filter in the {@code EventHandlerManager}.
     * <dl>
     * <dt>{@link AddressDAO}</dt><dd>{@link AddressDaoEvent#ANY_ADDRESS_EVENT}, {@link AddressDaoEvent#ADDRESS_DAO_INSERT},
     * {@link AddressDaoEvent#ADDRESS_DAO_UPDATE}, {@link AddressDaoEvent#ADDRESS_DAO_DELETE}</dd>
     * <dt>{@link AppointmentDAO}</dt><dd>{@link AppointmentDaoEvent#ANY_APPOINTMENT_EVENT}, {@link AppointmentDaoEvent#APPOINTMENT_DAO_INSERT},
     * {@link AppointmentDaoEvent#APPOINTMENT_DAO_UPDATE}, {@link AppointmentDaoEvent#APPOINTMENT_DAO_DELETE}</dd>
     * <dt>{@link CityDAO}</dt><dd>{@link CityDaoEvent#ANY_CITY_EVENT}, {@link CityDaoEvent#CITY_DAO_INSERT}, {@link CityDaoEvent#CITY_DAO_UPDATE},
     * {@link CityDaoEvent#CITY_DAO_DELETE}</dd>
     * <dt>{@link CountryDAO}</dt><dd>{@link CountryDaoEvent#ANY_COUNTRY_EVENT}, {@link CountryDaoEvent#COUNTRY_DAO_INSERT},
     * {@link CountryDaoEvent#COUNTRY_DAO_UPDATE}, {@link CountryDaoEvent#COUNTRY_DAO_DELETE}</dd>
     * <dt>{@link CustomerDAO}</dt><dd>{@link CustomerDaoEvent#ANY_CUSTOMER_EVENT}, {@link CustomerDaoEvent#CUSTOMER_DAO_INSERT},
     * {@link CustomerDaoEvent#CUSTOMER_DAO_UPDATE}, {@link CustomerDaoEvent#CUSTOMER_DAO_DELETE}</dd>
     * <dt>{@link UserDAO}</dt><dd>{@link UserDaoEvent#ANY_USER_EVENT}, {@link UserDaoEvent#USER_DAO_INSERT}, {@link UserDaoEvent#USER_DAO_UPDATE},
     * {@link UserDaoEvent#USER_DAO_DELETE}</dd>
     * <dt>{@link DataAccessObject}</dt><dd>{@link DataObjectEvent#ANY_DAO_EVENT}, {@link DataObjectEvent#ANY_DAO_INSERT},
     * {@link DataObjectEvent#ANY_DAO_UPDATE}, {@link DataObjectEvent#ANY_DAO_DELETE}</dd>
     * </dl>
     *
     *
     * @param <T> The type of {@link Event}.
     * @param type The {@link EventType}.
     * @param eventHandler The {@link EventHandler}.
     */
    public <T extends DataObjectEvent<? extends DataAccessObject>> void addDaoEventFilter(EventType<T> type, EventHandler<T> eventHandler) {
        eventHandlerManager.addEventFilter(type, eventHandler);
    }

    /**
     * Un-registers a {@link DataObjectEvent} handler from the {@code EventHandlerManager}.
     * <dl>
     * <dt>{@link AddressDAO}</dt><dd>{@link AddressDaoEvent#ANY_ADDRESS_EVENT}, {@link AddressDaoEvent#ADDRESS_DAO_INSERT},
     * {@link AddressDaoEvent#ADDRESS_DAO_UPDATE}, {@link AddressDaoEvent#ADDRESS_DAO_DELETE}</dd>
     * <dt>{@link AppointmentDAO}</dt><dd>{@link AppointmentDaoEvent#ANY_APPOINTMENT_EVENT}, {@link AppointmentDaoEvent#APPOINTMENT_DAO_INSERT},
     * {@link AppointmentDaoEvent#APPOINTMENT_DAO_UPDATE}, {@link AppointmentDaoEvent#APPOINTMENT_DAO_DELETE}</dd>
     * <dt>{@link CityDAO}</dt><dd>{@link CityDaoEvent#ANY_CITY_EVENT}, {@link CityDaoEvent#CITY_DAO_INSERT}, {@link CityDaoEvent#CITY_DAO_UPDATE},
     * {@link CityDaoEvent#CITY_DAO_DELETE}</dd>
     * <dt>{@link CountryDAO}</dt><dd>{@link CountryDaoEvent#ANY_COUNTRY_EVENT}, {@link CountryDaoEvent#COUNTRY_DAO_INSERT},
     * {@link CountryDaoEvent#COUNTRY_DAO_UPDATE}, {@link CountryDaoEvent#COUNTRY_DAO_DELETE}</dd>
     * <dt>{@link CustomerDAO}</dt><dd>{@link CustomerDaoEvent#ANY_CUSTOMER_EVENT}, {@link CustomerDaoEvent#CUSTOMER_DAO_INSERT},
     * {@link CustomerDaoEvent#CUSTOMER_DAO_UPDATE}, {@link CustomerDaoEvent#CUSTOMER_DAO_DELETE}</dd>
     * <dt>{@link UserDAO}</dt><dd>{@link UserDaoEvent#ANY_USER_EVENT}, {@link UserDaoEvent#USER_DAO_INSERT}, {@link UserDaoEvent#USER_DAO_UPDATE},
     * {@link UserDaoEvent#USER_DAO_DELETE}</dd>
     * <dt>{@link DataAccessObject}</dt><dd>{@link DataObjectEvent#ANY_DAO_EVENT}, {@link DataObjectEvent#ANY_DAO_INSERT},
     * {@link DataObjectEvent#ANY_DAO_UPDATE}, {@link DataObjectEvent#ANY_DAO_DELETE}</dd>
     * </dl>
     *
     * @param <T> The type of {@link Event}.
     * @param type The {@link EventType}.
     * @param eventHandler The {@link EventHandler}.
     */
    public <T extends DataObjectEvent<? extends DataAccessObject>> void removeDaoEventHandler(EventType<T> type, EventHandler<T> eventHandler) {
        eventHandlerManager.removeEventHandler(type, eventHandler);
    }

    /**
     * Un-registers a {@link DataObjectEvent} filter in the {@code EventHandlerManager}.
     * <dl>
     * <dt>{@link AddressDAO}</dt><dd>{@link AddressDaoEvent#ANY_ADDRESS_EVENT}, {@link AddressDaoEvent#ADDRESS_DAO_INSERT},
     * {@link AddressDaoEvent#ADDRESS_DAO_UPDATE}, {@link AddressDaoEvent#ADDRESS_DAO_DELETE}</dd>
     * <dt>{@link AppointmentDAO}</dt><dd>{@link AppointmentDaoEvent#ANY_APPOINTMENT_EVENT}, {@link AppointmentDaoEvent#APPOINTMENT_DAO_INSERT},
     * {@link AppointmentDaoEvent#APPOINTMENT_DAO_UPDATE}, {@link AppointmentDaoEvent#APPOINTMENT_DAO_DELETE}</dd>
     * <dt>{@link CityDAO}</dt><dd>{@link CityDaoEvent#ANY_CITY_EVENT}, {@link CityDaoEvent#CITY_DAO_INSERT}, {@link CityDaoEvent#CITY_DAO_UPDATE},
     * {@link CityDaoEvent#CITY_DAO_DELETE}</dd>
     * <dt>{@link CountryDAO}</dt><dd>{@link CountryDaoEvent#ANY_COUNTRY_EVENT}, {@link CountryDaoEvent#COUNTRY_DAO_INSERT},
     * {@link CountryDaoEvent#COUNTRY_DAO_UPDATE}, {@link CountryDaoEvent#COUNTRY_DAO_DELETE}</dd>
     * <dt>{@link CustomerDAO}</dt><dd>{@link CustomerDaoEvent#ANY_CUSTOMER_EVENT}, {@link CustomerDaoEvent#CUSTOMER_DAO_INSERT},
     * {@link CustomerDaoEvent#CUSTOMER_DAO_UPDATE}, {@link CustomerDaoEvent#CUSTOMER_DAO_DELETE}</dd>
     * <dt>{@link UserDAO}</dt><dd>{@link UserDaoEvent#ANY_USER_EVENT}, {@link UserDaoEvent#USER_DAO_INSERT}, {@link UserDaoEvent#USER_DAO_UPDATE},
     * {@link UserDaoEvent#USER_DAO_DELETE}</dd>
     * <dt>{@link DataAccessObject}</dt><dd>{@link DataObjectEvent#ANY_DAO_EVENT}, {@link DataObjectEvent#ANY_DAO_INSERT},
     * {@link DataObjectEvent#ANY_DAO_UPDATE}, {@link DataObjectEvent#ANY_DAO_DELETE}</dd>
     * </dl>
     *
     * @param <T> The type of {@link Event}.
     * @param type The {@link EventType}.
     * @param eventHandler The {@link EventHandler}.
     */
    public <T extends DataObjectEvent<? extends DataAccessObject>> void removeDaoEventFilter(EventType<T> type, EventHandler<T> eventHandler) {
        eventHandlerManager.removeEventFilter(type, eventHandler);
    }

    @Override
    public EventDispatchChain buildEventDispatchChain(EventDispatchChain tail) {
        return rootStackPane.buildEventDispatchChain(tail.prepend(eventHandlerManager));
    }

    private class DeleteTask<D extends DataAccessObject, M extends FxRecordModel<D>> extends Task<String> {

        private final M model;
        private final Window parentWindow;
        private final Consumer<M> onDeleted;
        private final DataAccessObject.DaoFactory<D> factory;

        DeleteTask(M model, Window parentWindow, FxRecordModel.ModelFactory<D, M> factory, Consumer<M> onDeleted) {
            updateTitle(AppResources.getResourceString(RESOURCEKEY_DELETINGRECORD));
            this.model = model;
            this.parentWindow = parentWindow;
            this.onDeleted = onDeleted;
            this.factory = factory.getDaoFactory();
        }

        @Override
        protected void succeeded() {
            super.succeeded();
            String message = getValue();
            if (null != message && !message.trim().isEmpty()) {
                AlertHelper.showWarningAlert(parentWindow, LOG, AppResources.getResourceString(RESOURCEKEY_DELETEFAILURE), message);
            } else if (null != onDeleted) {
                onDeleted.accept(model);
            }
        }

        @Override
        protected void failed() {
            super.failed();
            ErrorDetailControl.logShowAndWait(LOG, AppResources.getResourceString(RESOURCEKEY_DELETEFAILURE), parentWindow, getException(),
                    AppResources.getResourceString(RESOURCEKEY_ERRORDELETINGFROMDB));
        }

        @Override
        protected String call() throws Exception {
            try (DbConnector connector = new DbConnector()) {
                updateMessage(AppResources.getResourceString(RESOURCEKEY_CHECKINGDEPENDENCIES));
                String message = factory.getDeleteDependencyMessage(model.getDataObject(), connector.getConnection());
                if (null != message && !message.trim().isEmpty()) {
                    return message;
                }
                updateMessage(AppResources.getResourceString(RESOURCEKEY_COMPLETINGOPERATION));
                factory.delete(model.getDataObject(), connector.getConnection());
            }
            return null;
        }
    }

}
