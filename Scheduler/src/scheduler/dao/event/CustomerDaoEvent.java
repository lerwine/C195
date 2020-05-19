package scheduler.dao.event;

import javafx.event.EventType;
import scheduler.dao.CustomerDAO;

/**
 * Represents a {@link DbChangeType#CREATED}, {@link DbChangeType#CREATED} or {@link DbChangeType#CREATED} event for a {@link CustomerDAO}.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class CustomerDaoEvent extends DataObjectEvent<CustomerDAO> {

    /**
     * Base event type for all {@link CustomerDAO} events.
     */
    public static final EventType<CustomerDaoEvent> ANY_CUSTOMER_EVENT = new EventType<>(ANY_DAO_EVENT, "SCHEDULER_ANY_CUSTOMER_EVENT");

    /**
     * Event type for {@link CustomerDAO} {@link DbChangeType#CREATED} events.
     */
    public static final EventType<CustomerDaoEvent> CUSTOMER_DAO_INSERT = new EventType<>(ANY_CUSTOMER_EVENT, "SCHEDULER_CUSTOMER_DAO_INSERT");

    /**
     * Event type for {@link CustomerDAO} {@link DbChangeType#UPDATED} events.
     */
    public static final EventType<CustomerDaoEvent> CUSTOMER_DAO_UPDATE = new EventType<>(ANY_CUSTOMER_EVENT, "SCHEDULER_CUSTOMER_DAO_UPDATE");

    /**
     * Event type for {@link CustomerDAO} {@link DbChangeType#DELETED} events.
     */
    public static final EventType<CustomerDaoEvent> CUSTOMER_DAO_DELETE = new EventType<>(ANY_CUSTOMER_EVENT, "SCHEDULER_CUSTOMER_DAO_DELETE");

    public static EventType<CustomerDaoEvent> toEventType(DbChangeType changeType) {
        switch (changeType) {
            case CREATED:
                return CUSTOMER_DAO_INSERT;
            case DELETED:
                return CUSTOMER_DAO_DELETE;
            default:
                return CUSTOMER_DAO_UPDATE;
        }
    }

    /**
     * Initializes a new {@link CustomerDAO} event.
     *
     * @param source The object which sent the {@code CustomerDaoEvent}.
     * @param changeAction The {@link DbChangeType} value indicating the type of change event that occurred.
     * @param dataObject The target {@link CustomerDAO}.
     */
    public CustomerDaoEvent(Object source, DbChangeType changeAction, CustomerDAO dataObject) {
        super(source, dataObject, changeAction, toEventType(changeAction));
    }

    @Override
    @SuppressWarnings("unchecked")
    public EventType<CustomerDaoEvent> getEventType() {
        return (EventType<CustomerDaoEvent>) super.getEventType();
    }

}
