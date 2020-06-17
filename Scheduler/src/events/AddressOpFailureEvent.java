package events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.AddressDAO;
import scheduler.model.ui.AddressModel;


public final class AddressOpFailureEvent extends OperationFailureEvent<AddressModel, AddressDAO> {

    /**
     * Base {@link EventType} for all {@code AddressOpFailureEvent}s.
     */
    public static final EventType<AddressOpFailureEvent> ADDRESS_OP_FAILURE = new EventType<>(OperationFailureEvent.OP_FAILURE_EVENT, "SCHEDULER_ADDRESS_OP_FAILURE");
    
    /**
     * {@link EventType} for all {@link DbOperationType#INSERT} {@code AddressOpFailureEvent}s.
     */
    public static final EventType<AddressOpFailureEvent> _INSERT_FAILURE = new EventType<>(AddressOpFailureEvent.ADDRESS_OP_FAILURE, "SCHEDULER_");

    /**
     * {@link EventType} for all {@link DbOperationType#UPDATE} {@code AddressOpFailureEvent}s.
     */
    public static final EventType<AddressOpFailureEvent> _UPDATE_FAILURE = new EventType<>(AddressOpFailureEvent.ADDRESS_OP_FAILURE, "SCHEDULER_");

    /**
     * {@link EventType} for all {@link DbOperationType#DELETE} {@code AddressOpFailureEvent}s.
     */
    public static final EventType<AddressOpFailureEvent> _DELETE_FAILURE = new EventType<>(AddressOpFailureEvent.ADDRESS_OP_FAILURE, "SCHEDULER_");

    static final EventType<AddressOpFailureEvent> toEventType(DbOperationType operation) {
        switch (operation) {
            case INSERT:
                return _INSERT_FAILURE;
            case DELETE:
                return _DELETE_FAILURE;
            default:
                return _UPDATE_FAILURE;
        }
    }
    
    private AddressOpFailureEvent(AddressOpFailureEvent sourceEvent, Object newSource, EventTarget newTarget) {
        super(sourceEvent, newSource, newTarget);
    }

    public AddressOpFailureEvent(Object source, EventTarget target, AddressModel model, Throwable fault, DbOperationType operation, String message) {
        super(source, target, toEventType(operation), model, fault, operation, message);
    }

    public AddressOpFailureEvent(Object source, EventTarget target, AddressModel model, DbOperationType operation, boolean canceled, String message) {
        super(source, target, toEventType(operation), model, operation, canceled, message);
    }

    public AddressOpFailureEvent(Object source, EventTarget target, AddressDAO dao, Throwable fault, DbOperationType operation, String message) {
        super(source, target, toEventType(operation), dao, fault, operation, message);
    }

    public AddressOpFailureEvent(Object source, EventTarget target, AddressDAO dao, DbOperationType operation, boolean canceled, String message) {
        super(source, target, toEventType(operation), dao, operation, canceled, message);
    }

    AddressOpFailureEvent(AddressBeginOpEvent failedEvent, EventType<AddressOpFailureEvent> eventType) {
        super(failedEvent, eventType);
    }

    AddressOpFailureEvent(AddressValidatingEvent failedEvent, EventType<AddressOpFailureEvent> eventType) {
        super(failedEvent, eventType);
    }

    @Override
    public AddressOpFailureEvent copyFor(Object newSource, EventTarget newTarget) {
        return new AddressOpFailureEvent(this, newSource, newTarget);
    }
    
}
