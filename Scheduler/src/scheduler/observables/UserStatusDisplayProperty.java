package scheduler.observables;

import java.util.Objects;
import javafx.beans.property.ReadOnlyObjectProperty;
import scheduler.model.UserStatus;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class UserStatusDisplayProperty extends StringBindingProperty {

    private final ReadOnlyObjectProperty<UserStatus> backingProperty;

    public UserStatusDisplayProperty(Object bean, String name, ReadOnlyObjectProperty<UserStatus> statusProperty) {
        super(bean, name, statusProperty);
        backingProperty = Objects.requireNonNull(statusProperty);
    }

    @Override
    protected String computeValue() {
        return UserStatus.toDisplayValue(backingProperty.get());
    }

}
