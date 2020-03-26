package scheduler.dao.dml.deprecated;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;
import java.util.function.BiPredicate;
import scheduler.dao.DataObjectImpl;
import scheduler.dao.ItemIntComparer;
import scheduler.dao.schema.ValueType;
import scheduler.view.ItemModel;

/**
 * Interface for filtering {@link ItemModel} and {@link DataObjectImpl} items by integer values.
 * 
 * @author Leonard T. Erwine (Student ID 356334)
 * @param <T> The data access object type.
 * @param <U> The item model type.
 */
public interface IntegerComparisonStatement<T extends DataObjectImpl, U extends ItemModel<T>> extends ColumnComparisonStatement<T, U> {
    
    int getValue();

    static <T extends DataObjectImpl, U extends ItemModel<T>> IntegerComparisonStatement<T, U> of(QueryColumnSelector columnSelector,
            ComparisonOperator op, int value, BiPredicate<U, Integer> predicate) {
        assert columnSelector.getColumn().getType().getValueType() == ValueType.INT : "Column type mismatch";
        Objects.requireNonNull(op);
        Objects.requireNonNull(predicate);
        return new IntegerComparisonStatement<T, U>() {
            @Override
            public int getValue() {
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
                ps.setInt(currentIndex, value);
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
     * @deprecated Use {@link #of(scheduler.dao.dml.QueryColumnSelector, scheduler.dao.dml.ComparisonOperator, int, java.util.function.BiPredicate)}, instead.
     */
    @Deprecated
    static <T extends DataObjectImpl, U extends ItemModel<T>> IntegerComparisonStatement<T, U> of(TableReference table, ColumnReference column, ComparisonOperator op,
            int value, BiPredicate<U, Integer> predicate) {
        ValueType valueType = column.getColumn().getType().getValueType();
        assert valueType == ValueType.INT : "Column type mismatch";
        assert null == table || table.getTable() == column.getColumn().getTable() : "Table/Column mismatch";
        Objects.requireNonNull(op);
        Objects.requireNonNull(predicate);
        return new IntegerComparisonStatement<T, U>() {
            
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
     * @deprecated Use {@link #of(scheduler.dao.dml.QueryColumnSelector, scheduler.dao.dml.ComparisonOperator, int, java.util.function.BiPredicate)}, instead.
     */
    @Deprecated
    static <T extends DataObjectImpl, U extends ItemModel<T>> IntegerComparisonStatement<T, U> of(ColumnReference column, ComparisonOperator op,
            int value, BiPredicate<U, Integer> predicate) {
        return of(null, column, op, value, predicate);
    }
    
    /**
     * Creates a Filter and SQL query definition for {@code `table`.`field`=?}.
     * 
     * @param <T> The type of {@link DataObjectImpl} represented by the target {@link ItemModel}.
     * @param <U> The type of {@link ItemModel}.
     * @param columnSelector Specifies the database column to select from a {@link TableReference}.
     * @param value The value to match.
     * @param valueAccessor An {@link ItemIntComparer} that reads the corresponding property value from a prospective {@link ItemModel} and compares it to another value.
     * @return A Filter and SQL query definition for generating a SQL query statement for filtering by an integer {@code value}.
     */
    public static <T extends DataObjectImpl, U extends ItemModel<T>> IntegerComparisonStatement<T, U> columnEquals(QueryColumnSelector columnSelector,
            int value,
            ItemIntComparer<T, U> valueAccessor) {
        return of(columnSelector, ComparisonOperator.EQUAL_TO, value, (x, y) -> valueAccessor.test(x, y));
    }
    
    /**
     * @deprecated Use {@link #columnEquals(scheduler.dao.dml.QueryColumnSelector, int, scheduler.dao.ItemIntComparer)}
     */
    @Deprecated
    public static <T extends DataObjectImpl, U extends ItemModel<T>> IntegerComparisonStatement<T, U> columnEquals(TableReference table, ColumnReference column, int value,
            ItemIntComparer<T, U> valueAccessor) {
        return of(table, column, ComparisonOperator.EQUAL_TO, value, (x, y) -> valueAccessor.test(x, y));
    }
    
    /**
     * @deprecated Use {@link #columnEquals(scheduler.dao.dml.QueryColumnSelector, int, scheduler.dao.ItemIntComparer)}
     */
    @Deprecated
    public static <T extends DataObjectImpl, U extends ItemModel<T>> IntegerComparisonStatement<T, U> columnEquals(ColumnReference column, int value,
            ItemIntComparer<T, U> valueAccessor) {
        return columnEquals(null, column, value, valueAccessor);
    }
    
    /**
     * Creates a Filter and SQL query definition for {@code `table`.`field`<>?}.
     * 
     * @param <T> The type of {@link DataObjectImpl} represented by the target {@link ItemModel}.
     * @param <U> The type of {@link ItemModel}.
     * @param columnSelector Specifies the database column to select from a {@link TableReference}.
     * @param value The value to match.
     * @param valueAccessor An {@link ItemIntComparer} that reads the corresponding property value from a prospective {@link ItemModel} and compares it to another value.
     * @return A Filter and SQL query definition for generating a SQL query statement for exclusive filtering by an integer {@code value}.
     */
    public static <T extends DataObjectImpl, U extends ItemModel<T>> IntegerComparisonStatement<T, U> columnNotEquals(QueryColumnSelector columnSelector,
            int value, ItemIntComparer<T, U> valueAccessor) {
        return of(columnSelector, ComparisonOperator.NOT_EQUAL_TO, value, (x, y) -> !valueAccessor.test(x, y));
    }
    
    /**
     * @deprecated Use {@link #columnNotEquals(scheduler.dao.dml.QueryColumnSelector, int, scheduler.dao.ItemIntComparer)}
     */
    @Deprecated
    public static <T extends DataObjectImpl, U extends ItemModel<T>> IntegerComparisonStatement<T, U> columnNotEquals(TableReference table, ColumnReference column, int value,
            ItemIntComparer<T, U> valueAccessor) {
        return of(table, column, ComparisonOperator.NOT_EQUAL_TO, value, (x, y) -> !valueAccessor.test(x, y));
    }
    
    /**
     * @deprecated Use {@link #columnNotEquals(scheduler.dao.dml.QueryColumnSelector, int, scheduler.dao.ItemIntComparer)}
     */
    @Deprecated
    public static <T extends DataObjectImpl, U extends ItemModel<T>> IntegerComparisonStatement<T, U> columnNotEquals(ColumnReference column, int value,
            ItemIntComparer<T, U> valueAccessor) {
        return columnNotEquals(null, column, value, valueAccessor);
    }
    
    /**
     * Creates a Filter and SQL query definition for {@code `table`.`field`>?}.
     * 
     * @param <T> The type of {@link DataObjectImpl} represented by the target {@link ItemModel}.
     * @param <U> The type of {@link ItemModel}.
     * @param columnSelector Specifies the database column to select from a {@link TableReference}.
     * @param value The value to match.
     * @param valueAccessor An {@link ItemIntComparer} that reads the corresponding property value from a prospective {@link ItemModel} and compares it to another value.
     * @return A Filter and SQL query definition for generating a SQL query statement for filtering items with a specific column value greater than an integer {@code value}.
     */
    public static <T extends DataObjectImpl, U extends ItemModel<T>> IntegerComparisonStatement<T, U> columnGreaterThan(QueryColumnSelector columnSelector,
            int value, ItemIntComparer<T, U> valueAccessor) {
        return of(columnSelector, ComparisonOperator.GREATER_THAN, value, (x, y) -> valueAccessor.compareTo(x, y) > 0);
    }
    
    /**
     * @deprecated Use {@link #columnGreaterThan(scheduler.dao.dml.QueryColumnSelector, int, scheduler.dao.ItemIntComparer)}
     */
    @Deprecated
    public static <T extends DataObjectImpl, U extends ItemModel<T>> IntegerComparisonStatement<T, U> columnGreaterThan(TableReference table, ColumnReference column, int value,
            ItemIntComparer<T, U> valueAccessor) {
        return of(table, column, ComparisonOperator.GREATER_THAN, value, (x, y) -> valueAccessor.compareTo(x, y) > 0);
    }
    
    /**
     * @deprecated Use {@link #columnGreaterThan(scheduler.dao.dml.QueryColumnSelector, int, scheduler.dao.ItemIntComparer)}
     */
    @Deprecated
    public static <T extends DataObjectImpl, U extends ItemModel<T>> IntegerComparisonStatement<T, U> columnGreaterThan(ColumnReference column, int value,
            ItemIntComparer<T, U> valueAccessor) {
        return columnGreaterThan(null, column, value, valueAccessor);
    }
    
    /**
     * Creates a Filter and SQL query definition for {@code `table`.`field`>=?}.
     * 
     * @param <T> The type of {@link DataObjectImpl} represented by the target {@link ItemModel}.
     * @param <U> The type of {@link ItemModel}.
     * @param columnSelector Specifies the database column to select from a {@link TableReference}.
     * @param value The value to match.
     * @param valueAccessor An {@link ItemIntComparer} that reads the corresponding property value from a prospective {@link ItemModel} and compares it to another value.
     * @return A Filter and SQL query definition for generating a SQL query statement for filtering items with a specific column value greater than or equal to an integer {@code value}.
     */
    public static <T extends DataObjectImpl, U extends ItemModel<T>> IntegerComparisonStatement<T, U> columnGreaterThanOrEqualTo(QueryColumnSelector columnSelector,
            int value, ItemIntComparer<T, U> valueAccessor) {
        return of(columnSelector, ComparisonOperator.NOT_LESS_THAN, value, (x, y) -> valueAccessor.compareTo(x, y) >= 0);
    }
    
    /**
     * @deprecated Use {@link #columnGreaterThanOrEqualTo(scheduler.dao.dml.QueryColumnSelector, int, scheduler.dao.ItemIntComparer)}
     */
    @Deprecated
    public static <T extends DataObjectImpl, U extends ItemModel<T>> IntegerComparisonStatement<T, U> columnGreaterThanOrEqualTo(TableReference table, ColumnReference column,
            int value, ItemIntComparer<T, U> valueAccessor) {
        return of(table, column, ComparisonOperator.NOT_LESS_THAN, value, (x, y) -> valueAccessor.compareTo(x, y) >= 0);
    }
    
    /**
     * @deprecated Use {@link #columnGreaterThanOrEqualTo(scheduler.dao.dml.QueryColumnSelector, int, scheduler.dao.ItemIntComparer)}
     */
    @Deprecated
    public static <T extends DataObjectImpl, U extends ItemModel<T>> IntegerComparisonStatement<T, U> columnGreaterThanOrEqualTo(ColumnReference column,
            int value, ItemIntComparer<T, U> valueAccessor) {
        return columnGreaterThanOrEqualTo(null, column, value, valueAccessor);
    }
    
    /**
     * Creates a Filter and SQL query definition for {@code `table`.`field`<?}.
     * 
     * @param <T> The type of {@link DataObjectImpl} represented by the target {@link ItemModel}.
     * @param <U> The type of {@link ItemModel}.
     * @param columnSelector Specifies the database column to select from a {@link TableReference}.
     * @param value The value to match.
     * @param valueAccessor An {@link ItemIntComparer} that reads the corresponding property value from a prospective {@link ItemModel} and compares it to another value.
     * @return A Filter and SQL query definition for generating a SQL query statement for filtering items with a specific column value less than an integer {@code value}.
     */
    public static <T extends DataObjectImpl, U extends ItemModel<T>> IntegerComparisonStatement<T, U> columnLessThan(QueryColumnSelector columnSelector,
            int value, ItemIntComparer<T, U> valueAccessor) {
        return of(columnSelector, ComparisonOperator.LESS_THAN, value, (x, y) -> valueAccessor.compareTo(x, y) < 0);
    }
    
    /**
     * @deprecated Use {@link #columnLessThan(scheduler.dao.dml.QueryColumnSelector, int, scheduler.dao.ItemIntComparer)}
     */
    @Deprecated
    public static <T extends DataObjectImpl, U extends ItemModel<T>> IntegerComparisonStatement<T, U> columnLessThan(TableReference table, ColumnReference column, int value,
            ItemIntComparer<T, U> valueAccessor) {
        return of(table, column, ComparisonOperator.LESS_THAN, value, (x, y) -> valueAccessor.compareTo(x, y) < 0);
    }
    
    /**
     * @deprecated Use {@link #columnLessThan(scheduler.dao.dml.QueryColumnSelector, int, scheduler.dao.ItemIntComparer)}
     */
    @Deprecated
    public static <T extends DataObjectImpl, U extends ItemModel<T>> IntegerComparisonStatement<T, U> columnLessThan(ColumnReference column, int value,
            ItemIntComparer<T, U> valueAccessor) {
        return columnLessThan(null, column, value, valueAccessor);
    }
    
    /**
     * Creates a Filter and SQL query definition for {@code `table`.`field`<=?}.
     * 
     * @param <T> The type of {@link DataObjectImpl} represented by the target {@link ItemModel}.
     * @param <U> The type of {@link ItemModel}.
     * @param columnSelector Specifies the database column to select from a {@link TableReference}.
     * @param value The value to match.
     * @param valueAccessor An {@link ItemIntComparer} that reads the corresponding property value from a prospective {@link ItemModel} and compares it to another value.
     * @return A Filter and SQL query definition for generating a SQL query statement for filtering items with a specific column value less than or equal to an integer {@code value}.
     */
    public static <T extends DataObjectImpl, U extends ItemModel<T>> IntegerComparisonStatement<T, U> columnLessThanOrEqualTo(QueryColumnSelector columnSelector,
            int value, ItemIntComparer<T, U> valueAccessor) {
        return of(columnSelector, ComparisonOperator.NOT_GREATER_THAN, value, (x, y) -> valueAccessor.compareTo(x, y) <= 0);
    }
    
    /**
     * @deprecated Use {@link #columnLessThanOrEqualTo(scheduler.dao.dml.QueryColumnSelector, int, scheduler.dao.ItemIntComparer)}
     */
    @Deprecated
    public static <T extends DataObjectImpl, U extends ItemModel<T>> IntegerComparisonStatement<T, U> columnLessThanOrEqualTo(TableReference table, ColumnReference column,
            int value, ItemIntComparer<T, U> valueAccessor) {
        return of(table, column, ComparisonOperator.NOT_GREATER_THAN, value, (x, y) -> valueAccessor.compareTo(x, y) <= 0);
    }
    
    /**
     * @deprecated Use {@link #columnLessThanOrEqualTo(scheduler.dao.dml.QueryColumnSelector, int, scheduler.dao.ItemIntComparer)}
     */
    @Deprecated
    public static <T extends DataObjectImpl, U extends ItemModel<T>> IntegerComparisonStatement<T, U> columnLessThanOrEqualTo(ColumnReference column, int value,
            ItemIntComparer<T, U> valueAccessor) {
        return columnLessThanOrEqualTo(null, column, value, valueAccessor);
    }
    
}
