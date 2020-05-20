package scheduler.dao.event;

import javafx.event.EventType;
import scheduler.dao.CountryDAO;

/**
 * Represents a {@link DbChangeType#CREATED}, {@link DbChangeType#CREATED} or {@link DbChangeType#CREATED} event for a {@link CountryDAO}.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class CountryDaoEvent extends DataObjectEvent<CountryDAO> {

    /**
     *
     */
    private static final long serialVersionUID = 7785046806532887128L;

    /**
     * Base event type for all {@link CountryDAO} events.
     */
    public static final EventType<CountryDaoEvent> ANY_COUNTRY_EVENT = new EventType<>(ANY_DAO_EVENT, "SCHEDULER_ANY_COUNTRY_EVENT");

    /**
     * Event type for {@link CountryDAO} {@link DbChangeType#CREATED} events.
     */
    public static final EventType<CountryDaoEvent> COUNTRY_DAO_INSERT = new EventType<>(ANY_COUNTRY_EVENT, "SCHEDULER_COUNTRY_DAO_INSERT");

    /**
     * Event type for {@link CountryDAO} {@link DbChangeType#UPDATED} events.
     */
    public static final EventType<CountryDaoEvent> COUNTRY_DAO_UPDATE = new EventType<>(ANY_COUNTRY_EVENT, "SCHEDULER_COUNTRY_DAO_UPDATE");

    /**
     * Event type for {@link CountryDAO} {@link DbChangeType#DELETED} events.
     */
    public static final EventType<CountryDaoEvent> COUNTRY_DAO_DELETE = new EventType<>(ANY_COUNTRY_EVENT, "SCHEDULER_COUNTRY_DAO_DELETE");

    public static EventType<CountryDaoEvent> toEventType(DbChangeType changeAction) {
        switch (changeAction) {
            case CREATED:
                return COUNTRY_DAO_INSERT;
            case DELETED:
                return COUNTRY_DAO_DELETE;
            default:
                return COUNTRY_DAO_UPDATE;
        }
    }

    /**
     * Initializes a new {@link CountryDAO} event.
     *
     * @param source The object which sent the {@code CountryDaoEvent}.
     * @param changeAction The {@link DbChangeType} value indicating the type of change event that occurred.
     * @param dataObject The target {@link CountryDAO}.
     */
    public CountryDaoEvent(Object source, DbChangeType changeAction, CountryDAO dataObject) {
        super(source, dataObject, changeAction, toEventType(changeAction));
    }

    @Override
    @SuppressWarnings("unchecked")
    public EventType<CountryDaoEvent> getEventType() {
        return (EventType<CountryDaoEvent>) super.getEventType();
    }

}
