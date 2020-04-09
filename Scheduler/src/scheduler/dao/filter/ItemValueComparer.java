package scheduler.dao.filter;

import scheduler.dao.DataAccessObject;
import scheduler.view.model.ItemModel;

/**
 * Compares {@link DataAccessObject} and {@link ItemModel} to generic values.
 * 
 @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
 * @param <T> The type of {@link DataAccessObject} object.
 * @param <U> The type of {@link ItemModel} object.
 * @param <S> The type of value being compared.
 */
public interface ItemValueComparer<T extends DataAccessObject, U extends ItemModel<T>, S> {
    
    /**
     * Gets the integer value associated with the target {@link DataAccessObject}.
     *
     * @param dao The target {@link DataAccessObject}.
     * @return The value associated with the target {@link DataAccessObject}.
     */
    S get(T dao);

    /**
     * Gets the integer value associated with the target {@link ItemModel}.
     *
     * @param model The target {@link ItemModel}.
     * @return The value associated with the target {@link ItemModel}.
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
     * Tests whether the value associated with an {@link ItemModel} object is equal to another value.
     *
     * @param model The target {@link ItemModel}.
     * @param value The value to compare.
     * @return {@code true} if the value associated with a {@link ItemModel} object is equal to {@code value}; otherwise, {@code false}.
     */
    default boolean test(U model, S value) {
        S s = get(model);
        return (null == value) ? null == s : null != s && value.equals(s);
    }

}
