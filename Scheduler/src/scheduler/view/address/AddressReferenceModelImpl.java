/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.view.address;

import java.sql.SQLException;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import scheduler.dao.Address;
import scheduler.dao.City;
import scheduler.dao.CityImpl;
import scheduler.dao.DataObjectImpl;
import scheduler.observables.ChildPropertyWrapper;
import scheduler.observables.CityZipCountryProperty;
import scheduler.util.Values;
import scheduler.view.city.CityReferenceModel;
import scheduler.view.city.CityReferenceModelImpl;

/**
 *
 * @author lerwi
 */
public class AddressReferenceModelImpl extends DataObjectImpl.DataObjectReferenceModelImpl<Address> implements AddressReferenceModel<Address> {

    private final ReadOnlyStringWrapper address1;
    private final ReadOnlyStringWrapper address2;
    private final AddressLinesProperty addressLines;
    private final ReadOnlyObjectWrapper<CityReferenceModel<? extends City>> city;
    private final ChildPropertyWrapper<String, CityReferenceModel<? extends City>> cityName;
    private final ChildPropertyWrapper<String, CityReferenceModel<? extends City>> countryName;
    private final ReadOnlyStringWrapper postalCode;
    private final ReadOnlyStringWrapper phone;
    private final CityZipCountryProperty cityZipCountry;

    public AddressReferenceModelImpl(Address dao) throws SQLException, ClassNotFoundException {
        super(dao);
        address1 = new ReadOnlyStringWrapper(this, "address1", dao.getAddress1());
        address2 = new ReadOnlyStringWrapper(this, "address2", dao.getAddress2());
        addressLines = new AddressLinesProperty();
        City c = dao.getCity().ensurePartial(CityImpl.getFactory());
        city = new ReadOnlyObjectWrapper<>(this, "city", (null == c) ? null : new CityReferenceModelImpl(c));
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
    public CityReferenceModel<? extends City> getCity() {
        return city.get();
    }

    @Override
    public ReadOnlyProperty<CityReferenceModel<? extends City>> cityProperty() {
        return city.getReadOnlyProperty();
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
            return AddressReferenceModelImpl.this;
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
