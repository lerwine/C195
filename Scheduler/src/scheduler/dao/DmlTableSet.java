package scheduler.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import scheduler.dao.dml.TableJoinType;
import scheduler.dao.schema.ColumnUsage;
import scheduler.dao.schema.DbColumn;
import scheduler.dao.schema.DbTable;

/**
 *
 * @author lerwi
 */
// TODO: Replace with scheduler.dao.dml.SelectList
@Deprecated
public final class DmlTableSet implements IDmlTableSet {

    private final Object syncRoot = new Object();
    private final JoinList tableJoins = new JoinList();
    private final ColumnList dmlColumns = new ColumnList();
    private final DbTable tableName;
    private String tableAlias;

    public DmlTableSet(DbTable primaryTable, String tableAlias, Predicate<DbColumn> columnSelector, Function<DbColumn, String> columnAliasMapper) {
        this.tableName = primaryTable;
        if (null == tableAlias) {
            this.tableAlias = primaryTable.getAlias();
        } else if (tableAlias.trim().isEmpty()) {
            this.tableAlias = primaryTable.getDbName();
        } else {
            this.tableAlias = tableAlias;
        }
        Function<DbColumn, String> mapper = (null == columnAliasMapper) ? (t) -> t.getAlias() : columnAliasMapper;
        List<DmlColumn> backingList = ((BaseList<DmlColumn>) dmlColumns).backingList;
        ((null == columnSelector) ? DbColumn.getColumns(primaryTable) : DbColumn.getColumns(primaryTable, columnSelector)).forEach((t) -> {
            DmlColumn c = new DmlColumn(this, t, mapper.apply(t));
            if (c.alias.equalsIgnoreCase(this.tableAlias) && !(c.alias.equalsIgnoreCase(c.column.getDbName()) ||
                    c.alias.equalsIgnoreCase(c.column.getAlias()))) {
                throw new UnsupportedOperationException("Columns cannot use same alias as the table");
            }
            if (dmlColumns.aliasExists(c.alias)) {
                throw new UnsupportedOperationException("Duplicate column alias");
            }
            backingList.add(c);
        });
    }

    public DmlTableSet(DbTable primaryTable, String tableAlias, Predicate<DbColumn> columnSelector) {
        this(primaryTable, tableAlias, columnSelector, null);
    }

    public DmlTableSet(DbTable primaryTable, String tableAlias) {
        this(primaryTable, tableAlias, null, null);
    }

    public DmlTableSet(DbTable primaryTable, Predicate<DbColumn> columnSelector, Function<DbColumn, String> columnAliasMapper) {
        this(primaryTable, null, columnSelector, columnAliasMapper);
    }

    public DmlTableSet(DbTable primaryTable, Predicate<DbColumn> columnSelector) {
        this(primaryTable, null, columnSelector, null);
    }

    public DmlTableSet(DbTable primaryTable) {
        this(primaryTable, null, null, null);
    }

    @Override
    public DbTable getTableName() {
        return tableName;
    }

    @Override
    public List<JoinedTable> getTableJoins() {
        return tableJoins;
    }

    @Override
    public List<DmlColumn> getDmlColumns() {
        return dmlColumns;
    }

    @Override
    public String getTableAlias() {
        return tableAlias;
    }

    public void setTableAlias(String alias) {
        synchronized (syncRoot) {
            if (null == alias) {
                alias = tableName.getAlias();
            } else if (alias.trim().isEmpty()) {
                alias = tableName.getDbName();
            }
            if (!alias.equalsIgnoreCase(tableAlias) && tableAliasExists(alias)) {
                throw new UnsupportedOperationException("Table alias is already being used");
            }
            tableAlias = alias;
        }
    }

    public synchronized StringBuilder getSelectQuery() {
        StringBuilder result = new StringBuilder("SELECT ");
        synchronized (syncRoot) {
            Iterator<DmlColumn> iterator = dmlColumns.iterator();
            DmlColumn col;
            String dbName;
            if (tableJoins.isEmpty()) {
                if (!iterator.hasNext()) {
                    throw new UnsupportedOperationException("Table set has no columns");
                }
                col = iterator.next();
                dbName = col.column.getDbName();
                result.append("`").append(dbName);
                if (dbName.equals(col.alias)) {
                    result.append("`");
                } else {
                    result.append("` AS ").append(col.alias);
                }
                while (iterator.hasNext()) {
                    col = iterator.next();
                    dbName = col.column.getDbName();
                    result.append(", `").append(dbName);
                    if (dbName.equals(col.alias)) {
                        result.append("`");
                    } else {
                        result.append("` AS ").append(col.alias);
                    }
                }
                return result.append(" FROM `").append(tableName.getDbName()).append("`");
            }

            Stream.Builder<DmlColumn> allColumns = Stream.builder();
            while (iterator.hasNext()) {
                allColumns.accept(iterator.next());
            }
            tableJoins.forEach((t) -> t.getAllColumns(allColumns));
            iterator = allColumns.build().iterator();
            if (!iterator.hasNext()) {
                throw new UnsupportedOperationException("Table set has no columns");
            }
            col = iterator.next();
            dbName = col.parent.getTableAlias();
            if (dbName.equals(col.column.getTable().getDbName())) {
                result.append("`").append(dbName).append("`.`");
            } else {
                result.append(dbName).append(".`");
            }
            result.append(col.column.getDbName()).append("` AS ").append(col.alias);
            while (iterator.hasNext()) {
                col = iterator.next();
                dbName = col.parent.getTableAlias();
                if (dbName.equals(col.column.getTable().getDbName())) {
                    result.append(", `").append(dbName).append("`.`");
                } else {
                    result.append(", ").append(dbName).append(".`");
                }
                result.append(col.column.getDbName()).append("` AS ").append(col.alias);
            }
            result.append(" FROM `").append(tableName.getDbName());
            if (tableAlias.equals(tableName.getDbName())) {
                result.append("`");
            } else {
                result.append("` ").append(tableAlias);
            }
            Stream.Builder<JoinedTable> allJoins = Stream.builder();
            tableJoins.forEach((t) -> {
                allJoins.accept(t);
                t.getAllNestedJoins(allJoins);
            });
            allJoins.build().forEach((t) -> {
                String rightDbName = t.joinedColumn.getTable().getDbName();
                result.append(" ").append(t.type.toString()).append(" `").append(rightDbName);
                if (t.tableAlias.equals(rightDbName)) {
                    result.append("` ON ");
                } else {
                    result.append("` ").append(t.tableAlias).append(" ON ");
                }
                result.append(t.parentSet.getTableAlias()).append(".`").append(t.parentColumn.getDbName()).append("`=")
                        .append(t.tableAlias).append(".`").append(t.joinedColumn.getDbName()).append("`");
            });
        }
        return result;
    }

    private synchronized JoinedTable join(IDmlTableSet parentSet, DbColumn left, TableJoinType type, DbColumn right, String tableAlias, Predicate<DbColumn> columnSelector,
            Function<DbColumn, String> columnAliasMapper) {
        JoinedTable result = new JoinedTable(parentSet, left, right, type, tableAlias);
        synchronized (syncRoot) {
            if (left.getTable() != parentSet.getTableName()) {
                throw new UnsupportedOperationException("Table/Column mismatch");
            }
            if (tableAliasExists(result.tableAlias)) {
                throw new UnsupportedOperationException(String.format("Alias \"%s\" is already being used", result.tableAlias));
            }
            String rPkCol = right.getTable().getPkColName();
            String lPkCol = left.getTable().getPkColName();
            List<DmlColumn> backingList = ((BaseList<DmlColumn>) result.dmlColumns).backingList;
            ((null == columnSelector) ? DbColumn.getColumns(right.getTable()) : DbColumn.getColumns(right.getTable(), columnSelector)).forEach((t) -> {
                DmlColumn col = new DmlColumn(result, t, columnAliasMapper.apply(t));
                if (columnAliasExists(col.alias)) {
                    if (col.column.getUsage() != ColumnUsage.OTHER && (col.alias.equals(col.column.getAlias()) || col.alias.equals(col.column.getDbName())))
                        return;
                    throw new UnsupportedOperationException(String.format("Alias \"%s\" is already being used", col.alias));
                }
                if (result.dmlColumns.aliasExists(col.alias)) {
                    throw new UnsupportedOperationException(String.format("Duplicate alias \"%s\"", col.alias));
                }
                backingList.add(col);
            });

            if (parentSet instanceof JoinedTable) {
                ((BaseList<JoinedTable>) ((JoinedTable) parentSet).tableJoins).backingList.add(result);
            } else {
                ((BaseList<JoinedTable>) tableJoins).backingList.add(result);
            }
        }
        return result;
    }

    @Override
    public JoinedTable innerJoin(DbColumn leftColumn, DbColumn rightColumn, String tableAlias, Predicate<DbColumn> columnSelector, Function<DbColumn, String> columnAliasMapper) {
        return join(this, leftColumn, TableJoinType.INNER, rightColumn, tableAlias, columnSelector,
                (null == columnAliasMapper) ? (t) -> t.getAlias() : columnAliasMapper);
    }

    @Override
    public JoinedTable leftJoin(DbColumn leftColumn, DbColumn rightColumn, String tableAlias, Predicate<DbColumn> columnSelector, Function<DbColumn, String> columnAliasMapper) {
        return join(this, leftColumn, TableJoinType.LEFT, rightColumn, tableAlias, columnSelector,
                (null == columnAliasMapper) ? (t) -> t.getAlias() : columnAliasMapper);
    }

    @Override
    public JoinedTable rightJoin(DbColumn leftColumn, DbColumn rightColumn, String tableAlias, Predicate<DbColumn> columnSelector, Function<DbColumn, String> columnAliasMapper) {
        return join(this, leftColumn, TableJoinType.RIGHT, rightColumn, tableAlias, columnSelector,
                (null == columnAliasMapper) ? (t) -> t.getAlias() : columnAliasMapper);
    }

    @Override
    public JoinedTable fullJoin(DbColumn leftColumn, DbColumn rightColumn, String tableAlias, Predicate<DbColumn> columnSelector, Function<DbColumn, String> columnAliasMapper) {
        return join(this, leftColumn, TableJoinType.FULL, rightColumn, tableAlias, columnSelector,
                (null == columnAliasMapper) ? (t) -> t.getAlias() : columnAliasMapper);
    }

    public final class JoinedTable implements IDmlTableSet {

        private IDmlTableSet parentSet;
        private final DbColumn parentColumn;
        private final DbColumn joinedColumn;
        private final TableJoinType type;
        private final JoinList tableJoins = new JoinList();
        private final ColumnList dmlColumns = new ColumnList();
        private String tableAlias;

        @Override
        public DbTable getTableName() {
            return joinedColumn.getTable();
        }

        public DbColumn getJoinedColumn() {
            return joinedColumn;
        }

        public DbColumn getParentColumn() {
            return parentColumn;
        }

        public IDmlTableSet getParentSet() {
            return parentSet;
        }

        @Override
        public String getTableAlias() {
            return tableAlias;
        }

        public void setTableAlias(String alias) {
            synchronized (syncRoot) {
                if (null == alias) {
                    alias = parentSet.getTableName().getAlias();
                } else if (alias.trim().isEmpty()) {
                    alias = parentSet.getTableName().getDbName();
                }
                if (!alias.equalsIgnoreCase(tableAlias) && DmlTableSet.this.tableAliasExists(alias)) {
                    throw new UnsupportedOperationException("Table alias is already being used");
                }
                tableAlias = alias;
            }
        }

        public TableJoinType getType() {
            return type;
        }

        @Override
        public List<JoinedTable> getTableJoins() {
            return tableJoins;
        }

        @Override
        public List<DmlColumn> getDmlColumns() {
            return dmlColumns;
        }

        private JoinedTable(IDmlTableSet parentSet, DbColumn parentColumn, DbColumn joinedColumn, TableJoinType type, String alias) {
            this.parentSet = parentSet;

            this.parentColumn = parentColumn;
            this.joinedColumn = joinedColumn;
            this.type = type;
            if (null == alias) {
                tableAlias = joinedColumn.getTable().getAlias();
            } else if (alias.trim().isEmpty()) {
                tableAlias = joinedColumn.getTable().getDbName();
            } else {
                tableAlias = alias;
            }
        }

        public String getColumnAlias(DbColumn column, boolean global) {
            if (!global) {
                return getColumnAlias(column);
            }
            return DmlTableSet.this.getColumnAlias(column);
        }

        public DmlColumn findDmlColumn(String alias, boolean global) {
            if (!global) {
                return findDmlColumn(alias);
            }
            return DmlTableSet.this.findDmlColumn(alias);
        }

        public IDmlTableSet findTableSet(String alias, boolean global) {
            if (!global) {
                return findTableSet(alias);
            }
            return DmlTableSet.this.findTableSet(alias);
        }

        public IDmlTableSet findTableSet(DbTable tableName, boolean global) {
            if (!global) {
                return findTableSet(tableName);
            }
            return DmlTableSet.this.findTableSet(tableName);
        }

        public boolean tableAliasExists(String alias, boolean global) {
            if (!global) {
                return tableAliasExists(alias);
            }
            return DmlTableSet.this.tableAliasExists(alias);
        }

        public boolean columnAliasExists(String alias, boolean global) {
            if (!global) {
                return columnAliasExists(alias);
            }
            return DmlTableSet.this.columnAliasExists(alias);
        }

        private void getAllColumns(Stream.Builder<DmlColumn> streamBuilder) {
            dmlColumns.forEach((t) -> streamBuilder.accept(t));
            tableJoins.stream().forEach((t) -> t.getAllColumns(streamBuilder));
        }

        @Override
        public JoinedTable innerJoin(DbColumn leftColumn, DbColumn rightColumn, String tableAlias, Predicate<DbColumn> columnSelector, Function<DbColumn, String> columnAliasMapper) {
            return join(this, leftColumn, TableJoinType.INNER, rightColumn, tableAlias, columnSelector,
                    (null == columnAliasMapper) ? (t) -> t.getAlias() : columnAliasMapper);
        }

        @Override
        public JoinedTable leftJoin(DbColumn leftColumn, DbColumn rightColumn, String tableAlias, Predicate<DbColumn> columnSelector, Function<DbColumn, String> columnAliasMapper) {
            return join(this, leftColumn, TableJoinType.LEFT, rightColumn, tableAlias, columnSelector,
                    (null == columnAliasMapper) ? (t) -> t.getAlias() : columnAliasMapper);
        }

        @Override
        public JoinedTable rightJoin(DbColumn leftColumn, DbColumn rightColumn, String tableAlias, Predicate<DbColumn> columnSelector, Function<DbColumn, String> columnAliasMapper) {
            return join(this, leftColumn, TableJoinType.RIGHT, rightColumn, tableAlias, columnSelector,
                    (null == columnAliasMapper) ? (t) -> t.getAlias() : columnAliasMapper);
        }

        @Override
        public JoinedTable fullJoin(DbColumn leftColumn, DbColumn rightColumn, String tableAlias, Predicate<DbColumn> columnSelector, Function<DbColumn, String> columnAliasMapper) {
            return join(this, leftColumn, TableJoinType.FULL, rightColumn, tableAlias, columnSelector,
                    (null == columnAliasMapper) ? (t) -> t.getAlias() : columnAliasMapper);
        }

        private void getAllNestedJoins(Stream.Builder<JoinedTable> streamBuilder) {
            tableJoins.forEach((t) -> {
                streamBuilder.accept(t);
                t.getAllNestedJoins(streamBuilder);
            });
        }

    }

    public final class DmlColumn {

        private final DbColumn column;
        private IDmlTableSet parent;
        private String alias;

        public DbColumn getColumn() {
            return column;
        }

        public String getAlias() {
            return alias;
        }

        public IDmlTableSet getParent() {
            return parent;
        }

        public void setAlias(String alias) {
            synchronized (syncRoot) {
                if (null == alias) {
                    alias = column.getAlias();
                } else if (alias.trim().isEmpty()) {
                    alias = column.getDbName();
                }
                if (!alias.equalsIgnoreCase(this.alias) && DmlTableSet.this.columnAliasExists(alias)) {
                    throw new UnsupportedOperationException("Column alias is already being used");
                }
                this.alias = alias;
            }
        }

        private DmlColumn(IDmlTableSet parent, DbColumn column, String alias) {
            this.column = column;
            this.parent = parent;
            if (null == alias) {
                this.alias = column.getAlias();
            } else if (alias.trim().isEmpty()) {
                this.alias = column.getDbName();
            } else {
                this.alias = alias;
            }
        }
    }

    final class ColumnList extends BaseList<DmlColumn> {

        @Override
        public boolean aliasExists(String alias) {
            return null != alias && !alias.trim().isEmpty() && getBackingList().stream().anyMatch((t) -> t.alias.equalsIgnoreCase(alias));
        }

        @Override
        public DmlColumn getByAlias(String alias) {
            if (null != alias && !alias.trim().isEmpty()) {
                Optional<DmlColumn> result = getBackingList().stream().filter((t) -> t.alias.equalsIgnoreCase(alias)).findFirst();
                if (result.isPresent()) {
                    return result.get();
                }
            }
            return null;
        }

        @Override
        protected void onRemoved(DmlColumn item) {
            item.parent = null;
        }

    }

    final class JoinList extends BaseList<JoinedTable> {

        @Override
        public JoinedTable getByAlias(String alias) {
            if (null != alias && !alias.trim().isEmpty()) {
                Optional<JoinedTable> result = getBackingList().stream().filter((t) -> t.tableAlias.equalsIgnoreCase(alias)).findFirst();
                if (result.isPresent()) {
                    return result.get();
                }
            }
            return null;
        }

        @Override
        public boolean aliasExists(String alias) {
            return null != alias && !alias.trim().isEmpty() && getBackingList().stream().anyMatch((t) -> t.tableAlias.equalsIgnoreCase(alias));
        }

        @Override
        protected void onRemoved(JoinedTable item) {
            item.parentSet = null;
        }

    }

    abstract class BaseList<T> implements IDmlTableSet.IList<T> {

        private final ArrayList<T> backingList = new ArrayList<>();

        protected final ArrayList<T> getBackingList() {
            return backingList;
        }

        @Override
        public int size() {
            return backingList.size();
        }

        @Override
        public boolean isEmpty() {
            return backingList.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return backingList.contains(o);
        }

        @Override
        public Iterator<T> iterator() {
            return backingList.iterator();
        }

        @Override
        public Object[] toArray() {
            return backingList.toArray();
        }

        @Override
        public boolean remove(Object o) {
            T item;
            synchronized (syncRoot) {
                int index = backingList.indexOf(o);
                if (index < 0) {
                    return false;
                }
                backingList.remove(index);
                item = backingList.get(index);
            }
            onRemoved(item);
            return true;
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return backingList.containsAll(c);
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            Object[] items;
            synchronized (syncRoot) {
                items = toArray();
                if (!backingList.removeAll(c)) {
                    return false;
                }
                items = Arrays.stream(items).filter((t) -> !backingList.contains((T) t)).toArray();
            }
            for (Object o : items) {
                onRemoved((T) o);
            }
            return true;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            Object[] items;
            synchronized (syncRoot) {
                items = toArray();
                if (!backingList.retainAll(c)) {
                    return false;
                }
                items = Arrays.stream(items).filter((t) -> !backingList.contains((T) t)).toArray();
            }
            for (Object o : items) {
                onRemoved((T) o);
            }
            return true;
        }

        @Override
        public void clear() {
            Object[] removed;
            synchronized (syncRoot) {
                if (backingList.isEmpty()) {
                    return;
                }
                removed = backingList.toArray();
                backingList.clear();
            }
            for (Object o : removed) {
                onRemoved((T) o);
            }
        }

        @Override
        public T get(int index) {
            return backingList.get(index);
        }

        @Override
        public T remove(int index) {
            T item;
            synchronized (syncRoot) {
                item = backingList.remove(index);
            }
            if (null != item) {
                onRemoved(item);
            }
            return item;
        }

        @Override
        public int indexOf(Object o) {
            return backingList.indexOf(o);
        }

        @Override
        public int lastIndexOf(Object o) {
            return backingList.lastIndexOf(o);
        }

        @Override
        public ListIterator<T> listIterator() {
            return backingList.listIterator();
        }

        @Override
        public ListIterator<T> listIterator(int index) {
            return backingList.listIterator(index);
        }

        @Override
        public List<T> subList(int fromIndex, int toIndex) {
            return backingList.subList(fromIndex, toIndex);
        }

        protected abstract void onRemoved(T item);

    }

}
