/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.model.schema;

/**
 *
 * @author lerwi
 * @param <T>
 */
public interface IDbColumn<T extends IDbSchema<? extends IDbColumn<T>>> {
    T getSchema();
    String getName();
    DbColType getType();
}
