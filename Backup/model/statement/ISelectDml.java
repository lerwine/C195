package scheduler.model.statement;

import scheduler.model.schema.IDbColumn;
import scheduler.model.schema.IDbSchema;
import scheduler.model.ResultRow;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
 * @param <T> The schema type.
 * @param <U> The result row type.
 */
public interface ISelectDml<T extends IDbSchema<? extends IDbColumn<T>>, U extends ResultRow<T>> extends IFilteredDml<T, Iterable<U>> {
    Iterable<? extends IDbColumn<T>> getSelectColumns();
}
