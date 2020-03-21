package scheduler.dao.dml;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import scheduler.dao.schema.DbColumn;

/**
 * Represents a list of table columns that can be joined to another list of table columns.
 * 
 * @author lerwi
 * @param <T> The type of {@link JoinedTableColumnList} that represents the joined list of table columns.
 * @param <E> The type of {@link ColumnReference} for table column references.
 */
public interface JoinableTableColumnList<T extends JoinedTableColumnList<T, E>, E extends ColumnReference> extends JoinableTable<T>,
        TableColumnList<E> {
    
    @Override
    public default boolean isColumnRefNameUsed(String name) {
        return null != name && !name.trim().isEmpty() && (stream().anyMatch((t) -> t.getName().equalsIgnoreCase(name)) ||
                getJoinedTables().stream().anyMatch((t) -> t.isColumnRefNameUsed(name)));
    }
    
    /**
     * Checks whether a column reference name is being used for any columns of the current list or in any related joins.
     * 
     * @param source A joined table to search from.
     * @param name The column reference name to search for.
     * @return {@code true} if the column reference {@code name} is being used for any columns of the current list or in any related joins;
     * otherwise, {@code false}.
     */
    public static boolean isColumnRefNameUsedGlobally(JoinableTableColumnList<?, ?> source, String name) {
        if (source instanceof JoinedTableColumnList)
            source = (JoinableTableColumnList<?, ?>)((JoinedTableColumnList<?, ?>)source).getPrimaryTable();
        return source.isColumnRefNameUsed(name);
    }
    
    @Override
    public default E get(String name) {
        if (null != name && !name.trim().isEmpty()) {
            Optional<E> result = stream().filter((t) -> t.getName().equalsIgnoreCase(name)).findFirst();
            if (!result.isPresent())
                result = getJoinedTables().stream().map((t) -> t.get(name)).filter((t) -> null != t).findFirst();
            if (result.isPresent())
                return result.get();
        }
        return null;
    }
    
    /**
     * Gets the {@link ColumnReference} that is referenced by a specified name within the current list or in related joins.
     * 
     * @param <E>
     * @param source A {@link JoinableTableColumnList} table to search from.
     * @param name The column reference name to search for.
     * @return The {@link ColumnReference} that is referenced by the specified {@code name} or {@code null} if no match was found within the
     * current list or in any related joins.
     */
    public static <E extends ColumnReference> E getGlobal(JoinableTableColumnList<?, E> source, String name) {
        if (source instanceof JoinedTableColumnList)
            source = (JoinableTableColumnList<?, E>)((JoinedTableColumnList<?, E>)source).getPrimaryTable();
        return source.get(name);
    }
    
    /**
     * Adds an INNER join to another table.
     * 
     * @param leftColumn The parent column from the current table.
     * @param rightColumn The child column from the target table.
     * @param tableAlias The alias that is used to reference the table for the new join.
     * If this is null, the default table alias will be used. If this is empty, then no alias will be used.
     * @param columnSelector A {@link Predicate} that determines what table column references will be added.
     * @param columnAliasMapper A {@link Function} that gets the column alias to use for each column initially added.
     * @return The {@link JoinedTableColumnList} that represents the appended join.
     */
    T innerJoin(DbColumn leftColumn, DbColumn rightColumn, String tableAlias, Predicate<DbColumn> columnSelector,
            Function<DbColumn, String> columnAliasMapper);

    /**
     * Adds an INNER join to another table.
     * 
     * @param leftColumn The parent column from the current table.
     * @param rightColumn The child column from the target table.
     * @param tableAlias The alias that is used to reference the table for the new join.
     * If this is null, the default table alias will be used. If this is empty, then no alias will be used.
     * @param columnSelector A {@link Predicate} that determines what table column references will be added.
     * @return The {@link JoinedTableColumnList} that represents the appended join.
     */
    default T innerJoin(DbColumn leftColumn, DbColumn rightColumn, String tableAlias, Predicate<DbColumn> columnSelector) {
        return leftJoin(leftColumn, rightColumn, tableAlias, columnSelector, null);
    }

    /**
     * Adds an INNER join to another table.
     * 
     * @param leftColumn The parent column from the current table.
     * @param rightColumn The child column from the target table.
     * @param columnSelector A {@link Predicate} that determines what table column references will be added.
     * @param columnAliasMapper A {@link Function} that gets the column alias to use for each column initially added.
     * @return The {@link JoinedTableColumnList} that represents the appended join.
     */
    default T innerJoin(DbColumn leftColumn, DbColumn rightColumn, Predicate<DbColumn> columnSelector,
            Function<DbColumn, String> columnAliasMapper) {
        return leftJoin(leftColumn, rightColumn, null, columnSelector, columnAliasMapper);
    }

    /**
     * Adds an INNER join to another table.
     * 
     * @param leftColumn The parent column from the current table.
     * @param rightColumn The child column from the target table.
     * @param tableAlias The alias that is used to reference the table for the new join.
     * If this is null, the default table alias will be used. If this is empty, then no alias will be used.
     * @param columnAliasMapper A {@link Function} that gets the column alias to use for each column initially added.
     * @return The {@link JoinedTableColumnList} that represents the appended join.
     */
    default T innerJoin(DbColumn leftColumn, DbColumn rightColumn, String tableAlias, Function<DbColumn, String> columnAliasMapper) {
        return leftJoin(leftColumn, rightColumn, tableAlias, null, columnAliasMapper);
    }

    /**
     * Adds an INNER join to another table.
     * 
     * @param leftColumn The parent column from the current table.
     * @param rightColumn The child column from the target table.
     * @param columnSelector A {@link Predicate} that determines what table column references will be added.
     * @return The {@link JoinedTableColumnList} that represents the appended join.
     */
    default T innerJoin(DbColumn leftColumn, DbColumn rightColumn, Predicate<DbColumn> columnSelector) {
        return leftJoin(leftColumn, rightColumn, null, columnSelector, null);
    }

    /**
     * Adds an INNER join to another table.
     * 
     * @param leftColumn The parent column from the current table.
     * @param rightColumn The child column from the target table.
     * @param columnAliasMapper A {@link Function} that gets the column alias to use for each column initially added.
     * @return The {@link JoinedTableColumnList} that represents the appended join.
     */
    default T innerJoin(DbColumn leftColumn, DbColumn rightColumn, Function<DbColumn, String> columnAliasMapper) {
        return leftJoin(leftColumn, rightColumn, null, null, columnAliasMapper);
    }

    @Override
    public default T innerJoin(DbColumn leftColumn, DbColumn rightColumn, String tableAlias) {
        return innerJoin(leftColumn, rightColumn, tableAlias, null, null);
    }

    /**
     * Adds a LEFT join to another table.
     * 
     * @param leftColumn The parent column from the current table.
     * @param rightColumn The child column from the target table.
     * @param tableAlias The alias that is used to reference the table for the new join.
     * If this is null, the default table alias will be used. If this is empty, then no alias will be used.
     * @param columnSelector A {@link Predicate} that determines what table column references will be added.
     * @param columnAliasMapper A {@link Function} that gets the column alias to use for each column initially added.
     * @return The {@link JoinedTableColumnList} that represents the appended join.
     */
    T leftJoin(DbColumn leftColumn, DbColumn rightColumn, String tableAlias, Predicate<DbColumn> columnSelector,
            Function<DbColumn, String> columnAliasMapper);

    /**
     * Adds a LEFT join to another table.
     * 
     * @param leftColumn The parent column from the current table.
     * @param rightColumn The child column from the target table.
     * @param tableAlias The alias that is used to reference the table for the new join.
     * If this is null, the default table alias will be used. If this is empty, then no alias will be used.
     * @param columnSelector A {@link Predicate} that determines what table column references will be added.
     * @return The {@link JoinedTableColumnList} that represents the appended join.
     */
    default T leftJoin(DbColumn leftColumn, DbColumn rightColumn, String tableAlias, Predicate<DbColumn> columnSelector) {
        return leftJoin(leftColumn, rightColumn, tableAlias, columnSelector, null);
    }

    /**
     * Adds a LEFT join to another table.
     * 
     * @param leftColumn The parent column from the current table.
     * @param rightColumn The child column from the target table.
     * @param columnSelector A {@link Predicate} that determines what table column references will be added.
     * @param columnAliasMapper A {@link Function} that gets the column alias to use for each column initially added.
     * @return The {@link JoinedTableColumnList} that represents the appended join.
     */
    default T leftJoin(DbColumn leftColumn, DbColumn rightColumn, Predicate<DbColumn> columnSelector,
            Function<DbColumn, String> columnAliasMapper) {
        return leftJoin(leftColumn, rightColumn, null, columnSelector, columnAliasMapper);
    }

    /**
     * Adds a LEFT join to another table.
     * 
     * @param leftColumn The parent column from the current table.
     * @param rightColumn The child column from the target table.
     * @param tableAlias The alias that is used to reference the table for the new join.
     * If this is null, the default table alias will be used. If this is empty, then no alias will be used.
     * @param columnAliasMapper A {@link Function} that gets the column alias to use for each column initially added.
     * @return The {@link JoinedTableColumnList} that represents the appended join.
     */
    default T leftJoin(DbColumn leftColumn, DbColumn rightColumn, String tableAlias, Function<DbColumn, String> columnAliasMapper) {
        return leftJoin(leftColumn, rightColumn, tableAlias, null, columnAliasMapper);
    }

    /**
     * Adds a LEFT join to another table.
     * 
     * @param leftColumn The parent column from the current table.
     * @param rightColumn The child column from the target table.
     * @param columnSelector A {@link Predicate} that determines what table column references will be added.
     * @return The {@link JoinedTableColumnList} that represents the appended join.
     */
    default T leftJoin(DbColumn leftColumn, DbColumn rightColumn, Predicate<DbColumn> columnSelector) {
        return leftJoin(leftColumn, rightColumn, null, columnSelector, null);
    }

    /**
     * Adds a LEFT join to another table.
     * 
     * @param leftColumn The parent column from the current table.
     * @param rightColumn The child column from the target table.
     * @param columnAliasMapper A {@link Function} that gets the column alias to use for each column initially added.
     * @return The {@link JoinedTableColumnList} that represents the appended join.
     */
    default T leftJoin(DbColumn leftColumn, DbColumn rightColumn, Function<DbColumn, String> columnAliasMapper) {
        return leftJoin(leftColumn, rightColumn, null, null, columnAliasMapper);
    }

    @Override
    public default T leftJoin(DbColumn leftColumn, DbColumn rightColumn, String tableAlias) {
        return leftJoin(leftColumn, rightColumn, tableAlias, null, null);
    }

    /**
     * Adds a RIGHT join to another table.
     * 
     * @param leftColumn The parent column from the current table.
     * @param rightColumn The child column from the target table.
     * @param tableAlias The alias that is used to reference the table for the new join.
     * If this is null, the default table alias will be used. If this is empty, then no alias will be used.
     * @param columnSelector A {@link Predicate} that determines what table column references will be added.
     * @param columnAliasMapper A {@link Function} that gets the column alias to use for each column initially added.
     * @return The {@link JoinedTableColumnList} that represents the appended join.
     */
    T rightJoin(DbColumn leftColumn, DbColumn rightColumn, String tableAlias, Predicate<DbColumn> columnSelector,
            Function<DbColumn, String> columnAliasMapper);

    /**
     * Adds a RIGHT join to another table.
     * 
     * @param leftColumn The parent column from the current table.
     * @param rightColumn The child column from the target table.
     * @param tableAlias The alias that is used to reference the table for the new join.
     * If this is null, the default table alias will be used. If this is empty, then no alias will be used.
     * @param columnSelector A {@link Predicate} that determines what table column references will be added.
     * @return The {@link JoinedTableColumnList} that represents the appended join.
     */
    default T rightJoin(DbColumn leftColumn, DbColumn rightColumn, String tableAlias, Predicate<DbColumn> columnSelector) {
        return rightJoin(leftColumn, rightColumn, tableAlias, columnSelector, null);
    }

    /**
     * Adds a RIGHT join to another table.
     * 
     * @param leftColumn The parent column from the current table.
     * @param rightColumn The child column from the target table.
     * @param columnSelector A {@link Predicate} that determines what table column references will be added.
     * @param columnAliasMapper A {@link Function} that gets the column alias to use for each column initially added.
     * @return The {@link JoinedTableColumnList} that represents the appended join.
     */
    default T rightJoin(DbColumn leftColumn, DbColumn rightColumn, Predicate<DbColumn> columnSelector,
            Function<DbColumn, String> columnAliasMapper) {
        return rightJoin(leftColumn, rightColumn, null, columnSelector, columnAliasMapper);
    }

    /**
     * Adds a RIGHT join to another table.
     * 
     * @param leftColumn The parent column from the current table.
     * @param rightColumn The child column from the target table.
     * @param tableAlias The alias that is used to reference the table for the new join.
     * If this is null, the default table alias will be used. If this is empty, then no alias will be used.
     * @param columnAliasMapper A {@link Function} that gets the column alias to use for each column initially added.
     * @return The {@link JoinedTableColumnList} that represents the appended join.
     */
    default T rightJoin(DbColumn leftColumn, DbColumn rightColumn, String tableAlias, Function<DbColumn, String> columnAliasMapper) {
        return rightJoin(leftColumn, rightColumn, tableAlias, null, columnAliasMapper);
    }

    /**
     * Adds a RIGHT join to another table.
     * 
     * @param leftColumn The parent column from the current table.
     * @param rightColumn The child column from the target table.
     * @param columnSelector A {@link Predicate} that determines what table column references will be added.
     * @return The {@link JoinedTableColumnList} that represents the appended join.
     */
    default T rightJoin(DbColumn leftColumn, DbColumn rightColumn, Predicate<DbColumn> columnSelector) {
        return rightJoin(leftColumn, rightColumn, null, columnSelector, null);
    }

    /**
     * Adds a RIGHT join to another table.
     * 
     * @param leftColumn The parent column from the current table.
     * @param rightColumn The child column from the target table.
     * @param columnAliasMapper A {@link Function} that gets the column alias to use for each column initially added.
     * @return The {@link JoinedTableColumnList} that represents the appended join.
     */
    default T rightJoin(DbColumn leftColumn, DbColumn rightColumn, Function<DbColumn, String> columnAliasMapper) {
        return rightJoin(leftColumn, rightColumn, null, null, columnAliasMapper);
    }

    @Override
    public default T rightJoin(DbColumn leftColumn, DbColumn rightColumn, String tableAlias) {
        return rightJoin(leftColumn, rightColumn, tableAlias, null, null);
    }

    /**
     * Adds a FULL join to another table.
     * 
     * @param leftColumn The parent column from the current table.
     * @param rightColumn The child column from the target table.
     * @param tableAlias The alias that is used to reference the table for the new join.
     * If this is null, the default table alias will be used. If this is empty, then no alias will be used.
     * @param columnSelector A {@link Predicate} that determines what table column references will be added.
     * @param columnAliasMapper A {@link Function} that gets the column alias to use for each column initially added.
     * @return The {@link JoinedTableColumnList} that represents the appended join.
     */
    T fullJoin(DbColumn leftColumn, DbColumn rightColumn, String tableAlias, Predicate<DbColumn> columnSelector,
            Function<DbColumn, String> columnAliasMapper);

    /**
     * Adds a FULL join to another table.
     * 
     * @param leftColumn The parent column from the current table.
     * @param rightColumn The child column from the target table.
     * @param tableAlias The alias that is used to reference the table for the new join.
     * If this is null, the default table alias will be used. If this is empty, then no alias will be used.
     * @param columnSelector A {@link Predicate} that determines what table column references will be added.
     * @return The {@link JoinedTableColumnList} that represents the appended join.
     */
    default T fullJoin(DbColumn leftColumn, DbColumn rightColumn, String tableAlias, Predicate<DbColumn> columnSelector) {
        return fullJoin(leftColumn, rightColumn, tableAlias, columnSelector, null);
    }

    /**
     * Adds a FULL join to another table.
     * 
     * @param leftColumn The parent column from the current table.
     * @param rightColumn The child column from the target table.
     * @param columnSelector A {@link Predicate} that determines what table column references will be added.
     * @param columnAliasMapper A {@link Function} that gets the column alias to use for each column initially added.
     * @return The {@link JoinedTableColumnList} that represents the appended join.
     */
    default T fullJoin(DbColumn leftColumn, DbColumn rightColumn, Predicate<DbColumn> columnSelector,
            Function<DbColumn, String> columnAliasMapper) {
        return fullJoin(leftColumn, rightColumn, null, columnSelector, columnAliasMapper);
    }

    /**
     * Adds a FULL join to another table.
     * 
     * @param leftColumn The parent column from the current table.
     * @param rightColumn The child column from the target table.
     * @param tableAlias The alias that is used to reference the table for the new join.
     * If this is null, the default table alias will be used. If this is empty, then no alias will be used.
     * @param columnAliasMapper A {@link Function} that gets the column alias to use for each column initially added.
     * @return The {@link JoinedTableColumnList} that represents the appended join.
     */
    default T fullJoin(DbColumn leftColumn, DbColumn rightColumn, String tableAlias, Function<DbColumn, String> columnAliasMapper) {
        return fullJoin(leftColumn, rightColumn, tableAlias, null, columnAliasMapper);
    }

    /**
     * Adds a FULL join to another table.
     * 
     * @param leftColumn The parent column from the current table.
     * @param rightColumn The child column from the target table.
     * @param columnSelector A {@link Predicate} that determines what table column references will be added.
     * @return The {@link JoinedTableColumnList} that represents the appended join.
     */
    default T fullJoin(DbColumn leftColumn, DbColumn rightColumn, Predicate<DbColumn> columnSelector) {
        return fullJoin(leftColumn, rightColumn, null, columnSelector, null);
    }

    /**
     * Adds a FULL join to another table.
     * 
     * @param leftColumn The parent column from the current table.
     * @param rightColumn The child column from the target table.
     * @param columnAliasMapper A {@link Function} that gets the column alias to use for each column initially added.
     * @return The {@link JoinedTableColumnList} that represents the appended join.
     */
    default T fullJoin(DbColumn leftColumn, DbColumn rightColumn, Function<DbColumn, String> columnAliasMapper) {
        return fullJoin(leftColumn, rightColumn, null, null, columnAliasMapper);
    }

    @Override
    public default T fullJoin(DbColumn leftColumn, DbColumn rightColumn, String tableAlias) {
        return fullJoin(leftColumn, rightColumn, tableAlias, null, null);
    }

}
