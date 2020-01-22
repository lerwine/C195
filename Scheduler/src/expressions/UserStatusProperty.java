package expressions;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerPropertyBase;
import javafx.beans.property.SimpleIntegerProperty;
import scheduler.dao.factory.UserFactory;

/**
 *
 * @author erwinel
 */
public class UserStatusProperty extends SimpleIntegerProperty {

        private ReadOnlyIntegerProperty readOnlyProperty;
        
        /**
         * Returns the readonly property, that is synchronized with this {@code StatusProperty}.
         *
         * @return the readonly property
         */
        public ReadOnlyIntegerProperty getReadOnlyProperty() {
            if (readOnlyProperty == null)
                readOnlyProperty = new ReadOnlyPropertyImpl();
            return readOnlyProperty;
        }

        public UserStatusProperty() {
            this(UserFactory.STATUS_USER);
        }

        public UserStatusProperty(int initialValue) {
            super(UserFactory.asValidStatus(initialValue));
        }

        public UserStatusProperty(Object bean, String name) {
            this(bean, name, UserFactory.STATUS_USER);
        }

        public UserStatusProperty(Object bean, String name, int initialValue) {
            super(bean, name, UserFactory.asValidStatus(initialValue));
        }

        @Override
        public void set(int newValue) {
            super.set(UserFactory.asValidStatus(newValue));
        }

        private class ReadOnlyPropertyImpl extends ReadOnlyIntegerPropertyBase {

            @Override
            public int get() { return UserStatusProperty.this.get(); }
            
            @Override
            public Object getBean() { return UserStatusProperty.this.getBean(); }

            @Override
            public String getName() { return UserStatusProperty.this.getName(); }
            
            private ReadOnlyPropertyImpl() {
                UserStatusProperty.this.addListener((observable, oldValue, newValue) -> {
                    super.fireValueChangedEvent();
                });
            }
        };
}
