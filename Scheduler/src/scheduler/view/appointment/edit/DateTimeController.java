package scheduler.view.appointment.edit;

import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.StringProperty;
import scheduler.util.BinarySelective;
import scheduler.util.Tuple;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @todo Implement {@code scheduler.view.appointment.edit.DateTimeController}
 */
public class DateTimeController {

    private static final Pattern INT_PATTERN = Pattern.compile("^\\s*\\d{1,9}\\s*");
    private static final NumberFormat INT_FORMAT = NumberFormat.getIntegerInstance();

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
            Number parse = INT_FORMAT.parse(trimmed);
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
            Number parse = INT_FORMAT.parse(trimmed);
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
    private final ReadOnlyStringWrapper startValidationMessage;
    private final ReadOnlyObjectWrapper<BinarySelective<Integer, String>> durationHour;
    private final ReadOnlyObjectWrapper<BinarySelective<Integer, String>> durationMinute;
    private final ObjectBinding<BinarySelective<LocalDateTime, String>> endDateTimeValueBinding;
    private final ReadOnlyObjectWrapper<LocalDateTime> endDateTime;
    private final ReadOnlyStringWrapper durationValidationMessage;
    private final ReadOnlyObjectWrapper<Tuple<LocalDateTime, LocalDateTime>> range;
    private final ObjectBinding<Tuple<LocalDateTime, LocalDateTime>> rangeBinding;
    private final ReadOnlyBooleanWrapper valid;

    public DateTimeController(ObjectProperty<LocalDate> startDate, StringProperty startHourText, StringProperty startMinuteText,
            ReadOnlyObjectProperty<Boolean> amPm, StringProperty durationHourText, StringProperty durationMinuteText) {
        startHour = new ReadOnlyObjectWrapper<>(this, "startHour", calculateHour(startHourText.get(), 1, 12));
        startMinute = new ReadOnlyObjectWrapper<>(this, "startMinute", calculateMinute(startMinuteText.get()));
        startDateTimeValueBinding = Bindings.createObjectBinding(() -> calculateDateTime(startDate.get(), startHour.get(), startMinute.get(),
                amPm.get()), startDate, startHour, startMinute, amPm);
        startDateTime = new ReadOnlyObjectWrapper<>(this, "startDateTime", startDateTimeValueBinding.get().toPrimary(null));
        startValidationMessage = new ReadOnlyStringWrapper(this, "startValidationMessage", startDateTimeValueBinding.get().toSecondary(""));
        durationHour = new ReadOnlyObjectWrapper<>(this, "durationHour", calculateHour(durationHourText.get(), 1, 12));
        durationMinute = new ReadOnlyObjectWrapper<>(this, "durationMinute", calculateMinute(durationMinuteText.get()));
        endDateTimeValueBinding = Bindings.createObjectBinding(() -> calculateEndDateTime(startDateTimeValueBinding.get(), durationHour.get(),
                durationMinute.get()), startDateTime, durationHour, durationMinute);
        endDateTime = new ReadOnlyObjectWrapper<>(this, "endDateTime", endDateTimeValueBinding.get().toPrimary(null));
        durationValidationMessage = new ReadOnlyStringWrapper(this, "durationValidationMessage", endDateTimeValueBinding.get().toSecondary(""));
        rangeBinding = Bindings.createObjectBinding(() -> {
            LocalDateTime e = endDateTime.get();
            LocalDateTime s = startDateTime.get();
            if (null == e || null == s) {
                return null;
            }
            return Tuple.of(s, e);
        }, startDateTime, endDateTime);
        range = new ReadOnlyObjectWrapper<>(this, "range", rangeBinding.get());
        valid = new ReadOnlyBooleanWrapper(this, "valid", null != range.get());

        startHourText.addListener((observable, oldValue, newValue) -> startHour.set(calculateHour(newValue, 1, 12)));
        startMinuteText.addListener((observable, oldValue, newValue) -> startMinute.set(calculateMinute(newValue)));
        amPm.addListener((observable, oldValue, newValue) -> onStartValueChanged());
        startDate.addListener((observable, oldValue, newValue) -> onStartValueChanged());
        startHour.addListener((observable, oldValue, newValue) -> onStartValueChanged());
        startMinute.addListener((observable, oldValue, newValue) -> onStartValueChanged());
        durationHourText.addListener((observable, oldValue, newValue) -> durationHour.set(calculateHour(newValue, 0, 512)));
        durationMinuteText.addListener((observable, oldValue, newValue) -> durationMinute.set(calculateMinute(newValue)));
        startDateTime.addListener((observable, oldValue, newValue) -> onEndValueChanged());
        durationHour.addListener((observable, oldValue, newValue) -> onEndValueChanged());
        durationMinute.addListener((observable, oldValue, newValue) -> onEndValueChanged());
        range.addListener((observable, oldValue, newValue) -> valid.set(null != newValue));
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
        return startValidationMessage.getReadOnlyProperty();
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
        return durationValidationMessage.getReadOnlyProperty();
    }

    public boolean isValid() {
        return valid.get();
    }

    public ReadOnlyBooleanProperty validProperty() {
        return valid.getReadOnlyProperty();
    }

    private void onStartValueChanged() {
        startDateTimeValueBinding.get().accept((t) -> {
            startDateTime.set(t);
            startValidationMessage.set("");
        }, (t) -> {
            startDateTime.set(null);
            startValidationMessage.set(t);
        });
        range.set(rangeBinding.get());
    }

    private void onEndValueChanged() {
        endDateTimeValueBinding.get().accept((t) -> {
            endDateTime.set(t);
            durationValidationMessage.set("");
        }, (t) -> {
            endDateTime.set(null);
            durationValidationMessage.set(t);
        });
        range.set(rangeBinding.get());
    }

}
