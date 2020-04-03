package scheduler.dao.event;

import scheduler.dao.DataAccessObject;

/**
 * Interface for handling {@link DataObjectEvent}s.
 * 
 * @author Leonard T. Erwine (Student ID 356334)
 * @param <T>
 */
@FunctionalInterface
public interface DataObjectEventListener<T extends DataAccessObject> {
    /**
     * Handles a {@link DataObjectEvent}.
     * Do not use the {@link scheduler.view.annotations.HandlesDataObjectEvent} annotation on implementing methods or else
     * the method may be called twice.
     * 
     * @param event The target {@link DataObjectEvent}.
     */
    public void onDataObjectEvent(DataObjectEvent<T> event);
}
