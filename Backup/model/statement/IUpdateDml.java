package scheduler.model.statement;

import scheduler.model.schema.DbTable;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
 * @param <T> The schema type.
 */
public interface IUpdateDml<T extends DbTable<? extends DbTable.DbColumn<T>>> extends IFilteredDml<T, Integer> {
    
}
