package scheduler.dao;

import java.beans.PropertyChangeSupport;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import scheduler.Scheduler;
import scheduler.dao.filter.ComparisonOperator;
import scheduler.dao.filter.DaoFilter;
import scheduler.dao.filter.DaoFilterExpression;
import scheduler.dao.filter.UserFilter;
import scheduler.dao.schema.DatabaseTable;
import scheduler.dao.schema.DbColumn;
import scheduler.dao.schema.DbTable;
import scheduler.dao.schema.DmlSelectQueryBuilder;
import scheduler.dao.schema.SchemaHelper;
import scheduler.events.DbOperationType;
import scheduler.events.EventEvaluationStatus;
import scheduler.events.UserEvent;
import scheduler.model.ModelHelper;
import scheduler.model.User;
import scheduler.model.UserStatus;
import scheduler.model.ui.UserModel;
import scheduler.util.InternalException;
import scheduler.util.LogHelper;
import scheduler.util.PropertyBindable;
import scheduler.util.ToStringPropertyBuilder;
import static scheduler.util.Values.asNonNullAndTrimmed;

/**
 * Data access object for the {@code user} database table.
 * <p>
 * This object contains the login credentials for users of the current application.</p>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@DatabaseTable(DbTable.USER)
public final class UserDAO extends DataAccessObject implements UserDbRecord {

    public static final FactoryImpl FACTORY = new FactoryImpl();
    private static final Logger LOG = Logger.getLogger(UserDAO.class.getName());

    public static FactoryImpl getFactory() {
        return FACTORY;
    }

    private final OriginalValues originalValues;
    private String userName;
    // PENDING: Change to using something that can accept raw password and produce hash.
    private String password;
    private UserStatus status;

    /**
     * Initializes a {@link DataRowState#NEW} user object.
     */
    public UserDAO() {
        super();
        userName = "";
        password = "";
        status = UserStatus.NORMAL;
        originalValues = new OriginalValues();
    }

    @Override
    public String getUserName() {
        return userName;
    }

    /**
     * Set the value of userName
     *
     * @param value new value of userName
     */
    public void setUserName(String value) {
        String oldValue = this.userName;
        this.userName = asNonNullAndTrimmed(value);
        firePropertyChange(PROP_USERNAME, oldValue, this.userName);
    }

    public String getPassword() {
        return password;
    }

    /**
     * Set the value of password
     *
     * @param value new value of password
     */
    public void setPassword(String value) {
        String oldValue = this.password;
        this.password = asNonNullAndTrimmed(value);
        firePropertyChange(PROP_PASSWORD, oldValue, this.password);
    }

    @Override
    public UserStatus getStatus() {
        return status;
    }

    /**
     * Set the value of status
     *
     * @param status new value of status
     */
    public void setStatus(UserStatus status) {
        UserStatus oldValue = this.status;
        this.status = (null == status) ? UserStatus.NORMAL : status;
        firePropertyChange(PROP_STATUS, oldValue, this.status);
    }

    @Override
    protected void onAcceptChanges() {
        originalValues.userName = userName;
        originalValues.password = password;
        originalValues.status = status;
    }

    @Override
    protected void onRejectChanges() {
        String oldUserName = userName;
        String oldPassword = password;
        UserStatus oldStatus = status;
        userName = originalValues.userName;
        password = originalValues.password;
        status = originalValues.status;
        firePropertyChange(PROP_USERNAME, oldUserName, userName);
        firePropertyChange(PROP_PASSWORD, oldPassword, password);
        firePropertyChange(PROP_STATUS, oldStatus, status);
    }

    @Override
    public EventDispatchChain buildEventDispatchChain(EventDispatchChain tail) {
        LOG.fine(() -> String.format("Adding %s to dispatch chain", FACTORY.getClass().getName()));
        return FACTORY.buildEventDispatchChain(super.buildEventDispatchChain(tail));
    }

    @Override
    public int hashCode() {
        if (this.getRowState() != DataRowState.NEW) {
            return this.getPrimaryKey();
        }
        int hash = 7;
        hash = 43 * hash + Objects.hashCode(this.userName);
        hash = 43 * hash + Objects.hashCode(this.password);
        hash = 43 * hash + Objects.hashCode(this.status);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        return null != obj && obj instanceof User && ModelHelper.areSameRecord(this, (User) obj);
    }

    @Override
    public String toString() {
        return toStringBuilder().build();
    }

    @Override
    public ToStringPropertyBuilder toStringBuilder() {
        ToStringPropertyBuilder builder = ToStringPropertyBuilder.create(this);
        if (getRowState() != DataRowState.NEW) {
            builder.addNumber(PROP_PRIMARYKEY, getPrimaryKey());
        }
        return builder.addEnum(PROP_ROWSTATE, getRowState())
                .addString(PROP_USERNAME, userName)
                .addString(PROP_PASSWORD, password)
                .addEnum(PROP_STATUS, status)
                .addTimestamp(PROP_CREATEDATE, getCreateDate())
                .addString(PROP_CREATEDBY, getCreatedBy())
                .addTimestamp(PROP_LASTMODIFIEDDATE, getLastModifiedDate())
                .addString(PROP_LASTMODIFIEDBY, getLastModifiedBy());
    }

    /**
     * Factory implementation for {@link UserDAO} objects.
     */
    public static final class FactoryImpl extends DataAccessObject.DaoFactory<UserDAO, UserEvent> {

        private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(FactoryImpl.class.getName()), Level.FINER);
//        private static final Logger LOG = Logger.getLogger(FactoryImpl.class.getName());

        // This is a singleton instance
        private FactoryImpl() {
        }

        @Override
        void insert(UserEvent event, Connection connection) {
            if (event.getOperation() != DbOperationType.DB_INSERT || event.getStatus() != EventEvaluationStatus.EVALUATING) {
                throw new IllegalArgumentException();
            }
            String sql = "SELECT COUNT(" + DbColumn.USER_ID.getDbName() + ") FROM " + DbTable.USER.getDbName()
                    + " WHERE LOWER(" + DbColumn.USER_NAME.getDbName() + ")=?";
            int count;
            UserDAO dao = IUserDAO.assertValidUser(event.getDataAccessObject());
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, dao.getUserName());
                LOG.fine(() -> String.format("Executing DML statement: %s", sql));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        count = rs.getInt(1);
                    } else {
                        SQLWarning sqlWarning = connection.getWarnings();
                        if (null != sqlWarning) {
                            do {
                                LOG.log(Level.WARNING, "Encountered warning", sqlWarning);
                            } while (null != (sqlWarning = sqlWarning.getNextWarning()));
                        }
                        throw new SQLException("Unexpected lack of results from database query");
                    }
                    SQLWarning sqlWarning = connection.getWarnings();
                    if (null != sqlWarning) {
                        do {
                            LOG.log(Level.WARNING, "Encountered warning", sqlWarning);
                        } while (null != (sqlWarning = sqlWarning.getNextWarning()));
                    }
                }
            } catch (SQLException ex) {
                event.setFaulted("Unexpected error", "Error user naming conflicts", ex);
                LOG.log(Level.SEVERE, event.getDetailMessage(), ex);
                Platform.runLater(() -> Event.fireEvent(dao, event));
                return;
            }
            if (count > 0) {
                event.setInvalid("User name already in use", "Another user has the same name");
                Platform.runLater(() -> Event.fireEvent(dao, event));
            } else {
                super.insert(event, connection);
            }
        }

        @Override
        void update(UserEvent event, Connection connection) {
            if (event.getOperation() != DbOperationType.DB_UPDATE || event.getStatus() != EventEvaluationStatus.EVALUATING) {
                throw new IllegalArgumentException();
            }
            String sql = "SELECT COUNT(" + DbColumn.USER_ID.getDbName() + ") FROM " + DbTable.USER.getDbName()
                    + " WHERE LOWER(" + DbColumn.USER_NAME.getDbName() + ")=?" + " AND " + DbColumn.USER_ID.getDbName() + "<>?";
            int count;
            UserDAO dao = IUserDAO.assertValidUser(event.getDataAccessObject());
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, dao.getUserName());
                ps.setInt(2, dao.getPrimaryKey());
                LOG.fine(() -> String.format("Executing DML statement: %s", sql));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        count = rs.getInt(1);
                    } else {
                        SQLWarning sqlWarning = connection.getWarnings();
                        if (null != sqlWarning) {
                            do {
                                LOG.log(Level.WARNING, "Encountered warning", sqlWarning);
                            } while (null != (sqlWarning = sqlWarning.getNextWarning()));
                        }
                        throw new SQLException("Unexpected lack of results from database query");
                    }
                    SQLWarning sqlWarning = connection.getWarnings();
                    if (null != sqlWarning) {
                        do {
                            LOG.log(Level.WARNING, "Encountered warning", sqlWarning);
                        } while (null != (sqlWarning = sqlWarning.getNextWarning()));
                    }
                }
            } catch (SQLException ex) {
                event.setFaulted("Unexpected error", "Error user naming conflicts", ex);
                LOG.log(Level.SEVERE, event.getDetailMessage(), ex);
                Platform.runLater(() -> Event.fireEvent(dao, event));
                return;
            }
            if (count > 0) {
                event.setInvalid("User name already in use", "Another user has the same name");
                Platform.runLater(() -> Event.fireEvent(dao, event));
            } else {
                super.update(event, connection);
            }
        }

        @Override
        protected void delete(UserEvent event, Connection connection) {
            if (event.getOperation() != DbOperationType.DB_DELETE || event.getStatus() != EventEvaluationStatus.EVALUATING) {
                throw new IllegalArgumentException();
            }
            UserDAO dao = event.getDataAccessObject();

            if (dao == Scheduler.getCurrentUser()) {
                event.setInvalid("Self-delete", "Cannot delete the current user");
                Platform.runLater(() -> Event.fireEvent(dao, event));
                return;
            }

            int count;
            try {
                count = AppointmentDAO.FACTORY.countByUser(connection, dao.getPrimaryKey(), null, null);
            } catch (SQLException ex) {
                event.setFaulted("Unexpected error", "Error checking dependencies", ex);
                LOG.log(Level.SEVERE, event.getDetailMessage(), ex);
                Platform.runLater(() -> Event.fireEvent(dao, event));
                return;
            }
            switch (count) {
                case 0:
                    super.delete(event, connection);
                    return;
                case 1:
                    event.setInvalid("User in use", "User is referenced by one appointment.");
                    break;
                default:
                    event.setInvalid("User in use", String.format("User is referenced by %d other appointments", count));
                    break;
            }
            Platform.runLater(() -> Event.fireEvent(dao, event));
        }

        @Override
        public boolean isCompoundSelect() {
            return false;
        }

        @Override
        protected void applyColumnValue(UserDAO dao, DbColumn dbColumn, PreparedStatement ps, int index) throws SQLException {
            switch (dbColumn) {
                case USER_NAME:
                    ps.setString(index, dao.userName);
                    break;
                case PASSWORD:
                    ps.setString(index, dao.password);
                    break;
                case STATUS:
                    ps.setInt(index, dao.status.getValue());
                    break;
                default:
                    throw new InternalException(String.format("Unexpected %s column name %s", dbColumn.getTable().getDbName(), dbColumn.getDbName()));
            }
        }

        @Override
        public UserDAO createNew() {
            return new UserDAO();
        }

        @Override
        public DaoFilter<UserDAO> getAllItemsFilter() {
            return UserFilter.of(DaoFilterExpression.empty());
        }

        public DaoFilter<UserDAO> getActiveUsersFilter() {
            return UserFilter.of(UserFilter.expressionOf(UserStatus.INACTIVE, ComparisonOperator.NOT_EQUALS));
        }

        @Override
        public DmlSelectQueryBuilder createDmlSelectQueryBuilder() {
            return new DmlSelectQueryBuilder(DbTable.USER, SchemaHelper.getTableColumns(DbTable.USER));
        }

        @Override
        protected UserDAO fromResultSet(ResultSet rs) throws SQLException {
            UserDAO result = super.fromResultSet(rs);
            UserDAO currentUser = Scheduler.getCurrentUser();
            if (null != currentUser && currentUser.getPrimaryKey() == result.getPrimaryKey()) {
                UserDAO.FACTORY.cloneProperties(currentUser, result);
                return currentUser;
            }
            return result;
        }

        @Override
        protected void onCloneProperties(UserDAO fromDAO, UserDAO toDAO) {
            String oldUserName = toDAO.userName;
            String oldPassword = toDAO.password;
            UserStatus oldStatus = toDAO.status;
            toDAO.userName = fromDAO.userName;
            toDAO.password = fromDAO.password;
            toDAO.status = fromDAO.status;
            toDAO.originalValues.userName = fromDAO.originalValues.userName;
            toDAO.originalValues.password = fromDAO.originalValues.password;
            toDAO.originalValues.status = fromDAO.originalValues.status;
            toDAO.firePropertyChange(PROP_USERNAME, oldUserName, toDAO.userName);
            toDAO.firePropertyChange(PROP_PASSWORD, oldPassword, toDAO.password);
            toDAO.firePropertyChange(PROP_STATUS, oldStatus, toDAO.status);
        }

        @Override
        protected Consumer<PropertyChangeSupport> onInitializeFromResultSet(UserDAO dao, ResultSet rs) throws SQLException {
            Consumer<PropertyChangeSupport> propertyChanges = new Consumer<PropertyChangeSupport>() {
                private final String oldUserName = dao.userName;
                private final String oldPassword = dao.password;
                private final UserStatus oldStatus = dao.status;

                @Override
                public void accept(PropertyChangeSupport t) {
                    if (!dao.userName.equals(oldUserName)) {
                        t.firePropertyChange(PROP_USERNAME, oldUserName, dao.userName);
                    }
                    if (!dao.password.equals(oldPassword)) {
                        t.firePropertyChange(PROP_PASSWORD, oldPassword, dao.password);
                    }
                    if (dao.status != oldStatus) {
                        t.firePropertyChange(PROP_STATUS, oldStatus, dao.status);
                    }
                }
            };

            dao.userName = asNonNullAndTrimmed(rs.getString(DbColumn.USER_NAME.toString()));
            dao.password = asNonNullAndTrimmed(rs.getString(DbColumn.PASSWORD.toString()));
            dao.status = UserStatus.of(rs.getInt(DbColumn.STATUS.toString()), (null == dao.status) ? UserStatus.NORMAL : dao.status);
            return propertyChanges;
        }

        IUserDAO fromJoinedResultSet(ResultSet rs) throws SQLException {
            return new Related(rs.getInt(DbColumn.APPOINTMENT_USER.toString()), asNonNullAndTrimmed(rs.getString(DbColumn.USER_NAME.toString())),
                    UserStatus.of(rs.getInt(DbColumn.STATUS.toString()), UserStatus.INACTIVE));
        }

        /**
         * Loads the first {@link UserDAO} record matching the specified user name.
         *
         * @param connection The {@link Connection} to use to retrieve the data.
         * @param userName The user name value to match.
         * @return The first {@link UserDAO} record matching {@code userName} or {@link Optional#EMPTY} if no match was found..
         * @throws SQLException if unable to perform database query.
         */
        public Optional<UserDAO> findByUserName(Connection connection, String userName) throws SQLException {
            String sql = createDmlSelectQueryBuilder().build().append(" WHERE LOWER(").append(DbColumn.USER_NAME.getDbName()).append(")=?").toString();
            LOG.fine(() -> String.format("Executing query \"%s\"", sql));
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                LOG.fine(() -> String.format("Set first parameter to \"%s\"", userName));
                ps.setString(1, userName.trim().toLowerCase());
                //ps.setShort(2, Values.USER_STATUS_INACTIVE);
                try (ResultSet rs = ps.executeQuery()) {
                    if (null != rs && rs.next()) {
                        Optional<UserDAO> result = Optional.of(fromResultSet(rs));
                        SQLWarning sqlWarning = connection.getWarnings();
                        if (null != sqlWarning) {
                            do {
                                LOG.log(Level.WARNING, "Encountered warning", sqlWarning);
                            } while (null != (sqlWarning = sqlWarning.getNextWarning()));
                        }
                        return result;
                    }
                    SQLWarning sqlWarning = connection.getWarnings();
                    if (null != sqlWarning) {
                        do {
                            LOG.log(Level.WARNING, "Encountered warning", sqlWarning);
                        } while (null != (sqlWarning = sqlWarning.getNextWarning()));
                    } else {
                        LOG.log(Level.WARNING, "No results, no warnings.");
                    }
                }
            }
            return Optional.empty();
        }

        @Override
        public Class<? extends UserDAO> getDaoClass() {
            return UserDAO.class;
        }

        @Override
        protected UserEvent createDbOperationEvent(UserEvent sourceEvent, DbOperationType operation) {
            UserModel model = sourceEvent.getModel();
            if (null != model) {
                return new UserEvent(model, sourceEvent.getSource(), this, operation);
            }
            return new UserEvent(sourceEvent.getSource(), this, sourceEvent.getDataAccessObject(), operation);
        }

        @Override
        public EventDispatchChain buildEventDispatchChain(EventDispatchChain tail) {
            LOG.fine(() -> String.format("Adding %s to dispatch chain", UserModel.FACTORY.getClass().getName()));
            return UserModel.FACTORY.buildEventDispatchChain(super.buildEventDispatchChain(tail));
        }

    }

    public static final class Related extends PropertyBindable implements IUserDAO {

        private final int primaryKey;
        private final String userName;
        private final UserStatus status;

        private Related(int primaryKey, String userName, UserStatus status) {
            this.primaryKey = primaryKey;
            this.userName = userName;
            this.status = status;
        }

        @Override
        public String getUserName() {
            return userName;
        }

        @Override
        public UserStatus getStatus() {
            return status;
        }

        @Override
        public int getPrimaryKey() {
            return primaryKey;
        }

        @Override
        public int hashCode() {
            return primaryKey;
        }

        @Override
        public boolean equals(Object obj) {
            return null != obj && obj instanceof User && ModelHelper.areSameRecord(this, (User) obj);
        }

        @Override
        public String toString() {
            return toStringBuilder().build();
        }

        @Override
        public ToStringPropertyBuilder toStringBuilder() {
            return ToStringPropertyBuilder.create(this)
                    .addNumber(PROP_PRIMARYKEY, getPrimaryKey())
                    .addString(PROP_USERNAME, userName)
                    .addEnum(PROP_STATUS, status);
        }

    }

    private class OriginalValues {

        private String userName;
        private String password;
        private UserStatus status;

        private OriginalValues() {
            this.userName = UserDAO.this.userName;
            this.password = UserDAO.this.password;
            this.status = UserDAO.this.status;
        }
    }
}
