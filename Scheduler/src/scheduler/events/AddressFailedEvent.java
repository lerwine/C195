package scheduler.events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.AddressDAO;
import scheduler.model.ui.AddressModel;

public final class AddressFailedEvent extends AddressEvent implements ModelFailedEvent<AddressDAO, AddressModel> {

    private static final long serialVersionUID = 3057696288962212229L;

    private static final String BASE_EVENT_NAME = "SCHEDULER_ADDRESS_FAILED_EVENT";

    /**
     * Base {@link EventType} for all {@code AddressFailedEvent}s.
     */
    public static final EventType<AddressFailedEvent> FAILED_EVENT_TYPE = new EventType<>(OP_EVENT_TYPE, BASE_EVENT_NAME);

    /**
     * {@link EventType} for save {@code AddressFailedEvent}s.
     */
    public static final EventType<AddressFailedEvent> SAVE_FAILED = new EventType<>(FAILED_EVENT_TYPE, "SCHEDULER_ADDRESS_SAVE_FAILED");

    /**
     * {@link EventType} for delete {@code AddressFailedEvent}s.
     */
    public static final EventType<AddressFailedEvent> DELETE_FAILED = new EventType<>(FAILED_EVENT_TYPE, "SCHEDULER_ADDRESS_DELETE_FAILED");

    private static EventType<AddressFailedEvent> assertValidEventType(EventType<AddressFailedEvent> eventType) {
        if (eventType.getName().equals(BASE_EVENT_NAME)) {
            throw new IllegalArgumentException();
        }
        return eventType;
    }

    private final String message;
    private final Throwable fault;
    private final boolean canceled;

    public AddressFailedEvent(AddressEvent event, String message, Throwable fault, Object source, EventTarget target, EventType<AddressFailedEvent> eventType) {
        super(event, source, target, assertValidEventType(eventType));
        if ((null == message || message.trim().isEmpty()) && (null == fault || null == (message = fault.getMessage()) || message.trim().isEmpty())) {
            this.message = "Unknown error";
        } else {
            this.message = message;
        }
        this.fault = fault;
        canceled = false;
    }

    public AddressFailedEvent(AddressEvent event, boolean canceled, Object source, EventTarget target, EventType<AddressFailedEvent> eventType) {
        super(event, source, target, assertValidEventType(eventType));
        message = (canceled) ? "Operation canceled" : "Unknown error";
        fault = null;
        this.canceled = canceled;
    }

    public AddressFailedEvent(AddressEvent event, String message, Throwable fault, EventType<AddressFailedEvent> eventType) {
        super(event, assertValidEventType(eventType));
        if ((null == message || message.trim().isEmpty()) && (null == fault || null == (message = fault.getMessage()) || message.trim().isEmpty())) {
            this.message = "Unknown error";
        } else {
            this.message = message;
        }
        this.fault = fault;
        canceled = false;
    }

    public AddressFailedEvent(AddressEvent event, boolean canceled, EventType<AddressFailedEvent> eventType) {
        super(event, assertValidEventType(eventType));
        message = (canceled) ? "Operation canceled" : "Unknown error";
        fault = null;
        this.canceled = canceled;
    }

    public AddressFailedEvent(AddressModel fxRecordModel, String message, Throwable fault, Object source, EventTarget target, EventType<AddressFailedEvent> eventType) {
        super(fxRecordModel, source, target, assertValidEventType(eventType));
        if ((null == message || message.trim().isEmpty()) && (null == fault || null == (message = fault.getMessage()) || message.trim().isEmpty())) {
            this.message = "Unknown error";
        } else {
            this.message = message;
        }
        this.fault = fault;
        canceled = false;
    }

    public AddressFailedEvent(AddressModel fxRecordModel, boolean canceled, Object source, EventTarget target, EventType<AddressFailedEvent> eventType) {
        super(fxRecordModel, source, target, assertValidEventType(eventType));
        message = (canceled) ? "Operation canceled" : "Unknown error";
        fault = null;
        this.canceled = canceled;
    }

    public AddressFailedEvent(AddressDAO dao, String message, Throwable fault, Object source, EventTarget target, EventType<AddressFailedEvent> eventType) {
        super(dao, source, target, assertValidEventType(eventType));
        if ((null == message || message.trim().isEmpty()) && (null == fault || null == (message = fault.getMessage()) || message.trim().isEmpty())) {
            this.message = "Unknown error";
        } else {
            this.message = message;
        }
        this.fault = fault;
        canceled = false;
    }

    public AddressFailedEvent(AddressDAO dao, boolean canceled, Object source, EventTarget target, EventType<AddressFailedEvent> eventType) {
        super(dao, source, target, assertValidEventType(eventType));
        message = (canceled) ? "Operation canceled" : "Unknown error";
        fault = null;
        this.canceled = canceled;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Throwable getFault() {
        return fault;
    }

    @Override
    public boolean isCanceled() {
        return canceled;
    }

}
