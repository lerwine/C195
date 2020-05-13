package scheduler.dao.event;

import javafx.event.EventType;

/**
 * Represents the type of database change that occurred.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public enum DbChangeType {
    /**
     * The target {@link scheduler.dao.DataAccessObject} was inserted into the database.
     */
    CREATED(DataObjectEvent.ANY_DAO_INSERT),
    /**
     * The target {@link scheduler.dao.DataAccessObject} was updated in the database.
     */
    UPDATED(DataObjectEvent.ANY_DAO_UPDATE),
    /**
     * The target {@link scheduler.dao.DataAccessObject} was removed from the database.
     */
    DELETED(DataObjectEvent.ANY_DAO_DELETE);

    private final EventType<? extends DataObjectEvent> eventType;

    private DbChangeType(EventType<? extends DataObjectEvent> eventType) {
        this.eventType = eventType;
    }

    /**
     * Gets the generic {@link EventType} for the change type.
     *
     * @return The generic {@link EventType} for the change type.
     */
    public EventType<? extends DataObjectEvent> getEventType() {
        return eventType;
    }
}
