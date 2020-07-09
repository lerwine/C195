package scheduler.model;

import scheduler.dao.DataRowState;
import scheduler.util.ToStringPropertyBuilder;

/**
 * Base interface for all objects that represent a database entity.
 */
public interface PartialDataEntity {

    /**
     * The name of the 'primaryKey' property.
     */
    public static final String PROP_PRIMARYKEY = "primaryKey";
    /**
     * The name of the 'rowState' property.
     */
    public static final String PROP_ROWSTATE = "rowState";

    /**
     * Gets the value of the primary key for the current data object.
     *
     * @return The unique primary key value for the current data object.
     */
    int getPrimaryKey();

    ToStringPropertyBuilder toStringBuilder();

    default DataRowState getRowState() {
        return DataRowState.UNMODIFIED;
    }
}
