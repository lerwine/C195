package scheduler.model.ui;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyProperty;
import scheduler.dao.DataRowState;
import scheduler.model.DataObject;
import scheduler.dao.DbObject;

/**
 * Interface for UI {@code DbDataObject}s with bindable JavaFX properties.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface FxModel extends DataObject {

    ReadOnlyIntegerProperty primaryKeyProperty();

    /**
     * Gets a value indicating if all model properties are valid.
     *
     * @return {@code true} if all properties are valid; otherwise, {@code false} if one or more properties are not valid.
     */
    boolean isValid();

    ReadOnlyBooleanProperty validProperty();

    /**
     * Gets the backing data access object.
     *
     * @return The backing data access object.
     */
    DbObject getDataObject();

    /**
     * Gets the {@link ReadOnlyProperty} that contains the backing data access object.
     *
     * @return The {@link ReadOnlyProperty} that contains the backing data access object.
     */
    ReadOnlyObjectProperty<? extends DbObject> dataObjectProperty();

    DataRowState getRowState();

    /**
     * Gets the property that contains the {@link DataRowState} value which represents the disposition of the current database entity object.
     *
     * @return The property that contains the {@link DataRowState} value which represents the disposition of the current database entity object.
     */
    ReadOnlyObjectProperty<? extends DataRowState> rowStateProperty();

}
