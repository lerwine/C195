package scheduler.dao.event;

import java.util.Objects;
import javafx.event.Event;
import javafx.event.EventType;
import scheduler.dao.DataAccessObject;

/**
 * Represents a {@link DbChangeType#CREATED}, {@link DbChangeType#CREATED} or {@link DbChangeType#CREATED} event for a {@link DataAccessObject}.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> The type of {@link DataAccessObject}.
 */
public class DataObjectEvent<T extends DataAccessObject> extends Event {

    private static final long serialVersionUID = -4153967201114554143L;

    /**
     * Base event type for all {@link DataAccessObject} events.
     */
    public static final EventType<DataObjectEvent<? extends DataAccessObject>> ANY_DAO_EVENT = new EventType<>(ANY, "SCHEDULER_ANY_DAO_EVENT");

    /**
     * Generic event type for all {@link DataAccessObject} {@link DbChangeType#CREATED} events. This a generic super-type and does not use
     * {@link #ANY_DAO_EVENT} as the super-type, because that would cause handlers for {@link #ANY_DAO_EVENT} to get invoked twice.
     */
    public static final EventType<DataObjectEvent<DataAccessObject>> ANY_DAO_INSERT = new EventType<>(ANY, "SCHEDULER_ANY_DAO_INSERT");

    /**
     * Generic event type for all {@link DataAccessObject} {@link DbChangeType#UPDATED} events. This a generic super-type and does not use
     * {@link #ANY_DAO_EVENT} as the super-type, because that would cause handlers for {@link #ANY_DAO_EVENT} to get invoked twice.
     */
    public static final EventType<DataObjectEvent<DataAccessObject>> ANY_DAO_UPDATE = new EventType<>(ANY, "SCHEDULER_ANY_DAO_UPDATE");

    /**
     * Generic event type for all {@link DataAccessObject} {@link DbChangeType#DELETED} events. This a generic super-type and does not use
     * {@link #ANY_DAO_EVENT} as the super-type, because that would cause handlers for {@link #ANY_DAO_EVENT} to get invoked twice.
     */
    public static final EventType<DataObjectEvent<DataAccessObject>> ANY_DAO_DELETE = new EventType<>(ANY, "SCHEDULER_ANY_DAO_DELETE");

    public static void fireGenericEvent(AddressDaoEvent event) {
        Event.fireEvent(event.getTarget(), new DataObjectEvent<>(event));
    }

    public static void fireGenericEvent(AppointmentDaoEvent event) {
        Event.fireEvent(event.getTarget(), new DataObjectEvent<>(event));
    }

    public static void fireGenericEvent(CityDaoEvent event) {
        Event.fireEvent(event.getTarget(), new DataObjectEvent<>(event));
    }

    public static void fireGenericEvent(CountryDaoEvent event) {
        Event.fireEvent(event.getTarget(), new DataObjectEvent<>(event));
    }

    public static void fireGenericEvent(CustomerDaoEvent event) {
        Event.fireEvent(event.getTarget(), new DataObjectEvent<>(event));
    }

    public static void fireGenericEvent(UserDaoEvent event) {
        Event.fireEvent(event.getTarget(), new DataObjectEvent<>(event));
    }

    private final DbChangeType changeType;

    /**
     * Initializes a new {@link DataAccessObject} event.
     *
     * @param source The object which sent the {@code DataObjectEvent}.
     * @param dataObject The target {@link DataAccessObject}.
     * @param changeType The {@link DbChangeType} value indicating the type of change event that occurred.
     * @param eventType The event type.
     */
    protected DataObjectEvent(Object source, T dataObject, DbChangeType changeType, EventType<? extends DataObjectEvent<? extends T>> eventType) {
        super(source, Objects.requireNonNull(dataObject), eventType);
        this.changeType = Objects.requireNonNull(changeType);
    }

    private DataObjectEvent(DataObjectEvent<? extends T> event) {
        super(event.getSource(), event.getTarget(), event.changeType.getEventType());
        changeType = event.changeType;
    }

    /**
     * Gets the type of database change that occurred.
     *
     * @return A {@link DbChangeType} value indicating the type of database change that occurred.
     */
    public DbChangeType getChangeType() {
        return changeType;
    }

    /**
     * Gets the {@link DataAccessObject} that was affected.
     *
     * @return The {@link DataAccessObject} instance that was affected.
     */
    @Override
    @SuppressWarnings("unchecked")
    public T getTarget() {
        return (T) super.getTarget();
    }

    @Override
    @SuppressWarnings("unchecked")
    public EventType<? extends DataObjectEvent<? extends T>> getEventType() {
        return (EventType<? extends DataObjectEvent<? extends T>>) super.getEventType();
    }

}
