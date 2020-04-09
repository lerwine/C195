package scheduler.dao.schema;

/**
 * A {@link DbColumn} category that indicates how it data column is used in the application.
 * 
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
 */
public enum ColumnCategory {
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
     * An entity data column.
     */
    DATA
}
