package scheduler.dao.dml;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;
import scheduler.dao.DataObjectImpl;
import scheduler.dao.schema.ValueType;
import scheduler.util.DB;
import scheduler.view.ItemModel;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
 * @param <T> The data access object type.
 * @param <U> The item model type.
 */
public interface DateTimeComparisonStatement<T extends DataObjectImpl, U extends ItemModel<T>> extends ColumnComparisonStatement<T, U> {
    
    LocalDateTime getValue();
    
    public static <T extends DataObjectImpl, U extends ItemModel<T>> DateTimeComparisonStatement<T, U> of(TableReference table, ColumnReference column, ComparisonOperator op,
            LocalDateTime value, Function<U, Timestamp> getColValue, BiPredicate<LocalDateTime, LocalDateTime> predicate) {
        ValueType valueType = column.getColumn().getType().getValueType();
        assert valueType == ValueType.TIMESTAMP : "Column type mismatch";
        assert null == table || table.getTable() == column.getColumn().getTable() : "Table/Column mismatch";
        Objects.requireNonNull(op);
        Objects.requireNonNull(getColValue);
        Objects.requireNonNull(predicate);
        Objects.requireNonNull(value);
        return new DateTimeComparisonStatement<T, U>() {
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
            public boolean test(U t) {
                return predicate.test(DB.fromUtcTimestamp(getColValue.apply(t)), value);
            }

        };
    }
    
    public static <T extends DataObjectImpl, U extends ItemModel<T>> DateTimeComparisonStatement<T, U> of(ColumnReference column, ComparisonOperator op,
            LocalDateTime value, Function<U, Timestamp> getColValue, BiPredicate<LocalDateTime, LocalDateTime> predicate) {
        return of(null, column, op, value, getColValue, predicate);
    }
    
    public static <T extends DataObjectImpl, U extends ItemModel<T>> DateTimeComparisonStatement<T, U> columnGreaterThan(TableReference table, ColumnReference column, LocalDateTime value,
            Function<U, Timestamp> getColValue) {
        return of(table, column, ComparisonOperator.GREATER_THAN, value, getColValue, (x, y) -> x.compareTo(y) > 0);
    }
    
    public static <T extends DataObjectImpl, U extends ItemModel<T>> DateTimeComparisonStatement<T, U> columnGreaterThan(ColumnReference column, LocalDateTime value,
            Function<U, Timestamp> getColValue) {
        return columnGreaterThan(null, column, value, getColValue);
    }
    
    public static <T extends DataObjectImpl, U extends ItemModel<T>> DateTimeComparisonStatement<T, U> columnGreaterThanOrEqualTo(TableReference table, ColumnReference column, LocalDateTime value,
            Function<U, Timestamp> getColValue) {
        return of(table, column, ComparisonOperator.NOT_LESS_THAN, value, getColValue, (x, y) -> x.compareTo(y) >= 0);
    }
    
    public static <T extends DataObjectImpl, U extends ItemModel<T>> DateTimeComparisonStatement<T, U> columnGreaterThanOrEqualTo(ColumnReference column, LocalDateTime value,
            Function<U, Timestamp> getColValue) {
        return columnGreaterThanOrEqualTo(null, column, value, getColValue);
    }
    
    public static <T extends DataObjectImpl, U extends ItemModel<T>> DateTimeComparisonStatement<T, U> columnLessThan(TableReference table, ColumnReference column, LocalDateTime value,
            Function<U, Timestamp> getColValue) {
        return of(table, column, ComparisonOperator.LESS_THAN, value, getColValue, (x, y) -> x.compareTo(y) < 0);
    }
    
    public static <T extends DataObjectImpl, U extends ItemModel<T>> DateTimeComparisonStatement<T, U> columnLessThan(ColumnReference column, LocalDateTime value,
            Function<U, Timestamp> getColValue) {
        return columnLessThan(null, column, value, getColValue);
    }
    
    public static <T extends DataObjectImpl, U extends ItemModel<T>> DateTimeComparisonStatement<T, U> columnLessThanOrEqualTo(TableReference table, ColumnReference column, LocalDateTime value,
            Function<U, Timestamp> getColValue) {
        return of(table, column, ComparisonOperator.NOT_GREATER_THAN, value, getColValue, (x, y) -> x.compareTo(y) <= 0);
    }
    
    public static <T extends DataObjectImpl, U extends ItemModel<T>> DateTimeComparisonStatement<T, U> columnLessThanOrEqualTo(ColumnReference column, LocalDateTime value,
            Function<U, Timestamp> getColValue) {
        return columnLessThanOrEqualTo(null, column, value, getColValue);
    }
    
}
