package scheduler.view;

import javafx.scene.Parent;

/**
 * Interface for handling {@link ViewLifecycleEvent}s.
 *
 * @author lerwi
 * @param <T>
 */
public interface ViewLifecycleEventListener<T extends Parent> {
    
    /**
     * Handles a {@link ViewLifecycleEvent}.
     * Do not use the {@link scheduler.view.annotations.HandlesViewLifecycleEvent} annotation on implementing methods or else
     * the method may be called twice.
     * 
     * @param event The target {@link ViewLifecycleEvent}.
     */
    void onViewLifecycleEvent(ViewLifecycleEvent<T> event);
}
