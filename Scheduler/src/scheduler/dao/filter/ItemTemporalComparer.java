package scheduler.dao.filter;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import scheduler.dao.DataAccessObject;
import scheduler.util.DB;
import scheduler.model.ui.FxRecordModel;

/**
 * Compares {@link DataAccessObject} and {@link FxRecordModel} to {@link LocalDateTime}, {@link LocalDate} and {@link Timestamp} values.
 *
 * @param <T> The type of {@link DataAccessObject} object.
 * @param <U> The type of {@link FxRecordModel} object.
 @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface ItemTemporalComparer<T extends DataAccessObject, U extends FxRecordModel<T>> extends ItemValueComparer<T, U, LocalDateTime> {

    @Override
    public default LocalDateTime get(T dao) {
        return DB.toLocalDateTime(getTimestamp(dao));
    }

    /**
     * Gets the {@link Timestamp} value associated with the target {@link DataAccessObject}.
     *
     * @param dao The target {@link DataAccessObject}.
     * @return The value associated with the target {@link DataAccessObject}.
     */
    Timestamp getTimestamp(T dao);

    /**
     * Gets the {@link Timestamp} value associated with the target {@link FxRecordModel}.
     *
     * @param model The target {@link FxRecordModel}.
     * @return The value associated with the target {@link FxRecordModel}.
     */
    default Timestamp getTimestamp(U model) {
        return DB.toUtcTimestamp(get(model));
    }

    /**
     * Gets the {@link LocalDate} value associated with the target {@link FxRecordModel}.
     *
     * @param model The target {@link FxRecordModel}.
     * @return The value associated with the target {@link FxRecordModel}.
     */
    default LocalDate getLocalDate(U model) {
        return get(model).toLocalDate();
    }

    /**
     * Gets the {@link LocalDate} value associated with the target {@link DataAccessObject}.
     *
     * @param dao The target {@link DataAccessObject}.
     * @return The value associated with the target {@link DataAccessObject}.
     */
    default LocalDate getLocalDate(T dao) {
        return DB.toLocalDateTime(getTimestamp(dao)).toLocalDate();
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
     * Compares the value associated with a {@link DataAccessObject} object with a {@link LocalDateTime} value.
     *
     * @param dao The target {@link DataAccessObject}.
     * @param value The value to compare.
     * @return {@code 0} if the value associated with a {@link DataAccessObject} object is equal to {@code value}. If the value associated with a
     * {@link DataAccessObject} object is less than {@code value}, a negative value is returned; otherwise a positive value indicates that the value
     * associated with a {@link DataAccessObject} object is greater than {@code value}.
     */
    default int compareTo(T dao, LocalDateTime value) {
        LocalDateTime s = get(dao);
        if (null == s) {
            return (null == value) ? 0 : -1;
        }
        return (null == value) ? 1 : s.compareTo(value);
    }

    /**
     * Compares the value associated with a {@link DataAccessObject} object a {@link LocalDateTime} value.
     *
     * @param model The target {@link FxRecordModel}.
     * @param value The value to compare.
     * @return {@code 0} if the value associated with a {@link FxRecordModel} object is equal to {@code value}. If the value associated with a
     * {@link FxRecordModel} object is less than {@code value}, a negative value is returned; otherwise a positive value indicates that the value
     * associated with a {@link FxRecordModel} object is greater than {@code value}.
     */
    default int compareTo(U model, LocalDateTime value) {
        LocalDateTime s = get(model);
        if (null == s) {
            return (null == value) ? 0 : -1;
        }
        return (null == value) ? 1 : s.compareTo(value);
    }

    /**
     * Compares the value associated with a {@link DataAccessObject} object with a {@link LocalDate} value.
     *
     * @param dao The target {@link DataAccessObject}.
     * @param value The value to compare.
     * @return {@code 0} if the value associated with a {@link DataAccessObject} object is equal to {@code value}. If the value associated with a
     * {@link DataAccessObject} object is less than {@code value}, a negative value is returned; otherwise a positive value indicates that the value
     * associated with a {@link DataAccessObject} object is greater than {@code value}.
     */
    default int compareTo(T dao, LocalDate value) {
        LocalDate s = getLocalDate(dao);
        if (null == s) {
            return (null == value) ? 0 : -1;
        }
        return (null == value) ? 1 : s.compareTo(value);
    }

    /**
     * Compares the value associated with a {@link DataAccessObject} object a {@link LocalDate} value.
     *
     * @param model The target {@link FxRecordModel}.
     * @param value The value to compare.
     * @return {@code 0} if the value associated with a {@link FxRecordModel} object is equal to {@code value}. If the value associated with a
     * {@link FxRecordModel} object is less than {@code value}, a negative value is returned; otherwise a positive value indicates that the value
     * associated with a {@link FxRecordModel} object is greater than {@code value}.
     */
    default int compareTo(U model, LocalDate value) {
        LocalDate s = getLocalDate(model);
        if (null == s) {
            return (null == value) ? 0 : -1;
        }
        return (null == value) ? 1 : s.compareTo(value);
    }

    /**
     * Compares the value associated with a {@link DataAccessObject} object with a {@link Timestamp} value.
     *
     * @param dao The target {@link DataAccessObject}.
     * @param value The value to compare.
     * @return {@code 0} if the value associated with a {@link DataAccessObject} object is equal to {@code value}. If the value associated with a
     * {@link DataAccessObject} object is less than {@code value}, a negative value is returned; otherwise a positive value indicates that the value
     * associated with a {@link DataAccessObject} object is greater than {@code value}.
     */
    default int compareTo(T dao, Timestamp value) {
        Timestamp s = getTimestamp(dao);
        if (null == s) {
            return (null == value) ? 0 : -1;
        }
        return (null == value) ? 1 : s.compareTo(value);
    }

    /**
     * Compares the value associated with a {@link DataAccessObject} object a {@link Timestamp} value.
     *
     * @param model The target {@link FxRecordModel}.
     * @param value The value to compare.
     * @return {@code 0} if the value associated with a {@link FxRecordModel} object is equal to {@code value}. If the value associated with a
     * {@link FxRecordModel} object is less than {@code value}, a negative value is returned; otherwise a positive value indicates that the value
     * associated with a {@link FxRecordModel} object is greater than {@code value}.
     */
    default int compareTo(U model, Timestamp value) {
        Timestamp s = getTimestamp(model);
        if (null == s) {
            return (null == value) ? 0 : -1;
        }
        return (null == value) ? 1 : s.compareTo(value);
    }

}
