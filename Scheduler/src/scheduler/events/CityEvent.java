package scheduler.events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.CityDAO;
import scheduler.dao.IFxModelOptional;
import scheduler.dao.OperationFailureException;
import scheduler.dao.ValidationFailureException;
import scheduler.model.ui.CityModel;

public abstract class CityEvent extends ModelEvent<CityDAO, CityModel> {

    private static final long serialVersionUID = -6996428374286059723L;

    /**
     * Base {@link EventType} for all {@code CityEvent}s.
     */
    public static final EventType<CityEvent> CITY_EVENT_TYPE = new EventType<>(MODEL_EVENT_TYPE, "SCHEDULER_CITY_EVENT");

    /**
     * Base {@link EventType} for all operational {@code CityEvent}s.
     */
    public static final EventType<CityEvent> OP_EVENT_TYPE = new EventType<>(CITY_EVENT_TYPE, "SCHEDULER_CITY_OP_EVENT");

    public static final boolean isSuccess(CityEvent event) {
        return event instanceof CitySuccessEvent;
    }

    public static final boolean isInvalid(CityEvent event) {
        return event instanceof CityFailedEvent && CityFailedEvent.isInvalidEvent((CityFailedEvent) event);
    }

    public static final boolean isCanceled(CityEvent event) {
        return event instanceof CityFailedEvent && CityFailedEvent.isCanceledEvent((CityFailedEvent) event);
    }

    public static final boolean isFaulted(CityEvent event) {
        return event instanceof CityFailedEvent && CityFailedEvent.isFaultedEvent((CityFailedEvent) event);
    }

    public static final CityEvent createInsertSuccessEvent(IFxModelOptional<CityDAO, CityModel> target, Object source) {
        CityModel model = target.getFxRecordModel();
        if (null == model) {
            return new CitySuccessEvent(target.getDataAccessObject(), source, CitySuccessEvent.INSERT_SUCCESS);
        }
        return new CitySuccessEvent(model, source, CitySuccessEvent.INSERT_SUCCESS);
    }

    public static final CityEvent createUpdateSuccessEvent(IFxModelOptional<CityDAO, CityModel> target, Object source) {
        CityModel model = target.getFxRecordModel();
        if (null == model) {
            return new CitySuccessEvent(target.getDataAccessObject(), source, CitySuccessEvent.UPDATE_SUCCESS);
        }
        return new CitySuccessEvent(model, source, CitySuccessEvent.UPDATE_SUCCESS);
    }

    public static final CityEvent createDeleteSuccessEvent(IFxModelOptional<CityDAO, CityModel> target, Object source) {
        CityModel model = target.getFxRecordModel();
        if (null == model) {
            return new CitySuccessEvent(target.getDataAccessObject(), source, CitySuccessEvent.DELETE_SUCCESS);
        }
        return new CitySuccessEvent(model, source, CitySuccessEvent.DELETE_SUCCESS);
    }

    public static final CityEvent createInsertInvalidEvent(IFxModelOptional<CityDAO, CityModel> target, Object source, ValidationFailureException ex) {
        CityModel model = target.getFxRecordModel();
        if (null == model) {
            if (null != ex) {
                return new CityFailedEvent(target.getDataAccessObject(), ex.getMessage(), ex.getCause(), source, CityFailedEvent.INSERT_INVALID);
            }
            return new CityFailedEvent(target.getDataAccessObject(), null, null, source, CityFailedEvent.INSERT_INVALID);
        }
        if (null != ex) {
            return new CityFailedEvent(model, ex.getMessage(), ex.getCause(), source, CityFailedEvent.INSERT_INVALID);
        }
        return new CityFailedEvent(model, null, null, source, CityFailedEvent.INSERT_INVALID);
    }

    public static final CityEvent createUpdateInvalidEvent(IFxModelOptional<CityDAO, CityModel> target, Object source, ValidationFailureException ex) {
        CityModel model = target.getFxRecordModel();
        if (null == model) {
            if (null != ex) {
                return new CityFailedEvent(target.getDataAccessObject(), ex.getMessage(), ex.getCause(), source, CityFailedEvent.UPDATE_INVALID);
            }
            return new CityFailedEvent(target.getDataAccessObject(), null, null, source, CityFailedEvent.UPDATE_INVALID);
        }
        if (null != ex) {
            return new CityFailedEvent(model, ex.getMessage(), ex.getCause(), source, CityFailedEvent.UPDATE_INVALID);
        }
        return new CityFailedEvent(model, null, null, source, CityFailedEvent.UPDATE_INVALID);
    }

    public static final CityEvent createDeleteInvalidEvent(IFxModelOptional<CityDAO, CityModel> target, Object source, ValidationFailureException ex) {
        CityModel model = target.getFxRecordModel();
        if (null == model) {
            if (null != ex) {
                return new CityFailedEvent(target.getDataAccessObject(), ex.getMessage(), ex.getCause(), source, CityFailedEvent.DELETE_INVALID);
            }
            return new CityFailedEvent(target.getDataAccessObject(), null, null, source, CityFailedEvent.DELETE_INVALID);
        }
        if (null != ex) {
            return new CityFailedEvent(model, ex.getMessage(), ex.getCause(), source, CityFailedEvent.DELETE_INVALID);
        }
        return new CityFailedEvent(model, null, null, source, CityFailedEvent.DELETE_INVALID);
    }

    public static final CityEvent createInsertFaultedEvent(IFxModelOptional<CityDAO, CityModel> target, Object source, Throwable ex) {
        CityModel model = target.getFxRecordModel();
        if (null == model) {
            if (null != ex && ex instanceof OperationFailureException) {
                return new CityFailedEvent(target.getDataAccessObject(), ex.getMessage(), ex.getCause(), source, CityFailedEvent.INSERT_FAULTED);
            }
            return new CityFailedEvent(target.getDataAccessObject(), null, ex, source, CityFailedEvent.INSERT_INVALID);
        }
        if (null != ex && ex instanceof OperationFailureException) {
            return new CityFailedEvent(model, ex.getMessage(), ex.getCause(), source, CityFailedEvent.INSERT_FAULTED);
        }
        return new CityFailedEvent(model, null, ex, source, CityFailedEvent.INSERT_INVALID);
    }

    public static final CityEvent createUpdateFaultedEvent(IFxModelOptional<CityDAO, CityModel> target, Object source, Throwable ex) {
        CityModel model = target.getFxRecordModel();
        if (null == model) {
            if (null != ex && ex instanceof OperationFailureException) {
                return new CityFailedEvent(target.getDataAccessObject(), ex.getMessage(), ex.getCause(), source, CityFailedEvent.UPDATE_FAULTED);
            }
            return new CityFailedEvent(target.getDataAccessObject(), null, ex, source, CityFailedEvent.UPDATE_FAULTED);
        }
        if (null != ex && ex instanceof OperationFailureException) {
            return new CityFailedEvent(model, ex.getMessage(), ex.getCause(), source, CityFailedEvent.UPDATE_FAULTED);
        }
        return new CityFailedEvent(model, null, ex, source, CityFailedEvent.UPDATE_FAULTED);
    }

    public static final CityEvent createDeleteFaultedEvent(IFxModelOptional<CityDAO, CityModel> target, Object source, Throwable ex) {
        CityModel model = target.getFxRecordModel();
        if (null == model) {
            if (null != ex && ex instanceof OperationFailureException) {
                return new CityFailedEvent(target.getDataAccessObject(), ex.getMessage(), ex.getCause(), source, CityFailedEvent.DELETE_FAULTED);
            }
            return new CityFailedEvent(target.getDataAccessObject(), null, ex, source, CityFailedEvent.DELETE_FAULTED);
        }
        if (null != ex && ex instanceof OperationFailureException) {
            return new CityFailedEvent(model, ex.getMessage(), ex.getCause(), source, CityFailedEvent.DELETE_FAULTED);
        }
        return new CityFailedEvent(model, null, ex, source, CityFailedEvent.DELETE_FAULTED);
    }

    public static final CityEvent createInsertCanceledEvent(IFxModelOptional<CityDAO, CityModel> target, Object source, InterruptedException ex) {
        CityModel model = target.getFxRecordModel();
        if (null == model) {
            if (null == ex) {
                return new CityFailedEvent(target.getDataAccessObject(), null, null, source, CityFailedEvent.INSERT_CANCELED);
            }
            if (null != ex.getCause()) {
                return new CityFailedEvent(target.getDataAccessObject(), ex.getMessage(), ex.getCause(), source, CityFailedEvent.INSERT_CANCELED);
            }
            return new CityFailedEvent(target.getDataAccessObject(), ex.getMessage(), ex, source, CityFailedEvent.INSERT_CANCELED);
        }
        if (null == ex) {
            return new CityFailedEvent(model, null, null, source, CityFailedEvent.INSERT_CANCELED);
        }
        if (null != ex.getCause()) {
            return new CityFailedEvent(model, ex.getMessage(), ex.getCause(), source, CityFailedEvent.INSERT_CANCELED);
        }
        return new CityFailedEvent(model, ex.getMessage(), ex, source, CityFailedEvent.INSERT_CANCELED);
    }

    public static final CityEvent createUpdateCanceledEvent(IFxModelOptional<CityDAO, CityModel> target, Object source, InterruptedException ex) {
        CityModel model = target.getFxRecordModel();
        if (null == model) {
            if (null == ex) {
                return new CityFailedEvent(target.getDataAccessObject(), null, null, source, CityFailedEvent.UPDATE_CANCELED);
            }
            if (null != ex.getCause()) {
                return new CityFailedEvent(target.getDataAccessObject(), ex.getMessage(), ex.getCause(), source, CityFailedEvent.UPDATE_CANCELED);
            }
            return new CityFailedEvent(target.getDataAccessObject(), ex.getMessage(), ex, source, CityFailedEvent.UPDATE_CANCELED);
        }
        if (null == ex) {
            return new CityFailedEvent(model, null, null, source, CityFailedEvent.UPDATE_CANCELED);
        }
        if (null != ex.getCause()) {
            return new CityFailedEvent(model, ex.getMessage(), ex.getCause(), source, CityFailedEvent.UPDATE_CANCELED);
        }
        return new CityFailedEvent(model, ex.getMessage(), ex, source, CityFailedEvent.UPDATE_CANCELED);
    }

    public static final CityEvent createDeleteCanceledEvent(IFxModelOptional<CityDAO, CityModel> target, Object source, InterruptedException ex) {
        CityModel model = target.getFxRecordModel();
        if (null == model) {
            if (null == ex) {
                return new CityFailedEvent(target.getDataAccessObject(), null, null, source, CityFailedEvent.DELETE_CANCELED);
            }
            if (null != ex.getCause()) {
                return new CityFailedEvent(target.getDataAccessObject(), ex.getMessage(), ex.getCause(), source, CityFailedEvent.DELETE_CANCELED);
            }
            return new CityFailedEvent(target.getDataAccessObject(), ex.getMessage(), ex, source, CityFailedEvent.DELETE_CANCELED);
        }
        if (null == ex) {
            return new CityFailedEvent(model, null, null, source, CityFailedEvent.DELETE_CANCELED);
        }
        if (null != ex.getCause()) {
            return new CityFailedEvent(model, ex.getMessage(), ex.getCause(), source, CityFailedEvent.DELETE_CANCELED);
        }
        return new CityFailedEvent(model, ex.getMessage(), ex, source, CityFailedEvent.DELETE_CANCELED);
    }

    public static final CityEvent createInsertCanceledEvent(IFxModelOptional<CityDAO, CityModel> target, Object source) {
        return createInsertCanceledEvent(target, source, null);
    }

    public static final CityEvent createUpdateCanceledEvent(IFxModelOptional<CityDAO, CityModel> target, Object source) {
        return createInsertCanceledEvent(target, source, null);
    }

    public static final CityEvent createDeleteCanceledEvent(IFxModelOptional<CityDAO, CityModel> target, Object source) {
        return createInsertCanceledEvent(target, source, null);
    }

    protected CityEvent(CityEvent event, Object source, EventTarget target, EventType<? extends CityEvent> eventType, DbOperationType operation) {
        super(event, source, target, eventType, operation);
    }

    protected CityEvent(CityEvent event, EventType<? extends CityEvent> eventType, DbOperationType operation) {
        super(event, eventType, operation);
    }

    protected CityEvent(CityModel target, Object source, EventType<? extends CityEvent> eventType, DbOperationType operation) {
        super(target, source, eventType, operation);
    }

    protected CityEvent(CityDAO target, Object source, EventType<? extends CityEvent> eventType, DbOperationType operation) {
        super(target, source, eventType, operation);
    }

}
