package scheduler.events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.CustomerDAO;
import scheduler.model.ui.CustomerModel;

public final class CustomerSuccessEvent extends CustomerEvent {

    private static final String BASE_EVENT_NAME = "SCHEDULER_CUSTOMER_SUCCESS_EVENT";

    /**
     * Base {@link EventType} for all {@code CustomerSuccessEvent}s.
     */
    public static final EventType<CustomerSuccessEvent> SUCCESS_EVENT_TYPE = new EventType<>(OP_EVENT_TYPE, BASE_EVENT_NAME);

    /**
     * {@link EventType} for save {@code CustomerSuccessEvent}s.
     */
    public static final EventType<CustomerSuccessEvent> SAVE_SUCCESS = new EventType<>(SUCCESS_EVENT_TYPE, "SCHEDULER_CUSTOMER_SAVE_SUCCESS");

    /**
     * {@link EventType} for delete {@code CustomerSuccessEvent}s.
     */
    public static final EventType<CustomerSuccessEvent> DELETE_SUCCESS = new EventType<>(SUCCESS_EVENT_TYPE, "SCHEDULER_CUSTOMER_DELETE_SUCCESS");

    private static EventType<CustomerSuccessEvent> assertValidEventType(EventType<CustomerSuccessEvent> eventType) {
        if (eventType.getName().equals(BASE_EVENT_NAME)) {
            throw new IllegalArgumentException();
        }
        return eventType;
    }

    public CustomerSuccessEvent(CustomerEvent event, Object source, EventTarget target, EventType<CustomerSuccessEvent> eventType) {
        super(event, source, target, assertValidEventType(eventType));
    }

    public CustomerSuccessEvent(CustomerEvent event, EventType<CustomerSuccessEvent> eventType) {
        super(event, assertValidEventType(eventType));
    }

    public CustomerSuccessEvent(CustomerModel fxRecordModel, Object source, EventTarget target, EventType<CustomerSuccessEvent> eventType) {
        super(fxRecordModel, source, target, assertValidEventType(eventType));
    }

    public CustomerSuccessEvent(CustomerDAO dao, Object source, EventTarget target, EventType<CustomerSuccessEvent> eventType) {
        super(dao, source, target, assertValidEventType(eventType));
    }

}
