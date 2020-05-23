package scheduler.model.ui;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import scheduler.dao.IAddressDAO;
import scheduler.dao.ICustomerDAO;
import scheduler.observables.AddressTextProperty;
import scheduler.observables.NestedStringProperty;
import scheduler.observables.ObservableStringDerivitive;
import scheduler.observables.WrappedStringObservableProperty;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class RelatedCustomer extends RelatedModel<ICustomerDAO> implements CustomerItem<ICustomerDAO> {

    private final ReadOnlyStringWrapper name;
    private final ReadOnlyObjectProperty<AddressItem<? extends IAddressDAO>> address;
    private final NestedStringProperty<AddressItem<? extends IAddressDAO>> address1;
    private final NestedStringProperty<AddressItem<? extends IAddressDAO>> address2;
    private final NestedStringProperty<AddressItem<? extends IAddressDAO>> cityName;
    private final NestedStringProperty<AddressItem<? extends IAddressDAO>> countryName;
    private final NestedStringProperty<AddressItem<? extends IAddressDAO>> postalCode;
    private final NestedStringProperty<AddressItem<? extends IAddressDAO>> phone;
    private final NestedStringProperty<AddressItem<? extends IAddressDAO>> cityZipCountry;
    private final AddressTextProperty addressText;
    private final ReadOnlyBooleanWrapper active;
    private final WrappedStringObservableProperty multiLineAddress;

    public RelatedCustomer(ICustomerDAO rowData) {
        super(rowData);
        name = new ReadOnlyStringWrapper(this, "name", rowData.getName());
        address = createReadOnlyNestedDaoModelProperty("address", (t) -> (null == t) ? null : t.getAddress(), AddressItem::createModel);
        address1 = new NestedStringProperty<>(this, "address1", address, (c) -> c.address1Property());
        address2 = new NestedStringProperty<>(this, "address2", address, (c) -> c.address2Property());
        cityName = new NestedStringProperty<>(this, "cityName", address, (c) -> c.cityNameProperty());
        countryName = new NestedStringProperty<>(this, "countryName", address, (c) -> c.countryNameProperty());
        postalCode = new NestedStringProperty<>(this, "postalCode", address, (c) -> c.postalCodeProperty());
        phone = new NestedStringProperty<>(this, "phone", address, (c) -> c.phoneProperty());
        cityZipCountry = new NestedStringProperty<>(this, "cityZipCountry", address, (t) -> t.cityZipCountryProperty());
        addressText = new AddressTextProperty(this, "addressText", this);
        active = new ReadOnlyBooleanWrapper(this, "active", rowData.isActive());
        multiLineAddress = new WrappedStringObservableProperty(this, "multiLineAddress",
                ObservableStringDerivitive.of(
                        ObservableStringDerivitive.ofNested(address, (t) -> t.addressLinesProperty()),
                        cityZipCountry,
                        phone,
                        AddressModel::calculateMultiLineAddress)
        );
    }

    @Override
    public String getName() {
        return name.get();
    }

    @Override
    public ReadOnlyStringProperty nameProperty() {
        return name.getReadOnlyProperty();
    }

    @Override
    public AddressItem<? extends IAddressDAO> getAddress() {
        return address.get();
    }

    @Override
    public ReadOnlyObjectProperty<AddressItem<? extends IAddressDAO>> addressProperty() {
        return address;
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
    public String getAddress1() {
        return address1.get();
    }

    @Override
    public NestedStringProperty<AddressItem<? extends IAddressDAO>> address1Property() {
        return address1;
    }

    @Override
    public String getAddress2() {
        return address2.get();
    }

    @Override
    public NestedStringProperty<AddressItem<? extends IAddressDAO>> address2Property() {
        return address2;
    }

    @Override
    public String getCityName() {
        return cityName.get();
    }

    @Override
    public NestedStringProperty<AddressItem<? extends IAddressDAO>> cityNameProperty() {
        return cityName;
    }

    @Override
    public String getCityZipCountry() {
        return cityZipCountry.get();
    }

    @Override
    public NestedStringProperty<AddressItem<? extends IAddressDAO>> cityZipCountryProperty() {
        return cityZipCountry;
    }

    @Override
    public String getCountryName() {
        return countryName.get();
    }

    @Override
    public NestedStringProperty<AddressItem<? extends IAddressDAO>> countryNameProperty() {
        return countryName;
    }

    @Override
    public String getPhone() {
        return phone.get();
    }

    @Override
    public NestedStringProperty<AddressItem<? extends IAddressDAO>> phoneProperty() {
        return phone;
    }

    @Override
    public String getPostalCode() {
        return postalCode.get();
    }

    @Override
    public NestedStringProperty<AddressItem<? extends IAddressDAO>> postalCodeProperty() {
        return postalCode;
    }

    @Override
    public boolean isActive() {
        return active.get();
    }

    @Override
    public ReadOnlyBooleanProperty activeProperty() {
        return active.getReadOnlyProperty();
    }

    @Override
    public ReadOnlyProperty<String> getMultiLineAddress() {
        return multiLineAddress;
    }

}
