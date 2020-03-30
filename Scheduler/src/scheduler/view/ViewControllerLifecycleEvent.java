package scheduler.view;

import java.util.Objects;
import javafx.scene.Parent;
import javafx.stage.Stage;

/**
 *
 * @author lerwi
 * @param <T>
 * @param <U>
 */
public class ViewControllerLifecycleEvent<T extends Parent, U> extends ViewLifecycleEvent<T> implements ViewAndController<T, U> {
    private final U controller;

    @Override
    public U getController() {
        return controller;
    }
    
    public ViewControllerLifecycleEvent(Object source, ViewLifecycleEventReason type, T view, U controller, Stage stage) {
        super(source, type, view, stage);
        this.controller = Objects.requireNonNull(controller);
    }

    public ViewControllerLifecycleEvent(Object source, ViewLifecycleEventReason type, T view, U controller) {
        this(source, type, view, controller, null);
    }
}
