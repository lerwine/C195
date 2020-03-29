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
 * Represents one or more tables involved in a DML query.
 * 
 * @author lerwi
 */
public final class DmlTable {

    /**
     * Creates a new DML table {@link Builder}.
     * 
     * @param table The primary {@link DbTable}.
     * @param alias The DML query alias to use for this table.
     * If this value is {@code null}, then the default alias will be used. If it is empty, then the database table name itself will be used.
     * @param columnSelector A {@link Predicate} that determines what data columns are included.
     * @param aliasMapper A {@link Function} that provides the DML query alias to use for each {@link DbColumn}.
     * @return A new {@link Builder} object.
     */
    public static Builder builder(DbTable table, String alias, Predicate<DbColumn> columnSelector, Function<DbColumn, String> aliasMapper) {
        Builder result = new Builder(null, table, alias, (null == columnSelector) ? SchemaHelper.getTableColumns(table).iterator()
                : SchemaHelper.getTableColumns(table, columnSelector).iterator(), aliasMapper);
        result.builderTables.put(result.alias, result);
        return result;
    }

    /**
     * Creates a new DML table {@link Builder}.
     * 
     * @param table The primary {@link DbTable}.
     * @param alias The DML query alias to use for this table.
     * If this value is {@code null}, then the default alias will be used. If it is empty, then the database table name itself will be used.
     * @param columnSelector A {@link Predicate} that determines what data columns are included.
     * @return A new {@link Builder} object.
     */
    public static Builder builder(DbTable table, String alias, Predicate<DbColumn> columnSelector) {
        return builder(table, alias, columnSelector, null);
    }

    /**
     * Creates a new DML table {@link Builder}.
     * 
     * @param table The primary {@link DbTable}.
     * @param alias The DML query alias to use for this table.
     * If this value is {@code null}, then the default alias will be used. If it is empty, then the database table name itself will be used.
     * @param aliasMapper A {@link Function} that provides the DML query alias to use for each {@link DbColumn}.
     * If this returns is {@code null}, then the default column alias will be used. If it is empty, then the database column name itself will be used.
     * @return A new {@link Builder} object.
     */
    public static Builder builder(DbTable table, String alias, Function<DbColumn, String> aliasMapper) {
        return builder(table, alias, null, aliasMapper);
    }

    /**
     * Creates a new DML table {@link Builder}.
     * 
     * @param table The primary {@link DbTable}.
     * @param alias The DML query alias to use for this table.
     * If this value is {@code null}, then the default alias will be used. If it is empty, then the database table name itself will be used.
     * @param noColumns If {@code true}, then no columns will be added; otherwise, all columns will be added.
     * @return A new {@link Builder} object.
     */
    public static Builder builder(DbTable table, String alias, boolean noColumns) {
        Builder result = new Builder(null, table, alias, (noColumns) ? Collections.emptyIterator() : SchemaHelper.getTableColumns(table).iterator(), null);
        result.builderTables.put(result.alias, result);
        return result;
    }

    /**
     * Creates a new DML table {@link Builder} with all columns added.
     * 
     * @param table The primary {@link DbTable}.
     * @param alias The DML query alias to use for this table.
     * If this value is {@code null}, then the default alias will be used. If it is empty, then the database table name itself will be used.
     * @return A new {@link Builder} object.
     */
    public static Builder builder(DbTable table, String alias) {
        return builder(table, alias, null, null);
    }

    /**
     * Creates a new DML table {@link Builder}.
     * 
     * @param table The primary {@link DbTable}.
     * @param columnSelector A {@link Predicate} that determines what data columns are included.
     * @param aliasMapper A {@link Function} that provides the DML query alias to use for each {@link DbColumn}.
     * If this returns is {@code null}, then the default column alias will be used. If it is empty, then the database column name itself will be used.
     * @return A new {@link Builder} object.
     */
    public static Builder builder(DbTable table, Predicate<DbColumn> columnSelector, Function<DbColumn, String> aliasMapper) {
        return builder(table, null, columnSelector, aliasMapper);
    }

    /**
     * Creates a new DML table {@link Builder}.
     * 
     * @param table The primary {@link DbTable}.
     * @param columnSelector A {@link Predicate} that determines what data columns are included.
     * @return A new {@link Builder} object.
     */
    public static Builder builder(DbTable table, Predicate<DbColumn> columnSelector) {
        return builder(table, null, columnSelector, null);
    }

    /**
     * Creates a new DML table {@link Builder}.
     * 
     * @param table The primary {@link DbTable}.
     * @param aliasMapper A {@link Function} that provides the DML query alias to use for each {@link DbColumn}.
     * If this returns is {@code null}, then the default column alias will be used. If it is empty, then the database column name itself will be used.
     * @return A new {@link Builder} object.
     */
    public static Builder builder(DbTable table, Function<DbColumn, String> aliasMapper) {
        return builder(table, null, null, aliasMapper);
    }

    /**
     * Creates a new DML table {@link Builder}.
     * 
     * @param table The primary {@link DbTable}.
     * @param noColumns If {@code true}, then no columns will be added; otherwise, all columns will be added.
     * @return A new {@link Builder} object.
     */
    public static Builder builder(DbTable table, boolean noColumns) {
        return builder(table, null, noColumns);
    }

    /**
     * Creates a new DML table {@link Builder} with all columns added.
     * 
     * @param table The primary {@link DbTable}.
     * @return A new {@link Builder} object.
     */
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

    /**
     * Gets the alias that will be used for the current table in the DML query string.
     * 
     * @return The alias that will be used for the current table in the DML query string.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the value of the current table.
     * 
     * @return A {@link DbTable} value representing the current table.
     */
    public DbTable getTable() {
        return table;
    }

    /**
     * Gets the parent join relationship.
     * 
     * @return A {@link Join} object representing the parent join relationship or {@link null} if this is the primary DML query table.
     */
    public Join getParent() {
        return parent;
    }

    /**
     * Gets child tables that is joined to the current table.
     * 
     * @return A {@link List} of {@link Join} objects representing foreign key relationships.
     */
    public List<Join> getJoins() {
        return joins;
    }

    /**
     * Gets {@link DmlColumn} objects representing columns from the current table that will be included in the DML query string.
     * 
     * @return A {@link List} of {@link DmlColumn} objects representing columns from the current table that will be included in the DML query string.
     */
    public List<DmlColumn> getColumns() {
        return columns;
    }

    /**
     * Gets a mapping of all tables to be included in the result DML query string.
     * 
     * @return A {@link Map} of all {@link DmlTable}s to be included in the result DML query string, keyed by their DML query alias.
     */
    public Map<String, DmlTable> getAllTables() {
        return allTables;
    }

    /**
     * Gets a mapping of all columns from all tables to be included in the result DML query string.
     * 
     * @return A {@link Map} of all {@link DmlColumn}s to be included in the result DML query string, keyed by their DML query alias.
     */
    public Map<String, DmlColumn> getAllColumns() {
        return allColumns;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return null != obj && obj instanceof DmlTable && obj == this;
    }

    /**
     * A database table column for a {@link Builder}.
     */
    public static final class BuilderColumn {

        private final Builder owner;
        private final DbColumn column;
        private final String alias;
        private BuilderJoin parentJoin;
        private final ReadOnlyList.Wrapper<BuilderJoin> childJoins;

        /**
         * Gets the {@link Builder} that represents the parent table.
         * 
         * @return The {@link Builder} that represents the parent table.
         */
        public Builder getOwner() {
            return owner;
        }

        /**
         * Gets the {@link DbColumn} value for the current database column.
         * 
         * @return The {@link DbColumn} value for the current database column.
         */
        public DbColumn getColumn() {
            return column;
        }

        /**
         * Gets the alias that will be used for this column in resulting DML queries.
         * 
         * @return The alias that will be used for this column in resulting DML queries.
         */
        public String getAlias() {
            return alias;
        }

        /**
         * Gets the {@link BuilderJoin} object that represents the parent table join.
         * 
         * @return The {@link BuilderJoin} object that represents the parent table join or {@link null} if this is not joined as a child to another
         * column.
         */
        public BuilderJoin getParentJoin() {
            return parentJoin;
        }

        /**
         * Gets all {@link BuilderJoin} objects that represent foreign key (child) relationships from the current column.
         * 
         * @return All {@link BuilderJoin} objects that represent foreign key (child) relationships from the current column.
         */
        public ReadOnlyList<BuilderJoin> getChildJoins() {
            return childJoins.getReadOnlyList();
        }

        private BuilderColumn(Builder owner, DbColumn column, String alias) {
            this.owner = owner;
            this.column = column;
            this.alias = (null == alias) ? column.toString() : ((alias.trim().isEmpty()) ? column.getDbName().toString() : alias);
            childJoins = new ReadOnlyList.Wrapper<>();
        }

        private DmlColumn build(DmlTable owner, ReadOnlyList<Join> joins) {
            return DmlColumn.of(column, alias, owner, joins);
        }

    }

    /**
     * Represents a pair of joined {@link Builder} objects representing joined tables.
     */
    public static final class BuilderJoin {

        private final Builder parentTable;
        private final Builder childTable;
        private final DbColumn parentColumn;
        private final TableJoinType type;
        private final DbColumn childColumn;

        /**
         * Gets the {@link Builder} object that represents the parent table.
         * 
         * @return The {@link Builder} object that represents the parent table.
         */
        public Builder getParentTable() {
            return parentTable;
        }

        /**
         * Gets the {@link DbColumn} on the parent {@link Builder} that the relationship is bound to.
         * 
         * @return The {@link DbColumn} on the parent {@link Builder} that the relationship is bound to.
         */
        public DbColumn getParentColumn() {
            return parentColumn;
        }

        /**
         * Gets the join type.
         * 
         * @return A {@link TableJoinType} value that represents the join type.
         */
        public TableJoinType getType() {
            return type;
        }

        /**
         * Gets the {@link Builder} object that represents the child table.
         * 
         * @return The {@link Builder} object that represents the child table.
         */
        public Builder getChildTable() {
            return childTable;
        }

        /**
         * Gets the {@link DbColumn} on the child {@link Builder} that the relationship is bound to.
         * 
         * @return The {@link DbColumn} on the child {@link Builder} that the relationship is bound to.
         */
        public DbColumn getChildColumn() {
            return childColumn;
        }

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

    /**
     * Represents an object for building one or more joined {@link DmlTable} objects.
     */
    public static final class Builder {

        private final Builder parent;
        private final DbTable table;
        private final String alias;
        private final ReadOnlyList.Wrapper<BuilderColumn> builderColumns;
        private final ReadOnlyList.Wrapper<BuilderJoin> builderJoins;
        private final ReadOnlyMap.Wrapper<String, Builder> builderTables;
        private final ReadOnlyMap.Wrapper<String, BuilderColumn> builderColumnMap;

        private Join build(DmlTable parent, BuildContext context, BuilderJoin builderJoin) {
            ReadOnlyList.Builder<Join> resultJoinList = new ReadOnlyList.Builder<>(builderJoins.size());
            int startIndex = context.allOrderedColumns.size();
            DmlTable dmlTable = new DmlTable(table, alias,
                    context.allOrderedColumns.build().subList(startIndex, startIndex + builderColumnMap.size()), resultJoinList.build(),
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

        /**
         * Builds a {@link DmlTable} object.
         * 
         * @return A newly-constructed {@link DmlTable} object.
         */
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

        /**
         * Gets the value of the data table being build.
         * 
         * @return The {@link DbTable} value representing the data table being built.
         */
        public DbTable getTable() {
            return table;
        }

        /**
         * Gets the alias that will be used for this table in DML query strings.
         * 
         * @return The alias that will be used for this table in DML query strings.
         */
        public String getAlias() {
            return alias;
        }

        /**
         * Gets the parent Builder object in a join relationship.
         * 
         * @return The parent Builder object in a join relationship or {@link null} if this is the builder for the primary table.
         */
        public Builder getParent() {
            return parent;
        }

        /**
         * Gets the columns that will be included in the DML query.
         * 
         * @return The columns that will be included in the DML query.
         */
        public List<BuilderColumn> getColumns() {
            return builderColumns.getReadOnlyList();
        }

        /**
         * Gets tables that are directly joined to the current table in foreign-key (child) relationships.
         * @return 
         */
        public List<BuilderJoin> getJoinedTables() {
            return builderJoins.getReadOnlyList();
        }

        /**
         * Gets all tables that directly or indirectly joined to the current table.
         * 
         * @return All tables that directly or indirectly joined to the current table, including all child and parent relationships.
         */
        public Map<String, Builder> getAllTables() {
            return builderTables.getReadOnlyMap();
        }

        /**
         * Gets all columns from all tables that directly or indirectly joined to the current table.
         * 
         * @return All columns from all tables that directly or indirectly joined to the current table.
         */
        public Map<String, BuilderColumn> getAllColumns() {
            return builderColumnMap.getReadOnlyMap();
        }

        private Builder(Builder parent, DbTable table, String alias, Iterator<DbColumn> iterator, Function<DbColumn, String> aliasMapper) {
            this.parent = parent;
            this.table = table;
            this.alias = (null == alias) ? table.toString() : ((alias.trim().isEmpty()) ? table.getDbName().toString() : alias);

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
                    assert t.getTable() == table : "Column does not belong to current table";
                    String a = aliasMapper.apply(t);
                    if (!builderColumnMap.containsKey(a)) {
                        BuilderColumn c = new BuilderColumn(this, t, a);
                        builderColumnMap.put(a, c);
                        builderColumns.add(c);
                    }
                });
            } else {
                iterator.forEachRemaining((t) -> {
                    assert t.getTable() == table : "Column does not belong to current table";
                    String a = t.toString();
                    if (!builderColumnMap.containsKey(a)) {
                        BuilderColumn c = new BuilderColumn(this, t, a);
                        builderColumnMap.put(a, c);
                        builderColumns.add(c);
                    }
                });
            }
            builderJoins = new ReadOnlyList.Wrapper<>();
        }

        /**
         * Joins the current table as the parent to another table.
         * 
         * @param parentColumn The parent column from the current table.
         * @param type The join type.
         * @param childColumn The child column in for the child table.
         * @param tableAlias The alias to use for the child table.
         * If this value is {@code null}, then the default alias will be used. If it is empty, then the database table name itself will be used.
         * @param columns The child columns to be added to the joined table builder.
         * @param aliasMapper A {@link Function} that provides the DML query alias to use for each {@link DbColumn}.
         * If this returns is {@code null}, then the default column alias will be used.
         * If it is empty, then the database column name itself will be used.
         * @return The Builder for the joined child table.
         */
        public Builder join(DbColumn parentColumn, TableJoinType type, DbColumn childColumn, String tableAlias, Iterator<DbColumn> columns,
                Function<DbColumn, String> aliasMapper) {
            assert parentColumn.getTable() == table : "Parent column does not belong to the current table";
            if (null == tableAlias) {
                tableAlias = childColumn.getTable().toString();
            } else if (tableAlias.trim().isEmpty() || tableAlias.equals(childColumn.getTable().getDbName().toString())) {
                tableAlias = "";
            }

            assert !builderTables.containsKey(tableAlias) : "Table alias not available.";
            
            Builder result = new Builder(this, childColumn.getTable(), tableAlias, columns, aliasMapper);
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

        /**
         * Creates an inner-join relationship to another table as the child table.
         * 
         * @param parentColumn The parent column from the current table.
         * @param childColumn The child column in for the child table.
         * @param tableAlias The alias to use for the child table.
         * If this value is {@code null}, then the default alias will be used. If it is empty, then the database table name itself will be used.
         * @param columnSelector A {@link Predicate} that determines what columns from the child table are to be included in the DML query.
         * @param aliasMapper A {@link Function} that provides the DML query alias to use for each {@link DbColumn}.
         * If this returns is {@code null}, then the default column alias will be used.
         * If it is empty, then the database column name itself will be used.
         * @return The Builder for the joined child table.
         */
        public Builder innerJoin(DbColumn parentColumn, DbColumn childColumn, String tableAlias, Predicate<DbColumn> columnSelector,
                Function<DbColumn, String> aliasMapper) {
            return join(parentColumn, TableJoinType.INNER, childColumn, tableAlias,
                    (null == columnSelector) ? SchemaHelper.getTableColumns(childColumn.getTable()).iterator()
                            : SchemaHelper.getTableColumns(childColumn.getTable(), columnSelector).iterator(), aliasMapper);
        }

        /**
         * Creates an inner-join relationship to another table as the child table.
         * 
         * @param parentColumn The parent column from the current table.
         * @param childColumn The child column in for the child table.
         * @param tableAlias The alias to use for the child table.
         * If this value is {@code null}, then the default alias will be used. If it is empty, then the database table name itself will be used.
         * @param columnSelector A {@link Predicate} that determines what columns from the child table are to be included in the DML query.
         * @return The Builder for the joined child table.
         */
        public Builder innerJoin(DbColumn parentColumn, DbColumn childColumn, String tableAlias, Predicate<DbColumn> columnSelector) {
            return join(parentColumn, TableJoinType.INNER, childColumn, tableAlias,
                    (null == columnSelector) ? SchemaHelper.getTableColumns(childColumn.getTable()).iterator()
                            : SchemaHelper.getTableColumns(childColumn.getTable(), columnSelector).iterator(), null);
        }

        /**
         * Creates an inner-join relationship to another table as the child table.
         * 
         * @param parentColumn The parent column from the current table.
         * @param childColumn The child column in for the child table.
         * @param tableAlias The alias to use for the child table.
         * If this value is {@code null}, then the default alias will be used. If it is empty, then the database table name itself will be used.
         * @param aliasMapper A {@link Function} that provides the DML query alias to use for each {@link DbColumn}.
         * If this returns is {@code null}, then the default column alias will be used.
         * If it is empty, then the database column name itself will be used.
         * @return The Builder for the joined child table.
         */
        public Builder innerJoin(DbColumn parentColumn, DbColumn childColumn, String tableAlias, Function<DbColumn, String> aliasMapper) {
            return join(parentColumn, TableJoinType.INNER, childColumn, tableAlias, SchemaHelper.getTableColumns(childColumn.getTable()).iterator(),
                    aliasMapper);
        }

        /**
         * Creates an inner-join relationship to another table as the child table.
         * 
         * @param parentColumn The parent column from the current table.
         * @param childColumn The child column in for the child table.
         * @param tableAlias The alias to use for the child table.
         * If this value is {@code null}, then the default alias will be used. If it is empty, then the database table name itself will be used.
         * @param noColumns If {@code true}, then no columns will be included; otherwise, all possible columns from the child table will be included.
         * @return The Builder for the joined child table.
         */
        public Builder innerJoin(DbColumn parentColumn, DbColumn childColumn, String tableAlias, boolean noColumns) {
            return join(parentColumn, TableJoinType.INNER, childColumn, tableAlias, (noColumns) ? Collections.emptyIterator()
                    : SchemaHelper.getTableColumns(childColumn.getTable()).iterator(), null);
        }

        /**
         * Creates an inner-join relationship to another table as the child table.
         * 
         * @param parentColumn The parent column from the current table.
         * @param childColumn The child column in for the child table.
         * @param tableAlias The alias to use for the child table.
         * If this value is {@code null}, then the default alias will be used. If it is empty, then the database table name itself will be used.
         * @return The Builder for the joined child table.
         */
        public Builder innerJoin(DbColumn parentColumn, DbColumn childColumn, String tableAlias) {
            return join(parentColumn, TableJoinType.INNER, childColumn, tableAlias, SchemaHelper.getTableColumns(childColumn.getTable()).iterator(),
                    null);
        }

        /**
         * Creates an inner-join relationship to another table as the child table.
         * 
         * @param parentColumn The parent column from the current table.
         * @param childColumn The child column in for the child table.
         * @param columnSelector A {@link Predicate} that determines what columns from the child table are to be included in the DML query.
         * @param aliasMapper A {@link Function} that provides the DML query alias to use for each {@link DbColumn}.
         * If this returns is {@code null}, then the default column alias will be used.
         * If it is empty, then the database column name itself will be used.
         * @return The Builder for the joined child table.
         */
        public Builder innerJoin(DbColumn parentColumn, DbColumn childColumn, Predicate<DbColumn> columnSelector,
                Function<DbColumn, String> aliasMapper) {
            return join(parentColumn, TableJoinType.INNER, childColumn, null,
                    (null == columnSelector) ? SchemaHelper.getTableColumns(childColumn.getTable()).iterator()
                            : SchemaHelper.getTableColumns(childColumn.getTable(), columnSelector).iterator(), aliasMapper);
        }

        /**
         * Creates an inner-join relationship to another table as the child table.
         * 
         * @param parentColumn The parent column from the current table.
         * @param childColumn The child column in for the child table.
         * @param columnSelector A {@link Predicate} that determines what columns from the child table are to be included in the DML query.
         * @return The Builder for the joined child table.
         */
        public Builder innerJoin(DbColumn parentColumn, DbColumn childColumn, Predicate<DbColumn> columnSelector) {
            return join(parentColumn, TableJoinType.INNER, childColumn, null,
                    (null == columnSelector) ? SchemaHelper.getTableColumns(childColumn.getTable()).iterator()
                            : SchemaHelper.getTableColumns(childColumn.getTable(), columnSelector).iterator(), null);
        }

        /**
         * Creates an inner-join relationship to another table as the child table.
         * 
         * @param parentColumn The parent column from the current table.
         * @param childColumn The child column in for the child table.
         * @param aliasMapper A {@link Function} that provides the DML query alias to use for each {@link DbColumn}.
         * If this returns is {@code null}, then the default column alias will be used.
         * If it is empty, then the database column name itself will be used.
         * @return The Builder for the joined child table.
         */
        public Builder innerJoin(DbColumn parentColumn, DbColumn childColumn, Function<DbColumn, String> aliasMapper) {
            return join(parentColumn, TableJoinType.INNER, childColumn, null, SchemaHelper.getTableColumns(childColumn.getTable()).iterator(),
                    aliasMapper);
        }

        /**
         * Creates an inner-join relationship to another table as the child table.
         * 
         * @param parentColumn The parent column from the current table.
         * @param childColumn The child column in for the child table.
         * @param noColumns If {@code true}, then no columns will be included; otherwise, all possible columns from the child table will be included.
         * @return The Builder for the joined child table.
         */
        public Builder innerJoin(DbColumn parentColumn, DbColumn childColumn, boolean noColumns) {
            return join(parentColumn, TableJoinType.INNER, childColumn, null, (noColumns) ? Collections.emptyIterator()
                    : SchemaHelper.getTableColumns(childColumn.getTable()).iterator(), null);
        }

        /**
         * Creates an inner-join relationship to another table as the child table.
         * 
         * @param parentColumn The parent column from the current table.
         * @param childColumn The child column in for the child table.
         * @return The Builder for the joined child table.
         */
        public Builder innerJoin(DbColumn parentColumn, DbColumn childColumn) {
            return join(parentColumn, TableJoinType.INNER, childColumn, null, SchemaHelper.getTableColumns(childColumn.getTable()).iterator(),
                    null);
        }

        /**
         * Creates an left-join relationship to another table as the child table.
         * 
         * @param parentColumn The parent column from the current table.
         * @param childColumn The child column in for the child table.
         * @param tableAlias The alias to use for the child table.
         * If this value is {@code null}, then the default alias will be used. If it is empty, then the database table name itself will be used.
         * @param columnSelector A {@link Predicate} that determines what columns from the child table are to be included in the DML query.
         * @param aliasMapper A {@link Function} that provides the DML query alias to use for each {@link DbColumn}.
         * If this returns is {@code null}, then the default column alias will be used.
         * If it is empty, then the database column name itself will be used.
         * @return The Builder for the joined child table.
         */
        public Builder leftJoin(DbColumn parentColumn, DbColumn childColumn, String tableAlias, Predicate<DbColumn> columnSelector,
                Function<DbColumn, String> aliasMapper) {
            return join(parentColumn, TableJoinType.LEFT, childColumn, tableAlias,
                    (null == columnSelector) ? SchemaHelper.getTableColumns(childColumn.getTable()).iterator()
                            : SchemaHelper.getTableColumns(childColumn.getTable(), columnSelector).iterator(), aliasMapper);
        }

        /**
         * Creates an left-join relationship to another table as the child table.
         * 
         * @param parentColumn The parent column from the current table.
         * @param childColumn The child column in for the child table.
         * @param tableAlias The alias to use for the child table.
         * If this value is {@code null}, then the default alias will be used. If it is empty, then the database table name itself will be used.
         * @param columnSelector A {@link Predicate} that determines what columns from the child table are to be included in the DML query.
         * @return The Builder for the joined child table.
         */
        public Builder leftJoin(DbColumn parentColumn, DbColumn childColumn, String tableAlias, Predicate<DbColumn> columnSelector) {
            return join(parentColumn, TableJoinType.LEFT, childColumn, tableAlias,
                    (null == columnSelector) ? SchemaHelper.getTableColumns(childColumn.getTable()).iterator()
                            : SchemaHelper.getTableColumns(childColumn.getTable(), columnSelector).iterator(), null);
        }

        /**
         * Creates an left-join relationship to another table as the child table.
         * 
         * @param parentColumn The parent column from the current table.
         * @param childColumn The child column in for the child table.
         * @param tableAlias The alias to use for the child table.
         * If this value is {@code null}, then the default alias will be used. If it is empty, then the database table name itself will be used.
         * @param aliasMapper A {@link Function} that provides the DML query alias to use for each {@link DbColumn}.
         * If this returns is {@code null}, then the default column alias will be used.
         * If it is empty, then the database column name itself will be used.
         * @return The Builder for the joined child table.
         */
        public Builder leftJoin(DbColumn parentColumn, DbColumn childColumn, String tableAlias, Function<DbColumn, String> aliasMapper) {
            return join(parentColumn, TableJoinType.LEFT, childColumn, tableAlias, SchemaHelper.getTableColumns(childColumn.getTable()).iterator(),
                    aliasMapper);
        }

        /**
         * Creates an left-join relationship to another table as the child table.
         * 
         * @param parentColumn The parent column from the current table.
         * @param childColumn The child column in for the child table.
         * @param tableAlias The alias to use for the child table.
         * If this value is {@code null}, then the default alias will be used. If it is empty, then the database table name itself will be used.
         * @param noColumns If {@code true}, then no columns will be included; otherwise, all possible columns from the child table will be included.
         * @return The Builder for the joined child table.
         */
        public Builder leftJoin(DbColumn parentColumn, DbColumn childColumn, String tableAlias, boolean noColumns) {
            return join(parentColumn, TableJoinType.LEFT, childColumn, tableAlias, (noColumns) ? Collections.emptyIterator()
                    : SchemaHelper.getTableColumns(childColumn.getTable()).iterator(), null);
        }

        /**
         * Creates an left-join relationship to another table as the child table.
         * 
         * @param parentColumn The parent column from the current table.
         * @param childColumn The child column in for the child table.
         * @param tableAlias The alias to use for the child table.
         * If this value is {@code null}, then the default alias will be used. If it is empty, then the database table name itself will be used.
         * @return The Builder for the joined child table.
         */
        public Builder leftJoin(DbColumn parentColumn, DbColumn childColumn, String tableAlias) {
            return join(parentColumn, TableJoinType.LEFT, childColumn, tableAlias, SchemaHelper.getTableColumns(childColumn.getTable()).iterator(),
                    null);
        }

        /**
         * Creates an left-join relationship to another table as the child table.
         * 
         * @param parentColumn The parent column from the current table.
         * @param childColumn The child column in for the child table.
         * @param columnSelector A {@link Predicate} that determines what columns from the child table are to be included in the DML query.
         * @param aliasMapper A {@link Function} that provides the DML query alias to use for each {@link DbColumn}.
         * If this returns is {@code null}, then the default column alias will be used.
         * If it is empty, then the database column name itself will be used.
         * @return The Builder for the joined child table.
         */
        public Builder leftJoin(DbColumn parentColumn, DbColumn childColumn, Predicate<DbColumn> columnSelector,
                Function<DbColumn, String> aliasMapper) {
            return join(parentColumn, TableJoinType.LEFT, childColumn, null,
                    (null == columnSelector) ? SchemaHelper.getTableColumns(childColumn.getTable()).iterator()
                            : SchemaHelper.getTableColumns(childColumn.getTable(), columnSelector).iterator(), aliasMapper);
        }

        /**
         * Creates an left-join relationship to another table as the child table.
         * 
         * @param parentColumn The parent column from the current table.
         * @param childColumn The child column in for the child table.
         * @param columnSelector A {@link Predicate} that determines what columns from the child table are to be included in the DML query.
         * @return The Builder for the joined child table.
         */
        public Builder leftJoin(DbColumn parentColumn, DbColumn childColumn, Predicate<DbColumn> columnSelector) {
            return join(parentColumn, TableJoinType.LEFT, childColumn, null,
                    (null == columnSelector) ? SchemaHelper.getTableColumns(childColumn.getTable()).iterator()
                            : SchemaHelper.getTableColumns(childColumn.getTable(), columnSelector).iterator(), null);
        }

        /**
         * Creates an left-join relationship to another table as the child table.
         * 
         * @param parentColumn The parent column from the current table.
         * @param childColumn The child column in for the child table.
         * @param aliasMapper A {@link Function} that provides the DML query alias to use for each {@link DbColumn}.
         * If this returns is {@code null}, then the default column alias will be used.
         * If it is empty, then the database column name itself will be used.
         * @return The Builder for the joined child table.
         */
        public Builder leftJoin(DbColumn parentColumn, DbColumn childColumn, Function<DbColumn, String> aliasMapper) {
            return join(parentColumn, TableJoinType.LEFT, childColumn, null, SchemaHelper.getTableColumns(childColumn.getTable()).iterator(),
                    aliasMapper);
        }

        /**
         * Creates an left-join relationship to another table as the child table.
         * 
         * @param parentColumn The parent column from the current table.
         * @param childColumn The child column in for the child table.
         * @param noColumns If {@code true}, then no columns will be included; otherwise, all possible columns from the child table will be included.
         * @return The Builder for the joined child table.
         */
        public Builder leftJoin(DbColumn parentColumn, DbColumn childColumn, boolean noColumns) {
            return join(parentColumn, TableJoinType.LEFT, childColumn, null, (noColumns) ? Collections.emptyIterator()
                    : SchemaHelper.getTableColumns(childColumn.getTable()).iterator(), null);
        }

        /**
         * Creates an left-join relationship to another table as the child table.
         * 
         * @param parentColumn The parent column from the current table.
         * @param childColumn The child column in for the child table.
         * @return The Builder for the joined child table.
         */
        public Builder leftJoin(DbColumn parentColumn, DbColumn childColumn) {
            return join(parentColumn, TableJoinType.LEFT, childColumn, null, SchemaHelper.getTableColumns(childColumn.getTable()).iterator(),
                    null);
        }

        /**
         * Creates an right-join relationship to another table as the child table.
         * 
         * @param parentColumn The parent column from the current table.
         * @param childColumn The child column in for the child table.
         * @param tableAlias The alias to use for the child table.
         * If this value is {@code null}, then the default alias will be used. If it is empty, then the database table name itself will be used.
         * @param columnSelector A {@link Predicate} that determines what columns from the child table are to be included in the DML query.
         * @param aliasMapper A {@link Function} that provides the DML query alias to use for each {@link DbColumn}.
         * If this returns is {@code null}, then the default column alias will be used.
         * If it is empty, then the database column name itself will be used.
         * @return The Builder for the joined child table.
         */
        public Builder rightJoin(DbColumn parentColumn, DbColumn childColumn, String tableAlias, Predicate<DbColumn> columnSelector,
                Function<DbColumn, String> aliasMapper) {
            return join(parentColumn, TableJoinType.RIGHT, childColumn, tableAlias,
                    (null == columnSelector) ? SchemaHelper.getTableColumns(childColumn.getTable()).iterator()
                            : SchemaHelper.getTableColumns(childColumn.getTable(), columnSelector).iterator(), aliasMapper);
        }

        /**
         * Creates an right-join relationship to another table as the child table.
         * 
         * @param parentColumn The parent column from the current table.
         * @param childColumn The child column in for the child table.
         * @param tableAlias The alias to use for the child table.
         * If this value is {@code null}, then the default alias will be used. If it is empty, then the database table name itself will be used.
         * @param columnSelector A {@link Predicate} that determines what columns from the child table are to be included in the DML query.
         * @return The Builder for the joined child table.
         */
        public Builder rightJoin(DbColumn parentColumn, DbColumn childColumn, String tableAlias, Predicate<DbColumn> columnSelector) {
            return join(parentColumn, TableJoinType.RIGHT, childColumn, tableAlias,
                    (null == columnSelector) ? SchemaHelper.getTableColumns(childColumn.getTable()).iterator()
                            : SchemaHelper.getTableColumns(childColumn.getTable(), columnSelector).iterator(), null);
        }

        /**
         * Creates an right-join relationship to another table as the child table.
         * 
         * @param parentColumn The parent column from the current table.
         * @param childColumn The child column in for the child table.
         * @param tableAlias The alias to use for the child table.
         * If this value is {@code null}, then the default alias will be used. If it is empty, then the database table name itself will be used.
         * @param aliasMapper A {@link Function} that provides the DML query alias to use for each {@link DbColumn}.
         * If this returns is {@code null}, then the default column alias will be used.
         * If it is empty, then the database column name itself will be used.
         * @return The Builder for the joined child table.
         */
        public Builder rightJoin(DbColumn parentColumn, DbColumn childColumn, String tableAlias, Function<DbColumn, String> aliasMapper) {
            return join(parentColumn, TableJoinType.RIGHT, childColumn, tableAlias, SchemaHelper.getTableColumns(childColumn.getTable()).iterator(),
                    aliasMapper);
        }

        /**
         * Creates an right-join relationship to another table as the child table.
         * 
         * @param parentColumn The parent column from the current table.
         * @param childColumn The child column in for the child table.
         * @param tableAlias The alias to use for the child table.
         * If this value is {@code null}, then the default alias will be used. If it is empty, then the database table name itself will be used.
         * @param noColumns If {@code true}, then no columns will be included; otherwise, all possible columns from the child table will be included.
         * @return The Builder for the joined child table.
         */
        public Builder rightJoin(DbColumn parentColumn, DbColumn childColumn, String tableAlias, boolean noColumns) {
            return join(parentColumn, TableJoinType.RIGHT, childColumn, tableAlias, (noColumns) ? Collections.emptyIterator()
                    : SchemaHelper.getTableColumns(childColumn.getTable()).iterator(), null);
        }

        /**
         * Creates an right-join relationship to another table as the child table.
         * 
         * @param parentColumn The parent column from the current table.
         * @param childColumn The child column in for the child table.
         * @param tableAlias The alias to use for the child table.
         * If this value is {@code null}, then the default alias will be used. If it is empty, then the database table name itself will be used.
         * @return The Builder for the joined child table.
         */
        public Builder rightJoin(DbColumn parentColumn, DbColumn childColumn, String tableAlias) {
            return join(parentColumn, TableJoinType.RIGHT, childColumn, tableAlias, SchemaHelper.getTableColumns(childColumn.getTable()).iterator(), null);
        }

        /**
         * Creates an right-join relationship to another table as the child table.
         * 
         * @param parentColumn The parent column from the current table.
         * @param childColumn The child column in for the child table.
         * @param columnSelector A {@link Predicate} that determines what columns from the child table are to be included in the DML query.
         * @param aliasMapper A {@link Function} that provides the DML query alias to use for each {@link DbColumn}.
         * If this returns is {@code null}, then the default column alias will be used.
         * If it is empty, then the database column name itself will be used.
         * @return The Builder for the joined child table.
         */
        public Builder rightJoin(DbColumn parentColumn, DbColumn childColumn, Predicate<DbColumn> columnSelector,
                Function<DbColumn, String> aliasMapper) {
            return join(parentColumn, TableJoinType.RIGHT, childColumn, null,
                    (null == columnSelector) ? SchemaHelper.getTableColumns(childColumn.getTable()).iterator()
                            : SchemaHelper.getTableColumns(childColumn.getTable(), columnSelector).iterator(), aliasMapper);
        }

        /**
         * Creates an right-join relationship to another table as the child table.
         * 
         * @param parentColumn The parent column from the current table.
         * @param childColumn The child column in for the child table.
         * @param columnSelector A {@link Predicate} that determines what columns from the child table are to be included in the DML query.
         * @return The Builder for the joined child table.
         */
        public Builder rightJoin(DbColumn parentColumn, DbColumn childColumn, Predicate<DbColumn> columnSelector) {
            return join(parentColumn, TableJoinType.RIGHT, childColumn, null,
                    (null == columnSelector) ? SchemaHelper.getTableColumns(childColumn.getTable()).iterator()
                            : SchemaHelper.getTableColumns(childColumn.getTable(), columnSelector).iterator(), null);
        }

        /**
         * Creates an right-join relationship to another table as the child table.
         * 
         * @param parentColumn The parent column from the current table.
         * @param childColumn The child column in for the child table.
         * @param aliasMapper A {@link Function} that provides the DML query alias to use for each {@link DbColumn}.
         * If this returns is {@code null}, then the default column alias will be used.
         * If it is empty, then the database column name itself will be used.
         * @return The Builder for the joined child table.
         */
        public Builder rightJoin(DbColumn parentColumn, DbColumn childColumn, Function<DbColumn, String> aliasMapper) {
            return join(parentColumn, TableJoinType.RIGHT, childColumn, null, SchemaHelper.getTableColumns(childColumn.getTable()).iterator(),
                    aliasMapper);
        }

        /**
         * Creates an right-join relationship to another table as the child table.
         * 
         * @param parentColumn The parent column from the current table.
         * @param childColumn The child column in for the child table.
         * @param noColumns If {@code true}, then no columns will be included; otherwise, all possible columns from the child table will be included.
         * @return The Builder for the joined child table.
         */
        public Builder rightJoin(DbColumn parentColumn, DbColumn childColumn, boolean noColumns) {
            return join(parentColumn, TableJoinType.RIGHT, childColumn, null, (noColumns) ? Collections.emptyIterator()
                    : SchemaHelper.getTableColumns(childColumn.getTable()).iterator(), null);
        }

        /**
         * Creates an right-join relationship to another table as the child table.
         * 
         * @param parentColumn The parent column from the current table.
         * @param childColumn The child column in for the child table.
         * @return The Builder for the joined child table.
         */
        public Builder rightJoin(DbColumn parentColumn, DbColumn childColumn) {
            return join(parentColumn, TableJoinType.RIGHT, childColumn, null, SchemaHelper.getTableColumns(childColumn.getTable()).iterator(),
                    null);
        }

        /**
         * Creates an full-join relationship to another table as the child table.
         * 
         * @param parentColumn The parent column from the current table.
         * @param childColumn The child column in for the child table.
         * @param tableAlias The alias to use for the child table.
         * If this value is {@code null}, then the default alias will be used. If it is empty, then the database table name itself will be used.
         * @param columnSelector A {@link Predicate} that determines what columns from the child table are to be included in the DML query.
         * @param aliasMapper A {@link Function} that provides the DML query alias to use for each {@link DbColumn}.
         * If this returns is {@code null}, then the default column alias will be used.
         * If it is empty, then the database column name itself will be used.
         * @return The Builder for the joined child table.
         */
        public Builder fullJoin(DbColumn parentColumn, DbColumn childColumn, String tableAlias, Predicate<DbColumn> columnSelector,
                Function<DbColumn, String> aliasMapper) {
            return join(parentColumn, TableJoinType.FULL, childColumn, tableAlias,
                    (null == columnSelector) ? SchemaHelper.getTableColumns(childColumn.getTable()).iterator()
                            : SchemaHelper.getTableColumns(childColumn.getTable(), columnSelector).iterator(), aliasMapper);
        }

        /**
         * Creates an full-join relationship to another table as the child table.
         * 
         * @param parentColumn The parent column from the current table.
         * @param childColumn The child column in for the child table.
         * @param tableAlias The alias to use for the child table.
         * If this value is {@code null}, then the default alias will be used. If it is empty, then the database table name itself will be used.
         * @param columnSelector A {@link Predicate} that determines what columns from the child table are to be included in the DML query.
         * @return The Builder for the joined child table.
         */
        public Builder fullJoin(DbColumn parentColumn, DbColumn childColumn, String tableAlias, Predicate<DbColumn> columnSelector) {
            return join(parentColumn, TableJoinType.FULL, childColumn, tableAlias,
                    (null == columnSelector) ? SchemaHelper.getTableColumns(childColumn.getTable()).iterator()
                            : SchemaHelper.getTableColumns(childColumn.getTable(), columnSelector).iterator(), null);
        }

        /**
         * Creates an full-join relationship to another table as the child table.
         * 
         * @param parentColumn The parent column from the current table.
         * @param childColumn The child column in for the child table.
         * @param tableAlias The alias to use for the child table.
         * If this value is {@code null}, then the default alias will be used. If it is empty, then the database table name itself will be used.
         * @param aliasMapper A {@link Function} that provides the DML query alias to use for each {@link DbColumn}.
         * If this returns is {@code null}, then the default column alias will be used.
         * If it is empty, then the database column name itself will be used.
         * @return The Builder for the joined child table.
         */
        public Builder fullJoin(DbColumn parentColumn, DbColumn childColumn, String tableAlias, Function<DbColumn, String> aliasMapper) {
            return join(parentColumn, TableJoinType.FULL, childColumn, tableAlias, SchemaHelper.getTableColumns(childColumn.getTable()).iterator(),
                    aliasMapper);
        }

        /**
         * Creates an full-join relationship to another table as the child table.
         * 
         * @param parentColumn The parent column from the current table.
         * @param childColumn The child column in for the child table.
         * @param tableAlias The alias to use for the child table.
         * If this value is {@code null}, then the default alias will be used. If it is empty, then the database table name itself will be used.
         * @param noColumns If {@code true}, then no columns will be included; otherwise, all possible columns from the child table will be included.
         * @return The Builder for the joined child table.
         */
        public Builder fullJoin(DbColumn parentColumn, DbColumn childColumn, String tableAlias, boolean noColumns) {
            return join(parentColumn, TableJoinType.FULL, childColumn, tableAlias, (noColumns) ? Collections.emptyIterator()
                    : SchemaHelper.getTableColumns(childColumn.getTable()).iterator(), null);
        }

        /**
         * Creates an full-join relationship to another table as the child table.
         * 
         * @param parentColumn The parent column from the current table.
         * @param childColumn The child column in for the child table.
         * @param tableAlias The alias to use for the child table.
         * If this value is {@code null}, then the default alias will be used. If it is empty, then the database table name itself will be used.
         * @return The Builder for the joined child table.
         */
        public Builder fullJoin(DbColumn parentColumn, DbColumn childColumn, String tableAlias) {
            return join(parentColumn, TableJoinType.FULL, childColumn, tableAlias, SchemaHelper.getTableColumns(childColumn.getTable()).iterator(),
                    null);
        }

        /**
         * Creates an full-join relationship to another table as the child table.
         * 
         * @param parentColumn The parent column from the current table.
         * @param childColumn The child column in for the child table.
         * @param columnSelector A {@link Predicate} that determines what columns from the child table are to be included in the DML query.
         * @param aliasMapper A {@link Function} that provides the DML query alias to use for each {@link DbColumn}.
         * If this returns is {@code null}, then the default column alias will be used.
         * If it is empty, then the database column name itself will be used.
         * @return The Builder for the joined child table.
         */
        public Builder fullJoin(DbColumn parentColumn, DbColumn childColumn, Predicate<DbColumn> columnSelector,
                Function<DbColumn, String> aliasMapper) {
            return join(parentColumn, TableJoinType.FULL, childColumn, null,
                    (null == columnSelector) ? SchemaHelper.getTableColumns(childColumn.getTable()).iterator()
                            : SchemaHelper.getTableColumns(childColumn.getTable(), columnSelector).iterator(), aliasMapper);
        }

        /**
         * Creates an full-join relationship to another table as the child table.
         * 
         * @param parentColumn The parent column from the current table.
         * @param childColumn The child column in for the child table.
         * @param columnSelector A {@link Predicate} that determines what columns from the child table are to be included in the DML query.
         * @return The Builder for the joined child table.
         */
        public Builder fullJoin(DbColumn parentColumn, DbColumn childColumn, Predicate<DbColumn> columnSelector) {
            return join(parentColumn, TableJoinType.FULL, childColumn, null,
                    (null == columnSelector) ? SchemaHelper.getTableColumns(childColumn.getTable()).iterator()
                            : SchemaHelper.getTableColumns(childColumn.getTable(), columnSelector).iterator(), null);
        }

        /**
         * Creates an full-join relationship to another table as the child table.
         * 
         * @param parentColumn The parent column from the current table.
         * @param childColumn The child column in for the child table.
         * @param aliasMapper A {@link Function} that provides the DML query alias to use for each {@link DbColumn}.
         * If this returns is {@code null}, then the default column alias will be used.
         * If it is empty, then the database column name itself will be used.
         * @return The Builder for the joined child table.
         */
        public Builder fullJoin(DbColumn parentColumn, DbColumn childColumn, Function<DbColumn, String> aliasMapper) {
            return join(parentColumn, TableJoinType.FULL, childColumn, null, SchemaHelper.getTableColumns(childColumn.getTable()).iterator(), aliasMapper);
        }

        /**
         * Creates an full-join relationship to another table as the child table.
         * 
         * @param parentColumn The parent column from the current table.
         * @param childColumn The child column in for the child table.
         * @param noColumns If {@code true}, then no columns will be included; otherwise, all possible columns from the child table will be included.
         * @return The Builder for the joined child table.
         */
        public Builder fullJoin(DbColumn parentColumn, DbColumn childColumn, boolean noColumns) {
            return join(parentColumn, TableJoinType.FULL, childColumn, null, (noColumns) ? Collections.emptyIterator()
                    : SchemaHelper.getTableColumns(childColumn.getTable()).iterator(), null);
        }

        /**
         * Creates an full-join relationship to another table as the child table.
         * 
         * @param parentColumn The parent column from the current table.
         * @param childColumn The child column in for the child table.
         * @return The Builder for the joined child table.
         */
        public Builder fullJoin(DbColumn parentColumn, DbColumn childColumn) {
            return join(parentColumn, TableJoinType.FULL, childColumn, null, SchemaHelper.getTableColumns(childColumn.getTable()).iterator(), null);
        }

    }

    /**
     * Represents Child/Parent join relationship between {@link DmlTable}s.
     */
    public final class Join {

        private final DmlTable parentTable;
        private final DbColumn parentColumn;
        private final TableJoinType type;
        private final DbColumn childColumn;

        /**
         * Gets the parent {@link DmlTable} object.
         * 
         * @return The {@link DmlTable} object that represents the parent table in the join relationship.
         */
        public DmlTable getParentTable() {
            return parentTable;
        }

        /**
         * Gets the {@link DbColumn} on the parent {@link DmlTable} that the relationship is bound to.
         * @return 
         */
        public DbColumn getParentColumn() {
            return parentColumn;
        }

        /**
         * Gets the join type.
         * 
         * @return A {@link TableJoinType} value that represents the join type.
         */
        public TableJoinType getType() {
            return type;
        }

        /**
         * Gets the child {@link DmlTable} object.
         * 
         * @return The {@link DmlTable} object that represents the child table in the join relationship.
         */
        public DmlTable getChildTable() {
            return DmlTable.this;
        }

        /**
         * Gets the {@link DbColumn} on the child {@link DmlTable} that the relationship is bound to.
         * @return 
         */
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
