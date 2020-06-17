package events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.CustomerDAO;
import scheduler.model.ui.CustomerModel;


public final class CustomerOpFailureEvent extends OperationFailureEvent<CustomerModel, CustomerDAO> {

    /**
     * Base {@link EventType} for all {@code CustomerOpFailureEvent}s.
     */
    public static final EventType<CustomerOpFailureEvent> CUSTOMER_OP_FAILURE = new EventType<>(OperationFailureEvent.OP_FAILURE_EVENT, "SCHEDULER_CUSTOMER_OP_FAILURE");
    
    /**
     * {@link EventType} for all {@link DbOperationType#INSERT} {@code CustomerOpFailureEvent}s.
     */
    public static final EventType<CustomerOpFailureEvent> _INSERT_FAILURE = new EventType<>(CustomerOpFailureEvent.CUSTOMER_OP_FAILURE, "SCHEDULER_");

    /**
     * {@link EventType} for all {@link DbOperationType#UPDATE} {@code CustomerOpFailureEvent}s.
     */
    public static final EventType<CustomerOpFailureEvent> _UPDATE_FAILURE = new EventType<>(CustomerOpFailureEvent.CUSTOMER_OP_FAILURE, "SCHEDULER_");

    /**
     * {@link EventType} for all {@link DbOperationType#DELETE} {@code CustomerOpFailureEvent}s.
     */
    public static final EventType<CustomerOpFailureEvent> _DELETE_FAILURE = new EventType<>(CustomerOpFailureEvent.CUSTOMER_OP_FAILURE, "SCHEDULER_");

    static final EventType<CustomerOpFailureEvent> toEventType(DbOperationType operation) {
        switch (operation) {
            case INSERT:
                return _INSERT_FAILURE;
            case DELETE:
                return _DELETE_FAILURE;
            default:
                return _UPDATE_FAILURE;
        }
    }
    
    private CustomerOpFailureEvent(CustomerOpFailureEvent sourceEvent, Object newSource, EventTarget newTarget) {
        super(sourceEvent, newSource, newTarget);
    }

    public CustomerOpFailureEvent(Object source, EventTarget target, CustomerModel model, Throwable fault, DbOperationType operation, String message) {
        super(source, target, toEventType(operation), model, fault, operation, message);
    }

    public CustomerOpFailureEvent(Object source, EventTarget target, CustomerModel model, DbOperationType operation, boolean canceled, String message) {
        super(source, target, toEventType(operation), model, operation, canceled, message);
    }

    public CustomerOpFailureEvent(Object source, EventTarget target, CustomerDAO dao, Throwable fault, DbOperationType operation, String message) {
        super(source, target, toEventType(operation), dao, fault, operation, message);
    }

    public CustomerOpFailureEvent(Object source, EventTarget target, CustomerDAO dao, DbOperationType operation, boolean canceled, String message) {
        super(source, target, toEventType(operation), dao, operation, canceled, message);
    }

    CustomerOpFailureEvent(CustomerBeginOpEvent failedEvent, EventType<CustomerOpFailureEvent> eventType) {
        super(failedEvent, eventType);
    }

    CustomerOpFailureEvent(CustomerValidatingEvent failedEvent, EventType<CustomerOpFailureEvent> eventType) {
        super(failedEvent, eventType);
    }

    @Override
    public CustomerOpFailureEvent copyFor(Object newSource, EventTarget newTarget) {
        return new CustomerOpFailureEvent(this, newSource, newTarget);
    }
    
}
