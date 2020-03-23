package scheduler.model.statement;

import scheduler.model.schema.DbTable;

/**
 *
 * @author lerwi
 * @param <T> The schema type.
 */
public interface IDeleteDml<T extends DbTable<? extends DbTable.DbColumn<T>>> extends IFilteredDml<T, Integer> {
}
