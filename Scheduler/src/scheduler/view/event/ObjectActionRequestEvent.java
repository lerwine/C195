package scheduler.view.event;

import java.util.Objects;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventType;

/**
 * Represents an item edit or delete request event.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class ObjectActionRequestEvent extends Event {

    private static final long serialVersionUID = -203758390149542530L;

    public static final EventType<ObjectActionRequestEvent> OBJECT_ACTION_REQUEST = new EventType<ObjectActionRequestEvent>(
            ANY,
            "OBJECT_ACTION_REQUEST");

    private final Event fxEvent;
    private final Object item;
    private final boolean delete;

    /**
     * Creates a new {@code ItemActionRequestEvent} object.
     *
     * @param fxEvent The {@link ActionEvent} that initiated this request.
     * @param item The target item.
     * @param isDelete {@code true} if this is a delete event; otherwise, {@code false} if it is an edit event.
     */
    public ObjectActionRequestEvent(Event fxEvent, Object item, boolean isDelete) {
        this(fxEvent, item, isDelete, OBJECT_ACTION_REQUEST);
    }

    protected ObjectActionRequestEvent(Event fxEvent, Object item, boolean isDelete, EventType<? extends ObjectActionRequestEvent> eventType) {
        super(fxEvent.getSource(), fxEvent.getTarget(), Objects.requireNonNull(eventType));
        this.fxEvent = Objects.requireNonNull(fxEvent);
        this.item = Objects.requireNonNull(item);
        delete = isDelete;
    }

    /**
     * Gets a value that indicates whether the event is for a deletion.
     * 
     * @return {@code true} if this is a delete event; otherwise, {@code false} if it is an edit event.
     */
    public boolean isDelete() {
        return delete;
    }

    /**
     * Gets the {@link ActionEvent} that initiated this request.
     * 
     * @return The {@link ActionEvent} that initiated this request.
     */
    public Event getFxEvent() {
        return fxEvent;
    }

    public Object getItem() {
        return item;
    }

}
