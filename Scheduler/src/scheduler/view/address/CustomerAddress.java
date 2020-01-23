/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.view.address;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import scheduler.dao.Address;
import scheduler.dao.AddressImpl;
import scheduler.dao.City;
import scheduler.view.city.AddressCity;
import scheduler.view.ChildModel;

/**
 *
 * @author erwinel
 * @param <T>
 */
public interface CustomerAddress<T extends Address> extends ChildModel<T> {
    String getAddress1();
    ReadOnlyStringProperty address1Property();
    String getAddress2();
    ReadOnlyStringProperty address2Property();
    AddressCity<?> getCity();
    ReadOnlyObjectProperty<AddressCity<?>> cityProperty();
    String getCityName();
    ReadOnlyStringProperty cityNameProperty();
    String getCountryName();
    ReadOnlyStringProperty countryNameProperty();
    String getPostalCode();
    ReadOnlyStringProperty postalCodeProperty();
    String getPhone();
    ReadOnlyStringProperty phoneProperty();

    public static CustomerAddress<?> of(Address dao) {
        if (null == dao)
            return null;
        if (dao instanceof AddressImpl)
            return new AddressModel((AddressImpl)dao);
        return new CustomerAddress<Address>() {
            
            private final ReadOnlyStringWrapper address1 = new ReadOnlyStringWrapper(dao.getAddress1());

            @Override
            public ReadOnlyStringProperty address1Property() { return address1.getReadOnlyProperty(); }

            @Override
            public String getAddress1() { return address1.get(); }

            private final ReadOnlyStringWrapper address2 = new ReadOnlyStringWrapper(dao.getAddress2());

            @Override
            public String getAddress2() { return address2.get(); }

            @Override
            public ReadOnlyStringProperty address2Property() { return address2.getReadOnlyProperty(); }

            private final ReadOnlyObjectWrapper<AddressCity<? extends City>> city = new ReadOnlyObjectWrapper<>(AddressCity.of(dao.getCity()));

            @Override
            public AddressCity<? extends City> getCity() { return city.get(); }

            @Override
            public ReadOnlyObjectProperty<AddressCity<? extends City>> cityProperty() { return city.getReadOnlyProperty(); }

            @Override
            public String getCityName() { return city.get().getName(); }

            @Override
            public ReadOnlyStringProperty cityNameProperty() { return city.get().nameProperty(); }

            @Override
            public String getCountryName() { return city.get().getCountry().getName(); }

            @Override
            public ReadOnlyStringProperty countryNameProperty() { return city.get().getCountry().nameProperty(); }

            private final ReadOnlyStringWrapper postalCode = new ReadOnlyStringWrapper(dao.getPostalCode());

            @Override
            public String getPostalCode() { return postalCode.get(); }

            @Override
            public ReadOnlyStringProperty postalCodeProperty() { return postalCode.getReadOnlyProperty(); }

            private final ReadOnlyStringWrapper phone = new ReadOnlyStringWrapper(dao.getPhone());

            @Override
            public String getPhone() { return phone.get(); }

            @Override
            public ReadOnlyStringProperty phoneProperty() { return phone.getReadOnlyProperty(); }

            @Override
            public Address getDataObject() { return dao; }

        };
    }
}
