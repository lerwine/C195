package events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.CustomerDAO;
import scheduler.model.ui.CustomerModel;


public final class CustomerValidatingEvent extends DbValidatingEvent<CustomerModel, CustomerDAO> {

    /**
     * Base {@link EventType} for all {@code CustomerValidatingEvent}s.
     */
    public static final EventType<CustomerValidatingEvent> CUSTOMER_DB_VALIDATING = new EventType<>(DbValidatingEvent.DB_VALIDATING_EVENT, "SCHEDULER_CUSTOMER_DB_VALIDATING");

    /**
     * {@link EventType} for all {@link DbOperationType#INSERT} {@code CustomerValidatingEvent}s.
     */
    public static final EventType<CustomerValidatingEvent> _VALIDATING_INSERT = new EventType<>(CustomerValidatingEvent.CUSTOMER_DB_VALIDATING, "SCHEDULER_");

    /**
     * {@link EventType} for all {@link DbOperationType#UPDATE} {@code CustomerValidatingEvent}s.
     */
    public static final EventType<CustomerValidatingEvent> _VALIDATING_UPDATE = new EventType<>(CustomerValidatingEvent.CUSTOMER_DB_VALIDATING, "SCHEDULER_");

    /**
     * {@link EventType} for all {@link DbOperationType#DELETE} {@code CustomerValidatingEvent}s.
     */
    public static final EventType<CustomerValidatingEvent> _VALIDATING_DELETE = new EventType<>(CustomerValidatingEvent.CUSTOMER_DB_VALIDATING, "SCHEDULER_");

    static final EventType<CustomerValidatingEvent> toEventType(DbOperationType operation) {
        switch (operation) {
            case INSERT:
                return _VALIDATING_INSERT;
            case DELETE:
                return _VALIDATING_DELETE;
            default:
                return _VALIDATING_UPDATE;
        }
    }
    
    CustomerValidatingEvent(CustomerBeginOpEvent precedingEvent, EventType<CustomerValidatingEvent> newType) {
        super(precedingEvent, newType);
    }

    private CustomerValidatingEvent(CustomerValidatingEvent sourceEvent, Object newSource, EventTarget newTarget) {
        super(sourceEvent, newSource, newTarget);
    }

    public CustomerValidatingEvent(Object source, EventTarget target, CustomerModel model, DbOperationType operation) {
        super(source, target, toEventType(operation), model, operation);
    }

    public CustomerValidatingEvent(Object source, EventTarget target, CustomerDAO dao, DbOperationType operation) {
        super(source, target, toEventType(operation), dao, operation);
    }

    @Override
    public CustomerValidatingEvent copyFor(Object newSource, EventTarget newTarget) {
        return new CustomerValidatingEvent(this, newSource, newTarget);
    }

    @Override
    public CustomerOpCompletedEvent toOperationCompletedEvent() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement events.CustomerValidatingEvent#toOperationCompletedEvent
    }

    @Override
    public CustomerOpFailureEvent toOperationFaultEvent(Throwable fault, String message) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement events.CustomerValidatingEvent#toOperationFaultEvent
    }

    @Override
    public CustomerOpFailureEvent toOperationCanceledEvent(String message) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement events.CustomerValidatingEvent#toOperationCanceledEvent
    }
    
}
