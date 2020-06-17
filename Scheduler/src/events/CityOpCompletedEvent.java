package events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.CityDAO;
import scheduler.model.ui.CityModel;


public final class CityOpCompletedEvent extends OperationCompletedEvent<CityModel, CityDAO> {

    /**
     * Base {@link EventType} for all {@code CityOpCompletedEvent}s.
     */
    public static final EventType<CityOpCompletedEvent> CITY_OP_COMPLETED = new EventType<>(OperationCompletedEvent.OP_COMPLETED_EVENT, "SCHEDULER_CITY_OP_COMPLETED");

    /**
     * {@link EventType} for all {@link DbOperationType#INSERT} {@code CityOpCompletedEvent}s.
     */
    public static final EventType<CityOpCompletedEvent> _INSERT_COMPLETED = new EventType<>(CityOpCompletedEvent.CITY_OP_COMPLETED, "SCHEDULER_");

    /**
     * {@link EventType} for all {@link DbOperationType#UPDATE} {@code CityOpCompletedEvent}s.
     */
    public static final EventType<CityOpCompletedEvent> _UPDATE_COMPLETED = new EventType<>(CityOpCompletedEvent.CITY_OP_COMPLETED, "SCHEDULER_");

    /**
     * {@link EventType} for all {@link DbOperationType#DELETE} {@code CityOpCompletedEvent}s.
     */
    public static final EventType<CityOpCompletedEvent> _DELETE_COMPLETED = new EventType<>(CityOpCompletedEvent.CITY_OP_COMPLETED, "SCHEDULER_");

    static final EventType<CityOpCompletedEvent> toEventType(DbOperationType operation) {
        switch (operation) {
            case INSERT:
                return _INSERT_COMPLETED;
            case DELETE:
                return _DELETE_COMPLETED;
            default:
                return _UPDATE_COMPLETED;
        }
    }
    
    CityOpCompletedEvent(CityBeginOpEvent skippedEvent, EventType<CityOpCompletedEvent> eventType) {
        super(skippedEvent, eventType);
    }

    CityOpCompletedEvent(CityValidatingEvent validatedEvent, EventType<CityOpCompletedEvent> eventType) {
        super(validatedEvent, eventType);
    }

    private CityOpCompletedEvent(CityOpCompletedEvent sourceEvent, Object newSource, EventTarget newTarget) {
        super(sourceEvent, newSource, newTarget);
    }

    public CityOpCompletedEvent(Object source, EventTarget target, CityModel model, DbOperationType operation) {
        super(source, target, toEventType(operation), model, operation);
    }

    public CityOpCompletedEvent(Object source, EventTarget target, CityDAO dao, DbOperationType operation) {
        super(source, target, toEventType(operation), dao, operation);
    }

    @Override
    public CityOpCompletedEvent copyFor(Object newSource, EventTarget newTarget) {
        return new CityOpCompletedEvent(this, newSource, newTarget);
    }
    
}
