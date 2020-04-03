package scheduler.dao.filter.value;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.function.Supplier;
import scheduler.dao.filter.ComparisonOperator;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
 */
public interface ValueFilter<T> extends Supplier<T> {
    
    ComparisonOperator getOperator();
    void accept(PreparedStatement ps, int index) throws SQLException;
    
}
