package scheduler.view.customer;

import java.time.ZoneId;
import javafx.beans.property.ReadOnlyProperty;
import scheduler.observables.ChildPropertyWrapper;
import scheduler.view.address.AddressModel;
import scheduler.view.model.ElementModel;
import scheduler.model.db.AddressRowData;
import scheduler.model.db.CustomerRowData;

/**
 * Interface that represents a customer model object.
 * 
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> The type of underlying {@link CustomerRowData} for the model.
 * @deprecated Use {@link scheduler.model.ui.CustomerItem}, instead.
 */
public interface CustomerModel<T extends CustomerRowData> extends ElementModel<T> {

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
    AddressModel<? extends AddressRowData> getAddress();

    /**
     * Gets the property that contains the address model associated with the customer.
     * 
     * @return The property that contains the address model associated with the customer.
     */
    ReadOnlyProperty<AddressModel<? extends AddressRowData>> addressProperty();

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
    ChildPropertyWrapper<String, AddressModel<? extends AddressRowData>> address1Property();

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
    ChildPropertyWrapper<String, AddressModel<? extends AddressRowData>> address2Property();

    /**
     * Gets the the name of the city.
     * 
     * @return The value of the {@link AddressModel#cityNameProperty()} or an empty string if the {@link #addressProperty()} is null.
     */
    String getCityName();

    /**
     * Gets the binding for the name of the city.
     * 
     * @return A {@link ChildPropertyWrapper} gets the value of the {@link AddressModel#cityNameProperty()} from the {@link #addressProperty()}
     */
    ChildPropertyWrapper<String, AddressModel<? extends AddressRowData>> cityNameProperty();

    /**
     * Gets the name of the country.
     * 
     * @return The value of the {@link AddressModel#countryNameProperty()} or an empty string if the {@link #addressProperty()} is null.
     */
    String getCountryName();

    /**
     * Gets the binding for the name of the country.
     * 
     * @return A {@link ChildPropertyWrapper} gets the value of the {@link AddressModel#countryNameProperty()} from the {@link #addressProperty()}
     */
    ChildPropertyWrapper<String, AddressModel<? extends AddressRowData>> countryNameProperty();

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
    ChildPropertyWrapper<String, AddressModel<? extends AddressRowData>> postalCodeProperty();

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
    ChildPropertyWrapper<String, AddressModel<? extends AddressRowData>> phoneProperty();

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
    ChildPropertyWrapper<String, AddressModel<? extends AddressRowData>> cityZipCountryProperty();

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
