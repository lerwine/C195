package scheduler.model;

import java.sql.PreparedStatement;
import java.util.function.BiFunction;
import java.util.function.Predicate;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
 * @param <T>
 */
public interface DataRowFilter<T extends IDataRow> extends BiFunction<Integer, PreparedStatement, Integer>, Predicate<T> {
    
    void appendSubClauseSql(StringBuilder sql);
    
    /**
     * Applies filter values to the prepared statement for WHERE clause.
     * 
     * @param t The current sequential parameter index.
     * @param u The prepared statement to apply filter values to.
     * @return The next sequential parameter index.
     */
    @Override
    public Integer apply(Integer t, PreparedStatement u);
    
}
