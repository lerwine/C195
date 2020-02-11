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
import scheduler.dao.CustomerFactory;
import scheduler.view.ItemModel;
import scheduler.view.address.CustomerAddress;

/**
 *
 * @author erwinel
 */
public class CustomerModel extends ItemModel<CustomerFactory.CustomerImpl> implements AppointmentCustomer<CustomerFactory.CustomerImpl> {

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
    
    public CustomerModel(CustomerFactory.CustomerImpl dao) {
        super(dao);
        name = new ReadOnlyStringWrapper(dao.getName());
        address = new ReadOnlyObjectWrapper<>(CustomerAddress.of(dao.getAddress()));
        active = new ReadOnlyBooleanWrapper(dao.isActive());
    }

    @Override
    protected void refreshFromDAO(CustomerFactory.CustomerImpl dao) {
        name.set(dao.getName());
        address.set(CustomerAddress.of(dao.getAddress()));
        active.set(dao.isActive());
    }
    
}
