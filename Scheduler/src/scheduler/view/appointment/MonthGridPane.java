package scheduler.view.appointment;

import java.text.NumberFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import scheduler.fx.CalendarListCellFactory;
import scheduler.model.ModelHelper.AppointmentHelper;
import scheduler.model.fx.AppointmentModel;
import scheduler.util.LogHelper;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/view/appointment/ManageAppointments")
@FXMLResource("/scheduler/view/appointment/MonthGridPane.fxml")
public final class MonthGridPane extends GridPane {

    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(MonthGridPane.class.getName()), Level.FINE);
//    private static final Logger LOG = Logger.getLogger(MonthGridPane.class.getName());
    private static final NumberFormat FORMATTER = NumberFormat.getIntegerInstance();

    private final RowConstraints rowConstraint5;
    private final RowConstraints rowConstraint6;
    private final ObjectProperty<LocalDate> targetDate;
    private final ListProperty<AppointmentModel> items;
    private final BorderPane emtpyPane1;
    private final ObservableList<CalendarDateCell> cellNodes;
    private final BorderPane emtpyPane2;
    private int year;
    private Month month;
    private final ObservableList<DayOfWeek> daysOfWeekOrdered;

    public MonthGridPane() {
        setMaxHeight(USE_PREF_SIZE);
        setMaxWidth(USE_PREF_SIZE);
        setMinHeight(USE_PREF_SIZE);
        setMinWidth(USE_PREF_SIZE);
        setPrefWidth(900.0);
        getStyleClass().add("month-gridpane");
        rowConstraint5 = new RowConstraints();
        rowConstraint6 = new RowConstraints();
        getRowConstraints().add(new RowConstraints());
        getRowConstraints().add(new RowConstraints());
        getRowConstraints().add(new RowConstraints());
        getRowConstraints().add(new RowConstraints());
        getRowConstraints().add(new RowConstraints());
        daysOfWeekOrdered = FXCollections.unmodifiableObservableList(FXCollections.observableArrayList(new DayOfWeek[]{
            DayOfWeek.SUNDAY, DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY
        }));
        LocalDate d = LocalDate.now();
        targetDate = new SimpleObjectProperty<>(d);
        year = d.getYear();
        month = d.getMonth();

        items = new SimpleListProperty<>();

        while (d.getDayOfWeek() != DayOfWeek.SUNDAY) {
            d = d.minusDays(1L);
        }
        DateTimeFormatter dowFmt = DateTimeFormatter.ofPattern("eeee");
        ObservableList<Node> children = getChildren();
        ObservableList<ColumnConstraints> columnConstraints = getColumnConstraints();
        double pct = 100.0 / 7.0;
        for (int i = 0; i < daysOfWeekOrdered.size(); i++) {
            ColumnConstraints c = new ColumnConstraints();
            c.setHgrow(javafx.scene.layout.Priority.NEVER);
            c.setPercentWidth(pct);
            columnConstraints.add(c);
            Label label = new Label();
            GridPane.setColumnIndex(label, i);
            label.setText(dowFmt.format(d));
            children.add(label);
            d = d.plusDays(1L);
        }

        cellNodes = FXCollections.observableArrayList();
        for (int i = 1; i < 32; i++) {
            CalendarDateCell cell = new CalendarDateCell(i);
            GridPane.setHgrow(cell, javafx.scene.layout.Priority.ALWAYS);
            GridPane.setVgrow(cell, javafx.scene.layout.Priority.ALWAYS);
            cellNodes.add(cell);
        }
        emtpyPane1 = new BorderPane();
        GridPane.setHgrow(emtpyPane1, javafx.scene.layout.Priority.ALWAYS);
        GridPane.setVgrow(emtpyPane1, javafx.scene.layout.Priority.ALWAYS);
        emtpyPane2 = new BorderPane();
        GridPane.setHgrow(emtpyPane2, javafx.scene.layout.Priority.ALWAYS);
        GridPane.setVgrow(emtpyPane2, javafx.scene.layout.Priority.ALWAYS);

        onTargetDateChange(targetDate, null, targetDate.get());
        targetDate.addListener(this::onTargetDateChange);
        items.addListener(this::onItemsListChange);
    }

    private synchronized void populateCalendar() {
        LOG.entering(LOG.getName(), "populateCalendar");
        ObservableList<AppointmentModel> itemList = items.get();
        ObservableList<RowConstraints> rowConstraints = getRowConstraints();
        ObservableList<Node> children = getChildren();

        //<editor-fold defaultstate="collapsed" desc="Clear all cells">
        if (null != emtpyPane1) {
            children.remove(emtpyPane1);
        }
        cellNodes.forEach((t) -> {
            if (null != t.getParent()) {
                t.items.get().clear();
                children.remove(t);
            }
        });
        if (null != emtpyPane2) {
            children.remove(emtpyPane2);
        }
        rowConstraints.removeAll(rowConstraint5, rowConstraint6);

        //</editor-fold>
        final LocalDate startDate = LocalDate.of(year, month, 1);
        int offset = daysOfWeekOrdered.indexOf(startDate.getDayOfWeek());
        final LocalDate lastDate = startDate.plusMonths(1L).minusDays(1L);
        int daysInMonth = lastDate.getDayOfMonth();
        LOG.fine(() -> String.format("Month of %s has %d days with %d blank cells on the first row", startDate.getMonth(), daysInMonth, offset));
        if (offset > 0) {
            GridPane.setColumnSpan(emtpyPane1, offset);
            children.add(emtpyPane1);
        }
        int cellCount = daysInMonth + offset;
        int finalPartialCount = cellCount % 7;
        int rowCount = (cellCount - finalPartialCount) / 7 + ((finalPartialCount > 0) ? 2 : 1);
        LOG.fine(() -> String.format("Using %d rows with the final partial row using %d cells", rowCount, finalPartialCount));
        switch (rowCount) {
            case 6:
                rowConstraints.add(rowConstraint5);
                break;
            case 7:
                rowConstraints.addAll(rowConstraint5, rowConstraint6);
                break;
        }

        for (int i = 0; i < daysInMonth; i++) {
            int cellIndex = offset + i;
            int colIndex = cellIndex % 7;
            int rowIndex = ((cellIndex - colIndex) / 7) + 1;
            CalendarDateCell cn = cellNodes.get(i);
            LOG.finer(String.format("Adding cell from index %d at row %d, column %d", i, rowIndex, colIndex));
            GridPane.setColumnIndex(cn, colIndex);
            GridPane.setRowIndex(cn, rowIndex);
            children.add(cn);
        }

        if (finalPartialCount > 0) {
            LOG.finer(String.format("Adding %d blank cells to final row at index %d", 7 - finalPartialCount, finalPartialCount));
            GridPane.setColumnIndex(emtpyPane2, finalPartialCount);
            GridPane.setRowIndex(emtpyPane2, rowCount);
            GridPane.setColumnSpan(emtpyPane2, 7 - finalPartialCount);
            children.add(emtpyPane2);
        }
        if (!(null == itemList || itemList.isEmpty())) {
            itemList.stream().sorted(AppointmentHelper::compareByDates).forEach((t) -> {
                LocalDate startRange = t.getStart().toLocalDate();
                LocalDate endRange = t.getEnd().toLocalDate();
                int startIndex = ((startRange.compareTo(startDate) < 0) ? startDate : startRange).getDayOfMonth() - 1;
                int endIndex = ((endRange.compareTo(lastDate) > 0) ? lastDate : endRange).getDayOfMonth() - 1;
                CalendarDateCell cell = cellNodes.get(startIndex);
                if (startIndex == endIndex) {
                    cell.items.get().add(new CalendarCellData(t, false, false));
                } else {
                    cell.items.get().add(new CalendarCellData(t, false, true));
                    for (int i = startIndex + 1; i < endIndex; i++) {
                        cellNodes.get(i).items.get().add(new CalendarCellData(t, true, true));
                    }
                    cellNodes.get(endIndex).items.get().add(new CalendarCellData(t, true, false));
                }
            });
        }
    }

    private void onTargetDateChange(ObservableValue<? extends LocalDate> observable, LocalDate oldValue, LocalDate newValue) {
        LocalDate d = (null == newValue) ? LocalDate.now() : newValue;
        Month m = d.getMonth();
        int y = d.getYear();
        if (m != month || y != year) {
            month = m;
            year = y;
            populateCalendar();
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
        populateCalendar();
    }

    private void onItemsContentChange(ListChangeListener.Change<? extends AppointmentModel> change) {
        populateCalendar();
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

    public final class CalendarDateCell extends AnchorPane {

        private final ReadOnlyListWrapper<CalendarCellData> items;
        private final ReadOnlyIntegerWrapper value;
        private final ListView<CalendarCellData> listView;
        private final CalendarListCellFactory listCellFactory;
        private final Label label;

        public CalendarDateCell(int value) {
            ObservableList<CalendarCellData> list = FXCollections.observableArrayList();
            listView = new ListView<>();
            AnchorPane.setBottomAnchor(listView, 0.0);
            AnchorPane.setLeftAnchor(listView, 0.0);
            AnchorPane.setRightAnchor(listView, 0.0);
            AnchorPane.setTopAnchor(listView, 1.0);
            listCellFactory = new CalendarListCellFactory();
            listView.setCellFactory(listCellFactory);
            listView.setItems(list);
            ObservableList<Node> children = getChildren();
            children.add(listView);
            label = new Label();
            AnchorPane.setLeftAnchor(label, 0.0);
            AnchorPane.setTopAnchor(label, 0.0);
            label.setText(FORMATTER.format(value));
            children.add(label);
            this.value = new ReadOnlyIntegerWrapper(value);
            items = new ReadOnlyListWrapper<>(list);
        }

        public int getValue() {
            return value.get();
        }

        public ReadOnlyIntegerProperty valueProperty() {
            return value.getReadOnlyProperty();
        }

        public ObservableList<CalendarCellData> getItems() {
            return items.get();
        }

        public ReadOnlyListProperty<CalendarCellData> itemsProperty() {
            return items.getReadOnlyProperty();
        }

    }

}
