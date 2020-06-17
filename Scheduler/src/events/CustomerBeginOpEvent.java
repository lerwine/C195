package events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.CustomerDAO;
import scheduler.model.ui.CustomerModel;


public final class CustomerBeginOpEvent extends BeginOperationEvent<CustomerModel, CustomerDAO> {

    /**
     * Base {@link EventType} for all {@code CustomerBeginOpEvent}s.
     */
    public static final EventType<CustomerBeginOpEvent> BEGIN_CUSTOMER_OP = new EventType<>(BeginOperationEvent.BEGIN_OP_EVENT, "SCHEDULER_BEGIN_CUSTOMER_OP");

    /**
     * {@link EventType} for all {@link DbOperationType#INSERT} {@code CustomerBeginOpEvent}s.
     */
    public static final EventType<CustomerBeginOpEvent> BEGIN__INSERT = new EventType<>(CustomerBeginOpEvent.BEGIN_CUSTOMER_OP, "SCHEDULER_");

    /**
     * {@link EventType} for all {@link DbOperationType#UPDATE} {@code CustomerBeginOpEvent}s.
     */
    public static final EventType<CustomerBeginOpEvent> BEGIN__UPDATE = new EventType<>(CustomerBeginOpEvent.BEGIN_CUSTOMER_OP, "SCHEDULER_");

    /**
     * {@link EventType} for all {@link DbOperationType#DELETE} {@code CustomerBeginOpEvent}s.
     */
    public static final EventType<CustomerBeginOpEvent> BEGIN__DELETE = new EventType<>(CustomerBeginOpEvent.BEGIN_CUSTOMER_OP, "SCHEDULER_");

    static final EventType<CustomerBeginOpEvent> toEventType(DbOperationType operation) {
        switch (operation) {
            case INSERT:
                return BEGIN__INSERT;
            case DELETE:
                return BEGIN__DELETE;
            default:
                return BEGIN__UPDATE;
        }
    }
    
    CustomerBeginOpEvent(CustomerEditRequestEvent precedingEvent, EventType<CustomerBeginOpEvent> eventType) {
        super(precedingEvent, eventType);
    }

    CustomerBeginOpEvent(CustomerDeleteRequestEvent precedingEvent, EventType<CustomerBeginOpEvent> eventType) {
        super(precedingEvent, eventType);
    }

    private CustomerBeginOpEvent(CustomerBeginOpEvent sourceEvent, Object newSource, EventTarget newTarget) {
        super(sourceEvent, newSource, newTarget);
    }

    public CustomerBeginOpEvent(Object source, EventTarget target, CustomerModel model, DbOperationType operation) {
        super(source, target, toEventType(operation), model, operation);
    }

    public CustomerBeginOpEvent(Object source, EventTarget target, CustomerDAO dao, DbOperationType operation) {
        super(source, target, toEventType(operation), dao, operation);
    }

    @Override
    public CustomerBeginOpEvent copyFor(Object newSource, EventTarget newTarget) {
        return new CustomerBeginOpEvent(this, newSource, newTarget);
    }

    @Override
    public CustomerValidatingEvent toDbValidatingEvent() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement events.CustomerBeginOpEvent#toDbValidatingEvent
    }

    @Override
    public CustomerOpFailureEvent toOperationFaultEvent(Throwable fault, String message) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement events.CustomerBeginOpEvent#toOperationFaultEvent
    }

    @Override
    public CustomerOpFailureEvent toOperationCanceledEvent(String message) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement events.CustomerBeginOpEvent#toOperationCanceledEvent
    }
    
}
