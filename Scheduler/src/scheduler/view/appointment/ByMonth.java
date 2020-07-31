package scheduler.view.appointment;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import scheduler.Scheduler;
import scheduler.fx.CssClassName;
import scheduler.model.fx.AppointmentModel;
import scheduler.util.LogHelper;
import scheduler.util.NodeUtil;
import scheduler.util.Values;
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

    private ByMonth() {
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

    private void initialize() {
        LOG.entering(LOG.getName(), "initialize");
        getStylesheets().add("/scheduler/defaultStyles.css");
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

    static final class HeaderHBox extends HBox {

        protected final Button button;
        protected final Label monthNameLabel;
        protected final Button button0;

        HeaderHBox() {

            button = new Button();
            monthNameLabel = new Label();
            button0 = new Button();

            HBox.setHgrow(button, javafx.scene.layout.Priority.NEVER);
            button.setMnemonicParsing(false);
            button.setOnAction(this::onPreviousMonthButtonAction);
            button.setText("?");

            HBox.setHgrow(monthNameLabel, javafx.scene.layout.Priority.ALWAYS);
            monthNameLabel.setAlignment(javafx.geometry.Pos.CENTER);
            monthNameLabel.setMaxWidth(Double.MAX_VALUE);
            monthNameLabel.setText("January");

            HBox.setHgrow(button0, javafx.scene.layout.Priority.NEVER);
            button0.setMnemonicParsing(false);
            button0.setOnAction(this::onNextMonthButtonAction);
            button0.getStyleClass().add("symbol-button");
            button0.setText("?");

            getChildren().add(button);
            getChildren().add(monthNameLabel);
            getChildren().add(button0);

        }

        void onPreviousMonthButtonAction(ActionEvent actionEvent) {

        }

        void onNextMonthButtonAction(ActionEvent actionEvent) {

        }

    }

    static final class AppointmentTextFlow extends TextFlow {

        private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("d");
        private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("h:mm a");

        private static LocalDate toEndDateExclusive(LocalDateTime value) {
            return (value.toLocalTime().equals(LocalTime.MIN)) ? value.toLocalDate() : value.toLocalDate().plusDays(1);
        }

        private static LocalDate getLater(LocalDate a, LocalDate b) {
            return (a.compareTo(b) > 0) ? a : b;
        }

        private static LocalDate getEarlier(LocalDate a, LocalDate b) {
            return (a.compareTo(b) < 0) ? a : b;
        }

        private static LocalDate assertRangeValid(AppointmentModel model, LocalDate targetRangeStart, LocalDate targetRangeEnd) {
            LocalDate end = model.getEnd().toLocalDate();
            if (model.getStart().toLocalDate().compareTo(Objects.requireNonNull(targetRangeEnd)) > 0 || end.compareTo(targetRangeStart) <= 0) {
                throw new IllegalArgumentException("Target date does not fall on or after the start date and before the end date");
            }
            return (end.compareTo(targetRangeEnd) < 0) ? end : targetRangeEnd;
        }

        static AppointmentTextFlow setFirstInCell(ObservableList<AppointmentTextFlow> nodes) {
            Iterator<AppointmentTextFlow> iterator = nodes.iterator();
            AppointmentTextFlow result;
            do {
                if (!iterator.hasNext()) {
                    return null;
                }
            } while (null == (result = iterator.next()));

            result.firstInCell.set(true);
            while (iterator.hasNext()) {
                AppointmentTextFlow n = iterator.next();
                if (null != n && n.firstInCell.get()) {
                    n.firstInCell.set(false);
                }
            }
            return result;
        }

        private final ReadOnlyObjectWrapper<AppointmentModel> model;
        private final ReadOnlyObjectWrapper<LocalDate> day;
        private final ReadOnlyObjectWrapper<LocalTime> startTime;
        private final ReadOnlyObjectWrapper<LocalTime> endTime;
        private final ReadOnlyObjectWrapper<AppointmentTextFlow> previousDay;
        private final ReadOnlyObjectWrapper<AppointmentTextFlow> nextDay;
        private final ReadOnlyBooleanWrapper firstInCell;
        private final Label dateNumberLabel;
        private final Hyperlink appointmentTimeHyperlink;
        private final Text prevDaySymbolText;
        private final Text appointmentTitleText;
        private final Text nextDaySymbolText;

        private AppointmentTextFlow(AppointmentModel model, LocalDate targetRangeStart, AppointmentTextFlow previous) {
            this.model = new ReadOnlyObjectWrapper<>(model);
            day = new ReadOnlyObjectWrapper<>(targetRangeStart);
            startTime = new ReadOnlyObjectWrapper<>(model.getStart().toLocalTime());
            endTime = new ReadOnlyObjectWrapper<>(model.getEnd().toLocalTime());
            nextDay = new ReadOnlyObjectWrapper<>();
            previousDay = new ReadOnlyObjectWrapper<>(previous);
            dateNumberLabel = NodeUtil.collapseNode(new Label(DATE_FORMATTER.format(targetRangeStart)));
            day.addListener((observable, oldValue, newValue) -> dateNumberLabel.setText(DATE_FORMATTER.format(newValue)));
            appointmentTimeHyperlink = new Hyperlink(String.format("%s - %s", TIME_FORMATTER.format(startTime.get()), TIME_FORMATTER.format(endTime.get())));
            startTime.addListener((observable, oldValue, newValue) -> onTimeChanged(newValue, endTime.get()));
            endTime.addListener((observable, oldValue, newValue) -> onTimeChanged(startTime.get(), newValue));

            prevDaySymbolText = NodeUtil.addCssClass(new Text(" "), CssClassName.SYMBOL);
            if (null == previous) {
                NodeUtil.collapseNode(prevDaySymbolText);
            }
            appointmentTitleText = new Text(model.getTitle());
            nextDaySymbolText = NodeUtil.collapseNode(NodeUtil.addCssClass(new Text(" "), CssClassName.SYMBOL));
            firstInCell = new ReadOnlyBooleanWrapper(false);
            firstInCell.addListener((observable, oldValue, newValue) -> onFirstInCellChanged(newValue));
            previousDay.addListener((observable, oldValue, newValue) -> {
                if (null == newValue) {
                    NodeUtil.collapseNode(prevDaySymbolText);
                } else {
                    NodeUtil.restoreNode(prevDaySymbolText);
                }
            });
            nextDay.addListener((observable, oldValue, newValue) -> {
                if (null == newValue) {
                    NodeUtil.collapseNode(nextDaySymbolText);
                } else {
                    NodeUtil.restoreNode(nextDaySymbolText);
                }
            });
            getChildren().addAll(dateNumberLabel, appointmentTimeHyperlink, new Text(Values.LINEBREAK_STRING), prevDaySymbolText, appointmentTitleText, nextDaySymbolText);
        }

        private AppointmentTextFlow(AppointmentModel model, LocalDate targetRangeStart, LocalDate targetRangeEnd, AppointmentTextFlow previous) {
            this(model, targetRangeStart, previous);

            LocalDate startDate = targetRangeStart.plusDays(1);
            if (startDate.compareTo(targetRangeEnd) < 0) {
                nextDay.set(new AppointmentTextFlow(model, startDate, targetRangeEnd, this));
            }
        }

        public AppointmentTextFlow(AppointmentModel model, LocalDate targetRangeStart, LocalDate targetRangeEnd) {
            this(model, getLater(targetRangeStart, model.getStart().toLocalDate()), assertRangeValid(model, targetRangeStart, targetRangeEnd), null);
        }

        public AppointmentTextFlow(AppointmentModel model) {
            this(model, model.getStart().toLocalDate(), toEndDateExclusive(model.getEnd()));
        }

        public AppointmentModel getModel() {
            return model.get();
        }

        public ReadOnlyObjectProperty<AppointmentModel> modelProperty() {
            return model.getReadOnlyProperty();
        }

        public LocalDate getDay() {
            return day.get();
        }

        public ReadOnlyObjectProperty<LocalDate> dayOfMonthProperty() {
            return day.getReadOnlyProperty();
        }

        public LocalTime getStartTime() {
            return startTime.get();
        }

        public ReadOnlyObjectProperty<LocalTime> startTimeProperty() {
            return startTime.getReadOnlyProperty();
        }

        public LocalTime getEndTime() {
            return endTime.get();
        }

        public ReadOnlyObjectProperty<LocalTime> endTimeProperty() {
            return endTime.getReadOnlyProperty();
        }

        public AppointmentTextFlow getPreviousDay() {
            return previousDay.get();
        }

        public ReadOnlyObjectProperty<AppointmentTextFlow> previousDayProperty() {
            return previousDay.getReadOnlyProperty();
        }

        public AppointmentTextFlow getNextDay() {
            return nextDay.get();
        }

        public ReadOnlyObjectProperty<AppointmentTextFlow> nextDayProperty() {
            return nextDay.getReadOnlyProperty();
        }

        public boolean isFirstInCell() {
            return firstInCell.get();
        }

        public ReadOnlyBooleanProperty firstInCellProperty() {
            return firstInCell.getReadOnlyProperty();
        }

        private void onFirstInCellChanged(Boolean newValue) {
            if (newValue) {
                NodeUtil.addCssClass(this, CssClassName.FIRST_ITEM);
                NodeUtil.restoreNode(dateNumberLabel);
            } else {
                NodeUtil.removeCssClass(this, CssClassName.FIRST_ITEM);
                NodeUtil.collapseNode(dateNumberLabel);
            }
        }

        public AppointmentTextFlow firstDay() {
            AppointmentTextFlow result = previousDay.get();
            return (null == result) ? this : result.firstDay();
        }

        public AppointmentTextFlow lastDay() {
            AppointmentTextFlow result = nextDay.get();
            return (null == result) ? this : result.lastDay();
        }

        public Stream<AppointmentTextFlow> allDays() {
            Stream.Builder<AppointmentTextFlow> builder = Stream.builder();
            AppointmentTextFlow item = firstDay();
            do {
                builder.accept(item);
            } while (null != (item = item.nextDay.get()));
            return builder.build();
        }

        private void onTimeChanged(LocalTime start, LocalTime end) {
            appointmentTimeHyperlink.setText(String.format("%s - %s", TIME_FORMATTER.format(start), TIME_FORMATTER.format(end)));
        }

    }

    static final class CalendarCellVBox extends VBox {

        private final ObservableList<Node> children;

        CalendarCellVBox() {
            children = FXCollections.unmodifiableObservableList(super.getChildren());
        }

        @Override
        public ObservableList<Node> getChildren() {
            return children;
        }
        
        Stream<AppointmentTextFlow> stream() {
            ObservableList<Node> c = super.getChildren();
            if (c.isEmpty()) {
                return Stream.empty();
            }
            Stream.Builder<AppointmentTextFlow> builder = Stream.builder();
            c.forEach((t) -> {
                if (null != t && t instanceof AppointmentTextFlow) {
                    builder.accept((AppointmentTextFlow) t);
                }
            });
            return builder.build();
        }
        
        Optional<AppointmentTextFlow> find(int pk) {
            Iterator<AppointmentTextFlow> iterator = stream().iterator();
            while (iterator.hasNext()) {
                AppointmentTextFlow item = iterator.next();
                if (item.getModel().getPrimaryKey() == pk) {
                    return Optional.of(item.firstDay());
                }
            }
            return Optional.empty();
        }
        
    }

    static final class CalendarGridPane extends GridPane {

        private final RowConstraints week5RowConstraints;
        private final RowConstraints week6RowConstraints;
        private final BorderPane[] emptyPanesTop;
        private final BorderPane[] emptyPanesBottom;
        private final CalendarCellVBox[] calendarCells;
        private final SimpleListProperty<AppointmentModel> items;
        private final SimpleObjectProperty<YearMonth> targetMonth;

        CalendarGridPane(YearMonth targetMonth, ObservableList<AppointmentModel> items) {
            this.targetMonth = new SimpleObjectProperty<>((null == targetMonth) ? YearMonth.now() : targetMonth);
            this.items = new SimpleListProperty<>((null == items) ? FXCollections.observableArrayList() : items);
            ObservableList<RowConstraints> rc = getRowConstraints();
            for (int i = 0; i < 6; i++) {
                RowConstraints r = new RowConstraints();
                rc.add(r);
            }
            week5RowConstraints = new RowConstraints();
            week6RowConstraints = new RowConstraints();
            getStyleClass().add("month-gridpane");

            double percent = 100.0 / 7.0;
            ObservableList<Node> children = getChildren();
            ObservableList<ColumnConstraints> columnConstraints = getColumnConstraints();
            for (int i = 0; i < 7; i++) {
                ColumnConstraints cc = new ColumnConstraints();
                cc.setHgrow(Priority.NEVER);
                cc.setPercentWidth(percent);
                columnConstraints.add(cc);
                Label label = new Label(DayOfWeek.of(i).getDisplayName(TextStyle.FULL, Locale.getDefault()));
                GridPane.setColumnIndex(label, 1);
                children.add(label);
            }

            emptyPanesTop = new BorderPane[6];
            emptyPanesBottom = new BorderPane[6];
            for (int i = 0; i < 6; i++) {
                BorderPane b = NodeUtil.addCssClass(new BorderPane(), CssClassName.EMPTY_CELL);
                GridPane.setHgrow(b, Priority.ALWAYS);
                GridPane.setVgrow(b, Priority.ALWAYS);
                GridPane.setColumnIndex(b, i);
                GridPane.setRowIndex(b, 1);
                emptyPanesTop[i] = b;
                b = NodeUtil.addCssClass(new BorderPane(), CssClassName.EMPTY_CELL);
                GridPane.setHgrow(b, Priority.ALWAYS);
                GridPane.setVgrow(b, Priority.ALWAYS);
                GridPane.setColumnIndex(b, i + 6);
                GridPane.setRowIndex(b, 5);
                emptyPanesBottom[i] = b;
                // TODO: set row and column index and Add to child controls
            }

            calendarCells = new CalendarCellVBox[31];
            for (int i = 0; i < 31; i++) {
                CalendarCellVBox c = new CalendarCellVBox();
                GridPane.setHgrow(c, Priority.ALWAYS);
                GridPane.setVgrow(c, Priority.ALWAYS);
                calendarCells[i] = c;
                // TODO: set row and column index and Add to child controls
            }

            this.targetMonth.addListener(this::onMonthChanged);
            this.items.addListener(this::onItemsChanged);
        }

        YearMonth getTargetMonth() {
            return targetMonth.get();
        }

        void setTargetMonth(YearMonth value) {
            targetMonth.set(value);
        }

        ObjectProperty<YearMonth> targetMonthProperty() {
            return targetMonth;
        }

        ObservableList<AppointmentModel> getItems() {
            return items.get();
        }

        void setItems(ObservableList<AppointmentModel> value) {
            items.set(value);
        }

        ListProperty<AppointmentModel> itemsProperty() {
            return items;
        }

        private void onMonthChanged(ObservableValue<? extends YearMonth> observable, YearMonth oldValue, YearMonth newValue) {
            if (null == newValue) {
                targetMonth.set((null == oldValue) ? YearMonth.now() : oldValue);
            }
        }

        private void onItemsChanged(ListChangeListener.Change<? extends AppointmentModel> c) {
            while (c.next()) {
                if (!c.wasPermutated()) {
                    List<? extends AppointmentModel> list;
                    if (c.wasUpdated()) {
                        list = c.getList();
                        int e = c.getTo();
                        for (int i = c.getFrom(); i < e && i < list.size(); i++) {
                            onItemChanged(list.get(i));
                        }
                    } else {
                        list = c.getRemoved();
                        if (null != list && !list.isEmpty()) {
                            list.forEach(this::onItemRemoved);
                        }
                        list = c.getAddedSubList();
                        if (null != list && !list.isEmpty()) {
                            list.forEach(this::onItemAdded);
                        }
                    }
                }
            }
        }

        private void onItemAdded(AppointmentModel item) {
            LocalDateTime startDateTime = item.getStart();
            LocalDateTime endDateTime = item.getEnd();
            LocalDate targetStart = targetMonth.get().atDay(1);
            LocalDate targetEnd = targetStart.plusMonths(1);
            LocalDate startDate = startDateTime.toLocalDate();
            LocalTime endTime = endDateTime.toLocalTime();
            LocalDate endDate = (endTime.equals(LocalTime.MIN)) ? endDateTime.toLocalDate() : endDateTime.toLocalDate().plusDays(1);
            if (startDate.isBefore(targetEnd) && endDate.isAfter(targetStart)) {
                
            } else {
                // TODO: Search all for existing and remove it.
            }
        }

        private void onItemChanged(AppointmentModel item) {
            LocalDateTime startDateTime = item.getStart();
            LocalDateTime endDateTime = item.getEnd();
            LocalDate targetStart = targetMonth.get().atDay(1);
            LocalDate targetEnd = targetStart.plusMonths(1);
            LocalDate startDate = startDateTime.toLocalDate();
            LocalTime endTime = endDateTime.toLocalTime();
            LocalDate endDate = (endTime.equals(LocalTime.MIN)) ? endDateTime.toLocalDate() : endDateTime.toLocalDate().plusDays(1);
            if (startDate.isBefore(targetEnd) && endDate.isAfter(targetStart)) {
                // TODO: 
            } else {
                // TODO: Search all for existingn and remove it.
            }
        }

        private void onItemRemoved(AppointmentModel item) {
            LocalDateTime startDateTime = item.getStart();
            LocalDateTime endDateTime = item.getEnd();
            LocalDate targetStart = targetMonth.get().atDay(1);
            LocalDate targetEnd = targetStart.plusMonths(1);
            LocalDate startDate = startDateTime.toLocalDate();
            LocalTime endTime = endDateTime.toLocalTime();
            LocalDate endDate = (endTime.equals(LocalTime.MIN)) ? endDateTime.toLocalDate() : endDateTime.toLocalDate().plusDays(1);
            if (startDate.isBefore(targetEnd) && endDate.isAfter(targetStart)) {
                // TODO: 
            } else {
                // TODO: Search all for existingn and remove it.
            }
        }

    }

    static final class FooterHBox extends HBox {

        private final SingleSelectionModel<Month> monthSelection;
        private final ReadOnlyObjectProperty<Integer> year;
        private final SpinnerValueFactory<Integer> yearValue;
        private final ReadOnlyObjectWrapper<YearMonth> targetDate;
        private final Button button;

        FooterHBox(YearMonth targetDate) {
            this.targetDate = new ReadOnlyObjectWrapper<>((null == targetDate) ? YearMonth.now() : targetDate);

            setAlignment(Pos.CENTER_RIGHT);
            setMaxWidth(Double.MAX_VALUE);

            Spinner<Integer> yearSpinner = new Spinner<>(LocalDate.MIN.getYear() + 1, LocalDate.MAX.getYear() - 1, this.targetDate.get().getYear());
            year = yearSpinner.valueProperty();
            yearValue = yearSpinner.getValueFactory();

            ComboBox<Month> monthComboBox = new ComboBox<>(FXCollections.observableArrayList(Month.values()));
            monthSelection = monthComboBox.getSelectionModel();
            monthSelection.select(this.targetDate.get().getMonth());
            button = NodeUtil.createButton("Submit");
            HBox.setMargin(button, new Insets(0.0, 0.0, 0.0, 8.0));
            getChildren().addAll(
                    NodeUtil.createLabel("Year:", CssClassName.LEFTCONTROLLABEL),
                    NodeUtil.addCssClass(yearSpinner, CssClassName.LEFTLABELEDCONTROL),
                    NodeUtil.createLabel("Month:", CssClassName.INNERLEFTCONTROLLABEL),
                    NodeUtil.addCssClass(monthComboBox, CssClassName.LEFTLABELEDCONTROL),
                    button
            );
            yearValue.valueProperty().addListener(this::onYearChanged);
            monthSelection.selectedItemProperty().addListener(this::onMonthChanged);
        }

        YearMonth getTargetDate() {
            return targetDate.get();
        }

        void setTargetDate(YearMonth value) {
            if (null == value) {
                setTargetDate(YearMonth.now());
            } else {
                Month m = monthSelection.getSelectedItem();
                if (value.getMonth() != m) {
                    monthSelection.select(value.getMonth());
                }
                Integer y = year.get();
                int n = value.getYear();
                if (y < n) {
                    yearValue.increment(n - y);
                } else if (y > n) {
                    yearValue.decrement(y - n);
                }
            }
        }

        ReadOnlyObjectProperty<YearMonth> targetDateProperty() {
            return targetDate.getReadOnlyProperty();
        }

        final void addSubmitEventHandler(EventType<ActionEvent> eventType, EventHandler<? super ActionEvent> eventHandler) {
            button.addEventFilter(eventType, eventHandler);
        }
        
        final void removeSubmitEventHandler(EventType<ActionEvent> eventType, EventHandler<? super ActionEvent> eventHandler) {
            button.removeEventFilter(eventType, eventHandler);
        }

        private void onYearChanged(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
            YearMonth t = targetDate.get();
            if (null == t) {
                t = YearMonth.now();
            }
            if (newValue != t.getYear()) {
                targetDate.set(t.withYear(newValue));
            }
        }

        private void onMonthChanged(ObservableValue<? extends Month> observable, Month oldValue, Month newValue) {
            YearMonth t = targetDate.get();
            if (null == t) {
                t = YearMonth.now();
            }
            if (newValue != t.getMonth()) {
                targetDate.set(t.withMonth(newValue.getValue()));
            }
        }
        
    }
}
