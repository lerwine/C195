/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.dao.builder;

import java.util.Objects;
import java.util.TreeMap;
import scheduler.dao.DataObjectImpl;

/**
 *
 * @author erwinel
 * @param <R>
 */
public class SelectQueryBuilder<R extends DataObjectImpl> implements SelectTable {
        private final String tableName;
        
        @Override
        public String getTableName() { return tableName; }

        private String tableAlias;
        
        public String getTableAlias() { return tableAlias; }

        public synchronized void setTableAlias(String name) {
            if (name == null || name.trim().isEmpty())
                name = tableName;
            if (tableAlias.equals(name))
                return;
            assert !containsJoin(name, true) : "Table name already used in a JOIN statement";
            tableAlias = name;
        }
        
        private final TreeMap<String, SelectColumn> columns;
        
        private final TreeMap<String, JoinedSelectTable> joins;

        public SelectQueryBuilder(Class<R> daoClass) {
            tableName = DataObjectImpl.getTableName(daoClass);
            tableAlias = tableName;
            columns = new TreeMap<>();
            joins = new TreeMap<>();
        }

        public synchronized void addColumn(String tableName, String colName, String name) {
            Objects.requireNonNull(tableName, "Table name cannot be null");
            Objects.requireNonNull(colName, "Column name cannot be null");
            Objects.requireNonNull(name, "Aliased name cannot be null");
            assert !tableName.trim().isEmpty() : "Table name cannot be empty";
            assert !colName.trim().isEmpty() : "Column name cannot be empty";
            assert !name.trim().isEmpty() : "Aliased name cannot be empty";
            assert containsTable(tableName) : "Table with that alias not found";
            assert !columns.containsKey(name) : "Column with that aliased name already exists";
            columns.put(name, new SelectColumn() {
                @Override
                public String getColName() { return colName; }
                @Override
                public String getTableName() { return tableName; }
            });
        }

        @Override
        public void addColumn(String name, String alias) { addColumn(tableAlias, name, alias); }

        public synchronized boolean containsTable(String name) {
            return name != null && (name.equals(tableAlias) || containsJoin(name, true));
        }

        public synchronized SelectTable getTable(String name) {
            if (name == null)
                return null;
            if (name.equals(tableAlias))
                return this;
            return getJoin(name, true);
        }
        
        private synchronized JoinedSelectTable createJoin(TreeMap<String, JoinedSelectTable> map, String foreignKeyColumn, String tableName,
                String refKeyColumn, String joinAs, String joinType) {
            Objects.requireNonNull(foreignKeyColumn, "Foreign key column cannot be null");
            Objects.requireNonNull(tableName, "Table name cannot be null");
            Objects.requireNonNull(refKeyColumn, "Reference key column cannot be null");
            Objects.requireNonNull(joinAs, "Join-as name cannot be null");
            assert !foreignKeyColumn.trim().isEmpty() : "Foreign key column cannot be empty";
            assert !tableName.trim().isEmpty() : "Table name cannot be empty";
            assert !refKeyColumn.trim().isEmpty() : "Reference key column cannot be empty";
            assert !joinAs.trim().isEmpty() : "Join-as name cannot be empty";
            assert !joinAs.equals(tableAlias) : "Name already used by the current SELECT statement";
            assert !containsJoin(joinAs, true) : "Table name already used in a JOIN statement";
            JoinedSelectTable result = new JoinedSelectTable() {
                private final TreeMap<String, JoinedSelectTable> nestedJoins = new TreeMap<>();
                @Override
                public String getJoinType() { return joinType; }
                @Override
                public String getTableName() { return tableName; }
                @Override
                public void addColumn(String name, String alias) { SelectQueryBuilder.this.addColumn(joinAs, name, alias); }
                @Override
                public synchronized JoinedSelectTable leftJoin(String foreignKeyColumn, String tableName, String refKeyColumn, String joinAs) {
                    return createJoin(nestedJoins, foreignKeyColumn, tableName, refKeyColumn, joinAs, "LEFT");
                }

                @Override
                public synchronized JoinedSelectTable innerJoin(String foreignKeyColumn, String tableName, String refKeyColumn, String joinAs) {
                    return createJoin(nestedJoins, foreignKeyColumn, tableName, refKeyColumn, joinAs, "INNER");
                }

                @Override
                public synchronized JoinedSelectTable rightJoin(String foreignKeyColumn, String tableName, String refKeyColumn, String joinAs) {
                    return createJoin(nestedJoins, foreignKeyColumn, tableName, refKeyColumn, joinAs, "RIGHT");
                }

                @Override
                public synchronized boolean containsJoin(String name, boolean includeNested) {
                    if (name == null || name.trim().isEmpty())
                        return false;
                    if (nestedJoins.containsKey(name))
                        return true;
                    return includeNested && nestedJoins.values().stream().anyMatch((JoinedSelectTable t) -> t.containsJoin(name, true));
                }

                @Override
                public synchronized JoinedSelectTable getJoin(String name, boolean includeNested) {
                    if (name == null || name.trim().isEmpty())
                        return null;
                    if (nestedJoins.containsKey(name))
                        return nestedJoins.get(name);
                    if (!includeNested)
                        return null;
                    for (String k : nestedJoins.keySet()) {
                        JoinedSelectTable t = nestedJoins.get(k).getJoin(name, true);
                        if (t != null)
                            return t;
                    }
                    return null;
                }
                
            };
            map.put(joinAs, result);
            return result;
        }

        @Override
        public JoinedSelectTable leftJoin(String foreignKeyColumn, String tableName, String refKeyColumn, String joinAs) {
            return createJoin(joins, foreignKeyColumn, tableName, refKeyColumn, joinAs, "LEFT");
        }

        @Override
        public JoinedSelectTable innerJoin(String foreignKeyColumn, String tableName, String refKeyColumn, String joinAs) {
            return createJoin(joins, foreignKeyColumn, tableName, refKeyColumn, joinAs, "INNER");
        }

        @Override
        public JoinedSelectTable rightJoin(String foreignKeyColumn, String tableName, String refKeyColumn, String joinAs) {
            return createJoin(joins, foreignKeyColumn, tableName, refKeyColumn, joinAs, "RIGHT");
        }

        @Override
        public synchronized boolean containsJoin(String name, boolean includeNested) {
            if (name == null || name.trim().isEmpty())
                return false;
            if (joins.containsKey(name))
                return true;
            return includeNested && joins.values().stream().anyMatch((JoinedSelectTable t) -> t.containsJoin(name, true));
        }

        @Override
        public synchronized JoinedSelectTable getJoin(String name, boolean includeNested) {
            if (name == null || name.trim().isEmpty())
                return null;
            if (joins.containsKey(name))
                return joins.get(name);
            if (!includeNested)
                return null;
            for (String k : joins.keySet()) {
                JoinedSelectTable t = joins.get(k).getJoin(name, true);
                if (t != null)
                    return t;
            }
            return null;
        }
    }
    
