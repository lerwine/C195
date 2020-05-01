package scheduler.view.report;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import static scheduler.AppResourceKeys.RESOURCEKEY_CONNECTEDTODB;
import static scheduler.AppResourceKeys.RESOURCEKEY_DBREADERROR;
import scheduler.AppResources;
import scheduler.Scheduler;
import scheduler.dao.AppointmentDAO;
import scheduler.dao.UserDAO;
import scheduler.dao.filter.AppointmentFilter;
import scheduler.util.DB;
import scheduler.view.ErrorDetailDialog;
import scheduler.view.MainController;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.FxmlViewEventHandling;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.annotations.HandlesFxmlViewEvent;
import scheduler.view.appointment.AppointmentModel;
import scheduler.view.event.FxmlViewEvent;
import scheduler.view.task.TaskWaiter;
import scheduler.view.user.UserModel;

@GlobalizationResource("scheduler/view/report/Reports")
@FXMLResource("/scheduler/view/report/ConsultantSchedule.fxml")
public class ConsultantSchedule {

    public static ConsultantSchedule loadInto(MainController mainController, Stage stage, Object loadEventListener) throws IOException {
        return mainController.loadContent(ConsultantSchedule.class, loadEventListener);
    }

    public static ConsultantSchedule loadInto(MainController mainController, Stage stage) throws IOException {
        return mainController.loadContent(ConsultantSchedule.class);
    }

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
        TaskWaiter.startNow(new AppointmentReloadTask((Stage) ((Button) event.getSource()).getScene().getWindow()));
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
    }

    @SuppressWarnings("unchecked")
    @HandlesFxmlViewEvent(FxmlViewEventHandling.BEFORE_SHOW)
    private void onFxmlViewEvent(FxmlViewEvent<VBox> event) {
        TaskWaiter.startNow(new InitializeTask(event.getStage()));
    }

    public void accept(List<? extends AppointmentDAO> appointments) {
        if (null != appointments && !appointments.isEmpty()) {
            HashMap<LocalDate, ArrayList<AppointmentModel>> byDate = new HashMap<>();
            appointments.forEach((t) -> {
                AppointmentModel am = new AppointmentModel(t);
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

    private class InitializeTask extends TaskWaiter<List<UserDAO>> {

        private List<AppointmentDAO> appointments;
        private final LocalDate start;
        private final UserDAO user;

        private InitializeTask(Stage owner) {
            super(owner, AppResources.getResourceString(AppResources.RESOURCEKEY_CONNECTINGTODB),
                    AppResources.getResourceString(AppResources.RESOURCEKEY_LOADINGUSERS));
            start = LocalDate.now();
            user = Scheduler.getCurrentUser();
        }

        @Override
        protected void processResult(List<UserDAO> result, Stage stage) {
            rangeStartDatePicker.setValue(start);
            rangeEndDatePicker.setValue(start);
            if (null != result && !result.isEmpty()) {
                result.forEach((t) -> consultantList.add(new UserModel(t)));
                int id = user.getPrimaryKey();
                Optional<UserModel> um = consultantList.stream().filter((t) -> t.getPrimaryKey() == id).findFirst();
                um.ifPresent((t) -> consultantsComboBox.getSelectionModel().select(t));
            }

            accept(appointments);
        }

        @Override
        protected void processException(Throwable ex, Stage stage) {
            ErrorDetailDialog.logShowAndWait(LOG, AppResources.getResourceString(RESOURCEKEY_DBREADERROR), stage, ex);
            stage.close();
        }

        @Override
        protected List<UserDAO> getResult(Connection connection) throws SQLException {
            updateMessage(AppResources.getResourceString(RESOURCEKEY_CONNECTEDTODB));
            appointments = AppointmentDAO.getFactory().load(connection, AppointmentFilter.of(null, user,
                    DB.toUtcTimestamp(start.atStartOfDay()), DB.toUtcTimestamp(start.plusDays(1L).atStartOfDay())));
            UserDAO.FactoryImpl uf = UserDAO.getFactory();
            return uf.load(connection, uf.getAllItemsFilter());
        }

    }

    private class AppointmentReloadTask extends TaskWaiter<List<AppointmentDAO>> {

        private final LocalDate start;
        private final LocalDate end;
        private final UserDAO user;

        private AppointmentReloadTask(Stage owner) {
            super(owner, AppResources.getResourceString(AppResources.RESOURCEKEY_CONNECTINGTODB),
                    AppResources.getResourceString(AppResources.RESOURCEKEY_LOADINGUSERS));
            start = rangeStartDatePicker.getValue();
            end = rangeEndDatePicker.getValue();
            user = consultantsComboBox.getValue().getDataObject();
        }

        @Override
        protected void processResult(List<AppointmentDAO> result, Stage stage) {
            accept(result);
        }

        @Override
        protected void processException(Throwable ex, Stage stage) {
            ErrorDetailDialog.logShowAndWait(LOG, AppResources.getResourceString(RESOURCEKEY_DBREADERROR), stage, ex);
            stage.close();
        }

        @Override
        protected List<AppointmentDAO> getResult(Connection connection) throws SQLException {
            updateMessage(AppResources.getResourceString(RESOURCEKEY_CONNECTEDTODB));
            return AppointmentDAO.getFactory().load(connection, AppointmentFilter.of(null, user,
                    DB.toUtcTimestamp(start.atStartOfDay()), DB.toUtcTimestamp(end.plusDays(1L).atStartOfDay())));
        }

    }

}
