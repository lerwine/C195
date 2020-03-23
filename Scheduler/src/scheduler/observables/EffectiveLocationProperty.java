package scheduler.observables;

import javafx.beans.binding.StringBinding;
import javafx.beans.property.ReadOnlyProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import scheduler.AppResources;
import scheduler.dao.Appointment;
import scheduler.dao.AppointmentType;
import scheduler.dao.Customer;
import scheduler.view.appointment.AppointmentReferenceModel;
import scheduler.view.customer.CustomerReferenceModel;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
 */
public class EffectiveLocationProperty extends StringBinding implements ReadOnlyProperty<String> {

    private final Object bean;
    private final String name;
    private final ReadOnlyProperty<CustomerReferenceModel<? extends Customer>> customer;
    private final ReadOnlyProperty<AppointmentType> type;
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
        //CustomerReferenceModel<? extends Customer> c = customer.getValue();
        switch (type.getValue()) {
            case GERMANY_SITE_MEETING:
                return AppResources.getResourceString(AppResources.RESOURCEKEY_APPOINTMENTTYPE_GERMANY);
            case HONDURAS_SITE_MEETING:
                return AppResources.getResourceString(AppResources.RESOURCEKEY_APPOINTMENTTYPE_HONDURAS);
            case CORPORATE_HQ_MEETING:
                return AppResources.getResourceString(AppResources.RESOURCEKEY_APPOINTMENTTYPE_HOME);
            case INDIA_SITE_MEETING:
                return AppResources.getResourceString(AppResources.RESOURCEKEY_APPOINTMENTTYPE_INDIA);
            case CUSTOMER_SITE:
            case PHONE:
                return (u.startsWith("tel:")) ? u.substring(4) : u;
            case VIRTUAL:
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
