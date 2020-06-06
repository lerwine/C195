package scheduler.view.event;

import javafx.event.Event;
import javafx.event.EventType;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> The target item type.
 */
public final class ItemActionRequestEvent<T> extends ObjectActionRequestEvent {

    private static final long serialVersionUID = -680774297349983367L;

    public static final EventType<ObjectActionRequestEvent> ITEM_ACTION_REQUEST = new EventType<ObjectActionRequestEvent>(
            OBJECT_ACTION_REQUEST,
            "ITEM_ACTION_REQUEST");

    public static final EventType<ObjectActionRequestEvent> ITEM_EDIT_REQUEST = new EventType<ObjectActionRequestEvent>(ITEM_ACTION_REQUEST,
            "ITEM_EDIT_REQUEST");

    public static final EventType<ObjectActionRequestEvent> ITEM_DELETE_REQUEST = new EventType<ObjectActionRequestEvent>(ITEM_ACTION_REQUEST,
            "ITEM_DELETE_REQUEST");

    public ItemActionRequestEvent(Event fxEvent, T item, boolean isDelete) {
        super(fxEvent, item, isDelete, (isDelete) ? ITEM_DELETE_REQUEST : ITEM_EDIT_REQUEST);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getItem() {
        return (T) super.getItem();
    }

}
