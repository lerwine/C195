package scheduler.model;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface AddressProperties extends AddressLookup {

    public static final int MAX_LENGTH_ADDRESS1 = 50;

    /**
     * The name of the 'address1' property.
     */
    public static final String PROP_ADDRESS1 = "address1";

    public static final int MAX_LENGTH_ADDRESS2 = 50;

    /**
     * The name of the 'address2' property.
     */
    public static final String PROP_ADDRESS2 = "address2";

    /**
     * The name of the 'city' property.
     */
    public static final String PROP_CITY = "city";

    public static final int MAX_LENGTH_POSTALCODE = 10;

    /**
     * The name of the 'postalCode' property.
     */
    public static final String PROP_POSTALCODE = "postalCode";

    public static final int MAX_LENGTH_PHONE = 20;

    /**
     * The name of the 'phone' property.
     */
    public static final String PROP_PHONE = "phone";

    /**
     * Gets the {@link CityProperties} for the current address.
     *
     * @return The {@link CityProperties} for the current address.
     */
    CityProperties getCity();

}
