package scheduler.view.address;

import javafx.beans.binding.StringBinding;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import scheduler.dao.AddressElement;
import scheduler.dao.CityElement;
import scheduler.observables.ChildPropertyWrapper;
import scheduler.observables.CityZipCountryProperty;
import scheduler.util.Values;
import scheduler.view.city.CityModel;
import scheduler.view.city.RelatedCityModel;
import scheduler.view.model.RelatedItemModel;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
 */
public class RelatedAddressModel extends RelatedItemModel<AddressElement> implements AddressModel<AddressElement> {

    private final ReadOnlyStringWrapper address1;
    private final ReadOnlyStringWrapper address2;
    private final AddressLinesProperty addressLines;
    private final ReadOnlyObjectWrapper<CityModel<? extends CityElement>> city;
    private final ChildPropertyWrapper<String, CityModel<? extends CityElement>> cityName;
    private final ChildPropertyWrapper<String, CityModel<? extends CityElement>> countryName;
    private final ReadOnlyStringWrapper postalCode;
    private final ReadOnlyStringWrapper phone;
    private final CityZipCountryProperty cityZipCountry;

    public RelatedAddressModel(AddressElement dao) {
        super(dao);
        address1 = new ReadOnlyStringWrapper(this, "address1", dao.getAddress1());
        address2 = new ReadOnlyStringWrapper(this, "address2", dao.getAddress2());
        addressLines = new AddressLinesProperty();
        CityElement c = dao.getCity();
        city = new ReadOnlyObjectWrapper<>(this, "city", (null == c) ? null : new RelatedCityModel(c));
        cityName = new ChildPropertyWrapper<>(this, "cityName", city, (t) -> t.nameProperty());
        countryName = new ChildPropertyWrapper<>(this, "countryName", city, (t) -> t.countryNameProperty());
        postalCode = new ReadOnlyStringWrapper(this, "postalCode", dao.getPostalCode());
        phone = new ReadOnlyStringWrapper(this, "phone", dao.getPhone());
        cityZipCountry = new CityZipCountryProperty(this, "cityZipCountry", this);
    }

    @Override
    public String getAddress1() {
        return address1.get();
    }

    @Override
    public ReadOnlyProperty<String> address1Property() {
        return address1.getReadOnlyProperty();
    }

    @Override
    public String getAddress2() {
        return address2.get();
    }

    @Override
    public ReadOnlyProperty<String> address2Property() {
        return address2.getReadOnlyProperty();
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

    @Override
    public ReadOnlyProperty<CityModel<? extends CityElement>> cityProperty() {
        return city.getReadOnlyProperty();
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

    @Override
    public ReadOnlyProperty<String> postalCodeProperty() {
        return postalCode.getReadOnlyProperty();
    }

    @Override
    public String getPhone() {
        return phone.get();
    }

    @Override
    public ReadOnlyProperty<String> phoneProperty() {
        return phone.getReadOnlyProperty();
    }

    @Override
    public String getCityZipCountry() {
        return cityZipCountry.get();
    }

    @Override
    public ReadOnlyProperty<String> cityZipCountryProperty() {
        return cityZipCountry;
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
            return RelatedAddressModel.this;
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
