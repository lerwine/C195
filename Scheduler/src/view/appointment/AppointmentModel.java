/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.appointment;

import java.net.URL;
import java.time.LocalDateTime;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import scheduler.dao.AppointmentImpl;
import util.DB;
import view.ModelBase;
import view.customer.AppointmentCustomer;
import view.user.AppointmentUser;

/**
 *
 * @author erwinel
 */
public class AppointmentModel extends ModelBase<AppointmentImpl> {

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

    public LocalDateTime getEnd() {
        return end.get();
    }

    public ReadOnlyObjectProperty<LocalDateTime> endProperty() {
        return end.getReadOnlyProperty();
    }

    public AppointmentModel(AppointmentImpl dao) {
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
    }

}
