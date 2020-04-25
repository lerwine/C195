package scheduler.model.ui;

import javafx.beans.property.ReadOnlyProperty;
import scheduler.model.Address;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface AddressItem extends Address, UIModel {

    /**
     * Gets the property that contains the first line of the address.
     * 
     * @return The property that contains the first line of the address.
     */
    ReadOnlyProperty<String> address1Property();

    /**
     * Gets the property that contains the second line of the address.
     * 
     * @return The property that contains the second line of the address.
     */
    ReadOnlyProperty<String> address2Property();

    @Override
    public CityItem getCity();

    /**
     * Gets the property that contains the city model for the address.
     * 
     * @return The property that contains the city model for the address.
     */
    ReadOnlyProperty<? extends CityItem> cityProperty();

    /**
     * Gets the property that contains the postal code for the address.
     * 
     * @return The property that contains the postal code for the address.
     */
    ReadOnlyProperty<String> postalCodeProperty();

    /**
     * Gets the property that contains the phone number associated with the address.
     * 
     * @return The property that contains the phone number associated with the address.
     */
    ReadOnlyProperty<String> phoneProperty();

}
