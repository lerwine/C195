package scheduler.dao.dml.deprecated;

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
 * Interface for filtering {@link ItemModel} and {@link DataObjectImpl} items by {@link LocalDateTime} values.
 * 
 * @author Leonard T. Erwine (Student ID 356334)
 * @param <T> The data access object type.
 * @param <U> The item model type.
 */
public interface DateTimeComparisonStatement<T extends DataObjectImpl, U extends ItemModel<T>> extends ColumnComparisonStatement<T, U> {
    
    LocalDateTime getValue();
    
    public static <T extends DataObjectImpl, U extends ItemModel<T>> DateTimeComparisonStatement<T, U> of(QueryColumnSelector columnSelector,
            ComparisonOperator op, LocalDateTime value, BiPredicate<U, LocalDateTime> predicate) {
        ValueType valueType = columnSelector.getColumn().getType().getValueType();
        assert valueType == ValueType.TIMESTAMP : "Column type mismatch";
        Objects.requireNonNull(op);
        Objects.requireNonNull(predicate);
        Objects.requireNonNull(value);
        return new DateTimeComparisonStatement<T, U>() {
            @Override
            public LocalDateTime getValue() {
                return value;
            }

            @Override
            public QueryColumnSelector getColumnSelector() {
                return columnSelector;
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
                return predicate.test(t, value);
            }

        };
    }
    
    /**
     * 
     * @param <T>
     * @param <U>
     * @param table
     * @param column
     * @param op
     * @param value
     * @param predicate
     * @return 
     * @deprecated Use {@link #of(scheduler.dao.dml.QueryColumnSelector, scheduler.dao.dml.ComparisonOperator, java.time.LocalDateTime, java.util.function.BiPredicate)}, instead.
     */
    @Deprecated
    public static <T extends DataObjectImpl, U extends ItemModel<T>> DateTimeComparisonStatement<T, U> of(TableReference table, ColumnReference column, ComparisonOperator op,
            LocalDateTime value, BiPredicate<U, LocalDateTime> predicate) {
        ValueType valueType = column.getColumn().getType().getValueType();
        assert valueType == ValueType.TIMESTAMP : "Column type mismatch";
        assert null == table || table.getTable() == column.getColumn().getTable() : "Table/Column mismatch";
        Objects.requireNonNull(op);
        Objects.requireNonNull(predicate);
        Objects.requireNonNull(value);
        return new DateTimeComparisonStatement<T, U>() {
            
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
                return predicate.test(t, value);
            }

            @Override
            public QueryColumnSelector getColumnSelector() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

        };
    }
    
    /**
     * 
     * @param <T>
     * @param <U>
     * @param column
     * @param op
     * @param value
     * @param predicate
     * @return 
     * @deprecated Use {@link #of(scheduler.dao.dml.QueryColumnSelector, scheduler.dao.dml.ComparisonOperator, java.time.LocalDateTime, java.util.function.BiPredicate)}, instead.
     */
    @Deprecated
    static <T extends DataObjectImpl, U extends ItemModel<T>> DateTimeComparisonStatement<T, U> of(ColumnReference column, ComparisonOperator op,
            LocalDateTime value, BiPredicate<U, LocalDateTime> predicate) {
        return of(null, column, op, value, predicate);
    }
    
    /**
     * 
     * @param <T>
     * @param <U>
     * @param table
     * @param column
     * @param op
     * @param value
     * @param getColValue
     * @param predicate
     * @return 
     * @deprecated Use {@link #of(scheduler.dao.dml.QueryColumnSelector, scheduler.dao.dml.ComparisonOperator, java.time.LocalDateTime, java.util.function.BiPredicate)}, instead.
     */
    @Deprecated
    static <T extends DataObjectImpl, U extends ItemModel<T>> DateTimeComparisonStatement<T, U> of(TableReference table, ColumnReference column,
            ComparisonOperator op, LocalDateTime value, Function<U, Timestamp> getColValue, BiPredicate<LocalDateTime, LocalDateTime> predicate) {
        ValueType valueType = column.getColumn().getType().getValueType();
        assert valueType == ValueType.TIMESTAMP : "Column type mismatch";
        assert null == table || table.getTable() == column.getColumn().getTable() : "Table/Column mismatch";
        Objects.requireNonNull(op);
        Objects.requireNonNull(getColValue);
        Objects.requireNonNull(predicate);
        Objects.requireNonNull(value);
        return new DateTimeComparisonStatement<T, U>() {
            
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

            @Override
            public QueryColumnSelector getColumnSelector() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

        };
    }
    
    /**
     * 
     * @param <T>
     * @param <U>
     * @param column
     * @param op
     * @param value
     * @param getColValue
     * @param predicate
     * @deprecated Use {@link #of(scheduler.dao.dml.QueryColumnSelector, scheduler.dao.dml.ComparisonOperator, java.time.LocalDateTime, java.util.function.BiPredicate)}, instead.
     */
    @Deprecated
    static <T extends DataObjectImpl, U extends ItemModel<T>> DateTimeComparisonStatement<T, U> of(ColumnReference column, ComparisonOperator op,
            LocalDateTime value, Function<U, Timestamp> getColValue, BiPredicate<LocalDateTime, LocalDateTime> predicate) {
        return of(null, column, op, value, getColValue, predicate);
    }
    
    /**
     * Creates a Filter and SQL query definition for {@code `table`.`field`>?}.
     * 
     * @param <T> The type of {@link DataObjectImpl} represented by the target {@link ItemModel}.
     * @param <U> The type of {@link ItemModel}.
     * @param columnSelector Specifies the database column to select from a {@link TableReference}.
     * @param value The value to match.
     * @param getColValue {@link Function} to get the {@link LocalDateTime} value from an {@link ItemModel}.
     * @return A Filter and SQL query definition for generating a SQL query statement for filtering items with a specific column value greater than
     * a {@link LocalDateTime} {@code value}.
     */
    public static <T extends DataObjectImpl, U extends ItemModel<T>> DateTimeComparisonStatement<T, U> columnGreaterThan(
            QueryColumnSelector columnSelector, LocalDateTime value, Function<U, LocalDateTime> getColValue) {
        return of(columnSelector, ComparisonOperator.GREATER_THAN, value, (t, y) -> getColValue.apply(t).compareTo(y) > 0);
    }
    
    /**
     * @deprecated Use {@link #columnGreaterThan(scheduler.dao.dml.QueryColumnSelector, java.time.LocalDateTime, java.util.function.Function)}
     */
    @Deprecated
    public static <T extends DataObjectImpl, U extends ItemModel<T>> DateTimeComparisonStatement<T, U> columnGreaterThan(TableReference table, ColumnReference column, LocalDateTime value,
            Function<U, Timestamp> getColValue) {
        return of(table, column, ComparisonOperator.GREATER_THAN, value, getColValue, (x, y) -> x.compareTo(y) > 0);
    }
    
    /**
     * @deprecated Use {@link #columnGreaterThan(scheduler.dao.dml.QueryColumnSelector, java.time.LocalDateTime, java.util.function.Function)}
     */
    @Deprecated
    public static <T extends DataObjectImpl, U extends ItemModel<T>> DateTimeComparisonStatement<T, U> columnGreaterThan(ColumnReference column, LocalDateTime value,
            Function<U, Timestamp> getColValue) {
        return columnGreaterThan(null, column, value, getColValue);
    }
    
    /**
     * Creates a Filter and SQL query definition for {@code `table`.`field`>=?}.
     * 
     * @param <T> The type of {@link DataObjectImpl} represented by the target {@link ItemModel}.
     * @param <U> The type of {@link ItemModel}.
     * @param columnSelector Specifies the database column to select from a {@link TableReference}.
     * @param value The value to match.
     * @param getColValue {@link Function} to get the {@link LocalDateTime} value from an {@link ItemModel}.
     * @return A Filter and SQL query definition for generating a SQL query statement for filtering items with a specific column value greater than
     * or equal to a {@link LocalDateTime} {@code value}.
     */
    public static <T extends DataObjectImpl, U extends ItemModel<T>> DateTimeComparisonStatement<T, U> columnGreaterThanOrEqualTo(
            QueryColumnSelector columnSelector, LocalDateTime value, Function<U, LocalDateTime> getColValue) {
        return of(columnSelector, ComparisonOperator.GREATER_THAN, value, (t, y) -> getColValue.apply(t).compareTo(y) >= 0);
    }
    
    /**
     * @deprecated Use {@link #columnGreaterThanOrEqualTo(scheduler.dao.dml.QueryColumnSelector, java.time.LocalDateTime, java.util.function.Function)}
     */
    @Deprecated
    public static <T extends DataObjectImpl, U extends ItemModel<T>> DateTimeComparisonStatement<T, U> columnGreaterThanOrEqualTo(TableReference table, ColumnReference column, LocalDateTime value,
            Function<U, Timestamp> getColValue) {
        return of(table, column, ComparisonOperator.NOT_LESS_THAN, value, getColValue, (x, y) -> x.compareTo(y) >= 0);
    }
    
    /**
     * @deprecated Use {@link #columnGreaterThanOrEqualTo(scheduler.dao.dml.QueryColumnSelector, java.time.LocalDateTime, java.util.function.Function)}
     */
    @Deprecated
    public static <T extends DataObjectImpl, U extends ItemModel<T>> DateTimeComparisonStatement<T, U> columnGreaterThanOrEqualTo(ColumnReference column, LocalDateTime value,
            Function<U, Timestamp> getColValue) {
        return columnGreaterThanOrEqualTo(null, column, value, getColValue);
    }
    
    /**
     * Creates a Filter and SQL query definition for {@code `table`.`field`<?}.
     * 
     * @param <T> The type of {@link DataObjectImpl} represented by the target {@link ItemModel}.
     * @param <U> The type of {@link ItemModel}.
     * @param columnSelector Specifies the database column to select from a {@link TableReference}.
     * @param value The value to match.
     * @param getColValue {@link Function} to get the {@link LocalDateTime} value from an {@link ItemModel}.
     * @return A Filter and SQL query definition for generating a SQL query statement for filtering items with a specific column value less than
     * a {@link LocalDateTime} {@code value}.
     */
    public static <T extends DataObjectImpl, U extends ItemModel<T>> DateTimeComparisonStatement<T, U> columnLessThan(
            QueryColumnSelector columnSelector, LocalDateTime value, Function<U, LocalDateTime> getColValue) {
        return of(columnSelector, ComparisonOperator.GREATER_THAN, value, (t, y) -> getColValue.apply(t).compareTo(y) < 0);
    }
    
    /**
     * @deprecated Use {@link #columnLessThan(scheduler.dao.dml.QueryColumnSelector, java.time.LocalDateTime, java.util.function.Function)}
     */
    @Deprecated
    public static <T extends DataObjectImpl, U extends ItemModel<T>> DateTimeComparisonStatement<T, U> columnLessThan(TableReference table, ColumnReference column, LocalDateTime value,
            Function<U, Timestamp> getColValue) {
        return of(table, column, ComparisonOperator.LESS_THAN, value, getColValue, (x, y) -> x.compareTo(y) < 0);
    }
    
    /**
     * @deprecated Use {@link #columnLessThan(scheduler.dao.dml.QueryColumnSelector, java.time.LocalDateTime, java.util.function.Function)}
     */
    @Deprecated
    public static <T extends DataObjectImpl, U extends ItemModel<T>> DateTimeComparisonStatement<T, U> columnLessThan(ColumnReference column, LocalDateTime value,
            Function<U, Timestamp> getColValue) {
        return columnLessThan(null, column, value, getColValue);
    }
    
    /**
     * Creates a Filter and SQL query definition for {@code `table`.`field`<=?}.
     * 
     * @param <T> The type of {@link DataObjectImpl} represented by the target {@link ItemModel}.
     * @param <U> The type of {@link ItemModel}.
     * @param columnSelector Specifies the database column to select from a {@link TableReference}.
     * @param value The value to match.
     * @param getColValue {@link Function} to get the {@link LocalDateTime} value from an {@link ItemModel}.
     * @return A Filter and SQL query definition for generating a SQL query statement for filtering items with a specific column value less than
     * or equal to a {@link LocalDateTime} {@code value}.
     */
    public static <T extends DataObjectImpl, U extends ItemModel<T>> DateTimeComparisonStatement<T, U> columnLessThanOrEqualTo(
            QueryColumnSelector columnSelector, LocalDateTime value, Function<U, LocalDateTime> getColValue) {
        return of(columnSelector, ComparisonOperator.GREATER_THAN, value, (t, y) -> getColValue.apply(t).compareTo(y) < 0);
    }
    
    /**
     * @deprecated Use {@link #columnLessThanOrEqualTo(scheduler.dao.dml.QueryColumnSelector, java.time.LocalDateTime, java.util.function.Function)}
     */
    @Deprecated
    public static <T extends DataObjectImpl, U extends ItemModel<T>> DateTimeComparisonStatement<T, U> columnLessThanOrEqualTo(TableReference table, ColumnReference column, LocalDateTime value,
            Function<U, Timestamp> getColValue) {
        return of(table, column, ComparisonOperator.NOT_GREATER_THAN, value, getColValue, (x, y) -> x.compareTo(y) <= 0);
    }
    
    /**
     * @deprecated Use {@link #columnLessThanOrEqualTo(scheduler.dao.dml.QueryColumnSelector, java.time.LocalDateTime, java.util.function.Function)}
     */
    @Deprecated
    public static <T extends DataObjectImpl, U extends ItemModel<T>> DateTimeComparisonStatement<T, U> columnLessThanOrEqualTo(ColumnReference column, LocalDateTime value,
            Function<U, Timestamp> getColValue) {
        return columnLessThanOrEqualTo(null, column, value, getColValue);
    }
    
}
