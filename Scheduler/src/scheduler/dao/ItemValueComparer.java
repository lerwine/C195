package scheduler.dao;

import scheduler.view.ItemModel;

/**
 * Compares {@link DataObjectImpl} and {@link ItemModel} to generic values.
 * 
 * @author erwinel
 * @param <T> The type of {@link DataObjectImpl} object.
 * @param <U> The type of {@link ItemModel} object.
 * @param <S> The type of value being compared.
 */
public interface ItemValueComparer<T extends DataObjectImpl, U extends ItemModel<T>, S> {
    
    /**
     * Gets the integer value associated with the target {@link DataObjectImpl}.
     *
     * @param dao The target {@link DataObjectImpl}.
     * @return The value associated with the target {@link DataObjectImpl}.
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
     * Tests whether the value associated with a {@link DataObjectImpl} object is equal to another value.
     *
     * @param dao The target {@link DataObjectImpl}.
     * @param value The value to compare.
     * @return {@code true} if the value associated with a {@link DataObjectImpl} object is equal to {@code value}; otherwise, {@code false}.
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
