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

    /**
     * Gets the first line of the address.
     * 
     * @return The first line of the address.
     */
    String getAddress1();

    /**
     * Gets the property that contains the first line of the address.
     * 
     * @return The property that contains the first line of the address.
     */
    ReadOnlyProperty<String> address1Property();

    /**
     * Gets the second line of the address.
     * 
     * @return The second line of the address.
     */
    String getAddress2();

    /**
     * Gets the property that contains the second line of the address.
     * 
     * @return The property that contains the second line of the address.
     */
    ReadOnlyProperty<String> address2Property();

    /**
     * Gets the first and second lines of the address.
     * If both the first and second lines are non-empty, they will be separated by line-break.
     * 
     * @return The first and second lines of the address.
     */
    String getAddressLines();

    /**
     * Gets the property that contains the first and second lines of the address.
     * 
     * @return The property that contains the first and second lines of the address.
     */
    ReadOnlyProperty<String> addressLinesProperty();

    /**
     * Gets the city model for the address.
     * 
     * @return The city model for the address.
     */
    CityModel<? extends CityElement> getCity();

    /**
     * Gets the property that contains the city model for the address.
     * 
     * @return The property that contains the city model for the address.
     */
    ReadOnlyProperty<CityModel<? extends CityElement>> cityProperty();

    /**
     * Gets the the name of the city.
     * 
     * @return The value of the {@link CityModel#nameProperty()} or an empty string if the {@link #cityProperty()} is null.
     */
    String getCityName();

    /**
     * Gets the binding for the name of the city.
     * 
     * @return A {@link ChildPropertyWrapper} gets the value of the {@link CityModel#nameProperty()} from the {@link #cityProperty()}
     */
    ChildPropertyWrapper<String, CityModel<? extends CityElement>> cityNameProperty();

    /**
     * Gets the name of the country.
     * 
     * @return The value of the {@link CityModel#countryNameProperty()} or an empty string if the {@link #cityProperty()} is null.
     */
    String getCountryName();

    /**
     * Gets the binding for the name of the country.
     * 
     * @return A {@link ChildPropertyWrapper} gets the value of the {@link CityModel#countryNameProperty()} from the {@link #cityProperty()}
     */
    ChildPropertyWrapper<String, CityModel<? extends CityElement>> countryNameProperty();

    /**
     * Gets the postal code for the address.
     * 
     * @return The postal code for the address.
     */
    String getPostalCode();

    /**
     * Gets the property that contains the postal code for the address.
     * 
     * @return The property that contains the postal code for the address.
     */
    ReadOnlyProperty<String> postalCodeProperty();

    /**
     * Gets the phone number associated with the address.
     * 
     * @return The phone number associated with the address.
     */
    String getPhone();

    /**
     * Gets the property that contains the phone number associated with the address.
     * 
     * @return The property that contains the phone number associated with the address.
     */
    ReadOnlyProperty<String> phoneProperty();

    /**
     * Gets the combined city name, postal code and country.
     * 
     * @return A string value that combines the {@link #cityNameProperty()}, the {@link #countryNameProperty()} and the {@link #postalCodeProperty()}.
     */
    String getCityZipCountry();

    /**
     * Gets the property that contains the combined city name, postal code and country.
     * 
     * @return The property that contains the combined city name, postal code and country.
     */
    ReadOnlyProperty<String> cityZipCountryProperty();
}
