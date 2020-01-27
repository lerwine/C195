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
public abstract class ItemController<M extends ItemModel<?>> extends SchedulerController implements Consumer<EditItem<M>>, Function<EditItem<M>, Boolean> {
    public boolean isValid() { return validProperty().get(); }
    public abstract BooleanExpression validProperty();  
    public void afterCloseDialog(EditItem.ShowAndWaitResult<M> result) { }
    public void onError(EditItem.ShowAndWaitResult<M> result) {
        Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, result.getFault());
    }
}
