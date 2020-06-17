package events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.CountryDAO;
import scheduler.model.ui.CountryModel;


public final class CountryOpFailureEvent extends OperationFailureEvent<CountryModel, CountryDAO> {

    /**
     * Base {@link EventType} for all {@code CountryOpFailureEvent}s.
     */
    public static final EventType<CountryOpFailureEvent> COUNTRY_OP_FAILURE = new EventType<>(OperationFailureEvent.OP_FAILURE_EVENT, "SCHEDULER_COUNTRY_OP_FAILURE");
    
    /**
     * {@link EventType} for all {@link DbOperationType#INSERT} {@code CountryOpFailureEvent}s.
     */
    public static final EventType<CountryOpFailureEvent> _INSERT_FAILURE = new EventType<>(CountryOpFailureEvent.COUNTRY_OP_FAILURE, "SCHEDULER_");

    /**
     * {@link EventType} for all {@link DbOperationType#UPDATE} {@code CountryOpFailureEvent}s.
     */
    public static final EventType<CountryOpFailureEvent> _UPDATE_FAILURE = new EventType<>(CountryOpFailureEvent.COUNTRY_OP_FAILURE, "SCHEDULER_");

    /**
     * {@link EventType} for all {@link DbOperationType#DELETE} {@code CountryOpFailureEvent}s.
     */
    public static final EventType<CountryOpFailureEvent> _DELETE_FAILURE = new EventType<>(CountryOpFailureEvent.COUNTRY_OP_FAILURE, "SCHEDULER_");

    static final EventType<CountryOpFailureEvent> toEventType(DbOperationType operation) {
        switch (operation) {
            case INSERT:
                return _INSERT_FAILURE;
            case DELETE:
                return _DELETE_FAILURE;
            default:
                return _UPDATE_FAILURE;
        }
    }
    
    private CountryOpFailureEvent(CountryOpFailureEvent sourceEvent, Object newSource, EventTarget newTarget) {
        super(sourceEvent, newSource, newTarget);
    }

    public CountryOpFailureEvent(Object source, EventTarget target, CountryModel model, Throwable fault, DbOperationType operation, String message) {
        super(source, target, toEventType(operation), model, fault, operation, message);
    }

    public CountryOpFailureEvent(Object source, EventTarget target, CountryModel model, DbOperationType operation, boolean canceled, String message) {
        super(source, target, toEventType(operation), model, operation, canceled, message);
    }

    public CountryOpFailureEvent(Object source, EventTarget target, CountryDAO dao, Throwable fault, DbOperationType operation, String message) {
        super(source, target, toEventType(operation), dao, fault, operation, message);
    }

    public CountryOpFailureEvent(Object source, EventTarget target, CountryDAO dao, DbOperationType operation, boolean canceled, String message) {
        super(source, target, toEventType(operation), dao, operation, canceled, message);
    }

    CountryOpFailureEvent(CountryBeginOpEvent failedEvent, EventType<CountryOpFailureEvent> eventType) {
        super(failedEvent, eventType);
    }

    CountryOpFailureEvent(CountryValidatingEvent failedEvent, EventType<CountryOpFailureEvent> eventType) {
        super(failedEvent, eventType);
    }

    @Override
    public CountryOpFailureEvent copyFor(Object newSource, EventTarget newTarget) {
        return new CountryOpFailureEvent(this, newSource, newTarget);
    }
    
}
