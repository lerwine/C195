package scheduler.view.event;

import java.util.Objects;
import javafx.scene.Parent;
import javafx.stage.Stage;
import scheduler.view.ViewAndController;

/**
 * Represents an FXML view/controller loading event.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> The type of {@link Parent} at the root of the view.
 * @param <U> The type of controller for the view.
 */
public class FxmlViewControllerEvent<T extends Parent, U> extends FxmlViewEvent<T> implements ViewAndController<T, U> {

    private static final long serialVersionUID = -3622723151229766163L;
    private final U controller;

    /**
     * Initializes a new FXML view/controller loading event object.
     *
     * @param source The object on which the {@code FxmlViewControllerEvent} initially occurred.
     * @param type The {@link FxmlViewEventType} value indicating the type of event that occurred.
     * @param view The {@link Parent} at the root of the view.
     * @param controller The instantiated controller for the view.
     * @param stage The {@link Stage} that is related to the event.
     */
    public FxmlViewControllerEvent(Object source, FxmlViewEventType type, T view, U controller, Stage stage) {
        super(source, type, view, stage);
        this.controller = Objects.requireNonNull(controller);
    }

    /**
     * Initializes a new FXML view/controller loading event object with no defined {@link Stage}.
     *
     * @param source The object on which the {@code FxmlViewControllerEvent} initially occurred.
     * @param type The {@link FxmlViewEventType} value indicating the type of event that occurred.
     * @param view The {@link Parent} at the root of the view.
     * @param controller The instantiated controller for the view.
     */
    public FxmlViewControllerEvent(Object source, FxmlViewEventType type, T view, U controller) {
        this(source, type, view, controller, null);
    }

    @Override
    public U getController() {
        return controller;
    }

}
