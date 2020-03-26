package scheduler.dao.dml.deprecated;

import java.util.Objects;
import java.util.function.Function;
import scheduler.dao.schema.DbColumn;
import scheduler.dao.schema.DbTable;

/**
 * Refers to a {@link ColumnReference} in a prospective {@link TableColumnList}.
 *
 * @author Leonard T. Erwine (Student ID 356334)
 */
public interface QueryColumnSelector extends Function<TableReference, ColumnReference> {

    /**
     * Creates a QueryColumnSelector with no alternate {@link DbColumn}.
     *
     * @param column The {@link DbColumn} to match from a {@link TableColumnList}.
     * @return A QueryColumnSelector where {@link #getAltColumn()} returns a {@code null} value.
     */
    public static QueryColumnSelector of(DbColumn column) {
        Objects.requireNonNull(column);
        return new QueryColumnSelector() {
            @Override
            public DbColumn getColumn() {
                return column;
            }

            @Override
            public DbColumn getAltColumn() {
                return null;
            }

            @Override
            public void appendColumnName(TableReference table, StringBuilder stringBuilder) {
                ColumnReference colRef;
                if (table instanceof TableColumnList) {
                    TableColumnList<? extends ColumnReference> tableColumns = (TableColumnList<? extends ColumnReference>) table;
                    colRef = tableColumns.findFirst(column);
                    if (null != colRef) {
                        stringBuilder.append(colRef.getName());
                        return;
                    }
                }
                DbTable dbTable = column.getTable();
                if (table.getTable() == dbTable || (table instanceof JoinableTable && null != ((JoinableTable<? extends JoinedTable<?>>) table).findFirst(dbTable))) {
                    stringBuilder.append("`").append(dbTable.getDbName().toString()).append("`.`").append(column.getDbName().toString()).append("`");
                    return;
                }

                throw new UnsupportedOperationException("Table not found");
            }

            @Override
            public ColumnReference apply(TableReference t) {
                ColumnReference colRef;
                if (t instanceof TableColumnList) {
                    TableColumnList<? extends ColumnReference> tableColumns = (TableColumnList<? extends ColumnReference>) t;
                    colRef = tableColumns.findFirst(column);
                    if (null != colRef) {
                        return colRef;
                    }
                }
                DbTable dbTable = column.getTable();
                if (t.getTable() == dbTable || (t instanceof JoinableTable && null != ((JoinableTable<? extends JoinedTable<?>>) t).findFirst(dbTable))) {
                    return () -> column;
                }

                return null;
            }

            @Override
            public String toString() {
                return column.toString();
            }

            @Override
            public boolean equals(Object obj) {
                if (null != obj && obj instanceof QueryColumnSelector) {
                    QueryColumnSelector other = (QueryColumnSelector) obj;
                    return column == other.getColumn() && null == other.getAltColumn();
                }
                return false;
            }

            @Override
            public int hashCode() {
                return column.hashCode();
            }

        };
    }

    /**
     * Creates a QueryColumnSelector that includes an alternate {@link DbColumn}.
     *
     * @param column The preferred {@link DbColumn} to match from a {@link TableColumnList}.
     * @param altColumn The alternative {@link DbColumn} to match from a {@link TableColumnList} if no match for {@link column} is found.
     * @return A QueryColumnSelector that includes an alternate {@link DbColumn}.
     */
    public static QueryColumnSelector of(DbColumn column, DbColumn altColumn) {
        assert column.getType() == altColumn.getType() : "Column type mismatch";
        return new QueryColumnSelector() {
            @Override
            public DbColumn getColumn() {
                return column;
            }

            @Override
            public DbColumn getAltColumn() {
                return altColumn;
            }

            @Override
            public void appendColumnName(TableReference table, StringBuilder stringBuilder) {
                ColumnReference colRef;
                if (table instanceof TableColumnList) {
                    TableColumnList<? extends ColumnReference> tableColumns = (TableColumnList<? extends ColumnReference>) table;
                    colRef = tableColumns.findFirst(column);
                    if (null != colRef) {
                        stringBuilder.append(colRef.getName());
                        return;
                    }
                    colRef = tableColumns.findFirst(altColumn);
                    if (null != colRef) {
                        stringBuilder.append(colRef.getName());
                        return;
                    }
                }
                DbColumn dbColumn = column;
                DbTable dbTable = dbColumn.getTable();
                if (table.getTable() != dbTable && !(table instanceof JoinableTable && null != ((JoinableTable<? extends JoinedTable<?>>) table).findFirst(dbTable))) {
                    dbColumn = altColumn;
                    dbTable = dbColumn.getTable();
                    if (table.getTable() != dbTable && !(table instanceof JoinableTable && null != ((JoinableTable<? extends JoinedTable<?>>) table).findFirst(dbTable))) {
                        throw new UnsupportedOperationException("Table not found");
                    }
                }
                stringBuilder.append("`").append(dbTable.getDbName().toString()).append("`.`").append(column.getDbName().toString()).append("`");
            }

            @Override
            public ColumnReference apply(TableReference t) {
                ColumnReference colRef;
                if (t instanceof TableColumnList) {
                    TableColumnList<? extends ColumnReference> tableColumns = (TableColumnList<? extends ColumnReference>) t;
                    colRef = tableColumns.findFirst(column);
                    if (null != colRef) {
                        return colRef;
                    }
                    colRef = tableColumns.findFirst(altColumn);
                    if (null != colRef) {
                        return colRef;
                    }
                }
                DbTable dbTable = column.getTable();
                if (t.getTable() == dbTable || (t instanceof JoinableTable && null != ((JoinableTable<? extends JoinedTable<?>>) t).findFirst(dbTable))) {
                    return () -> column;
                }

                dbTable = altColumn.getTable();
                if (t.getTable() == dbTable || (t instanceof JoinableTable && null != ((JoinableTable<? extends JoinedTable<?>>) t).findFirst(dbTable))) {
                    return () -> altColumn;
                }

                return null;
            }

            @Override
            public String toString() {
                return column.toString();
            }

            @Override
            public boolean equals(Object obj) {
                if (null != obj && obj instanceof QueryColumnSelector) {
                    QueryColumnSelector other = (QueryColumnSelector) obj;
                    if (column == other.getColumn()) {
                        DbColumn c = other.getAltColumn();
                        return null != c && c == altColumn;
                    }
                }
                return false;
            }

            @Override
            public int hashCode() {
                return column.hashCode();
            }

        };
    }

    /**
     * Gets the preferred {@link DbColumn}.
     *
     * @return The preferred {@link DbColumn}.
     */
    DbColumn getColumn();

    /**
     * Gets the alternate {@link DbColumn} if there is no direct match for {@link #getColumn()}.
     *
     * @return The alternate {@link DbColumn} if there is no direct match for {@link #getColumn()} or {@code null} if an alternate {@link DbColumn} is
     * not applicable.
     */
    DbColumn getAltColumn();

    void appendColumnName(TableReference table, StringBuilder stringBuilder);

}
