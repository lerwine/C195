package scheduler.view.event;

import java.util.EventObject;
import java.util.Objects;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * Represents an event in the view life-cycle of this application.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> The type of {@link Parent} node for the view.
 */
public class FxmlViewEvent<T extends Parent> extends EventObject {

    private static final long serialVersionUID = -6648547745154985742L;
    private final FxmlViewEventType type;
    private final T view;
    private final Stage stage;

    /**
     * Creates a new view life-cycle event object.
     *
     * @param source The source of the event.
     * @param reason The type for the event.
     * @param view The view for the event.
     * @param stage The stage associated with the event.
     */
    public FxmlViewEvent(Object source, FxmlViewEventType reason, T view, Stage stage) {
        super(source);
        this.type = Objects.requireNonNull(reason);
        this.view = Objects.requireNonNull(view);
        if (null != stage) {
            this.stage = stage;
        } else {
            Scene scene = view.getScene();
            if (null != scene) {
                Window window = scene.getWindow();
                if (window instanceof Stage) {
                    this.stage = (Stage) window;
                    return;
                }
            }
            this.stage = null;
        }
    }

    /**
     * Describes the type for the event.
     *
     * @return A {@link FxmlViewEventType} value indicating the type for the event.
     */
    public FxmlViewEventType getType() {
        return type;
    }

    /**
     * Gets the target view.
     *
     * @return The {@link Parent} node for the view.
     */
    public T getView() {
        return view;
    }

    /**
     * Gets the {@link Stage} associated with the event. For {@link FxmlViewEventType#LOADED} events, this will either be the owning {@link Stage} of
     * a one that will be created (for instances where the view is going to be for a new window) or it will be the owning {@link Stage} of the
     * {@link Parent} control that will contain the current view. For {@link FxmlViewEventType#UNLOADED} events, this will either be the owning
     * {@link Stage} of the one that was just closed or it will be the {@link Stage} of the {@link Parent} control that the view was removed from.
     *
     * @return The {@link Stage} associated with the event or {@code null} if it was not possible to determine the associated {@link Stage}.
     */
    public Stage getStage() {
        return stage;
    }

}
