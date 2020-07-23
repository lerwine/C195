package scheduler.model.fx;

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
import scheduler.events.ModelEvent;
import scheduler.events.UserEvent;
import scheduler.events.UserOpRequestEvent;
import scheduler.events.UserSuccessEvent;
import scheduler.model.ModelHelper.UserHelper;
import static scheduler.model.User.MAX_LENGTH_PASSWORD;
import static scheduler.model.User.MAX_LENGTH_USERNAME;
import scheduler.model.UserEntity;
import scheduler.model.UserStatus;
import scheduler.observables.property.ReadOnlyStringBindingProperty;
import scheduler.util.WeakEventHandlingReference;
import scheduler.view.user.UserModelFilter;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public final class UserModel extends EntityModel<UserDAO> implements PartialUserModel<UserDAO>, UserEntity<LocalDateTime> {

    public static final Factory FACTORY = new Factory();

    private final WeakEventHandlingReference<UserSuccessEvent> modelEventHandler;
    private final SimpleStringProperty userName;
    private final SimpleStringProperty password;
    private final SimpleObjectProperty<UserStatus> status;
    private final ReadOnlyStringBindingProperty statusDisplay;

    private UserModel(UserDAO dao) {
        super(dao);
        userName = new ReadOnlyStringWrapper(this, PROP_USERNAME, dao.getUserName());
        password = new SimpleStringProperty(this, PROP_PASSWORD, dao.getPassword());
        status = new SimpleObjectProperty<>(this, PROP_STATUS, dao.getStatus());
        statusDisplay = new ReadOnlyStringBindingProperty(this, PROP_STATUSDISPLAY, () -> UserStatus.toDisplayValue(status.get()), status);
        modelEventHandler = WeakEventHandlingReference.create(this::onModelEvent);
    }

    @Override
    protected void onDaoChanged(ModelEvent<UserDAO, ? extends EntityModel<UserDAO>> event) {
        UserDAO dao = event.getDataAccessObject();
        userName.set(dao.getUserName());
        password.set(dao.getPassword());
        status.set(dao.getStatus());
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

    @Override
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
            @SuppressWarnings("unchecked")
            final PartialUserModel<? extends UserDAO> other = (PartialUserModel<? extends UserDAO>) obj;
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
        return UserHelper.appendModelProperties(this, new StringBuilder(UserModel.class.getName()).append(" { ")).append("}").toString();
    }

    public final static class Factory extends EntityModel.EntityModelFactory<UserDAO, UserModel> {

        // Singleton
        private Factory() {
            super();
            if (null != FACTORY) {
                throw new IllegalStateException();
            }
        }

        @Override
        public DataAccessObject.DaoFactory<UserDAO> getDaoFactory() {
            return UserDAO.FACTORY;
        }

        @Override
        public UserModel createNew(UserDAO dao) {
            UserModel newModel = new UserModel(dao);
            dao.addEventFilter(UserSuccessEvent.SUCCESS_EVENT_TYPE, newModel.modelEventHandler.getWeakEventHandler());
            return newModel;
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
        public DataAccessObject.SaveDaoTask<UserDAO, UserModel> createSaveTask(UserModel model) {
            return new UserDAO.SaveTask(model, false);
        }

        @Override
        public DataAccessObject.DeleteDaoTask<UserDAO, UserModel> createDeleteTask(UserModel model) {
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
        public Class<UserEvent> getModelResultEventClass() {
            return UserEvent.class;
        }

        @Override
        public EventType<UserSuccessEvent> getSuccessEventType() {
            return UserSuccessEvent.SUCCESS_EVENT_TYPE;
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
