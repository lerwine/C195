/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.model;

/**
 *
 * @author lerwi
 */
public class SelectStatement<T extends IDataRow> {
    private final SelectStatementTable<T> table;
    public SelectStatement(SelectStatementTable<T> table) {
        this.table = table;
    }
    private SelectStatementTable<? extends IDataRow> findTable(JoinedSelectTable<? extends IDataRow> current, String name) {
        
        if (current.getTableName().equalsIgnoreCase(name))
            return current;
        if (current instanceof )
    }
    public final SelectStatementTable<? extends IDataRow> findTable(String name) {
        if (table.getTableName().equalsIgnoreCase(name))
            return table;
        if (table instanceof JoinedSelectTable)
            return findTable((JoinedSelectTable<? extends IDataRow>) table, name);
        return null;
    }
}
