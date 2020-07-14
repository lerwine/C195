package scheduler.view.appointment.edit;

import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TextField;
import scheduler.util.BinarySelective;
import scheduler.util.LogHelper;
import scheduler.util.Tuple;
import scheduler.view.appointment.EditAppointment;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class DateRangeController {

    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(DateRangeController.class.getName()), Level.FINER);
//    private static final Logger LOG = Logger.getLogger(DateRangeController.class.getName());

    private static final Pattern INT_PATTERN = Pattern.compile("^\\s*\\d{1,4}\\s*");
    public static final NumberFormat INTN_FORMAT;
    public static final NumberFormat INT2_FORMAT;

    static {
        INT2_FORMAT = NumberFormat.getIntegerInstance();
        INT2_FORMAT.setMinimumIntegerDigits(2);
        INT2_FORMAT.setMaximumIntegerDigits(2);
        INTN_FORMAT = NumberFormat.getIntegerInstance();
    }

    private static BinarySelective<LocalDateTime, String> calculateEndDateTime(BinarySelective<LocalDateTime, String> start,
            BinarySelective<Integer, String> hour, BinarySelective<Integer, String> minute) {
        return start.map(
                (t) -> hour.map(
                        (u) -> minute.map(
                                (v) -> BinarySelective.ofPrimary(
                                        (u > 0)
                                                ? ((v > 0) ? t.plusMinutes(v) : t).plusHours(u)
                                                : ((v > 0) ? t.plusMinutes(v) : t)
                                ),
                                (v) -> BinarySelective.ofSecondary((v.isEmpty()) ? "* Minute required" : v)
                        ),
                        (u) -> BinarySelective.ofSecondary(
                                (u.isEmpty())
                                ? minute.map(
                                        (v) -> "* Hour required",
                                        (v) -> (v.isEmpty()) ? "* Hour required" : v
                                )
                                : u
                        )
                ),
                (t) -> BinarySelective.ofSecondary(
                        (t.isEmpty())
                        ? hour.map(
                                (u) -> minute.map(
                                        (v) -> "* Required",
                                        (v) -> (v.isEmpty()) ? "* Minute required" : v
                                ),
                                (u) -> (u.isEmpty())
                                ? minute.map(
                                        (v) -> "* Hour required",
                                        (v) -> (v.isEmpty()) ? "* Hour required" : v)
                                : u
                        )
                        : t
                )
        );
    }

    private static BinarySelective<LocalDateTime, String> calculateDateTime(LocalDate date, BinarySelective<Integer, String> hour,
            BinarySelective<Integer, String> minute, boolean isAm) {
        if (null == date) {
            return BinarySelective.ofSecondary(
                    hour.map(
                            (t) -> minute.map((u) -> "* Required", (u) -> (u.isEmpty()) ? "* Required" : u),
                            (t) -> {
                                if (t.isEmpty()) {
                                    return minute.map((u) -> "* Required", (u) -> (u.isEmpty()) ? "* Required" : u);
                                }
                                return t;
                            }
                    )
            );
        }

        return hour.map(
                (hv) -> minute.map(
                        (mv) -> BinarySelective.ofPrimary(date.atTime(hv, mv)),
                        (mm) -> BinarySelective.ofSecondary((mm.isEmpty()) ? "* Minute required" : mm)
                ),
                (hm) -> BinarySelective.ofSecondary(
                        (hm.isEmpty())
                        ? minute.map((mv) -> "* Hour required", (mm) -> (mm.isEmpty()) ? "* Hour required" : "* Minute required")
                        : hm
                )
        );
    }

    private static BinarySelective<Integer, String> calculateHour(String text, int minValue, int maxValue) {
        String trimmed;
        if (null == text || (trimmed = text.trim()).isEmpty()) {
            return BinarySelective.ofSecondary("");
        }
        try {
            Matcher m = INT_PATTERN.matcher(trimmed);
            if (!m.find()) {
                throw new ParseException("Invalid number", text.length() - trimmed.length());
            } else if (m.end() < trimmed.length()) {
                throw new ParseException("Invalid number", m.end() + (text.length() - trimmed.length()));
            }
            Number parse = INTN_FORMAT.parse(trimmed);
            int i = parse.intValue();
            if (i < minValue || i > maxValue) {
                return BinarySelective.ofSecondary("Hour out of range");
            }
            return BinarySelective.ofPrimary(i);
        } catch (ParseException ex) {
            int i = ex.getErrorOffset();
            return BinarySelective.ofSecondary((i > 0 && i < trimmed.length()) ? String.format("Invalid hour format at position %d", i)
                    : "Invalid hour number");
        }
    }

    private static BinarySelective<Integer, String> calculateMinute(String text) {
        String trimmed;
        if (null == text || (trimmed = text.trim()).isEmpty()) {
            return BinarySelective.ofSecondary("");
        }
        try {
            Matcher m = INT_PATTERN.matcher(trimmed);
            if (!m.find()) {
                throw new ParseException("Invalid number", text.length() - trimmed.length());
            } else if (m.end() < trimmed.length()) {
                throw new ParseException("Invalid number", m.end() + (text.length() - trimmed.length()));
            }
            Number parse = INTN_FORMAT.parse(trimmed);
            int i = parse.intValue();
            if (i < 0 || i > 59) {
                return BinarySelective.ofSecondary("Minute out of range");
            }
            return BinarySelective.ofPrimary(i);
        } catch (ParseException ex) {
            int i = ex.getErrorOffset();
            return BinarySelective.ofSecondary((i > 0 && i < trimmed.length()) ? String.format("Invalid minute format at position %d", i)
                    : "Invalid minute number");
        }
    }

    private final ReadOnlyObjectWrapper<BinarySelective<Integer, String>> startHour;
    private final ReadOnlyObjectWrapper<BinarySelective<Integer, String>> startMinute;
    private final ObjectBinding<BinarySelective<LocalDateTime, String>> startDateTimeValueBinding;
    private final ReadOnlyObjectWrapper<LocalDateTime> startDateTime;
    private final StringProperty startValidationMessage;
    private final BooleanProperty startInvalid;
    private final ReadOnlyObjectWrapper<BinarySelective<Integer, String>> durationHour;
    private final ReadOnlyObjectWrapper<BinarySelective<Integer, String>> durationMinute;
    private final ObjectBinding<BinarySelective<LocalDateTime, String>> endDateTimeValueBinding;
    private final ReadOnlyObjectWrapper<LocalDateTime> endDateTime;
    private final StringProperty durationValidationMessage;
    private final BooleanProperty durationInvalid;
    private final ReadOnlyObjectWrapper<Tuple<LocalDateTime, LocalDateTime>> dateRange;
    private final ObjectBinding<Tuple<LocalDateTime, LocalDateTime>> rangeBinding;
    private final ReadOnlyBooleanWrapper valid;

    public DateRangeController(EditAppointment editAppointmentControl) {
        TextField textField = editAppointmentControl.getStartHourTextField();
        StringProperty startHourText = textField.textProperty();
        startHour = new ReadOnlyObjectWrapper<>(this, "startHour", calculateHour(textField.getText(), 1, 12));
        textField = editAppointmentControl.getStartMinuteTextField();
        StringProperty startMinuteText = textField.textProperty();
        startMinute = new ReadOnlyObjectWrapper<>(this, "startMinute", calculateMinute(textField.getText()));
        ObjectProperty<LocalDate> startDate = editAppointmentControl.getStartDatePicker().valueProperty();
        ComboBox<Boolean> amPmComboBox = editAppointmentControl.getAmPmComboBox();
        amPmComboBox.setItems(FXCollections.observableArrayList(Boolean.TRUE, Boolean.FALSE));
        SingleSelectionModel<Boolean> selectedAmPmSelectionModel = editAppointmentControl.getAmPmComboBox().getSelectionModel();
        ReadOnlyObjectProperty<Boolean> amPm = selectedAmPmSelectionModel.selectedItemProperty();
        startDateTimeValueBinding = Bindings.createObjectBinding(() -> {
            LOG.fine(() -> String.format("Calculating startDateTimeValueBinding: startDate=%s; startHour=%s; startMinute=%s; amPm=%s", LogHelper.toLogText(startDate.get()),
                    LogHelper.toLogText(startHour.get()), LogHelper.toLogText(startMinute.get()), LogHelper.toLogText(amPm.get())));
            BinarySelective<LocalDateTime, String> r = calculateDateTime(startDate.get(), startHour.get(), startMinute.get(), amPm.get());
            LOG.fine(() -> String.format("startDateTimeValueBinding changing to %s", r));
            return r;
        }, startDate, startHour, startMinute, amPm);
        startDateTime = new ReadOnlyObjectWrapper<>(this, "startDateTime", startDateTimeValueBinding.get().toPrimary(null));
        Label label = editAppointmentControl.getStartValidationLabel();
        startValidationMessage = label.textProperty();
        startValidationMessage.set(startDateTimeValueBinding.get().toSecondary(""));
        startInvalid = label.visibleProperty();
        startInvalid.bind(startValidationMessage.isNotEmpty());
        textField = editAppointmentControl.getDurationHourTextField();
        StringProperty durationHourText = textField.textProperty();
        durationHour = new ReadOnlyObjectWrapper<>(this, "durationHour", calculateHour(durationHourText.get(), 1, 12));
        textField = editAppointmentControl.getDurationMinuteTextField();
        StringProperty durationMinuteText = textField.textProperty();
        durationMinute = new ReadOnlyObjectWrapper<>(this, "durationMinute", calculateMinute(durationMinuteText.get()));
        endDateTimeValueBinding = Bindings.createObjectBinding(() -> {
            LOG.fine(() -> String.format("Calculating endDateTimeValueBinding: startDateTimeValueBinding=%s; durationHour=%s; durationMinute=%s",
                    LogHelper.toLogText(startDateTimeValueBinding.get()), LogHelper.toLogText(durationHour.get()), LogHelper.toLogText(durationMinute.get())));
            BinarySelective<LocalDateTime, String> r = calculateEndDateTime(startDateTimeValueBinding.get(), durationHour.get(), durationMinute.get());
            LOG.fine(() -> String.format("endDateTimeValueBinding changing to %s", r));
            return r;
        }, startDateTime, durationHour, durationMinute);
        endDateTime = new ReadOnlyObjectWrapper<>(this, "endDateTime", endDateTimeValueBinding.get().toPrimary(null));
        label = editAppointmentControl.getDurationValidationLabel();
        durationValidationMessage = label.textProperty();
        durationValidationMessage.set(endDateTimeValueBinding.get().toSecondary(""));
        durationInvalid = label.visibleProperty();
        durationInvalid.bind(durationValidationMessage.isNotEmpty());
        rangeBinding = Bindings.createObjectBinding(() -> {
            LocalDateTime s = startDateTime.get();
            LocalDateTime e = endDateTime.get();
            LOG.fine(() -> String.format("Calculating rangeBinding: startDateTime=%s; endDateTime=%s", LogHelper.toLogText(s), LogHelper.toLogText(e)));
            if (null == e || null == s) {
                LOG.fine("rangeBinding changing to null");
                return null;
            }
            LOG.fine(() -> String.format("rangeBinding changing to Tuple.of(%s, %s)", LogHelper.toLogText(s), LogHelper.toLogText(e)));
            return Tuple.of(s, e);
        }, startDateTime, endDateTime);
        dateRange = new ReadOnlyObjectWrapper<>(this, "range", rangeBinding.get());
        valid = new ReadOnlyBooleanWrapper(this, "valid", null != dateRange.get());

        startHourText.addListener((observable, oldValue, newValue) -> {
            LOG.fine(() -> String.format("Calculating startHourText: %s", LogHelper.toLogText(newValue)));
            BinarySelective<Integer, String> r = calculateHour(newValue, 1, 12);
            LOG.fine(() -> String.format("Setting startHour to %s", r));
            startHour.set(r);
        });
        startMinuteText.addListener((observable, oldValue, newValue) -> {
            LOG.fine(() -> String.format("Calculating startMinuteText: %s", LogHelper.toLogText(newValue)));
            BinarySelective<Integer, String> r = calculateMinute(newValue);
            LOG.fine(() -> String.format("Setting startMinute to %s", r));
            startMinute.set(r);
        });
        amPm.addListener((observable, oldValue, newValue) -> onStartValueChanged());
        startDate.addListener((observable, oldValue, newValue) -> onStartValueChanged());
        startHour.addListener((observable, oldValue, newValue) -> onStartValueChanged());
        startMinute.addListener((observable, oldValue, newValue) -> onStartValueChanged());
        durationHourText.addListener((observable, oldValue, newValue) -> {
            LOG.fine(() -> String.format("Calculating durationHourText: %s", LogHelper.toLogText(newValue)));
            BinarySelective<Integer, String> r = calculateHour(newValue, 0, 512);
            LOG.fine(() -> String.format("Setting durationHour to %s", r));
            durationHour.set(r);
        });
        durationMinuteText.addListener((observable, oldValue, newValue) -> {
            LOG.fine(() -> String.format("Calculating durationMinuteText: %s", LogHelper.toLogText(newValue)));
            BinarySelective<Integer, String> r = calculateMinute(newValue);
            LOG.fine(() -> String.format("Setting durationMinute to %s", r));
            durationMinute.set(r);
        });
        startDateTime.addListener((observable, oldValue, newValue) -> onEndValueChanged());
        durationHour.addListener((observable, oldValue, newValue) -> onEndValueChanged());
        durationMinute.addListener((observable, oldValue, newValue) -> onEndValueChanged());
        dateRange.addListener((observable, oldValue, newValue) -> valid.set(null != newValue));
    }

    public BinarySelective<Integer, String> getStartHour() {
        return startHour.get();
    }

    public ReadOnlyObjectProperty<BinarySelective<Integer, String>> startHourProperty() {
        return startHour.getReadOnlyProperty();
    }

    public BinarySelective<Integer, String> getStartMinute() {
        return startMinute.get();
    }

    public ReadOnlyObjectProperty<BinarySelective<Integer, String>> startMinuteProperty() {
        return startMinute.getReadOnlyProperty();
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime.get();
    }

    public ReadOnlyObjectProperty<LocalDateTime> startDateTimeProperty() {
        return startDateTime.getReadOnlyProperty();
    }

    public String getStartValidationMessage() {
        return startValidationMessage.get();
    }

    public ReadOnlyStringProperty startValidationMessageProperty() {
        return startValidationMessage;
    }

    public boolean isStartInvalid() {
        return startInvalid.get();
    }

    public ReadOnlyBooleanProperty startInvalidProperty() {
        return startInvalid;
    }

    public BinarySelective<Integer, String> getDurationHour() {
        return durationHour.get();
    }

    public ReadOnlyObjectProperty<BinarySelective<Integer, String>> durationHourProperty() {
        return durationHour.getReadOnlyProperty();
    }

    public BinarySelective<Integer, String> getDurationMinute() {
        return durationMinute.get();
    }

    public ReadOnlyObjectProperty<BinarySelective<Integer, String>> durationMinuteProperty() {
        return durationMinute.getReadOnlyProperty();
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime.get();
    }

    public ReadOnlyObjectProperty<LocalDateTime> endDateTimeProperty() {
        return endDateTime.getReadOnlyProperty();
    }

    public String getDurationValidationMessage() {
        return durationValidationMessage.get();
    }

    public ReadOnlyStringProperty durationValidationMessageProperty() {
        return durationValidationMessage;
    }

    public boolean isDurationInvalid() {
        return durationInvalid.get();
    }

    public ReadOnlyBooleanProperty durationInvalidProperty() {
        return durationInvalid;
    }

    public Tuple<LocalDateTime, LocalDateTime> getDateRange() {
        return dateRange.get();
    }

    public ReadOnlyObjectProperty<Tuple<LocalDateTime, LocalDateTime>> dateRangeProperty() {
        return dateRange.getReadOnlyProperty();
    }

    public boolean isValid() {
        return valid.get();
    }

    public ReadOnlyBooleanProperty validProperty() {
        return valid.getReadOnlyProperty();
    }

    private void onStartValueChanged() {
        startDateTimeValueBinding.get().accept((t) -> {
            LOG.fine(() -> String.format("Start value changed to %s", t));
            startDateTime.set(t);
            startValidationMessage.set("");
        }, (t) -> {
            LOG.fine(() -> String.format("Start validation changed to %s", t));
            startDateTime.set(null);
            startValidationMessage.set(t);
        });
        Tuple<LocalDateTime, LocalDateTime> r = rangeBinding.get();
        LOG.fine(() -> String.format("Setting range to %s", r));
        dateRange.set(r);
    }

    private void onEndValueChanged() {
        endDateTimeValueBinding.get().accept((t) -> {
            LOG.fine(() -> String.format("End value changed to %s", t));
            endDateTime.set(t);
            durationValidationMessage.set("");
        }, (t) -> {
            LOG.fine(() -> String.format("End validation changed to %s", t));
            endDateTime.set(null);
            durationValidationMessage.set(t);
        });
        Tuple<LocalDateTime, LocalDateTime> r = rangeBinding.get();
        LOG.fine(() -> String.format("Setting range to %s", r));
        dateRange.set(r);
    }

}
