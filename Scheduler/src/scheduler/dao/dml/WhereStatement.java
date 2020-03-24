package scheduler.dao.dml;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.function.Predicate;
import scheduler.dao.DataObjectImpl;
import scheduler.view.ItemModel;

/**
 * Represents a statement within an SQL WHERE clause which can also filter corresponding data access objects.
 *      <dl>
 *          <dt>{@link ComparisonStatement}</dt>
 *          <dd>{@link ComparisonOperator} {@link ComparisonStatement#getOperator()}
 *              <dl>
 *                  <dt>&rArr; {@link ColumnComparisonStatement}</dt>
 *                  <dd>&rArr; {@link ComparisonOperator}
 *                      <ul style="list-style-type: none; margin-top:0px;margin-bottom:0px">
 *                          <li>&rArr; {@link StringComparisonStatement}, {@link BooleanComparisonStatement},
 *                              {@link DateTimeComparisonStatement}, {@link IntegerComparisonStatement}</li>
 *                      </ul>
 *                  </dd>
 *              </dl>
 *          </dd>
 *          <dt>{@link LogicalStatement}</dt>
 *          <dd>
 *              <dl>
 *                  <dt>OR</dt>
 *                  <dd>{@link LogicalStatement#or(WhereStatement, WhereStatement, WhereStatement...)}</dd>
 *                  <dt>AND</dt>
 *                  <dd>{@link LogicalStatement#and(WhereStatement, WhereStatement, WhereStatement...)}</dd>
 *              </dl>
 *          </dd>
 *      </dl>
 * 
 * @author Leonard T. Erwine (Student ID 356334)
 * @param <T> The data access object type.
 * 
 */
// BUG: Need to have a U generic parameter for ItemModel<T>.
public interface WhereStatement<T extends DataObjectImpl> extends Predicate<ItemModel<T>> {
    /**
     * Appends the current SQL statement to a {@link StringBuilder}.
     * This does not add any leading or trailing whitespace.
     * 
     * @param stringBuilder The {@link StringBuilder} to append to.
     */
    void appendSqlStatement(StringBuilder stringBuilder);
    
    /**
     * Applies any values to a {@link PreparedStatement} and returns the next index.
     * 
     * @param ps The {@link PreparedStatement} to apply values to.
     * @param currentIndex The parameter index for the next value to be applied.
     * @return The next parameter index, which was incremented for each value applied.
     * @throws java.sql.SQLException if unable to set statement values.
     */
    int applyValues(PreparedStatement ps, int currentIndex) throws SQLException;
}
