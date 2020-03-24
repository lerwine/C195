/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.dao;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalDate;
import scheduler.util.DB;
import scheduler.view.ItemModel;

/**
 * Compares {@link DataObjectImpl} and {@link ItemModel} to {@link LocalDateTime}, {@link LocalDate} and {@link Timestamp} values.
 *
 * @param <T> The type of {@link DataObjectImpl} object.
 * @param <U> The type of {@link ItemModel} object.
 * @author erwinel
 */
public interface ItemTemporalComparer<T extends DataObjectImpl, U extends ItemModel<T>> extends ItemValueComparer<T, U, LocalDateTime> {

    @Override
    public default LocalDateTime get(T dao) {
        return DB.fromUtcTimestamp(getTimestamp(dao));
    }

    /**
     * Gets the {@link Timestamp} value associated with the target {@link DataObjectImpl}.
     *
     * @param dao The target {@link DataObjectImpl}.
     * @return The value associated with the target {@link DataObjectImpl}.
     */
    Timestamp getTimestamp(T dao);
    
    /**
     * Gets the {@link Timestamp} value associated with the target {@link ItemModel}.
     *
     * @param model The target {@link ItemModel}.
     * @return The value associated with the target {@link ItemModel}.
     */
    default Timestamp getTimestamp(U model) {
        return DB.toUtcTimestamp(get(model));
    }
    
    /**
     * Gets the {@link LocalDate} value associated with the target {@link ItemModel}.
     *
     * @param model The target {@link ItemModel}.
     * @return The value associated with the target {@link ItemModel}.
     */
    default LocalDate getLocalDate(U model) {
        return get(model).toLocalDate();
    }
    
    /**
     * Gets the {@link LocalDate} value associated with the target {@link DataObjectImpl}.
     *
     * @param dao The target {@link DataObjectImpl}.
     * @return The value associated with the target {@link DataObjectImpl}.
     */
    default LocalDate getLocalDate(T dao) {
        return DB.fromUtcTimestamp(getTimestamp(dao)).toLocalDate();
    }

    default boolean test(T dao, LocalDate value) {
        LocalDate v = getLocalDate(dao);
        return (null == value) ? null == v : null != v && value.equals(v);
    }

    default boolean test(U model, LocalDate value) {
        LocalDate v = getLocalDate(model);
        return (null == value) ? null == v : null != v && value.equals(v);
    }

    default boolean test(T dao, Timestamp value) {
        Timestamp v = getTimestamp(dao);
        return (null == value) ? null == v : null != v && value.equals(v);
    }

    default boolean test(U model, Timestamp value) {
        Timestamp v = getTimestamp(model);
        return (null == value) ? null == v : null != v && value.equals(v);
    }

    /**
     * Compares the value associated with a {@link DataObjectImpl} object with a {@link LocalDateTime} value.
     *
     * @param dao The target {@link DataObjectImpl}.
     * @param value The value to compare.
     * @return {@code 0} if the value associated with a {@link DataObjectImpl} object is equal to {@code value}. If the value associated with a {@link DataObjectImpl} object is
     * less than {@code value}, a negative value is returned; otherwise a positive value indicates that the value associated with a {@link DataObjectImpl} object is greater than
     * {@code value}.
     */
    default int compareTo(T dao, LocalDateTime value) {
        LocalDateTime s = get(dao);
        if (null == s)
            return (null == value) ? 0 : -1;
        return (null == value) ? 1 : s.compareTo(value);
    }

    /**
     * Compares the value associated with a {@link DataObjectImpl} object a {@link LocalDateTime} value.
     *
     * @param model The target {@link ItemModel}.
     * @param value The value to compare.
     * @return {@code 0} if the value associated with a {@link ItemModel} object is equal to {@code value}. If the value associated with a {@link ItemModel} object is less than
     * {@code value}, a negative value is returned; otherwise a positive value indicates that the value associated with a {@link ItemModel} object is greater than {@code value}.
     */
    default int compareTo(U model, LocalDateTime value) {
        LocalDateTime s = get(model);
        if (null == s)
            return (null == value) ? 0 : -1;
        return (null == value) ? 1 : s.compareTo(value);
    }

    /**
     * Compares the value associated with a {@link DataObjectImpl} object with a {@link LocalDate} value.
     *
     * @param dao The target {@link DataObjectImpl}.
     * @param value The value to compare.
     * @return {@code 0} if the value associated with a {@link DataObjectImpl} object is equal to {@code value}. If the value associated with a {@link DataObjectImpl} object is
     * less than {@code value}, a negative value is returned; otherwise a positive value indicates that the value associated with a {@link DataObjectImpl} object is greater than
     * {@code value}.
     */
    default int compareTo(T dao, LocalDate value) {
        LocalDate s = getLocalDate(dao);
        if (null == s)
            return (null == value) ? 0 : -1;
        return (null == value) ? 1 : s.compareTo(value);
    }

    /**
     * Compares the value associated with a {@link DataObjectImpl} object a {@link LocalDate} value.
     *
     * @param model The target {@link ItemModel}.
     * @param value The value to compare.
     * @return {@code 0} if the value associated with a {@link ItemModel} object is equal to {@code value}. If the value associated with a {@link ItemModel} object is less than
     * {@code value}, a negative value is returned; otherwise a positive value indicates that the value associated with a {@link ItemModel} object is greater than {@code value}.
     */
    default int compareTo(U model, LocalDate value) {
        LocalDate s = getLocalDate(model);
        if (null == s)
            return (null == value) ? 0 : -1;
        return (null == value) ? 1 : s.compareTo(value);
    }

    /**
     * Compares the value associated with a {@link DataObjectImpl} object with a {@link Timestamp} value.
     *
     * @param dao The target {@link DataObjectImpl}.
     * @param value The value to compare.
     * @return {@code 0} if the value associated with a {@link DataObjectImpl} object is equal to {@code value}. If the value associated with a {@link DataObjectImpl} object is
     * less than {@code value}, a negative value is returned; otherwise a positive value indicates that the value associated with a {@link DataObjectImpl} object is greater than
     * {@code value}.
     */
    default int compareTo(T dao, Timestamp value) {
        Timestamp s = getTimestamp(dao);
        if (null == s)
            return (null == value) ? 0 : -1;
        return (null == value) ? 1 : s.compareTo(value);
    }

    /**
     * Compares the value associated with a {@link DataObjectImpl} object a {@link Timestamp} value.
     *
     * @param model The target {@link ItemModel}.
     * @param value The value to compare.
     * @return {@code 0} if the value associated with a {@link ItemModel} object is equal to {@code value}. If the value associated with a {@link ItemModel} object is less than
     * {@code value}, a negative value is returned; otherwise a positive value indicates that the value associated with a {@link ItemModel} object is greater than {@code value}.
     */
    default int compareTo(U model, Timestamp value) {
        Timestamp s = getTimestamp(model);
        if (null == s)
            return (null == value) ? 0 : -1;
        return (null == value) ? 1 : s.compareTo(value);
    }

}
