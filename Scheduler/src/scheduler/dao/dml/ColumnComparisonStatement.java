/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.dao.dml;

import scheduler.dao.DataObject;
import scheduler.dao.schema.DbColumn;

/**
 *
 * @author lerwi
 */
interface ColumnComparisonStatement<T extends DataObject> extends ComparisonStatement<T> {
    
    ColumnReference getColumn();
    
    TableReference getTable();
    
    @Override
    public default void appendSqlStatement(StringBuilder stringBuilder) {
        TableReference tr = getTable();
        String a;
        if (null != tr) {
            a = tr.getName();
            if (a.equalsIgnoreCase(tr.getTableName().getDbName().getValue()))
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
