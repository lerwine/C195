package scheduler.dao.dml;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;
import scheduler.dao.DataObjectImpl;
import scheduler.dao.schema.ValueType;
import scheduler.view.ItemModel;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
 * @param <T> The data access object type.
 * @param <U> The item model type.
 */
public interface StringComparisonStatement<T extends DataObjectImpl, U extends ItemModel<T>> extends ColumnComparisonStatement<T, U> {
    
    String getValue();
    
    public static <T extends DataObjectImpl, U extends ItemModel<T>> StringComparisonStatement<T, U> of(TableReference table, ColumnReference column, ComparisonOperator op,
            String value, Function<U, String> getColValue, BiPredicate<String, String> predicate) {
        ValueType valueType = column.getColumn().getType().getValueType();
        assert valueType == ValueType.STRING : "Column type mismatch";
        assert null == table || table.getTable() == column.getColumn().getTable() : "Table/Column mismatch";
        Objects.requireNonNull(op);
        Objects.requireNonNull(getColValue);
        Objects.requireNonNull(predicate);
        Objects.requireNonNull(value);
        return new StringComparisonStatement<T, U>() {
            @Override
            public ColumnReference getColumn() {
                return column;
            }

            @Override
            public TableReference getTable() {
                return table;
            }
            
            @Override
            public String getValue() {
                return value;
            }

            @Override
            public ComparisonOperator getOperator() {
                return op;
            }

            @Override
            public int applyValues(PreparedStatement ps, int currentIndex) throws SQLException {
                ps.setString(currentIndex, ComparisonOperator.toStringParam(value, op));
                return currentIndex + 1;
            }

            @Override
            public boolean test(U t) {
                return predicate.test(getColValue.apply(t), value);
            }

        };
    }
    
    public static <T extends DataObjectImpl, U extends ItemModel<T>> StringComparisonStatement<T, U> of(ColumnReference column, ComparisonOperator op,
            String value, Function<U, String> getColValue, BiPredicate<String, String> predicate) {
        return of(null, column, op, value, getColValue, predicate);
    }
    
    public static <T extends DataObjectImpl, U extends ItemModel<T>> StringComparisonStatement<T, U> columnEquals(TableReference table, ColumnReference column, String value,
            Function<U, String> getColValue) {
        return of(table, column, ComparisonOperator.EQUAL_TO, value, getColValue, (x, y) -> x.equals(y));
    }
    
    public static <T extends DataObjectImpl, U extends ItemModel<T>> StringComparisonStatement<T, U> columnEquals(ColumnReference column, String value, Function<U, String> getColValue) {
        return columnEquals(null, column, value, getColValue);
    }
    
    public static <T extends DataObjectImpl, U extends ItemModel<T>> StringComparisonStatement<T, U> columnNotEquals(TableReference table, ColumnReference column, String value,
            Function<U, String> getColValue) {
        return of(table, column, ComparisonOperator.NOT_EQUAL_TO, value, getColValue, (x, y) -> !x.equals(y));
    }
    
    public static <T extends DataObjectImpl, U extends ItemModel<T>> StringComparisonStatement<T, U> columnNotEquals(ColumnReference column, String value, Function<U, String> getColValue) {
        return columnNotEquals(null, column, value, getColValue);
    }
    
    public static <T extends DataObjectImpl, U extends ItemModel<T>> StringComparisonStatement<T, U> columnGreaterThan(TableReference table, ColumnReference column, String value,
            Function<U, String> getColValue) {
        return of(table, column, ComparisonOperator.GREATER_THAN, value, getColValue, (x, y) -> x.compareTo(y) > 0);
    }
    
    public static <T extends DataObjectImpl, U extends ItemModel<T>> StringComparisonStatement<T, U> columnGreaterThan(ColumnReference column, String value, Function<U, String> getColValue) {
        return columnGreaterThan(null, column, value, getColValue);
    }
    
    public static <T extends DataObjectImpl, U extends ItemModel<T>> StringComparisonStatement<T, U> columnGreaterThanOrEqualTo(TableReference table, ColumnReference column, String value,
            Function<U, String> getColValue) {
        return of(table, column, ComparisonOperator.NOT_LESS_THAN, value, getColValue, (x, y) -> x.compareTo(y) >= 0);
    }
    
    public static <T extends DataObjectImpl, U extends ItemModel<T>> StringComparisonStatement<T, U> columnGreaterThanOrEqualTo(ColumnReference column, String value,
            Function<U, String> getColValue) {
        return columnGreaterThanOrEqualTo(null, column, value, getColValue);
    }
    
    public static <T extends DataObjectImpl, U extends ItemModel<T>> StringComparisonStatement<T, U> columnLessThan(TableReference table, ColumnReference column, String value,
            Function<U, String> getColValue) {
        return of(table, column, ComparisonOperator.LESS_THAN, value, getColValue, (x, y) -> x.compareTo(y) < 0);
    }
    
    public static <T extends DataObjectImpl, U extends ItemModel<T>> StringComparisonStatement<T, U> columnLessThan(ColumnReference column, String value, Function<U, String> getColValue) {
        return columnLessThan(null, column, value, getColValue);
    }
    
    public static <T extends DataObjectImpl, U extends ItemModel<T>> StringComparisonStatement<T, U> columnLessThanOrEqualTo(TableReference table, ColumnReference column, String value,
            Function<U, String> getColValue) {
        return of(table, column, ComparisonOperator.NOT_GREATER_THAN, value, getColValue, (x, y) -> x.compareTo(y) <= 0);
    }
    
    public static <T extends DataObjectImpl, U extends ItemModel<T>> StringComparisonStatement<T, U> columnLessThanOrEqualTo(ColumnReference column, String value,
            Function<U, String> getColValue) {
        return columnLessThanOrEqualTo(null, column, value, getColValue);
    }
    
    public static <T extends DataObjectImpl, U extends ItemModel<T>> StringComparisonStatement<T, U> columnStartsWith(TableReference table, ColumnReference column, String value,
            Function<U, String> getColValue) {
        return of(table, column, ComparisonOperator.STARTS_WITH, value, getColValue, (x, y) -> x.startsWith(y));
    }
    
    public static <T extends DataObjectImpl, U extends ItemModel<T>> StringComparisonStatement<T, U> columnStartsWith(ColumnReference column, String value,
            Function<U, String> getColValue) {
        return columnStartsWith(null, column, value, getColValue);
    }
    
    public static <T extends DataObjectImpl, U extends ItemModel<T>> StringComparisonStatement<T, U> columnEndsWith(TableReference table, ColumnReference column, String value,
            Function<U, String> getColValue) {
        return of(table, column, ComparisonOperator.ENDS_WITH, value, getColValue, (x, y) -> x.startsWith(y));
    }
    
    public static <T extends DataObjectImpl, U extends ItemModel<T>> StringComparisonStatement<T, U> columnEndsWith(ColumnReference column, String value,
            Function<U, String> getColValue) {
        return columnEndsWith(null, column, value, getColValue);
    }
    
    public static <T extends DataObjectImpl, U extends ItemModel<T>> StringComparisonStatement<T, U> columnContains(TableReference table, ColumnReference column, String value,
            Function<U, String> getColValue) {
        return of(table, column, ComparisonOperator.CONTAINS, value, getColValue, (x, y) -> x.startsWith(y));
    }
    
    public static <T extends DataObjectImpl, U extends ItemModel<T>> StringComparisonStatement<T, U> columnContains(ColumnReference column, String value,
            Function<U, String> getColValue) {
        return columnContains(null, column, value, getColValue);
    }
}
