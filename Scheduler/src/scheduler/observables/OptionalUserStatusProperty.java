package scheduler.observables;

import java.util.Optional;
import scheduler.util.Values;

/**
 *
 * @author erwinel
 */
public class OptionalUserStatusProperty extends OptionalValueProperty<Integer> {

    public OptionalUserStatusProperty() {
        super(Optional.empty());
    }

    public OptionalUserStatusProperty(Optional<Integer> initialValue) {
        super(Values.requireValidUserStatus(initialValue, "Invalid initial value"));
    }

    public OptionalUserStatusProperty(Object bean, String name) {
        super(bean, name, Optional.empty());
    }

    public OptionalUserStatusProperty(Object bean, String name, Optional<Integer> initialValue) {
        super(bean, name, Values.requireValidUserStatus(initialValue, "Invalid initial value"));
    }

    @Override
    public void set(Optional<Integer> newValue) {
        super.set(Values.requireValidUserStatus(newValue, "Invalid initial value"));
    }

}
