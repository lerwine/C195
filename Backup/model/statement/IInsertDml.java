/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.model.statement;

import scheduler.model.schema.DbTable;
import scheduler.model.TableRow;

/**
 *
 * @author lerwi
 * @param <T> The schema type.
 * @param <U> The source row type.
 */
public interface IInsertDml<T extends DbTable<? extends DbTable.DbColumn<T>>, U extends TableRow<T>> extends IDml<T, Boolean> {
}
