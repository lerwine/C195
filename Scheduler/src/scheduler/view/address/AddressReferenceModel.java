package scheduler.view.address;

import javafx.beans.property.ReadOnlyProperty;
import scheduler.dao.Address;
import scheduler.dao.City;
import scheduler.observables.ChildPropertyWrapper;
import scheduler.view.DataObjectReferenceModel;
import scheduler.view.city.CityReferenceModel;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
 * @param <T>
 */
    // TODO: Parameterize this
public interface AddressReferenceModel<T extends Address> extends DataObjectReferenceModel<T> {

    String getAddress1();

    ReadOnlyProperty<String> address1Property();

    String getAddress2();

    ReadOnlyProperty<String> address2Property();

    String getAddressLines();

    ReadOnlyProperty<String> addressLinesProperty();

    // TODO: Parameterize this
    CityReferenceModel<? extends City> getCity();

    // TODO: Parameterize this
    ReadOnlyProperty<CityReferenceModel<? extends City>> cityProperty();

    String getCityName();

    // TODO: Parameterize this
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
