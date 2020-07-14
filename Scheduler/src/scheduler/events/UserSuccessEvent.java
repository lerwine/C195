package scheduler.events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.model.fx.UserModel;

/**
 * Represents a successful {@link AppointmentEvent}.
 * <h3>Event Registration</h3>
 * <dl>
 * <dt>{@link #BASE_EVENT_NAME "SCHEDULER_USER_SUCCESS_EVENT"} &lArr; {@link #CHANGE_EVENT_TYPE "SCHEDULER_USER_OP_EVENT"} &lArr;
 * {@link #USER_EVENT_TYPE "SCHEDULER_USER_EVENT"} &lArr; {@link ModelEvent#MODEL_EVENT_TYPE "SCHEDULER_MODEL_EVENT"} &lArr;
 * {@link javafx.event.Event#ANY "EVENT"}</dt>
 * <dd>
 * <dl>
 * <dt>(inherit) SCHEDULER_USER_SAVE_SUCCESS</dt>
 * <dd>&rarr; {@link #SAVE_SUCCESS}
 * <dl>
 * <dt>(inherit) SCHEDULER_USER_INSERT_SUCCESS</dt>
 * <dd>&rarr; {@link #INSERT_SUCCESS}</dd>
 * <dt>(inherit) SCHEDULER_USER_UPDATE_SUCCESS</dt>
 * <dd>&rarr; {@link #UPDATE_SUCCESS}</dd>
 * </dl></dd>
 * <dt>(inherit) SCHEDULER_USER_DELETE_SUCCESS</dt>
 * <dd>&rarr; {@link #DELETE_SUCCESS}</dd>
 * </dl>
 * </dd>
 * </dl>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
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
    public static final EventType<UserSuccessEvent> SUCCESS_EVENT_TYPE = new EventType<>(CHANGE_EVENT_TYPE, BASE_EVENT_NAME);

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

    public UserSuccessEvent(UserModel target, Object source, EventType<UserSuccessEvent> eventType) {
        super(target, source, eventType, toDbOperationType(eventType));
    }

}
