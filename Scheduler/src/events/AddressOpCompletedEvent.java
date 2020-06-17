package events;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.AddressDAO;
import scheduler.model.ui.AddressModel;


public final class AddressOpCompletedEvent extends OperationCompletedEvent<AddressModel, AddressDAO> {

    /**
     * Base {@link EventType} for all {@code AddressOpCompletedEvent}s.
     */
    public static final EventType<AddressOpCompletedEvent> ADDRESS_OP_COMPLETED = new EventType<>(OperationCompletedEvent.OP_COMPLETED_EVENT, "SCHEDULER_ADDRESS_OP_COMPLETED");

    /**
     * {@link EventType} for all {@link DbOperationType#INSERT} {@code AddressOpCompletedEvent}s.
     */
    public static final EventType<AddressOpCompletedEvent> _INSERT_COMPLETED = new EventType<>(AddressOpCompletedEvent.ADDRESS_OP_COMPLETED, "SCHEDULER_");

    /**
     * {@link EventType} for all {@link DbOperationType#UPDATE} {@code AddressOpCompletedEvent}s.
     */
    public static final EventType<AddressOpCompletedEvent> _UPDATE_COMPLETED = new EventType<>(AddressOpCompletedEvent.ADDRESS_OP_COMPLETED, "SCHEDULER_");

    /**
     * {@link EventType} for all {@link DbOperationType#DELETE} {@code AddressOpCompletedEvent}s.
     */
    public static final EventType<AddressOpCompletedEvent> _DELETE_COMPLETED = new EventType<>(AddressOpCompletedEvent.ADDRESS_OP_COMPLETED, "SCHEDULER_");

    static final EventType<AddressOpCompletedEvent> toEventType(DbOperationType operation) {
        switch (operation) {
            case INSERT:
                return _INSERT_COMPLETED;
            case DELETE:
                return _DELETE_COMPLETED;
            default:
                return _UPDATE_COMPLETED;
        }
    }
    
    AddressOpCompletedEvent(AddressBeginOpEvent skippedEvent, EventType<AddressOpCompletedEvent> eventType) {
        super(skippedEvent, eventType);
    }

    AddressOpCompletedEvent(AddressValidatingEvent validatedEvent, EventType<AddressOpCompletedEvent> eventType) {
        super(validatedEvent, eventType);
    }

    private AddressOpCompletedEvent(AddressOpCompletedEvent sourceEvent, Object newSource, EventTarget newTarget) {
        super(sourceEvent, newSource, newTarget);
    }

    public AddressOpCompletedEvent(Object source, EventTarget target, AddressModel model, DbOperationType operation) {
        super(source, target, toEventType(operation), model, operation);
    }

    public AddressOpCompletedEvent(Object source, EventTarget target, AddressDAO dao, DbOperationType operation) {
        super(source, target, toEventType(operation), dao, operation);
    }

    @Override
    public AddressOpCompletedEvent copyFor(Object newSource, EventTarget newTarget) {
        return new AddressOpCompletedEvent(this, newSource, newTarget);
    }
    
}
