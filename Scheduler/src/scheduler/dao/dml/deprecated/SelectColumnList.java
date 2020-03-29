package scheduler.dao.dml.deprecated;

import scheduler.dao.dml.TableJoinType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import scheduler.dao.schema.DbColumn;
import scheduler.dao.schema.DbTable;
import scheduler.dao.schema.SchemaHelper;
import scheduler.util.CaseInsensitiveStringMap;
import scheduler.util.ReadOnlyList;

/**
 * A read-only list of {@link ColumnReference}s associated with a specific {@link scheduler.dao.schema.DbTable}. No two {@link ColumnReference}
 * elements are to have the same value for {@link ColumnReference#getName()}.
 *
 * @author Leonard T. Erwine (Student ID 356334)
 */
public class SelectColumnList implements JoinableTableColumnList<SelectColumnList.Joined, SelectColumnList.SelectColumn> {
    
    /**
     * Gets a valid indicating whether this object is read-only.
     * 
     * @return {@code true} if this object is read-only; otherwise {@code false}.
     */
    public boolean isReadOnly() {
        return globalLookup.readOnly;
    }
    
    private static String ensureForeignTableAlias(DbColumn col, String alias) {
        return (null == alias) ? col.getTable().toString() : ((alias.trim().isEmpty()) ? col.getTable().getDbName().toString() : alias);
    }

    private static String ensureTableAlias(DbTable table, String alias) {
        return (null == alias) ? table.toString() : ((alias.trim().isEmpty()) ? table.getDbName().toString() : alias);
    }

    private static String ensureColumnAlias(DbColumn col, String alias) {
        return (null == alias) ? col.toString() : ((alias.trim().isEmpty()) ? col.getDbName().toString() : alias);
    }

    private final GlobalLookup globalLookup;
    private final DbTable table;
    private String tableAlias;
    private final ArrayList<SelectColumn> backingList;
    private final JoinedTables joinedTables;

    private SelectColumnList(GlobalLookup globalLookup, DbTable table, String alias, Predicate<DbColumn> columnSelector,
            Function<DbColumn, String> columnAliasMapper) {
        this.table = Objects.requireNonNull(table);
        backingList = new ArrayList<>();
        joinedTables = new JoinedTables();
        tableAlias = ensureTableAlias(table, alias);
        Iterator<DbColumn> iterator = (null == columnSelector) ? SchemaHelper.getTableColumns(table).iterator() : 
                SchemaHelper.getTableColumns(table, columnSelector).iterator();
        while (iterator.hasNext()) {
            DbColumn c = iterator.next();
            String a = columnAliasMapper.apply(c);
            Optional<SelectColumn> matching = backingList.stream().filter((d) -> d.name.equalsIgnoreCase(a)).findFirst();
            assert !matching.isPresent() || matching.get().column == c : "Duplicate column aliases no permitted";
            backingList.add(new SelectColumn(this, c, a));
        }
        this.globalLookup = (null == globalLookup) ? new GlobalLookup(this) : globalLookup;
    }

    /**
     * Creates a new SelectColumnList object.
     * 
     * @param dbTable The primary {@link DbTable} for this query.
     * @param tableAlias The table alias to use.
     * @param columnSelector Invoked during class initialization to selects the columns to be included in this query.
     * @param columnAliasMapper Invoked during class initialization to specify column alias names.
     */
    public SelectColumnList(DbTable dbTable, String tableAlias, Predicate<DbColumn> columnSelector, Function<DbColumn, String> columnAliasMapper) {
        this(null, dbTable, ensureTableAlias(dbTable, tableAlias), columnSelector,
                (null == columnAliasMapper) ? (t) -> t.toString() : (t) -> ensureColumnAlias(t, columnAliasMapper.apply(t)));
    }

    /**
     * Creates a new SelectColumnList object.
     * 
     * @param dbTable The primary {@link DbTable} for this query.
     * @param tableAlias The table alias to use.
     * @param columnSelector Invoked during class initialization to selects the columns to be included in this query.
     */
    public SelectColumnList(DbTable dbTable, String tableAlias, Predicate<DbColumn> columnSelector) {
        this(null, dbTable, ensureTableAlias(dbTable, tableAlias), columnSelector, (t) -> t.toString());
    }

    /**
     * Creates a new SelectColumnList object.
     * 
     * @param dbTable The primary {@link DbTable} for this query.
     * @param columnSelector Invoked during class initialization to selects the columns to be included in this query.
     * @param columnAliasMapper Invoked during class initialization to specify column alias names.
     */
    public SelectColumnList(DbTable dbTable, Predicate<DbColumn> columnSelector, Function<DbColumn, String> columnAliasMapper) {
        this(null, dbTable, dbTable.toString(), columnSelector,
                (null == columnAliasMapper) ? (t) -> t.toString() : (t) -> ensureColumnAlias(t, columnAliasMapper.apply(t)));
    }

    /**
     * Creates a new SelectColumnList object.
     * 
     * @param dbTable The primary {@link DbTable} for this query.
     * @param columnSelector Invoked during class initialization to selects the columns to be included in this query.
     */
    public SelectColumnList(DbTable dbTable, Predicate<DbColumn> columnSelector) {
        this(null, dbTable, dbTable.toString(), columnSelector, (t) -> t.toString());
    }

    /**
     * Creates a new SelectColumnList object.
     * 
     * @param dbTable The primary {@link DbTable} for this query.
     * @param tableAlias The table alias to use.
     */
    public SelectColumnList(DbTable dbTable, String tableAlias) {
        this(null, dbTable, ensureTableAlias(dbTable, tableAlias), null, (t) -> t.toString());
    }

    /**
     * Creates a new SelectColumnList object.
     * 
     * @param dbTable The primary {@link DbTable} for this query.
     */
    public SelectColumnList(DbTable dbTable) {
        this(null, dbTable, dbTable.toString(), null, (t) -> t.toString());
    }

    @Override
    public String getTableAlias() {
        return tableAlias;
    }

    /**
     * Sets the table alias for this query.
     * 
     * @param name
     */
    public void setTableAlias(String name) {
        name = ensureTableAlias(table, name);

        synchronized (globalLookup) {
            if (globalLookup.readOnly)
                throw new IllegalStateException();
            if (!name.equalsIgnoreCase(this.tableAlias) && globalLookup.tables.containsKey(name)) {
                throw new UnsupportedOperationException("Table reference name is already used");
            }
            this.tableAlias = name;
        }
    }

    @Override
    public ReadOnlyList<Joined> getJoinedTables() {
        return joinedTables;
    }

    @Override
    public DbTable getTable() {
        return table;
    }

    /**
     * Initializes a {@link StringBuilder} with a SELECT query containing the column references and joined tables.
     * 
     * @return A {@link StringBuilder} with a SELECT query containing the column references and joined tables.
     */
    public StringBuilder getSelectQuery() {
        StringBuilder result = new StringBuilder("SELECT ");
        SelectColumnList primaryTable = globalLookup.primaryTable;
        synchronized (globalLookup) {
            Iterator<SelectColumn> iterator = primaryTable.backingList.iterator();
            SelectColumn col;
            String dbName;
            if (primaryTable.joinedTables.isEmpty()) {
                if (!iterator.hasNext()) {
                    throw new UnsupportedOperationException("Table set has no columns");
                }
                col = iterator.next();
                dbName = col.column.getDbName().toString();
                result.append("`").append(dbName);
                if (dbName.equals(col.name)) {
                    result.append("`");
                } else {
                    result.append("` AS ").append(col.name);
                }
                while (iterator.hasNext()) {
                    col = iterator.next();
                    dbName = col.column.getDbName().toString();
                    result.append(", `").append(dbName);
                    if (dbName.equals(col.name)) {
                        result.append("`");
                    } else {
                        result.append("` AS ").append(col.name);
                    }
                }
                return result.append(" FROM `").append(primaryTable.table.getDbName()).append("`");
            }

            Stream.Builder<SelectColumn> allColumns = Stream.builder();
            while (iterator.hasNext()) {
                allColumns.accept(iterator.next());
            }
            primaryTable.joinedTables.forEach((t) -> t.getAllColumns(allColumns));
            iterator = allColumns.build().iterator();
            if (!iterator.hasNext()) {
                throw new UnsupportedOperationException("Table set has no columns");
            }
            col = iterator.next();
            dbName = col.parent.tableAlias;
            if (dbName.equals(col.column.getTable().getDbName().toString())) {
                result.append("`").append(dbName).append("`.`");
            } else {
                result.append(dbName).append(".`");
            }
            result.append(col.column.getDbName()).append("` AS ").append(col.name);
            while (iterator.hasNext()) {
                col = iterator.next();
                dbName = col.parent.tableAlias;
                if (dbName.equals(col.column.getTable().getDbName().toString())) {
                    result.append(", `").append(dbName).append("`.`");
                } else {
                    result.append(", ").append(dbName).append(".`");
                }
                result.append(col.column.getDbName()).append("` AS ").append(col.name);
            }
            result.append(" FROM `").append(primaryTable.table.getDbName());
            if (primaryTable.tableAlias.equals(primaryTable.table.getDbName().toString())) {
                result.append("`");
            } else {
                result.append("` ").append(primaryTable.tableAlias);
            }
            Stream.Builder<Joined> allJoins = Stream.builder();
            primaryTable.joinedTables.forEach((t) -> t.getAllNestedJoins(allJoins));
            allJoins.build().forEach((t) -> {
                String rightDbName = t.childColumn.getTable().getDbName().toString();
                result.append(" ").append(t.type.toString()).append(" `").append(rightDbName);
                if (t.getTableAlias().equals(rightDbName)) {
                    result.append("` ON ");
                } else {
                    result.append("` ").append(t.getTableAlias()).append(" ON ");
                }
                result.append(t.parentTable.tableAlias).append(".`").append(t.parentColumn.getDbName()).append("`=")
                        .append(t.getTableAlias()).append(".`").append(t.childColumn.getDbName()).append("`");
            });
        }
        return result;
    }

    protected void getAllColumns(Stream.Builder<SelectColumn> builder) {
        if (!backingList.isEmpty()) {
            backingList.forEach((t) -> builder.accept(t));
        }
        if (!joinedTables.isEmpty()) {
            joinedTables.forEach((t) -> t.getAllColumns(builder));
        }
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
    public Iterator<SelectColumn> iterator() {
        return backingList.iterator();
    }

    @Override
    public Object[] toArray() {
        return backingList.toArray();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return backingList.containsAll(c);
    }

    @Override
    public SelectColumn get(int index) {
        return backingList.get(index);
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
    public ListIterator<SelectColumn> listIterator() {
        return backingList.listIterator();
    }

    @Override
    public ListIterator<SelectColumn> listIterator(int index) {
        return backingList.listIterator(index);
    }

    @Override
    public List<SelectColumn> subList(int fromIndex, int toIndex) {
        return backingList.subList(fromIndex, toIndex);
    }

    @Override
    public Joined innerJoin(DbColumn leftColumn, DbColumn rightColumn, String tableAlias, Predicate<DbColumn> columnSelector,
            Function<DbColumn, String> columnAliasMapper) {
        return globalLookup.join(this, Objects.requireNonNull(leftColumn), TableJoinType.INNER, Objects.requireNonNull(rightColumn),
                ensureForeignTableAlias(rightColumn, tableAlias), columnSelector,
                (null == columnAliasMapper) ? (t) -> t.toString() : (t) -> ensureColumnAlias(t, columnAliasMapper.apply(t)));
    }

    @Override
    public Joined leftJoin(DbColumn leftColumn, DbColumn rightColumn, String tableAlias, Predicate<DbColumn> columnSelector,
            Function<DbColumn, String> columnAliasMapper) {
        return globalLookup.join(this, Objects.requireNonNull(leftColumn), TableJoinType.LEFT, Objects.requireNonNull(rightColumn),
                ensureForeignTableAlias(rightColumn, tableAlias), columnSelector,
                (null == columnAliasMapper) ? (t) -> t.toString() : (t) -> ensureColumnAlias(t, columnAliasMapper.apply(t)));
    }

    @Override
    public Joined rightJoin(DbColumn leftColumn, DbColumn rightColumn, String tableAlias, Predicate<DbColumn> columnSelector,
            Function<DbColumn, String> columnAliasMapper) {
        return globalLookup.join(this, Objects.requireNonNull(leftColumn), TableJoinType.RIGHT, Objects.requireNonNull(rightColumn),
                ensureForeignTableAlias(rightColumn, tableAlias), columnSelector,
                (null == columnAliasMapper) ? (t) -> t.toString() : (t) -> ensureColumnAlias(t, columnAliasMapper.apply(t)));
    }

    @Override
    public Joined fullJoin(DbColumn leftColumn, DbColumn rightColumn, String tableAlias, Predicate<DbColumn> columnSelector,
            Function<DbColumn, String> columnAliasMapper) {
        return globalLookup.join(this, Objects.requireNonNull(leftColumn), TableJoinType.FULL, Objects.requireNonNull(rightColumn),
                ensureForeignTableAlias(rightColumn, tableAlias), columnSelector,
                (null == columnAliasMapper) ? (t) -> t.toString() : (t) -> ensureColumnAlias(t, columnAliasMapper.apply(t)));
    }

    public void makeUnmodifiable() {
        synchronized (globalLookup) {
            globalLookup.readOnly = true;
        }
    }

    /**
     * Represents a {@link SelectColumnList} that has been joined to another.
     * 
     * @author Leonard T. Erwine (Student ID 356334)
     */
    public static class Joined extends SelectColumnList implements JoinedTableColumnList<Joined, SelectColumn> {

        private SelectColumnList parentTable;
        private final TableJoinType type;
        private final DbColumn parentColumn;
        private final DbColumn childColumn;

        private Joined(GlobalLookup globalLookup, DbTable tableName, DbColumn parentColumn, TableJoinType type, DbColumn childColumn,
                String tableAlias, Predicate<DbColumn> columnSelector, Function<DbColumn, String> columnAliasMapper) {
            super(globalLookup, tableName, tableAlias, columnSelector, columnAliasMapper);
            this.parentColumn = parentColumn;
            this.type = type;
            this.childColumn = childColumn;
        }

        @Override
        public JoinableTable<Joined> getParentTable() {
            return parentTable;
        }

        @Override
        public DbColumn getParentColumn() {
            return parentColumn;
        }

        @Override
        public DbColumn getChildColumn() {
            return childColumn;
        }

        @Override
        public TableJoinType getType() {
            return type;
        }

        @Override
        public JoinableTable<Joined> getPrimaryTable() {
            return ((SelectColumnList)this).globalLookup.primaryTable;
        }

        protected void getAllNestedJoins(Stream.Builder<Joined> builder) {
            builder.add(this);
            if (!getJoinedTables().isEmpty()) {
                getJoinedTables().forEach((t) -> t.getAllNestedJoins(builder));
            }
        }

    }

    static class GlobalLookup {

        private final SelectColumnList primaryTable;
        private final CaseInsensitiveStringMap<SelectColumnList> tables;
        private final CaseInsensitiveStringMap<SelectColumn> columns;
        private boolean readOnly = false;

        GlobalLookup(SelectColumnList primaryTable) {
            this.primaryTable = primaryTable;
            tables = new CaseInsensitiveStringMap<>();
            columns = new CaseInsensitiveStringMap<>();
            tables.put(primaryTable.tableAlias, primaryTable);
            primaryTable.backingList.forEach((t) -> columns.put(t.name, t));
        }

        private synchronized Joined join(SelectColumnList leftTable, DbColumn leftColumn, TableJoinType type, DbColumn rightColumn, String tableAlias,
                Predicate<DbColumn> columnSelector, Function<DbColumn, String> columnAliasMapper) {
            if (readOnly)
                throw new IllegalStateException();
            assert leftTable.getTable() == leftColumn.getTable() : "Left column does not belong to the current table";
            assert !tables.containsKey(tableAlias) : "The specified table alias is already used by a joined table";
            Predicate<DbColumn> cs;
            if (null == columnSelector) {
                cs = (c) -> {
                    String a = columnAliasMapper.apply(c);
                    SelectColumn existing = columns.get(a);
                    if (null != existing) {
                        assert SchemaHelper.areColumnsRelated(c, existing.column) || !SchemaHelper.isEntityData(c) :
                                "The specified column alias is already used in a joined table";
                        return false;
                    }
                    return true;
                };
            } else {
                cs = (c) -> {
                    String a = columnAliasMapper.apply(c);
                    SelectColumn existing = columns.get(a);
                    if (null != existing) {
                        assert SchemaHelper.areColumnsRelated(c, existing.column) || !SchemaHelper.isEntityData(c) :
                                "The specified column alias is already used in a joined table";
                        return false;
                    }
                    return columnSelector.test(c);
                };
            }
            Joined result = new Joined(this, rightColumn.getTable(), leftColumn, type, rightColumn, tableAlias, cs, columnAliasMapper);
            result.parentTable = leftTable;
            leftTable.joinedTables.backingList.add(result);
            result.forEach((t) -> columns.put(t.name, t));
            tables.put(result.getTableAlias(), result);
            return result;
        }
    }

    public final class SelectColumn implements ColumnReference {

        private SelectColumnList parent;
        private final DbColumn column;
        private String name;

        private SelectColumn(SelectColumnList parent, DbColumn column, String name) {
            this.column = column;
            this.name = name;
            this.parent = parent;
        }

        @Override
        public DbColumn getColumn() {
            return column;
        }

        @Override
        public String getName() {
            return name;
        }

        public void setName(String name) {
            name = ensureColumnAlias(column, name);

            synchronized (globalLookup) {
                if (!name.equalsIgnoreCase(this.name) && globalLookup.primaryTable.isColumnRefNameUsed(name)) {
                    throw new UnsupportedOperationException("Column reference name is already used");
                }
                this.name = name;
            }
        }

    }

    class JoinedTables implements ReadOnlyList<Joined> {

        private final ArrayList<Joined> backingList = new ArrayList<>();

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
        public Iterator<Joined> iterator() {
            return backingList.iterator();
        }

        @Override
        public Object[] toArray() {
            return backingList.toArray();
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return backingList.containsAll(c);
        }

        @Override
        public Joined get(int index) {
            return backingList.get(index);
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
        public ListIterator<Joined> listIterator() {
            return backingList.listIterator();
        }

        @Override
        public ListIterator<Joined> listIterator(int index) {
            return backingList.listIterator(index);
        }

        @Override
        public List<Joined> subList(int fromIndex, int toIndex) {
            return backingList.subList(fromIndex, toIndex);
        }

    }

}
