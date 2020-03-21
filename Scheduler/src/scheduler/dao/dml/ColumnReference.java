package scheduler.dao.dml;

import scheduler.dao.schema.DbColumn;

/**
 * A named reference to a {@link DbColumn}.
 * 
 * @author lerwi
 */
public interface ColumnReference {
    
    /**
     * Gets the target {@link DbColumn} that value.
     * This should never be null.
     * 
     * @return The {@link DbColumn} that this refers to.
     */
    DbColumn getColumn();
    
    /**
     * Gets the name that refers to the target {@link DbColumn}.
     * This value should never be null, empty or completely whitespace.
     * This is the same as {@link DbColumn#getDbName()} unless overridden.
     * 
     * @return The name that is used to reference the target {@link DbColumn} value.
     * @see {@link #getColumn()}
     */
    default String getName() {
        return getColumn().getDbName();
    }
}
