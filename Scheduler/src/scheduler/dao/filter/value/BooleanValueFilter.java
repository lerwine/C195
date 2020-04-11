package scheduler.dao.filter.value;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.function.BooleanSupplier;
import scheduler.dao.filter.ComparisonOperator;
/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface BooleanValueFilter extends ValueFilter<Boolean>, BooleanSupplier {

    @Override
    public default void accept(PreparedStatement ps, int index) throws SQLException {
        ps.setBoolean(index, getAsBoolean());
    }

    boolean test(boolean t);
    
    @Override
    public default Boolean get() { return getAsBoolean(); }

    static boolean areEqual(BooleanValueFilter a, BooleanValueFilter b) {
        return (null == a) ? null == b : (null != b && a.getAsBoolean() == b.getAsBoolean() && a.getOperator() == b.getOperator());
    }

    public static BooleanValueFilter ofTrue() {
        return new BooleanValueFilter() {
            @Override
            public boolean test(boolean t) { return t; }
            @Override
            public ComparisonOperator getOperator() { return ComparisonOperator.EQUALS; }
            @Override
            public boolean getAsBoolean() { return true; }
        };
    }
    
    public static BooleanValueFilter ofFalse() {
        return new BooleanValueFilter() {
            @Override
            public boolean test(boolean t) { return !t; }
            @Override
            public ComparisonOperator getOperator() { return ComparisonOperator.EQUALS; }
            @Override
            public boolean getAsBoolean() { return false; }
        };
    }
    
    public static BooleanValueFilter of(boolean value) {
        return (value) ? ofTrue() : ofFalse();
    }

    public static BooleanValueFilter ofNot(boolean value) {
        return new BooleanValueFilter() {
            @Override
            public boolean test(boolean t) { return t != value; }
            @Override
            public ComparisonOperator getOperator() { return ComparisonOperator.NOT_EQUALS; }
            @Override
            public boolean getAsBoolean() { return value; }
        };
    }
}
