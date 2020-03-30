package scheduler.dao;

/**
 * Interface for handling {@link DataObjectEvent}s.
 * 
 * @author lerwi
 * @param <T>
 */
@FunctionalInterface
public interface DataObjectEventListener<T extends DataObjectImpl> {
    /**
     * Handles a {@link DataObjectEvent}.
     * Do not use the {@link scheduler.view.annotations.HandlesDataObjectEvent} annotation on implementing methods or else
     * the method may be called twice.
     * 
     * @param event The target {@link DataObjectEvent}.
     */
    public void onDataObjectEvent(DataObjectEvent<T> event);
}
