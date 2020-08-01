package scheduler.dao.filter;

import scheduler.dao.DataAccessObject;
import scheduler.model.fx.EntityModel;

/**
 * Compares {@link DataAccessObject} and {@link EntityModel} to generic values.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> The type of {@link DataAccessObject} object.
 * @param <U> The type of {@link EntityModel} object.
 * @param <S> The type of value being compared.
 */
public interface ItemValueComparer<T extends DataAccessObject, U extends EntityModel<T>, S> {

    /**
     * Gets the integer value associated with the target {@link DataAccessObject}.
     *
     * @param dao The target {@link DataAccessObject}.
     * @return The value associated with the target {@link DataAccessObject}.
     */
    S get(T dao);

    /**
     * Gets the integer value associated with the target {@link EntityModel}.
     *
     * @param model The target {@link EntityModel}.
     * @return The value associated with the target {@link EntityModel}.
     */
    S get(U model);

    /**
     * Tests whether the value associated with a {@link DataAccessObject} object is equal to another value.
     *
     * @param dao The target {@link DataAccessObject}.
     * @param value The value to compare.
     * @return {@code true} if the value associated with a {@link DataAccessObject} object is equal to {@code value}; otherwise, {@code false}.
     */
    default boolean test(T dao, S value) {
        S s = get(dao);
        return (null == value) ? null == s : null != s && value.equals(s);
    }

    /**
     * Tests whether the value associated with an {@link EntityModel} object is equal to another value.
     *
     * @param model The target {@link EntityModel}.
     * @param value The value to compare.
     * @return {@code true} if the value associated with a {@link EntityModel} object is equal to {@code value}; otherwise, {@code false}.
     */
    default boolean test(U model, S value) {
        S s = get(model);
        return (null == value) ? null == s : null != s && value.equals(s);
    }

}
