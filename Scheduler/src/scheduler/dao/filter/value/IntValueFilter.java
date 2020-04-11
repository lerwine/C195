package scheduler.dao.filter.value;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;
import java.util.function.IntPredicate;
import java.util.function.IntSupplier;
import scheduler.dao.filter.ComparisonOperator;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface IntValueFilter extends ValueFilter<Integer>, IntPredicate, IntSupplier {

    @Override
    public default void accept(PreparedStatement ps, int index) throws SQLException {
        ps.setInt(index, getAsInt());
    }

    @Override
    public default Integer get() { return getAsInt(); }

    static boolean areEqual(IntValueFilter a, IntValueFilter b) {
        return (null == a) ? null == b : (null != b && a.getAsInt() == b.getAsInt() && a.getOperator() == b.getOperator());
    }

    public static IntValueFilter of(int value, ComparisonOperator operator) {
        int h = 0;
        Objects.requireNonNull(operator);
        for (ComparisonOperator op : ComparisonOperator.values()) {
            if (op == operator) {
                break;
            }
            h++;
        }
        final int hashcode = (value << 4) | h;
        switch (operator) {
            case EQUALS:
                return new IntValueFilter() {
                    @Override
                    public int getAsInt() { return value; }
                    @Override
                    public ComparisonOperator getOperator() { return operator; }
                    @Override
                    public boolean test(int lValue) { return lValue == value; }
                    @Override
                    public int hashCode() { return hashcode; }
                    @Override
                    public boolean equals(Object obj) { return null != obj && obj instanceof IntValueFilter && areEqual(this, (IntValueFilter) obj); }
                };
            case NOT_EQUALS:
                return new IntValueFilter() {
                    @Override
                    public int getAsInt() { return value; }
                    @Override
                    public ComparisonOperator getOperator() { return operator; }
                    @Override
                    public boolean test(int lValue) { return lValue != value; }
                    @Override
                    public int hashCode() { return hashcode; }
                    @Override
                    public boolean equals(Object obj) { return null != obj && obj instanceof IntValueFilter && areEqual(this, (IntValueFilter) obj); }
                };
            case GREATER_THAN:
                return new IntValueFilter() {
                    @Override
                    public int getAsInt() { return value; }
                    @Override
                    public ComparisonOperator getOperator() { return operator; }
                    @Override
                    public boolean test(int lValue) { return lValue > value; }
                    @Override
                    public int hashCode() { return hashcode; }
                    @Override
                    public boolean equals(Object obj) { return null != obj && obj instanceof IntValueFilter && areEqual(this, (IntValueFilter) obj); }
                };
            case NOT_LESS_THAN:
                return new IntValueFilter() {
                    @Override
                    public int getAsInt() { return value; }
                    @Override
                    public ComparisonOperator getOperator() { return operator; }
                    @Override
                    public boolean test(int lValue) { return lValue >= value; }
                    @Override
                    public int hashCode() { return hashcode; }
                    @Override
                    public boolean equals(Object obj) { return null != obj && obj instanceof IntValueFilter && areEqual(this, (IntValueFilter) obj); }
                };
            case LESS_THAN:
                return new IntValueFilter() {
                    @Override
                    public int getAsInt() { return value; }
                    @Override
                    public ComparisonOperator getOperator() { return operator; }
                    @Override
                    public boolean test(int lValue) { return lValue < value; }
                    @Override
                    public int hashCode() { return hashcode; }
                    @Override
                    public boolean equals(Object obj) { return null != obj && obj instanceof IntValueFilter && areEqual(this, (IntValueFilter) obj); }
                };
            case NOT_GREATER_THAN:
                return new IntValueFilter() {
                    @Override
                    public int getAsInt() { return value; }
                    @Override
                    public ComparisonOperator getOperator() { return operator; }
                    @Override
                    public boolean test(int lValue) { return lValue <= value; }
                    @Override
                    public int hashCode() { return hashcode; }
                    @Override
                    public boolean equals(Object obj) { return null != obj && obj instanceof IntValueFilter && areEqual(this, (IntValueFilter) obj); }
                };
            default:
                throw new IllegalArgumentException(String.format("Operator %s cannot be applied to integer values", operator));
        }
    }
}
