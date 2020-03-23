/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.model;

import java.util.Collections;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class SelectStatementTableImpl<T extends IDataRow> implements SelectStatementTable<T> {
    private final String tableName;
    private final List<SelectStatementColumn> columns;
    public SelectStatementTableImpl(String tableName, Iterator<SelectStatementColumn> columns) {
        ArrayList<SelectStatementColumn> backingColumns = new ArrayList<>();
        do {
            SelectStatementColumn col = columns.next();
            if (!backingColumns.stream().anyMatch((t) -> SelectStatementColumn.areEqual(t, col)))
                backingColumns.add(col);
        } while (columns.hasNext());
        this.tableName = tableName;
        this.columns = Collections.unmodifiableList(backingColumns);
    }
    
    @Override
    public String getTableName() {
        return tableName;
    }

    @Override
    public List<SelectStatementColumn> getColumns() {
        return columns;
    }

    @Override
    public StringBuilder toSelectSql() {
        StringBuilder result = new StringBuilder("SELECT `");
        String cn = columns.get(0).get();
        result.append(cn);
        String an = columns.get(0).getAlias();
        if (null == an || an.trim().isEmpty() || an.equals(cn))
            result.append("`");
        else
            result.append("` AS ").append(an);
        columns.stream().skip(1L).forEach((t) -> {
            String c = t.get();
            result.append(", `").append(cn);
            String a = t.getAlias();
            if (null == a || a.trim().isEmpty() || a.equals(c))
                result.append("`");
            else
                result.append("` AS ").append(a);
        });
        
        return result.append(" FROM `").append(tableName).append("`");
    }
    
}
