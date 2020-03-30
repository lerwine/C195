package scheduler.dao;

import java.util.EventObject;
import java.util.Objects;

/**
 *
 * @author lerwi
 * @param <T>
 */
public class DataObjectEvent<T extends DataObjectImpl> extends EventObject {
    private final DaoChangeAction changeAction;
    private final T dataObject;

    public DaoChangeAction getChangeAction() {
        return changeAction;
    }

    public T getDataObject() {
        return dataObject;
    }
    
    public DataObjectEvent(Object source, DaoChangeAction changeAction, T dataObject) {
        super(source);
        this.changeAction = Objects.requireNonNull(changeAction);
        this.dataObject = Objects.requireNonNull(dataObject);
    }
    
}
