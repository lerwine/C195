package events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.CountryDAO;
import scheduler.model.ui.CountryModel;


public final class CountryBeginOpEvent extends BeginOperationEvent<CountryModel, CountryDAO> {

    /**
     * Base {@link EventType} for all {@code CountryBeginOpEvent}s.
     */
    public static final EventType<CountryBeginOpEvent> BEGIN_COUNTRY_OP = new EventType<>(BeginOperationEvent.BEGIN_OP_EVENT, "SCHEDULER_BEGIN_COUNTRY_OP");

    /**
     * {@link EventType} for all {@link DbOperationType#INSERT} {@code CountryBeginOpEvent}s.
     */
    public static final EventType<CountryBeginOpEvent> BEGIN__INSERT = new EventType<>(CountryBeginOpEvent.BEGIN_COUNTRY_OP, "SCHEDULER_");

    /**
     * {@link EventType} for all {@link DbOperationType#UPDATE} {@code CountryBeginOpEvent}s.
     */
    public static final EventType<CountryBeginOpEvent> BEGIN__UPDATE = new EventType<>(CountryBeginOpEvent.BEGIN_COUNTRY_OP, "SCHEDULER_");

    /**
     * {@link EventType} for all {@link DbOperationType#DELETE} {@code CountryBeginOpEvent}s.
     */
    public static final EventType<CountryBeginOpEvent> BEGIN__DELETE = new EventType<>(CountryBeginOpEvent.BEGIN_COUNTRY_OP, "SCHEDULER_");

    static final EventType<CountryBeginOpEvent> toEventType(DbOperationType operation) {
        switch (operation) {
            case INSERT:
                return BEGIN__INSERT;
            case DELETE:
                return BEGIN__DELETE;
            default:
                return BEGIN__UPDATE;
        }
    }
    
    CountryBeginOpEvent(CountryEditRequestEvent precedingEvent, EventType<CountryBeginOpEvent> eventType) {
        super(precedingEvent, eventType);
    }

    CountryBeginOpEvent(CountryDeleteRequestEvent precedingEvent, EventType<CountryBeginOpEvent> eventType) {
        super(precedingEvent, eventType);
    }

    private CountryBeginOpEvent(CountryBeginOpEvent sourceEvent, Object newSource, EventTarget newTarget) {
        super(sourceEvent, newSource, newTarget);
    }

    public CountryBeginOpEvent(Object source, EventTarget target, CountryModel model, DbOperationType operation) {
        super(source, target, toEventType(operation), model, operation);
    }

    public CountryBeginOpEvent(Object source, EventTarget target, CountryDAO dao, DbOperationType operation) {
        super(source, target, toEventType(operation), dao, operation);
    }

    @Override
    public CountryBeginOpEvent copyFor(Object newSource, EventTarget newTarget) {
        return new CountryBeginOpEvent(this, newSource, newTarget);
    }

    @Override
    public CountryValidatingEvent toDbValidatingEvent() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement events.CountryBeginOpEvent#toDbValidatingEvent
    }

    @Override
    public CountryOpFailureEvent toOperationFaultEvent(Throwable fault, String message) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement events.CountryBeginOpEvent#toOperationFaultEvent
    }

    @Override
    public CountryOpFailureEvent toOperationCanceledEvent(String message) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement events.CountryBeginOpEvent#toOperationCanceledEvent
    }
    
}
