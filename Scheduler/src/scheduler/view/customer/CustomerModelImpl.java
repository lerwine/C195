package scheduler.view.customer;

import java.util.Objects;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import scheduler.dao.AddressElement;
import scheduler.dao.CustomerDAO;
import scheduler.dao.DataAccessObject.DaoFactory;
import scheduler.dao.DataRowState;
import scheduler.observables.AddressTextProperty;
import scheduler.observables.ChildPropertyWrapper;
import scheduler.observables.NonNullableStringProperty;
import static scheduler.util.ResourceBundleLoader.getResourceString;
import scheduler.view.address.AddressModel;
import scheduler.view.address.EditAddress;
import scheduler.view.address.RelatedAddressModel;
import static scheduler.view.appointment.EditAppointmentConstants.RESOURCEKEY_PHONENUMBER;
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
    private final StringBinding multiLineAddress;

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
        this.multiLineAddress = Bindings.createStringBinding(() -> {
            String a1 = address1.get().trim();
            String a2 = address2.get().trim();
            String c = cityZipCountry.get().trim();
            String p = phone.get().trim();
            if (a1.isEmpty()) {
                if (a2.isEmpty()) {
                    if (c.isEmpty()) {
                        return (p.isEmpty()) ? "" : String.format("%s %s", getResourceString(EditAddress.class, RESOURCEKEY_PHONENUMBER), p);
                    }
                    return (p.isEmpty()) ? c : String.format("%s%n%s %s", c, getResourceString(EditAddress.class, RESOURCEKEY_PHONENUMBER), p);
                }
                if (c.isEmpty()) {
                    return (p.isEmpty()) ? a2 : String.format("%s%n%s %s", a2, getResourceString(EditAddress.class, RESOURCEKEY_PHONENUMBER), p);
                }
                return (p.isEmpty()) ? String.format("%s%n%s", a2, c)
                        : String.format("%s%n%s%n%s %s", a2, c, getResourceString(EditAddress.class, RESOURCEKEY_PHONENUMBER), p);
            }
            if (a2.isEmpty()) {
                if (c.isEmpty()) {
                    return (p.isEmpty()) ? a1 : String.format("%s%n%s %s", a1, getResourceString(EditAddress.class, RESOURCEKEY_PHONENUMBER), p);
                }
                return (p.isEmpty()) ? String.format("%s%n%s", a1, c)
                        : String.format("%s%n%s%n%s %s", a1, c, getResourceString(EditAddress.class, RESOURCEKEY_PHONENUMBER), p);
            }
            if (c.isEmpty()) {
                return (p.isEmpty()) ? String.format("%s%n%s", a1, a2)
                        : String.format("%s%n%s%n%s %s", a1, a2, getResourceString(EditAddress.class, RESOURCEKEY_PHONENUMBER), p);
            }
            return (p.isEmpty()) ? String.format("%s%n%s%n%s", a1, a2, c)
                    : String.format("%s%n%s%n%s%n%s %s", a1, a2, c, getResourceString(EditAddress.class, RESOURCEKEY_PHONENUMBER), p);
        }, address1, address2, cityZipCountry, phone);
    }

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
            item.setName(dao.getName());
            item.setActive(item.isActive());
            AddressElement addressDAO = dao.getAddress();
            item.setAddress((null == addressDAO) ? null : new RelatedAddressModel(addressDAO));
        }

        public CustomerModelFilter getAllItemsFilter() {
            return CustomerModelFilter.all();
        }

        @Override
        public CustomerDAO updateDAO(CustomerModelImpl item) {
            CustomerDAO dao = item.getDataObject();
            if (dao.getRowState() == DataRowState.DELETED) {
                throw new IllegalArgumentException("Customer has been deleted");
            }
            String name = item.name.get();
            if (name.trim().isEmpty()) {
                throw new IllegalArgumentException("Customer name empty");
            }
            AddressModel<? extends AddressElement> addressModel = item.address.get();
            if (null == addressModel) {
                throw new IllegalArgumentException("No associated address");
            }
            AddressElement addressDAO = addressModel.getDataObject();
            switch (addressDAO.getRowState()) {
                case DELETED:
                    throw new IllegalArgumentException("Associated address has been deleted");
                case NEW:
                    throw new IllegalArgumentException("Associated address has never been saved");
                default:
                    dao.setAddress(addressDAO);
                    break;
            }
            dao.setName(name);
            dao.setActive(item.isActive());
            return dao;
        }

    }

}
