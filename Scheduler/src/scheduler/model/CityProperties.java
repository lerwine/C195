package scheduler.model;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface CityProperties {

    public static final int MAX_LENGTH_NAME = 50;

    /**
     * The name of the 'name' property.
     */
    public static final String PROP_NAME = "name";

    /**
     * The name of the 'country' property.
     */
    public static final String PROP_COUNTRY = "country";

    /**
     * Gets the name of the current city. This corresponds to the first part of the text in the "city" database column.
     *
     * @return The name of the current city.
     */
    String getName();

    /**
     * Gets the {@link CountryProperties} for the current city. This corresponds to the "country" data row referenced by the "countryId" database column.
     *
     * @return The {@link CountryProperties} for the current city.
     */
    CountryProperties getCountry();

}
