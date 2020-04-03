package scheduler.view.customer;

import java.util.Objects;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import scheduler.dao.AddressElement;
import scheduler.dao.CustomerDAO;
import scheduler.dao.DataAccessObject.DaoFactory;
import scheduler.observables.AddressTextProperty;
import scheduler.observables.ChildPropertyWrapper;
import scheduler.observables.NonNullableStringProperty;
import scheduler.view.address.AddressModel;
import scheduler.view.address.RelatedAddressModel;
import scheduler.view.model.ItemModel;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
 */
public final class CustomerModelImpl extends ItemModel<CustomerDAO> implements CustomerModel<CustomerDAO> {

    private static final Factory FACTORY = new Factory();

    public static final Factory getFactory() {
        return FACTORY;
    }

    private final NonNullableStringProperty name;
    private final SimpleObjectProperty<AddressModel<? extends AddressElement>> address;
    private final ChildPropertyWrapper<String, AddressModel<? extends AddressElement>> address1;
    private final ChildPropertyWrapper<String, AddressModel<? extends AddressElement>> address2;
    private final ChildPropertyWrapper<String, AddressModel<? extends AddressElement>> cityName;
    private final ChildPropertyWrapper<String, AddressModel<? extends AddressElement>> countryName;
    private final ChildPropertyWrapper<String, AddressModel<? extends AddressElement>> postalCode;
    private final ChildPropertyWrapper<String, AddressModel<? extends AddressElement>> phone;
    private final ChildPropertyWrapper<String, AddressModel<? extends AddressElement>> cityZipCountry;
    private final AddressTextProperty addressText;
    private final SimpleBooleanProperty active;

    public CustomerModelImpl(CustomerDAO dao) {
        super(dao);
        name = new NonNullableStringProperty(this, "name", dao.getName());
        AddressElement a = dao.getAddress();
        address = new SimpleObjectProperty<>(this, "address", (null == a) ? null : new RelatedAddressModel(a));
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
    public String getName() {
        return name.get();
    }

    public void setName(String value) {
        name.set(value);
    }

    @Override
    public StringProperty nameProperty() {
        return name;
    }

    @Override
    public AddressModel<? extends AddressElement> getAddress() {
        return address.get();
    }

    public void setAddress(AddressModel<? extends AddressElement> value) {
        address.set(value);
    }

    @Override
    public ObjectProperty<AddressModel<? extends AddressElement>> addressProperty() {
        return address;
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
    public boolean isActive() {
        return active.get();
    }

    public void setActive(boolean value) {
        active.set(value);
    }

    @Override
    public BooleanProperty activeProperty() {
        return active;
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
    public String toString() {
        return name.get();
    }

    @Override
    public int hashCode() {
        if (isNewItem()) {
            int hash = 5;
            hash = 73 * hash + Objects.hashCode(name.get());
            hash = 73 * hash + Objects.hashCode(address.get());
            hash = 73 * hash + Objects.hashCode(active.get());
            return hash;
        }
        return getPrimaryKey();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (null != obj && obj instanceof CustomerModelImpl) {
            final CustomerModelImpl other = (CustomerModelImpl) obj;
            if (isNewItem()) {
                return name.isEqualTo(other.name).get() && address.isEqualTo(other.address).get() && active.isEqualTo(other.active).get();
            }
            return !other.isNewItem() && primaryKeyProperty().isEqualTo(other.primaryKeyProperty()).get();
        }
        return false;
    }

    public final static class Factory extends ItemModel.ModelFactory<CustomerDAO, CustomerModelImpl> {

        private Factory() {
        }

        @Override
        public DaoFactory<CustomerDAO> getDaoFactory() {
            return CustomerDAO.getFactory();
        }

        @Override
        public CustomerModelImpl createNew(CustomerDAO dao) {
            return new CustomerModelImpl(dao);
        }

        @Override
        public void updateItem(CustomerModelImpl item, CustomerDAO dao) {
            super.updateItem(item, dao);
            // TODO: Implement updateItem(CustomerModelImpl item, CustomerDAO dao)
        }

        public CustomerModelFilter getAllItemsFilter() {
            throw new UnsupportedOperationException("Not supported yet.");
            // TODO: Implement getAllItemsFilter()
        }

        @Override
        public CustomerDAO applyChanges(CustomerModelImpl item) {
            throw new UnsupportedOperationException("Not supported yet.");
            // TODO: Implement applyChanges(CustomerModelImpl item)
        }

    }

}
