package scheduler.view.appointment;

import java.text.NumberFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
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
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import scheduler.fx.AppointmentListCellFactory;
import scheduler.fx.CssClassName;
import scheduler.model.ModelHelper.AppointmentHelper;
import scheduler.model.fx.AppointmentModel;
import scheduler.util.NodeUtil;
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

    private static final Logger LOG = Logger.getLogger(MonthGridPane.class.getName());
    private static final NumberFormat FORMATTER = NumberFormat.getIntegerInstance();

    private final RowConstraints firstRow;
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
        firstRow = new RowConstraints();
        getRowConstraints().add(firstRow);
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

    private synchronized void populateCalender() {
        ObservableList<AppointmentModel> itemList = items.get();
        ObservableList<Node> children = getChildren();
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
        ObservableList<RowConstraints> rowConstraints = getRowConstraints();
        rowConstraints.retainAll(firstRow);
        LocalDate d = targetDate.get();
        if (null == d) {
            return;
        }

        final LocalDate startDate = d.withDayOfMonth(1);
        int offset = daysOfWeekOrdered.indexOf(startDate.getDayOfWeek());
        final LocalDate lastDate = startDate.plusMonths(1L).minusDays(1L);
        int daysInMonth = lastDate.getDayOfMonth();
        if (offset > 0) {
            GridPane.setColumnSpan(emtpyPane1, offset);
            children.add(emtpyPane1);
        }
        int cellIndex;
        int colIndex;
        int rowIndex;
        int rc = rowConstraints.size();
        for (int i = 0; i < daysInMonth; i++) {
            cellIndex = offset + i;
            colIndex = cellIndex % 7;
            rowIndex = ((cellIndex - colIndex) / 7) + 1;
            if (rowIndex == rc) {
                rowConstraints.add(new RowConstraints());
                rc++;
            }
            CalendarDateCell cn = cellNodes.get(i);
            GridPane.setColumnIndex(cn, colIndex);
            GridPane.setRowIndex(cn, rowIndex);
            children.add(cn);
        }
        cellIndex = offset + daysInMonth;
        colIndex = cellIndex % 7;
        rowIndex = ((cellIndex - colIndex) / 7) + 1;
        if (rowIndex < rc) {
            GridPane.setColumnIndex(emtpyPane2, colIndex);
            GridPane.setRowIndex(emtpyPane2, rowIndex);
            GridPane.setColumnSpan(emtpyPane2, 7 - colIndex);
            children.add(emtpyPane2);
        }
        // FIXME: Need to create a method that adds and removes only the changed items, versus re-populating the entire calendar every time
        if (!(null == itemList || itemList.isEmpty())) {
            itemList.stream().sorted(AppointmentHelper::compareByDates).forEach((t) -> {
                LocalDate startRange = t.getStart().toLocalDate();
                LocalDate endRange = t.getEnd().toLocalDate();
                if (startRange.compareTo(startDate) < 0) {
                    startRange = startDate;
                }
                int e = ((endRange.compareTo(lastDate) > 0) ? lastDate : endRange).getDayOfMonth();
                for (int i = ((startRange.compareTo(startDate) < 0) ? startDate : startRange).getDayOfMonth() - 1; i < e; i++) {
                    cellNodes.get(i).items.get().add(t);
                }
            });
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


    public final class CalendarDateCell extends AnchorPane {

        private final ReadOnlyListWrapper<AppointmentModel> items;
        private final ReadOnlyIntegerWrapper value;
        private final ListView<AppointmentModel> listView;
        private final AppointmentListCellFactory listCellFactory;
        private final Label label;

        public CalendarDateCell(int value) {
            ObservableList<AppointmentModel> list = FXCollections.observableArrayList();
            listView = new ListView<>();
            AnchorPane.setBottomAnchor(listView, 0.0);
            AnchorPane.setLeftAnchor(listView, 0.0);
            AnchorPane.setRightAnchor(listView, 0.0);
            AnchorPane.setTopAnchor(listView, 1.0);
            listCellFactory = new AppointmentListCellFactory();
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

        public ObservableList<AppointmentModel> getItems() {
            return items.get();
        }

        public ReadOnlyListProperty<AppointmentModel> itemsProperty() {
            return items.getReadOnlyProperty();
        }

    }

    class DateHBox extends HBox {

        private final ObservableList<AppointmentModel> backingList;
        private final StringProperty text;
        private final ReadOnlyListWrapper<AppointmentModel> items;

        @SuppressWarnings("LeakingThisInConstructor")
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
