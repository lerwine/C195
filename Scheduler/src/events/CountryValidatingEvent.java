package events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.CountryDAO;
import scheduler.model.ui.CountryModel;


public final class CountryValidatingEvent extends DbValidatingEvent<CountryModel, CountryDAO> {

    /**
     * Base {@link EventType} for all {@code CountryValidatingEvent}s.
     */
    public static final EventType<CountryValidatingEvent> COUNTRY_DB_VALIDATING = new EventType<>(DbValidatingEvent.DB_VALIDATING_EVENT, "SCHEDULER_COUNTRY_DB_VALIDATING");

    /**
     * {@link EventType} for all {@link DbOperationType#INSERT} {@code CountryValidatingEvent}s.
     */
    public static final EventType<CountryValidatingEvent> _VALIDATING_INSERT = new EventType<>(CountryValidatingEvent.COUNTRY_DB_VALIDATING, "SCHEDULER_");

    /**
     * {@link EventType} for all {@link DbOperationType#UPDATE} {@code CountryValidatingEvent}s.
     */
    public static final EventType<CountryValidatingEvent> _VALIDATING_UPDATE = new EventType<>(CountryValidatingEvent.COUNTRY_DB_VALIDATING, "SCHEDULER_");

    /**
     * {@link EventType} for all {@link DbOperationType#DELETE} {@code CountryValidatingEvent}s.
     */
    public static final EventType<CountryValidatingEvent> _VALIDATING_DELETE = new EventType<>(CountryValidatingEvent.COUNTRY_DB_VALIDATING, "SCHEDULER_");

    static final EventType<CountryValidatingEvent> toEventType(DbOperationType operation) {
        switch (operation) {
            case INSERT:
                return _VALIDATING_INSERT;
            case DELETE:
                return _VALIDATING_DELETE;
            default:
                return _VALIDATING_UPDATE;
        }
    }
    
    CountryValidatingEvent(CountryBeginOpEvent precedingEvent, EventType<CountryValidatingEvent> newType) {
        super(precedingEvent, newType);
    }

    private CountryValidatingEvent(CountryValidatingEvent sourceEvent, Object newSource, EventTarget newTarget) {
        super(sourceEvent, newSource, newTarget);
    }

    public CountryValidatingEvent(Object source, EventTarget target, CountryModel model, DbOperationType operation) {
        super(source, target, toEventType(operation), model, operation);
    }

    public CountryValidatingEvent(Object source, EventTarget target, CountryDAO dao, DbOperationType operation) {
        super(source, target, toEventType(operation), dao, operation);
    }

    @Override
    public CountryValidatingEvent copyFor(Object newSource, EventTarget newTarget) {
        return new CountryValidatingEvent(this, newSource, newTarget);
    }

    @Override
    public CountryOpCompletedEvent toOperationCompletedEvent() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement events.CountryValidatingEvent#toOperationCompletedEvent
    }

    @Override
    public CountryOpFailureEvent toOperationFaultEvent(Throwable fault, String message) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement events.CountryValidatingEvent#toOperationFaultEvent
    }

    @Override
    public CountryOpFailureEvent toOperationCanceledEvent(String message) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement events.CountryValidatingEvent#toOperationCanceledEvent
    }
    
}
