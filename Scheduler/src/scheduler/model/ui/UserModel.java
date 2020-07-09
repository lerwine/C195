package scheduler.model.ui;

import java.time.LocalDateTime;
import java.util.Objects;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventType;
import scheduler.dao.DataAccessObject;
import scheduler.dao.DataRowState;
import scheduler.dao.UserDAO;
import scheduler.events.UserEvent;
import scheduler.events.UserOpRequestEvent;
import static scheduler.model.User.MAX_LENGTH_PASSWORD;
import static scheduler.model.User.MAX_LENGTH_USERNAME;
import scheduler.model.UserEntity;
import scheduler.model.UserStatus;
import scheduler.observables.property.ReadOnlyStringBindingProperty;
import scheduler.util.ToStringPropertyBuilder;
import scheduler.view.user.UserModelFilter;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public final class UserModel extends EntityModelImpl<UserDAO> implements PartialUserModel<UserDAO>, UserEntity<LocalDateTime> {

    public static final Factory FACTORY = new Factory();

    private final SimpleStringProperty userName;
    private final SimpleStringProperty password;
    private final SimpleObjectProperty<UserStatus> status;
    private final ReadOnlyStringBindingProperty statusDisplay;

    public UserModel(UserDAO dao) {
        super(dao);
        userName = new ReadOnlyStringWrapper(this, PROP_USERNAME, dao.getUserName());
        password = new SimpleStringProperty(this, PROP_PASSWORD, "");
        status = new SimpleObjectProperty<>(this, PROP_STATUS, dao.getStatus());
        statusDisplay = new ReadOnlyStringBindingProperty(this, PROP_STATUSDISPLAY, () -> UserStatus.toDisplayValue(status.get()), status);
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
        if (null != obj && obj instanceof PartialUserModel) {
            final PartialUserModel<? extends UserDAO> other = (UserModel) obj;
            if (isNewRow()) {
                if (other.getRowState() == DataRowState.NEW && userName.isEqualTo(other.userNameProperty()).get()
                        && status.isEqualTo(other.statusProperty()).get()) {
                    if (other instanceof UserModel) {
                        return password.isEqualTo(((UserModel) other).passwordProperty()).get();
                    }
                    return true;
                }
            } else {
                return other.getRowState() != DataRowState.NEW && primaryKeyProperty().isEqualTo(other.primaryKeyProperty()).get();
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return toStringBuilder().build();
    }

    @Override
    public ToStringPropertyBuilder toStringBuilder() {
        ToStringPropertyBuilder builder = ToStringPropertyBuilder.create(this);
        if (getRowState() != DataRowState.NEW) {
            builder.addNumber(primaryKeyProperty());
        }
        return builder.addEnum(PROP_ROWSTATE, getRowState())
                .addString(userName)
                .addString(password)
                .addEnum(status)
                .addLocalDateTime(createDateProperty())
                .addString(createdByProperty())
                .addLocalDateTime(lastModifiedDateProperty())
                .addString(lastModifiedByProperty());
    }

    public final static class Factory extends EntityModelImpl.FxModelFactory<UserDAO, UserModel, UserEvent> {

        // Singleton
        private Factory() {
            super();
            if (null != FACTORY) {
                throw new IllegalStateException();
            }
        }

        @Override
        public DataAccessObject.DaoFactory<UserDAO, UserEvent> getDaoFactory() {
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
        public DataAccessObject.SaveDaoTask<UserDAO, UserModel, UserEvent> createSaveTask(UserModel model) {
            return new UserDAO.SaveTask(model, false);
        }

        @Override
        public DataAccessObject.DeleteDaoTask<UserDAO, UserModel, UserEvent> createDeleteTask(UserModel model) {
            return new UserDAO.DeleteTask(model, false);
        }

        @Override
        public UserEvent validateForSave(UserModel target) {
            UserDAO dao = target.dataObject();
            String message;
            if (dao.getRowState() == DataRowState.DELETED) {
                message = "User has already been deleted";
            } else {
                String userName = dao.getUserName();
                if (userName.isEmpty()) {
                    message = "User name not defined";
                } else if (userName.length() > MAX_LENGTH_USERNAME) {
                    message = "User name too long";
                } else {
                    String password = dao.getPassword();
                    if (password.isEmpty()) {
                        message = "Password not defined";
                    } else if (password.length() > MAX_LENGTH_PASSWORD) {
                        message = "Password length too long";
                    } else {
                        return null;
                    }
                }
            }
            if (dao.getRowState() == DataRowState.NEW) {
                return UserEvent.createInsertInvalidEvent(target, this, message);
            }
            return UserEvent.createUpdateInvalidEvent(target, this, message);
        }

        @Override
        public UserOpRequestEvent createEditRequestEvent(UserModel model, Object source) {
            return new UserOpRequestEvent(model, source, false);
        }

        @Override
        public UserOpRequestEvent createDeleteRequestEvent(UserModel model, Object source) {
            return new UserOpRequestEvent(model, source, true);
        }

        @Override
        public EventType<UserOpRequestEvent> getBaseRequestEventType() {
            return UserOpRequestEvent.USER_OP_REQUEST;
        }

        @Override
        public EventType<UserOpRequestEvent> getEditRequestEventType() {
            return UserOpRequestEvent.EDIT_REQUEST;
        }

        @Override
        public EventType<UserOpRequestEvent> getDeleteRequestEventType() {
            return UserOpRequestEvent.DELETE_REQUEST;
        }

    }

}
