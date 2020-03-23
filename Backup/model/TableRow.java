/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.model;

import scheduler.model.schema.DbTable;

/**
 *
 * @author lerwi
 * @param <T>
 */
public abstract class TableRow<T extends DbTable<? extends DbTable.DbColumn<T>>> implements ResultRow<T> {    

}
