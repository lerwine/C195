package scheduler.view.event;

import java.util.EventObject;

/**
 * Represents an event that indicates all initialization data has been loaded and the base controller is finished.
 * 
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> The type of data loaded by the base controller.
 */
public class DataLoadedEvent<T> extends EventObject {

    /**
     *
     */
    private static final long serialVersionUID = 6942714826626847942L;
    private final T data;
    
    /**
     * Creates a new {@code DataLoadedEvent} object.
     * 
     * @param source The source of the event.
     * @param data The data loaded by the base controller.
     */
    public DataLoadedEvent(Object source, T data) {
        super(source);
        this.data = data;
    }

    /**
     * Gets the data loaded by the base controller.
     * 
     * @return The data loaded by the base controller.
     */
    public T getData() {
        return data;
    }
    
}
