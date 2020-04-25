package scheduler.view.customer;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import scheduler.observables.AddressTextProperty;
import scheduler.observables.ChildPropertyWrapper;
import scheduler.view.address.AddressModel;
import scheduler.view.address.RelatedAddressModel;
import scheduler.view.model.RelatedItemModel;
import scheduler.model.db.AddressRowData;
import scheduler.model.db.CustomerRowData;
import scheduler.model.ui.CustomerItem;
import scheduler.model.ui.AddressDbItem;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class RelatedCustomerModel extends RelatedItemModel<CustomerRowData> implements CustomerItem<CustomerRowData> {

    private final ReadOnlyStringWrapper name;
    private final ReadOnlyObjectWrapper<AddressDbItem<? extends AddressRowData>> address;
    private final ChildPropertyWrapper<String, AddressDbItem<? extends AddressRowData>> address1;
    private final ChildPropertyWrapper<String, AddressDbItem<? extends AddressRowData>> address2;
    private final ChildPropertyWrapper<String, AddressDbItem<? extends AddressRowData>> cityName;
    private final ChildPropertyWrapper<String, AddressDbItem<? extends AddressRowData>> countryName;
    private final ChildPropertyWrapper<String, AddressDbItem<? extends AddressRowData>> postalCode;
    private final ChildPropertyWrapper<String, AddressDbItem<? extends AddressRowData>> phone;
    private final ChildPropertyWrapper<String, AddressDbItem<? extends AddressRowData>> cityZipCountry;
    private final AddressTextProperty addressText;
    private final ReadOnlyBooleanWrapper active;

    public RelatedCustomerModel(CustomerRowData dao) {
        super(dao);
        name = new ReadOnlyStringWrapper(this, "address1", dao.getName());
        AddressRowData a = dao.getAddress();
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
    public AddressDbItem<? extends AddressRowData> getAddress() {
        return address.get();
    }

    @Override
    public ReadOnlyProperty<AddressDbItem<? extends AddressRowData>> addressProperty() {
        return address.getReadOnlyProperty();
    }

    public String getAddress1() {
        return address1.get();
    }

    public ChildPropertyWrapper<String, AddressDbItem<? extends AddressRowData>> address1Property() {
        return address1;
    }

    public String getAddress2() {
        return address2.get();
    }

    public ChildPropertyWrapper<String, AddressDbItem<? extends AddressRowData>> address2Property() {
        return address2;
    }

    public String getCityName() {
        return cityName.get();
    }

    public ChildPropertyWrapper<String, AddressDbItem<? extends AddressRowData>> cityNameProperty() {
        return cityName;
    }

    public String getCountryName() {
        return countryName.get();
    }

    public ChildPropertyWrapper<String, AddressDbItem<? extends AddressRowData>> countryNameProperty() {
        return countryName;
    }

    public String getPostalCode() {
        return postalCode.get();
    }

    public ChildPropertyWrapper<String, AddressDbItem<? extends AddressRowData>> postalCodeProperty() {
        return postalCode;
    }

    public String getPhone() {
        return phone.get();
    }

    public ChildPropertyWrapper<String, AddressDbItem<? extends AddressRowData>> phoneProperty() {
        return phone;
    }

    public String getCityZipCountry() {
        return cityZipCountry.get();
    }

    public ChildPropertyWrapper<String, AddressDbItem<? extends AddressRowData>> cityZipCountryProperty() {
        return cityZipCountry;
    }

    public String getAddressText() {
        return addressText.get();
    }

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
