package scheduler.dao;

/**
 * Base interface for all data objects representing a data row or a related row in the database.
 *
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
 */
public interface DataElement {

    /**
     * Gets the value of the primary key for the current data object.
     *
     * @return The unique primary key value for the current data object.
     */
    int getPrimaryKey();

    /**
     * Gets a value which indicates the disposition of the current data object in relation to the corresponding data row in the database.
     *
     * @return {@link DataRowState} value that indicates the disposition of the current data object in relation to the corresponding
     * data row in the database.
     */
    DataRowState getRowState();
    
    /**
     * Checks validity of {@link DataElement} properties.
     * This simply checks property values and does not do any other referential validation.
     * 
     * @return A {@link ValidationResult} value indicating validation status.
     */
    default ValidationResult validate() { return ValidationResult.OK; }
    
    /**
     * Gets a value which indicates whether the current data object exists in the database.
     *
     * @return {@code true} if the row state is {@link DataRowState#UNMODIFIED} or {@link DataRowState#MODIFIED},
     * otherwise, {@code false} if the row state is {@link DataRowState#NEW} or {@link DataRowState#DELETED}.
     */
    default boolean isExisting() {
        return DataRowState.existsInDb(getRowState());
    }
}
