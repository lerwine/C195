package scheduler.events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.model.RecordModelContext;
import scheduler.dao.UserDAO;
import scheduler.model.ui.UserModel;

public final class UserSuccessEvent extends UserEvent {

    private static final long serialVersionUID = 8315764794660229474L;

    private static final String BASE_EVENT_NAME = "SCHEDULER_USER_SUCCESS_EVENT";
    private static final String SAVE_SUCCESS_EVENT_NAME = "SCHEDULER_USER_SAVE_SUCCESS";
    private static final String INSERT_EVENT_NAME = "SCHEDULER_USER_INSERT_SUCCESS";
    private static final String UPDATE_EVENT_NAME = "SCHEDULER_USER_UPDATE_SUCCESS";
    private static final String DELETE_EVENT_NAME = "SCHEDULER_USER_DELETE_SUCCESS";

    /**
     * Base {@link EventType} for all {@code UserSuccessEvent}s.
     */
    public static final EventType<UserSuccessEvent> SUCCESS_EVENT_TYPE = new EventType<>(OP_EVENT_TYPE, BASE_EVENT_NAME);

    /**
     * {@link EventType} for save {@code UserSuccessEvent}s.
     */
    public static final EventType<UserSuccessEvent> SAVE_SUCCESS = new EventType<>(SUCCESS_EVENT_TYPE, SAVE_SUCCESS_EVENT_NAME);

    /**
     * {@link EventType} for database insert {@code UserSuccessEvent}s.
     */
    public static final EventType<UserSuccessEvent> INSERT_SUCCESS = new EventType<>(SAVE_SUCCESS, INSERT_EVENT_NAME);

    /**
     * {@link EventType} for database update {@code UserSuccessEvent}s.
     */
    public static final EventType<UserSuccessEvent> UPDATE_SUCCESS = new EventType<>(SAVE_SUCCESS, UPDATE_EVENT_NAME);

    /**
     * {@link EventType} for delete {@code UserSuccessEvent}s.
     */
    public static final EventType<UserSuccessEvent> DELETE_SUCCESS = new EventType<>(SUCCESS_EVENT_TYPE, DELETE_EVENT_NAME);

    private static DbOperationType toDbOperationType(EventType<UserSuccessEvent> eventType) {
        switch (eventType.getName()) {
            case INSERT_EVENT_NAME:
                return DbOperationType.DB_INSERT;
            case UPDATE_EVENT_NAME:
                return DbOperationType.DB_UPDATE;
            case DELETE_EVENT_NAME:
                return DbOperationType.DB_DELETE;
            default:
                throw new IllegalArgumentException();
        }
    }

    public UserSuccessEvent(UserEvent event, Object source, EventTarget target, EventType<UserSuccessEvent> eventType) {
        super(event, source, target, eventType, toDbOperationType(eventType));
    }

    public UserSuccessEvent(UserEvent event, EventType<UserSuccessEvent> eventType) {
        super(event, eventType, toDbOperationType(eventType));
    }

    public UserSuccessEvent(RecordModelContext<UserDAO, UserModel> target, Object source, EventType<UserSuccessEvent> eventType) {
        super(target, source, eventType, toDbOperationType(eventType));
    }

}
