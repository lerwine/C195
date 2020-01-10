package scheduler.dao;

/**
 * Represents a data row from the "address" database table.
 * 
 * @author erwinel
 */
public interface Address extends DataObject {

    /**
     * Gets the first line of the current address.
     * This corresponds to the "address" database column.
     * 
     * @return the first line of the current address.
     */
    String getAddress1();

    /**
     * Gets the second line of the current address.
     * This corresponds to the "address2" database column.
     * 
     * @return the second line of the current address.
     */
    String getAddress2();
    
    /**
     * Gets the {@link City} for the current address.
     * This corresponds to the "city" data row referenced by the "cityId" database column.
     * 
     * @return The {@link City} for the current address.
     */
    City getCity();
    
    /**
     * Gets the postal code for the current address.
     * This corresponds to the "postalCode" database column.
     * 
     * @return the postal code for the current address.
     */
    String getPostalCode();
    
    /**
     * Gets the phone number associated with the current address.
     * This corresponds to the "phone" database column.
     * 
     * @return the phone number associated with the current address.
     */
    String getPhone();
}
