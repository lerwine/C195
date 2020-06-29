package scheduler.events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.UserDAO;
import scheduler.model.RecordModelContext;
import scheduler.model.ui.UserModel;

/**
 * Represents a failed {@link AppointmentEvent}.
 * <h3>Event Registration</h3>
 * <dl>
 * <dt>{@link #BASE_EVENT_NAME "SCHEDULER_USER_FAILED_EVENT"} &lArr; {@link #OP_EVENT_TYPE "SCHEDULER_USER_OP_EVENT"} &lArr; {@link #USER_EVENT_TYPE "SCHEDULER_USER_EVENT"} &lArr;
 * {@link ModelEvent#MODEL_EVENT_TYPE "SCHEDULER_MODEL_EVENT"} &lArr; {@link javafx.event.Event#ANY "EVENT"}</dt>
 * <dd>
 * <dl>
 * <dt>&#x21B3; SCHEDULER_USER_SAVE_FAILED</dt>
 * <dd>&rarr; {@link #SAVE_FAILED}
 * <dl>
 * <dt>&#x21B3; SCHEDULER_USER_INSERT_FAILED</dt>
 * <dd>&rarr; {@link #INSERT_FAILED}
 * <dl>
 * <dt>&#x21B3; SCHEDULER_USER_INSERT_FAULTED</dt>
 * <dd>&rarr; {@link #INSERT_FAULTED}</dd>
 * <dt>&#x21B3; SCHEDULER_USER_INSERT_INVALID</dt>
 * <dd>&rarr; {@link #INSERT_INVALID}</dd>
 * <dt>&#x21B3; SCHEDULER_USER_INSERT_CANCELED</dt>
 * <dd>&rarr; {@link #INSERT_CANCELED}</dd>
 * </dl></dd>
 * <dt>&#x21B3; SCHEDULER_USER_UPDATE_FAILED</dt>
 * <dd>&rarr; {@link #UPDATE_FAILED}
 * <dl>
 * <dt>&#x21B3; SCHEDULER_USER_UPDATE_FAULTED</dt>
 * <dd>&rarr; {@link #UPDATE_FAULTED}</dd>
 * <dt>&#x21B3; SCHEDULER_USER_UPDATE_INVALID</dt>
 * <dd>&rarr; {@link #UPDATE_INVALID}</dd>
 * <dt>&#x21B3; SCHEDULER_USER_UPDATE_CANCELED</dt>
 * <dd>&rarr; {@link #UPDATE_CANCELED}</dd>
 * </dl></dd>
 * </dl></dd>
 * <dt>&#x21B3; SCHEDULER_USER_DELETE_FAILED</dt>
 * <dd>&rarr; {@link #DELETE_FAILED}
 * <dl>
 * <dt>&#x21B3; SCHEDULER_USER_DELETE_FAULTED</dt>
 * <dd>&rarr; {@link #DELETE_FAULTED}</dd>
 * <dt>&#x21B3; SCHEDULER_USER_DELETE_INVALID</dt>
 * <dd>&rarr; {@link #DELETE_INVALID}</dd>
 * <dt>&#x21B3; SCHEDULER_USER_DELETE_CANCELED</dt>
 * <dd>&rarr; {@link #DELETE_CANCELED}</dd>
 * </dl></dd>
 * </dl>
 * </dd>
 * </dl>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public final class UserFailedEvent extends UserEvent implements ModelFailedEvent<UserDAO, UserModel> {

    private static final long serialVersionUID = -4551008464207593559L;

    private static final String BASE_EVENT_NAME = "SCHEDULER_USER_FAILED_EVENT";
    private static final String SAVE_FAILED_EVENT_NAME = "SCHEDULER_USER_SAVE_FAILED";
    private static final String INSERT_FAILED_EVENT_NAME = "SCHEDULER_USER_INSERT_FAILED";
    private static final String UPDATE_FAILED_EVENT_NAME = "SCHEDULER_USER_UPDATE_FAILED";
    private static final String DELETE_FAILED_EVENT_NAME = "SCHEDULER_USER_DELETE_FAILED";
    private static final String INSERT_FAULTED_EVENT_NAME = "SCHEDULER_USER_INSERT_FAULTED";
    private static final String UPDATE_FAULTED_EVENT_NAME = "SCHEDULER_USER_UPDATE_FAULTED";
    private static final String DELETE_FAULTED_EVENT_NAME = "SCHEDULER_USER_DELETE_FAULTED";
    private static final String INSERT_INVALID_EVENT_NAME = "SCHEDULER_USER_INSERT_INVALID";
    private static final String UPDATE_INVALID_EVENT_NAME = "SCHEDULER_USER_UPDATE_INVALID";
    private static final String DELETE_INVALID_EVENT_NAME = "SCHEDULER_USER_DELETE_INVALID";
    private static final String INSERT_CANCELED_EVENT_NAME = "SCHEDULER_USER_INSERT_CANCELED";
    private static final String UPDATE_CANCELED_EVENT_NAME = "SCHEDULER_USER_UPDATE_CANCELED";
    private static final String DELETE_CANCELED_EVENT_NAME = "SCHEDULER_USER_DELETE_CANCELED";

    /**
     * Base {@link EventType} for all {@code UserFailedEvent}s.
     */
    public static final EventType<UserFailedEvent> FAILED_EVENT_TYPE = new EventType<>(OP_EVENT_TYPE, BASE_EVENT_NAME);

    /**
     * Base {@link EventType} for save {@code UserFailedEvent}s.
     */
    public static final EventType<UserFailedEvent> SAVE_FAILED = new EventType<>(FAILED_EVENT_TYPE, SAVE_FAILED_EVENT_NAME);

    /**
     * Base {@link EventType} for insert {@code UserFailedEvent}s.
     */
    public static final EventType<UserFailedEvent> INSERT_FAILED = new EventType<>(SAVE_FAILED, INSERT_FAILED_EVENT_NAME);

    /**
     * Base {@link EventType} for update {@code UserFailedEvent}s.
     */
    public static final EventType<UserFailedEvent> UPDATE_FAILED = new EventType<>(SAVE_FAILED, UPDATE_FAILED_EVENT_NAME);

    /**
     * Base {@link EventType} for delete {@code UserFailedEvent}s.
     */
    public static final EventType<UserFailedEvent> DELETE_FAILED = new EventType<>(FAILED_EVENT_TYPE, DELETE_FAILED_EVENT_NAME);

    /**
     * {@link EventType} for insert fault {@code UserFailedEvent}s.
     */
    public static final EventType<UserFailedEvent> INSERT_FAULTED = new EventType<>(INSERT_FAILED, INSERT_FAULTED_EVENT_NAME);

    /**
     * {@link EventType} for update fault {@code UserFailedEvent}s.
     */
    public static final EventType<UserFailedEvent> UPDATE_FAULTED = new EventType<>(UPDATE_FAILED, UPDATE_FAULTED_EVENT_NAME);

    /**
     * {@link EventType} for delete fault {@code UserFailedEvent}s.
     */
    public static final EventType<UserFailedEvent> DELETE_FAULTED = new EventType<>(DELETE_FAILED, DELETE_FAULTED_EVENT_NAME);

    /**
     * {@link EventType} for insert invalid {@code UserFailedEvent}s.
     */
    public static final EventType<UserFailedEvent> INSERT_INVALID = new EventType<>(INSERT_FAILED, INSERT_INVALID_EVENT_NAME);

    /**
     * {@link EventType} for update invalid {@code UserFailedEvent}s.
     */
    public static final EventType<UserFailedEvent> UPDATE_INVALID = new EventType<>(UPDATE_FAILED, UPDATE_INVALID_EVENT_NAME);

    /**
     * {@link EventType} for delete invalid {@code UserFailedEvent}s.
     */
    public static final EventType<UserFailedEvent> DELETE_INVALID = new EventType<>(DELETE_FAILED, DELETE_INVALID_EVENT_NAME);

    /**
     * {@link EventType} for insert canceled {@code UserFailedEvent}s.
     */
    public static final EventType<UserFailedEvent> INSERT_CANCELED = new EventType<>(INSERT_FAILED, INSERT_CANCELED_EVENT_NAME);

    /**
     * {@link EventType} for update canceled {@code UserFailedEvent}s.
     */
    public static final EventType<UserFailedEvent> UPDATE_CANCELED = new EventType<>(UPDATE_FAILED, UPDATE_CANCELED_EVENT_NAME);

    /**
     * {@link EventType} for delete canceled {@code UserFailedEvent}s.
     */
    public static final EventType<UserFailedEvent> DELETE_CANCELED = new EventType<>(DELETE_FAILED, DELETE_CANCELED_EVENT_NAME);

    public static boolean isFaultedEvent(UserFailedEvent event) {
        switch (event.getEventType().getName()) {
            case INSERT_FAULTED_EVENT_NAME:
            case UPDATE_FAULTED_EVENT_NAME:
            case DELETE_FAULTED_EVENT_NAME:
                return true;
            default:
                return false;
        }
    }

    public static boolean isInvalidEvent(UserFailedEvent event) {
        switch (event.getEventType().getName()) {
            case INSERT_INVALID_EVENT_NAME:
            case UPDATE_INVALID_EVENT_NAME:
            case DELETE_INVALID_EVENT_NAME:
                return true;
            default:
                return false;
        }
    }

    public static boolean isCanceledEvent(UserFailedEvent event) {
        switch (event.getEventType().getName()) {
            case INSERT_CANCELED_EVENT_NAME:
            case UPDATE_CANCELED_EVENT_NAME:
            case DELETE_CANCELED_EVENT_NAME:
                return true;
            default:
                return false;
        }
    }

    @SuppressWarnings("incomplete-switch")
    private static DbOperationType toDbOperationType(EventType<UserFailedEvent> eventType, Throwable fault) {
        switch (eventType.getName()) {
            case INSERT_INVALID_EVENT_NAME:
                if (null != fault) {
                    break;
                }
            case INSERT_FAULTED_EVENT_NAME:
                return DbOperationType.DB_INSERT;
            case INSERT_CANCELED_EVENT_NAME:
                if (null == fault || fault instanceof InterruptedException) {
                    return DbOperationType.DB_INSERT;
                }
                break;
            case UPDATE_INVALID_EVENT_NAME:
                if (null != fault) {
                    break;
                }
            case UPDATE_FAULTED_EVENT_NAME:
                return DbOperationType.DB_UPDATE;
            case UPDATE_CANCELED_EVENT_NAME:
                if (null == fault || fault instanceof InterruptedException) {
                    return DbOperationType.DB_UPDATE;
                }
                break;
            case DELETE_INVALID_EVENT_NAME:
                if (null != fault) {
                    break;
                }
            case DELETE_FAULTED_EVENT_NAME:
                return DbOperationType.DB_DELETE;
            case DELETE_CANCELED_EVENT_NAME:
                if (null == fault || fault instanceof InterruptedException) {
                    return DbOperationType.DB_DELETE;
                }
                break;
        }
        throw new IllegalArgumentException();
    }

    private static String ensureMessage(String message, Throwable fault, EventType<UserFailedEvent> eventType) {
        if (null == message || message.trim().isEmpty()) {
            switch (eventType.getName()) {
                case INSERT_INVALID_EVENT_NAME:
                case UPDATE_INVALID_EVENT_NAME:
                case DELETE_INVALID_EVENT_NAME:
                    return "Validation failed.";
                case INSERT_CANCELED_EVENT_NAME:
                case UPDATE_CANCELED_EVENT_NAME:
                case DELETE_CANCELED_EVENT_NAME:
                    if (null != fault && fault instanceof InterruptedException && null != (message = fault.getMessage()) && !message.isEmpty()) {
                        return message;
                    }
                    return "Operation canceled.";
                default:
                    return "An unexpected error has occured.";
            }
        }
        return message;
    }

    private final String message;
    private final Throwable fault;

    public UserFailedEvent(UserEvent event, String message, Throwable fault, Object source, EventType<UserFailedEvent> eventType, EventTarget target) {
        super(event, source, target, eventType, toDbOperationType(eventType, fault));
        this.message = ensureMessage(message, fault, eventType);
        this.fault = fault;
    }

    public UserFailedEvent(UserEvent event, Object source, String message, EventType<UserFailedEvent> eventType, EventTarget target) {
        super(event, source, target, eventType, toDbOperationType(eventType, null));
        this.message = ensureMessage(message, null, eventType);
        fault = null;
    }

    public UserFailedEvent(UserEvent event, String message, Throwable fault, EventType<UserFailedEvent> eventType) {
        super(event, eventType, toDbOperationType(eventType, fault));
        this.message = ensureMessage(message, fault, eventType);
        this.fault = fault;
    }

    public UserFailedEvent(UserEvent event, String message, EventType<UserFailedEvent> eventType) {
        super(event, eventType, toDbOperationType(eventType, null));
        this.message = ensureMessage(message, null, eventType);
        fault = null;
    }

    public UserFailedEvent(RecordModelContext<UserDAO, UserModel> target, String message, Throwable fault, Object source, EventType<UserFailedEvent> eventType) {
        super(target, source, eventType, toDbOperationType(eventType, fault));
        this.message = ensureMessage(message, fault, eventType);
        this.fault = fault;
    }

    public UserFailedEvent(RecordModelContext<UserDAO, UserModel> target, Object source, String message, EventType<UserFailedEvent> eventType) {
        super(target, source, eventType, toDbOperationType(eventType, null));
        this.message = ensureMessage(message, null, eventType);
        fault = null;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Throwable getFault() {
        return fault;
    }

}
