package view;

import scheduler.dao.DataObject;

/**
 * Java FX object model for a child item in a foreign key relationship.
 * @author erwinel
 * @param <R> The type of object that is used for data operations.
 */
public interface ChildModel<R extends DataObject> {

    /**
     * Gets the {@link DataObjectImpl} to be used for data access operations.
     * @return The {@link DataObjectImpl} to be used for data access operations.
     */
    R getDataObject();
    
}
