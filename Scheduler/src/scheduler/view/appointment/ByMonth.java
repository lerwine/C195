package scheduler.view.appointment;

import java.io.IOException;
import java.text.NumberFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import static scheduler.AppResourceKeys.RESOURCEKEY_DBREADERROR;
import scheduler.AppResources;
import scheduler.dao.filter.AppointmentFilter;
import scheduler.util.DB;
import scheduler.util.EventHelper;
import scheduler.view.ErrorDetailDialog;
import scheduler.view.MainController;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.FxmlViewEventHandling;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.annotations.HandlesFxmlViewEvent;
import scheduler.view.event.FxmlViewControllerEventListener;
import scheduler.view.event.FxmlViewEvent;
import scheduler.view.event.FxmlViewEventType;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/view/appointment/ManageAppointments")
@FXMLResource("/scheduler/view/appointment/ByMonth.fxml")
public class ByMonth {

    private static final Logger LOG = Logger.getLogger(ByMonth.class.getName());

    private LocalDate monthStart;
    ObservableList<AppointmentModel> allAppointments;

    public static ByMonth loadInto(MainController mainController, Stage stage, LocalDate month,
            Object loadEventListener) throws IOException {
        return mainController.loadContent(ByMonth.class, (FxmlViewControllerEventListener<Parent, ByMonth>) (event) -> {
            if (event.getType() == FxmlViewEventType.LOADED) {
                event.getController().monthStart = ((null == month) ? LocalDate.now() : month).withDayOfMonth(1);
            }

            EventHelper.fireFxmlViewEvent(loadEventListener, event);
        });
    }

    public static ByMonth loadInto(MainController mainController, Stage stage, LocalDate month) throws IOException {
        return loadInto(mainController, stage, month, null);
    }

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
    void onRunButtonAction(ActionEvent event) {
        monthNameLabel.setText(monthComboBox.getValue());
        loadAppointments(yearSpinner.getValue(), monthComboBox.getSelectionModel().getSelectedIndex() + 1,
                (Stage)((Button)event.getSource()).getScene().getWindow());
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
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
    }

    @HandlesFxmlViewEvent(FxmlViewEventHandling.BEFORE_SHOW)
    private void onBeforeShow(FxmlViewEvent<? extends Parent> event) {
        loadAppointments(yearSpinner.getValue(), monthComboBox.getSelectionModel().getSelectedIndex() + 1, event.getStage());
    }

    private void loadAppointments(int year, int month, Stage stage) {
        LocalDate d = LocalDate.of(year, Month.of(month), 1);
        AppointmentModel.getFactory().loadAsync(stage,
                AppointmentFilter.of(AppointmentFilter.expressionOf(DB.toUtcTimestamp(d.atStartOfDay()),
                        DB.toUtcTimestamp(d.plusMonths(1).atStartOfDay()))), allAppointments, (t) -> {
            allAppointments.clear();
            allAppointments.addAll(t);
        }, (Throwable t) -> {
            ErrorDetailDialog.logShowAndWait(LOG, AppResources.getResourceString(RESOURCEKEY_DBREADERROR), stage, t);
        });
    }

}
