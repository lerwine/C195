package scheduler.view.appointment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.HBox;
import scheduler.Scheduler;
import scheduler.fx.CssClassName;
import scheduler.model.fx.AppointmentModel;
import scheduler.util.LogHelper;
import scheduler.util.NodeUtil;
import scheduler.view.annotations.GlobalizationResource;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/view/appointment/ManageAppointments")
public final class ByMonth extends HBox {

    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(ByMonth.class.getName()), Level.FINE);
//    private static final Logger LOG = Logger.getLogger(ByMonth.class.getName());

    public static ByMonth loadIntoMainContent(LocalDate month) {
        ByMonth newContent = new ByMonth();
        newContent.monthStart = ((null == month) ? LocalDate.now() : month).withDayOfMonth(1);
        newContent.initialize();
        Scheduler.getMainController().replaceContent(newContent);
        return newContent;
    }

    private LocalDate monthStart;
    ObservableList<AppointmentModel> allAppointments;

//    private ResourceBundle resources;
    private final Spinner<Integer> yearSpinner;

    private final ComboBox<String> monthComboBox;

    private final Button runButton;

    private final Label monthLabel;
    
    private final MonthGridPane monthGridPane;
    private Object SpinnerValueFactory;

    public ByMonth() {
        yearSpinner = NodeUtil.setCssClass(new Spinner<>(LocalDateTime.MIN.getYear() + 1, LocalDateTime.MAX.getYear() - 1, LocalDateTime.now().getYear()),
                CssClassName.LEFTLABELEDCONTROL);
        monthComboBox = NodeUtil.setCssClass(new ComboBox<>(), CssClassName.LEFTLABELEDCONTROL);
        runButton = NodeUtil.createButton("Run", this::onRunButtonAction);
        monthLabel = NodeUtil.createLabel("Month: ", CssClassName.TOPCONTROLLABEL);
        monthGridPane = new MonthGridPane();
        
        ObservableList<Node> children = getChildren();
        children.addAll(
                NodeUtil.createFillingHBox(
                        NodeUtil.createLabel("Year: ", CssClassName.LEFTCONTROLLABEL),
                        yearSpinner,
                        NodeUtil.createLabel("Month: ", CssClassName.INNERLEFTCONTROLLABEL),
                        monthComboBox,
                        runButton
                ),
                monthLabel,
                NodeUtil.setCssClass(monthGridPane, CssClassName.TOPLABELEDCONTROL)
        );
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    private void initialize() {
        LOG.entering(LOG.getName(), "initialize");
        int y = yearSpinner.getValue();
        if (y < monthStart.getYear()) {
            do {
                yearSpinner.increment();
            } while (y < monthStart.getYear());
        } else if (y > monthStart.getYear()) {
            do {
                yearSpinner.decrement();
            } while (y > monthStart.getYear());
        }
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMMM");
        ObservableList<String> monthNames = FXCollections.observableArrayList();
        for (int i = 1; i < 13; i++) {
            monthNames.add(fmt.format(monthStart.withMonth(i)));
        }
        monthComboBox.setItems(monthNames);
        allAppointments = FXCollections.observableArrayList();
        monthGridPane.setItems(allAppointments);
//        yearSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(LocalDate.MIN.getYear(),
//                LocalDate.MAX.getYear(), d.getYear()));
        monthComboBox.setItems(monthNames);
        monthComboBox.getSelectionModel().clearAndSelect(monthStart.getMonthValue() - 1);
        monthLabel.setText(monthStart.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()));
        loadAppointments(yearSpinner.getValue(), monthComboBox.getSelectionModel().getSelectedIndex() + 1);
        LOG.exiting(LOG.getName(), "initialize");
    }

    @FXML
    private void onRunButtonAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onRunButtonAction", event);
        loadAppointments(yearSpinner.getValue(), monthComboBox.getSelectionModel().getSelectedIndex() + 1);
        LOG.exiting(LOG.getName(), "onRunButtonAction");
    }

    private void loadAppointments(int year, int month) {
        LOG.entering(LOG.getName(), "loadAppointments", new Object[]{year, month});
        LocalDate d = LocalDate.of(year, Month.of(month), 1);
        monthLabel.setText(d.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()));
//        AppointmentModel.FACTORY.loadAsync(AppointmentFilter.of(AppointmentFilter.expressionOf(DB.toUtcTimestamp(d.atStartOfDay()),
//                DB.toUtcTimestamp(d.plusMonths(1).atStartOfDay()))), allAppointments, (t) -> {
//            allAppointments.clear();
//            allAppointments.addAll(t);
//        });
    }

}
