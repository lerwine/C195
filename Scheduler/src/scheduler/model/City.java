package scheduler.model;

import java.time.ZoneId;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface City {

    /**
     * Gets the name of the current city. This corresponds to the first part of the text in the "city" database column.
     *
     * @return The name of the current city.
     */
    String getName();

    /**
     * Gets the {@link ZoneId} for the current city. This is parsed from the end of the text in the "city" database column.
     *
     * @return The {@link Country} for the current city.
     */
    ZoneId getZoneId();
    
    /**
     * Gets the {@link Country} for the current city. This corresponds to the "country" data row referenced by the "countryId" database column.
     *
     * @return The {@link Country} for the current city.
     */
    Country getCountry();

}
