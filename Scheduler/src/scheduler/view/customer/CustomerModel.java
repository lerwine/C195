package scheduler.view.customer;

import javafx.beans.property.ReadOnlyProperty;
import scheduler.dao.AddressElement;
import scheduler.dao.CustomerElement;
import scheduler.observables.ChildPropertyWrapper;
import scheduler.view.address.AddressModel;
import scheduler.view.model.ElementModel;

/**
 * Interface that represents a customer model object.
 * 
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
 * @param <T> The type of underlying {@link CustomerElement} for the model.
 */
public interface CustomerModel<T extends CustomerElement> extends ElementModel<T> {

    /**
     * Gets the name of the customer.
     * 
     * @return The name of the customer.
     */
    String getName();

    /**
     * Gets the property that contains the name of the customer.
     * 
     * @return The property that contains the name of the customer.
     */
    ReadOnlyProperty<String> nameProperty();

    /**
     * Gets the address model associated with the customer.
     * 
     * @return The address model associated with the customer.
     */
    AddressModel<? extends AddressElement> getAddress();

    /**
     * Gets the property that contains the address model associated with the customer.
     * 
     * @return The property that contains the address model associated with the customer.
     */
    ReadOnlyProperty<AddressModel<? extends AddressElement>> addressProperty();

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
    ChildPropertyWrapper<String, AddressModel<? extends AddressElement>> address1Property();

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
    ChildPropertyWrapper<String, AddressModel<? extends AddressElement>> address2Property();

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
    ChildPropertyWrapper<String, AddressModel<? extends AddressElement>> cityNameProperty();

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
    ChildPropertyWrapper<String, AddressModel<? extends AddressElement>> countryNameProperty();

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
    ChildPropertyWrapper<String, AddressModel<? extends AddressElement>> postalCodeProperty();

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
    ChildPropertyWrapper<String, AddressModel<? extends AddressElement>> phoneProperty();

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
    ChildPropertyWrapper<String, AddressModel<? extends AddressElement>> cityZipCountryProperty();

    /**
     * Gets the customer address as a single line of text with components separated by commas.
     * 
     * @return The customer address as a single line of text with components separated by commas.
     */
    String getAddressText();

    /**
     * Gets the property that contains the customer address as a single line of text with components separated by commas.
     * 
     * @return The property that contains the customer address as a single line of text with components separated by commas.
     */
    ReadOnlyProperty<String> addressTextProperty();

    /**
     * Gets a value that indicates whether the customer is active.
     * 
     * @return {@code true} if the customer is active; otherwise, {@code false}.
     */
    boolean isActive();

    /**
     * Gets the property that contains the value that indicates whether the customer is active.
     * 
     * @return The property that contains the value that indicates whether the customer is active.
     */
    ReadOnlyProperty<Boolean> activeProperty();
}
