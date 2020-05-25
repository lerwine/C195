package scheduler.dao.event;

import javafx.event.EventType;
import scheduler.dao.AppointmentDAO;

/**
 * Represents a {@link DbChangeType#CREATED}, {@link DbChangeType#CREATED} or {@link DbChangeType#CREATED} event for a {@link AppointmentDAO}.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class AppointmentDaoEvent extends DataObjectEvent<AppointmentDAO> {

    private static final long serialVersionUID = -275981984109138306L;

    /**
     * Base event type for all {@link AppointmentDAO} events.
     */
    public static final EventType<AppointmentDaoEvent> ANY_APPOINTMENT_EVENT = new EventType<>(ANY_DAO_EVENT, "SCHEDULER_ANY_APPOINTMENT_EVENT");

    /**
     * Event type for {@link AppointmentDAO} {@link DbChangeType#CREATED} events.
     */
    public static final EventType<AppointmentDaoEvent> APPOINTMENT_DAO_INSERT = new EventType<>(ANY_APPOINTMENT_EVENT, "SCHEDULER_APPOINTMENT_DAO_INSERT");

    /**
     * Event type for {@link AppointmentDAO} {@link DbChangeType#UPDATED} events.
     */
    public static final EventType<AppointmentDaoEvent> APPOINTMENT_DAO_UPDATE = new EventType<>(ANY_APPOINTMENT_EVENT, "SCHEDULER_APPOINTMENT_DAO_UPDATE");

    /**
     * Event type for {@link AppointmentDAO} {@link DbChangeType#DELETED} events.
     */
    public static final EventType<AppointmentDaoEvent> APPOINTMENT_DAO_DELETE = new EventType<>(ANY_APPOINTMENT_EVENT, "SCHEDULER_APPOINTMENT_DAO_DELETE");

    public static EventType<AppointmentDaoEvent> toEventType(DbChangeType changeAction) {
        switch (changeAction) {
            case CREATED:
                return APPOINTMENT_DAO_INSERT;
            case DELETED:
                return APPOINTMENT_DAO_DELETE;
            default:
                return APPOINTMENT_DAO_UPDATE;
        }
    }

    /**
     * Initializes a new {@link AppointmentDAO} event.
     *
     * @param source The object which sent the {@code AppointmentDaoEvent}.
     * @param dataObject The {@link AppointmentDAO} that changed.
     * @param changeAction The {@link DbChangeType} value indicating the type of change event that occurred.
     */
    public AppointmentDaoEvent(Object source, AppointmentDAO dataObject, DbChangeType changeAction) {
        super(source, dataObject, changeAction, toEventType(changeAction));
    }

    @Override
    @SuppressWarnings("unchecked")
    public EventType<AppointmentDaoEvent> getEventType() {
        return (EventType<AppointmentDaoEvent>) super.getEventType();
    }

}
