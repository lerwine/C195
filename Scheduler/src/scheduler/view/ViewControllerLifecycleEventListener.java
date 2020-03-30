package scheduler.view;

import javafx.scene.Parent;

/**
 * Interface for handling {@link ViewControllerLifecycleEvent}s.
 * 
 * @author lerwi
 * @param <T>
 * @param <U>
 */
@FunctionalInterface
public interface ViewControllerLifecycleEventListener<T extends Parent, U> {
    
    /**
     * Handles a {@link ViewControllerLifecycleEvent}.
     * Do not use the {@link scheduler.view.annotations.HandlesViewLifecycleEvent} annotation on implementing methods or else
     * the method may be called twice.
     * 
     * @param event The target {@link ViewControllerLifecycleEvent}.
     */
    void onViewControllerLifecycleEvent(ViewControllerLifecycleEvent<T, U> event);
}
