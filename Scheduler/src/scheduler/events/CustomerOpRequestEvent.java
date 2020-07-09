package scheduler.events;

import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.CustomerDAO;
import scheduler.model.ui.CustomerModel;

public final class CustomerOpRequestEvent extends OperationRequestEvent<CustomerDAO, CustomerModel> {

    private static final long serialVersionUID = -8689654096247827717L;

    /**
     * Base {@link EventType} for all {@code CustomerOpRequestEvent}s.
     */
    public static final EventType<CustomerOpRequestEvent> CUSTOMER_OP_REQUEST = new EventType<>(OP_REQUEST_EVENT, "SCHEDULER_CUSTOMER_OP_REQUEST");

    /**
     * {@link EventType} for edit {@code CustomerOpRequestEvent}s.
     */
    public static final EventType<CustomerOpRequestEvent> EDIT_REQUEST = new EventType<>(CUSTOMER_OP_REQUEST, "SCHEDULER_CUSTOMER_EDIT_REQUEST");

    /**
     * {@link EventType} for delete {@code CustomerOpRequestEvent}s.
     */
    public static final EventType<CustomerOpRequestEvent> DELETE_REQUEST = new EventType<>(CUSTOMER_OP_REQUEST, "SCHEDULER_CUSTOMER_DELETE_REQUEST");

    public CustomerOpRequestEvent(ModelEvent<CustomerDAO, CustomerModel> event, Object source, EventTarget target, boolean isDelete) {
        super(event, source, target, (isDelete) ? DELETE_REQUEST : EDIT_REQUEST, isDelete);
    }

    public CustomerOpRequestEvent(ModelEvent<CustomerDAO, CustomerModel> event, boolean isDelete) {
        super(event, (isDelete) ? DELETE_REQUEST : EDIT_REQUEST, isDelete);
    }

    public CustomerOpRequestEvent(CustomerModel target, Object source, boolean isDelete) {
        super(target, source, (isDelete) ? DELETE_REQUEST : EDIT_REQUEST, isDelete);
    }

}
