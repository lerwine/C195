package scheduler.dao.filter;

import scheduler.dao.DataAccessObject;
import scheduler.model.ui.EntityModelImpl;

/**
 * Compares {@link DataAccessObject} and {@link EntityModelImpl} to boolean values.
 *
 @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> The type of {@link DataAccessObject} object.
 * @param <U> The type of {@link EntityModelImpl} object.
 */
public interface ItemBooleanComparer<T extends DataAccessObject, U extends EntityModelImpl<T>> {

    /**
     * Gets the boolean value associated with the target {@link DataAccessObject}.
     *
     * @param dao The target {@link DataAccessObject}.
     * @return The boolean value associated with the target {@link DataAccessObject}.
     */
    boolean get(T dao);

    /**
     * Gets the boolean value associated with the target {@link EntityModelImpl}.
     *
     * @param model The target {@link EntityModelImpl}.
     * @return The boolean value associated with the target {@link EntityModelImpl}.
     */
    boolean get(U model);

    /**
     * Tests whether the value associated with a {@link DataAccessObject} object is equal to another value.
     *
     * @param dao The target {@link DataAccessObject}.
     * @param value The value to compare.
     * @return {@code true} if the value associated with a {@link DataAccessObject} object is equal to {@code value}; otherwise, {@code false}.
     */
    default boolean test(T dao, boolean value) {
        return get(dao) == value;
    }

    /**
     * Tests whether the value associated with an {@link EntityModelImpl} object is equal to another value.
     *
     * @param model The target {@link EntityModelImpl}.
     * @param value The value to compare.
     * @return {@code true} if the value associated with a {@link EntityModelImpl} object is equal to {@code value}; otherwise, {@code false}.
     */
    default boolean test(U model, boolean value) {
        return get(model) == value;
    }

}
