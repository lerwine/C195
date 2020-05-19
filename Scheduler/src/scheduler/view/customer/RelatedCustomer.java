package scheduler.view.customer;

import javafx.beans.binding.StringBinding;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import scheduler.dao.CustomerDAO;
import scheduler.dao.IAddressDAO;
import scheduler.dao.ICustomerDAO;
import scheduler.model.RelatedModel;
import scheduler.model.ui.AddressItem;
import scheduler.model.ui.CustomerItem;
import scheduler.model.ui.RelatedAddress;
import scheduler.observables.AddressTextProperty;
import scheduler.observables.NestedStringProperty;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class RelatedCustomer extends RelatedModel<ICustomerDAO> implements CustomerItem<ICustomerDAO> {

    private final ReadOnlyStringWrapper name;
    private final ReadOnlyObjectWrapper<RelatedAddress> address;
    private final NestedStringProperty<RelatedAddress> address1;
    private final NestedStringProperty<RelatedAddress> address2;
    private final NestedStringProperty<RelatedAddress> cityName;
    private final NestedStringProperty<RelatedAddress> countryName;
    private final NestedStringProperty<RelatedAddress> postalCode;
    private final NestedStringProperty<RelatedAddress> phone;
    private final NestedStringProperty<RelatedAddress> cityZipCountry;
    private final AddressTextProperty addressText;
    private final ReadOnlyBooleanWrapper active;
    private final StringBinding multiLineAddress;

    public RelatedCustomer(ICustomerDAO rowData) {
        super(rowData);
        name = new ReadOnlyStringWrapper(this, "name", rowData.getName());
        IAddressDAO a = rowData.getAddress();
        address = new ReadOnlyObjectWrapper<>((null == a) ? null : new RelatedAddress(a));
        address1 = new NestedStringProperty<>(this, "address1", address, (c) -> c.address1Property());
        address2 = new NestedStringProperty<>(this, "address2", address, (c) -> c.address2Property());
        cityName = new NestedStringProperty<>(this, "cityName", address, (c) -> c.cityNameProperty());
        countryName = new NestedStringProperty<>(this, "countryName", address, (c) -> c.countryNameProperty());
        postalCode = new NestedStringProperty<>(this, "postalCode", address, (c) -> c.postalCodeProperty());
        phone = new NestedStringProperty<>(this, "phone", address, (c) -> c.phoneProperty());
        cityZipCountry = new NestedStringProperty<>(this, "cityZipCountry", address, (t) -> t.cityZipCountryProperty());
        addressText = new AddressTextProperty(this, "addressText", this);
        active = new ReadOnlyBooleanWrapper(this, "active", rowData.isActive());
        multiLineAddress = AddressItem.createMultiLineAddressBinding(address1, address2, cityZipCountry, phone);
    }

    @Override
    protected void onDataObjectPropertyChanged(ICustomerDAO dao, String propertyName) {
        switch (propertyName) {
            case CustomerDAO.PROP_ACTIVE:
                active.set(dao.isActive());
                break;
            case CustomerDAO.PROP_ADDRESS:
                IAddressDAO a = dao.getAddress();
                address.set((null == a) ? null : new RelatedAddress(a));
                break;
            case CustomerDAO.PROP_NAME:
                name.set(dao.getName());
                break;
        }
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
    public RelatedAddress getAddress() {
        return address.get();
    }

    @Override
    public ReadOnlyObjectProperty<RelatedAddress> addressProperty() {
        return address.getReadOnlyProperty();
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
    public NestedStringProperty<RelatedAddress> address1Property() {
        return address1;
    }

    @Override
    public String getAddress2() {
        return address2.get();
    }

    @Override
    public NestedStringProperty<RelatedAddress> address2Property() {
        return address2;
    }

    @Override
    public String getCityName() {
        return cityName.get();
    }

    @Override
    public NestedStringProperty<RelatedAddress> cityNameProperty() {
        return cityName;
    }

    @Override
    public String getCityZipCountry() {
        return cityZipCountry.get();
    }

    @Override
    public NestedStringProperty<RelatedAddress> cityZipCountryProperty() {
        return cityZipCountry;
    }

    @Override
    public String getCountryName() {
        return countryName.get();
    }

    @Override
    public NestedStringProperty<RelatedAddress> countryNameProperty() {
        return countryName;
    }

    @Override
    public StringBinding getMultiLineAddress() {
        return multiLineAddress;
    }

    @Override
    public String getPhone() {
        return phone.get();
    }

    @Override
    public NestedStringProperty<RelatedAddress> phoneProperty() {
        return phone;
    }

    @Override
    public String getPostalCode() {
        return postalCode.get();
    }

    @Override
    public NestedStringProperty<RelatedAddress> postalCodeProperty() {
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

}
