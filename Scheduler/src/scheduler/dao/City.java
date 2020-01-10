package scheduler.dao;

/**
 * Represents a data row from the "city" database table.
 * 
 *
 * @author erwinel
 */
public interface City extends DataObject {
    
    /**
     * Gets the name of the current city.
     * This corresponds to the "city" database column.
     * 
     * @return The name of the current city.
     */
    String getName();

    /**
     * Gets the {@link Country} for the current city.
     * This corresponds to the "country" data row referenced by the "countryId" database column.
     * 
     * @return The {@link Country} for the current city.
     */
    Country getCountry();
}
