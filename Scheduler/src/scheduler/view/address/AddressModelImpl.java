package scheduler.view.address;

import java.time.ZoneId;
import java.util.Objects;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import scheduler.dao.AddressDAO;
import scheduler.dao.AddressElement;
import scheduler.dao.CityElement;
import scheduler.dao.DataAccessObject.DaoFactory;
import scheduler.dao.DataRowState;
import scheduler.observables.ChildPropertyWrapper;
import scheduler.observables.CityZipCountryProperty;
import scheduler.observables.NonNullableStringProperty;
import scheduler.util.Values;
import scheduler.view.city.CityModel;
import scheduler.view.city.CityModelImpl;
import scheduler.view.city.RelatedCityModel;
import scheduler.view.model.ItemModel;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public final class AddressModelImpl extends scheduler.view.model.ItemModel<AddressDAO> implements AddressModel<AddressDAO> {

    private static final Factory FACTORY = new Factory();

    public static final Factory getFactory() {
        return FACTORY;
    }

    public static ZoneId getZoneId(AddressModel<? extends AddressElement> address) {
        if (null != address) {
            return CityModelImpl.getZoneId(address.getCity());
        }
        return ZoneId.systemDefault();
    }

    private final NonNullableStringProperty address1;
    private final NonNullableStringProperty address2;
    private final AddressLinesProperty addressLines;
    private final SimpleObjectProperty<CityModel<? extends CityElement>> city;
    private final ChildPropertyWrapper<String, CityModel<? extends CityElement>> cityName;
    private final ChildPropertyWrapper<String, CityModel<? extends CityElement>> countryName;
    private final NonNullableStringProperty postalCode;
    private final NonNullableStringProperty phone;
    private final CityZipCountryProperty cityZipCountry;

    public AddressModelImpl(AddressDAO dao) {
        super(dao);
        address1 = new NonNullableStringProperty(this, "address1", dao.getAddress1());
        address2 = new NonNullableStringProperty(this, "address2", dao.getAddress2());
        addressLines = new AddressLinesProperty();
        CityElement c = dao.getCity();
        city = new SimpleObjectProperty<>(this, "city", (null == c) ? null : new RelatedCityModel(c));
        cityName = new ChildPropertyWrapper<>(this, "cityName", city, (t) -> t.nameProperty());
        countryName = new ChildPropertyWrapper<>(this, "countryName", city, (t) -> t.countryNameProperty());
        postalCode = new NonNullableStringProperty(this, "postalCode", dao.getPostalCode());
        phone = new NonNullableStringProperty(this, "phone", dao.getPhone());
        cityZipCountry = new CityZipCountryProperty(this, "cityZipCountry", this);
    }

    @Override
    public String getAddress1() {
        return address1.get();
    }

    public void setAddress1(String value) {
        address1.set(value);
    }

    @Override
    public StringProperty address1Property() {
        return address1;
    }

    @Override
    public String getAddress2() {
        return address2.get();
    }

    public void setAddress2(String value) {
        address2.set(value);
    }

    @Override
    public StringProperty address2Property() {
        return address2;
    }

    @Override
    public String getAddressLines() {
        return addressLines.get();
    }

    @Override
    public ReadOnlyProperty<String> addressLinesProperty() {
        return addressLines;
    }

    @Override
    public CityModel<? extends CityElement> getCity() {
        return city.get();
    }

    public void setCity(CityModel<? extends CityElement> value) {
        city.set(value);
    }

    @Override
    public ObjectProperty<CityModel<? extends CityElement>> cityProperty() {
        return city;
    }

    @Override
    public String getCityName() {
        return cityName.get();
    }

    @Override
    public ChildPropertyWrapper<String, CityModel<? extends CityElement>> cityNameProperty() {
        return cityName;
    }

    @Override
    public String getCountryName() {
        return countryName.get();
    }

    @Override
    public ChildPropertyWrapper<String, CityModel<? extends CityElement>> countryNameProperty() {
        return countryName;
    }

    @Override
    public String getPostalCode() {
        return postalCode.get();
    }

    public void setPostalCode(String value) {
        postalCode.set(value);
    }

    @Override
    public StringProperty postalCodeProperty() {
        return postalCode;
    }

    @Override
    public String getPhone() {
        return phone.get();
    }

    public void setPhone(String value) {
        phone.set(value);
    }

    @Override
    public StringProperty phoneProperty() {
        return phone;
    }

    @Override
    public String getCityZipCountry() {
        return cityZipCountry.get();
    }

    @Override
    public ReadOnlyProperty<String> cityZipCountryProperty() {
        return cityZipCountry;
    }

    @Override
    public int hashCode() {
        if (isNewItem()) {
            int hash = 3;
            hash = 19 * hash + Objects.hashCode(address1.get());
            hash = 19 * hash + Objects.hashCode(address2.get());
            hash = 19 * hash + Objects.hashCode(city.get());
            hash = 19 * hash + Objects.hashCode(postalCode.get());
            hash = 19 * hash + Objects.hashCode(phone.get());
            return hash;
        }
        return getPrimaryKey();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (null != obj && obj instanceof AddressModelImpl) {
            final AddressModelImpl other = (AddressModelImpl) obj;
            if (isNewItem()) {
                return address1.isEqualTo(other.address1).get() && address2.isEqualTo(other.address2).get() && city.isEqualTo(other.city).get()
                        && postalCode.isEqualTo(other.postalCode).get() && phone.isEqualTo(other.phone).get();
            }
            return !other.isNewItem() && primaryKeyProperty().isEqualTo(other.primaryKeyProperty()).get();
        }
        return false;
    }

    public final static class Factory extends ItemModel.ModelFactory<AddressDAO, AddressModelImpl> {

        private Factory() {
        }

        @Override
        public DaoFactory<AddressDAO> getDaoFactory() {
            return AddressDAO.getFactory();
        }

        @Override
        public AddressModelImpl createNew(AddressDAO dao) {
            return new AddressModelImpl(dao);
        }

        @Override
        public void updateItem(AddressModelImpl item, AddressDAO dao) {
            super.updateItem(item, dao);
            
            item.address1.set(dao.getAddress1());
            item.address2.set(dao.getAddress2());
            CityElement c = dao.getCity();
            item.city.set((null == c) ? null : new RelatedCityModel(c));
            item.postalCode.set(dao.getPostalCode());
            item.phone.set(dao.getPhone());
        }

        @Override
        public AddressDAO updateDAO(AddressModelImpl item) {
            AddressDAO dao = item.getDataObject();
            if (dao.getRowState() == DataRowState.DELETED)
                throw new IllegalArgumentException("Address has been deleted");
            String address1 = item.address1.get();
            String address2 = item.address2.get();
            if (address1.trim().isEmpty() && address2.trim().isEmpty())
                throw new IllegalArgumentException("Address lines 1 and 2 are empty");
            CityModel<? extends CityElement> cityModel = item.city.get();
            if (null == cityModel)
                throw new IllegalArgumentException("No associated city");
            CityElement cityDAO = cityModel.getDataObject();
            switch (cityDAO.getRowState()) {
                case DELETED:
                    throw new IllegalArgumentException("Associated city has been deleted");
                case NEW:
                    throw new IllegalArgumentException("Associated city has never been saved");
                default:
                    dao.setCity(cityDAO);
                    break;
            }
            dao.setAddress1(address1);
            dao.setAddress2(address2);
            dao.setPostalCode(item.getPostalCode());
            dao.setPhone(item.getPhone());
            return dao;
        }

    }

    class AddressLinesProperty extends StringBinding implements ReadOnlyProperty<String> {

        AddressLinesProperty() {
            super.bind(address1, address2);
        }

        @Override
        protected String computeValue() {
            String a1 = Values.asNonNullAndWsNormalized(address1.get());
            String a2 = Values.asNonNullAndWsNormalized(address2.get());
            if (a2.isEmpty()) {
                return a1;
            }
            return a1.isEmpty() ? a2 : String.format("%s%n%s", a1, a2);
        }

        @Override
        public Object getBean() {
            return AddressModelImpl.this;
        }

        @Override
        public String getName() {
            return "addressLines";
        }

        @Override
        public ObservableList<?> getDependencies() {
            return FXCollections.observableArrayList(address1, address2);
        }

        @Override
        public void dispose() {
            super.unbind(address1, address2);
            super.dispose();
        }

    }
}
