package scheduler.dao;

/**
 *
 * @author lerwi
 */
public enum TableJoinType {
    
    /**
     * Returns records that have matching values in both tables.
     */
    INNER("JOIN"),
    
    /**
     * Returns all records from the left table, and the matched records from the right table.
     */
    LEFT("LEFT JOIN"),

    /**
     * Returns all records from the right table, and the matched records from the left table.
     */
    RIGHT("RIGHT JOIN"),

    /**
     * Returns all records when there is a match in either left or right table.
     */
    FULL("FULL JOIN");
    
    private final String sql;
    
    TableJoinType(String sql) {
        this.sql = sql;
    }

    @Override
    public String toString() {
        return sql;
    }

}
