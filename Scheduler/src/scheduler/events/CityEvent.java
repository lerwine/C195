package scheduler.events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.CityDAO;
import scheduler.dao.OperationFailureException;
import scheduler.model.fx.CityModel;

/**
 * Base {@link ModelEvent} for appointment events.
 * <h3>Event Registration</h3>
 * <dl>
 * <dt>{@link #CITY_EVENT_TYPE "SCHEDULER_CITY_EVENT"} &lArr; {@link ModelEvent#MODEL_EVENT_TYPE "SCHEDULER_MODEL_EVENT"} &lArr;
 * {@link javafx.event.Event#ANY "EVENT"}</dt>
 * <dd>
 * <dl>
 * <dt>(inherit) {@link #CITY_EVENT_TYPE "SCHEDULER_CITY_OP_EVENT"}</dt>
 * <dd>
 * <dl>
 * <dt>(inherit) {@link CitySuccessEvent#BASE_EVENT_NAME "SCHEDULER_CITY_SUCCESS_EVENT"}</dt>
 * <dd>&rarr; {@link CitySuccessEvent}</dd>
 * <dt>(inherit) {@link CityFailedEvent#BASE_EVENT_NAME "SCHEDULER_CITY_FAILED_EVENT"}</dt>
 * <dd>&rarr; {@link CityFailedEvent}</dd>
 * </dl>
 * </dd>
 * </dl>
 * </dd>
 * </dl>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public abstract class CityEvent extends ModelEvent<CityDAO, CityModel> {

    private static final long serialVersionUID = -6996428374286059723L;

    /**
     * Base {@link EventType} for all {@code CityEvent}s.
     */
    public static final EventType<CityEvent> CITY_EVENT_TYPE = new EventType<>(MODEL_EVENT_TYPE, "SCHEDULER_CITY_EVENT");

    /**
     * Base {@link EventType} for all change {@code CityEvent}s.
     */
    public static final EventType<CityEvent> CHANGE_EVENT_TYPE = new EventType<>(CITY_EVENT_TYPE, "SCHEDULER_CITY_CHANGE_EVENT");

    public static final boolean isSuccess(CityEvent event) {
        return event instanceof CitySuccessEvent;
    }

    public static final boolean isInvalid(CityEvent event) {
        return event instanceof CityFailedEvent && ((CityFailedEvent) event).getFailKind() == FailKind.INVALID;
    }

    public static final boolean isCanceled(CityEvent event) {
        return event instanceof CityFailedEvent && ((CityFailedEvent) event).getFailKind() == FailKind.CANCELED;
    }

    public static final boolean isFaulted(CityEvent event) {
        return event instanceof CityFailedEvent && ((CityFailedEvent) event).getFailKind() == FailKind.FAULT;
    }

    public static final CityEvent createInsertSuccessEvent(CityModel target, Object source) {
        return new CitySuccessEvent(target, source, CitySuccessEvent.INSERT_SUCCESS);
    }

    public static final CityEvent createUpdateSuccessEvent(CityModel target, Object source) {
        return new CitySuccessEvent(target, source, CitySuccessEvent.UPDATE_SUCCESS);
    }

    public static final CityEvent createDeleteSuccessEvent(CityModel target, Object source) {
        return new CitySuccessEvent(target, source, CitySuccessEvent.DELETE_SUCCESS);
    }

    public static final CityEvent createInsertInvalidEvent(CityModel target, Object source, String message) {
        return new CityFailedEvent(target, message, null, source, CityFailedEvent.INSERT_INVALID);
    }

    public static final CityEvent createUpdateInvalidEvent(CityModel target, Object source, String message) {
        return new CityFailedEvent(target, message, null, source, CityFailedEvent.UPDATE_INVALID);
    }

    public static final CityEvent createDeleteInvalidEvent(CityModel target, Object source, String message) {
        return new CityFailedEvent(target, message, null, source, CityFailedEvent.DELETE_INVALID);
    }

    public static final CityEvent createInsertFaultedEvent(CityModel target, Object source, Throwable ex) {
        if (null != ex && ex instanceof OperationFailureException) {
            return new CityFailedEvent(target, ex.getMessage(), ex.getCause(), source, CityFailedEvent.INSERT_FAULTED);
        }
        return new CityFailedEvent(target, null, ex, source, CityFailedEvent.INSERT_INVALID);
    }

    public static final CityEvent createUpdateFaultedEvent(CityModel target, Object source, Throwable ex) {
        if (null != ex && ex instanceof OperationFailureException) {
            return new CityFailedEvent(target, ex.getMessage(), ex.getCause(), source, CityFailedEvent.UPDATE_FAULTED);
        }
        return new CityFailedEvent(target, null, ex, source, CityFailedEvent.UPDATE_FAULTED);
    }

    public static final CityEvent createDeleteFaultedEvent(CityModel target, Object source, Throwable ex) {
        if (null != ex && ex instanceof OperationFailureException) {
            return new CityFailedEvent(target, ex.getMessage(), ex.getCause(), source, CityFailedEvent.DELETE_FAULTED);
        }
        return new CityFailedEvent(target, null, ex, source, CityFailedEvent.DELETE_FAULTED);
    }

    public static final CityEvent createInsertCanceledEvent(CityModel target, Object source, InterruptedException ex) {
        if (null == ex) {
            return new CityFailedEvent(target, null, null, source, CityFailedEvent.INSERT_CANCELED);
        }
        if (null != ex.getCause()) {
            return new CityFailedEvent(target, ex.getMessage(), ex.getCause(), source, CityFailedEvent.INSERT_CANCELED);
        }
        return new CityFailedEvent(target, ex.getMessage(), ex, source, CityFailedEvent.INSERT_CANCELED);
    }

    public static final CityEvent createUpdateCanceledEvent(CityModel target, Object source, InterruptedException ex) {
        if (null == ex) {
            return new CityFailedEvent(target, null, null, source, CityFailedEvent.UPDATE_CANCELED);
        }
        if (null != ex.getCause()) {
            return new CityFailedEvent(target, ex.getMessage(), ex.getCause(), source, CityFailedEvent.UPDATE_CANCELED);
        }
        return new CityFailedEvent(target, ex.getMessage(), ex, source, CityFailedEvent.UPDATE_CANCELED);
    }

    public static final CityEvent createDeleteCanceledEvent(CityModel target, Object source, InterruptedException ex) {
        if (null == ex) {
            return new CityFailedEvent(target, null, null, source, CityFailedEvent.DELETE_CANCELED);
        }
        if (null != ex.getCause()) {
            return new CityFailedEvent(target, ex.getMessage(), ex.getCause(), source, CityFailedEvent.DELETE_CANCELED);
        }
        return new CityFailedEvent(target, ex.getMessage(), ex, source, CityFailedEvent.DELETE_CANCELED);
    }

    public static final CityEvent createInsertCanceledEvent(CityModel target, Object source) {
        return createInsertCanceledEvent(target, source, null);
    }

    public static final CityEvent createUpdateCanceledEvent(CityModel target, Object source) {
        return createUpdateCanceledEvent(target, source, null);
    }

    public static final CityEvent createDeleteCanceledEvent(CityModel target, Object source) {
        return createDeleteCanceledEvent(target, source, null);
    }

    public static CityEvent createInsertInvalidEvent(CityModel target, Object source, CountryFailedEvent event) {
        return new CityFailedEvent(target, source, CityFailedEvent.INSERT_INVALID, event);
    }

    public static CityEvent createUpdateInvalidEvent(CityModel target, Object source, CountryFailedEvent event) {
        return new CityFailedEvent(target, source, CityFailedEvent.UPDATE_INVALID, event);
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

}
