package events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.DataAccessObject;
import scheduler.model.ui.FxRecordModel;


public abstract class OperationCompletedEvent<M extends FxRecordModel<D>, D extends DataAccessObject> extends OperationFinishedEvent<M, D> {

    /**
     * Base {@link EventType} for all {@code OperationCompletedEvent}s.
     */
    public static final EventType<OperationCompletedEvent<? extends FxRecordModel<? extends DataAccessObject>, ? extends DataAccessObject>> OP_COMPLETED_EVENT
            = new EventType<>(OperationFinishedEvent.OP_FINISHED_EVENT, "SCHEDULER_OP_COMPLETED_EVENT");

    protected OperationCompletedEvent(BeginOperationEvent<M, D> skippedEvent, EventType<? extends OperationCompletedEvent<M, D>> eventType) {
        super(BeginOperationEvent.assertNotFailed(skippedEvent), eventType, true);
    }

    protected OperationCompletedEvent(DbValidatingEvent<M, D> validatedEvent, EventType<? extends OperationCompletedEvent<M, D>> eventType) {
        super(DbValidatingEvent.assertValid(validatedEvent), eventType, true);
    }

    protected OperationCompletedEvent(OperationCompletedEvent<M, D> sourceEvent, Object newSource, EventTarget newTarget) {
        super(sourceEvent, newSource, newTarget);
    }

    protected OperationCompletedEvent(Object source, EventTarget target, EventType<? extends OperationCompletedEvent<M, D>> eventType, M model, DbOperationType operation) {
        super(source, target, eventType, model, operation, true);
    }

    protected OperationCompletedEvent(Object source, EventTarget target, EventType<? extends OperationCompletedEvent<M, D>> eventType, D dao, DbOperationType operation) {
        super(source, target, eventType, dao, operation, true);
    }

}
