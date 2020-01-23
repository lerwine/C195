/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.view;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.BooleanExpression;

/**
 * Defines a controller for editing an {@link ItemModel}.
 * @author Leonard T. Erwine
 * @param <M>
 */
public interface ItemController<M extends ItemModel<?>> extends Consumer<EditItem<M>>, Function<EditItem<M>, Boolean> {
    boolean isValid();
    BooleanExpression validProperty();  
    default void afterCloseDialog(EditItem.ShowAndWaitResult<M> result) { }
    default void onError(EditItem.ShowAndWaitResult<M> result) {
        Logger.getLogger(EditItem.class.getName()).log(Level.SEVERE, null, result.getFault());
    }
}
