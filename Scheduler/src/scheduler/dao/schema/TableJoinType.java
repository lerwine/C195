package scheduler.dao.schema;

/**
 * SQL query table join types.
 * 
 * @author Leonard T. Erwine (Student ID 356334)
 */
public enum TableJoinType {
    INNER("JOIN"),  
    LEFT("LEFT JOIN"),
    RIGHT("RIGHT JOIN"),
    FULL("FULL JOIN");
    
    private final String sql;
    private TableJoinType(String sql) {
        this.sql = sql;
    }

    @Override
    public String toString() {
        return sql;
    }
    
}
