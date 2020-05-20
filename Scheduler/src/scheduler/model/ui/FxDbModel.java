package scheduler.model.ui;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyProperty;
import scheduler.model.DataRecord;
import scheduler.dao.DbObject;

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
public interface FxDbModel<T extends DbObject> extends FxModel {

    /**
     * Gets the backing data access object.
     *
     * @return The backing data access object.
     */
    @Override
    T getDataObject();

    /**
     * Gets the {@link ReadOnlyProperty} that contains the backing data access object.
     *
     * @return The {@link ReadOnlyProperty} that contains the backing data access object.
     */
    @Override
    ReadOnlyObjectProperty<? extends T> dataObjectProperty();

}
