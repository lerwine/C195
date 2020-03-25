package scheduler.dao.dml;

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
    
    public static <T extends DataObjectImpl, U extends ItemModel<T>> BooleanComparisonStatement<T, U> of(TableReference table, ColumnReference column, boolean value,
            Predicate<ItemModel<T>> getColValue) {
        ValueType valueType = column.getColumn().getType().getValueType();
        assert valueType == ValueType.BOOLEAN : "Column type mismatch";
        assert null == table || table.getTable() == column.getColumn().getTable() : "Table/Column mismatch";
        Objects.requireNonNull(getColValue);
        return new BooleanComparisonStatement<T, U>() {
            @Override
            public ColumnReference getColumn() {
                return column;
            }

            @Override
            public TableReference getTable() {
                return table;
            }
            
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

        };
    }
    
    public static <T extends DataObjectImpl, U extends ItemModel<T>> BooleanComparisonStatement<T, U> of(ColumnReference column, boolean value, Predicate<ItemModel<T>> getColValue) {
        return of(null, column, value, getColValue);
    }
}
