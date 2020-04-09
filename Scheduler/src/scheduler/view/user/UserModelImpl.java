package scheduler.view.user;

import java.util.Objects;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import scheduler.dao.DataAccessObject.DaoFactory;
import scheduler.dao.DataRowState;
import scheduler.dao.UserDAO;
import scheduler.dao.UserStatus;
import scheduler.observables.UserStatusDisplayProperty;
import scheduler.observables.UserStatusProperty;
import scheduler.view.model.ItemModel;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
 */
public final class UserModelImpl extends ItemModel<UserDAO> implements UserModel<UserDAO> {

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

    public String getPassword() {
        return password.get();
    }

    public void setPassword(String value) {
        password.set(value);
    }

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

    public UserModelImpl(UserDAO dao) {
        super(dao);
        userName = new ReadOnlyStringWrapper(this, "userName", dao.getUserName());
        password = new ReadOnlyStringWrapper(this, "password", dao.getPassword());
        status = new UserStatusProperty(this, "status", dao.getStatus());
        statusDisplay = new UserStatusDisplayProperty(this, "statusDisplay", status);
    }

    private static final Factory FACTORY = new Factory();

    public static final Factory getFactory() {
        return FACTORY;
    }

    @Override
    public int hashCode() {
        if (isNewItem()) {
            int hash = 7;
            hash = 43 * hash + Objects.hashCode(userName.get());
            hash = 43 * hash + Objects.hashCode(password.get());
            hash = 43 * hash + Objects.hashCode(status.get());
            return hash;
        }
        return getPrimaryKey();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (null != obj && obj instanceof UserModelImpl) {
            final UserModelImpl other = (UserModelImpl) obj;
            if (isNewItem()) {
                return userName.isEqualTo(other.userName).get() && password.isEqualTo(other.password).get() && status.isEqualTo(other.status).get();
            }
            return !other.isNewItem() && primaryKeyProperty().isEqualTo(other.primaryKeyProperty()).get();
        }
        return false;
    }
    
    public final static class Factory extends ItemModel.ModelFactory<UserDAO, UserModelImpl> {

        private Factory() { }
        
        @Override
        public DaoFactory<UserDAO> getDaoFactory() {
            return UserDAO.getFactory();
        }

        @Override
        public UserModelImpl createNew(UserDAO dao) {
            return new UserModelImpl(dao);
        }

        @Override
        public void updateItem(UserModelImpl item, UserDAO dao) {
            super.updateItem(item, dao);
            item.setPassword(dao.getPassword());
            item.setUserName(dao.getUserName());
            item.setStatus(dao.getStatus());
        }

        public UserModelFilter getAllItemsFilter() {
            return UserModelFilter.all();
        }

        @Override
        public UserDAO updateDAO(UserModelImpl item) {
            UserDAO dao = item.getDataObject();
            if (dao.getRowState() == DataRowState.DELETED)
                throw new IllegalArgumentException("User has been deleted");
            String name = item.userName.get();
            if (name.trim().isEmpty())
                throw new IllegalArgumentException("User name is empty");
            String pwd = item.password.get();
            if (name.trim().isEmpty())
                throw new IllegalArgumentException("Password is empty");
            dao.setUserName(name);
            dao.setPassword(pwd);
            dao.setStatus(item.getStatus());
            return dao;
        }

    }

}
