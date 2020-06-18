package scheduler.view.appointment;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.StackPane;
import scheduler.Scheduler;
import scheduler.model.ui.AppointmentModel;
import scheduler.util.ViewControllerLoader;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/view/appointment/ManageAppointments")
@FXMLResource("/scheduler/view/appointment/ByMonth.fxml")
public class ByMonth extends StackPane {

    private static final Logger LOG = Logger.getLogger(ByMonth.class.getName());

    public static ByMonth loadIntoMainContent(LocalDate month) {
        ByMonth newContent = new ByMonth();
        newContent.monthStart = ((null == month) ? LocalDate.now() : month).withDayOfMonth(1);
        try {
            ViewControllerLoader.initializeCustomControl(newContent);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error loading view", ex);
            throw new InternalError("Error loading view", ex);
        }
        Scheduler.getMainController().replaceContent(newContent);
        return newContent;
    }

    private LocalDate monthStart;
    ObservableList<AppointmentModel> allAppointments;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // fx:id="yearSpinner"
    private Spinner<Integer> yearSpinner; // Value injected by FXMLLoader

    @FXML // fx:id="monthComboBox"
    private ComboBox<String> monthComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="runButton"
    private Button runButton; // Value injected by FXMLLoader

    @FXML // fx:id="monthNameLabel"
    private Label monthNameLabel; // Value injected by FXMLLoader

    @FXML // fx:id="monthGridPane"
    private MonthGridPane monthGridPane; // Value injected by FXMLLoader
    private Object SpinnerValueFactory;

    @FXML
    private void onRunButtonAction(ActionEvent event) {
        monthNameLabel.setText(monthComboBox.getValue());
        loadAppointments(yearSpinner.getValue(), monthComboBox.getSelectionModel().getSelectedIndex() + 1);
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    private void initialize() {
        assert yearSpinner != null : "fx:id=\"yearSpinner\" was not injected: check your FXML file 'ByMonth.fxml'.";
        assert monthComboBox != null : "fx:id=\"monthComboBox\" was not injected: check your FXML file 'ByMonth.fxml'.";
        assert runButton != null : "fx:id=\"runButton\" was not injected: check your FXML file 'ByMonth.fxml'.";
        assert monthNameLabel != null : "fx:id=\"monthNameLabel\" was not injected: check your FXML file 'ByMonth.fxml'.";
        assert monthGridPane != null : "fx:id=\"monthGridPane\" was not injected: check your FXML file 'ByMonth.fxml'.";

        allAppointments = FXCollections.observableArrayList();
        monthGridPane.setItems(allAppointments);
        LocalDate d = LocalDate.now().withDayOfMonth(1);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMMM");
        ObservableList<String> monthNames = FXCollections.observableArrayList();
        for (int i = 1; i < 13; i++) {
            monthNames.add(fmt.format(d.withMonth(i)));
        }
        d = LocalDate.now();
        yearSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(LocalDate.MIN.getYear(),
                LocalDate.MAX.getYear(), d.getYear()));
        monthComboBox.setItems(monthNames);
        monthComboBox.getSelectionModel().select(d.getMonthValue() - 1);
        monthNameLabel.setText(monthComboBox.getValue());

        loadAppointments(yearSpinner.getValue(), monthComboBox.getSelectionModel().getSelectedIndex() + 1);
    }

    private void loadAppointments(int year, int month) {
        LocalDate d = LocalDate.of(year, Month.of(month), 1);
//        AppointmentModel.FACTORY.loadAsync(AppointmentFilter.of(AppointmentFilter.expressionOf(DB.toUtcTimestamp(d.atStartOfDay()),
//                DB.toUtcTimestamp(d.plusMonths(1).atStartOfDay()))), allAppointments, (t) -> {
//            allAppointments.clear();
//            allAppointments.addAll(t);
//        });
    }

}
