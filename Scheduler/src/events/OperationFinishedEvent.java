package events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.DataAccessObject;
import scheduler.model.ui.FxRecordModel;

/**
 * The base base class for {@link FxRecordModel} and {@link DataAccessObject} operation completion events.
 * 
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
 * @param <M> The {@link FxRecordModel} type.
 * @param <D> The {@link DataAccessObject} type.
 */
public abstract class OperationFinishedEvent<M extends FxRecordModel<D>, D extends DataAccessObject> extends ModelEvent<M, D> {

    /**
     * Base {@link EventType} for all {@code OperationFinishedEvent}s.
     */
    public static final EventType<OperationFinishedEvent<? extends FxRecordModel<? extends DataAccessObject>, ? extends DataAccessObject>> OP_FINISHED_EVENT
            = new EventType<>(ModelEvent.MODEL_EVENT, "SCHEDULER_OP_FINISHED_EVENT");
    
    private final boolean successful;
    private final DbOperationType operation;

    protected OperationFinishedEvent(BeginOperationEvent<M, D> precedingEvent, EventType<? extends OperationFinishedEvent<M, D>> eventType, boolean successful) {
        super(precedingEvent, eventType);
        this.successful = successful;
        operation = precedingEvent.getOperation();
    }
    
    protected OperationFinishedEvent(DbValidatingEvent<M, D> precedingEvent, EventType<? extends OperationFinishedEvent<M, D>> eventType, boolean successful) {
        super(precedingEvent, eventType);
        this.successful = successful;
        operation = precedingEvent.getOperation();
    }
    
    protected OperationFinishedEvent(OperationFinishedEvent<M, D> sourceEvent, Object newSource, EventTarget newTarget) {
        super(sourceEvent, newSource, newTarget);
        successful = sourceEvent.successful;
        operation = sourceEvent.operation;
    }
    
    protected OperationFinishedEvent(Object source, EventTarget target, EventType<? extends OperationFinishedEvent<M, D>> eventType, M model, DbOperationType operation, boolean successful) {
        super(source, target, eventType, model);
        this.successful = successful;
        this.operation = operation;
    }

    protected OperationFinishedEvent(Object source, EventTarget target, EventType<? extends OperationFinishedEvent<M, D>> eventType, D dao, DbOperationType operation, boolean successful) {
        super(source, target, eventType, dao);
        this.successful = successful;
        this.operation = operation;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public DbOperationType getOperation() {
        return operation;
    }

}
