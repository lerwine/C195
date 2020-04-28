package scheduler.model.ui;

import javafx.beans.property.ReadOnlyIntegerProperty;
import scheduler.model.RelatedRecord;

/**
 * Interface for UI {@code DbDataModel}s with bindable JavaFX properties.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface FxModel extends RelatedRecord {

    ReadOnlyIntegerProperty primaryKeyProperty();

}
