package scheduler.observables;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerPropertyBase;
import javafx.beans.property.SimpleIntegerProperty;
import scheduler.util.Values;

/**
 *
 * @author erwinel
 */
public class UserStatusProperty extends SimpleIntegerProperty {

    private ReadOnlyIntegerProperty readOnlyProperty;

    public UserStatusProperty() {
        this(Values.USER_STATUS_NORMAL);
    }

    public UserStatusProperty(int initialValue) {
        super(Values.asValidUserStatus(initialValue));
    }

    public UserStatusProperty(Object bean, String name) {
        this(bean, name, Values.USER_STATUS_NORMAL);
    }

    public UserStatusProperty(Object bean, String name, int initialValue) {
        super(bean, name, Values.asValidUserStatus(initialValue));
    }

    /**
     * Returns the readonly property, that is synchronized with this {@code StatusProperty}.
     *
     * @return the readonly property
     */
    public ReadOnlyIntegerProperty getReadOnlyProperty() {
        if (readOnlyProperty == null) {
            readOnlyProperty = new ReadOnlyPropertyImpl();
        }
        return readOnlyProperty;
    }

    @Override
    public void set(int newValue) {
        super.set(Values.asValidUserStatus(newValue));
    }

    private class ReadOnlyPropertyImpl extends ReadOnlyIntegerPropertyBase {

        @Override
        public int get() {
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
