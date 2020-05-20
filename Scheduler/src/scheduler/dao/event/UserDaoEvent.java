package scheduler.dao.event;

import javafx.event.EventType;
import scheduler.dao.UserDAO;

/**
 * Represents a {@link DbChangeType#CREATED}, {@link DbChangeType#CREATED} or {@link DbChangeType#CREATED} event for a {@link UserDAO}.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class UserDaoEvent extends DataObjectEvent<UserDAO> {

    private static final long serialVersionUID = -1168833365092070768L;

    /**
     * Base event type for all {@link UserDAO} events.
     */
    public static final EventType<UserDaoEvent> ANY_USER_EVENT = new EventType<>(ANY_DAO_EVENT, "SCHEDULER_ANY_USER_EVENT");

    /**
     * Event type for {@link UserDAO} {@link DbChangeType#CREATED} events.
     */
    public static final EventType<UserDaoEvent> USER_DAO_INSERT = new EventType<>(ANY_USER_EVENT, "SCHEDULER_USER_DAO_INSERT");

    /**
     * Event type for {@link UserDAO} {@link DbChangeType#UPDATED} events.
     */
    public static final EventType<UserDaoEvent> USER_DAO_UPDATE = new EventType<>(ANY_USER_EVENT, "SCHEDULER_USER_DAO_UPDATE");

    /**
     * Event type for {@link UserDAO} {@link DbChangeType#DELETED} events.
     */
    public static final EventType<UserDaoEvent> USER_DAO_DELETE = new EventType<>(ANY_USER_EVENT, "SCHEDULER_USER_DAO_DELETE");

    public static EventType<UserDaoEvent> toEventType(DbChangeType changeType) {
        switch (changeType) {
            case CREATED:
                return USER_DAO_INSERT;
            case DELETED:
                return USER_DAO_DELETE;
            default:
                return USER_DAO_UPDATE;
        }
    }

    /**
     * Initializes a new {@link UserDAO} event.
     *
     * @param source The object which sent the {@code UserDaoEvent}.
     * @param changeAction The {@link DbChangeType} value indicating the type of change event that occurred.
     * @param dataObject The target {@link UserDAO}.
     */
    public UserDaoEvent(Object source, DbChangeType changeAction, UserDAO dataObject) {
        super(source, dataObject, changeAction, toEventType(changeAction));
    }

    @Override
    @SuppressWarnings("unchecked")
    public EventType<UserDaoEvent> getEventType() {
        return (EventType<UserDaoEvent>) super.getEventType();
    }

}
