package scheduler.model.ui;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import scheduler.dao.IUserDAO;
import scheduler.dao.UserDAO;
import scheduler.model.User;
import scheduler.model.UserStatus;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> Type of object for database access.
 */
public interface UserItem<T extends IUserDAO> extends User, FxDbModel<T> {

    public static UserItem<? extends IUserDAO> createModel(IUserDAO t) {
        if (null == t) {
            return null;
        }
        if (t instanceof UserDAO) {
            return new UserModel((UserDAO) t);
        }

        return new RelatedUser(t);
    }

    ReadOnlyStringProperty userNameProperty();

    ReadOnlyObjectProperty<UserStatus> statusProperty();

    String getStatusDisplay();

    ReadOnlyStringProperty statusDisplayProperty();
}
