package scheduler.dao.schema;

import java.sql.JDBCType;
import scheduler.util.ReadOnlyList;

/**
 * Data types for reading and writing database values. The {@link ColumnType} enumeration uses this to map a value type to the respective database
 * column type.
 *
 * @author lerwi
 */
public enum ValueType {
    /**
     * {@link Integer} data type.
     */
    INT(JDBCType.INTEGER, JDBCType.TINYINT),
    /**
     * {@link String} data type.
     */
    STRING(JDBCType.VARCHAR, JDBCType.LONGVARCHAR),
    /**
     * {@link Boolean} data type.
     */
    BOOLEAN(JDBCType.BOOLEAN, JDBCType.TINYINT),
    /**
     * {@link java.sql.Timestamp} data type.
     */
    TIMESTAMP(JDBCType.TIMESTAMP);

    private final JDBCType primaryType;

    private final ReadOnlyList<JDBCType> alternateTypes;

    private ValueType(JDBCType primaryType, JDBCType... alternateTypes) {
        this.primaryType = primaryType;
        this.alternateTypes = (null == alternateTypes) ? ReadOnlyList.empty() : ReadOnlyList.of(alternateTypes);
    }

    /**
     * Gets the primary associated JDBC data type.
     *
     * @return The primary {@code JDBCType}.
     */
    public JDBCType getPrimaryType() {
        return primaryType;
    }

    /**
     * Gets alternate associated JDBC data types.
     *
     * @return Alternate {@code JDBCType}s.
     */
    public ReadOnlyList<JDBCType> getAlternateTypes() {
        return alternateTypes;
    }

}
