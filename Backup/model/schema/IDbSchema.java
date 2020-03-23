/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.model.schema;

import java.util.List;

/**
 *
 * @author lerwi
 * @param <T>
 */
public interface IDbSchema<T extends IDbColumn<? extends IDbSchema<T>>> extends List<T> {
    String getTableName();
}
