package scheduler.view.customer;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import scheduler.dao.AddressElement;
import scheduler.dao.CustomerElement;
import scheduler.observables.AddressTextProperty;
import scheduler.observables.ChildPropertyWrapper;
import scheduler.view.address.AddressModel;
import scheduler.view.address.RelatedAddressModel;
import scheduler.view.model.RelatedItemModel;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
 */
public class RelatedCustomerModel extends RelatedItemModel<CustomerElement> implements CustomerModel<CustomerElement> {

    private final ReadOnlyStringWrapper name;
    private final ReadOnlyObjectWrapper<AddressModel<? extends AddressElement>> address;
    private final ChildPropertyWrapper<String, AddressModel<? extends AddressElement>> address1;
    private final ChildPropertyWrapper<String, AddressModel<? extends AddressElement>> address2;
    private final ChildPropertyWrapper<String, AddressModel<? extends AddressElement>> cityName;
    private final ChildPropertyWrapper<String, AddressModel<? extends AddressElement>> countryName;
    private final ChildPropertyWrapper<String, AddressModel<? extends AddressElement>> postalCode;
    private final ChildPropertyWrapper<String, AddressModel<? extends AddressElement>> phone;
    private final ChildPropertyWrapper<String, AddressModel<? extends AddressElement>> cityZipCountry;
    private final AddressTextProperty addressText;
    private final ReadOnlyBooleanWrapper active;

    public RelatedCustomerModel(CustomerElement dao) {
        super(dao);
        name = new ReadOnlyStringWrapper(this, "address1", dao.getName());
        AddressElement a = dao.getAddress();
        address = new ReadOnlyObjectWrapper<>(this, "address", (null == a) ? null : new RelatedAddressModel(a));
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
    public AddressModel<? extends AddressElement> getAddress() {
        return address.get();
    }

    @Override
    public ReadOnlyProperty<AddressModel<? extends AddressElement>> addressProperty() {
        return address.getReadOnlyProperty();
    }

    @Override
    public String getAddress1() {
        return address1.get();
    }

    @Override
    public ChildPropertyWrapper<String, AddressModel<? extends AddressElement>> address1Property() {
        return address1;
    }

    @Override
    public String getAddress2() {
        return address2.get();
    }

    @Override
    public ChildPropertyWrapper<String, AddressModel<? extends AddressElement>> address2Property() {
        return address2;
    }

    @Override
    public String getCityName() {
        return cityName.get();
    }

    @Override
    public ChildPropertyWrapper<String, AddressModel<? extends AddressElement>> cityNameProperty() {
        return cityName;
    }

    @Override
    public String getCountryName() {
        return countryName.get();
    }

    @Override
    public ChildPropertyWrapper<String, AddressModel<? extends AddressElement>> countryNameProperty() {
        return countryName;
    }

    @Override
    public String getPostalCode() {
        return postalCode.get();
    }

    @Override
    public ChildPropertyWrapper<String, AddressModel<? extends AddressElement>> postalCodeProperty() {
        return postalCode;
    }

    @Override
    public String getPhone() {
        return phone.get();
    }

    @Override
    public ChildPropertyWrapper<String, AddressModel<? extends AddressElement>> phoneProperty() {
        return phone;
    }

    @Override
    public String getCityZipCountry() {
        return cityZipCountry.get();
    }

    @Override
    public ChildPropertyWrapper<String, AddressModel<? extends AddressElement>> cityZipCountryProperty() {
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
