package scheduler.view.user;

import java.util.Objects;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import scheduler.dao.DataObjectImpl.DaoFactory;
import scheduler.dao.UserImpl;
import scheduler.dao.UserStatus;
import scheduler.observables.UserStatusDisplayProperty;
import scheduler.observables.UserStatusProperty;
import scheduler.view.ItemModel;
import scheduler.view.ModelFilter;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
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

    public UserModel(UserImpl dao) {
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
        if (null != obj && obj instanceof UserModel) {
            final UserModel other = (UserModel) obj;
            if (isNewItem()) {
                return userName.isEqualTo(other.userName).get() && password.isEqualTo(other.password).get() && status.isEqualTo(other.status).get();
            }
            return !other.isNewItem() && primaryKeyProperty().isEqualTo(other.primaryKeyProperty()).get();
        }
        return false;
    }
    
    public final static class Factory extends ItemModel.ModelFactory<UserImpl, UserModel> {

        private Factory() { }
        
        @Override
        public DaoFactory<UserImpl> getDaoFactory() {
            return UserImpl.getFactory();
        }

        @Override
        public UserModel createNew(UserImpl dao) {
            return new UserModel(dao);
        }

        @Override
        protected void updateItem(UserModel item, UserImpl dao) {
            super.updateItem(item, dao);
            // TODO: Implement this
        }

        @Override
        public ModelFilter<UserImpl, UserModel> getAllItemsFilter() {
            throw new UnsupportedOperationException("Not supported yet.");
            // TODO: Implement this
        }

        @Override
        public ModelFilter<UserImpl, UserModel> getDefaultFilter() {
            throw new UnsupportedOperationException("Not supported yet.");
            // TODO: Implement this
        }

        @Override
        public UserImpl applyChanges(UserModel item) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

}
