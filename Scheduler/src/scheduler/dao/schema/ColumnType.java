package scheduler.dao.schema;

/**
 * Supported database column types.
 * This is used to indicate the types for each value of the {@link DbColumn} enumeration.
 * 
 * @author lerwi
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

    /**
     * Gets the data type for the current column type.
     * 
     * @return A {@link ValueType} that describes the column data type.
     */
    public ValueType getValueType() {
        return valueType;
    }
    
    private ColumnType(ValueType valueType) {
        this.valueType = valueType;
    }
}
