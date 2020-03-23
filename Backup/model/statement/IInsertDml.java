package scheduler.model.statement;

import scheduler.model.schema.DbTable;
import scheduler.model.TableRow;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
 * @param <T> The schema type.
 * @param <U> The source row type.
 */
public interface IInsertDml<T extends DbTable<? extends DbTable.DbColumn<T>>, U extends TableRow<T>> extends IDml<T, Boolean> {
}
