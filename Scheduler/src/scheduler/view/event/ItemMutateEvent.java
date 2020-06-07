package scheduler.view.event;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.DataAccessObject;
import scheduler.model.ui.FxRecordModel;

/**
 * Base class for {@link FxRecordModel} save and delete events.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> The {@link FxRecordModel} type.
 */
public class ItemMutateEvent<T extends FxRecordModel<? extends DataAccessObject>> extends Event {

    private static final long serialVersionUID = -3123630331690153612L;

    public static final EventType<ItemMutateEvent<? extends FxRecordModel<? extends DataAccessObject>>> ITEM_MUTATE_EVENT = new EventType<>(
            ANY,
            "ITEM_MUTATE_EVENT");

    private final Event fxEvent;
    private boolean canceled;

    protected ItemMutateEvent(T source, EventTarget target,
            EventType<? extends ItemMutateEvent<? extends FxRecordModel<? extends DataAccessObject>>> type, Event fxEvent) {
        super(source, target, type);
        this.fxEvent = fxEvent;
        canceled = false;
    }

    /**
     * Gets the {@link ActionEvent} that initiated this event.
     * 
     * @return The {@link ActionEvent} that initiated this event.
     */
    public Event getFxEvent() {
        return fxEvent;
    }

    /**
     * Gets a value that indicates whether the represented save or delete operation is to be canceled.
     * 
     * @return {@code true} if the represented save or delete operation is to be canceled; otherwise {@code false}.
     */
    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getSource() {
        return (T) super.getSource();
    }

}
