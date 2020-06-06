package scheduler.view.report;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.VBox;
import scheduler.AppResourceKeys;
import static scheduler.AppResourceKeys.RESOURCEKEY_CONNECTEDTODB;
import scheduler.AppResources;
import scheduler.dao.AppointmentDAO;
import scheduler.dao.ItemCountResult;
import scheduler.util.DbConnector;
import scheduler.view.MainController;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.task.WaitTitledPane;

@GlobalizationResource("scheduler/view/report/Reports")
@FXMLResource("/scheduler/view/report/AppointmentsByRegion.fxml")
public class AppointmentsByRegion extends VBox {

    private static final Logger LOG = Logger.getLogger(AppointmentsByRegion.class.getName());

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
    private void onMonthComboBoxAction(ActionEvent event) {
        onDateChanged(yearSpinner.getValue());
    }

    @FXML
    private void onRunButtonAction(ActionEvent event) {
        MainController.startBusyTaskNow(new CountLoadTask(date, monthComboBox.getValue()));
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

    @SuppressWarnings("unchecked")
    @FXML // This method is called by the FXMLLoader when initialization is complete
    private void initialize() {
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

        WaitTitledPane pane = new WaitTitledPane();
        pane.addOnFailAcknowledged((evt) -> getScene().getWindow().hide())
                .addOnCancelAcknowledged((evt) -> getScene().getWindow().hide());
        MainController.startBusyTaskNow(pane, new CountLoadTask(date, monthComboBox.getValue()));
    }

    private class CountLoadTask extends Task<List<ItemCountResult<String>>> {

        private final LocalDate start;
        private final String monthName;

        private CountLoadTask(LocalDate start, String monthName) {
            updateTitle(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_LOADINGAPPOINTMENTS));
            this.start = start.withDayOfMonth(1);
            this.monthName = monthName;
        }

        @Override
        protected void succeeded() {
            super.succeeded();
            List<ItemCountResult<String>> result = getValue();
            HashMap<String, Integer> regions = new HashMap<>();
            result.forEach((t) -> regions.put(t.getValue(), t.getCount()));
//            ObservableMap<String, CountryDAO.PredefinedCountryElement> countryMap = PredefinedData.getCountryMap();
//            pieChartData.clear();
//            countryMap.keySet().forEach((t) -> {
//                if (regions.containsKey(t)) {
//                    pieChartData.add(new PieChart.Data(countryMap.get(t).getLocale().getDisplayCountry(), regions.get(t)));
//                } else {
//                    pieChartData.add(new PieChart.Data(countryMap.get(t).getLocale().getDisplayCountry(), 0));
//                }
//            });
//            reportPieChart.setTitle(String.format(resources.getString("appointmentRegionsForS"), monthName));
        }

        @Override
        protected List<ItemCountResult<String>> call() throws Exception {
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
            try (DbConnector dbConnector = new DbConnector()) {
                updateMessage(AppResources.getResourceString(RESOURCEKEY_CONNECTEDTODB));
                return AppointmentDAO.FACTORY.getCountsByCustomerRegion(dbConnector.getConnection(), start.atStartOfDay(), start.plusMonths(1).atStartOfDay());
            }
        }

    }
}
