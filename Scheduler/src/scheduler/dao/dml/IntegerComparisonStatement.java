package scheduler.dao.dml;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;
import java.util.function.ToIntFunction;
import scheduler.dao.DataObjectImpl;
import scheduler.dao.schema.ValueType;
import scheduler.util.BiIntPredicate;
import scheduler.view.ItemModel;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
 * @param <T>
 */
public interface IntegerComparisonStatement<T extends DataObjectImpl> extends ColumnComparisonStatement<T> {
    
    int getValue();
    
    public static <T extends DataObjectImpl> IntegerComparisonStatement<T> of(TableReference table, ColumnReference column, ComparisonOperator op,
            int value, ToIntFunction<ItemModel<T>> getColValue, BiIntPredicate predicate) {
        ValueType valueType = column.getColumn().getType().getValueType();
        assert valueType == ValueType.INT : "Column type mismatch";
        assert null == table || table.getTable() == column.getColumn().getTable() : "Table/Column mismatch";
        Objects.requireNonNull(op);
        Objects.requireNonNull(getColValue);
        Objects.requireNonNull(predicate);
        return new IntegerComparisonStatement<T>() {
            @Override
            public ColumnReference getColumn() {
                return column;
            }

            @Override
            public TableReference getTable() {
                return table;
            }
            
            @Override
            public int getValue() {
                return value;
            }

            @Override
            public ComparisonOperator getOperator() {
                return op;
            }

            @Override
            public int applyValues(PreparedStatement ps, int currentIndex) throws SQLException {
                ps.setInt(currentIndex, value);
                return currentIndex + 1;
            }

            @Override
            public boolean test(ItemModel<T> t) {
                return predicate.test(getColValue.applyAsInt(t), value);
            }

        };
    }
    
    public static <T extends DataObjectImpl> IntegerComparisonStatement<T> of(ColumnReference column, ComparisonOperator op,
            int value, ToIntFunction<ItemModel<T>> getColValue, BiIntPredicate predicate) {
        return of(null, column, op, value, getColValue, predicate);
    }
    
    public static <T extends DataObjectImpl> IntegerComparisonStatement<T> columnEquals(TableReference table, ColumnReference column, int value,
            ToIntFunction<ItemModel<T>> getColValue) {
        return of(table, column, ComparisonOperator.EQUAL_TO, value, getColValue, (x, y) -> x == y);
    }
    
    public static <T extends DataObjectImpl> IntegerComparisonStatement<T> columnEquals(ColumnReference column, int value,
            ToIntFunction<ItemModel<T>> getColValue) {
        return columnEquals(null, column, value, getColValue);
    }
    
    public static <T extends DataObjectImpl> IntegerComparisonStatement<T> columnNotEquals(TableReference table, ColumnReference column, int value,
            ToIntFunction<ItemModel<T>> getColValue) {
        return of(table, column, ComparisonOperator.NOT_EQUAL_TO, value, getColValue, (x, y) -> x != y);
    }
    
    public static <T extends DataObjectImpl> IntegerComparisonStatement<T> columnNotEquals(ColumnReference column, int value,
            ToIntFunction<ItemModel<T>> getColValue) {
        return columnNotEquals(null, column, value, getColValue);
    }
    
    public static <T extends DataObjectImpl> IntegerComparisonStatement<T> columnGreaterThan(TableReference table, ColumnReference column, int value,
            ToIntFunction<ItemModel<T>> getColValue) {
        return of(table, column, ComparisonOperator.GREATER_THAN, value, getColValue, (x, y) -> x > y);
    }
    
    public static <T extends DataObjectImpl> IntegerComparisonStatement<T> columnGreaterThan(ColumnReference column, int value,
            ToIntFunction<ItemModel<T>> getColValue) {
        return columnGreaterThan(null, column, value, getColValue);
    }
    
    public static <T extends DataObjectImpl> IntegerComparisonStatement<T> columnGreaterThanOrEqualTo(TableReference table, ColumnReference column,
            int value, ToIntFunction<ItemModel<T>> getColValue) {
        return of(table, column, ComparisonOperator.NOT_LESS_THAN, value, getColValue, (x, y) -> x >= y);
    }
    
    public static <T extends DataObjectImpl> IntegerComparisonStatement<T> columnGreaterThanOrEqualTo(ColumnReference column,
            int value, ToIntFunction<ItemModel<T>> getColValue) {
        return columnGreaterThanOrEqualTo(null, column, value, getColValue);
    }
    
    public static <T extends DataObjectImpl> IntegerComparisonStatement<T> columnLessThan(TableReference table, ColumnReference column, int value,
            ToIntFunction<ItemModel<T>> getColValue) {
        return of(table, column, ComparisonOperator.LESS_THAN, value, getColValue, (x, y) -> x < y);
    }
    
    public static <T extends DataObjectImpl> IntegerComparisonStatement<T> columnLessThan(ColumnReference column, int value,
            ToIntFunction<ItemModel<T>> getColValue) {
        return columnLessThan(null, column, value, getColValue);
    }
    
    public static <T extends DataObjectImpl> IntegerComparisonStatement<T> columnLessThanOrEqualTo(TableReference table, ColumnReference column,
            int value, ToIntFunction<ItemModel<T>> getColValue) {
        return of(table, column, ComparisonOperator.NOT_GREATER_THAN, value, getColValue, (x, y) -> x <= y);
    }
    
    public static <T extends DataObjectImpl> IntegerComparisonStatement<T> columnLessThanOrEqualTo(ColumnReference column, int value,
            ToIntFunction<ItemModel<T>> getColValue) {
        return columnLessThanOrEqualTo(null, column, value, getColValue);
    }
    
}
