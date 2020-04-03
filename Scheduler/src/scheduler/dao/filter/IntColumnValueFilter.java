package scheduler.dao.filter;

import java.util.Objects;
import scheduler.dao.filter.value.IntValueFilter;
import java.util.function.ToIntFunction;
import scheduler.dao.DataAccessObject;
import scheduler.dao.schema.DbColumn;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
 * @param <T>
 */
public interface IntColumnValueFilter<T extends DataAccessObject> extends ColumnValueFilter<T, Integer, IntValueFilter>, ToIntFunction<T> {

    @Override
    public default Integer apply(T t) {
        return applyAsInt(t);
    }

    public static <T extends DataAccessObject> IntColumnValueFilter<T> of(DbColumn column, IntValueFilter filter, ToIntFunction<T> getColumnValue) {
        int h = 0;
        Objects.requireNonNull(column);
        for (DbColumn c : DbColumn.values()) {
            if (c == column) {
                break;
            }
            h++;
        }
        final int hashcode = (filter.hashCode() << 6) | h;
        return new IntColumnValueFilter<T>() {
            @Override
            public DbColumn getColumn() {
                return column;
            }

            @Override
            public IntValueFilter getValueFilter() {
                return filter;
            }

            @Override
            public boolean test(T t) {
                return getValueFilter().test(getColumnValue.applyAsInt(t));
            }

            @Override
            public int applyAsInt(T value) {
                return getColumnValue.applyAsInt(value);
            }

            @Override
            public int hashCode() {
                return hashcode;
            }

            @Override
            public boolean equals(Object obj) {
                if (null != obj && obj instanceof IntColumnValueFilter) {
                    IntColumnValueFilter<T> other = (IntColumnValueFilter<T>)obj;
                    return getColumn() == other.getColumn() && getValueFilter().equals(other.getValueFilter());
                }
                return false;
            }
            
        };
    }

    public static <T extends DataAccessObject> IntColumnValueFilter<T> of(DbColumn column, ComparisonOperator operator, int value,
            ToIntFunction<T> getColumnValue) {
        return of (column, IntValueFilter.of(value, operator), getColumnValue);
    }
    
}
