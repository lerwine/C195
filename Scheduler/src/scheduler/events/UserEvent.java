package scheduler.events;

import java.util.Objects;
import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.UserDAO;
import scheduler.model.ui.FxRecordModel;
import scheduler.model.ui.UserModel;

/**
 * Event that is fired when a {@link UserModel} is about to be saved or deleted.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class UserEvent extends DbOperationEvent<UserModel, UserDAO> {

    private static final long serialVersionUID = -4702962471823130721L;
    public static final String USER_MODEL_EVENT_NAME = "SCHEDULER_USER_DB_OPERATION";
    public static final String EDIT_REQUEST_EVENT_NAME = "SCHEDULER_USER_EDIT_REQUEST";
    public static final String DELETE_REQUEST_EVENT_NAME = "SCHEDULER_USER_DELETE_REQUEST";
    public static final String INSERT_VALIDATION_EVENT_NAME = "SCHEDULER_USER_INSERT_VALIDATION";
    public static final String DB_INSERT_EVENT_NAME = "SCHEDULER_USER_DB_INSERT";
    public static final String UPDATE_VALIDATION_EVENT_NAME = "SCHEDULER_USER_UPDATE_VALIDATION";
    public static final String UPDATED_EVENT_NAME = "SCHEDULER_USER_DB_UPDATE";
    public static final String DELETING_EVENT_NAME = "SCHEDULER_USER_DELETE_VALIDATION";
    public static final String DB_DELETE_EVENT_NAME = "SCHEDULER_USER_DB_DELETE";

    /**
     * Base {@link EventType} for all {@code UserEvent}s.
     */
    public static final EventType<UserEvent> USER_MODEL_EVENT_TYPE = new EventType<>(DB_OPERATION, USER_MODEL_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#EDIT_REQUEST} {@code UserEvent}s.
     */
    public static final EventType<UserEvent> EDIT_REQUEST_EVENT_TYPE = new EventType<>(USER_MODEL_EVENT_TYPE, EDIT_REQUEST_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#DELETE_REQUEST} {@code UserEvent}s.
     */
    public static final EventType<UserEvent> DELETE_REQUEST_EVENT_TYPE = new EventType<>(USER_MODEL_EVENT_TYPE, DELETE_REQUEST_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#INSERT_VALIDATION} {@code UserEvent}s.
     */
    public static final EventType<UserEvent> INSERT_VALIDATION_EVENT_TYPE = new EventType<>(USER_MODEL_EVENT_TYPE, INSERT_VALIDATION_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#DB_INSERT} {@code UserEvent}s.
     */
    public static final EventType<UserEvent> DB_INSERT_EVENT_TYPE = new EventType<>(USER_MODEL_EVENT_TYPE, DB_INSERT_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#UPDATE_VALIDATION} {@code UserEvent}s.
     */
    public static final EventType<UserEvent> UPDATE_VALIDATION_EVENT_TYPE = new EventType<>(USER_MODEL_EVENT_TYPE, UPDATE_VALIDATION_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#DB_UPDATE} {@code UserEvent}s.
     */
    public static final EventType<UserEvent> UPDATED_EVENT_TYPE = new EventType<>(USER_MODEL_EVENT_TYPE, UPDATED_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#DELETE_VALIDATION} {@code UserEvent}s.
     */
    public static final EventType<UserEvent> DELETING_EVENT_TYPE = new EventType<>(USER_MODEL_EVENT_TYPE, DELETING_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#DB_DELETE} {@code UserEvent}s.
     */
    public static final EventType<UserEvent> DB_DELETE_EVENT_TYPE = new EventType<>(USER_MODEL_EVENT_TYPE, DB_DELETE_EVENT_NAME);

    public static DbOperationType toDbOperationType(String eventName) {
        if (null != eventName) {
            switch (eventName) {
                case EDIT_REQUEST_EVENT_NAME:
                    return DbOperationType.EDIT_REQUEST;
                case DELETE_REQUEST_EVENT_NAME:
                    return DbOperationType.DELETE_REQUEST;
                case INSERT_VALIDATION_EVENT_NAME:
                    return DbOperationType.INSERT_VALIDATION;
                case DB_INSERT_EVENT_NAME:
                    return DbOperationType.DB_INSERT;
                case UPDATE_VALIDATION_EVENT_NAME:
                    return DbOperationType.UPDATE_VALIDATION;
                case UPDATED_EVENT_NAME:
                    return DbOperationType.DB_UPDATE;
                case DELETING_EVENT_NAME:
                    return DbOperationType.DELETE_VALIDATION;
                case DB_DELETE_EVENT_NAME:
                    return DbOperationType.DB_DELETE;
            }
        }
        return DbOperationType.NONE;
    }

    @SuppressWarnings("incomplete-switch")
    public static EventType<UserEvent> toEventType(DbOperationType operation) {
        if (null != operation) {
            switch (operation) {
                case EDIT_REQUEST:
                    return EDIT_REQUEST_EVENT_TYPE;
                case DELETE_REQUEST:
                    return DELETE_REQUEST_EVENT_TYPE;
                case INSERT_VALIDATION:
                    return INSERT_VALIDATION_EVENT_TYPE;
                case DB_INSERT:
                    return DB_INSERT_EVENT_TYPE;
                case UPDATE_VALIDATION:
                    return UPDATE_VALIDATION_EVENT_TYPE;
                case DB_UPDATE:
                    return UPDATED_EVENT_TYPE;
                case DELETE_VALIDATION:
                    return DELETING_EVENT_TYPE;
                case DB_DELETE:
                    return DB_DELETE_EVENT_TYPE;
            }
        }
        return null;
    }

    private UserEvent(UserEvent copyFrom, Object source, EventTarget target) {
        super(copyFrom, source, target);
    }

    private UserEvent(UserEvent copyFrom, EventTarget target, DbOperationType operation) {
        super(copyFrom, target, Objects.requireNonNull(toEventType(operation)), operation);
    }

    public UserEvent(UserModel model, Object source, EventTarget target, DbOperationType operation, boolean confirmed) {
        super(model, source, target, Objects.requireNonNull(toEventType(operation)), operation, confirmed);
    }

    public UserEvent(UserModel model, Object source, EventTarget target, DbOperationType operation) {
        super(model, source, target, Objects.requireNonNull(toEventType(operation)), operation, false);
    }

    public UserEvent(Object source, EventTarget target, UserDAO dao, DbOperationType operation, boolean confirmed) {
        super(source, target, dao, Objects.requireNonNull(toEventType(operation)), operation, confirmed);
    }

    public UserEvent(Object source, EventTarget target, UserDAO dao, DbOperationType operation) {
        super(source, target, dao, Objects.requireNonNull(toEventType(operation)), operation, false);
    }

    @Override
    public FxRecordModel.ModelFactory<UserDAO, UserModel, ? extends DbOperationEvent<UserModel, UserDAO>> getModelFactory() {
        return UserModel.FACTORY;
    }

    @Override
    public synchronized UserEvent copyFor(Object newSource, EventTarget newTarget) {
        return new UserEvent(this, newSource, newTarget);
    }

    @Override
    @SuppressWarnings("unchecked")
    public EventType<UserEvent> getEventType() {
        return (EventType<UserEvent>) super.getEventType();
    }

    @Override
    public UserEvent toDbOperationType(DbOperationType operation) {
        return new UserEvent(this, getTarget(), operation);
    }

}
