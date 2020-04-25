package scheduler.model;

/**
 * Interface for objects that contain either partial or complete information from the {@code city} database entity.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @todo Implement {@code scheduler.model.City}
 */
public interface City extends DataModel {

    /**
     * Gets the name of the current city. This corresponds to the "city" database column.
     *
     * @return The name of the current city.
     */
    String getName();

    /**
     * Gets the {@link CountryElement} for the current city. This corresponds to the "country" data row referenced by the "countryId" database column.
     *
     * @return The {@link CountryElement} for the current city.
     */
    Country getCountry();
}
