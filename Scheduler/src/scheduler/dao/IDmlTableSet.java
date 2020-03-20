package scheduler.dao;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 *
 * @author lerwi
 */
public interface IDmlTableSet {

    TableName getTableName();
    
    String getTableAlias();

    List<DmlTableSet.JoinedTable> getTableJoins();

    List<DmlTableSet.DmlColumn> getDmlColumns();

    default String getColumnAlias(DbColumn column) {
        if (null != column) {
            Optional<DmlTableSet.DmlColumn> col = getDmlColumns().stream().filter((t) -> t.getColumn() == column).findFirst();
            if (col.isPresent())
                return col.get().getAlias();
            Optional<String> result = getTableJoins().stream().map((t) -> t.getColumnAlias(column)).filter((t) -> null != t).findFirst();
            if (result.isPresent()) {
                return result.get();
            }
        }
        return null;
    }
    
    default DmlTableSet.DmlColumn findDmlColumn(String alias) {
        if (null != alias && !alias.trim().isEmpty()) {
            DmlTableSet.DmlColumn col = ((IList<DmlTableSet.DmlColumn>) getDmlColumns()).getByAlias(alias);
            if (null != col) {
                return col;
            }
            Optional<DmlTableSet.DmlColumn> result = getTableJoins().stream().map((t) ->
                    t.findDmlColumn(alias)).filter((t) -> null != t).findFirst();
            if (result.isPresent()) {
                return result.get();
            }
        }
        return null;
    }

    default IDmlTableSet findTableSet(String alias) {
        if (null != alias && !alias.trim().isEmpty()) {
            if (alias.equalsIgnoreCase(getTableAlias())) {
                return this;
            }
            Optional<IDmlTableSet> result = getTableJoins().stream().map((t) -> t.findTableSet(alias)).filter((t) -> null != t).findFirst();
            if (result.isPresent()) {
                return result.get();
            }
        }
        return null;
    }

    default IDmlTableSet findTableSet(TableName tableName) {
        if (null != tableName) {
            if (tableName == getTableName()) {
                return this;
            }
            Optional<IDmlTableSet> result = getTableJoins().stream().map((t) -> t.findTableSet(tableName)).filter((t) -> null != t).findFirst();
            if (result.isPresent()) {
                return result.get();
            }
        }
        return null;
    }

    default boolean tableAliasExists(String alias) {
        return null != alias && !alias.trim().isEmpty() && (alias.equalsIgnoreCase(getTableAlias())
                || getTableJoins().stream().anyMatch((t) -> t.tableAliasExists(alias)));
    }
    
    default boolean columnAliasExists(String alias) {
        return null != alias && !alias.trim().isEmpty() && (getDmlColumns().stream().anyMatch((t) -> t.getAlias().equalsIgnoreCase(alias))
                || getTableJoins().stream().anyMatch((t) -> t.columnAliasExists(alias)));
    }

    DmlTableSet.JoinedTable innerJoin(DbColumn leftColumn, DbColumn rightColumn, String tableAlias, Predicate<DbColumn> columnSelector,
            Function<DbColumn, String> columnAliasMapper);

    default DmlTableSet.JoinedTable innerJoin(DbColumn leftColumn, DbColumn rightColumn, String tableAlias, Predicate<DbColumn> columnSelector) {
        return innerJoin(leftColumn, rightColumn, tableAlias, columnSelector, null);
    }

    default DmlTableSet.JoinedTable innerJoin(DbColumn leftColumn, DbColumn rightColumn, Predicate<DbColumn> columnSelector,
            Function<DbColumn, String> columnAliasMapper) {
        return innerJoin(leftColumn, rightColumn, null, columnSelector, columnAliasMapper);
    }

    default DmlTableSet.JoinedTable innerJoin(DbColumn leftColumn, DbColumn rightColumn, String tableAlias, Function<DbColumn, String> columnAliasMapper) {
        return innerJoin(leftColumn, rightColumn, tableAlias, null, columnAliasMapper);
    }

    default DmlTableSet.JoinedTable innerJoin(DbColumn leftColumn, DbColumn rightColumn, String tableAlias) {
        return innerJoin(leftColumn, rightColumn, tableAlias, null, null);
    }

    default DmlTableSet.JoinedTable innerJoin(DbColumn leftColumn, DbColumn rightColumn, Predicate<DbColumn> columnSelector) {
        return innerJoin(leftColumn, rightColumn, null, columnSelector, null);
    }

    default DmlTableSet.JoinedTable innerJoin(DbColumn leftColumn, DbColumn rightColumn, Function<DbColumn, String> columnAliasMapper) {
        return innerJoin(leftColumn, rightColumn, null, null, columnAliasMapper);
    }

    default DmlTableSet.JoinedTable innerJoin(DbColumn leftColumn, DbColumn rightColumn) {
        return innerJoin(leftColumn, rightColumn, null, null, null);
    }

    DmlTableSet.JoinedTable leftJoin(DbColumn leftColumn, DbColumn rightColumn, String tableAlias, Predicate<DbColumn> columnSelector,
            Function<DbColumn, String> columnAliasMapper);

    default DmlTableSet.JoinedTable leftJoin(DbColumn leftColumn, DbColumn rightColumn, String tableAlias, Predicate<DbColumn> columnSelector) {
        return leftJoin(leftColumn, rightColumn, tableAlias, columnSelector, null);
    }

    default DmlTableSet.JoinedTable leftJoin(DbColumn leftColumn, DbColumn rightColumn, Predicate<DbColumn> columnSelector,
            Function<DbColumn, String> columnAliasMapper) {
        return leftJoin(leftColumn, rightColumn, null, columnSelector, columnAliasMapper);
    }

    default DmlTableSet.JoinedTable leftJoin(DbColumn leftColumn, DbColumn rightColumn, String tableAlias, Function<DbColumn, String> columnAliasMapper) {
        return leftJoin(leftColumn, rightColumn, tableAlias, null, columnAliasMapper);
    }

    default DmlTableSet.JoinedTable leftJoin(DbColumn leftColumn, DbColumn rightColumn, String tableAlias) {
        return leftJoin(leftColumn, rightColumn, tableAlias, null, null);
    }

    default DmlTableSet.JoinedTable leftJoin(DbColumn leftColumn, DbColumn rightColumn, Predicate<DbColumn> columnSelector) {
        return leftJoin(leftColumn, rightColumn, null, columnSelector, null);
    }

    default DmlTableSet.JoinedTable leftJoin(DbColumn leftColumn, DbColumn rightColumn, Function<DbColumn, String> columnAliasMapper) {
        return leftJoin(leftColumn, rightColumn, null, null, columnAliasMapper);
    }

    default DmlTableSet.JoinedTable leftJoin(DbColumn leftColumn, DbColumn rightColumn) {
        return leftJoin(leftColumn, rightColumn, null, null, null);
    }

    DmlTableSet.JoinedTable rightJoin(DbColumn leftColumn, DbColumn rightColumn, String tableAlias, Predicate<DbColumn> columnSelector,
            Function<DbColumn, String> columnAliasMapper);

    default DmlTableSet.JoinedTable rightJoin(DbColumn leftColumn, DbColumn rightColumn, String tableAlias, Predicate<DbColumn> columnSelector) {
        return rightJoin(leftColumn, rightColumn, tableAlias, columnSelector, null);
    }

    default DmlTableSet.JoinedTable rightJoin(DbColumn leftColumn, DbColumn rightColumn, Predicate<DbColumn> columnSelector,
            Function<DbColumn, String> columnAliasMapper) {
        return rightJoin(leftColumn, rightColumn, null, columnSelector, columnAliasMapper);
    }

    default DmlTableSet.JoinedTable rightJoin(DbColumn leftColumn, DbColumn rightColumn, String tableAlias, Function<DbColumn, String> columnAliasMapper) {
        return rightJoin(leftColumn, rightColumn, tableAlias, null, columnAliasMapper);
    }

    default DmlTableSet.JoinedTable rightJoin(DbColumn leftColumn, DbColumn rightColumn, String tableAlias) {
        return rightJoin(leftColumn, rightColumn, tableAlias, null, null);
    }

    default DmlTableSet.JoinedTable rightJoin(DbColumn leftColumn, DbColumn rightColumn, Predicate<DbColumn> columnSelector) {
        return rightJoin(leftColumn, rightColumn, null, columnSelector, null);
    }

    default DmlTableSet.JoinedTable rightJoin(DbColumn leftColumn, DbColumn rightColumn, Function<DbColumn, String> columnAliasMapper) {
        return rightJoin(leftColumn, rightColumn, null, null, columnAliasMapper);
    }

    default DmlTableSet.JoinedTable rightJoin(DbColumn leftColumn, DbColumn rightColumn) {
        return rightJoin(leftColumn, rightColumn, null, null, null);
    }

    DmlTableSet.JoinedTable fullJoin(DbColumn leftColumn, DbColumn rightColumn, String tableAlias, Predicate<DbColumn> columnSelector,
            Function<DbColumn, String> columnAliasMapper);

    default DmlTableSet.JoinedTable fullJoin(DbColumn leftColumn, DbColumn rightColumn, String tableAlias, Predicate<DbColumn> columnSelector) {
        return fullJoin(leftColumn, rightColumn, tableAlias, columnSelector, null);
    }

    default DmlTableSet.JoinedTable fullJoin(DbColumn leftColumn, DbColumn rightColumn, Predicate<DbColumn> columnSelector,
            Function<DbColumn, String> columnAliasMapper) {
        return fullJoin(leftColumn, rightColumn, null, columnSelector, columnAliasMapper);
    }

    default DmlTableSet.JoinedTable fullJoin(DbColumn leftColumn, DbColumn rightColumn, String tableAlias, Function<DbColumn, String> columnAliasMapper) {
        return fullJoin(leftColumn, rightColumn, tableAlias, null, columnAliasMapper);
    }

    default DmlTableSet.JoinedTable fullJoin(DbColumn leftColumn, DbColumn rightColumn, String tableAlias) {
        return fullJoin(leftColumn, rightColumn, tableAlias, null, null);
    }

    default DmlTableSet.JoinedTable fullJoin(DbColumn leftColumn, DbColumn rightColumn, Predicate<DbColumn> columnSelector) {
        return fullJoin(leftColumn, rightColumn, null, columnSelector, null);
    }

    default DmlTableSet.JoinedTable fullJoin(DbColumn leftColumn, DbColumn rightColumn, Function<DbColumn, String> columnAliasMapper) {
        return fullJoin(leftColumn, rightColumn, null, null, columnAliasMapper);
    }

    default DmlTableSet.JoinedTable fullJoin(DbColumn leftColumn, DbColumn rightColumn) {
        return fullJoin(leftColumn, rightColumn, null, null, null);
    }

    interface IList<T> extends List<T> {

        T getByAlias(String alias);

        boolean aliasExists(String alias);

        @Override
        public default <T> T[] toArray(T[] a) {
            Object[] elementData = toArray();
            if (a.length < elementData.length) // Make a new array of a's runtime type, but my contents:
            {
                return (T[]) Arrays.copyOf(elementData, elementData.length, a.getClass());
            }
            System.arraycopy(elementData, 0, a, 0, elementData.length);
            if (a.length > elementData.length) {
                a[elementData.length] = null;
            }
            return a;
        }

        @Override
        public default boolean add(T e) {
            throw new UnsupportedOperationException();
        }

        @Override
        public default boolean addAll(Collection<? extends T> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public default boolean addAll(int index, Collection<? extends T> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public default T set(int index, T element) {
            throw new UnsupportedOperationException();
        }

        @Override
        public default void add(int index, T element) {
            throw new UnsupportedOperationException();
        }

    }

}
