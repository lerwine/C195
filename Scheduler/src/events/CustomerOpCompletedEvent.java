package events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.CustomerDAO;
import scheduler.model.ui.CustomerModel;


public final class CustomerOpCompletedEvent extends OperationCompletedEvent<CustomerModel, CustomerDAO> {

    /**
     * Base {@link EventType} for all {@code CustomerOpCompletedEvent}s.
     */
    public static final EventType<CustomerOpCompletedEvent> CUSTOMER_OP_COMPLETED = new EventType<>(OperationCompletedEvent.OP_COMPLETED_EVENT, "SCHEDULER_CUSTOMER_OP_COMPLETED");

    /**
     * {@link EventType} for all {@link DbOperationType#INSERT} {@code CustomerOpCompletedEvent}s.
     */
    public static final EventType<CustomerOpCompletedEvent> _INSERT_COMPLETED = new EventType<>(CustomerOpCompletedEvent.CUSTOMER_OP_COMPLETED, "SCHEDULER_");

    /**
     * {@link EventType} for all {@link DbOperationType#UPDATE} {@code CustomerOpCompletedEvent}s.
     */
    public static final EventType<CustomerOpCompletedEvent> _UPDATE_COMPLETED = new EventType<>(CustomerOpCompletedEvent.CUSTOMER_OP_COMPLETED, "SCHEDULER_");

    /**
     * {@link EventType} for all {@link DbOperationType#DELETE} {@code CustomerOpCompletedEvent}s.
     */
    public static final EventType<CustomerOpCompletedEvent> _DELETE_COMPLETED = new EventType<>(CustomerOpCompletedEvent.CUSTOMER_OP_COMPLETED, "SCHEDULER_");

    static final EventType<CustomerOpCompletedEvent> toEventType(DbOperationType operation) {
        switch (operation) {
            case INSERT:
                return _INSERT_COMPLETED;
            case DELETE:
                return _DELETE_COMPLETED;
            default:
                return _UPDATE_COMPLETED;
        }
    }
    
    CustomerOpCompletedEvent(CustomerBeginOpEvent skippedEvent, EventType<CustomerOpCompletedEvent> eventType) {
        super(skippedEvent, eventType);
    }

    CustomerOpCompletedEvent(CustomerValidatingEvent validatedEvent, EventType<CustomerOpCompletedEvent> eventType) {
        super(validatedEvent, eventType);
    }

    private CustomerOpCompletedEvent(CustomerOpCompletedEvent sourceEvent, Object newSource, EventTarget newTarget) {
        super(sourceEvent, newSource, newTarget);
    }

    public CustomerOpCompletedEvent(Object source, EventTarget target, CustomerModel model, DbOperationType operation) {
        super(source, target, toEventType(operation), model, operation);
    }

    public CustomerOpCompletedEvent(Object source, EventTarget target, CustomerDAO dao, DbOperationType operation) {
        super(source, target, toEventType(operation), dao, operation);
    }

    @Override
    public CustomerOpCompletedEvent copyFor(Object newSource, EventTarget newTarget) {
        return new CustomerOpCompletedEvent(this, newSource, newTarget);
    }
    
}
