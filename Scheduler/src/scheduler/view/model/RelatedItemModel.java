package scheduler.view.model;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import scheduler.dao.DataElement;

/**
 * A model that is the related child item of another.
 * 
 * @author Leonard T. Erwine (Student ID 356334)
 * @param <T> The type of related {@link DataElement}.
 */
public abstract class RelatedItemModel<T extends DataElement> implements ElementModel<T> {
    private final ReadOnlyObjectWrapper<T> dataObject;
    private final ReadOnlyIntegerWrapper primaryKey;

    @Override
    public T getDataObject() {
        return dataObject.get();
    }

    @Override
    public ReadOnlyObjectProperty<T> dataObjectProperty() {
        return dataObject.getReadOnlyProperty();
    }

    @Override
    public int getPrimaryKey() {
        return primaryKey.get();
    }

    @Override
    public ReadOnlyIntegerProperty primaryKeyProperty() {
        return primaryKey.getReadOnlyProperty();
    }

    protected RelatedItemModel(T dao) {
        primaryKey = new ReadOnlyIntegerWrapper(this, "primaryKey", dao.getPrimaryKey());
        dataObject = new ReadOnlyObjectWrapper<>(this, "dataObject", dao);
    }

}
