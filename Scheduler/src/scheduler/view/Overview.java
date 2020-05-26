package scheduler.view;

import java.io.IOException;
import java.text.NumberFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;
import scheduler.AppResourceKeys;
import scheduler.AppResources;
import scheduler.dao.AppointmentDAO;
import scheduler.model.ui.CustomerModel;
import scheduler.model.ui.UserModel;
import scheduler.util.DbConnector;
import scheduler.util.ViewControllerLoader;
import static scheduler.view.OverviewResourceKeys.*;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.appointment.AppointmentModelFilter;
import scheduler.view.appointment.ByMonth;
import scheduler.view.appointment.ByWeek;
import scheduler.view.appointment.EditAppointment;
import scheduler.view.appointment.ManageAppointments;
import scheduler.view.country.ManageCountries;
import scheduler.view.customer.ManageCustomers;
import scheduler.view.user.ManageUsers;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/view/Overview")
@FXMLResource("/scheduler/view/Overview.fxml")
public class Overview extends VBox {

    private static final Logger LOG = Logger.getLogger(Overview.class.getName());

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

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

    @SuppressWarnings("LeakingThisInConstructor")
    public Overview() {
        try {
            ViewControllerLoader.initializeCustomControl(this);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error loading view", ex);
            throw new InternalError("Error loading view", ex);
        }
    }

    void onByMonthHyperlinkAction(ActionEvent event) {
        ByMonth.loadIntoMainContent(LocalDate.now());
    }

    void onByWeekHyperlinkAction(ActionEvent event) {
        ByWeek.loadIntoMainContent(LocalDate.now());
    }

    @FXML
    void onCountryListingHyperlinkAction(ActionEvent event) {
        ManageCountries.loadIntoMainContent();
    }

    @FXML
    void onCustomerListingHyperlinkAction(ActionEvent event) {
        ManageCustomers.loadIntoMainContent(CustomerModel.getFactory().getDefaultFilter());
    }

    @FXML
    void onNewAppointmentHyperlinkAction(ActionEvent event) {
        try {
            EditAppointment.editNew(null, null, getScene().getWindow(), true);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error opening child window", ex);
        }
    }

    @FXML
    void onAppointmentListHyperlinkAction(ActionEvent event) {
        ManageAppointments.loadIntoMainContent(AppointmentModelFilter.myCurrentAndFuture());
    }

    @FXML
    void onUserListingHyperlinkAction(ActionEvent event) {
        ManageUsers.loadIntoMainContent(UserModel.getFactory().getDefaultFilter());
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
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
                resourcePath = String.format("/scheduler/fx/Overview_%s.fxml", Locale.getDefault().getLanguage());
                break;
            default:
                resourcePath = "/scheduler/fx/Overview_en.fxml";
                break;
        }
        try {
            TextFlow textFlow = FXMLLoader.load(getClass().getResource(resourcePath));
            getChildren().add(textFlow);
            Hyperlink hyperlink = (Hyperlink) textFlow.lookup("#byMonthHyperlink");
            hyperlink.setOnAction(this::onByMonthHyperlinkAction);
            hyperlink = (Hyperlink) textFlow.lookup("#byWeekHyperlink");
            hyperlink.setOnAction(this::onByWeekHyperlinkAction);
            hyperlink = (Hyperlink) textFlow.lookup("#newAppointmentHyperlink");
            hyperlink.setOnAction(this::onNewAppointmentHyperlinkAction);
            hyperlink = (Hyperlink) textFlow.lookup("#appointmentListHyperlink");
            hyperlink.setOnAction(this::onAppointmentListHyperlinkAction);
            hyperlink = (Hyperlink) textFlow.lookup("#customerListingHyperlink1");
            hyperlink.setOnAction(this::onCustomerListingHyperlinkAction);
            hyperlink = (Hyperlink) textFlow.lookup("#customerListingHyperlink2");
            hyperlink.setOnAction(this::onCustomerListingHyperlinkAction);
            hyperlink = (Hyperlink) textFlow.lookup("#countryListingHyperlink");
            hyperlink.setOnAction(this::onCountryListingHyperlinkAction);
            hyperlink = (Hyperlink) textFlow.lookup("#userListingHyperlink");
            hyperlink.setOnAction(this::onUserListingHyperlinkAction);
        } catch (IOException ex) {
            Logger.getLogger(Overview.class.getName()).log(Level.SEVERE, null, ex);
        }
        MainController.startBusyTaskNow(new InitializeTask());
    }

    private class InitializeTask extends Task<Integer> {

        private int appointmentsTomorrow;
        private int appointmentsThisWeek;
        private int appointmentsNextWeek;
        private int appointmentsThisMonth;
        private int appointmentsNextMonth;

        public InitializeTask() {
            updateTitle(resources.getString(RESOURCEKEY_GETTINGAPPOINTMENTCOUNTS));
        }

        @Override
        protected void succeeded() {
            Integer result = getValue();
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
        protected Integer call() throws Exception {
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
            try (DbConnector dbConnector = new DbConnector()) {
                updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTEDTODB));
                AppointmentDAO.FactoryImpl factory = AppointmentDAO.getFactory();
                final LocalDateTime start = LocalDateTime.now();
                LocalDateTime end = start.toLocalDate().atStartOfDay().plusDays(1);
                appointmentsTomorrow = factory.countByRange(dbConnector.getConnection(), end, end.plusDays(1));
                while (end.getDayOfWeek() != DayOfWeek.SUNDAY) {
                    end = end.plusDays(1);
                }
                appointmentsThisWeek = factory.countByRange(dbConnector.getConnection(), start, end);
                appointmentsNextWeek = factory.countByRange(dbConnector.getConnection(), end, end.plusDays(7));
                Month month = start.getMonth();
                end = start.toLocalDate().atStartOfDay().plusDays(1);
                while (end.getMonth() != month) {
                    end = end.plusDays(1);
                }
                appointmentsThisMonth = factory.countByRange(dbConnector.getConnection(), start, end);
                appointmentsNextMonth = factory.countByRange(dbConnector.getConnection(), end, end.plusMonths(1));
                return factory.countByRange(dbConnector.getConnection(), start, start.toLocalDate().atStartOfDay().plusDays(1));
            }
        }
    }
}
