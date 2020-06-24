package scheduler.events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.AppointmentDAO;
import scheduler.dao.IFxModelOptional;
import scheduler.dao.OperationFailureException;
import scheduler.dao.ValidationFailureException;
import scheduler.model.ui.AppointmentModel;

public abstract class AppointmentEvent extends ModelEvent<AppointmentDAO, AppointmentModel> {

    private static final long serialVersionUID = -3677443789026319836L;

    /**
     * Base {@link EventType} for all {@code AppointmentEvent}s.
     */
    public static final EventType<AppointmentEvent> APPOINTMENT_EVENT_TYPE = new EventType<>(MODEL_EVENT_TYPE, "SCHEDULER_APPOINTMENT_EVENT");

    /**
     * Base {@link EventType} for all operational {@code AppointmentEvent}s.
     */
    public static final EventType<AppointmentEvent> OP_EVENT_TYPE = new EventType<>(APPOINTMENT_EVENT_TYPE, "SCHEDULER_APPOINTMENT_OP_EVENT");

    public static final boolean isSuccess(AppointmentEvent event) {
        return event instanceof AppointmentSuccessEvent;
    }

    public static final boolean isInvalid(AppointmentEvent event) {
        return event instanceof AppointmentFailedEvent && AppointmentFailedEvent.isInvalidEvent((AppointmentFailedEvent) event);
    }

    public static final boolean isCanceled(AppointmentEvent event) {
        return event instanceof AppointmentFailedEvent && AppointmentFailedEvent.isCanceledEvent((AppointmentFailedEvent) event);
    }

    public static final boolean isFaulted(AppointmentEvent event) {
        return event instanceof AppointmentFailedEvent && AppointmentFailedEvent.isFaultedEvent((AppointmentFailedEvent) event);
    }

    public static final AppointmentEvent createInsertSuccessEvent(IFxModelOptional<AppointmentDAO, AppointmentModel> target, Object source) {
        AppointmentModel model = target.getFxRecordModel();
        if (null == model) {
            return new AppointmentSuccessEvent(target.getDataAccessObject(), source, AppointmentSuccessEvent.INSERT_SUCCESS);
        }
        return new AppointmentSuccessEvent(model, source, AppointmentSuccessEvent.INSERT_SUCCESS);
    }

    public static final AppointmentEvent createUpdateSuccessEvent(IFxModelOptional<AppointmentDAO, AppointmentModel> target, Object source) {
        AppointmentModel model = target.getFxRecordModel();
        if (null == model) {
            return new AppointmentSuccessEvent(target.getDataAccessObject(), source, AppointmentSuccessEvent.UPDATE_SUCCESS);
        }
        return new AppointmentSuccessEvent(model, source, AppointmentSuccessEvent.UPDATE_SUCCESS);
    }

    public static final AppointmentEvent createDeleteSuccessEvent(IFxModelOptional<AppointmentDAO, AppointmentModel> target, Object source) {
        AppointmentModel model = target.getFxRecordModel();
        if (null == model) {
            return new AppointmentSuccessEvent(target.getDataAccessObject(), source, AppointmentSuccessEvent.DELETE_SUCCESS);
        }
        return new AppointmentSuccessEvent(model, source, AppointmentSuccessEvent.DELETE_SUCCESS);
    }

    public static final AppointmentEvent createInsertInvalidEvent(IFxModelOptional<AppointmentDAO, AppointmentModel> target, Object source, ValidationFailureException ex) {
        AppointmentModel model = target.getFxRecordModel();
        if (null == model) {
            if (null != ex) {
                return new AppointmentFailedEvent(target.getDataAccessObject(), ex.getMessage(), ex.getCause(), source, AppointmentFailedEvent.INSERT_INVALID);
            }
            return new AppointmentFailedEvent(target.getDataAccessObject(), null, null, source, AppointmentFailedEvent.INSERT_INVALID);
        }
        if (null != ex) {
            return new AppointmentFailedEvent(model, ex.getMessage(), ex.getCause(), source, AppointmentFailedEvent.INSERT_INVALID);
        }
        return new AppointmentFailedEvent(model, null, null, source, AppointmentFailedEvent.INSERT_INVALID);
    }

    public static final AppointmentEvent createUpdateInvalidEvent(IFxModelOptional<AppointmentDAO, AppointmentModel> target, Object source, ValidationFailureException ex) {
        AppointmentModel model = target.getFxRecordModel();
        if (null == model) {
            if (null != ex) {
                return new AppointmentFailedEvent(target.getDataAccessObject(), ex.getMessage(), ex.getCause(), source, AppointmentFailedEvent.UPDATE_INVALID);
            }
            return new AppointmentFailedEvent(target.getDataAccessObject(), null, null, source, AppointmentFailedEvent.UPDATE_INVALID);
        }
        if (null != ex) {
            return new AppointmentFailedEvent(model, ex.getMessage(), ex.getCause(), source, AppointmentFailedEvent.UPDATE_INVALID);
        }
        return new AppointmentFailedEvent(model, null, null, source, AppointmentFailedEvent.UPDATE_INVALID);
    }

    public static final AppointmentEvent createDeleteInvalidEvent(IFxModelOptional<AppointmentDAO, AppointmentModel> target, Object source, ValidationFailureException ex) {
        AppointmentModel model = target.getFxRecordModel();
        if (null == model) {
            if (null != ex) {
                return new AppointmentFailedEvent(target.getDataAccessObject(), ex.getMessage(), ex.getCause(), source, AppointmentFailedEvent.DELETE_INVALID);
            }
            return new AppointmentFailedEvent(target.getDataAccessObject(), null, null, source, AppointmentFailedEvent.DELETE_INVALID);
        }
        if (null != ex) {
            return new AppointmentFailedEvent(model, ex.getMessage(), ex.getCause(), source, AppointmentFailedEvent.DELETE_INVALID);
        }
        return new AppointmentFailedEvent(model, null, null, source, AppointmentFailedEvent.DELETE_INVALID);
    }

    public static final AppointmentEvent createInsertFaultedEvent(IFxModelOptional<AppointmentDAO, AppointmentModel> target, Object source, Throwable ex) {
        AppointmentModel model = target.getFxRecordModel();
        if (null == model) {
            if (null != ex && ex instanceof OperationFailureException) {
                return new AppointmentFailedEvent(target.getDataAccessObject(), ex.getMessage(), ex.getCause(), source, AppointmentFailedEvent.INSERT_FAULTED);
            }
            return new AppointmentFailedEvent(target.getDataAccessObject(), null, ex, source, AppointmentFailedEvent.INSERT_INVALID);
        }
        if (null != ex && ex instanceof OperationFailureException) {
            return new AppointmentFailedEvent(model, ex.getMessage(), ex.getCause(), source, AppointmentFailedEvent.INSERT_FAULTED);
        }
        return new AppointmentFailedEvent(model, null, ex, source, AppointmentFailedEvent.INSERT_INVALID);
    }

    public static final AppointmentEvent createUpdateFaultedEvent(IFxModelOptional<AppointmentDAO, AppointmentModel> target, Object source, Throwable ex) {
        AppointmentModel model = target.getFxRecordModel();
        if (null == model) {
            if (null != ex && ex instanceof OperationFailureException) {
                return new AppointmentFailedEvent(target.getDataAccessObject(), ex.getMessage(), ex.getCause(), source, AppointmentFailedEvent.UPDATE_FAULTED);
            }
            return new AppointmentFailedEvent(target.getDataAccessObject(), null, ex, source, AppointmentFailedEvent.UPDATE_FAULTED);
        }
        if (null != ex && ex instanceof OperationFailureException) {
            return new AppointmentFailedEvent(model, ex.getMessage(), ex.getCause(), source, AppointmentFailedEvent.UPDATE_FAULTED);
        }
        return new AppointmentFailedEvent(model, null, ex, source, AppointmentFailedEvent.UPDATE_FAULTED);
    }

    public static final AppointmentEvent createDeleteFaultedEvent(IFxModelOptional<AppointmentDAO, AppointmentModel> target, Object source, Throwable ex) {
        AppointmentModel model = target.getFxRecordModel();
        if (null == model) {
            if (null != ex && ex instanceof OperationFailureException) {
                return new AppointmentFailedEvent(target.getDataAccessObject(), ex.getMessage(), ex.getCause(), source, AppointmentFailedEvent.DELETE_FAULTED);
            }
            return new AppointmentFailedEvent(target.getDataAccessObject(), null, ex, source, AppointmentFailedEvent.DELETE_FAULTED);
        }
        if (null != ex && ex instanceof OperationFailureException) {
            return new AppointmentFailedEvent(model, ex.getMessage(), ex.getCause(), source, AppointmentFailedEvent.DELETE_FAULTED);
        }
        return new AppointmentFailedEvent(model, null, ex, source, AppointmentFailedEvent.DELETE_FAULTED);
    }

    public static final AppointmentEvent createInsertCanceledEvent(IFxModelOptional<AppointmentDAO, AppointmentModel> target, Object source, InterruptedException ex) {
        AppointmentModel model = target.getFxRecordModel();
        if (null == model) {
            if (null == ex) {
                return new AppointmentFailedEvent(target.getDataAccessObject(), null, null, source, AppointmentFailedEvent.INSERT_CANCELED);
            }
            if (null != ex.getCause()) {
                return new AppointmentFailedEvent(target.getDataAccessObject(), ex.getMessage(), ex.getCause(), source, AppointmentFailedEvent.INSERT_CANCELED);
            }
            return new AppointmentFailedEvent(target.getDataAccessObject(), ex.getMessage(), ex, source, AppointmentFailedEvent.INSERT_CANCELED);
        }
        if (null == ex) {
            return new AppointmentFailedEvent(model, null, null, source, AppointmentFailedEvent.INSERT_CANCELED);
        }
        if (null != ex.getCause()) {
            return new AppointmentFailedEvent(model, ex.getMessage(), ex.getCause(), source, AppointmentFailedEvent.INSERT_CANCELED);
        }
        return new AppointmentFailedEvent(model, ex.getMessage(), ex, source, AppointmentFailedEvent.INSERT_CANCELED);
    }

    public static final AppointmentEvent createUpdateCanceledEvent(IFxModelOptional<AppointmentDAO, AppointmentModel> target, Object source, InterruptedException ex) {
        AppointmentModel model = target.getFxRecordModel();
        if (null == model) {
            if (null == ex) {
                return new AppointmentFailedEvent(target.getDataAccessObject(), null, null, source, AppointmentFailedEvent.UPDATE_CANCELED);
            }
            if (null != ex.getCause()) {
                return new AppointmentFailedEvent(target.getDataAccessObject(), ex.getMessage(), ex.getCause(), source, AppointmentFailedEvent.UPDATE_CANCELED);
            }
            return new AppointmentFailedEvent(target.getDataAccessObject(), ex.getMessage(), ex, source, AppointmentFailedEvent.UPDATE_CANCELED);
        }
        if (null == ex) {
            return new AppointmentFailedEvent(model, null, null, source, AppointmentFailedEvent.UPDATE_CANCELED);
        }
        if (null != ex.getCause()) {
            return new AppointmentFailedEvent(model, ex.getMessage(), ex.getCause(), source, AppointmentFailedEvent.UPDATE_CANCELED);
        }
        return new AppointmentFailedEvent(model, ex.getMessage(), ex, source, AppointmentFailedEvent.UPDATE_CANCELED);
    }

    public static final AppointmentEvent createDeleteCanceledEvent(IFxModelOptional<AppointmentDAO, AppointmentModel> target, Object source, InterruptedException ex) {
        AppointmentModel model = target.getFxRecordModel();
        if (null == model) {
            if (null == ex) {
                return new AppointmentFailedEvent(target.getDataAccessObject(), null, null, source, AppointmentFailedEvent.DELETE_CANCELED);
            }
            if (null != ex.getCause()) {
                return new AppointmentFailedEvent(target.getDataAccessObject(), ex.getMessage(), ex.getCause(), source, AppointmentFailedEvent.DELETE_CANCELED);
            }
            return new AppointmentFailedEvent(target.getDataAccessObject(), ex.getMessage(), ex, source, AppointmentFailedEvent.DELETE_CANCELED);
        }
        if (null == ex) {
            return new AppointmentFailedEvent(model, null, null, source, AppointmentFailedEvent.DELETE_CANCELED);
        }
        if (null != ex.getCause()) {
            return new AppointmentFailedEvent(model, ex.getMessage(), ex.getCause(), source, AppointmentFailedEvent.DELETE_CANCELED);
        }
        return new AppointmentFailedEvent(model, ex.getMessage(), ex, source, AppointmentFailedEvent.DELETE_CANCELED);
    }

    public static final AppointmentEvent createInsertCanceledEvent(IFxModelOptional<AppointmentDAO, AppointmentModel> target, Object source) {
        return createInsertCanceledEvent(target, source, null);
    }

    public static final AppointmentEvent createUpdateCanceledEvent(IFxModelOptional<AppointmentDAO, AppointmentModel> target, Object source) {
        return createInsertCanceledEvent(target, source, null);
    }

    public static final AppointmentEvent createDeleteCanceledEvent(IFxModelOptional<AppointmentDAO, AppointmentModel> target, Object source) {
        return createInsertCanceledEvent(target, source, null);
    }

    protected AppointmentEvent(AppointmentEvent event, Object source, EventTarget target, EventType<? extends AppointmentEvent> eventType, DbOperationType operation) {
        super(event, source, target, eventType, operation);
    }

    protected AppointmentEvent(AppointmentEvent event, EventType<? extends AppointmentEvent> eventType, DbOperationType operation) {
        super(event, eventType, operation);
    }

    protected AppointmentEvent(AppointmentModel target, Object source, EventType<? extends AppointmentEvent> eventType, DbOperationType operation) {
        super(target, source, eventType, operation);
    }

    protected AppointmentEvent(AppointmentDAO target, Object source, EventType<? extends AppointmentEvent> eventType, DbOperationType operation) {
        super(target, source, eventType, operation);
    }

}
