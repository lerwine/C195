package scheduler.view.annotations;

import scheduler.dao.DaoChangeAction;

/**
 *
 * @author lerwi
 */
public enum DaoChangeType {
    /**
     * Handles any {@link scheduler.dao.DataObjectEvent};
     */
    ANY(null),
    
    /**
     * Handles {@link scheduler.dao.DataObjectEvent}s where {@link scheduler.dao.DataObjectEvent#changeAction} is
     * {@link DaoChangeAction.CREATED}.
     */
    CREATED(DaoChangeAction.CREATED),
    
    /**
     * Handles {@link scheduler.dao.DataObjectEvent}s where {@link scheduler.dao.DataObjectEvent#changeAction} is
     * {@link DaoChangeAction.DELETED}.
     */
    DELETED(DaoChangeAction.DELETED),
    
    /**
     * Handles {@link scheduler.dao.DataObjectEvent}s where {@link scheduler.dao.DataObjectEvent#changeAction} is
     * {@link DaoChangeAction.UPDATED}.
     */
    UPDATED(DaoChangeAction.UPDATED);
    
    private final DaoChangeAction changeAction;

    /**
     * The {@link DaoChangeAction} for the handling method.
     * 
     * @return The {@link scheduler.dao.DataObjectEvent#changeAction} that the handling method is limited to or {@code null} if it handles all events.
     */
    public DaoChangeAction getChangeAction() {
        return changeAction;
    }
    
    private DaoChangeType(DaoChangeAction changeAction) {
        this.changeAction = changeAction;
    }
}
