package scheduler.view.event;

/**
 * Interface for handling {@link DataLoadedEvent}s.
 * 
 * @author Leonard T. Erwine (Student ID 356334)
 * @param <T> The type of data loaded by the base controller.
 */
@FunctionalInterface
public interface DataLoadedEventListener<T> {
    
    /**
     * Handles a {@link FxmlViewControllerEvent}.
     * <p>
     * Do not use the {@link scheduler.view.annotations.HandlesDataLoaded} annotation on classes that implement this method or else the method may
     * be called twice.</p>
     * 
     * @param event The {@link DataLoadedEvent} that occurred.
     */
    void onDataLoaded(DataLoadedEvent event);
}
