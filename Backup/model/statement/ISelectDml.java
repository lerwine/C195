/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.model.statement;

import scheduler.model.schema.IDbColumn;
import scheduler.model.schema.IDbSchema;
import scheduler.model.ResultRow;

/**
 *
 * @author lerwi
 * @param <T> The schema type.
 * @param <U> The result row type.
 */
public interface ISelectDml<T extends IDbSchema<? extends IDbColumn<T>>, U extends ResultRow<T>> extends IFilteredDml<T, Iterable<U>> {
    Iterable<? extends IDbColumn<T>> getSelectColumns();
}
