package scheduler.model.ui;

import scheduler.model.DataModel;
import scheduler.model.db.RowData;

/**
 * Interface for a UI {@link DataModel} with JavaFX properties.
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
public interface UIDbModel<T extends RowData> extends UIModel {
    T getDataObject();
}
