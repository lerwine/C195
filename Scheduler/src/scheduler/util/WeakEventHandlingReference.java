package scheduler.util;

import java.util.Objects;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.WeakEventHandler;

/**
 * Contains a reference to the original {@link EventHandler} and exposes an associated {@link WeakEventHandler}. This keeps the original {@link EventHandler} from being
 * garbage-collected until the owning class is garbage-collected, while allowing it to be referenced as a {@link WeakEventHandler}.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> The event class for the event handler this class references.
 */
public abstract class WeakEventHandlingReference<T extends Event> implements EventHandler<T> {
    public static <T extends Event> WeakEventHandlingReference<T> create(EventHandler<T> eventHandler) {
        return new WeakEventHandlingReference<T>(Objects.requireNonNull(eventHandler)) {
            private final EventHandler<T> _eventHandler = eventHandler;
            
            @Override
            public void handle(T event) {
                _eventHandler.handle(event);
            }
        };
    }

    private final WeakEventHandler<T> weakEventHandler;

    private WeakEventHandlingReference(EventHandler<T> eventHandler) {
        weakEventHandler = new WeakEventHandler<>(eventHandler);
    }

    protected WeakEventHandlingReference() {
        weakEventHandler = new WeakEventHandler<>(this);
    }

    public WeakEventHandler<T> getWeakEventHandler() {
        return weakEventHandler;
    }

}
