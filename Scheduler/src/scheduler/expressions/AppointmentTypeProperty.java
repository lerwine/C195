package scheduler.expressions;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringPropertyBase;
import javafx.beans.property.SimpleStringProperty;
import scheduler.dao.AppointmentFactory;

/**
 *
 * @author erwinel
 */
public class AppointmentTypeProperty extends SimpleStringProperty {
    ReadOnlyStringProperty readOnlyProperty;
    
    /**
     * Returns the readonly property, that is synchronized with this {@code RowStateProperty}.
     *
     * @return the readonly property
     */
    public ReadOnlyStringProperty getReadOnlyProperty() {
        if (readOnlyProperty == null)
            readOnlyProperty = new AppointmentTypeProperty.ReadOnlyPropertyImpl();
        return readOnlyProperty;
    }

    public AppointmentTypeProperty() {
        super(AppointmentFactory.APPOINTMENTTYPE_OTHER);
    }

    public AppointmentTypeProperty(String initialValue) {
        super(AppointmentFactory.asValidAppointmentType(initialValue));
    }

    public AppointmentTypeProperty(Object bean, String name) {
        super(bean, name, AppointmentFactory.APPOINTMENTTYPE_OTHER);
    }

    public AppointmentTypeProperty(Object bean, String name, String initialValue) {
        super(bean, name, AppointmentFactory.asValidAppointmentType(initialValue));
    }
    
    @Override
    public void set(String newValue) { super.set(AppointmentFactory.asValidAppointmentType(newValue)); }

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
