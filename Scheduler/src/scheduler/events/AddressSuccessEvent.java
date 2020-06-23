package scheduler.events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.AddressDAO;
import scheduler.model.ui.AddressModel;

public final class AddressSuccessEvent extends AddressEvent {

    private static final long serialVersionUID = -4782268395905350399L;

    private static final String BASE_EVENT_NAME = "SCHEDULER_ADDRESS_SUCCESS_EVENT";

    /**
     * Base {@link EventType} for all {@code AddressSuccessEvent}s.
     */
    public static final EventType<AddressSuccessEvent> SUCCESS_EVENT_TYPE = new EventType<>(OP_EVENT_TYPE, BASE_EVENT_NAME);

    /**
     * {@link EventType} for save {@code AddressSuccessEvent}s.
     */
    public static final EventType<AddressSuccessEvent> SAVE_SUCCESS = new EventType<>(SUCCESS_EVENT_TYPE, "SCHEDULER_ADDRESS_SAVE_SUCCESS");

    /**
     * {@link EventType} for delete {@code AddressSuccessEvent}s.
     */
    public static final EventType<AddressSuccessEvent> DELETE_SUCCESS = new EventType<>(SUCCESS_EVENT_TYPE, "SCHEDULER_ADDRESS_DELETE_SUCCESS");

    private static EventType<AddressSuccessEvent> assertValidEventType(EventType<AddressSuccessEvent> eventType) {
        if (eventType.getName().equals(BASE_EVENT_NAME)) {
            throw new IllegalArgumentException();
        }
        return eventType;
    }

    public AddressSuccessEvent(AddressEvent event, Object source, EventTarget target, EventType<AddressSuccessEvent> eventType) {
        super(event, source, target, assertValidEventType(eventType));
    }

    public AddressSuccessEvent(AddressEvent event, EventType<AddressSuccessEvent> eventType) {
        super(event, assertValidEventType(eventType));
    }

    public AddressSuccessEvent(AddressModel fxRecordModel, Object source, EventTarget target, EventType<AddressSuccessEvent> eventType) {
        super(fxRecordModel, source, target, assertValidEventType(eventType));
    }

    public AddressSuccessEvent(AddressDAO dao, Object source, EventTarget target, EventType<AddressSuccessEvent> eventType) {
        super(dao, source, target, assertValidEventType(eventType));
    }

}
