package scheduler.model.ui;

import java.util.Objects;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventTarget;
import scheduler.dao.DataAccessObject.DaoFactory;
import scheduler.dao.DataRowState;
import scheduler.dao.UserDAO;
import scheduler.model.UserStatus;
import scheduler.observables.property.ReadOnlyBooleanBindingProperty;
import scheduler.observables.property.ReadOnlyStringBindingProperty;
import scheduler.util.PwHash;
import scheduler.util.Values;
import scheduler.view.event.ModelItemEvent;
import scheduler.view.event.UserEvent;
import scheduler.view.user.UserModelFilter;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public final class UserModel extends FxRecordModel<UserDAO> implements UserItem<UserDAO> {

    public static final Factory FACTORY = new Factory();

    public static final Factory getFactory() {
        return FACTORY;
    }

    private final SimpleStringProperty userName;
    private final SimpleStringProperty password;
    private final SimpleObjectProperty<UserStatus> status;
    private final ReadOnlyStringBindingProperty statusDisplay;
    private final ReadOnlyBooleanBindingProperty valid;

    public UserModel(UserDAO dao) {
        super(dao);
        userName = new ReadOnlyStringWrapper(this, PROP_USERNAME, dao.getUserName());
        password = new SimpleStringProperty(this, PROP_PASSWORD, "");
        status = new SimpleObjectProperty<>(this, PROP_STATUS, dao.getStatus());
        statusDisplay = new ReadOnlyStringBindingProperty(this, PROP_STATUSDISPLAY, () -> UserStatus.toDisplayValue(status.get()), status);
        valid = new ReadOnlyBooleanBindingProperty(this, PROP_VALID,
                Bindings.createBooleanBinding(() -> Values.isNotNullWhiteSpaceOrEmpty(userName.get()), userName).and(status.isNull()));
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
    public ObjectProperty<UserStatus> statusProperty() {
        return status;
    }

    @Override
    public String getStatusDisplay() {
        return statusDisplay.get();
    }

    @Override
    public ReadOnlyStringProperty statusDisplayProperty() {
        return statusDisplay;
    }

    @Override
    public int hashCode() {
        if (isNewRow()) {
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
            if (isNewRow()) {
                return userName.isEqualTo(other.userName).get() && password.isEqualTo(other.password).get() && status.isEqualTo(other.status).get();
            }
            return !other.isNewRow() && primaryKeyProperty().isEqualTo(other.primaryKeyProperty()).get();
        }
        return false;
    }

    @Override
    public boolean isValid() {
        return valid.get();
    }

    @Override
    public ReadOnlyBooleanProperty validProperty() {
        return valid;
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
            return UserDAO.FACTORY;
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
            UserDAO dao = item.dataObject();
            if (dao.getRowState() == DataRowState.DELETED) {
                throw new IllegalArgumentException("User has been deleted");
            }
            dao.setUserName(item.userName.get());
            String pw = item.password.get();
            if (!pw.isEmpty()) {
                PwHash h = new PwHash(pw, true);
                dao.setPassword(h.getEncodedHash());
            }
            dao.setStatus(item.getStatus());
            return dao;
        }

        @Override
        protected void updateItemProperties(UserModel item, UserDAO dao) {
            item.setUserName(dao.getUserName());
            item.setPassword("");
            item.setStatus(dao.getStatus());
        }

        @Override
        public UserEvent createInsertEvent(UserModel model, Object source, EventTarget target) {
            return new UserEvent(model, source, target, UserEvent.USER_INSERTING_EVENT);
        }

        @Override
        public UserEvent createUpdateEvent(UserModel model, Object source, EventTarget target) {
            return new UserEvent(model, source, target, UserEvent.USER_UPDATING_EVENT);
        }

        @Override
        public UserEvent createDeleteEvent(UserModel model, Object source, EventTarget target) {
            return new UserEvent(model, source, target, UserEvent.USER_DELETING_EVENT);
        }

        @Override
        public ModelItemEvent<UserModel, UserDAO> createEditRequestEvent(UserModel model, Object source, EventTarget target) {
            return new UserEvent(model, source, target, UserEvent.USER_EDIT_REQUEST_EVENT);
        }

        @Override
        public ModelItemEvent<UserModel, UserDAO> createDeleteRequestEvent(UserModel model, Object source, EventTarget target) {
            return new UserEvent(model, source, target, UserEvent.USER_DELETE_REQUEST_EVENT);
        }

    }

}
