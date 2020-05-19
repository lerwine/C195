package scheduler.dao.filter;

import scheduler.dao.filter.value.TimestampValueFilter;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.Temporal;
import java.util.Objects;
import java.util.function.Function;
import scheduler.dao.DbRecordBase;
import scheduler.dao.schema.DbColumn;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T>
 * @param <U>
 */
public interface TimestampColumnValueFilter<T extends DbRecordBase, U extends Temporal>
        extends ColumnValueFilter<T, Timestamp, TimestampValueFilter<U>> {

    public static <T extends DbRecordBase, U extends Temporal> TimestampColumnValueFilter<T, U> of(DbColumn column,
            TimestampValueFilter<U> filter, Function<T, Timestamp> getColumnValue) {
        int h = 0;
        Objects.requireNonNull(column);
        for (DbColumn c : DbColumn.values()) {
            if (c == column) {
                break;
            }
            h++;
        }
        final int hashcode = (filter.hashCode() << 6) | h;
        return new TimestampColumnValueFilter<T, U>() {
            @Override
            public DbColumn getColumn() {
                return column;
            }

            @Override
            public TimestampValueFilter<U> getValueFilter() {
                return filter;
            }

            @Override
            public boolean test(T t) {
                return getValueFilter().test(getColumnValue.apply(t));
            }

            @Override
            public Timestamp apply(T t) {
                return getColumnValue.apply(t);
            }

            @Override
            public int hashCode() {
                return hashcode;
            }

            @SuppressWarnings("unchecked")
            @Override
            public boolean equals(Object obj) {
                if (null != obj && obj instanceof TimestampColumnValueFilter) {
                    TimestampColumnValueFilter<T, U> other = (TimestampColumnValueFilter<T, U>) obj;
                    return getColumn() == other.getColumn() && getValueFilter().equals(other.getValueFilter());
                }
                return false;
            }

        };
    }

    public static <T extends DbRecordBase> TimestampColumnValueFilter<T, LocalDateTime> of(DbColumn column, ComparisonOperator operator,
            Timestamp value, Function<T, Timestamp> getColumnValue) {
        return of(column, TimestampValueFilter.of(value, operator), getColumnValue);
    }

    public static <T extends DbRecordBase> TimestampColumnValueFilter<T, LocalDateTime> of(DbColumn column, ComparisonOperator operator,
            LocalDateTime value, Function<T, Timestamp> getColumnValue) {
        return of(column, TimestampValueFilter.of(value, operator), getColumnValue);
    }

    public static <T extends DbRecordBase> TimestampColumnValueFilter<T, LocalDate> of(DbColumn column, ComparisonOperator operator,
            LocalDate value, Function<T, Timestamp> getColumnValue) {
        return of(column, TimestampValueFilter.of(value, operator), getColumnValue);
    }

}
