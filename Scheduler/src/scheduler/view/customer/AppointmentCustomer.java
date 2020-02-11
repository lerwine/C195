/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.view.customer;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import scheduler.dao.Address;
import scheduler.dao.Customer;
import scheduler.dao.CustomerFactory;
import scheduler.view.address.CustomerAddress;
import scheduler.view.ChildModel;

/**
 * Defines a {@link Customer} that is associated with an {@link view.appointment.AppointmentModel}.
 * @author erwinel
 * @param <T> The type of {@link Customer} object.
 */
public interface AppointmentCustomer<T extends Customer> extends ChildModel<T> {
    String getName();
    ReadOnlyStringProperty nameProperty();
    CustomerAddress<?> getAddress();
    ReadOnlyObjectProperty<CustomerAddress<?>> addressProperty();
    boolean isActive();
    ReadOnlyBooleanProperty activeProperty();
    
    public static AppointmentCustomer<?> of(Customer dao) {
        if (null == dao)
            return null;
        if (dao instanceof CustomerFactory.CustomerImpl)
            return new CustomerModel((CustomerFactory.CustomerImpl)dao);
        return new AppointmentCustomer<Customer>() {
            private final ReadOnlyStringWrapper name = new ReadOnlyStringWrapper(dao.getName());
            @Override
            public String getName() { return name.get(); }
            @Override
            public ReadOnlyStringProperty nameProperty() { return name.getReadOnlyProperty(); }
            private final ReadOnlyObjectWrapper<CustomerAddress<? extends Address>> address = new ReadOnlyObjectWrapper<>(CustomerAddress.of(dao.getAddress()));
            @Override
            public CustomerAddress<? extends Address> getAddress() { return address.get(); }
            @Override
            public ReadOnlyObjectProperty<CustomerAddress<? extends Address>> addressProperty() { return address.getReadOnlyProperty(); }
            private final ReadOnlyBooleanWrapper active = new ReadOnlyBooleanWrapper(dao.isActive());
            @Override
            public boolean isActive() { return active.get(); }
            @Override
            public ReadOnlyBooleanProperty activeProperty() { return active.getReadOnlyProperty(); }
            @Override
            public Customer getDataObject() { return dao; }
        };
    }
}
