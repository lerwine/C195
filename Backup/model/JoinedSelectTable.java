package scheduler.model;

import java.util.HashSet;
import java.util.List;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
 * @param <T>
 */
public final class JoinedSelectTable<T extends IDataRow> implements SelectStatementTable<T> {
    private final HashSet<SelectTableJoin<T, ? extends IDataRow>> childJoins = new HashSet<>();
    private SelectTableJoin<? extends IDataRow, T> parentJoin = null;
    private final SelectStatementTableImpl<T> backingTable;
    private final String tableAlias;
    
    private JoinedSelectTable(SelectStatementTableImpl<T> backingTable, String tableAlias) {
        this.backingTable = backingTable;
        this.tableAlias = tableAlias;
    }
    
    private synchronized boolean childAliasExists(String name) {
        return childJoins.stream().map((t) -> t.getChildTable()).anyMatch((t) -> t.tableAlias.equalsIgnoreCase(name) ||
                t.backingTable.getTableName().equalsIgnoreCase(name) || t.childAliasExists(name));
    }
    
    public final synchronized boolean aliasExists(String name) {
        if (null == name || name.isEmpty())
            return false;
        if (name.equalsIgnoreCase(tableAlias) || name.equalsIgnoreCase(backingTable.getTableName()))
            return true;
        if (null != parentJoin)
            return parentJoin.getParentTable().childAliasExists(name);
        return childAliasExists(name);
    }
    
    private synchronized JoinedSelectTable<? extends IDataRow> findChild(String name) {
        if (null == name || name.isEmpty())
            return null;
        if (name.equalsIgnoreCase(tableAlias) || backingTable.getTableName().equalsIgnoreCase(tableAlias))
            return this;
        for (SelectTableJoin<T, ? extends IDataRow> t : childJoins) {
            JoinedSelectTable<? extends IDataRow> r = t.getChildTable();
            if (name.equalsIgnoreCase(r.tableAlias) || name.equalsIgnoreCase(r.backingTable.getTableName()))
                return r;
            r = r.find(name);
            if (null != r)
                return r;
        }
        return null;
    }
    
    public final synchronized JoinedSelectTable<? extends IDataRow> find(String name) {
        if (null == name || name.isEmpty())
            return null;
        if (name.equalsIgnoreCase(tableAlias) || name.equalsIgnoreCase(backingTable.getTableName()))
            return this;
        if (null != parentJoin)
            return parentJoin.getParentTable().findChild(name);
        return findChild(name);
    }
    
    public final boolean isJoinedParentOf(JoinedSelectTable<? extends IDataRow> other) {
        return null != other && other != this && !childJoins.isEmpty() && childJoins.stream().anyMatch((SelectTableJoin<T, ? extends IDataRow> t) -> {
            JoinedSelectTable<? extends IDataRow> c = t.getChildTable();
            return other == c || c.isJoinedParentOf(other);
        });
    }
    
    public final boolean isJoinedChildOf(JoinedSelectTable<? extends IDataRow> other) {
        return null != other && other != this && other.isJoinedParentOf(this);
    }
    
    public static <T extends IDataRow, U extends IDataRow> SelectTableJoin<T, U> join(TableJoinType type, SelectStatementTableImpl<T> parentTable,
            String parentAlias, String pkColName, SelectStatementTableImpl<U> childTable, String childAlias, String fkColName) {
        if (null == parentTable || null == parentAlias)
            throw new NullPointerException();
        if (parentAlias.trim().isEmpty())
            throw new IllegalArgumentException();
        JoinedSelectTable<T> joinedParent = new JoinedSelectTable<>(parentTable, parentAlias);
        return (SelectTableJoin<T, U>) joinedParent.join(type, pkColName, childTable, childAlias, fkColName).parentJoin;
    }
    
    public static <T extends IDataRow, U extends IDataRow> SelectTableJoin<T, U> innerJoin(SelectStatementTableImpl<T> parentTable,
            String parentAlias, String pkColName, SelectStatementTableImpl<U> childTable, String childAlias, String fkColName) {
        return join(TableJoinType.INNER, parentTable, parentAlias, pkColName, childTable, childAlias, fkColName);
    }
    
    public static <T extends IDataRow, U extends IDataRow> SelectTableJoin<T, U> leftJoin(SelectStatementTableImpl<T> parentTable,
            String parentAlias, String pkColName, SelectStatementTableImpl<U> childTable, String childAlias, String fkColName) {
        return join(TableJoinType.LEFT, parentTable, parentAlias, pkColName, childTable, childAlias, fkColName);
    }
    
    public static <T extends IDataRow, U extends IDataRow> SelectTableJoin<T, U> rightJoin(SelectStatementTableImpl<T> parentTable,
            String parentAlias, String pkColName, SelectStatementTableImpl<U> childTable, String childAlias, String fkColName) {
        return join(TableJoinType.RIGHT, parentTable, parentAlias, pkColName, childTable, childAlias, fkColName);
    }
    
    public static <T extends IDataRow, U extends IDataRow> SelectTableJoin<T, U> fullJoin(SelectStatementTableImpl<T> parentTable,
            String parentAlias, String pkColName, SelectStatementTableImpl<U> childTable, String childAlias, String fkColName) {
        return join(TableJoinType.FULL, parentTable, parentAlias, pkColName, childTable, childAlias, fkColName);
    }
    
    @Override
    public String getTableName() {
        return backingTable.getTableName();
    }

    public String getTableAlias() {
        return tableAlias;
    }

    @Override
    public List<SelectStatementColumn> getColumns() {
        return backingTable.getColumns();
    }

    private void appendSelectFieldSql(StringBuilder stringBuilder) {
        
    }
    
    private void appendJoinSql(StringBuilder stringBuilder) {
        childJoins.stream().forEach((t) -> {
            JoinedSelectTable<? extends IDataRow> childTable = t.getChildTable();
            String a = childTable.tableAlias;
            stringBuilder.append(" ").append(t.getType().toString()).append(" `").append(childTable.getTableName()).append("` ").append(a)
                    .append(" ON ").append(a).append(".`").append(t.getForeignKeyColName())
                    .append("`=").append(tableAlias).append(".`").append(t.getPrimaryKeyColName()).append("`");
            if (!childTable.childJoins.isEmpty())
                childTable.appendJoinSql(stringBuilder);
        });
    }
    
    @Override
    public StringBuilder toSelectSql() {
        if (null != parentJoin)
            throw new UnsupportedOperationException();
        StringBuilder result = new StringBuilder("SELECT ");
        List<SelectStatementColumn> columns = backingTable.getColumns();
        String cn = columns.get(0).get();
        result.append(tableAlias).append(".`").append(cn);
        String an = columns.get(0).getAlias();
        if (null == an || an.trim().isEmpty() || an.equals(cn))
            result.append("` AS `").append(cn).append("`");
        else
            result.append("` AS ").append(an);
        columns.stream().skip(1L).forEach((t) -> {
            String c = t.get();
            result.append(", ").append(tableAlias).append(".`").append(cn);
            String a = t.getAlias();
            if (null == a || a.trim().isEmpty() || a.equals(c))
                result.append("`");
            else
                result.append("` AS ").append(a);
        });
        childJoins.stream().forEach((t) -> t.getChildTable().appendSelectFieldSql(result));
        result.append(" FROM `").append(backingTable.getTableName()).append("` ").append(tableAlias);
        appendJoinSql(result);
        return result;
    }

    public final synchronized <U extends IDataRow> JoinedSelectTable<U> join(TableJoinType type, String pkColName,
            SelectStatementTableImpl<U> childTable, String childAlias, String fkColName) {
        if (null == type || null == pkColName || null == childAlias || null == fkColName)
            throw new NullPointerException();
        if (pkColName.trim().isEmpty() || childAlias.trim().isEmpty() || fkColName.trim().isEmpty())
            throw new IllegalArgumentException();
        if (aliasExists(childAlias))
            throw new UnsupportedOperationException("Alias already exists");
        
        JoinedSelectTable<U> result = new JoinedSelectTable<>(childTable, childAlias);
        SelectTableJoin<T, U> j = new SelectTableJoin<T, U>() {
            @Override
            public TableJoinType getType() {
                return type;
            }

            @Override
            public String getPrimaryKeyColName() {
                return pkColName;
            }

            @Override
            public String getForeignKeyColName() {
                return fkColName;
            }

            @Override
            public JoinedSelectTable<T> getParentTable() {
                return JoinedSelectTable.this;
            }

            @Override
            public JoinedSelectTable<U> getChildTable() {
                return result;
            }
        };
        result.parentJoin = j;
        childJoins.add(j);
        return result;
    }
    
    public final <U extends IDataRow> JoinedSelectTable<U> innerJoin(String pkColName, SelectStatementTableImpl<U> childTable, String childAlias,
            String fkColName) {
        return join(TableJoinType.INNER, pkColName, childTable, childAlias, fkColName);
    }
    
    public final <U extends IDataRow> JoinedSelectTable<U> leftJoin(String pkColName, SelectStatementTableImpl<U> childTable, String childAlias,
            String fkColName) {
        return join(TableJoinType.LEFT, pkColName, childTable, childAlias, fkColName);
    }
    
    public final <U extends IDataRow> JoinedSelectTable<U> rightJoin(String pkColName, SelectStatementTableImpl<U> childTable, String childAlias,
            String fkColName) {
        return join(TableJoinType.RIGHT, pkColName, childTable, childAlias, fkColName);
    }
    
    public final <U extends IDataRow> JoinedSelectTable<U> fullJoin(String pkColName, SelectStatementTableImpl<U> childTable, String childAlias,
            String fkColName) {
        return join(TableJoinType.FULL, pkColName, childTable, childAlias, fkColName);
    }
    
}
