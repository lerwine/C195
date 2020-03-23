package scheduler.dao;

/**
 * Represents a reference to another data object.
 *
 * @author Leonard T. Erwine (Student ID 356334)
 * @param <T> The full data access object type.
 * @param <U> The partial data access object type.
 */
public interface IDataObjectReference<T extends U, U extends DataObject> {
    
    /**
     * Gets the primary key for the referenced data access object.
     *
     * @return The primary key for the referenced data access object.
     */
     int getPrimaryKey();

    /**
     * Indicates whether this refers to an existing data object.
     *
     * @return {@code true} if this refers to an existing data object; otherwise, {@code false}.
     */
    boolean isEmpty();

    /**
     * Gets the full data access object instance.
     *
     * @return The full data access object instance or {@code null} if this does not contain a full instance.
     */
    T getFull();

    /**
     * Gets the partial data access object instance.
     *
     * @return The partial data access object instance or {@code null} if this does not contain a full or partial instance.
     */
    U getPartial();

}
