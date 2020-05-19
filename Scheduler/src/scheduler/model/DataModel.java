package scheduler.model;

/**
 * Base interface for all objects that represent a database entity.
 */
public interface DataModel {
    /**
     * Gets the value of the primary key for the current data object.
     *
     * @return The unique primary key value for the current data object.
     */
    int getPrimaryKey();

}
