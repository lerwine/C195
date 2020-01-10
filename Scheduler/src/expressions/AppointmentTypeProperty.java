/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package expressions;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringPropertyBase;
import javafx.beans.property.SimpleStringProperty;
import scheduler.dao.Appointment;

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
        super(Appointment.APPOINTMENTTYPE_OTHER);
    }

    public AppointmentTypeProperty(String initialValue) {
        super(asValidValue(initialValue));
    }

    public AppointmentTypeProperty(Object bean, String name) {
        super(bean, name, Appointment.APPOINTMENTTYPE_OTHER);
    }

    public AppointmentTypeProperty(Object bean, String name, String initialValue) {
        super(bean, name, asValidValue(initialValue));
    }

    public static String asValidValue(String value) {
        if (value != null) {
            if ((value = value.trim()).equalsIgnoreCase(Appointment.APPOINTMENTTYPE_CUSTOMER))
                return Appointment.APPOINTMENTTYPE_CUSTOMER;
            if (value.equalsIgnoreCase(Appointment.APPOINTMENTTYPE_GERMANY))
                return Appointment.APPOINTMENTTYPE_GERMANY;
            if (value.equalsIgnoreCase(Appointment.APPOINTMENTTYPE_HOME))
                return Appointment.APPOINTMENTTYPE_HOME;
            if (value.equalsIgnoreCase(Appointment.APPOINTMENTTYPE_HONDURAS))
                return Appointment.APPOINTMENTTYPE_HONDURAS;
            if (value.equalsIgnoreCase(Appointment.APPOINTMENTTYPE_INDIA))
                return Appointment.APPOINTMENTTYPE_INDIA;
            if (value.equalsIgnoreCase(Appointment.APPOINTMENTTYPE_PHONE))
                return Appointment.APPOINTMENTTYPE_PHONE;
            if (value.equalsIgnoreCase(Appointment.APPOINTMENTTYPE_VIRTUAL))
                return Appointment.APPOINTMENTTYPE_VIRTUAL;
        }
        return Appointment.APPOINTMENTTYPE_OTHER;
    }
    
    @Override
    public void set(String newValue) { super.set(asValidValue(newValue)); }

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
