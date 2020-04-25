package scheduler.view.address;

import javafx.beans.binding.StringBinding;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import scheduler.observables.ChildPropertyWrapper;
import scheduler.observables.CityZipCountryProperty;
import scheduler.util.Values;
import scheduler.view.city.CityModel;
import scheduler.view.city.RelatedCityModel;
import scheduler.view.model.RelatedItemModel;
import scheduler.model.db.AddressRowData;
import scheduler.model.db.CityRowData;
import scheduler.model.ui.CityDbItem;
import scheduler.model.ui.AddressDbItem;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class RelatedAddressModel extends RelatedItemModel<AddressRowData> implements AddressDbItem<AddressRowData> {

    private final ReadOnlyStringWrapper address1;
    private final ReadOnlyStringWrapper address2;
    private final AddressLinesProperty addressLines;
    private final ReadOnlyObjectWrapper<CityDbItem<? extends CityRowData>> city;
    private final ChildPropertyWrapper<String, CityDbItem<? extends CityRowData>> cityName;
    private final ChildPropertyWrapper<String, CityDbItem<? extends CityRowData>> countryName;
    private final ReadOnlyStringWrapper postalCode;
    private final ReadOnlyStringWrapper phone;
    private final CityZipCountryProperty cityZipCountry;

    public RelatedAddressModel(AddressRowData dao) {
        super(dao);
        address1 = new ReadOnlyStringWrapper(this, "address1", dao.getAddress1());
        address2 = new ReadOnlyStringWrapper(this, "address2", dao.getAddress2());
        addressLines = new AddressLinesProperty();
        CityRowData c = dao.getCity();
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

    public String getAddressLines() {
        return addressLines.get();
    }

    public ReadOnlyProperty<String> addressLinesProperty() {
        return addressLines;
    }

    @Override
    public CityDbItem<? extends CityRowData> getCity() {
        return city.get();
    }

    @Override
    public ReadOnlyProperty<CityDbItem<? extends CityRowData>> cityProperty() {
        return city.getReadOnlyProperty();
    }

    public String getCityName() {
        return cityName.get();
    }

    public ChildPropertyWrapper<String, CityDbItem<? extends CityRowData>> cityNameProperty() {
        return cityName;
    }

    public String getCountryName() {
        return countryName.get();
    }

    public ChildPropertyWrapper<String, CityDbItem<? extends CityRowData>> countryNameProperty() {
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

    public String getCityZipCountry() {
        return cityZipCountry.get();
    }

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
