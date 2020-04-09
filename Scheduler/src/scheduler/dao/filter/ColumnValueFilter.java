package scheduler.dao.filter;

import scheduler.dao.filter.value.ValueFilter;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.function.Function;
import scheduler.dao.DataAccessObject;
import scheduler.dao.schema.DbColumn;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
 * @param <T>
 * @param <U>
 * @param <S>
 */
public interface ColumnValueFilter<T extends DataAccessObject, U, S extends ValueFilter<U>> extends DaoFilterExpression<T>, Function<T, U> {
    DbColumn getColumn();
    S getValueFilter();
    
    @Override
    public default void appendSimpleDmlConditional(StringBuffer sb) {
        DbColumn column = getColumn();
        String n = column.getDbName().toString();
        if (!n.equals(column.toString())) {
            sb.append(column.getTable().getDbName()).append(".");
        }
        sb.append(n).append(getValueFilter().getOperator()).append("?");
    }

    @Override
    public default void appendJoinedDmlConditional(StringBuffer sb) {
        DbColumn column = getColumn();
        sb.append(column.getTable()).append(".").append(column.getDbName()).append(getValueFilter().getOperator()).append("?");
    }
    
    @Override
    public default int applyWhereParameters(PreparedStatement ps, int index) throws SQLException {
        getValueFilter().accept(ps, index);
        return index + 1;
    }
            
}
