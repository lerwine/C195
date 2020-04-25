package scheduler.model;

/**
 * Interface for objects that contain either partial or complete information from the {@code address} database entity.
 * 
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @todo Implement {@code scheduler.model.Address}
 */
public interface Address extends DataModel {

    /**
     * Gets the first line of the current address. Column definition: <code>`address` varchar(50) NOT NULL</code>
     *
     * @return the first line of the current address.
     */
    String getAddress1();

    /**
     * Gets the second line of the current address. Column definition: <code>`address2` varchar(50) NOT NULL</code>
     *
     * @return the second line of the current address.
     */
    String getAddress2();

    /**
     * Gets the {@link CityElement} for the current address. This corresponds to the "city" data row referenced by the "cityId" database column.
     * Column definition: <code>`cityId` int(10) NOT NULL</code> Key constraint definition:
     * <code>CONSTRAINT `address_ibfk_1` FOREIGN KEY (`cityId`) REFERENCES `city` (`cityId`)</code>
     *
     * @return The {@link CityElement} for the current address.
     */
    City getCity();

    /**
     * Gets the postal code for the current address. Column definition: <code>`postalCode` varchar(10) NOT NULL</code>
     *
     * @return the postal code for the current address.
     */
    String getPostalCode();

    /**
     * Gets the phone number associated with the current address. Column definition: <code>`phone` varchar(20) NOT NULL</code>
     *
     * @return the phone number associated with the current address.
     */
    String getPhone();

}
