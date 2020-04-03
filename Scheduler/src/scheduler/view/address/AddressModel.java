package scheduler.view.address;

import javafx.beans.property.ReadOnlyProperty;
import scheduler.dao.AddressElement;
import scheduler.dao.CityElement;
import scheduler.observables.ChildPropertyWrapper;
import scheduler.view.city.CityModel;
import scheduler.view.model.ElementModel;

/**
 * An {@link ElementModel} for an {@link AddressElement} data access object.
 *
 * @author Leonard T. Erwine (Student ID 356334)
 * @param <T> The type of {@link AddressElement} data access object this model represents.
 */
public interface AddressModel<T extends AddressElement> extends ElementModel<T> {

    String getAddress1();

    ReadOnlyProperty<String> address1Property();

    String getAddress2();

    ReadOnlyProperty<String> address2Property();

    String getAddressLines();

    ReadOnlyProperty<String> addressLinesProperty();

    CityModel<? extends CityElement> getCity();

    ReadOnlyProperty<CityModel<? extends CityElement>> cityProperty();

    String getCityName();

    ChildPropertyWrapper<String, CityModel<? extends CityElement>> cityNameProperty();

    String getCountryName();

    ChildPropertyWrapper<String, CityModel<? extends CityElement>> countryNameProperty();

    String getPostalCode();

    ReadOnlyProperty<String> postalCodeProperty();

    String getPhone();

    ReadOnlyProperty<String> phoneProperty();

    String getCityZipCountry();

    ReadOnlyProperty<String> cityZipCountryProperty();
}
