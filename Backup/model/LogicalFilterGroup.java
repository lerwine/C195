package scheduler.model;

import java.sql.PreparedStatement;
import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Stream;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
 * @param <T>
 */
public interface LogicalFilterGroup<T extends IDataRow> extends DataRowFilter<T>, Iterable<DataRowFilter<T>> {

    FilterLogicalOperator getOperator();

    @Override
    public default void appendSubClauseSql(StringBuilder sql) {
        Iterator<DataRowFilter<T>> it = iterator();
        DataRowFilter<T> filter = it.next();
        if (isLogicalGroup(filter)) {
            sql.append("(");
            filter.appendSubClauseSql(sql);
            sql.append(")");
        } else
            filter.appendSubClauseSql(sql);
        String op = getOperator().toString();
        do {
            filter = it.next();
            if (isLogicalGroup(filter)) {
                sql.append(" ").append(op).append(" (");
                filter.appendSubClauseSql(sql);
                sql.append(")");
            } else {
                sql.append(" ").append(op).append(" ");
                filter.appendSubClauseSql(sql);
            }
        } while (it.hasNext());
    }

    @Override
    public default Integer apply(Integer t, PreparedStatement u) {
        Iterator<DataRowFilter<T>> it = iterator();
        do {
            t = it.next().apply(t, u);
        } while (it.hasNext());
        return t;
    }

    public static <T extends IDataRow> boolean isLogicalGroup(DataRowFilter<T> filter) {
        return null != filter && filter instanceof LogicalFilterGroup;
    }
    
    public static <T extends IDataRow> boolean isLogicalGroup(DataRowFilter<T> filter, FilterLogicalOperator op) {
        return null != filter && filter instanceof LogicalFilterGroup && ((LogicalFilterGroup)filter).getOperator() == op;
    }
    
    public static <T extends IDataRow> LogicalFilterGroup<T> and(DataRowFilter<T> x, DataRowFilter<T> y, DataRowFilter<T>... z) {
        if (null == x || null == y || (null != z && z.length > 0 || Arrays.stream(z).anyMatch((t) -> null == t)))
            throw new NullPointerException();
        if (isLogicalGroup(x, FilterLogicalOperator.AND) || isLogicalGroup(y, FilterLogicalOperator.AND) || 
                (null != z && z.length > 0 || Arrays.stream(z).anyMatch((t) -> isLogicalGroup(t, FilterLogicalOperator.AND))))
            throw new IllegalArgumentException();
        return new LogicalFilterGroup<T>() {
            private final FilterLogicalOperator operator = FilterLogicalOperator.AND;
            private final DataRowFilter<T>[] items = (DataRowFilter<T>[])((null == z || z.length == 0) ? Stream.of(x, y) : Stream.concat(Stream.of(x, y), Arrays.stream(z))).toArray();
            @Override
            public FilterLogicalOperator getOperator() {
                return operator;
            }

            @Override
            public boolean test(T t) {
                return Arrays.stream(items).allMatch((u) -> u.test(t));
            }

            @Override
            public Iterator<DataRowFilter<T>> iterator() {
                return Arrays.stream(items).iterator();
            }

        };
    }

    public static <T extends IDataRow> LogicalFilterGroup<T> or(DataRowFilter<T> x, DataRowFilter<T> y, DataRowFilter<T>... z) {
        if (null == x || null == y || (null != z && z.length > 0 || Arrays.stream(z).anyMatch((t) -> null == t)))
            throw new NullPointerException();
        if (isLogicalGroup(x, FilterLogicalOperator.AND) || isLogicalGroup(y, FilterLogicalOperator.AND) || 
                (null != z && z.length > 0 || Arrays.stream(z).anyMatch((t) -> isLogicalGroup(t, FilterLogicalOperator.AND))))
            throw new IllegalArgumentException();
        return new LogicalFilterGroup<T>() {
            private final FilterLogicalOperator operator = FilterLogicalOperator.OR;
            private final DataRowFilter<T>[] items = (DataRowFilter<T>[])((null == z || z.length == 0) ? Stream.of(x, y) : Stream.concat(Stream.of(x, y), Arrays.stream(z))).toArray();
            @Override
            public FilterLogicalOperator getOperator() {
                return operator;
            }

            @Override
            public boolean test(T t) {
                return Arrays.stream(items).anyMatch((u) -> u.test(t));
            }

            @Override
            public Iterator<DataRowFilter<T>> iterator() {
                return Arrays.stream(items).iterator();
            }

        };
    }

}
