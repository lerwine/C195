package scheduler.observables;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import scheduler.util.BinarySelective;

/**
 *
 * @author Leonard T. Erwine
 */
public class LocalDateTimeBindingProperty extends ObjectBinding<BinarySelective<LocalDateTime, String>> implements ReadOnlyProperty<BinarySelective<LocalDateTime, String>> {
    private final String name;
    private final Object bean;
    private final ObservableList<Observable> dependencies;
    private final ObservableList<Observable> readOnlyDependencies;
    private final ObjectProperty<LocalDate> date;
    private final ReadOnlyObjectProperty<Integer> hour;
    private final ReadOnlyObjectProperty<Integer> minute;
    private final Messages messages;

    public interface Messages {
        String getDateNotSelectedMessage();
        String getTimeNotSelectedMessage();
    }
    
    public LocalDateTimeBindingProperty(Object bean, String name, ObjectProperty<LocalDate> date, ReadOnlyObjectProperty<Integer> hour,
            ReadOnlyObjectProperty<Integer> minute, Messages messages) {
        this.bean = bean;
        this.name = (null == name) ? "" : name;
        this.date = Objects.requireNonNull(date);
        this.hour = Objects.requireNonNull(hour);
        this.minute = Objects.requireNonNull(minute);
        this.messages = Objects.requireNonNull(messages);
        this.dependencies = FXCollections.observableArrayList(date, hour, minute);
        readOnlyDependencies = FXCollections.unmodifiableObservableList(this.dependencies);
        super.bind(dependencies);
    }
    
    @Override
    protected BinarySelective<LocalDateTime, String> computeValue() {
        LocalDate d = date.get();
        Integer h = hour.get();
        Integer m = minute.get();
        if (null == d)
            return BinarySelective.ofSecondary(messages.getDateNotSelectedMessage());
        if (null == h || null == m)
            return BinarySelective.ofSecondary(messages.getTimeNotSelectedMessage());
        return BinarySelective.ofPrimary(d.atTime(h, h, 0, 0));
    }

    @Override
    public Object getBean() {
        return bean;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ObservableList<?> getDependencies() {
        return readOnlyDependencies;
    }

    @Override
    public void dispose() {
        super.dispose();
        super.unbind(date, hour, minute);
        dependencies.clear();
    }
    
    public StringBinding validationMessage() {
        return Bindings.createStringBinding(() -> {
            BinarySelective<LocalDateTime, String> result = get();
            return (result.isPrimary()) ? "" : result.getSecondary();
        }, this);
    }
    
}
