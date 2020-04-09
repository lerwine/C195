package scheduler.dao.schema;

import java.sql.JDBCType;
import scheduler.util.ReadOnlyList;

/**
 * Data types for reading and writing database values.
 * <p>
 * The {@link ColumnType} enumeration uses this to specify the Java type that is corresponds to the database column type.</p>
 *
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
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
     * Gets the primary JDBC data type to use with this type.
     *
     * @return The primary {@code JDBCType}.
     */
    public JDBCType getPrimaryType() {
        return primaryType;
    }

    /**
     * Gets alternate JDBC data types that can be used with this type.
     *
     * @return Alternate {@code JDBCType}s.
     */
    public ReadOnlyList<JDBCType> getAlternateTypes() {
        return alternateTypes;
    }

}
