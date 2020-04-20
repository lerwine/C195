package scheduler.view;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import static scheduler.AppResourceBundleConstants.RESOURCEKEY_CONNECTEDTODB;
import static scheduler.AppResourceBundleConstants.RESOURCEKEY_DBREADERROR;
import scheduler.AppResources;
import static scheduler.Scheduler.getMainController;
import scheduler.dao.AppointmentDAO;
import scheduler.util.AlertHelper;
import static scheduler.view.MainResourceKeys.RESOURCEKEY_USERLOADERROR;
import static scheduler.view.OverviewResourceKeys.*;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.FxmlViewEventHandling;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.annotations.HandlesFxmlViewEvent;
import scheduler.view.country.CountryModel;
import scheduler.view.country.ManageCountries;
import scheduler.view.customer.CustomerModelImpl;
import scheduler.view.customer.ManageCustomers;
import scheduler.view.event.FxmlViewEvent;
import scheduler.view.task.TaskWaiter;
import scheduler.view.user.ManageUsers;
import scheduler.view.user.UserModelImpl;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/view/Overview")
@FXMLResource("/scheduler/view/Overview.fxml")
public class Overview {

    private static final Logger LOG = Logger.getLogger(Overview.class.getName());

    public static Overview loadInto(MainController mainController, Stage stage, Object loadEventListener) throws IOException {
        return mainController.loadContent(Overview.class, loadEventListener);
    }

    public static Overview loadInto(MainController mainController, Stage stage) throws IOException {
        return mainController.loadContent(Overview.class);
    }

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

//    @FXML // URL location of the FXML file that was given to the FXMLLoader
//    private URL location;
    @FXML // fx:id="appointmentsTodayLabel"
    private Label appointmentsTodayLabel; // Value injected by FXMLLoader

    @FXML // fx:id="appointmentsTomorrowLabel"
    private Label appointmentsTomorrowLabel; // Value injected by FXMLLoader

    @FXML // fx:id="appointmentsThisWeekLabel"
    private Label appointmentsThisWeekLabel; // Value injected by FXMLLoader

    @FXML // fx:id="appointmentsNextWeekLabel"
    private Label appointmentsNextWeekLabel; // Value injected by FXMLLoader

    @FXML // fx:id="appointmentsThisMonthLabel"
    private Label appointmentsThisMonthLabel; // Value injected by FXMLLoader

    @FXML // fx:id="appointmentsnextMonthLabel"
    private Label appointmentsNextMonthLabel; // Value injected by FXMLLoader

    @FXML
    void onCountryListingHyperlinkAction(ActionEvent event) {
        Stage stage = (Stage) ((Hyperlink) event.getSource()).getScene().getWindow();
        try {
            ManageCountries.loadInto(getMainController(stage.getScene()), stage, CountryModel.getFactory().getAllItemsFilter());
        } catch (IOException ex) {
            AlertHelper.showErrorAlert(stage, LOG, resources.getString(RESOURCEKEY_USERLOADERROR), ex);
        }
    }

    @FXML
    void onCustomerListingHyperlinkAction(ActionEvent event) {
        Stage stage = (Stage) ((Hyperlink) event.getSource()).getScene().getWindow();
        try {
            ManageCustomers.loadInto(getMainController(stage.getScene()), stage, CustomerModelImpl.getFactory().getAllItemsFilter());
        } catch (IOException ex) {
            AlertHelper.showErrorAlert(stage, LOG, resources.getString(RESOURCEKEY_USERLOADERROR), ex);
        }
    }

    @FXML
    void onNewAppointmentHyperlinkAction(ActionEvent event) {
        Stage stage = (Stage) ((Hyperlink) event.getSource()).getScene().getWindow();
        getMainController(stage.getScene()).addNewAppointment(stage, null, null);
    }

    @FXML
    void onUserListingHyperlinkAction(ActionEvent event) {
        Stage stage = (Stage) ((Hyperlink) event.getSource()).getScene().getWindow();
        try {
            ManageUsers.loadInto(getMainController(stage.getScene()), stage, UserModelImpl.getFactory().getAllItemsFilter());
        } catch (IOException ex) {
            AlertHelper.showErrorAlert(stage, LOG, resources.getString(RESOURCEKEY_USERLOADERROR), ex);
        }
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert appointmentsTodayLabel != null : "fx:id=\"appointmentsTodayLabel\" was not injected: check your FXML file 'Overview.fxml'.";
        assert appointmentsTomorrowLabel != null : "fx:id=\"appointmentsTomorrowLabel\" was not injected: check your FXML file 'Overview.fxml'.";
        assert appointmentsThisWeekLabel != null : "fx:id=\"appointmentsThisWeekLabel\" was not injected: check your FXML file 'Overview.fxml'.";
        assert appointmentsNextWeekLabel != null : "fx:id=\"appointmentsNextWeekLabel\" was not injected: check your FXML file 'Overview.fxml'.";
        assert appointmentsThisMonthLabel != null : "fx:id=\"appointmentsThisMonthLabel\" was not injected: check your FXML file 'Overview.fxml'.";
        assert appointmentsNextMonthLabel != null : "fx:id=\"appointmentsnextMonthLabel\" was not injected: check your FXML file 'Overview.fxml'.";
    }

    @HandlesFxmlViewEvent(FxmlViewEventHandling.BEFORE_SHOW)
    private void onBeforeShow(FxmlViewEvent<? extends Parent> event) {
        TaskWaiter.startNow(new InitializeTask(event.getStage()));
    }

    private class InitializeTask extends TaskWaiter<Integer> {

        private int appointmentsTomorrow;
        private int appointmentsThisWeek;
        private int appointmentsNextWeek;
        private int appointmentsThisMonth;
        private int appointmentsNextMonth;

        public InitializeTask(Stage stage) {
            super(stage, resources.getString(RESOURCEKEY_GETTINGAPPOINTMENTCOUNTS));
        }

        @Override
        protected void processResult(Integer result, Stage stage) {
            NumberFormat nf = NumberFormat.getIntegerInstance();
            if (null != result) {
                appointmentsTodayLabel.setText(nf.format(result.intValue()));
            }
            appointmentsTomorrowLabel.setText(nf.format(appointmentsTomorrow));
            appointmentsThisWeekLabel.setText(nf.format(appointmentsThisWeek));
            appointmentsNextWeekLabel.setText(nf.format(appointmentsNextWeek));
            appointmentsThisMonthLabel.setText(nf.format(appointmentsThisMonth));
            appointmentsNextMonthLabel.setText(nf.format(appointmentsNextMonth));
        }

        @Override
        protected void processException(Throwable ex, Stage stage) {
            AlertHelper.showErrorAlert(stage, LOG, AppResources.getResourceString(RESOURCEKEY_DBREADERROR),
                    "Unexpected error getting appointment counts", ex);
        }

        @Override
        protected Integer getResult(Connection connection) throws SQLException {
            updateMessage(AppResources.getResourceString(RESOURCEKEY_CONNECTEDTODB));
            AppointmentDAO.FactoryImpl factory = AppointmentDAO.getFactory();
            final LocalDateTime start = LocalDateTime.now();
            LocalDateTime end = start.toLocalDate().atStartOfDay().plusDays(1);
            appointmentsTomorrow = factory.countByRange(connection, end, end.plusDays(1));
            while (end.getDayOfWeek() != DayOfWeek.SUNDAY) {
                end = end.plusDays(1);
            }
            appointmentsThisWeek = factory.countByRange(connection, start, end);
            appointmentsNextWeek = factory.countByRange(connection, end, end.plusDays(7));
            Month month = start.getMonth();
            end = start.toLocalDate().atStartOfDay().plusDays(1);
            while (end.getMonth() != month) {
                end = end.plusDays(1);
            }
            appointmentsThisMonth = factory.countByRange(connection, start, end);
            appointmentsNextMonth = factory.countByRange(connection, end, end.plusMonths(1));
            return factory.countByRange(connection, start, start.toLocalDate().atStartOfDay().plusDays(1));
        }
    }
}
