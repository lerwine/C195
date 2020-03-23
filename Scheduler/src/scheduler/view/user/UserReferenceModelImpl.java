package scheduler.view.user;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import scheduler.dao.DataObjectImpl;
import scheduler.dao.User;
import scheduler.dao.UserStatus;
import scheduler.observables.UserStatusDisplayProperty;
import scheduler.observables.UserStatusProperty;

/**
 *
 * @author lerwi
 */
public class UserReferenceModelImpl extends DataObjectImpl.DataObjectReferenceModelImpl<User> implements UserReferenceModel<User> {

    private final ReadOnlyStringWrapper userName;
    private final ReadOnlyStringWrapper password;
    private final UserStatusProperty status;
    private final UserStatusDisplayProperty statusDisplay;

    public UserReferenceModelImpl(User dao) {
        super(dao);
        userName = new ReadOnlyStringWrapper(this, "userName", dao.getUserName());
        password = new ReadOnlyStringWrapper(this, "password", dao.getPassword());
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
    public String getPassword() {
        return password.get();
    }

    @Override
    public ReadOnlyProperty<String> passwordProperty() {
        return password.getReadOnlyProperty();
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
