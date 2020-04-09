package scheduler.model.schema;

import java.util.List;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
 * @param <T>
 */
public interface IDbSchema<T extends IDbColumn<? extends IDbSchema<T>>> extends List<T> {
    String getTableName();
}
