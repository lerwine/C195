package scheduler.events;

import java.util.Objects;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.DataAccessObject;
import scheduler.model.ui.FxRecordModel;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <D> The type of {@link DataAccessObject}.
 * @param <M> The type of {@link FxRecordModel};
 */
public abstract class ModelEvent<D extends DataAccessObject, M extends FxRecordModel<D>> extends Event implements IModelEvent<D, M> {

    private static final long serialVersionUID = -6832461936768738020L;

    /**
     * Base {@link EventType} for all {@code ModelEvent}s.
     */
    public static final EventType<ModelEvent<? extends DataAccessObject, ? extends FxRecordModel<? extends DataAccessObject>>> MODEL_EVENT_TYPE = 
            new EventType<>(ANY, "SCHEDULER_MODEL_EVENT");

    private State state;

    protected ModelEvent(ModelEvent<D, M> event, Object source, EventTarget target, EventType<? extends ModelEvent<D, M>> eventType) {
        super(source, target, eventType);
        state = event.state;
    }
    
    protected ModelEvent(ModelEvent<D, M> event, EventType<? extends ModelEvent<D, M>> eventType) {
        super(event.getSource(), event.getTarget(), eventType);
        state = event.state;
    }
    
    protected ModelEvent(M fxRecordModel, Object source, EventTarget target, EventType<? extends ModelEvent<D, M>> eventType) {
        super(source, target, eventType);
        (state = new State(fxRecordModel.dataObject())).fxRecordModel = fxRecordModel;
    }
    
    protected ModelEvent(D dao, Object source, EventTarget target, EventType<? extends ModelEvent<D, M>> eventType) {
        super(source, target, eventType);
        state = new State(dao);
    }
    
    @Override
    public D getDataAccessObject() {
        return state.dataAccessObject;
    }
    
    @Override
    public M getFxRecordModel() {
        return state.fxRecordModel;
    }
    
    @Override
    public void setFxRecordModel(M fxRecordModel) {
        state.setFxRecordModel(fxRecordModel);
    }
    
    private class State {
        private final D dataAccessObject;
        private M fxRecordModel;
        private State(D dataAccessObject) {
            this.dataAccessObject = Objects.requireNonNull(dataAccessObject);
        }
        private synchronized void setFxRecordModel(M fxRecordModel) {
            if (null != this.fxRecordModel) {
                if (null != fxRecordModel && fxRecordModel == this.fxRecordModel) {
                    return;
                }
                throw new IllegalStateException("Model has already been set");
            }
            if (dataAccessObject != fxRecordModel.dataObject()) {
                throw new IllegalArgumentException("Model is for a different data access object");
            }
            this.fxRecordModel = fxRecordModel;
        }
    }

    @Override
    public Event copyFor(Object newSource, EventTarget newTarget) {
        ModelEvent<D, M> copy = (ModelEvent<D, M>)super.copyFor(newSource, newTarget);
        copy.state = state;
        return copy;
    }
    
}
