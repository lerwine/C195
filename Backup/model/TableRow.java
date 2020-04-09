package scheduler.model;

import scheduler.model.schema.DbTable;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
 * @param <T>
 */
public abstract class TableRow<T extends DbTable<? extends DbTable.DbColumn<T>>> implements ResultRow<T> {    

}
