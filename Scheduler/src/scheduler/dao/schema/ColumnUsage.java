package scheduler.dao.schema;

/**
 * Specifies a column usage type.
 * 
 * @author Leonard T. Erwine (Student ID 356334)
 */
public enum ColumnUsage {
    /**
     * Primary key column.
     */
    PRIMARY_KEY,
    /**
     * A column used in a foreign-key relationship.
     */
    FOREIGN_KEY,
    /**
     * A column that is intended to store a unique value.
     */
    UNIQUE_KEY,
    /**
     * A column used in tracking data row creation and modification.
     */
    AUDIT,
    /**
     * A column that stores a cryptographic hash.
     */
    CRYPTO_HASH,
    /**
     * A general table data column.
     */
    DATA
}
