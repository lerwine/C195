package scheduler.view.customer;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import scheduler.dao.Address;
import scheduler.dao.Customer;
import scheduler.dao.DataObjectImpl;
import scheduler.observables.AddressTextProperty;
import scheduler.observables.ChildPropertyWrapper;
import scheduler.view.address.AddressReferenceModel;
import scheduler.view.address.AddressReferenceModelImpl;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
 */
public class CustomerReferenceModelImpl extends DataObjectImpl.DataObjectReferenceModelImpl<Customer> implements CustomerReferenceModel<Customer> {

    private final ReadOnlyStringWrapper name;
    private final ReadOnlyObjectWrapper<AddressReferenceModel<? extends Address>> address;
    private final ChildPropertyWrapper<String, AddressReferenceModel<? extends Address>> address1;
    private final ChildPropertyWrapper<String, AddressReferenceModel<? extends Address>> address2;
    private final ChildPropertyWrapper<String, AddressReferenceModel<? extends Address>> cityName;
    private final ChildPropertyWrapper<String, AddressReferenceModel<? extends Address>> countryName;
    private final ChildPropertyWrapper<String, AddressReferenceModel<? extends Address>> postalCode;
    private final ChildPropertyWrapper<String, AddressReferenceModel<? extends Address>> phone;
    private final ChildPropertyWrapper<String, AddressReferenceModel<? extends Address>> cityZipCountry;
    private final AddressTextProperty addressText;
    private final ReadOnlyBooleanWrapper active;

    public CustomerReferenceModelImpl(Customer dao) {
        super(dao);
        name = new ReadOnlyStringWrapper(this, "address1", dao.getName());
        Address a = dao.getAddress();
        address = new ReadOnlyObjectWrapper<>(this, "address", (null == a) ? null : new AddressReferenceModelImpl(a));
        address1 = new ChildPropertyWrapper<>(this, "address1", address, (t) -> t.address1Property());
        address2 = new ChildPropertyWrapper<>(this, "address2", address, (t) -> t.address2Property());
        cityName = new ChildPropertyWrapper<>(this, "cityName", address, (t) -> t.cityNameProperty());
        countryName = new ChildPropertyWrapper<>(this, "countryName", address, (t) -> t.countryNameProperty());
        postalCode = new ChildPropertyWrapper<>(this, "postalCode", address, (t) -> t.postalCodeProperty());
        phone = new ChildPropertyWrapper<>(this, "phone", address, (t) -> t.phoneProperty());
        cityZipCountry = new ChildPropertyWrapper<>(this, "cityZipCountry", address, (t) -> t.cityZipCountryProperty());
        addressText = new AddressTextProperty(this, "addressText", this);
        active = new ReadOnlyBooleanWrapper(this, "active", dao.isActive());
    }

    @Override
    public String getName() {
        return name.get();
    }

    @Override
    public ReadOnlyProperty<String> nameProperty() {
        return name.getReadOnlyProperty();
    }

    @Override
    public AddressReferenceModel<? extends Address> getAddress() {
        return address.get();
    }

    @Override
    public ReadOnlyProperty<AddressReferenceModel<? extends Address>> addressProperty() {
        return address.getReadOnlyProperty();
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

    @Override
    public boolean isActive() {
        return active.get();
    }

    @Override
    public ReadOnlyBooleanProperty activeProperty() {
        return active.getReadOnlyProperty();
    }

}
