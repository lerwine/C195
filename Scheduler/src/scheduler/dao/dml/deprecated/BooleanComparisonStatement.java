package scheduler.dao.dml.deprecated;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;
import java.util.function.Predicate;
import scheduler.dao.DataObjectImpl;
import scheduler.dao.schema.ValueType;
import scheduler.view.ItemModel;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
 * @param <T> The data access object type.
 * @param <U> The item model type.
 */
public interface BooleanComparisonStatement<T extends DataObjectImpl, U extends ItemModel<T>> extends ColumnComparisonStatement<T, U> {
    
    boolean getValue();

    public static <T extends DataObjectImpl, U extends ItemModel<T>> BooleanComparisonStatement<T, U> of(QueryColumnSelector columnSelector, 
            boolean value, Predicate<U> getColValue) {
        Objects.requireNonNull(getColValue);
        assert columnSelector.getColumn().getType().getValueType() == ValueType.BOOLEAN : "Column type mismatch";
        if (value) {
            return new BooleanComparisonStatement<T, U>() {
                @Override
                public boolean getValue() {
                    return true;
                }

                @Override
                public QueryColumnSelector getColumnSelector() {
                    return columnSelector;
                }

                @Override
                public ComparisonOperator getOperator() {
                    return ComparisonOperator.EQUAL_TO;
                }

                @Override
                public int applyValues(PreparedStatement ps, int currentIndex) throws SQLException {
                    ps.setBoolean(currentIndex, true);
                    return currentIndex + 1;
                }

                @Override
                public boolean test(U t) {
                    return getColValue.test(t);
                }

            };
        }
        return new BooleanComparisonStatement<T, U>() {
            @Override
            public boolean getValue() {
                return false;
            }

            @Override
            public QueryColumnSelector getColumnSelector() {
                return columnSelector;
            }

            @Override
            public ComparisonOperator getOperator() {
                return ComparisonOperator.NOT_EQUAL_TO;
            }

            @Override
            public int applyValues(PreparedStatement ps, int currentIndex) throws SQLException {
                ps.setBoolean(currentIndex, false);
                return currentIndex + 1;
            }

            @Override
            public boolean test(U t) {
                return !getColValue.test(t);
            }

        };
    }

    /**
     * 
     * @param <T>
     * @param <U>
     * @param table
     * @param column
     * @param value
     * @param getColValue
     * @return 
     * @deprecated Use {@link #of(scheduler.dao.dml.QueryColumnSelector, boolean, java.util.function.Predicate)}, instead.
     */
    @Deprecated
    public static <T extends DataObjectImpl, U extends ItemModel<T>> BooleanComparisonStatement<T, U> of(TableReference table, ColumnReference column, boolean value,
            Predicate<U> getColValue) {
        ValueType valueType = column.getColumn().getType().getValueType();
        assert valueType == ValueType.BOOLEAN : "Column type mismatch";
        assert null == table || table.getTable() == column.getColumn().getTable() : "Table/Column mismatch";
        Objects.requireNonNull(getColValue);
        return new BooleanComparisonStatement<T, U>() {
            
            @Override
            public boolean getValue() {
                return value;
            }

            @Override
            public ComparisonOperator getOperator() {
                return ComparisonOperator.EQUAL_TO;
            }

            @Override
            public int applyValues(PreparedStatement ps, int currentIndex) throws SQLException {
                ps.setBoolean(currentIndex, value);
                return currentIndex + 1;
            }

            @Override
            public boolean test(U u) {
                return getColValue.test(u) == value;
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
     * @param value
     * @param getColValue
     * @return 
     * @deprecated Use {@link #of(scheduler.dao.dml.QueryColumnSelector, boolean, java.util.function.Predicate)}, instead.
     */
    @Deprecated
    public static <T extends DataObjectImpl, U extends ItemModel<T>> BooleanComparisonStatement<T, U> of(ColumnReference column, boolean value, Predicate<U> getColValue) {
        return of(null, column, value, getColValue);
    }
}
