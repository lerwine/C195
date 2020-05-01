package scheduler.view.report;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import static scheduler.AppResourceKeys.RESOURCEKEY_CONNECTEDTODB;
import static scheduler.AppResourceKeys.RESOURCEKEY_DBREADERROR;
import scheduler.AppResources;
import scheduler.dao.AppointmentDAO;
import scheduler.dao.ItemCountResult;
import scheduler.model.predefined.PredefinedCountry;
import scheduler.model.predefined.PredefinedData;
import scheduler.view.ErrorDetailDialog;
import scheduler.view.MainController;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.FxmlViewEventHandling;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.annotations.HandlesFxmlViewEvent;
import scheduler.view.event.FxmlViewEvent;
import scheduler.view.task.TaskWaiter;

@GlobalizationResource("scheduler/view/report/Reports")
@FXMLResource("/scheduler/view/report/AppointmentsByRegion.fxml")
public class AppointmentsByRegion {

    private static final Logger LOG = Logger.getLogger(AppointmentsByRegion.class.getName());

    public static AppointmentsByRegion loadInto(MainController mainController, Stage stage, Object loadEventListener) throws IOException {
        return mainController.loadContent(AppointmentsByRegion.class, loadEventListener);
    }

    public static AppointmentsByRegion loadInto(MainController mainController, Stage stage) throws IOException {
        return mainController.loadContent(AppointmentsByRegion.class);
    }

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // fx:id="yearSpinner"
    private Spinner<Integer> yearSpinner; // Value injected by FXMLLoader

    @FXML // fx:id="monthComboBox"
    private ComboBox<String> monthComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="yearValidationLabel"
    private Label yearValidationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="runButton"
    private Button runButton; // Value injected by FXMLLoader

    @FXML // fx:id="reportPieChart"
    private PieChart reportPieChart; // Value injected by FXMLLoader

    private LocalDate date;
    ObservableList<PieChart.Data> pieChartData;

    @FXML
    void onMonthComboBoxAction(ActionEvent event) {
        onDateChanged(yearSpinner.getValue());
    }

    @FXML
    void onRunButtonAction(ActionEvent event) {
        TaskWaiter.startNow(new CountLoadTask((Stage) ((Button) event.getSource()).getScene().getWindow(), date, monthComboBox.getValue()));
    }

    private void onDateChanged(Integer year) {
        if (null == year) {
            yearValidationLabel.setVisible(true);
        } else {
            yearValidationLabel.setVisible(false);
            yearValidationLabel.setVisible(false);
            runButton.setDisable(false);
            date = LocalDate.of(year, monthComboBox.getSelectionModel().getSelectedIndex() + 1, 1);
        }
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert yearSpinner != null : "fx:id=\"yearSpinner\" was not injected: check your FXML file 'AppointmentsByRegion.fxml'.";
        assert monthComboBox != null : "fx:id=\"monthComboBox\" was not injected: check your FXML file 'AppointmentsByRegion.fxml'.";
        assert yearValidationLabel != null : "fx:id=\"yearValidationLabel\" was not injected: check your FXML file 'AppointmentsByRegion.fxml'.";
        assert runButton != null : "fx:id=\"runButton\" was not injected: check your FXML file 'AppointmentsByRegion.fxml'.";
        assert reportPieChart != null : "fx:id=\"reportPieChart\" was not injected: check your FXML file 'AppointmentsByRegion.fxml'.";

        date = LocalDate.now().withDayOfMonth(1);
        pieChartData = FXCollections.observableArrayList();
        reportPieChart.setData(pieChartData);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM");
        ObservableList<String> monthNames = FXCollections.observableArrayList();
        int y = LocalDate.now().getYear();
        for (int i = 1; i < 13; i++) {
            monthNames.add(formatter.format(LocalDate.of(y, i, 1)));
        }
        monthComboBox.setItems(monthNames);
        monthComboBox.getSelectionModel().select(date.getMonthValue() - 1);
        yearSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(LocalDate.MIN.getYear(),
                LocalDate.MAX.getYear(), date.getYear()));
        yearSpinner.valueProperty().addListener((observable) -> {
            onDateChanged(((ReadOnlyObjectProperty<Integer>) observable).get());
        });
    }

    @HandlesFxmlViewEvent(FxmlViewEventHandling.BEFORE_SHOW)
    private void onBeforeShow(FxmlViewEvent<? extends Parent> event) {
        TaskWaiter.startNow(new CountLoadTask(event.getStage(), date, monthComboBox.getValue()));
    }

    private class CountLoadTask extends TaskWaiter<List<ItemCountResult<String>>> {

        private final LocalDate start;
        private final String monthName;

        private CountLoadTask(Stage owner, LocalDate start, String monthName) {
            super(owner, AppResources.getResourceString(AppResources.RESOURCEKEY_CONNECTINGTODB),
                    AppResources.getResourceString(AppResources.RESOURCEKEY_LOADINGAPPOINTMENTS));
            this.start = start.withDayOfMonth(1);
            this.monthName = monthName;
        }

        @Override
        protected void processResult(List<ItemCountResult<String>> result, Stage stage) {
            HashMap<String, Integer> regions = new HashMap<>();
            result.forEach((t) -> regions.put(t.getValue(), t.getCount()));
            ObservableMap<String, PredefinedCountry> countryMap = PredefinedData.getCountryMap();
            pieChartData.clear();
            countryMap.keySet().forEach((t) -> {
                if (regions.containsKey(t)) {
                    pieChartData.add(new PieChart.Data(countryMap.get(t).getName(), regions.get(t)));
                } else {
                    pieChartData.add(new PieChart.Data(countryMap.get(t).getName(), 0));
                }
            });
            reportPieChart.setTitle(String.format(resources.getString("appointmentRegionsForS"), monthName));
        }

        @Override
        protected void processException(Throwable ex, Stage stage) {
            ErrorDetailDialog.logShowAndWait(LOG, AppResources.getResourceString(RESOURCEKEY_DBREADERROR), stage, ex);
            stage.close();
        }

        @Override
        protected List<ItemCountResult<String>> getResult(Connection connection) throws SQLException {
            updateMessage(AppResources.getResourceString(RESOURCEKEY_CONNECTEDTODB));
            return AppointmentDAO.getFactory().getCountsByCustomerRegion(connection, start.atStartOfDay(), start.plusMonths(1).atStartOfDay());
        }

    }
}
