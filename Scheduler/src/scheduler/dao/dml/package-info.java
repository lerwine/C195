/**
 * DML-generation classes.
 * 
 * <dl>
 *  <dt>{@linkplain scheduler.dao.dml.TableReference}</dt>
 *  <dd>
 *      <dl>
 *          <dt>&rArr; {@linkplain scheduler.dao.dml.JoinableTable}, {@linkplain scheduler.dao.dml.TableColumnList}</dt>
 *          <dd>&rArr; {@linkplain scheduler.dao.dml.JoinedTable}, {@linkplain scheduler.dao.dml.JoinedTableColumnList}</dd>
 *      </dl>
 *  </dd>
 *  <dt>{@link scheduler.dao.dml.WhereStatement}</dt>
 *  <dd>
 *      <dl>
 *          <dt>&rArr; {@link scheduler.dao.dml.ComparisonStatement}</dt>
 *          <dd>&rArr; {@link scheduler.dao.dml.ComparisonOperator} {@link scheduler.dao.dml.ComparisonStatement#getOperator()}
 *              <dl>
 *                  <dt>&rArr; {@link scheduler.dao.dml.ColumnComparisonStatement}</dt>
 *                  <dd>&rArr; {@link scheduler.dao.dml.ComparisonOperator}
 *                      <ul style="list-style-type: none; margin-top:0px;margin-bottom:0px">
 *                          <li>&rArr; {@link scheduler.dao.dml.StringComparisonStatement}, {@link scheduler.dao.dml.BooleanComparisonStatement},
 *                              {@link scheduler.dao.dml.DateTimeComparisonStatement}, {@link scheduler.dao.dml.IntegerComparisonStatement}</li>
 *                      </ul>
 *                  </dd>
 *              </dl>
 *          </dd>
 *          <dt>&rArr; {@link scheduler.dao.dml.LogicalStatement}</dt>
 *          <dd>
 *              <dl>
 *                  <dt>OR</dt>
 *                  <dd>{@link scheduler.dao.dml.LogicalStatement#or(scheduler.dao.dml.WhereStatement, scheduler.dao.dml.WhereStatement, scheduler.dao.dml.WhereStatement...)}</dd>
 *                  <dt>AND</dt>
 *                  <dd>{@link scheduler.dao.dml.LogicalStatement#and(scheduler.dao.dml.WhereStatement, scheduler.dao.dml.WhereStatement, scheduler.dao.dml.WhereStatement...)}</dd>
 *              </dl>
 *          </dd>
 *      </dl>
 *  </dd>
 * </dl>
 */
package scheduler.dao.dml;
