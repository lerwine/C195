package scheduler.model.ui;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyProperty;
import scheduler.model.User;
import scheduler.model.UserStatus;
import scheduler.model.db.UserRowData;
import scheduler.observables.UserStatusDisplayProperty;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> Type of object for database access.
 */
public interface UserItem<T extends UserRowData> extends User, UIDbModel<T> {
    
    ReadOnlyProperty<String> userNameProperty();

    ReadOnlyObjectProperty<UserStatus> statusProperty();

    String getStatusDisplay();

    UserStatusDisplayProperty statusDisplayProperty();
}
