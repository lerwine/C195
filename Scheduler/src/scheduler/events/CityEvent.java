package scheduler.events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.CityDAO;
import scheduler.dao.OperationFailureException;
import scheduler.model.RecordModelContext;
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

    public static final CityEvent createInsertSuccessEvent(RecordModelContext<CityDAO, CityModel> target, Object source) {
        return new CitySuccessEvent(target, source, CitySuccessEvent.INSERT_SUCCESS);
    }

    public static final CityEvent createUpdateSuccessEvent(RecordModelContext<CityDAO, CityModel> target, Object source) {
        return new CitySuccessEvent(target, source, CitySuccessEvent.UPDATE_SUCCESS);
    }

    public static final CityEvent createDeleteSuccessEvent(RecordModelContext<CityDAO, CityModel> target, Object source) {
        return new CitySuccessEvent(target, source, CitySuccessEvent.DELETE_SUCCESS);
    }

    public static final CityEvent createInsertInvalidEvent(RecordModelContext<CityDAO, CityModel> target, Object source, String message) {
        return new CityFailedEvent(target, message, null, source, CityFailedEvent.INSERT_INVALID);
    }

    public static final CityEvent createUpdateInvalidEvent(RecordModelContext<CityDAO, CityModel> target, Object source, String message) {
        return new CityFailedEvent(target, message, null, source, CityFailedEvent.UPDATE_INVALID);
    }

    public static final CityEvent createDeleteInvalidEvent(RecordModelContext<CityDAO, CityModel> target, Object source, String message) {
        return new CityFailedEvent(target, message, null, source, CityFailedEvent.DELETE_INVALID);
    }

    public static final CityEvent createInsertFaultedEvent(RecordModelContext<CityDAO, CityModel> target, Object source, Throwable ex) {
        if (null != ex && ex instanceof OperationFailureException) {
            return new CityFailedEvent(target, ex.getMessage(), ex.getCause(), source, CityFailedEvent.INSERT_FAULTED);
        }
        return new CityFailedEvent(target, null, ex, source, CityFailedEvent.INSERT_INVALID);
    }

    public static final CityEvent createUpdateFaultedEvent(RecordModelContext<CityDAO, CityModel> target, Object source, Throwable ex) {
        if (null != ex && ex instanceof OperationFailureException) {
            return new CityFailedEvent(target, ex.getMessage(), ex.getCause(), source, CityFailedEvent.UPDATE_FAULTED);
        }
        return new CityFailedEvent(target, null, ex, source, CityFailedEvent.UPDATE_FAULTED);
    }

    public static final CityEvent createDeleteFaultedEvent(RecordModelContext<CityDAO, CityModel> target, Object source, Throwable ex) {
        if (null != ex && ex instanceof OperationFailureException) {
            return new CityFailedEvent(target, ex.getMessage(), ex.getCause(), source, CityFailedEvent.DELETE_FAULTED);
        }
        return new CityFailedEvent(target, null, ex, source, CityFailedEvent.DELETE_FAULTED);
    }

    public static final CityEvent createInsertCanceledEvent(RecordModelContext<CityDAO, CityModel> target, Object source, InterruptedException ex) {
        if (null == ex) {
            return new CityFailedEvent(target, null, null, source, CityFailedEvent.INSERT_CANCELED);
        }
        if (null != ex.getCause()) {
            return new CityFailedEvent(target, ex.getMessage(), ex.getCause(), source, CityFailedEvent.INSERT_CANCELED);
        }
        return new CityFailedEvent(target, ex.getMessage(), ex, source, CityFailedEvent.INSERT_CANCELED);
    }

    public static final CityEvent createUpdateCanceledEvent(RecordModelContext<CityDAO, CityModel> target, Object source, InterruptedException ex) {
        if (null == ex) {
            return new CityFailedEvent(target, null, null, source, CityFailedEvent.UPDATE_CANCELED);
        }
        if (null != ex.getCause()) {
            return new CityFailedEvent(target, ex.getMessage(), ex.getCause(), source, CityFailedEvent.UPDATE_CANCELED);
        }
        return new CityFailedEvent(target, ex.getMessage(), ex, source, CityFailedEvent.UPDATE_CANCELED);
    }

    public static final CityEvent createDeleteCanceledEvent(RecordModelContext<CityDAO, CityModel> target, Object source, InterruptedException ex) {
        if (null == ex) {
            return new CityFailedEvent(target, null, null, source, CityFailedEvent.DELETE_CANCELED);
        }
        if (null != ex.getCause()) {
            return new CityFailedEvent(target, ex.getMessage(), ex.getCause(), source, CityFailedEvent.DELETE_CANCELED);
        }
        return new CityFailedEvent(target, ex.getMessage(), ex, source, CityFailedEvent.DELETE_CANCELED);
    }

    public static final CityEvent createInsertCanceledEvent(RecordModelContext<CityDAO, CityModel> target, Object source) {
        return createInsertCanceledEvent(target, source, null);
    }

    public static final CityEvent createUpdateCanceledEvent(RecordModelContext<CityDAO, CityModel> target, Object source) {
        return createUpdateCanceledEvent(target, source, null);
    }

    public static final CityEvent createDeleteCanceledEvent(RecordModelContext<CityDAO, CityModel> target, Object source) {
        return createDeleteCanceledEvent(target, source, null);
    }

    public static CityEvent createInsertInvalidEvent(RecordModelContext<CityDAO, CityModel> target, Object source, CountryFailedEvent event) {
        return new CityFailedEvent(target, source, CityFailedEvent.INSERT_INVALID, event);
    }

    public static CityEvent createUpdateInvalidEvent(RecordModelContext<CityDAO, CityModel> target, Object source, CountryFailedEvent event) {
        return new CityFailedEvent(target, source, CityFailedEvent.UPDATE_INVALID, event);
    }

    protected CityEvent(CityEvent event, Object source, EventTarget target, EventType<? extends CityEvent> eventType, DbOperationType operation) {
        super(event, source, target, eventType, operation);
    }

    protected CityEvent(CityEvent event, EventType<? extends CityEvent> eventType, DbOperationType operation) {
        super(event, eventType, operation);
    }

    protected CityEvent(RecordModelContext<CityDAO, CityModel> target, Object source, EventType<? extends CityEvent> eventType, DbOperationType operation) {
        super(target, source, eventType, operation);
    }

}
