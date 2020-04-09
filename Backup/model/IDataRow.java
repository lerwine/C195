package scheduler.model;

import java.sql.Timestamp;
import scheduler.util.IPropertyBindable;

/**
 * Interface for objects that represent a data row in the scheduler database.
 * 
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
 */
public interface IDataRow extends IPropertyBindable {
    
    /**
     * The name of the 'primaryKey' property.
     */
    public static final String PROP_PRIMARYKEY = "primaryKey";
    
    /**
     * The name of the 'createDate' property.
     */
    public static final String PROP_CREATEDATE = "createDate";

    /**
     * The name of the 'createDate' column.
     */
    public static final String COLNAME_CREATEDATE = "createDate";

    /**
     * The name of the 'createdBy' property.
     */
    public static final String PROP_CREATEDBY = "createdBy";

    /**
     * The name of the 'createdBy' column.
     */
    public static final String COLNAME_CREATEDBY = "createdBy";

    /**
     * The name of the 'lastModifiedDate' property.
     */
    public static final String PROP_LASTMODIFIEDDATE = "lastModifiedDate";

    /**
     * The name of the 'lastUpdate' column.
     */
    public static final String COLNAME_LASTUPDATE = "lastUpdate";

    /**
     * The name of the 'lastModifiedBy' property.
     */
    public static final String PROP_LASTMODIFIEDBY = "lastModifiedBy";

    /**
     * The name of the 'lastUpdateBy' column.
     */
    public static final String COLNAME_LASTUPDATEBY = "lastUpdateBy";
    
    /**
     * The name of the 'rowState' property.
     */
    public static final String PROP_ROWSTATE = "rowState";

    /**
     * Gets the value of the primary key for the associated data row.
     *
     * @return The value of the primary key for the associated data row or {@code -1} if the {@link DataRowState} is {@link DataRowState#NEW} or
     * {@link DataRowState#DELETED}.
     */
    int getPrimaryKey();
    
    /**
     * Gets the timestamp when the data row associated with the current data object was inserted into the database.
     *
     * @return The timestamp when the data row associated with the current data object was inserted into the database or the {@link Timestamp}
     * when the object was created if the {@link DataRowState} is {@link DataRowState#NEW}.
     */
    Timestamp getCreateDate();
    
    /**
     * Gets the user name of the person who inserted the data row associated with the current data object into the database.
     *
     * @return The user name of the person who inserted the data row associated with the current data object into the database or the
     * {@link UserDataRow#userName} of the {@link scheduler.App#getCurrentUser()} if the {@link DataRowState} is {@link DataRowState#NEW}.
     */
    String getCreatedBy();
    
    /**
     * Gets the timestamp when the data row associated with the current data object was last modified.
     *
     * @return The timestamp when the data row associated with the current data object was last modified or the {@link Timestamp}
     * when the object was created if the {@link DataRowState} is {@link DataRowState#NEW}.
     */
    Timestamp getLastModifiedDate();
    
    /**
     * Gets the user name of the person who last modified the data row associated with the current data object in the database.
     *
     * @return The user name of the person who last modified the data row associated with the current data object in the database or the
     * {@link UserDataRow#userName} of the {@link scheduler.App#getCurrentUser()} if the {@link DataRowState} is {@link DataRowState#NEW}.
     */
    String getLastModifiedBy();
    
    /**
     * Gets the {@link DataRowState} for the current object.
     * 
     * @return The {@link DataRowState} for the current object.
     */
    DataRowState getRowState();
}
