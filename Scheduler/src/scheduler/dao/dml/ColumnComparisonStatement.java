package scheduler.dao.dml;

import scheduler.dao.DataObjectImpl;
import scheduler.view.ItemModel;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
 * @param <T> The data access object type.
 * @param <U> The item model type.
 */
public interface ColumnComparisonStatement<T extends DataObjectImpl, U extends ItemModel<T>> extends ComparisonStatement<T, U> {
    
    ColumnReference getColumn();
    
    TableReference getTable();
    
    @Override
    public default void appendSqlStatement(StringBuilder stringBuilder) {
        TableReference tr = getTable();
        String a;
        if (null != tr) {
            a = tr.getTableAlias();
            if (a.equalsIgnoreCase(tr.getTable().getDbName().getValue()))
                stringBuilder.append("`").append(a).append("`.");
            else
                stringBuilder.append(a).append(".");
        }
        ColumnReference cr = getColumn();
        a = cr.getName();
        if (a.equalsIgnoreCase(cr.getColumn().getDbName().getValue()))
            stringBuilder.append("`").append(a).append("` ");
        else
            stringBuilder.append(a).append(" ");
        stringBuilder.append(getOperator().toString()).append(" ").append("?");
    }

}
