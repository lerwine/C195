package scheduler.dao.filter;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
 */
public enum ComparisonOperator {
    EQUALS("=", false, false),
    EQUALS_CASE_INSENSITIVE(" LIKE ", false, true),
    NOT_EQUALS("<>", false, false),
    NOT_EQUALS_CASE_INSENSITIVE(" NOT LIKE ", false, true),
    GREATER_THAN(">", true, false),
    NOT_LESS_THAN(">=", true, false),
    LESS_THAN("<", true, false),
    NOT_GREATER_THAN("<=", true, false),
    STARTS_WITH(" LIKE ", true, true),
    ENDS_WITH(" LIKE ", true, true),
    CONTAINS(" LIKE ", true, true),
    LIKE(" LIKE ", true, true),
    NOT_LIKE(" NOT LIKE ", true, true);
    
    private final String sql;
    private final boolean relative;
    private final boolean stringOnly;

    public String getSql() {
        return sql;
    }

    public boolean isRelative() {
        return relative;
    }

    public boolean isStringOnly() {
        return stringOnly;
    }

    ComparisonOperator(String sql, boolean relative, boolean stringOnly) {
        this.sql = sql;
        this.relative = relative;
        this.stringOnly = stringOnly;
    }

    @Override
    public String toString() {
        return sql;
    }
    
    
}
