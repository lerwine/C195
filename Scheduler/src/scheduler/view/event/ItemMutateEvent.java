package scheduler.view.event;

import javafx.event.Event;
import static javafx.event.Event.ANY;
import javafx.event.EventType;
import scheduler.dao.DataAccessObject;
import scheduler.model.ui.FxRecordModel;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T>
 */
public class ItemMutateEvent<T extends FxRecordModel<? extends DataAccessObject>> extends Event {

    public static final EventType<ItemMutateEvent<? extends FxRecordModel<? extends DataAccessObject>>> ITEM_MUTATE_EVENT = new EventType<>(
            ANY,
            "ITEM_MUTATE_EVENT");

    private final Event fxEvent;
    private boolean canceled;

    protected ItemMutateEvent(T source, EventType<? extends ItemMutateEvent<? extends FxRecordModel<? extends DataAccessObject>>> type, Event fxEvent) {
        super(source, source.dataObject(), type);
        this.fxEvent = fxEvent;
        canceled = false;
    }

    public Event getFxEvent() {
        return fxEvent;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    @Override
    public T getSource() {
        return (T) super.getSource();
    }

    @Override
    public DataAccessObject getTarget() {
        return (DataAccessObject) super.getTarget();
    }

}