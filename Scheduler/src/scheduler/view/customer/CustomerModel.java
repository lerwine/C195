package scheduler.view.customer;

import java.time.ZoneId;
import java.util.Objects;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import scheduler.dao.CustomerDAO;
import scheduler.dao.DataAccessObject.DaoFactory;
import scheduler.dao.DataRowState;
import scheduler.model.ModelHelper;
import scheduler.model.db.AddressRowData;
import scheduler.model.db.CustomerRowData;
import scheduler.model.ui.AddressDbItem;
import scheduler.model.ui.AddressItem;
import scheduler.model.ui.CustomerItem;
import scheduler.model.ui.FxRecordModel;
import scheduler.observables.AddressTextProperty;
import scheduler.observables.NestedStringBindingProperty;
import scheduler.observables.NonNullableStringProperty;
import scheduler.view.address.AddressModel;
import scheduler.view.address.RelatedAddress;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public final class CustomerModel extends FxRecordModel<CustomerDAO> implements CustomerItem<CustomerDAO> {

    private static final Factory FACTORY = new Factory();

    public static ZoneId getZoneId(CustomerItem<? extends CustomerRowData> customer) {
        if (null != customer) {
            return AddressModel.getZoneId(customer.getAddress());
        }
        return ZoneId.systemDefault();
    }

    public static final Factory getFactory() {
        return FACTORY;
    }

    private final NonNullableStringProperty name;
    private final SimpleObjectProperty<AddressItem> address;
    private final NestedStringBindingProperty<AddressItem> address1;
    private final NestedStringBindingProperty<AddressItem> address2;
    private final NestedStringBindingProperty<AddressItem> cityName;
    private final NestedStringBindingProperty<AddressItem> countryName;
    private final NestedStringBindingProperty<AddressItem> postalCode;
    private final NestedStringBindingProperty<AddressItem> phone;
    private final NestedStringBindingProperty<AddressItem> cityZipCountry;
    private final AddressTextProperty addressText;
    private final SimpleBooleanProperty active;
    private final StringBinding multiLineAddress;

    public CustomerModel(CustomerDAO dao) {
        super(dao);
        name = new NonNullableStringProperty(this, "name", dao.getName());
        AddressRowData a = dao.getAddress();
        address = new SimpleObjectProperty<>(this, "address", (null == a) ? null : new RelatedAddress(a));
        address1 = new NestedStringBindingProperty<>(this, "address1", address, (c) -> c.address1Property());
        address2 = new NestedStringBindingProperty<>(this, "address2", address, (c) -> c.address2Property());
        cityName = new NestedStringBindingProperty<>(this, "cityName", address, (c) -> c.cityNameProperty());
        countryName = new NestedStringBindingProperty<>(this, "countryName", address, (c) -> c.countryNameProperty());
        postalCode = new NestedStringBindingProperty<>(this, "postalCode", address, (c) -> c.postalCodeProperty());
        phone = new NestedStringBindingProperty<>(this, "phone", address, (c) -> c.phoneProperty());
        cityZipCountry = new NestedStringBindingProperty<>(this, "cityZipCountry", address, (t) -> t.cityZipCountryProperty());
        addressText = new AddressTextProperty(this, "addressText", this);
        active = new SimpleBooleanProperty(this, "active", dao.isActive());
        multiLineAddress = AddressItem.createMultiLineAddressBinding(address1, address2, cityZipCountry, phone);
    }

    @Override
    public StringBinding getMultiLineAddress() {
        return multiLineAddress;
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
    public AddressItem getAddress() {
        return address.get();
    }

    public void setAddress(AddressItem value) {
        address.set(value);
    }

    @Override
    public ObjectProperty<AddressItem> addressProperty() {
        return address;
    }

    @Override
    public String getAddress1() {
        return address1.get();
    }

    @Override
    public NestedStringBindingProperty<AddressItem> address1Property() {
        return address1;
    }

    @Override
    public String getAddress2() {
        return address2.get();
    }

    @Override
    public NestedStringBindingProperty<AddressItem> address2Property() {
        return address2;
    }

    @Override
    public String getCityName() {
        return cityName.get();
    }

    @Override
    public NestedStringBindingProperty<AddressItem> cityNameProperty() {
        return cityName;
    }

    @Override
    public String getCountryName() {
        return countryName.get();
    }

    @Override
    public NestedStringBindingProperty<AddressItem> countryNameProperty() {
        return countryName;
    }

    @Override
    public String getPostalCode() {
        return postalCode.get();
    }

    @Override
    public NestedStringBindingProperty<AddressItem> postalCodeProperty() {
        return postalCode;
    }

    @Override
    public String getPhone() {
        return phone.get();
    }

    @Override
    public NestedStringBindingProperty<AddressItem> phoneProperty() {
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
    public NestedStringBindingProperty<AddressItem> cityZipCountryProperty() {
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
        if (null != obj && obj instanceof CustomerModel) {
            final CustomerModel other = (CustomerModel) obj;
            if (isNewItem()) {
                return name.isEqualTo(other.name).get() && address.isEqualTo(other.address).get() && active.isEqualTo(other.active).get();
            }
            return !other.isNewItem() && primaryKeyProperty().isEqualTo(other.primaryKeyProperty()).get();
        }
        return false;
    }

    public final static class Factory extends FxRecordModel.ModelFactory<CustomerDAO, CustomerModel> {

        private Factory() {
        }

        @Override
        public DaoFactory<CustomerDAO> getDaoFactory() {
            return CustomerDAO.getFactory();
        }

        @Override
        public CustomerModel createNew(CustomerDAO dao) {
            return new CustomerModel(dao);
        }

        @Override
        public void updateItem(CustomerModel item, CustomerDAO dao) {
            super.updateItem(item, dao);
            item.setName(dao.getName());
            item.setActive(item.isActive());
            AddressRowData addressDAO = dao.getAddress();
            item.setAddress((null == addressDAO) ? null : new RelatedAddress(addressDAO));
        }

        @Override
        public CustomerModelFilter getAllItemsFilter() {
            return CustomerModelFilter.all();
        }

        @Override
        public CustomerModelFilter getDefaultFilter() {
            return CustomerModelFilter.active();
        }

        @Override
        public CustomerDAO updateDAO(CustomerModel item) {
            CustomerDAO dao = item.getDataObject();
            if (dao.getRowState() == DataRowState.DELETED) {
                throw new IllegalArgumentException("Customer has been deleted");
            }
            String name = item.name.get();
            if (name.trim().isEmpty()) {
                throw new IllegalArgumentException("Customer name empty");
            }
            AddressItem addressModel = item.address.get();
            if (null == addressModel) {
                throw new IllegalArgumentException("No associated address");
            }
            AddressRowData addressDAO = (addressModel instanceof AddressDbItem)
                    ? ((AddressDbItem<? extends AddressRowData>) addressModel).getDataObject() : (AddressRowData) addressModel;
            if (ModelHelper.getRowState(addressDAO) == DataRowState.DELETED) {
                throw new IllegalArgumentException("Associated address has been deleted");
            }
            dao.setAddress(addressDAO);
            dao.setName(name);
            dao.setActive(item.isActive());
            return dao;
        }

    }

}
