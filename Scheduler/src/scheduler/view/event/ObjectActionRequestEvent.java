package scheduler.view.event;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventType;

/**
 * Represents an item edit or delete request event.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class ObjectActionRequestEvent extends Event {

    public static final EventType<ObjectActionRequestEvent> OBJECT_ACTION_REQUEST =
            new EventType<ObjectActionRequestEvent>(Event.ANY, "OBJECT_ACTION_REQUEST");

    private final ActionEvent fxEvent;
    private final Object item;

    /**
     * Creates a new {@code ItemActionRequestEvent} object.
     *
     * @param fxEvent The {@link ActionEvent} that initiated this request.
     * @param item The target item.
     * @param isDelete {@code true} if this is a delete event; otherwise, {@code false} if it is an edit event.
     */
    public ObjectActionRequestEvent(ActionEvent fxEvent, Object item, boolean isDelete) {
        super(fxEvent.getSource(), fxEvent.getTarget(), OBJECT_ACTION_REQUEST);
        this.fxEvent = fxEvent;
        this.item = item;
    }

    public ActionEvent getFxEvent() {
        return fxEvent;
    }

    public Object getItem() {
        return item;
    }

}
