package scheduler.dao.filter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;
import java.util.function.Function;
import scheduler.dao.filter.value.StringValueFilter;
import scheduler.dao.DataAccessObject;
import scheduler.dao.schema.DbColumn;
import scheduler.dao.schema.ValueType;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T>
 */
public interface StringColumnValueFilter<T extends DataAccessObject> extends ColumnValueFilter<T, String, StringValueFilter> {

    public static <T extends DataAccessObject> StringColumnValueFilter<T> of(DbColumn column, StringValueFilter filter,
            Function<T, String> getColumnValue) {
        if (column.getType().getValueType() != ValueType.STRING) {
            throw new IllegalArgumentException("Column is not a string type");
        }
        int h = 0;
        Objects.requireNonNull(column);
        for (DbColumn c : DbColumn.values()) {
            if (c == column) {
                break;
            }
            h++;
        }
        final int hashcode = (filter.hashCode() << 6) | h;
        return new StringColumnValueFilter<T>() {
            @Override
            public DbColumn getColumn() {
                return column;
            }

            @Override
            public StringValueFilter getValueFilter() {
                return filter;
            }

            @Override
            public boolean test(T t) {
                return getValueFilter().test(getColumnValue.apply(t));
            }

            @Override
            public String apply(T t) {
                return getColumnValue.apply(t);
            }

            @Override
            public int hashCode() {
                return hashcode;
            }

            @SuppressWarnings("unchecked")
            @Override
            public boolean equals(Object obj) {
                if (null != obj && obj instanceof StringColumnValueFilter) {
                    StringColumnValueFilter<T> other = (StringColumnValueFilter<T>) obj;
                    return getColumn() == other.getColumn() && getValueFilter().equals(other.getValueFilter());
                }
                return false;
            }

        };
    }

    public static <T extends DataAccessObject> StringColumnValueFilter<T> of(DbColumn column, ComparisonOperator operator, String value,
            Function<T, String> getColumnValue) {
        return of(column, StringValueFilter.of(value, operator), getColumnValue);
    }

    public static <T extends DataAccessObject> StringColumnValueFilter<T> ofEmptyString(DbColumn column, Function<T, String> getColumnValue) {
        return new StringColumnValueFilter<T>() {
            private final StringValueFilter valueFilter = StringValueFilter.ofEmptyString(false);

            @Override
            public DbColumn getColumn() {
                return column;
            }

            @Override
            public StringValueFilter getValueFilter() {
                return valueFilter;
            }

            @Override
            public void appendJoinedDmlConditional(StringBuffer sb) {
                DbColumn column = getColumn();
                sb.append("LENGTH(").append(column.getTable()).append(".").append(column.getDbName()).append(")=0");
            }

            @Override
            public void appendSimpleDmlConditional(StringBuffer sb) {
                sb.append("LENGTH(");
                DbColumn column = getColumn();
                String n = column.getDbName().toString();
                if (!n.equals(column.toString())) {
                    sb.append(column.getTable().getDbName()).append(".");
                }
                sb.append(n).append(")=0");
            }

            @Override
            public int applyWhereParameters(PreparedStatement ps, int index) throws SQLException {
                return index;
            }

            @Override
            public boolean test(T t) {
                return getValueFilter().test(getColumnValue.apply(t));
            }

            @Override
            public String apply(T t) {
                return getColumnValue.apply(t);
            }

            @Override
            public int hashCode() {
                return valueFilter.hashCode();
            }

            @SuppressWarnings("unchecked")
            @Override
            public boolean equals(Object obj) {
                if (null != obj && obj instanceof StringColumnValueFilter) {
                    StringColumnValueFilter<T> other = (StringColumnValueFilter<T>) obj;
                    return getColumn() == other.getColumn() && getValueFilter().equals(other.getValueFilter());
                }
                return false;
            }

        };
    }

    public static <T extends DataAccessObject> StringColumnValueFilter<T> ofNotEmptyString(DbColumn column, Function<T, String> getColumnValue) {
        return new StringColumnValueFilter<T>() {
            private final StringValueFilter valueFilter = StringValueFilter.ofEmptyString(true);

            @Override
            public DbColumn getColumn() {
                return column;
            }

            @Override
            public StringValueFilter getValueFilter() {
                return valueFilter;
            }

            @Override
            public void appendJoinedDmlConditional(StringBuffer sb) {
                DbColumn column = getColumn();
                sb.append("LENGTH(").append(column.getTable()).append(".").append(column.getDbName()).append(")>0");
            }

            @Override
            public void appendSimpleDmlConditional(StringBuffer sb) {
                sb.append("LENGTH(");
                DbColumn column = getColumn();
                String n = column.getDbName().toString();
                if (!n.equals(column.toString())) {
                    sb.append(column.getTable().getDbName()).append(".");
                }
                sb.append(n).append(")>0");
            }

            @Override
            public int applyWhereParameters(PreparedStatement ps, int index) throws SQLException {
                return index;
            }

            @Override
            public boolean test(T t) {
                return getValueFilter().test(getColumnValue.apply(t));
            }

            @Override
            public String apply(T t) {
                return getColumnValue.apply(t);
            }

            @Override
            public int hashCode() {
                return valueFilter.hashCode();
            }

            @SuppressWarnings("unchecked")
            @Override
            public boolean equals(Object obj) {
                if (null != obj && obj instanceof StringColumnValueFilter) {
                    StringColumnValueFilter<T> other = (StringColumnValueFilter<T>) obj;
                    return getColumn() == other.getColumn() && getValueFilter().equals(other.getValueFilter());
                }
                return false;
            }

        };
    }

}
