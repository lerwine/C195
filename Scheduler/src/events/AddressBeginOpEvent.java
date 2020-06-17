package events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.AddressDAO;
import scheduler.model.ui.AddressModel;


public final class AddressBeginOpEvent extends BeginOperationEvent<AddressModel, AddressDAO> {

    /**
     * Base {@link EventType} for all {@code AddressBeginOpEvent}s.
     */
    public static final EventType<AddressBeginOpEvent> BEGIN_ADDRESS_OP = new EventType<>(BeginOperationEvent.BEGIN_OP_EVENT, "SCHEDULER_BEGIN_ADDRESS_OP");

    /**
     * {@link EventType} for all {@link DbOperationType#INSERT} {@code AddressBeginOpEvent}s.
     */
    public static final EventType<AddressBeginOpEvent> BEGIN__INSERT = new EventType<>(AddressBeginOpEvent.BEGIN_ADDRESS_OP, "SCHEDULER_");

    /**
     * {@link EventType} for all {@link DbOperationType#UPDATE} {@code AddressBeginOpEvent}s.
     */
    public static final EventType<AddressBeginOpEvent> BEGIN__UPDATE = new EventType<>(AddressBeginOpEvent.BEGIN_ADDRESS_OP, "SCHEDULER_");

    /**
     * {@link EventType} for all {@link DbOperationType#DELETE} {@code AddressBeginOpEvent}s.
     */
    public static final EventType<AddressBeginOpEvent> BEGIN__DELETE = new EventType<>(AddressBeginOpEvent.BEGIN_ADDRESS_OP, "SCHEDULER_");

    static final EventType<AddressBeginOpEvent> toEventType(DbOperationType operation) {
        switch (operation) {
            case INSERT:
                return BEGIN__INSERT;
            case DELETE:
                return BEGIN__DELETE;
            default:
                return BEGIN__UPDATE;
        }
    }
    
    AddressBeginOpEvent(AddressEditRequestEvent precedingEvent, EventType<AddressBeginOpEvent> eventType) {
        super(precedingEvent, eventType);
    }

    AddressBeginOpEvent(AddressDeleteRequestEvent precedingEvent, EventType<AddressBeginOpEvent> eventType) {
        super(precedingEvent, eventType);
    }

    private AddressBeginOpEvent(AddressBeginOpEvent sourceEvent, Object newSource, EventTarget newTarget) {
        super(sourceEvent, newSource, newTarget);
    }

    public AddressBeginOpEvent(Object source, EventTarget target, EventType<AddressBeginOpEvent> eventType, AddressModel model, DbOperationType operation) {
        super(source, target, toEventType(operation), model, operation);
    }

    public AddressBeginOpEvent(Object source, EventTarget target, EventType<AddressBeginOpEvent> eventType, AddressDAO dao, DbOperationType operation) {
        super(source, target, toEventType(operation), dao, operation);
    }

    @Override
    public AddressBeginOpEvent copyFor(Object newSource, EventTarget newTarget) {
        return new AddressBeginOpEvent(this, newSource, newTarget);
    }

    @Override
    public AddressValidatingEvent toDbValidatingEvent() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement events.AddressBeginOpEvent#toDbValidatingEvent
    }

    @Override
    public AddressOpFailureEvent toOperationFaultEvent(Throwable fault, String message) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement events.AddressBeginOpEvent#toOperationFaultEvent
    }

    @Override
    public AddressOpFailureEvent toOperationCanceledEvent(String message) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement events.AddressBeginOpEvent#toOperationCanceledEvent
    }
    
}
