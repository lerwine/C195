package scheduler.view.appointment;

import java.io.IOException;
import java.text.NumberFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
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
@GlobalizationResource("scheduler/view/appointment/Calendar")
@FXMLResource("/scheduler/view/appointment/ByMonth.fxml")
public class ByMonth {

    private static final Logger LOG = Logger.getLogger(ByMonth.class.getName());

    private LocalDate monthStart;
    ObservableList<AppointmentModel> allAppointments;
    private ObservableList<ObservableList<AppointmentModel>> itemsLists;
    private ObservableList<ListView<AppointmentModel>> listViews;

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

//    @FXML // URL location of the FXML file that was given to the FXMLLoader
//    private URL location;
    @FXML // fx:id="monthNameLabel"
    private Label monthNameLabel; // Value injected by FXMLLoader

    @FXML // fx:id="monthGridPane"
    private GridPane monthGridPane; // Value injected by FXMLLoader

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert monthNameLabel != null : "fx:id=\"monthNameLabel\" was not injected: check your FXML file 'ByMonth.fxml'.";
        assert monthGridPane != null : "fx:id=\"monthGridPane\" was not injected: check your FXML file 'ByMonth.fxml'.";

    }

    @HandlesFxmlViewEvent(FxmlViewEventHandling.BEFORE_SHOW)
    private void onBeforeShow(FxmlViewEvent<? extends Parent> event) {
        int gridCol;
        LocalDate d = monthStart;
        Month month = monthStart.getMonth();
        switch (d.getDayOfWeek()) {
            case MONDAY:
                gridCol = 1;
                break;
            case TUESDAY:
                gridCol = 2;
                break;
            case WEDNESDAY:
                gridCol = 3;
                break;
            case THURSDAY:
                gridCol = 4;
                break;
            case FRIDAY:
                gridCol = 5;
                break;
            case SATURDAY:
                gridCol = 6;
                break;
            default:
                gridCol = 0;
                break;
        }
        int gridRow = 1;
        NumberFormat format = NumberFormat.getNumberInstance();
        allAppointments = FXCollections.observableArrayList();
        itemsLists = FXCollections.observableArrayList();
        listViews = FXCollections.observableArrayList();
        ObservableList<Node> gridPaneChildren = monthGridPane.getChildren();
        ObservableList<RowConstraints> rowConstraints = monthGridPane.getRowConstraints();
        if (d.getDayOfWeek() != DayOfWeek.SUNDAY) {
            rowConstraints.add(new RowConstraints(RowConstraints.CONSTRAIN_TO_PREF, RowConstraints.CONSTRAIN_TO_PREF,
                    RowConstraints.CONSTRAIN_TO_PREF, Priority.ALWAYS, VPos.TOP, true));
        }
        do {
            if (d.getDayOfWeek() == DayOfWeek.SUNDAY) {
                rowConstraints.add(new RowConstraints(RowConstraints.CONSTRAIN_TO_PREF, RowConstraints.CONSTRAIN_TO_PREF,
                        RowConstraints.CONSTRAIN_TO_PREF, Priority.ALWAYS, VPos.TOP, true));
            }
            VBox vBox = new VBox();
            GridPane.setColumnIndex(vBox, gridCol);
            GridPane.setRowIndex(vBox, gridRow);
            GridPane.setVgrow(vBox, Priority.ALWAYS);
            gridPaneChildren.add(vBox);

            Label label = new Label();
            label.setText(format.format(d.getDayOfMonth()));
            label.setMaxWidth(Double.MAX_VALUE);
            label.setAlignment(Pos.CENTER);
            VBox.setVgrow(label, Priority.SOMETIMES);

            ObservableList<Node> vBoxChildren = vBox.getChildren();
            vBoxChildren.add(label);
            ObservableList<AppointmentModel> items = FXCollections.observableArrayList();
            itemsLists.add(items);
            ListView<AppointmentModel> listView = new ListView<>();
            listView.setItems(items);
            VBox.setVgrow(listView, Priority.ALWAYS);
            listView.setMaxWidth(Double.MAX_VALUE);
            vBoxChildren.add(listView);
            listViews.add(listView);
            if (d.getDayOfWeek() == DayOfWeek.SATURDAY) {
                gridCol = 0;
                gridRow++;
            }
        } while ((d = d.plusDays(1)).getMonth() == month);

        AppointmentModel.getFactory().loadAsync(event.getStage(),
                AppointmentFilter.of(AppointmentFilter.expressionOf(DB.toUtcTimestamp(d.atStartOfDay()),
                        DB.toUtcTimestamp(d.plusMonths(1).atStartOfDay()))), allAppointments, (t) -> {
            t.stream().sorted((AppointmentModel o1, AppointmentModel o2) -> {
                int r = o1.getStart().compareTo(o2.getStart());
                return (r == 0) ? o1.getEnd().compareTo(o2.getEnd()) : r;
            }).forEach((u) -> {
                itemsLists.get(u.getStart().getDayOfMonth() - 1).add(u);
            });
        }, (Throwable t) -> {
            ErrorDetailDialog.logShowAndWait(LOG, AppResources.getResourceString(RESOURCEKEY_DBREADERROR), event.getStage(), t);
        });
    }

}
