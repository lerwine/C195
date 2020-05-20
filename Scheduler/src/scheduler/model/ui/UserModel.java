package scheduler.model.ui;

import java.util.Objects;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import scheduler.dao.DataRowState;
import scheduler.dao.DataAccessObject.DaoFactory;
import scheduler.dao.UserDAO;
import scheduler.model.UserStatus;
import scheduler.observables.CalculatedBooleanProperty;
import scheduler.observables.ObservableTriplet;
import scheduler.observables.UserStatusProperty;
import scheduler.util.Triplet;
import scheduler.view.user.UserModelFilter;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public final class UserModel extends FxRecordModel<UserDAO> implements UserItem<UserDAO> {

    private static final Factory FACTORY = new Factory();

    public static final Factory getFactory() {
        return FACTORY;
    }

    private final SimpleStringProperty userName;
    private final SimpleStringProperty password;
    private final UserStatusProperty status;
    private final CalculatedBooleanProperty<Triplet<String, String, UserStatus>> valid;

    public UserModel(UserDAO dao) {
        super(dao);
        userName = new ReadOnlyStringWrapper(this, "userName", dao.getUserName());
        password = new SimpleStringProperty(this, "password", dao.getPassword());
        status = new UserStatusProperty(this, "status", dao.getStatus());
        valid = new CalculatedBooleanProperty<>(this, "valid", new ObservableTriplet<>(userName, password, status), (t) -> {
            String s = t.getValue1();
            if (null != s && !s.trim().isEmpty()) {
                s = t.getValue2();
                if (null != s && !s.trim().isEmpty()) {
                    return null != t.getValue2();
                }
            }
            return false;
        });
    }

    @Override
    protected void onDaoPropertyChanged(UserDAO dao, String propertyName) {
        switch (propertyName) {
            case UserDAO.PROP_PASSWORD:
                password.set(dao.getPassword());
                break;
            case UserDAO.PROP_STATUS:
                status.set(dao.getStatus());
                break;
            case UserDAO.PROP_USERNAME:
                userName.set(dao.getUserName());
                break;
        }
    }

    @Override
    protected void onDataObjectChanged(UserDAO dao) {
        password.set(dao.getPassword());
        setUserName(dao.getUserName());
        setStatus(dao.getStatus());
    }

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
        return status.getDisplayText();
    }

    @Override
    public ReadOnlyStringProperty statusDisplayProperty() {
        return status.displayTextProperty();
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

    @Override
    public boolean isValid() {
        return valid.get();
    }

    @Override
    public ReadOnlyBooleanProperty validProperty() {
        return valid.getReadOnlyBooleanProperty();
    }

    public final static class Factory extends FxRecordModel.ModelFactory<UserDAO, UserModel> {

        // Singleton
        private Factory() {
            if (null != FACTORY) {
                throw new IllegalStateException();
            }
        }

        @Override
        public DaoFactory<UserDAO> getDaoFactory() {
            return UserDAO.getFactory();
        }

        @Override
        public UserModel createNew(UserDAO dao) {
            return new UserModel(dao);
        }

        @Override
        public UserModelFilter getAllItemsFilter() {
            return UserModelFilter.all();
        }

        @Override
        public UserModelFilter getDefaultFilter() {
            return UserModelFilter.active();
        }

        @Override
        public UserDAO updateDAO(UserModel item) {
            UserDAO dao = item.getDataObject();
            if (dao.getRowState() == DataRowState.DELETED) {
                throw new IllegalArgumentException("User has been deleted");
            }
            dao.setUserName(item.userName.get());
            dao.setPassword(item.password.get());
            dao.setStatus(item.getStatus());
            return dao;
        }

    }

}