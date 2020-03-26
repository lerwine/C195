package scheduler.dao.dml.deprecated;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import scheduler.dao.DataObjectImpl;
import scheduler.view.ItemModel;

/**
 * {@link WhereStatement} for value comparisons.
 * <dl>
 *    <dt>{@link ColumnComparisonStatement}</dt>
 *    <dd>Compares column-related value to a given filter value.
 *        <dl>
 *            <dt>{@link StringComparisonStatement}</dt>
 *            <dd>Compares string values.</dd>
 *            <dt>{@link BooleanComparisonStatement}</dt>
 *            <dd>Compares boolean values.</dd>
 *            <dt>{@link DateTimeComparisonStatement}</dt>
 *            <dd>Compares {@link java.time.LocalDateTime}, {@link java.time.LocalDate} and {@link java.sql.Timestamp} values.</dd>
 *            <dt>{@link IntegerComparisonStatement}</dt>
 *            <dd>Compares integer values</dd>
 *        </dl>
 *    </dd>
 * </dl>
 * @author Leonard T. Erwine (Student ID 356334)
 * @param <T> The data access object type.
 * @param <U> The item model type.
 */
public interface ComparisonStatement<T extends DataObjectImpl, U extends ItemModel<T>> extends WhereStatement<T, U> {
    /**
     * Gets the value comparison operator.
     * 
     * @return The {@link ComparisonOperator} that represents the comparison operator.
     */
    ComparisonOperator getOperator();   
    
}
