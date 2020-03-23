package scheduler.dao.dml;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;
import java.util.function.Predicate;
import scheduler.dao.DataObject;
import scheduler.dao.schema.ValueType;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
 * @param <T>
 */
public interface BooleanComparisonStatement<T extends DataObject> extends ColumnComparisonStatement<T> {
    
    boolean getValue();
    
    public static <T extends DataObject> BooleanComparisonStatement<T> of(TableReference table, ColumnReference column, boolean value,
            Predicate<T> getColValue) {
        ValueType valueType = column.getColumn().getType().getValueType();
        assert valueType == ValueType.BOOLEAN : "Column type mismatch";
        assert null == table || table.getTableName() == column.getColumn().getTable() : "Table/Column mismatch";
        Objects.requireNonNull(getColValue);
        return new BooleanComparisonStatement<T>() {
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
            public boolean test(T t) {
                return getColValue.test(t) == value;
            }

        };
    }
    
    public static <T extends DataObject> BooleanComparisonStatement<T> of(ColumnReference column, boolean value, Predicate<T> getColValue) {
        return of(null, column, value, getColValue);
    }
}
