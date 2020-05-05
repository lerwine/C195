package scheduler.view.event;

import javafx.event.ActionEvent;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> The target item type.
 */
public class ItemActionRequestEvent<T> extends ObjectActionRequestEvent {
    private final boolean delete;

    public ItemActionRequestEvent(ActionEvent fxEvent, T item, boolean isDelete) {
        super(fxEvent, item, isDelete);
        delete = isDelete;
    }

    @Override
    public T getItem() {
        return (T)super.getItem();
    }

    public boolean isDelete() {
        return delete;
    }

}
