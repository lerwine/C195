package scheduler.model.statement;

import scheduler.model.schema.DbTable;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
 * @param <T> The schema type.
 */
public interface IDeleteDml<T extends DbTable<? extends DbTable.DbColumn<T>>> extends IFilteredDml<T, Integer> {
}
