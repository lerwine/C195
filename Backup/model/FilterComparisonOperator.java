package scheduler.model;

import java.util.Optional;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
 */
public enum FilterComparisonOperator {
    EQUAL_TO("=", true, Optional.empty(), false),
    NOT_EQUAL_TO("<>", true, Optional.empty(), false),
    LESS_THAN("<", true, Optional.empty(), false),
    NOT_LESS_THAN(">=", true, Optional.empty(), false),
    GREATER_THAN(">", true, Optional.empty(), false),
    NOT_GREATER_THAN("<=", true, Optional.empty(), false),
    STARTS_WITH("LIKE", true, Optional.of(false), true),
    ENDS_WITH("LIKE", true, Optional.of(true), false),
    CONTAINS("LIKE", true, Optional.of(true), true),
    LIKE("LIKE", true, Optional.of(false), false),
    NULL("IS NULL", false, Optional.empty(), false),
    NOT_NULL("IS NOT NULL", false, Optional.empty(), false);

    public static boolean isWithPlaceHolder(FilterComparisonOperator op) {
        return op.withPlaceHolder;
    }

    public static boolean isStringOnly(FilterComparisonOperator op) {
        return op.valuePrepend.isPresent();
    }

    public static boolean supportsBoolean(FilterComparisonOperator op) {
        switch (op) {
            case EQUAL_TO:
            case NOT_EQUAL_TO:
            case NULL:
            case NOT_NULL:
                return true;
        }
        return false;
    }
    
    public static void appendSubClauseSql(StringBuilder sql, String tableName, String colName, FilterComparisonOperator op) {
        sql.append("`");
        if (null != tableName && !tableName.isEmpty()) {
            sql.append(tableName).append("`.`");
        }
        sql.append(colName).append("` ").append(op.sql);
        if (op.withPlaceHolder)
            sql.append(" ?");
    }

    public static String toStringParam(String value, FilterComparisonOperator op, String escape) {
        if (null != value && op.valuePrepend.isPresent()) {
            if (op.valuePrepend.get()) {
                if (op.valueAppend) {
                    return "%" + value.replace("%", escape + "%").replace("_", escape + "_") + "%";
                }
                return "%" + value.replace("%", escape + "%").replace("_", escape + "_");
            }
            if (op.valueAppend) {
                return value.replace("%", escape + "%").replace("_", escape + "_") + "%";
            }
        }
        return value;
    }
    private final String sql;
    private final boolean withPlaceHolder;
    private final Optional<Boolean> valuePrepend;
    private final boolean valueAppend;

    FilterComparisonOperator(String sql, boolean withPlaceHolder, Optional<Boolean> valuePrepend, boolean valueAppend) {
        this.sql = sql;
        this.withPlaceHolder = withPlaceHolder;
        this.valuePrepend = valuePrepend;
        this.valueAppend = valueAppend;
    }

    @Override
    public String toString() {
        return sql;
    }
    
}
