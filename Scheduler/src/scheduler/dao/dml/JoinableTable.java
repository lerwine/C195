package scheduler.dao.dml;

import scheduler.dao.schema.DbColumn;
import scheduler.dao.schema.DbTable;
import scheduler.util.ReadOnlyList;

/**
 * Represents a table reference that can be joined to other table references.
 * 
 * @author Leonard T. Erwine (Student ID 356334)
 * @param <T> The type of {@link JoinedTable} object to represent the joined table.
 */
public interface JoinableTable<T extends JoinedTable<? extends T>> extends TableReference {
    
    /**
     * Gets the {@link JoinedTable}s that are directly joined to the current table.
     * 
     * @return The {@link ReadOnlyList} of {@link JoinedTable}s that are directly joined to the current table.
     */
    ReadOnlyList<T> getJoinedTables();
    

    /**
     * Indicates whether any {@link JoinableTable} are referenced by the specified name.
     * @param name The name to search for.
     * @return {@code true} if any {@link JoinableTable} is referenced by the specified {@code name}; otherwise, false.
     */
    default boolean isTableRefNameUsed(String name) {
        return null != name && !name.trim().isEmpty() && (getName().equalsIgnoreCase(name) || 
                getJoinedTables().stream().anyMatch((t) -> t.isTableRefNameUsed(name)));
    }
    
    public static boolean isTableRefNameUsedGlobally(JoinableTable<?> source, String name) {
        if (source instanceof JoinedTable)
            source = (JoinableTable<?>)((JoinedTable<?>)source).getPrimaryTable();
        return source.isTableRefNameUsed(name);
    }
    
    /**
     * Gets the {@link JoinableTable} that is referenced by the specified name.
     * @param referenceName The name to search for.
     * @return The {@link JoinableTable} that is referenced by the specified name or {@code null} if no match was found.
     */
    default JoinableTable<? extends T> getTable(String referenceName) {
        if (getName().equalsIgnoreCase(referenceName))
            return this;
        
        for (T t : getJoinedTables()) {
            JoinableTable<? extends T> result = t.getTable(referenceName);
            if (null != result)
                return result;
        }
        
        return null;
    }
    
    public static <T extends JoinedTable<? extends T>> JoinableTable<? extends T> getTableGlobal(JoinableTable<? extends T> source, String name) {
        if (source instanceof JoinedTable)
            source = ((JoinedTable<? extends T>)source).getPrimaryTable();
        return source.getTable(name);
    }

    public static <T extends JoinedTable<? extends T>> JoinableTable<? extends T> findFirstGlobally(JoinableTable<? extends T> source, DbTable tableName) {
        if (source instanceof JoinedTable)
            source = ((JoinedTable<? extends T>)source).getPrimaryTable();
        return source.findFirst(tableName);
    }

    /**
     * Indicates whether a specified {@link DbTable} is referenced by an joined tables.
     * 
     * @param tableName The {@link DbTable} to look for.
     * @return {@code true} if any {@link JoinableTable} references the specified {@code tableName}; otherwise, false.
     */
    default boolean isTableReferenced(DbTable tableName) {
        return null != tableName && (getTableName() == tableName || getJoinedTables().stream().anyMatch((t) -> t.getTableName() == tableName));
    }
    
    public static boolean isTableReferencedGlobally(JoinableTable<?> source, DbTable tableName) {
        if (source instanceof JoinedTable)
            source = (JoinableTable<?>)((JoinedTable<?>)source).getPrimaryTable();
        return source.isTableReferenced(tableName);
    }

    /**
     * Finds the first {@link JoinableTable} that references the specified {@link DbTable}.
     * 
     * @param tableName The {@link DbTable} to look for.
     * @return The first {@link JoinableTable} that references the specified {@code tableName}.
     */
    default JoinableTable<? extends T> findFirst(DbTable tableName) {
        if (getTableName() == tableName)
            return this;
        
        for (T t : getJoinedTables()) {
            JoinableTable<? extends T> result = t.findFirst(tableName);
            if (null != result)
                return result;
        }
        
        return null;
    }
    
    /**
     * Adds an INNER join to another table.
     * 
     * @param leftColumn The parent column from the current table.
     * @param rightColumn The child column from the target table.
     * @param tableAlias The alias that is used to reference the table for the new join.
     * If this is null, the default table alias will be used. If this is empty, then no alias will be used.
     * @return The {@link JoinedTable} that represents the appended join.
     */
    T innerJoin(DbColumn leftColumn, DbColumn rightColumn, String tableAlias);

    /**
     * Adds an INNER join to another table.
     * 
     * @param leftColumn The parent column from the current table.
     * @param rightColumn The child column from the target table.
     * @return The {@link JoinedTable} that represents the appended join.
     */
    default T innerJoin(DbColumn leftColumn, DbColumn rightColumn) {
        return innerJoin(leftColumn, rightColumn, null);
    }

    /**
     * Adds a LEFT join to another table.
     * 
     * @param leftColumn The parent column from the current table.
     * @param rightColumn The child column from the target table.
     * @param tableAlias The alias that is used to reference the table for the new join.
     * If this is null, the default table alias will be used. If this is empty, then no alias will be used.
     * @return The {@link JoinedTable} that represents the appended join.
     */
    T leftJoin(DbColumn leftColumn, DbColumn rightColumn, String tableAlias);

    /**
     * Adds a LEFT join to another table.
     * 
     * @param leftColumn The parent column from the current table.
     * @param rightColumn The child column from the target table.
     * @return The {@link JoinedTable} that represents the appended join.
     */
    default T leftJoin(DbColumn leftColumn, DbColumn rightColumn) {
        return leftJoin(leftColumn, rightColumn, null);
    }

    /**
     * Adds a RIGHT join to another table.
     * 
     * @param leftColumn The parent column from the current table.
     * @param rightColumn The child column from the target table.
     * @param tableAlias The alias that is used to reference the table for the new join.
     * If this is null, the default table alias will be used. If this is empty, then no alias will be used.
     * @return The {@link JoinedTable} that represents the appended join.
     */
    T rightJoin(DbColumn leftColumn, DbColumn rightColumn, String tableAlias);

    /**
     * Adds a RIGHT join to another table.
     * 
     * @param leftColumn The parent column from the current table.
     * @param rightColumn The child column from the target table.
     * @return The {@link JoinedTable} that represents the appended join.
     */
    default T rightJoin(DbColumn leftColumn, DbColumn rightColumn) {
        return rightJoin(leftColumn, rightColumn, null);
    }

    /**
     * Adds a FULL join to another table.
     * 
     * @param leftColumn The parent column from the current table.
     * @param rightColumn The child column from the target table.
     * @param tableAlias The alias that is used to reference the table for the new join.
     * If this is null, the default table alias will be used. If this is empty, then no alias will be used.
     * @return The {@link JoinedTable} that represents the appended join.
     */
    T fullJoin(DbColumn leftColumn, DbColumn rightColumn, String tableAlias);

    /**
     * Adds a FULL join to another table.
     * 
     * @param leftColumn The parent column from the current table.
     * @param rightColumn The child column from the target table.
     * @return The {@link JoinedTable} that represents the appended join.
     */
    default T fullJoin(DbColumn leftColumn, DbColumn rightColumn) {
        return fullJoin(leftColumn, rightColumn, null);
    }

}
