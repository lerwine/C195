package scheduler.events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.UserDAO;
import scheduler.model.ui.UserModel;

public final class UserSuccessEvent extends UserEvent {

    /**
     *
     */
    private static final long serialVersionUID = 8315764794660229474L;

    private static final String BASE_EVENT_NAME = "SCHEDULER_USER_SUCCESS_EVENT";

    /**
     * Base {@link EventType} for all {@code UserSuccessEvent}s.
     */
    public static final EventType<UserSuccessEvent> SUCCESS_EVENT_TYPE = new EventType<>(OP_EVENT_TYPE, BASE_EVENT_NAME);

    /**
     * {@link EventType} for save {@code UserSuccessEvent}s.
     */
    public static final EventType<UserSuccessEvent> SAVE_SUCCESS = new EventType<>(SUCCESS_EVENT_TYPE, "SCHEDULER_USER_SAVE_SUCCESS");

    /**
     * {@link EventType} for delete {@code UserSuccessEvent}s.
     */
    public static final EventType<UserSuccessEvent> DELETE_SUCCESS = new EventType<>(SUCCESS_EVENT_TYPE, "SCHEDULER_USER_DELETE_SUCCESS");

    private static EventType<UserSuccessEvent> assertValidEventType(EventType<UserSuccessEvent> eventType) {
        if (eventType.getName().equals(BASE_EVENT_NAME)) {
            throw new IllegalArgumentException();
        }
        return eventType;
    }

    public UserSuccessEvent(UserEvent event, Object source, EventTarget target, EventType<UserSuccessEvent> eventType) {
        super(event, source, target, assertValidEventType(eventType));
    }

    public UserSuccessEvent(UserEvent event, EventType<UserSuccessEvent> eventType) {
        super(event, assertValidEventType(eventType));
    }

    public UserSuccessEvent(UserModel fxRecordModel, Object source, EventTarget target, EventType<UserSuccessEvent> eventType) {
        super(fxRecordModel, source, target, assertValidEventType(eventType));
    }

    public UserSuccessEvent(UserDAO dao, Object source, EventTarget target, EventType<UserSuccessEvent> eventType) {
        super(dao, source, target, assertValidEventType(eventType));
    }

}
