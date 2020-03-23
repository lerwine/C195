/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.model.statement;

import scheduler.model.schema.DbTable;

/**
 *
 * @author lerwi
 * @param <T> The schema type.
 */
public interface IUpdateDml<T extends DbTable<? extends DbTable.DbColumn<T>>> extends IFilteredDml<T, Integer> {
    
}
