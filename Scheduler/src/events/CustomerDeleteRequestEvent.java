package events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.CustomerDAO;
import scheduler.model.ui.CustomerModel;


public final class CustomerDeleteRequestEvent extends ModelDeleteRequestEvent<CustomerModel, CustomerDAO> {

    /**
     * {@link EventType} for all {@code CustomerDeleteRequestEvent}s.
     */
    public static final EventType<CustomerDeleteRequestEvent> CUSTOMER_DELETE_REQUEST = new EventType<>(ModelDeleteRequestEvent.DELETE_REQUEST_EVENT, "SCHEDULER_CUSTOMER_DELETE_REQUEST");

    private CustomerDeleteRequestEvent(CustomerDeleteRequestEvent sourceEvent, Object newSource, EventTarget newTarget) {
        super(sourceEvent, newSource, newTarget);
    }

    public CustomerDeleteRequestEvent(Object source, EventTarget target, EventType<CustomerDeleteRequestEvent> eventType, CustomerModel model) {
        super(source, target, eventType, model);
    }

    public CustomerDeleteRequestEvent(Object source, EventTarget target, EventType<CustomerDeleteRequestEvent> eventType, CustomerDAO dao) {
        super(source, target, eventType, dao);
    }

    @Override
    public CustomerDeleteRequestEvent copyFor(Object newSource, EventTarget newTarget) {
        return new CustomerDeleteRequestEvent(this, newSource, newTarget);
    }

    @Override
    public CustomerBeginOpEvent toBeginOperationEvent() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement events.CustomerDeleteRequestEvent#toBeginOperationEvent
    }
    
}
