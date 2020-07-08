package scheduler.model.ui;

import java.util.Objects;
import java.util.logging.Logger;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import scheduler.dao.DataAccessObject;
import scheduler.dao.DataRowState;
import scheduler.dao.UserDAO;
import static scheduler.model.User.MAX_LENGTH_PASSWORD;
import static scheduler.model.User.MAX_LENGTH_USERNAME;
import scheduler.model.UserStatus;
import scheduler.observables.property.PasswordHashProperty;
import scheduler.observables.property.ReadOnlyStringBindingProperty;
import scheduler.util.AnyTrueSet;
import scheduler.util.ToStringPropertyBuilder;
import scheduler.util.Values;
import scheduler.view.user.UserModelFilter;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public final class UserModel extends FxRecordModel<UserDAO> implements UserItem<UserDAO> {

    private static final Logger LOG = Logger.getLogger(UserModel.class.getName());

    public static final Factory FACTORY = new Factory();

    private final AnyTrueSet changeIndicator;
    private final AnyTrueSet validityIndicator;
    private final ReadOnlyBooleanWrapper valid;
    private final ReadOnlyBooleanWrapper changed;
    private final SimpleStringProperty userName;
    private final AnyTrueSet.Node userIsChanged;
    private final AnyTrueSet.Node userIsValid;
    private final PasswordHashProperty password;
    private final AnyTrueSet.Node passwordIsChanged;
    private final AnyTrueSet.Node passwordIsValid;
    private final SimpleObjectProperty<UserStatus> status;
    private final AnyTrueSet.Node statusIsChanged;
    private final ReadOnlyStringBindingProperty statusDisplay;
    private final AnyTrueSet.Node rowStateIsChange;

    public UserModel(UserDAO dao) {
        super(dao);
        changeIndicator = new AnyTrueSet();
        validityIndicator = new AnyTrueSet();
        userName = new ReadOnlyStringWrapper(this, PROP_USERNAME, dao.getUserName());
        userIsChanged = changeIndicator.add(false);
        userIsValid = validityIndicator.add(Values.isNotNullWhiteSpaceOrEmpty(userName.get()));
        userName.addListener((observable, oldValue, newValue) -> {
            userIsValid.setValid(Values.isNotNullWhiteSpaceOrEmpty(newValue));
            userIsChanged.setValid(!dao.getUserName().equals(newValue));
        });
        password = new PasswordHashProperty(this, PROP_PASSWORD, dao.getPassword());
        passwordIsChanged = changeIndicator.add(false);
        passwordIsValid = validityIndicator.add(Values.isNotNullWhiteSpaceOrEmpty(password.get()));
        password.addListener((observable, oldValue, newValue) -> {
            passwordIsValid.setValid(Values.isNotNullWhiteSpaceOrEmpty(newValue));
            passwordIsChanged.setValid(!dao.getPassword().equals(newValue));
        });
        status = new SimpleObjectProperty<>(this, PROP_STATUS, dao.getStatus());
        statusIsChanged = changeIndicator.add(false);
        status.addListener((observable, oldValue, newValue) -> {
            statusIsChanged.setValid(dao.getStatus() != newValue);
        });
        statusDisplay = new ReadOnlyStringBindingProperty(this, PROP_STATUSDISPLAY, () -> UserStatus.toDisplayValue(status.get()), status);
        rowStateIsChange = changeIndicator.add(DataRowState.isChanged(getRowState()));
        dao.addPropertyChangeListener((evt) -> {
            switch (evt.getPropertyName()) {
                case PROP_USERNAME:
                    userIsChanged.setValid(!dao.getUserName().equals(getUserName()));
                    // FIXME: update change
                    break;
                case PROP_PASSWORD:
                    passwordIsChanged.setValid(!dao.getPassword().equals(getPassword()));
                    // FIXME: update change
                    break;
                case PROP_STATUS:
                    statusIsChanged.setValid(dao.getStatus() != getStatus());
                    // FIXME: update change
                    break;
                case PROP_ROWSTATE:
                    rowStateIsChange.setValid(DataRowState.isChanged(getRowState()));
                    // FIXME: update change
                    break;
            }
        });
        valid = new ReadOnlyBooleanWrapper(this, PROP_VALID, validityIndicator.isValid());
        validityIndicator.validProperty().addListener((observable, oldValue, newValue) -> {
            valid.set(newValue);
        });
        changed = new ReadOnlyBooleanWrapper(this, PROP_CHANGED, changeIndicator.isValid());
        changeIndicator.validProperty().addListener((observable, oldValue, newValue) -> {
            changed.set(newValue);
        });
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
    public boolean isValid() {
        return valid.get();
    }

    @Override
    public ReadOnlyBooleanProperty validProperty() {
        return valid;
    }

    @Override
    public boolean isChanged() {
        return changed.get();
    }

    @Override
    public ReadOnlyBooleanProperty changedProperty() {
        return changed;
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
        if (null != obj && obj instanceof UserItem) {
            final UserItem<? extends UserDAO> other = (UserModel) obj;
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
                .addString(lastModifiedByProperty())
                .addBoolean(valid);
    }

    public final static class Factory extends FxRecordModel.FxModelFactory<UserDAO, UserModel> {

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
        public DataAccessObject.SaveDaoTask<UserDAO, UserModel> createSaveTask(UserModel model, boolean force) {
            return new UserDAO.SaveTask(model, force);
        }

        @Override
        public DataAccessObject.DeleteDaoTask<UserDAO, UserModel> createDeleteTask(UserModel model) {
            return new UserDAO.DeleteTask(model);
        }

        @Override
        public String validateProperties(UserModel target) {
            UserDAO dao = target.dataObject();
            if (dao.getRowState() == DataRowState.DELETED) {
                return "User has already been deleted";
            }
            String userName = dao.getUserName();
            if (userName.isEmpty()) {
                return "User name not defined";
            }
            if (userName.length() > MAX_LENGTH_USERNAME) {
                return "User name too long";
            }
            String password = dao.getPassword();
            if (password.isEmpty()) {
                return "Password not defined";
            }
            if (password.length() > MAX_LENGTH_PASSWORD) {
                return "Password length too long";
            }
            return null;
        }

    }

}
