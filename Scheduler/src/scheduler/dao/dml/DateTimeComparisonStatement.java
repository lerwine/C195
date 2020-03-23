package scheduler.dao.dml;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;
import scheduler.dao.DataObject;
import scheduler.dao.schema.ValueType;
import scheduler.util.DB;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
 * @param <T>
 */
public interface DateTimeComparisonStatement<T extends DataObject> extends ColumnComparisonStatement<T> {
    
    LocalDateTime getValue();
    
    public static <T extends DataObject> DateTimeComparisonStatement<T> of(TableReference table, ColumnReference column, ComparisonOperator op,
            LocalDateTime value, Function<T, Timestamp> getColValue, BiPredicate<LocalDateTime, LocalDateTime> predicate) {
        ValueType valueType = column.getColumn().getType().getValueType();
        assert valueType == ValueType.TIMESTAMP : "Column type mismatch";
        assert null == table || table.getTableName() == column.getColumn().getTable() : "Table/Column mismatch";
        Objects.requireNonNull(op);
        Objects.requireNonNull(getColValue);
        Objects.requireNonNull(predicate);
        Objects.requireNonNull(value);
        return new DateTimeComparisonStatement<T>() {
            @Override
            public ColumnReference getColumn() {
                return column;
            }

            @Override
            public TableReference getTable() {
                return table;
            }
            
            @Override
            public LocalDateTime getValue() {
                return value;
            }

            @Override
            public ComparisonOperator getOperator() {
                return op;
            }

            @Override
            public int applyValues(PreparedStatement ps, int currentIndex) throws SQLException {
                ps.setTimestamp(currentIndex, DB.toUtcTimestamp(value));
                return currentIndex + 1;
            }

            @Override
            public boolean test(T t) {
                return predicate.test(DB.fromUtcTimestamp(getColValue.apply(t)), value);
            }

        };
    }
    
    public static <T extends DataObject> DateTimeComparisonStatement<T> of(ColumnReference column, ComparisonOperator op,
            LocalDateTime value, Function<T, Timestamp> getColValue, BiPredicate<LocalDateTime, LocalDateTime> predicate) {
        return of(null, column, op, value, getColValue, predicate);
    }
    
    public static <T extends DataObject> DateTimeComparisonStatement<T> columnGreaterThan(TableReference table, ColumnReference column, LocalDateTime value,
            Function<T, Timestamp> getColValue) {
        return of(table, column, ComparisonOperator.GREATER_THAN, value, getColValue, (x, y) -> x.compareTo(y) > 0);
    }
    
    public static <T extends DataObject> DateTimeComparisonStatement<T> columnGreaterThan(ColumnReference column, LocalDateTime value, Function<T, Timestamp> getColValue) {
        return columnGreaterThan(null, column, value, getColValue);
    }
    
    public static <T extends DataObject> DateTimeComparisonStatement<T> columnGreaterThanOrEqualTo(TableReference table, ColumnReference column, LocalDateTime value,
            Function<T, Timestamp> getColValue) {
        return of(table, column, ComparisonOperator.NOT_LESS_THAN, value, getColValue, (x, y) -> x.compareTo(y) >= 0);
    }
    
    public static <T extends DataObject> DateTimeComparisonStatement<T> columnGreaterThanOrEqualTo(ColumnReference column, LocalDateTime value, Function<T, Timestamp> getColValue) {
        return columnGreaterThanOrEqualTo(null, column, value, getColValue);
    }
    
    public static <T extends DataObject> DateTimeComparisonStatement<T> columnLessThan(TableReference table, ColumnReference column, LocalDateTime value,
            Function<T, Timestamp> getColValue) {
        return of(table, column, ComparisonOperator.LESS_THAN, value, getColValue, (x, y) -> x.compareTo(y) < 0);
    }
    
    public static <T extends DataObject> DateTimeComparisonStatement<T> columnLessThan(ColumnReference column, LocalDateTime value, Function<T, Timestamp> getColValue) {
        return columnLessThan(null, column, value, getColValue);
    }
    
    public static <T extends DataObject> DateTimeComparisonStatement<T> columnLessThanOrEqualTo(TableReference table, ColumnReference column, LocalDateTime value,
            Function<T, Timestamp> getColValue) {
        return of(table, column, ComparisonOperator.NOT_GREATER_THAN, value, getColValue, (x, y) -> x.compareTo(y) <= 0);
    }
    
    public static <T extends DataObject> DateTimeComparisonStatement<T> columnLessThanOrEqualTo(ColumnReference column, LocalDateTime value, Function<T, Timestamp> getColValue) {
        return columnLessThanOrEqualTo(null, column, value, getColValue);
    }
    
}
