/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.dao.dml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
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
    private final DbTable table;
    private final String name;
    private final Map<String, DmlColumn> allColumns;
    private final Map<String, DmlTable> allTables;
    private final ReadOnlyList<DmlColumn> columns;
    private final ReadOnlyList<Join> joins;
    private final DmlTable parent;
    
    private DmlTable(DbTable table, String name, ReadOnlyList<DmlColumn> columns, ReadOnlyList<Join> joins, DmlTable parent) {
        this.table = table;
        this.name = name;
        this.joins = joins;
        this.columns = columns;
        this.allColumns = (this.parent = parent).allColumns;
        this.allTables = parent.allTables;
    }

    public String getName() {
        return name;
    }

    public DbTable getTable() {
        return table;
    }
    
    public static Builder builder(DbTable table, String alias, Predicate<DbColumn> columnSelector, Function<DbColumn, String> aliasMapper) {
        HashMap<String, Builder> tableMap = new HashMap<>();
        HashMap<String, BuilderColumn> columnMap = new HashMap<>();
        Builder result = new Builder(tableMap, columnMap, table, alias, (null == columnSelector) ? SchemaHelper.getTableColumns(table).iterator() :
                        SchemaHelper.getTableColumns(table, columnSelector).iterator(), aliasMapper);
        tableMap.put(result.alias, result);
        return result;
    }

    public static Builder builder(DbTable table, String alias, Predicate<DbColumn> columnSelector) {
        return builder(table, alias, columnSelector, null);
    }

    public static Builder builder(DbTable table, String alias, Function<DbColumn, String> aliasMapper) {
        return builder(table, alias, null, aliasMapper);
    }

    public static Builder builder(DbTable table, String alias, boolean noColumns) {
        HashMap<String, Builder> tableMap = new HashMap<>();
        HashMap<String, BuilderColumn> columnMap = new HashMap<>();
        Builder result = new Builder(tableMap, columnMap, table, alias, (noColumns) ? Collections.emptyIterator() : SchemaHelper.getTableColumns(table).iterator(), null);
        tableMap.put(result.alias, result);
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

    public static class Join implements ReadOnlyList<Join> {
        
        @Override
        public int size() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean isEmpty() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean contains(Object o) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Iterator<Join> iterator() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Object[] toArray() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Join get(int index) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public int indexOf(Object o) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public int lastIndexOf(Object o) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public ListIterator<Join> listIterator() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public ListIterator<Join> listIterator(int index) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public List<Join> subList(int fromIndex, int toIndex) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        
    }
    
    public static final class BuilderColumn {
        private final Builder owner;
        private final DbColumn column;
        private final String alias;
        private BuilderJoin parentJoin;
        private final ArrayList<BuilderJoin> backingChildJoins;
        private final List<BuilderJoin> childJoins;

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

        public List<BuilderJoin> getChildJoins() {
            return childJoins;
        }
        
        private BuilderColumn(Builder owner, DbColumn column, String alias) {
            this.owner = owner;
            this.column = column;
            this.alias = (null == alias) ? column.getDefaultAlias() : ((alias.trim().isEmpty()) ? column.getDbName().toString() : alias);
            backingChildJoins = new ArrayList();
            childJoins = Collections.unmodifiableList(backingChildJoins);
        }

        private DmlColumn build(DmlTable root) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        
    }
    
    public static class BuilderJoin {

        private final Builder parentTable;
        private final Builder childTable;
        private final DbColumn parentColumn;
        private final TableJoinType type;
        private final DbColumn childColumn;

        public Builder getParentTable() {
            return parentTable;
        }

        public Builder getChildTable() {
            return childTable;
        }

        public DbColumn getParentColumn() {
            return parentColumn;
        }

        public TableJoinType getType() {
            return type;
        }

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
    
    private DmlTable(DbTable table, String name, ReadOnlyList<DmlColumn> columns, Join[] joins, Map<String, DmlColumn> allColumns,
            Map<String, DmlTable> allTables) {
        this.table = table;
        this.name = name;
        this.joins = ReadOnlyList.of(joins);
        this.columns = columns;
        this.allColumns = allColumns;
        this.allTables = allTables;
        this.parent = null;
    }

    public static class Builder {
        private final DbTable table;
        private final String alias;
        private final ArrayList<BuilderColumn> backingColumnList;
        private final List<BuilderColumn> columns;
        private final ArrayList<BuilderJoin> backingTableList;
        private final List<BuilderJoin> joinedTables;
        private final HashMap<String, Builder> backingTableMap;
        private final Map<String, Builder> allTables;
        private final HashMap<String, BuilderColumn> backingColumnMap;
        private final Map<String, BuilderColumn> allColumns;

        public DmlTable build() {
            DmlColumn[] colArray = new DmlColumn[this.allColumns.size()];
            Join[] joinArray = new Join[this.joinedTables.size()];
            HashMap<String, DmlColumn> allColumns = new HashMap<>();
            HashMap<String, DmlTable> allTables = new HashMap<>();
            DmlTable root = new DmlTable(table, alias, ReadOnlyList.of(colArray, 0, backingColumnList.size()), joinArray, allColumns, allTables);
            for (int i = 0; i < backingColumnList.size(); i++)
                colArray[i] = backingColumnList.get(i).build(root);
        }
        
        public DbTable getTable() {
            return table;
        }

        public String getAlias() {
            return alias;
        }

        public List<BuilderColumn> getColumns() {
            return columns;
        }

        public List<BuilderJoin> getJoinedTables() {
            return joinedTables;
        }

        public Map<String, Builder> getAllTables() {
            return allTables;
        }

        public Map<String, BuilderColumn> getAllColumns() {
            return allColumns;
        }
        
        private Builder(HashMap<String, Builder> backingTableMap, HashMap<String, BuilderColumn> backingColumnMap, DbTable table, String alias,
                Iterator<DbColumn> iterator, Function<DbColumn, String> aliasMapper) {
            this.table = table;
            this.alias = alias;
            backingColumnList = new ArrayList<>();
            if (null != aliasMapper) {
                iterator.forEachRemaining((t) -> {
                    String a = aliasMapper.apply(t);
                    if (!backingColumnMap.containsKey(a)) {
                        BuilderColumn c = new BuilderColumn(this, t, a);
                        backingColumnMap.put(a, c);
                        backingColumnList.add(c);
                    }
                });
            } else {
                iterator.forEachRemaining((t) -> {
                    String a = t.getDefaultAlias();
                    if (!backingColumnMap.containsKey(a)) {
                        BuilderColumn c = new BuilderColumn(this, t, a);
                        backingColumnMap.put(a, c);
                        backingColumnList.add(c);
                    }
                });
            }
            columns = Collections.unmodifiableList(backingColumnList);
            backingTableList = new ArrayList<>();
            joinedTables = Collections.unmodifiableList(backingTableList);
            this.backingTableMap = backingTableMap;
            allTables = Collections.unmodifiableMap(backingTableMap);
            this.backingColumnMap = backingColumnMap;
            allColumns = Collections.unmodifiableMap(backingColumnMap);
        }
        
        public Builder join(DbColumn parentColumn, TableJoinType type, DbColumn childColumn, String tableAlias, Iterator<DbColumn> columns,
                Function<DbColumn, String> aliasMapper) {
            if (null == tableAlias)
                tableAlias = parentColumn.getTable().getAlias();
            else if (tableAlias.trim().isEmpty() || tableAlias.equals(parentColumn.getTable().getDbName().toString()))
                tableAlias = "";
            
            if (backingTableMap.containsKey(tableAlias))
                throw new UnsupportedOperationException("Table alias not available.");
            Builder result = new Builder(backingTableMap, backingColumnMap, parentColumn.getTable(), tableAlias, columns, aliasMapper);
            backingTableMap.put(result.alias, result);
            BuilderJoin j = new BuilderJoin(this, parentColumn, type, result, childColumn);
            backingTableList.add(j);
            Optional<BuilderColumn> bc = backingColumnList.stream().filter((t) -> t.column == parentColumn).findFirst();
            if (bc.isPresent())
                bc.get().backingChildJoins.add(j);
            bc = result.backingColumnList.stream().filter((t) -> t.column == childColumn).findFirst();
            if (bc.isPresent())
                bc.get().parentJoin = j;
            return result;
        }
        
        public Builder innerJoin(DbColumn parentColumn, DbColumn column, String tableAlias, Predicate<DbColumn> columnSelector, 
                Function<DbColumn, String> aliasMapper) {
            return join(parentColumn, TableJoinType.INNER, column, tableAlias,
                    (null == columnSelector) ? SchemaHelper.getTableColumns(column.getTable()).iterator() :
                            SchemaHelper.getTableColumns(column.getTable(), columnSelector).iterator(), aliasMapper);
        }
        
        public Builder innerJoin(DbColumn parentColumn, DbColumn column, String tableAlias, Predicate<DbColumn> columnSelector) {
            return join(parentColumn, TableJoinType.INNER, column, tableAlias,
                    (null == columnSelector) ? SchemaHelper.getTableColumns(column.getTable()).iterator() :
                            SchemaHelper.getTableColumns(column.getTable(), columnSelector).iterator(), null);
        }
        
        public Builder innerJoin(DbColumn parentColumn, DbColumn column, String tableAlias, Function<DbColumn, String> aliasMapper) {
            return join(parentColumn, TableJoinType.INNER, column, tableAlias, SchemaHelper.getTableColumns(column.getTable()).iterator(),
                    aliasMapper);
        }
        
        public Builder innerJoin(DbColumn parentColumn, DbColumn column, String tableAlias, boolean noColumns) {
            return join(parentColumn, TableJoinType.INNER, column, tableAlias, (noColumns) ? Collections.emptyIterator() : 
                    SchemaHelper.getTableColumns(column.getTable()).iterator(), null);
        }
        
        public Builder innerJoin(DbColumn parentColumn, DbColumn column, String tableAlias) {
            return join(parentColumn, TableJoinType.INNER, column, tableAlias, SchemaHelper.getTableColumns(column.getTable()).iterator(),
                    null);
        }
        
        public Builder innerJoin(DbColumn parentColumn, DbColumn column, Predicate<DbColumn> columnSelector, 
                Function<DbColumn, String> aliasMapper) {
            return join(parentColumn, TableJoinType.INNER, column, null,
                    (null == columnSelector) ? SchemaHelper.getTableColumns(column.getTable()).iterator() :
                            SchemaHelper.getTableColumns(column.getTable(), columnSelector).iterator(), aliasMapper);
        }
        
        public Builder innerJoin(DbColumn parentColumn, DbColumn column, Predicate<DbColumn> columnSelector) {
            return join(parentColumn, TableJoinType.INNER, column, null,
                    (null == columnSelector) ? SchemaHelper.getTableColumns(column.getTable()).iterator() :
                            SchemaHelper.getTableColumns(column.getTable(), columnSelector).iterator(), null);
        }
        
        public Builder innerJoin(DbColumn parentColumn, DbColumn column, Function<DbColumn, String> aliasMapper) {
            return join(parentColumn, TableJoinType.INNER, column, null, SchemaHelper.getTableColumns(column.getTable()).iterator(),
                    aliasMapper);
        }
        
        public Builder innerJoin(DbColumn parentColumn, DbColumn column, boolean noColumns) {
            return join(parentColumn, TableJoinType.INNER, column, null, (noColumns) ? Collections.emptyIterator() : 
                    SchemaHelper.getTableColumns(column.getTable()).iterator(), null);
        }
        
        public Builder innerJoin(DbColumn parentColumn, DbColumn column) {
            return join(parentColumn, TableJoinType.INNER, column, null, SchemaHelper.getTableColumns(column.getTable()).iterator(),
                    null);
        }
        
        public Builder leftJoin(DbColumn parentColumn, DbColumn column, String tableAlias, Predicate<DbColumn> columnSelector, 
                Function<DbColumn, String> aliasMapper) {
            return join(parentColumn, TableJoinType.LEFT, column, tableAlias,
                    (null == columnSelector) ? SchemaHelper.getTableColumns(column.getTable()).iterator() :
                            SchemaHelper.getTableColumns(column.getTable(), columnSelector).iterator(), aliasMapper);
        }
        
        public Builder leftJoin(DbColumn parentColumn, DbColumn column, String tableAlias, Predicate<DbColumn> columnSelector) {
            return join(parentColumn, TableJoinType.LEFT, column, tableAlias,
                    (null == columnSelector) ? SchemaHelper.getTableColumns(column.getTable()).iterator() :
                            SchemaHelper.getTableColumns(column.getTable(), columnSelector).iterator(), null);
        }
        
        public Builder leftJoin(DbColumn parentColumn, DbColumn column, String tableAlias, Function<DbColumn, String> aliasMapper) {
            return join(parentColumn, TableJoinType.LEFT, column, tableAlias, SchemaHelper.getTableColumns(column.getTable()).iterator(),
                    aliasMapper);
        }
        
        public Builder leftJoin(DbColumn parentColumn, DbColumn column, String tableAlias, boolean noColumns) {
            return join(parentColumn, TableJoinType.LEFT, column, tableAlias, (noColumns) ? Collections.emptyIterator() : 
                    SchemaHelper.getTableColumns(column.getTable()).iterator(), null);
        }
        
        public Builder leftJoin(DbColumn parentColumn, DbColumn column, String tableAlias) {
            return join(parentColumn, TableJoinType.LEFT, column, tableAlias, SchemaHelper.getTableColumns(column.getTable()).iterator(),
                    null);
        }
        
        public Builder leftJoin(DbColumn parentColumn, DbColumn column, Predicate<DbColumn> columnSelector, 
                Function<DbColumn, String> aliasMapper) {
            return join(parentColumn, TableJoinType.LEFT, column, null,
                    (null == columnSelector) ? SchemaHelper.getTableColumns(column.getTable()).iterator() :
                            SchemaHelper.getTableColumns(column.getTable(), columnSelector).iterator(), aliasMapper);
        }
        
        public Builder leftJoin(DbColumn parentColumn, DbColumn column, Predicate<DbColumn> columnSelector) {
            return join(parentColumn, TableJoinType.LEFT, column, null,
                    (null == columnSelector) ? SchemaHelper.getTableColumns(column.getTable()).iterator() :
                            SchemaHelper.getTableColumns(column.getTable(), columnSelector).iterator(), null);
        }
        
        public Builder leftJoin(DbColumn parentColumn, DbColumn column, Function<DbColumn, String> aliasMapper) {
            return join(parentColumn, TableJoinType.LEFT, column, null, SchemaHelper.getTableColumns(column.getTable()).iterator(),
                    aliasMapper);
        }
        
        public Builder leftJoin(DbColumn parentColumn, DbColumn column, boolean noColumns) {
            return join(parentColumn, TableJoinType.LEFT, column, null, (noColumns) ? Collections.emptyIterator() : 
                    SchemaHelper.getTableColumns(column.getTable()).iterator(), null);
        }
        
        public Builder leftJoin(DbColumn parentColumn, DbColumn column) {
            return join(parentColumn, TableJoinType.LEFT, column, null, SchemaHelper.getTableColumns(column.getTable()).iterator(),
                    null);
        }
        
        public Builder rightJoin(DbColumn parentColumn, DbColumn column, String tableAlias, Predicate<DbColumn> columnSelector, 
                Function<DbColumn, String> aliasMapper) {
            return join(parentColumn, TableJoinType.RIGHT, column, tableAlias,
                    (null == columnSelector) ? SchemaHelper.getTableColumns(column.getTable()).iterator() :
                            SchemaHelper.getTableColumns(column.getTable(), columnSelector).iterator(), aliasMapper);
        }
        
        public Builder rightJoin(DbColumn parentColumn, DbColumn column, String tableAlias, Predicate<DbColumn> columnSelector) {
            return join(parentColumn, TableJoinType.RIGHT, column, tableAlias,
                    (null == columnSelector) ? SchemaHelper.getTableColumns(column.getTable()).iterator() :
                            SchemaHelper.getTableColumns(column.getTable(), columnSelector).iterator(), null);
        }
        
        public Builder rightJoin(DbColumn parentColumn, DbColumn column, String tableAlias, Function<DbColumn, String> aliasMapper) {
            return join(parentColumn, TableJoinType.RIGHT, column, tableAlias, SchemaHelper.getTableColumns(column.getTable()).iterator(),
                    aliasMapper);
        }
        
        public Builder rightJoin(DbColumn parentColumn, DbColumn column, String tableAlias, boolean noColumns) {
            return join(parentColumn, TableJoinType.RIGHT, column, tableAlias, (noColumns) ? Collections.emptyIterator() : 
                    SchemaHelper.getTableColumns(column.getTable()).iterator(), null);
        }
        
        public Builder rightJoin(DbColumn parentColumn, DbColumn column, String tableAlias) {
            return join(parentColumn, TableJoinType.RIGHT, column, tableAlias, SchemaHelper.getTableColumns(column.getTable()).iterator(), null);
        }
        
        public Builder rightJoin(DbColumn parentColumn, DbColumn column, Predicate<DbColumn> columnSelector, 
                Function<DbColumn, String> aliasMapper) {
            return join(parentColumn, TableJoinType.RIGHT, column, null,
                    (null == columnSelector) ? SchemaHelper.getTableColumns(column.getTable()).iterator() :
                            SchemaHelper.getTableColumns(column.getTable(), columnSelector).iterator(), aliasMapper);
        }
        
        public Builder rightJoin(DbColumn parentColumn, DbColumn column, Predicate<DbColumn> columnSelector) {
            return join(parentColumn, TableJoinType.RIGHT, column, null,
                    (null == columnSelector) ? SchemaHelper.getTableColumns(column.getTable()).iterator() :
                            SchemaHelper.getTableColumns(column.getTable(), columnSelector).iterator(), null);
        }
        
        public Builder rightJoin(DbColumn parentColumn, DbColumn column, Function<DbColumn, String> aliasMapper) {
            return join(parentColumn, TableJoinType.RIGHT, column, null, SchemaHelper.getTableColumns(column.getTable()).iterator(),
                    aliasMapper);
        }
        
        public Builder rightJoin(DbColumn parentColumn, DbColumn column, boolean noColumns) {
            return join(parentColumn, TableJoinType.RIGHT, column, null, (noColumns) ? Collections.emptyIterator() : 
                    SchemaHelper.getTableColumns(column.getTable()).iterator(), null);
        }
        
        public Builder rightJoin(DbColumn parentColumn, DbColumn column) {
            return join(parentColumn, TableJoinType.RIGHT, column, null, SchemaHelper.getTableColumns(column.getTable()).iterator(),
                    null);
        }
        
        public Builder fullJoin(DbColumn parentColumn, DbColumn column, String tableAlias, Predicate<DbColumn> columnSelector, 
                Function<DbColumn, String> aliasMapper) {
            return join(parentColumn, TableJoinType.FULL, column, tableAlias,
                    (null == columnSelector) ? SchemaHelper.getTableColumns(column.getTable()).iterator() :
                            SchemaHelper.getTableColumns(column.getTable(), columnSelector).iterator(), aliasMapper);
        }
        
        public Builder fullJoin(DbColumn parentColumn, DbColumn column, String tableAlias, Predicate<DbColumn> columnSelector) {
            return join(parentColumn, TableJoinType.FULL, column, tableAlias,
                    (null == columnSelector) ? SchemaHelper.getTableColumns(column.getTable()).iterator() :
                            SchemaHelper.getTableColumns(column.getTable(), columnSelector).iterator(), null);
        }
        
        public Builder fullJoin(DbColumn parentColumn, DbColumn column, String tableAlias, Function<DbColumn, String> aliasMapper) {
            return join(parentColumn, TableJoinType.FULL, column, tableAlias, SchemaHelper.getTableColumns(column.getTable()).iterator(),
                    aliasMapper);
        }
        
        public Builder fullJoin(DbColumn parentColumn, DbColumn column, String tableAlias, boolean noColumns) {
            return join(parentColumn, TableJoinType.FULL, column, tableAlias, (noColumns) ? Collections.emptyIterator() : 
                    SchemaHelper.getTableColumns(column.getTable()).iterator(), null);
        }
        
        public Builder fullJoin(DbColumn parentColumn, DbColumn column, String tableAlias) {
            return join(parentColumn, TableJoinType.FULL, column, tableAlias, SchemaHelper.getTableColumns(column.getTable()).iterator(),
                    null);
        }
        
        public Builder fullJoin(DbColumn parentColumn, DbColumn column, Predicate<DbColumn> columnSelector, 
                Function<DbColumn, String> aliasMapper) {
            return join(parentColumn, TableJoinType.FULL, column, null,
                    (null == columnSelector) ? SchemaHelper.getTableColumns(column.getTable()).iterator() :
                            SchemaHelper.getTableColumns(column.getTable(), columnSelector).iterator(), aliasMapper);
        }
        
        public Builder fullJoin(DbColumn parentColumn, DbColumn column, Predicate<DbColumn> columnSelector) {
            return join(parentColumn, TableJoinType.FULL, column, null,
                    (null == columnSelector) ? SchemaHelper.getTableColumns(column.getTable()).iterator() :
                            SchemaHelper.getTableColumns(column.getTable(), columnSelector).iterator(), null);
        }
        
        public Builder fullJoin(DbColumn parentColumn, DbColumn column, Function<DbColumn, String> aliasMapper) {
            return join(parentColumn, TableJoinType.FULL, column, null, SchemaHelper.getTableColumns(column.getTable()).iterator(), aliasMapper);
        }
        
        public Builder fullJoin(DbColumn parentColumn, DbColumn column, boolean noColumns) {
            return join(parentColumn, TableJoinType.FULL, column, null, (noColumns) ? Collections.emptyIterator() : 
                    SchemaHelper.getTableColumns(column.getTable()).iterator(), null);
        }
        
        public Builder fullJoin(DbColumn parentColumn, DbColumn column) {
            return join(parentColumn, TableJoinType.FULL, column, null, SchemaHelper.getTableColumns(column.getTable()).iterator(), null);
        }
        
    }
}
