package scheduler.events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.CustomerDAO;
import scheduler.model.ui.CustomerModel;

public abstract class CustomerEvent extends ModelEvent<CustomerDAO, CustomerModel> {

    /**
     * Base {@link EventType} for all {@code AppointmentEvent}s.
     */
    public static final EventType<CustomerEvent> CUSTOMER_EVENT_TYPE = new EventType<>(MODEL_EVENT_TYPE, "SCHEDULER_CUSTOMER_EVENT");

    /**
     * Base {@link EventType} for all operational {@code AppointmentEvent}s.
     */
    public static final EventType<CustomerEvent> OP_EVENT_TYPE = new EventType<>(CUSTOMER_EVENT_TYPE, "SCHEDULER_CUSTOMER_OP_EVENT");

    protected CustomerEvent(CustomerEvent event, Object source, EventTarget target, EventType<? extends CustomerEvent> eventType) {
        super(event, source, target, eventType);
    }

    protected CustomerEvent(CustomerEvent event, EventType<? extends CustomerEvent> eventType) {
        super(event, eventType);
    }

    protected CustomerEvent(CustomerModel fxRecordModel, Object source, EventTarget target, EventType<? extends CustomerEvent> eventType) {
        super(fxRecordModel, source, target, eventType);
    }

    protected CustomerEvent(CustomerDAO dao, Object source, EventTarget target, EventType<? extends CustomerEvent> eventType) {
        super(dao, source, target, eventType);
    }

}
