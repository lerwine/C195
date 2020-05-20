package scheduler.view.appointment;

import scheduler.model.ui.AppointmentModel;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import scheduler.fx.AppointmentListCellFactory;
import scheduler.util.NodeUtil;
import scheduler.util.ViewControllerLoader;
import scheduler.fx.CssClassName;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.report.DailyAppointmentsBorderPane;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/view/appointment/ManageAppointments")
@FXMLResource("/scheduler/view/appointment/MonthGridPane.fxml")
public class MonthGridPane extends GridPane {

    private static final Logger LOG = Logger.getLogger(MonthGridPane.class.getName());

    private final ObjectProperty<LocalDate> targetDate;
    private final ListProperty<AppointmentModel> items;
    private final ObservableList<DateHBox> cellNodes;
    private int year;
    private Month month;
    private final ObservableList<DayOfWeek> daysOfWeekOrdered;

    @FXML // fx:id="sundayLabel"
    private Label sundayLabel; // Value injected by FXMLLoader

    @FXML // fx:id="mondayLabel"
    private Label mondayLabel; // Value injected by FXMLLoader

    @FXML // fx:id="tuesdayLabel"
    private Label tuesdayLabel; // Value injected by FXMLLoader

    @FXML // fx:id="wednesdayLabel"
    private Label wednesdayLabel; // Value injected by FXMLLoader

    @FXML // fx:id="thursdayLabel"
    private Label thursdayLabel; // Value injected by FXMLLoader

    @FXML // fx:id="fridayLabel"
    private Label fridayLabel; // Value injected by FXMLLoader

    @FXML // fx:id="saturdayLabel"
    private Label saturdayLabel; // Value injected by FXMLLoader

    @SuppressWarnings("LeakingThisInConstructor")
    public MonthGridPane() {
        LocalDate d = LocalDate.now();
        targetDate = new SimpleObjectProperty<>(d);
        year = d.getYear();
        month = d.getMonth();

        daysOfWeekOrdered = FXCollections.unmodifiableObservableList(FXCollections.observableArrayList(new DayOfWeek[]{
            DayOfWeek.SUNDAY, DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY
        }));

        cellNodes = FXCollections.observableArrayList();
        for (int i = 0; i < 31; i++) {
            cellNodes.add(new DateHBox());
        }
        items = new SimpleListProperty<>();
        targetDate.addListener(this::onTargetDateChange);
        items.addListener(this::onItemsListChange);
        try {
            ViewControllerLoader.initializeCustomControl(this);
        } catch (IOException ex) {
            Logger.getLogger(DailyAppointmentsBorderPane.class.getName()).log(Level.SEVERE, "Error loading view", ex);
        }
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    private void initialize() {
        assert wednesdayLabel != null : "fx:id=\"wednesdayLabel\" was not injected: check your FXML file 'MonthGridPane.fxml'.";
        assert sundayLabel != null : "fx:id=\"sundayLabel\" was not injected: check your FXML file 'MonthGridPane.fxml'.";
        assert mondayLabel != null : "fx:id=\"mondayLabel\" was not injected: check your FXML file 'MonthGridPane.fxml'.";
        assert tuesdayLabel != null : "fx:id=\"tuesdayLabel\" was not injected: check your FXML file 'MonthGridPane.fxml'.";
        assert thursdayLabel != null : "fx:id=\"thursdayLabel\" was not injected: check your FXML file 'MonthGridPane.fxml'.";
        assert fridayLabel != null : "fx:id=\"fridayLabel\" was not injected: check your FXML file 'MonthGridPane.fxml'.";
        assert saturdayLabel != null : "fx:id=\"saturdayLabel\" was not injected: check your FXML file 'MonthGridPane.fxml'.";

        LocalDate d = LocalDate.now();
        while (d.getDayOfWeek() != DayOfWeek.SUNDAY) {
            d = d.minusDays(1L);
        }
        DateTimeFormatter dowFmt = DateTimeFormatter.ofPattern("eeee");
        sundayLabel.setText(dowFmt.format(d));
        d = d.plusDays(1L);
        mondayLabel.setText(dowFmt.format(d));
        d = d.plusDays(1L);
        tuesdayLabel.setText(dowFmt.format(d));
        d = d.plusDays(1L);
        wednesdayLabel.setText(dowFmt.format(d));
        d = d.plusDays(1L);
        thursdayLabel.setText(dowFmt.format(d));
        d = d.plusDays(1L);
        fridayLabel.setText(dowFmt.format(d));
        d = d.plusDays(1L);
        saturdayLabel.setText(dowFmt.format(d));
    }

    private synchronized void populateCalender() {
        ObservableList<AppointmentModel> itemList = items.get();
        LocalDate startDate = targetDate.get();
        if (null == startDate) {
            getChildren().removeAll(cellNodes);
        } else {
            LocalDate endDateExcl = (startDate = startDate.withDayOfMonth(1)).plusMonths(1L);
            int rowIndex = (daysOfWeekOrdered.indexOf(startDate.getDayOfWeek()) == 0) ? 0 : 1;
            int daysInMonth = 0;
            for (LocalDate d = startDate; d.compareTo(endDateExcl) < 0; d = d.plusDays(1L)) {
                int colIndex = daysOfWeekOrdered.indexOf(d.getDayOfWeek());
                if (colIndex == 0) {
                    rowIndex++;
                }
                DateHBox dateHBox = cellNodes.get(daysInMonth++);
                dateHBox.backingList.clear();
                NodeUtil.setGridPanePosition(dateHBox, this, colIndex, rowIndex);
            }
            if (daysInMonth < cellNodes.size()) {
                ObservableList<Node> children = getChildren();
                do {
                    DateHBox dateHBox = cellNodes.get(daysInMonth++);
                    dateHBox.backingList.clear();
                    children.remove(dateHBox);
                } while (daysInMonth < cellNodes.size());
            }
            if (!(null == itemList || itemList.isEmpty())) {
                LocalDateTime beginIncl = startDate.atStartOfDay();
                LocalDateTime endExcl = endDateExcl.atStartOfDay();
                itemList.stream().sorted(AppointmentModel::compareByDates).forEach((t) -> {
                    LocalDateTime startRange = t.getStart();
                    LocalDateTime endRange = t.getEnd();
                    if (startRange.compareTo(endExcl) < 0 && endRange.compareTo(beginIncl) > 0) {
                        int s = ((startRange.compareTo(beginIncl) < 0) ? beginIncl : startRange).getDayOfMonth() - 1;
                        int e = (endRange.compareTo(endExcl) < 0) ? ((endRange.equals(endRange.toLocalDate().atStartOfDay())) ?
                                endRange.getDayOfMonth() - 1 : endRange.getDayOfMonth()) : 31;
                        for (int i = s; i < e; i++) {
                            cellNodes.get(i).backingList.add(t);
                        }
                    }
                });
            }
        }
    }

    private void onTargetDateChange(ObservableValue<? extends LocalDate> observable, LocalDate oldValue, LocalDate newValue) {
        if (newValue.getMonth() != month || newValue.getYear() != year) {
            populateCalender();
        }
    }

    private void onItemsListChange(ObservableValue<? extends ObservableList<AppointmentModel>> observable, ObservableList<AppointmentModel> oldValue,
            ObservableList<AppointmentModel> newValue) {
        if (null != oldValue) {
            newValue.removeListener(this::onItemsContentChange);
        }
        if (null != newValue) {
            newValue.addListener(this::onItemsContentChange);
        }
        populateCalender();
    }

    private void onItemsContentChange(ListChangeListener.Change<? extends AppointmentModel> change) {
        populateCalender();
    }

    public LocalDate getTargetDate() {
        return targetDate.get();
    }

    public void setTargetDate(LocalDate value) {
        targetDate.set(value);
    }

    public ObjectProperty<LocalDate> targetDateProperty() {
        return targetDate;
    }

    public ObservableList<AppointmentModel> getItems() {
        return items.get();
    }

    public void setItems(ObservableList<AppointmentModel> value) {
        items.set(value);
    }

    public ListProperty<AppointmentModel> itemsProperty() {
        return items;
    }

    class DateHBox extends HBox {

        private final ObservableList<AppointmentModel> backingList;
        private final StringProperty text;
        private final ReadOnlyListWrapper<AppointmentModel> items;

        DateHBox() {
            backingList = FXCollections.observableArrayList();
            text = new SimpleStringProperty("");
            items = new ReadOnlyListWrapper<>(FXCollections.unmodifiableObservableList(backingList));
            ObservableList<Node> children = super.getChildren();
            children.add(NodeUtil.createLabel(text, CssClassName.LEFTCONTROLLABEL));
            children.add(NodeUtil.createListView(items, new AppointmentListCellFactory(), CssClassName.LEFTLABELEDCONTROL));
            NodeUtil.setCssClass(this, CssClassName.BORDERED);
        }

        public String getText() {
            return text.get();
        }

        public void setText(String value) {
            text.set(value);
        }

        public StringProperty textProperty() {
            return text;
        }

        public ObservableList<AppointmentModel> getItems() {
            return items.getReadOnlyProperty();
        }

        public ReadOnlyListProperty<AppointmentModel> itemsProperty() {
            return items;
        }

    }
}
