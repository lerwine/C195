package scheduler.view.event;

import java.util.Objects;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.Node;

/**
 * An event that affects a view {@link Node} for a controller.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> The target node type.
 * @param <U> The controller type.
 */
public abstract class SchedulerNodeEvent<T extends Node, U> extends Event {

    private static final long serialVersionUID = 5773445431243864055L;

    /**
     * The base event type for all {@code SchedulerNodeEvent}s.
     */
    public static final EventType<? extends SchedulerNodeEvent<? extends Node, ?>> ANY_NODE = new EventType<>(ANY, "SCHEDULER_ANY_NODE");

    private final U controller;

    /**
     * Construct a new {@code SchedulerNodeEvent} with the specified event source, view (target), controller and event type.
     *
     * @param source The object which sent the event.
     * @param view The view {@link Node}.
     * @param controller The controller object.
     * @param eventType The event type.
     */
    protected SchedulerNodeEvent(Object source, T view, U controller, EventType<? extends SchedulerNodeEvent<T, U>> eventType) {
        super(source, Objects.requireNonNull(view), Objects.requireNonNull(eventType));
        this.controller = Objects.requireNonNull(controller);
    }

    /**
     * Gets the controller object.
     *
     * @return The controller object.
     */
    public U getController() {
        return controller;
    }

    /**
     * Gets the target {@link Node} representing the view.
     *
     * @return The target {@link Node} representing the view.
     */
    @Override
    @SuppressWarnings("unchecked")
    public T getTarget() {
        return (T) super.getTarget();
    }

}
