package scheduler.model;

/**
 * Base interface for all objects that represent a database entity.
 * <p>
 * Sub-interface overview</p>
 * <dl>
 * <dt>{@link RelatedRecord}</dt><dd>{@code DataModel}s containing informational properties of database entities joined by a foreign key
 * relationship.</dd>
 * <dt>{@link scheduler.model.ui.UIModel}</dt><dd>UI {@code DataModel}s with bindable JavaFX properties and a backing {@link DataRecord} or
 * {@link RelatedRecord} object.</dd>
 * <dt>Entity-specific interfaces</dt><dd>{@link Appointment}, {@link Customer}, {@link Address}, {@link City}, and {@link Country}</dd>
 * </dl>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface DataModel {

}
