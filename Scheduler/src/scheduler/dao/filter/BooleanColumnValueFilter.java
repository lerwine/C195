package scheduler.dao.filter;

import java.util.Objects;
import java.util.function.Predicate;
import scheduler.dao.filter.value.BooleanValueFilter;
import scheduler.dao.DataAccessObject;
import scheduler.dao.schema.DbColumn;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T>
 */
public interface BooleanColumnValueFilter<T extends DataAccessObject> extends ColumnValueFilter<T, Boolean, BooleanValueFilter> {
    boolean applyAsBoolean(T t);

    @Override
    public default Boolean apply(T t) { return applyAsBoolean(t); }

    public static <T extends DataAccessObject> BooleanColumnValueFilter<T> of(DbColumn column, BooleanValueFilter filter, Predicate<T> getColumnValue) {
        int h = 0;
        Objects.requireNonNull(column);
        for (DbColumn c : DbColumn.values()) {
            if (c == column) {
                break;
            }
            h++;
        }
        final int hashcode = (filter.hashCode() << 6) | h;
        return new BooleanColumnValueFilter<T>() {
            @Override
            public boolean applyAsBoolean(T t) {
                return getColumnValue.test(t);
            }

            @Override
            public DbColumn getColumn() {
                return column;
            }

            @Override
            public BooleanValueFilter getValueFilter() {
                return filter;
            }

            @Override
            public boolean test(T t) {
                return getValueFilter().test(getColumnValue.test(t));
            }

            @Override
            public int hashCode() {
                return hashcode;
            }

            @SuppressWarnings("unchecked")
            @Override
            public boolean equals(Object obj) {
                if (null != obj && obj instanceof BooleanColumnValueFilter) {
                    BooleanColumnValueFilter<T> other = (BooleanColumnValueFilter<T>)obj;
                    return getColumn() == other.getColumn() && getValueFilter().equals(other.getValueFilter());
                }
                return false;
            }
            
        };
    }

    public static <T extends DataAccessObject> BooleanColumnValueFilter<T> ofTrue(DbColumn column, Predicate<T> getColumnValue) {
        return of(column, BooleanValueFilter.ofTrue(), getColumnValue);
    }
    
    public static <T extends DataAccessObject> BooleanColumnValueFilter<T> ofFalse(DbColumn column, Predicate<T> getColumnValue) {
        return of(column, BooleanValueFilter.ofFalse(), getColumnValue);
    }
    
    public static <T extends DataAccessObject> BooleanColumnValueFilter<T> of(DbColumn column, boolean value, Predicate<T> getColumnValue) {
        return of(column, BooleanValueFilter.of(value), getColumnValue);
    }
    
    public static <T extends DataAccessObject> BooleanColumnValueFilter<T> ofNot(DbColumn column, boolean value, Predicate<T> getColumnValue) {
        return of(column, BooleanValueFilter.ofNot(value), getColumnValue);
    }
    
}
