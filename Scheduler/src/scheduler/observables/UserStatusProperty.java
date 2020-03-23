package scheduler.observables;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectPropertyBase;
import javafx.beans.property.SimpleObjectProperty;
import scheduler.dao.UserStatus;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
 */
public class UserStatusProperty extends SimpleObjectProperty<UserStatus> {

    private ReadOnlyObjectProperty<UserStatus> readOnlyProperty;

    public UserStatusProperty() {
        this(UserStatus.NORMAL);
    }

    public UserStatusProperty(UserStatus initialValue) {
        super((null == initialValue) ? UserStatus.NORMAL : initialValue);
    }

    public UserStatusProperty(Object bean, String name) {
        this(bean, name, UserStatus.NORMAL);
    }

    public UserStatusProperty(Object bean, String name, UserStatus initialValue) {
        super(bean, name, (null == initialValue) ? UserStatus.NORMAL : initialValue);
    }

    /**
     * Returns the readonly property, that is synchronized with this {@code StatusProperty}.
     *
     * @return the readonly property
     */
    public ReadOnlyObjectProperty<UserStatus> getReadOnlyProperty() {
        if (readOnlyProperty == null) {
            readOnlyProperty = new ReadOnlyPropertyImpl();
        }
        return readOnlyProperty;
    }

    @Override
    public void set(UserStatus newValue) {
        super.set((null == newValue) ? UserStatus.NORMAL : newValue);
    }

    private class ReadOnlyPropertyImpl extends ReadOnlyObjectPropertyBase<UserStatus> {

        @Override
        public UserStatus get() {
            return UserStatusProperty.this.get();
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
                super.fireValueChangedEvent();
            });
        }
    };
}
