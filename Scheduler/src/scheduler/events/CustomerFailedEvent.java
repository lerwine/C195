package scheduler.events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.CustomerDAO;
import scheduler.model.ui.CustomerModel;

public final class CustomerFailedEvent extends CustomerEvent implements ModelFailedEvent<CustomerDAO, CustomerModel> {

    private static final long serialVersionUID = -7166159668722232485L;

    private static final String BASE_EVENT_NAME = "SCHEDULER_CUSTOMER_FAILED_EVENT";

    /**
     * Base {@link EventType} for all {@code CustomerFailedEvent}s.
     */
    public static final EventType<CustomerFailedEvent> FAILED_EVENT_TYPE = new EventType<>(OP_EVENT_TYPE, BASE_EVENT_NAME);

    /**
     * {@link EventType} for save {@code CustomerFailedEvent}s.
     */
    public static final EventType<CustomerFailedEvent> SAVE_FAILED = new EventType<>(FAILED_EVENT_TYPE, "SCHEDULER_CUSTOMER_SAVE_FAILED");

    /**
     * {@link EventType} for delete {@code CustomerFailedEvent}s.
     */
    public static final EventType<CustomerFailedEvent> DELETE_FAILED = new EventType<>(FAILED_EVENT_TYPE, "SCHEDULER_CUSTOMER_DELETE_FAILED");

    private static EventType<CustomerFailedEvent> assertValidEventType(EventType<CustomerFailedEvent> eventType) {
        if (eventType.getName().equals(BASE_EVENT_NAME)) {
            throw new IllegalArgumentException();
        }
        return eventType;
    }

    private final String message;
    private final Throwable fault;
    private final boolean canceled;

    public CustomerFailedEvent(CustomerEvent event, String message, Throwable fault, Object source, EventTarget target, EventType<CustomerFailedEvent> eventType) {
        super(event, source, target, assertValidEventType(eventType));
        if ((null == message || message.trim().isEmpty()) && (null == fault || null == (message = fault.getMessage()) || message.trim().isEmpty())) {
            this.message = "Unknown error";
        } else {
            this.message = message;
        }
        this.fault = fault;
        canceled = false;
    }

    public CustomerFailedEvent(CustomerEvent event, boolean canceled, Object source, EventTarget target, EventType<CustomerFailedEvent> eventType) {
        super(event, source, target, assertValidEventType(eventType));
        message = (canceled) ? "Operation canceled" : "Unknown error";
        fault = null;
        this.canceled = canceled;
    }

    public CustomerFailedEvent(CustomerEvent event, String message, Throwable fault, EventType<CustomerFailedEvent> eventType) {
        super(event, assertValidEventType(eventType));
        if ((null == message || message.trim().isEmpty()) && (null == fault || null == (message = fault.getMessage()) || message.trim().isEmpty())) {
            this.message = "Unknown error";
        } else {
            this.message = message;
        }
        this.fault = fault;
        canceled = false;
    }

    public CustomerFailedEvent(CustomerEvent event, boolean canceled, EventType<CustomerFailedEvent> eventType) {
        super(event, assertValidEventType(eventType));
        message = (canceled) ? "Operation canceled" : "Unknown error";
        fault = null;
        this.canceled = canceled;
    }

    public CustomerFailedEvent(CustomerModel fxRecordModel, String message, Throwable fault, Object source, EventTarget target, EventType<CustomerFailedEvent> eventType) {
        super(fxRecordModel, source, target, assertValidEventType(eventType));
        if ((null == message || message.trim().isEmpty()) && (null == fault || null == (message = fault.getMessage()) || message.trim().isEmpty())) {
            this.message = "Unknown error";
        } else {
            this.message = message;
        }
        this.fault = fault;
        canceled = false;
    }

    public CustomerFailedEvent(CustomerModel fxRecordModel, boolean canceled, Object source, EventTarget target, EventType<CustomerFailedEvent> eventType) {
        super(fxRecordModel, source, target, assertValidEventType(eventType));
        message = (canceled) ? "Operation canceled" : "Unknown error";
        fault = null;
        this.canceled = canceled;
    }

    public CustomerFailedEvent(CustomerDAO dao, String message, Throwable fault, Object source, EventTarget target, EventType<CustomerFailedEvent> eventType) {
        super(dao, source, target, assertValidEventType(eventType));
        if ((null == message || message.trim().isEmpty()) && (null == fault || null == (message = fault.getMessage()) || message.trim().isEmpty())) {
            this.message = "Unknown error";
        } else {
            this.message = message;
        }
        this.fault = fault;
        canceled = false;
    }

    public CustomerFailedEvent(CustomerDAO dao, boolean canceled, Object source, EventTarget target, EventType<CustomerFailedEvent> eventType) {
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
