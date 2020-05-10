package scheduler.view;

import javafx.scene.Parent;

/**
 * Contains a view and it's controller.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
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

}
