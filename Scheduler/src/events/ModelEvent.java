package events;

import java.util.function.Supplier;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.DataAccessObject;
import scheduler.model.ui.FxRecordModel;

/**
 * The base base class for {@link FxRecordModel} and {@link DataAccessObject} events.
 * 
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
 * @param <M> The {@link FxRecordModel} type.
 * @param <D> The {@link DataAccessObject} type.
 */
public abstract class ModelEvent<M extends FxRecordModel<D>, D extends DataAccessObject> extends Event {

    private static final long serialVersionUID = -6832461936768738020L;

    /**
     * Base {@link EventType} for all {@code ModelEvent}s.
     */
    public static final EventType<ModelEvent<? extends FxRecordModel<? extends DataAccessObject>, ? extends DataAccessObject>> MODEL_EVENT
            = new EventType<>(ANY, "SCHEDULER_MODEL_EVENT");

    public static boolean isEqualToOrExtending(EventType<? extends Event> eventType, EventType<? extends Event> parentType) {
        if (null == parentType) {
            return null == eventType;
        }
        while (null != eventType) {
            if (eventType == parentType) {
                return true;
            }
            eventType = eventType.getSuperType();
        }
        return false;
    }
    
    public static <T extends EventType<?>> T assertEqualToOrExtending(T eventType, T parentType) {
        if (isEqualToOrExtending(eventType, parentType)) {
            return eventType;
        }
        throw new IllegalArgumentException();
    }
    
    public static boolean isOfType(Event event, EventType<? extends Event> type) {
        return (null == type) ? null == event : null != event && isEqualToOrExtending(event.getEventType(), type);
    }

    public static <T extends Event> T assertOfType(T event, EventType<T> type) {
        if (isOfType(event, type)) {
            return event;
        }
        throw new IllegalArgumentException();
    }
    
    public static boolean isExtending(EventType<? extends Event> eventType, EventType<? extends Event> parentType) {
        if (null == parentType) {
            return null == eventType;
        }
        if (null != eventType && eventType != parentType) {
            while (null != (eventType = eventType.getSuperType())) {
                if (eventType == parentType) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static <T extends EventType<?>> T assertExtending(T eventType, T parentType) {
        if (isExtending(eventType, parentType)) {
            return eventType;
        }
        throw new IllegalArgumentException();
    }
    
    private final D dataAccessObject;
    private final State state;

    protected ModelEvent(ModelEvent<M, D> precedingEvent, EventType<? extends ModelEvent<M, D>> newType) {
        super(precedingEvent.getSource(), precedingEvent.getTarget(), newType);
        dataAccessObject = precedingEvent.dataAccessObject;
        state = precedingEvent.state;
    }

    protected ModelEvent(ModelEvent<M, D> sourceEvent, Object newSource, EventTarget newTarget) {
        super(newSource, newTarget, sourceEvent.getEventType());
        dataAccessObject = sourceEvent.dataAccessObject;
        state = sourceEvent.state;
    }

    protected ModelEvent(Object source, EventTarget target, EventType<? extends ModelEvent<M, D>> eventType, M model) {
        super(source, target, eventType);
        dataAccessObject = ((state = new State()).model = model).dataObject();
    }

    protected ModelEvent(Object source, EventTarget target, EventType<? extends ModelEvent<M, D>> eventType, D dao) {
        super(source, target, eventType);
        dataAccessObject = dao;
        state = new State();
    }

    @Override
    public EventType<? extends ModelEvent<M, D>> getEventType() {
        return (EventType<? extends ModelEvent<M, D>>)super.getEventType();
    }

    public D getDataAccessObject() {
        return dataAccessObject;
    }

    public M getModel() {
        return state.model;
    }

    public void setModel(M model) {
        state.setModel(model);
    }

    public M ensureModel(Supplier<M> ifNotDefined) {
        return state.getModel(ifNotDefined);
    }

    private class State {
        private M model;
        synchronized void setModel(M model) {
            if (null != this.model) {
                if (model == this.model) {
                    return;
                }
                throw new IllegalStateException();
            }
            if (model.dataObject() != dataAccessObject) {
                throw new IllegalArgumentException();
            }
            this.model = model;
        }

        synchronized M getModel(Supplier<M> ifNotDefined) {
            if (null == model) {
                model = ifNotDefined.get();
            }
            return model;
        }

    }
}
