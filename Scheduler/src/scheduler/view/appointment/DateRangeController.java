package scheduler.view.appointment;

import java.text.NumberFormat;
import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import scheduler.AppResources;
import scheduler.model.fx.AppointmentModel;
import scheduler.util.BinarySelective;
import scheduler.util.LogHelper;
import scheduler.util.Tuple;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public final class DateRangeController {

    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(DateRangeController.class.getName()), Level.FINE);
//    private static final Logger LOG = Logger.getLogger(DateRangeController.class.getName());
    private static final Pattern INT_PATTERN = Pattern.compile("^\\s*\\d{1,9}\\s*");
    private static final String INVALID_HOUR_NUMBER = "Invalid hour number";
    private static final String INVALID_MINUTE_NUMBER = "Invalid minute number";
    private static final NumberFormat INTN_FORMAT;
    private static final NumberFormat INT2_FORMAT;
    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL, FormatStyle.FULL).withZone(ZoneId.systemDefault());

    static {
        INT2_FORMAT = NumberFormat.getIntegerInstance();
        INT2_FORMAT.setMinimumIntegerDigits(2);
        INT2_FORMAT.setMaximumIntegerDigits(2);
        INTN_FORMAT = NumberFormat.getIntegerInstance();
    }

    private static BinarySelective<LocalDateTime, String> calculateEndDateTime(LocalDateTime start, BinarySelective<Integer, String> hour, BinarySelective<Integer, String> minute) {
        BinarySelective<LocalDateTime, String> result;
        LOG.entering(LOG.getName(), "calculateEndDateTime", new Object[]{start, hour, minute});
        if (null == start) {
            result = BinarySelective.ofSecondary(hour.toSecondary(minute.toSecondary("")));
        } else {
            result = hour.map(
                    (hv) -> minute.map((mv) -> {
                        if (hv > 0) {
                            if (mv > 0) {
                                return BinarySelective.ofPrimary(start.plusHours(hv).plusMinutes(mv));
                            }
                            return BinarySelective.ofPrimary(start.plusHours(hv));
                        }
                        if (mv > 0) {
                            return BinarySelective.ofPrimary(start.plusMinutes(mv));
                        }
                        return BinarySelective.ofPrimary(start);
                    },
                            (v) -> BinarySelective.ofSecondary(v)
                    ),
                    (u) -> BinarySelective.ofSecondary(u)
            );
        }
        LOG.exiting(LOG.getName(), "calculateEndDateTime", result);
        return result;
    }

    private static BinarySelective<LocalDateTime, String> calculateStartDateTime(LocalDate date, BinarySelective<Integer, String> hour,
            BinarySelective<Integer, String> minute, boolean isPm) {
        LOG.entering(LOG.getName(), "calculateStartDateTime", new Object[]{date, hour, minute, isPm});
        BinarySelective<LocalDateTime, String> result;
        if (null == date) {
            result = BinarySelective.ofSecondary(hour.toSecondary(minute.toSecondary("* Required")));
        } else {
            result = hour.map((hv) -> {
                return minute.map((mv) -> {
                    int h = (isPm) ? ((hv < 12) ? hv + 12 : 12) : ((hv == 12) ? 0 : hv);
                    return BinarySelective.ofPrimary(date.atTime(h, mv));
                },
                        (mm) -> BinarySelective.ofSecondary(mm)
                );
            },
                    (hm) -> BinarySelective.ofSecondary(hm)
            );
        }
        LOG.exiting(LOG.getName(), "calculateStartDateTime", result);
        return result;
    }

    private static BinarySelective<Integer, String> calculateHour(String text, int minValue, int maxValue) {
        LOG.entering(LOG.getName(), "calculateHour", new Object[]{text, minValue, maxValue});
        BinarySelective<Integer, String> result;
        String trimmed;
        if (null == text || (trimmed = text.trim()).isEmpty()) {
            result = BinarySelective.ofSecondary("");
            LOG.exiting(LOG.getName(), "calculateHour", result);
            return result;
        }
        try {
            Matcher m = INT_PATTERN.matcher(trimmed);
            if (!m.find()) {
                throw new ParseException(INVALID_HOUR_NUMBER, text.length() - trimmed.length());
            } else if (m.end() < trimmed.length()) {
                throw new ParseException(INVALID_HOUR_NUMBER, m.end() + (text.length() - trimmed.length()));
            }
            Number parse = INTN_FORMAT.parse(trimmed);
            int i = parse.intValue();
            if (i < minValue || i > maxValue) {
                result = BinarySelective.ofSecondary("Hour out of range");
            } else {
                result = BinarySelective.ofPrimary(i);
            }
        } catch (ParseException ex) {
            int i = ex.getErrorOffset();
            result = BinarySelective.ofSecondary((i > 0 && i < trimmed.length()) ? String.format("Invalid hour format at position %d", i)
                    : INVALID_HOUR_NUMBER);
        }
        LOG.exiting(LOG.getName(), "calculateHour", result);
        return result;
    }

    private static BinarySelective<Integer, String> calculateMinute(String text) {
        LOG.entering(LOG.getName(), "calculateMinute", text);
        BinarySelective<Integer, String> result;
        String trimmed;
        if (null == text || (trimmed = text.trim()).isEmpty()) {
            result = BinarySelective.ofSecondary("");
            LOG.exiting(LOG.getName(), "calculateMinute", result);
            return result;
        }
        try {
            Matcher m = INT_PATTERN.matcher(trimmed);
            if (!m.find()) {
                throw new ParseException(INVALID_MINUTE_NUMBER, text.length() - trimmed.length());
            } else if (m.end() < trimmed.length()) {
                throw new ParseException(INVALID_MINUTE_NUMBER, m.end() + (text.length() - trimmed.length()));
            }
            Number parse = INTN_FORMAT.parse(trimmed);
            int i = parse.intValue();
            if (i < 0 || i > 59) {
                result = BinarySelective.ofSecondary("Minute out of range");
            } else {
                result = BinarySelective.ofPrimary(i);
            }
        } catch (ParseException ex) {
            int i = ex.getErrorOffset();
            result = BinarySelective.ofSecondary((i > 0 && i < trimmed.length()) ? String.format("Invalid minute format at position %d", i)
                    : INVALID_MINUTE_NUMBER);
        }
        LOG.exiting(LOG.getName(), "calculateMinute", result);
        return result;
    }

    private final EditAppointment editAppointmentControl;
    private final LocalTime businessHoursStart;
    private final LocalTime businessHoursEnd;
    private final ReadOnlyObjectWrapper<LocalDateTime> startDateTimeValue;
    private final ReadOnlyStringWrapper startValidationMessage;
    private final ReadOnlyObjectWrapper<LocalDateTime> endDateTimeValue;
    private final ReadOnlyObjectWrapper<Tuple<LocalDateTime, LocalDateTime>> range;
    private final BooleanBinding withinBusinessHours;
    private final ReadOnlyBooleanWrapper valid;
    private ObjectBinding<BinarySelective<Integer, String>> parsedStartHour;
    private ObjectBinding<BinarySelective<Integer, String>> parsedStartMinute;
    private ObjectBinding<BinarySelective<LocalDateTime, String>> startDateTimeBinding;
    private ObjectBinding<BinarySelective<Integer, String>> parsedDurationHour;
    private ObjectBinding<BinarySelective<Integer, String>> parsedDurationMinute;
    private ObjectBinding<BinarySelective<LocalDateTime, String>> endDateTimeBinding;
    private DatePicker startDatePicker;
    private ComboBox<Boolean> amPmComboBox;
    private TextField startHourTextField;
    private TextField startMinuteTextField;
    private TextField durationHourTextField;
    private TextField durationMinuteTextField;
    private Label endDateTimeLabel;
    private Label durationValidationLabel;

    DateRangeController(EditAppointment editAppointmentControl) {
        this.editAppointmentControl = editAppointmentControl;
        try {
            businessHoursStart = AppResources.getBusinessHoursStart();
            businessHoursEnd = businessHoursStart.plusMinutes(AppResources.getBusinessHoursDuration());
        } catch (ParseException ex) {
            Logger.getLogger(EditAppointment.class.getName()).log(Level.SEVERE, "Error parsing start business hours", ex);
            throw new RuntimeException(ex);
        }
        startValidationMessage = new ReadOnlyStringWrapper(this, "startValidationMessage", "");
        startDateTimeValue = new ReadOnlyObjectWrapper<>(this, "startDateTimeValue", null);
        endDateTimeValue = new ReadOnlyObjectWrapper<>(this, "endDateTimeValue", null);
        range = new ReadOnlyObjectWrapper<>(this, "range", null);
        withinBusinessHours = Bindings.createBooleanBinding(() -> {
            LOG.entering(LogHelper.toLambdaSourceClass(LOG, "<init>", "withinBusinessHours"), "computeValue");
            Tuple<LocalDateTime, LocalDateTime> value = range.get();
            if (null == value) {
                LOG.exiting(LogHelper.toLambdaSourceClass(LOG, "<init>", "withinBusinessHours"), "computeValue", true);
                return true;
            }
            LocalDateTime s = value.getValue1();
            LocalDateTime e = value.getValue2();
            switch (s.getDayOfWeek()) {
                case SATURDAY:
                case SUNDAY:
                    LOG.exiting(LogHelper.toLambdaSourceClass(LOG, "<init>", "withinBusinessHours"), "computeValue", false);
                    return false;
                default:
                    switch (e.getDayOfWeek()) {
                        case SATURDAY:
                        case SUNDAY:
                            LOG.exiting(LogHelper.toLambdaSourceClass(LOG, "<init>", "withinBusinessHours"), "computeValue", false);
                            return false;
                        default:
                            break;
                    }
                    break;
            }
            boolean result = s.toLocalTime().compareTo(businessHoursStart) >= 0 && e.toLocalTime().compareTo(businessHoursEnd) < 0;
            LOG.exiting(LogHelper.toLambdaSourceClass(LOG, "<init>", "withinBusinessHours"), "computeValue", result);
            return result;
        }, range);
        valid = new ReadOnlyBooleanWrapper(this, "valid", false);
    }

    public LocalDateTime getStartDateTimeValue() {
        return startDateTimeValue.get();
    }

    public ReadOnlyObjectProperty<LocalDateTime> startDateTimeValueProperty() {
        return startDateTimeValue.getReadOnlyProperty();
    }

    public String getStartValidationMessage() {
        return startValidationMessage.get();
    }

    public ReadOnlyStringProperty startValidationMessageProperty() {
        return startValidationMessage.getReadOnlyProperty();
    }

    public LocalDateTime getEndDateTimeValue() {
        return endDateTimeValue.get();
    }

    public ReadOnlyObjectProperty<LocalDateTime> endDateTimeValueProperty() {
        return endDateTimeValue.getReadOnlyProperty();
    }

    public Tuple<LocalDateTime, LocalDateTime> getRange() {
        return range.get();
    }

    public ReadOnlyObjectProperty<Tuple<LocalDateTime, LocalDateTime>> rangeProperty() {
        return range.getReadOnlyProperty();
    }

    public boolean isWithinBusinessHours() {
        return withinBusinessHours.get();
    }

    public BooleanBinding withinBusinessHoursBinding() {
        return withinBusinessHours;
    }

    LocalTime getBusinessHoursStart() {
        return businessHoursStart;
    }

    LocalTime getBusinessHoursEnd() {
        return businessHoursEnd;
    }

    public boolean isValid() {
        return valid.get();
    }

    public ReadOnlyBooleanProperty validProperty() {
        return valid;
    }

    void initialize() {
        LOG.entering(LOG.getName(), "initialize");
        AppointmentModel model = editAppointmentControl.getModel();
        LocalDateTime rangeStart = model.getStart();
        startDatePicker = editAppointmentControl.getStartDatePicker();
        startHourTextField = editAppointmentControl.getStartHourTextField();
        startMinuteTextField = editAppointmentControl.getStartMinuteTextField();
        durationHourTextField = editAppointmentControl.getDurationHourTextField();
        durationMinuteTextField = editAppointmentControl.getDurationMinuteTextField();
        amPmComboBox = editAppointmentControl.getAmPmComboBox();
        endDateTimeLabel = editAppointmentControl.getEndDateTimeLabel();
        durationValidationLabel = editAppointmentControl.getDurationValidationLabel();
        if (null != rangeStart) {
            startDatePicker.setValue(rangeStart.toLocalDate());
            amPmComboBox.setItems(FXCollections.observableArrayList(Boolean.FALSE, Boolean.TRUE));
            int hv = rangeStart.getHour();
            if (hv < 12) {
                amPmComboBox.getSelectionModel().select(false);
                startHourTextField.setText(INTN_FORMAT.format((hv > 0) ? hv : 12));
            } else {
                amPmComboBox.getSelectionModel().select(true);
                startHourTextField.setText(INTN_FORMAT.format((hv > 12) ? 12 : hv - 12));
            }
            startMinuteTextField.setText(INT2_FORMAT.format(rangeStart.getMinute()));
            LocalDateTime rangeEnd = model.getEnd();
            if (null != rangeEnd) {
                long h = Duration.between(rangeStart, rangeEnd).toMinutes();
                long m = h % 60;
                durationHourTextField.setText(INTN_FORMAT.format((h - m) / 60));
                durationMinuteTextField.setText(INT2_FORMAT.format(m));
            }
        }
        parsedStartHour = Bindings.createObjectBinding(() -> calculateHour(startHourTextField.getText(), 1, 12), startHourTextField.textProperty());
        parsedStartMinute = Bindings.createObjectBinding(() -> calculateMinute(startMinuteTextField.getText()), startMinuteTextField.textProperty());
        startDateTimeBinding = Bindings.createObjectBinding(() -> calculateStartDateTime(startDatePicker.getValue(), parsedStartHour.get(),
                parsedStartMinute.get(), amPmComboBox.getSelectionModel().getSelectedItem()), startDatePicker.valueProperty(), parsedStartHour,
                parsedStartMinute, amPmComboBox.getSelectionModel().selectedItemProperty());
        parsedDurationHour = Bindings.createObjectBinding(() -> calculateHour(durationHourTextField.getText(), 0, 256),
                durationHourTextField.textProperty());
        parsedDurationMinute = Bindings.createObjectBinding(() -> calculateMinute(durationMinuteTextField.getText()),
                durationMinuteTextField.textProperty());
        endDateTimeBinding = Bindings.createObjectBinding(() -> calculateEndDateTime(startDateTimeValue.get(), parsedDurationHour.get(),
                parsedDurationMinute.get()), startDateTimeValue, parsedDurationHour, parsedDurationMinute);
        startDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            LOG.entering(LogHelper.toLambdaSourceClass(LOG, "initialize", "startDatePicker#value"), "changed", new Object[]{oldValue, newValue});
            checkStartChange();
            LOG.exiting(LogHelper.toLambdaSourceClass(LOG, "initialize", "startDatePicker#value"), "changed");
        });
        startHourTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            LOG.entering(LogHelper.toLambdaSourceClass(LOG, "initialize", "startHourTextField#text"), "changed", new Object[]{oldValue, newValue});
            checkStartChange();
            LOG.exiting(LogHelper.toLambdaSourceClass(LOG, "initialize", "startHourTextField#text"), "changed");
        });
        startMinuteTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            LOG.entering(LogHelper.toLambdaSourceClass(LOG, "initialize", "startMinuteTextField#text"), "changed", new Object[]{oldValue, newValue});
            checkStartChange();
            LOG.exiting(LogHelper.toLambdaSourceClass(LOG, "initialize", "startMinuteTextField#text"), "changed");
        });
        amPmComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            LOG.entering(LogHelper.toLambdaSourceClass(LOG, "initialize", "amPmComboBox#value"), "changed", new Object[]{oldValue, newValue});
            checkStartChange();
            LOG.exiting(LogHelper.toLambdaSourceClass(LOG, "initialize", "amPmComboBox#value"), "changed");
        });
        startDateTimeValue.addListener((observable, oldValue, newValue) -> {
            LOG.entering(LogHelper.toLambdaSourceClass(LOG, "initialize", "startDateTimeValue#value"), "changed", new Object[]{oldValue, newValue});
            checkEndChange(Optional.of(newValue));
            LOG.exiting(LogHelper.toLambdaSourceClass(LOG, "initialize", "startDateTimeValue#value"), "changed");
        });
        durationHourTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            LOG.entering(LogHelper.toLambdaSourceClass(LOG, "initialize", "durationHourTextField#text"), "changed", new Object[]{oldValue, newValue});
            checkEndChange(Optional.empty());
            LOG.exiting(LogHelper.toLambdaSourceClass(LOG, "initialize", "durationHourTextField#text"), "changed");
        });
        durationMinuteTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            LOG.entering(LogHelper.toLambdaSourceClass(LOG, "initialize", "durationMinuteTextField#text"), "changed", new Object[]{oldValue, newValue});
            checkEndChange(Optional.empty());
            LOG.exiting(LogHelper.toLambdaSourceClass(LOG, "initialize", "durationMinuteTextField#text"), "changed");
        });
        endDateTimeValue.addListener((observable, oldValue, newValue) -> {
            LOG.entering(LogHelper.toLambdaSourceClass(LOG, "initialize", "endDateTimeValue#value"), "changed", new Object[]{oldValue, newValue});
            checkRangeChange(startDateTimeValue.get(), endDateTimeValue.get());
            LOG.exiting(LogHelper.toLambdaSourceClass(LOG, "initialize", "endDateTimeValue#value"), "changed");
        });
        checkStartChange();
        checkEndChange(Optional.empty());
        LOG.exiting(LOG.getName(), "initialize");
    }

    private synchronized void checkStartChange() {
        startDateTimeBinding.get().accept(
                (t) -> {
                    if (!t.equals(startDateTimeValue.get())) {
                        startDateTimeValue.set(t);
                    }
                    if (!startValidationMessage.get().isEmpty()) {
                        startValidationMessage.set("");
                    }
                },
                (t) -> {
                    if (null != startDateTimeValue.getValue()) {
                        startDateTimeValue.setValue(null);
                    }
                    if (!t.equals(startValidationMessage.get())) {
                        startValidationMessage.set(t);
                    }
                }
        );
    }

    private synchronized void checkEndChange(Optional<LocalDateTime> startChange) {
        endDateTimeBinding.get().accept(
                (t) -> {
                    boolean c = !t.equals(endDateTimeValue.get());
                    if (c) {
                        endDateTimeValue.set(t);
                        String s = DATETIME_FORMAT.format(t);
                        if (!endDateTimeLabel.getText().equals(s)) {
                            endDateTimeLabel.setText(s);
                        }
                    }
                    if (!durationValidationLabel.getText().isEmpty()) {
                        durationValidationLabel.setText("");
                    }
                    if (durationValidationLabel.isVisible()) {
                        durationValidationLabel.setVisible(false);
                    }
                    if (!c) {
                        startChange.ifPresent((u) -> checkRangeChange(u, t));
                    }
                },
                (t) -> {
                    if (!endDateTimeLabel.getText().isEmpty()) {
                        endDateTimeLabel.setText("");
                    }
                    boolean c = !t.isEmpty();
                    if (durationValidationLabel.isVisible() != c) {
                        durationValidationLabel.setVisible(c);
                    }
                    c = null != endDateTimeValue.get();
                    if (c) {
                        endDateTimeValue.set(null);
                    }
                    if (!t.equals(durationValidationLabel.getText())) {
                        durationValidationLabel.setText(t);
                    }
                    if (!c) {
                        startChange.ifPresent((u) -> checkRangeChange(u, null));
                    }
                }
        );
    }

    private synchronized void checkRangeChange(LocalDateTime start, LocalDateTime end) {
        Tuple<LocalDateTime, LocalDateTime> oldValue = range.get();
        if (null == start || null == end) {
            if (null != oldValue) {
                range.set(null);
                valid.set(false);
            }
        } else if (null == oldValue) {
            range.set(Tuple.of(start, end));
            valid.set(true);
        } else if (!(start.equals(oldValue.getValue1()) && end.equals(oldValue.getValue2()))) {
            range.set(Tuple.of(start, end));
        }
    }

}
