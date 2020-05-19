package scheduler.model.ui;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import scheduler.dao.IUserDAO;
import scheduler.model.User;
import scheduler.model.UserStatus;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> Type of object for database access.
 */
public interface UserItem<T extends IUserDAO> extends User, FxDbModel<T> {

    ReadOnlyStringProperty userNameProperty();

    ReadOnlyObjectProperty<UserStatus> statusProperty();

    String getStatusDisplay();

    ReadOnlyStringProperty statusDisplayProperty();
}
