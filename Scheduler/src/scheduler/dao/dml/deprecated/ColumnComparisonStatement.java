package scheduler.dao.dml.deprecated;

import scheduler.dao.DataObjectImpl;
import scheduler.view.ItemModel;

/**
 * Compares a column-related value from a {@link DataObjectImpl} or {@link ItemModel} to a given filter value.
 * <dl>
 *      <dt>{@link StringComparisonStatement}</dt>
 *      <dd>Compares string values.</dd>
 *      <dt>{@link BooleanComparisonStatement}</dt>
 *      <dd>Compares boolean values.</dd>
 *      <dt>{@link DateTimeComparisonStatement}</dt>
 *      <dd>Compares {@link java.time.LocalDateTime}, {@link java.time.LocalDate} and {@link java.sql.Timestamp} values.</dd>
 *      <dt>{@link IntegerComparisonStatement}</dt>
 *      <dd>Compares integer values</dd>
 * </dl>
 *
 * @author Leonard T. Erwine (Student ID 356334)
 * @param <T> The data access object type.
 * @param <U> The item model type.
 */
public interface ColumnComparisonStatement<T extends DataObjectImpl, U extends ItemModel<T>> extends ComparisonStatement<T, U> {

    /**
     * Describes the {@link scheduler.dao.schema.DbColumn} being compared.
     * 
     * @return 
     */
    QueryColumnSelector getColumnSelector();
    
    @Override
    public default void appendSqlStatement(TableReference table, StringBuilder stringBuilder) {
        QueryColumnSelector cs = getColumnSelector();
        cs.appendColumnName(table, stringBuilder);
        stringBuilder.append(getOperator().toString()).append(" ").append("?");
    }
}
