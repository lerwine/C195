package scheduler.dao.filter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import scheduler.dao.DataAccessObject;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
 * @param <T>
 */
public interface LogicalFilter<T extends DataAccessObject> extends DaoFilterExpression<T> {

    static <T extends DataAccessObject> boolean areEqual(LogicalFilter<T> a, LogicalFilter<T> b) {
        if (null == a) {
            return null == b;
        }
        if (null == b) {
            return false;
        }
        if (a == b) {
            return true;
        }
        if (a.getLogicalOperator() == b.getLogicalOperator()) {
            List<DaoFilterExpression<T>> x = a.getSubExpressions();
            List<DaoFilterExpression<T>> y = b.getSubExpressions();
            if (x.size() == y.size()) {
                Iterator<DaoFilterExpression<T>> i = x.iterator();
                Iterator<DaoFilterExpression<T>> j = y.iterator();
                while (i.hasNext()) {
                    if (!(j.hasNext() && i.next().equals(j.next()))) {
                        return false;
                    }
                }
                return !j.hasNext();
            }
        }
        return false;
    }

    public static boolean isLogicalOperator(DaoFilterExpression<? extends DataAccessObject> expr, LogicalOperator op) {
        return null != expr && expr instanceof LogicalFilter && ((LogicalFilter<? extends DataAccessObject>) expr).getLogicalOperator() == op;
    }

    public static <T extends DataAccessObject> DaoFilterExpression<T> of(LogicalOperator operator, DaoFilterExpression<T>... expr) {
        ArrayList<DaoFilterExpression<T>> items = new ArrayList<>();
        Arrays.stream(expr).forEach((t) -> {
            if (null != t && !t.isEmpty() && !items.contains(t)) {
                if (isLogicalOperator(t, operator)) {
                    ((LogicalFilter<T>) t).getSubExpressions().forEach((u) -> {
                        if (!items.contains(u)) {
                            items.add(u);
                        }
                    });
                } else {
                    items.add(t);
                }
            }
        });
        if (items.isEmpty()) {
            return DaoFilterExpression.empty();
        }
        if (items.size() == 1) {
            return items.get(0);
        }
        if (operator == LogicalOperator.OR) {
            return new LogicalFilter<T>() {
                private final List<DaoFilterExpression<T>> subExpressions = Collections.unmodifiableList(items);

                @Override
                public LogicalOperator getLogicalOperator() {
                    return LogicalOperator.OR;
                }

                @Override
                public List<DaoFilterExpression<T>> getSubExpressions() {
                    return subExpressions;
                }

                @Override
                public void appendJoinedDmlConditional(StringBuffer sb) {
                    Iterator<DaoFilterExpression<T>> iterator = getSubExpressions().iterator();
                    DaoFilterExpression<T> expr = iterator.next();
                    if (expr instanceof LogicalFilter) {
                        sb.append("(");
                        expr.appendJoinedDmlConditional(sb);
                        sb.append(")");
                    } else {
                        expr.appendJoinedDmlConditional(sb);
                    }
                    do {
                        sb.append(" OR ");
                        expr = iterator.next();
                        if (expr instanceof LogicalFilter) {
                            sb.append("(");
                            expr.appendJoinedDmlConditional(sb);
                            sb.append(")");
                        } else {
                            expr.appendJoinedDmlConditional(sb);
                        }
                    } while (iterator.hasNext());
                }

                @Override
                public void appendSimpleDmlConditional(StringBuffer sb) {
                    Iterator<DaoFilterExpression<T>> iterator = getSubExpressions().iterator();
                    DaoFilterExpression<T> expr = iterator.next();
                    if (expr instanceof LogicalFilter) {
                        sb.append("(");
                        expr.appendSimpleDmlConditional(sb);
                        sb.append(")");
                    } else {
                        expr.appendSimpleDmlConditional(sb);
                    }
                    do {
                        sb.append(" OR ");
                        expr = iterator.next();
                        if (expr instanceof LogicalFilter) {
                            sb.append("(");
                            expr.appendSimpleDmlConditional(sb);
                            sb.append(")");
                        } else {
                            expr.appendSimpleDmlConditional(sb);
                        }
                    } while (iterator.hasNext());
                }

                @Override
                public int applyWhereParameters(PreparedStatement ps, int index) throws SQLException {
                    for (DaoFilterExpression<T> t : getSubExpressions()) {
                        index = t.applyWhereParameters(ps, index);
                    }
                    return index;
                }

                @Override
                public boolean test(T t) {
                    return getSubExpressions().stream().anyMatch((u) -> u.test(t));
                }

            };
        }

        return new LogicalFilter<T>() {
            private final List<DaoFilterExpression<T>> subExpressions = Collections.unmodifiableList(items);

            @Override
            public LogicalOperator getLogicalOperator() {
                return LogicalOperator.AND;
            }

            @Override
            public List<DaoFilterExpression<T>> getSubExpressions() {
                return subExpressions;
            }

            @Override
            public void appendJoinedDmlConditional(StringBuffer sb) {
                Iterator<DaoFilterExpression<T>> iterator = getSubExpressions().iterator();
                DaoFilterExpression<T> expr = iterator.next();
                if (expr instanceof LogicalFilter) {
                    sb.append("(");
                    expr.appendJoinedDmlConditional(sb);
                    sb.append(")");
                } else {
                    expr.appendJoinedDmlConditional(sb);
                }
                do {
                    sb.append(" AND ");
                    expr = iterator.next();
                    if (expr instanceof LogicalFilter) {
                        sb.append("(");
                        expr.appendJoinedDmlConditional(sb);
                        sb.append(")");
                    } else {
                        expr.appendJoinedDmlConditional(sb);
                    }
                } while (iterator.hasNext());
            }

            @Override
            public void appendSimpleDmlConditional(StringBuffer sb) {
                Iterator<DaoFilterExpression<T>> iterator = getSubExpressions().iterator();
                DaoFilterExpression<T> expr = iterator.next();
                if (expr instanceof LogicalFilter) {
                    sb.append("(");
                    expr.appendSimpleDmlConditional(sb);
                    sb.append(")");
                } else {
                    expr.appendSimpleDmlConditional(sb);
                }
                do {
                    sb.append(" AND ");
                    expr = iterator.next();
                    if (expr instanceof LogicalFilter) {
                        sb.append("(");
                        expr.appendSimpleDmlConditional(sb);
                        sb.append(")");
                    } else {
                        expr.appendSimpleDmlConditional(sb);
                    }
                } while (iterator.hasNext());
            }

            @Override
            public int applyWhereParameters(PreparedStatement ps, int index) throws SQLException {
                for (DaoFilterExpression<T> t : getSubExpressions()) {
                    index = t.applyWhereParameters(ps, index);
                }
                return index;
            }

            @Override
            public boolean test(T t) {
                return getSubExpressions().stream().allMatch((u) -> u.test(t));
            }

        };
    }

    LogicalOperator getLogicalOperator();

    List<DaoFilterExpression<T>> getSubExpressions();
}
