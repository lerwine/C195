package scheduler.dao.event;

import java.util.EventObject;
import java.util.Objects;
import scheduler.dao.DataAccessObject;

/**
 * Represents a {@link DaoChangeAction#CREATED}, {@link DaoChangeAction#CREATED} or {@link DaoChangeAction#CREATED} event for a {@link DataAccessObject}.
 * 
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> The type of {@link DataAccessObject} affected.
 */
public class DataObjectEvent<T extends DataAccessObject> extends EventObject {
    private static final long serialVersionUID = -4153967201114554143L;
    private final DaoChangeAction changeAction;
    private final T dataObject;

    /**
     * Gets the type of change event that occurred.
     * 
     * @return A {@link DaoChangeAction} value indicating the type of change event that occurred.
     */
    public DaoChangeAction getChangeAction() {
        return changeAction;
    }

    /**
     * Gets the {@link DataAccessObject} that was affected.
     * 
     * @return The {@link DataAccessObject} instance that was affected.
     */
    public T getDataObject() {
        return dataObject;
    }
    
    /**
     * Initializes a new data access object event.
     * 
     * @param source The object on which the {@code DataObjectEvent} initially occurred.
     * @param changeAction The {@link DaoChangeAction} value indicating the type of change event that occurred.
     * @param dataObject The {@link DataAccessObject} instance that was affected.
     */
    public DataObjectEvent(Object source, DaoChangeAction changeAction, T dataObject) {
        super(source);
        this.changeAction = Objects.requireNonNull(changeAction);
        this.dataObject = Objects.requireNonNull(dataObject);
    }
    
}
