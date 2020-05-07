package scheduler.dao.event;

import javafx.event.EventType;
import scheduler.dao.DataAccessObject;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public enum DaoChangeAction {
    CREATED(DataObjectEvent.ANY_DAO_INSERT),
    UPDATED(DataObjectEvent.ANY_DAO_UPDATE),
    DELETED(DataObjectEvent.ANY_DAO_DELETE);
    
    private final EventType<? extends DataObjectEvent<? extends DataAccessObject>> eventType;

    public EventType<? extends DataObjectEvent<? extends DataAccessObject>> getEventType() {
        return eventType;
    }
    
    private DaoChangeAction(EventType<? extends DataObjectEvent<? extends DataAccessObject>> eventType) {
        this.eventType = eventType;
    }
}
