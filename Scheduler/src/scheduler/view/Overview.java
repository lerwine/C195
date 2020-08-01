package scheduler.view;

import java.io.IOException;
import java.text.NumberFormat;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.YearMonth;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Window;
import scheduler.AppResourceKeys;
import scheduler.AppResources;
import scheduler.Scheduler;
import scheduler.dao.AppointmentDAO;
import scheduler.dao.CustomerDAO;
import scheduler.dao.PartialCustomerDAO;
import scheduler.model.fx.AppointmentModel;
import scheduler.model.fx.CustomerModel;
import scheduler.model.fx.PartialCustomerModel;
import scheduler.model.fx.UserModel;
import scheduler.util.AlertHelper;
import scheduler.util.DateTimeUtil;
import scheduler.util.DbConnector;
import scheduler.util.LogHelper;
import scheduler.util.NodeUtil;
import scheduler.util.ViewControllerLoader;
import static scheduler.view.OverviewResourceKeys.*;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.appointment.AppointmentModelFilter;
import scheduler.view.appointment.AppointmentsByMonth;
import scheduler.view.appointment.AppointmentsByWeek;
import scheduler.view.appointment.EditAppointment;
import scheduler.view.appointment.ManageAppointments;
import scheduler.view.country.ManageCountries;
import scheduler.view.customer.EditCustomer;
import scheduler.view.customer.ManageCustomers;
import scheduler.view.report.AppointmentTypesByMonth;
import scheduler.view.report.AppointmentsByRegion;
import scheduler.view.report.ConsultantSchedule;
import scheduler.view.user.ManageUsers;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/view/Overview")
@FXMLResource("/scheduler/view/Overview.fxml")
public final class Overview extends VBox {

    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(Overview.class.getName()), Level.FINE);
//    private static final Logger LOG = Logger.getLogger(Overview.class.getName());

    public static Overview loadIntoMainContent() {
        Overview newContent = new Overview();
        try {
            ViewControllerLoader.initializeCustomControl(newContent);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error loading view", ex);
            throw new InternalError("Error loading view", ex);
        }
        Scheduler.getMainController().replaceContent(newContent);
        return newContent;
    }

    private AppointmentModel nextAppointment;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // fx:id="nextAppointmentLeadText"
    private Text nextAppointmentLeadText; // Value injected by FXMLLoader

    @FXML // fx:id="nextAppointmentHyperlink"
    private Hyperlink nextAppointmentHyperlink; // Value injected by FXMLLoader

    @FXML // fx:id="nextAppointmentAppendLabel"
    private Text nextAppointmentAppendLabel; // Value injected by FXMLLoader

    @FXML // fx:id="nextAppointmentCustomerHyperlink"
    private Hyperlink nextAppointmentCustomerHyperlink; // Value injected by FXMLLoader

    @FXML // fx:id="nextAppointmentTerminalText"
    private Text nextAppointmentTerminalText; // Value injected by FXMLLoader

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
    private Overview() {
        
    }

    @FXML
    void onAllAppointmentsHyperlinkAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onAppointmentListHyperlinkAction", event);
        ManageAppointments.loadIntoMainContent(AppointmentModelFilter.myCurrentAndFuture());
        LOG.exiting(LOG.getName(), "onAllAppointmentsHyperlinkAction");
    }

    @FXML
    void onByMonthHyperlinkAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onByMonthHyperlinkAction", event);
        AppointmentsByMonth.loadIntoMainContent(YearMonth.now());
        LOG.exiting(LOG.getName(), "onByMonthHyperlinkAction");
    }

    @FXML
    void onByRegionHyperlinkAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onByRegionHyperlinkAction", event);
        Scheduler.getMainController().replaceContent(AppointmentsByRegion.create());
        LOG.exiting(LOG.getName(), "onByRegionHyperlinkAction");
    }

    @FXML
    void onByWeekHyperlinkAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onByWeekHyperlinkAction", event);
        AppointmentsByWeek.loadIntoMainContent(LocalDate.now());
        LOG.exiting(LOG.getName(), "onByWeekHyperlinkAction");
    }

    @FXML
    void onConsultantScheduleHyperlinkAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onConsultantScheduleHyperlinkAction", event);
        Scheduler.getMainController().replaceContent(ConsultantSchedule.create());
        LOG.exiting(LOG.getName(), "onConsultantScheduleHyperlinkAction");
    }

    @FXML
    void onCountryListingHyperlinkAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onCountryListingHyperlinkAction", event);
        ManageCountries.loadIntoMainContent();
        LOG.exiting(LOG.getName(), "onCountryListingHyperlinkAction");
    }

    @FXML
    void onCustomerListingHyperlinkAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onCustomerListingHyperlinkAction", event);
        ManageCustomers.loadIntoMainContent(CustomerModel.FACTORY.getDefaultFilter());
        LOG.exiting(LOG.getName(), "onCustomerListingHyperlinkAction");
    }

    @FXML
    void onMyCurrentAndUpcomingHyperlinkAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onMyCurrentAndUpcomingHyperlinkAction", event);
        ManageAppointments.loadIntoMainContent(AppointmentModelFilter.myCurrentAndFuture());
        LOG.exiting(LOG.getName(), "onMyCurrentAndUpcomingHyperlinkAction");
    }

    @FXML
    void onNewAppointmentHyperlinkAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onNewAppointmentHyperlinkAction", event);
        try {
            EditAppointment.editNew(null, null, getScene().getWindow());
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error opening child window", ex);
        }
        LOG.exiting(LOG.getName(), "onNewAppointmentHyperlinkAction");
    }

    @FXML
    void onNextAppointmentCustomerHyperlinkAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onNextAppointmentCustomerHyperlinkAction", event);
        PartialCustomerModel<? extends PartialCustomerDAO> customer = nextAppointment.getCustomer();
        if (customer instanceof CustomerModel) {
            try {
                Window w = getScene().getWindow();
                EditCustomer.edit((CustomerModel) customer, w);
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, "Error opening child window", ex);
            }
        } else {
            MainController.startBusyTaskNow(new EditCustomerLoadTask(customer.getPrimaryKey()));
        }
        LOG.exiting(LOG.getName(), "onNextAppointmentCustomerHyperlinkAction");
    }

    @FXML
    void onNextAppointmentHyperlinkAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onNextAppointmentHyperlinkAction", event);
        try {
            Window w = getScene().getWindow();
            EditAppointment.edit(nextAppointment, w);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error opening child window", ex);
        }
        LOG.exiting(LOG.getName(), "onNextAppointmentHyperlinkAction");
    }

    @FXML
    void onRefreshButtonAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onRefreshButtonAction", event);
        NodeUtil.collapseNodes(nextAppointmentHyperlink, nextAppointmentAppendLabel, nextAppointmentCustomerHyperlink, nextAppointmentTerminalText);
        nextAppointmentLeadText.setText("Loading data. Please wait...");
        MainController.startBusyTaskNow(new OverviewLoadTask());
        LOG.exiting(LOG.getName(), "onRefreshButtonAction");
    }

    @FXML
    void onTypesByMonthHyperlinkAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onTypesByMonthHyperlinkAction", event);
        Scheduler.getMainController().replaceContent(AppointmentTypesByMonth.create());
        LOG.exiting(LOG.getName(), "onTypesByMonthHyperlinkAction");
    }

    @FXML
    void onUserListingHyperlinkAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onUserListingHyperlinkAction", event);
        ManageUsers.loadIntoMainContent(UserModel.FACTORY.getDefaultFilter());
        LOG.exiting(LOG.getName(), "onUserListingHyperlinkAction");
    }


    @FXML // This method is called by the FXMLLoader when initialization is complete
    private void initialize() {
        assert nextAppointmentLeadText != null : "fx:id=\"nextAppointmentLeadText\" was not injected: check your FXML file 'Overview.fxml'.";
        assert nextAppointmentHyperlink != null : "fx:id=\"nextAppointmentHyperlink\" was not injected: check your FXML file 'Overview.fxml'.";
        assert nextAppointmentAppendLabel != null : "fx:id=\"nextAppointmentAppendLabel\" was not injected: check your FXML file 'Overview.fxml'.";
        assert nextAppointmentCustomerHyperlink != null : "fx:id=\"nextAppointmentCustomerHyperlink\" was not injected: check your FXML file 'Overview.fxml'.";
        assert nextAppointmentTerminalText != null : "fx:id=\"nextAppointmentTerminalText\" was not injected: check your FXML file 'Overview.fxml'.";
        assert appointmentsTodayLabel != null : "fx:id=\"appointmentsTodayLabel\" was not injected: check your FXML file 'Overview.fxml'.";
        assert appointmentsThisWeekLabel != null : "fx:id=\"appointmentsThisWeekLabel\" was not injected: check your FXML file 'Overview.fxml'.";
        assert appointmentsTomorrowLabel != null : "fx:id=\"appointmentsTomorrowLabel\" was not injected: check your FXML file 'Overview.fxml'.";
        assert appointmentsNextWeekLabel != null : "fx:id=\"appointmentsNextWeekLabel\" was not injected: check your FXML file 'Overview.fxml'.";
        assert appointmentsThisMonthLabel != null : "fx:id=\"appointmentsThisMonthLabel\" was not injected: check your FXML file 'Overview.fxml'.";
        assert appointmentsNextMonthLabel != null : "fx:id=\"appointmentsNextMonthLabel\" was not injected: check your FXML file 'Overview.fxml'.";

        MainController.startBusyTaskNow(new OverviewLoadTask());
    }

    private class OverviewResult {

        private final int appointmentsToday;
        private final int appointmentsTomorrow;
        private final int appointmentsThisWeek;
        private final int appointmentsNextWeek;
        private final int appointmentsThisMonth;
        private final int appointmentsNextMonth;
        private final AppointmentDAO nextAppointment;

        private OverviewResult(int appointmentsToday, int appointmentsTomorrow, int appointmentsThisWeek, int appointmentsNextWeek, int appointmentsThisMonth,
                int appointmentsNextMonth, AppointmentDAO nextAppointment) {
            this.appointmentsToday = appointmentsToday;
            this.appointmentsTomorrow = appointmentsTomorrow;
            this.appointmentsThisWeek = appointmentsThisWeek;
            this.appointmentsNextWeek = appointmentsNextWeek;
            this.appointmentsThisMonth = appointmentsThisMonth;
            this.appointmentsNextMonth = appointmentsNextMonth;
            this.nextAppointment = nextAppointment;
        }
    }

    private class OverviewLoadTask extends Task<OverviewResult> {

        OverviewLoadTask() {
            updateTitle(resources.getString(RESOURCEKEY_GETTINGAPPOINTMENTCOUNTS));
        }

        @Override
        protected void succeeded() {
            OverviewResult result = getValue();
            NumberFormat nf = NumberFormat.getIntegerInstance();
            appointmentsTodayLabel.setText(nf.format(result.appointmentsToday));
            appointmentsTomorrowLabel.setText(nf.format(result.appointmentsTomorrow));
            appointmentsThisWeekLabel.setText(nf.format(result.appointmentsThisWeek));
            appointmentsNextWeekLabel.setText(nf.format(result.appointmentsNextWeek));
            appointmentsThisMonthLabel.setText(nf.format(result.appointmentsThisMonth));
            appointmentsNextMonthLabel.setText(nf.format(result.appointmentsNextMonth));
            if (null == result.nextAppointment) {
                nextAppointment = null;
                nextAppointmentLeadText.setText("You have no upcoming appointments.");
            } else {
                nextAppointment = result.nextAppointment.cachedModel(true);
                NodeUtil.restoreNodes(nextAppointmentHyperlink, nextAppointmentAppendLabel, nextAppointmentCustomerHyperlink, nextAppointmentTerminalText);
                LocalDateTime n = LocalDateTime.now();
                LocalDateTime s = nextAppointment.getStart();
                Duration t;
                StringBuilder sb = new StringBuilder();
                nextAppointmentLeadText.setText("The ");
                if (s.compareTo(n) > 0) {
                    t = Duration.between(s, n);
                    nextAppointmentHyperlink.setText("next appointment");
                    nextAppointmentCustomerHyperlink.setText(nextAppointment.getCustomerName());
                    sb.append(" occurs in ");
                } else {
                    nextAppointmentHyperlink.setText("current appointment");
                    sb.append(" ends in ");
                    LocalDateTime e = nextAppointment.getEnd();
                    t = Duration.between(e, n);
                }
                long i = t.toMinutes();
                if (i == 1) {
                    nextAppointmentTerminalText.setText(sb.append("1 minute.").toString());
                } else if (i < 60) {
                    nextAppointmentTerminalText.setText(sb.append(i).append(" minutes.").toString());
                } else {
                    int m = (int) (i % 60);
                    i = (i - m) / 60;
                    if (i == 1) {
                        switch (m) {
                            case 0:
                                nextAppointmentTerminalText.setText(sb.append("1 hour.").toString());
                                break;
                            case 1:
                                nextAppointmentTerminalText.setText(sb.append("1 hour and 1 minute.").toString());
                                break;
                            default:
                                nextAppointmentTerminalText.setText(sb.append("1 hour and ").append(m).append(" minutes.").toString());
                                break;
                        }
                    } else if (i < 24) {
                        switch (m) {
                            case 0:
                                nextAppointmentTerminalText.setText(sb.append(i).append(" hours.").toString());
                                break;
                            case 1:
                                nextAppointmentTerminalText.setText(sb.append(i).append(" hours and 1 minute.").toString());
                                break;
                            default:
                                nextAppointmentTerminalText.setText(sb.append(i).append(" hours and ").append(m).append(" minutes.").toString());
                                break;
                        }
                    } else {
                        int h = (int) (i % 24);
                        i = (i - h) / 24;
                        if (i == 1) {
                            switch (h) {
                                case 0:
                                    nextAppointmentTerminalText.setText(sb.append("1 day.").toString());
                                    break;
                                case 1:
                                    nextAppointmentTerminalText.setText(sb.append("1 day and 1 hour.").toString());
                                    break;
                                default:
                                    nextAppointmentTerminalText.setText(sb.append("1 day and ").append(m).append(" hours.").toString());
                                    break;
                            }
                        } else {
                            switch (h) {
                                case 0:
                                    nextAppointmentTerminalText.setText(sb.append(i).append(" days.").toString());
                                    break;
                                case 1:
                                    nextAppointmentTerminalText.setText(sb.append(i).append(" days and 1 hour.").toString());
                                    break;
                                default:
                                    nextAppointmentTerminalText.setText(sb.append(i).append(" hours and ").append(h).append(" hours.").toString());
                                    break;
                            }
                        }
                    }
                }
            }
        }

        @Override
        protected OverviewResult call() throws Exception {
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
            try (DbConnector dbConnector = new DbConnector()) {
                updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTEDTODB));
                AppointmentDAO.FactoryImpl factory = AppointmentDAO.FACTORY;
                final LocalDateTime start = LocalDateTime.now();
                LocalDateTime end = start.toLocalDate().atStartOfDay().plusDays(1);
                Month month = start.getMonth();
                while (end.getMonth() != month) {
                    end = end.plusDays(1);
                }
                int appointmentsThisMonth = factory.countByRange(dbConnector.getConnection(), start, end);
                int appointmentsNextMonth = factory.countByRange(dbConnector.getConnection(), end, end.plusMonths(1));
                int appointmentsToday;
                int appointmentsThisWeek;
                int appointmentsNextWeek;
                end = start.toLocalDate().atStartOfDay().plusDays(1);
                int appointmentsTomorrow = factory.countByRange(dbConnector.getConnection(), end, end.plusDays(1));
                while (end.getDayOfWeek() != DayOfWeek.SUNDAY) {
                    end = end.plusDays(1);
                }
                if (appointmentsThisMonth == 0) {
                    appointmentsToday = appointmentsThisWeek = 0;
                    appointmentsNextWeek = factory.countByRange(dbConnector.getConnection(), end, end.plusDays(7));
                } else {
                    appointmentsThisWeek = factory.countByRange(dbConnector.getConnection(), start, end);
                    appointmentsNextWeek = factory.countByRange(dbConnector.getConnection(), end, end.plusDays(7));
                    if (appointmentsThisWeek == 0) {
                        appointmentsToday = 0;
                    } else {
                        appointmentsToday = factory.countByRange(dbConnector.getConnection(), start, start.toLocalDate().atStartOfDay().plusDays(1));
                    }
                }
                Optional<AppointmentDAO> nextAppointment = factory.nextOnOrAfter(dbConnector.getConnection(), DateTimeUtil.toUtcTimestamp(start));
                return new OverviewResult(appointmentsToday, appointmentsTomorrow, appointmentsThisWeek, appointmentsNextWeek, appointmentsThisMonth, appointmentsNextMonth,
                        nextAppointment.orElse(null));
            }
        }
    }

    private class EditCustomerLoadTask extends Task<CustomerDAO> {

        private final int primaryKey;

        EditCustomerLoadTask(int primaryKey) {
            this.primaryKey = primaryKey;
            updateTitle("Loading customer record");
        }

        @Override
        protected void succeeded() {
            CustomerDAO result = getValue();
            Window w = getScene().getWindow();
            if (null != result) {
                try {
                    EditCustomer.edit(result.cachedModel(true), w);
                } catch (IOException ex) {
                    LOG.log(Level.SEVERE, "Error opening child window", ex);
                }
            } else {
                AlertHelper.showWarningAlert(w, "Not found", "Customer record was not found.", ButtonType.OK);
            }
        }

        @Override
        protected CustomerDAO call() throws Exception {
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
            try (DbConnector dbConnector = new DbConnector()) {
                updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTEDTODB));
                return CustomerDAO.FACTORY.loadByPrimaryKey(dbConnector.getConnection(), primaryKey).orElse(null);
            }
        }
    }

}
