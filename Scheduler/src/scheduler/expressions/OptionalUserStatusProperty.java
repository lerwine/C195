package scheduler.expressions;

import java.util.Optional;
import scheduler.dao.UserFactory;

/**
 *
 * @author erwinel
 */
public class OptionalUserStatusProperty extends OptionalValueProperty<Integer> {

    public OptionalUserStatusProperty() {
        super(Optional.empty());
    }

    public OptionalUserStatusProperty(Optional<Integer> initialValue) {
        super(UserFactory.requireValidStatus(initialValue));
    }

    public OptionalUserStatusProperty(Object bean, String name) {
        super(bean, name, Optional.empty());
    }

    public OptionalUserStatusProperty(Object bean, String name, Optional<Integer> initialValue) {
        super(bean, name, UserFactory.requireValidStatus(initialValue));
    }

    @Override
    public void set(Optional<Integer> newValue) {
        super.set(UserFactory.requireValidStatus(newValue));
    }
    
}
