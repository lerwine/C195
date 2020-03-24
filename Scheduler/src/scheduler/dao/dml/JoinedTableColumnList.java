package scheduler.dao.dml;

/**
 * Represents a list of table columns that has been joined to another.
 * 
 * @author Leonard T. Erwine (Student ID 356334)
 * @param <T> The type of object that represents other joined table column lists.
 * @param <E> The type of {@link ColumnReference} that references the columns in the list. 
 */
public interface JoinedTableColumnList<T extends JoinedTableColumnList<T, E>, E extends ColumnReference> extends JoinedTable<T>,
        JoinableTableColumnList<T, E> {
    
}
