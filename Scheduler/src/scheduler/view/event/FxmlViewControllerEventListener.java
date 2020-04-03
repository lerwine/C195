package scheduler.view.event;

import javafx.scene.Parent;

/**
 * Interface for handling {@link FxmlViewControllerEvent}s.
 *
 * @author Leonard T. Erwine (Student ID 356334)
 * @param <T> The type of {@link Parent} at the root of the view.
 * @param <U> The type of controller for the view.
 */
@FunctionalInterface
public interface FxmlViewControllerEventListener<T extends Parent, U> {

    /**
     * Handles a {@link FxmlViewControllerEvent}.
     * <p>
     * Do not use the {@link scheduler.view.annotations.HandlesFxmlViewEvent} annotation on classes that implement this method or else the method may
     * be called twice.</p>
     *
     * @param event The {@link FxmlViewControllerEvent} that occurred.
     */
    void onFxmlViewControllerEvent(FxmlViewControllerEvent<T, U> event);
}
