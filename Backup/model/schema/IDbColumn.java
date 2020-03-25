package scheduler.model.schema;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
 * @param <T>
 */
public interface IDbColumn<T extends IDbSchema<? extends IDbColumn<T>>> {
    T getSchema();
    String getName();
    DbColType getType();
}