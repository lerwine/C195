/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.customer;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import scheduler.dao.CustomerImpl;
import view.ModelBase;
import view.address.CustomerAddress;

/**
 *
 * @author erwinel
 */
public class CustomerModel extends ModelBase<CustomerImpl> implements AppointmentCustomer<CustomerImpl> {

    private final ReadOnlyStringWrapper name;

    @Override
    public String getName() { return name.get(); }

    @Override
    public ReadOnlyStringProperty nameProperty() { return name.getReadOnlyProperty(); }
    
    private final ReadOnlyObjectWrapper<CustomerAddress<?>> address;

    @Override
    public CustomerAddress<?> getAddress() { return address.get(); }

    @Override
    public ReadOnlyObjectProperty<CustomerAddress<?>> addressProperty() { return address.getReadOnlyProperty(); }
    
    private final ReadOnlyBooleanWrapper active;

    @Override
    public boolean isActive() { return active.get(); }

    @Override
    public ReadOnlyBooleanProperty activeProperty() { return active.getReadOnlyProperty(); }
    
    public CustomerModel(CustomerImpl dao) {
        super(dao);
        this.name = new ReadOnlyStringWrapper(dao.getName());
        this.address = new ReadOnlyObjectWrapper<>(CustomerAddress.of(dao.getAddress()));
        this.active = new ReadOnlyBooleanWrapper(dao.isActive());
    }
}
