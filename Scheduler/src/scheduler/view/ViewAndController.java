/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.view;

import javafx.scene.Parent;
import javafx.stage.Stage;

/**
 * Contains a view and it's controller.
 * 
 * @author lerwi
 * @param <T> The type of {@link Parent} node for the view.
 * @param <U> The type of controller for the view.
 */
public interface ViewAndController<T extends Parent, U> {
    /**
     * Gets the {@link Parent} node for the view.
     * 
     * @return The {@link Parent} node of the view.
     */
    T getView();
    
    /**
     * Gets the controller for the view.
     * 
     * @return The controller for the view.
     */
    U getController();
    
    default ViewControllerLifecycleEvent<T, U> toEvent(Object source, ViewLifecycleEventReason type, Stage stage) {
        if (this instanceof ViewControllerLifecycleEvent)
            return (ViewControllerLifecycleEvent)this;
        return new ViewControllerLifecycleEvent<>(source, type, getView(), getController(), stage);
    }
}
