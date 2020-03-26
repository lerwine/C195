package scheduler.dao.dml;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import scheduler.dao.schema.DbColumn;
import scheduler.dao.schema.DbTable;
import scheduler.dao.schema.SchemaHelper;
import scheduler.util.ReadOnlyList;
import scheduler.util.ReadOnlyMap;

/**
 *
 * @author lerwi
 */
public class DmlTable {

    public static Builder builder(DbTable table, String alias, Predicate<DbColumn> columnSelector, Function<DbColumn, String> aliasMapper) {
        Builder result = new Builder(null, table, alias, (null == columnSelector) ? SchemaHelper.getTableColumns(table).iterator()
                : SchemaHelper.getTableColumns(table, columnSelector).iterator(), aliasMapper);
        result.builderTables.put(alias, result);
        return result;
    }

    public static Builder builder(DbTable table, String alias, Predicate<DbColumn> columnSelector) {
        return builder(table, alias, columnSelector, null);
    }

    public static Builder builder(DbTable table, String alias, Function<DbColumn, String> aliasMapper) {
        return builder(table, alias, null, aliasMapper);
    }

    public static Builder builder(DbTable table, String alias, boolean noColumns) {
        Builder result = new Builder(null, table, alias, (noColumns) ? Collections.emptyIterator() : SchemaHelper.getTableColumns(table).iterator(), null);
        result.builderTables.put(alias, result);
        return result;
    }

    public static Builder builder(DbTable table, String alias) {
        return builder(table, alias, null, null);
    }

    public static Builder builder(DbTable table, Predicate<DbColumn> columnSelector, Function<DbColumn, String> aliasMapper) {
        return builder(table, null, columnSelector, aliasMapper);
    }

    public static Builder builder(DbTable table, Predicate<DbColumn> columnSelector) {
        return builder(table, null, columnSelector, null);
    }

    public static Builder builder(DbTable table, Function<DbColumn, String> aliasMapper) {
        return builder(table, null, null, aliasMapper);
    }

    public static Builder builder(DbTable table, boolean noColumns) {
        return builder(table, null, noColumns);
    }

    public static Builder builder(DbTable table) {
        return builder(table, null, false);
    }

    private final DbTable table;
    private final String name;
    private final Map<String, DmlColumn> allColumns;
    private final Map<String, DmlTable> allTables;
    private final List<DmlColumn> columns;
    private final List<Join> joins;
    private final Join parent;

    private DmlTable(DbTable table, String name, List<DmlColumn> columns, List<Join> joins, Map<String, DmlColumn> allColumns, Map<String, DmlTable> allTables) {
        this(table, name, columns, joins, allColumns, allTables, null, null, null, null);
    }

    private DmlTable(DbTable table, String name, List<DmlColumn> columns, List<Join> joins, Map<String, DmlColumn> allColumns, Map<String, DmlTable> allTables,
            DmlTable parentTable, DbColumn parentColumn, TableJoinType type, DbColumn childColumn) {
        this.table = table;
        this.name = name;
        this.joins = joins;
        this.columns = columns;
        this.allColumns = allColumns;
        this.allTables = allTables;
        if (null == parentTable) {
            parent = null;
        } else {
            parent = new Join(parentTable, parentColumn, type, childColumn);
        }
    }

    public String getName() {
        return name;
    }

    public DbTable getTable() {
        return table;
    }

    public static final class BuilderColumn {

        private final Builder owner;
        private final DbColumn column;
        private final String alias;
        private BuilderJoin parentJoin;
        private final ReadOnlyList.Wrapper<BuilderJoin> childJoins;

        public Builder getOwner() {
            return owner;
        }

        public DbColumn getColumn() {
            return column;
        }

        public String getAlias() {
            return alias;
        }

        public BuilderJoin getParentJoin() {
            return parentJoin;
        }

        public ReadOnlyList<BuilderJoin> getChildJoins() {
            return childJoins.getReadOnlyList();
        }

        private BuilderColumn(Builder owner, DbColumn column, String alias) {
            this.owner = owner;
            this.column = column;
            this.alias = (null == alias) ? column.getDefaultAlias() : ((alias.trim().isEmpty()) ? column.getDbName().toString() : alias);
            childJoins = new ReadOnlyList.Wrapper<>();
        }

        private DmlColumn build(DmlTable owner, ReadOnlyList<Join> joins) {
            return DmlColumn.of(column, alias, owner, joins);
        }

    }

    public static class BuilderJoin {

        private final Builder parentTable;
        private final Builder childTable;
        private final DbColumn parentColumn;
        private final TableJoinType type;
        private final DbColumn childColumn;

        private BuilderJoin(Builder parentTable, DbColumn parentColumn, TableJoinType type, Builder childTable, DbColumn childColumn) {
            this.parentTable = parentTable;
            this.childTable = childTable;
            this.parentColumn = parentColumn;
            this.type = type;
            this.childColumn = childColumn;
        }

    }

    private static class BuildContext {

        private final ReadOnlyList.Builder<DmlColumn> allOrderedColumns;
        private final ReadOnlyMap.Wrapper<String, DmlColumn> resultColumnMap;
        private final ReadOnlyMap.Wrapper<String, DmlTable> resultTableMap;

        private BuildContext(int size) {
            allOrderedColumns = new ReadOnlyList.Builder<>(size);
            resultColumnMap = new ReadOnlyMap.Wrapper<>();
            resultTableMap = new ReadOnlyMap.Wrapper<>();
        }
    }

    public static class Builder {

        private final DbTable table;
        private final String alias;
        private final ReadOnlyList.Wrapper<BuilderColumn> builderColumns;
        private final ReadOnlyList.Wrapper<BuilderJoin> builderJoins;
        private final ReadOnlyMap.Wrapper<String, Builder> builderTables;
        private final ReadOnlyMap.Wrapper<String, BuilderColumn> builderColumnMap;

        private Join build(DmlTable parent, BuildContext context, BuilderJoin builderJoin) {
            ReadOnlyList.Builder<Join> resultJoinList = new ReadOnlyList.Builder<>(builderJoins.size());
            int startIndex = context.allOrderedColumns.size();
            DmlTable dmlTable = new DmlTable(table, alias, context.allOrderedColumns.build().subList(startIndex, startIndex + builderColumnMap.size()), resultJoinList.build(),
                    context.resultColumnMap.getReadOnlyMap(), context.resultTableMap.getReadOnlyMap(), parent, builderJoin.parentColumn, builderJoin.type, builderJoin.childColumn);
            HashMap<DbColumn, ReadOnlyList.Builder<DmlTable.Join>> joinLists = new HashMap<>();
            builderColumns.forEach((t) -> {
                ReadOnlyList.Builder<DmlTable.Join> jb = new ReadOnlyList.Builder<>(t.childJoins.size());
                joinLists.put(t.column, jb);
                DmlColumn dmlColumn = t.build(dmlTable, jb.build());
                context.allOrderedColumns.accept(dmlColumn);
                context.resultColumnMap.put(dmlColumn.getName(), dmlColumn);
            });
            context.resultTableMap.put(alias, dmlTable);
            builderJoins.forEach((t) -> {
                Join j = t.childTable.build(dmlTable, context, t);
                joinLists.get(t.childColumn).accept(j);
                resultJoinList.accept(j);
            });
            return dmlTable.parent;
        }

        public DmlTable build() {
            BuildContext context = new BuildContext(builderColumnMap.size());
            ReadOnlyList.Builder<Join> resultJoinList = new ReadOnlyList.Builder<>(builderJoins.size());
            DmlTable root = new DmlTable(table, alias, context.allOrderedColumns.build().subList(0, builderColumnMap.size()), resultJoinList.build(), context.resultColumnMap.getReadOnlyMap(),
                    context.resultTableMap.getReadOnlyMap());
            HashMap<DbColumn, ReadOnlyList.Builder<DmlTable.Join>> joinLists = new HashMap<>();
            builderColumns.forEach((t) -> {
                ReadOnlyList.Builder<DmlTable.Join> jb = new ReadOnlyList.Builder<>(t.childJoins.size());
                joinLists.put(t.column, jb);
                DmlColumn dmlColumn = t.build(root, jb.build());
                context.allOrderedColumns.accept(dmlColumn);
                context.resultColumnMap.put(dmlColumn.getName(), dmlColumn);
            });
            context.resultTableMap.put(alias, root);
            builderJoins.forEach((t) -> {
                Join j = t.childTable.build(root, context, t);
                joinLists.get(t.childColumn).accept(j);
                resultJoinList.accept(j);
            });
            return root;
        }

        public DbTable getTable() {
            return table;
        }

        public String getAlias() {
            return alias;
        }

        public List<BuilderColumn> getColumns() {
            return builderColumns.getReadOnlyList();
        }

        public List<BuilderJoin> getJoinedTables() {
            return builderJoins.getReadOnlyList();
        }

        public Map<String, Builder> getAllTables() {
            return builderTables.getReadOnlyMap();
        }

        public Map<String, BuilderColumn> getAllColumns() {
            return builderColumnMap.getReadOnlyMap();
        }

        private Builder(Builder parent, DbTable table, String alias, Iterator<DbColumn> iterator, Function<DbColumn, String> aliasMapper) {
            this.table = table;
            this.alias = alias;

            builderColumns = new ReadOnlyList.Wrapper<>();
            if (null == parent) {
                builderTables = new ReadOnlyMap.Wrapper<>();
                builderColumnMap = new ReadOnlyMap.Wrapper<>();

            } else {
                builderTables = parent.builderTables;
                builderColumnMap = parent.builderColumnMap;
            }
            if (null != aliasMapper) {
                iterator.forEachRemaining((t) -> {
                    String a = aliasMapper.apply(t);
                    if (!builderColumnMap.containsKey(a)) {
                        BuilderColumn c = new BuilderColumn(this, t, a);
                        builderColumnMap.put(a, c);
                        builderColumns.add(c);
                    }
                });
            } else {
                iterator.forEachRemaining((t) -> {
                    String a = t.getDefaultAlias();
                    if (!builderColumnMap.containsKey(a)) {
                        BuilderColumn c = new BuilderColumn(this, t, a);
                        builderColumnMap.put(a, c);
                        builderColumns.add(c);
                    }
                });
            }
            builderJoins = new ReadOnlyList.Wrapper<>();
        }

        public Builder join(DbColumn parentColumn, TableJoinType type, DbColumn childColumn, String tableAlias, Iterator<DbColumn> columns,
                Function<DbColumn, String> aliasMapper) {
            if (null == tableAlias) {
                tableAlias = parentColumn.getTable().getAlias();
            } else if (tableAlias.trim().isEmpty() || tableAlias.equals(parentColumn.getTable().getDbName().toString())) {
                tableAlias = "";
            }

            if (builderTables.containsKey(tableAlias)) {
                throw new UnsupportedOperationException("Table alias not available.");
            }
            Builder result = new Builder(this, parentColumn.getTable(), tableAlias, columns, aliasMapper);
            builderTables.put(result.alias, result);
            BuilderJoin j = new BuilderJoin(this, parentColumn, type, result, childColumn);
            builderJoins.add(j);
            Optional<BuilderColumn> bc = builderColumns.stream().filter((t) -> t.column == parentColumn).findFirst();
            if (bc.isPresent()) {
                bc.get().childJoins.add(j);
            }
            bc = result.builderColumns.stream().filter((t) -> t.column == childColumn).findFirst();
            if (bc.isPresent()) {
                bc.get().parentJoin = j;
            }
            return result;
        }

        public Builder innerJoin(DbColumn parentColumn, DbColumn column, String tableAlias, Predicate<DbColumn> columnSelector,
                Function<DbColumn, String> aliasMapper) {
            return join(parentColumn, TableJoinType.INNER, column, tableAlias,
                    (null == columnSelector) ? SchemaHelper.getTableColumns(column.getTable()).iterator()
                            : SchemaHelper.getTableColumns(column.getTable(), columnSelector).iterator(), aliasMapper);
        }

        public Builder innerJoin(DbColumn parentColumn, DbColumn column, String tableAlias, Predicate<DbColumn> columnSelector) {
            return join(parentColumn, TableJoinType.INNER, column, tableAlias,
                    (null == columnSelector) ? SchemaHelper.getTableColumns(column.getTable()).iterator()
                            : SchemaHelper.getTableColumns(column.getTable(), columnSelector).iterator(), null);
        }

        public Builder innerJoin(DbColumn parentColumn, DbColumn column, String tableAlias, Function<DbColumn, String> aliasMapper) {
            return join(parentColumn, TableJoinType.INNER, column, tableAlias, SchemaHelper.getTableColumns(column.getTable()).iterator(),
                    aliasMapper);
        }

        public Builder innerJoin(DbColumn parentColumn, DbColumn column, String tableAlias, boolean noColumns) {
            return join(parentColumn, TableJoinType.INNER, column, tableAlias, (noColumns) ? Collections.emptyIterator()
                    : SchemaHelper.getTableColumns(column.getTable()).iterator(), null);
        }

        public Builder innerJoin(DbColumn parentColumn, DbColumn column, String tableAlias) {
            return join(parentColumn, TableJoinType.INNER, column, tableAlias, SchemaHelper.getTableColumns(column.getTable()).iterator(),
                    null);
        }

        public Builder innerJoin(DbColumn parentColumn, DbColumn column, Predicate<DbColumn> columnSelector,
                Function<DbColumn, String> aliasMapper) {
            return join(parentColumn, TableJoinType.INNER, column, null,
                    (null == columnSelector) ? SchemaHelper.getTableColumns(column.getTable()).iterator()
                            : SchemaHelper.getTableColumns(column.getTable(), columnSelector).iterator(), aliasMapper);
        }

        public Builder innerJoin(DbColumn parentColumn, DbColumn column, Predicate<DbColumn> columnSelector) {
            return join(parentColumn, TableJoinType.INNER, column, null,
                    (null == columnSelector) ? SchemaHelper.getTableColumns(column.getTable()).iterator()
                            : SchemaHelper.getTableColumns(column.getTable(), columnSelector).iterator(), null);
        }

        public Builder innerJoin(DbColumn parentColumn, DbColumn column, Function<DbColumn, String> aliasMapper) {
            return join(parentColumn, TableJoinType.INNER, column, null, SchemaHelper.getTableColumns(column.getTable()).iterator(),
                    aliasMapper);
        }

        public Builder innerJoin(DbColumn parentColumn, DbColumn column, boolean noColumns) {
            return join(parentColumn, TableJoinType.INNER, column, null, (noColumns) ? Collections.emptyIterator()
                    : SchemaHelper.getTableColumns(column.getTable()).iterator(), null);
        }

        public Builder innerJoin(DbColumn parentColumn, DbColumn column) {
            return join(parentColumn, TableJoinType.INNER, column, null, SchemaHelper.getTableColumns(column.getTable()).iterator(),
                    null);
        }

        public Builder leftJoin(DbColumn parentColumn, DbColumn column, String tableAlias, Predicate<DbColumn> columnSelector,
                Function<DbColumn, String> aliasMapper) {
            return join(parentColumn, TableJoinType.LEFT, column, tableAlias,
                    (null == columnSelector) ? SchemaHelper.getTableColumns(column.getTable()).iterator()
                            : SchemaHelper.getTableColumns(column.getTable(), columnSelector).iterator(), aliasMapper);
        }

        public Builder leftJoin(DbColumn parentColumn, DbColumn column, String tableAlias, Predicate<DbColumn> columnSelector) {
            return join(parentColumn, TableJoinType.LEFT, column, tableAlias,
                    (null == columnSelector) ? SchemaHelper.getTableColumns(column.getTable()).iterator()
                            : SchemaHelper.getTableColumns(column.getTable(), columnSelector).iterator(), null);
        }

        public Builder leftJoin(DbColumn parentColumn, DbColumn column, String tableAlias, Function<DbColumn, String> aliasMapper) {
            return join(parentColumn, TableJoinType.LEFT, column, tableAlias, SchemaHelper.getTableColumns(column.getTable()).iterator(),
                    aliasMapper);
        }

        public Builder leftJoin(DbColumn parentColumn, DbColumn column, String tableAlias, boolean noColumns) {
            return join(parentColumn, TableJoinType.LEFT, column, tableAlias, (noColumns) ? Collections.emptyIterator()
                    : SchemaHelper.getTableColumns(column.getTable()).iterator(), null);
        }

        public Builder leftJoin(DbColumn parentColumn, DbColumn column, String tableAlias) {
            return join(parentColumn, TableJoinType.LEFT, column, tableAlias, SchemaHelper.getTableColumns(column.getTable()).iterator(),
                    null);
        }

        public Builder leftJoin(DbColumn parentColumn, DbColumn column, Predicate<DbColumn> columnSelector,
                Function<DbColumn, String> aliasMapper) {
            return join(parentColumn, TableJoinType.LEFT, column, null,
                    (null == columnSelector) ? SchemaHelper.getTableColumns(column.getTable()).iterator()
                            : SchemaHelper.getTableColumns(column.getTable(), columnSelector).iterator(), aliasMapper);
        }

        public Builder leftJoin(DbColumn parentColumn, DbColumn column, Predicate<DbColumn> columnSelector) {
            return join(parentColumn, TableJoinType.LEFT, column, null,
                    (null == columnSelector) ? SchemaHelper.getTableColumns(column.getTable()).iterator()
                            : SchemaHelper.getTableColumns(column.getTable(), columnSelector).iterator(), null);
        }

        public Builder leftJoin(DbColumn parentColumn, DbColumn column, Function<DbColumn, String> aliasMapper) {
            return join(parentColumn, TableJoinType.LEFT, column, null, SchemaHelper.getTableColumns(column.getTable()).iterator(),
                    aliasMapper);
        }

        public Builder leftJoin(DbColumn parentColumn, DbColumn column, boolean noColumns) {
            return join(parentColumn, TableJoinType.LEFT, column, null, (noColumns) ? Collections.emptyIterator()
                    : SchemaHelper.getTableColumns(column.getTable()).iterator(), null);
        }

        public Builder leftJoin(DbColumn parentColumn, DbColumn column) {
            return join(parentColumn, TableJoinType.LEFT, column, null, SchemaHelper.getTableColumns(column.getTable()).iterator(),
                    null);
        }

        public Builder rightJoin(DbColumn parentColumn, DbColumn column, String tableAlias, Predicate<DbColumn> columnSelector,
                Function<DbColumn, String> aliasMapper) {
            return join(parentColumn, TableJoinType.RIGHT, column, tableAlias,
                    (null == columnSelector) ? SchemaHelper.getTableColumns(column.getTable()).iterator()
                            : SchemaHelper.getTableColumns(column.getTable(), columnSelector).iterator(), aliasMapper);
        }

        public Builder rightJoin(DbColumn parentColumn, DbColumn column, String tableAlias, Predicate<DbColumn> columnSelector) {
            return join(parentColumn, TableJoinType.RIGHT, column, tableAlias,
                    (null == columnSelector) ? SchemaHelper.getTableColumns(column.getTable()).iterator()
                            : SchemaHelper.getTableColumns(column.getTable(), columnSelector).iterator(), null);
        }

        public Builder rightJoin(DbColumn parentColumn, DbColumn column, String tableAlias, Function<DbColumn, String> aliasMapper) {
            return join(parentColumn, TableJoinType.RIGHT, column, tableAlias, SchemaHelper.getTableColumns(column.getTable()).iterator(),
                    aliasMapper);
        }

        public Builder rightJoin(DbColumn parentColumn, DbColumn column, String tableAlias, boolean noColumns) {
            return join(parentColumn, TableJoinType.RIGHT, column, tableAlias, (noColumns) ? Collections.emptyIterator()
                    : SchemaHelper.getTableColumns(column.getTable()).iterator(), null);
        }

        public Builder rightJoin(DbColumn parentColumn, DbColumn column, String tableAlias) {
            return join(parentColumn, TableJoinType.RIGHT, column, tableAlias, SchemaHelper.getTableColumns(column.getTable()).iterator(), null);
        }

        public Builder rightJoin(DbColumn parentColumn, DbColumn column, Predicate<DbColumn> columnSelector,
                Function<DbColumn, String> aliasMapper) {
            return join(parentColumn, TableJoinType.RIGHT, column, null,
                    (null == columnSelector) ? SchemaHelper.getTableColumns(column.getTable()).iterator()
                            : SchemaHelper.getTableColumns(column.getTable(), columnSelector).iterator(), aliasMapper);
        }

        public Builder rightJoin(DbColumn parentColumn, DbColumn column, Predicate<DbColumn> columnSelector) {
            return join(parentColumn, TableJoinType.RIGHT, column, null,
                    (null == columnSelector) ? SchemaHelper.getTableColumns(column.getTable()).iterator()
                            : SchemaHelper.getTableColumns(column.getTable(), columnSelector).iterator(), null);
        }

        public Builder rightJoin(DbColumn parentColumn, DbColumn column, Function<DbColumn, String> aliasMapper) {
            return join(parentColumn, TableJoinType.RIGHT, column, null, SchemaHelper.getTableColumns(column.getTable()).iterator(),
                    aliasMapper);
        }

        public Builder rightJoin(DbColumn parentColumn, DbColumn column, boolean noColumns) {
            return join(parentColumn, TableJoinType.RIGHT, column, null, (noColumns) ? Collections.emptyIterator()
                    : SchemaHelper.getTableColumns(column.getTable()).iterator(), null);
        }

        public Builder rightJoin(DbColumn parentColumn, DbColumn column) {
            return join(parentColumn, TableJoinType.RIGHT, column, null, SchemaHelper.getTableColumns(column.getTable()).iterator(),
                    null);
        }

        public Builder fullJoin(DbColumn parentColumn, DbColumn column, String tableAlias, Predicate<DbColumn> columnSelector,
                Function<DbColumn, String> aliasMapper) {
            return join(parentColumn, TableJoinType.FULL, column, tableAlias,
                    (null == columnSelector) ? SchemaHelper.getTableColumns(column.getTable()).iterator()
                            : SchemaHelper.getTableColumns(column.getTable(), columnSelector).iterator(), aliasMapper);
        }

        public Builder fullJoin(DbColumn parentColumn, DbColumn column, String tableAlias, Predicate<DbColumn> columnSelector) {
            return join(parentColumn, TableJoinType.FULL, column, tableAlias,
                    (null == columnSelector) ? SchemaHelper.getTableColumns(column.getTable()).iterator()
                            : SchemaHelper.getTableColumns(column.getTable(), columnSelector).iterator(), null);
        }

        public Builder fullJoin(DbColumn parentColumn, DbColumn column, String tableAlias, Function<DbColumn, String> aliasMapper) {
            return join(parentColumn, TableJoinType.FULL, column, tableAlias, SchemaHelper.getTableColumns(column.getTable()).iterator(),
                    aliasMapper);
        }

        public Builder fullJoin(DbColumn parentColumn, DbColumn column, String tableAlias, boolean noColumns) {
            return join(parentColumn, TableJoinType.FULL, column, tableAlias, (noColumns) ? Collections.emptyIterator()
                    : SchemaHelper.getTableColumns(column.getTable()).iterator(), null);
        }

        public Builder fullJoin(DbColumn parentColumn, DbColumn column, String tableAlias) {
            return join(parentColumn, TableJoinType.FULL, column, tableAlias, SchemaHelper.getTableColumns(column.getTable()).iterator(),
                    null);
        }

        public Builder fullJoin(DbColumn parentColumn, DbColumn column, Predicate<DbColumn> columnSelector,
                Function<DbColumn, String> aliasMapper) {
            return join(parentColumn, TableJoinType.FULL, column, null,
                    (null == columnSelector) ? SchemaHelper.getTableColumns(column.getTable()).iterator()
                            : SchemaHelper.getTableColumns(column.getTable(), columnSelector).iterator(), aliasMapper);
        }

        public Builder fullJoin(DbColumn parentColumn, DbColumn column, Predicate<DbColumn> columnSelector) {
            return join(parentColumn, TableJoinType.FULL, column, null,
                    (null == columnSelector) ? SchemaHelper.getTableColumns(column.getTable()).iterator()
                            : SchemaHelper.getTableColumns(column.getTable(), columnSelector).iterator(), null);
        }

        public Builder fullJoin(DbColumn parentColumn, DbColumn column, Function<DbColumn, String> aliasMapper) {
            return join(parentColumn, TableJoinType.FULL, column, null, SchemaHelper.getTableColumns(column.getTable()).iterator(), aliasMapper);
        }

        public Builder fullJoin(DbColumn parentColumn, DbColumn column, boolean noColumns) {
            return join(parentColumn, TableJoinType.FULL, column, null, (noColumns) ? Collections.emptyIterator()
                    : SchemaHelper.getTableColumns(column.getTable()).iterator(), null);
        }

        public Builder fullJoin(DbColumn parentColumn, DbColumn column) {
            return join(parentColumn, TableJoinType.FULL, column, null, SchemaHelper.getTableColumns(column.getTable()).iterator(), null);
        }

    }

    public class Join {

        private final DmlTable parentTable;
        private final DbColumn parentColumn;
        private final TableJoinType type;
        private final DbColumn childColumn;

        public DmlTable getParentTable() {
            return parentTable;
        }

        public DbColumn getParentColumn() {
            return parentColumn;
        }

        public TableJoinType getType() {
            return type;
        }

        public DmlTable getChildTable() {
            return DmlTable.this;
        }

        public DbColumn getChildColumn() {
            return childColumn;
        }

        private Join(DmlTable parentTable, DbColumn parentColumn, TableJoinType type, DbColumn childColumn) {
            this.parentTable = parentTable;
            this.parentColumn = parentColumn;
            this.type = type;
            this.childColumn = childColumn;
        }
    }
}
