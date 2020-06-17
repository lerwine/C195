package events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.DataAccessObject;
import scheduler.model.ui.FxRecordModel;


public abstract class ModelDeleteRequestEvent<M extends FxRecordModel<D>, D extends DataAccessObject> extends OperationRequestEvent<M, D> {

    /**
     * Base {@link EventType} for all {@code ModelDeleteRequestEvent}s.
     */
    public static final EventType<ModelDeleteRequestEvent<? extends FxRecordModel<? extends DataAccessObject>, ? extends DataAccessObject>> DELETE_REQUEST_EVENT
            = new EventType<>(OperationRequestEvent.OP_REQUEST_EVENT, "SCHEDULER_DELETE_REQUEST_EVENT");

    protected ModelDeleteRequestEvent(ModelDeleteRequestEvent<M, D> sourceEvent, Object newSource, EventTarget newTarget) {
        super(sourceEvent, newSource, newTarget);
    }

    protected ModelDeleteRequestEvent(Object source, EventTarget target, EventType<? extends ModelDeleteRequestEvent<M, D>> eventType, M model) {
        super(source, target, eventType, model);
    }

    protected ModelDeleteRequestEvent(Object source, EventTarget target, EventType<? extends ModelDeleteRequestEvent<M, D>> eventType, D dao) {
        super(source, target, eventType, dao);
    }
    
    public abstract <E extends BeginOperationEvent<? extends M, ? extends D>> E toBeginOperationEvent();

}
