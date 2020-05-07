package scheduler.dao.event;

import java.util.Objects;
import javafx.event.Event;
import static javafx.event.Event.ANY;
import javafx.event.EventType;
import scheduler.dao.DataAccessObject;

/**
 * Represents a {@link DaoChangeAction#CREATED}, {@link DaoChangeAction#CREATED} or {@link DaoChangeAction#CREATED} event for a
 * {@link DataAccessObject}.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> The type of {@link DataAccessObject} affected.
 */
public abstract class DataObjectEvent<T extends DataAccessObject> extends Event {

    private static final long serialVersionUID = -4153967201114554143L;

    /**
     * Base event type for all {@link DataAccessObject} events.
     */
    public static final EventType<DataObjectEvent<? extends DataAccessObject>> ANY_DAO_EVENT = new EventType<>(ANY, "SCHEDULER_ANY_DAO_EVENT");

    /**
     * Generic event type for all {@link DataAccessObject} {@link DaoChangeAction#CREATED} events.
     */
    public static final EventType<DataObjectEvent<? extends DataAccessObject>> ANY_DAO_INSERT = new EventType<>(ANY_DAO_EVENT, "SCHEDULER_ANY_DAO_INSERT");

    /**
     * Generic event type for all {@link DataAccessObject} {@link DaoChangeAction#UPDATED} events.
     */
    public static final EventType<DataObjectEvent<? extends DataAccessObject>> ANY_DAO_UPDATE = new EventType<>(ANY_DAO_EVENT, "SCHEDULER_ANY_DAO_UPDATE");

    /**
     * Generic event type for all {@link DataAccessObject} {@link DaoChangeAction#DELETED} events.
     */
    public static final EventType<DataObjectEvent<? extends DataAccessObject>> ANY_DAO_DELETE = new EventType<>(ANY_DAO_EVENT, "SCHEDULER_ANY_DAO_DELETE");

    private final DaoChangeAction changeAction;

    /**
     * Initializes a new {@link DataAccessObject} event.
     *
     * @param source The object which sent the {@code DataObjectEvent}.
     * @param dataObject The target {@link DataAccessObject}.
     * @param changeAction The {@link DaoChangeAction} value indicating the type of change event that occurred.
     * @param eventType The event type.
     */
    protected DataObjectEvent(Object source, T dataObject, DaoChangeAction changeAction, EventType<? extends DataObjectEvent<T>> eventType) {
        super(source, Objects.requireNonNull(dataObject), eventType);
        this.changeAction = Objects.requireNonNull(changeAction);
    }

    /**
     * Gets the type of change event that occurred.
     *
     * @return A {@link DaoChangeAction} value indicating the type of change event that occurred.
     */
    public DaoChangeAction getChangeAction() {
        return changeAction;
    }

    /**
     * Gets the {@link DataAccessObject} that was affected.
     *
     * @return The {@link DataAccessObject} instance that was affected.
     */
    @Override
    public T getTarget() {
        return (T) super.getTarget();
    }
}
