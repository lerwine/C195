package scheduler.view.report;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import scheduler.AppResourceKeys;
import static scheduler.AppResourceKeys.RESOURCEKEY_CONNECTEDTODB;
import scheduler.AppResources;
import scheduler.Scheduler;
import scheduler.dao.AppointmentDAO;
import scheduler.dao.UserDAO;
import scheduler.dao.filter.AppointmentFilter;
import scheduler.model.fx.AppointmentModel;
import scheduler.model.fx.UserModel;
import scheduler.util.DateTimeUtil;
import scheduler.util.DbConnector;
import static scheduler.util.NodeUtil.clearAndSelect;
import scheduler.view.MainController;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.task.WaitTitledPane;

@GlobalizationResource("scheduler/view/report/Reports")
@FXMLResource("/scheduler/view/report/ConsultantSchedule.fxml")
public class ConsultantSchedule extends VBox {

    private static final Logger LOG = Logger.getLogger(ConsultantSchedule.class.getName());

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // fx:id="rangeStartDatePicker"
    private DatePicker rangeStartDatePicker; // Value injected by FXMLLoader

    @FXML // fx:id="rangeEndDatePicker"
    private DatePicker rangeEndDatePicker; // Value injected by FXMLLoader

    @FXML // fx:id="rangeValidationLabel"
    private Label rangeValidationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="consultantsComboBox"
    private ComboBox<UserModel> consultantsComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="consultantValidationLabel"
    private Label consultantValidationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="runButton"
    private Button runButton; // Value injected by FXMLLoader

    @FXML // fx:id="appointmentScheduleListView"
    private ListView<DailyAppointmentsBorderPane> appointmentScheduleListView; // Value injected by FXMLLoader

    private ObservableList<DailyAppointmentsBorderPane> appointmentsByDay;

    private ObservableList<UserModel> consultantList;

    @FXML
    private void onParameterAction(ActionEvent event) {
        LocalDate start = rangeStartDatePicker.getValue();
        LocalDate end = rangeEndDatePicker.getValue();
        if (null == start || null == end) {
            rangeValidationLabel.setVisible(true);
            rangeValidationLabel.setText(resources.getString("required"));
        } else if (end.compareTo(start) < 0) {
            rangeValidationLabel.setVisible(true);
            rangeValidationLabel.setText(resources.getString("endCannotBeBeforeStart"));
        } else {
            rangeValidationLabel.setVisible(true);
            if (null == consultantsComboBox.getValue()) {
                consultantValidationLabel.setVisible(true);
                runButton.setDisable(true);
            } else {
                consultantValidationLabel.setVisible(false);
                runButton.setDisable(false);
            }
            return;
        }
        consultantValidationLabel.setVisible(true);
        runButton.setDisable(null == consultantsComboBox.getValue());
    }

    @FXML
    private void onRunButtonAction(ActionEvent event) {
        MainController.startBusyTaskNow(new AppointmentReloadTask());
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    private void initialize() {
        assert rangeStartDatePicker != null : "fx:id=\"rangeStartDatePicker\" was not injected: check your FXML file 'ConsultantSchedule.fxml'.";
        assert rangeEndDatePicker != null : "fx:id=\"rangeEndDatePicker\" was not injected: check your FXML file 'ConsultantSchedule.fxml'.";
        assert rangeValidationLabel != null : "fx:id=\"rangeValidationLabel\" was not injected: check your FXML file 'ConsultantSchedule.fxml'.";
        assert runButton != null : "fx:id=\"runButton\" was not injected: check your FXML file 'ConsultantSchedule.fxml'.";
        assert appointmentScheduleListView != null : "fx:id=\"appointmentScheduleListView\" was not injected: check your FXML file 'ConsultantSchedule.fxml'.";

        appointmentsByDay = FXCollections.observableArrayList();
        consultantList = FXCollections.observableArrayList();
        consultantsComboBox.setItems(consultantList);
        appointmentScheduleListView.setItems(appointmentsByDay);
        WaitTitledPane pane = new WaitTitledPane();
        pane.addOnFailAcknowledged((evt) -> getScene().getWindow().hide())
                .addOnCancelAcknowledged((evt) -> getScene().getWindow().hide());
        MainController.startBusyTaskNow(pane, new InitializeTask());
    }

    public void accept(List<? extends AppointmentDAO> appointments) {
        if (null != appointments && !appointments.isEmpty()) {
            HashMap<LocalDate, ArrayList<AppointmentModel>> byDate = new HashMap<>();
            appointments.forEach((t) -> {
                AppointmentModel am = t.cachedModel(true);
                LocalDate d = am.getStart().toLocalDate();
                if (byDate.containsKey(d)) {
                    byDate.get(d).add(am);
                } else {
                    ArrayList<AppointmentModel> al = new ArrayList<>();
                    al.add(am);
                    byDate.put(d, al);
                }
            });
            byDate.keySet().stream().sorted().forEach((t) -> appointmentsByDay.add(new DailyAppointmentsBorderPane(t, byDate.get(t))));
        }
    }

    private class InitializeTask extends Task<List<UserDAO>> {

        private List<AppointmentDAO> appointments;
        private final LocalDate start;
        private final UserDAO user;

        private InitializeTask() {
            updateTitle(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_LOADINGUSERS));
            start = LocalDate.now();
            user = Scheduler.getCurrentUser();
        }

        @Override
        protected void succeeded() {
            super.succeeded();
            List<UserDAO> result = getValue();
            rangeStartDatePicker.setValue(start);
            rangeEndDatePicker.setValue(start);
            if (null != result && !result.isEmpty()) {
                result.forEach((t) -> consultantList.add(t.cachedModel(true)));
                clearAndSelect(consultantsComboBox, user);
            }

            accept(appointments);
        }

        @Override
        protected List<UserDAO> call() throws Exception {
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
            try (DbConnector dbConnector = new DbConnector()) {
                updateMessage(AppResources.getResourceString(RESOURCEKEY_CONNECTEDTODB));
                appointments = AppointmentDAO.FACTORY.load(dbConnector.getConnection(), AppointmentFilter.of(null, user,
                        DateTimeUtil.toUtcTimestamp(start.atStartOfDay()), DateTimeUtil.toUtcTimestamp(start.plusDays(1L).atStartOfDay())));
                UserDAO.FactoryImpl uf = UserDAO.FACTORY;
                return uf.load(dbConnector.getConnection(), uf.getAllItemsFilter());
            }
        }

    }

    private class AppointmentReloadTask extends Task<List<AppointmentDAO>> {

        private final LocalDate start;
        private final LocalDate end;
        private final UserDAO user;

        private AppointmentReloadTask() {
            updateTitle(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_LOADINGUSERS));
            start = rangeStartDatePicker.getValue();
            end = rangeEndDatePicker.getValue();
            user = consultantsComboBox.getValue().dataObject();
        }

        @Override
        protected void succeeded() {
            super.succeeded();
            accept(getValue());
        }

        @Override
        protected List<AppointmentDAO> call() throws Exception {
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
            try (DbConnector dbConnector = new DbConnector()) {
                updateMessage(AppResources.getResourceString(RESOURCEKEY_CONNECTEDTODB));
                return AppointmentDAO.FACTORY.load(dbConnector.getConnection(), AppointmentFilter.of(null, user,
                        DateTimeUtil.toUtcTimestamp(start.atStartOfDay()), DateTimeUtil.toUtcTimestamp(end.plusDays(1L).atStartOfDay())));
            }
        }

    }

}
