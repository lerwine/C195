/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.view.address;

import java.sql.Connection;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import scheduler.dao.AddressFactory;
import scheduler.view.city.AddressCity;

/**
 *
 * @author erwinel
 */
public class AddressModel extends scheduler.view.ItemModel<AddressFactory.AddressImpl> implements CustomerAddress<AddressFactory.AddressImpl> {

    private final ReadOnlyStringWrapper address1;

    @Override
    public ReadOnlyStringProperty address1Property() { return address1.getReadOnlyProperty(); }

    @Override
    public String getAddress1() { return address1.get(); }
    
    private final ReadOnlyStringWrapper address2;

    @Override
    public String getAddress2() { return address2.get(); }

    @Override
    public ReadOnlyStringProperty address2Property() { return address2.getReadOnlyProperty(); }
    
    private final ReadOnlyObjectWrapper<AddressCity<?>> city;

    @Override
    public AddressCity<?> getCity() { return city.get(); }

    @Override
    public ReadOnlyObjectProperty<AddressCity<?>> cityProperty() { return city.getReadOnlyProperty(); }
    
    @Override
    public String getCityName() { return city.get().getName(); }

    @Override
    public ReadOnlyStringProperty cityNameProperty() { return city.get().nameProperty(); }
    
    @Override
    public String getCountryName() { return city.get().getCountry().getName(); }

    @Override
    public ReadOnlyStringProperty countryNameProperty() { return city.get().getCountry().nameProperty(); }
    
    private final ReadOnlyStringWrapper postalCode;

    @Override
    public String getPostalCode() { return postalCode.get(); }

    @Override
    public ReadOnlyStringProperty postalCodeProperty() { return postalCode.getReadOnlyProperty(); }
    
    private final ReadOnlyStringWrapper phone;

    @Override
    public String getPhone() { return phone.get(); }

    @Override
    public ReadOnlyStringProperty phoneProperty() { return phone.getReadOnlyProperty(); }

    public AddressModel(AddressFactory.AddressImpl dao) {
        super(dao);
        address1 = new ReadOnlyStringWrapper(dao.getAddress1());
        address2 = new ReadOnlyStringWrapper(dao.getAddress2());
        city = new ReadOnlyObjectWrapper<>(AddressCity.of(dao.getCity()));
        postalCode = new ReadOnlyStringWrapper(dao.getPostalCode());
        phone = new ReadOnlyStringWrapper(dao.getPhone());
    }

    @Override
    protected void refreshFromDAO(AddressFactory.AddressImpl dao) {
        address1.set(dao.getAddress1());
        address2.set(dao.getAddress2());
        city.set(AddressCity.of(dao.getCity()));
        postalCode.set(dao.getPostalCode());
        phone.set(dao.getPhone());
    }
    
}
