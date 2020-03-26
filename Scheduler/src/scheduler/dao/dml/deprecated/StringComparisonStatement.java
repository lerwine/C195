package scheduler.dao.dml.deprecated;

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
    
    static <T extends DataObjectImpl, U extends ItemModel<T>> StringComparisonStatement<T, U> of(QueryColumnSelector columnSelector,
            ComparisonOperator op, String value, BiPredicate<U, String> predicate) {
        ValueType valueType = columnSelector.getColumn().getType().getValueType();
        assert valueType == ValueType.STRING : "Column type mismatch";
        Objects.requireNonNull(op);
        Objects.requireNonNull(predicate);
        Objects.requireNonNull(value);
        return new StringComparisonStatement<T, U>() {
            @Override
            public String getValue() {
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
                ps.setString(currentIndex, ComparisonOperator.toStringParam(value, op));
                return currentIndex + 1;
            }

            @Override
            public boolean test(U t) {
                return predicate.test(t, value);
            }

        };
    }
    
    /**
     * @deprecated Use {@link #of(scheduler.dao.dml.QueryColumnSelector, scheduler.dao.dml.ComparisonOperator, java.lang.String, java.util.function.BiPredicate)}
     */
    @Deprecated
    public static <T extends DataObjectImpl, U extends ItemModel<T>> StringComparisonStatement<T, U> of(TableReference table, ColumnReference column, ComparisonOperator op,
            String value, BiPredicate<U, String> predicate) {
        ValueType valueType = column.getColumn().getType().getValueType();
        assert valueType == ValueType.STRING : "Column type mismatch";
        assert null == table || table.getTable() == column.getColumn().getTable() : "Table/Column mismatch";
        Objects.requireNonNull(op);
        Objects.requireNonNull(predicate);
        Objects.requireNonNull(value);
        return new StringComparisonStatement<T, U>() {
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
                return predicate.test(t, value);
            }

            @Override
            public QueryColumnSelector getColumnSelector() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

        };
    }
    
    /**
     * @deprecated Use {@link #of(scheduler.dao.dml.QueryColumnSelector, scheduler.dao.dml.ComparisonOperator, java.lang.String, java.util.function.BiPredicate)}
     */
    @Deprecated
    public static <T extends DataObjectImpl, U extends ItemModel<T>> StringComparisonStatement<T, U> of(ColumnReference column, ComparisonOperator op,
            String value, BiPredicate<U, String> predicate) {
        return of(null, column, op, value, predicate);
    }
    
    /**
     * @deprecated Use {@link #of(scheduler.dao.dml.QueryColumnSelector, scheduler.dao.dml.ComparisonOperator, java.lang.String, java.util.function.BiPredicate)}
     */
    @Deprecated
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

            @Override
            public QueryColumnSelector getColumnSelector() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

        };
    }
    
    /**
     * @deprecated Use {@link #of(scheduler.dao.dml.QueryColumnSelector, scheduler.dao.dml.ComparisonOperator, java.lang.String, java.util.function.BiPredicate)}
     */
    @Deprecated
    public static <T extends DataObjectImpl, U extends ItemModel<T>> StringComparisonStatement<T, U> of(ColumnReference column, ComparisonOperator op,
            String value, Function<U, String> getColValue, BiPredicate<String, String> predicate) {
        return of(null, column, op, value, getColValue, predicate);
    }
    
    /**
     * Creates a Filter and SQL query definition for {@code `table`.`field`=?}.
     * 
     * @param <T> The type of {@link DataObjectImpl} represented by the target {@link ItemModel}.
     * @param <U> The type of {@link ItemModel}.
     * @param columnSelector Specifies the database column to select from a {@link TableReference}.
     * @param value The value to match.
     * @param getColValue {@link Function} to get the {@link LocalDateTime} value from an {@link ItemModel}.
     * @return A Filter and SQL query definition for generating a SQL query statement for filtering items with a specific column value equal to
     * a string {@code value}.
     */
    public static <T extends DataObjectImpl, U extends ItemModel<T>> StringComparisonStatement<T, U> columnEquals(QueryColumnSelector columnSelector,
            String value, Function<U, String> getColValue) {
        return of(columnSelector, ComparisonOperator.EQUAL_TO, value, (t, u) -> getColValue.apply(t).equals(u));
    }
    
    /**
     * @deprecated Use {@link #columnEquals(scheduler.dao.dml.QueryColumnSelector, java.lang.String, java.util.function.Function)}
     */
    @Deprecated
    public static <T extends DataObjectImpl, U extends ItemModel<T>> StringComparisonStatement<T, U> columnEquals(TableReference table, ColumnReference column, String value,
            Function<U, String> getColValue) {
        return of(table, column, ComparisonOperator.EQUAL_TO, value, getColValue, (x, y) -> x.equals(y));
    }
    
    /**
     * @deprecated Use {@link #columnEquals(scheduler.dao.dml.QueryColumnSelector, java.lang.String, java.util.function.Function)}
     */
    @Deprecated
    public static <T extends DataObjectImpl, U extends ItemModel<T>> StringComparisonStatement<T, U> columnEquals(ColumnReference column, String value, Function<U, String> getColValue) {
        return columnEquals(null, column, value, getColValue);
    }
    
    public static <T extends DataObjectImpl, U extends ItemModel<T>> StringComparisonStatement<T, U> columnNotEquals(QueryColumnSelector columnSelector,
            String value, Function<U, String> getColValue) {
        return of(columnSelector, ComparisonOperator.NOT_EQUAL_TO, value, (x, y) -> !getColValue.apply(x).equals(y));
    }
    
    /**
     * @deprecated Use {@link #columnNotEquals(scheduler.dao.dml.QueryColumnSelector, java.lang.String, java.util.function.Function)}
     */
    @Deprecated
    public static <T extends DataObjectImpl, U extends ItemModel<T>> StringComparisonStatement<T, U> columnNotEquals(TableReference table, ColumnReference column, String value,
            Function<U, String> getColValue) {
        return of(table, column, ComparisonOperator.NOT_EQUAL_TO, value, getColValue, (x, y) -> !x.equals(y));
    }
    
    /**
     * @deprecated Use {@link #columnNotEquals(scheduler.dao.dml.QueryColumnSelector, java.lang.String, java.util.function.Function)}
     */
    @Deprecated
    public static <T extends DataObjectImpl, U extends ItemModel<T>> StringComparisonStatement<T, U> columnNotEquals(ColumnReference column, String value, Function<U, String> getColValue) {
        return columnNotEquals(null, column, value, getColValue);
    }
    
    public static <T extends DataObjectImpl, U extends ItemModel<T>> StringComparisonStatement<T, U> columnGreaterThan(QueryColumnSelector columnSelector,
            String value, Function<U, String> getColValue) {
        return of(columnSelector, ComparisonOperator.GREATER_THAN, value, (x, y) -> getColValue.apply(x).compareTo(y) > 0);
    }
    
    /**
     * @deprecated Use {@link #columnGreaterThan(scheduler.dao.dml.QueryColumnSelector, java.lang.String, java.util.function.Function)}
     */
    @Deprecated
    public static <T extends DataObjectImpl, U extends ItemModel<T>> StringComparisonStatement<T, U> columnGreaterThan(TableReference table, ColumnReference column, String value,
            Function<U, String> getColValue) {
        return of(table, column, ComparisonOperator.GREATER_THAN, value, getColValue, (x, y) -> x.compareTo(y) > 0);
    }
    
    /**
     * @deprecated Use {@link #columnGreaterThan(scheduler.dao.dml.QueryColumnSelector, java.lang.String, java.util.function.Function)}
     */
    @Deprecated
    public static <T extends DataObjectImpl, U extends ItemModel<T>> StringComparisonStatement<T, U> columnGreaterThan(ColumnReference column, String value, Function<U, String> getColValue) {
        return columnGreaterThan(null, column, value, getColValue);
    }
    
    public static <T extends DataObjectImpl, U extends ItemModel<T>> StringComparisonStatement<T, U> columnGreaterThanOrEqualTo(QueryColumnSelector columnSelector,
            String value, Function<U, String> getColValue) {
        return of(columnSelector, ComparisonOperator.NOT_LESS_THAN, value, (x, y) -> getColValue.apply(x).compareTo(y) >= 0);
    }
    
    /**
     * @deprecated Use {@link #columnGreaterThanOrEqualTo(scheduler.dao.dml.QueryColumnSelector, java.lang.String, java.util.function.Function)}
     */
    @Deprecated
    public static <T extends DataObjectImpl, U extends ItemModel<T>> StringComparisonStatement<T, U> columnGreaterThanOrEqualTo(TableReference table, ColumnReference column, String value,
            Function<U, String> getColValue) {
        return of(table, column, ComparisonOperator.NOT_LESS_THAN, value, getColValue, (x, y) -> x.compareTo(y) >= 0);
    }
    
    /**
     * @deprecated Use {@link #columnGreaterThanOrEqualTo(scheduler.dao.dml.QueryColumnSelector, java.lang.String, java.util.function.Function)}
     */
    @Deprecated
    public static <T extends DataObjectImpl, U extends ItemModel<T>> StringComparisonStatement<T, U> columnGreaterThanOrEqualTo(ColumnReference column, String value,
            Function<U, String> getColValue) {
        return columnGreaterThanOrEqualTo(null, column, value, getColValue);
    }
    
    public static <T extends DataObjectImpl, U extends ItemModel<T>> StringComparisonStatement<T, U> columnLessThan(QueryColumnSelector columnSelector,
            String value, Function<U, String> getColValue) {
        return of(columnSelector, ComparisonOperator.LESS_THAN, value, (x, y) -> getColValue.apply(x).compareTo(y) < 0);
    }
    
    /**
     * @deprecated Use {@link #columnLessThan(scheduler.dao.dml.QueryColumnSelector, java.lang.String, java.util.function.Function)}
     */
    @Deprecated
    public static <T extends DataObjectImpl, U extends ItemModel<T>> StringComparisonStatement<T, U> columnLessThan(TableReference table, ColumnReference column, String value,
            Function<U, String> getColValue) {
        return of(table, column, ComparisonOperator.LESS_THAN, value, getColValue, (x, y) -> x.compareTo(y) < 0);
    }
    
    /**
     * @deprecated Use {@link #columnLessThan(scheduler.dao.dml.QueryColumnSelector, java.lang.String, java.util.function.Function)}
     */
    @Deprecated
    public static <T extends DataObjectImpl, U extends ItemModel<T>> StringComparisonStatement<T, U> columnLessThan(ColumnReference column, String value, Function<U, String> getColValue) {
        return columnLessThan(null, column, value, getColValue);
    }
    
    public static <T extends DataObjectImpl, U extends ItemModel<T>> StringComparisonStatement<T, U> columnLessThanOrEqualTo(QueryColumnSelector columnSelector,
            String value, Function<U, String> getColValue) {
        return of(columnSelector, ComparisonOperator.NOT_GREATER_THAN, value, (x, y) -> getColValue.apply(x).compareTo(y) <= 0);
    }
    
    /**
     * @deprecated Use {@link #columnLessThanOrEqualTo(scheduler.dao.dml.QueryColumnSelector, java.lang.String, java.util.function.Function)}
     */
    @Deprecated
    public static <T extends DataObjectImpl, U extends ItemModel<T>> StringComparisonStatement<T, U> columnLessThanOrEqualTo(TableReference table, ColumnReference column, String value,
            Function<U, String> getColValue) {
        return of(table, column, ComparisonOperator.NOT_GREATER_THAN, value, getColValue, (x, y) -> x.compareTo(y) <= 0);
    }
    
    /**
     * @deprecated Use {@link #columnLessThanOrEqualTo(scheduler.dao.dml.QueryColumnSelector, java.lang.String, java.util.function.Function)}
     */
    @Deprecated
    public static <T extends DataObjectImpl, U extends ItemModel<T>> StringComparisonStatement<T, U> columnLessThanOrEqualTo(ColumnReference column, String value,
            Function<U, String> getColValue) {
        return columnLessThanOrEqualTo(null, column, value, getColValue);
    }
    
    public static <T extends DataObjectImpl, U extends ItemModel<T>> StringComparisonStatement<T, U> columnStartsWith(QueryColumnSelector columnSelector,
            String value, Function<U, String> getColValue) {
        return of(columnSelector, ComparisonOperator.STARTS_WITH, value, (x, y) -> getColValue.apply(x).startsWith(y));
    }
    
    /**
     * @deprecated Use {@link #columnStartsWith(scheduler.dao.dml.QueryColumnSelector, java.lang.String, java.util.function.Function)}
     */
    @Deprecated
    public static <T extends DataObjectImpl, U extends ItemModel<T>> StringComparisonStatement<T, U> columnStartsWith(TableReference table, ColumnReference column, String value,
            Function<U, String> getColValue) {
        return of(table, column, ComparisonOperator.STARTS_WITH, value, getColValue, (x, y) -> x.startsWith(y));
    }
    
    /**
     * @deprecated Use {@link #columnStartsWith(scheduler.dao.dml.QueryColumnSelector, java.lang.String, java.util.function.Function)}
     */
    @Deprecated
    public static <T extends DataObjectImpl, U extends ItemModel<T>> StringComparisonStatement<T, U> columnStartsWith(ColumnReference column, String value,
            Function<U, String> getColValue) {
        return columnStartsWith(null, column, value, getColValue);
    }
    
    public static <T extends DataObjectImpl, U extends ItemModel<T>> StringComparisonStatement<T, U> columnEndsWith(QueryColumnSelector columnSelector,
            String value, Function<U, String> getColValue) {
        return of(columnSelector, ComparisonOperator.ENDS_WITH, value, (x, y) -> getColValue.apply(x).startsWith(y));
    }
    
    /**
     * @deprecated Use {@link #columnEndsWith(scheduler.dao.dml.QueryColumnSelector, java.lang.String, java.util.function.Function)}
     */
    @Deprecated
    public static <T extends DataObjectImpl, U extends ItemModel<T>> StringComparisonStatement<T, U> columnEndsWith(TableReference table, ColumnReference column, String value,
            Function<U, String> getColValue) {
        return of(table, column, ComparisonOperator.ENDS_WITH, value, getColValue, (x, y) -> x.startsWith(y));
    }
    
    /**
     * @deprecated Use {@link #columnEndsWith(scheduler.dao.dml.QueryColumnSelector, java.lang.String, java.util.function.Function)}
     */
    @Deprecated
    public static <T extends DataObjectImpl, U extends ItemModel<T>> StringComparisonStatement<T, U> columnEndsWith(ColumnReference column, String value,
            Function<U, String> getColValue) {
        return columnEndsWith(null, column, value, getColValue);
    }
    
    public static <T extends DataObjectImpl, U extends ItemModel<T>> StringComparisonStatement<T, U> columnContains(QueryColumnSelector columnSelector,
            String value, Function<U, String> getColValue) {
        return of(columnSelector, ComparisonOperator.CONTAINS, value, (x, y) -> getColValue.apply(x).contains(y));
    }
    
    /**
     * @deprecated Use {@link #columnContains(scheduler.dao.dml.QueryColumnSelector, java.lang.String, java.util.function.Function)}
     */
    @Deprecated
    public static <T extends DataObjectImpl, U extends ItemModel<T>> StringComparisonStatement<T, U> columnContains(TableReference table, ColumnReference column, String value,
            Function<U, String> getColValue) {
        return of(table, column, ComparisonOperator.CONTAINS, value, getColValue, (x, y) -> x.startsWith(y));
    }
    
    /**
     * @deprecated Use {@link #columnContains(scheduler.dao.dml.QueryColumnSelector, java.lang.String, java.util.function.Function)}
     */
    @Deprecated
    public static <T extends DataObjectImpl, U extends ItemModel<T>> StringComparisonStatement<T, U> columnContains(ColumnReference column, String value,
            Function<U, String> getColValue) {
        return columnContains(null, column, value, getColValue);
    }
}
