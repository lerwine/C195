package scheduler.observables;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import scheduler.util.BinarySelective;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class LocalDateTimeBindingProperty extends ObjectBindingProperty<BinarySelective<LocalDateTime, String>> {

    private final ObjectProperty<LocalDate> date;
    private final ReadOnlyObjectProperty<Integer> hour;
    private final ReadOnlyObjectProperty<Integer> minute;
    private final Messages messages;

    public LocalDateTimeBindingProperty(Object bean, String name, ObjectProperty<LocalDate> date, ReadOnlyObjectProperty<Integer> hour,
            ReadOnlyObjectProperty<Integer> minute, Messages messages) {
        super(bean, name, date, hour, minute);
        this.date = Objects.requireNonNull(date);
        this.hour = Objects.requireNonNull(hour);
        this.minute = Objects.requireNonNull(minute);
        this.messages = Objects.requireNonNull(messages);
    }

    @Override
    protected BinarySelective<LocalDateTime, String> computeValue() {
        LocalDate d = date.get();
        Integer h = hour.get();
        Integer m = minute.get();
        if (null == d) {
            return BinarySelective.ofSecondary(messages.getDateNotSelectedMessage());
        }
        if (null == h || null == m) {
            return BinarySelective.ofSecondary(messages.getTimeNotSelectedMessage());
        }
        return BinarySelective.ofPrimary(d.atTime(h, h, 0, 0));
    }

    public StringBinding validationMessage() {
        return Bindings.createStringBinding(() -> {
            BinarySelective<LocalDateTime, String> result = get();
            return (result.isPrimary()) ? "" : result.getSecondary();
        }, this);
    }

    public interface Messages {

        String getDateNotSelectedMessage();

        String getTimeNotSelectedMessage();
    }

}
