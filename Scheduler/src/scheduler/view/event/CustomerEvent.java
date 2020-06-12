package scheduler.view.event;

import java.util.Objects;
import javafx.event.EventTarget;
import javafx.event.EventType;
import scheduler.dao.CustomerDAO;
import scheduler.model.ui.CustomerModel;

/**
 * Event that is fired when a {@link CustomerModel} is about to be saved or deleted.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public final class CustomerEvent extends ModelItemEvent<CustomerModel, CustomerDAO> {

    private static final long serialVersionUID = 2391804793246253841L;
    public static final String CUSTOMER_MODEL_EVENT_NAME = "CUSTOMER_MODEL_EVENT";
    public static final String EDIT_REQUEST_EVENT_NAME = "CUSTOMER_EDIT_REQUEST_EVENT";
    public static final String DELETE_REQUEST_EVENT_NAME = "CUSTOMER_DELETE_REQUEST_EVENT";
    public static final String INSERTING_EVENT_NAME = "CUSTOMER_INSERTING_EVENT";
    public static final String INSERTED_EVENT_NAME = "CUSTOMER_INSERTED_EVENT";
    public static final String UPDATING_EVENT_NAME = "CUSTOMER_UPDATING_EVENT";
    public static final String UPDATED_EVENT_NAME = "CUSTOMER_UPDATED_EVENT";
    public static final String DELETING_EVENT_NAME = "CUSTOMER_DELETING_EVENT";
    public static final String DELETED_EVENT_NAME = "CUSTOMER_DELETED_EVENT";

    public static final EventType<CustomerEvent> CUSTOMER_MODEL_EVENT = new EventType<>(MODEL_ITEM_EVENT, CUSTOMER_MODEL_EVENT_NAME);

    public static final EventType<CustomerEvent> EDIT_REQUEST_EVENT_TYPE = new EventType<>(CUSTOMER_MODEL_EVENT, EDIT_REQUEST_EVENT_NAME);

    public static final EventType<CustomerEvent> DELETE_REQUEST_EVENT_TYPE = new EventType<>(CUSTOMER_MODEL_EVENT, DELETE_REQUEST_EVENT_NAME);

    public static final EventType<CustomerEvent> INSERTING_EVENT_TYPE = new EventType<>(CUSTOMER_MODEL_EVENT, INSERTING_EVENT_NAME);

    public static final EventType<CustomerEvent> INSERTED_EVENT_TYPE = new EventType<>(CUSTOMER_MODEL_EVENT, INSERTED_EVENT_NAME);

    public static final EventType<CustomerEvent> UPDATING_EVENT_TYPE = new EventType<>(CUSTOMER_MODEL_EVENT, UPDATING_EVENT_NAME);

    public static final EventType<CustomerEvent> UPDATED_EVENT_TYPE = new EventType<>(CUSTOMER_MODEL_EVENT, UPDATED_EVENT_NAME);

    public static final EventType<CustomerEvent> DELETING_EVENT_TYPE = new EventType<>(CUSTOMER_MODEL_EVENT, DELETING_EVENT_NAME);

    public static final EventType<CustomerEvent> DELETED_EVENT_TYPE = new EventType<>(CUSTOMER_MODEL_EVENT, DELETED_EVENT_NAME);

    public static ActivityType toActionType(String eventName) {
        if (null != eventName) {
            switch (eventName) {
                case EDIT_REQUEST_EVENT_NAME:
                    return ActivityType.EDIT_REQUEST;
                case DELETE_REQUEST_EVENT_NAME:
                    return ActivityType.DELETE_REQUEST;
                case INSERTING_EVENT_NAME:
                    return ActivityType.INSERTING;
                case INSERTED_EVENT_NAME:
                    return ActivityType.INSERTED;
                case UPDATING_EVENT_NAME:
                    return ActivityType.UPDATING;
                case UPDATED_EVENT_NAME:
                    return ActivityType.UPDATED;
                case DELETING_EVENT_NAME:
                    return ActivityType.DELETING;
                case DELETED_EVENT_NAME:
                    return ActivityType.DELETED;
            }
        }
        return ActivityType.NONE;
    }

    public static EventType<CustomerEvent> toEventType(ActivityType action) {
        if (null != action) {
            switch (action) {
                case EDIT_REQUEST:
                    return EDIT_REQUEST_EVENT_TYPE;
                case DELETE_REQUEST:
                    return DELETE_REQUEST_EVENT_TYPE;
                case INSERTING:
                    return INSERTING_EVENT_TYPE;
                case INSERTED:
                    return INSERTED_EVENT_TYPE;
                case UPDATING:
                    return UPDATING_EVENT_TYPE;
                case UPDATED:
                    return UPDATED_EVENT_TYPE;
                case DELETING:
                    return DELETING_EVENT_TYPE;
                case DELETED:
                    return DELETED_EVENT_TYPE;
            }
        }
        return null;
    }

    private CustomerEvent(CustomerEvent copyFrom, Object source, EventTarget target) {
        super(copyFrom, source, target);
    }

    public CustomerEvent(CustomerModel model, Object source, EventTarget target, ActivityType action, boolean confirmed) {
        super(model, source, target, Objects.requireNonNull(toEventType(action)), action, confirmed);
    }

    public CustomerEvent(CustomerModel model, Object source, EventTarget target, ActivityType action) {
        super(model, source, target, Objects.requireNonNull(toEventType(action)), action, false);
    }

    public CustomerEvent(Object source, EventTarget target, CustomerDAO dao, ActivityType action, boolean confirmed) {
        super(source, target, dao, Objects.requireNonNull(toEventType(action)), action, confirmed);
    }

    public CustomerEvent(Object source, EventTarget target, CustomerDAO dao, ActivityType action) {
        super(source, target, dao, Objects.requireNonNull(toEventType(action)), action, false);
    }

    @Override
    public CustomerModel.Factory getModelFactory() {
        return CustomerModel.FACTORY;
    }

    @Override
    public synchronized CustomerEvent copyFor(Object newSource, EventTarget newTarget) {
        return new CustomerEvent(this, newSource, newTarget);
    }

    @Override
    @SuppressWarnings("unchecked")
    public EventType<CustomerEvent> getEventType() {
        return (EventType<CustomerEvent>) super.getEventType();
    }

}
