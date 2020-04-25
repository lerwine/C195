package scheduler.model;

/**
 * Base interface for objects that contain either partial or complete information from database entities.
 * <p>
 * Extending types:</p>
 * <dl>
 * <dt>{@link DataRecord}</dt><dd>{@code DataModel} with all data from a database entity.</dd>
 * <dt>{@link DbDataModel}</dt><dd>{@code DataModel} with a primary key property.</dd>
 * <dt>{@link scheduler.model.db.RowData}</dt><dd>{@code DataModel} for data access objects.</dd>
 * <dt>{@link scheduler.model.ui.UIModel}</dt><dd>UI {@code DataModel} with JavaFX properties.</dd>
 * </dl>
 * Entity-specific extensions:
 * <ul>
 * <li>{@link Appointment}</li>
 * <li>{@link Customer}</li>
 * <li>{@link Address}</li>
 * <li>{@link City}</li>
 * <li>{@link Country}</li>
 * </ul>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface DataModel {

}
