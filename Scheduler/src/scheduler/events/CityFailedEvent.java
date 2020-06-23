package scheduler.events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.CityDAO;
import scheduler.model.ui.CityModel;

public final class CityFailedEvent extends CityEvent implements ModelFailedEvent<CityDAO, CityModel> {

    private static final long serialVersionUID = 4383081185660810957L;

    private static final String BASE_EVENT_NAME = "SCHEDULER_CITY_FAILED_EVENT";

    /**
     * Base {@link EventType} for all {@code CityFailedEvent}s.
     */
    public static final EventType<CityFailedEvent> FAILED_EVENT_TYPE = new EventType<>(OP_EVENT_TYPE, BASE_EVENT_NAME);

    /**
     * {@link EventType} for save {@code CityFailedEvent}s.
     */
    public static final EventType<CityFailedEvent> SAVE_FAILED = new EventType<>(FAILED_EVENT_TYPE, "SCHEDULER_CITY_SAVE_FAILED");

    /**
     * {@link EventType} for delete {@code CityFailedEvent}s.
     */
    public static final EventType<CityFailedEvent> DELETE_FAILED = new EventType<>(FAILED_EVENT_TYPE, "SCHEDULER_CITY_DELETE_FAILED");

    private static EventType<CityFailedEvent> assertValidEventType(EventType<CityFailedEvent> eventType) {
        if (eventType.getName().equals(BASE_EVENT_NAME)) {
            throw new IllegalArgumentException();
        }
        return eventType;
    }

    private final String message;
    private final Throwable fault;
    private final boolean canceled;

    public CityFailedEvent(CityEvent event, String message, Throwable fault, Object source, EventTarget target, EventType<CityFailedEvent> eventType) {
        super(event, source, target, assertValidEventType(eventType));
        if ((null == message || message.trim().isEmpty()) && (null == fault || null == (message = fault.getMessage()) || message.trim().isEmpty())) {
            this.message = "Unknown error";
        } else {
            this.message = message;
        }
        this.fault = fault;
        canceled = false;
    }

    public CityFailedEvent(CityEvent event, boolean canceled, Object source, EventTarget target, EventType<CityFailedEvent> eventType) {
        super(event, source, target, assertValidEventType(eventType));
        message = (canceled) ? "Operation canceled" : "Unknown error";
        fault = null;
        this.canceled = canceled;
    }

    public CityFailedEvent(CityEvent event, String message, Throwable fault, EventType<CityFailedEvent> eventType) {
        super(event, assertValidEventType(eventType));
        if ((null == message || message.trim().isEmpty()) && (null == fault || null == (message = fault.getMessage()) || message.trim().isEmpty())) {
            this.message = "Unknown error";
        } else {
            this.message = message;
        }
        this.fault = fault;
        canceled = false;
    }

    public CityFailedEvent(CityEvent event, boolean canceled, EventType<CityFailedEvent> eventType) {
        super(event, assertValidEventType(eventType));
        message = (canceled) ? "Operation canceled" : "Unknown error";
        fault = null;
        this.canceled = canceled;
    }

    public CityFailedEvent(CityModel fxRecordModel, String message, Throwable fault, Object source, EventTarget target, EventType<CityFailedEvent> eventType) {
        super(fxRecordModel, source, target, assertValidEventType(eventType));
        if ((null == message || message.trim().isEmpty()) && (null == fault || null == (message = fault.getMessage()) || message.trim().isEmpty())) {
            this.message = "Unknown error";
        } else {
            this.message = message;
        }
        this.fault = fault;
        canceled = false;
    }

    public CityFailedEvent(CityModel fxRecordModel, boolean canceled, Object source, EventTarget target, EventType<CityFailedEvent> eventType) {
        super(fxRecordModel, source, target, assertValidEventType(eventType));
        message = (canceled) ? "Operation canceled" : "Unknown error";
        fault = null;
        this.canceled = canceled;
    }

    public CityFailedEvent(CityDAO dao, String message, Throwable fault, Object source, EventTarget target, EventType<CityFailedEvent> eventType) {
        super(dao, source, target, assertValidEventType(eventType));
        if ((null == message || message.trim().isEmpty()) && (null == fault || null == (message = fault.getMessage()) || message.trim().isEmpty())) {
            this.message = "Unknown error";
        } else {
            this.message = message;
        }
        this.fault = fault;
        canceled = false;
    }

    public CityFailedEvent(CityDAO dao, boolean canceled, Object source, EventTarget target, EventType<CityFailedEvent> eventType) {
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
