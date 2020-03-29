package scheduler.dao.dml.deprecated;

import scheduler.dao.schema.DbTable;

/**
 * A {@link DbTable} that can reference by an alias ({@link TableReference#getTableAlias()}).
 * <p>Derived interfaces:</p>
 * <ul>
 *  <li>{@link JoinableTable} &mdash; A <strong>TableReference</strong> with foreign key relationships to other {@link JoinedTable}s ({@link JoinableTable#getJoinedTables()}).
 *      <ul>
 *          <li>{@link JoinedTable} &mdash; A <strong>TableReference</strong> that is joined to another {@link JoinableTable} ({@link TableJoinType}, {@link JoinedTable#getParentTable()}).
*               <ul>
*                   <li>{@link JoinedTable} + {@link JoinableTableColumnList} &rArr; {@link JoinedTableColumnList}</li>
*               </ul>
 *          </li>
 *          <li>{@link JoinableTable} + {@link TableColumnList} &rArr; {@link JoinableTableColumnList}
*               <ul>
*                   <li>{@link JoinableTableColumnList} + {@link JoinedTable} &rArr; {@link JoinedTableColumnList}</li>
*               </ul>
*           </li>
 *      </ul>
 *  </li>
 *  <li>{@link TableColumnList} - A <strong>TableReference</strong> with columns for SELECT, INSERT or UPDATE.
 *      <ul>
 *          <li>{@link TableColumnList} + {@link JoinableTable} &rArr; {@link JoinableTableColumnList}
*               <ul>
*                   <li>{@link JoinableTableColumnList} + {@link JoinedTable} &rArr; {@link JoinedTableColumnList}</li>
*               </ul>
*           </li>
 *      </ul>
 *  </li>
 * </ul>
 * @author Leonard T. Erwine (Student ID 356334)
 */
public interface TableReference {

    /**
     * Gets the target {@link DbTable} value.
     * This should never be null.
     * 
     * @return The {@link DbTable} that this refers to.
     */
    DbTable getTable();
 
    /**
     * Gets the name that refers to the target {@link DbTable}.
     * This value should never be null, empty or completely whitespace.
     * This is the same as {@link DbTable#getDbName()} unless overridden.
     * 
     * @return The name that is used to reference the target {@link DbTable} value.
     */
    default String getTableAlias() {
        return getTable().getDbName().toString();
    }
}
