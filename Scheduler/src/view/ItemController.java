/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.BooleanExpression;
import scheduler.dao.DataObjectImpl;

/**
 *
 * @author Leonard T. Erwine
 * @param <R>
 */
public interface ItemController<R extends DataObjectImpl> extends Consumer<EditItem<R>>, Function<EditItem<R>, Boolean> {
    boolean isValid();
    BooleanExpression validProperty();  
    default void afterCloseDialog(EditItem.ShowAndWaitResult<R> result) { }
    default void onError(EditItem.ShowAndWaitResult<R> result) {
        Logger.getLogger(EditItem.class.getName()).log(Level.SEVERE, null, result.getFault());
    }
}
