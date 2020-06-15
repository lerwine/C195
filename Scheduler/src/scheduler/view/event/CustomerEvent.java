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
public final class CustomerEvent extends DbOperationEvent<CustomerModel, CustomerDAO> {

    private static final long serialVersionUID = 2391804793246253841L;
    public static final String CUSTOMER_MODEL_EVENT_NAME = "SCHEDULER_CUSTOMER_DB_OPERATION";
    public static final String EDIT_REQUEST_EVENT_NAME = "SCHEDULER_CUSTOMER_EDIT_REQUEST";
    public static final String DELETE_REQUEST_EVENT_NAME = "SCHEDULER_CUSTOMER_DELETE_REQUEST";
    public static final String INSERTING_EVENT_NAME = "SCHEDULER_CUSTOMER_INSERTING";
    public static final String INSERTED_EVENT_NAME = "SCHEDULER_CUSTOMER_INSERTED";
    public static final String UPDATING_EVENT_NAME = "SCHEDULER_CUSTOMER_UPDATING";
    public static final String UPDATED_EVENT_NAME = "SCHEDULER_CUSTOMER_UPDATED";
    public static final String DELETING_EVENT_NAME = "SCHEDULER_CUSTOMER_DELETING";
    public static final String DELETED_EVENT_NAME = "SCHEDULER_CUSTOMER_DELETED";

    /**
     * Base {@link EventType} for all {@code CustomerEvent}s.
     */
    public static final EventType<CustomerEvent> CUSTOMER_MODEL_EVENT_TYPE = new EventType<>(DB_OPERATION, CUSTOMER_MODEL_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#EDIT_REQUEST} {@code CustomerEvent}s.
     */
    public static final EventType<CustomerEvent> EDIT_REQUEST_EVENT_TYPE = new EventType<>(CUSTOMER_MODEL_EVENT_TYPE, EDIT_REQUEST_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#DELETE_REQUEST} {@code CustomerEvent}s.
     */
    public static final EventType<CustomerEvent> DELETE_REQUEST_EVENT_TYPE = new EventType<>(CUSTOMER_MODEL_EVENT_TYPE, DELETE_REQUEST_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#INSERTING} {@code CustomerEvent}s.
     */
    public static final EventType<CustomerEvent> INSERTING_EVENT_TYPE = new EventType<>(CUSTOMER_MODEL_EVENT_TYPE, INSERTING_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#INSERTED} {@code CustomerEvent}s.
     */
    public static final EventType<CustomerEvent> INSERTED_EVENT_TYPE = new EventType<>(CUSTOMER_MODEL_EVENT_TYPE, INSERTED_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#UPDATING} {@code CustomerEvent}s.
     */
    public static final EventType<CustomerEvent> UPDATING_EVENT_TYPE = new EventType<>(CUSTOMER_MODEL_EVENT_TYPE, UPDATING_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#UPDATED} {@code CustomerEvent}s.
     */
    public static final EventType<CustomerEvent> UPDATED_EVENT_TYPE = new EventType<>(CUSTOMER_MODEL_EVENT_TYPE, UPDATED_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#DELETING} {@code CustomerEvent}s.
     */
    public static final EventType<CustomerEvent> DELETING_EVENT_TYPE = new EventType<>(CUSTOMER_MODEL_EVENT_TYPE, DELETING_EVENT_NAME);

    /**
     * {@link EventType} for {@link DbOperationType#DELETED} {@code CustomerEvent}s.
     */
    public static final EventType<CustomerEvent> DELETED_EVENT_TYPE = new EventType<>(CUSTOMER_MODEL_EVENT_TYPE, DELETED_EVENT_NAME);

    public static DbOperationType toDbOperationType(String eventName) {
        if (null != eventName) {
            switch (eventName) {
                case EDIT_REQUEST_EVENT_NAME:
                    return DbOperationType.EDIT_REQUEST;
                case DELETE_REQUEST_EVENT_NAME:
                    return DbOperationType.DELETE_REQUEST;
                case INSERTING_EVENT_NAME:
                    return DbOperationType.INSERTING;
                case INSERTED_EVENT_NAME:
                    return DbOperationType.INSERTED;
                case UPDATING_EVENT_NAME:
                    return DbOperationType.UPDATING;
                case UPDATED_EVENT_NAME:
                    return DbOperationType.UPDATED;
                case DELETING_EVENT_NAME:
                    return DbOperationType.DELETING;
                case DELETED_EVENT_NAME:
                    return DbOperationType.DELETED;
            }
        }
        return DbOperationType.NONE;
    }

    @SuppressWarnings("incomplete-switch")
    public static EventType<CustomerEvent> toEventType(DbOperationType operation) {
        if (null != operation) {
            switch (operation) {
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
        return CUSTOMER_MODEL_EVENT_TYPE;
    }

    private CustomerEvent(CustomerEvent copyFrom, Object source, EventTarget target) {
        super(copyFrom, source, target);
    }

    public CustomerEvent(CustomerModel model, Object source, EventTarget target, DbOperationType operation, boolean confirmed) {
        super(model, source, target, Objects.requireNonNull(toEventType(operation)), operation, confirmed);
    }

    public CustomerEvent(CustomerModel model, Object source, EventTarget target, DbOperationType operation) {
        super(model, source, target, Objects.requireNonNull(toEventType(operation)), operation, false);
    }

    public CustomerEvent(Object source, EventTarget target, CustomerDAO dao, DbOperationType operation, boolean confirmed) {
        super(source, target, dao, Objects.requireNonNull(toEventType(operation)), operation, confirmed);
    }

    public CustomerEvent(Object source, EventTarget target, CustomerDAO dao, DbOperationType operation) {
        super(source, target, dao, Objects.requireNonNull(toEventType(operation)), operation, false);
    }

    @Override
    @SuppressWarnings("unchecked")
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
