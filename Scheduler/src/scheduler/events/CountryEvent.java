package scheduler.events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.CountryDAO;
import scheduler.dao.IFxModelOptional;
import scheduler.dao.OperationFailureException;
import scheduler.dao.ValidationFailureException;
import scheduler.model.ui.CountryModel;

public abstract class CountryEvent extends ModelEvent<CountryDAO, CountryModel> {

    private static final long serialVersionUID = -6121391358660436488L;

    /**
     * Base {@link EventType} for all {@code CountryEvent}s.
     */
    public static final EventType<CountryEvent> COUNTRY_EVENT_TYPE = new EventType<>(MODEL_EVENT_TYPE, "SCHEDULER_COUNTRY_EVENT");

    /**
     * Base {@link EventType} for all operational {@code CountryEvent}s.
     */
    public static final EventType<CountryEvent> OP_EVENT_TYPE = new EventType<>(COUNTRY_EVENT_TYPE, "SCHEDULER_COUNTRY_OP_EVENT");

    public static final boolean isSuccess(CountryEvent event) {
        return event instanceof CountrySuccessEvent;
    }

    public static final boolean isInvalid(CountryEvent event) {
        return event instanceof CountryFailedEvent && CountryFailedEvent.isInvalidEvent((CountryFailedEvent) event);
    }

    public static final boolean isCanceled(CountryEvent event) {
        return event instanceof CountryFailedEvent && CountryFailedEvent.isCanceledEvent((CountryFailedEvent) event);
    }

    public static final boolean isFaulted(CountryEvent event) {
        return event instanceof CountryFailedEvent && CountryFailedEvent.isFaultedEvent((CountryFailedEvent) event);
    }

    public static final CountryEvent createInsertSuccessEvent(IFxModelOptional<CountryDAO, CountryModel> target, Object source) {
        CountryModel model = target.getFxRecordModel();
        if (null == model) {
            return new CountrySuccessEvent(target.getDataAccessObject(), source, CountrySuccessEvent.INSERT_SUCCESS);
        }
        return new CountrySuccessEvent(model, source, CountrySuccessEvent.INSERT_SUCCESS);
    }

    public static final CountryEvent createUpdateSuccessEvent(IFxModelOptional<CountryDAO, CountryModel> target, Object source) {
        CountryModel model = target.getFxRecordModel();
        if (null == model) {
            return new CountrySuccessEvent(target.getDataAccessObject(), source, CountrySuccessEvent.UPDATE_SUCCESS);
        }
        return new CountrySuccessEvent(model, source, CountrySuccessEvent.UPDATE_SUCCESS);
    }

    public static final CountryEvent createDeleteSuccessEvent(IFxModelOptional<CountryDAO, CountryModel> target, Object source) {
        CountryModel model = target.getFxRecordModel();
        if (null == model) {
            return new CountrySuccessEvent(target.getDataAccessObject(), source, CountrySuccessEvent.DELETE_SUCCESS);
        }
        return new CountrySuccessEvent(model, source, CountrySuccessEvent.DELETE_SUCCESS);
    }

    public static final CountryEvent createInsertInvalidEvent(IFxModelOptional<CountryDAO, CountryModel> target, Object source, ValidationFailureException ex) {
        CountryModel model = target.getFxRecordModel();
        if (null == model) {
            if (null != ex) {
                return new CountryFailedEvent(target.getDataAccessObject(), ex.getMessage(), ex.getCause(), source, CountryFailedEvent.INSERT_INVALID);
            }
            return new CountryFailedEvent(target.getDataAccessObject(), null, null, source, CountryFailedEvent.INSERT_INVALID);
        }
        if (null != ex) {
            return new CountryFailedEvent(model, ex.getMessage(), ex.getCause(), source, CountryFailedEvent.INSERT_INVALID);
        }
        return new CountryFailedEvent(model, null, null, source, CountryFailedEvent.INSERT_INVALID);
    }

    public static final CountryEvent createUpdateInvalidEvent(IFxModelOptional<CountryDAO, CountryModel> target, Object source, ValidationFailureException ex) {
        CountryModel model = target.getFxRecordModel();
        if (null == model) {
            if (null != ex) {
                return new CountryFailedEvent(target.getDataAccessObject(), ex.getMessage(), ex.getCause(), source, CountryFailedEvent.UPDATE_INVALID);
            }
            return new CountryFailedEvent(target.getDataAccessObject(), null, null, source, CountryFailedEvent.UPDATE_INVALID);
        }
        if (null != ex) {
            return new CountryFailedEvent(model, ex.getMessage(), ex.getCause(), source, CountryFailedEvent.UPDATE_INVALID);
        }
        return new CountryFailedEvent(model, null, null, source, CountryFailedEvent.UPDATE_INVALID);
    }

    public static final CountryEvent createDeleteInvalidEvent(IFxModelOptional<CountryDAO, CountryModel> target, Object source, ValidationFailureException ex) {
        CountryModel model = target.getFxRecordModel();
        if (null == model) {
            if (null != ex) {
                return new CountryFailedEvent(target.getDataAccessObject(), ex.getMessage(), ex.getCause(), source, CountryFailedEvent.DELETE_INVALID);
            }
            return new CountryFailedEvent(target.getDataAccessObject(), null, null, source, CountryFailedEvent.DELETE_INVALID);
        }
        if (null != ex) {
            return new CountryFailedEvent(model, ex.getMessage(), ex.getCause(), source, CountryFailedEvent.DELETE_INVALID);
        }
        return new CountryFailedEvent(model, null, null, source, CountryFailedEvent.DELETE_INVALID);
    }

    public static final CountryEvent createInsertFaultedEvent(IFxModelOptional<CountryDAO, CountryModel> target, Object source, Throwable ex) {
        CountryModel model = target.getFxRecordModel();
        if (null == model) {
            if (null != ex && ex instanceof OperationFailureException) {
                return new CountryFailedEvent(target.getDataAccessObject(), ex.getMessage(), ex.getCause(), source, CountryFailedEvent.INSERT_FAULTED);
            }
            return new CountryFailedEvent(target.getDataAccessObject(), null, ex, source, CountryFailedEvent.INSERT_INVALID);
        }
        if (null != ex && ex instanceof OperationFailureException) {
            return new CountryFailedEvent(model, ex.getMessage(), ex.getCause(), source, CountryFailedEvent.INSERT_FAULTED);
        }
        return new CountryFailedEvent(model, null, ex, source, CountryFailedEvent.INSERT_INVALID);
    }

    public static final CountryEvent createUpdateFaultedEvent(IFxModelOptional<CountryDAO, CountryModel> target, Object source, Throwable ex) {
        CountryModel model = target.getFxRecordModel();
        if (null == model) {
            if (null != ex && ex instanceof OperationFailureException) {
                return new CountryFailedEvent(target.getDataAccessObject(), ex.getMessage(), ex.getCause(), source, CountryFailedEvent.UPDATE_FAULTED);
            }
            return new CountryFailedEvent(target.getDataAccessObject(), null, ex, source, CountryFailedEvent.UPDATE_FAULTED);
        }
        if (null != ex && ex instanceof OperationFailureException) {
            return new CountryFailedEvent(model, ex.getMessage(), ex.getCause(), source, CountryFailedEvent.UPDATE_FAULTED);
        }
        return new CountryFailedEvent(model, null, ex, source, CountryFailedEvent.UPDATE_FAULTED);
    }

    public static final CountryEvent createDeleteFaultedEvent(IFxModelOptional<CountryDAO, CountryModel> target, Object source, Throwable ex) {
        CountryModel model = target.getFxRecordModel();
        if (null == model) {
            if (null != ex && ex instanceof OperationFailureException) {
                return new CountryFailedEvent(target.getDataAccessObject(), ex.getMessage(), ex.getCause(), source, CountryFailedEvent.DELETE_FAULTED);
            }
            return new CountryFailedEvent(target.getDataAccessObject(), null, ex, source, CountryFailedEvent.DELETE_FAULTED);
        }
        if (null != ex && ex instanceof OperationFailureException) {
            return new CountryFailedEvent(model, ex.getMessage(), ex.getCause(), source, CountryFailedEvent.DELETE_FAULTED);
        }
        return new CountryFailedEvent(model, null, ex, source, CountryFailedEvent.DELETE_FAULTED);
    }

    public static final CountryEvent createInsertCanceledEvent(IFxModelOptional<CountryDAO, CountryModel> target, Object source, InterruptedException ex) {
        CountryModel model = target.getFxRecordModel();
        if (null == model) {
            if (null == ex) {
                return new CountryFailedEvent(target.getDataAccessObject(), null, null, source, CountryFailedEvent.INSERT_CANCELED);
            }
            if (null != ex.getCause()) {
                return new CountryFailedEvent(target.getDataAccessObject(), ex.getMessage(), ex.getCause(), source, CountryFailedEvent.INSERT_CANCELED);
            }
            return new CountryFailedEvent(target.getDataAccessObject(), ex.getMessage(), ex, source, CountryFailedEvent.INSERT_CANCELED);
        }
        if (null == ex) {
            return new CountryFailedEvent(model, null, null, source, CountryFailedEvent.INSERT_CANCELED);
        }
        if (null != ex.getCause()) {
            return new CountryFailedEvent(model, ex.getMessage(), ex.getCause(), source, CountryFailedEvent.INSERT_CANCELED);
        }
        return new CountryFailedEvent(model, ex.getMessage(), ex, source, CountryFailedEvent.INSERT_CANCELED);
    }

    public static final CountryEvent createUpdateCanceledEvent(IFxModelOptional<CountryDAO, CountryModel> target, Object source, InterruptedException ex) {
        CountryModel model = target.getFxRecordModel();
        if (null == model) {
            if (null == ex) {
                return new CountryFailedEvent(target.getDataAccessObject(), null, null, source, CountryFailedEvent.UPDATE_CANCELED);
            }
            if (null != ex.getCause()) {
                return new CountryFailedEvent(target.getDataAccessObject(), ex.getMessage(), ex.getCause(), source, CountryFailedEvent.UPDATE_CANCELED);
            }
            return new CountryFailedEvent(target.getDataAccessObject(), ex.getMessage(), ex, source, CountryFailedEvent.UPDATE_CANCELED);
        }
        if (null == ex) {
            return new CountryFailedEvent(model, null, null, source, CountryFailedEvent.UPDATE_CANCELED);
        }
        if (null != ex.getCause()) {
            return new CountryFailedEvent(model, ex.getMessage(), ex.getCause(), source, CountryFailedEvent.UPDATE_CANCELED);
        }
        return new CountryFailedEvent(model, ex.getMessage(), ex, source, CountryFailedEvent.UPDATE_CANCELED);
    }

    public static final CountryEvent createDeleteCanceledEvent(IFxModelOptional<CountryDAO, CountryModel> target, Object source, InterruptedException ex) {
        CountryModel model = target.getFxRecordModel();
        if (null == model) {
            if (null == ex) {
                return new CountryFailedEvent(target.getDataAccessObject(), null, null, source, CountryFailedEvent.DELETE_CANCELED);
            }
            if (null != ex.getCause()) {
                return new CountryFailedEvent(target.getDataAccessObject(), ex.getMessage(), ex.getCause(), source, CountryFailedEvent.DELETE_CANCELED);
            }
            return new CountryFailedEvent(target.getDataAccessObject(), ex.getMessage(), ex, source, CountryFailedEvent.DELETE_CANCELED);
        }
        if (null == ex) {
            return new CountryFailedEvent(model, null, null, source, CountryFailedEvent.DELETE_CANCELED);
        }
        if (null != ex.getCause()) {
            return new CountryFailedEvent(model, ex.getMessage(), ex.getCause(), source, CountryFailedEvent.DELETE_CANCELED);
        }
        return new CountryFailedEvent(model, ex.getMessage(), ex, source, CountryFailedEvent.DELETE_CANCELED);
    }

    public static final CountryEvent createInsertCanceledEvent(IFxModelOptional<CountryDAO, CountryModel> target, Object source) {
        return createInsertCanceledEvent(target, source, null);
    }

    public static final CountryEvent createUpdateCanceledEvent(IFxModelOptional<CountryDAO, CountryModel> target, Object source) {
        return createInsertCanceledEvent(target, source, null);
    }

    public static final CountryEvent createDeleteCanceledEvent(IFxModelOptional<CountryDAO, CountryModel> target, Object source) {
        return createInsertCanceledEvent(target, source, null);
    }

    protected CountryEvent(CountryEvent event, Object source, EventTarget target, EventType<? extends CountryEvent> eventType, DbOperationType operation) {
        super(event, source, target, eventType, operation);
    }

    protected CountryEvent(CountryEvent event, EventType<? extends CountryEvent> eventType, DbOperationType operation) {
        super(event, eventType, operation);
    }

    protected CountryEvent(CountryModel target, Object source, EventType<? extends CountryEvent> eventType, DbOperationType operation) {
        super(target, source, eventType, operation);
    }

    protected CountryEvent(CountryDAO target, Object source, EventType<? extends CountryEvent> eventType, DbOperationType operation) {
        super(target, source, eventType, operation);
    }

}
