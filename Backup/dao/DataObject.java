package scheduler.dao;

import scheduler.util.Values;

/**
 * Base interface for all data objects representing a data row in the database.
 *
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
 */
public interface DataObject {

    /**
     * Gets the value of the primary key for the current data object.
     *
     * @return The unique primary key value for the current data object.
     */
    int getPrimaryKey();

    /**
     * Gets a value which indicates the disposition of the current data object in relation to the corresponding data row in the database.
     *
     * @return {@link Values#ROWSTATE_UNMODIFIED}, {@link Values#ROWSTATE_MODIFIED}, {@link Values#ROWSTATE_NEW} or {@link Values#ROWSTATE_DELETED}.
     */
    int getRowState();

    /**
     * Gets a value which indicates whether the current data object exists in the database.
     *
     * @return {@code true} if the row state is {@link Values#ROWSTATE_UNMODIFIED}, {@link Values#ROWSTATE_MODIFIED}, otherwise, {@code false} if the row state is
     * {@link Values#ROWSTATE_NEW} or {@link Values#ROWSTATE_DELETED}.
     */
    default boolean isExisting() {
        return getRowState() == Values.ROWSTATE_MODIFIED || getRowState() == Values.ROWSTATE_UNMODIFIED;
    }
}
