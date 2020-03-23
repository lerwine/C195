package scheduler.observables;

import java.util.Optional;
import scheduler.dao.UserStatus;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
 */
public class OptionalUserStatusProperty extends OptionalValueProperty<UserStatus> {

    public OptionalUserStatusProperty() {
        super(Optional.empty());
    }

    public OptionalUserStatusProperty(Optional<UserStatus> initialValue) {
        super(initialValue);
    }

    public OptionalUserStatusProperty(Object bean, String name) {
        super(bean, name, Optional.empty());
    }

    public OptionalUserStatusProperty(Object bean, String name, Optional<UserStatus> initialValue) {
        super(bean, name, initialValue);
    }

    @Override
    public void set(Optional<UserStatus> newValue) {
        super.set(newValue);
    }

}
