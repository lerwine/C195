package scheduler.events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.IFxModelOptional;
import scheduler.dao.OperationFailureException;
import scheduler.dao.UserDAO;
import scheduler.dao.ValidationFailureException;
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

    public static final UserEvent createInsertSuccessEvent(IFxModelOptional<UserDAO, UserModel> target, Object source) {
        UserModel model = target.getFxRecordModel();
        if (null == model) {
            return new UserSuccessEvent(target.getDataAccessObject(), source, UserSuccessEvent.INSERT_SUCCESS);
        }
        return new UserSuccessEvent(model, source, UserSuccessEvent.INSERT_SUCCESS);
    }

    public static final UserEvent createUpdateSuccessEvent(IFxModelOptional<UserDAO, UserModel> target, Object source) {
        UserModel model = target.getFxRecordModel();
        if (null == model) {
            return new UserSuccessEvent(target.getDataAccessObject(), source, UserSuccessEvent.UPDATE_SUCCESS);
        }
        return new UserSuccessEvent(model, source, UserSuccessEvent.UPDATE_SUCCESS);
    }

    public static final UserEvent createDeleteSuccessEvent(IFxModelOptional<UserDAO, UserModel> target, Object source) {
        UserModel model = target.getFxRecordModel();
        if (null == model) {
            return new UserSuccessEvent(target.getDataAccessObject(), source, UserSuccessEvent.DELETE_SUCCESS);
        }
        return new UserSuccessEvent(model, source, UserSuccessEvent.DELETE_SUCCESS);
    }

    public static final UserEvent createInsertInvalidEvent(IFxModelOptional<UserDAO, UserModel> target, Object source, ValidationFailureException ex) {
        UserModel model = target.getFxRecordModel();
        if (null == model) {
            if (null != ex) {
                return new UserFailedEvent(target.getDataAccessObject(), ex.getMessage(), ex.getCause(), source, UserFailedEvent.INSERT_INVALID);
            }
            return new UserFailedEvent(target.getDataAccessObject(), null, null, source, UserFailedEvent.INSERT_INVALID);
        }
        if (null != ex) {
            return new UserFailedEvent(model, ex.getMessage(), ex.getCause(), source, UserFailedEvent.INSERT_INVALID);
        }
        return new UserFailedEvent(model, null, null, source, UserFailedEvent.INSERT_INVALID);
    }

    public static final UserEvent createUpdateInvalidEvent(IFxModelOptional<UserDAO, UserModel> target, Object source, ValidationFailureException ex) {
        UserModel model = target.getFxRecordModel();
        if (null == model) {
            if (null != ex) {
                return new UserFailedEvent(target.getDataAccessObject(), ex.getMessage(), ex.getCause(), source, UserFailedEvent.UPDATE_INVALID);
            }
            return new UserFailedEvent(target.getDataAccessObject(), null, null, source, UserFailedEvent.UPDATE_INVALID);
        }
        if (null != ex) {
            return new UserFailedEvent(model, ex.getMessage(), ex.getCause(), source, UserFailedEvent.UPDATE_INVALID);
        }
        return new UserFailedEvent(model, null, null, source, UserFailedEvent.UPDATE_INVALID);
    }

    public static final UserEvent createDeleteInvalidEvent(IFxModelOptional<UserDAO, UserModel> target, Object source, ValidationFailureException ex) {
        UserModel model = target.getFxRecordModel();
        if (null == model) {
            if (null != ex) {
                return new UserFailedEvent(target.getDataAccessObject(), ex.getMessage(), ex.getCause(), source, UserFailedEvent.DELETE_INVALID);
            }
            return new UserFailedEvent(target.getDataAccessObject(), null, null, source, UserFailedEvent.DELETE_INVALID);
        }
        if (null != ex) {
            return new UserFailedEvent(model, ex.getMessage(), ex.getCause(), source, UserFailedEvent.DELETE_INVALID);
        }
        return new UserFailedEvent(model, null, null, source, UserFailedEvent.DELETE_INVALID);
    }

    public static final UserEvent createInsertFaultedEvent(IFxModelOptional<UserDAO, UserModel> target, Object source, Throwable ex) {
        UserModel model = target.getFxRecordModel();
        if (null == model) {
            if (null != ex && ex instanceof OperationFailureException) {
                return new UserFailedEvent(target.getDataAccessObject(), ex.getMessage(), ex.getCause(), source, UserFailedEvent.INSERT_FAULTED);
            }
            return new UserFailedEvent(target.getDataAccessObject(), null, ex, source, UserFailedEvent.INSERT_INVALID);
        }
        if (null != ex && ex instanceof OperationFailureException) {
            return new UserFailedEvent(model, ex.getMessage(), ex.getCause(), source, UserFailedEvent.INSERT_FAULTED);
        }
        return new UserFailedEvent(model, null, ex, source, UserFailedEvent.INSERT_INVALID);
    }

    public static final UserEvent createUpdateFaultedEvent(IFxModelOptional<UserDAO, UserModel> target, Object source, Throwable ex) {
        UserModel model = target.getFxRecordModel();
        if (null == model) {
            if (null != ex && ex instanceof OperationFailureException) {
                return new UserFailedEvent(target.getDataAccessObject(), ex.getMessage(), ex.getCause(), source, UserFailedEvent.UPDATE_FAULTED);
            }
            return new UserFailedEvent(target.getDataAccessObject(), null, ex, source, UserFailedEvent.UPDATE_FAULTED);
        }
        if (null != ex && ex instanceof OperationFailureException) {
            return new UserFailedEvent(model, ex.getMessage(), ex.getCause(), source, UserFailedEvent.UPDATE_FAULTED);
        }
        return new UserFailedEvent(model, null, ex, source, UserFailedEvent.UPDATE_FAULTED);
    }

    public static final UserEvent createDeleteFaultedEvent(IFxModelOptional<UserDAO, UserModel> target, Object source, Throwable ex) {
        UserModel model = target.getFxRecordModel();
        if (null == model) {
            if (null != ex && ex instanceof OperationFailureException) {
                return new UserFailedEvent(target.getDataAccessObject(), ex.getMessage(), ex.getCause(), source, UserFailedEvent.DELETE_FAULTED);
            }
            return new UserFailedEvent(target.getDataAccessObject(), null, ex, source, UserFailedEvent.DELETE_FAULTED);
        }
        if (null != ex && ex instanceof OperationFailureException) {
            return new UserFailedEvent(model, ex.getMessage(), ex.getCause(), source, UserFailedEvent.DELETE_FAULTED);
        }
        return new UserFailedEvent(model, null, ex, source, UserFailedEvent.DELETE_FAULTED);
    }

    public static final UserEvent createInsertCanceledEvent(IFxModelOptional<UserDAO, UserModel> target, Object source, InterruptedException ex) {
        UserModel model = target.getFxRecordModel();
        if (null == model) {
            if (null == ex) {
                return new UserFailedEvent(target.getDataAccessObject(), null, null, source, UserFailedEvent.INSERT_CANCELED);
            }
            if (null != ex.getCause()) {
                return new UserFailedEvent(target.getDataAccessObject(), ex.getMessage(), ex.getCause(), source, UserFailedEvent.INSERT_CANCELED);
            }
            return new UserFailedEvent(target.getDataAccessObject(), ex.getMessage(), ex, source, UserFailedEvent.INSERT_CANCELED);
        }
        if (null == ex) {
            return new UserFailedEvent(model, null, null, source, UserFailedEvent.INSERT_CANCELED);
        }
        if (null != ex.getCause()) {
            return new UserFailedEvent(model, ex.getMessage(), ex.getCause(), source, UserFailedEvent.INSERT_CANCELED);
        }
        return new UserFailedEvent(model, ex.getMessage(), ex, source, UserFailedEvent.INSERT_CANCELED);
    }

    public static final UserEvent createUpdateCanceledEvent(IFxModelOptional<UserDAO, UserModel> target, Object source, InterruptedException ex) {
        UserModel model = target.getFxRecordModel();
        if (null == model) {
            if (null == ex) {
                return new UserFailedEvent(target.getDataAccessObject(), null, null, source, UserFailedEvent.UPDATE_CANCELED);
            }
            if (null != ex.getCause()) {
                return new UserFailedEvent(target.getDataAccessObject(), ex.getMessage(), ex.getCause(), source, UserFailedEvent.UPDATE_CANCELED);
            }
            return new UserFailedEvent(target.getDataAccessObject(), ex.getMessage(), ex, source, UserFailedEvent.UPDATE_CANCELED);
        }
        if (null == ex) {
            return new UserFailedEvent(model, null, null, source, UserFailedEvent.UPDATE_CANCELED);
        }
        if (null != ex.getCause()) {
            return new UserFailedEvent(model, ex.getMessage(), ex.getCause(), source, UserFailedEvent.UPDATE_CANCELED);
        }
        return new UserFailedEvent(model, ex.getMessage(), ex, source, UserFailedEvent.UPDATE_CANCELED);
    }

    public static final UserEvent createDeleteCanceledEvent(IFxModelOptional<UserDAO, UserModel> target, Object source, InterruptedException ex) {
        UserModel model = target.getFxRecordModel();
        if (null == model) {
            if (null == ex) {
                return new UserFailedEvent(target.getDataAccessObject(), null, null, source, UserFailedEvent.DELETE_CANCELED);
            }
            if (null != ex.getCause()) {
                return new UserFailedEvent(target.getDataAccessObject(), ex.getMessage(), ex.getCause(), source, UserFailedEvent.DELETE_CANCELED);
            }
            return new UserFailedEvent(target.getDataAccessObject(), ex.getMessage(), ex, source, UserFailedEvent.DELETE_CANCELED);
        }
        if (null == ex) {
            return new UserFailedEvent(model, null, null, source, UserFailedEvent.DELETE_CANCELED);
        }
        if (null != ex.getCause()) {
            return new UserFailedEvent(model, ex.getMessage(), ex.getCause(), source, UserFailedEvent.DELETE_CANCELED);
        }
        return new UserFailedEvent(model, ex.getMessage(), ex, source, UserFailedEvent.DELETE_CANCELED);
    }

    public static final UserEvent createInsertCanceledEvent(IFxModelOptional<UserDAO, UserModel> target, Object source) {
        return createInsertCanceledEvent(target, source, null);
    }

    public static final UserEvent createUpdateCanceledEvent(IFxModelOptional<UserDAO, UserModel> target, Object source) {
        return createInsertCanceledEvent(target, source, null);
    }

    public static final UserEvent createDeleteCanceledEvent(IFxModelOptional<UserDAO, UserModel> target, Object source) {
        return createInsertCanceledEvent(target, source, null);
    }

    protected UserEvent(UserEvent event, Object source, EventTarget target, EventType<? extends UserEvent> eventType, DbOperationType operation) {
        super(event, source, target, eventType, operation);
    }

    protected UserEvent(UserEvent event, EventType<? extends UserEvent> eventType, DbOperationType operation) {
        super(event, eventType, operation);
    }

    protected UserEvent(UserModel target, Object source, EventType<? extends UserEvent> eventType, DbOperationType operation) {
        super(target, source, eventType, operation);
    }

    protected UserEvent(UserDAO target, Object source, EventType<? extends UserEvent> eventType, DbOperationType operation) {
        super(target, source, eventType, operation);
    }

}
