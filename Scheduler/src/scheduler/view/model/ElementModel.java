package scheduler.view.model;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyProperty;
import scheduler.dao.DataElement;

/**
 * Interface for object models that reference a {@link DataElement}.
 *
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
 * @param <T> The type of {@link DataElement} being represented.
 */
public interface ElementModel<T extends DataElement> {
    T getDataObject();

    ReadOnlyProperty<T> dataObjectProperty();

    int getPrimaryKey();

    ReadOnlyIntegerProperty primaryKeyProperty();
}
