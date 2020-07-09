package scheduler.model.ui;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
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
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> Type of object for database access.
 */
public interface FxDbModel<T extends DbObject> extends DataObject {

    /**
     * The name of the 'valid' property.
     */
    public static final String PROP_VALID = "valid";

    /**
     * Gets the backing data access object.
     *
     * @return The backing data access object.
     */
    T dataObject();

    ReadOnlyIntegerProperty primaryKeyProperty();

    @Override
    DataRowState getRowState();

    /**
     * Gets the property that contains the {@link DataRowState} value which represents the disposition of the current database entity object.
     *
     * @return The property that contains the {@link DataRowState} value which represents the disposition of the current database entity object.
     */
    ReadOnlyObjectProperty<? extends DataRowState> rowStateProperty();

}
