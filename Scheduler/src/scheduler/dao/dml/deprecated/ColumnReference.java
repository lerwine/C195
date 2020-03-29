package scheduler.dao.dml.deprecated;

import scheduler.dao.schema.DbColumn;

/**
 * A named reference to a {@link DbColumn}.
 * 
 * @author Leonard T. Erwine (Student ID 356334)
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
     */
    default String getName() {
        return getColumn().getDbName().toString();
    }
}
