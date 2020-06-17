package events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.DataAccessObject;
import scheduler.model.ui.FxRecordModel;


public abstract class DbValidatingEvent<M extends FxRecordModel<D>, D extends DataAccessObject> extends ModelEvent<M, D> {

    /**
     * Base {@link EventType} for all {@code DbValidatingEvent}s.
     */
    public static final EventType<DbValidatingEvent<? extends FxRecordModel<? extends DataAccessObject>, ? extends DataAccessObject>> DB_VALIDATING_EVENT
            = new EventType<>(ModelEvent.MODEL_EVENT, "SCHEDULER_DB_VALIDATING_EVENT");

    public static final <M extends FxRecordModel<D>, D extends DataAccessObject, E extends DbValidatingEvent<M, D>> E assertHandled(E event) {
        if (event.isHandled()) {
            return event;
        }
        throw new IllegalArgumentException();
    }
    
    public static final <M extends FxRecordModel<D>, D extends DataAccessObject, E extends DbValidatingEvent<M, D>> E assertInvalid(E event) {
        if (event.isHandled() && !event.isValid()) {
            return event;
        }
        throw new IllegalArgumentException();
    }
    
    public static final <M extends FxRecordModel<D>, D extends DataAccessObject, E extends DbValidatingEvent<M, D>> E assertValid(E event) {
        if (event.isValid()) {
            return event;
        }
        throw new IllegalArgumentException();
    }
    
    private final State state;
    private final DbOperationType operation;
    
    protected DbValidatingEvent(BeginOperationEvent<M, D> precedingEvent, EventType<? extends DbValidatingEvent<M, D>> newType) {
        super(BeginOperationEvent.assertNotFailed(precedingEvent), newType);
        state = new State();
        operation = precedingEvent.getOperation();
    }

    protected DbValidatingEvent(DbValidatingEvent<M, D> sourceEvent, Object newSource, EventTarget newTarget) {
        super(sourceEvent, newSource, newTarget);
        state = sourceEvent.state;
        operation = sourceEvent.operation;
    }

    protected DbValidatingEvent(Object source, EventTarget target, EventType<? extends DbValidatingEvent<M, D>> eventType, M model, DbOperationType operation) {
        super(source, target, eventType, model);
        state = new State();
        this.operation = operation;
    }

    protected DbValidatingEvent(Object source, EventTarget target, EventType<? extends DbValidatingEvent<M, D>> eventType, D dao, DbOperationType operation) {
        super(source, target, eventType, dao);
        state = new State();
        this.operation = operation;
    }

    public abstract <E extends OperationCompletedEvent<? extends M, ? extends D>> E toOperationCompletedEvent();

    public abstract <E extends OperationFailureEvent<? extends M, ? extends D>> E toOperationFaultEvent(Throwable fault, String message);

    public abstract <E extends OperationFailureEvent<? extends M, ? extends D>> E toOperationCanceledEvent(String message);

    public boolean isCanceled() {
        return state.canceled;
    }

    public boolean isValid() {
        return state.valid;
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
        state.setCanceled( message);
    }

    public void setInvalid(String message) {
        state.setCanceled( message);
    }

    public void setValid() {
        state.setValid();
    }
    
    public DbOperationType getOperation() {
        return operation;
    }

    private class State {
        private String message = "";
        private boolean canceled = false;
        private boolean handled = false;
        private boolean valid = false;
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
        
        synchronized void setInvalid(String message) {
            if (handled) {
                throw new IllegalStateException();
            }
            if (null == message || message.trim().isEmpty()) {
                this.message = "Validation failed";
            }
                
            handled = true;
        }
        
        synchronized void setValid() {
            if (handled) {
                throw new IllegalStateException();
            }
            handled = valid = true;
        }
    }
    
}
