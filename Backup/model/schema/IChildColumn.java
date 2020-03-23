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
 * @param <U>
 */
public interface IChildColumn<T extends IDbSchema<? extends IDbColumn<T>>, U extends IDbColumn<? extends IDbSchema<? super U>>> extends IDbColumn<T> {
    U getParentColumn();
}
