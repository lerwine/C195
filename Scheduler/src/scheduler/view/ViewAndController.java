package scheduler.view;

import javafx.scene.Parent;
import javafx.stage.Stage;
import scheduler.view.event.FxmlViewControllerEvent;
import scheduler.view.event.FxmlViewEventType;

/**
 * Contains a view and it's controller.
 *
 * @author Leonard T. Erwine (Student ID 356334)
 * @param <T> The type of {@link Parent} at the root of the view.
 * @param <U> The type of controller for the view.
 */
public interface ViewAndController<T extends Parent, U> {

    /**
     * Gets the {@link Parent} at the root of the view.
     *
     * @return The {@link Parent} at the root of the view.
     */
    T getView();

    /**
     * Gets the controller for the view.
     *
     * @return The controller for the view.
     */
    U getController();

    /**
     * Creates an {@link FxmlViewControllerEvent} from the current view and controller.
     *
     * @param source The object on which the {@code DataObjectEvent} initially occurred.
     * @param type The {@link FxmlViewEventType} value indicating the type of event that occurred.
     * @param stage The {@link Stage} that is related to the event.
     * @return A new {@link FxmlViewControllerEvent} containing the current view and controller.
     */
    default FxmlViewControllerEvent<T, U> toEvent(Object source, FxmlViewEventType type, Stage stage) {
        if (this instanceof FxmlViewControllerEvent) {
            return (FxmlViewControllerEvent<T, U>) this;
        }
        return new FxmlViewControllerEvent<>(source, type, getView(), getController(), stage);
    }
}
