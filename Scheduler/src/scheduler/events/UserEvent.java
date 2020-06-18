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
    public static final String INSERTING_EVENT_NAME = "SCHEDULER_USER_INSERTING";
    public static final String INSERTED_EVENT_NAME = "SCHEDULER_USER_INSERTED";
    public static final String UPDATING_EVENT_NAME = "SCHEDULER_USER_UPDATING";
    public static final String UPDATED_EVENT_NAME = "SCHEDULER_USER_UPDATED";
    public static final String DELETING_EVENT_NAME = "SCHEDULER_USER_DELETING";
    public static final String DELETED_EVENT_NAME = "SCHEDULER_USER_DELETED";

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
     * {@link EventType} for {@link DbOperationType#INSERTING} {@code UserEvent}s.
     */
    public static final EventType<UserEvent> INSERTING_EVENT_TYPE = new EventType<>(USER_MODEL_EVENT_TYPE, INSERTING_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#INSERTED} {@code UserEvent}s.
     */
    public static final EventType<UserEvent> INSERTED_EVENT_TYPE = new EventType<>(USER_MODEL_EVENT_TYPE, INSERTED_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#UPDATING} {@code UserEvent}s.
     */
    public static final EventType<UserEvent> UPDATING_EVENT_TYPE = new EventType<>(USER_MODEL_EVENT_TYPE, UPDATING_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#UPDATED} {@code UserEvent}s.
     */
    public static final EventType<UserEvent> UPDATED_EVENT_TYPE = new EventType<>(USER_MODEL_EVENT_TYPE, UPDATED_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#DELETING} {@code UserEvent}s.
     */
    public static final EventType<UserEvent> DELETING_EVENT_TYPE = new EventType<>(USER_MODEL_EVENT_TYPE, DELETING_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#DELETED} {@code UserEvent}s.
     */
    public static final EventType<UserEvent> DELETED_EVENT_TYPE = new EventType<>(USER_MODEL_EVENT_TYPE, DELETED_EVENT_NAME);

    public static DbOperationType toDbOperationType(String eventName) {
        if (null != eventName) {
            switch (eventName) {
                case EDIT_REQUEST_EVENT_NAME:
                    return DbOperationType.EDIT_REQUEST;
                case DELETE_REQUEST_EVENT_NAME:
                    return DbOperationType.DELETE_REQUEST;
                case INSERTING_EVENT_NAME:
                    return DbOperationType.INSERTING;
                case INSERTED_EVENT_NAME:
                    return DbOperationType.INSERTED;
                case UPDATING_EVENT_NAME:
                    return DbOperationType.UPDATING;
                case UPDATED_EVENT_NAME:
                    return DbOperationType.UPDATED;
                case DELETING_EVENT_NAME:
                    return DbOperationType.DELETING;
                case DELETED_EVENT_NAME:
                    return DbOperationType.DELETED;
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
                case INSERTING:
                    return INSERTING_EVENT_TYPE;
                case INSERTED:
                    return INSERTED_EVENT_TYPE;
                case UPDATING:
                    return UPDATING_EVENT_TYPE;
                case UPDATED:
                    return UPDATED_EVENT_TYPE;
                case DELETING:
                    return DELETING_EVENT_TYPE;
                case DELETED:
                    return DELETED_EVENT_TYPE;
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
