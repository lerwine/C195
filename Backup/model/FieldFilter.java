package scheduler.model;

/**
 *
 * @author lerwi
 * @param <T>
 */
public interface FieldFilter<T extends IDataRow> extends DataRowFilter<T> {
    
    /**
     * Gets the name of the database column being filtered.
     * 
     * @return The name of the database column being filtered.
     */
    String getColName();
    
    /**
     * Gets the select statement table associated with the current database column.
     * 
     * @return The select statement table associated with the current database column.
     */
    SelectStatementTable<? extends IDataRow> getTable();

    FilterComparisonOperator getOperator();
    
    /**
     *
     * @param sql
     */
    @Override
    public default void appendSubClauseSql(StringBuilder sql) {
        SelectStatementTable<? extends IDataRow> table = getTable();
        if (table instanceof JoinedSelectTable)
            FilterComparisonOperator.appendSubClauseSql(sql, table.getTableName(), getColName(), getOperator());
        else
            FilterComparisonOperator.appendSubClauseSql(sql, null, getColName(), getOperator());
    }
    
}
