package scheduler.view.event;

import java.util.EventObject;
import javafx.event.ActionEvent;

/**
 * Represents an item edit or delete request event.
 * 
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
 * @param <T> The target item type.
 */
public class ItemActionRequestEvent<T> extends EventObject {
    private final ActionEvent fxEvent;
    private final T item;
    private final boolean delete;

    public ActionEvent getFxEvent() {
        return fxEvent;
    }

    public T getItem() {
        return item;
    }

    public boolean isDelete() {
        return delete;
    }
    
    /**
     * Creates a new {@code ItemActionRequestEvent} object.
     * 
     * @param fxEvent The {@link ActionEvent} that initiated this request.
     * @param item The target item.
     * @param isDelete {@code true} if this is a delete event; otherwise, {@code false} if it is an edit event.
     */
    public ItemActionRequestEvent(ActionEvent fxEvent, T item, boolean isDelete) {
        super(fxEvent.getSource());
        this.fxEvent = fxEvent;
        this.item = item;
        delete = isDelete;
    }

}
