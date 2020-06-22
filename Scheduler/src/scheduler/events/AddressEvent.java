package scheduler.events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.AddressDAO;
import scheduler.model.ui.AddressModel;

public abstract class AddressEvent extends ModelEvent<AddressDAO, AddressModel> {

    /**
     * Base {@link EventType} for all {@code AddressEvent}s.
     */
    public static final EventType<AddressEvent> ADDRESS_EVENT_TYPE = new EventType<>(MODEL_EVENT_TYPE, "SCHEDULER_ADDRESS_EVENT");

    /**
     * Base {@link EventType} for all operational {@code AddressEvent}s.
     */
    public static final EventType<AddressEvent> OP_EVENT_TYPE = new EventType<>(ADDRESS_EVENT_TYPE, "SCHEDULER_ADDRESS_OP_EVENT");

    protected AddressEvent(AddressEvent event, Object source, EventTarget target, EventType<? extends AddressEvent> eventType) {
        super(event, source, target, eventType);
    }

    protected AddressEvent(AddressEvent event, EventType<? extends AddressEvent> eventType) {
        super(event, eventType);
    }

    protected AddressEvent(AddressModel fxRecordModel, Object source, EventTarget target, EventType<? extends AddressEvent> eventType) {
        super(fxRecordModel, source, target, eventType);
    }

    protected AddressEvent(AddressDAO dao, Object source, EventTarget target, EventType<? extends AddressEvent> eventType) {
        super(dao, source, target, eventType);
    }

}
