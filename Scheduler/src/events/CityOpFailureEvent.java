package events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.CityDAO;
import scheduler.model.ui.CityModel;


public final class CityOpFailureEvent extends OperationFailureEvent<CityModel, CityDAO> {

    /**
     * Base {@link EventType} for all {@code CityOpFailureEvent}s.
     */
    public static final EventType<CityOpFailureEvent> CITY_OP_FAILURE = new EventType<>(OperationFailureEvent.OP_FAILURE_EVENT, "SCHEDULER_CITY_OP_FAILURE");
    
    /**
     * {@link EventType} for all {@link DbOperationType#INSERT} {@code CityOpFailureEvent}s.
     */
    public static final EventType<CityOpFailureEvent> _INSERT_FAILURE = new EventType<>(CityOpFailureEvent.CITY_OP_FAILURE, "SCHEDULER_");

    /**
     * {@link EventType} for all {@link DbOperationType#UPDATE} {@code CityOpFailureEvent}s.
     */
    public static final EventType<CityOpFailureEvent> _UPDATE_FAILURE = new EventType<>(CityOpFailureEvent.CITY_OP_FAILURE, "SCHEDULER_");

    /**
     * {@link EventType} for all {@link DbOperationType#DELETE} {@code CityOpFailureEvent}s.
     */
    public static final EventType<CityOpFailureEvent> _DELETE_FAILURE = new EventType<>(CityOpFailureEvent.CITY_OP_FAILURE, "SCHEDULER_");

    static final EventType<CityOpFailureEvent> toEventType(DbOperationType operation) {
        switch (operation) {
            case INSERT:
                return _INSERT_FAILURE;
            case DELETE:
                return _DELETE_FAILURE;
            default:
                return _UPDATE_FAILURE;
        }
    }
    
    private CityOpFailureEvent(CityOpFailureEvent sourceEvent, Object newSource, EventTarget newTarget) {
        super(sourceEvent, newSource, newTarget);
    }

    public CityOpFailureEvent(Object source, EventTarget target, CityModel model, Throwable fault, DbOperationType operation, String message) {
        super(source, target, toEventType(operation), model, fault, operation, message);
    }

    public CityOpFailureEvent(Object source, EventTarget target, CityModel model, DbOperationType operation, boolean canceled, String message) {
        super(source, target, toEventType(operation), model, operation, canceled, message);
    }

    public CityOpFailureEvent(Object source, EventTarget target, CityDAO dao, Throwable fault, DbOperationType operation, String message) {
        super(source, target, toEventType(operation), dao, fault, operation, message);
    }

    public CityOpFailureEvent(Object source, EventTarget target, CityDAO dao, DbOperationType operation, boolean canceled, String message) {
        super(source, target, toEventType(operation), dao, operation, canceled, message);
    }

    CityOpFailureEvent(CityBeginOpEvent failedEvent, EventType<CityOpFailureEvent> eventType) {
        super(failedEvent, eventType);
    }

    CityOpFailureEvent(CityValidatingEvent failedEvent, EventType<CityOpFailureEvent> eventType) {
        super(failedEvent, eventType);
    }

    @Override
    public CityOpFailureEvent copyFor(Object newSource, EventTarget newTarget) {
        return new CityOpFailureEvent(this, newSource, newTarget);
    }
    
}
