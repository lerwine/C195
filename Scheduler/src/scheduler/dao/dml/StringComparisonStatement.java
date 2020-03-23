/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.dao.dml;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;
import scheduler.dao.DataObject;
import scheduler.dao.schema.ValueType;

/**
 *
 * @author lerwi
 * @param <T>
 */
public interface StringComparisonStatement<T extends DataObject> extends ColumnComparisonStatement<T> {
    
    String getValue();
    
    public static <T extends DataObject> StringComparisonStatement<T> of(TableReference table, ColumnReference column, ComparisonOperator op,
            String value, Function<T, String> getColValue, BiPredicate<String, String> predicate) {
        ValueType valueType = column.getColumn().getType().getValueType();
        assert valueType == ValueType.STRING : "Column type mismatch";
        assert null == table || table.getTableName() == column.getColumn().getTable() : "Table/Column mismatch";
        Objects.requireNonNull(op);
        Objects.requireNonNull(getColValue);
        Objects.requireNonNull(predicate);
        Objects.requireNonNull(value);
        return new StringComparisonStatement<T>() {
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
            public boolean test(T t) {
                return predicate.test(getColValue.apply(t), value);
            }

        };
    }
    
    public static <T extends DataObject> StringComparisonStatement<T> of(ColumnReference column, ComparisonOperator op,
            String value, Function<T, String> getColValue, BiPredicate<String, String> predicate) {
        return of(null, column, op, value, getColValue, predicate);
    }
    
    public static <T extends DataObject> StringComparisonStatement<T> columnEquals(TableReference table, ColumnReference column, String value,
            Function<T, String> getColValue) {
        return of(table, column, ComparisonOperator.EQUAL_TO, value, getColValue, (x, y) -> x.equals(y));
    }
    
    public static <T extends DataObject> StringComparisonStatement<T> columnEquals(ColumnReference column, String value, Function<T, String> getColValue) {
        return columnEquals(null, column, value, getColValue);
    }
    
    public static <T extends DataObject> StringComparisonStatement<T> columnNotEquals(TableReference table, ColumnReference column, String value,
            Function<T, String> getColValue) {
        return of(table, column, ComparisonOperator.NOT_EQUAL_TO, value, getColValue, (x, y) -> !x.equals(y));
    }
    
    public static <T extends DataObject> StringComparisonStatement<T> columnNotEquals(ColumnReference column, String value, Function<T, String> getColValue) {
        return columnNotEquals(null, column, value, getColValue);
    }
    
    public static <T extends DataObject> StringComparisonStatement<T> columnGreaterThan(TableReference table, ColumnReference column, String value,
            Function<T, String> getColValue) {
        return of(table, column, ComparisonOperator.GREATER_THAN, value, getColValue, (x, y) -> x.compareTo(y) > 0);
    }
    
    public static <T extends DataObject> StringComparisonStatement<T> columnGreaterThan(ColumnReference column, String value, Function<T, String> getColValue) {
        return columnGreaterThan(null, column, value, getColValue);
    }
    
    public static <T extends DataObject> StringComparisonStatement<T> columnGreaterThanOrEqualTo(TableReference table, ColumnReference column, String value,
            Function<T, String> getColValue) {
        return of(table, column, ComparisonOperator.NOT_LESS_THAN, value, getColValue, (x, y) -> x.compareTo(y) >= 0);
    }
    
    public static <T extends DataObject> StringComparisonStatement<T> columnGreaterThanOrEqualTo(ColumnReference column, String value, Function<T, String> getColValue) {
        return columnGreaterThanOrEqualTo(null, column, value, getColValue);
    }
    
    public static <T extends DataObject> StringComparisonStatement<T> columnLessThan(TableReference table, ColumnReference column, String value,
            Function<T, String> getColValue) {
        return of(table, column, ComparisonOperator.LESS_THAN, value, getColValue, (x, y) -> x.compareTo(y) < 0);
    }
    
    public static <T extends DataObject> StringComparisonStatement<T> columnLessThan(ColumnReference column, String value, Function<T, String> getColValue) {
        return columnLessThan(null, column, value, getColValue);
    }
    
    public static <T extends DataObject> StringComparisonStatement<T> columnLessThanOrEqualTo(TableReference table, ColumnReference column, String value,
            Function<T, String> getColValue) {
        return of(table, column, ComparisonOperator.NOT_GREATER_THAN, value, getColValue, (x, y) -> x.compareTo(y) <= 0);
    }
    
    public static <T extends DataObject> StringComparisonStatement<T> columnLessThanOrEqualTo(ColumnReference column, String value, Function<T, String> getColValue) {
        return columnLessThanOrEqualTo(null, column, value, getColValue);
    }
    
    public static <T extends DataObject> StringComparisonStatement<T> columnStartsWith(TableReference table, ColumnReference column, String value,
            Function<T, String> getColValue) {
        return of(table, column, ComparisonOperator.STARTS_WITH, value, getColValue, (x, y) -> x.startsWith(y));
    }
    
    public static <T extends DataObject> StringComparisonStatement<T> columnStartsWith(ColumnReference column, String value, Function<T, String> getColValue) {
        return columnStartsWith(null, column, value, getColValue);
    }
    
    public static <T extends DataObject> StringComparisonStatement<T> columnEndsWith(TableReference table, ColumnReference column, String value,
            Function<T, String> getColValue) {
        return of(table, column, ComparisonOperator.ENDS_WITH, value, getColValue, (x, y) -> x.startsWith(y));
    }
    
    public static <T extends DataObject> StringComparisonStatement<T> columnEndsWith(ColumnReference column, String value, Function<T, String> getColValue) {
        return columnEndsWith(null, column, value, getColValue);
    }
    
    public static <T extends DataObject> StringComparisonStatement<T> columnContains(TableReference table, ColumnReference column, String value,
            Function<T, String> getColValue) {
        return of(table, column, ComparisonOperator.CONTAINS, value, getColValue, (x, y) -> x.startsWith(y));
    }
    
    public static <T extends DataObject> StringComparisonStatement<T> columnContains(ColumnReference column, String value, Function<T, String> getColValue) {
        return columnContains(null, column, value, getColValue);
    }
}
