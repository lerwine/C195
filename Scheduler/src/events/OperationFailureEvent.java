package events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.DataAccessObject;
import scheduler.model.ui.FxRecordModel;

/**
 * The base base class for {@link FxRecordModel} and {@link DataAccessObject} operation failure events.
 * 
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
 * @param <M> The {@link FxRecordModel} type.
 * @param <D> The {@link DataAccessObject} type.
 */
public abstract class OperationFailureEvent<M extends FxRecordModel<D>, D extends DataAccessObject> extends OperationFinishedEvent<M, D> {

    /**
     * Base {@link EventType} for all {@code OperationFailureEvent}s.
     */
    public static final EventType<OperationFailureEvent<? extends FxRecordModel<? extends DataAccessObject>, ? extends DataAccessObject>> OP_FAILURE_EVENT
            = new EventType<>(OperationFinishedEvent.OP_FINISHED_EVENT, "SCHEDULER_OP_FAILURE_EVENT");
    
    private final boolean canceled;
    private final Throwable fault;
    private final String message;

    protected OperationFailureEvent(OperationFailureEvent<M, D> sourceEvent, Object newSource, EventTarget newTarget) {
        super(sourceEvent, newSource, newTarget);
        canceled = sourceEvent.canceled;
        fault = sourceEvent.fault;
        message = sourceEvent.message;
    }

    protected OperationFailureEvent(Object source, EventTarget target, EventType<? extends OperationFailureEvent<M, D>> eventType, M model, Throwable fault, DbOperationType operation, String message) {
        super(source, target, eventType, model, operation, false);
        this.canceled = false;
        this.fault = fault;
        if ((null == message || message.trim().isEmpty()) && (null == fault || (null == (message = fault.getMessage()) || message.trim().isEmpty()))) {
            this.message = "Unexpected error";
        } else {
            this.message = message;
        }
    }

    protected OperationFailureEvent(Object source, EventTarget target, EventType<? extends OperationFailureEvent<M, D>> eventType, M model, DbOperationType operation, boolean canceled, String message) {
        super(source, target, eventType, model, operation, false);
        this.canceled = canceled;
        this.message = (null == message || message.trim().isEmpty()) ? ((canceled) ? "Operaton canceled" : "Unexpected error") : message;
        fault = null;
    }

    protected OperationFailureEvent(Object source, EventTarget target, EventType<? extends OperationFailureEvent<M, D>> eventType, D dao, Throwable fault, DbOperationType operation, String message) {
        super(source, target, eventType, dao, operation, false);
        this.canceled = false;
        this.fault = fault;
        if ((null == message || message.trim().isEmpty()) && (null == fault || (null == (message = fault.getMessage()) || message.trim().isEmpty()))) {
            this.message = "Unexpected error";
        } else {
            this.message = message;
        }
    }

    protected OperationFailureEvent(Object source, EventTarget target, EventType<? extends OperationFailureEvent<M, D>> eventType, D dao, DbOperationType operation, boolean canceled, String message) {
        super(source, target, eventType, dao, operation, false);
        this.canceled = canceled;
        this.message = (null == message || message.trim().isEmpty()) ? ((canceled) ? "Operaton canceled" : "Unexpected error") : message;
        fault = null;
    }

    protected OperationFailureEvent(BeginOperationEvent<M, D> failedEvent, EventType<? extends OperationFailureEvent<M, D>> eventType) {
        super(BeginOperationEvent.assertFailed(failedEvent), eventType, false);
        canceled = failedEvent.isCanceled();
        fault = failedEvent.getFault();
        message = failedEvent.getMessage();
    }

    protected OperationFailureEvent(DbValidatingEvent<M, D> failedEvent, EventType<? extends OperationFailureEvent<M, D>> eventType) {
        super(DbValidatingEvent.assertInvalid(failedEvent), eventType, false);
        canceled = failedEvent.isCanceled();
        fault = failedEvent.getFault();
        message = failedEvent.getMessage();
    }

    public boolean isCanceled() {
        return canceled;
    }

    public Throwable getFault() {
        return fault;
    }

    public String getMessage() {
        return message;
    }

}
