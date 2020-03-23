package scheduler.dao.dml;

import scheduler.dao.schema.DbTable;

/**
 * A named reference to a {@link DbTable}.
 * 
 * @author lerwi
 */
public interface TableReference {

    /**
     * Gets the target {@link DbTable} value.
     * This should never be null.
     * 
     * @return The {@link DbTable} that this refers to.
     */
    DbTable getTableName();
 
    /**
     * Gets the name that refers to the target {@link DbTable}.
     * This value should never be null, empty or completely whitespace.
     * This is the same as {@link DbTable#getDbName()} unless overridden.
     * 
     * @return The name that is used to reference the target {@link DbTable} value.
     */
    default String getName() {
        return getTableName().getDbName().getValue();
    }
}
