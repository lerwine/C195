package scheduler.model.ui;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyProperty;
import scheduler.dao.DataRowState;
import scheduler.dao.DbObject;
import scheduler.model.DataObject;
import scheduler.model.DataRecord;

/**
 * A UI {@code DataObject} with bindable JavaFX properties and a backing data access object.
 * <p>
 * Extending types:</p>
 * Abstract {@link DataRecord} implementation:
 * <ul>
 * <li>{@link FxRecordModel}</li>
 * </ul>
 * Entity-specific extensions:
 * <ul>
 * <li>{@link AppointmentItem}</li>
 * <li>{@link CustomerItem}</li>
 * <li>{@link AddressItem}</li>
 * <li>{@link CityItem}</li>
 * <li>{@link CountryItem}</li>
 * </ul>
 *
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> Type of object for database access.
 */
public interface FxDbModel<T extends DbObject> extends DataObject {

    /**
     * Gets the backing data access object.
     *
     * @return The backing data access object.
     */
    T getDataObject();

    /**
     * Gets the {@link ReadOnlyProperty} that contains the backing data access object.
     *
     * @return The {@link ReadOnlyProperty} that contains the backing data access object.
     */
    ReadOnlyObjectProperty<? extends T> dataObjectProperty();

    ReadOnlyIntegerProperty primaryKeyProperty();

    /**
     * Gets a value indicating if all model properties are valid.
     *
     * @return {@code true} if all properties are valid; otherwise, {@code false} if one or more properties are not valid.
     */
    boolean isValid();

    ReadOnlyBooleanProperty validProperty();

    DataRowState getRowState();

    /**
     * Gets the property that contains the {@link DataRowState} value which represents the disposition of the current database entity object.
     *
     * @return The property that contains the {@link DataRowState} value which represents the disposition of the current database entity object.
     */
    ReadOnlyObjectProperty<? extends DataRowState> rowStateProperty();

}
