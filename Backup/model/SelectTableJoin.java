package scheduler.model;

/**
 *
 * @author lerwi
 * @param <T>
 * @param <U>
 */
public interface SelectTableJoin<T extends IDataRow, U extends IDataRow> {
    TableJoinType getType();
    String getPrimaryKeyColName();
    String getForeignKeyColName();
    JoinedSelectTable<T> getParentTable();
    JoinedSelectTable<U> getChildTable();
}
