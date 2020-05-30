package scheduler.model.ui;

import java.util.Objects;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import scheduler.dao.CustomerDAO;
import scheduler.dao.DataAccessObject.DaoFactory;
import scheduler.dao.DataRowState;
import scheduler.dao.IAddressDAO;
import scheduler.observables.NonNullableStringProperty;
import scheduler.observables.property.ReadOnlyBooleanBindingProperty;
import scheduler.observables.property.ReadOnlyStringBindingProperty;
import scheduler.util.Values;
import scheduler.view.customer.CustomerModelFilter;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public final class CustomerModel extends FxRecordModel<CustomerDAO> implements CustomerItem<CustomerDAO> {

    private static final Factory FACTORY = new Factory();

    public static final Factory getFactory() {
        return FACTORY;
    }

    private final NonNullableStringProperty name;
    private final SimpleObjectProperty<AddressItem<? extends IAddressDAO>> address;
    private final ReadOnlyStringBindingProperty address1;
    private final ReadOnlyStringBindingProperty address2;
    private final ReadOnlyStringBindingProperty cityName;
    private final ReadOnlyStringBindingProperty countryName;
    private final ReadOnlyStringBindingProperty postalCode;
    private final ReadOnlyStringBindingProperty phone;
    private final ReadOnlyStringBindingProperty cityZipCountry;
    private final ReadOnlyStringBindingProperty addressText;
    private final SimpleBooleanProperty active;
    private final ReadOnlyStringBindingProperty multiLineAddress;
    private final ReadOnlyBooleanBindingProperty valid;

    public CustomerModel(CustomerDAO dao) {
        super(dao);
        name = new NonNullableStringProperty(this, "name", dao.getName());
        address = new SimpleObjectProperty<>(this, "address", AddressItem.createModel(dao.getAddress()));
        active = new SimpleBooleanProperty(this, "active", dao.isActive());
        address1 = new ReadOnlyStringBindingProperty(this, "address1", Bindings.selectString(address, "address1"));
        address2 = new ReadOnlyStringBindingProperty(this, "address2", Bindings.selectString(address, "address2"));
        cityName = new ReadOnlyStringBindingProperty(this, "cityName", Bindings.selectString(address, "cityName"));
        countryName = new ReadOnlyStringBindingProperty(this, "countryName", Bindings.selectString(address, "countryName"));
        postalCode = new ReadOnlyStringBindingProperty(this, "postalCode", Bindings.selectString(address, "postalCode"));
        phone = new ReadOnlyStringBindingProperty(this, "phone", Bindings.selectString(address, "phone"));
        cityZipCountry = new ReadOnlyStringBindingProperty(this, "cityZipCountry", Bindings.selectString(address, "cityZipCountry"));
        addressText = new ReadOnlyStringBindingProperty(this, "cityZipCountry",
                () -> AddressModel.calculateSingleLineAddress(address1.get(), address2.get(), cityZipCountry.get(), phone.get()));
        multiLineAddress = new ReadOnlyStringBindingProperty(this, "multiLineAddress",
                () -> AddressModel.calculateMultiLineAddress(AddressModel.calculateAddressLines(address1.get(), address2.get()),
                        cityZipCountry.get(), phone.get()));
        valid = new ReadOnlyBooleanBindingProperty(this, "valid",
                Bindings.createBooleanBinding(() -> Values.isNotNullWhiteSpaceOrEmpty(name.get()), name)
                        .and(Bindings.selectBoolean(address, "valid")).and(Bindings.select(address, "rowState").isNotEqualTo(DataRowState.DELETED)));
    }

    @Override
    public ReadOnlyStringProperty getMultiLineAddress() {
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
    public AddressItem<? extends IAddressDAO> getAddress() {
        return address.get();
    }

    public void setAddress(AddressItem<? extends IAddressDAO> value) {
        address.set(value);
    }

    @Override
    public ObjectProperty<AddressItem<? extends IAddressDAO>> addressProperty() {
        return address;
    }

    @Override
    public String getAddress1() {
        return address1.get();
    }

    @Override
    public ReadOnlyStringBindingProperty address1Property() {
        return address1;
    }

    @Override
    public String getAddress2() {
        return address2.get();
    }

    @Override
    public ReadOnlyStringBindingProperty address2Property() {
        return address2;
    }

    @Override
    public String getCityName() {
        return cityName.get();
    }

    @Override
    public ReadOnlyStringBindingProperty cityNameProperty() {
        return cityName;
    }

    @Override
    public String getCountryName() {
        return countryName.get();
    }

    @Override
    public ReadOnlyStringBindingProperty countryNameProperty() {
        return countryName;
    }

    @Override
    public String getPostalCode() {
        return postalCode.get();
    }

    @Override
    public ReadOnlyStringBindingProperty postalCodeProperty() {
        return postalCode;
    }

    @Override
    public String getPhone() {
        return phone.get();
    }

    @Override
    public ReadOnlyStringBindingProperty phoneProperty() {
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
    public ReadOnlyStringBindingProperty cityZipCountryProperty() {
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
        if (isNewRow()) {
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
            if (isNewRow()) {
                return name.isEqualTo(other.name).get() && address.isEqualTo(other.address).get() && active.isEqualTo(other.active).get();
            }
            return !other.isNewRow() && primaryKeyProperty().isEqualTo(other.primaryKeyProperty()).get();
        }
        return false;
    }

    @Override
    public boolean isValid() {
        return valid.get();
    }

    @Override
    public ReadOnlyBooleanProperty validProperty() {
        return valid;
    }

    public final static class Factory extends FxRecordModel.ModelFactory<CustomerDAO, CustomerModel> {

        private Factory() {
        }

        @Override
        public DaoFactory<CustomerDAO> getDaoFactory() {
            return CustomerDAO.FACTORY;
        }

        @Override
        public CustomerModel createNew(CustomerDAO dao) {
            return new CustomerModel(dao);
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
            CustomerDAO dao = item.dataObject();
            if (dao.getRowState() == DataRowState.DELETED) {
                throw new IllegalArgumentException("Customer has been deleted");
            }
            String name = item.name.get();
            if (name.trim().isEmpty()) {
                throw new IllegalArgumentException("Customer name empty");
            }
            AddressItem<? extends IAddressDAO> addressModel = item.address.get();
            if (null == addressModel) {
                throw new IllegalArgumentException("No associated address");
            }
            IAddressDAO addressDAO = (addressModel instanceof AddressItem)
                    ? ((AddressItem<? extends IAddressDAO>) addressModel).dataObject() : (IAddressDAO) addressModel;
            if (null == addressDAO || addressDAO.getRowState() == DataRowState.DELETED) {
                throw new IllegalArgumentException("Associated address has been deleted");
            }
            dao.setAddress(addressDAO);
            dao.setName(name);
            dao.setActive(item.isActive());
            return dao;
        }

        @Override
        protected void updateItemProperties(CustomerModel item, CustomerDAO dao) {
            item.setName(dao.getName());
            item.setActive(dao.isActive());
            item.setAddress(AddressItem.createModel(dao.getAddress()));
        }

    }

}
