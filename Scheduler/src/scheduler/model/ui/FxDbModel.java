package scheduler.model.ui;

import javafx.beans.property.ReadOnlyProperty;
import scheduler.dao.DataRowState;
import scheduler.model.RelatedRecord;

/**
 * A UI {@code DataModel} with bindable JavaFX properties and a backing {@link DataRecord} or {@link RelatedRecord} object.
 * <p>
 * Extending types:</p>
 * Abstract {@link DataRecord} implementation:
 * <ul>
 * <li>{@link scheduler.view.model.ItemModel}</li>
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
public interface FxDbModel<T extends RelatedRecord> extends FxModel {

    /**
     * Gets the backing {@link RelatedRecord} data access object.
     *
     * @return The backing {@link RelatedRecord} data access object.
     */
    T getDataObject();

    /**
     * Gets the {@link ReadOnlyProperty} that contains the backing {@link RelatedRecord} data access object.
     *
     * @return The {@link ReadOnlyProperty} that contains the backing {@link RelatedRecord} data access object.
     */
    ReadOnlyProperty<? extends T> dataObjectProperty();

    DataRowState getRowState();

    /**
     * Gets the property that contains the {@link DataRowState} value which represents the disposition of the current database entity object.
     *
     * @return The property that contains the {@link DataRowState} value which represents the disposition of the current database entity object.
     */
    ReadOnlyProperty<? extends DataRowState> rowStateProperty();

}
