package scheduler.model;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
 */
public class SelectStatement<T extends IDataRow> {
    private final SelectStatementTable<T> table;
    public SelectStatement(SelectStatementTable<T> table) {
        this.table = table;
    }
    private SelectStatementTable<? extends IDataRow> findTable(JoinedSelectTable<? extends IDataRow> current, String name) {
        
        if (current.getTableName().equalsIgnoreCase(name))
            return current;
        if (current instanceof )
    }
    public final SelectStatementTable<? extends IDataRow> findTable(String name) {
        if (table.getTableName().equalsIgnoreCase(name))
            return table;
        if (table instanceof JoinedSelectTable)
            return findTable((JoinedSelectTable<? extends IDataRow>) table, name);
        return null;
    }
}
