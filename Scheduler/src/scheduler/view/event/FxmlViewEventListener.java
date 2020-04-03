package scheduler.view.event;

import javafx.scene.Parent;

/**
 * Interface for handling {@link FxmlViewEvent}s.
 *
 * @author Leonard T. Erwine (Student ID 356334)
 * @param <T>
 */
public interface FxmlViewEventListener<T extends Parent> {

    /**
     * Handles a {@link FxmlViewEvent}.
     * <p>
     * Do not use the {@link scheduler.view.annotations.HandlesFxmlViewEvent} annotation on implementing methods or else the method may be called
     * twice.</p>
     *
     * @param event The target {@link FxmlViewEvent}.
     */
    void onFxmlViewEvent(FxmlViewEvent<T> event);
}
