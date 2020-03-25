package scheduler.view.address;

import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import scheduler.dao.AddressImpl;
import scheduler.dao.City;
import scheduler.dao.DataObjectImpl.Factory;
import scheduler.observables.ChildPropertyWrapper;
import scheduler.observables.CityZipCountryProperty;
import scheduler.observables.NonNullableStringProperty;
import scheduler.util.Values;
import scheduler.view.city.CityReferenceModel;
import scheduler.view.city.CityReferenceModelImpl;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
 */
public final class AddressModel extends scheduler.view.ItemModel<AddressImpl> implements AddressReferenceModel<AddressImpl> {

    private final NonNullableStringProperty address1;
    private final NonNullableStringProperty address2;
    private final AddressLinesProperty addressLines;
    private final SimpleObjectProperty<CityReferenceModel<? extends City>> city;
    private final ChildPropertyWrapper<String, CityReferenceModel<? extends City>> cityName;
    private final ChildPropertyWrapper<String, CityReferenceModel<? extends City>> countryName;
    private final NonNullableStringProperty postalCode;
    private final NonNullableStringProperty phone;
    private final CityZipCountryProperty cityZipCountry;

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
    public CityReferenceModel<? extends City> getCity() {
        return city.get();
    }

    public void setCity(CityReferenceModel<? extends City> value) {
        city.set(value);
    }

    @Override
    public ObjectProperty<CityReferenceModel<? extends City>> cityProperty() {
        return city;
    }

    @Override
    public String getCityName() {
        return cityName.get();
    }

    @Override
    public ChildPropertyWrapper<String, CityReferenceModel<? extends City>> cityNameProperty() {
        return cityName;
    }

    @Override
    public String getCountryName() {
        return countryName.get();
    }

    @Override
    public ChildPropertyWrapper<String, CityReferenceModel<? extends City>> countryNameProperty() {
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

    public AddressModel(AddressImpl dao) {
        super(dao);
        address1 = new NonNullableStringProperty(this, "address1", dao.getAddress1());
        address2 = new NonNullableStringProperty(this, "address2", dao.getAddress2());
        addressLines = new AddressLinesProperty();
        City c = dao.getCity();
        city = new SimpleObjectProperty<>(this, "city", (null == c) ? null : new CityReferenceModelImpl(c));
        cityName = new ChildPropertyWrapper<>(this, "cityName", city, (t) -> t.nameProperty());
        countryName = new ChildPropertyWrapper<>(this, "countryName", city, (t) -> t.countryNameProperty());
        postalCode = new NonNullableStringProperty(this, "postalCode", dao.getPostalCode());
        phone = new NonNullableStringProperty(this, "phone", dao.getPhone());
        cityZipCountry = new CityZipCountryProperty(this, "cityZipCountry", this);
    }

    @Override
    protected void refreshFromDAO(AddressImpl dao) {
        address1.set(dao.getAddress1());
        address2.set(dao.getAddress2());
        // TODO: Parameterize this
        City c = dao.getCity();
        city.set((null == c) ? null : new CityReferenceModelImpl(c));
        postalCode.set(dao.getPostalCode());
        phone.set(dao.getPhone());
    }

    @Override
    public Factory<AddressImpl, AddressModel> getDaoFactory() {
        return AddressImpl.getFactory();
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
            return a1.isEmpty() ? a2 : String.format("%s\n%s", a1, a2);
        }

        @Override
        public Object getBean() {
            return AddressModel.this;
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
