package scheduler.model.schema;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
 * @param <T>
 * @param <U>
 */
public interface IChildColumn<T extends IDbSchema<? extends IDbColumn<T>>, U extends IDbColumn<? extends IDbSchema<? super U>>> extends IDbColumn<T> {
    U getParentColumn();
}
