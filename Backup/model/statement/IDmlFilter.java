/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.model.statement;

import scheduler.model.schema.IDbColumn;
import scheduler.model.schema.IDbSchema;

/**
 *
 * @author lerwi
 * @param <T> The schema type.
 */
public interface IDmlFilter<T extends IDbSchema<? extends IDbColumn<T>>> {
    
}
