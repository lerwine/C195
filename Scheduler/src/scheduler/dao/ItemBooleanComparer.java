package scheduler.dao;

import scheduler.view.ItemModel;

/**
 * Compares {@link DataObjectImpl} and {@link ItemModel} to boolean values.
 *
 * @author erwinel
 * @param <T> The type of {@link DataObjectImpl} object.
 * @param <U> The type of {@link ItemModel} object.
 */
public interface ItemBooleanComparer<T extends DataObjectImpl, U extends ItemModel<T>> {

    /**
     * Gets the boolean value associated with the target {@link DataObjectImpl}.
     *
     * @param dao The target {@link DataObjectImpl}.
     * @return The boolean value associated with the target {@link DataObjectImpl}.
     */
    boolean get(T dao);

    /**
     * Gets the boolean value associated with the target {@link ItemModel}.
     *
     * @param model The target {@link ItemModel}.
     * @return The boolean value associated with the target {@link ItemModel}.
     */
    boolean get(U model);

    /**
     * Tests whether the value associated with a {@link DataObjectImpl} object is equal to another value.
     *
     * @param dao The target {@link DataObjectImpl}.
     * @param value The value to compare.
     * @return {@code true} if the value associated with a {@link DataObjectImpl} object is equal to {@code value}; otherwise, {@code false}.
     */
    default boolean test(T dao, boolean value) {
        return get(dao) == value;
    }

    /**
     * Tests whether the value associated with an {@link ItemModel} object is equal to another value.
     *
     * @param model The target {@link ItemModel}.
     * @param value The value to compare.
     * @return {@code true} if the value associated with a {@link ItemModel} object is equal to {@code value}; otherwise, {@code false}.
     */
    default boolean test(U model, boolean value) {
        return get(model) == value;
    }

}
