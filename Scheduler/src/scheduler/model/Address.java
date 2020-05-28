package scheduler.model;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface Address {

    /**
     * Gets the first line of the current address.
     *
     * @return the first line of the current address.
     */
    String getAddress1();

    /**
     * Gets the second line of the current address.
     *
     * @return the second line of the current address.
     */
    String getAddress2();

    /**
     * Gets the {@link City} for the current address.
     *
     * @return The {@link City} for the current address.
     */
    City getCity();

    /**
     * Gets the postal code for the current address.
     *
     * @return the postal code for the current address.
     */
    String getPostalCode();

    /**
     * Gets the phone number associated with the current address.
     *
     * @return the phone number associated with the current address.
     */
    String getPhone();

}
