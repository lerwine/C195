package scheduler.dao.filter;

import scheduler.dao.DbRecordBase;
import scheduler.model.ui.FxRecordModel;

/**
 * Compares {@link DbRecordBase} and {@link FxRecordModel} to boolean values.
 *
 @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> The type of {@link DbRecordBase} object.
 * @param <U> The type of {@link FxRecordModel} object.
 */
public interface ItemBooleanComparer<T extends DbRecordBase, U extends FxRecordModel<T>> {

    /**
     * Gets the boolean value associated with the target {@link DbRecordBase}.
     *
     * @param dao The target {@link DbRecordBase}.
     * @return The boolean value associated with the target {@link DbRecordBase}.
     */
    boolean get(T dao);

    /**
     * Gets the boolean value associated with the target {@link FxRecordModel}.
     *
     * @param model The target {@link FxRecordModel}.
     * @return The boolean value associated with the target {@link FxRecordModel}.
     */
    boolean get(U model);

    /**
     * Tests whether the value associated with a {@link DbRecordBase} object is equal to another value.
     *
     * @param dao The target {@link DbRecordBase}.
     * @param value The value to compare.
     * @return {@code true} if the value associated with a {@link DbRecordBase} object is equal to {@code value}; otherwise, {@code false}.
     */
    default boolean test(T dao, boolean value) {
        return get(dao) == value;
    }

    /**
     * Tests whether the value associated with an {@link FxRecordModel} object is equal to another value.
     *
     * @param model The target {@link FxRecordModel}.
     * @param value The value to compare.
     * @return {@code true} if the value associated with a {@link FxRecordModel} object is equal to {@code value}; otherwise, {@code false}.
     */
    default boolean test(U model, boolean value) {
        return get(model) == value;
    }

}
