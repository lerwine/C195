/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.view.appointment;

import java.time.LocalDateTime;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import scheduler.App;
import scheduler.dao.AppointmentFactory;
import scheduler.util.DB;
import scheduler.util.Values;
import scheduler.view.ItemModel;
import scheduler.view.address.CustomerAddress;
import scheduler.view.customer.AppointmentCustomer;
import scheduler.view.user.AppointmentUser;

/**
 * List item model for {@link AppointmentFactory.AppointmentImpl} data access objects.
 * @author erwinel
 */
public final class AppointmentModel extends ItemModel<AppointmentFactory.AppointmentImpl> {

    private final ReadOnlyObjectWrapper<AppointmentCustomer<?>> customer;

    public AppointmentCustomer<?> getCustomer() { return customer.get(); }

    public ReadOnlyObjectProperty<AppointmentCustomer<?>> customerProperty() { return customer.getReadOnlyProperty(); }

    private final ReadOnlyObjectWrapper<AppointmentUser<?>> user;

    public AppointmentUser<?> getUser() { return user.get(); }

    public ReadOnlyObjectProperty<AppointmentUser<?>> userProperty() { return user.getReadOnlyProperty(); }

    private final ReadOnlyStringWrapper title;
    
    public String getTitle() { return title.get(); }

    public ReadOnlyStringProperty titleProperty() { return title.getReadOnlyProperty(); }

    private final ReadOnlyStringWrapper description;

    public String getDescription() { return description.get(); }

    public ReadOnlyStringProperty descriptionProperty() { return description.getReadOnlyProperty(); }

    private final ReadOnlyStringWrapper location;

    public String getLocation() { return location.get(); }

    public ReadOnlyStringProperty locationProperty() { return location.getReadOnlyProperty(); }

    private final ReadOnlyStringWrapper effectiveLocation;

    public String getEffectiveLocation() { return effectiveLocation.get(); }

    public ReadOnlyStringProperty effectiveLocationProperty() { return effectiveLocation.getReadOnlyProperty(); }

    private final ReadOnlyStringWrapper contact;

    public String getContact() { return contact.get(); }

    public ReadOnlyStringProperty contactProperty() { return contact.getReadOnlyProperty(); }

    private final ReadOnlyStringWrapper type;

    public String getType() { return type.get(); }

    public ReadOnlyStringProperty typeProperty() { return type.getReadOnlyProperty(); }

    private final ReadOnlyStringWrapper url;

    public String getUrl() { return url.get(); }

    public ReadOnlyStringProperty urlProperty() { return url.getReadOnlyProperty(); }

    private final ReadOnlyObjectWrapper<LocalDateTime> start;

    public LocalDateTime getStart() { return start.get(); }

    public ReadOnlyObjectProperty<LocalDateTime> startProperty() { return start.getReadOnlyProperty(); }

    private final ReadOnlyObjectWrapper<LocalDateTime> end;

    public LocalDateTime getEnd() { return end.get(); }

    public ReadOnlyObjectProperty<LocalDateTime> endProperty() {
        return end.getReadOnlyProperty();
    }

    public AppointmentModel(AppointmentFactory.AppointmentImpl dao) {
        super(dao);
        customer = new ReadOnlyObjectWrapper<>(AppointmentCustomer.of(dao.getCustomer()));
        user = new ReadOnlyObjectWrapper<>(AppointmentUser.of(dao.getUser()));
        title = new ReadOnlyStringWrapper(dao.getTitle());
        description = new ReadOnlyStringWrapper(dao.getDescription());
        location = new ReadOnlyStringWrapper(dao.getLocation());
        contact = new ReadOnlyStringWrapper(dao.getContact());
        type = new ReadOnlyStringWrapper(dao.getType());
        url = new ReadOnlyStringWrapper(dao.getUrl());
        start = new ReadOnlyObjectWrapper<>(DB.fromUtcTimestamp(dao.getStart()));
        end = new ReadOnlyObjectWrapper<>(DB.fromUtcTimestamp(dao.getEnd()));
        effectiveLocation = new ReadOnlyStringWrapper();
        setEffectiveLocation();
    }

    private void setEffectiveLocation() {
        String s;
        switch (type.get()) {
            case Values.APPOINTMENTTYPE_CUSTOMER:
                AppointmentCustomer c = customer.get();
                if (null == c)
                    effectiveLocation.set("");
                else {
                    CustomerAddress a = c.getAddress();
                    if (null == a)
                        effectiveLocation.set("");
                    else {
                        StringBuilder sb = new StringBuilder();
                        s = Values.asNonNullAndWsNormalized(a.getAddress1());
                        if (!s.isEmpty())
                            sb.append(s);
                        s = Values.asNonNullAndWsNormalized(a.getAddress2());
                        if (!s.isEmpty()) {
                            if (sb.length() > 0)
                                sb.append("\n");
                            sb.append(s);
                        }
                        s = Values.asNonNullAndWsNormalized(a.getCityName());
                        if (!s.isEmpty()) {
                            if (sb.length() > 0)
                                sb.append("\n");
                            sb.append(s);
                            s = Values.asNonNullAndWsNormalized(a.getPostalCode());
                            if (!s.isEmpty())
                                sb.append(" ").append(s);
                        } else {
                            if (sb.length() > 0)
                                sb.append("\n");
                            sb.append(s);
                        }
                        s = Values.asNonNullAndWsNormalized(a.getCountryName());
                        if (!s.isEmpty()) {
                            if (sb.length() > 0)
                                sb.append("\n");
                            sb.append(s);
                        }
                        s = Values.asNonNullAndWsNormalized(a.getPhone());
                        if (!s.isEmpty()) {
                            if (sb.length() > 0)
                                sb.append("\n");
                            sb.append(s);
                        }
                        
                        effectiveLocation.set(sb.toString());
                    }
                }
                break;
            case Values.APPOINTMENTTYPE_VIRTUAL:
                effectiveLocation.set(getUrl());
                break;
            case Values.APPOINTMENTTYPE_PHONE:
                s = getUrl();
                effectiveLocation.set((s.startsWith("tel:")) ? s.substring(4) : s);
                break;
            case Values.APPOINTMENTTYPE_GERMANY:
                effectiveLocation.set(App.getResourceString(App.RESOURCEKEY_APPOINTMENTTYPE_GERMANY));
                break;
            case Values.APPOINTMENTTYPE_HONDURAS:
                effectiveLocation.set(App.getResourceString(App.RESOURCEKEY_APPOINTMENTTYPE_HONDURAS));
                break;
            case Values.APPOINTMENTTYPE_INDIA:
                effectiveLocation.set(App.getResourceString(App.RESOURCEKEY_APPOINTMENTTYPE_INDIA));
                break;
            case Values.APPOINTMENTTYPE_HOME:
                effectiveLocation.set(App.getResourceString(App.RESOURCEKEY_APPOINTMENTTYPE_HOME));
                break;
            default:
                effectiveLocation.set(getLocation());
        }
    }

    @Override
    protected void refreshFromDAO(AppointmentFactory.AppointmentImpl dao) {
        customer.set(AppointmentCustomer.of(dao.getCustomer()));
        user.set(AppointmentUser.of(dao.getUser()));
        title.set(dao.getTitle());
        description.set(dao.getDescription());
        location.set(dao.getLocation());
        contact.set(dao.getContact());
        type.set(dao.getType());
        url.set(dao.getUrl());
        start.set(DB.fromUtcTimestamp(dao.getStart()));
        end.set(DB.fromUtcTimestamp(dao.getEnd()));
        setEffectiveLocation();
    }
    
}
