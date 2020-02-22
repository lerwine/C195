package scheduler.observables;

import javafx.beans.binding.StringBinding;
import javafx.beans.property.ReadOnlyProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import scheduler.App;
import scheduler.dao.Appointment;
import scheduler.dao.Customer;
import scheduler.util.Values;
import scheduler.view.appointment.AppointmentReferenceModel;
import scheduler.view.customer.CustomerReferenceModel;

/**
 *
 * @author lerwi
 */
public class EffectiveLocationProperty extends StringBinding implements ReadOnlyProperty<String> {

    private final Object bean;
    private final String name;
    private final ReadOnlyProperty<CustomerReferenceModel<? extends Customer>> customer;
    private final ReadOnlyProperty<String> type;
    private final ReadOnlyProperty<String> location;
    private final ReadOnlyProperty<String> url;

    public EffectiveLocationProperty(Object bean, String name, AppointmentReferenceModel<? extends Appointment> source) {
        customer = source.customerProperty();
        type = source.typeProperty();
        location = source.locationProperty();
        url = source.urlProperty();
        this.bean = bean;
        this.name = (null == name) ? "" : name;
        super.bind(customer, type, location, url);
    }

    @Override
    protected String computeValue() {
        String l = location.getValue();
        String u = url.getValue();
        CustomerReferenceModel<? extends Customer> c = customer.getValue();
        switch (type.getValue()) {
            case Values.APPOINTMENTTYPE_GERMANY:
                return App.getResourceString(App.RESOURCEKEY_APPOINTMENTTYPE_GERMANY);
            case Values.APPOINTMENTTYPE_HONDURAS:
                return App.getResourceString(App.RESOURCEKEY_APPOINTMENTTYPE_HONDURAS);
            case Values.APPOINTMENTTYPE_HOME:
                return App.getResourceString(App.RESOURCEKEY_APPOINTMENTTYPE_HOME);
            case Values.APPOINTMENTTYPE_INDIA:
                return App.getResourceString(App.RESOURCEKEY_APPOINTMENTTYPE_INDIA);
            case Values.APPOINTMENTTYPE_CUSTOMER:
            case Values.APPOINTMENTTYPE_PHONE:
                return (u.startsWith("tel:")) ? u.substring(4) : u;
            case Values.APPOINTMENTTYPE_VIRTUAL:
                return u;
        }
        return l;
    }

    @Override
    public Object getBean() {
        return bean;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ObservableList<?> getDependencies() {
        return FXCollections.observableArrayList(type, location, url, customer);
    }

    @Override
    public void dispose() {
        super.unbind(type, location, url, customer);
        super.dispose();
    }

}
