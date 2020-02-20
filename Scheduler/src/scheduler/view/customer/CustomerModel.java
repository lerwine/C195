/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.view.customer;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import scheduler.dao.Address;
import scheduler.dao.CustomerImpl;
import scheduler.dao.DataObjectImpl.Factory;
import scheduler.observables.AddressTextProperty;
import scheduler.observables.ChildPropertyWrapper;
import scheduler.observables.NonNullableStringProperty;
import scheduler.view.ItemModel;
import scheduler.view.address.AddressReferenceModel;
import scheduler.view.address.AddressReferenceModelImpl;

/**
 *
 * @author erwinel
 */
public final class CustomerModel extends ItemModel<CustomerImpl> implements CustomerReferenceModel<CustomerImpl> {

    private final NonNullableStringProperty name;
    private final SimpleObjectProperty<AddressReferenceModel<? extends Address>> address;
    private final ChildPropertyWrapper<String, AddressReferenceModel<? extends Address>> address1;
    private final ChildPropertyWrapper<String, AddressReferenceModel<? extends Address>> address2;
    private final ChildPropertyWrapper<String, AddressReferenceModel<? extends Address>> cityName;
    private final ChildPropertyWrapper<String, AddressReferenceModel<? extends Address>> countryName;
    private final ChildPropertyWrapper<String, AddressReferenceModel<? extends Address>> postalCode;
    private final ChildPropertyWrapper<String, AddressReferenceModel<? extends Address>> phone;
    private final ChildPropertyWrapper<String, AddressReferenceModel<? extends Address>> cityZipCountry;
    private final AddressTextProperty addressText;
    private final SimpleBooleanProperty active;

    @Override
    public String getName() {
        return name.get();
    }

    public void setName(String value) { name.set(value); }
    
    @Override
    public StringProperty nameProperty() {
        return name;
    }

    @Override
    public AddressReferenceModel<? extends Address> getAddress() {
        return address.get();
    }

    public void setAddress(AddressReferenceModel<? extends Address> value) { address.set(value); }
    
    @Override
    public ObjectProperty<AddressReferenceModel<? extends Address>> addressProperty() {
        return address;
    }

    @Override
    public String getAddress1() {
        return address1.get();
    }

    @Override
    public ChildPropertyWrapper<String, AddressReferenceModel<? extends Address>> address1Property() {
        return address1;
    }

    @Override
    public String getAddress2() {
        return address2.get();
    }

    @Override
    public ChildPropertyWrapper<String, AddressReferenceModel<? extends Address>> address2Property() {
        return address2;
    }

    @Override
    public String getCityName() {
        return cityName.get();
    }

    @Override
    public ChildPropertyWrapper<String, AddressReferenceModel<? extends Address>> cityNameProperty() {
        return cityName;
    }

    @Override
    public String getCountryName() {
        return countryName.get();
    }

    @Override
    public ChildPropertyWrapper<String, AddressReferenceModel<? extends Address>> countryNameProperty() {
        return countryName;
    }

    @Override
    public String getPostalCode() {
        return postalCode.get();
    }

    @Override
    public ChildPropertyWrapper<String, AddressReferenceModel<? extends Address>> postalCodeProperty() {
        return postalCode;
    }

    @Override
    public String getPhone() {
        return phone.get();
    }

    @Override
    public ChildPropertyWrapper<String, AddressReferenceModel<? extends Address>> phoneProperty() {
        return phone;
    }

    @Override
    public boolean isActive() {
        return active.get();
    }

    public void setActive(boolean value) { active.set(value); }
    
    @Override
    public BooleanProperty activeProperty() {
        return active;
    }

    public CustomerModel(CustomerImpl dao) {
        super(dao);
        name = new NonNullableStringProperty(this, "name", dao.getName());
        Address a = dao.getAddress().getPartial();
        address = new SimpleObjectProperty<>(this, "address", (null == a) ? null : new AddressReferenceModelImpl(a));
        address1 = new ChildPropertyWrapper<>(this, "address1", address, (c) -> c.address1Property());
        address2 = new ChildPropertyWrapper<>(this, "address2", address, (c) -> c.address2Property());
        cityName = new ChildPropertyWrapper<>(this, "cityName", address, (c) -> c.cityNameProperty());
        countryName = new ChildPropertyWrapper<>(this, "countryName", address, (c) -> c.countryNameProperty());
        postalCode = new ChildPropertyWrapper<>(this, "postalCode", address, (c) -> c.postalCodeProperty());
        phone = new ChildPropertyWrapper<>(this, "phone", address, (c) -> c.phoneProperty());
        cityZipCountry = new ChildPropertyWrapper<>(this, "cityZipCountry", address, (t) -> t.cityZipCountryProperty());
        addressText = new AddressTextProperty(this, "addressText", this);
        active = new SimpleBooleanProperty(this, "active", dao.isActive());
    }

    @Override
    protected void refreshFromDAO(CustomerImpl dao) {
        name.set(dao.getName());
        Address a = dao.getAddress().getPartial();
        address.set((null == a) ? null : new AddressReferenceModelImpl(a));
        active.set(dao.isActive());
    }

    @Override
    public Factory<CustomerImpl, CustomerModel> getDaoFactory() {
        return CustomerImpl.getFactory();
    }

    @Override
    public String getCityZipCountry() {
        return cityZipCountry.get();
    }

    @Override
    public ChildPropertyWrapper<String, AddressReferenceModel<? extends Address>> cityZipCountryProperty() {
        return cityZipCountry;
    }

    @Override
    public String getAddressText() {
        return addressText.get();
    }

    @Override
    public ReadOnlyProperty<String> addressTextProperty() {
        return addressText;
    }

}
