package scheduler.view.annotations;

import scheduler.dao.event.DaoChangeAction;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
 */
public enum DaoChangeType {
    /**
     * Handles any {@link scheduler.dao.event.DataObjectEvent};
     */
    ANY(null),
    
    /**
     * Handles {@link scheduler.dao.event.DataObjectEvent}s where {@link scheduler.dao.event.DataObjectEvent#changeAction} is
     * {@link DaoChangeAction#CREATED}.
     */
    CREATED(DaoChangeAction.CREATED),
    
    /**
     * Handles {@link scheduler.dao.event.DataObjectEvent}s where {@link scheduler.dao.event.DataObjectEvent#changeAction} is
     * {@link DaoChangeAction#DELETED}.
     */
    DELETED(DaoChangeAction.DELETED),
    
    /**
     * Handles {@link scheduler.dao.event.DataObjectEvent}s where {@link scheduler.dao.event.DataObjectEvent#changeAction} is
     * {@link DaoChangeAction#UPDATED}.
     */
    UPDATED(DaoChangeAction.UPDATED);
    
    private final DaoChangeAction changeAction;

    /**
     * The {@link DaoChangeAction} for the handling method.
     * 
     * @return The {@link scheduler.dao.event.DataObjectEvent#changeAction} that the handling method is limited to or {@code null} if it handles all
     * events.
     */
    public DaoChangeAction getChangeAction() {
        return changeAction;
    }
    
    private DaoChangeType(DaoChangeAction changeAction) {
        this.changeAction = changeAction;
    }
}
