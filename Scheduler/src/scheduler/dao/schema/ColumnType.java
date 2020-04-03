package scheduler.dao.schema;

/**
 * Supported database column types.
 * <p>
 * This reflects database schema type compatibility. Refer to {@link ValueType} for Java type compatibility.</p>
 *
 * @author Leonard T. Erwine (Student ID 356334)
 */
public enum ColumnType {
    /**
     * An auto-incrementing data column.
     */
    AUTO_INCREMENT(ValueType.INT),
    /**
     * An integer data column
     */
    INT(ValueType.INT),
    /**
     * A string data column.
     */
    VARCHAR(ValueType.STRING),
    /**
     * A long text data column.
     */
    TEXT(ValueType.STRING),
    /**
     * A data column that contains a cryptographic hash string.
     */
    VARCHAR_PWD_HASH(ValueType.STRING),
    /**
     * A data column that contains a short integer value.
     */
    TINYINT(ValueType.INT),
    /**
     * A data column that contains a tiny integer value that is used as a boolean value.
     */
    TINYINT_BOOLEAN(ValueType.BOOLEAN),
    /**
     * A datetime data column.
     */
    DATETIME(ValueType.TIMESTAMP),
    /**
     * A Timestamp data column.
     */
    TIMESTAMP(ValueType.TIMESTAMP);

    private final ValueType valueType;

    private ColumnType(ValueType valueType) {
        this.valueType = valueType;
    }

    /**
     * Gets the Java data type compatibility for the current column type.
     *
     * @return A {@link ValueType} that describes the corresponding Java type.
     */
    public ValueType getValueType() {
        return valueType;
    }
}
