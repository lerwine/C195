package events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.DataAccessObject;
import scheduler.model.ui.FxRecordModel;


public abstract class ModelEditRequestEvent<M extends FxRecordModel<D>, D extends DataAccessObject> extends OperationRequestEvent<M, D> {

    /**
     * Base {@link EventType} for all {@code ModelEditRequestEvent}s.
     */
    public static final EventType<ModelEditRequestEvent<? extends FxRecordModel<? extends DataAccessObject>, ? extends DataAccessObject>> EDIT_REQUEST_EVENT
            = new EventType<>(OperationRequestEvent.OP_REQUEST_EVENT, "SCHEDULER_EDIT_REQUEST_EVENT");

    protected ModelEditRequestEvent(ModelEditRequestEvent<M, D> sourceEvent, Object newSource, EventTarget newTarget) {
        super(sourceEvent, newSource, newTarget);
    }

    protected ModelEditRequestEvent(Object source, EventTarget target, EventType<? extends ModelEditRequestEvent<M, D>> eventType, M model) {
        super(source, target, eventType, model);
    }

    protected ModelEditRequestEvent(Object source, EventTarget target, EventType<? extends ModelEditRequestEvent<M, D>> eventType, D dao) {
        super(source, target, eventType, dao);
    }

    public abstract <E extends BeginOperationEvent<? extends M, ? extends D>> E toBeginOperationEvent();

}
