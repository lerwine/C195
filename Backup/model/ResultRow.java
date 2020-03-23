package scheduler.model;

import scheduler.model.schema.IDbColumn;
import scheduler.model.schema.IDbSchema;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
 * @param <T>
 */
public interface ResultRow<T extends IDbSchema<? extends IDbColumn<T>>> {
    T getSchema();
}
