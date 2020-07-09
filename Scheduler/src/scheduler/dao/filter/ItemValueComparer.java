package scheduler.dao.filter;

import scheduler.dao.DataAccessObject;
import scheduler.model.ui.EntityModelImpl;

/**
 * Compares {@link DataAccessObject} and {@link EntityModelImpl} to generic values.
 * 
 @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> The type of {@link DataAccessObject} object.
 * @param <U> The type of {@link EntityModelImpl} object.
 * @param <S> The type of value being compared.
 */
public interface ItemValueComparer<T extends DataAccessObject, U extends EntityModelImpl<T>, S> {
    
    /**
     * Gets the integer value associated with the target {@link DataAccessObject}.
     *
     * @param dao The target {@link DataAccessObject}.
     * @return The value associated with the target {@link DataAccessObject}.
     */
    S get(T dao);

    /**
     * Gets the integer value associated with the target {@link EntityModelImpl}.
     *
     * @param model The target {@link EntityModelImpl}.
     * @return The value associated with the target {@link EntityModelImpl}.
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
     * Tests whether the value associated with an {@link EntityModelImpl} object is equal to another value.
     *
     * @param model The target {@link EntityModelImpl}.
     * @param value The value to compare.
     * @return {@code true} if the value associated with a {@link EntityModelImpl} object is equal to {@code value}; otherwise, {@code false}.
     */
    default boolean test(U model, S value) {
        S s = get(model);
        return (null == value) ? null == s : null != s && value.equals(s);
    }

}
