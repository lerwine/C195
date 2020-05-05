package scheduler.view;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import static scheduler.AppResourceKeys.RESOURCEKEY_CONNECTEDTODB;
import static scheduler.AppResourceKeys.RESOURCEKEY_DBREADERROR;
import static scheduler.AppResourceKeys.RESOURCEKEY_ERRORLOADINGAPPOINTMENTS;
import scheduler.AppResources;
import static scheduler.Scheduler.getMainController;
import scheduler.dao.AppointmentDAO;
import static scheduler.view.OverviewResourceKeys.*;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.FxmlViewEventHandling;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.annotations.HandlesFxmlViewEvent;
import scheduler.view.appointment.AppointmentModelFilter;
import scheduler.view.appointment.ByMonth;
import scheduler.view.appointment.ByWeek;
import scheduler.view.appointment.ManageAppointments;
import scheduler.view.country.CountryModel;
import scheduler.view.country.ManageCountries;
import scheduler.view.customer.CustomerModel;
import scheduler.view.customer.ManageCustomers;
import scheduler.view.event.FxmlViewEvent;
import scheduler.view.task.TaskWaiter;
import scheduler.view.user.ManageUsers;
import scheduler.view.user.UserModel;

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

    @FXML // fx:id="contentVBox"
    private VBox contentVBox; // Value injected by FXMLLoader

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

    @FXML // fx:id="appointmentsNextMonthLabel"
    private Label appointmentsNextMonthLabel; // Value injected by FXMLLoader

    void onByMonthHyperlinkAction(ActionEvent event) {
        Stage stage = (Stage) ((Hyperlink) event.getSource()).getScene().getWindow();
        try {
            ByMonth.loadInto(getMainController(), stage, LocalDate.now());
        } catch (IOException ex) {
            ErrorDetailDialog.logShowAndWait(LOG, resources.getString(RESOURCEKEY_USERLOADERROR), stage, ex);
        }
    }

    void onByWeekHyperlinkAction(ActionEvent event) {
        Stage stage = (Stage) ((Hyperlink) event.getSource()).getScene().getWindow();
        try {
            ByWeek.loadInto(getMainController(), stage, LocalDate.now());
        } catch (IOException ex) {
            ErrorDetailDialog.logShowAndWait(LOG, resources.getString(RESOURCEKEY_USERLOADERROR), stage, ex);
        }
    }

    @FXML
    void onCountryListingHyperlinkAction(ActionEvent event) {
        Stage stage = (Stage) ((Hyperlink) event.getSource()).getScene().getWindow();
        try {
            ManageCountries.loadInto(getMainController(), stage, CountryModel.getFactory().getAllItemsFilter());
        } catch (IOException ex) {
            ErrorDetailDialog.logShowAndWait(LOG, resources.getString(RESOURCEKEY_COUNTRYLOADERROR), stage, ex);
        }
    }

    @FXML
    void onCustomerListingHyperlinkAction(ActionEvent event) {
        Stage stage = (Stage) ((Hyperlink) event.getSource()).getScene().getWindow();
        try {
            ManageCustomers.loadInto(getMainController(), stage, CustomerModel.getFactory().getAllItemsFilter());
        } catch (IOException ex) {
            ErrorDetailDialog.logShowAndWait(LOG, resources.getString(RESOURCEKEY_CUSTOMERLOADERROR), stage, ex);
        }
    }

    @FXML
    void onNewAppointmentHyperlinkAction(ActionEvent event) {
        Stage stage = (Stage) ((Hyperlink) event.getSource()).getScene().getWindow();
        getMainController().addNewAppointment(stage, null, null);
    }

    @FXML
    void onAppointmentListHyperlinkAction(ActionEvent event) {
        Stage stage = (Stage) ((Hyperlink) event.getSource()).getScene().getWindow();
        try {
            ManageAppointments.loadInto(getMainController(), stage, AppointmentModelFilter.myCurrentAndFuture());
        } catch (IOException ex) {
            ErrorDetailDialog.logShowAndWait(LOG, AppResources.getResourceString(RESOURCEKEY_ERRORLOADINGAPPOINTMENTS), stage, ex);
        }
    }

    @FXML
    void onUserListingHyperlinkAction(ActionEvent event) {
        Stage stage = (Stage) ((Hyperlink) event.getSource()).getScene().getWindow();
        try {
            ManageUsers.loadInto(getMainController(), stage, UserModel.getFactory().getAllItemsFilter());
        } catch (IOException ex) {
            ErrorDetailDialog.logShowAndWait(LOG, resources.getString(RESOURCEKEY_USERLOADERROR), stage, ex);
        }
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert contentVBox != null : "fx:id=\"contentVBox\" was not injected: check your FXML file 'Overview.fxml'.";
        assert appointmentsTodayLabel != null : "fx:id=\"appointmentsTodayLabel\" was not injected: check your FXML file 'Overview.fxml'.";
        assert appointmentsTomorrowLabel != null : "fx:id=\"appointmentsTomorrowLabel\" was not injected: check your FXML file 'Overview.fxml'.";
        assert appointmentsThisWeekLabel != null : "fx:id=\"appointmentsThisWeekLabel\" was not injected: check your FXML file 'Overview.fxml'.";
        assert appointmentsNextWeekLabel != null : "fx:id=\"appointmentsNextWeekLabel\" was not injected: check your FXML file 'Overview.fxml'.";
        assert appointmentsThisMonthLabel != null : "fx:id=\"appointmentsThisMonthLabel\" was not injected: check your FXML file 'Overview.fxml'.";
        assert appointmentsNextMonthLabel != null : "fx:id=\"appointmentsNextMonthLabel\" was not injected: check your FXML file 'Overview.fxml'.";

        String resourcePath;
        switch (Locale.getDefault().getLanguage()) {
            case "de":
            case "hi":
            case "es":
                resourcePath = String.format("/scheduler/view/Overview_%s.fxml", Locale.getDefault().getLanguage());
                break;
            default:
                resourcePath = "/scheduler/view/Overview_en.fxml";
                break;
        }
        try {
            contentVBox.getChildren().add(FXMLLoader.load(getClass().getResource(resourcePath)));
            Hyperlink hyperlink = (Hyperlink)contentVBox.lookup("#byMonthHyperlink");
            hyperlink.setOnAction(this::onByMonthHyperlinkAction);
            hyperlink = (Hyperlink)contentVBox.lookup("#byWeekHyperlink");
            hyperlink.setOnAction(this::onByWeekHyperlinkAction);
            hyperlink = (Hyperlink)contentVBox.lookup("#newAppointmentHyperlink");
            hyperlink.setOnAction(this::onNewAppointmentHyperlinkAction);
            hyperlink = (Hyperlink)contentVBox.lookup("#appointmentListHyperlink");
            hyperlink.setOnAction(this::onAppointmentListHyperlinkAction);
            hyperlink = (Hyperlink)contentVBox.lookup("#customerListingHyperlink1");
            hyperlink.setOnAction(this::onCustomerListingHyperlinkAction);
            hyperlink = (Hyperlink)contentVBox.lookup("#customerListingHyperlink2");
            hyperlink.setOnAction(this::onCustomerListingHyperlinkAction);
            hyperlink = (Hyperlink)contentVBox.lookup("#countryListingHyperlink");
            hyperlink.setOnAction(this::onCountryListingHyperlinkAction);
            hyperlink = (Hyperlink)contentVBox.lookup("#userListingHyperlink");
            hyperlink.setOnAction(this::onUserListingHyperlinkAction);
        } catch (IOException ex) {
            Logger.getLogger(Overview.class.getName()).log(Level.SEVERE, null, ex);
        }
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
            ErrorDetailDialog.logShowAndWait(LOG, AppResources.getResourceString(RESOURCEKEY_DBREADERROR), stage, ex,
                    AppResources.getResourceString(RESOURCEKEY_ERRORLOADINGAPPOINTMENTS));
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
