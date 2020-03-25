package scheduler.dao.dml;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Stream;
import scheduler.dao.DataObjectImpl;
import scheduler.util.ReadOnlyList;
import scheduler.view.ItemModel;

/**
 * Represents a logical grouping of {@link WhereStatement} objects.
 *
 * @author Leonard T. Erwine (Student ID 356334)
 * @param <T> The data access object type.
 * @param <U> The item model type.
 */
public interface LogicalStatement<T extends DataObjectImpl, U extends ItemModel<T>> extends WhereStatement<T, U>, ReadOnlyList<WhereStatement<T, U>> {

    LogicalOperator getOperator();

    @Override
    public default int applyValues(PreparedStatement ps, int currentIndex) throws SQLException {
        Iterator<WhereStatement<T, U>> it = iterator();
        do {
            currentIndex = it.next().applyValues(ps, currentIndex);
        } while (it.hasNext());
        return currentIndex;
    }

    @Override
    public default void appendSqlStatement(StringBuilder stringBuilder) {
        Iterator<WhereStatement<T, U>> it = iterator();
        WhereStatement<T, U> filter = it.next();
        if (isLogicalGroup(filter)) {
            stringBuilder.append("(");
            filter.appendSqlStatement(stringBuilder);
            stringBuilder.append(")");
        } else
            filter.appendSqlStatement(stringBuilder);
        String op = getOperator().toString();
        do {
            filter = it.next();
            if (isLogicalGroup(filter)) {
                stringBuilder.append(" ").append(op).append(" (");
                filter.appendSqlStatement(stringBuilder);
                stringBuilder.append(")");
            } else {
                stringBuilder.append(" ").append(op).append(" ");
                filter.appendSqlStatement(stringBuilder);
            }
        } while (it.hasNext());
    }

    public static <T extends DataObjectImpl, U extends ItemModel<T>> boolean isLogicalGroup(WhereStatement<T, U> filter) {
        return null != filter && filter instanceof LogicalStatement;
    }

    public static <T extends DataObjectImpl, U extends ItemModel<T>> boolean isLogicalGroup(WhereStatement<T, U> filter, LogicalOperator op) {
        return null != filter && filter instanceof LogicalStatement && ((LogicalStatement<T, U>) filter).getOperator() == op;
    }
    
    public static <T extends DataObjectImpl, U extends ItemModel<T>> LogicalStatement<T, U> and(WhereStatement<T, U> x, WhereStatement<T, U> y, WhereStatement<T, U>... z) {
        if (null == x || null == y || (null != z && z.length > 0 || Arrays.stream(z).anyMatch((t) -> null == t))) {
            throw new NullPointerException();
        }
        if (isLogicalGroup(x, LogicalOperator.AND) || isLogicalGroup(y, LogicalOperator.AND)
                || (null != z && z.length > 0 || Arrays.stream(z).anyMatch((t) -> isLogicalGroup(t, LogicalOperator.AND)))) {
            throw new IllegalArgumentException();
        }

        return new LogicalStatement<T, U>() {
            private final LogicalOperator operator = LogicalOperator.AND;
            private final WhereStatement<T, U>[] items = (WhereStatement<T, U>[]) ((null == z || z.length == 0) ? Stream.of(x, y) : Stream.concat(Stream.of(x, y), Arrays.stream(z))).toArray();

            @Override
            public LogicalOperator getOperator() {
                return operator;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public boolean test(U t) {
                return Arrays.stream(items).allMatch((u) -> u.test(t));
            }

            @Override
            public int size() {
                return items.length;
            }

            @Override
            public boolean contains(Object o) {
                return null != o && o instanceof WhereStatement && Arrays.stream(items).anyMatch((t) -> o.equals(t));
            }

            @Override
            public Iterator<WhereStatement<T, U>> iterator() {
                return ReadOnlyList.of(items).iterator();
            }

            @Override
            public Object[] toArray() {
                Object[] result = new Object[items.length];
                System.arraycopy(items, 0, result, 0, items.length);
                return result;
            }

            @Override
            public boolean containsAll(Collection<?> c) {
                return c.stream().allMatch((t) -> contains((WhereStatement<T, U>) t));
            }

            @Override
            public WhereStatement<T, U> get(int index) {
                return items[index];
            }

            @Override
            public int indexOf(Object o) {
                if (null == o) {
                    for (int i = 0; i < items.length; i++) {
                        if (null == items[i]) {
                            return i;
                        }
                    }
                } else {
                    for (int i = 0; i < items.length; i++) {
                        if (o.equals(items[i])) {
                            return i;
                        }
                    }
                }
                return -1;
            }

            @Override
            public int lastIndexOf(Object o) {
                if (null == o) {
                    for (int i = items.length - 1; i >= 0 ; i--) {
                        if (null == items[i]) {
                            return i;
                        }
                    }
                } else {
                    for (int i = items.length - 1; i >= 0 ; i--) {
                        if (o.equals(items[i])) {
                            return i;
                        }
                    }
                }
                return -1;
            }

            @Override
            public ListIterator<WhereStatement<T, U>> listIterator() {
                return ReadOnlyList.of(items).listIterator();
            }

            @Override
            public ListIterator<WhereStatement<T, U>> listIterator(int index) {
                return ReadOnlyList.of(items).listIterator(index);
            }

            @Override
            public List<WhereStatement<T, U>> subList(int fromIndex, int toIndex) {
                return ReadOnlyList.of(items, fromIndex, toIndex);
            }

        };
    }

    public static <T extends DataObjectImpl, U extends ItemModel<T>> LogicalStatement<T, U> or(WhereStatement<T, U> x, WhereStatement<T, U> y, WhereStatement<T, U>... z) {
        if (null == x || null == y || (null != z && z.length > 0 || Arrays.stream(z).anyMatch((t) -> null == t))) {
            throw new NullPointerException();
        }
        if (isLogicalGroup(x, LogicalOperator.OR) || isLogicalGroup(y, LogicalOperator.OR)
                || (null != z && z.length > 0 || Arrays.stream(z).anyMatch((t) -> isLogicalGroup(t, LogicalOperator.OR)))) {
            throw new IllegalArgumentException();
        }

        return new LogicalStatement<T, U>() {
            private final LogicalOperator operator = LogicalOperator.OR;
            private final WhereStatement<T, U>[] items = (WhereStatement<T, U>[]) ((null == z || z.length == 0) ? Stream.of(x, y) : Stream.concat(Stream.of(x, y), Arrays.stream(z))).toArray();

            @Override
            public LogicalOperator getOperator() {
                return operator;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public boolean test(U t) {
                return Arrays.stream(items).allMatch((u) -> u.test(t));
            }

            @Override
            public int size() {
                return items.length;
            }

            @Override
            public boolean contains(Object o) {
                return null != o && o instanceof WhereStatement && Arrays.stream(items).anyMatch((t) -> o.equals(t));
            }

            @Override
            public Iterator<WhereStatement<T, U>> iterator() {
                return ReadOnlyList.of(items).iterator();
            }

            @Override
            public Object[] toArray() {
                Object[] result = new Object[items.length];
                System.arraycopy(items, 0, result, 0, items.length);
                return result;
            }

            @Override
            public boolean containsAll(Collection<?> c) {
                return c.stream().allMatch((t) -> contains((WhereStatement<T, U>) t));
            }

            @Override
            public WhereStatement<T, U> get(int index) {
                return items[index];
            }

            @Override
            public int indexOf(Object o) {
                if (null == o) {
                    for (int i = 0; i < items.length; i++) {
                        if (null == items[i]) {
                            return i;
                        }
                    }
                } else {
                    for (int i = 0; i < items.length; i++) {
                        if (o.equals(items[i])) {
                            return i;
                        }
                    }
                }
                return -1;
            }

            @Override
            public int lastIndexOf(Object o) {
                if (null == o) {
                    for (int i = items.length - 1; i >= 0 ; i--) {
                        if (null == items[i]) {
                            return i;
                        }
                    }
                } else {
                    for (int i = items.length - 1; i >= 0 ; i--) {
                        if (o.equals(items[i])) {
                            return i;
                        }
                    }
                }
                return -1;
            }

            @Override
            public ListIterator<WhereStatement<T, U>> listIterator() {
                return ReadOnlyList.of(items).listIterator();
            }

            @Override
            public ListIterator<WhereStatement<T, U>> listIterator(int index) {
                return ReadOnlyList.of(items).listIterator(index);
            }

            @Override
            public List<WhereStatement<T, U>> subList(int fromIndex, int toIndex) {
                return ReadOnlyList.of(items, fromIndex, toIndex);
            }

        };
    }

}
