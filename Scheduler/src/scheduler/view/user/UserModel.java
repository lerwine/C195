package scheduler.view.user;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import scheduler.dao.DataObjectImpl.Factory;
import scheduler.dao.UserImpl;
import scheduler.dao.UserStatus;
import scheduler.observables.UserStatusDisplayProperty;
import scheduler.observables.UserStatusProperty;
import scheduler.view.ItemModel;

/**
 *
 * @author erwinel
 */
public final class UserModel extends ItemModel<UserImpl> implements UserReferenceModel<UserImpl> {

    private final SimpleStringProperty userName;
    private final SimpleStringProperty password;
    private final UserStatusProperty status;
    private final UserStatusDisplayProperty statusDisplay;

    @Override
    public String getUserName() {
        return userName.get();
    }

    public void setUserName(String value) {
        userName.set(value);
    }

    @Override
    public StringProperty userNameProperty() {
        return userName;
    }

    @Override
    public String getPassword() {
        return password.get();
    }

    public void setPassword(String value) {
        password.set(value);
    }

    @Override
    public StringProperty passwordProperty() {
        return password;
    }

    @Override
    public UserStatus getStatus() {
        return status.get();
    }

    public void setStatus(UserStatus value) {
        status.set(value);
    }

    @Override
    public UserStatusProperty statusProperty() {
        return status;
    }

    @Override
    public String getStatusDisplay() {
        return statusDisplay.get();
    }

    @Override
    public UserStatusDisplayProperty statusDisplayProperty() {
        return statusDisplay;
    }

    public UserModel(UserImpl dao) {
        super(dao);
        userName = new ReadOnlyStringWrapper(this, "userName", dao.getUserName());
        password = new ReadOnlyStringWrapper(this, "password", dao.getPassword());
        status = new UserStatusProperty(this, "status", dao.getStatus());
        statusDisplay = new UserStatusDisplayProperty(this, "statusDisplay", status);
    }

    @Override
    protected void refreshFromDAO(UserImpl dao) {
        userName.set(dao.getUserName());
        password.set(dao.getPassword());
        status.set(dao.getStatus());
    }

    @Override
    public Factory<UserImpl, UserModel> getDaoFactory() {
        return UserImpl.getFactory();
    }

}
