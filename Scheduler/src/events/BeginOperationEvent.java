package events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.DataAccessObject;
import scheduler.model.ui.FxRecordModel;

/**
 * The base base class for cancelable {@link FxRecordModel} and {@link DataAccessObject} begin operation events.
 * 
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
 * @param <M> The {@link FxRecordModel} type.
 * @param <D> The {@link DataAccessObject} type.
 */
public abstract class BeginOperationEvent<M extends FxRecordModel<D>, D extends DataAccessObject> extends ModelEvent<M, D> {

    /**
     * Base {@link EventType} for all {@code BeginOperationEvent}s.
     */
    public static final EventType<BeginOperationEvent<? extends FxRecordModel<? extends DataAccessObject>, ? extends DataAccessObject>> BEGIN_OP_EVENT
            = new EventType<>(ModelEvent.MODEL_EVENT, "SCHEDULER_BEGIN_OP_EVENT");

    public static final <M extends FxRecordModel<D>, D extends DataAccessObject, E extends BeginOperationEvent<M, D>> E assertHandled(E event) {
        if (event.isHandled()) {
            return event;
        }
        throw new IllegalArgumentException();
    }
    
    public static final <M extends FxRecordModel<D>, D extends DataAccessObject, E extends BeginOperationEvent<M, D>> E assertNotFailed(E event) {
        if (event.isHandled() && event.getMessage().isEmpty()) {
            return event;
        }
        throw new IllegalArgumentException();
    }
    
    public static final <M extends FxRecordModel<D>, D extends DataAccessObject, E extends BeginOperationEvent<M, D>> E assertFailed(E event) {
        if (event.isHandled() && !event.getMessage().isEmpty()) {
            return event;
        }
        throw new IllegalArgumentException();
    }
    
    private final State state;
    private final DbOperationType operation;
    
    protected BeginOperationEvent(ModelEditRequestEvent<M, D> precedingEvent, EventType<? extends BeginOperationEvent<M, D>> eventType) {
        super(precedingEvent, eventType);
        state = new State();
        operation = DbOperationType.UPDATE;
    }
    
    protected BeginOperationEvent(ModelDeleteRequestEvent<M, D> precedingEvent, EventType<? extends BeginOperationEvent<M, D>> eventType) {
        super(precedingEvent, eventType);
        state = new State();
        operation = DbOperationType.DELETE;
    }
    
    protected BeginOperationEvent(BeginOperationEvent<M, D> sourceEvent, Object newSource, EventTarget newTarget) {
        super(sourceEvent, newSource, newTarget);
        state = sourceEvent.state;
        operation = sourceEvent.operation;
    }
    
    protected BeginOperationEvent(Object source, EventTarget target, EventType<? extends BeginOperationEvent<M, D>> eventType, M model, DbOperationType operation) {
        super(source, target, eventType, model);
        state = new State();
        this.operation = operation;
    }

    protected BeginOperationEvent(Object source, EventTarget target, EventType<? extends BeginOperationEvent<M, D>> eventType, D dao, DbOperationType operation) {
        super(source, target, eventType, dao);
        state = new State();
        this.operation = operation;
    }

    public abstract <E extends DbValidatingEvent<? extends M, ? extends D>> E toDbValidatingEvent();

    public abstract <E extends OperationFailureEvent<? extends M, ? extends D>> E toOperationFaultEvent(Throwable fault, String message);

    public abstract <E extends OperationFailureEvent<? extends M, ? extends D>> E toOperationCanceledEvent(String message);

    public boolean isCanceled() {
        return state.canceled;
    }

    public boolean isHandled() {
        return state.handled;
    }

    public String getMessage() {
        return state.message;
    }

    public Throwable getFault() {
        return state.fault;
    }

    public void setFault(Throwable fault, String message) {
        state.setFault(fault, message);
    }

    public void setCanceled(String message) {
        state.setCanceled(message);
    }

    public void setHandled() {
        state.setHandled();
    }
    
    public DbOperationType getOperation() {
        return operation;
    }

    private class State {
        private String message = "";
        private boolean canceled = false;
        private boolean handled = false;
        private Throwable fault = null;
        synchronized void setFault(Throwable fault, String message) {
            if (handled) {
                throw new IllegalStateException();
            }
            this.fault = fault;
            if ((null == message || message.trim().isEmpty()) && (null == fault || (null == (message = fault.getMessage()) || message.trim().isEmpty()))) {
                this.message = "Unexpected error";
            }
            handled = true;
        }

        synchronized void setCanceled(String message) {
            if (handled) {
                throw new IllegalStateException();
            }
            if (null == message || message.trim().isEmpty()) {
                this.message = "Operation canceled";
            }
                
            canceled = handled = true;
        }
        
        synchronized void setHandled() {
            handled = true;
        }
    }
    
}
