package scheduler.observables;

import java.util.Objects;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectPropertyBase;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import scheduler.model.UserStatus;

/**
 * An integer property that only stores specific integer values that represent active status for users.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public final class UserStatusProperty extends SimpleObjectProperty<UserStatus> {

    private final ReadOnlyPropertyImpl readOnlyProperty;
    private final CalculatedBooleanProperty<UserStatus> valid;
    private final CalculatedStringProperty<UserStatus> displayText;
    private final CalculatedBooleanProperty<UserStatus> active;

    /**
     *
     * @param bean
     * @param name
     * @param initialValue
     */
    public UserStatusProperty(Object bean, String name, UserStatus initialValue) {
        super(bean, name, initialValue);
        readOnlyProperty = new ReadOnlyPropertyImpl();
        valid = new CalculatedBooleanProperty<>(this, "valid", this, Objects::nonNull);
        displayText = new CalculatedStringProperty<>(this, "active", this, UserStatus::toDisplayValue);
        active = new CalculatedBooleanProperty<>(this, "active", this, UserStatus::isActive);
    }

    public UserStatus getSafe() {
        UserStatus status = get();
        return (null == status) ? UserStatus.NORMAL : status;
    }

    public ReadOnlyObjectProperty<UserStatus> getReadOnlyProperty() {
        return readOnlyProperty;
    }

    public boolean isValid() {
        return valid.get();
    }

    public ReadOnlyBooleanProperty validProperty() {
        return valid.getReadOnlyBooleanProperty();
    }

    public String getDisplayText() {
        return displayText.get();
    }
    
    public ReadOnlyStringProperty displayTextProperty() {
        return displayText.getReadOnlyStringProperty();
    }
    
    public boolean isActive() {
        return active.get();
    }

    public ReadOnlyBooleanProperty activeProperty() {
        return active.getReadOnlyBooleanProperty();
    }

    private class ReadOnlyPropertyImpl extends ReadOnlyObjectPropertyBase<UserStatus> {

        @Override
        public UserStatus get() {
            return UserStatusProperty.this.getSafe();
        }

        @Override
        public Object getBean() {
            return UserStatusProperty.this.getBean();
        }

        @Override
        public String getName() {
            return UserStatusProperty.this.getName();
        }

        private ReadOnlyPropertyImpl() {
            UserStatusProperty.this.addListener((observable, oldValue, newValue) -> {
                ReadOnlyPropertyImpl.this.fireValueChangedEvent();
            });
        }

    }

}
