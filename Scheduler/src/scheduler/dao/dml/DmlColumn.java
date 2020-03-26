/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.dao.dml;

import scheduler.dao.schema.DbColumn;
import scheduler.util.ReadOnlyList;

/**
 *
 * @author lerwi
 */
public interface DmlColumn {

    public static DmlColumn of(DbColumn column, String name, DmlTable owner, ReadOnlyList<DmlTable.Join> joins) {
        return new DmlColumn() {
            @Override
            public DbColumn getDbColumn() {
                return column;
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            public DmlTable getOwner() {
                return owner;
            }

            @Override
            public ReadOnlyList<DmlTable.Join> getJoins() {
                return joins;
            }
            
        };
    }
    DbColumn getDbColumn();
    String getName();
    DmlTable getOwner();
    ReadOnlyList<DmlTable.Join> getJoins();
}
