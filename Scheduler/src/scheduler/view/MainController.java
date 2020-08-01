package scheduler.view;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javafx.beans.binding.DoubleBinding;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import scheduler.AppResourceKeys;
import scheduler.AppResources;
import scheduler.Scheduler;
import scheduler.fx.AppointmentAlert;
import scheduler.fx.HelpContent;
import scheduler.model.fx.AppointmentModel;
import scheduler.model.fx.CustomerModel;
import scheduler.model.fx.UserModel;
import scheduler.util.LogHelper;
import static scheduler.util.NodeUtil.bindExtents;
import static scheduler.view.MainResourceKeys.*;
import scheduler.view.address.EditAddress;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.appointment.AppointmentModelFilter;
import scheduler.view.appointment.MonthlyCalendar;
import scheduler.view.appointment.WeeklyCalendar;
import scheduler.view.appointment.EditAppointment;
import scheduler.view.appointment.ManageAppointments;
import scheduler.view.country.ManageCountries;
import scheduler.view.customer.EditCustomer;
import scheduler.view.customer.ManageCustomers;
import scheduler.view.report.AppointmentTypesByMonth;
import scheduler.view.report.AppointmentsByRegion;
import scheduler.view.report.ConsultantSchedule;
import scheduler.view.task.WaitBorderPane;
import scheduler.view.task.WaitTitledPane;
import scheduler.view.user.EditUser;
import scheduler.view.user.ManageUsers;

/**
 * FXML Controller class for main application content.
 * <p>
 * This controller will remain active from the time the consultant is logged in until the application exits.</p>
 * <p>
 * All data object create, update and delete operations should be initiated through this controller. This allows dynamically loaded views to be notified of changes, if
 * necessary.</p>
 * <p>
 * The associated view is {@code /resources/scheduler/view/MainView.fxml}.</p>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/view/Main")
@FXMLResource("/scheduler/view/MainView.fxml")
public final class MainController {

    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(MainController.class.getName()), Level.FINE);
//    private static final Logger LOG = Logger.getLogger(MainController.class.getName());

    public static void startBusyTaskNow(WaitTitledPane waitTitledPane, Task<?> task) {
        Scheduler.getMainController().waitBorderPane.startNow(waitTitledPane, task);
    }

    public static void startBusyTaskNow(Task<?> task) {
        Scheduler.getMainController().waitBorderPane.startNow(task);
    }

    public static void scheduleBusyTask(WaitTitledPane waitTitledPane, Task<?> task, long delay, TimeUnit unit) {
        Scheduler.getMainController().waitBorderPane.schedule(waitTitledPane, task, delay, unit);
    }

    public static void scheduleBusyTask(Task<?> task, long delay, TimeUnit unit) {
        Scheduler.getMainController().waitBorderPane.schedule(task, delay, unit);
    }

    private Node contentView;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // fx:id="rootStackPane"
    private StackPane rootStackPane; // Value injected by FXMLLoader

//    @FXML // fx:id="contentVBox"
//    private VBox contentVBox; // Value injected by FXMLLoader
    @FXML // fx:id="contentAnchorPane"
    private AnchorPane contentAnchorPane; // Value injected by FXMLLoader

    @FXML // fx:id="weeklyCalendarMenuItem"
    private MenuItem weeklyCalendarMenuItem; // Value injected by FXMLLoader

    @FXML // fx:id="monthlyCalendarMenuItem"
    private MenuItem monthlyCalendarMenuItem; // Value injected by FXMLLoader

    @FXML // fx:id="myCurrentAndFutureAppointmentsMenuItem"
    private MenuItem myCurrentAndFutureAppointmentsMenuItem; // Value injected by FXMLLoader

    @FXML // fx:id="allAppointmentsMenuItem"
    private MenuItem allAppointmentsMenuItem; // Value injected by FXMLLoader

    @FXML // fx:id="overviewMenuItem"
    private MenuItem overviewMenuItem; // Value injected by FXMLLoader

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
    private DoubleBinding contentWidthBinding;
    private DoubleBinding contentHeightBinding;

    public MainController() {
    }

    @FXML
    private void onAllAppointmentsMenuItemAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onAllAppointmentsMenuItemAction", event);
        ManageAppointments.loadIntoMainContent(AppointmentModel.FACTORY.getAllItemsFilter());
        LOG.exiting(LOG.getName(), "onAllAppointmentsMenuItemAction");
    }

    @FXML
    private void onByRegionMenuItemAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onByRegionMenuItemAction", event);
        replaceContent(AppointmentsByRegion.create());
        LOG.exiting(LOG.getName(), "onByRegionMenuItemAction");
    }

    @FXML
    private void onConsultantScheduleMenuItemAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onConsultantScheduleMenuItemAction", event);
        replaceContent(ConsultantSchedule.create());
        LOG.exiting(LOG.getName(), "onConsultantScheduleMenuItemAction");
    }

    @FXML
    private void onTypesByMonthMenuItemAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onTypesByMonthMenuItemAction", event);
        replaceContent(AppointmentTypesByMonth.create());
        LOG.exiting(LOG.getName(), "onTypesByMonthMenuItemAction");
    }

    @FXML
    private void onManageCustomersMenuItemAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onManageCustomersMenuItemAction", event);
        ManageCustomers.loadIntoMainContent(CustomerModel.FACTORY.getDefaultFilter());
        LOG.exiting(LOG.getName(), "onManageCustomersMenuItemAction");
    }

    @FXML
    private void onManageUsersMenuItemAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onManageUsersMenuItemAction", event);
        ManageUsers.loadIntoMainContent(UserModel.FACTORY.getDefaultFilter());
        LOG.exiting(LOG.getName(), "onManageUsersMenuItemAction");
    }

    @FXML
    private void onNewAddressMenuItem(ActionEvent event) {
        LOG.entering(LOG.getName(), "onNewAddressMenuItem", event);
        try {
            EditAddress.editNew(null, contentView.getScene().getWindow(), true);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error opening child window", ex);
        }
        LOG.exiting(LOG.getName(), "getName");
    }

    @FXML
    private void onNewAppointmentMenuItemAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onNewAppointmentMenuItemAction", event);
        try {
            EditAppointment.editNew(null, null, contentView.getScene().getWindow());
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error opening child window", ex);
        }
        LOG.exiting(LOG.getName(), "onNewAppointmentMenuItemAction");
    }

    @FXML
    private void onNewCustomerMenuItemAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onNewCustomerMenuItemAction", event);
        try {
            EditCustomer.editNew(null, contentView.getScene().getWindow(), true);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error opening child window", ex);
        }
        LOG.exiting(LOG.getName(), "onNewCustomerMenuItemAction");
    }

    @FXML
    private void onNewUserMenuItemAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onNewUserMenuItemAction", event);
        try {
            EditUser.editNew(contentView.getScene().getWindow(), true);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error opening child window", ex);
        }
        LOG.exiting(LOG.getName(), "onNewUserMenuItemAction");
    }

    @FXML
    private void onManageAddressesMenuItemAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onManageAddressesMenuItemAction", event);
        ManageCountries.loadIntoMainContent();
        LOG.exiting(LOG.getName(), "onManageAddressesMenuItemAction");
    }

    @FXML
    private void onMyCurrentAndFutureAppointmentsMenuItemAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onMyCurrentAndFutureAppointmentsMenuItemAction", event);
        ManageAppointments.loadIntoMainContent(AppointmentModelFilter.myCurrentAndFuture());
        LOG.exiting(LOG.getName(), "onMyCurrentAndFutureAppointmentsMenuItemAction");
    }

    @FXML
    private void onOverviewMenuItemAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onOverviewMenuItemAction", event);
        replaceContent(Overview.loadIntoMainContent());
        LOG.exiting(LOG.getName(), "onOverviewMenuItemAction");
    }

    @FXML
    private void onWeeklyCalendarMenuItemAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onWeeklyCalendarMenuItemAction", event);
        WeeklyCalendar.loadIntoMainContent(LocalDate.now());
        LOG.exiting(LOG.getName(), "onWeeklyCalendarMenuItemAction");
    }

    @FXML
    private void onMonthlyCalendarMenuItemAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onMonthlyCalendarMenuItemAction", event);
        MonthlyCalendar.loadIntoMainContent(YearMonth.now());
        LOG.exiting(LOG.getName(), "onMonthlyCalendarMenuItemAction");
    }

    @FXML
    private void initialize() {
        LOG.entering(LOG.getName(), "initialize");
        assert rootStackPane != null : "fx:id=\"rootStackPane\" was not injected: check your FXML file 'MainView.fxml'.";
//        assert contentVBox != null : "fx:id=\"contentVBox\" was not injected: check your FXML file 'MainView.fxml'.";
        assert contentAnchorPane != null : "fx:id=\"contentAnchorPane\" was not injected: check your FXML file 'MainView.fxml'.";
        assert weeklyCalendarMenuItem != null : "fx:id=\"weeklyCalendarMenuItem\" was not injected: check your FXML file 'MainView.fxml'.";
        assert monthlyCalendarMenuItem != null : "fx:id=\"monthlyCalendarMenuItem\" was not injected: check your FXML file 'MainView.fxml'.";
        assert myCurrentAndFutureAppointmentsMenuItem != null : "fx:id=\"myCurrentAndFutureAppointmentsMenuItem\" was not injected: check your FXML file 'MainView.fxml'.";
        assert allAppointmentsMenuItem != null : "fx:id=\"allAppointmentsMenuItem\" was not injected: check your FXML file 'MainView.fxml'.";
        assert overviewMenuItem != null : "fx:id=\"overviewMenuItem\" was not injected: check your FXML file 'MainView.fxml'.";
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
        LOG.exiting(LOG.getName(), "initialize");
    }

    public synchronized void replaceContent(Node newContent) {
        Node oldView = contentView;
        contentView = Objects.requireNonNull(newContent);
        if (null != oldView) {
            contentAnchorPane.getChildren().remove(oldView);
            ((Stage) contentAnchorPane.getScene().getWindow()).setTitle(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_APPOINTMENTSCHEDULER));
        }
        AnchorPane.setTopAnchor(newContent, 0.0);
        AnchorPane.setRightAnchor(newContent, 0.0);
        AnchorPane.setBottomAnchor(newContent, 0.0);
        AnchorPane.setLeftAnchor(newContent, 0.0);
        contentAnchorPane.getChildren().add(newContent);
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

}
