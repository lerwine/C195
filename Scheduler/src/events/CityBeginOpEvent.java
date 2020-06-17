package events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.CityDAO;
import scheduler.model.ui.CityModel;


public final class CityBeginOpEvent extends BeginOperationEvent<CityModel, CityDAO> {

    /**
     * Base {@link EventType} for all {@code CityBeginOpEvent}s.
     */
    public static final EventType<CityBeginOpEvent> BEGIN_CITY_OP = new EventType<>(BeginOperationEvent.BEGIN_OP_EVENT, "SCHEDULER_BEGIN_CITY_OP");

    /**
     * {@link EventType} for all {@link DbOperationType#INSERT} {@code CityBeginOpEvent}s.
     */
    public static final EventType<CityBeginOpEvent> BEGIN__INSERT = new EventType<>(CityBeginOpEvent.BEGIN_CITY_OP, "SCHEDULER_");

    /**
     * {@link EventType} for all {@link DbOperationType#UPDATE} {@code CityBeginOpEvent}s.
     */
    public static final EventType<CityBeginOpEvent> BEGIN__UPDATE = new EventType<>(CityBeginOpEvent.BEGIN_CITY_OP, "SCHEDULER_");

    /**
     * {@link EventType} for all {@link DbOperationType#DELETE} {@code CityBeginOpEvent}s.
     */
    public static final EventType<CityBeginOpEvent> BEGIN__DELETE = new EventType<>(CityBeginOpEvent.BEGIN_CITY_OP, "SCHEDULER_");

    static final EventType<CityBeginOpEvent> toEventType(DbOperationType operation) {
        switch (operation) {
            case INSERT:
                return BEGIN__INSERT;
            case DELETE:
                return BEGIN__DELETE;
            default:
                return BEGIN__UPDATE;
        }
    }
    
    CityBeginOpEvent(CityEditRequestEvent precedingEvent, EventType<CityBeginOpEvent> eventType) {
        super(precedingEvent, eventType);
    }

    CityBeginOpEvent(CityDeleteRequestEvent precedingEvent, EventType<CityBeginOpEvent> eventType) {
        super(precedingEvent, eventType);
    }

    private CityBeginOpEvent(CityBeginOpEvent sourceEvent, Object newSource, EventTarget newTarget) {
        super(sourceEvent, newSource, newTarget);
    }

    public CityBeginOpEvent(Object source, EventTarget target, EventType<CityBeginOpEvent> eventType, CityModel model, DbOperationType operation) {
        super(source, target, toEventType(operation), model, operation);
    }

    public CityBeginOpEvent(Object source, EventTarget target, EventType<CityBeginOpEvent> eventType, CityDAO dao, DbOperationType operation) {
        super(source, target, toEventType(operation), dao, operation);
    }

    @Override
    public CityBeginOpEvent copyFor(Object newSource, EventTarget newTarget) {
        return new CityBeginOpEvent(this, newSource, newTarget);
    }

    @Override
    public CityValidatingEvent toDbValidatingEvent() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement events.CityBeginOpEvent#toDbValidatingEvent
    }

    @Override
    public CityOpFailureEvent toOperationFaultEvent(Throwable fault, String message) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement events.CityBeginOpEvent#toOperationFaultEvent
    }

    @Override
    public CityOpFailureEvent toOperationCanceledEvent(String message) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement events.CityBeginOpEvent#toOperationCanceledEvent
    }
    
}
