package scheduler.observables;

import javafx.beans.property.ReadOnlyProperty;
import scheduler.AppResourceKeys;
import scheduler.AppResources;
import scheduler.model.AppointmentType;
import scheduler.model.db.CustomerRowData;
import scheduler.model.predefined.PredefinedAddress;
import scheduler.model.predefined.PredefinedData;
import scheduler.model.ui.CustomerItem;
import scheduler.view.appointment.AppointmentModel;

/**
 * Binds to the properties of a {@link AppointmentModel} to create a string binding property representing the effective location.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class EffectiveLocationProperty extends StringBindingProperty {

    private final ReadOnlyProperty<? extends CustomerItem<? extends CustomerRowData>> customer;
    private final ReadOnlyProperty<AppointmentType> type;
    private final ReadOnlyProperty<String> location;
    private final ReadOnlyProperty<String> url;

    public EffectiveLocationProperty(Object bean, String name, AppointmentModel source) {
        super(bean, name);
        customer = source.customerProperty();
        type = source.typeProperty();
        location = source.locationProperty();
        url = source.urlProperty();
        super.addDependency(customer, type, location, url);
    }

    @Override
    protected String computeValue() {
        String l = location.getValue();
        String u = url.getValue();
        CustomerItem<? extends CustomerRowData> c = customer.getValue();
        switch (type.getValue()) {
            case CORPORATE_LOCATION:
                PredefinedAddress a = PredefinedData.lookupAddress(l);
                return (null == a) ? AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_APPOINTMENTTYPE_CORPORATE) : AddressTextProperty.convertToString(a);
            case CUSTOMER_SITE:
                if (null != c) {
                    return c.addressTextProperty().getValue();
                }
                return "";
            case PHONE:
                return (u.startsWith("tel:")) ? u.substring(4) : u;
            case VIRTUAL:
                return u;
            default:
                return l;
        }
    }

}
