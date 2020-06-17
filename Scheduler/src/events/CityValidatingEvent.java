package events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.CityDAO;
import scheduler.model.ui.CityModel;


public final class CityValidatingEvent extends DbValidatingEvent<CityModel, CityDAO> {

    /**
     * Base {@link EventType} for all {@code CityValidatingEvent}s.
     */
    public static final EventType<CityValidatingEvent> CITY_DB_VALIDATING = new EventType<>(DbValidatingEvent.DB_VALIDATING_EVENT, "SCHEDULER_CITY_DB_VALIDATING");

    /**
     * {@link EventType} for all {@link DbOperationType#INSERT} {@code CityValidatingEvent}s.
     */
    public static final EventType<CityValidatingEvent> _VALIDATING_INSERT = new EventType<>(CityValidatingEvent.CITY_DB_VALIDATING, "SCHEDULER_");

    /**
     * {@link EventType} for all {@link DbOperationType#UPDATE} {@code CityValidatingEvent}s.
     */
    public static final EventType<CityValidatingEvent> _VALIDATING_UPDATE = new EventType<>(CityValidatingEvent.CITY_DB_VALIDATING, "SCHEDULER_");

    /**
     * {@link EventType} for all {@link DbOperationType#DELETE} {@code CityValidatingEvent}s.
     */
    public static final EventType<CityValidatingEvent> _VALIDATING_DELETE = new EventType<>(CityValidatingEvent.CITY_DB_VALIDATING, "SCHEDULER_");

    static final EventType<CityValidatingEvent> toEventType(DbOperationType operation) {
        switch (operation) {
            case INSERT:
                return _VALIDATING_INSERT;
            case DELETE:
                return _VALIDATING_DELETE;
            default:
                return _VALIDATING_UPDATE;
        }
    }
    
    CityValidatingEvent(CityBeginOpEvent precedingEvent, EventType<CityValidatingEvent> newType) {
        super(precedingEvent, newType);
    }

    private CityValidatingEvent(CityValidatingEvent sourceEvent, Object newSource, EventTarget newTarget) {
        super(sourceEvent, newSource, newTarget);
    }

    public CityValidatingEvent(Object source, EventTarget target, CityModel model, DbOperationType operation) {
        super(source, target, toEventType(operation), model, operation);
    }

    public CityValidatingEvent(Object source, EventTarget target, CityDAO dao, DbOperationType operation) {
        super(source, target, toEventType(operation), dao, operation);
    }

    @Override
    public CityValidatingEvent copyFor(Object newSource, EventTarget newTarget) {
        return new CityValidatingEvent(this, newSource, newTarget);
    }

    @Override
    public CityOpCompletedEvent toOperationCompletedEvent() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement events.CityValidatingEvent#toOperationCompletedEvent
    }

    @Override
    public CityOpFailureEvent toOperationFaultEvent(Throwable fault, String message) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement events.CityValidatingEvent#toOperationFaultEvent
    }

    @Override
    public CityOpFailureEvent toOperationCanceledEvent(String message) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement events.CityValidatingEvent#toOperationCanceledEvent
    }
    
}
