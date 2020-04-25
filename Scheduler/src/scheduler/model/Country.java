package scheduler.model;

/**
 * Interface for objects that contain either partial or complete information from the {@code country} database entity.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @todo Implement {@code scheduler.model.Country}
 */
public interface Country extends DataModel {

    /**
     * Gets the name of the current country.
     *
     * @return The name of the current country.
     */
    String getName();

}
