package events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.AddressDAO;
import scheduler.model.ui.AddressModel;


public final class AddressValidatingEvent extends DbValidatingEvent<AddressModel, AddressDAO> {

    /**
     * Base {@link EventType} for all {@code AddressValidatingEvent}s.
     */
    public static final EventType<AddressValidatingEvent> ADDRESS_DB_VALIDATING = new EventType<>(DbValidatingEvent.DB_VALIDATING_EVENT, "SCHEDULER_ADDRESS_DB_VALIDATING");

    /**
     * {@link EventType} for all {@link DbOperationType#INSERT} {@code AddressValidatingEvent}s.
     */
    public static final EventType<AddressValidatingEvent> _VALIDATING_INSERT = new EventType<>(AddressValidatingEvent.ADDRESS_DB_VALIDATING, "SCHEDULER_");

    /**
     * {@link EventType} for all {@link DbOperationType#UPDATE} {@code AddressValidatingEvent}s.
     */
    public static final EventType<AddressValidatingEvent> _VALIDATING_UPDATE = new EventType<>(AddressValidatingEvent.ADDRESS_DB_VALIDATING, "SCHEDULER_");

    /**
     * {@link EventType} for all {@link DbOperationType#DELETE} {@code AddressValidatingEvent}s.
     */
    public static final EventType<AddressValidatingEvent> _VALIDATING_DELETE = new EventType<>(AddressValidatingEvent.ADDRESS_DB_VALIDATING, "SCHEDULER_");

    static final EventType<AddressValidatingEvent> toEventType(DbOperationType operation) {
        switch (operation) {
            case INSERT:
                return _VALIDATING_INSERT;
            case DELETE:
                return _VALIDATING_DELETE;
            default:
                return _VALIDATING_UPDATE;
        }
    }
    
    AddressValidatingEvent(AddressBeginOpEvent precedingEvent, EventType<AddressValidatingEvent> newType) {
        super(precedingEvent, newType);
    }

    private AddressValidatingEvent(AddressValidatingEvent sourceEvent, Object newSource, EventTarget newTarget) {
        super(sourceEvent, newSource, newTarget);
    }

    public AddressValidatingEvent(Object source, EventTarget target, AddressModel model, DbOperationType operation) {
        super(source, target, toEventType(operation), model, operation);
    }

    public AddressValidatingEvent(Object source, EventTarget target, AddressDAO dao, DbOperationType operation) {
        super(source, target, toEventType(operation), dao, operation);
    }

    @Override
    public AddressValidatingEvent copyFor(Object newSource, EventTarget newTarget) {
        return new AddressValidatingEvent(this, newSource, newTarget);
    }

    @Override
    public AddressOpCompletedEvent toOperationCompletedEvent() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement events.AddressValidatingEvent#toOperationCompletedEvent
    }

    @Override
    public AddressOpFailureEvent toOperationFaultEvent(Throwable fault, String message) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement events.AddressValidatingEvent#toOperationFaultEvent
    }

    @Override
    public AddressOpFailureEvent toOperationCanceledEvent(String message) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement events.AddressValidatingEvent#toOperationCanceledEvent
    }
    
}
