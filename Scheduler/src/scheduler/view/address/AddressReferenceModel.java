package scheduler.view.address;

import javafx.beans.property.ReadOnlyProperty;
import scheduler.dao.Address;
import scheduler.dao.City;
import scheduler.observables.ChildPropertyWrapper;
import scheduler.view.DataObjectReferenceModel;
import scheduler.view.city.CityReferenceModel;

/**
 *
 * @author lerwi
 * @param <T>
 */
public interface AddressReferenceModel<T extends Address> extends DataObjectReferenceModel<T> {

    String getAddress1();

    ReadOnlyProperty<String> address1Property();

    String getAddress2();

    ReadOnlyProperty<String> address2Property();

    String getAddressLines();

    ReadOnlyProperty<String> addressLinesProperty();

    CityReferenceModel<? extends City> getCity();

    ReadOnlyProperty<CityReferenceModel<? extends City>> cityProperty();

    String getCityName();

    ChildPropertyWrapper<String, CityReferenceModel<? extends City>> cityNameProperty();

    String getCountryName();

    ChildPropertyWrapper<String, CityReferenceModel<? extends City>> countryNameProperty();

    String getPostalCode();

    ReadOnlyProperty<String> postalCodeProperty();

    String getPhone();

    ReadOnlyProperty<String> phoneProperty();

    String getCityZipCountry();

    ReadOnlyProperty<String> cityZipCountryProperty();
}
