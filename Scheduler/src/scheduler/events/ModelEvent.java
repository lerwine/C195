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
 * @param <M> The type of {@link FxRecordModel}.
 */
public abstract class ModelEvent<D extends DataAccessObject, M extends FxRecordModel<D>> extends Event implements IModelEvent<D, M> {

    private static final long serialVersionUID = -6832461936768738020L;

    /**
     * Base {@link EventType} for all {@code ModelEvent}s.
     */
    public static final EventType<ModelEvent<? extends DataAccessObject, ? extends FxRecordModel<? extends DataAccessObject>>> MODEL_EVENT_TYPE
            = new EventType<>(ANY, "SCHEDULER_MODEL_EVENT");

    public static final String getMessage(ModelEvent<? extends DataAccessObject, ? extends FxRecordModel<? extends DataAccessObject>> event) {
        if (event instanceof ModelFailedEvent) {
            return ((ModelFailedEvent<? extends DataAccessObject, ? extends FxRecordModel<? extends DataAccessObject>>) event).getMessage();
        }
        return "";
    }

    private State state;

    /**
     * Creates a new {@link ModelEvent} that shares the same {@link DataAccessObject} and {@link FxRecordModel} as another {@link ModelEvent}.
     *
     * @param event The event that will share the same {@link DataAccessObject} and {@link FxRecordModel}.
     * @param source The object which sent the event or {@code null} to use the same source as the {@code event} parameter.
     * @param target The target to associate with the event or {@code null} to use the same target as the {@code event} parameter.
     * @param eventType The event type.
     * @param operation The database operation associated with the event {@code null} to use the same operation as the {@code event} parameter.
     * @throws NullPointerException if {@code event} or {@code eventType} is {@code null}.
     */
    protected ModelEvent(ModelEvent<D, M> event, Object source, EventTarget target, EventType<? extends ModelEvent<D, M>> eventType, DbOperationType operation) {
        super((null == target) ? event.getSource() : target, (null == target) ? event.getTarget() : target, Objects.requireNonNull(eventType));
        state = event.state.copyOf(operation);
    }

    /**
     * Creates a new {@link ModelEvent} that shares the same {@link DataAccessObject} and {@link FxRecordModel} as another {@link ModelEvent}.
     *
     * @param event The event that will share the same {@link DataAccessObject} and {@link FxRecordModel}.
     * @param eventType The event type.
     * @param operation The database operation associated with the event {@code null} to use the same operation as the {@code event} parameter.
     */
    protected ModelEvent(ModelEvent<D, M> event, EventType<? extends ModelEvent<D, M>> eventType, DbOperationType operation) {
        super(event.getSource(), event.getTarget(), Objects.requireNonNull(eventType));
        state = event.state.copyOf(operation);
    }

    /**
     * Creates a new {@link ModelEvent} from a {@link FxRecordModel} object.
     *
     * @param target The {@link FxRecordModel} that wraps the {@link DataAccessObject} that is associated with the event.
     * @param source The object which sent the event.
     * @param eventType The event type.
     * @param operation The database operation associated with the event.
     */
    protected ModelEvent(M target, Object source, EventType<? extends ModelEvent<D, M>> eventType, DbOperationType operation) {
        super(source, target, Objects.requireNonNull(eventType));
        StateOriginal s = new StateOriginal(target.dataObject(), operation);
        s.fxRecordModel = target;
        state = s;
    }

    /**
     * Creates a new {@link ModelEvent} for a {@link DataAccessObject} object.
     *
     * @param target The {@link DataAccessObject} that is associated with the event.
     * @param source The object which sent the event.
     * @param eventType The event type.
     * @param operation The database operation associated with the event.
     */
    protected ModelEvent(D target, Object source, EventType<? extends ModelEvent<D, M>> eventType, DbOperationType operation) {
        super(source, target, Objects.requireNonNull(eventType));
        state = new StateOriginal(target, operation);
    }

    @Override
    public D getDataAccessObject() {
        return state.getDataAccessObject();
    }

    @Override
    public M getFxRecordModel() {
        return state.getFxRecordModel();
    }

    @Override
    public void setFxRecordModel(M fxRecordModel) {
        state.setFxRecordModel(fxRecordModel);
    }

    @Override
    public DbOperationType getOperation() {
        return state.getOperation();
    }

    @Override
    public Event copyFor(Object newSource, EventTarget newTarget) {
        @SuppressWarnings("unchecked")
        ModelEvent<D, M> copy = (ModelEvent<D, M>) super.copyFor(newSource, newTarget);
        copy.state = state;
        return copy;
    }

    private class StateOriginal extends State {

        private final DbOperationType operation;
        private final D dataAccessObject;
        private M fxRecordModel;

        private StateOriginal(D dataAccessObject, DbOperationType operation) {
            this.dataAccessObject = Objects.requireNonNull(dataAccessObject);
            this.operation = Objects.requireNonNull(operation);
        }

        @Override
        protected DbOperationType getOperation() {
            return operation;
        }

        @Override
        protected D getDataAccessObject() {
            return dataAccessObject;
        }

        @Override
        protected M getFxRecordModel() {
            return fxRecordModel;
        }

        @Override
        protected synchronized void setFxRecordModel(M fxRecordModel) {
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

        @Override
        protected State copyOf(DbOperationType operation) {
            if (null == operation || operation == this.operation) {
                return this;
            }
            return new StateCopy(this, operation);
        }

    }

    private class StateCopy extends State {

        private final StateOriginal source;
        private final DbOperationType operation;

        private StateCopy(StateOriginal source, DbOperationType operation) {
            this.source = source;
            this.operation = Objects.requireNonNull(operation);
        }

        @Override
        protected DbOperationType getOperation() {
            return operation;
        }

        @Override
        protected D getDataAccessObject() {
            return source.getDataAccessObject();
        }

        @Override
        protected M getFxRecordModel() {
            return source.getFxRecordModel();
        }

        @Override
        protected void setFxRecordModel(M fxRecordModel) {
            source.setFxRecordModel(fxRecordModel);
        }

        @Override
        protected State copyOf(DbOperationType operation) {
            if (null == operation || operation == this.operation) {
                return this;
            }
            return source.copyOf(operation);
        }
    }

    private abstract class State {

        protected abstract DbOperationType getOperation();

        protected abstract D getDataAccessObject();

        protected abstract M getFxRecordModel();

        protected abstract void setFxRecordModel(M fxRecordModel);

        protected abstract State copyOf(DbOperationType operation);
    }

}
