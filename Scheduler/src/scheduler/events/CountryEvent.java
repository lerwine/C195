package scheduler.events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.CountryDAO;
import scheduler.dao.OperationFailureException;
import scheduler.model.RecordModelContext;
import scheduler.model.ui.CountryModel;

/**
 * Base {@link ModelEvent} for appointment events.
 * <h3>Event Registration</h3>
 * <dl>
 * <dt>{@link #COUNTRY_EVENT_TYPE "SCHEDULER_COUNTRY_EVENT"} &lArr; {@link ModelEvent#MODEL_EVENT_TYPE "SCHEDULER_MODEL_EVENT"} &lArr; {@link javafx.event.Event#ANY "EVENT"}</dt>
 * <dd>
 * <dl>
 * <dt>(inherit) {@link #COUNTRY_EVENT_TYPE "SCHEDULER_COUNTRY_OP_EVENT"}</dt>
 * <dd>
 * <dl>
 * <dt>(inherit) {@link CountrySuccessEvent#BASE_EVENT_NAME "SCHEDULER_COUNTRY_SUCCESS_EVENT"}</dt>
 * <dd>&rarr; {@link CountrySuccessEvent}</dd>
 * <dt>(inherit) {@link CountryFailedEvent#BASE_EVENT_NAME "SCHEDULER_COUNTRY_FAILED_EVENT"}</dt>
 * <dd>&rarr; {@link CountryFailedEvent}</dd>
 * </dl>
 * </dd>
 * </dl>
 * </dd>
 * </dl>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public abstract class CountryEvent extends ModelEvent<CountryDAO, CountryModel> {

    private static final long serialVersionUID = -6121391358660436488L;

    /**
     * Base {@link EventType} for all {@code CountryEvent}s.
     */
    public static final EventType<CountryEvent> COUNTRY_EVENT_TYPE = new EventType<>(MODEL_EVENT_TYPE, "SCHEDULER_COUNTRY_EVENT");

    /**
     * Base {@link EventType} for all change {@code CountryEvent}s.
     */
    public static final EventType<CountryEvent> CHANGE_EVENT_TYPE = new EventType<>(COUNTRY_EVENT_TYPE, "SCHEDULER_COUNTRY_CHANGE_EVENT");

    public static final boolean isSuccess(CountryEvent event) {
        return event instanceof CountrySuccessEvent;
    }

    public static final boolean isInvalid(CountryEvent event) {
        return event instanceof CountryFailedEvent && ((CountryFailedEvent) event).getFailKind() == FailKind.INVALID;
    }

    public static final boolean isCanceled(CountryEvent event) {
        return event instanceof CountryFailedEvent && ((CountryFailedEvent) event).getFailKind() == FailKind.CANCELED;
    }

    public static final boolean isFaulted(CountryEvent event) {
        return event instanceof CountryFailedEvent && ((CountryFailedEvent) event).getFailKind() == FailKind.FAULT;
    }

    public static final CountryEvent createInsertSuccessEvent(RecordModelContext<CountryDAO, CountryModel> target, Object source) {
        return new CountrySuccessEvent(target, source, CountrySuccessEvent.INSERT_SUCCESS);
    }

    public static final CountryEvent createUpdateSuccessEvent(RecordModelContext<CountryDAO, CountryModel> target, Object source) {
        return new CountrySuccessEvent(target, source, CountrySuccessEvent.UPDATE_SUCCESS);
    }

    public static final CountryEvent createDeleteSuccessEvent(RecordModelContext<CountryDAO, CountryModel> target, Object source) {
        return new CountrySuccessEvent(target, source, CountrySuccessEvent.DELETE_SUCCESS);
    }

    public static final CountryEvent createInsertInvalidEvent(RecordModelContext<CountryDAO, CountryModel> target, Object source, String message) {
        return new CountryFailedEvent(target, message, null, source, CountryFailedEvent.INSERT_INVALID);
    }

    public static final CountryEvent createUpdateInvalidEvent(RecordModelContext<CountryDAO, CountryModel> target, Object source, String message) {
        return new CountryFailedEvent(target, message, null, source, CountryFailedEvent.UPDATE_INVALID);
    }

    public static final CountryEvent createDeleteInvalidEvent(RecordModelContext<CountryDAO, CountryModel> target, Object source, String message) {
        return new CountryFailedEvent(target, message, null, source, CountryFailedEvent.DELETE_INVALID);
    }

    public static final CountryEvent createInsertFaultedEvent(RecordModelContext<CountryDAO, CountryModel> target, Object source, Throwable ex) {
        if (null != ex && ex instanceof OperationFailureException) {
            return new CountryFailedEvent(target, ex.getMessage(), ex.getCause(), source, CountryFailedEvent.INSERT_FAULTED);
        }
        return new CountryFailedEvent(target, null, ex, source, CountryFailedEvent.INSERT_INVALID);
    }

    public static final CountryEvent createUpdateFaultedEvent(RecordModelContext<CountryDAO, CountryModel> target, Object source, Throwable ex) {
        if (null != ex && ex instanceof OperationFailureException) {
            return new CountryFailedEvent(target, ex.getMessage(), ex.getCause(), source, CountryFailedEvent.UPDATE_FAULTED);
        }
        return new CountryFailedEvent(target, null, ex, source, CountryFailedEvent.UPDATE_FAULTED);
    }

    public static final CountryEvent createDeleteFaultedEvent(RecordModelContext<CountryDAO, CountryModel> target, Object source, Throwable ex) {
        if (null != ex && ex instanceof OperationFailureException) {
            return new CountryFailedEvent(target, ex.getMessage(), ex.getCause(), source, CountryFailedEvent.DELETE_FAULTED);
        }
        return new CountryFailedEvent(target, null, ex, source, CountryFailedEvent.DELETE_FAULTED);
    }

    public static final CountryEvent createInsertCanceledEvent(RecordModelContext<CountryDAO, CountryModel> target, Object source, InterruptedException ex) {
        if (null == ex) {
            return new CountryFailedEvent(target, null, null, source, CountryFailedEvent.INSERT_CANCELED);
        }
        if (null != ex.getCause()) {
            return new CountryFailedEvent(target, ex.getMessage(), ex.getCause(), source, CountryFailedEvent.INSERT_CANCELED);
        }
        return new CountryFailedEvent(target, ex.getMessage(), ex, source, CountryFailedEvent.INSERT_CANCELED);
    }

    public static final CountryEvent createUpdateCanceledEvent(RecordModelContext<CountryDAO, CountryModel> target, Object source, InterruptedException ex) {
        if (null == ex) {
            return new CountryFailedEvent(target, null, null, source, CountryFailedEvent.UPDATE_CANCELED);
        }
        if (null != ex.getCause()) {
            return new CountryFailedEvent(target, ex.getMessage(), ex.getCause(), source, CountryFailedEvent.UPDATE_CANCELED);
        }
        return new CountryFailedEvent(target, ex.getMessage(), ex, source, CountryFailedEvent.UPDATE_CANCELED);
    }

    public static final CountryEvent createDeleteCanceledEvent(RecordModelContext<CountryDAO, CountryModel> target, Object source, InterruptedException ex) {
        if (null == ex) {
            return new CountryFailedEvent(target, null, null, source, CountryFailedEvent.DELETE_CANCELED);
        }
        if (null != ex.getCause()) {
            return new CountryFailedEvent(target, ex.getMessage(), ex.getCause(), source, CountryFailedEvent.DELETE_CANCELED);
        }
        return new CountryFailedEvent(target, ex.getMessage(), ex, source, CountryFailedEvent.DELETE_CANCELED);
    }

    public static final CountryEvent createInsertCanceledEvent(RecordModelContext<CountryDAO, CountryModel> target, Object source) {
        return createInsertCanceledEvent(target, source, null);
    }

    public static final CountryEvent createUpdateCanceledEvent(RecordModelContext<CountryDAO, CountryModel> target, Object source) {
        return createUpdateCanceledEvent(target, source, null);
    }

    public static final CountryEvent createDeleteCanceledEvent(RecordModelContext<CountryDAO, CountryModel> target, Object source) {
        return createDeleteCanceledEvent(target, source, null);
    }

    protected CountryEvent(CountryEvent event, Object source, EventTarget target, EventType<? extends CountryEvent> eventType, DbOperationType operation) {
        super(event, source, target, eventType, operation);
    }

    protected CountryEvent(CountryEvent event, EventType<? extends CountryEvent> eventType, DbOperationType operation) {
        super(event, eventType, operation);
    }

    protected CountryEvent(RecordModelContext<CountryDAO, CountryModel> target, Object source, EventType<? extends CountryEvent> eventType, DbOperationType operation) {
        super(target, source, eventType, operation);
    }

}
