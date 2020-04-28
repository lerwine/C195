package scheduler.model.db;

import scheduler.model.RelatedRecord;
import scheduler.model.User;

/**
 * Represents a data row from the "user" database table.
 * <dl>
 * <dt>{@link scheduler.dao.UserDAO}</dt><dd>Data access object.</dd>
 * <dt>{@link scheduler.model.ui.UserItem}</dt><dd>UI Model with JavaFX properties.</dd>
 * </dl>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface UserRowData extends User, RelatedRecord {

}
