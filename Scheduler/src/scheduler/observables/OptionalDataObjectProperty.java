package scheduler.observables;

import java.util.Optional;
import scheduler.dao.DataElement;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T>
 */
public class OptionalDataObjectProperty<T extends DataElement> extends OptionalValueProperty<T> {

    public OptionalDataObjectProperty() {
    }

    public OptionalDataObjectProperty(Optional<T> initialValue) {
        super(requireExisting(initialValue, "Object does not exist"));
    }

    public OptionalDataObjectProperty(Object bean, String name) {
        super(bean, name);
    }

    public OptionalDataObjectProperty(Object bean, String name, Optional<T> initialValue) {
        super(bean, name, requireExisting(initialValue, "Object does not exist"));
    }

    @Override
    public void set(Optional<T> newValue) {
        super.set(requireExisting(newValue, "Object does not exist")); //To change body of generated methods, choose Tools | Templates.
    }

    private static <V extends DataElement> Optional<V> requireExisting(Optional<V> value, String message) {
        if (value != null) {
            value.ifPresent((t) -> {
                if (!t.isExisting()) {
                    throw new IllegalArgumentException(message);
                }
            });
        }
        return value;
    }
}
