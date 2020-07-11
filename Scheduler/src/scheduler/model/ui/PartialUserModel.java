package scheduler.model.ui;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import scheduler.dao.PartialUserDAO;
import scheduler.dao.UserDAO;
import scheduler.model.User;
import scheduler.model.UserStatus;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> Type of object for database access.
 */
public interface PartialUserModel<T extends PartialUserDAO> extends User, PartialEntityModel<T> {

    /**
     * The name of the 'statusDisplay' property.
     */
    public static final String PROP_STATUSDISPLAY = "statusDisplay";

    public static PartialUserModel<? extends PartialUserDAO> createModel(PartialUserDAO t) {
        if (null == t) {
            return null;
        }
        if (t instanceof UserDAO) {
            return UserModel.FACTORY.createNew((UserDAO) t);
        }

        return new PartialUserModelImpl(t);
    }

    ReadOnlyStringProperty userNameProperty();

    ReadOnlyObjectProperty<UserStatus> statusProperty();

    String getStatusDisplay();

    ReadOnlyStringProperty statusDisplayProperty();
}
