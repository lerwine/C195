package scheduler.dao.dml;

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
    
    static <T extends DataObjectImpl, U extends ItemModel<T>> IntegerComparisonStatement<T, U> of(TableReference table, ColumnReference column, ComparisonOperator op,
            int value, BiPredicate<U, Integer> predicate) {
        ValueType valueType = column.getColumn().getType().getValueType();
        assert valueType == ValueType.INT : "Column type mismatch";
        assert null == table || table.getTable() == column.getColumn().getTable() : "Table/Column mismatch";
        Objects.requireNonNull(op);
        Objects.requireNonNull(predicate);
        return new IntegerComparisonStatement<T, U>() {
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
            public boolean test(U t) {
                
                return predicate.test(t, value);
            }

        };
    }
    
    static <T extends DataObjectImpl, U extends ItemModel<T>> IntegerComparisonStatement<T, U> of(ColumnReference column, ComparisonOperator op,
            int value, BiPredicate<U, Integer> predicate) {
        return of(null, column, op, value, predicate);
    }
    
    /**
     * Creates a Filter and SQL query definition for {@code `table`.`field`=?}.
     * 
     * @param <T> The type of {@link DataObjectImpl} represented by the target {@link ItemModel}.
     * @param <U> The type of {@link ItemModel}.
     * @param table The explicitly-defined database table.
     * @param column The database column from the given {@link TableReference}.
     * @param value The value to match.
     * @param valueAccessor An {@link ItemIntComparer} that reads the corresponding property value from a prospective {@link ItemModel} and compares it to another value.
     * @return A Filter and SQL query definition for generating a SQL query statement for filtering by an integer {@code value}.
     */
    public static <T extends DataObjectImpl, U extends ItemModel<T>> IntegerComparisonStatement<T, U> columnEquals(TableReference table, ColumnReference column, int value,
            ItemIntComparer<T, U> valueAccessor) {
        return of(table, column, ComparisonOperator.EQUAL_TO, value, (x, y) -> valueAccessor.test(x, y));
    }
    
    /**
     * Creates a Filter and SQL query definition for {@code `field`=?}.
     * 
     * @param <T> The type of {@link DataObjectImpl} represented by the target {@link ItemModel}.
     * @param <U> The type of {@link ItemModel}.
     * @param column The database column from the source {@link TableColumnList}.
     * @param value The value to match.
     * @param valueAccessor An {@link ItemIntComparer} that reads the corresponding property value from a prospective {@link ItemModel} and compares it to another value.
     * @return A Filter and SQL query definition for generating a SQL query statement for filtering by a matching integer {@code value}.
     */
    public static <T extends DataObjectImpl, U extends ItemModel<T>> IntegerComparisonStatement<T, U> columnEquals(ColumnReference column, int value,
            ItemIntComparer<T, U> valueAccessor) {
        return columnEquals(null, column, value, valueAccessor);
    }
    
    /**
     * Creates a Filter and SQL query definition for {@code `table`.`field`<>?}.
     * 
     * @param <T> The type of {@link DataObjectImpl} represented by the target {@link ItemModel}.
     * @param <U> The type of {@link ItemModel}.
     * @param table The explicitly-defined database table.
     * @param column The database column from the given {@link TableReference}.
     * @param value The value to match.
     * @param valueAccessor An {@link ItemIntComparer} that reads the corresponding property value from a prospective {@link ItemModel} and compares it to another value.
     * @return A Filter and SQL query definition for generating a SQL query statement for exclusive filtering by an integer {@code value}.
     */
    public static <T extends DataObjectImpl, U extends ItemModel<T>> IntegerComparisonStatement<T, U> columnNotEquals(TableReference table, ColumnReference column, int value,
            ItemIntComparer<T, U> valueAccessor) {
        return of(table, column, ComparisonOperator.NOT_EQUAL_TO, value, (x, y) -> !valueAccessor.test(x, y));
    }
    
    /**
     * Creates a Filter and SQL query definition for {@code `table`.`field`<>?}.
     * 
     * @param <T> The type of {@link DataObjectImpl} represented by the target {@link ItemModel}.
     * @param <U> The type of {@link ItemModel}.
     * @param column The database column from the source {@link TableColumnList}.
     * @param value The value to match.
     * @param valueAccessor An {@link ItemIntComparer} that reads the corresponding property value from a prospective {@link ItemModel} and compares it to another value.
     * @return A Filter and SQL query definition for generating a SQL query statement for exclusive filtering by an integer {@code value}.
     */
    public static <T extends DataObjectImpl, U extends ItemModel<T>> IntegerComparisonStatement<T, U> columnNotEquals(ColumnReference column, int value,
            ItemIntComparer<T, U> valueAccessor) {
        return columnNotEquals(null, column, value, valueAccessor);
    }
    
    /**
     * Creates a Filter and SQL query definition for {@code `table`.`field`>?}.
     * 
     * @param <T> The type of {@link DataObjectImpl} represented by the target {@link ItemModel}.
     * @param <U> The type of {@link ItemModel}.
     * @param table The explicitly-defined database table.
     * @param column The database column from the given {@link TableReference}.
     * @param value The value to match.
     * @param valueAccessor An {@link ItemIntComparer} that reads the corresponding property value from a prospective {@link ItemModel} and compares it to another value.
     * @return A Filter and SQL query definition for generating a SQL query statement for filtering items with a specific column value greater than an integer {@code value}.
     */
    public static <T extends DataObjectImpl, U extends ItemModel<T>> IntegerComparisonStatement<T, U> columnGreaterThan(TableReference table, ColumnReference column, int value,
            ItemIntComparer<T, U> valueAccessor) {
        return of(table, column, ComparisonOperator.GREATER_THAN, value, (x, y) -> valueAccessor.compareTo(x, y) > 0);
    }
    
    /**
     * Creates a Filter and SQL query definition for {@code `table`.`field`>?}.
     * 
     * @param <T> The type of {@link DataObjectImpl} represented by the target {@link ItemModel}.
     * @param <U> The type of {@link ItemModel}.
     * @param column The database column from the source {@link TableColumnList}.
     * @param value The value to match.
     * @param valueAccessor An {@link ItemIntComparer} that reads the corresponding property value from a prospective {@link ItemModel} and compares it to another value.
     * @return A Filter and SQL query definition for generating a SQL query statement for filtering items with a specific column value greater than an integer {@code value}.
     */
    public static <T extends DataObjectImpl, U extends ItemModel<T>> IntegerComparisonStatement<T, U> columnGreaterThan(ColumnReference column, int value,
            ItemIntComparer<T, U> valueAccessor) {
        return columnGreaterThan(null, column, value, valueAccessor);
    }
    
    /**
     * Creates a Filter and SQL query definition for {@code `table`.`field`>=?}.
     * 
     * @param <T> The type of {@link DataObjectImpl} represented by the target {@link ItemModel}.
     * @param <U> The type of {@link ItemModel}.
     * @param table The explicitly-defined database table.
     * @param column The database column from the given {@link TableReference}.
     * @param value The value to match.
     * @param valueAccessor An {@link ItemIntComparer} that reads the corresponding property value from a prospective {@link ItemModel} and compares it to another value.
     * @return A Filter and SQL query definition for generating a SQL query statement for filtering items with a specific column value greater than or equal to an integer {@code value}.
     */
    public static <T extends DataObjectImpl, U extends ItemModel<T>> IntegerComparisonStatement<T, U> columnGreaterThanOrEqualTo(TableReference table, ColumnReference column,
            int value, ItemIntComparer<T, U> valueAccessor) {
        return of(table, column, ComparisonOperator.NOT_LESS_THAN, value, (x, y) -> valueAccessor.compareTo(x, y) >= 0);
    }
    
    /**
     * Creates a Filter and SQL query definition for {@code `table`.`field`>=?}.
     * 
     * @param <T> The type of {@link DataObjectImpl} represented by the target {@link ItemModel}.
     * @param <U> The type of {@link ItemModel}.
     * @param column The database column from the source {@link TableColumnList}.
     * @param value The value to match.
     * @param valueAccessor An {@link ItemIntComparer} that reads the corresponding property value from a prospective {@link ItemModel} and compares it to another value.
     * @return A Filter and SQL query definition for generating a SQL query statement for filtering items with a specific column value greater than or equal to an integer {@code value}.
     */
    public static <T extends DataObjectImpl, U extends ItemModel<T>> IntegerComparisonStatement<T, U> columnGreaterThanOrEqualTo(ColumnReference column,
            int value, ItemIntComparer<T, U> valueAccessor) {
        return columnGreaterThanOrEqualTo(null, column, value, valueAccessor);
    }
    
    /**
     * Creates a Filter and SQL query definition for {@code `table`.`field`<?}.
     * 
     * @param <T> The type of {@link DataObjectImpl} represented by the target {@link ItemModel}.
     * @param <U> The type of {@link ItemModel}.
     * @param table The explicitly-defined database table.
     * @param column The database column from the given {@link TableReference}.
     * @param value The value to match.
     * @param valueAccessor An {@link ItemIntComparer} that reads the corresponding property value from a prospective {@link ItemModel} and compares it to another value.
     * @return A Filter and SQL query definition for generating a SQL query statement for filtering items with a specific column value less than an integer {@code value}.
     */
    public static <T extends DataObjectImpl, U extends ItemModel<T>> IntegerComparisonStatement<T, U> columnLessThan(TableReference table, ColumnReference column, int value,
            ItemIntComparer<T, U> valueAccessor) {
        return of(table, column, ComparisonOperator.LESS_THAN, value, (x, y) -> valueAccessor.compareTo(x, y) < 0);
    }
    
    /**
     * Creates a Filter and SQL query definition for {@code `table`.`field`<?}.
     * 
     * @param <T> The type of {@link DataObjectImpl} represented by the target {@link ItemModel}.
     * @param <U> The type of {@link ItemModel}.
     * @param column The database column from the source {@link TableColumnList}.
     * @param value The value to match.
     * @param valueAccessor An {@link ItemIntComparer} that reads the corresponding property value from a prospective {@link ItemModel} and compares it to another value.
     * @return A Filter and SQL query definition for generating a SQL query statement for filtering items with a specific column value less than an integer {@code value}.
     */
    public static <T extends DataObjectImpl, U extends ItemModel<T>> IntegerComparisonStatement<T, U> columnLessThan(ColumnReference column, int value,
            ItemIntComparer<T, U> valueAccessor) {
        return columnLessThan(null, column, value, valueAccessor);
    }
    
    /**
     * Creates a Filter and SQL query definition for {@code `table`.`field`<=?}.
     * 
     * @param <T> The type of {@link DataObjectImpl} represented by the target {@link ItemModel}.
     * @param <U> The type of {@link ItemModel}.
     * @param table The explicitly-defined database table.
     * @param column The database column from the given {@link TableReference}.
     * @param value The value to match.
     * @param valueAccessor An {@link ItemIntComparer} that reads the corresponding property value from a prospective {@link ItemModel} and compares it to another value.
     * @return A Filter and SQL query definition for generating a SQL query statement for filtering items with a specific column value less than or equal to an integer {@code value}.
     */
    public static <T extends DataObjectImpl, U extends ItemModel<T>> IntegerComparisonStatement<T, U> columnLessThanOrEqualTo(TableReference table, ColumnReference column,
            int value, ItemIntComparer<T, U> valueAccessor) {
        return of(table, column, ComparisonOperator.NOT_GREATER_THAN, value, (x, y) -> valueAccessor.compareTo(x, y) <= 0);
    }
    
    /**
     * Creates a Filter and SQL query definition for {@code `table`.`field`<=?}.
     * 
     * @param <T> The type of {@link DataObjectImpl} represented by the target {@link ItemModel}.
     * @param <U> The type of {@link ItemModel}.
     * @param column The database column from the source {@link TableColumnList}.
     * @param value The value to match.
     * @param valueAccessor An {@link ItemIntComparer} that reads the corresponding property value from a prospective {@link ItemModel} and compares it to another value.
     * @return A Filter and SQL query definition for generating a SQL query statement for filtering items with a specific column value less than or equal to an integer {@code value}.
     */
    public static <T extends DataObjectImpl, U extends ItemModel<T>> IntegerComparisonStatement<T, U> columnLessThanOrEqualTo(ColumnReference column, int value,
            ItemIntComparer<T, U> valueAccessor) {
        return columnLessThanOrEqualTo(null, column, value, valueAccessor);
    }
    
}
