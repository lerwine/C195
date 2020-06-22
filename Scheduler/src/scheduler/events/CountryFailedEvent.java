package scheduler.events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.CountryDAO;
import scheduler.model.ui.CountryModel;

public final class CountryFailedEvent extends CountryEvent implements ModelFailedEvent<CountryDAO, CountryModel> {

    private static final String BASE_EVENT_NAME = "SCHEDULER_COUNTRY_FAILED_EVENT";

    /**
     * Base {@link EventType} for all {@code CountryFailedEvent}s.
     */
    public static final EventType<CountryFailedEvent> FAILED_EVENT_TYPE = new EventType<>(OP_EVENT_TYPE, BASE_EVENT_NAME);

    /**
     * {@link EventType} for save {@code CountryFailedEvent}s.
     */
    public static final EventType<CountryFailedEvent> SAVE_FAILED = new EventType<>(FAILED_EVENT_TYPE, "SCHEDULER_COUNTRY_SAVE_FAILED");

    /**
     * {@link EventType} for delete {@code CountryFailedEvent}s.
     */
    public static final EventType<CountryFailedEvent> DELETE_FAILED = new EventType<>(FAILED_EVENT_TYPE, "SCHEDULER_COUNTRY_DELETE_FAILED");

    private static EventType<CountryFailedEvent> assertValidEventType(EventType<CountryFailedEvent> eventType) {
        if (eventType.getName().equals(BASE_EVENT_NAME)) {
            throw new IllegalArgumentException();
        }
        return eventType;
    }

    private final String message;
    private final Throwable fault;
    private final boolean canceled;

    public CountryFailedEvent(CountryEvent event, String message, Throwable fault, Object source, EventTarget target, EventType<CountryFailedEvent> eventType) {
        super(event, source, target, assertValidEventType(eventType));
        if ((null == message || message.trim().isEmpty()) && (null == fault || null == (message = fault.getMessage()) || message.trim().isEmpty())) {
            this.message = "Unknown error";
        } else {
            this.message = message;
        }
        this.fault = fault;
        canceled = false;
    }

    public CountryFailedEvent(CountryEvent event, boolean canceled, Object source, EventTarget target, EventType<CountryFailedEvent> eventType) {
        super(event, source, target, assertValidEventType(eventType));
        message = (canceled) ? "Operation canceled" : "Unknown error";
        fault = null;
        this.canceled = canceled;
    }

    public CountryFailedEvent(CountryEvent event, String message, Throwable fault, EventType<CountryFailedEvent> eventType) {
        super(event, assertValidEventType(eventType));
        if ((null == message || message.trim().isEmpty()) && (null == fault || null == (message = fault.getMessage()) || message.trim().isEmpty())) {
            this.message = "Unknown error";
        } else {
            this.message = message;
        }
        this.fault = fault;
        canceled = false;
    }

    public CountryFailedEvent(CountryEvent event, boolean canceled, EventType<CountryFailedEvent> eventType) {
        super(event, assertValidEventType(eventType));
        message = (canceled) ? "Operation canceled" : "Unknown error";
        fault = null;
        this.canceled = canceled;
    }

    public CountryFailedEvent(CountryModel fxRecordModel, String message, Throwable fault, Object source, EventTarget target, EventType<CountryFailedEvent> eventType) {
        super(fxRecordModel, source, target, assertValidEventType(eventType));
        if ((null == message || message.trim().isEmpty()) && (null == fault || null == (message = fault.getMessage()) || message.trim().isEmpty())) {
            this.message = "Unknown error";
        } else {
            this.message = message;
        }
        this.fault = fault;
        canceled = false;
    }

    public CountryFailedEvent(CountryModel fxRecordModel, boolean canceled, Object source, EventTarget target, EventType<CountryFailedEvent> eventType) {
        super(fxRecordModel, source, target, assertValidEventType(eventType));
        message = (canceled) ? "Operation canceled" : "Unknown error";
        fault = null;
        this.canceled = canceled;
    }

    public CountryFailedEvent(CountryDAO dao, String message, Throwable fault, Object source, EventTarget target, EventType<CountryFailedEvent> eventType) {
        super(dao, source, target, assertValidEventType(eventType));
        if ((null == message || message.trim().isEmpty()) && (null == fault || null == (message = fault.getMessage()) || message.trim().isEmpty())) {
            this.message = "Unknown error";
        } else {
            this.message = message;
        }
        this.fault = fault;
        canceled = false;
    }

    public CountryFailedEvent(CountryDAO dao, boolean canceled, Object source, EventTarget target, EventType<CountryFailedEvent> eventType) {
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
