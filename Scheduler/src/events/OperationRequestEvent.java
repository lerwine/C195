package events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.DataAccessObject;
import scheduler.model.ui.FxRecordModel;

/**
 * The base base class for cancelable {@link FxRecordModel} and {@link DataAccessObject} operation request events.
 * 
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
 * @param <M> The {@link FxRecordModel} type.
 * @param <D> The {@link DataAccessObject} type.
 */
public abstract class OperationRequestEvent<M extends FxRecordModel<D>, D extends DataAccessObject> extends ModelEvent<M, D> {

    /**
     * Base {@link EventType} for all {@code OperationRequestEvent}s.
     */
    public static final EventType<OperationRequestEvent<? extends FxRecordModel<? extends DataAccessObject>, ? extends DataAccessObject>> OP_REQUEST_EVENT
            = new EventType<>(ModelEvent.MODEL_EVENT, "SCHEDULER_OP_REQUEST_EVENT");

    public static final <M extends FxRecordModel<D>, D extends DataAccessObject, E extends OperationRequestEvent<M, D>> E assertHandled(E event) {
        if (event.isHandled()) {
            return event;
        }
        throw new IllegalArgumentException();
    }
    
    private final State state;
    
    protected OperationRequestEvent(OperationRequestEvent<M, D> sourceEvent, Object newSource, EventTarget newTarget) {
        super(sourceEvent, newSource, newTarget);
        state = sourceEvent.state;
    }
    
    protected OperationRequestEvent(Object source, EventTarget target, EventType<? extends OperationRequestEvent<M, D>> eventType, M model) {
        super(source, target, eventType, model);
        state = new State();
    }

    protected OperationRequestEvent(Object source, EventTarget target, EventType<? extends OperationRequestEvent<M, D>> eventType, D dao) {
        super(source, target, eventType, dao);
        state = new State();
    }

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
        state.setCanceled( message);
    }

    public void setHandled() {
        state.setHandled();
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
