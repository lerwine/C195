package scheduler.events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.OperationFailureException;
import scheduler.model.RecordModelContext;
import scheduler.dao.UserDAO;
import scheduler.model.ui.UserModel;

public abstract class UserEvent extends ModelEvent<UserDAO, UserModel> {

    private static final long serialVersionUID = -4220071150094259420L;

    /**
     * Base {@link EventType} for all {@code UserEvent}s.
     */
    public static final EventType<UserEvent> USER_EVENT_TYPE = new EventType<>(MODEL_EVENT_TYPE, "SCHEDULER_USER_EVENT");

    /**
     * Base {@link EventType} for all operational {@code UserEvent}s.
     */
    public static final EventType<UserEvent> OP_EVENT_TYPE = new EventType<>(USER_EVENT_TYPE, "SCHEDULER_USER_OP_EVENT");

    public static final boolean isSuccess(UserEvent event) {
        return event instanceof UserSuccessEvent;
    }

    public static final boolean isInvalid(UserEvent event) {
        return event instanceof UserFailedEvent && UserFailedEvent.isInvalidEvent((UserFailedEvent) event);
    }

    public static final boolean isCanceled(UserEvent event) {
        return event instanceof UserFailedEvent && UserFailedEvent.isCanceledEvent((UserFailedEvent) event);
    }

    public static final boolean isFaulted(UserEvent event) {
        return event instanceof UserFailedEvent && UserFailedEvent.isFaultedEvent((UserFailedEvent) event);
    }

    public static final UserEvent createInsertSuccessEvent(RecordModelContext<UserDAO, UserModel> target, Object source) {
        return new UserSuccessEvent(target, source, UserSuccessEvent.INSERT_SUCCESS);
    }

    public static final UserEvent createUpdateSuccessEvent(RecordModelContext<UserDAO, UserModel> target, Object source) {
        return new UserSuccessEvent(target, source, UserSuccessEvent.UPDATE_SUCCESS);
    }

    public static final UserEvent createDeleteSuccessEvent(RecordModelContext<UserDAO, UserModel> target, Object source) {
        return new UserSuccessEvent(target, source, UserSuccessEvent.DELETE_SUCCESS);
    }

    public static final UserEvent createInsertInvalidEvent(RecordModelContext<UserDAO, UserModel> target, Object source, String message) {
        return new UserFailedEvent(target, message, null, source, UserFailedEvent.INSERT_INVALID);
    }

    public static final UserEvent createUpdateInvalidEvent(RecordModelContext<UserDAO, UserModel> target, Object source, String message) {
        return new UserFailedEvent(target, message, null, source, UserFailedEvent.UPDATE_INVALID);
    }

    public static final UserEvent createDeleteInvalidEvent(RecordModelContext<UserDAO, UserModel> target, Object source, String message) {
        return new UserFailedEvent(target, message, null, source, UserFailedEvent.DELETE_INVALID);
    }

    public static final UserEvent createInsertFaultedEvent(RecordModelContext<UserDAO, UserModel> target, Object source, Throwable ex) {
        if (null != ex && ex instanceof OperationFailureException) {
            return new UserFailedEvent(target, ex.getMessage(), ex.getCause(), source, UserFailedEvent.INSERT_FAULTED);
        }
        return new UserFailedEvent(target, null, ex, source, UserFailedEvent.INSERT_INVALID);
    }

    public static final UserEvent createUpdateFaultedEvent(RecordModelContext<UserDAO, UserModel> target, Object source, Throwable ex) {
        if (null != ex && ex instanceof OperationFailureException) {
            return new UserFailedEvent(target, ex.getMessage(), ex.getCause(), source, UserFailedEvent.UPDATE_FAULTED);
        }
        return new UserFailedEvent(target, null, ex, source, UserFailedEvent.UPDATE_FAULTED);
    }

    public static final UserEvent createDeleteFaultedEvent(RecordModelContext<UserDAO, UserModel> target, Object source, Throwable ex) {
        if (null != ex && ex instanceof OperationFailureException) {
            return new UserFailedEvent(target, ex.getMessage(), ex.getCause(), source, UserFailedEvent.DELETE_FAULTED);
        }
        return new UserFailedEvent(target, null, ex, source, UserFailedEvent.DELETE_FAULTED);
    }

    public static final UserEvent createInsertCanceledEvent(RecordModelContext<UserDAO, UserModel> target, Object source, InterruptedException ex) {
        if (null == ex) {
            return new UserFailedEvent(target, null, null, source, UserFailedEvent.INSERT_CANCELED);
        }
        if (null != ex.getCause()) {
            return new UserFailedEvent(target, ex.getMessage(), ex.getCause(), source, UserFailedEvent.INSERT_CANCELED);
        }
        return new UserFailedEvent(target, ex.getMessage(), ex, source, UserFailedEvent.INSERT_CANCELED);
    }

    public static final UserEvent createUpdateCanceledEvent(RecordModelContext<UserDAO, UserModel> target, Object source, InterruptedException ex) {
        if (null == ex) {
            return new UserFailedEvent(target, null, null, source, UserFailedEvent.UPDATE_CANCELED);
        }
        if (null != ex.getCause()) {
            return new UserFailedEvent(target, ex.getMessage(), ex.getCause(), source, UserFailedEvent.UPDATE_CANCELED);
        }
        return new UserFailedEvent(target, ex.getMessage(), ex, source, UserFailedEvent.UPDATE_CANCELED);
    }

    public static final UserEvent createDeleteCanceledEvent(RecordModelContext<UserDAO, UserModel> target, Object source, InterruptedException ex) {
        if (null == ex) {
            return new UserFailedEvent(target, null, null, source, UserFailedEvent.DELETE_CANCELED);
        }
        if (null != ex.getCause()) {
            return new UserFailedEvent(target, ex.getMessage(), ex.getCause(), source, UserFailedEvent.DELETE_CANCELED);
        }
        return new UserFailedEvent(target, ex.getMessage(), ex, source, UserFailedEvent.DELETE_CANCELED);
    }

    public static final UserEvent createInsertCanceledEvent(RecordModelContext<UserDAO, UserModel> target, Object source) {
        return createInsertCanceledEvent(target, source, null);
    }

    public static final UserEvent createUpdateCanceledEvent(RecordModelContext<UserDAO, UserModel> target, Object source) {
        return createUpdateCanceledEvent(target, source, null);
    }

    public static final UserEvent createDeleteCanceledEvent(RecordModelContext<UserDAO, UserModel> target, Object source) {
        return createDeleteCanceledEvent(target, source, null);
    }

    protected UserEvent(UserEvent event, Object source, EventTarget target, EventType<? extends UserEvent> eventType, DbOperationType operation) {
        super(event, source, target, eventType, operation);
    }

    protected UserEvent(UserEvent event, EventType<? extends UserEvent> eventType, DbOperationType operation) {
        super(event, eventType, operation);
    }

    protected UserEvent(RecordModelContext<UserDAO, UserModel> target, Object source, EventType<? extends UserEvent> eventType, DbOperationType operation) {
        super(target, source, eventType, operation);
    }

}
