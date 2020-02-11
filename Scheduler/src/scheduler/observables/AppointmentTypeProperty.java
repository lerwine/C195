package scheduler.observables;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringPropertyBase;
import javafx.beans.property.SimpleStringProperty;
import scheduler.util.Values;

/**
 * A {@SimpleStringProperty} that contains appointment type code strings
 * @author erwinel
 */
public class AppointmentTypeProperty extends SimpleStringProperty {
    ReadOnlyStringProperty readOnlyProperty;
    
    /**
     * Returns the readonly property, that is synchronized with this {@code AppointmentTypeProperty}.
     * @return the readonly property
     */
    public ReadOnlyStringProperty getReadOnlyProperty() {
        if (readOnlyProperty == null)
            readOnlyProperty = new AppointmentTypeProperty.ReadOnlyPropertyImpl();
        return readOnlyProperty;
    }

    public AppointmentTypeProperty() {
        super(Values.APPOINTMENTTYPE_OTHER);
    }

    public AppointmentTypeProperty(String initialValue) {
        super(Values.asValidAppointmentType(initialValue));
    }

    public AppointmentTypeProperty(Object bean, String name) {
        super(bean, name, Values.APPOINTMENTTYPE_OTHER);
    }

    public AppointmentTypeProperty(Object bean, String name, String initialValue) {
        super(bean, name, Values.asValidAppointmentType(initialValue));
    }
    
    @Override
    public void set(String newValue) { super.set(Values.asValidAppointmentType(newValue)); }

    private class ReadOnlyPropertyImpl extends ReadOnlyStringPropertyBase {
        @Override
        public String get() { return AppointmentTypeProperty.this.get(); }
        @Override
        public Object getBean() { return AppointmentTypeProperty.this.getBean(); }
        @Override
        public String getName() { return AppointmentTypeProperty.this.getName(); }
        private ReadOnlyPropertyImpl() {
            AppointmentTypeProperty.this.addListener((observable, oldValue, newValue) -> {
                super.fireValueChangedEvent();
            });
        }
    };
}
