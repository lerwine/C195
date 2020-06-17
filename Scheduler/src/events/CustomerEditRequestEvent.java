package events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.CustomerDAO;
import scheduler.model.ui.CustomerModel;


public final class CustomerEditRequestEvent extends ModelEditRequestEvent<CustomerModel, CustomerDAO> {

    /**
     * {@link EventType} for all {@code CustomerEditRequestEvent}s.
     */
    public static final EventType<CustomerEditRequestEvent> CUSTOMER_EDIT_REQUEST = new EventType<>(ModelEditRequestEvent.EDIT_REQUEST_EVENT, "SCHEDULER_CUSTOMER_EDIT_REQUEST");

    private CustomerEditRequestEvent(CustomerEditRequestEvent sourceEvent, Object newSource, EventTarget newTarget) {
        super(sourceEvent, newSource, newTarget);
    }

    public CustomerEditRequestEvent(Object source, EventTarget target, EventType<CustomerEditRequestEvent> eventType, CustomerModel model) {
        super(source, target, eventType, model);
    }

    public CustomerEditRequestEvent(Object source, EventTarget target, EventType<CustomerEditRequestEvent> eventType, CustomerDAO dao) {
        super(source, target, eventType, dao);
    }

    @Override
    public CustomerEditRequestEvent copyFor(Object newSource, EventTarget newTarget) {
        return new CustomerEditRequestEvent(this, newSource, newTarget);
    }

    @Override
    public CustomerBeginOpEvent toBeginOperationEvent() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement events.CustomerEditRequestEvent#toBeginOperationEvent
    }
    
}
