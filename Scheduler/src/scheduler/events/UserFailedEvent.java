package scheduler.events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.UserDAO;
import scheduler.model.ui.UserModel;

public final class UserFailedEvent extends UserEvent implements ModelFailedEvent<UserDAO, UserModel> {

    private static final long serialVersionUID = -4551008464207593559L;

    private static final String BASE_EVENT_NAME = "SCHEDULER_USER_FAILED_EVENT";

    /**
     * Base {@link EventType} for all {@code UserFailedEvent}s.
     */
    public static final EventType<UserFailedEvent> FAILED_EVENT_TYPE = new EventType<>(OP_EVENT_TYPE, BASE_EVENT_NAME);

    /**
     * {@link EventType} for save {@code UserFailedEvent}s.
     */
    public static final EventType<UserFailedEvent> SAVE_FAILED = new EventType<>(FAILED_EVENT_TYPE, "SCHEDULER_USER_SAVE_FAILED");

    /**
     * {@link EventType} for delete {@code UserFailedEvent}s.
     */
    public static final EventType<UserFailedEvent> DELETE_FAILED = new EventType<>(FAILED_EVENT_TYPE, "SCHEDULER_USER_DELETE_FAILED");

    private static EventType<UserFailedEvent> assertValidEventType(EventType<UserFailedEvent> eventType) {
        if (eventType.getName().equals(BASE_EVENT_NAME)) {
            throw new IllegalArgumentException();
        }
        return eventType;
    }

    private final String message;
    private final Throwable fault;
    private final boolean canceled;

    public UserFailedEvent(UserEvent event, String message, Throwable fault, Object source, EventTarget target, EventType<UserFailedEvent> eventType) {
        super(event, source, target, assertValidEventType(eventType));
        if ((null == message || message.trim().isEmpty()) && (null == fault || null == (message = fault.getMessage()) || message.trim().isEmpty())) {
            this.message = "Unknown error";
        } else {
            this.message = message;
        }
        this.fault = fault;
        canceled = false;
    }

    public UserFailedEvent(UserEvent event, boolean canceled, Object source, EventTarget target, EventType<UserFailedEvent> eventType) {
        super(event, source, target, assertValidEventType(eventType));
        message = (canceled) ? "Operation canceled" : "Unknown error";
        fault = null;
        this.canceled = canceled;
    }

    public UserFailedEvent(UserEvent event, String message, Throwable fault, EventType<UserFailedEvent> eventType) {
        super(event, assertValidEventType(eventType));
        if ((null == message || message.trim().isEmpty()) && (null == fault || null == (message = fault.getMessage()) || message.trim().isEmpty())) {
            this.message = "Unknown error";
        } else {
            this.message = message;
        }
        this.fault = fault;
        canceled = false;
    }

    public UserFailedEvent(UserEvent event, boolean canceled, EventType<UserFailedEvent> eventType) {
        super(event, assertValidEventType(eventType));
        message = (canceled) ? "Operation canceled" : "Unknown error";
        fault = null;
        this.canceled = canceled;
    }

    public UserFailedEvent(UserModel fxRecordModel, String message, Throwable fault, Object source, EventTarget target, EventType<UserFailedEvent> eventType) {
        super(fxRecordModel, source, target, assertValidEventType(eventType));
        if ((null == message || message.trim().isEmpty()) && (null == fault || null == (message = fault.getMessage()) || message.trim().isEmpty())) {
            this.message = "Unknown error";
        } else {
            this.message = message;
        }
        this.fault = fault;
        canceled = false;
    }

    public UserFailedEvent(UserModel fxRecordModel, boolean canceled, Object source, EventTarget target, EventType<UserFailedEvent> eventType) {
        super(fxRecordModel, source, target, assertValidEventType(eventType));
        message = (canceled) ? "Operation canceled" : "Unknown error";
        fault = null;
        this.canceled = canceled;
    }

    public UserFailedEvent(UserDAO dao, String message, Throwable fault, Object source, EventTarget target, EventType<UserFailedEvent> eventType) {
        super(dao, source, target, assertValidEventType(eventType));
        if ((null == message || message.trim().isEmpty()) && (null == fault || null == (message = fault.getMessage()) || message.trim().isEmpty())) {
            this.message = "Unknown error";
        } else {
            this.message = message;
        }
        this.fault = fault;
        canceled = false;
    }

    public UserFailedEvent(UserDAO dao, boolean canceled, Object source, EventTarget target, EventType<UserFailedEvent> eventType) {
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
