package scheduler.dao;

import scheduler.dao.factory.DataObjectFactory;

/**
 * Base interface for all data objects representing a data row in the database.
 * 
 * @author erwinel
 */
public interface DataObject {

    /**
     * Gets the value of the primary key for the current data object.
     * @return The unique primary key value for the current data object.
     */
    int getPrimaryKey();
    
    /**
     * Gets a value which indicates the disposition of the current data object in relation to the corresponding data row in the database.
     * @return {@link DataObjectFactory#ROWSTATE_UNMODIFIED}, {@link DataObjectFactory#ROWSTATE_MODIFIED}, {@link DataObjectFactory#ROWSTATE_NEW} or {@link DataObjectFactory#ROWSTATE_DELETED}.
     */
    int getRowState();
    
    /**
     * Gets a value which indicates whether the current data object exists in the database.
     * @return {@code true} if the row state is {@link DataObjectFactory#ROWSTATE_UNMODIFIED}, {@link DataObjectFactory#ROWSTATE_MODIFIED}, otherwise, {@code false} if
     * the row state is {@link DataObjectFactory#ROWSTATE_NEW} or {@link DataObjectFactory#ROWSTATE_DELETED}.
     */
    default  boolean isExisting() { return getRowState() == DataObjectFactory.ROWSTATE_MODIFIED || getRowState() == DataObjectFactory.ROWSTATE_UNMODIFIED; }
}
