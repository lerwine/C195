package scheduler.view;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyProperty;
import scheduler.dao.DataObject;

/**
 *
 * @author lerwi
 * @param <T>
 */
public interface DataObjectReferenceModel<T extends DataObject> {

    T getDataObject();

    ReadOnlyProperty<T> dataObjectProperty();

    int getPrimaryKey();

    ReadOnlyIntegerProperty primaryKeyProperty();
}
