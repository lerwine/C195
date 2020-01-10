package scheduler.dao;

import java.sql.Timestamp;

/**
 * Base interface for all data objects representing a data row in the database.
 * 
 * @author erwinel
 */
public interface DataObject {

    //<editor-fold defaultstate="collapsed" desc="Row state values">
    
    /**
     * Value of {@link #getRowState()} when the current data object has been deleted from the database.
     */
    public static final int ROWSTATE_DELETED = -1;
    
    /**
     * Value of {@link #getRowState()} when the current data object has not yet been added to the database.
     */
    public static final int ROWSTATE_NEW = 0;
    
    /**
     * Value of {@link #getRowState()} when the properties of the current data object has not been modified since it was last synchronized with the database.
     */
    public static final int ROWSTATE_UNMODIFIED = 1;
    
    /**
     * Value of {@link #getRowState()} when the properties of the current data object differ from the data stored in the database.
     */
    public static final int ROWSTATE_MODIFIED = 2;
    
    //</editor-fold>
    
    /**
     * Gets the value of the primary key for the current data object.
     * @return The unique primary key value for the current data object.
     */
    int getPrimaryKey();
    
    /**
     * Gets the timestamp when the data row associated with the current data object was inserted into the database.
     * @return The timestamp when the data row associated with the current data object was inserted into the database.
     */
    Timestamp getCreateDate();
    
    /**
     * Gets the user name of the person who inserted the data row associated with the current data object into the database.
     * @return The user name of the person who inserted the data row associated with the current data object into the database.
     */
    String getCreatedBy();
    
    /**
     * Gets the timestamp when the data row associated with the current data object was last modified.
     * @return The timestamp when the data row associated with the current data object was last modified.
     */
    Timestamp getLastModifiedDate();
    
    /**
     * Gets the user name of the person who last modified the data row associated with the current data object in the database.
     * @return The user name of the person who last modified the data row associated with the current data object in the database.
     */
    String getLastModifiedBy();
    
    /**
     * Gets a value which indicates the disposition of the current data object in relation to the corresponding data row in the database.
     * @return {@link #ROWSTATE_UNMODIFIED}, {@link #ROWSTATE_MODIFIED}, {@link #ROWSTATE_NEW} or {@link #ROWSTATE_DELETED}.
     */
    int getRowState();
}
