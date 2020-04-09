package scheduler.observables;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectPropertyBase;
import javafx.beans.property.SimpleObjectProperty;
import scheduler.dao.AppointmentType;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
 */
public class AppointmentTypeProperty extends SimpleObjectProperty<AppointmentType> {
    private final ReadOnlyPropertyImpl readOnlyProperty;
    
    public AppointmentTypeProperty() {
        super(AppointmentType.OTHER);
        readOnlyProperty = new ReadOnlyPropertyImpl();
    }

    public AppointmentTypeProperty(AppointmentType initialValue) {
        super((null == initialValue) ? AppointmentType.OTHER : initialValue);
        readOnlyProperty = new ReadOnlyPropertyImpl();
    }

    public AppointmentTypeProperty(Object bean, String name) {
        super(bean, name, AppointmentType.OTHER);
        readOnlyProperty = new ReadOnlyPropertyImpl();
    }

    public AppointmentTypeProperty(Object bean, String name, AppointmentType initialValue) {
        super(bean, name, (null == initialValue) ? AppointmentType.OTHER : initialValue);
        readOnlyProperty = new ReadOnlyPropertyImpl();
    }

    /**
     * Returns the readonly property, that is synchronized with this {@code AppointmentTypeProperty}.
     *
     * @return the readonly property
     */
    public ReadOnlyObjectProperty<AppointmentType> getReadOnlyProperty() {
        return readOnlyProperty;
    }

    @Override
    public void set(AppointmentType newValue) {
        super.set((null == newValue) ? AppointmentType.OTHER : newValue);
    }

    private class ReadOnlyPropertyImpl extends ReadOnlyObjectPropertyBase<AppointmentType> {

        @Override
        public AppointmentType get() {
            return AppointmentTypeProperty.this.get();
        }

        @Override
        public Object getBean() {
            return AppointmentTypeProperty.this.getBean();
        }

        @Override
        public String getName() {
            return AppointmentTypeProperty.this.getName();
        }

        private ReadOnlyPropertyImpl() {
            AppointmentTypeProperty.this.addListener((observable, oldValue, newValue) -> {
                super.fireValueChangedEvent();
            });
        }
    };
}
