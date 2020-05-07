package scheduler.dao.event;

import javafx.event.EventType;
import scheduler.dao.CityDAO;

/**
 * Represents a {@link DaoChangeAction#CREATED}, {@link DaoChangeAction#CREATED} or {@link DaoChangeAction#CREATED} event for a {@link CityDAO}.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class CityDaoEvent extends DataObjectEvent<CityDAO> {

    /**
     * Base event type for all {@link CityDAO} events.
     */
    public static final EventType<CityDaoEvent> ANY_CITY_EVENT = new EventType<>(ANY_DAO_EVENT, "SCHEDULER_ANY_CITY_EVENT");

    /**
     * Event type for {@link CityDAO} {@link DaoChangeAction#CREATED} events.
     */
    public static final EventType<CityDaoEvent> CITY_DAO_INSERT = new EventType<>(ANY_CITY_EVENT, "SCHEDULER_CITY_DAO_INSERT");

    /**
     * Event type for {@link CityDAO} {@link DaoChangeAction#UPDATED} events.
     */
    public static final EventType<CityDaoEvent> CITY_DAO_UPDATE = new EventType<>(ANY_CITY_EVENT, "SCHEDULER_CITY_DAO_UPDATE");

    /**
     * Event type for {@link CityDAO} {@link DaoChangeAction#DELETED} events.
     */
    public static final EventType<CityDaoEvent> CITY_DAO_DELETE = new EventType<>(ANY_CITY_EVENT, "SCHEDULER_CITY_DAO_DELETE");

    public static EventType<CityDaoEvent> toEventType(DaoChangeAction changeAction) {
        switch (changeAction) {
            case CREATED:
                return CITY_DAO_INSERT;
            case DELETED:
                return CITY_DAO_DELETE;
            default:
                return CITY_DAO_UPDATE;
        }
    }

    /**
     * Initializes a new {@link CityDAO} event.
     *
     * @param source The object which sent the {@code CityDaoEvent}.
     * @param changeAction The {@link DaoChangeAction} value indicating the type of change event that occurred.
     * @param dataObject The target {@link CityDAO}.
     */
    public CityDaoEvent(Object source, DaoChangeAction changeAction, CityDAO dataObject) {
        super(source, dataObject, changeAction, toEventType(changeAction));
    }

}
