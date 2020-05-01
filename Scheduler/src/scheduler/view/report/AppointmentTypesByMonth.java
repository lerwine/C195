package scheduler.view.report;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
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
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
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
import scheduler.dao.AppointmentCountByType;
import scheduler.dao.AppointmentDAO;
import scheduler.model.AppointmentType;
import scheduler.util.ViewControllerLoader;
import scheduler.view.ErrorDetailDialog;
import scheduler.view.MainController;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.FxmlViewEventHandling;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.annotations.HandlesFxmlViewEvent;
import scheduler.view.event.FxmlViewEvent;
import scheduler.view.task.TaskWaiter;

@GlobalizationResource("scheduler/view/report/Reports")
@FXMLResource("/scheduler/view/report/AppointmentTypesByMonth.fxml")
public class AppointmentTypesByMonth {

    public static AppointmentTypesByMonth loadInto(MainController mainController, Stage stage, Object loadEventListener) throws IOException {
        return mainController.loadContent(AppointmentTypesByMonth.class, loadEventListener);
    }

    public static AppointmentTypesByMonth loadInto(MainController mainController, Stage stage) throws IOException {
        return mainController.loadContent(AppointmentTypesByMonth.class);
    }

    private static final Logger LOG = Logger.getLogger(AppointmentTypesByMonth.class.getName());

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

    @FXML // fx:id="reportBarChart"
    private BarChart<String, Number> reportBarChart; // Value injected by FXMLLoader

    @FXML // fx:id="reportCategoryAxis"
    private CategoryAxis reportCategoryAxis; // Value injected by FXMLLoader

    @FXML // fx:id="reportNumberAxis"
    private NumberAxis reportNumberAxis; // Value injected by FXMLLoader

    private ObservableMap<AppointmentType, String> typeToTextMap;
    private XYChart.Series<String, Number> dataSeries;
    private LocalDate date;
    private NumberFormat numberFormat;

    @FXML
    private void onMonthComboBoxAction(ActionEvent event) {
        onDateChanged(yearSpinner.getValue());
    }

    @FXML
    private void onRunButtonAction(ActionEvent event) {
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
        assert yearSpinner != null : "fx:id=\"yearSpinner\" was not injected: check your FXML file 'AppointmentTypesByMonth.fxml'.";
        assert monthComboBox != null : "fx:id=\"monthComboBox\" was not injected: check your FXML file 'AppointmentTypesByMonth.fxml'.";
        assert yearValidationLabel != null : "fx:id=\"yearValidationLabel\" was not injected: check your FXML file 'AppointmentTypesByMonth.fxml'.";
        assert runButton != null : "fx:id=\"runButton\" was not injected: check your FXML file 'AppointmentTypesByMonth.fxml'.";
        assert reportBarChart != null : "fx:id=\"reportBarChart\" was not injected: check your FXML file 'AppointmentTypesByMonth.fxml'.";
        assert reportCategoryAxis != null : "fx:id=\"reportCategoryAxis\" was not injected: check your FXML file 'AppointmentTypesByMonth.fxml'.";
        assert reportNumberAxis != null : "fx:id=\"reportNumberAxis\" was not injected: check your FXML file 'AppointmentTypesByMonth.fxml'.";

        typeToTextMap = FXCollections.observableHashMap();
        ObservableList<String> categories = FXCollections.observableArrayList();
        Arrays.stream(AppointmentType.values()).forEach((t) -> {
            String s = AppointmentType.toDisplayText(t);
            categories.add(s);
            typeToTextMap.put(t, s);
        });
        reportCategoryAxis.setCategories(categories);
        dataSeries = new XYChart.Series();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM");
        ObservableList<String> monthNames = FXCollections.observableArrayList();
        int y = LocalDate.now().getYear();
        for (int i = 1; i < 13; i++) {
            monthNames.add(formatter.format(LocalDate.of(y, i, 1)));
        }
        monthComboBox.setItems(monthNames);
        date = LocalDate.now().withDayOfMonth(1);
        //dateConverter = new LocalDateStringConverter(FormatStyle.SHORT, null, Chronology.from(d));
        numberFormat = NumberFormat.getIntegerInstance();
        monthComboBox.getSelectionModel().select(date.getMonthValue() - 1);
        yearSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(LocalDate.MIN.getYear(),
                LocalDate.MAX.getYear(), date.getYear()));
        reportBarChart.getData().add(dataSeries);
        yearSpinner.valueProperty().addListener((observable) -> {
            onDateChanged(((ReadOnlyObjectProperty<Integer>) observable).get());
        });
    }

    @HandlesFxmlViewEvent(FxmlViewEventHandling.BEFORE_SHOW)
    private void onBeforeShow(FxmlViewEvent<? extends Parent> event) {
        TaskWaiter.startNow(new CountLoadTask(event.getStage(), date, monthComboBox.getValue()));
    }

    private class CountLoadTask extends TaskWaiter<List<AppointmentCountByType>> {

        private final LocalDate start;
        private final String monthName;

        private CountLoadTask(Stage owner, LocalDate start, String monthName) {
            super(owner, AppResources.getResourceString(AppResources.RESOURCEKEY_CONNECTINGTODB),
                    AppResources.getResourceString(AppResources.RESOURCEKEY_LOADINGAPPOINTMENTS));
            this.start = start.withDayOfMonth(1);
            this.monthName = monthName;
        }

        @Override
        protected void processResult(List<AppointmentCountByType> result, Stage stage) {
            ObservableList<XYChart.Data<String, Number>> data = dataSeries.getData();
            data.clear();
            HashMap<AppointmentType, Integer> types = new HashMap<>();
            result.forEach((t) -> types.put(t.getValue(), t.getCount()));
            Arrays.stream(AppointmentType.values()).forEach((t) -> {
                if (types.containsKey(t)) {
                    data.add(new XYChart.Data<>(typeToTextMap.get(t), types.get(t)));
                } else {
                    data.add(new XYChart.Data<>(typeToTextMap.get(t), 0));
                }
            });
            reportBarChart.setTitle(String.format(resources.getString("appointmentTypesForS"), monthName));
        }

        @Override
        protected void processException(Throwable ex, Stage stage) {
            ErrorDetailDialog.logShowAndWait(LOG, AppResources.getResourceString(RESOURCEKEY_DBREADERROR), stage, ex);
            stage.close();
        }

        @Override
        protected List<AppointmentCountByType> getResult(Connection connection) throws SQLException {
            updateMessage(AppResources.getResourceString(RESOURCEKEY_CONNECTEDTODB));
            return AppointmentDAO.getFactory().getCountsByType(connection, start.atStartOfDay(), start.plusMonths(1).atStartOfDay());
        }

    }

}
