package scheduler.events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.DataAccessObject;
import scheduler.model.RecordModelContext;
import scheduler.model.ui.FxRecordModel;

public abstract class OperationRequestEvent<D extends DataAccessObject, M extends FxRecordModel<D>> extends ModelEvent<D, M> {

    private static final long serialVersionUID = 6645421544057756121L;

    /**
     * Base {@link EventType} for all {@code OperationRequestEvent}s.
     */
    public static final EventType<OperationRequestEvent<? extends DataAccessObject, ? extends FxRecordModel<? extends DataAccessObject>>> OP_REQUEST_EVENT
            = new EventType<>(MODEL_EVENT_TYPE, "SCHEDULER_OP_REQUEST_EVENT");

    protected OperationRequestEvent(ModelEvent<D, M> event, Object source, EventTarget target, EventType<? extends OperationRequestEvent<D, M>> eventType, boolean isDelete) {
        super(event, source, target, eventType, (isDelete) ? DbOperationType.DB_DELETE : DbOperationType.NONE);
    }

    protected OperationRequestEvent(ModelEvent<D, M> event, EventType<? extends OperationRequestEvent<D, M>> eventType, boolean isDelete) {
        super(event, eventType, (isDelete) ? DbOperationType.DB_DELETE : DbOperationType.NONE);
    }

    protected OperationRequestEvent(RecordModelContext<D, M> target, Object source, EventType<? extends OperationRequestEvent<D, M>> eventType, boolean isDelete) {
        super(target, source, eventType, (isDelete) ? DbOperationType.DB_DELETE : DbOperationType.NONE);
    }

    protected OperationRequestEvent(M target, Object source, EventType<? extends OperationRequestEvent<D, M>> eventType, boolean isDelete) {
        super(target, source, eventType, (isDelete) ? DbOperationType.DB_DELETE : DbOperationType.NONE);
    }

    public final boolean isEdit() {
        return getOperation() == DbOperationType.NONE;
    }
}
