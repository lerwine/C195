package scheduler.model.statement;

import scheduler.model.schema.IDbColumn;
import scheduler.model.schema.IDbSchema;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
 * @param <T> The schema type.
 * @param <U> The result type.
 */
interface IFilteredDml<T extends IDbSchema<? extends IDbColumn<T>>, R> extends IDml<T, R> {
    IDmlFilter<T> getWhereClause();
}
