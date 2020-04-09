package scheduler.model.statement;

import scheduler.model.schema.IDbColumn;
import scheduler.model.schema.IDbSchema;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
 * @param <T> The schema type.
 */
public interface IDmlFilter<T extends IDbSchema<? extends IDbColumn<T>>> {
    
}
