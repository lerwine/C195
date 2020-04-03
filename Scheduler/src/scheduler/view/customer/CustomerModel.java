package scheduler.view.customer;

import javafx.beans.property.ReadOnlyProperty;
import scheduler.dao.AddressElement;
import scheduler.dao.CustomerElement;
import scheduler.observables.ChildPropertyWrapper;
import scheduler.view.address.AddressModel;
import scheduler.view.model.ElementModel;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
 * @param <T>
 */
public interface CustomerModel<T extends CustomerElement> extends ElementModel<T> {

    String getName();

    ReadOnlyProperty<String> nameProperty();

    AddressModel<? extends AddressElement> getAddress();

    ReadOnlyProperty<AddressModel<? extends AddressElement>> addressProperty();

    String getAddress1();

    ChildPropertyWrapper<String, AddressModel<? extends AddressElement>> address1Property();

    String getAddress2();

    ChildPropertyWrapper<String, AddressModel<? extends AddressElement>> address2Property();

    String getCityName();

    ChildPropertyWrapper<String, AddressModel<? extends AddressElement>> cityNameProperty();

    String getCountryName();

    ChildPropertyWrapper<String, AddressModel<? extends AddressElement>> countryNameProperty();

    String getPostalCode();

    ChildPropertyWrapper<String, AddressModel<? extends AddressElement>> postalCodeProperty();

    String getPhone();

    ChildPropertyWrapper<String, AddressModel<? extends AddressElement>> phoneProperty();

    String getCityZipCountry();

    ChildPropertyWrapper<String, AddressModel<? extends AddressElement>> cityZipCountryProperty();

    String getAddressText();

    ReadOnlyProperty<String> addressTextProperty();

    boolean isActive();

    ReadOnlyProperty<Boolean> activeProperty();
}
