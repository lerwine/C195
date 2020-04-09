package scheduler.view.user;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import scheduler.dao.UserStatus;
import scheduler.observables.UserStatusDisplayProperty;
import scheduler.observables.UserStatusProperty;
import scheduler.view.model.RelatedItemModel;
import scheduler.dao.UserElement;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
 */
public class RelatedUserModel extends RelatedItemModel<UserElement> implements UserModel<UserElement> {

    private final ReadOnlyStringWrapper userName;
    private final UserStatusProperty status;
    private final UserStatusDisplayProperty statusDisplay;

    public RelatedUserModel(UserElement dao) {
        super(dao);
        userName = new ReadOnlyStringWrapper(this, "userName", dao.getUserName());
        status = new UserStatusProperty(this, "status", dao.getStatus());
        statusDisplay = new UserStatusDisplayProperty(this, "statusDisplay", status);
    }

    @Override
    public String getUserName() {
        return userName.get();
    }

    @Override
    public ReadOnlyProperty<String> userNameProperty() {
        return userName.getReadOnlyProperty();
    }

    @Override
    public UserStatus getStatus() {
        return status.get();
    }

    @Override
    public ReadOnlyObjectProperty<UserStatus> statusProperty() {
        return status.getReadOnlyProperty();
    }

    @Override
    public String getStatusDisplay() {
        return statusDisplay.get();
    }

    @Override
    public UserStatusDisplayProperty statusDisplayProperty() {
        return statusDisplay;
    }

}
