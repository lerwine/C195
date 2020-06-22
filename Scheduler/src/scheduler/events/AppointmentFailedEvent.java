package scheduler.events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.AppointmentDAO;
import scheduler.model.ui.AppointmentModel;

public final class AppointmentFailedEvent extends AppointmentEvent implements ModelFailedEvent<AppointmentDAO, AppointmentModel> {

    private static final String BASE_EVENT_NAME = "SCHEDULER_APPOINTMENT_FAILED_EVENT";

    /**
     * Base {@link EventType} for all {@code AppointmentFailedEvent}s.
     */
    public static final EventType<AppointmentFailedEvent> FAILED_EVENT_TYPE = new EventType<>(OP_EVENT_TYPE, BASE_EVENT_NAME);

    /**
     * {@link EventType} for save {@code AppointmentFailedEvent}s.
     */
    public static final EventType<AppointmentFailedEvent> SAVE_FAILED = new EventType<>(FAILED_EVENT_TYPE, "SCHEDULER_APPOINTMENT_SAVE_FAILED");

    /**
     * {@link EventType} for delete {@code AppointmentFailedEvent}s.
     */
    public static final EventType<AppointmentFailedEvent> DELETE_FAILED = new EventType<>(FAILED_EVENT_TYPE, "SCHEDULER_APPOINTMENT_DELETE_FAILED");

    private static EventType<AppointmentFailedEvent> assertValidEventType(EventType<AppointmentFailedEvent> eventType) {
        if (eventType.getName().equals(BASE_EVENT_NAME)) {
            throw new IllegalArgumentException();
        }
        return eventType;
    }

    private final String message;
    private final Throwable fault;
    private final boolean canceled;

    public AppointmentFailedEvent(AppointmentEvent event, String message, Throwable fault, Object source, EventTarget target, EventType<AppointmentFailedEvent> eventType) {
        super(event, source, target, assertValidEventType(eventType));
        if ((null == message || message.trim().isEmpty()) && (null == fault || null == (message = fault.getMessage()) || message.trim().isEmpty())) {
            this.message = "Unknown error";
        } else {
            this.message = message;
        }
        this.fault = fault;
        canceled = false;
    }

    public AppointmentFailedEvent(AppointmentEvent event, boolean canceled, Object source, EventTarget target, EventType<AppointmentFailedEvent> eventType) {
        super(event, source, target, assertValidEventType(eventType));
        message = (canceled) ? "Operation canceled" : "Unknown error";
        fault = null;
        this.canceled = canceled;
    }

    public AppointmentFailedEvent(AppointmentEvent event, String message, Throwable fault, EventType<AppointmentFailedEvent> eventType) {
        super(event, assertValidEventType(eventType));
        if ((null == message || message.trim().isEmpty()) && (null == fault || null == (message = fault.getMessage()) || message.trim().isEmpty())) {
            this.message = "Unknown error";
        } else {
            this.message = message;
        }
        this.fault = fault;
        canceled = false;
    }

    public AppointmentFailedEvent(AppointmentEvent event, boolean canceled, EventType<AppointmentFailedEvent> eventType) {
        super(event, assertValidEventType(eventType));
        message = (canceled) ? "Operation canceled" : "Unknown error";
        fault = null;
        this.canceled = canceled;
    }

    public AppointmentFailedEvent(AppointmentModel fxRecordModel, String message, Throwable fault, Object source, EventTarget target, EventType<AppointmentFailedEvent> eventType) {
        super(fxRecordModel, source, target, assertValidEventType(eventType));
        if ((null == message || message.trim().isEmpty()) && (null == fault || null == (message = fault.getMessage()) || message.trim().isEmpty())) {
            this.message = "Unknown error";
        } else {
            this.message = message;
        }
        this.fault = fault;
        canceled = false;
    }

    public AppointmentFailedEvent(AppointmentModel fxRecordModel, boolean canceled, Object source, EventTarget target, EventType<AppointmentFailedEvent> eventType) {
        super(fxRecordModel, source, target, assertValidEventType(eventType));
        message = (canceled) ? "Operation canceled" : "Unknown error";
        fault = null;
        this.canceled = canceled;
    }

    public AppointmentFailedEvent(AppointmentDAO dao, String message, Throwable fault, Object source, EventTarget target, EventType<AppointmentFailedEvent> eventType) {
        super(dao, source, target, assertValidEventType(eventType));
        if ((null == message || message.trim().isEmpty()) && (null == fault || null == (message = fault.getMessage()) || message.trim().isEmpty())) {
            this.message = "Unknown error";
        } else {
            this.message = message;
        }
        this.fault = fault;
        canceled = false;
    }

    public AppointmentFailedEvent(AppointmentDAO dao, boolean canceled, Object source, EventTarget target, EventType<AppointmentFailedEvent> eventType) {
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
