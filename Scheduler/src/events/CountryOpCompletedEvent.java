package events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.CountryDAO;
import scheduler.model.ui.CountryModel;


public final class CountryOpCompletedEvent extends OperationCompletedEvent<CountryModel, CountryDAO> {

    /**
     * Base {@link EventType} for all {@code CountryOpCompletedEvent}s.
     */
    public static final EventType<CountryOpCompletedEvent> COUNTRY_OP_COMPLETED = new EventType<>(OperationCompletedEvent.OP_COMPLETED_EVENT, "SCHEDULER_COUNTRY_OP_COMPLETED");

    /**
     * {@link EventType} for all {@link DbOperationType#INSERT} {@code CountryOpCompletedEvent}s.
     */
    public static final EventType<CountryOpCompletedEvent> _INSERT_COMPLETED = new EventType<>(CountryOpCompletedEvent.COUNTRY_OP_COMPLETED, "SCHEDULER_");

    /**
     * {@link EventType} for all {@link DbOperationType#UPDATE} {@code CountryOpCompletedEvent}s.
     */
    public static final EventType<CountryOpCompletedEvent> _UPDATE_COMPLETED = new EventType<>(CountryOpCompletedEvent.COUNTRY_OP_COMPLETED, "SCHEDULER_");

    /**
     * {@link EventType} for all {@link DbOperationType#DELETE} {@code CountryOpCompletedEvent}s.
     */
    public static final EventType<CountryOpCompletedEvent> _DELETE_COMPLETED = new EventType<>(CountryOpCompletedEvent.COUNTRY_OP_COMPLETED, "SCHEDULER_");

    static final EventType<CountryOpCompletedEvent> toEventType(DbOperationType operation) {
        switch (operation) {
            case INSERT:
                return _INSERT_COMPLETED;
            case DELETE:
                return _DELETE_COMPLETED;
            default:
                return _UPDATE_COMPLETED;
        }
    }
    
    CountryOpCompletedEvent(CountryBeginOpEvent skippedEvent, EventType<CountryOpCompletedEvent> eventType) {
        super(skippedEvent, eventType);
    }

    CountryOpCompletedEvent(CountryValidatingEvent validatedEvent, EventType<CountryOpCompletedEvent> eventType) {
        super(validatedEvent, eventType);
    }

    private CountryOpCompletedEvent(CountryOpCompletedEvent sourceEvent, Object newSource, EventTarget newTarget) {
        super(sourceEvent, newSource, newTarget);
    }

    public CountryOpCompletedEvent(Object source, EventTarget target, CountryModel model, DbOperationType operation) {
        super(source, target, toEventType(operation), model, operation);
    }

    public CountryOpCompletedEvent(Object source, EventTarget target, CountryDAO dao, DbOperationType operation) {
        super(source, target, toEventType(operation), dao, operation);
    }

    @Override
    public CountryOpCompletedEvent copyFor(Object newSource, EventTarget newTarget) {
        return new CountryOpCompletedEvent(this, newSource, newTarget);
    }
    
}
