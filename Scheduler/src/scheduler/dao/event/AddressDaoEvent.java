package scheduler.dao.event;

import javafx.event.EventType;
import scheduler.dao.AddressDAO;

/**
 * Represents a {@link DaoChangeAction#CREATED}, {@link DaoChangeAction#CREATED} or {@link DaoChangeAction#CREATED} event for a {@link AddressDAO}.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class AddressDaoEvent extends DataObjectEvent<AddressDAO> {

    /**
     * Base event type for all {@link AddressDAO} events.
     */
    public static final EventType<AddressDaoEvent> ANY_ADDRESS_EVENT = new EventType<>(ANY_DAO_EVENT, "SCHEDULER_ANY_ADDRESS_EVENT");

    /**
     * Event type for {@link AddressDAO} {@link DaoChangeAction#CREATED} events.
     */
    public static final EventType<AddressDaoEvent> ADDRESS_DAO_INSERT = new EventType<>(ANY_ADDRESS_EVENT, "SCHEDULER_ADDRESS_DAO_INSERT");

    /**
     * Event type for {@link AddressDAO} {@link DaoChangeAction#UPDATED} events.
     */
    public static final EventType<AddressDaoEvent> ADDRESS_DAO_UPDATE = new EventType<>(ANY_ADDRESS_EVENT, "SCHEDULER_ADDRESS_DAO_UPDATE");

    /**
     * Event type for {@link AddressDAO} {@link DaoChangeAction#DELETED} events.
     */
    public static final EventType<AddressDaoEvent> ADDRESS_DAO_DELETE = new EventType<>(ANY_ADDRESS_EVENT, "SCHEDULER_ADDRESS_DAO_DELETE");

    public static EventType<AddressDaoEvent> toEventType(DaoChangeAction changeAction) {
        switch (changeAction) {
            case CREATED:
                return ADDRESS_DAO_INSERT;
            case DELETED:
                return ADDRESS_DAO_DELETE;
            default:
                return ADDRESS_DAO_UPDATE;
        }
    }

    /**
     * Initializes a new {@link AddressDAO} event.
     *
     * @param source The object which sent the {@code AddressDaoEvent}.
     * @param changeAction The {@link DaoChangeAction} value indicating the type of change event that occurred.
     * @param dataObject The target {@link AddressDAO}.
     */
    public AddressDaoEvent(Object source, DaoChangeAction changeAction, AddressDAO dataObject) {
        super(source, dataObject, changeAction, toEventType(changeAction));
    }

}
