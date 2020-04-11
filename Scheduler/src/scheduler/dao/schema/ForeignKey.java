package scheduler.dao.schema;

/**
 * Represents a foreign key relationship to a {@link DbTable}.
 * 
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class ForeignKey {
    private final String name;
    private final DbTable table;
    private final DbName columnName;
    
    /**
     * Gets the name of the relationship.
     * 
     * @return The database name of the relationship.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the reference {@link DbTable}.
     * 
     * @return The reference {@link DbTable}.
     */
    public DbTable getTable() {
        return table;
    }

    /**
     * Gets the name referenced column.
     * 
     * @return The column that forms the relationship to the {@link DbTable}.
     */
    public DbName getColumnName() {
        return columnName;
    }
    
    ForeignKey(String name, DbTable table, DbName columnName) {
        this.name = name;
        this.table = table;
        this.columnName = columnName;
    }
}
